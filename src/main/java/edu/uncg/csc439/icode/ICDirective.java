package edu.uncg.csc439.icode;

import java.util.ArrayList;

/**
 * This class implements IC directives.
 * @author Fernando Villarreal
 * @date 11/18/2020
 */
public class ICDirective extends ICLine {

    //=============== TERMS ===============

    public static final int directive = 1;
    public static final int firstParameter = 2;

    // Directives
    public static final int fnStart = 0;
    public static final int fnEnd = 1;
    public static final int definedWord = 2;
    public static final int definedByte = 3;

    //=============== CLASS VARIABLES ===============

    private final int directiveType;

    //=============== CONSTRUCTOR ===============

    public ICDirective(int directive, String firstParameter) {
        super();
        this.directiveType = directive;
        this.addTerm(this.genDirective(directive));
        this.addTerm(firstParameter);
    }

    public ICDirective(int directive, ArrayList<String> parameters) {
        super();
        this.directiveType = directive;
        this.addTerm(this.genDirective(directive));
        this.addTerms(this.addCommasToParameters(parameters));
    }

    //=============== METHODS ===============

    public int getDirectiveType() {
        return this.directiveType;
    }

    public String getDirective() {
        return this.getTerm(ICDirective.directive);
    }

    public String getFirstParameter() {
        return this.getTerm(ICDirective.firstParameter);
    }

    public ArrayList<String> getParameters() {
        int firstIndex = ICDirective.firstParameter;
        int lastIndex = this.getTerms().size() - 1;
        return this.getTerms(firstIndex, lastIndex);
    }

    public void setDirective(int directive) {
        this.setTerm(ICDirective.directive, this.genDirective(directive));
    }

    public void setFirstParameter(String firstParameter) {
        this.setTerm(ICDirective.firstParameter, firstParameter);
    }

    public void setParameters(ArrayList<String> parameters) {
        this.setTerms(parameters);
    }

    private String genDirective(int directive) {
        if (directive == ICDirective.fnStart) {
            return ".fnStart";
        } else if (directive == ICDirective.fnEnd) {
            return ".fnEnd";
        } else if (directive == ICDirective.definedWord) {
            return ".dw";
        } else if (directive == ICDirective.definedByte) {
            return ".db";
        } else {
            return "???";
        }
    }

    /**
     * Add commas to the list of provided parameters.
     * @param parameters
     * @return
     */
    private ArrayList<String> addCommasToParameters(ArrayList<String> parameters) {
        ArrayList<String> modifiedParameters = new ArrayList<>();
        // Add commas to every parameter except the last one
        int length = parameters.size();
        for (int i = 0; i < length; i++) {
            String parameter = parameters.get(i);
            if (i < (length - 1)) {
                parameter += ",";
            }
            modifiedParameters.add(parameter);
        }
        // Return modifiedParameters
        return modifiedParameters;
    }
}
