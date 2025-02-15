package com.github.xepozz.sitemap.txt.language

import com.github.xepozz.sitemap.txt.language.parser.SitemapTxtLexerAdapter
import com.github.xepozz.sitemap.txt.language.psi.SitemapTxtTypes
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class SitemapTxtSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer() = SitemapTxtLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType) = when (tokenType) {
        SitemapTxtTypes.TEXT -> TEXT_KEYS
        TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS
        else -> EMPTY_KEYS
    }

    companion object {
        private val BAD_CHAR_KEYS = arrayOf(
            HighlighterColors.BAD_CHARACTER,
        )

        private val TEXT_KEYS = arrayOf(
            DefaultLanguageHighlighterColors.STRING,
        )
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }
}