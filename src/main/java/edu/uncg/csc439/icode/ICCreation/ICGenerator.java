package edu.uncg.csc439.icode.ICCreation;

import edu.uncg.csc439.LCElements.*;
import edu.uncg.csc439.SymbolTable;
import edu.uncg.csc439.icode.*;
import edu.uncg.csc439.syntaxtree.LCSyntaxTree;
import edu.uncg.csc439.syntaxtree.Nodes.*;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class is responsible for walking through a provided syntax tree and
 * generating its intermediate code.
 * @author Fernando Villarreal
 * @date 11/9/2020
 */
public class ICGenerator {

    //=============== CLASS VARIABLES ===============

    // Root of Syntax Tree
    private Node root;
    // Symbol Table associated with the syntax tree
    private SymbolTable symbolTable;
    // Names Generator
    private ICNames names;
    // Collection of IC Lines of the program
    private ICLines programLines;
    private ICLines endOfFileDirectives;
    // Assistance Variables
    private boolean inFunction;
    private boolean inWhileBINOPNode;
    // Memory Offsets for Locals and Parameters
    private int memoryOffsetLocals;
    private int memoryOffsetParameters;
    // Saved ICLine objects for post-creation updates
    private ICDirective fnStartDirective; // .fnStart directive is updated with the parameters space
    private ICLines whileLoopStart;    // The while loop start needs to be read and written
    // Saved Generated Parameter and Locals Names
    private ArrayList<String> savedParameterNames;
    private ArrayList<String> savedLocalNames;
    // Saved Function Call child nodes representing the actual parameters
    private ArrayList<Node> savedActualParameterNodes;
    // Saved names for values from expressions and string literals
    private Hashtable<Node, String> savedExpressionNames;
    // Saved nodes for special cases
    private AIDXNode writeAIDXNode;     // AIDXNodes used for writing to arrays
    private BINOPNode whileBINOPNode;   // BINOPNodes used as the condition for a loop
    // Saved control flow segments
    private IfsManager ifManager;
    private ArrayList<ICJump> breakOps;

    //=============== CONSTRUCTOR ===============

    /**
     * Create a new ICGenerator object for the creation of the intermediate code
     * of the provided syntax tree. The intermediate code is generated automatically
     * upon creation of this object. Use this.getIcLines to get the intermediate
     * code in an ICLines object.
     * @param syntaxTree
     */
    public ICGenerator(LCSyntaxTree syntaxTree) {
        this.root = syntaxTree.getRoot();
        this.symbolTable = syntaxTree.getSymbolTable();
        this.names = new ICNames();
        this.programLines = new ICLines();
        this.endOfFileDirectives = new ICLines();
        this.savedParameterNames = new ArrayList<>();
        this.savedLocalNames = new ArrayList<>();
        this.savedActualParameterNodes = new ArrayList<>();
        this.savedExpressionNames = new Hashtable<>();
        this.breakOps = new ArrayList<>();
        this.ifManager = new IfsManager();
        this.inFunction = false;
        this.inWhileBINOPNode = false;
        this.memoryOffsetLocals = 0;
        this.memoryOffsetParameters = 0;
        this.generateICCode();
    }

    //=============== METHODS ===============

    /**
     * Get the ICLines object created by this ICGenerator.
     * @return
     */
    public ICLines getProgramLines() {
        return this.programLines;
    }

    //============= IC CREATION AND TREE TRAVERSAL =============

    /**
     * Generate the intermediate code of the syntax tree.
     */
    private void generateICCode() {
        // Generate the predefined functions
        this.genPredefinedFunctions();
        // Traverse the syntax tree
        this.traverseTree(this.root);
        ArrayList<ICLine> directivesList = this.endOfFileDirectives.getICLinesList();
        this.programLines.addLines(directivesList);
    }

    /**
     * Perform a traversal on the syntax tree beginning at the provided node.
     * @param node
     */
    private void traverseTree(Node node) {
        if (!node.isEmpty()) {
            // Pre-order actions
            this.preOrderActions(node);
            // Visit every child node
            ArrayList<Node> children = node.getChildren();
            for (Node child : children) {
                this.traverseTree(child);
            }
            // Post-order actions
            this.postOrderActions(node);
        }
    }

    //============= PRE AND POST ORDER ACTIONS =============

    /**
     * Perform the required pre-order actions for the provided node to generate its respective
     * ICLine objects.
     * @param node
     * @return
     */
    private void preOrderActions(Node node) {
        // Entering a function node
        boolean enteredFunction = this.enteringFunction(node);
        if (enteredFunction) {
            FNDEFNode fnNode = (FNDEFNode)node;
            ICDirective icDirective = this.genFnStartDirective(fnNode);
            this.programLines.addLine(icDirective);
        }
        // Entering a while loop
        boolean enteredWhileLoop = this.enteringWhileLoop(node);
        if (enteredWhileLoop) {
            // Get the BINOPNode representing the loop condition and save it
            BINOPNode binopNode = (BINOPNode)node.getChild(0);
            this.whileBINOPNode = binopNode;
            // Generate the first two lines of the while loop
            WHILENode whileNode = (WHILENode)node;
            ICLines whileLoopStart = this.genWhileLoopStart(whileNode);
            // Save the while loop start so it can be added, updated, and read from later.
            this.whileLoopStart = whileLoopStart;
        }
        // Entering an array write operation
        boolean enteredArrayWriteOperation = this.enteringArrayWriteOperation(node);
        if (enteredArrayWriteOperation) {
            // The node is an ASNNode and its first child is an AIDXNode
            AIDXNode aidxNode = (AIDXNode)node.getChild(0);
            this.writeAIDXNode = aidxNode;
        }
        // Entered this.whileBINOPNode
        boolean enteredWhileBINOPNode = this.enteringWhileBINOPNode(node);
        if (enteredWhileBINOPNode) {
            this.inWhileBINOPNode = true;
        }
        // Entering a function call operation
        boolean enteredFunctionCall = this.enteringFunctionCall(node);
        if (enteredFunctionCall) {
            // Save the actual parameters nodes for later use (The list could be empty)
            FNCALLNode fncallNode = (FNCALLNode)node;
            ArrayList<Node> children = fncallNode.getChildren();
            for (Node child : children) {
                this.savedActualParameterNodes.add(child);
            }
        }
        // Entering an if-else statement
        boolean enteredIfStatement = this.enteringIfStatement(node);
        if (enteredIfStatement) {
            IFNode ifNode = (IFNode)node;
            this.saveIfStatementChildNodes(ifNode);
        }
    }

