/*
 * Copyright 2013 The authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.struts2.dom.struts.impl;

import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.struts2.dom.ConverterUtil;
import com.intellij.struts2.dom.struts.model.StrutsModel;
import com.intellij.struts2.dom.struts.strutspackage.StrutsPackage;
import com.intellij.struts2.dom.struts.strutspackage.StrutsPackageExtendsResolveConverter;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.ElementPresentationManager;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author Yann C&eacute;bron
 */
public class StrutsPackageExtendsResolveConverterImpl extends StrutsPackageExtendsResolveConverter {

  @Nullable
  @Override
  protected StrutsPackage convertString(@Nullable final String name, ConvertContext context) {
    if (name == null) {
      return null;
    }

    final StrutsModel strutsModel = ConverterUtil.getStrutsModel(context);
    if (strutsModel == null) {
      return null;
    }

    return ContainerUtil.find(strutsModel.getStrutsPackages(), new Condition<StrutsPackage>() {
      public boolean value(final StrutsPackage strutsPackage) {
        return Comparing.equal(name, strutsPackage.getName().getStringValue());
      }
    });
  }

  @Override
  protected Object[] getReferenceVariants(ConvertContext context, GenericDomValue<List<StrutsPackage>> genericDomValue) {
    final StrutsModel strutsModel = ConverterUtil.getStrutsModel(context);
    if (strutsModel == null) {
      return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }

    final Collection<StrutsPackage> variants = removeCurrentElementFromVariants(context, strutsModel.getStrutsPackages());
    return ElementPresentationManager.getInstance().createVariants(variants);
  }

  @Nullable
  @Override
  protected PsiElement resolveReference(@Nullable StrutsPackage strutsPackage, ConvertContext context) {
    return strutsPackage != null ? strutsPackage.getXmlTag() : null;
  }

  private Collection<StrutsPackage> removeCurrentElementFromVariants(final ConvertContext context,
                                                                     final Collection<StrutsPackage> allVariants) {
    final StrutsPackage currentElement = (StrutsPackage)DomUtil.getDomElement(context.getTag());
    assert currentElement != null : "currentElement was null for " + context.getTag();
    final GenericDomValue currentNameElement = currentElement.getGenericInfo().getNameDomElement(currentElement);
    if (currentNameElement == null) {
      return allVariants; // skip due to XML errors
    }

    final String currentName = currentNameElement.getStringValue();
    if (currentName == null) {
      return allVariants; // skip due to XML errors
    }

    final StrutsPackage currentElementInVariants = DomUtil.findByName(allVariants, currentName);
    if (currentElementInVariants != null) {
      allVariants.remove(currentElementInVariants);
    }

    return allVariants;
  }
}