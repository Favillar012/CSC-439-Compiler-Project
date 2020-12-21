package edu.uncg.csc439.LCElements;

import java.util.ArrayList;

/**
 * This class acts as container for actual parameters.
 * @author Fernando Villarreal
 * @date 10/10/2020
 */
public class LCActualParameters extends LCType {

    private ArrayList<LCType> parList;

    public LCActualParameters(ArrayList<LCType> parList) {
        super();
        this.parList = parList;
    }

    public LCType getParameter(int index) {
        return this.parList.get(index);
    }

    public ArrayList<LCType> getParList() {
        return this.parList;
    }

    public int getParCount() {
        return this.parList.size();
    }

    @Override
    public String toString() {
        return this.parList.toString();
    }
}
