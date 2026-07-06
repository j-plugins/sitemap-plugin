package com.github.xepozz.sitemap.xml.editor

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SitemapTableFileEditorProviderTest {
    @Test
    fun `accepts sitemap xml names case insensitively`() {
        assertTrue(SitemapTableFileEditorProvider.acceptsSitemapXml("Sitemap.XML", "XML"))
        assertTrue(SitemapTableFileEditorProvider.acceptsSitemapXml("product-sitemap.xml", "xml"))
        assertFalse(SitemapTableFileEditorProvider.acceptsSitemapXml("sitemap.txt", "txt"))
        assertFalse(SitemapTableFileEditorProvider.acceptsSitemapXml("feed.xml", "xml"))
    }

    @Test
    fun `supports only sitemap xml roots`() {
        assertTrue(SitemapTableFileEditorProvider.isSupportedRoot("urlset"))
        assertTrue(SitemapTableFileEditorProvider.isSupportedRoot("sitemapindex"))
        assertFalse(SitemapTableFileEditorProvider.isSupportedRoot("rss"))
    }
}
