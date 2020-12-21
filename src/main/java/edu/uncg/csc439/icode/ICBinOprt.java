package edu.uncg.csc439.icode;

/**
 * This class implements an IC binary operation.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class ICBinOprt extends ICLine {

    //=============== TERMS ===============

    public static final int address_1 = 1;
    public static final int asgn = 2;
    public static final int address_2 = 3;
    public static final int oprt = 4;
    public static final int address_3 = 5;

    //=============== CONSTRUCTORS ===============

    public ICBinOprt(String address_1, String operator, String address_2, String address_3) {
        super();
        this.addTerm(address_1);
        this.addTerm("=");
        this.addTerm(address_2);
        this.addTerm(operator);
        this.addTerm(address_3);
    }

    //=============== METHODS ===============

    public String getAddress_1() {
        return this.getTerm(ICBinOprt.address_1);
    }

    public String getOperator() {
        return this.getTerm(ICBinOprt.oprt);
    }

    public String getAddress_2() {
        return this.getTerm(ICBinOprt.address_2);
    }

    public String getAddress_3() {
        return this.getTerm(ICBinOprt.address_3);
    }

    public void setAddress_1(String term) {
        this.setTerm(ICBinOprt.address_1, term);
    }

    public void setOperator(String oprt) {
        this.setTerm(ICBinOprt.oprt, oprt);
    }

    public void setAddress_2(String term) {
        this.setTerm(ICBinOprt.address_2, term);
    }

    public void setAddress_3(String term) {
        this.setTerm(ICBinOprt.address_3, term);
    }
}
