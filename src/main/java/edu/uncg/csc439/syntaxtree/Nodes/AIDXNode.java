package edu.uncg.csc439.syntaxtree.Nodes;

import java.util.ArrayList;

/**
 * This class implements array indexing nodes.
 * @author Fernando Villarreal
 * @date 10/22/2020
 */
public class AIDXNode extends Node {

    public AIDXNode(int type) {
        super(type, "AIDX");
    }

    public AIDXNode(int type, ArrayList<Node> children) {
        super(type, "AIDX", children);
    }

}
