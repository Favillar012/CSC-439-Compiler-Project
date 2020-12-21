package edu.uncg.csc439.mipsgen;

import edu.uncg.csc439.icode.ICCreation.ICAddress;
import edu.uncg.csc439.mipsgen.MIPSCode.MIPSLines;

import java.util.*;

/**
 * A class for generating and managing MIPS code for a single function. The
 * provided methods keeps track of how much memory has been set aside in
 * the stack frame for temporary variables and allocates new space on demand.
 * The rest of the implementation is up to you!
 * @modifiedBy Fernando Villarreal
 * @date 11/26/2020
 */
public class MIPSFunction {

    //================== CLASS VARIABLES ==================

    // How much memory has been allocated for temp variables? Grow as needed.
    private int tempVarSize;

    // Track which temp variables have been allocated memory, and where they
    // are (offsets). Note that all temp variables are 4 bytes long.
    // (String, Integer) = (Temporary variable name, Memory offset of the variable)
    private final HashMap<String, Integer> tempVarLoc;

    // Name of the function
    private final String name;

    // Function Prologue and Epilogue
    private MIPSLines prologue;
    private MIPSLines epilogue;

    //================== CONSTRUCTORS ==================

    public MIPSFunction(String name) {
        this.name = name;
        this.tempVarSize = 0;
        this.tempVarLoc = new HashMap<>();
        this.prologue = new MIPSLines();
        this.epilogue = new MIPSLines();
    }

    //================== METHODS ==================

    /**
     * Get this function's name.
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get this function's prologue.
     * @return
     */
    public MIPSLines getPrologue() {
        return this.prologue;
    }

    /**
     * Get this function's epilogue.
     * @return
     */
    public MIPSLines getEpilogue() {
        return this.epilogue;
    }

    /**
     * Get the location (or memory offset) of the provided temporary variable name. If the
     * temporary variable is not in the stack, expand the space of temporary variables and
     * add the temporary variable to the end.
     * @param name
     * @return
     */
    public int getTempVariableLocation(ICAddress name) {
        Integer loc = tempVarLoc.get(name.getName());
        if (loc == null) {
            tempVarSize += 4;
            loc = tempVarSize;
            tempVarLoc.put(name.getName(), loc);
        }
        return loc;
    }

    /**
     * Add the temporary variable to memory (The stack frame of this MIPSFunction).
     * @param name
     */
    public void addTempVariable(ICAddress name) {
        this.tempVarSize += 4;
        int loc = this.tempVarSize;
        this.tempVarLoc.put(name.getName(), loc);
    }
}
