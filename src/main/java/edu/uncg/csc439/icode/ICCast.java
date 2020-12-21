package edu.uncg.csc439.icode;

/**
 * This class implements IC casting instructions.
 * @author Fernando Villarreal
 * @date 11/1/2020
 */
public class ICCast extends ICLine {

    //=============== TERMS ===============

    public static final int address_1 = 1;
    public static final int asgn = 2;
    public static final int castType = 3;
    public static final int address_2 = 4;

    // Cast Types
    public static final int widen = 0;      // cast char to int
    public static final int narrow = 1;     // cast int to char

    //=============== CONSTRUCTOR ===============

    public ICCast(String address_1, int castType, String address_2) {
        super();
        this.addTerm(address_1);
        this.addTerm("=");
        this.addTerm(this.genCastType(castType));
        this.addTerm(address_2);
    }

    //=============== METHODS ===============

    public String getAddress_1() {
        return this.getTerm(ICCast.address_1);
    }

    public String getCastType() {
        return this.getTerm(ICCast.castType);
    }

    public String getAddress_2() {
        return this.getTerm(ICCast.address_2);
    }

    public void setAddress_1(String address_1) {
        this.setTerm(ICCast.address_1, address_1);
    }

    public void setCastType(int castType) {
        this.setTerm(ICCast.castType, this.genCastType(castType));
    }

    public void setAddress_2(String address_2) {
        this.setTerm(ICCast.address_1, address_2);
    }

    private String genCastType(int castType) {
        if (castType == ICCast.widen) {
            return "widen";
        } else if (castType == ICCast.narrow) {
            return "narrow";
        } else {
            return "???";
        }
    }
}
