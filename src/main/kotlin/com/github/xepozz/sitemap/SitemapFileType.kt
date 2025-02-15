package com.github.xepozz.sitemap

import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import java.io.Serializable

class SitemapFileType private constructor() : LanguageFileType(XMLLanguage.INSTANCE), Serializable {
    override fun getName() = "Sitemap XML File"

    override fun getDescription() = "Sitemap.xml description file"

    override fun getDefaultExtension() = "xml"

    override fun getIcon() = SitemapIcons.FILE

    companion object {
        @JvmStatic
        val INSTANCE = SitemapFileType()
    }
}