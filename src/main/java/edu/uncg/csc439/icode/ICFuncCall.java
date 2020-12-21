package edu.uncg.csc439.icode;

/**
 * This class implements IC function calls. There are two types of function calls:
 * ones that return values and ones that do not return values (void).
 * @author Fernando Villarreal
 * @date 11/7/2020
 */
public class ICFuncCall extends ICLine {

    //=============== TERMS ===============

    public static final int address = 1;
    public static final int asgn = 2;
    public static final int call = 3;
    public static final int funcName = 4;
    public static final int parsNumber = 5;

    // Type of Function Call
    private boolean returnsValue;

    //=============== CONSTRUCTOR ===============

    public ICFuncCall(String address, String funcName, String parsNumber) {
        super();
        this.returnsValue = true;
        String asgn = "=";
        if (address.isEmpty()) {
            this.returnsValue = false;
            asgn = "";
        }
        this.addTerm(address);
        this.addTerm(asgn);
        this.addTerm("call");
        this.addTerm(funcName + ",");
        this.addTerm(parsNumber);
    }

    //=============== METHODS ===============

    public String getAddress() {
        return this.getTerm(ICFuncCall.address);
    }

    public String getFuncName() {
        String funcName = this.getTerm(ICFuncCall.funcName);
        return funcName.substring(0, funcName.length() - 1); // trim the comma off
    }

    public String getParsNumber() {
        return this.getTerm(ICFuncCall.parsNumber);
    }

    public void setAddress(String address) {
        this.setTerm(ICFuncCall.address, address);
        this.setTerm(ICFuncCall.asgn, "=");
        this.returnsValue = true;
    }

    public void setFuncName(String funcName) {
        this.setTerm(ICFuncCall.funcName, funcName + ",");
    }

    public void setParsNumber(String parsNumber) {
        this.setTerm(ICFuncCall.parsNumber, parsNumber);
    }

    public void removeAddress() {
        this.setTerm(ICFuncCall.address, "");
        this.setTerm(ICFuncCall.asgn, "");
        this.returnsValue = false;
    }

    /**
     * Check if this IC function call returns a value.
     * @return
     */
    public boolean returnsValue() {
        return this.returnsValue;
    }
}
