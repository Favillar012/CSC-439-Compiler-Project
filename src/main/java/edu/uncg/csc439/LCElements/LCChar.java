package edu.uncg.csc439.LCElements;

/**
 * This class implements a Little C Character.
 * @author Fernando Villarreal
 * @date 10/29/2020
 */
public class LCChar extends LCExprUnit {

    //================= VARIABLES =================

    private char character;

    //================= CONSTRUCTORS =================

    public LCChar(String identifier) {
        super(identifier);
    }

    public LCChar(char character) {
        super();
        this.character = character;
    }

    public LCChar(String identifier, char character) {
        super(identifier);
        this.character = character;
    }

    public LCChar(LCExpr exprValue) {
        super(exprValue);
    }

    public LCChar(String identifier, LCExpr exprValue) {
        super(identifier, exprValue);
    }

    public LCChar(String identifier, LCChar lcChar) {
        super(identifier);
        if (!lcChar.isValueExpr()) {
            this.character = lcChar.getCharacter();
        } else {
            this.setExprValue(lcChar.getExprValue());
        }
    }

    //================= METHODS =================

    public char getCharacter() {
        return this.character;
    }

    public void setCharacter(char character) {
        this.character = character;
        this.valueisNotExpr();
    }

    /**
     * Get the integer value that corresponds to this LCChar. The integer returned will
     * be the ASCII decimal number of the character of LCChar.
     * @return
     */
    public int getValue() {
        int value = this.getCharacter();
        return value;
    }

    @Override
    public String getExprStr() {
        if (!this.isValueExpr()) {
            return "'" + this.character + "'";
        }
        return super.getExprStr();
    }

    @Override
    public String toString() {
        return "char " + this.getIdentifier() + " = '" + this.getCharacter() + "'";
    }
}
