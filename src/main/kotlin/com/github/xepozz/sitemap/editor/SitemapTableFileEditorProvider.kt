package com.github.xepozz.sitemap.editor

import com.intellij.configurationStore.deserialize
import com.intellij.configurationStore.serializeObjectInto
import com.intellij.database.csv.CsvFormatResolver
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.WeighedFileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.serialization.SerializationException
import org.jdom.Element

class SitemapTableFileEditorProvider : WeighedFileEditorProvider(), DumbAware {
    override fun getWeight() = 10.0

    override fun accept(project: Project, file: VirtualFile) = file.extension == "xml" && file.name.contains("sitemap")

    override fun acceptRequiresReadAction() = false

    override fun createEditor(project: Project, file: VirtualFile) = SitemapTableFileEditor(project, file)

    override fun readState(sourceElement: Element, project: Project, file: VirtualFile): FileEditorState {
        var state: FileEditorState? = null
        try {
            println("readState $sourceElement")
            state = sourceElement.deserialize(CsvFormatResolver.State::class.java)
            println("readState result $state")
        } catch (ignore: SerializationException) {
        }
        return state ?: FileEditorState.INSTANCE
    }

    override fun writeState(state: FileEditorState, project: Project, targetElement: Element) {
        println("writeState $state, $targetElement")
        if (state is CsvFormatResolver.State) {
            serializeObjectInto(state, targetElement)
        }
    }

    override fun getPolicy(): FileEditorPolicy {
//        println("getPolicy, ${Registry.`is`("sitemap.open.as.tables.by.default", true)}")
        // todo change to false by default
        return if (Registry.`is`("sitemap.open.as.tables.by.default", true)) {
            FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR
        } else {
            FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
        }
    }

    override fun getEditorTypeId() = "sitemap editor"
}
//DatabaseElementVirtualFileImpl
//BackendFileEditorProviderSuppressor
