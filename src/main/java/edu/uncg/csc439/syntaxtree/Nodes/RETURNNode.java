package edu.uncg.csc439.syntaxtree.Nodes;

/**
 * This class implements return statement nodes.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class RETURNNode extends NonExprNode {

    private Node returnValue;

    public RETURNNode() {
        super("RETURN");
        this.addChild(new Node()); // Empty Node for the returnValue
    }

    public RETURNNode(Node returnValue) {
        super("RETURN");
        this.returnValue = returnValue;
        this.addChild(returnValue);
    }

    public Node getReturnValue() {
        return this.returnValue;
    }

    public void setReturnValue(Node returnValue) {
        this.returnValue = returnValue;
        this.getChildren().add(0, returnValue);
    }

    @Override
    public String toString() {
        this.setChildIndentation();
        if (this.returnValue != null) {
            String nodeStr = super.toString() + " ("
                    + "\n" + this.returnValue.getIndent() + this.returnValue.toString() + ")";
            return nodeStr;
        }
        return super.toString() + " ()";
    }

    private void setChildIndentation() {
        if (this.returnValue != null) {
            this.returnValue.setIndent(this.getIndent());
            this.returnValue.increaseIndent();
        }
    }
}
