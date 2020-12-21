package edu.uncg.csc439.icode;

import java.util.ArrayList;

/**
 * This class stores a sequence of Little C intermediate code lines. In
 * object-oriented terms, it stores a list of ICLine objects.
 * @author Fernando Villarreal
 * @date 11/2/2020
 */
public class ICLines {

    //=============== CLASS VARIABLES ===============

    private ArrayList<ICLine> icLines;

    //=============== CONSTRUCTORS ===============

    public ICLines() {
        this.icLines = new ArrayList<>();
    }

    public ICLines(ArrayList<ICLine> icLines) {
        this.icLines = icLines;
    }

    //=============== METHODS ===============

    /**
     * Get all the ICline objects in this ICLines object in an ArrayList.
     * @return
     */
    public ArrayList<ICLine> getICLinesList() {
        return this.icLines;
    }

    /**
     * Get the ICLine object at the specified index.
     * @param index
     * @return
     */
    public ICLine getICLine(int index) {
        return this.icLines.get(index);
    }

    /**
     * Adds an ICLine to the end of this ICLines object. The ICLine is not added if it is
     * empty.
     * @param icLine
     */
    public void addLine(ICLine icLine) {
        if (!icLine.isEmpty()) {
            this.icLines.add(icLine);
        }
    }

    /**
     * Adds an ICLine at the specified index. All ICLine objects at this index and onward
     * are shifted down (or to the right).
     * @param index
     * @param icLine
     */
    public void addLine(int index, ICLine icLine) {
        this.icLines.add(index, icLine);
    }

    /**
     * Add the list of provided ICLine objects to the list of this ICLines object.
     * @param linesToAdd
     */
    public void addLines(ArrayList<ICLine> linesToAdd) {
        for (ICLine icLine : linesToAdd) {
            this.icLines.add(icLine);
        }
    }

    /**
     * Replace the ICLine in the specified index for the provided ICLine.
     * @param index
     * @param icLine
     */
    public void setLine(int index, ICLine icLine) {
        this.icLines.set(index, icLine);
    }

    /**
     * Get the number of ICLine objects in this ICLines object.
     * @return
     */
    public int getSize() {
        return this.icLines.size();
    }

    @Override
    public String toString() {
        String linesStr = "";
        for (ICLine icLine : this.icLines) {
            linesStr += "\n" + icLine.toString();
        }
        return linesStr;
    }
}