    /**
     * Perform the required post-order actions for the provided node to generate its respective
     * ICLine objects.
     * @param node
     * @return
     */
    private void postOrderActions(Node node) {
        // Exiting a global variable declaration
        boolean exitedGlobalVariableDeclaration = this.exitingGlobalVariableDeclaration(node);
        if (exitedGlobalVariableDeclaration) {
            VDECLNode vdeclNode = (VDECLNode)node;
            ICLines icLines = this.genGlobalVariableDirective(vdeclNode);
            // Add the icLines to the endOfFileDirectives list
            this.endOfFileDirectives.addLines(icLines.getICLinesList());
        }
        // Exiting function body
        boolean exitedFunction = this.exitingFunction(node);
        if (exitedFunction) {
            FNDEFNode fnNode = (FNDEFNode)node;
            ICDirective icDirective = this.genFnEndDirective(fnNode);
            this.programLines.addLine(icDirective);
            // Update the parameter for the corresponding .fnStart directive
            String fnStartParameter = "" + this.memoryOffsetLocals;
            this.fnStartDirective.setFirstParameter(fnStartParameter);
            // Reset the memory offset for parameters and locals
            this.resetMemoryOffsetForParameters();
            this.resetMemoryOffsetForLocals();
            // Remove the saved parameters from icNames's list
            this.names.removeGeneratedNames(this.savedParameterNames);
            //this.icNames.removeGeneratedNames(this.savedLocalNames);
        }
        // Exiting a parameter declaration
        boolean exitedParameterDeclaration = this.exitingParameterDeclaration(node);
        if (exitedParameterDeclaration) {
            PDECLNode pdeclNode = (PDECLNode)node;
            this.genParameterDeclarationName(pdeclNode);
        }
        // Exiting a local variable declaration
        boolean exitedLocalDeclaration = this.exitingLocalDeclaration(node);
        if (exitedLocalDeclaration) {
            VDECLNode vdeclNode = (VDECLNode)node;
            ICLine icLine = this.genLocalDeclarationLine(vdeclNode);
            this.programLines.addLine(icLine);
        }
        // Exiting an ASNNode that does not involve an array write operation
        boolean exitedAssignment = this.exitingAssignment(node);
        if (exitedAssignment) {
            ASNNode asnNode = (ASNNode)node;
            ICLine copyInstruction = this.genAssignmentCopyInstruction(asnNode);
            this.programLines.addLine(copyInstruction);
        }
        // Exiting an IDNode
        boolean exitedId = this.exitingId(node);
        if (exitedId) {
            IDNode idNode = (IDNode)node;
            this.getAndSaveGenNameForId(idNode);
        }
        // Exiting a LITNode
        boolean exitedLiteral = this.exitingLiteral(node);
        if (exitedLiteral) {
            LITNode litNode = (LITNode)node;
            this.genAndSaveGenNameForLit(litNode);
        }
        // Exiting a cast operation
        boolean exitedCastOperation = this.exitingCastOperation(node);
        if (exitedCastOperation) {
            CASTNode castNode = (CASTNode)node;
            ICLine castOperation = this.genCastOperation(castNode);
            this.programLines.addLine(castOperation);
        }
        // Exiting a pre/post increment/decrement operation
        boolean exitedIncDecOp = this.exitingIncDecOperation(node);
        if (exitedIncDecOp) {
            INCDECNode incdecNode = (INCDECNode)node;
            ICLines incdecOperation = this.genIncDecOperation(incdecNode);
            this.programLines.addLines(incdecOperation.getICLinesList());
        }
        // Exiting array length operation
        boolean exitedArrayLengthOperation = this.exitingArrayLengthOperation(node);
        if (exitedArrayLengthOperation) {
            UNARYOPNode unaryopNode = (UNARYOPNode)node;
            ICLines icLines = this.genArrayLengthOperation(unaryopNode);
            this.programLines.addLines(icLines.getICLinesList());
        }
        // Exiting an array write operation
        boolean exitedArrayWriteOperation = this.exitingArrayWriteOperation(node);
        if (exitedArrayWriteOperation) {
            ASNNode asnNode = (ASNNode)node;
            AIDXNode aidxNode = (AIDXNode)asnNode.getChild(0);
            ICLine arrayWrite = this.genArrayWriteOperation(asnNode, aidxNode);
            this.programLines.addLine(arrayWrite);
            this.writeAIDXNode = null;
        }
        // Exiting an array read operation
        boolean exitedArrayReadOperation = this.exitingArrayReadOperation(node);
        if (exitedArrayReadOperation) {
            AIDXNode aidxNode = (AIDXNode)node;
            ICLine arrayRead = this.genArrayReadOperation(aidxNode);
            this.programLines.addLine(arrayRead);
        }
        // Exiting a unary operation
        boolean exitedUnaryOperation = this.exitingUnaryOperation(node);
        if (exitedUnaryOperation) {
            UNARYOPNode unaryopNode = (UNARYOPNode)node;
            ICLine unaryOprt = this.genUnaryOperation(unaryopNode);
            this.programLines.addLine(unaryOprt);
        }
        // Exiting a binary operation
        boolean exitedBinaryOperation = this.exitingBinaryOperation(node);
        if (exitedBinaryOperation) {
            BINOPNode binopNode = (BINOPNode)node;
            ICLine binOprt = this.genBinaryOperation(binopNode);
            this.programLines.addLine(binOprt);
        }
        // Exiting a while loop
        boolean exitedWhileLoop = this.exitingWhileLoop(node);
        if (exitedWhileLoop) {
            WHILENode whileNode = (WHILENode)node;
            ICLines whileLoopEnd = this.genWhileLoopEnd(whileNode);
            // Get the label at the end of the loop
            ICLine loopEndLine = whileLoopEnd.getICLine(1);
            String loopEndLabel = loopEndLine.getLabel();
            // Set the gotoLabel at the start of the loop to jump to the end of the loop
            ICReltJump reltJump = (ICReltJump)this.whileLoopStart.getICLine(1);
            reltJump.setGotoLabel(loopEndLabel);
            // Set the gotoLabel for any break statements inside the while loop
            this.updateBreakOps(loopEndLabel);
            this.breakOps.clear();
            // Add the whileLoopEnd lines to this.programLines
            this.programLines.addLines(whileLoopEnd.getICLinesList());
        }
        // Exited this.whileBINOPNode
        boolean exitedWhileBINOPNode = this.exitingWhileBINOPNode(node);
        if (exitedWhileBINOPNode) {
            this.inWhileBINOPNode = false;
            // Add this.whileLoopStart to this.programLines
            this.programLines.addLines(this.whileLoopStart.getICLinesList());
        }
        // Exiting a return operation
        boolean exitedReturnOperation = this.exitingReturnOperation(node);
        if (exitedReturnOperation) {
            RETURNNode returnNode = (RETURNNode)node;
            ICLine returnOperation = this.genReturnOperation(returnNode);
            this.programLines.addLine(returnOperation);
        }
        // Exiting a break operation
        boolean exitedBreakOperation = this.exitingBreakOperation(node);
        if (exitedBreakOperation) {
            ICJump jump = this.genBreakOperation();
            this.programLines.addLine(jump);
            // Save the break operation so its gotoLabel can be updated later
            this.breakOps.add(jump);
        }
        // Exiting an actual parameter node
        boolean exitedActualParameterNode = this.exitingActualParameterNode(node);
        if (exitedActualParameterNode) {
            ICLine parameterPass = this.genParameterPassInstruction(node);
            this.programLines.addLine(parameterPass);
        }
        // Exiting a function call node
        boolean exitedFunctionCall = this.exitingFunctionCall(node);
        if (exitedFunctionCall) {
            FNCALLNode fncallNode = (FNCALLNode)node;
            ICLine functionCall = this.genFunctionCall(fncallNode);
            this.programLines.addLine(functionCall);
        }
        // Exiting an if condition node (this.ifCondition)
        boolean exitedIfCondition = this.exitingIfCondition(node);
        if (exitedIfCondition) {
            ICLine ifCondition = this.genIfCondition(node);
            this.programLines.addLine(ifCondition);
        }
        // Exiting an ifPart node (this.ifPart)
        boolean exitedIfPart = this.exitingIfPart(node);
        if (exitedIfPart) {
            ICLines ifPartEnd = this.genIfPartEnd();
            this.programLines.addLines(ifPartEnd.getICLinesList());
        }
        // Exiting an elsePart node (this.elsePart)
        boolean exitedElsePart = this.exitingElsePart(node);
        if (exitedElsePart) {
            ICLine elsePartEnd = this.genElsePartEnd();
            this.programLines.addLine(elsePartEnd);
        }
        // Exiting an if-else statement node
        boolean exitedIfStatement = this.exitingIfStatement(node);
        if (exitedIfStatement) {
            // Reset the variables for tracking if-else constructs
            this.ifManager.exitedIfStatement();
        }
    }

    //================= ENTERING AND EXITING PROGRAM CONSTRUCTS =================

