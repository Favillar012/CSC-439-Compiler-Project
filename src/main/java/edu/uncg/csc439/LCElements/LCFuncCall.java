package edu.uncg.csc439.LCElements;

import java.util.ArrayList;

/**
 * This class implements a Little C function call.
 * @author Fernando Villarreal
 * @date 10/29/2020
 */
public class LCFuncCall extends LCType {

    //================= VARIABLES =================

    private final String funcType;
    private String funcID;
    private LCActualParameters actualParameters;

    //================= CONSTRUCTORS =================

    public LCFuncCall(String funcType, String funcID) {
        super("");
        this.funcType = funcType;
        this.funcID = funcID;
    }

    public LCFuncCall(String funcType, String funcID, LCActualParameters parameters) {
        super("");
        this.funcType = funcType;
        this.funcID = funcID;
        this.actualParameters = parameters;
    }

    public LCFuncCall(String funcType, String funcID, ArrayList<LCType> parameters) {
        super("");
        this.funcType = funcType;
        this.funcID = funcID;
        this.actualParameters = new LCActualParameters(parameters);
    }

    //================= METHODS =================

    public String getFuncType() {
        return this.funcType;
    }

    public LCType getParameter(int index) {
        return this.actualParameters.getParameter(index);
    }

    public LCActualParameters getActualParameters() {
        return this.actualParameters;
    }

    public int getParCount() {
        return this.actualParameters.getParCount();
    }

    public LCExpr getExpr() {
        return new LCExpr(this.toString());
    }

    public String getExprStr() {
        return this.toString();
    }

    @Override
    public String toString() {
        String parStr = this.funcID + "(";
        int size = this.getParCount();
        for (int i = 0; i < size; i++) {
            LCType par = this.getParameter(i);
            // If the parameter has an identifier, add the identifier. Otherwise, add the literal value.
            if (par.hasIdentifier()) {
                parStr += par.getIdentifier();
            } else if (par.isLCInteger()) {
                parStr += ((LCInteger)par).getExprStr();
            } else if (par.isLCChar()) {
                parStr += ((LCChar)par).getExprStr();
            } else if (par.isLCCharArray()) {
                parStr += ((LCCharArray)par).getAsString();
            } else if (par.isLCFuncCall()) {
                parStr += ((LCFuncCall)par).toString();
            }
            if (i < (size - 1)) {
                parStr += ", ";
            }
        }
        parStr += ")";
        return parStr;
    }
}
