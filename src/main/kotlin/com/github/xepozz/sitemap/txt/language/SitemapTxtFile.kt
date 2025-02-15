package com.github.xepozz.sitemap.txt.language

import com.github.xepozz.sitemap.txt.SitemapTxtFileType
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class SitemapTxtFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, SitemapTxtLanguage.INSTANCE) {
    override fun getFileType() = SitemapTxtFileType.INSTANCE
}
