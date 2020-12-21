package edu.uncg.csc439.syntaxtree.Nodes;

import java.util.ArrayList;

/**
 * This class implements type casting nodes.
 * @author Fernando Villarreal
 * @date 10/22/2020
 */
public class CASTNode extends Node {

    public CASTNode(int type) {
        super(type, "CAST");
    }

    public CASTNode(int type, ArrayList<Node> child) {
        super(type, "CAST", child);
    }
}
