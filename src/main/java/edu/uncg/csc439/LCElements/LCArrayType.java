package edu.uncg.csc439.LCElements;

/**
 * This class for arrays encapsulates the text for the following lexer token streams:
 * 'INT LBK RBK' and 'CHAR LBK RBK'. It extends LCType but does not use the identifier variable.
 * not use the identifier variable.
 * @author Fernando Villarreal
 * @date 10/19/2020
 */
public class LCArrayType extends LCType {

    private final static String charArType = "char[]";
    private final static String intArType = "int[]";

    private final String arrayType;

    public LCArrayType(String arrayType) {
        super();
        this.arrayType = arrayType;
    }

    public String getArrayType() {
        return this.arrayType;
    }

    public boolean isIntArType() {
        if (this.arrayType.compareTo(LCArrayType.intArType) == 0) {
            return true;
        }
        return false;
    }

    public boolean isCharArType() {
        if (this.arrayType.compareTo(LCArrayType.charArType) == 0) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Array_Type='" + this.arrayType + "'";
    }
}
