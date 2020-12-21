package edu.uncg.csc439.icode;

/**
 * This class implements an IC copy instruction.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class ICCopy extends ICLine {

    //=============== TERMS ===============

    public static final int address_1 = 1;
    public static final int asgn = 2;
    public static final int address_2 = 3;

    //=============== CONSTRUCTORS ===============

    public ICCopy(String address_1, String address_2) {
        super();
        this.addTerm(address_1);
        this.addTerm("=");
        this.addTerm(address_2);
    }

    //=============== METHODS ===============

    public String getAddress_1() {
        return this.getTerm(ICCopy.address_1);
    }

    public String getAddress_2() {
        return this.getTerm(ICCopy.address_2);
    }

    public void setAddress_1(String term) {
        this.setTerm(ICCopy.address_1, term);
    }

    public void setAddress_2(String term) {
        this.setTerm(ICCopy.address_2, term);
    }
}
