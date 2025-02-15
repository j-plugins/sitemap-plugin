package com.github.xepozz.sitemap.txt

import com.github.xepozz.sitemap.SitemapIcons
import com.intellij.lang.Language
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.fileTypes.PlainTextLanguage
import java.io.Serializable

class SitemapTxtFileType private constructor() : LanguageFileType(PlainTextLanguage.INSTANCE), Serializable {
    override fun getName() = "Sitemap Text File"

    override fun getDescription() = "Sitemap.txt description file"

    override fun getDefaultExtension() = "txt"

    override fun getIcon() = SitemapIcons.FILE

    companion object {
        @JvmStatic
        val INSTANCE = SitemapTxtFileType()
    }
}