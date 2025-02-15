package com.github.xepozz.sitemap.xml

import com.github.xepozz.sitemap.SitemapIcons
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

class SitemapFileAction : CreateFileFromTemplateAction("Sitemap", "Creates a new Sitemap file", SitemapIcons.FILE) {
    override fun getActionName(directory: PsiDirectory, name: String, templateName: String) = "Creating Sitemap File"

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("New Sitemap File")
            .addKind("Sitemap", SitemapIcons.FILE, "Sitemap Example")
            .addKind("Sitemap index", SitemapIcons.FILE, "Sitemap Index Example")
            .setDefaultText("sitemap")
    }

    override fun createFile(name: String, templateName: String, dir: PsiDirectory): PsiFile? {
        val name = if (name.endsWith(".xml")) name else "$name.xml"
        return super.createFile(name, templateName, dir)
    }
}