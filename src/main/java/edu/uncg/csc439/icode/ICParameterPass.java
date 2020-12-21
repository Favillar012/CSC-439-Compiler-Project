package edu.uncg.csc439.icode;

/**
 * This class implements parameter passing instructions.
 * @author Fernando Villarreal
 * @date 11/2/2020
 */
public class ICParameterPass extends ICLine {

    //=============== TERMS ===============

    public static final int parPassType = 1;
    public static final int address = 2;

    // Parameter Passing Types
    public static final int paramChar = 1;      // param1 (pass a char)
    public static final int paramInt = 4;       // param4 (pass an int)

    //=============== CONSTRUCTOR ===============

    public ICParameterPass(int parPassType, String address) {
        super();
        this.addTerm(this.genParPassType(parPassType));
        this.addTerm(address);
    }

    //=============== METHODS ===============

    public String getParPassType() {
        return this.getTerm(ICParameterPass.parPassType);
    }

    public String getAddress() {
        return this.getTerm(ICParameterPass.address);
    }

    public void setParPassType(int parPassType) {
        this.setTerm(ICParameterPass.parPassType, this.genParPassType(parPassType));
    }

    public void setAddress(String address) {
        this.setTerm(ICParameterPass.address, address);
    }
    
    private String genParPassType(int parPassType) {
        if (parPassType == ICParameterPass.paramChar) {
            return "param1";
        } else if (parPassType == ICParameterPass.paramInt) {
            return "param4";
        } else {
            return "param?";
        }
    }
}
