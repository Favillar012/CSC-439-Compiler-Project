package edu.uncg.csc439.mipsgen.FlowGraph;

import edu.uncg.csc439.icode.*;

import java.util.ArrayList;

/**
 * This class implements a flow control graph.
 * @author Fernando Villarreal
 * @date 11/19/2020
 */
public class FCGraph {

    //================== CLASS VARIABLES ==================

    private ArrayList<BasicBlock> basicBlocks;

    //================== CONSTRUCTOR ==================

    /**
     * Create a flow control graph object (FCGraph) for the provided ICode object.
     * @param iCode
     */
    public FCGraph(ICode iCode) {
        this.basicBlocks = new ArrayList<>();
        ICLines icLines = iCode.getIcLines();
        this.createFCGraph(icLines);
    }

    //================== PUBLIC METHODS ==================

    /**
     * Get the basic blocks in this FCGraph in an ArrayList.
     * @return
     */
    public ArrayList<BasicBlock> getBasicBlocksList() {
        return this.basicBlocks;
    }

    /**
     * Get the basic block at the specified index.
     * @param index
     * @return
     */
    public BasicBlock getBasicBlock(int index) {
        return this.basicBlocks.get(index);
    }

    @Override
    public String toString() {
        String str = "\n************ FCGraph ************";
        String blocksStr = "";
        int index = 0;
        for (BasicBlock basicBlock : this.basicBlocks) {
            String nextBlockIndexes = this.getNextBlockIndexes(basicBlock);
            blocksStr += "\n====== BasicBlock " + index + " -> " + nextBlockIndexes + " ======";
            ICLines icLines = basicBlock.getIcLines();
            blocksStr += icLines.toString();
            index++;
        }
        str += blocksStr;
        return str;
    }

    //================== PRIVATE METHODS ==================

    /**
     * Get the indexes of the nextBlocks of the provided basicBlock.
     * @param basicBlock
     * @return
     */
    private String getNextBlockIndexes(BasicBlock basicBlock) {
        String indexesString = "(";
        ArrayList<BasicBlock> nextBlocks = basicBlock.getNextBlocks();
        int length = nextBlocks.size();
        for (int i = 0; i < length; i++) {
            BasicBlock nextBlock = nextBlocks.get(i);
            int index = this.basicBlocks.indexOf(nextBlock);
            indexesString += index;
            if (i < (length - 1)) {
                indexesString += ", ";
            }
        }
        indexesString += ")";
        return indexesString;
    }

    //================== FCGRAPH CREATION ==================

    /**
     * Creates and sets the properties for this FCGraph object.
     * @param icLines
     */
    private void createFCGraph(ICLines icLines) {
        BasicBlock curBlock = new BasicBlock();
        // Iterate over the rest of icLines
        for (ICLine icLine : icLines.getICLinesList()) {
            // If icLine is a leader, begin to fill a new basic block.
            if (this.isLeader(icLine, icLines)) {
                if (!curBlock.isEmpty()) {
                    this.basicBlocks.add(curBlock);
                }
                curBlock = new BasicBlock();
                curBlock.addLine(icLine);
            }
            // icLine is not a leader, put it in curBlock.
            else {
                curBlock.addLine(icLine);
            }
        }
        // Add the last basic block to the list of basic blocks
        this.basicBlocks.add(curBlock);
        // Determine and set the next blocks for each basic block
        for (BasicBlock basicBlock : this.basicBlocks) {
            ArrayList<BasicBlock> nextBlocks = this.determineNextBlocks(basicBlock);
            basicBlock.setNextBlocks(nextBlocks);
        }
    }

    /**
     * Determine if the provided icLine is a leader in icLines.
     * @param icLine
     * @param icLines
     * @return
     */
    private boolean isLeader(ICLine icLine, ICLines icLines) {
        // The instruction is the first instruction in the program
        if (this.isFirstLine(icLine, icLines)) {
            return true;
        }
        // The instruction is the start of a function (fnStart directive)
        else if (this.isStartOfFunction(icLine, icLines)) {
            return true;
        }
        // The instruction is the target of a jump
        else if (this.isTargetOfJump(icLine, icLines)) {
            return true;
        }
        // The instruction comes immediately after some instruction containing a jump
        else if (this.comesAfterJump(icLine, icLines)) {
            return true;
        }
        // The instruction comes immediately after a function call instruction
        else if (this.comesAfterCall(icLine, icLines)) {
            return true;
        }
        // The instruction is a definedWord or definedByte directive with a label
        else if (this.isDefineByteWordDirectiveWithLabel(icLine)) {
            return true;
        }
        // The instruction is not a leader
        return false;
    }

