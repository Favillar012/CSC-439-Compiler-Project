package edu.uncg.csc439.icode;

/**
 * This class implements an IC unary operation.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class ICUniOprt extends ICLine {

    //=============== TERMS ===============

    public static final int address_1 = 1;
    public static final int asgn = 2;
    public static final int oprt = 3;
    public static final int address_2 = 4;

    //=============== CONSTRUCTORS ===============

    public ICUniOprt(String address_1, String oprt, String address_2) {
        super();
        this.addTerm(address_1);
        this.addTerm("=");
        this.addTerm(oprt);
        this.addTerm(address_2);
    }

    //=============== METHODS ===============

    public String getAddress_1() {
        return this.getTerm(ICUniOprt.address_1);
    }

    public String getOperator() {
        return this.getTerm(ICUniOprt.oprt);
    }

    public String getAddress_2() {
        return this.getTerm(ICUniOprt.address_2);
    }

    public void setAddress_1(String term) {
        this.setTerm(ICUniOprt.address_1, term);
    }

    public void setOperator(String oprt) {
        this.setTerm(ICUniOprt.oprt, oprt);
    }

    public void setAddress_2(String term) {
        this.setTerm(ICUniOprt.address_2, term);
    }
}
