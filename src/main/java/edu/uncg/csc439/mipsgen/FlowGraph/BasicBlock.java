package edu.uncg.csc439.mipsgen.FlowGraph;

import edu.uncg.csc439.icode.ICLine;
import edu.uncg.csc439.icode.ICLines;

import java.util.ArrayList;

/**
 * This class implements a basic block in a flow control graph for an intermediate
 * code program.
 * @author Fernando Villarreal
 * @date 11/18/2020
 */
public class BasicBlock {

    //================== CLASS VARIABLES ==================

    private ICLines icLines;
    private ArrayList<BasicBlock> nextBlocks;

    //================== CONSTRUCTOR ==================

    public BasicBlock() {
        this.icLines = new ICLines();
        this.nextBlocks = new ArrayList<>();
    }

    public BasicBlock(ICLines icLines) {
        this.icLines = icLines;
        this.nextBlocks = new ArrayList<>();
    }

    //================== METHODS ==================

    /**
     * Get the ICLines object of this BasicBlock.
     * @return
     */
    public ICLines getIcLines() {
        return this.icLines;
    }

    /**
     * Checks to see if this BasicBlock is empty.
     * @return
     */
    public boolean isEmpty() {
        if (this.icLines.getICLinesList().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Add a block to go to after this block in the flow control graph.
     * @param nextBlock
     */
    public void addNextBlock(BasicBlock nextBlock) {
        this.nextBlocks.add(nextBlock);
    }

    /**
     * Set the nextBlocks of this BasicBlock.
     * @param nextBlocks
     */
    public void setNextBlocks(ArrayList<BasicBlock> nextBlocks) {
        this.nextBlocks = nextBlocks;
    }

    /**
     * Add an IC Line to this basic block.
     * @param icLine
     */
    public void addLine(ICLine icLine) {
        this.icLines.addLine(icLine);
    }

    /**
     * Get the leader in this basic block.
     * @return
     */
    public ICLine getLeader() {
        return this.icLines.getICLine(0);
    }

    /**
     * Get the list of next blocks.
     * @return
     */
    public ArrayList<BasicBlock> getNextBlocks() {
        return this.nextBlocks;
    }

    @Override
    public String toString() {
        String str = "BasicBlock:\n" + this.icLines.toString();
        return str;
    }
}