    /**
     * Determine if the provided icLine is the first line in icLines.
     * @param icLine
     * @param icLines
     * @return
     */
    private boolean isFirstLine(ICLine icLine, ICLines icLines) {
        ICLine firstLine = icLines.getICLine(0);
        if (icLine == firstLine) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the provided icLine is the start of a function.
     * @param icLine
     * @param icLines
     * @return
     */
    private boolean isStartOfFunction(ICLine icLine, ICLines icLines) {
        if (icLine instanceof ICDirective) {
            ICDirective icDirective = (ICDirective)icLine;
            int directiveType = icDirective.getDirectiveType();
            if (directiveType == ICDirective.fnStart) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the provided icLine is the target of some jump in icLines. Returns true if it is.
     * @param icLine
     * @param icLines
     * @return
     */
    private boolean isTargetOfJump(ICLine icLine, ICLines icLines) {
        String label = icLine.getLabel();
        // If the label is empty, the instruction cannot be a jump target.
        if (label.isEmpty()) {
            return false;
        }
        // Search for the label in other jump instructions
        for (ICLine scanLine : icLines.getICLinesList()) {
            if (scanLine instanceof ICJump) {
                String gotoLabel = ((ICJump)scanLine).getGotoLabel();
                if (label.compareTo(gotoLabel) == 0) {
                    return true;
                }
            } else if (scanLine instanceof ICReltJump) {
                String gotoLabel = ((ICReltJump)scanLine).getGotoLabel();
                if (label.compareTo(gotoLabel) == 0) {
                    return true;
                }
            } else if (scanLine instanceof ICBoolJump) {
                String gotoLabel = ((ICBoolJump)scanLine).getGotoLabel();
                if (label.compareTo(gotoLabel) == 0) {
                    return true;
                }
            }
        }
        // The instruction is not the target of a jump
        return false;
    }

    /**
     * Determine if the provided icLine comes after some instruction that contains a jump in
     * icLines. Returns true if it does.
     * @param icLine
     * @param icLines
     * @return
     */
    private boolean comesAfterJump(ICLine icLine, ICLines icLines) {
        // Get the index of the provided icLine in icLines
        int lineIndex = icLines.getICLinesList().indexOf(icLine);
        // Get the line that comes before icLine
        ICLine prevLine = icLines.getICLine(lineIndex - 1);
        // Determine if prevLine contains a jump. Return true if so.
        if (prevLine instanceof ICJump) {
            return true;
        } else if (prevLine instanceof ICReltJump) {
            return true;
        } else if (prevLine instanceof ICBoolJump) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the provided icLine comes after some instruction that contains a function call
     * in icLines. Returns true if it does.
     * @param icLine
     * @param icLines
     * @return
     */
    private boolean comesAfterCall(ICLine icLine, ICLines icLines) {
        // Get the index of the provided icLine in icLines
        int lineIndex = icLines.getICLinesList().indexOf(icLine);
        // Get the line that comes before icLine
        ICLine prevLine = icLines.getICLine(lineIndex - 1);
        // Determine if prevLine is a function call instruction
        if (prevLine instanceof ICFuncCall) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the provided icLine is a definedByte or definedWord directive with a label
     * at the end of the program.
     * @param icLine
     * @return
     */
    private boolean isDefineByteWordDirectiveWithLabel(ICLine icLine) {
        if (icLine instanceof ICDirective) {
            ICDirective icDirective = (ICDirective)icLine;
            int directiveType = icDirective.getDirectiveType();
            if (directiveType == ICDirective.definedByte || directiveType == ICDirective.definedWord) {
                String label = icDirective.getLabel();
                if (!label.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine and return the list of next blocks from the provided basicBlock.
     * @param basicBlock
     * @return
     */
    private ArrayList<BasicBlock> determineNextBlocks(BasicBlock basicBlock) {
        int index = this.basicBlocks.indexOf(basicBlock);
        int lastIndex = this.basicBlocks.size() - 1;
        ArrayList<BasicBlock> nextBlocks = new ArrayList<>();
        // Check what type of line lastLine is
        ICLine lastLine = this.getLastLine(basicBlock);
        // The last line is a jump
        if (this.isJump(lastLine)) {
            // The last line is an unconditional jump
            if (lastLine instanceof ICJump) {
                ICJump icJump = (ICJump)lastLine;
                String gotoLabel = icJump.getGotoLabel();
                BasicBlock nextBlock = this.getBlockWithLabel(gotoLabel);
                if (nextBlock != null) {
                    nextBlocks.add(nextBlock);
                }
            }
            // The last line is a conditional jump
            else {
                int nextBlockIndex = index + 1;
                // Get the next block if the nextBlockIndex is not out of bounds
                if (nextBlockIndex <= lastIndex) {
                    BasicBlock nextBlock1 = this.basicBlocks.get(nextBlockIndex);
                    nextBlocks.add(nextBlock1);
                }
                // Get the block to jump to from the conditional jump
                String gotoLabel;
                if (lastLine instanceof ICBoolJump) {
                    gotoLabel = ((ICBoolJump)lastLine).getGotoLabel();
                } else {
                    gotoLabel = ((ICReltJump)lastLine).getGotoLabel();
                }
                BasicBlock nextBlock2 = this.getBlockWithLabel(gotoLabel);
                if (nextBlock2 != null) {
                    nextBlocks.add(nextBlock2);
                }
            }
        }
        // The last line is a function call
        else if (this.isFuncCall(lastLine)) {
            ICFuncCall icFuncCall = (ICFuncCall)lastLine;
            String funcName = icFuncCall.getFuncName();
            BasicBlock nextBlock = this.getBlockWithLabel(funcName);
            if (nextBlock != null) {
                nextBlocks.add(nextBlock);
            }
        }
        // The last line is the end of a function block (a function end directive)
        else if (this.isFunctionEndDirective(lastLine)) {
            // No next blocks (Do nothing)
        }
        // The last line is a define byte or define word directive
        else if (this.isDefineByteWordDirective(lastLine)) {
            // No next blocks (Do nothing)
        }
        // The last line is some other kind of line
        else {
            int nextBlockIndex = index + 1;
            // Get the next block if the nextBlockIndex is not out of bounds
            if (nextBlockIndex <= lastIndex) {
                BasicBlock nextBlock = this.basicBlocks.get(nextBlockIndex);
                nextBlocks.add(nextBlock);
            }
        }
        // Return nextBlocks
        return nextBlocks;
    }

    /**
     * Get the last ICLine in the given BasicBlock.
     * @param basicBlock
     * @return
     */
    private ICLine getLastLine(BasicBlock basicBlock) {
        ICLines icLines = basicBlock.getIcLines();
        int lastIndex = icLines.getSize() - 1;
        return icLines.getICLine(lastIndex);
    }

    /**
     * Check if the provided ICLine is a jump instruction.
     * @param icLine
     * @return
     */
    private boolean isJump(ICLine icLine) {
        if (icLine instanceof ICJump) {
            return true;
        } else if (icLine instanceof ICBoolJump) {
            return true;
        } else if (icLine instanceof ICReltJump) {
            return true;
        }
        return false;
    }

    /**
     * Check if the provided ICLine is a function call instruction.
     * @param icLine
     * @return
     */
    private boolean isFuncCall(ICLine icLine) {
        if (icLine instanceof ICFuncCall) {
            return true;
        }
        return false;
    }

    /**
     * Check if the provided icLine is a function end directive.
     * @param icLine
     * @return
     */
    private boolean isFunctionEndDirective(ICLine icLine) {
        if (icLine instanceof ICDirective) {
            ICDirective icDirective = (ICDirective)icLine;
            int directiveType = icDirective.getDirectiveType();
            if (directiveType == ICDirective.fnEnd) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the provided icLine is a define byte or define word directive.
     * @param icLine
     * @return
     */
    private boolean isDefineByteWordDirective(ICLine icLine) {
        if (icLine instanceof ICDirective) {
            ICDirective icDirective = (ICDirective)icLine;
            int directiveType = icDirective.getDirectiveType();
            if (directiveType == ICDirective.definedByte || directiveType == ICDirective.definedWord) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the basic block with the provided label on its first line. Returns null if the
     * label is not found.
     * @param label
     * @return
     */
    private BasicBlock getBlockWithLabel(String label) {
        for (BasicBlock basicBlock : this.basicBlocks) {
            ICLine firstLine = basicBlock.getIcLines().getICLine(0);
            String curLabel = firstLine.getLabel();
            if (curLabel.compareTo(label) == 0) {
                return basicBlock;
            }
        }
        return null;
    }
}
