package edu.uncg.csc439.icode;

import edu.uncg.csc439.icode.ICCreation.ICGenerator;
import edu.uncg.csc439.syntaxtree.*;

/**
 * Class for intermediate code. The two most important external interfaces to
 * this code are the constructor and the toString method -- see below for more
 * information. You can add other methods, fields, or code as you see fit.
 *
 * @author Fernando Villarreal
 * @date 11/3/2020
 */
public class ICode {

    //=============== CLASS VARIABLES ===============

    private ICLines icLines;

    //=============== CONSTRUCTORS ===============

    /**
     * The constructor takes a syntax tree, and creates some internal
     * representation of intermediate code. While the representation is up
     * to you, I strongly encourage you to keep the code structured. In the
     * final phase you'll have to do some "basic" analysis on the intermediate
     * code in order to produce the target code, and that's hard if you're
     * just storing strings (for example).
     *
     * @param tree the syntax tree for the input program
     */
    public ICode(LCSyntaxTree tree) {
        ICGenerator generator = new ICGenerator(tree);
        this.icLines = generator.getProgramLines();
    }

    //=============== METHODS ===============

    /**
     *
     * @return
     */
    public ICLines getIcLines() {
        return this.icLines;
    }

    /**
     * This performs the important function of turning the internal form
     * of your intermediate code into a text (readable) format.
     *
     * @return the code, as a long multi-line string
     */
    @Override
    public String toString() {
        return icLines.toString();
    }
}
