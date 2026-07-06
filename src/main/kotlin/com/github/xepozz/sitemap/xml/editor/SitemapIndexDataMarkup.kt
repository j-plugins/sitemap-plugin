package com.github.xepozz.sitemap.xml.editor

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
    private val fieldKeys: List<String>,
) : DocumentDataHookUp.DataMarkup(columns, rows) {
    companion object {
        internal val columnLabels = linkedMapOf(
            "loc" to "Location",
            "lastmod" to "Last modified",
            "changefreq" to "Change frequency",
            "priority" to "Priority",
        )

        internal val urlSetFieldKeys = listOf("loc", "lastmod", "changefreq", "priority")
        internal val sitemapIndexFieldKeys = listOf("loc", "lastmod")

        private val labels = columnLabels.entries.associate { (key, value) -> value to key }

        internal fun fieldKeysFor(isIndex: Boolean) = if (isIndex) sitemapIndexFieldKeys else urlSetFieldKeys

        internal fun deletionOrder(indices: Iterable<Int>) = indices.sortedDescending()

        internal fun cellText(value: Any?) = value?.toString().orEmpty()

        internal fun isIndexRoot(rootName: String): Boolean? = when (rootName) {
            "sitemapindex" -> true
            "urlset" -> false
            else -> null
        }

        fun build(project: Project, document: Document): DocumentDataHookUp.DataMarkup? {
            val psiFile = getPsiFile(project, document) ?: return null
            val isIndex = ReadAction.compute<Boolean?, Exception> {
                psiFile.rootTag?.localName?.let(::isIndexRoot)
            } ?: return null

            return build(psiFile, isIndex)
        }

        fun build(project: Project, document: Document, isIndex: Boolean): DocumentDataHookUp.DataMarkup? {
            val psiFile = getPsiFile(project, document) ?: return null
            return build(psiFile, isIndex)
        }

        private fun build(psiFile: XmlFile, isIndex: Boolean): DocumentDataHookUp.DataMarkup {
            val fieldKeys = fieldKeysFor(isIndex)
            val columns = fieldKeys.mapIndexed { index, fieldKey ->
                DataConsumer.Column(index, columnLabels.getValue(fieldKey), 1, null, null)
            }

            var index = 0
            val rows = mutableListOf<GridRow>()

            val subTagName = if (isIndex) "sitemap" else "url"

            ReadAction.compute<Void, Exception> {
                psiFile.rootTag
                    ?.findSubTags(subTagName)
                    ?.forEach {
                        val result = fieldKeys.associateWith { "" }.toMutableMap()
                        for (xmlTag in it.subTags) {
                            if (xmlTag.name !in fieldKeys) continue
                            result[xmlTag.name] = xmlTag.value.text
                        }

                        rows.add(
                            DataConsumer.Row.create(
                                index,
                                fieldKeys.map { result[it] }.toTypedArray()
                            )
                        )
                        index++
                    }
                null
            }

            return SitemapIndexDataMarkup(columns, rows, psiFile, subTagName, fieldKeys)
        }

        private fun getPsiFile(
            project: Project,
            document: Document
        ): XmlFile? {
            return ReadAction.compute<XmlFile?, Exception> {
                PsiDocumentManager.getInstance(project).getPsiFile(document) as? XmlFile
            }
        }
    }

    override fun deleteRows(
        session: DocumentDataHookUp.UpdateSession,
        rows: List<GridRow>
    ): Boolean {
        val psiFile = myPsiFile

        com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(psiFile.project) {
            val subTags = psiFile.rootTag?.findSubTags(subTagName) ?: return@runWriteCommandAction
            deletionOrder(rows.map { GridRow.toRealIdx(it) })
                .mapNotNull { subTags.getOrNull(it) }
                .forEach { it.delete() }
        }

        return true
    }

    override fun insertRow(session: DocumentDataHookUp.UpdateSession): Boolean {
        val psiFile = myPsiFile
        val rootTag = psiFile.rootTag ?: return false

        com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(psiFile.project) {
            val newChild = rootTag.createChildTag(subTagName, rootTag.namespace, "", true)
            rootTag.addSubTag(newChild, false)
        }

        return true
    }

    override fun cloneRow(
        session: DocumentDataHookUp.UpdateSession,
        row: GridRow
    ): Boolean {
        val psiFile = myPsiFile
        val rootTag = psiFile.rootTag ?: return false

        com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(psiFile.project) {
            val newChild = rootTag.createChildTag(subTagName, rootTag.namespace, "", true)
            fieldKeys.forEachIndexed { index, fieldKey ->
                newChild.add(
                    newChild.createChildTag(fieldKey, newChild.namespace, cellText(row.getValue(index)), true)
                )
            }
            rootTag.addSubTag(newChild, false)
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
                val index = GridRow.toRealIdx(row)

                val xmlTag = psiFile.rootTag
                    ?.findSubTags(subTagName)
                    ?.getOrNull(index) ?: return@forEach

                for (queryData in mutated.data) {
                    val columnName = queryData.column.name
                    val attributeName = labels[columnName]
                        ?.takeIf { it in fieldKeys }
                        ?: continue
                    val value = cellText(queryData.`object`)

                    val attribute = xmlTag.findFirstSubTag(attributeName)
                    if (attribute != null) {
                        attribute.value.text = value
                    } else {
                        val newChild = xmlTag.createChildTag(attributeName, xmlTag.namespace, value, true)
                        xmlTag.addSubTag(newChild, false)
                    }
                }
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
