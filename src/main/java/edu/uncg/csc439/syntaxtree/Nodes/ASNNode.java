package edu.uncg.csc439.syntaxtree.Nodes;

import java.util.ArrayList;

/**
 * This class implements assignment nodes.
 * @author Fernando Villarreal
 * @date 10/8/2020
 */
public class ASNNode extends Node {

    public ASNNode(int type) {
        super(type, "ASN");
    }

    public ASNNode(int type, ArrayList<Node> children) {
        super(type, "ASN", children);
    }
}
