package edu.uncg.csc439.icode;

/**
 * This class implements IC conditional jumps on relational values.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class ICReltJump extends ICLine {

    //=============== TERMS ===============

    public static final int ifTerm = 1;
    public static final int address_1 = 2;
    public static final int relOp = 3;
    public static final int address_2 = 4;
    public static final int goTo = 5;
    public static final int gotoLabel = 6;

    //=============== CONSTRUCTOR ===============

    public ICReltJump(boolean ifTest, String address_1, String relOp, String address_2, String gotoLabel) {
        super();
        this.addTerm(this.genIfTerm(ifTest));
        this.addTerm(address_1);
        this.addTerm(relOp);
        this.addTerm(address_2);
        this.addTerm("goto");
        this.addTerm(gotoLabel);
    }

    //=============== METHODS ===============

    public String getIfTerm() {
        return this.getTerm(ICReltJump.ifTerm);
    }

    public String getAddress_1() {
        return this.getTerm(ICReltJump.address_1);
    }

    public String getRelOp() {
        return this.getTerm(ICReltJump.relOp);
    }

    public String getAddress_2() {
        return this.getTerm(ICReltJump.address_2);
    }

    public String getGotoLabel() {
        return this.getTerm(ICReltJump.gotoLabel);
    }

    public void setIfTerm(boolean ifTest) {
        String ifTerm = this.genIfTerm(ifTest);
        this.setTerm(ICReltJump.ifTerm, ifTerm);
    }

    public void setAddress_1(String address_1) {
        this.setTerm(ICReltJump.address_1, address_1);
    }

    public void setRelOp(String relOp) {
        this.setTerm(ICReltJump.relOp, relOp);
    }

    public void setAddress_2(String address_2) {
        this.setTerm(ICReltJump.address_2, address_2);
    }

    public void setGotoLabel(String gotoLabel) {
        this.setTerm(ICReltJump.gotoLabel, gotoLabel);
    }
    
    private String genIfTerm(boolean ifTest) {
        if (ifTest) {
            return "if";
        }
        return "ifFalse";
    }
}
