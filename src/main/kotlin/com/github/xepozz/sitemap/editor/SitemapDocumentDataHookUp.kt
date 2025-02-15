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
        val indexColumnLabels = hashMapOf<String, String>(
            "loc" to "Location",
            "lastmod" to "Last modified",
            "changefreq" to "Change frequency",
            "priority" to "Priority",
        )
        val regularColumnLabels = hashMapOf<String, String>(
            "loc" to "Location",
            "lastmod" to "Last modified",
            "changefreq" to "Change frequency",
            "priority" to "Priority",
        )
    }

    override fun buildMarkup(
        sequence: CharSequence,
        requestSource: GridRequestSource
    ): DataMarkup {
        val columns = mutableListOf<GridColumn>(
            DataConsumer.Column(0, indexColumnLabels["loc"], 1, null, null),
            DataConsumer.Column(1, indexColumnLabels["lastmod"], 1, null, null),
            DataConsumer.Column(2, indexColumnLabels["changefreq"], 1, null, null),
            DataConsumer.Column(3, indexColumnLabels["priority"], 1, null, null),
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
                        "lastmod" to "",
                        "changefreq" to "",
                        "priority" to "",
                    )
                    for (xmlTag in it.subTags) {
                        result[xmlTag.name] = when (xmlTag.name) {
                            "loc" -> xmlTag.value.text
                            "lastmod" -> xmlTag.value.text
                            "changefreq" -> xmlTag.value.text
                            "priority" -> xmlTag.value.text
                            else -> continue
                        }
                    }

//                    println("consume ${result}")
                    rows.add(
                        DataConsumer.Row.create(
                            index,
                            arrayOf(
                                result["loc"],
                                result["lastmod"],
                                result["changefreq"],
                                result["priority"]
                            )
                        )
                    )
                    index++
                }
            null
        }

        return SitemapDataMarkup(columns, rows, psiFile)
    }
}
