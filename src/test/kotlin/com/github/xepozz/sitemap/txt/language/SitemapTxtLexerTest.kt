package com.github.xepozz.sitemap.txt.language

import com.github.xepozz.sitemap.txt.language.parser.SitemapTxtLexerAdapter
import com.github.xepozz.sitemap.txt.language.psi.SitemapTxtTypes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SitemapTxtLexerTest {
    @Test
    fun `text tokens do not include carriage returns in crlf files`() {
        val lexer = SitemapTxtLexerAdapter()
        lexer.start("https://example.com\r\nhttps://example.org\n")

        val textTokens = mutableListOf<String>()
        while (lexer.tokenType != null) {
            if (lexer.tokenType == SitemapTxtTypes.TEXT) {
                textTokens.add(lexer.tokenText)
                assertFalse(lexer.tokenText.contains('\r'))
            }
            lexer.advance()
        }

        assertEquals(listOf("https://example.com", "https://example.org"), textTokens)
    }
}
