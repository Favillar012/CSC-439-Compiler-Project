package edu.uncg.csc439.syntaxtree.Nodes;

/**
 * This class implements parameters declaration nodes.
 * @author Fernando Villarreal
 * @date 10/9/2020
 */
public class PDECLNode extends NonExprNode {

    private String idType;
    private String identifier;

    public PDECLNode(String idType, String identifier) {
        super("PDECL");
        this.idType = idType;
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getIdType() {
        return this.idType;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    @Override
    public String toString() {
        return super.toString() + " " + this.identifier + " (" + this.idType + ")";
    }
}
