package com.github.xepozz.sitemap.txt.language.psi

import com.github.xepozz.sitemap.txt.language.SitemapTxtLanguage
import com.intellij.psi.tree.IElementType

class SitemapTxtElementType(debugName: String) :
    IElementType("SitemapTxtElementType($debugName)", SitemapTxtLanguage.INSTANCE)