    /**
     * Determine whether a function node is being entered and set the inFunction variable
     * accordingly. Returns true if a function node is being entered.
     * @param node
     * @return
     */
    private boolean enteringFunction(Node node) {
        if (node instanceof FNDEFNode) {
            this.inFunction = true;
            return true;
        }
        return false;
    }

    /**
     * Determine whether a function call node is being entered.
     * @param node
     * @return
     */
    private boolean enteringFunctionCall(Node node) {
        if (node instanceof FNCALLNode) {
            return true;
        }
        return  false;
    }

    /**
     * Determine if an array write operation is being entered if so. This condition will
     * be true if the provided node is an ASNNode and its first child is an AIDXNode.
     * @param node
     * @return
     */
    private boolean enteringArrayWriteOperation(Node node) {
        // Check if the node is an ASNNode
        if (node instanceof ASNNode) {
            // Check if the first child of the ASNNode is an AIDXNode
            Node firstChild = node.getChild(0);
            if (firstChild instanceof AIDXNode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether a while loop is being entered.
     * @param node
     * @return
     */
    private boolean enteringWhileLoop(Node node) {
        if (node instanceof WHILENode) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether this.whileBINOPNode is being entered.
     * @param node
     * @return
     */
    private boolean enteringWhileBINOPNode(Node node) {
        if (node == this.whileBINOPNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether an if statement is being entered.
     * @param node
     * @return
     */
    private boolean enteringIfStatement(Node node) {
        if (node instanceof IFNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether a function node is being exited and set the inFunction variable
     * accordingly. Returns true if a function node is being exited.
     * @param node
     * @return
     */
    private boolean exitingFunction(Node node) {
        if (node instanceof FNDEFNode) {
            this.inFunction = false;
            return true;
        }
        return false;
    }

    /**
     * Determine whether a function call node is being exited.
     * @param node
     * @return
     */
    private boolean exitingFunctionCall(Node node) {
        if (node instanceof FNCALLNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether a while loop is being exited.
     * @param node
     * @return
     */
    private boolean exitingWhileLoop(Node node) {
        if (node instanceof WHILENode) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether this.whileBINOPNode is being exited.
     * @param node
     * @return
     */
    private boolean exitingWhileBINOPNode(Node node) {
        if (node == this.whileBINOPNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether an assignment node that is not part of an array write operation is
     * being exited.
     * @param node
     * @return
     */
    private boolean exitingAssignment(Node node) {
        if (node instanceof ASNNode) {
            // Check if this ASNNode corresponds to an array write operation
            ASNNode asnNode = (ASNNode)node;
            Node firstChild = asnNode.getChild(0);
            if (!(firstChild == this.writeAIDXNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if an id node in an expression in being exited.
     * @param node
     * @return
     */
    private boolean exitingId(Node node) {
        if (node instanceof IDNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine if a literal node is being exited.
     * @param node
     * @return
     */
    private boolean exitingLiteral(Node node) {
        if (node instanceof LITNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine if a return operation node is being exited.
     * @param node
     * @return
     */
    private boolean exitingReturnOperation(Node node) {
        if (node instanceof RETURNNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine if a break operation node is being exited.
     * @param node
     * @return
     */
    private boolean exitingBreakOperation(Node node) {
        if (node instanceof BREAKNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine if an actual parameter node of a function call node is being exited.
     * @param node
     * @return
     */
    private boolean exitingActualParameterNode(Node node) {
        // Check if the node is in this.savedActualParameterNodes
        for (Node actualParameter : this.savedActualParameterNodes) {
            if (node == actualParameter) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if a cast operation node is being exited.
     * @param node
     * @return
     */
    private boolean exitingCastOperation(Node node) {
        if (node instanceof CASTNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine if a post/pre increment/decrement operation is being exited.
     * @param node
     * @return
     */
    private boolean exitingIncDecOperation(Node node) {
        if (node instanceof INCDECNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine if an array length operation is being exited. This condition will be true
     * if the provided node is an UNARYOPNode and its operator is '#'.
     * @param node
     * @return
     */
    private boolean exitingArrayLengthOperation(Node node) {
        if (node instanceof UNARYOPNode) {
            UNARYOPNode unaryopNode = (UNARYOPNode)node;
            String oprt = unaryopNode.getOperator();
            if (oprt.compareTo("#") == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if an array write operation is being exited. This condition will be true if
     * the provided node is an ASNNode and its first child is an AIDXNode.
     * @param node
     * @return
     */
    private boolean exitingArrayWriteOperation(Node node) {
        if (node instanceof ASNNode) {
            Node firstChild = node.getChild(0);
            if (firstChild instanceof AIDXNode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if an array read operation is being exited. This condition will be true if the
     * provided node is AIDXNode and is not the same node object as this.writeAIDXNode.
     * @param node
     * @return
     */
    private boolean exitingArrayReadOperation(Node node) {
        if (node instanceof AIDXNode) {
            AIDXNode aidxNode = (AIDXNode)node;
            if (aidxNode != this.writeAIDXNode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the condition node for an if-else statement is being exited.
     * @param node
     * @return
     */
    private boolean exitingIfCondition(Node node) {
        // if (node == this.ifCondition)
        if (node == this.ifManager.currentIfCondition()) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the ifPart of an if-else statement is being exited.
     * @param node
     * @return
     */
    private boolean exitingIfPart(Node node) {
        // if (node == this.ifPart)
        if (node == this.ifManager.currentIfPart()) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the elsePart is of an if-else statement is being exited.
     * @param node
     * @return
     */
    private boolean exitingElsePart(Node node) {
        // if (node == this.elsePart)
        if (node == this.ifManager.currentElsePart()) {
            return true;
        }
        return false;
    }

    /**
     * Determine if an if-else statement is being exited.
     * @param node
     * @return
     */
    private boolean exitingIfStatement(Node node) {
        if (node instanceof IFNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether a variable declaration node for a global variable is being exited.
     * @param node
     * @return
     */
    private boolean exitingGlobalVariableDeclaration(Node node) {
        if (node instanceof VDECLNode && !this.inFunction) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether a parameter declaration node is being exited.
     * @param node
     * @return
     */
    private boolean exitingParameterDeclaration(Node node) {
        if (node instanceof PDECLNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether a unary operation that does not correspond to an array length
     * operation is being exited.
     * @param node
     * @return
     */
    private boolean exitingUnaryOperation(Node node) {
        if (node instanceof UNARYOPNode) {
            UNARYOPNode unaryopNode = (UNARYOPNode)node;
            // Check if the operator is for an array length operation
            String oprt = unaryopNode.getOperator();
            if (oprt.compareTo("#") != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether a binary operation is being exited.
     * @param node
     * @return
     */
    private boolean exitingBinaryOperation(Node node) {
        if (node == this.ifManager.currentIfCondition() || node == this.whileBINOPNode) {
            return false;
        }
        if (node instanceof BINOPNode) {
            return true;
        }
        return false;
    }

    /**
     * Determine if a variable declaration node for a local variable is being exited.
     * @param node
     * @return
     */
    private boolean exitingLocalDeclaration(Node node) {
        if (node instanceof VDECLNode && this.inFunction) {
            return true;
        }
        return false;
    }

    //============= DIRECTIVES CREATION =============

    /**
     * Generates the .fnStart directive for the provided FNDEFNode.
     * @param fnNode
     * @return
     */
    private ICDirective genFnStartDirective(FNDEFNode fnNode) {
        String funcName = fnNode.getIdentifier();
        String label = this.names.genNewName(ICNames.globalName, ICNames.functionType, funcName, funcName);
        // The parameter for this .fnStart directive is unknown at this point
        ICDirective icFnStart = new ICDirective(ICDirective.fnStart, "???");
        icFnStart.setLabel(label);
        this.fnStartDirective = icFnStart;
        return icFnStart;
    }

    /**
     * Generates the .fnEnd directive for the provided FNDEFNode.
     * @param fnNode
     * @return
     */
    private ICDirective genFnEndDirective(FNDEFNode fnNode) {
        // The .fnEnd directive has no parameters
        ICDirective icFnEnd = new ICDirective(ICDirective.fnEnd, "");
        return icFnEnd;
    }

    /**
     * Generate a sequence of the .dw or .db directives for a global variable declaration.
     * @param vdeclNode
     * @return
     */
    private ICLines genGlobalVariableDirective(VDECLNode vdeclNode) {
        ICLines icLines = new ICLines();
        String variableName = vdeclNode.getIdentifier();
        String init = vdeclNode.getInitializer();
        int idType = this.determineIdTypeOFVDECLNode(vdeclNode);
        // The type is an int
        if (idType == 4) {
            int value = 0;
            try {
                value = Integer.parseInt(init);
            } catch (NumberFormatException ex) {
                value = 0;
            }
            String label = this.names.genNewName(ICNames.globalName, ICNames.integerType, variableName, variableName);
            String parameter = "" + value;
            ICDirective icDirective = new ICDirective(ICDirective.definedWord, parameter);
            icDirective.setLabel(label);
            icLines.addLine(icDirective);
            return icLines;
        }
        // The type is a char
        else if (idType == 1) {
            String label = this.names.genNewName(ICNames.globalName, ICNames.characterType, variableName, variableName);
            char character = init.charAt(1);
            String parameter = this.convertCharacterToASCII(character);
            ICDirective icDirective = new ICDirective(ICDirective.definedByte, parameter);
            icDirective.setLabel(label);
            icLines.addLine(icDirective);
            return icLines;
        }
        // The type is an int array
        else if (idType == 40) {
            LCIntArray intArray = (LCIntArray)this.symbolTable.getLCType(variableName);
            String size = intArray.getSize().getExprStr();
            String parameter = "0#" + size;
            ICDirective icDirective = new ICDirective(ICDirective.definedWord, parameter);
            String label = this.names.genNewName(ICNames.globalName, ICNames.arrayType, variableName, variableName);
            icDirective.setLabel(label);
            icLines.addLine(icDirective);
            return icLines;
        }
        // The type is a char array
        else if (idType == 10) {
            LCCharArray charArray = (LCCharArray)this.symbolTable.getLCType(variableName);
            String size = charArray.getSize().getExprStr();
            String parameter1 = size;
            String label = this.names.genNewName(ICNames.globalName, ICNames.arrayType, variableName, variableName);
            ICDirective icDrt1 = new ICDirective(ICDirective.definedWord, parameter1);
            icDrt1.setLabel(label);
            icLines.addLine(icDrt1);
            // The char array has an implicit value
            if (charArray.isValueExpr()) {
                String parameter2 = "0#" + size; // initialize to null
                ICDirective icDrt2 = new ICDirective(ICDirective.definedByte, parameter2);
                icLines.addLine(icDrt2);
                return icLines;
            }
            // The char array has an explicit value
            else {
                ArrayList<String> parameters2 = this.convertCharactersToASCII(charArray);
                parameters2.add("0"); // char arrays are null terminated
                if (parameters2.isEmpty()) {
                    String parameter2 = "0#" + size;
                    ICDirective icDrt2 = new ICDirective(ICDirective.definedByte, parameter2);
                    icLines.addLine(icDrt2);
                    return icLines;
                }
                ICLines defineDirectives = this.createSetOfDefineDirectives(true, parameters2);
                icLines.addLines(defineDirectives.getICLinesList());
                return icLines;
            }
        }
        return icLines;
    }

    //============= ICLINES CREATION =============

    /**
     * Generate an ICLine object for the provided variable declaration node. The generated line
     * is for a local variable declaration.
     * @param vdeclNode
     * @return
     */
    private ICLine genLocalDeclarationLine(VDECLNode vdeclNode) {
        String originalName = vdeclNode.getIdentifier();
        int idType = this.determineIdTypeOFVDECLNode(vdeclNode);
        String init = vdeclNode.getInitializer();
        // The type is an int
        if (idType == 4) {
            if (init.isEmpty()) {
                init = "0";
            }
            int dataWidth = 4;
            this.calculateAndSetMemoryOffsetForLocals(dataWidth);
            String memoryOffset = "" + this.memoryOffsetLocals;
            String localName = this.names.genNewName(ICNames.localName, ICNames.integerType, memoryOffset, originalName);
            ICCopy icCopy = new ICCopy(localName, init);
            this.savedLocalNames.add(localName);
            this.addDataWidth(dataWidth);
            return icCopy;
        }
        // The type is a char
        else if (idType == 1) {
            if (init.isEmpty()) {
                byte nullByte = 0;
                init = "" + (char)nullByte;
            } else {
                char character = init.charAt(1);
                init = this.convertCharacterToASCII(character);
            }
            int dataWidth = 1;
            this.calculateAndSetMemoryOffsetForLocals(dataWidth);
            String memoryOffset = "" + this.memoryOffsetLocals;
            String localName = this.names.genNewName(ICNames.localName, ICNames.characterType, memoryOffset, originalName);
            ICCopy icCopy = new ICCopy(localName, init);
            this.savedLocalNames.add(localName);
            this.addDataWidth(dataWidth);
            return icCopy;
        }
        // The type is an char array
        else if (idType == 10) {
            ArrayList<LCType> typesList = this.symbolTable.getLCTypesInPoppedScopes(originalName);
            if (typesList.size() == 1) {
                LCCharArray charArray = (LCCharArray)typesList.get(0);
                // Calculate memory offset
                String size = charArray.getSize().getExprStr();
                int sizeInt;
                try {
                    sizeInt = Integer.parseInt(size);
                } catch (NumberFormatException ex) {
                    sizeInt = 0;
                }
                int dataWidth = sizeInt + 4;
                this.calculateAndSetMemoryOffsetForLocals(dataWidth);
                // Create the ICLine for the local array declaration
                String memoryOffset = "" + this.memoryOffsetLocals;
                String localName = this.names.genNewName(ICNames.localName, ICNames.arrayType, memoryOffset, originalName);
                ArrayList<String> terms = new ArrayList<>();
                terms.add(localName);
                terms.add("setsize");
                terms.add(size);
                ICLine icLine = new ICLine(terms);
                // Return the icLine
                this.savedLocalNames.add(localName);
                this.addDataWidth(dataWidth);
                return icLine;
            }
        }
        // The type is a int array
        else if (idType == 40) {
            ArrayList<LCType> typesList = this.symbolTable.getLCTypesInPoppedScopes(originalName);
            if (typesList.size() == 1) {
                LCIntArray intArray = (LCIntArray)typesList.get(0);
                // Calculate memory offset
                String size = intArray.getSize().getExprStr();
                int sizeInt;
                try {
                    sizeInt = Integer.parseInt(size);
                } catch (NumberFormatException ex) {
                    sizeInt = 0;
                }
                int dataWidth = sizeInt * 4 + 4;
                this.calculateAndSetMemoryOffsetForLocals(dataWidth);
                // Create the ICLine for the local array declaration
                String memoryOffset = "" + this.memoryOffsetLocals;
                String localName = this.names.genNewName(ICNames.localName, ICNames.arrayType, memoryOffset, originalName);
                ArrayList<String> terms = new ArrayList<>();
                terms.add(localName);
                terms.add("setsize");
                terms.add(size);
                ICLine icLine = new ICLine(terms);
                // Return the icLine
                this.savedLocalNames.add(localName);
                this.addDataWidth(dataWidth);
                return icLine;
            }
        }
        return new ICLine(ICLine.EMPTY);
    }

    /**
     * Generate the parameter name for the provided PDECLNode.
     * @param pdeclNode
     */
    private void genParameterDeclarationName(PDECLNode pdeclNode) {
        String originalName = pdeclNode.getIdentifier();
        String type = pdeclNode.getIdType();
        // The parameter is an int
        if (type.compareTo("int") == 0) {
            String memoryOffset = "" + this.memoryOffsetParameters;
            String genName = this.names.genNewName(ICNames.parameterName, ICNames.integerType, memoryOffset, originalName);
            this.savedParameterNames.add(genName);
            this.memoryOffsetParameters = this.memoryOffsetParameters + 4;
        }
        // The parameter is a char
        else if (type.compareTo("char") == 0) {
            String memoryOffset = "" + this.memoryOffsetParameters;
            String genName = this.names.genNewName(ICNames.parameterName, ICNames.characterType, memoryOffset, originalName);
            this.savedParameterNames.add(genName);
            this.memoryOffsetParameters = this.memoryOffsetParameters + 1;
        }
        // The parameter is an int array (Not implemented)
        else if (type.compareTo("int[]") == 0) {

        }
        // The parameter is a char array (Not implemented)
        else if (type.compareTo("char[]") == 0) {

        }
    }

    /**
     * Generate the ICCopy object that corresponds to the provided ASNNode.
     * @param asnNode
     * @return
     */
    private ICLine genAssignmentCopyInstruction(ASNNode asnNode) {
        // Get the generated names for both operands
        Node firstChild = asnNode.getChild(0);
        Node secondChild = asnNode.getChild(1);
        String genName1 = this.savedExpressionNames.get(firstChild);
        String genName2 = this.savedExpressionNames.get(secondChild);
        // Create the ICCopy instruction
        ICCopy icLine = new ICCopy(genName1, genName2);
        return icLine;
    }

    /**
     * Get and save the generated name for the identifier of the IDNode for future use in an
     * expression.
     * @param idNode
     */
    private void getAndSaveGenNameForId(IDNode idNode) {
        // Get the generated name for the identifier
        String id = idNode.getIdentifier();
        String idGenName = this.names.getGeneratedName(id);
        int nodeType = idNode.getType();
        // If the nodeType is an array, get or create then save a temporary pointer for it.
        if (nodeType == Node.INT_AR || nodeType == Node.CHAR_AR) {
            String existingArrayPointer = this.names.getGeneratedName(idGenName);
            // The array does not have an existing pointer
            if (existingArrayPointer.compareTo(ICNames.noSuchName) == 0) {
                // Generate an ICAddressPointer object to give a pointer to the array
                char objectType = ICNames.integerType; // objectType of pointer in an integer
                String arrayPointer = this.names.genNewName(ICNames.temporaryName, objectType, "", idGenName);
                int instrType = ICAddressPointer.copyAddressOf;
                ICAddressPointer pointerInstr = new ICAddressPointer(instrType, arrayPointer, idGenName);
                this.programLines.addLine(pointerInstr);
                // Saved the generated array pointer with the IDNode
                this.savedExpressionNames.put(idNode, arrayPointer);
            }
            // The array has an existing pointer (Save the existing array pointer with the idNode)
            else {
                this.savedExpressionNames.put(idNode, existingArrayPointer);
            }
        }
        // Else, save the generated name of the identifier with the IDNode.
        else {
            this.savedExpressionNames.put(idNode, idGenName);
        }
    }

    /**
     * Generate and save the generated name for the literal of the LITNode for future use in
     * an expression. If the literal is a string literal, its directives are also generated.
     * @param litNode
     */
    private void genAndSaveGenNameForLit(LITNode litNode) {
        // If the literal a string literal, generate and save its directives.
        LCType literalLCType = litNode.getLiteral();
        if (literalLCType.isLCCharArray()) {
            LCCharArray lcCharArray = (LCCharArray)literalLCType;
            ICLines strLitDirectives = new ICLines();
            // Convert the characters of the string literal to ASCII
            ArrayList<String> asciiList = this.convertCharactersToASCII(lcCharArray);
            asciiList.add("0"); // string literals are null terminated
            String size = "" + asciiList.size();
            // Generate the first directive defining the size
            String strlitGenLabel = this.names.genNewLabel(ICNames.strlitLabel);
            ICDirective directive1 = new ICDirective(ICDirective.definedWord, size);
            directive1.setLabel(strlitGenLabel);
            strLitDirectives.addLine(directive1);
            // Generate the lists of ICDirectives for defining the characters
            ICLines directives2 = this.createSetOfDefineDirectives(true, asciiList);
            strLitDirectives.addLines(directives2.getICLinesList());
            // Save the generated directives to add the to the end of the file
            this.endOfFileDirectives.addLines(strLitDirectives.getICLinesList());
            // Save the LITNode and the generated label for the string literal
            this.savedExpressionNames.put(litNode, strlitGenLabel);
        }
        // The literal is a char or an int
        else {
            // Get the correct representation of the literal char or int
            String literalString;
            if (literalLCType.isLCInteger()) {
                LCInteger lcInteger = (LCInteger)literalLCType;
                literalString = lcInteger.getExprStr();
            } else {
                LCChar lcChar = (LCChar)literalLCType;
                if (!lcChar.isValueExpr()) {
                    char character = lcChar.getCharacter();
                    literalString = this.convertCharacterToASCII(character);
                } else {
                    literalString = "0"; // null
                }
            }
            // Save the literal char or int with the litNode
            this.savedExpressionNames.put(litNode, literalString);
        }
    }

    /**
     * Generate the ICLines object required for the provided pre/post increment/decrement
     * node.
     * @param incdecNode
     * @return
     */
    private ICLines genIncDecOperation(INCDECNode incdecNode) {
        ICLines icLines = new ICLines();
        Node child = incdecNode.getChild(0);
        // Create the operation type
        String oprt = this.determineICOperatorForINCDECNode(incdecNode);
        // Create the ICBinOprt object for the inc/dec operation
        String childGenName = this.savedExpressionNames.get(child);
        char objectType = ICNames.getObjectTypeOfGenName(childGenName);
        // Create the copy instruction
        String tempName = this.names.genNewName(ICNames.temporaryName, objectType, "", "");
        ICCopy icCopy = new ICCopy(tempName, childGenName);
        icLines.addLine(icCopy);
        // Create the binary operation
        String one = "1";
        ICBinOprt icBinOprt = new ICBinOprt(childGenName, oprt, tempName, one);
        icLines.addLine(icBinOprt);
        this.savedExpressionNames.put(incdecNode, childGenName);
        // Return icBinOprt
        return icLines;
    }

    /**
     * Generate the sequence of ICLines required for the provided array length operation.
     * @param unaryopNode
     * @return
     */
    private ICLines genArrayLengthOperation(UNARYOPNode unaryopNode) {
        ICLines icLines = new ICLines();
        IDNode childIDNode = (IDNode)unaryopNode.getChild(0);
        // Create the copyAddressOf instruction ( pointer = & Array )
        String arrayPointer = this.savedExpressionNames.get(childIDNode);
//        String arrayPointer = this.names.genNewName(ICNames.temporaryName, ICNames.integerType, "", "");
//        int instructionType = ICAddressPointer.copyAddressOf;
//        ICAddressPointer icLine1 = new ICAddressPointer(instructionType, arrayPointer, arrayGenName);
//        icLines.addLine(icLine1);
        // Create the array reading instruction ( temp = Array ldidx4 0 )
        char objectType = this.determineObjectTypeForNode(unaryopNode);
        String tempName = this.names.genNewName(ICNames.temporaryName, objectType, "", "");
        String index = "0"; // The length of the array is stored at index 0
        boolean read = ICArrayIndexing.read;
        boolean integer = ICArrayIndexing.integer;
        ICArrayIndexing icLine2 = new ICArrayIndexing(tempName, arrayPointer, read, integer, index);
        this.savedExpressionNames.put(unaryopNode, tempName);
        icLines.addLine(icLine2);
        return icLines;
    }

    /**
     * Generate the ICLine object required for the provided array write operation.
     * @param asnNode
     * @param aidxNode
     * @return
     */
    private ICLine genArrayWriteOperation(ASNNode asnNode, AIDXNode aidxNode) {
        // Array Write: Array = index stidx4 value
        boolean write = ICArrayIndexing.write;
        char objectType = this.determineObjectTypeForNode(asnNode);
        // Determine the type of value being written (int or char)
        boolean type;
        if (objectType == ICNames.integerType) {
            type = ICArrayIndexing.integer;
        } else {
            type = ICArrayIndexing.character;
        }
        // Get the array's genName
        IDNode arrayIDNode = (IDNode)aidxNode.getChild(0);
        String arrayGenName = this.savedExpressionNames.get(arrayIDNode);
        // Get the index's genName from the AIDXNode (second child)
        Node indexNode = aidxNode.getChild(1);
        String indexGenName = this.savedExpressionNames.get(indexNode);
        // Get the value's genName to write from the second child of ASNNode
        Node valueNode = asnNode.getChild(1);
        String valueGenName = this.savedExpressionNames.get(valueNode);
        // Create the array write operation
        ICArrayIndexing arrayWrite = new ICArrayIndexing(arrayGenName, indexGenName, write, type, valueGenName);
        return arrayWrite;
    }

    /**
     * Generate the ICLine object required for the provided array read operation.
     * @param aidxNode
     * @return
     */
    private ICLine genArrayReadOperation(AIDXNode aidxNode) {
        // Array Read: temp = Array ldidx4 index
        boolean read = ICArrayIndexing.read;
        char objectType = this.determineObjectTypeForNode(aidxNode);
        // Determine the value being read (int or char)
        boolean type;
        if (objectType == ICNames.integerType) {
            type = ICArrayIndexing.integer;
        } else {
            type = ICArrayIndexing.character;
        }
        // Get the array's genName
        IDNode arrayIDNode = (IDNode)aidxNode.getChild(0);
        String arrayGenName = this.savedExpressionNames.get(arrayIDNode);
        // Get the index's genName
        Node indexNode = aidxNode.getChild(1);
        String indexGenName = this.savedExpressionNames.get(indexNode);
        // Generate a tempName for the value being read
        String tempName = this.names.genNewName(ICNames.temporaryName, objectType, "", "");
        // Create the array read operation
        ICArrayIndexing arrayRead = new ICArrayIndexing(tempName, arrayGenName, read, type, indexGenName);
        this.savedExpressionNames.put(aidxNode, tempName);
        return arrayRead;
    }

    /**
     * Generate the ICLine object required for the provided cast operation node.
     * @param castNode
     * @return
     */
    private ICLine genCastOperation(CASTNode castNode) {
        Node child = castNode.getChild(0);
        char objectType = this.determineObjectTypeForNode(castNode); // Type to cast to for tempName
        // Determine the cast type for the ICCast object
        int castType;
        if (objectType == ICNames.integerType) {
            castType = ICCast.widen;
        } else {
            castType = ICCast.narrow;
        }
        // Create the cast operation
        String tempName = this.names.genNewName(ICNames.temporaryName, objectType, "", "");
        String childGenName = this.savedExpressionNames.get(child);
        ICCast icCast = new ICCast(tempName, castType, childGenName);
        this.savedExpressionNames.put(castNode, tempName);
        return icCast;
    }

    /**
     * Generate the ICLines object required for the provided unary operation node.
     * @param unaryopNode
     * @return
     */
    private ICLine genUnaryOperation(UNARYOPNode unaryopNode) {
        Node child = unaryopNode.getChild(0);
        String operator = unaryopNode.getOperator();
        String childGenName = this.savedExpressionNames.get(child);
        char objectType = this.determineObjectTypeForNode(child);
        String tempName = this.names.genNewName(ICNames.temporaryName, objectType, "", "");
        ICUniOprt icUniOprt = new ICUniOprt(tempName, operator, childGenName);
        this.savedExpressionNames.put(unaryopNode, tempName);
        return icUniOprt;
    }

    /**
     * Generate the ICLines object required for the provided binary operation node.
     * @param binopNode
     * @return
     */
    private ICLine genBinaryOperation(BINOPNode binopNode) {
        Node leftChild = binopNode.getChild(0);
        Node rightChild = binopNode.getChild(1);
        String leftChildGenName = this.savedExpressionNames.get(leftChild);
        String rightChildGenName = this.savedExpressionNames.get(rightChild);
        String operator = binopNode.getOperator();
        char objectType = this.determineObjectTypeFromBinaryOperation(leftChild, rightChild);
        String tempName = this.names.genNewName(ICNames.temporaryName, objectType, "", "");
        ICBinOprt icBinOprt = new ICBinOprt(tempName, operator, leftChildGenName, rightChildGenName);
        this.savedExpressionNames.put(binopNode, tempName);
        return icBinOprt;
    }

    /**
     * Generate the binary operation for the provided BINOPNode.
     * @param binopNode
     * @return
     */
    private ICBinOprt genBinOpForBINOPNode(BINOPNode binopNode) {
        Node leftChild = binopNode.getChild(0);
        Node rightChild = binopNode.getChild(1);
        String leftChildGenName = this.savedExpressionNames.get(leftChild);
        String rightChildGenName = this.savedExpressionNames.get(rightChild);
        String operator = binopNode.getOperator();
        String tempName = "temp"; // Meaningless tempName (Not used)
        ICBinOprt icBinOprt = new ICBinOprt(tempName, operator, leftChildGenName, rightChildGenName);
        return icBinOprt;
    }

    /**
     * Generate the two ICLine objects required for the start of a while loop.
     * @param whileNode
     * @return
     */
    private ICLines genWhileLoopStart(WHILENode whileNode) {
        ICLines icLines = new ICLines();
        // Generate the first (empty) line containing the loop's label
        String loopLabel = this.names.genNewLabel(ICNames.lineLabel);
        ICLine icLine1 = new ICLine();
        icLine1.setLabel(loopLabel);
        icLines.addLine(icLine1);
        // The operator, leftOperand, and rightOperand cannot be determined yet
        String operator = "";
        String leftOperand = "";
        String rightOperand = "";
        // Generate the second line for the first statement of the while loop
        String gotoLabel = ""; // The goto label is unknown at this point
        ICReltJump icLine2 = new ICReltJump(true, leftOperand, operator, rightOperand, gotoLabel);
        icLines.addLine(icLine2);
        // Return the ICLines
        return icLines;
    }

    /**
     * Generate the two ICLine objects required for the end of a while loop. This method also updates
     * the saved this.whileLoopStart ICLines
     * @return
     */
    private ICLines genWhileLoopEnd(WHILENode whileNode) {
        ICLines icLines = new ICLines();
        // Generate the unconditional jump instruction to jump to the start of the loop
        ICLine whileLoopStartLine1 = this.whileLoopStart.getICLine(0);
        String loopStartLabel = whileLoopStartLine1.getLabel();
        ICJump icLine1 = new ICJump(loopStartLabel);
        icLines.addLine(icLine1);
        // Generate the second (empty) line for the end of the loop
        String loopEndLabel = this.names.genNewLabel(ICNames.lineLabel);
        ICLine icLine2 = new ICLine();
        icLine2.setLabel(loopEndLabel);
        icLines.addLine(icLine2);
        // Get and update the relational jump statement from the whileLoopStart
        ICReltJump reltJump = (ICReltJump)this.whileLoopStart.getICLine(1);
        // Set the operator, leftOperand, and rightOperand of reltJump
        ICBinOprt conditionBinOp = this.genBinOpForBINOPNode(this.whileBINOPNode);
        String leftOperand = conditionBinOp.getAddress_2();
        String operator = conditionBinOp.getOperator();
        operator = this.convertReltOprtToItsOppositeOprt(operator);
        String rightOperand = conditionBinOp.getAddress_3();
        reltJump.setAddress_1(leftOperand);
        reltJump.setRelOp(operator);
        reltJump.setAddress_2(rightOperand);
        // Return the ICLines
        return icLines;
    }

    /**
     * Generate the relational or boolean jump instruction required for the provided
     * if-condition node.
     * @param conditionNode
     * @return
     */
    private ICLine genIfCondition(Node conditionNode) {
        // The condition is a BINOPNode (A relational operation)
        if (conditionNode instanceof BINOPNode) {
            // Get the leftOperand, rightOperand, and relational operator
            BINOPNode conditionBinOp = (BINOPNode)conditionNode;
            ICBinOprt icBinOprt = this.genBinOpForBINOPNode(conditionBinOp);
            String leftOperand = icBinOprt.getAddress_2();
            String rightOperand = icBinOprt.getAddress_3();
            String reltOp = icBinOprt.getOperator();
            reltOp = this.convertReltOprtToItsOppositeOprt(reltOp);
            // Create the relational jump instruction
            String gotoLabel = this.names.genNewLabel(ICNames.lineLabel);
            ICReltJump reltJump = new ICReltJump(true, leftOperand, reltOp, rightOperand, gotoLabel);
            // Save the gotoLabel for later use and return reltJump
            this.ifManager.setSkipIfPartLabel(gotoLabel);
            return reltJump;
        }
        // The condition is not a BINOPNode (A boolean value)
        else {
            String conditionGenName = this.savedExpressionNames.get(conditionNode);
            // Create the boolean jump instruction
            String gotoLabel = this.names.genNewLabel(ICNames.lineLabel);
            ICBoolJump boolJump = new ICBoolJump(true, conditionGenName, gotoLabel);
            // Save the gotoLabel for later use and return boolJump
            this.ifManager.setSkipIfPartLabel(gotoLabel);
            return boolJump;
        }
    }

    /**
     * Generate the appropriate lines at the end of an ifPart. This method will also generate
     * and save the label to skip past the else part if the else part is present.
     * @return
     */
    private ICLines genIfPartEnd() {
        ICLines icLines = new ICLines();
        // The elsePart is present (this.elsePart != null)
        if (this.ifManager.isElsePartPresent()) {
            // Generate a new line label to skip past the elsePart
            String skipElseLabel = this.names.genNewLabel(ICNames.lineLabel);
            this.ifManager.setSkipElsePartLabel(skipElseLabel);
            // Generate the unconditional goto instruction to jump past the elsePart
            ICJump jump = new ICJump(skipElseLabel);
            icLines.addLine(jump);
            // Generate an empty line between the ifPart and the elsePart
            ICLine emptyLine = new ICLine();
            emptyLine.setLabel(this.ifManager.getSkipIfPartLabel());
            icLines.addLine(emptyLine);
        }
        // The elsePart is not present
        else {
            ICLine skipPastIfPartLine = new ICLine();
            skipPastIfPartLine.setLabel(this.ifManager.getSkipIfPartLabel());
            icLines.addLine(skipPastIfPartLine);
        }
        // Return the icLines
        return icLines;
    }

    /**
     * Generate the empty line after the end of the elsePart.
     * @return
     */
    private ICLine genElsePartEnd() {
        ICLine afterElsePart = new ICLine();
        afterElsePart.setLabel(this.ifManager.getSkipElsePartLabel());
        return afterElsePart;
    }

    /**
     * Generate the ICLine object required for the provided return operation node.
     * @param returnNode
     * @return
     */
    private ICLine genReturnOperation(RETURNNode returnNode) {
        int childCount = returnNode.getChildCount();
        // The return operation does not return a value
        if (childCount == 0) {
            ICReturn icReturn = new ICReturn(ICReturn.returnVoid, "");
            return icReturn;
        }
        // The return operation returns a value
        else {
            Node child = returnNode.getChild(0);
            // Determine whether the value being returned is an int or a char
            char objectType = this.determineObjectTypeForNode(child);
            int returnType;
            if (objectType == ICNames.integerType) {
                returnType = ICReturn.returnInt;
            } else {
                returnType = ICReturn.returnChar;
            }
            // Create the ICReturn object
            String childGenName = this.savedExpressionNames.get(child);
            ICReturn icReturn = new ICReturn(returnType, childGenName);
            return icReturn;
        }
    }

    /**
     * Generate the ICJump object required for the break operation.
     * @return
     */
    private ICJump genBreakOperation() {
        // Generate and return the unconditional jump instruction
        String gotoLabel = ""; // The gotoLabel is unknown at this point
        ICJump jump = new ICJump(gotoLabel);
        return jump;
    }

    /**
     * Generate the ICLine object required for the parameter pass instruction.
     * @param atlParNode
     * @return
     */
    private ICLine genParameterPassInstruction(Node atlParNode) {
        // Determine the type of parameter being passed
        char objectType = this.determineObjectTypeForNode(atlParNode);
        int parPassType;
        if (objectType == ICNames.integerType) {
            parPassType = ICParameterPass.paramInt;
        } else {
            parPassType = ICParameterPass.paramChar;
        }
        // Create the ICParameterPass object
        String atlParGenName = this.savedExpressionNames.get(atlParNode);
        ICParameterPass parameterPass = new ICParameterPass(parPassType, atlParGenName);
        return parameterPass;
    }

    /**
     * Generate the ICLine object required for the function call operation.
     * @param fncallNode
     * @return
     */
    private ICLine genFunctionCall(FNCALLNode fncallNode) {
        // Determine if the function call returns a value or not
        String returnAddress;
        int funcReturnType = fncallNode.getType();
        if (funcReturnType == Node.VOID) {
            returnAddress = "";
        } else {
            char objectType = this.determineObjectTypeForNode(fncallNode);
            returnAddress = this.names.genNewName(ICNames.temporaryName, objectType, "", "");
        }
        // Generate the ICFuncCall object
        String funcID = fncallNode.getFuncID();
        String funcGenName = this.names.getGeneratedName(funcID);
        String atlParsCount = "" + fncallNode.getChildCount();
        ICFuncCall icFuncCall = new ICFuncCall(returnAddress, funcGenName, atlParsCount);
        this.savedExpressionNames.put(fncallNode, returnAddress);
        return icFuncCall;
    }

    //============= HELPER METHODS =============

    /**
     * Determine the type of the id of this provided VDECLNode.
     * @param vdeclNode
     * @return
     */
    private int determineIdTypeOFVDECLNode(VDECLNode vdeclNode) {
        String variableName = vdeclNode.getIdentifier();
        LCType lcType = this.symbolTable.getLCType(variableName);
        if (lcType == null) {
            ArrayList<LCType> typesList = this.symbolTable.getLCTypesInPoppedScopes(variableName);
            if (typesList.size() == 1) {
                lcType = typesList.get(0);
            } else {
                return -1;
            }
        }
        if (lcType.isLCInteger()) {
            return 4;
        } else if (lcType.isLCChar()) {
            return 1;
        } else if (lcType.isLCIntArray()) {
            return 40;
        } else if (lcType.isLCCharArray()) {
            return 10;
        } else {
            return -1;
        }
    }

    /**
     * Determine the ICNames.objectType for the provided Node.
     * @param node
     * @return
     */
    private char determineObjectTypeForNode(Node node) {
        int type = node.getType();
        if (type == Node.CHAR) {
            return ICNames.characterType;
        } else if (type == Node.INT) {
            return ICNames.integerType;
        } else if (type == Node.CHAR_AR || type == Node.INT_AR) {
            return ICNames.arrayType;
        }
        return '?';
    }

    /**
     * Determine the ICNames.objectType for the two provided Nodes. This method will determine
     * what object type a generated name should be based on the types of the two nodes.
     * @param node1
     * @param node2
     * @return
     */
    private char determineObjectTypeFromBinaryOperation(Node node1, Node node2) {
        int type1 = node1.getType();
        int type2 = node2.getType();
        if (type1 == Node.INT || type2 == Node.INT) {
            return ICNames.integerType;
        } else if (type1 == Node.CHAR && type2 == Node.CHAR) {
            return ICNames.characterType;
        } else {
            return '?';
        }
    }

    /**
     * Save the if statement child nodes.
     * @param ifNode
     */
    private void saveIfStatementChildNodes(IFNode ifNode) {
        int childCount = ifNode.getChildCount();
        // The elsePart is present
        if (childCount == 3) {
            Node ifCondition = ifNode.getChild(0);
            Node ifPart = ifNode.getChild(1);
            Node elsePart = ifNode.getChild(2);
            this.ifManager.enteredIfStatement(ifCondition, ifPart, elsePart);
        }
        // The elsePart is not present
        else {
            Node ifCondition = ifNode.getChild(0);
            Node ifPart = ifNode.getChild(1);
            this.ifManager.enteredIfStatement(ifCondition, ifPart);
        }
    }

    /**
     * Get the literal in a string for the provided LITNode.
     * @param litNode
     * @return
     */
    private String getLiteralStringOfLITNode(LITNode litNode) {
        LCType litLCType = litNode.getLiteral();
        if (litLCType.isLCChar()) {
            LCChar lcChar = (LCChar)litLCType;
            String str;
            if (!lcChar.isValueExpr()) {
                char character = lcChar.getCharacter();
                str = this.convertCharacterToASCII(character);
            } else {
                str = "0"; // null
            }
            return str;
        } else if (litLCType.isLCInteger()) {
            LCInteger lcInteger = (LCInteger)litLCType;
            String str = lcInteger.getExprStr();
            return str;
        } else if (litLCType.isLCCharArray()) {
            LCCharArray lcCharArray = (LCCharArray)litLCType;
            String str = lcCharArray.getExprStr();
            return str;
        }
        return "?";
    }

    /**
     * Converts the characters in the provided LCCharArray into their respective ASCII numbers.
     * The provided LCCharArray must have its characters set.
     * @param charArray
     * @return
     */
    private ArrayList<String> convertCharactersToASCII(LCCharArray charArray) {
        ArrayList<LCChar> lcCharacters = charArray.getCharacters();
        ArrayList<String> asciiChars = new ArrayList<>();
        for (LCChar lcChar : lcCharacters) {
            char character = lcChar.getCharacter();
            byte charByte = (byte)character;
            if (charByte != 34) {
                String asciiStr = "" + charByte;
                asciiChars.add(asciiStr);
            }
        }
        return asciiChars;
    }

    /**
     * Convert a single character to its ASCII number.
     * @param character
     * @return
     */
    private String convertCharacterToASCII(char character) {
        byte charByte = (byte)character;
        return "" + charByte;
    }

    /**
     * Determine the IC operation required for the translation of and pre/post
     * increment/decrement operation node.
     * @param incdecNode
     * @return
     */
    private String determineICOperatorForINCDECNode(INCDECNode incdecNode) {
        String originalOprt = incdecNode.getOprt();
        if (originalOprt.compareTo(INCDECNode.PREINC) == 0) {
            return "+";
        } else if (originalOprt.compareTo(INCDECNode.POSTINC) == 0) {
            return "+";
        } else if (originalOprt.compareTo(INCDECNode.PREDEC) == 0) {
            return "-";
        } else if (originalOprt.compareTo(INCDECNode.POSTDEC) == 0) {
            return "-";
        }
        return "?";
    }

    /**
     * Convert the given relational operator to its opposite version.
     * @param operator
     * @return
     */
    private String convertReltOprtToItsOppositeOprt(String operator) {
        if (operator.compareTo("<") == 0) {
            return ">=";
        } else if (operator.compareTo("<=") == 0) {
            return ">";
        } else if (operator.compareTo(">") == 0) {
            return "<=";
        } else if (operator.compareTo(">=") == 0) {
            return "<";
        } else if (operator.compareTo("==") == 0) {
            return "!=";
        } else if (operator.compareTo("!=") == 0) {
            return "==";
        } else {
            return operator; // Invalid operator provided
        }
    }

    /**
     * Update the gotoLabel of every saved break operation to the provided gotoLabel.
     * @param gotoLabel
     */
    private void updateBreakOps(String gotoLabel) {
        for (ICJump jump : this.breakOps) {
            jump.setGotoLabel(gotoLabel);
        }
    }

    /**
     * Generate a list of IC directives that define bytes (defineByte = true) or words (defineByte = false)
     * of the provided list of parameters (strList).
     * @param defineByte
     * @param strList
     * @return
     */
    private ICLines createSetOfDefineDirectives(boolean defineByte, ArrayList<String> strList) {
        // Determine if bytes or words are being defined
        int defineType = ICDirective.definedWord;
        if (defineByte) {
            defineType = ICDirective.definedByte;
        }
        ICLines directivesList = new ICLines();
        // Iterate over the strList and create the other directives
        int parsCount = 0;
        ArrayList<ArrayList<String>> listsOfParameters = new ArrayList<>();
        ArrayList<String> parsList = new ArrayList<>();
        // Create lists of parameters
        int listSize = strList.size();
        int lastIndex = listSize - 1;
        for (int i = 0; i < listSize; i++) {
            String asciiStr = strList.get(i);
            if (parsCount <= 0) {
                parsList = new ArrayList<>();
            }
            parsList.add(asciiStr);
            parsCount++;
            if (parsCount >= 8) {
                parsCount = 0;
                listsOfParameters.add(parsList);
            }
            if (i == lastIndex && parsCount != 0) {
                listsOfParameters.add(parsList);
            }
        }
        // Create a directive for each list of the parameters
        for (ArrayList<String> pars : listsOfParameters) {
            ICDirective directive = new ICDirective(defineType, pars);
            directivesList.addLine(directive);
        }
        return directivesList;
    }

    /**
     * Reset the memory offset number for parameters.
     */
    private void resetMemoryOffsetForParameters() {
        this.memoryOffsetParameters = 0;
    }

    /**
     * Reset the memory offset number for locals.
     */
    private void resetMemoryOffsetForLocals() {
        this.memoryOffsetLocals = 0;
    }

    /**
     * Calculates and sets this.memoryOffsetLocals to the appropriate multiple of the provided
     * dataWidth.
     * @param dataWidth
     */
    private void calculateAndSetMemoryOffsetForLocals(int dataWidth) {
        int spaceOverMultiple = this.memoryOffsetLocals % dataWidth;
        if (spaceOverMultiple != 0) {
            int toNextMultiple = dataWidth - spaceOverMultiple;
            this.memoryOffsetLocals = this.memoryOffsetLocals + toNextMultiple;
        }
    }

    /**
     * Add the dataWidth for the memory offset for locals.
     * @param dataWidth
     */
    private void addDataWidth(int dataWidth) {
        this.memoryOffsetLocals = this.memoryOffsetLocals + dataWidth;
    }

    /**
     * Generates a list of IC predefined functions to be used in this program generation.
     */
    private void genPredefinedFunctions() {
        ICPredefinedFunctions.generateFunctions(this.names);
    }
}
