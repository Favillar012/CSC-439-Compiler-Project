package edu.uncg.csc439.LCElements;

import java.util.ArrayList;

/**
 * This class implements a Little C Function. It also contains an inner class called
 * FormalParameter which is used for the implementation of a function's formal parameters.
 * @author Fernando Villarreal
 * @date 10/17/2020
 */
public class LCFunction extends LCType {

    //================= VARIABLES =================

    private final String type;
    private ArrayList<FormalParameter> formalParameters;
    private LCBlock block;

    //================= CONSTRUCTORS =================

    public LCFunction(String type, String identifier) {
        super(identifier);
        this.type = type;
        this.formalParameters = new ArrayList<>();
    }

    public LCFunction(String type, String identifier, LCBlock block) {
        super(identifier);
        this.type = type;
        this.formalParameters = new ArrayList<>();
        this.block = block;
    }

    public LCFunction(String type, String identifier, ArrayList<FormalParameter> formalParameters) {
        super(identifier);
        this.type = type;
        this.formalParameters = formalParameters;
    }

    public LCFunction(String type, String identifier, ArrayList<FormalParameter> formalParameters, LCBlock block) {
        super(identifier);
        this.type = type;
        this.formalParameters = formalParameters;
        this.block = block;
    }

    //================= METHODS =================

    public String getType() {
        return this.type;
    }

    public ArrayList<FormalParameter> getFormalParameters() {
        return this.formalParameters;
    }

    public FormalParameter getFormalParameter(int index) {
        return this.formalParameters.get(index);
    }

    public LCBlock getBlock() {
        return block;
    }

    public void setBlock(LCBlock block) {
        this.block = block;
    }

    public void setFormalParameters(ArrayList<FormalParameter> formalParameters) {
        this.formalParameters = formalParameters;
    }

    public void addFormalParameter(LCFunction.FormalParameter formalParameter) {
        this.formalParameters.add(formalParameter);
    }

    /**
     * Get the number of formal parameters for this function.
     * @return
     */
    public int getNumOfPars() {
        return this.formalParameters.size();
    }

    /**
     * Checks if this function has a definition. Return true if so, returns false otherwise.
     * @return
     */
    public boolean isDefined() {
        if (this.block != null) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the provided function call is valid. Returns true if so, false otherwise.
     * @param funcCall
     * @return
     */
    public boolean isFuncCallValid(LCFuncCall funcCall) {
        // Check if the function call has the correct number of parameters
        int actualParsCount = funcCall.getParCount();
        int formalParsCount = this.formalParameters.size();
        if (actualParsCount != formalParsCount) {
            return false;
        }
        // Check the types of each actual and formal parameter to make sure they match
        for (int i = 0; i < actualParsCount; i++) {
            LCType formalPar = this.formalParameters.get(i).getParAsLCType();
            LCType actualPar = funcCall.getParameter(i);
            // Check if both the parameters are different, return false if so.
            if (!this.areParametersCompatible(formalPar, actualPar)) {
                return false;
            }
        }
        // The function call is valid, return true.
        return true;
    }

    @Override
    public String toString() {
        return this.type + " " + this.getIdentifier() + "(" + this.formalParametersToString() + ")";
    }

    public String getSignatureString() {
        String signature = this.type + " (" + this.formalParametersTypesToString() + ")";
        return signature;
    }

    /**
     * Checks if the provided formal and actual parameters are compatible.
     * The supported LCTypes are: LCInteger, LCChar, LCCharArray, LCIntArray.
     * @param frmPar
     * @param atlPar
     * @return
     */
    private boolean areParametersCompatible(LCType frmPar, LCType atlPar) {
        if (frmPar.isLCInteger() && atlPar.isLCInteger()) {
            return true;
        } else if (frmPar.isLCChar() && atlPar.isLCChar()) {
            return true;
        } else if (frmPar.isLCInteger() && atlPar.isLCChar()) {
            return true;
        } else if (frmPar.isLCChar() && atlPar.isLCInteger()) {
            return true;
        } else if (frmPar.isLCCharArray() && atlPar.isLCCharArray()) {
            return true;
        } else if (frmPar.isLCIntArray() && atlPar.isLCIntArray()) {
            return true;
        }
        // The atlPar is an LCFuncCall
        else if (atlPar.isLCFuncCall()) {
            LCFuncCall atlParFuncCall = (LCFuncCall)atlPar;
            // Return type for atlParFuncCall
            String returnType = atlParFuncCall.getFuncType();
            int isChar = returnType.compareTo("char");
            int isInt = returnType.compareTo("int");
            int isCharArray = returnType.compareTo("char[]");
            int isIntArray = returnType.compareTo("int[]");
            // Check if the types are compatible
            if (frmPar.isLCInteger() && (isChar == 0 || isInt == 0)) {
                return true;
            } else if (frmPar.isLCChar() && (isChar == 0 || isInt == 0)) {
                return true;
            } else if (frmPar.isLCCharArray() && isCharArray == 0) {
                return true;
            } else if (frmPar.isLCIntArray() && isIntArray == 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Generates and returns a string for the list of formal parameters.
     * @return
     */
    private String formalParametersToString() {
        String fpString = "";
        int size = this.formalParameters.size();
        for (int i = 0; i < size; i++) {
            FormalParameter fp = this.formalParameters.get(i);
            fpString += fp.toString();
            if (i < (size - 1)) {
                fpString += ", ";
            }
        }
        return fpString;
    }

    /**
     * Generates and returns a string for the types of the formal parameters.
     */
    private String formalParametersTypesToString() {
        String fpString = "";
        int size = this.formalParameters.size();
        for (int i = 0; i < size; i++) {
            FormalParameter fp = this.formalParameters.get(i);
            fpString += fp.getType();
            if (i < (size - 1)) {
                fpString += ",";
            }
        }
        return fpString;
    }

    /**
     * Formal Parameter subclass for LCFunctions.
     */
    public static class FormalParameter extends LCType {

        private final String type;
        private final String identifier;

        public FormalParameter(String type, String identifier) {
            this.type = type;
            this.identifier = identifier;
        }

        public String getType() {
            return this.type;
        }

        public String getIdentifier() {
            return this.identifier;
        }

        public LCType getParAsLCType() {
            // Check what LCType this formal parameter corresponds to and return the appropriate one
            if (this.type.compareTo("int") == 0) {
                return new LCInteger(this.identifier, new LCExpr(this.identifier));
            } else if (this.type.compareTo("char") == 0) {
                return new LCChar(this.identifier, new LCExpr(this.identifier));
            } else if (this.type.compareTo("char[]") == 0) {
                return new LCCharArray(this.identifier, new LCExpr(this.identifier));
            } else if (this.type.compareTo("int[]") == 0) {
                return new LCIntArray(this.identifier, new LCExpr(this.identifier));
            } else {
                return new LCType(this.identifier);
            }
        }

        public String toString() {
            return this.type + " " + this.identifier;
        }
    }
}
