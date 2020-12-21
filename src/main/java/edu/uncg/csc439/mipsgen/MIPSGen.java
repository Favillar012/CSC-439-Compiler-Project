package edu.uncg.csc439.mipsgen;

import edu.uncg.csc439.icode.ICode;
import edu.uncg.csc439.mipsgen.MIPSCode.MIPSLines;

/**
 * The top-level class for generating MIPS target code from intermediate-code.
 * The constructor does all the work, and "toString" gives a string/printable
 * representation.
 */
public class MIPSGen {

    //==================== CLASS VARIABLES ====================

    private String stdFunctions;
    private MIPSLines code;

    //==================== CONSTRUCTOR ====================

    public MIPSGen(ICode iCode) {
        MIPSGenerator generator = new MIPSGenerator(iCode);
        this.code = generator.getProgramLines();
        this.stdFunctions = MIPSReg.stdFunctions();
    }

    //==================== PUBLIC METHODS ====================

    @Override
    public String toString() {
        return this.stdFunctions + this.code.toString();
    }
}
