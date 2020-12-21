package edu.uncg.csc439.syntaxtree;

import edu.uncg.csc439.*;
import edu.uncg.csc439.syntaxtree.Nodes.Node;

/**
 * Syntax tree class - the purpose of the parser is to construct a syntax
 * tree for any valid LittleC program (and detect errors for invalid programs).
 *
 * While you can use any structure you want in your code, my suggestion is
 * that you make this class be a generic syntax tree node, and then you can
 * extend this class with different node types for each type of node. For
 * example, you'd have a class for "if statement" nodes, and a class for
 * "assignment operations", and so on. I have 19 different node classes
 * defined in my solution.
 *
 * Regardless of how you implement the syntax tree, every tree node needs
 * to have a type, and should keep track of information relevant to that node,
 * and it can have 0 or more children. The README.md file defines all of
 * the syntax tree nodes, and how they should be printed. You'll need to
 * write a tree printing method to show a tree. Spacing is flexible, but
 * otherwise the tree must be printed *exactly* as defined in the README.
 * It won't pass the tests otherwise!
 *
 * @modified Fernando Villarreal
 * @date 11/3/2020
 */
public class LCSyntaxTree {

    private Node root;
    private SymbolTable symbolTable;

    public LCSyntaxTree() { }

    public LCSyntaxTree(Node root) {
        this.root = root;
    }

    public SymbolTable getSymbolTable() {
        return this.symbolTable;
    }

    public Node getRoot() {
        return this.root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    /**
     * A method which will print this syntax tree. Since you need to
     * print the top node, it's children, their children, their children, ...
     * this is obviously going to have to be recursive, probably through
     * a recursive helper function.
     */
    public void printSyntaxTree() {
        System.out.println("Syntax Tree:\n\n" + this.toString());
    }

    @Override
    public String toString() {
        return this.root.toString();
    }
}
