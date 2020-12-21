package edu.uncg.csc439.LCElements;

/**
 * This class implements Little C return statements.
 * @author Fernando Villarreal
 * @date 10/10/2020
 */
public class LCReturn extends LCStmt {

    private LCType rtnValue;

    public LCReturn() {}

    public LCReturn(LCType returnValue) {
        this.rtnValue = returnValue;
    }

    public boolean isRtnValueNull() {
        if (this.rtnValue != null) {
            return false;
        }
        return true;
    }

    public boolean isLCInteger() {
        if (this.rtnValue.isLCInteger()) {
            return true;
        }
        return false;
    }

    public void setReturnValue(LCType returnValue) {
        this.rtnValue = returnValue;
    }

    public LCType getReturnValue() {
        return this.rtnValue;
    }

    @Override
    public String toString() {
        return "return " + this.rtnValue;
    }
}
