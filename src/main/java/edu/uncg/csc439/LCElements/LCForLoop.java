package edu.uncg.csc439.LCElements;

/**
 * This class implements a Little C for loop.
 * @author Fernando Villarreal
 * @date 10/8/2020
 */
public class LCForLoop extends LCStmt {

    private LCInteger initialValue;
    private LCInteger limit;
    private LCType update;
    private LCBlock block;

    public LCForLoop(LCInteger initialValue, LCInteger limit, LCType update, LCBlock block) {
        this.initialValue = initialValue;
        this.limit = limit;
        this.update = update;
        this.block = block;
    }

    public LCInteger getInitialValue() {
        return this.initialValue;
    }

    public LCInteger getLimit() {
        return this.limit;
    }

    public LCType getupdate() {
        return this.update;
    }

    public LCBlock getBlock() {
        return this.block;
    }

    public void setBlock(LCBlock block) {
        this.block = block;
    }
}
