package edu.uncg.csc439.icode.ICCreation;

import edu.uncg.csc439.syntaxtree.Nodes.Node;

import java.util.ArrayList;

/**
 * This class helps to manage nested sequences of if-else statements.
 * @author Fernando Villarreal
 * @date 11/9/2020
 */
public class IfsManager {

    //============= CLASS VARIABLES =============

    private ArrayList<IfConstruct> ifConstructs;

    //============= CONSTRUCTOR =============

    public IfsManager() {
        this.ifConstructs = new ArrayList<>();
        this.ifConstructs.add(new IfConstruct());
    }

    //============= METHODS =============

    /**
     * Returns true if the current elsePar is present, false otherwise.
     * @return
     */
    public boolean isElsePartPresent() {
        if (this.currentIfConstruct().elsePart() != null) {
            return true;
        }
        return false;
    }

    /**
     * Entered a new if statement without an else clause.
     * @param ifCondition
     * @param ifPart
     */
    public void enteredIfStatement(Node ifCondition, Node ifPart) {
        int size = this.ifConstructs.size();
        if (size == 1) {
            IfConstruct ifConstruct = new IfConstruct(ifCondition, ifPart);
            this.ifConstructs.set(0, ifConstruct);
        } else {
            IfConstruct ifConstruct = new IfConstruct(ifCondition, ifPart);
            this.ifConstructs.add(ifConstruct);
        }
    }

    /**
     * Entered a new if statement with an else clause.
     * @param ifCondition
     * @param ifPart
     * @param elsePart
     */
    public void enteredIfStatement(Node ifCondition, Node ifPart, Node elsePart) {
        int size = this.ifConstructs.size();
        if (size == 1) {
            IfConstruct ifConstruct = new IfConstruct(ifCondition, ifPart, elsePart);
            this.ifConstructs.set(0, ifConstruct);
        } else {
            IfConstruct ifConstruct = new IfConstruct(ifCondition, ifPart, elsePart);
            this.ifConstructs.add(ifConstruct);
        }
    }

    /**
     * Exited the most recent if statement that was entered.
     */
    public void exitedIfStatement() {
        int size = this.ifConstructs.size();
        if (size == 1) {
            IfConstruct emptyIfConstruct = new IfConstruct();
            this.ifConstructs.set(0, emptyIfConstruct);
        } else {
            int lastIndex = size - 1;
            this.ifConstructs.remove(lastIndex);
        }
    }

    public Node currentIfCondition() {
        Node ifCondition = this.currentIfConstruct().ifCondition();
        return ifCondition;
    }

    public Node currentIfPart() {
        Node ifPart = this.currentIfConstruct().ifPart();
        return ifPart;
    }

    public Node currentElsePart() {
        Node elsePart = this.currentIfConstruct().elsePart();
        return elsePart;
    }

    public String getSkipIfPartLabel() {
        String label = this.currentIfConstruct().getSkipIfPartLabel();
        return label;
    }

    public String getSkipElsePartLabel() {
        String label = this.currentIfConstruct().getSkipElsePartLabel();
        return label;
    }

    public void setSkipIfPartLabel(String skipIfPartLabel) {
        this.currentIfConstruct().setSkipIfPartLabel(skipIfPartLabel);
    }

    public void setSkipElsePartLabel(String skipElsePartLabel) {
        this.currentIfConstruct().setSkipElsePartLabel(skipElsePartLabel);
    }

    private IfConstruct currentIfConstruct() {
        int index = this.ifConstructs.size() - 1;
        IfConstruct currentIfConstruct = this.ifConstructs.get(index);
        return currentIfConstruct;
    }

    //============= PRIVATE INNER CLASSES =============

    /**
     * Private inner class IfConstruct manages the different constructs and labels required
     * for a single if-else statement.
     * @author Fernando Villarreal
     * @date 11/9/2020
     */
    private class IfConstruct {

        //============= CLASS VARIABLES =============

        private Node ifCondition;
        private Node ifPart;
        private Node elsePart;
        private String skipIfPartLabel;
        private String skipElsePartLabel;

        //============= CONSTRUCTORS =============

        private IfConstruct() {
            this.ifCondition = null;
            this.ifPart = null;
            this.elsePart = null;
        }

        private IfConstruct(Node ifCondition, Node ifPart) {
            this.ifCondition = ifCondition;
            this.ifPart = ifPart;
            this.elsePart = null;
        }

        private IfConstruct(Node ifCondition, Node ifPart, Node elsePart) {
            this.ifCondition = ifCondition;
            this.ifPart = ifPart;
            this.elsePart = elsePart;
        }

        //============= METHODS =============

        private Node ifCondition() {
            return this.ifCondition;
        }

        private Node ifPart() {
            return this.ifPart;
        }

        private Node elsePart() {
            return this.elsePart;
        }

        private String getSkipIfPartLabel() {
            return this.skipIfPartLabel;
        }

        private String getSkipElsePartLabel() {
            return this.skipElsePartLabel;
        }

        private void setSkipIfPartLabel(String skipIfPartLabel) {
            this.skipIfPartLabel = skipIfPartLabel;
        }

        private void setSkipElsePartLabel(String skipElsePartLabel) {
            this.skipElsePartLabel = skipElsePartLabel;
        }
    }
}
