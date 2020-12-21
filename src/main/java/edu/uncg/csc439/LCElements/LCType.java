package edu.uncg.csc439.LCElements;

/**
 * This class implements a Little C Type. A Little C Type can be an integer, a function,
 * a character, or an array.
 * @author Fernando Villarreal
 * @date 10/17/2020
 */
public class LCType implements Comparable<LCType> {

    //================= VARIABLES =================

    private String identifier;

    //================= CONSTRUCTORS =================

    public LCType(String id) {
        this.identifier = id;
    }

    public LCType() {}

    //================= METHODS =================

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String newID) {
        this.identifier = newID;
    }

    /**
     * Checks whether this LCType has an identifier or not. LCTypes without identifiers
     * correspond to literal values.
     * @return
     */
    public boolean hasIdentifier() {
        if (this.identifier != null) {
            if (!this.identifier.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this LCType object is an instance of an LCInteger.
     * @return
     */
    public boolean isLCInteger() {
        if (this instanceof LCInteger) {
            return true;
        }
        return false;
    }

    /**
     * Checks if this LCType object is an instance of an LCChar.
     * @return
     */
    public boolean isLCChar() {
        if (this instanceof LCChar) {
            return true;
        }
        return false;
    }

    /**
     * Checks if this LCType object is an instance of an LCCharArray.
     * @return
     */
    public boolean isLCCharArray() {
        if (this instanceof LCCharArray) {
            return true;
        }
        return false;
    }

    /**
     * Checks if this LCType object is an instance of an LCIntArray.
     * @return
     */
    public boolean isLCIntArray() {
        if (this instanceof LCIntArray) {
            return true;
        }
        return false;
    }

    /**
     * Checks if this LCType object is an instance of an LCFunction.
     * @return
     */
    public boolean isLCFunction() {
        if (this instanceof LCFunction) {
            return true;
        }
        return false;
    }

    /**
     * Checks if this LCType object is an instance of an LCFuncCall.
     * @return
     */
    public boolean isLCFuncCall() {
        if (this instanceof LCFuncCall) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ID='" + this.identifier + "'";
    }

    @Override
    public int compareTo(LCType other) {
        return this.identifier.compareTo(other.getIdentifier());
    }
}
