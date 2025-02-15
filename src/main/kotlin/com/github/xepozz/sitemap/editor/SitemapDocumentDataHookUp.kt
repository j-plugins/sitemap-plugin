package com.github.xepozz.sitemap.editor

import com.intellij.database.datagrid.DataConsumer
import com.intellij.database.datagrid.DocumentDataHookUp
import com.intellij.database.datagrid.GridColumn
import com.intellij.database.datagrid.GridRequestSource
import com.intellij.database.datagrid.GridRow
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.xml.XmlFile

class SitemapDocumentDataHookUp(project: Project, document: Document, val myVirtualFile: VirtualFile) :
    DocumentDataHookUp(project, document, null) {
    companion object {
    }

    override fun buildMarkup(
        sequence: CharSequence,
        requestSource: GridRequestSource
    ): DataMarkup? {
        return when{
            sequence.contains("<sitemapindex") -> SitemapIndexDataMarkup.build(project, document, true)
            sequence.contains("<urlset") -> SitemapIndexDataMarkup.build(project, document, false)
            else->null
        }
//        if (sequence.contains("<sitemapindex")) {
//            return SitemapIndexDataMarkup.build(project, document)
//        }
//        if (sequence.contains("<urlset")) {
//            return SitemapRegularDataMarkup.build(project, document)
//        }
//        return null
    }
}
