package org.my.example;

import org.my.factory.FlexFactory;
import org.my.handler.FlexHandler;

import java.io.*;

public class Example {

    public static void main(String[] args) throws Exception {
        try(Reader reader = new InputStreamReader(Example.class.getClassLoader().getResourceAsStream("example/example_numbers_0_9.jflex"))){
            FlexHandler handler = FlexFactory.newFlexHandler(reader, new StringReader("0123456789"));
            handler.yylex();
            System.err.println(handler.yytext());
        }

        try(Reader reader = new InputStreamReader(Example.class.getClassLoader().getResourceAsStream("example/example_numbers_0_5.jflex"))){
            FlexHandler handler = FlexFactory.newFlexHandler(reader, new StringReader("0123456789"));
            handler.yylex();
            System.err.println(handler.yytext());
        }

    }
}
