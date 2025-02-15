package com.github.xepozz.sitemap.txt.language

import com.github.xepozz.sitemap.txt.language.parser.SitemapTxtLexerAdapter
import com.github.xepozz.sitemap.txt.language.parser.SitemapTxtParser
import com.github.xepozz.sitemap.txt.language.psi.SitemapTxtTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.psi.FileViewProvider
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class SitemapTxtParserDefinition : ParserDefinition {
    override fun createLexer(project: com.intellij.openapi.project.Project?) = SitemapTxtLexerAdapter()

    override fun getWhitespaceTokens() = TokenSet.WHITE_SPACE

    override fun createParser(project: com.intellij.openapi.project.Project?) = SitemapTxtParser()

    override fun getFileNodeType() = FILE

    override fun getCommentTokens() = TokenSet.EMPTY

    override fun getStringLiteralElements() = TokenSet.EMPTY

    override fun createElement(node: ASTNode) = SitemapTxtTypes.Factory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider) = SitemapTxtFile(viewProvider)

    companion object {
        val FILE = IFileElementType(SitemapTxtLanguage.INSTANCE)
    }
}
