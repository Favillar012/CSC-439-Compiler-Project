package edu.uncg.csc439.LCElements;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * This class contains a list of LCTypes that constitute a Little C program.
 * @author Fernando Villarreal
 * @date 10/10/2020
 */
public class LCProgram extends LCType {

    private ArrayList<LCType> programList;

    public LCProgram() {
        super();
        this.programList = new ArrayList<>();
    }

    public LCProgram(ArrayList<LCType> programList) {
        super();
        this.programList = programList;
    }

    public void addLCType(LCType lcType) {
        this.programList.add(lcType);
    }

    public ArrayList<LCType> getProgramList() {
        return this.programList;
    }

    public void setProgramList(ArrayList<LCType> programList) {
        this.programList = programList;
    }

    @Override
    public String toString() {
        String programStr = "";
        for (LCType lcType : this.programList) {
            programStr += lcType.toString() + "\n";
        }
        return programStr;
    }
}
