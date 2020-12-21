package edu.uncg.csc439.LCElements;

/**
 * This class implements a Little C Integer.
 * @author Fernando Villarreal
 * @date 10/29/2020
 */
public class LCInteger extends LCExprUnit {

    //================= VARIABLES =================

    private int value;

    //================= CONSTRUCTORS =================

    public LCInteger(String identifier) {
        super(identifier);
    }

    public LCInteger(int value) {
        super();
        this.value = value;
    }

    public LCInteger(String identifier, int value) {
        super(identifier);
        this.value = value;
    }

    public LCInteger(LCExpr exprValue) {
        super(exprValue);
    }

    public LCInteger(String identifier, LCExpr exprValue) {
        super(identifier, exprValue);
    }

    public LCInteger(String identifier, LCInteger lcInteger) {
        super(identifier);
        if (!lcInteger.isValueExpr()) {
            this.value = lcInteger.getValue();
        } else {
            this.setExprValue(lcInteger.getExprValue());
        }
    }

    //================= METHODS =================

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
        this.valueisNotExpr();
    }

    @Override
    public String getExprStr() {
        if (!this.isValueExpr()) {
            return "" + this.value;
        }
        return super.getExprStr();
    }

    @Override
    public String toString() {
        return "int " + this.getIdentifier() + " = " + this.getValue();
    }
}
