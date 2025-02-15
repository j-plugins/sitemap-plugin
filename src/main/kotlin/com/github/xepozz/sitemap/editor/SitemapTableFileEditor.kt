package com.github.xepozz.sitemap.editor

import com.intellij.database.csv.CsvFormat
import com.intellij.database.csv.CsvFormatEditor
import com.intellij.database.csv.CsvFormatResolver
import com.intellij.database.csv.CsvFormatter
import com.intellij.database.datagrid.CsvDocumentDataHookUp
import com.intellij.database.datagrid.DataGrid
import com.intellij.database.datagrid.DataGridAppearance
import com.intellij.database.datagrid.GridDataHookUpManager
import com.intellij.database.datagrid.GridRequestSource
import com.intellij.database.datagrid.GridUtil
import com.intellij.database.datagrid.ScriptedGridDataHookUp
import com.intellij.database.editor.TableFileEditor
import com.intellij.database.extensions.DataConsumer
import com.intellij.database.loaders.DataLoader
import com.intellij.database.run.ui.DataGridRequestPlace
import com.intellij.database.vfs.fragment.CsvTableDataFragmentFile
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findDocument
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.xmlb.XmlSerializer
import com.intellij.database.run.ui.TableResultPanel

class SitemapTableFileEditor : TableFileEditor, CsvFormatEditor {
    private val myDataGrid: TableResultPanel

    constructor(project: Project, file: VirtualFile) : super(project, file) {
//        val hookUp = GridDataHookUpManager.getInstance(project).getHookUp(file, MyDataLoader(), this)
        val hookUp = SitemapDocumentDataHookUp(project, file.findDocument()!!, file)
        myDataGrid = createDataGrid(hookUp) as TableResultPanel
//        println("myDataGrid $myDataGrid")
        GridUtil.addGridHeaderComponent(myDataGrid)
    }

     override fun getState(level: FileEditorStateLevel): FileEditorState {
        val hookup = this.hookup
//         println("getState $hookup")
        return FileEditorState.INSTANCE
    }

    protected override fun configure(grid: DataGrid, appearance: DataGridAppearance) {
//        println("configure $grid $appearance")
        GridUtil.configureCsvTable(grid, appearance)
    }

    override fun setState(state: FileEditorState) {
        CsvFormatResolver.readCsvFormat(state)
    }

    override fun getDataGrid() = myDataGrid

    private val hookup
        get() = myDataGrid.dataHookup as SitemapDocumentDataHookUp

    override fun firstRowIsHeader() = false

    override fun setFirstRowIsHeader(value: Boolean){}
}