// This is a generated file. Not intended for manual editing.
package com.github.xepozz.sitemap.txt.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xepozz.sitemap.txt.language.psi.SitemapTxtTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.github.xepozz.sitemap.txt.language.psi.*;

public class SitemapTxtUrlImpl extends ASTWrapperPsiElement implements SitemapTxtUrl {

  public SitemapTxtUrlImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SitemapTxtVisitor visitor) {
    visitor.visitUrl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SitemapTxtVisitor) accept((SitemapTxtVisitor)visitor);
    else super.accept(visitor);
  }

}
