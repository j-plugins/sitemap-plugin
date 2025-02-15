package com.github.xepozz.sitemap.txt.language.psi.impl

import com.github.xepozz.sitemap.txt.language.psi.SitemapTxtUrl
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry

object SitemapImplUtil {
    @JvmStatic
    fun getValue(element: SitemapTxtUrl): String = element.node.firstChildNode.text

    @JvmStatic
    fun getReferences(element: SitemapTxtUrl): Array<PsiReference> =
        ReferenceProvidersRegistry.getReferencesFromProviders(element)
}