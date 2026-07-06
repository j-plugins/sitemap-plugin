package com.github.xepozz.sitemap.xml.editor

import com.intellij.configurationStore.serializeObjectInto
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.WeighedFileEditorProvider
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import org.jdom.Element

class SitemapTableFileEditorProvider : WeighedFileEditorProvider(), DumbAware {
    override fun getWeight() = 10.0

    override fun accept(project: Project, file: VirtualFile): Boolean {
        if (!acceptsSitemapXml(file.name, file.extension) || file.fileType.isBinary) {
            return false
        }

        return isSupportedSitemapXml(project, file)
    }

    override fun acceptRequiresReadAction() = true

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        val document = FileDocumentManager.getInstance().getDocument(file)
            ?: return TextEditorProvider.getInstance().createEditor(project, file)

        return SitemapTableFileEditor(project, file, document)
    }

    override fun readState(sourceElement: Element, project: Project, file: VirtualFile) = FileEditorState.INSTANCE

    override fun writeState(state: FileEditorState, project: Project, targetElement: Element) {
        serializeObjectInto(state, targetElement)
    }

    override fun getPolicy(): FileEditorPolicy {
        return if (Registry.`is`("sitemap.open.as.tables.by.default", true)) {
            FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR
        } else {
            FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
        }
    }

    override fun getEditorTypeId() = "sitemap editor"

    companion object {
        internal fun acceptsSitemapXml(name: String, extension: String?) =
            extension.equals("xml", ignoreCase = true) && name.contains("sitemap", ignoreCase = true)

        internal fun isSupportedRoot(rootName: String) = SitemapIndexDataMarkup.isIndexRoot(rootName) != null

        private fun isSupportedSitemapXml(project: Project, file: VirtualFile): Boolean {
            val psiFile = PsiManager.getInstance(project).findFile(file) as? XmlFile ?: return false
            return psiFile.rootTag?.localName?.let(::isSupportedRoot) == true
        }
    }
}
//DatabaseElementVirtualFileImpl
//BackendFileEditorProviderSuppressor
