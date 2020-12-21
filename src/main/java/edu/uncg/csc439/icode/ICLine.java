package edu.uncg.csc439.icode;

import java.util.ArrayList;

/**
 * This class implements a single line of Little C intermediate code.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class ICLine {

    //=============== CLASS VARIABLES ===============

    public static final int label = 0;
    public static final boolean EMPTY = true;

    private ArrayList<String> terms;
    private final boolean isEmpty;

    //=============== CONSTRUCTORS ===============

    public ICLine() {
        this.terms = new ArrayList<>();
        String label = "";
        this.addTerm(label);
        this.isEmpty = false;
    }

    public ICLine(ArrayList<String> terms) {
        this.terms = new ArrayList<>();
        String label = "";
        this.addTerm(label);
        this.addTerms(terms);
        this.isEmpty = false;
    }

    public ICLine(String label, ArrayList<String> terms) {
        this.terms = new ArrayList<>();
        this.addTerm(label);
        this.addTerms(terms);
        this.isEmpty = false;
    }

    /**
     * This constructor is to be used for the creation of empty ICLine objects only.
     * @param isEmpty
     */
    public ICLine(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    //=============== METHODS ===============

    /**
     * Check whether this ICLine object is empty or not.
     * @return
     */
    public boolean isEmpty() {
        return this.isEmpty;
    }

    public String getLabel() {
        return this.getTerm(ICLine.label);
    }

    public void setLabel(String label) {
        this.setTerm(ICLine.label, label);
    }

    /**
     * Get all terms in this ICLine.
     * @return
     */
    public ArrayList<String> getTerms() {
        return this.terms;
    }

    /**
     * Get the terms in the specified range of indexes. Both the provided first and last
     * indexes are inclusive.
     * @param firstIndex
     * @param lastIndex
     * @return
     */
    public ArrayList<String> getTerms(int firstIndex, int lastIndex) {
        ArrayList<String> subListTerms = new ArrayList<>();
        for (int i = firstIndex; i <= lastIndex; i++) {
            String term = this.terms.get(i);
            subListTerms.add(term);
        }
        return subListTerms;
    }

    public void setTerms(ArrayList<String> terms) {
        String label = this.getLabel();
        this.terms = new ArrayList<>();
        this.addTerm(label);
        this.addTerms(terms);
    }

    /**
     * Get the term at the specified index.
     * @param index
     * @return
     */
    public String getTerm(int index) {
        return this.terms.get(index);
    }

    /**
     * Sets the term at the specified index for the given term.
     * @param index
     * @param term
     */
    public void setTerm(int index, String term) {
        this.terms.set(index, term);
    }

    /**
     * Add a term to the end of the terms list.
     * @param term
     */
    public void addTerm(String term) {
        this.terms.add(term);
    }

    /**
     * Adds all of the given terms to the end of the terms list.
     * @param terms
     */
    public void addTerms(ArrayList<String> terms) {
        for (String term : terms) {
            this.terms.add(term);
        }
    }

    @Override
    public String toString() {
        // Get the label
        int size = this.terms.size();
        String label = this.getLabel();
        // Append terms into a string
        String termsStr = "";
        for (int i = 1; i < size; i++) {
            String term = this.getTerm(i);
            if (term != null) {
                termsStr += " " + term;
            }
        }
        termsStr = termsStr.trim();
        // Return the string
        if (!label.isEmpty()) {
            return label + ": " + termsStr;
        } else {
            return "\t\t" + termsStr;
        }
    }
}
