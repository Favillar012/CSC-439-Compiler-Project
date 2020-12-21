package edu.uncg.csc439.syntaxtree.Nodes;

import java.util.ArrayList;

/**
 * This class implements a function call node.
 * @author Fernando Villarreal
 * @date 10/10/2020
 */
public class FNCALLNode extends Node {

    private String funcID;

    public FNCALLNode(int type, String funcID) {
        super(type, "FNCALL " + funcID);
        this.funcID = funcID;
    }

    public FNCALLNode(int type, String funcID, ArrayList<Node> children) {
        super(type, "FNCALL " + funcID, children);
        this.funcID = funcID;
    }

    public void setFuncID(String funcID) {
        this.funcID = funcID;
    }

    public String getFuncID() {
        return this.funcID;
    }
}
