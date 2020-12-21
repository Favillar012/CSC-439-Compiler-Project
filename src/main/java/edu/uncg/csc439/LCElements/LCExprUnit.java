package edu.uncg.csc439.LCElements;

/**
 * This class acts as a superclass to the basic units of a Little C expression,
 * these units are: LCInteger, LCChar, LCCharArray, and LCIntArray.
 * @author Fernando Villarreal
 * @date 10/29/2020
 */
public class LCExprUnit extends LCType {

    //========== CLASS VARIABLES ==========

    private LCExpr exprValue;
    private boolean valueIsExpr;

    //========== CONSTRUCTORS ==========

    public LCExprUnit(String identifier) {
        super(identifier);
        this.valueIsExpr = false;
    }

    public LCExprUnit() {
        super();
        this.valueIsExpr = false;
    }

    public LCExprUnit(String identifier, LCExpr exprValue) {
        super(identifier);
        this.exprValue = exprValue;
        this.valueIsExpr = true;
    }

    public LCExprUnit(LCExpr exprValue) {
        super();
        this.exprValue = exprValue;
        this.valueIsExpr = true;
    }

    //========== METHODS ==========

    public void setExprValue(LCExpr exprValue) {
        this.exprValue = exprValue;
        this.valueIsExpr = true;
    }

    public LCExpr getExprValue() {
        return this.exprValue;
    }

    public String getExprStr() {
        return this.exprValue.getExprStr();
    }

    /**
     * Checks if the value of this LCExprUnit is an expression instead of a literal
     * integer, character, character array, or integer array.
     * @return
     */
    public boolean isValueExpr() {
        return this.valueIsExpr;
    }

    protected void valueisNotExpr() {
        this.valueIsExpr = false;
    }
}
