package edu.uncg.csc439.syntaxtree.Nodes;

/** This class implements if statement nodes.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class IFNode extends NonExprNode {

    private Node condition;
    private SEQNode thenPart;
    private SEQNode elsePart;

    public IFNode(Node condition, SEQNode thenPart) {
        super("IF");
        this.condition = condition;
        this.thenPart = thenPart;
        this.addChild(condition);
        this.addChild(thenPart);
    }

    public IFNode(Node condition, SEQNode thenPart, SEQNode elsePart) {
        super("IF");
        this.condition = condition;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
        this.addChild(condition);
        this.addChild(thenPart);
        this.addChild(elsePart);
    }

    public void setCondition(Node condition) {
        this.condition = condition;
        this.getChildren().add(0, condition);
    }

    public void setThenPart(SEQNode thenPart) {
        this.thenPart = thenPart;
        this.getChildren().add(1, thenPart);
    }

    public void setElsePart(SEQNode elsePart) {
        this.elsePart = elsePart;
        this.getChildren().add(2, elsePart);
    }

    public Node getCondition() {
        return this.condition;
    }

    public SEQNode getThenPart() {
        return this.thenPart;
    }

    public SEQNode getElsePart() {
        return this.elsePart;
    }

    public boolean isElsePresent() {
        if (this.elsePart != null) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        this.setChildIndentation();
        // If the SEQNode thenPart has one child, get it.
        String thenStr = "\n" + this.thenPart.getIndent() + this.thenPart.toString();
        if (this.thenPart.getChildCount() == 1) {
            Node thenChild = this.thenPart.getChild(0);
            thenStr = "\n" + this.thenPart.getIndent() + thenChild.toString();
        }
        // If the SEQNode elsePart has one child, get it.
        String elseStr = "";
        if (this.isElsePresent()) {
            elseStr += "," + "\n" + this.elsePart.getIndent() + this.elsePart.toString();
            if (this.elsePart.getChildCount() == 1) {
                Node elseChild = this.elsePart.getChild(0);
                elseStr = "," + "\n" + this.elsePart.getIndent() + elseChild.toString();
            }
        }
        // Node String
        String nodeStr = super.toString() + " ("
                + "\n" + this.condition.getIndent() + this.condition.toString() + ","
                + thenStr;
        if (this.isElsePresent()) {
            nodeStr += elseStr;
        }
        nodeStr += ")";
        return nodeStr;
    }

    private void setChildIndentation() {
        this.condition.setIndent(this.getIndent());
        this.thenPart.setIndent(this.getIndent());
        this.condition.increaseIndent();
        this.thenPart.increaseIndent();
        if (this.elsePart != null) {
            this.elsePart.setIndent(this.getIndent());
            this.elsePart.increaseIndent();
        }
    }
}
