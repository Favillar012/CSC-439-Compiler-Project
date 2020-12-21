package edu.uncg.csc439.LCElements;

/**
 * This class implements a Little while loop.
 * @author Fernando Villarreal
 * @date 10/8/2020
 */
public class LCWhileLoop extends LCStmt{

    private LCInteger limit;
    private LCBlock block;

    public LCWhileLoop(LCInteger limit, LCBlock block) {
        this.limit = limit;
        this.block = block;
    }

    public LCInteger getLimit() {
        return this.limit;
    }

    public LCBlock getBlock() {
        return this.block;
    }
}
