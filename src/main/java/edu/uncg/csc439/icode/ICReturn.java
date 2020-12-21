package edu.uncg.csc439.icode;

/**
 * This class implements an IC return instruction.
 * @author Fernando Villarreal
 * @date 11/2/2020
 */
public class ICReturn extends ICLine {

    //=============== TERMS ===============

    public static final int returnTypeIdx = 1;
    public static final int addressIdx = 2;

    // Return Types
    public static final int returnVoid = 0;     // return (no return value)
    public static final int returnChar = 1;     // return1 (return char)
    public static final int returnInt = 4;      // return4 (return int)

    //=============== CLASS VARIABLES ===============

    private int returnType;

    //=============== CONSTRUCTOR ===============

    public ICReturn(int returnType, String address) {
        super();
        this.returnType = returnType;
        this.addTerm(this.genReturnTypeString(returnType));
        this.addTerm(address);
    }

    //=============== METHODS ===============

    public int getReturnType() {
        return this.returnType;
    }

    public String getAddress() {
        return this.getTerm(ICReturn.addressIdx);
    }

    public void setReturnType(int returnType) {
        this.returnType = returnType;
        this.setTerm(ICReturn.returnTypeIdx, this.genReturnTypeString(returnType));
    }

    public void setAddress(String address) {
        this.setTerm(ICReturn.addressIdx, address);
    }

    private String genReturnTypeString(int returnType) {
        if (returnType == ICReturn.returnChar) {
            return "return1";
        } else if (returnType == ICReturn.returnInt) {
            return "return4";
        } else {
            return "return?";
        }
    }
}
