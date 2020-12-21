package edu.uncg.csc439.mipsgen.MIPSCode;

import java.util.ArrayList;

/**
 * This class manages lists of MIPSLines.
 * @author Fernando Villarreal
 * @date 11/27/2020
 */
public class MIPSLines {

    //==================== CLASS VARIABLES ====================

    private ArrayList<MIPSLine> mipsLineList;

    //==================== CONSTRUCTOR ====================

    public MIPSLines() {
        this.mipsLineList = new ArrayList<>();
    }

    public MIPSLines(ArrayList<MIPSLine> mipsLineList) {
        this.mipsLineList = mipsLineList;
    }

    //==================== METHODS ====================

    /**
     * Get the MIPSLine objects in this MIPSLines object in an ArrayList.
     * @return
     */
    public ArrayList<MIPSLine> getMIPSLineList() {
        return this.mipsLineList;
    }

    /**
     * Get the MIPSLine object at the specified index.
     * @param index
     * @return
     */
    public MIPSLine getMIPSLine(int index) {
        return this.mipsLineList.get(index);
    }

    /**
     * Add the given MIPSLine to the end of this MIPSLines list.
     * @param mipsLine
     */
    public void addMIPSLine(MIPSLine mipsLine) {
        this.mipsLineList.add(mipsLine);
    }

    /**
     * Add all the MIPSLine objects in the provided list to the end of this MIPSLines list.
     * @param mipsLineList
     */
    public void addMIPSLines(ArrayList<MIPSLine> mipsLineList) {
        this.mipsLineList.addAll(mipsLineList);
    }

    @Override
    public String toString() {
        String str = "";
        for (MIPSLine mipsLine : this.mipsLineList) {
            str += "\n" + mipsLine.toString();
        }
        return str;
    }
}
