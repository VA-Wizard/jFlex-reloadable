package org.my.factory;

import org.my.generator.ClassGenerator;
import org.my.generator.JavaGenerator;
import org.my.handler.FlexHandler;

import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class FlexFactory {

    /**
     * Get generated FlexHandler class from jflex file with specified input reader
     * make sure the flex class implements FlexHandler (there should be '%implements FlexHandler' in options part)
     *
     * @param flexFileReader   jflex source
     * @param lexScannerReader input to parse
     * @return handler to manipulate
     */
    public static FlexHandler newFlexHandler(Reader flexFileReader, Reader lexScannerReader) {
        Writer writer = new StringWriter();
        new JavaGenerator().generate(flexFileReader, writer);
        Class<?> aClass = new ClassGenerator().compileAndLoadClass(writer.toString());
        try {
            return (FlexHandler) aClass.getConstructor(Reader.class).newInstance(lexScannerReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
