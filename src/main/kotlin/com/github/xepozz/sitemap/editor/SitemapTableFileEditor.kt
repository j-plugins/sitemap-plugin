package com.github.xepozz.sitemap.editor

import com.intellij.database.csv.CsvFormatResolver
import com.intellij.database.datagrid.DataGrid
import com.intellij.database.datagrid.DataGridAppearance
import com.intellij.database.datagrid.GridUtil
import com.intellij.database.editor.TableFileEditor
import com.intellij.database.run.ui.TableResultPanel
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findDocument

class SitemapTableFileEditor : TableFileEditor {
    private val myDataGrid: TableResultPanel
    private val hookup
        get() = myDataGrid.dataHookup as SitemapDocumentDataHookUp

    constructor(project: Project, file: VirtualFile) : super(project, file) {
//        val hookUp = GridDataHookUpManager.getInstance(project).getHookUp(file, MyDataLoader(), this)
        val hookUp = SitemapDocumentDataHookUp(project, file.findDocument()!!)
        myDataGrid = createDataGrid(hookUp) as TableResultPanel
//        println("myDataGrid $myDataGrid")
        GridUtil.addGridHeaderComponent(myDataGrid)
    }

     override fun getState(level: FileEditorStateLevel): FileEditorState {
         this.hookup
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

}