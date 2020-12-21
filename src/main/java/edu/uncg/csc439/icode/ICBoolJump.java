package edu.uncg.csc439.icode;

/**
 * This class implements IC conditional jumps on boolean values.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class ICBoolJump extends ICLine {

    //=============== TERMS ===============

    public static final int ifTerm = 1;
    public static final int address_1 = 2;
    public static final int goTo = 3;
    public static final int gotoLabel = 4;

    //=============== CONSTRUCTOR ===============

    public ICBoolJump(boolean ifTest, String address_1, String gotoLabel) {
        super();
        this.addTerm(this.genIfTerm(ifTest));
        this.addTerm(address_1);
        this.addTerm("goto");
        this.addTerm(gotoLabel);
    }

    //=============== METHODS ===============

    public String getAddress_1() {
        return this.getTerm(ICBoolJump.address_1);
    }

    public String getGotoLabel() {
        return this.getTerm(ICBoolJump.gotoLabel);
    }

    public void setAddress_1(String address_1) {
        this.setTerm(ICBoolJump.address_1, address_1);
    }

    public void setGotoLabel(String gotoLabel) {
        this.setTerm(ICBoolJump.gotoLabel, gotoLabel);
    }

    private String genIfTerm(boolean ifTest) {
        if (ifTest) {
            return "if";
        }
        return "ifFalse";
    }
}
