package org.my.generator;

import jflex.*;

import java.io.*;
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
            hackEmitter(e, writer);
            e.emit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneratorException();
        }
    }

    private void hackEmitter(Emitter e, Writer writer) {
        try {
            PrintWriter printWriter = new PrintWriter(writer);
            Field out = e.getClass().getDeclaredField("out");
            out.setAccessible(true);
            Writer o = (Writer) out.get(e);
            o.close();
            out.set(e, printWriter);

            Field f1 = e.getClass().getDeclaredField("skel");
            f1.setAccessible(true);
            f1.set(e, new Skeleton(printWriter));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
