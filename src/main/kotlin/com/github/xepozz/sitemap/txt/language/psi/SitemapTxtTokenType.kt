package com.github.xepozz.sitemap.txt.language.psi

import com.github.xepozz.sitemap.txt.language.SitemapTxtLanguage
import com.intellij.psi.tree.IElementType

class SitemapTxtTokenType(debugName: String) : IElementType(debugName, SitemapTxtLanguage.INSTANCE) {
    override fun toString() = "SitemapTxtTokenType." + super.toString()
}