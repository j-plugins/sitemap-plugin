package com.github.xepozz.sitemap.editor

import com.intellij.configurationStore.deserialize
import com.intellij.configurationStore.serializeObjectInto
import com.intellij.database.csv.*
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.fileEditor.AsyncFileEditorProvider
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.WeighedFileEditorProvider
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager
import com.intellij.openapi.fileEditor.impl.EditorComposite
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.progress.util.BackgroundTaskUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.serialization.SerializationException
import com.intellij.util.containers.ContainerUtil
import org.jdom.Element
import java.util.*

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
