package org.my.example;

import org.my.factory.FlexFactory;
import org.my.handler.FlexHandler;

import java.io.*;

public class Example {

    private static String exampleDigits = "package org.my;\n" +
            "import org.my.handler.FlexHandler;\n" +
            "%%\n" +
            "%class Lexer\n" +
            "%public\n" +
            "%implements FlexHandler\n" +
            "%unicode\n" +
            "%standalone\n" +
            "MyRegex = ([:digit:])+\n" +
            "%%\n" +
            "{MyRegex}                   { return 1; }\n" +
            "[^]                              { break; }";

    private static String exampleLetters = "package org.my;\n" +
            "import org.my.handler.FlexHandler;\n" +
            "%%\n" +
            "%class Lexer\n" +
            "%public\n" +
            "%implements FlexHandler\n" +
            "%unicode\n" +
            "%standalone\n" +
            "MyRegex = ([:letter:])+\n" +
            "%%\n" +
            "{MyRegex}                   { return 1; }\n" +
            "[^]                              { break; }";

    public static void main(String[] args) throws Exception {
        FlexHandler handler = FlexFactory.newFlexHandler(new StringReader(exampleDigits), new StringReader("abcde12345"));
        handler.yylex();
        System.out.println(handler.yytext());   // 12345

        handler = FlexFactory.newFlexHandler(new StringReader(exampleLetters), new StringReader("abcde12345"));
        handler.yylex();
        System.out.println(handler.yytext());   // abcde
    }
}
