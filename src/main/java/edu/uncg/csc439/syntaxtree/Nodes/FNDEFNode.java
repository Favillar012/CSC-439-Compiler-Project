package edu.uncg.csc439.syntaxtree.Nodes;

/**
 * This class implements function definition nodes.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class FNDEFNode extends NonExprNode {

    private String identifier;
    private String signature;
    private SEQNode parameters;
    private SEQNode body;

    public FNDEFNode(String identifier, String signature) {
        super("FNDEF");
        this.identifier = identifier;
        this.signature = signature;
        this.addChild(new Node()); // Empty Node for the parameters
        this.addChild(new Node()); // Empty Node for the body
    }

    public FNDEFNode(String identifier, String signature, SEQNode body) {
        super("FNDEF");
        this.identifier = identifier;
        this.signature = signature;
        this.body = body;
        this.addChild(new Node()); // Empty Node for the parameters
        this.addChild(body);
    }

    public FNDEFNode(String identifier, String signature, SEQNode parameters, SEQNode body) {
        super("FNDEF");
        this.identifier = identifier;
        this.signature = signature;
        this.parameters = parameters;
        this.body = body;
        this.addChild(parameters);
        this.addChild(body);
    }

    public void setParameters(SEQNode parameters) {
        this.parameters = parameters;
        this.getChildren().add(0, parameters);
    }

    public void setBody(SEQNode body) {
        this.body = body;
        this.getChildren().add(1, body);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getSignature() {
        return this.signature;
    }

    public SEQNode getParameters() {
        return this.parameters;
    }

    public SEQNode getBody() {
        return this.body;
    }

    @Override
    public String toString() {
        this.setChildIndentation();
        String nodeStr = super.toString() + " " + this.identifier + " " + this.signature + " (";
        if (this.parameters != null) {
            nodeStr += "\n" + this.parameters.getIndent() + this.parameters.toString() + ",";
        }
        if (this.body != null) {
            nodeStr += "\n" + this.body.getIndent() + this.body.toString() + ")";
        }
        return nodeStr;
    }

    private void setChildIndentation() {
        if (this.body != null) {
            this.body.setIndent(this.getIndent());
            this.body.increaseIndent();
        }
        if (this.parameters != null) {
            this.parameters.setIndent(this.getIndent());
            this.parameters.increaseIndent();
        }
    }
}
