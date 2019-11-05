/* JFlex example: partial Java language lexer specification */
/*
    package and import statements
*/
package org.my;
/**
 * This is a simple example lexer.
 */
import org.my.handler.FlexHandler;
%%

%class Lexer
%public
%extends FlexHandler
%unicode
%standalone
%line
%column

%{

%}

MyRegex = [0-5]+

%%

<YYINITIAL> {

  {MyRegex}                   { return 1; }
}

/* error fallback */
[^]                              { throw new Error("Illegal character <"+
                                                    yytext()+">"); }