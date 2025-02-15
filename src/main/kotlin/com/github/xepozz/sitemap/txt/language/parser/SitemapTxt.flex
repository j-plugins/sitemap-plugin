package com.github.xepozz.sitemap.txt.language.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.github.xepozz.sitemap.txt.language.psi.SitemapTxtTypes;

%%
%class SitemapTxtLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}


NEWLINE=\r|\n|\r\n
WHITESPACE=[ \t]+

%%

<YYINITIAL> {
    [^\s][^\n]*   { return SitemapTxtTypes.TEXT; }
}

{WHITESPACE}      { return TokenType.WHITE_SPACE; }
{NEWLINE}         { yybegin(YYINITIAL); return SitemapTxtTypes.EOL; }
