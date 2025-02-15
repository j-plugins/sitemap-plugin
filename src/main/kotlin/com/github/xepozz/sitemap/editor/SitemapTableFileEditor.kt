package com.github.xepozz.sitemap.editor

import com.intellij.database.csv.CsvFormatResolver
import com.intellij.database.datagrid.DataGrid
import com.intellij.database.datagrid.DataGridAppearance
import com.intellij.database.datagrid.GridUtil
import com.intellij.database.editor.TableFileEditor
import com.intellij.database.run.ui.TableResultPanel
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.EmptyActionGroup
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findDocument
import com.intellij.ui.components.TwoSideComponent
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.JBUI.CurrentTheme
import javax.swing.JComponent
import javax.swing.border.Border
import javax.swing.border.CompoundBorder

class SitemapTableFileEditor : TableFileEditor {
    private val myDataGrid: TableResultPanel
    private val hookup
        get() = myDataGrid.dataHookup as SitemapDocumentDataHookUp

    constructor(project: Project, file: VirtualFile) : super(project, file) {
//        val hookUp = GridDataHookUpManager.getInstance(project).getHookUp(file, MyDataLoader(), this)
        val hookUp = SitemapDocumentDataHookUp(project, file.findDocument()!!)
        myDataGrid = createDataGrid(hookUp) as TableResultPanel
//        println("myDataGrid $myDataGrid")
        addGridHeaderComponent(myDataGrid)
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


private fun addGridHeaderComponent(dataGrid: DataGrid): JComponent {
    return addGridHeaderComponent(
        dataGrid,
        false,
        "Console.EditorTableResult.Group",
        "Console.TableResult.Group.Secondary"
    )
}

private fun addGridHeaderComponent(
    dataGrid: DataGrid,
    transparent: Boolean,
    actionGroupName: String?,
    secondaryActionsGroupName: String
): JComponent {
    val actionManager = ActionManager.getInstance()
    val actions =
        (if (actionGroupName == null) EmptyActionGroup() else actionManager.getAction(actionGroupName) as ActionGroup?) as ActionGroup
    val secondaryActions = actionManager.getAction(secondaryActionsGroupName) as ActionGroup
    return addGridHeaderComponent(dataGrid, transparent, actions, secondaryActions)
}

private fun addGridHeaderComponent(
    dataGrid: DataGrid,
    transparent: Boolean,
    actions: ActionGroup,
    secondaryActions: ActionGroup
): JComponent {
    val actionManager = ActionManager.getInstance()
    val toolbar = actionManager.createActionToolbar("EditorToolbar", actions, true)
    val toolbarSecondary = actionManager.createActionToolbar("EditorToolbar", secondaryActions, true)
    toolbar.targetComponent = dataGrid.panel.component
    toolbarSecondary.targetComponent = dataGrid.panel.component
    toolbarSecondary.isReservePlaceAutoPopupIcon = false
    val header: JComponent = TwoSideComponent(toolbar.component, toolbarSecondary.component)
    val insets = CurrentTheme.Toolbar.horizontalToolbarInsets()
    val border: Border = if (insets == null) JBUI.Borders.empty(1, 0, 0, 5) else JBUI.Borders.empty(
        insets.top,
        insets.left,
        insets.bottom - 1,
        insets.right
    )
    header.setBorder(CompoundBorder(JBUI.Borders.customLine(CurrentTheme.Editor.BORDER_COLOR, 0, 0, 1, 0), border))
    toolbar.component.setBorder(JBUI.Borders.empty())
    toolbarSecondary.component.setBorder(JBUI.Borders.empty())
    header.setOpaque(!transparent)
    toolbar.component.setOpaque(!transparent)
    toolbarSecondary.component.setOpaque(!transparent)
    dataGrid.panel.topComponent = header
    if (dataGrid.isFilteringSupported) {
        dataGrid.panel.secondTopComponent = dataGrid.filterComponent.component
    }
    return header
}