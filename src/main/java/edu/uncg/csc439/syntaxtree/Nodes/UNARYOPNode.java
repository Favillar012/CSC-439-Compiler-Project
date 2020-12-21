package edu.uncg.csc439.syntaxtree.Nodes;

/**
 * This class implements a unary operator node.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class UNARYOPNode extends Node {

    private Node value;
    private String operator;

    public UNARYOPNode(int type, char operator, Node value) {
        super(type);
        String label = "UNARYOP('" + operator + "')";
        this.setLabel(label);
        this.value = value;
        this.operator = "" + operator;
        this.addChild(value);
    }

    public Node getValue() {
        return this.value;
    }

    public void setValue(Node value) {
        this.value = value;
        this.getChildren().add(0, value);
    }

    public String getOperator() {
        return this.operator;
    }

    @Override
    public String toString() {
        this.setChildIndentation();
        String nodeStr = super.toString() + "("
                + "\n" + this.value.getIndent() + this.value.toString() + ")";
        return nodeStr;
    }

    private void setChildIndentation() {
        this.value.setIndent(this.getIndent());
        this.value.increaseIndent();
    }
}
