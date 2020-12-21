package edu.uncg.csc439.LCElements;

/**
 * This class implements Little C if statements.
 * @author Fernando Villarreal
 * @date 10/7/2020
 */
public class LCIfStmt extends LCStmt {

    private LCType ifExpr;
    private boolean elsePresent;
    private LCBlock ifBlock;
    private LCBlock elseBlock;

    public LCIfStmt(LCType ifExpr, LCBlock ifBlock) {
        super();
        this.ifExpr = ifExpr;
        this.ifBlock = ifBlock;
        this.elsePresent = false;
    }

    public LCIfStmt(LCType ifExpr, LCBlock ifBlock, LCBlock elseBlock) {
        super();
        this.ifExpr = ifExpr;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
        this.elsePresent = true;
    }

    public LCType getIfExpr() {
        return this.ifExpr;
    }

    public LCBlock getIfBlock() {
        return this.ifBlock;
    }

    public LCBlock getElseBlock() {
        return this.elseBlock;
    }

    public void setElseBlock(LCBlock elseBlock) {
        this.elseBlock = elseBlock;
        this.elsePresent = true;
    }

    public boolean isElsePresent() {
        return this.elsePresent;
    }
}
