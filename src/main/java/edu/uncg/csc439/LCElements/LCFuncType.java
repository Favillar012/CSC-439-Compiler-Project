package edu.uncg.csc439.LCElements;

/**
 * This class for functions encapsulates the text for VOID, INT, and CHAR Lexer tokens. It extends LCType but does
 * not use the identifier variable.
 * @author Fernando Villarreal
 * @date 10/3/2020
 */
public class LCFuncType extends LCType {

    private final static String intType = "int";
    private final static String charType = "char";
    private final static String voidType = "void";

    private final String funcType;

    public LCFuncType(String funcType) {
        super();
        this.funcType = funcType;
    }

    public String getfuncType() {
        return this.funcType;
    }

    public boolean isVoidType() {
        if (this.funcType.compareTo(LCFuncType.voidType) == 0) {
            return true;
        }
        return false;
    }

    public boolean isIntType() {
        if (this.funcType.compareTo(LCFuncType.intType) == 0) {
            return true;
        }
        return false;
    }

    public boolean isCharType() {
        if (this.funcType.compareTo((LCFuncType.charType)) == 0) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "func_Type='" + this.funcType + "'";
    }
}
