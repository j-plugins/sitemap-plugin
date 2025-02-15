package com.github.xepozz.sitemap.editor

import com.intellij.database.datagrid.DocumentDataHookUp
import com.intellij.database.datagrid.GridRequestSource
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project

class SitemapDocumentDataHookUp(project: Project, document: Document) :
    DocumentDataHookUp(project, document, null) {
    override fun buildMarkup(
        sequence: CharSequence,
        requestSource: GridRequestSource
    ) = when {
        sequence.contains("<sitemapindex") -> SitemapIndexDataMarkup.build(project, document, true)
        sequence.contains("<urlset") -> SitemapIndexDataMarkup.build(project, document, false)
        else -> null
    }
}
