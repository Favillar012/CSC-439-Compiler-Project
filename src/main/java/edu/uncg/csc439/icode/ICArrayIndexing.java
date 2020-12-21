package edu.uncg.csc439.icode;

/**
 * This class implements an IC array indexing instruction. This instruction can be
 * a read or a write.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class ICArrayIndexing extends ICLine {

    //=============== TERMS ===============

    public static final int address_1 = 1;
    public static final int asgn = 2;
    public static final int address_2 = 3;
    public static final int oprt = 4;
    public static final int address_3 = 5;

    // Read or Write (oprt in constructor)
    public static final boolean read = true;
    public static final boolean write = false;

    // Indexing an integer or a character (type in constructor)
    public static final boolean integer = true;
    public static final boolean character = false;

    //=============== CONSTRUCTOR ===============

    public ICArrayIndexing(String address_1, String address_2, boolean oprt, boolean type, String address_3) {
        super();
        this.addTerm(address_1);
        this.addTerm("=");
        this.addTerm(address_2);
        this.addTerm(this.genInstr(oprt, type));
        this.addTerm(address_3);
    }

    //=============== METHODS ===============

    public String getAddress_1() {
        return this.getTerm(ICArrayIndexing.address_1);
    }

    public String getAddress_2() {
        return this.getTerm(ICArrayIndexing.address_2);
    }

    public String getOperator() {
        return this.getTerm(ICArrayIndexing.oprt);
    }

    public String getAddress_3() {
        return this.getTerm(ICArrayIndexing.address_3);
    }

    public void setAddress_1(String term) {
        this.setTerm(ICArrayIndexing.address_1, term);
    }

    public void setOperator(String oprt) {
        this.setTerm(ICArrayIndexing.oprt, oprt);
    }

    public void setAddress_2(String term) {
        this.setTerm(ICArrayIndexing.address_2, term);
    }

    public void setAddress_3(String term) {
        this.setTerm(ICArrayIndexing.address_3, term);
    }
    
    private String genInstr(boolean oprt, boolean type) {
        // Read an int
        if (oprt && type) {
            return "ldidx4";
        } // Read a char
        else if (oprt && !type) {
            return "ldidx1";
        } // Write an int
        else if (!oprt && type) {
            return "stidx4";
        } // Write a char
        else if (!oprt && !type) {
            return "stdix1";
        } // Impossible outcome
        else {
            return "???";
        }
    }
}
