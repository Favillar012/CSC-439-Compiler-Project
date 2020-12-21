package edu.uncg.csc439.syntaxtree.Nodes;

/**
 * This class implements pre- and post- increment and decrement nodes.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class INCDECNode extends Node {

    public static final String PREINC = "PRE-INC";
    public static final String PREDEC = "PRE-DEC";
    public static final String POSTINC = "POST-INC";
    public static final String POSTDEC = "POST-DEC";

    private Node value;
    private final String oprt;

    public INCDECNode(int type, String label, Node value) {
        super(type, label);
        this.oprt = label;
        this.value = value;
        this.addChild(value);
    }

    public String getOprt() {
        return this.oprt;
    }

    public Node getValue() {
        return this.value;
    }

    public void setValue(Node value) {
        this.value = value;
        this.getChildren().add(0, value);
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
