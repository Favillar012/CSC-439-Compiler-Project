package edu.uncg.csc439.LCElements;

/**
 * This class is used to contain the text on a Little C expression. Note that it
 * does not extend LCType.
 * @author Fernando Villarreal
 * @date 10/26/2020
 */
public class LCExpr {

    private final String expr;

    public LCExpr(String expr) {
        this.expr = expr;
    }

    public String getExprStr() {
        return this.expr;
    }

    @Override
    public String toString() {
        return "LCExpr = \"" + expr + "\"";
    }
}
