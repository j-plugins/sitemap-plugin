package com.github.xepozz.sitemap.xml.editor

import com.intellij.database.datagrid.DocumentDataHookUp
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.xml.XmlFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class SitemapIndexDataMarkupTest : BasePlatformTestCase() {
    fun testSitemapIndexBuildUsesOnlyIndexColumns() {
        val markup = buildMarkup(
            "sitemap.xml",
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                    <sitemap>
                        <loc>https://example.com/sitemap.xml</loc>
                        <lastmod>2026-01-01</lastmod>
                        <changefreq>daily</changefreq>
                        <priority>1</priority>
                    </sitemap>
                </sitemapindex>
            """.trimIndent(),
        )

        assertEquals(listOf("Location", "Last modified"), markup.columns.map { it.name })
        assertEquals(1, markup.rows.size)
        assertEquals(2, markup.rows.single().size)
        assertEquals("https://example.com/sitemap.xml", markup.rows.single().getValue(0))
        assertEquals("2026-01-01", markup.rows.single().getValue(1))
    }

    fun testUrlSetBuildUsesAllUrlColumns() {
        val markup = buildMarkup(
            "sitemap.xml",
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                    <url>
                        <loc>https://example.com/</loc>
                        <lastmod>2026-01-01</lastmod>
                        <changefreq>weekly</changefreq>
                        <priority>0.8</priority>
                    </url>
                </urlset>
            """.trimIndent(),
        )

        assertEquals(
            listOf("Location", "Last modified", "Change frequency", "Priority"),
            markup.columns.map { it.name },
        )
        assertEquals(1, markup.rows.size)
        assertEquals(4, markup.rows.single().size)
        assertEquals("https://example.com/", markup.rows.single().getValue(0))
        assertEquals("2026-01-01", markup.rows.single().getValue(1))
        assertEquals("weekly", markup.rows.single().getValue(2))
        assertEquals("0.8", markup.rows.single().getValue(3))
    }

    fun testDeletionOrderDeletesRowsFromBottomToTop() {
        assertEquals(listOf(3, 1, 0), SitemapIndexDataMarkup.deletionOrder(listOf(1, 3, 0)))
    }

    fun testCellTextAcceptsNullAndNonStringValues() {
        assertEquals("", SitemapIndexDataMarkup.cellText(null))
        assertEquals("42", SitemapIndexDataMarkup.cellText(42))
    }

    fun testUnsupportedXmlRootDoesNotBuildMarkup() {
        val psiFile = myFixture.configureByText(
            "sitemap.xml",
            """
                <?xml version="1.0" encoding="UTF-8"?>
                <rss>
                    <channel/>
                </rss>
            """.trimIndent(),
        ) as XmlFile
        val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)!!

        assertNull(SitemapIndexDataMarkup.build(project, document))
    }

    private fun buildMarkup(
        name: String,
        text: String,
    ): DocumentDataHookUp.DataMarkup {
        val psiFile = myFixture.configureByText(name, text) as XmlFile
        val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)!!
        return SitemapIndexDataMarkup.build(project, document)!!
    }
}
