package com.github.xepozz.sitemap.xml.editor

import com.intellij.database.datagrid.DocumentDataHookUp
import com.intellij.database.datagrid.GridRequestSource
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project

class SitemapDocumentDataHookUp(project: Project, document: Document) :
    DocumentDataHookUp(project, document, null) {
    override fun buildMarkup(
        sequence: CharSequence,
        requestSource: GridRequestSource
    ) = SitemapIndexDataMarkup.build(project, document)
}
