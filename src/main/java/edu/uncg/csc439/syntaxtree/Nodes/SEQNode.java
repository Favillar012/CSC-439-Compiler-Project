package edu.uncg.csc439.syntaxtree.Nodes;

import java.util.ArrayList;

/**
 * This class implements a node that represents a sequence of syntax trees.
 * @author Fernando Villarreal
 * @date 10/12/2020
 */
public class SEQNode extends NonExprNode {

    public SEQNode() {
        super("SEQ");
    }

    public SEQNode(ArrayList<Node> children) {
        super("SEQ", children);
    }
}
