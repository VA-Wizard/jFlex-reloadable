/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * JFlex 1.7.0                                                             *
 * Copyright (C) 1998-2018  Gerwin Klein <lsf@jflex.de>                    *
 * All rights reserved.                                                    *
 *                                                                         *
 * License: BSD                                                            *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package org.my;

import jflex.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

public class JavaGenerator {

    /**
     * Generates a scanner for the specified input file.
     */
    public static void generate(Reader input, Writer writer) {

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
            Emitter e = new Emitter(parser, dfa, new PrintWriter(writer));
            e.emit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneratorException();
        }
    }

}
