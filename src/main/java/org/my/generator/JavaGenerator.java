package org.my.generator;

import jflex.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;

public class JavaGenerator {

    /**
     * Generates a scanner for the specified input file and write to writer
     */
    public void generate(Reader input, Writer writer) {

        Out.resetCounters();

        LexScan scanner = new LexScan(input);
        scanner.setFile(new File(""));
        LexParse parser = new LexParse(scanner);

        try {
            NFA nfa = (NFA) parser.parse().value;
            Out.checkErrors();
            DFA dfa = nfa.getDFA();
            dfa.checkActions(scanner, parser);
            nfa = null;
            Out.checkErrors();
            dfa.minimize();
            Emitter e = new Emitter(new File(""), parser, dfa);
            PrintWriter printWriter = new PrintWriter(writer);
            setPrivateField(e, "out", printWriter);
            setPrivateField(e, "skel", new Skeleton(printWriter));
            e.emit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneratorException();
        }
    }

    private void setPrivateField(Emitter e, String fieldName, Object o) {
        try {
            Field out = e.getClass().getDeclaredField(fieldName);
            out.setAccessible(true);
            out.set(e, o);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
