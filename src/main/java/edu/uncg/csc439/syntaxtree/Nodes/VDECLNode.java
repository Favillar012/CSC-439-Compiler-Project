package edu.uncg.csc439.syntaxtree.Nodes;

import edu.uncg.csc439.LCElements.LCExpr;

/**
 * This class implements a variable declaration node.
 * @author Fernando Villarreal
 * @date 10/9/2020
 */
public class VDECLNode extends NonExprNode {

    private String idType;
    private String identifier;
    private String initializer;

    public VDECLNode(String idType, String identifier) {
        super("DECL");
        this.idType = idType;
        this.identifier = identifier;
        this.initializer = "";
    }

    public VDECLNode(String idType, String identifier, LCExpr arraySize) {
        super("DECL");
        String sizeStr = arraySize.getExprStr();
        this.idType = idType + "[" + sizeStr + "]";
        this.identifier = identifier;
        this.initializer = "";
    }

    public VDECLNode(String idType, String identifier, String initializer) {
        super("DECL");
        this.idType = idType;
        this.identifier = identifier;
        this.initializer = initializer;
    }

    public VDECLNode(String idType, String identifier, String initializer, LCExpr arraySize) {
        super("DECL");
        String sizeStr = arraySize.getExprStr();
        this.idType = idType + "[" + sizeStr + "]";
        this.identifier = identifier;
        this.initializer = initializer;
    }

    public String getIdType() {
        return this.idType;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getInitializer() {
        return this.initializer;
    }

    @Override
    public String toString() {
        String nodeStr = super.toString() + " " + this.identifier + " (" + this.idType + ")";
        if (!this.initializer.isEmpty()) {
            nodeStr += " = " + this.initializer;
        }
        return nodeStr;
    }
}
