package edu.uncg.csc439.syntaxtree.Nodes;

/**
 * This class implements a while loop statement.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class WHILENode extends NonExprNode {

    private Node condition;
    private SEQNode block;

    public WHILENode(Node condition, SEQNode block) {
        super("WHILE");
        this.condition = condition;
        this.block = block;
        this.addChild(condition);
        this.addChild(block);
    }

    public void setCondition(Node condition) {
        this.condition = condition;
        this.getChildren().add(0, condition);
    }

    public void setBlock(SEQNode block) {
        this.block = block;
        this.getChildren().add(1, block);
    }

    public Node getCondition() {
        return this.condition;
    }

    public SEQNode getBlock() {
        return this.block;
    }

    @Override
    public String toString() {
        this.setChildIndentation();
        String bodyStr = "\n" + this.block.getIndent() + this.block.toString() + ")";
        if (this.block.getChildCount() == 1) {
            Node childNode = this.block.getChild(0);
            bodyStr = "\n" + this.block.getIndent() + childNode.toString() + ")";
        }
        String nodeStr = super.toString() + "("
                + "\n" + this.condition.getIndent() + this.condition.toString() + ","
                + bodyStr;
        return nodeStr;
    }

    private void setChildIndentation() {
        this.condition.setIndent(this.getIndent());
        this.block.setIndent(this.getIndent());
        this.condition.increaseIndent();
        this.block.increaseIndent();
    }
}
