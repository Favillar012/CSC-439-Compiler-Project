package edu.uncg.csc439.icode.ICCreation;

import edu.uncg.csc439.syntaxtree.LCSyntaxTree;
import edu.uncg.csc439.syntaxtree.Nodes.Node;

import java.util.ArrayList;

/**
 * This class helps to navigate, or walk, through the given syntax tree.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class SyntaxTreeWalker {

    //============= CLASS VARIABLES =============

    // Root of Syntax Tree
    private Node root;
    // Post Order Properties
    private ArrayList<Node> postOrderNodes;
    private int curIndex;

    //============= CONSTRUCTOR =============

    public SyntaxTreeWalker(LCSyntaxTree syntaxTree) {
        this.root = syntaxTree.getRoot();
        this.postOrderNodes = new ArrayList<>();
        this.curIndex = 0;
    }

    //============= METHODS =============

    /**
     * Performs a post order traversal of this walker's syntax tree, starting at the root.
     * @return
     */
    public String postOrder() {
        this.postOrderNodes = new ArrayList<>();
        return this.postOrder(this.root);
    }

    /**
     * Performs a post order traversal on the given syntax tree node.
     * @param node
     * @return
     */
    private String postOrder(Node node) {
        String postOrderStr = "";
        // Navigate the tree in post order
        if (!node.isEmpty()) {
            ArrayList<Node> children = node.getChildren();
            for (Node child : children) {
                postOrderStr += this.postOrder(child);
            }
            this.postOrderNodes.add(node);
            postOrderStr += "\n" + node.childlessToString();
        }
        return postOrderStr;
    }

    /**
     * Start a post order traversal on the syntax tree. Get the current node in the traversal
     * using this.nextPostOrderNode(). Calling this method also resets the traversal.
     */
    public void startPostOrder() {
        if (this.postOrderNodes.isEmpty()) {
            this.postOrder();
            this.curIndex = 0;
        } else {
            this.curIndex = 0;
        }
    }

    /**
     * Get the next node in the current post order traversal.
     * @return
     */
    public Node nextPostOrderNode() {
        try {
            Node node = this.postOrderNodes.get(this.curIndex);
            this.curIndex++;
            return node;
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("End of Post Order Traversal Reached. Empty node returned.");
        }
        return new Node();
    }

    /**
     * Get the nth node in the post order traversal.
     * @param n
     * @return
     */
    public Node getPostOrderNode(int n) {
        try {
            return this.postOrderNodes.get(n);
        } catch (IndexOutOfBoundsException ex) {
            System.err.println("Invalid Index. Empty node returned.");
        }
        return new Node();
    }

    /**
     * Get an ArrayList containing the nodes of the syntax tree in post order.
     * @return
     */
    public ArrayList<Node> getPostOrderNodes() {
        return this.postOrderNodes;
    }

    /**
     * Returns the root of the syntax tree.
     * @return
     */
    public Node getSyntaxTreeRoot() {
        return this.root;
    }

    /**
     * Print the syntax tree of this walker.
     */
    public void printSyntaxTree() {
        System.out.println(this.root.toString());
    }

    @Override
    public String toString() {
        return "Syntax Tree Info:\n\n Root: " + this.root.childlessToString() + "\n\n";
    }
}
