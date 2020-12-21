package edu.uncg.csc439.syntaxtree.Nodes;

import java.util.ArrayList;

/**
 * This is a base node class for non-expression nodes. All non-expression
 * nodes have type "void".
 * @author Fernando Villarreal
 * @date 10/9/2020
 */
public class NonExprNode extends Node {

    public NonExprNode(String label) {
        super(Node.VOID, label);
    }

    public NonExprNode(String label, ArrayList<Node> children) {
        super(Node.VOID, label, children);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
