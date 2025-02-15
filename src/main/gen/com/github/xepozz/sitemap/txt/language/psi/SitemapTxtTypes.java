// This is a generated file. Not intended for manual editing.
package com.github.xepozz.sitemap.txt.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.xepozz.sitemap.txt.language.psi.impl.*;

public interface SitemapTxtTypes {

  IElementType URL = new SitemapTxtElementType("URL");

  IElementType EOL = new SitemapTxtTokenType("EOL");
  IElementType TEXT = new SitemapTxtTokenType("TEXT");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == URL) {
        return new SitemapTxtUrlImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
