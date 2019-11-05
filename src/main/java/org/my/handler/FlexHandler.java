package org.my.handler;

public interface FlexHandler {

    /**
     * Enters a new lexical state
     *
     * @param newState the new lexical state
     */
    void yybegin(int newState);


    /**
     * Returns the current lexical state.
     */
    int yystate();


    /**
     * Returns the text matched by the current regular expression.
     */
    String yytext();


    /**
     * Returns the character at position <tt>pos</tt> from the
     * matched text.
     * <p>
     * It is equivalent to yytext().charAt(pos), but faster
     *
     * @param pos the position of the character to fetch.
     *            A value from 0 to yylength()-1.
     * @return the character at position pos
     */
    char yycharat(int pos);


    /**
     * Returns the length of the matched text region.
     */
    int yylength();


    /**
     * Resumes scanning until the next regular expression is matched,
     * the end of input is encountered or an I/O-Error occurs.
     *
     * @return the next token
     * @throws java.io.IOException if any I/O-Error occurs
     */
    int yylex() throws java.io.IOException;


    /**
     * Pushes the specified amount of characters back into the input stream.
     * <p>
     * They will be read again by then next call of the scanning method
     *
     * @param number the number of characters to be read again.
     *               This number must not be greater than yylength()!
     */
    void yypushback(int number);


    /**
     * Resets the scanner to read from a new input stream.
     * Does not close the old reader.
     * <p>
     * All internal variables are reset, the old input stream
     * <b>cannot</b> be reused (internal buffer is discarded and lost).
     * Lexical state is set to <tt>ZZ_INITIAL</tt>.
     * <p>
     * Internal scan buffer is resized down to its initial length, if it has grown.
     *
     * @param reader the new input stream
     */
    void yyreset(java.io.Reader reader);


    /**
     * Closes the input stream.
     */
    void yyclose() throws java.io.IOException;
}
