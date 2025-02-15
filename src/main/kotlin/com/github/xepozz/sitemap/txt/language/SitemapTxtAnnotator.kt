package com.github.xepozz.sitemap.txt.language

import com.github.xepozz.sitemap.txt.language.psi.SitemapTxtUrl
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

class SitemapTxtAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is SitemapTxtUrl -> {
                if (!element.text.startsWith("http://") && !element.text.startsWith("https://")) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "URL must be started with http:// or https://")
                        .range(element.textRange)
                        .create()
                }
            }
        }
    }
}