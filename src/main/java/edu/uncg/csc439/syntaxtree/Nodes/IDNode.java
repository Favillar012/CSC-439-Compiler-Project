package edu.uncg.csc439.syntaxtree.Nodes;

/**
 * This class implements a variable/identifier node.
 * @author Fernando Villarreal
 * @date 10/8/2020
 */
public class IDNode extends Node {

    private String identifier;

    public IDNode(int type, String id) {
        super(type, "ID");
        this.identifier = id;
    }

    public IDNode(int type, String id, String arrayStr) {
        super(type, arrayStr + " ID");
        this.identifier = id;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return super.toString() + " " + this.identifier;
    }
}
