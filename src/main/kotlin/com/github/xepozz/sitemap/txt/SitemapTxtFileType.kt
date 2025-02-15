package com.github.xepozz.sitemap.txt

import com.github.xepozz.sitemap.SitemapIcons
import com.github.xepozz.sitemap.txt.language.SitemapTxtLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import java.io.Serializable

class SitemapTxtFileType private constructor() : LanguageFileType(SitemapTxtLanguage.INSTANCE), Serializable {
    override fun getName() = "Sitemap Text"
    override fun getDescription() = "Sitemap.txt description file"
    override fun getDefaultExtension() = "txt"
    override fun getIcon() = SitemapIcons.FILE
    companion object {
        @JvmStatic
        val INSTANCE = SitemapTxtFileType()
    }
}