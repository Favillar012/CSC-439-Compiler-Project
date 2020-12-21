package edu.uncg.csc439.syntaxtree.Nodes;

import edu.uncg.csc439.LCElements.LCChar;
import edu.uncg.csc439.LCElements.LCCharArray;
import edu.uncg.csc439.LCElements.LCInteger;
import edu.uncg.csc439.LCElements.LCType;

/**
 * This class implements constant/literal nodes.
 * @author Fernando Villarreal
 * @date 10/19/2020
 */
public class LITNode extends Node {

    private LCType literal;

    public LITNode(int type, LCType literal) {
        super(type, "LIT");
        if (type == Node.CHAR_AR) {
            this.setLabel("[] LIT");
        }
        this.literal = literal;
        this.setLabel(this.literalToString());
    }

    public LCType getLiteral() {
        return this.literal;
    }

    public void setLiteral(LCType literal) {
        this.literal = literal;
    }

    @Override
    public String toString() {
        String nodeStr = super.toString();
        return nodeStr;
    }

    private String literalToString() {
        String literalStr = this.getLabel() + " = ";
        if (this.literal.isLCInteger()) {
            literalStr += ((LCInteger)this.literal).getValue();
        } else if (this.literal.isLCChar()) {
            literalStr += ((LCChar)this.literal).getCharacter();
        } else if (this.literal.isLCCharArray()) {
            literalStr += ((LCCharArray)this.literal).getAsString();
        } else {
            literalStr += "null";
        }
        // LCChar has not been implemented yet
        return literalStr;
    }
}
