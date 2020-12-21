package edu.uncg.csc439.LCElements;

/**
 * This class for variables encapsulates the text for INT and CHAR Lexer tokens. It extends LCType but does
 * not use the identifier variable.
 * @author Fernando Villarreal
 * @date 10/3/2020
 */
public class LCVrblType extends LCType {

    private final static String intType = "int";
    private final static String charType = "char";

    private final String vrblType;

    public LCVrblType(String vrblType) {
        super();
        this.vrblType = vrblType;
    }

    public String getVrblType() {
        return this.vrblType;
    }

    public boolean isIntType() {
        if (this.vrblType.compareTo(LCVrblType.intType) == 0) {
            return true;
        }
        return false;
    }

    public boolean isCharType() {
        if (this.vrblType.compareTo((LCVrblType.charType)) == 0) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Vrbl_Type='" + this.vrblType + "'";
    }
}
