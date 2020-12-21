package edu.uncg.csc439.LCElements;

/**
 * This class implements Little C break statements.
 * @author Fernando Villarreal
 * @date 10/7/2020
 */
public class LCBreak extends LCStmt {

    private LCType relatedBlock;

    public LCBreak(LCType relatedBlock) {
        this.relatedBlock = relatedBlock;
    }

    public LCBreak() {}

    public LCType getRelatedBlock() {
        return relatedBlock;
    }
}
