package edu.uncg.csc439.mipsgen.MIPSCode;

import java.util.ArrayList;

/**
 * This class implements a single line of MIPS Assembly code. Such a line consists of (in order):
 * an optional label, an operation, and a list of comma-separated arguments.
 * @author Fernando Villarreal
 * @date 11/26/2020
 */
public class MIPSLine {

    //====================== INDEXES FOR THE TERMS ======================

    private static final int labelIdx = 0;
    private static final int operatorIdx = 1;
    private static final int firstArgIdx = 2;

    //====================== CLASS VARIABLES ======================

    private ArrayList<String> terms;

    //====================== CONSTRUCTOR ======================

    /**
     * Create an empty MIPSLine.
     */
    public MIPSLine() {
        this.terms = new ArrayList<>();
    }

    /**
     * Create a MIPSLine with a label but no operations.
     * @param label
     */
    public MIPSLine(String label) {
        this.terms = new ArrayList<>();
        this.addTerm(label);
    }

    /**
     * Create a MIPSLine operation with one operand.
     * @param oprt
     * @param opnd1
     */
    public MIPSLine(String oprt, String opnd1) {
        this.terms = new ArrayList<>();
        this.addTerm(""); // Empty Label
        this.addTerm(oprt);
        this.addTerm(opnd1);
    }

    /**
     * Create a MIPSLine operation with two operands.
     * @param oprt
     * @param opnd1
     * @param opnd2
     */
    public MIPSLine(String oprt, String opnd1, String opnd2) {
        this.terms = new ArrayList<>();
        this.addTerm(""); // Empty Label
        this.addTerm(oprt);
        this.addTerm(opnd1);
        this.addTerm(opnd2);
    }

    /**
     * Create a MIPSLine operation with three operands.
     * @param oprt
     * @param opnd1
     * @param opnd2
     * @param opnd3
     */
    public MIPSLine(String oprt, String opnd1, String opnd2, String opnd3) {
        this.terms = new ArrayList<>();
        this.addTerm(""); // Empty Label
        this.addTerm(oprt);
        this.addTerm(opnd1);
        this.addTerm(opnd2);
        this.addTerm(opnd3);
    }

    //====================== PUBLIC METHODS ======================

    /**
     * Set the label for this MIPSLine.
     * @param label
     */
    public void setLabel(String label) {
        this.setTerm(MIPSLine.labelIdx, label);
    }

    /**
     * Get the label for this MIPSLine.
     * @return
     */
    public String getLabel() {
        return this.getTerm(MIPSLine.labelIdx);
    }

    /**
     * Set the operator for this MIPSLine.
     * @param operator
     */
    public void setOperator(String operator) {
        this.setTerm(MIPSLine.operatorIdx, operator);
    }

    /**
     * Get the operator for this MIPSLine.
     * @return
     */
    public String getOperator() {
        return this.getTerm(MIPSLine.operatorIdx);
    }

    /**
     * Set the operands for this MIPSLine.
     * @param operands
     */
    public void setOperands(ArrayList<String> operands) {
        this.removeTermsAfterIndex(MIPSLine.firstArgIdx);
        for (String operand : operands) {
            this.addTerm(operand);
        }
    }

    /**
     * Set all of the terms in this MIPSLine.
     * @param terms
     */
    public void setTerms(ArrayList<String> terms) {
        this.terms = terms;
    }

    @Override
    public String toString() {
        String str = "";
        // Get and append the label to the string if it's not empty
        String label = this.getLabel();
        if (!label.isEmpty()) {
            str += label + ":";
        } else {
            str += "\t\t";
        }
        // Get and append the rest of the terms
        int length = this.terms.size();
        int lastIndex = length - 1;
        for (int i = 1; i < length; i++) {
            String term = this.getTerm(i);
            if (!term.isEmpty()) {
                str += " " + term;
            }
            // Append commas to terms of index 2 and onward except the last one.
            if (i >= 2 && i != lastIndex) {
                str += ",";
            }
        }
        return str;
    }

    //====================== PRIVATE METHODS ======================

    /**
     * Remove all terms from the index and onward.
     * @param index
     */
    private void removeTermsAfterIndex(int index) {
        int size = this.terms.size();
        if (size > index) {
            this.terms.subList(index, size).clear();
        }
    }

    /**
     * Set the term at the specified index.
     * @param index
     * @param term
     */
    private void setTerm(int index, String term) {
        this.terms.set(index, term);
    }

    /**
     * Get the term at the specified index.
     * @param index
     * @return
     */
    private String getTerm(int index) {
        return this.terms.get(index);
    }

    /**
     * Add a term to the end of this MIPSLine.
     * @param term
     */
    private void addTerm(String term) {
        this.terms.add(term);
    }
}
