package edu.uncg.csc439.syntaxtree.Nodes;

import java.util.ArrayList;

/**
 * This class implements binary operator nodes.
 * @author Fernando Villarreal
 * @date 11/6/2020
 */
public class BINOPNode extends Node {

    private String operator;

    public BINOPNode(int type, String operator) {
        super(type);
        String label = "BINOP('" + operator + "')";
        this.operator = operator;
        this.setLabel(label);
    }

    public BINOPNode(int type, String operator, ArrayList<Node> children) {
        super(type);
        String label = "BINOP('" + operator + "')";
        this.operator = operator;
        this.setLabel(label);
        this.setChildren(children);
    }

    public String getOperator() {
        return this.operator;
    }
}
