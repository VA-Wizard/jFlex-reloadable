package org.my.factory;

import org.my.generator.ClassGenerator;
import org.my.generator.JavaGenerator;
import org.my.handler.FlexHandler;

import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class FlexFactory {

    public static FlexHandler newFlexHandler(Reader flexFileReader, Reader lexScannerReader) {
        Writer writer = new StringWriter();
        JavaGenerator.generate(flexFileReader, writer);
        Class<?> aClass = ClassGenerator.compileAndLoadClass(writer.toString());
        try {
            return (FlexHandler) aClass.getConstructor(Reader.class).newInstance(lexScannerReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
