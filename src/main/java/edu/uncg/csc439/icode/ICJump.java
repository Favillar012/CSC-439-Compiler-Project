package edu.uncg.csc439.icode;

/**
 * This class implements an IC unconditional jump instruction.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class ICJump extends ICLine {

    //=============== TERMS ===============

    public static final int goTo = 1;
    public static final int gotoLabel = 2;

    //=============== CONSTRUCTOR ===============

    public ICJump(String gotoLabel) {
        super();
        this.addTerm("goto");
        this.addTerm(gotoLabel);
    }

    //=============== METHODS ===============

    public String getGotoLabel() {
        return this.getTerm(ICJump.gotoLabel);
    }

    public void setGotoLabel(String gotoLabel) {
        this.setTerm(ICJump.gotoLabel, gotoLabel);
    }
}
