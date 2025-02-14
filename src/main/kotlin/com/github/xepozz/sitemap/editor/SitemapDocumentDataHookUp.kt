package com.github.xepozz.sitemap.editor

import com.intellij.codeInsight.editorActions.XmlEditUtil
import com.intellij.database.datagrid.DataConsumer
import com.intellij.database.datagrid.DocumentDataHookUp
import com.intellij.database.datagrid.GridColumn
import com.intellij.database.datagrid.GridRequestSource
import com.intellij.database.datagrid.GridRow
import com.intellij.database.datagrid.ModelIndex
import com.intellij.database.datagrid.mutating.RowMutation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.ReadResult
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiEditorUtilBase
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.util.XmlUtil

class SitemapDocumentDataHookUp(project: Project, document: Document, val myVirtualFile: VirtualFile) :
    DocumentDataHookUp(project, document, null) {
        companion object {
            val columnLabelsMapping = hashMapOf<String, String>(
                "loc" to "Location",
                "lastmod" to "Last modified",
            )
        }
    override fun buildMarkup(
        p0: CharSequence,
        p1: GridRequestSource
    ): DataMarkup? {
        val columns = mutableListOf<GridColumn>(
            DataConsumer.Column(0, columnLabelsMapping["loc"], 1, null, null),
            DataConsumer.Column(1, columnLabelsMapping["lastmod"], 1, null, null),
        )

        var index = 0
        val rows = mutableListOf<GridRow>()

        val psiFile = ReadAction.compute<XmlFile, Exception> {
            PsiDocumentManager.getInstance(project).getPsiFile(document) as XmlFile
        }
        println("build markup ")
        ReadAction.compute<Void, Exception> {

            val tag = psiFile.rootTag!!

            tag.findSubTags("sitemap")
                .forEach {
                    val result = hashMapOf(
                        "loc" to "",
                        "lastmod" to ""
                    )
                    for (xmlTag in it.subTags) {
                        result[xmlTag.name] = when (xmlTag.name) {
                            "loc" -> xmlTag.value.text
                            "lastmod" -> xmlTag.value.text
                            else -> continue
                        }
                    }

                    println("consume ${result}")
                    rows.add(DataConsumer.Row.create(index, listOf(result["loc"], result["lastmod"]).toTypedArray()))
                    index++
                }
            null
        }

        return SitemapDataMarkup(columns, rows, psiFile)
    }
}
