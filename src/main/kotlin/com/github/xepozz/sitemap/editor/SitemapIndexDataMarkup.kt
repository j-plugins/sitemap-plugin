package com.github.xepozz.sitemap.editor

import com.intellij.database.datagrid.DataConsumer
import com.intellij.database.datagrid.DocumentDataHookUp
import com.intellij.database.datagrid.GridColumn
import com.intellij.database.datagrid.GridRow
import com.intellij.database.datagrid.ModelIndex
import com.intellij.database.datagrid.mutating.RowMutation
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.xml.XmlFile

class SitemapIndexDataMarkup(
    columns: List<GridColumn>,
    rows: List<GridRow>,
    val myPsiFile: XmlFile,
    val subTagName: String,
) : DocumentDataHookUp.DataMarkup(columns, rows) {
    companion object {
        val columnLabels = hashMapOf<String, String>(
            "loc" to "Location",
            "lastmod" to "Last modified",
            "changefreq" to "Change frequency",
            "priority" to "Priority",
        )
        val labels = columnLabels.entries.associate { (key, value) -> value to key }
        fun build(project: Project, document: Document, isIndex: Boolean): DocumentDataHookUp.DataMarkup {
            val columns = mutableListOf<GridColumn>(
                DataConsumer.Column(0, columnLabels["loc"], 1, null, null),
                DataConsumer.Column(1, columnLabels["lastmod"], 1, null, null),
                DataConsumer.Column(2, columnLabels["changefreq"], 1, null, null),
                DataConsumer.Column(3, columnLabels["priority"], 1, null, null),
            )

            var index = 0
            val rows = mutableListOf<GridRow>()

            val psiFile = getPsiFile(project, document)

            val subTagName = if (isIndex) "sitemap" else "url"

            println("build markup ")
            ReadAction.compute<Void, Exception> {
                psiFile.rootTag
                    ?.findSubTags(subTagName)
                    ?.forEach {
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

            return SitemapIndexDataMarkup(columns, rows, psiFile, subTagName)
        }

        private fun getPsiFile(
            project: Project,
            document: Document
        ): XmlFile {
            return ReadAction.compute<XmlFile, Exception> {
                PsiDocumentManager.getInstance(project).getPsiFile(document) as XmlFile
            }
        }
    }

    override fun deleteRows(
        session: DocumentDataHookUp.UpdateSession,
        rows: List<GridRow>
    ): Boolean {
        val psiFile = myPsiFile

        com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(psiFile.project) {
            rows.forEach { row ->
                val index = GridRow.toRealIdx(row)
                val xmlTag = psiFile.rootTag
                    ?.subTags
                    ?.get(index) ?: return@forEach
                xmlTag.delete()
            }
        }

        return true
    }

    override fun insertRow(session: DocumentDataHookUp.UpdateSession): Boolean {
        val psiFile = myPsiFile
        val rootTag = psiFile.rootTag!!

        com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(psiFile.project) {
//            println("rootTag after: ${rootTag.text}")
            val newChild = rootTag.createChildTag(subTagName, rootTag.namespace, "", true)
//            println("new child: ${newChild}")
            rootTag.addSubTag(newChild, false)
//            println("rootTag after: ${rootTag.text}")
        }

        return true
    }

    override fun cloneRow(
        session: DocumentDataHookUp.UpdateSession,
        row: GridRow
    ): Boolean {
        val psiFile = myPsiFile
        val rootTag = psiFile.rootTag!!

        com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(psiFile.project) {
//            println("rootTag after: ${rootTag.text}")
            val newChild = rootTag.createChildTag(subTagName, "", "", true)
            newChild.apply {
                add(createChildTag("loc", "", row.getValue(0) as? String ?: "", true))
                add(createChildTag("lastmod", "", row.getValue(1) as? String ?: "", true))
                add(createChildTag("changefreq", "", row.getValue(2) as? String ?: "", true))
                add(createChildTag("priority", "", row.getValue(3) as? String ?: "", true))
            }
//            println("new child: ${newChild}")
            rootTag.addSubTag(newChild, false)
//            println("rootTag after: ${rootTag.text}")
        }

        return true
    }

    override fun update(
        session: DocumentDataHookUp.UpdateSession,
        mutations: List<RowMutation>
    ): Boolean {
        val psiFile = myPsiFile

        com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(psiFile.project) {
            mutations.forEach { mutated ->

                val row = mutated.row
                println("row: $row")
                val index = GridRow.toRealIdx(row)
                println("index: $index")

                val xmlTag = psiFile.rootTag
                    ?.subTags
                    ?.get(index) ?: return@forEach

                println("xmlTag before: ${xmlTag.text}")

                val data = mutated.data
                println("data $data")
                for (queryData in mutated.data) {
//                    val loc = row.getValue(queryData.column.columnNumber) as String
                    val columnName = queryData.column.name
                    println("columnName: ${columnName}")
                    val value = queryData.`object` as String
                    println("value: ${value}")

                    val attributeName = labels[columnName]
                    println("attributeName: ${attributeName}")

                    val attribute = xmlTag.findFirstSubTag(attributeName)
                    if (attribute != null) {
                        println("attribute: ${attribute}")

                        attribute.value.text = value
                    } else {
                        val newChild = xmlTag.createChildTag(attributeName, xmlTag.namespace, value, true)
                        println("new child: ${newChild}")
                        xmlTag.addSubTag(newChild, false)
                    }
                }

                println("xmlTag after: ${xmlTag.text}")
            }
        }

        return true
    }

    override fun renameColumn(
        session: DocumentDataHookUp.UpdateSession,
        p1: ModelIndex<GridColumn?>,
        p2: String
    ): Boolean = false

    override fun deleteColumns(
        session: DocumentDataHookUp.UpdateSession,
        p1: List<GridColumn?>
    ): Boolean = false

    override fun insertColumn(
        session: DocumentDataHookUp.UpdateSession,
        p1: String?
    ): Boolean = false

    override fun cloneColumn(
        session: DocumentDataHookUp.UpdateSession,
        p1: GridColumn
    ): Boolean = false
}