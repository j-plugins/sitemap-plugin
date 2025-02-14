package com.github.xepozz.sitemap.editor

import com.intellij.database.datagrid.DocumentDataHookUp
import com.intellij.database.datagrid.GridColumn
import com.intellij.database.datagrid.GridRow
import com.intellij.database.datagrid.ModelIndex
import com.intellij.database.datagrid.mutating.RowMutation
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.xml.XmlFile

class SitemapDataMarkup(
    columns: List<GridColumn>,
    rows: List<GridRow>,
    val myPsiFile: XmlFile,
) : DocumentDataHookUp.DataMarkup(columns, rows) {
    companion object {
        val labels = SitemapDocumentDataHookUp.columnLabelsMapping.entries.associate { (key, value) -> value to key }

    }
    override fun deleteRows(
        session: DocumentDataHookUp.UpdateSession,
        rows: List<GridRow?>
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun insertRow(session: DocumentDataHookUp.UpdateSession): Boolean {
        TODO("Not yet implemented")
    }

    override fun cloneRow(
        session: DocumentDataHookUp.UpdateSession,
        p1: GridRow
    ): Boolean {
        TODO("Not yet implemented")
    }

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
                println("index: $index, rn ${row.rowNum}")

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

                PsiDocumentManager.getInstance(psiFile.project)
                    .commitDocument(psiFile.fileDocument)
            }
        }

        return true
    }

    override fun renameColumn(
        session: DocumentDataHookUp.UpdateSession,
        p1: ModelIndex<GridColumn?>,
        p2: String
    ): Boolean = false
}