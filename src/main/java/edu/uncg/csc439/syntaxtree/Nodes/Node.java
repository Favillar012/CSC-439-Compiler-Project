package edu.uncg.csc439.syntaxtree.Nodes;

import java.util.ArrayList;

/**
 * This class acts as the base syntax tree node. All other kinds of nodes will be extensions
 * of this node.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class Node {

    //============== STATIC VARIABLES ==============

    // Node types
    public static final int UNKNOWN = -1;
    public static final int VOID = 0;
    public static final int INT = 1;
    public static final int CHAR = 2;
    public static final int CHAR_AR = 3;
    public static final int INT_AR = 4;

    //============== CLASS VARIABLES ==============

    // Class Variables
    private int type;
    private String label;
    private String indent;
    private ArrayList<Node> children;
    private final boolean isEmpty;

    //============== CONSTRUCTORS ==============

    public Node() {
        this.isEmpty = true;
    }

    public Node(int type) {
        this.type = type;
        this.label = "";
        this.indent = "";
        this.children = new ArrayList<>();
        this.isEmpty = false;
    }

    public Node(int type, String label) {
        this.type = type;
        this.label = label;
        this.indent = "";
        this.children = new ArrayList<>();
        this.isEmpty = false;
    }

    public Node(int type, String label, ArrayList<Node> children) {
        this.type = type;
        this.label = label;
        this.indent = "";
        this.children = children;
        this.isEmpty = false;
    }

    //============== METHODS ==============

    /**
     * Get the type of this node. Note that the type is returned as an integer which
     * should be compared to one of this class' static variables.
     * @return
     */
    public int getType() {
        return this.type;
    }

    /**
     * Get the type of the node as a string.
     * @return
     */
    public String getTypeAsString() {
        String typeStr = "UNKNOWN";
        if (this.type == Node.VOID) {
            typeStr = "void";
        } else if (this.type == Node.INT) {
            typeStr = "int";
        } else if (this.type == Node.CHAR) {
            typeStr = "char";
        } else if (this.type == Node.CHAR_AR) {
            typeStr = "char[]";
        } else if (this.type == Node.INT_AR) {
            typeStr = "int[]";
        }
        return typeStr;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<Node> getChildren() {
        return this.children;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public Node getChild(int index) {
        return this.children.get(index);
    }

    public int getChildCount() {
        return this.children.size();
    }

    /**
     * Check if this node is empty. An empty node is equivalent to a null node.
     * @return
     */
    public boolean isEmpty() {
        return this.isEmpty;
    }

    public String childlessToString() {
        if (this.isEmpty()) {
            return "";
        }
        String typeStr = "UNKNOWN";
        if (this.type == Node.VOID) {
            typeStr = "void";
        } else if (this.type == Node.INT) {
            typeStr = "int";
        } else if (this.type == Node.CHAR) {
            typeStr = "char";
        } else if (this.type == Node.CHAR_AR) {
            typeStr = "char[]";
        } else if (this.type == Node.INT_AR) {
            typeStr = "int[]";
        }
        String nodeStr = typeStr + " " + this.label;
        return nodeStr;
    }

    @Override
    public String toString() {
        this.setChildIndentation();
        String typeStr;
        String nodeStr = "";
        if (this.type == Node.VOID) {
            typeStr = "void ";
            nodeStr = typeStr + this.label;
        } else if (this.type == Node.INT) {
            typeStr = "int ";
            nodeStr = typeStr + this.label;
        } else if (this.type == Node.CHAR) {
            typeStr = "char ";
            nodeStr = typeStr + this.label;
        } else if (this.type == Node.CHAR_AR) {
            typeStr = "char";
            nodeStr = typeStr + this.label;
        } else if (this.type == Node.INT_AR) {
            typeStr = "int";
            nodeStr = typeStr + this.label;
        }
        /* Do not append children toStrings if the node has its own child pointers */
        if (this.hasOwnChildPointers()) {
            return nodeStr;
        }
        // Append any and all children in a string
        int childCount = this.getChildCount();
        String childrenStr = "";
        for (int i = 0; i < childCount; i++) {
            childrenStr += "\n" + this.children.get(i).getIndent() + this.children.get(i).toString();
            if (i < (childCount - 1)) {
                childrenStr += ",";
            }
        }
        if (childCount > 0) {
            nodeStr += " (" + childrenStr + ")";
        }
        return nodeStr;
    }

    public String getIndent() {
        return this.indent;
    }

    public void setIndent(String indent) {
        this.indent = indent;
    }

    public void increaseIndent() {
        this.indent += "\t";
    }

    private void setChildIndentation() {
        for (Node child : this.children) {
            child.setIndent(this.getIndent());
            child.increaseIndent();
        }
    }

    /**
     * Checks if this node is one that has its own child pointers. Nodes that have
     * their own child pointers are: FNDEFNode, IFNode, INCDECNode, RETURNNode, UNARYOPNode, and
     * WHILENode.
     * @return
     */
    private boolean hasOwnChildPointers() {
        if (this instanceof FNDEFNode) {
            return true;
        } else if (this instanceof IFNode) {
            return true;
        } else if (this instanceof INCDECNode) {
            return true;
        } else if (this instanceof RETURNNode) {
            return true;
        } else if (this instanceof UNARYOPNode) {
            return true;
        } else if (this instanceof WHILENode) {
            return true;
        }
        return false;
    }
}
