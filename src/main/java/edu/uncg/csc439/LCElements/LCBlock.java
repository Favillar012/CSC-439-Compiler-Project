package edu.uncg.csc439.LCElements;

import java.util.ArrayList;

/**
 * This class implements blocks or scopes in Little C.
 * @author Fernando Villarreal
 * @date 10/7/2020
 */
public class LCBlock extends LCStmt {

    private ArrayList<LCType> stmtsList;

    public LCBlock() {
        super();
        this.stmtsList = new ArrayList<>();
    }

    public LCBlock(ArrayList<LCType> stmtsList) {
        super();
        this.stmtsList = stmtsList;
    }

    public void setStmtsList(ArrayList<LCType> stmtsList) {
        this.stmtsList = stmtsList;
    }

    public boolean isBlockEmpty() {
        if (this.stmtsList.isEmpty()) {
            return true;
        }
        return false;
    }

    public ArrayList<LCType> getStmtsList() {
        return this.stmtsList;
    }

    public void addLCType(LCType lcType) {
        this.stmtsList.add(lcType);
    }
}
