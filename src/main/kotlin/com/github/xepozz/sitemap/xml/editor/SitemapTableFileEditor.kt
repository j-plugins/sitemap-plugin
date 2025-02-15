package com.github.xepozz.sitemap.xml.editor

import com.intellij.database.data.types.BaseConversionGraph
import com.intellij.database.datagrid.DataGrid
import com.intellij.database.datagrid.DataGridAppearance
import com.intellij.database.datagrid.GridHelper
import com.intellij.database.datagrid.GridHelperImpl
import com.intellij.database.editor.TableFileEditor
import com.intellij.database.extractors.BaseObjectFormatter
import com.intellij.database.extractors.FormatterCreator
import com.intellij.database.run.ui.TableResultPanel
import com.intellij.database.run.ui.grid.editors.FormatsCache
import com.intellij.database.run.ui.grid.editors.GridCellEditorFactoryImpl
import com.intellij.database.run.ui.grid.editors.GridCellEditorFactoryProvider
import com.intellij.database.run.ui.grid.editors.GridCellEditorHelper
import com.intellij.database.run.ui.grid.editors.GridCellEditorHelperImpl
import com.intellij.database.run.ui.grid.renderers.DefaultBooleanRendererFactory
import com.intellij.database.run.ui.grid.renderers.DefaultNumericRendererFactory
import com.intellij.database.run.ui.grid.renderers.DefaultTextRendererFactory
import com.intellij.database.run.ui.grid.renderers.GridCellRendererFactories
import com.intellij.database.settings.DataGridAppearanceSettings
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.EmptyActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findDocument
import com.intellij.ui.components.TwoSideComponent
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.JBUI.CurrentTheme
import java.util.function.Function
import java.util.function.Supplier
import javax.swing.JComponent
import javax.swing.border.Border
import javax.swing.border.CompoundBorder

class SitemapTableFileEditor : TableFileEditor {
    private val myDataGrid: TableResultPanel

    constructor(project: Project, file: VirtualFile) : super(project, file) {
//        val hookUp = GridDataHookUpManager.getInstance(project).getHookUp(file, MyDataLoader(), this)
        val hookUp = SitemapDocumentDataHookUp(project, file.findDocument()!!)
        myDataGrid = createDataGrid(hookUp) as TableResultPanel
//        println("myDataGrid $myDataGrid")
        addGridHeaderComponent(myDataGrid)
    }

    protected override fun configure(grid: DataGrid, appearance: DataGridAppearance) {
//        println("configure $grid $appearance")
        configureTableEditor(grid, appearance)
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


fun configureTableEditor(grid: DataGrid, appearance: DataGridAppearance) {
    GridCellEditorHelper.set(grid, GridCellEditorHelperImpl())
    GridHelper.set(grid, GridHelperImpl())
    GridCellEditorFactoryProvider.set(grid, GridCellEditorFactoryImpl.getInstance())
    val factories = listOf(
        DefaultBooleanRendererFactory(grid),
        DefaultNumericRendererFactory(grid),
        DefaultTextRendererFactory(grid)
    )
    GridCellRendererFactories.set(grid, GridCellRendererFactories(factories))
    val formatter = BaseObjectFormatter()
    grid.setObjectFormatterProvider(Function { dataGrid: DataGrid? -> formatter })
    BaseConversionGraph.set(
        grid,
        BaseConversionGraph(FormatsCache(), FormatterCreator.get(grid), Supplier { grid.objectFormatter })
    )
    appearance.setResultViewShowRowNumbers(true)
    appearance.booleanMode = DataGridAppearanceSettings.getSettings().booleanMode
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