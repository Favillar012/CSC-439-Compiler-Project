package edu.uncg.csc439;

import edu.uncg.csc439.LCElements.*;
import edu.uncg.csc439.antlr4.LittleCBaseListener;
import edu.uncg.csc439.antlr4.LittleCParser;
import edu.uncg.csc439.syntaxtree.*;
import edu.uncg.csc439.syntaxtree.Nodes.*;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will contain all of your listener methods, which are called
 * every time a rule is entered or exited during parsing. This is what ties
 * your LittleC.g4 grammar file to your syntax tree creation methods.
 *
 * You can decide what listeners you need, and how to structure your code,
 * but you must provide the methods shown below.
 *
 * @modifiedBy Fernando Villarreal
 * @date 10/30/2020
 */
public class LCListener extends LittleCBaseListener {

    //================= VARIABLES =================

    private ParseTreeProperty<LCType> values;
    private ParseTreeProperty<Node> nodes;
    private LittleCParser parser;
    private SymbolTable symbolTable;
    private LCSyntaxTree syntaxTree;

    private String idToBeDclr;
    private String idTypeToBeDclr;
    private String funcIdToBeDclr;
    private boolean assignInProgress;
    private boolean vrblDclrinProgress;
    private boolean funcDclrinProgress;
    private ArrayList<LCFunction.FormalParameter> frmlPars;
    private ArrayList<LCType> postOprtValues;

    //================= CONSTRUCTORS =================

    public LCListener() {
        this.values = new ParseTreeProperty<>();
        this.nodes = new ParseTreeProperty<>();
        this.symbolTable = new SymbolTable();
        this.syntaxTree = new LCSyntaxTree();
        this.vrblDclrinProgress = false;
        this.funcDclrinProgress = false;
        this.assignInProgress = false;
        this.frmlPars = new ArrayList<>();
        this.postOprtValues = new ArrayList<>();
    }

    /**
     * This constructor is expected by ParserTest, and so needs to be defined.
     * @param parser the parser class
     */
    public LCListener(LittleCParser parser) {
        this.parser = parser;
        this.values = new ParseTreeProperty<>();
        this.nodes = new ParseTreeProperty<>();
        this.symbolTable = new SymbolTable();
        this.syntaxTree = new LCSyntaxTree();
        this.vrblDclrinProgress = false;
        this.funcDclrinProgress = false;
        this.assignInProgress = false;
        this.frmlPars = new ArrayList<>();
        this.postOprtValues = new ArrayList<>();
    }

    //================= LISTENER METHODS =================

    //====== Program ======

    @Override
    public void enterProgram(LittleCParser.ProgramContext ctx) {
        // Get the list of predefined Little C functions
        ArrayList<LCFunction> predefinedFunctions = PredefinedLCFunctions.getFunctions();
        // Add the functions to the symbol table
        for (LCFunction func : predefinedFunctions) {
            this.symbolTable.addNewSymbol(func);
        }
    }

    @Override
    public void exitProgram(LittleCParser.ProgramContext ctx) {
        int childCount = ctx.getChildCount();
        LCProgram program = new LCProgram();
        for (int i = 0; i < childCount; i++) {
            LCType val = this.values.get(ctx.dclr_stmt(i));
            program.addLCType(val);
        }
        this.values.put(ctx, program);
        // Create the node
        SEQNode node = new SEQNode();
        for (int i = 0; i < childCount; i++) {
            Node child = this.nodes.get(ctx.dclr_stmt(i));
            node.addChild(child);
        }
        this.nodes.put(ctx, node);
        // Set the root node and the symbol table of the syntax tree
        this.syntaxTree.setRoot(node);
        this.syntaxTree.setSymbolTable(this.symbolTable);
    }

    //====== Statements======

    //=== Scopes and Blocks ===

    @Override
    public void exitStmts(LittleCParser.StmtsContext ctx) {
        List<LittleCParser.StmtContext> stmtList = ctx.stmt();
        LCBlock block = new LCBlock();
        int listSize = stmtList.size();
        for (int i = 0; i < listSize; i++) {
            LCType lcType = this.values.get(stmtList.get(i));
            block.addLCType(lcType);
        }
        this.values.put(ctx, block);
        // Create the node
        SEQNode node = new SEQNode();
        for (int i = 0; i < listSize; i++) {
            Node child = this.nodes.get(stmtList.get(i));
            node.addChild(child);
        }
        this.nodes.put(ctx, node);
    }

    @Override
    public void enterNewScope(LittleCParser.NewScopeContext ctx) {
        this.symbolTable.pushNewScope();
        // Check to see if there is a function declaration in progress
        if (this.funcDclrinProgress) {
            // Check if any formal parameters need to be added to the symbol table
            if (!this.frmlPars.isEmpty()) {
                // Add the formal parameters to the symbol table
                for (LCFunction.FormalParameter frmPar : this.frmlPars) {
                    LCType frmParLCType = frmPar.getParAsLCType();
                    this.symbolTable.addNewSymbol(frmParLCType);
                }
            }
            // Reset the saved formal parameters
            this.resetFrmPars();
        }
    }

    @Override
    public void exitNewScope(LittleCParser.NewScopeContext ctx) {
        // The block is not empty
        if (ctx.stmts() != null) {
            LCBlock block = (LCBlock)this.values.get(ctx.stmts());
            this.values.put(ctx, block);
            // Create the node
            SEQNode node = (SEQNode)this.nodes.get(ctx.stmts());
            this.nodes.put(ctx, node);
        }
        // The block is empty
        else {
            this.values.put(ctx, new LCBlock());
            // Create the node
            SEQNode node = new SEQNode();
            this.nodes.put(ctx, node);
        }
        // Pop the scope from the symbol table
        this.symbolTable.popScope();
    }

    //=== Other Statements ===

    @Override
    public void exitStmt(LittleCParser.StmtContext ctx) {
        if (ctx.vrbl_dclr() != null) {
            LCType val = this.values.get(ctx.vrbl_dclr());
            this.values.put(ctx, val);
            // Create the node
            Node node = this.nodes.get(ctx.vrbl_dclr());
            this.nodes.put(ctx, node);
        } else if (ctx.array_dclr() != null) {
            LCType val = this.values.get(ctx.array_dclr());
            this.values.put(ctx, val);
            // Create the node
            Node node = this.nodes.get(ctx.array_dclr());
            this.nodes.put(ctx, node);
        } else if (ctx.asgn_stmt() != null) {
            LCType val = this.values.get(ctx.asgn_stmt());
            this.values.put(ctx, val);
            // Create the node
            Node node = this.nodes.get(ctx.asgn_stmt());
            this.nodes.put(ctx, node);
        } else if (ctx.array_insert() != null) {
            LCType val = this.values.get(ctx.array_insert());
            this.values.put(ctx, val);
            // Pass the node
            Node node = this.nodes.get(ctx.array_insert());
            this.nodes.put(ctx, node);
        } else if (ctx.func_call() != null) {
            LCType val = this.values.get(ctx.func_call());
            this.values.put(ctx, val);
            // Create the node
            Node node = this.nodes.get(ctx.func_call());
            this.nodes.put(ctx, node);
        } else if (ctx.expr() != null) {
            LCType val = this.values.get(ctx.expr());
            this.values.put(ctx, val);
            // Create the node
            Node node = this.nodes.get(ctx.expr());
            this.nodes.put(ctx, node);
        } else if (ctx.if_stmt() != null) {
            LCType val = this.values.get(ctx.if_stmt());
            this.values.put(ctx, val);
            // Create the node
            Node node = this.nodes.get(ctx.if_stmt());
            this.nodes.put(ctx, node);
        } else if (ctx.for_loop() != null) {
            LCType val = this.values.get(ctx.for_loop());
            this.values.put(ctx, val);
            // Create the node
            Node node = this.nodes.get(ctx.for_loop());
            this.nodes.put(ctx, node);
        } else if (ctx.while_loop() != null) {
            LCType val = this.values.get(ctx.while_loop());
            this.values.put(ctx, val);
            // Create the node
            Node node = this.nodes.get(ctx.while_loop());
            this.nodes.put(ctx, node);
        } else if (ctx.rtrn_stmt() != null) {
            LCType val = this.values.get(ctx.rtrn_stmt());
            // Create the node
            Node node = this.nodes.get(ctx.rtrn_stmt());
            this.nodes.put(ctx, node);
            this.values.put(ctx, val);
        } else if (ctx.brk_stmt() != null) {
            LCType val = this.values.get(ctx.brk_stmt());
            this.values.put(ctx, val);
            // Create the node
            Node node = this.nodes.get(ctx.brk_stmt());
            this.nodes.put(ctx, node);
        }
    }

    @Override
    public void exitIf_stmt(LittleCParser.If_stmtContext ctx) {
        LCType expr = this.values.get(ctx.expr());
        LCBlock ifBlock = (LCBlock)this.values.get(ctx.if_else_block(0));
        // The if statement has an else clause
        if (ctx.if_else_block(1) != null) {
            LCBlock elseBlock = (LCBlock)this.values.get(ctx.if_else_block(1));
            LCIfStmt val = new LCIfStmt(expr, ifBlock, elseBlock);
            this.values.put(ctx, val);
            // Create the node
            Node condition = this.nodes.get(ctx.expr());
            SEQNode thenPart = (SEQNode)this.nodes.get(ctx.if_else_block(0));
            SEQNode elsePart = (SEQNode)this.nodes.get(ctx.if_else_block(1));
            IFNode node = new IFNode(condition, thenPart, elsePart);
            this.nodes.put(ctx, node);
        }
        // The if statement does not have an else clause
        else {
            LCIfStmt val = new LCIfStmt(expr, ifBlock);
            this.values.put(ctx, val);
            // Create the node
            Node condition = this.nodes.get(ctx.expr());
            SEQNode thenPart = (SEQNode)this.nodes.get(ctx.if_else_block(0));
            IFNode node = new IFNode(condition, thenPart);
            this.nodes.put(ctx, node);
        }
    }

    @Override
    public void enterNewIfScope1(LittleCParser.NewIfScope1Context ctx) {
        // Push a new scope for a lone statement
        this.symbolTable.pushNewScope();
        // The block rule (enterNewScope) already pushes a new scope
    }

    @Override
    public void exitNewIfScope1(LittleCParser.NewIfScope1Context ctx) {
        // The if_else block is a lone statement
        LCBlock val = new LCBlock();
        LCType lcType = this.values.get(ctx.stmt());
        val.addLCType(lcType);
        this.values.put(ctx, val);
        // Create the node
        Node stmtNode = this.nodes.get(ctx.stmt());
        SEQNode node = new SEQNode();
        node.addChild(stmtNode);
        this.nodes.put(ctx, node);
        // Pop the scope for a lone statement
        this.symbolTable.popScope();
    }

    @Override
    public void exitNewIfScope2(LittleCParser.NewIfScope2Context ctx) {
        // The if_else_block is a block
        LCBlock val = (LCBlock)this.values.get(ctx.block());
        this.values.put(ctx, val);
        // Create the node
        SEQNode node = (SEQNode)this.nodes.get(ctx.block());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitWhile_loop(LittleCParser.While_loopContext ctx) {
        LCInteger limit = (LCInteger)this.values.get(ctx.expr());
        LCBlock block1 = (LCBlock)this.values.get(ctx.block());
        LCWhileLoop val = new LCWhileLoop(limit, block1);
        this.values.put(ctx, val);
        // Create the node
        Node condition = this.nodes.get(ctx.expr());
        SEQNode block2 = (SEQNode)this.nodes.get(ctx.block());
        WHILENode node = new WHILENode(condition, block2);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitFor_loop(LittleCParser.For_loopContext ctx) {
        // Get the first two parts of the for loop statement and the block
        LCInteger initialValue = (LCInteger)this.values.get(ctx.asgn_stmt(0));
        LCInteger limit = (LCInteger)this.values.get(ctx.expr(0));
        LCBlock block = (LCBlock)this.values.get(ctx.for_loop_block());
        // Nodes for the SEQNode
        SEQNode node = new SEQNode();
        ASNNode asnNode = (ASNNode)this.nodes.get(ctx.asgn_stmt(0));
        Node condition = this.nodes.get(ctx.expr(0));
        SEQNode blockNode = (SEQNode)this.nodes.get(ctx.for_loop_block());
        node.addChild(asnNode);
        // The third part is an expr
        if (ctx.expr(1) != null) {
            LCType update = this.values.get(ctx.expr(1));
            LCForLoop val = new LCForLoop(initialValue, limit, update, block);
            this.values.put(ctx, val);
            // Create the WHILENode and add it to the SEQNode
            Node updateNode = this.nodes.get(ctx.expr(1));
            blockNode.addChild(updateNode); // Place the updateNode at the end of the blockNode
            WHILENode whileNode = new WHILENode(condition, blockNode);
            node.addChild(whileNode);
            this.nodes.put(ctx, node);
        }
        // The third part is an asgn_stmt
        else if (ctx.asgn_stmt(1) != null) {
            LCType update = this.values.get(ctx.asgn_stmt(1));
            LCForLoop val = new LCForLoop(initialValue, limit, update, block);
            this.values.put(ctx, val);
            // Create the WHILENode and add it to the SEQNode
            Node updateNode = this.nodes.get(ctx.asgn_stmt(1));
            blockNode.addChild(updateNode); // Place the updateNode at the end of the blockNode
            WHILENode whileNode = new WHILENode(condition, blockNode);
            node.addChild(whileNode);
            this.nodes.put(ctx, node);
        }
    }

    @Override
    public void exitForLoopBlock1(LittleCParser.ForLoopBlock1Context ctx) {
        LCType stmt = this.values.get(ctx.stmt());
        LCBlock val = new LCBlock();
        val.addLCType(stmt);
        this.values.put(ctx, val);
        // Pass the node in an SEQNode
        Node stmtNode = this.nodes.get(ctx.stmt());
        SEQNode node = new SEQNode();
        node.addChild(stmtNode);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitForLoopBlock2(LittleCParser.ForLoopBlock2Context ctx) {
        LCBlock val = (LCBlock)this.values.get(ctx.block());
        this.values.put(ctx, val);
        // Pass the node in an SEQNode
        Node blockNode = this.nodes.get(ctx.block());
        SEQNode node = new SEQNode();
        node.addChild(blockNode);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitReturn(LittleCParser.ReturnContext ctx) {
        LCReturn val = new LCReturn();
        this.values.put(ctx, val);
        // Create the node
        RETURNNode node = new RETURNNode();
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitReturnID(LittleCParser.ReturnIDContext ctx) {
        String id = ctx.ID().getText();
        // Check if the id has been declared
        boolean idDeclared = this.symbolTable.isIdDeclared(id);
        if (idDeclared) {
            LCType lcType = this.symbolTable.getLCType(id);
            // The identifier points to an integer
            if (lcType.isLCInteger()) {
                int intVal = ((LCInteger)lcType).getValue();
                LCInteger lcInteger = new LCInteger(id, intVal);
                this.values.put(ctx, new LCReturn(lcInteger));
            }
            // Create the node
            IDNode idNode = new IDNode(Node.INT, id);
            RETURNNode node = new RETURNNode(idNode);
            this.nodes.put(ctx, node);
        }
        // The id has not been declared (Error)
        else {
            try {
                throw new ParsingException(ParsingException.idNotDeclared, id);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitReturnLit(LittleCParser.ReturnLitContext ctx) {
        // Literals are assumed to be integers for now
        String literal = ctx.literal().getText();
        int intVal = Integer.parseInt(literal);
        LCInteger lcInteger = new LCInteger(intVal);
        LCReturn val = new LCReturn(lcInteger);
        this.values.put(ctx, val);
        // Create the node
        LITNode litNode = (LITNode)this.nodes.get(ctx.literal());
        RETURNNode node = new RETURNNode(litNode);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitReturnExpr(LittleCParser.ReturnExprContext ctx) {
        LCType lcType = this.values.get(ctx.expr());
        // The expression evaluates to an integer
        if (lcType.isLCInteger()) {
            int intVal = ((LCInteger)lcType).getValue();
            LCInteger lcInteger = new LCInteger(intVal);
            this.values.put(ctx, new LCReturn(lcInteger));
            // Create the node
            Node exprNode = this.nodes.get(ctx.expr());
            RETURNNode node = new RETURNNode(exprNode);
            this.nodes.put(ctx, node);
        }
    }

    @Override
    public void exitBreak(LittleCParser.BreakContext ctx) {
        LCBreak val = new LCBreak();
        this.values.put(ctx, val);
        // Create the node
        BREAKNode node = new BREAKNode();
        this.nodes.put(ctx, node);
    }

    //====== Declarations ======

    //=== Variables Declarations ===

    @Override
    public void exitDclr_stmt(LittleCParser.Dclr_stmtContext ctx) {
        if (ctx.vrbl_dclr() != null) {
            LCType val = this.values.get(ctx.vrbl_dclr());
            this.values.put(ctx, val);
            // Pass the node
            Node node = this.nodes.get(ctx.vrbl_dclr());
            this.nodes.put(ctx, node);
        }
        if (ctx.array_dclr() != null) {
            LCType val = this.values.get(ctx.array_dclr());
            this.values.put(ctx, val);
            // Pass the node
            Node node = this.nodes.get(ctx.array_dclr());
            this.nodes.put(ctx, node);
        }
        if (ctx.func_dclr() != null) {
            LCType val = this.values.get(ctx.func_dclr());
            this.values.put(ctx, val);
            // Pass the node
            Node node = this.nodes.get(ctx.func_dclr());
            this.nodes.put(ctx, node);
        }
    }

    @Override
    public void enterVrblDclr(LittleCParser.VrblDclrContext ctx) {
        this.vrblDclrinProgress = true;
        this.idToBeDclr = ctx.ID().getText();
        this.idTypeToBeDclr = ctx.vrbl_type().getText();
    }

    @Override
    public void exitVrblDclr(LittleCParser.VrblDclrContext ctx) {
        String id = ctx.ID().getText();
        LCVrblType vrblType = (LCVrblType)this.values.get(ctx.vrbl_type());
        // The variable type is an integer
        if (vrblType.isIntType()) {
            LCInteger val = new LCInteger(id);
            this.values.put(ctx, val);
            // Add the new symbol to the symbol table
            this.symbolTable.addNewSymbol(val);
            // Create the node
            String idType = "int";
            VDECLNode node = new VDECLNode(idType, id);
            this.nodes.put(ctx, node);
        }
        // The variable type is a character
        else if (vrblType.isCharType()) {
            LCChar val = new LCChar(id);
            this.values.put(ctx, val);
            // Add the new symbol to the symbol table
            this.symbolTable.addNewSymbol(val);
            // Create the node
            String idType = "char";
            VDECLNode node = new VDECLNode(idType, id);
            this.nodes.put(ctx, node);
        } else {
            try {
                throw new ParsingException(ParsingException.unsupportedType, vrblType.getVrblType());
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        this.vrblDclrinProgress = false;
    }

    @Override
    public void enterVrblDclrAndAsgn(LittleCParser.VrblDclrAndAsgnContext ctx) {
        this.vrblDclrinProgress = true;
        String id = ctx.asgn_stmt().getChild(0).getText();
        if (id != null) {
            this.idToBeDclr = id;
        }
        this.idTypeToBeDclr = ctx.vrbl_type().getText();
    }

    @Override
    public void exitVrblDclrAndAsgn(LittleCParser.VrblDclrAndAsgnContext ctx) {
        LCType asgnStmtType = this.values.get(ctx.asgn_stmt());
        LCVrblType vrblType = (LCVrblType)this.values.get(ctx.vrbl_type());
        LCType valCopy = new LCType(); // debugging element
        // Both the vrblType and the asgnStmtType correspond to integers.
        if (vrblType.isIntType() && asgnStmtType.isLCInteger()) {
            LCInteger asgnStmtInt = (LCInteger)asgnStmtType;
            if (!asgnStmtInt.isValueExpr()) {
                LCInteger val = new LCInteger(asgnStmtInt.getIdentifier(), asgnStmtInt.getValue());
                this.values.put(ctx, val);
                valCopy = val;
                // Add the new symbol to the symbol table
                this.symbolTable.addNewSymbol(val);
                // Create the node
                String idType = "int";
                String id = val.getIdentifier();
                String init = "" + val.getValue();
                VDECLNode node = new VDECLNode(idType, id, init);
                this.nodes.put(ctx, node);
            } else {
                LCInteger val = new LCInteger(asgnStmtInt.getIdentifier(), asgnStmtInt.getExprValue());
                this.values.put(ctx, val);
                valCopy = val;
                // Add the new symbol to the symbol table
                this.symbolTable.addNewSymbol(val);
                // Create the node
                String idType = "int";
                String id = val.getIdentifier();
                String init = "" + val.getExprStr();
                VDECLNode node = new VDECLNode(idType, id, init);
                this.nodes.put(ctx, node);
            }
        }
        // Both the vrblType and the asgnStmtType correspond to characters.
        else if (vrblType.isCharType() && asgnStmtType.isLCChar()) {
            LCChar asgnStmtChar = (LCChar)asgnStmtType;
            if (!asgnStmtChar.isValueExpr()) {
                LCChar val = new LCChar(asgnStmtChar.getIdentifier(), asgnStmtChar.getCharacter());
                this.values.put(ctx, val);
                valCopy = val;
                // Add the new symbol to the symbol table
                this.symbolTable.addNewSymbol(val);
                // Create the node
                String idType = "char";
                String id = val.getIdentifier();
                String init = "'" + val.getCharacter() + "'";
                VDECLNode node = new VDECLNode(idType, id, init);
                this.nodes.put(ctx, node);
            } else {
                LCChar val = new LCChar(asgnStmtChar.getIdentifier(), asgnStmtChar.getExprValue());
                this.values.put(ctx, val);
                valCopy = val;
                // Add the new symbol to the symbol table
                this.symbolTable.addNewSymbol(val);
                // Create the node
                String idType = "char";
                String id = val.getIdentifier();
                String init = "'" + val.getExprStr() + "'";
                VDECLNode node = new VDECLNode(idType, id, init);
                this.nodes.put(ctx, node);
            }
        }
        // vrblType corresponds to a char and asgnStmtType correspond to an int.
        else if (vrblType.isCharType() && asgnStmtType.isLCInteger()) {
            LCInteger asgnStmtInt = (LCInteger)asgnStmtType;
            if (!asgnStmtInt.isValueExpr()) {
                String valueStr = "" + asgnStmtInt.getValue();
                char valueChar = valueStr.charAt(0);
                LCChar val = new LCChar(asgnStmtInt.getIdentifier(), valueChar);
                this.values.put(ctx, val);
                valCopy = val;
                // Add the new symbol to the symbol table
                this.symbolTable.addNewSymbol(val);
                // Create the node
                String idType = "char";
                String id = val.getIdentifier();
                String init = "" + val.getCharacter();
                VDECLNode node = new VDECLNode(idType, id, init);
                this.nodes.put(ctx, node);
            } else {
                LCChar val = new LCChar(asgnStmtInt.getIdentifier(), asgnStmtInt.getExprValue());
                this.values.put(ctx, val);
                valCopy = val;
                // Add the new symbol to the symbol table
                this.symbolTable.addNewSymbol(val);
                // Create the node
                String idType = "char";
                String id = val.getIdentifier();
                String init = "" + val.getExprStr();
                VDECLNode node = new VDECLNode(idType, id, init);
                this.nodes.put(ctx, node);
            }
        }
        // vrblType corresponds to an int and asgnStmtType correspond to a char.
        else if (vrblType.isIntType() && asgnStmtType.isLCChar()) {
            LCChar asgnStmtChar = (LCChar)asgnStmtType;
            if (!asgnStmtChar.isValueExpr()) {
                int charValue = asgnStmtChar.getValue();
                LCInteger val = new LCInteger(asgnStmtChar.getIdentifier(), charValue);
                this.values.put(ctx, val);
                valCopy = val;
                // Add the new symbol to the symbol table
                this.symbolTable.addNewSymbol(val);
                // Create the node
                String idType = "int";
                String id = val.getIdentifier();
                String init = "" + asgnStmtChar.getCharacter();
                VDECLNode node = new VDECLNode(idType, id, init);
                this.nodes.put(ctx, node);
            } else {
                LCInteger val = new LCInteger(asgnStmtChar.getIdentifier(), asgnStmtChar.getExprValue());
                this.values.put(ctx, val);
                valCopy = val;
                // Add the new symbol to the symbol table
                this.symbolTable.addNewSymbol(val);
                // Create the node
                String idType = "int";
                String id = val.getIdentifier();
                String init = "" + asgnStmtChar.getExprStr();
                VDECLNode node = new VDECLNode(idType, id, init);
                this.nodes.put(ctx, node);
            }
        }
        // The types do not match (Error)
        else {
            try {
                throw new ParsingException(ParsingException.typeMismatch);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        this.vrblDclrinProgress = false;
    }

    @Override
    public void exitVrbl_type(LittleCParser.Vrbl_typeContext ctx) {
        // The variable type is an int
        if (ctx.INT() != null) {
            String intStr = "int";
            LCVrblType val = new LCVrblType(intStr);
            this.values.put(ctx, val);
        }
        // The variable type is a char
        else if (ctx.CHAR() != null) {
            String charStr = "char";
            LCVrblType val = new LCVrblType(charStr);
            this.values.put(ctx, val);
        }
    }

    @Override
    public void exitArray_type(LittleCParser.Array_typeContext ctx) {
        // The array type is an int array
        if (ctx.INT() != null) {
            String intArStr = "int[]";
            LCArrayType val = new LCArrayType(intArStr);
            this.values.put(ctx, val);
        }
        // The array type is a char array
        else if (ctx.CHAR() != null) {
            String charArStr = "char[]";
            LCArrayType val = new LCArrayType(charArStr);
            this.values.put(ctx, val);
        }
    }

    //=== Array Declarations ===


    @Override
    public void enterArrayDclr(LittleCParser.ArrayDclrContext ctx) {
        this.idToBeDclr = ctx.ID().getText();
        this.idTypeToBeDclr = ctx.vrbl_type().getText() + "[]";
    }

    @Override
    public void enterArrayDclrAndAsgn1(LittleCParser.ArrayDclrAndAsgn1Context ctx) {
        this.idToBeDclr = ctx.ID().getText();
        this.idTypeToBeDclr = ctx.vrbl_type().getText() + "[]";
    }

    @Override
    public void enterArrayDclrAndAsgn2(LittleCParser.ArrayDclrAndAsgn2Context ctx) {
        this.idToBeDclr = ctx.ID().getText();
        this.idTypeToBeDclr = ctx.vrbl_type().getText() + "[]";
    }

    @Override
    public void exitArrayDclr(LittleCParser.ArrayDclrContext ctx) {
        LCVrblType vrblType = (LCVrblType)this.values.get(ctx.vrbl_type());
        String typeStr = vrblType.getVrblType();
        LCExpr expr = new LCExpr(ctx.expr().getText());
        String id = ctx.ID().getText();
        // Create the correct type of array
        if (typeStr.compareTo("int") == 0) {
            LCIntArray val = new LCIntArray(id, expr);
            this.values.put(ctx, val);
            this.symbolTable.addNewSymbol(val);
            // Create the node
            // Get the expr node with this.nodes.get(ctx.expr())
            VDECLNode node = new VDECLNode("int", id, val.getSize());
            this.nodes.put(ctx, node);
        } else if (typeStr.compareTo("char") == 0) {
            LCCharArray val = new LCCharArray(id, expr);
            this.values.put(ctx, val);
            this.symbolTable.addNewSymbol(val);
            // Create the node
            VDECLNode node = new VDECLNode("char", id, val.getSize());
            this.nodes.put(ctx, node);
        }
    }

    @Override
    public void exitArrayDclrAndAsgn1(LittleCParser.ArrayDclrAndAsgn1Context ctx) {
        LCVrblType vrblType = (LCVrblType)this.values.get(ctx.vrbl_type());
        String typeStr = vrblType.getVrblType();
        String id = ctx.ID().getText();
        LCType exprType = this.values.get(ctx.expr().get(1));
        // Check if exprType is an LCFuncCall and convert to an int array or a char array
        if (exprType.isLCFuncCall()) {
            LCFuncCall exprFuncCall = (LCFuncCall)exprType;
            String returnType = exprFuncCall.getFuncType();
            LCExpr size = new LCExpr("size_unknown"); // size of arrays returned is unknown
            if (returnType.compareTo("int[]") == 0) {
                exprType = new LCIntArray("", exprFuncCall.getExpr(), size);
            } else if (returnType.compareTo("char[]") == 0) {
                exprType = new LCCharArray("", exprFuncCall.getExpr(), size);
            } else {
                try {
                    throw new ParsingException(ParsingException.typeMismatch);
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
            }
        }
        // Both typeStr and exprType correspond to int arrays
        if (typeStr.compareTo("int") == 0 && exprType.isLCIntArray()) {
            LCIntArray exprArray = (LCIntArray)exprType;
            if (exprArray.hasIdentifier()) {
                String exprArrayID = exprArray.getIdentifier();
                LCExpr size = exprArray.getSize();
                LCIntArray val = new LCIntArray(id, new LCExpr(exprArrayID), size);
                this.symbolTable.addNewSymbol(val);
                this.values.put(ctx, val);
                // Create the node
                String init = exprArrayID;
                VDECLNode node = new VDECLNode("int", id, init, val.getSize());
                this.nodes.put(ctx, node);
            } else {
                LCIntArray val = new LCIntArray(id, exprArray);
                this.symbolTable.addNewSymbol(val);
                this.values.put(ctx, val);
                // Create the node
                String init = exprArray.getExprStr();
                VDECLNode node = new VDECLNode("int", id, init, val.getSize());
                this.nodes.put(ctx, node);
            }
        }
        // Both typeStr and exprType correspond to char arrays
        else if (typeStr.compareTo("char") == 0 && exprType.isLCCharArray()) {
            LCCharArray exprArray = (LCCharArray)exprType;
            if (exprArray.hasIdentifier()) {
                String exprArrayID = exprArray.getIdentifier();
                LCExpr size = exprArray.getSize();
                LCCharArray val = new LCCharArray(id, new LCExpr(exprArrayID), size);
                this.symbolTable.addNewSymbol(val);
                this.values.put(ctx, val);
                // Create the node
                String init = exprArrayID;
                VDECLNode node = new VDECLNode("char", id, init, val.getSize());
                this.nodes.put(ctx, node);
            } else {
                LCCharArray val = new LCCharArray(id, exprArray);
                this.symbolTable.addNewSymbol(val);
                this.values.put(ctx, val);
                // Create the node
                String init = exprArray.getExprStr();
                VDECLNode node = new VDECLNode("char", id, init, val.getSize());
                this.nodes.put(ctx, node);
            }
        }
        // The types of the arrays do not match (Error)
        else {
            try {
                throw new ParsingException(ParsingException.typeMismatch);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitArrayDclrAndAsgn2(LittleCParser.ArrayDclrAndAsgn2Context ctx) {
        LCVrblType vrblType = (LCVrblType)this.values.get(ctx.vrbl_type());
        String typeStr = vrblType.getVrblType();
        String id = ctx.ID().getText();
        String stringlit = ctx.STRINGLIT().getText();
        LCExpr expr = new LCExpr(ctx.expr().getText());
        // Check the type of the array to make sure it is a char array
        if (typeStr.compareTo("char") == 0) {
            LCCharArray val = new LCCharArray(id, stringlit, expr);
            this.values.put(ctx, val);
            this.symbolTable.addNewSymbol(val);
            // Create the node
            String init = val.getAsString();
            VDECLNode node = new VDECLNode("char", id, init, val.getSize());
            this.nodes.put(ctx, node);
        }
        // The type of the array is not char (Error)
        else {
            try {
                throw new ParsingException(ParsingException.typeMismatch);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    //=== Function Declarations, Definitions, and Calls ===

    @Override
    public void enterFuncDclr(LittleCParser.FuncDclrContext ctx) {
        this.funcDclrinProgress = true;
        this.funcIdToBeDclr = ctx.ID().getText();
        // Check if the function has been declared and/or defined before. Throw an error if so.
        String id = ctx.ID().getText();
        if (this.symbolTable.isIdDeclared(id)) {
            try {
                throw new ParsingException(ParsingException.idAlreadyDeclared, id);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void enterFuncDclrAndDefnt(LittleCParser.FuncDclrAndDefntContext ctx) {
        this.funcDclrinProgress = true;
        this.funcIdToBeDclr = ctx.ID().getText();
        // Check if the function has been previously declared
        String id = ctx.ID().getText();
        if (this.symbolTable.isIdDeclared(id)) {
            // Check if the function has been previously defined. Throw an error if so.
            LCFunction function = (LCFunction)this.symbolTable.getLCType(id);
            if (function.isDefined()) {
                try {
                    throw new ParsingException(ParsingException.functionAlreadyDefined, id);
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void exitFuncDclr(LittleCParser.FuncDclrContext ctx) {
        String funcType = ((LCFuncType)this.values.get(ctx.func_type())).getfuncType();
        String id = ctx.ID().getText();
        // The function declaration has formal parameters
        if (ctx.frm_parameters() != null) {
            LCFunction parameters = (LCFunction)this.values.get(ctx.frm_parameters());
            LCFunction val = new LCFunction(funcType, id, parameters.getFormalParameters());
            this.values.put(ctx, val);
            this.symbolTable.addNewSymbol(val);
            // Create the node
            FNDEFNode node = new FNDEFNode(id, val.getSignatureString());
            SEQNode parsNode = (SEQNode)this.nodes.get(ctx.frm_parameters());
            node.setParameters(parsNode);
            this.nodes.put(ctx, node);
        }
        // The function declaration does not have formal parameters
        else {
            LCFunction val = new LCFunction(funcType, id);
            this.values.put(ctx, val);
            this.symbolTable.addNewSymbol(val);
            // Create the node
            FNDEFNode node = new FNDEFNode(id, val.getSignatureString());
            this.nodes.put(ctx, node);
        }
        this.funcDclrinProgress = false;
        this.funcIdToBeDclr = null;
    }

    @Override
    public void exitFuncDclrAndDefnt(LittleCParser.FuncDclrAndDefntContext ctx) {
        String funcType = ((LCFuncType)this.values.get(ctx.func_type())).getfuncType();
        String id = ctx.ID().getText();
        LCBlock block = (LCBlock)this.values.get(ctx.func_block());
        // The function declaration has formal parameters
        if (ctx.frm_parameters() != null) {
            LCFunction parameters = (LCFunction)this.values.get(ctx.frm_parameters());
            LCFunction val = new LCFunction(funcType, id, parameters.getFormalParameters(), block);
            // Check if the function has been previously declared and if this definition is valid
            if (this.symbolTable.isIdDeclared(id)) {
                LCFunction declaredFunc = (LCFunction)this.symbolTable.getLCType(id);
                if (!this.areFunctionsIdentical(declaredFunc, val)) {
                    try {
                        throw new ParsingException(ParsingException.funcFrwdDefIsInvalid, id);
                    } catch (ParsingException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.values.put(ctx, val);
            this.symbolTable.addNewSymbol(val);
            // Create the node
            SEQNode child = (SEQNode)this.nodes.get(ctx.func_block());
            SEQNode parsNode = (SEQNode)this.nodes.get(ctx.frm_parameters());
            FNDEFNode node = new FNDEFNode(id, val.getSignatureString(), parsNode, child);
            this.nodes.put(ctx, node);
        }
        // The function declaration does not have formal parameters
        else {
            LCFunction val = new LCFunction(funcType, id, block);
            // Check if the function has been previously declared and if this definition is valid
            if (this.symbolTable.isIdDeclared(id)) {
                LCFunction declaredFunc = (LCFunction)this.symbolTable.getLCType(id);
                if (!this.areFunctionsIdentical(declaredFunc, val)) {
                    try {
                        throw new ParsingException(ParsingException.funcFrwdDefIsInvalid, id);
                    } catch (ParsingException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.values.put(ctx, val);
            this.symbolTable.addNewSymbol(val);
            // Create the node
            SEQNode child = (SEQNode)this.nodes.get(ctx.func_block());
            FNDEFNode node = new FNDEFNode(id, val.getSignatureString(), child);
            this.nodes.put(ctx, node);
        }
        this.funcDclrinProgress = false;
        this.funcIdToBeDclr = null;
    }

    @Override
    public void enterFuncBlock(LittleCParser.FuncBlockContext ctx) {
        // Declare the function without its definition before entering the function definition
        LittleCParser.FuncDclrAndDefntContext parentCtx = (LittleCParser.FuncDclrAndDefntContext)ctx.getParent();
        String funcType = ((LCFuncType)this.values.get(parentCtx.func_type())).getfuncType();
        String id = parentCtx.ID().getText();
        // The function declaration has formal parameters
        if (parentCtx.frm_parameters() != null) {
            LCFunction parameters = (LCFunction)this.values.get(parentCtx.frm_parameters());
            LCFunction val = new LCFunction(funcType, id, parameters.getFormalParameters());
            // Check if the function has been previously declared and if this definition is valid
            if (this.symbolTable.isIdDeclared(id)) {
                LCFunction declaredFunc = (LCFunction)this.symbolTable.getLCType(id);
                if (!this.areFunctionsIdentical(declaredFunc, val)) {
                    try {
                        throw new ParsingException(ParsingException.funcFrwdDefIsInvalid, id);
                    } catch (ParsingException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.values.put(parentCtx, val);
            this.symbolTable.addNewSymbol(val);
            // Create the node
            FNDEFNode node = new FNDEFNode(id, val.getSignatureString());
            SEQNode parsNode = (SEQNode)this.nodes.get(parentCtx.frm_parameters());
            node.setParameters(parsNode);
            this.nodes.put(parentCtx, node);
        }
        // The function declaration does not have formal parameters
        else {
            LCFunction val = new LCFunction(funcType, id);
            // Check if the function has been previously declared and if this definition is valid
            if (this.symbolTable.isIdDeclared(id)) {
                LCFunction declaredFunc = (LCFunction)this.symbolTable.getLCType(id);
                if (!this.areFunctionsIdentical(declaredFunc, val)) {
                    try {
                        throw new ParsingException(ParsingException.funcFrwdDefIsInvalid, id);
                    } catch (ParsingException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.values.put(parentCtx, val);
            this.symbolTable.addNewSymbol(val);
            // Create the node
            FNDEFNode node = new FNDEFNode(id, val.getSignatureString());
            this.nodes.put(parentCtx, node);
        }
    }

    @Override
    public void exitFuncBlock(LittleCParser.FuncBlockContext ctx) {
        LCType val = this.values.get(ctx.block());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.block());
        this.nodes.put(ctx, node);
    }

    @Override
    public void enterFuncCall(LittleCParser.FuncCallContext ctx) {
        String funcID = ctx.ID().getText();
        String lp = ctx.LP().getText();
        String atlPars = "";
        if (ctx.atl_parameters() != null) {
            atlPars = ctx.atl_parameters().getText();
        }
        String rp = ctx.RP().getText();
    }

    @Override
    public void exitFuncCall(LittleCParser.FuncCallContext ctx) {
        try {
            String id = ctx.ID().getText();
            // Check if the id of the function being called has been declared, throw an error if not.
            if (!this.symbolTable.isIdDeclared(id)) {
                throw new ParsingException(ParsingException.idNotDeclared, id);
            }
            LCFunction lcFunction = (LCFunction)this.symbolTable.getLCType(id);
//            /* Check if the function has been defined, throw an error if not.
//            // Error is not thrown if the function calls itself. */
//            if (!lcFunction.isDefined() && this.funcIdToBeDclr.compareTo(id) != 0) {
//                throw new ParsingException(ParsingException.undefinedFunc, id);
//            }
            // The function has been declared and defined, continue.
            // The function call has parameters
            String returnType = lcFunction.getType();
            if (ctx.atl_parameters() != null) {
                LCActualParameters parList = (LCActualParameters)this.values.get(ctx.atl_parameters());
                LCFuncCall val = new LCFuncCall(returnType, id, parList);
                // Check if the function call is valid
                if (lcFunction.isFuncCallValid(val)) {
                    this.values.put(ctx, val);
                }
                // The function call is not valid (Error)
                else {
                    throw new ParsingException(ParsingException.invalidFuncCall, val.toString());
                }
                // Create the node
                int nodeType = this.getNodeType(returnType);
                SEQNode parNodes = (SEQNode)this.nodes.get(ctx.atl_parameters());
                ArrayList<Node> children = parNodes.getChildren();
                FNCALLNode node = new FNCALLNode(nodeType, id, children);
                node = this.castAtlParNodes(lcFunction, node);
                this.nodes.put(ctx, node);
            }
            // The function call does not have parameters
            else {
                LCFuncCall val = new LCFuncCall(returnType, id);
                // Check if the function call is valid
                LCFunction function = (LCFunction)this.symbolTable.getLCType(id);
                if (function.isFuncCallValid(val)) {
                    this.values.put(ctx, val);
                }
                // The function call is not valid (Error)
                else {
                    throw new ParsingException(ParsingException.invalidFuncCall, val.toString());
                }
                // Create the node
                int nodeType = this.getNodeType(returnType);
                FNCALLNode node = new FNCALLNode(nodeType, id);
                this.nodes.put(ctx, node);
            }
        } catch (ParsingException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void exitFrm_parameters(LittleCParser.Frm_parametersContext ctx) {
        // Get the list of formal parameters
        List<LittleCParser.Frm_parameterContext> frmParsList = ctx.frm_parameter();
        ArrayList<LCFunction.FormalParameter> parList = new ArrayList<>();
        SEQNode node = new SEQNode();
        // Load each FormalParameter object from values and Create Node objects for each parameter
        int size = frmParsList.size();
        for (int i = 0; i < size; i++) {
            LCFunction.FormalParameter frmPar = (LCFunction.FormalParameter)this.values.get(ctx.frm_parameter(i));
            parList.add(frmPar);
            // Create and add the node to SEQNode
            String type = frmPar.getType();
            String id = frmPar.getIdentifier();
            node.addChild(new PDECLNode(type, id));
        }
        // Create an LCFunction object and add it to values, the type and id are unknown at this point.
        LCFunction val = new LCFunction("", "", parList);
        this.values.put(ctx, val);
        // Pass the SEQNode
        this.nodes.put(ctx, node);
        /* Save the formal parameters so they can be added to the symbol table
        // when and if a block is entered. */
        this.frmlPars = parList;
    }

    @Override
    public void exitFrmParIsVrblType(LittleCParser.FrmParIsVrblTypeContext ctx) {
        LCVrblType vrblType = (LCVrblType)this.values.get(ctx.vrbl_type());
        String type = vrblType.getVrblType();
        String id = ctx.ID().getText();
        LCFunction.FormalParameter val = new LCFunction.FormalParameter(type, id);
        this.values.put(ctx, val);
    }

    @Override
    public void exitFrmParIsArrayType(LittleCParser.FrmParIsArrayTypeContext ctx) {
        LCArrayType arrayType = (LCArrayType)this.values.get(ctx.array_type());
        String type = arrayType.getArrayType();
        String id = ctx.ID().getText();
        LCFunction.FormalParameter val = new LCFunction.FormalParameter(type, id);
        this.values.put(ctx, val);
    }

    @Override
    public void exitAtl_parameters(LittleCParser.Atl_parametersContext ctx) {
        ArrayList<LCType> parList = new ArrayList<>();
        SEQNode node = new SEQNode();
        int listSize = ctx.atl_parameter().size();
        // Add every LCType or Node to their respective lists
        for (int i = 0; i < listSize; i++) {
            LCType lcType = this.values.get(ctx.atl_parameter(i));
            Node child = this.nodes.get(ctx.atl_parameter(i));
            parList.add(lcType);
            node.addChild(child);
        }
        LCActualParameters val = new LCActualParameters(parList);
        this.values.put(ctx, val);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitAltParIsExpr(LittleCParser.AltParIsExprContext ctx) {
        LCType val = this.values.get(ctx.expr());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.expr());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitAtlParIsStringlit(LittleCParser.AtlParIsStringlitContext ctx) {
        String strlit = ctx.STRINGLIT().getText();
        int size = strlit.length();
        LCExpr sizeExpr = new LCExpr("" + size);
        LCCharArray val = new LCCharArray("", strlit, sizeExpr);
        this.values.put(ctx, val);
        // Create the node
        LITNode node = new LITNode(Node.CHAR_AR, val);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitFunc_type(LittleCParser.Func_typeContext ctx) {
        // The function type is int or char
        if (ctx.vrbl_type() != null) {
            LCVrblType vrblType = (LCVrblType)this.values.get(ctx.vrbl_type());
            String funcType = vrblType.getVrblType();
            LCFuncType val = new LCFuncType(funcType);
            this.values.put(ctx, val);
        }
        // The function type is int array or char array
        else if (ctx.array_type() != null) {
            LCArrayType arrayType = (LCArrayType)this.values.get(ctx.array_type());
            String funcType = arrayType.getArrayType();
            LCFuncType val = new LCFuncType(funcType);
            this.values.put(ctx, val);
        }
        // The function type is void
        else if (ctx.VOID() != null) {
            String funcType = ctx.VOID().getText();
            LCFuncType val = new LCFuncType(funcType);
            this.values.put(ctx, val);
        }
    }

    //====== Assignments ======

    @Override
    public void enterAssignment1(LittleCParser.Assignment1Context ctx) {
        // If no variable declaration is in progress, check to see if the id has been declared.
        if (this.vrblDclrinProgress == false) {
            String id = ctx.ID().getText();
            boolean idDeclared = this.symbolTable.isIdDeclared(id);
            // If the id has not been declared, throw an error.
            if (idDeclared == false) {
                try {
                    throw new ParsingException(ParsingException.idNotDeclared, id);
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
            }
        }
        this.assignInProgress = true;
    }

    @Override
    public void enterAssignment2(LittleCParser.Assignment2Context ctx) {
        // If no variable declaration is in progress, check to see if the id has been declared.
        if (this.vrblDclrinProgress == false) {
            String id = ctx.ID().getText();
            boolean idDeclared = this.symbolTable.isIdDeclared(id);
            // If the id has not been declared, throw an error.
            if (idDeclared == false) {
                try {
                    throw new ParsingException(ParsingException.idNotDeclared, id);
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
            }
        }
        this.assignInProgress = true;
    }

    @Override
    public void exitAssignment1(LittleCParser.Assignment1Context ctx) {
        // First Rule: ID ASGN expr
        String id = ctx.ID().getText();
        LCType exprType = this.values.get(ctx.expr());
        // exprType is an LCInteger
        if (exprType.isLCInteger()) {
            LCInteger exprInteger = (LCInteger)exprType;
            if (exprInteger.hasIdentifier()) {
                String exprID = exprInteger.getIdentifier();
                LCInteger val = new LCInteger(id, new LCExpr(exprID));
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else {
                LCInteger val = new LCInteger(id, exprInteger);
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            }
            // Create the node
            IDNode leftChild = new IDNode(Node.INT, id);
            leftChild = this.setTypeFromExistingID(leftChild);
            Node rightChild = this.nodes.get(ctx.expr());
            ASNNode node = new ASNNode(Node.INT);
            node.addChild(leftChild);
            node.addChild(rightChild);
            node = this.setNodeTypesOfASNNode(node);
            this.nodes.put(ctx, node);
        }
        // exprType is an LCChar
        else if (exprType.isLCChar()) {
            LCChar exprCharacter = (LCChar)exprType;
            if (exprCharacter.hasIdentifier()) {
                String exprID = exprCharacter.getIdentifier();
                LCChar val = new LCChar(id, new LCExpr(exprID));
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(id, exprCharacter);
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            }
            // Create the node
            IDNode leftChild = new IDNode(Node.CHAR, id);
            leftChild = this.setTypeFromExistingID(leftChild);
            Node rightChild = this.nodes.get(ctx.expr());
            ASNNode node = new ASNNode(Node.CHAR);
            node.addChild(leftChild);
            node.addChild(rightChild);
            node = this.setNodeTypesOfASNNode(node);
            this.nodes.put(ctx, node);
        }
        // exprType is an LCFuncCall
        else if (exprType.isLCFuncCall()) {
            LCFuncCall exprFuncCall = (LCFuncCall)exprType;
            String returnType = exprFuncCall.getFuncType();
            if (returnType.compareTo("int") == 0) {
                LCInteger val = new LCInteger(id, exprFuncCall.getExpr());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else if (returnType.compareTo("char") == 0) {
                LCChar val = new LCChar(id, exprFuncCall.getExpr());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else if (returnType.compareTo("char[]") == 0) {
                LCCharArray charArray = (LCCharArray)this.symbolTable.getLCType(id);
                LCExpr exprValue = charArray.getExprValue();
                LCExpr size = charArray.getSize();
                LCCharArray val = new LCCharArray(id, exprValue, size);
                this.assignNewValueAtSymbolTable(id ,val);
                this.values.put(ctx, val);
            } else if (returnType.compareTo("int[]") == 0) {
                LCIntArray intArray = (LCIntArray)this.symbolTable.getLCType(id);
                LCExpr exprValue = intArray.getExprValue();
                LCExpr size = intArray.getSize();
                LCIntArray val = new LCIntArray(id, exprValue, size);
                this.assignNewValueAtSymbolTable(id ,val);
                this.values.put(ctx, val);
            }
            // Create the node
            if (returnType.compareTo("int") == 0) {
                IDNode leftChild = new IDNode(Node.INT, id);
                leftChild = this.setTypeFromExistingID(leftChild);
                Node rightChild = this.nodes.get(ctx.expr());
                ASNNode node = new ASNNode(Node.INT);
                node.addChild(leftChild);
                node.addChild(rightChild);
                node = this.setNodeTypesOfASNNode(node);
                this.nodes.put(ctx, node);
            } else if (returnType.compareTo("char") == 0) {
                IDNode leftChild = new IDNode(Node.CHAR, id);
                leftChild = this.setTypeFromExistingID(leftChild);
                Node rightChild = this.nodes.get(ctx.expr());
                ASNNode node = new ASNNode(Node.CHAR);
                node.addChild(leftChild);
                node.addChild(rightChild);
                node = this.setNodeTypesOfASNNode(node);
                this.nodes.put(ctx, node);
            } else if (returnType.compareTo("char[]") == 0) {
                IDNode leftChild = new IDNode(Node.CHAR_AR, id);
                leftChild = this.setTypeFromExistingID(leftChild);
                Node rightChild = this.nodes.get(ctx.expr());
                ASNNode node = new ASNNode(Node.CHAR_AR);
                node.addChild(leftChild);
                node.addChild(rightChild);
                node = this.setNodeTypesOfASNNode(node);
                this.nodes.put(ctx, node);
            } else if (returnType.compareTo("int[]") == 0) {
                IDNode leftChild = new IDNode(Node.INT_AR, id);
                leftChild = this.setTypeFromExistingID(leftChild);
                Node rightChild = this.nodes.get(ctx.expr());
                ASNNode node = new ASNNode(Node.INT_AR);
                node.addChild(leftChild);
                node.addChild(rightChild);
                node = this.setNodeTypesOfASNNode(node);
                this.nodes.put(ctx, node);
            }
        }
        // exprType is an LCCharArray
        else if (exprType.isLCCharArray()) {
            LCCharArray charArray = (LCCharArray)exprType;
            if (charArray.hasIdentifier()) {
                String exprID = charArray.getIdentifier();
                LCCharArray val = new LCCharArray(id, new LCExpr(exprID), charArray.getSize());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else {
                LCCharArray val = new LCCharArray(id, charArray.getExprValue(), charArray.getSize());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            }
            // Create the node
            IDNode leftChild = new IDNode(Node.CHAR_AR, id);
            leftChild = this.setTypeFromExistingID(leftChild);
            Node rightChild = this.nodes.get(ctx.expr());
            ASNNode node = new ASNNode(Node.CHAR_AR);
            node.addChild(leftChild);
            node.addChild(rightChild);
            node = this.setNodeTypesOfASNNode(node);
            this.nodes.put(ctx, node);
        }
        // exprType is an LCIntArray
        else if (exprType.isLCIntArray()) {
            LCIntArray intArray = (LCIntArray) exprType;
            if (intArray.hasIdentifier()) {
                String exprID = intArray.getIdentifier();
                LCIntArray val = new LCIntArray(id, new LCExpr(exprID), intArray.getSize());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else {
                LCIntArray val = new LCIntArray(id, intArray.getExprValue(), intArray.getSize());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            }
            // Create the node
            IDNode leftChild = new IDNode(Node.INT_AR, id);
            leftChild = this.setTypeFromExistingID(leftChild);
            Node rightChild = this.nodes.get(ctx.expr());
            ASNNode node = new ASNNode(Node.INT_AR);
            node.addChild(leftChild);
            node.addChild(rightChild);
            node = this.setNodeTypesOfASNNode(node);
            this.nodes.put(ctx, node);
        }
        this.assignInProgress = false;
    }

    @Override
    public void exitAssignment2(LittleCParser.Assignment2Context ctx) {
        // Second Rule: ID ASGN asgn_stmt
        String id = ctx.ID().getText();
        LCType asgnStmt = this.values.get(ctx.asgn_stmt());
        // asgnStmt is an LCInteger
        if (asgnStmt.isLCInteger()) {
            LCInteger exprInteger = (LCInteger)asgnStmt;
            if (exprInteger.hasIdentifier()) {
                String exprID = exprInteger.getIdentifier();
                LCInteger val = new LCInteger(id, new LCExpr(exprID));
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else {
                LCInteger val = new LCInteger(id, exprInteger);
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            }
            // Create the node
            IDNode leftChild = new IDNode(Node.INT, id);
            leftChild = this.setTypeFromExistingID(leftChild);
            Node rightChild = this.nodes.get(ctx.asgn_stmt());
            ASNNode node = new ASNNode(Node.INT);
            node.addChild(leftChild);
            node.addChild(rightChild);
            node = this.setNodeTypesOfASNNode(node);
            this.nodes.put(ctx, node);
        }
        // asgnStmt is an LCChar
        else if (asgnStmt.isLCChar()) {
            LCChar exprCharacter = (LCChar)asgnStmt;
            if (exprCharacter.hasIdentifier()) {
                String exprID = exprCharacter.getIdentifier();
                LCChar val = new LCChar(id, new LCExpr(exprID));
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(id, exprCharacter);
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            }
            // Create the node
            IDNode leftChild = new IDNode(Node.CHAR, id);
            leftChild = this.setTypeFromExistingID(leftChild);
            Node rightChild = this.nodes.get(ctx.asgn_stmt());
            ASNNode node = new ASNNode(Node.CHAR);
            node.addChild(leftChild);
            node.addChild(rightChild);
            node = this.setNodeTypesOfASNNode(node);
            this.nodes.put(ctx, node);
        }
        // asgnStmt is an LCFuncCall
        else if (asgnStmt.isLCFuncCall()) {
            LCFuncCall exprFuncCall = (LCFuncCall)asgnStmt;
            String returnType = exprFuncCall.getFuncType();
            if (returnType.compareTo("int") == 0) {
                LCInteger val = new LCInteger(id, exprFuncCall.getExpr());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else if (returnType.compareTo("char") == 0) {
                LCChar val = new LCChar(id, exprFuncCall.getExpr());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else if (returnType.compareTo("char[]") == 0) {
                LCCharArray charArray = (LCCharArray)this.symbolTable.getLCType(id);
                LCExpr exprValue = charArray.getExprValue();
                LCExpr size = charArray.getSize();
                LCCharArray val = new LCCharArray(id, exprValue, size);
                this.assignNewValueAtSymbolTable(id ,val);
                this.values.put(ctx, val);
            } else if (returnType.compareTo("int[]") == 0) {
                LCIntArray intArray = (LCIntArray)this.symbolTable.getLCType(id);
                LCExpr exprValue = intArray.getExprValue();
                LCExpr size = intArray.getSize();
                LCIntArray val = new LCIntArray(id, exprValue, size);
                this.assignNewValueAtSymbolTable(id ,val);
                this.values.put(ctx, val);
            }
            // Create the node
            if (returnType.compareTo("int") == 0) {
                IDNode leftChild = new IDNode(Node.INT, id);
                leftChild = this.setTypeFromExistingID(leftChild);
                Node rightChild = this.nodes.get(ctx.asgn_stmt());
                ASNNode node = new ASNNode(Node.INT);
                node.addChild(leftChild);
                node.addChild(rightChild);
                node = this.setNodeTypesOfASNNode(node);
                this.nodes.put(ctx, node);
            } else if (returnType.compareTo("char") == 0) {
                IDNode leftChild = new IDNode(Node.CHAR, id);
                leftChild = this.setTypeFromExistingID(leftChild);
                Node rightChild = this.nodes.get(ctx.asgn_stmt());
                ASNNode node = new ASNNode(Node.CHAR);
                node.addChild(leftChild);
                node.addChild(rightChild);
                node = this.setNodeTypesOfASNNode(node);
                this.nodes.put(ctx, node);
            } else if (returnType.compareTo("char[]") == 0) {
                IDNode leftChild = new IDNode(Node.CHAR_AR, id);
                leftChild = this.setTypeFromExistingID(leftChild);
                Node rightChild = this.nodes.get(ctx.asgn_stmt());
                ASNNode node = new ASNNode(Node.CHAR_AR);
                node.addChild(leftChild);
                node.addChild(rightChild);
                node = this.setNodeTypesOfASNNode(node);
                this.nodes.put(ctx, node);
            } else if (returnType.compareTo("int[]") == 0) {
                IDNode leftChild = new IDNode(Node.INT_AR, id);
                leftChild = this.setTypeFromExistingID(leftChild);
                Node rightChild = this.nodes.get(ctx.asgn_stmt());
                ASNNode node = new ASNNode(Node.INT_AR);
                node.addChild(leftChild);
                node.addChild(rightChild);
                node = this.setNodeTypesOfASNNode(node);
                this.nodes.put(ctx, node);
            }
        }
        // asgnStmt is an LCCharArray
        else if (asgnStmt.isLCCharArray()) {
            LCCharArray charArray = (LCCharArray)asgnStmt;
            if (charArray.hasIdentifier()) {
                String exprID = charArray.getIdentifier();
                LCCharArray val = new LCCharArray(id, new LCExpr(exprID), charArray.getSize());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else {
                LCCharArray val = new LCCharArray(id, charArray.getExprValue(), charArray.getSize());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            }
            // Create the node
            IDNode leftChild = new IDNode(Node.CHAR_AR, id);
            leftChild = this.setTypeFromExistingID(leftChild);
            Node rightChild = this.nodes.get(ctx.asgn_stmt());
            ASNNode node = new ASNNode(Node.CHAR_AR);
            node.addChild(leftChild);
            node.addChild(rightChild);
            node = this.setNodeTypesOfASNNode(node);
            this.nodes.put(ctx, node);
        }
        // asgnStmt is an LCIntArray
        else if (asgnStmt.isLCIntArray()) {
            LCIntArray intArray = (LCIntArray) asgnStmt;
            if (intArray.hasIdentifier()) {
                String exprID = intArray.getIdentifier();
                LCIntArray val = new LCIntArray(id, new LCExpr(exprID), intArray.getSize());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            } else {
                LCIntArray val = new LCIntArray(id, intArray.getExprValue(), intArray.getSize());
                this.assignNewValueAtSymbolTable(id, val);
                this.values.put(ctx, val);
            }
            // Create the node
            IDNode leftChild = new IDNode(Node.INT_AR, id);
            leftChild = this.setTypeFromExistingID(leftChild);
            Node rightChild = this.nodes.get(ctx.asgn_stmt());
            ASNNode node = new ASNNode(Node.INT_AR);
            node.addChild(leftChild);
            node.addChild(rightChild);
            node = this.setNodeTypesOfASNNode(node);
            this.nodes.put(ctx, node);
        }
        this.assignInProgress = false;
    }

    @Override
    public void exitAssignment3(LittleCParser.Assignment3Context ctx) {
        // Third Rule: array_asgn -> ID ASGN STRINGLIT
        /* NOTE: This assignment format is unique to char arrays, but assignments involving
        // all arrays can use the other assignment parser rules too. */
        LCCharArray val = (LCCharArray)this.values.get(ctx.array_asgn());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.array_asgn());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitArrayAsgn_Strlit(LittleCParser.ArrayAsgn_StrlitContext ctx) {
        // Third Rule: array_asgn -> ID ASGN STRINGLIT
        String id = ctx.ID().getText();
        String strLit = ctx.STRINGLIT().getText();
        int size = strLit.length() - 2; // Minus two because of double quotes
        LCExpr sizeExpr = new LCExpr("" + size);
        LCCharArray val = new LCCharArray(id, strLit, sizeExpr);
        this.values.put(ctx, val);
        // Create the node
        IDNode leftChild = new IDNode(Node.CHAR_AR, id);
        LITNode rightChild = new LITNode(Node.CHAR_AR, val);
        ASNNode node = new ASNNode(Node.CHAR_AR);
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitAssignment4(LittleCParser.Assignment4Context ctx) {
        // Fourth Rule: LP asgn_stmt RP
        LCType val = this.values.get(ctx.asgn_stmt());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.asgn_stmt());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitArrayInsertion(LittleCParser.ArrayInsertionContext ctx) {
        String arrayID = ctx.ID().getText();
        // Check if the array's id has been declared
        if (!this.symbolTable.isIdDeclared(arrayID)) {
            try {
                throw new ParsingException(ParsingException.idNotDeclared, arrayID);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        // Check if the Array is an int array or a char array
        LCType array = this.symbolTable.getLCType(arrayID);
        // Array is a char array
        if (array.isLCCharArray()) {
            // Create the node
            LCExpr size = ((LCCharArray)array).getSize();
            ASNNode node = new ASNNode(Node.CHAR);
            AIDXNode leftChild = new AIDXNode(Node.CHAR);
                String arrayStr = "[" + size.getExprStr() + "]";
                IDNode arrayIDNode = new IDNode(Node.CHAR_AR, arrayID, arrayStr);
                Node exprNode = this.nodes.get(ctx.expr(0));
                leftChild.addChild(arrayIDNode);
                leftChild.addChild(exprNode);
            Node rightChild = this.nodes.get(ctx.expr(1));
            node.addChild(leftChild);
            node.addChild(rightChild);
            this.nodes.put(ctx, node);
        }
        // Array is an int array
        else if (array.isLCIntArray()) {
            // Create the node
            LCExpr size = ((LCIntArray)array).getSize();
            ASNNode node = new ASNNode(Node.INT);
            AIDXNode leftChild = new AIDXNode(Node.INT);
                String arrayStr = "[" + size.getExprStr() + "]";
                IDNode arrayIDNode = new IDNode(Node.INT_AR, arrayID, arrayStr);
                Node exprNode = this.nodes.get(ctx.expr(0));
                leftChild.addChild(arrayIDNode);
                leftChild.addChild(exprNode);
            Node rightChild = this.nodes.get(ctx.expr(1));
            node.addChild(leftChild);
            node.addChild(rightChild);
            this.nodes.put(ctx, node);
        }
        // Array is not an array (Error)
        else {
            try {
                throw new ParsingException(ParsingException.idIsNotAnArray, arrayID);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        // Pass Array object
        this.values.put(ctx, array);
    }

    //====== Expressions ======

    @Override
    public void exitExpr(LittleCParser.ExprContext ctx) {
        if (ctx.oprt() != null) {
            LCType val = this.values.get(ctx.oprt());
            this.values.put(ctx, val);
            // Pass the node
            Node node = this.nodes.get(ctx.oprt());
            this.nodes.put(ctx, node);
            /* If there is no variable declaration in progress and there is no assignment in
            // in progress, then update any and all post operation values */
            if (this.vrblDclrinProgress == false && this.assignInProgress == false) {
                this.updatePostOprtValues();
                this.resetPostOprtValues();
            }
        }
        if (ctx.expr() != null) {
            LCType val = this.values.get(ctx.expr());
            this.values.put(ctx, val);
            // Pass the node
            Node node = this.nodes.get(ctx.expr());
            this.nodes.put(ctx, node);
            /* If there is no variable declaration in progress and there is no assignment in
            // in progress, then update any and all post operation values */
            if (this.vrblDclrinProgress == false && this.assignInProgress == false) {
                this.updatePostOprtValues();
                this.resetPostOprtValues();
            }
        }
    }

    //=== Boolean Operators ===

    @Override
    public void exitOprt(LittleCParser.OprtContext ctx) {
        LCType val = this.values.get(ctx.bool_oprt());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.bool_oprt());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitIsSubBool(LittleCParser.IsSubBoolContext ctx) {
        LCType val = this.values.get(ctx.sub_bool());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.sub_bool());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitOrOprt(LittleCParser.OrOprtContext ctx) {
        LCType boolOprt = this.values.get(ctx.bool_oprt());
        LCType subBool = this.values.get(ctx.sub_bool());
        String oprt = "||";
        try {
            LCExpr expr = this.createBinOprtExpr(boolOprt, subBool, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(boolOprt, subBool);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.bool_oprt());
        Node rightChild = this.nodes.get(ctx.sub_bool());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, "||");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitIsCmpElmt(LittleCParser.IsCmpElmtContext ctx) {
        LCType val = this.values.get(ctx.cmp_elmt());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.cmp_elmt());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitAndOprt(LittleCParser.AndOprtContext ctx) {
        LCType subBool = this.values.get(ctx.sub_bool());
        LCType cmpEmlt = this.values.get(ctx.cmp_elmt());
        String oprt = "&&";
        try {
            LCExpr expr = this.createBinOprtExpr(subBool, cmpEmlt, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(subBool, cmpEmlt);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.sub_bool());
        Node rightChild = this.nodes.get(ctx.cmp_elmt());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, "&&");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    //=== Comparison Operators ===

    @Override
    public void exitCmp_elmt(LittleCParser.Cmp_elmtContext ctx) {
        LCType val = this.values.get(ctx.cmp_oprt());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.cmp_oprt());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitIsSubCmp(LittleCParser.IsSubCmpContext ctx) {
        LCType val = this.values.get(ctx.sub_cmp());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.sub_cmp());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitIsEqualTo(LittleCParser.IsEqualToContext ctx) {
        LCType cmpOprt = this.values.get(ctx.cmp_oprt());
        LCType subCmp = this.values.get(ctx.sub_cmp());
        String oprt = "==";
        try {
            LCExpr expr = this.createBinOprtExpr(cmpOprt, subCmp, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(cmpOprt, subCmp);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.cmp_oprt());
        Node rightChild = this.nodes.get(ctx.sub_cmp());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, "==");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitIsNotEqualTo(LittleCParser.IsNotEqualToContext ctx) {
        LCType cmpOprt = this.values.get(ctx.cmp_oprt());
        LCType subCmp = this.values.get(ctx.sub_cmp());
        String oprt = "!=";
        try {
            LCExpr expr = this.createBinOprtExpr(cmpOprt, subCmp, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(cmpOprt, subCmp);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.cmp_oprt());
        Node rightChild = this.nodes.get(ctx.sub_cmp());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, "!=");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitIsMathElmt(LittleCParser.IsMathElmtContext ctx) {
        LCType val = this.values.get(ctx.math_elmt());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.math_elmt());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitLessThan(LittleCParser.LessThanContext ctx) {
        LCType subCmp = this.values.get(ctx.sub_cmp());
        LCType mathElmt = this.values.get(ctx.math_elmt());
        String oprt = "<";
        try {
            LCExpr expr = this.createBinOprtExpr(subCmp, mathElmt, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(subCmp, mathElmt);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.sub_cmp());
        Node rightChild = this.nodes.get(ctx.math_elmt());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, "<");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitLessThanEqual(LittleCParser.LessThanEqualContext ctx) {
        LCType subCmp = this.values.get(ctx.sub_cmp());
        LCType mathElmt = this.values.get(ctx.math_elmt());
        String oprt = "<=";
        try {
            LCExpr expr = this.createBinOprtExpr(subCmp, mathElmt, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(subCmp, mathElmt);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.sub_cmp());
        Node rightChild = this.nodes.get(ctx.math_elmt());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, "<=");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitGreaterThan(LittleCParser.GreaterThanContext ctx) {
        LCType subCmp = this.values.get(ctx.sub_cmp());
        LCType mathElmt = this.values.get(ctx.math_elmt());
        String oprt = ">";
        try {
            LCExpr expr = this.createBinOprtExpr(subCmp, mathElmt, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(subCmp, mathElmt);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.sub_cmp());
        Node rightChild = this.nodes.get(ctx.math_elmt());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, ">");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitGreaterThanEqual(LittleCParser.GreaterThanEqualContext ctx) {
        LCType subCmp = this.values.get(ctx.sub_cmp());
        LCType mathElmt = this.values.get(ctx.math_elmt());
        String oprt = ">=";
        try {
            LCExpr expr = this.createBinOprtExpr(subCmp, mathElmt, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(subCmp, mathElmt);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.sub_cmp());
        Node rightChild = this.nodes.get(ctx.math_elmt());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, ">=");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    //=== Math Operators ===

    @Override
    public void exitMath_elmt(LittleCParser.Math_elmtContext ctx) {
        LCType val = this.values.get(ctx.math_oprt());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.math_oprt());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitIsTerm(LittleCParser.IsTermContext ctx) {
        LCType val = this.values.get(ctx.term());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.term());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitAddition(LittleCParser.AdditionContext ctx) {
        LCType mathOprt = this.values.get(ctx.math_oprt());
        LCType term = this.values.get(ctx.term());
        String oprt = "+";
        try {
            LCExpr expr = this.createBinOprtExpr(mathOprt, term, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(mathOprt, term);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.math_oprt());
        Node rightChild = this.nodes.get(ctx.term());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, "+");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitSubtraction(LittleCParser.SubtractionContext ctx) {
        LCType mathOprt = this.values.get(ctx.math_oprt());
        LCType term = this.values.get(ctx.term());
        String oprt = "-";
        try {
            LCExpr expr = this.createBinOprtExpr(mathOprt, term, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(mathOprt, term);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.math_oprt());
        Node rightChild = this.nodes.get(ctx.term());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, "-");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitMultiply(LittleCParser.MultiplyContext ctx) {
        LCType term = this.values.get(ctx.term());
        LCType factor = this.values.get(ctx.factor());
        String oprt = "*";
        try {
            LCExpr expr = this.createBinOprtExpr(term, factor, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(term, factor);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.term());
        Node rightChild = this.nodes.get(ctx.factor());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, "*");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitDivide(LittleCParser.DivideContext ctx) {
        LCType term = this.values.get(ctx.term());
        LCType factor = this.values.get(ctx.factor());
        String oprt = "/";
        try {
            LCExpr expr = this.createBinOprtExpr(term, factor, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(term, factor);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.term());
        Node rightChild = this.nodes.get(ctx.factor());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, "/");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitMod(LittleCParser.ModContext ctx) {
        LCType term = this.values.get(ctx.term());
        LCType factor = this.values.get(ctx.factor());
        String oprt = "%";
        try {
            LCExpr expr = this.createBinOprtExpr(term, factor, oprt);
            boolean isResultInt = this.binaryOprtResultLCType(term, factor);
            if (isResultInt) {
                LCInteger val = new LCInteger(expr);
                this.values.put(ctx, val);
            } else {
                LCChar val = new LCChar(expr);
                this.values.put(ctx, val);
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        // Create the node
        Node leftChild = this.nodes.get(ctx.term());
        Node rightChild = this.nodes.get(ctx.factor());
        int nodeType = this.binaryOpResultNodeType(leftChild.getType(), rightChild.getType());
        leftChild = this.getCASTNode(nodeType, leftChild);
        rightChild = this.getCASTNode(nodeType, rightChild);
        BINOPNode node = new BINOPNode(nodeType, "%");
        node.addChild(leftChild);
        node.addChild(rightChild);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitIsFactor(LittleCParser.IsFactorContext ctx) {
        LCType val = this.values.get(ctx.factor());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.factor());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitFactor(LittleCParser.FactorContext ctx) {
        LCType val = this.values.get(ctx.uni_elmt());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.uni_elmt());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitUni_elmt(LittleCParser.Uni_elmtContext ctx) {
        LCType val = this.values.get(ctx.uni_oprt());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.uni_oprt());
        this.nodes.put(ctx, node);
    }

    //=== Unary Operators ===

    @Override
    public void exitIsElmt(LittleCParser.IsElmtContext ctx) {
        LCType val = this.values.get(ctx.elmt());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.elmt());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitPosOprnd(LittleCParser.PosOprndContext ctx) {
        LCType lcType = this.values.get(ctx.uni_oprt());
        String posOprt = "+";
        // lcType is an int
        if (lcType.isLCInteger()) {
            LCInteger lcInteger = (LCInteger)lcType;
            if (lcInteger.hasIdentifier()) {
                String id = lcInteger.getIdentifier();
                String exprStr = posOprt + id;
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                String exprStr = posOprt + lcInteger.getExprStr();
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.values.put(ctx, val);
            }
            // Create the node
            if (!lcInteger.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.INT, '+', litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.INT, '+', idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is a char
        else if (lcType.isLCChar()) {
            LCChar lcChar = (LCChar)lcType;
            if (lcChar.hasIdentifier()) {
                String id = lcChar.getIdentifier();
                String exprStr = posOprt + id;
                LCChar val = new LCChar(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                String exprStr = posOprt + lcChar.getExprStr();
                LCChar val = new LCChar(new LCExpr(exprStr));
                this.values.put(ctx, val);
            }
            // Create the node
            if (!lcChar.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.CHAR, '+', litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.CHAR, '+', idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is not an int or a char (Error)
        else {
            try {
                throw new ParsingException(ParsingException.invalidUnaryOprtUse, posOprt);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitNegOprnd(LittleCParser.NegOprndContext ctx) {
        LCType lcType = this.values.get(ctx.uni_oprt());
        String negOprt = "-";
        // lcType is an int
        if (lcType.isLCInteger()) {
            LCInteger lcInteger = (LCInteger)lcType;
            if (lcInteger.hasIdentifier()) {
                String id = lcInteger.getIdentifier();
                String exprStr = negOprt + id;
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                String exprStr = negOprt + lcInteger.getExprStr();
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.values.put(ctx, val);
            }
            // Create the node
            if (!lcInteger.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.INT, '-', litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.INT, '-', idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is a char
        else if (lcType.isLCChar()) {
            LCChar lcChar = (LCChar)lcType;
            if (lcChar.hasIdentifier()) {
                String id = lcChar.getIdentifier();
                String exprStr = negOprt + id;
                LCChar val = new LCChar(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                String exprStr = negOprt + lcChar.getExprStr();
                LCChar val = new LCChar(new LCExpr(exprStr));
                this.values.put(ctx, val);
            }
            // Create the node
            if (!lcChar.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.CHAR, '-', litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.CHAR, '-', idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is not an int or a char (Error)
        else {
            try {
                throw new ParsingException(ParsingException.invalidUnaryOprtUse, negOprt);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitNotOprnd(LittleCParser.NotOprndContext ctx) {
        LCType lcType = this.values.get(ctx.uni_oprt());
        String notOprt = "!";
        // lcType is an int
        if (lcType.isLCInteger()) {
            LCInteger lcInteger = (LCInteger)lcType;
            if (lcInteger.hasIdentifier()) {
                String id = lcInteger.getIdentifier();
                String exprStr = notOprt + id;
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                String exprStr = notOprt + lcInteger.getExprStr();
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.values.put(ctx, val);
            }
            // Create the node
            if (!lcInteger.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.INT, '!', litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.INT, '!', idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is a char
        else if (lcType.isLCChar()) {
            LCChar lcChar = (LCChar)lcType;
            if (lcChar.hasIdentifier()) {
                String id = lcChar.getIdentifier();
                String exprStr = notOprt + id;
                LCChar val = new LCChar(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                String exprStr = notOprt + lcChar.getExprStr();
                LCChar val = new LCChar(new LCExpr(exprStr));
                this.values.put(ctx, val);
            }
            // Create the node
            if (!lcChar.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.CHAR, '!', litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                UNARYOPNode node = new UNARYOPNode(Node.CHAR, '!', idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is not an int or a char (Error)
        else {
            try {
                throw new ParsingException(ParsingException.invalidUnaryOprtUse, notOprt);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitPreIncOprnd(LittleCParser.PreIncOprndContext ctx) {
        LCType lcType = this.values.get(ctx.uni_oprt());
        String preInc = "++";
        // lcType is an int
        if (lcType.isLCInteger()) {
            LCInteger lcInteger = (LCInteger)lcType;
            if (lcInteger.hasIdentifier()) {
                String id = lcInteger.getIdentifier();
                String exprStr = preInc + id;
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                String exprStr = preInc + lcInteger.getExprStr();
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.values.put(ctx, val);
            }
            // Create the node
            if (!lcInteger.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.INT, INCDECNode.PREINC, litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.INT, INCDECNode.PREINC, idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is a char
        else if (lcType.isLCChar()) {
            LCChar lcChar = (LCChar)lcType;
            if (lcChar.hasIdentifier()) {
                String id = lcChar.getIdentifier();
                String exprStr = preInc + id;
                LCChar val = new LCChar(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                String exprStr = preInc + lcChar.getExprStr();
                LCChar val = new LCChar((new LCExpr(exprStr)));
                this.values.put(ctx, val);
            }
            // Create the node
            if (!lcChar.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.CHAR, INCDECNode.PREINC, litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.CHAR, INCDECNode.PREINC, idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is not an int or a char (Error)
        else {
            try {
                throw new ParsingException(ParsingException.invalidPostPreOprtUse);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitPreDecOprnd(LittleCParser.PreDecOprndContext ctx) {
        LCType lcType = this.values.get(ctx.uni_oprt());
        String preDec = "--";
        // lcType is an int
        if (lcType.isLCInteger()) {
            LCInteger lcInteger = (LCInteger)lcType;
            if (lcInteger.hasIdentifier()) {
                String id = lcInteger.getIdentifier();
                String exprStr = preDec + id;
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                String exprStr = preDec + lcInteger.getExprStr();
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.values.put(ctx, val);
            }
            // Create the node
            if (!lcInteger.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.INT, INCDECNode.PREDEC, litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.INT, INCDECNode.PREDEC, idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is a char
        else if (lcType.isLCChar()) {
            LCChar lcChar = (LCChar)lcType;
            if (lcChar.hasIdentifier()) {
                String id = lcChar.getIdentifier();
                String exprStr = preDec + id;
                LCChar val = new LCChar(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                String exprStr = preDec + lcChar.getExprStr();
                LCChar val = new LCChar(new LCExpr(exprStr));
                this.values.put(ctx, val);
            }
            // Create the node
            if (!lcChar.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.CHAR, INCDECNode.PREDEC, litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.CHAR, INCDECNode.PREDEC, idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is not an int or a char (Error)
        else {
            try {
                throw new ParsingException(ParsingException.invalidPostPreOprtUse);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitPostIncOprnd(LittleCParser.PostIncOprndContext ctx) {
        LCType lcType = this.values.get(ctx.uni_oprt());
        String postInc = "++";
        // lcType is an int
        if (lcType.isLCInteger()) {
            LCInteger lcInteger = (LCInteger)lcType;
            if (lcInteger.hasIdentifier()) {
                String id = lcInteger.getIdentifier();
                String exprStr = id + postInc;
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                try {
                    throw new ParsingException(ParsingException.invalidPostOprtUse);
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
            }
            // Create the node
            if (!lcInteger.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.INT, INCDECNode.POSTINC, litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.INT, INCDECNode.POSTINC, idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is a char
        else if (lcType.isLCChar()) {
            LCChar lcChar = (LCChar)lcType;
            if (lcChar.hasIdentifier()) {
                String id = lcChar.getIdentifier();
                String exprStr = id + postInc;
                LCChar val = new LCChar(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                try {
                    throw new ParsingException(ParsingException.invalidPostOprtUse);
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
            }
            // Create the node
            if (!lcChar.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.CHAR, INCDECNode.POSTINC, litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.CHAR, INCDECNode.POSTINC, idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is not an int or a char (Error)
        else {
            try {
                throw new ParsingException(ParsingException.invalidPostPreOprtUse);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitPostDecOprnd(LittleCParser.PostDecOprndContext ctx) {
        LCType lcType = this.values.get(ctx.uni_oprt());
        String postDec = "--";
        // lcType is an int
        if (lcType.isLCInteger()) {
            LCInteger lcInteger = (LCInteger)lcType;
            if (lcInteger.hasIdentifier()) {
                String id = lcInteger.getIdentifier();
                String exprStr = id + postDec;
                LCInteger val = new LCInteger(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                try {
                    throw new ParsingException(ParsingException.invalidPostOprtUse);
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
            }
            // Create the node
            if (!lcInteger.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.INT, INCDECNode.POSTDEC, litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.INT, INCDECNode.POSTDEC, idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is a char
        else if (lcType.isLCChar()) {
            LCChar lcChar = (LCChar)lcType;
            if (lcChar.hasIdentifier()) {
                String id = lcChar.getIdentifier();
                String exprStr = id + postDec;
                LCChar val = new LCChar(new LCExpr(exprStr));
                this.symbolTable.assignNewValue(id, val);
                this.values.put(ctx, val);
            } else {
                try {
                    throw new ParsingException(ParsingException.invalidPostOprtUse);
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
            }
            // Create the node
            if (!lcChar.hasIdentifier()) {
                LITNode litNode = (LITNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.CHAR, INCDECNode.POSTDEC, litNode);
                this.nodes.put(ctx, node);
            } else {
                IDNode idNode = (IDNode)this.nodes.get(ctx.uni_oprt());
                INCDECNode node = new INCDECNode(Node.CHAR, INCDECNode.POSTDEC, idNode);
                this.nodes.put(ctx, node);
            }
        }
        // lcType is not an int or a char (Error)
        else {
            try {
                throw new ParsingException(ParsingException.invalidPostPreOprtUse);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitIsArrayLength(LittleCParser.IsArrayLengthContext ctx) {
        LCType val = this.values.get(ctx.ar_len_oprt());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.ar_len_oprt());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitIsArrayIndexing(LittleCParser.IsArrayIndexingContext ctx) {
        LCType val = this.values.get(ctx.ar_indexing());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.ar_indexing());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitNestedOprt(LittleCParser.NestedOprtContext ctx) {
        LCType val = this.values.get(ctx.bool_oprt());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.bool_oprt());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitArrayLength(LittleCParser.ArrayLengthContext ctx) {
        // This identifier should belong to an array
        String id = ctx.ID().getText();
        // Check if the identifier has been declared, throw an error if not.
        if (!this.symbolTable.isIdDeclared(id)) {
            try {
                throw new ParsingException(ParsingException.idNotDeclared, id);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        // Check if lcType is an int array or a char array
        LCType lcType = this.symbolTable.getLCType(id);
        if (lcType.isLCCharArray()) {
            LCCharArray lcCharArray = (LCCharArray)lcType;
            LCExpr lengthExpr = lcCharArray.getSize();
            LCInteger val = new LCInteger(lengthExpr);
            this.values.put(ctx, val);
            // Create the node
            String arrayStr = "[" + lengthExpr.getExprStr() + "]";
            IDNode idNode = new IDNode(Node.CHAR_AR, id, arrayStr);
            UNARYOPNode node = new UNARYOPNode(Node.CHAR, '#', idNode);
            this.nodes.put(ctx, node);
        } else if (lcType.isLCIntArray()) {
            LCIntArray lcIntArray = (LCIntArray)lcType;
            LCExpr lengthExpr = lcIntArray.getSize();
            LCInteger val = new LCInteger(lengthExpr);
            this.values.put(ctx, val);
            // Create the node
            String arrayStr = "[" + lengthExpr.getExprStr() + "]";
            IDNode idNode = new IDNode(Node.INT_AR, id, arrayStr);
            UNARYOPNode node = new UNARYOPNode(Node.INT, '#', idNode);
            this.nodes.put(ctx, node);
        }
        // The identifier does not belong to an array (Error)
        else {
            try {
                throw new ParsingException(ParsingException.idIsNotAnArray, id);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitArrayIndexing(LittleCParser.ArrayIndexingContext ctx) {
        // This identifier should belong to an array
        String id = ctx.ID().getText();
        // Check if the identifier has been declared, throw an error if not.
        if (!this.symbolTable.isIdDeclared(id)) {
            try {
                throw new ParsingException(ParsingException.idNotDeclared, id);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        // Check if lcType is an int array or a char array
        LCType lcType = this.symbolTable.getLCType(id);
        String exprStr = ctx.expr().getText();
        if (lcType.isLCCharArray()) {
            LCExpr expr = new LCExpr(id + "[" + exprStr + "]");
            LCChar val = new LCChar(expr);
            this.values.put(ctx, val);
            // Create the node
            LCExpr sizeExpr = ((LCCharArray)lcType).getSize();
            String arrayStr = "[" + sizeExpr.getExprStr() + "]";
            IDNode idNode = new IDNode(Node.CHAR_AR, id, arrayStr);
            Node exprNode = this.nodes.get(ctx.expr());
            AIDXNode node = new AIDXNode(Node.CHAR);
            node.addChild(idNode);
            node.addChild(exprNode);
            this.nodes.put(ctx, node);
        } else if (lcType.isLCIntArray()) {
            LCExpr expr = new LCExpr(id + "[" + exprStr + "]");
            LCInteger val = new LCInteger(expr);
            this.values.put(ctx, val);
            // Create the node
            LCExpr sizeExpr = ((LCIntArray)lcType).getSize();
            String arrayStr = "[" + sizeExpr.getExprStr() + "]";
            IDNode idNode = new IDNode(Node.INT_AR, id, arrayStr);
            Node exprNode = this.nodes.get(ctx.expr());
            AIDXNode node = new AIDXNode(Node.INT);
            node.addChild(idNode);
            node.addChild(exprNode);
            this.nodes.put(ctx, node);
        }
        // The identifier does not belong to an array (Error)
        else {
            try {
                throw new ParsingException(ParsingException.idIsNotAnArray, id);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitIsFuncCall(LittleCParser.IsFuncCallContext ctx) {
        LCFuncCall val = (LCFuncCall)this.values.get(ctx.func_call());
        this.values.put(ctx, val);
        // Pass the node
        Node node = this.nodes.get(ctx.func_call());
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitIsID(LittleCParser.IsIDContext ctx) {
        String id = ctx.ID().getText();
        // If the id has been declared, load its value from the symbol table.
        if (this.symbolTable.isIdDeclared(id)) {
            LCType lcType = this.symbolTable.getLCType(id);
            if (lcType.isLCInteger()) {
                LCInteger lcInteger = (LCInteger)lcType;
                LCInteger val = new LCInteger(id, new LCExpr(id));
                this.values.put(ctx, val);
                // Create the node
                IDNode node = new IDNode(Node.INT, id);
                this.nodes.put(ctx, node);
            } else if (lcType.isLCChar()) {
                LCChar lcChar = (LCChar)lcType;
                LCInteger val = new LCInteger(id, new LCExpr(id));
                this.values.put(ctx, val);
                // Create the node
                IDNode node = new IDNode(Node.CHAR, id);
                this.nodes.put(ctx, node);
            } else if (lcType.isLCCharArray()) {
                LCCharArray lcCharArray = (LCCharArray)lcType;
                LCExpr size = lcCharArray.getSize();
                LCCharArray val = new LCCharArray(id, new LCExpr(id), size);
                this.values.put(ctx, val);
                // Create the node
                String arrayStr = "[" + size.getExprStr() + "]";
                IDNode node = new IDNode(Node.CHAR_AR, id, arrayStr);
                this.nodes.put(ctx, node);
            } else if (lcType.isLCIntArray()) {
                LCIntArray lcIntArray = (LCIntArray)lcType;
                LCExpr size = lcIntArray.getSize();
                LCIntArray val = new LCIntArray(id, new LCExpr(id), size);
                this.values.put(ctx, val);
                // Create the node
                String arrayStr = "[" + size.getExprStr() + "]";
                IDNode node = new IDNode(Node.INT_AR, id, arrayStr);
                this.nodes.put(ctx, node);
            } else {
                try {
                    throw new ParsingException(ParsingException.invalidIdInExpr, id);
                } catch (ParsingException e) {
                    e.printStackTrace();
                }
            }
        }
        // The identifier has not been declared (Error)
        else {
            try {
                throw new ParsingException(ParsingException.idNotDeclared, id);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exitIsINTLIT(LittleCParser.IsINTLITContext ctx) {
        int intlit = Integer.parseInt(ctx.INTLIT().getText());
        LCInteger val = new LCInteger(intlit);
        this.values.put(ctx, val);
        // Create the node
        LITNode node = new LITNode(Node.INT, val);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitIsCHARLIT(LittleCParser.IsCHARLITContext ctx) {
        String charStr = ctx.CHARLIT().getText();
        char charChar = charStr.charAt(1);
        LCChar val = new LCChar(charChar);
        this.values.put(ctx, val);
        // Create the node
        LITNode node = new LITNode(Node.CHAR, val);
        this.nodes.put(ctx, node);
    }

    @Override
    public void exitLiteral(LittleCParser.LiteralContext ctx) {
        if (ctx.INTLIT() != null) {
            String intStr = ctx.INTLIT().getText();
            int intInt = Integer.parseInt(intStr, 10);
            LCInteger val = new LCInteger(intInt);
            this.values.put(ctx, val);
            // Create the node
            LITNode node = new LITNode(Node.INT, val);
            this.nodes.put(ctx, node);
        } else if (ctx.CHARLIT() != null) {
            String charStr = ctx.CHARLIT().getText();
            char charChar = charStr.charAt(1);
            LCChar val = new LCChar(charChar);
            this.values.put(ctx, val);
            // Create the node
            LITNode node = new LITNode(Node.CHAR, val);
            this.nodes.put(ctx, node);
        } else if (ctx.STRINGLIT() != null) {
            String str = ctx.STRINGLIT().getText();
            int size = str.length();
            LCExpr sizeExpr = new LCExpr("" + size);
            LCCharArray val = new LCCharArray("", str, sizeExpr);
            this.values.put(ctx, val);
            // Create the node
            LITNode node = new LITNode(Node.CHAR_AR, val);
            this.nodes.put(ctx, node);
        }
    }

    //================= GETTERS =================

    /**
     * Returns a syntax tree after successful parsing.
     *
     * @return a syntax tree, or null if an error was detected
     */
    public LCSyntaxTree getSyntaxTree() {
        return this.syntaxTree;
    }

    /**
     * Returns the symbol table. Can be called at any time, but
     * generally this will be called after all parsing is complete to
     * get the final symbol table containing all global declarations.
     *
     * @return the symbol table
     */
    public SymbolTable getSymbolTable() {
        return this.symbolTable;
    }

    //================= PRIVATE METHODS =================

    /**
     * Reset the formal parameters so that it is an empty array list.
     */
    private void resetFrmPars() {
        this.frmlPars = new ArrayList<>();
    }

    /**
     * Reset the post operation values so that it is an empty array list.
     */
    private void resetPostOprtValues() {
        this.postOprtValues = new ArrayList<>();
    }

    /**
     * Update any and all post operation values.
     */
    private void updatePostOprtValues() {
        // Update the values at the symbol table
        for (LCType lcType : this.postOprtValues) {
            String id = lcType.getIdentifier();
            this.symbolTable.assignNewValue(id, lcType);
        }
    }

    /**
     * Assign a new value (an LCType) to the given id at the symbol table. The assignment
     * is not performed if the id is not in the symbol table.
     * @param id
     * @param val
     */
    private void assignNewValueAtSymbolTable(String id, LCType val) {
        // If no variable declaration is in progress, change the value in the symbol table.
        if (this.vrblDclrinProgress == false) {
            this.symbolTable.assignNewValue(id, val);
        }
        /* If the id been assigned is different from the id been declared, change the value of
        // the id been assigned in the symbol table. */
        else if (id.compareTo(this.idToBeDclr) != 0) {
            this.symbolTable.assignNewValue(id, val);
        }
    }

    /**
     * Checks if the provided functions are identical. This method is used for verifying that
     * a forward function definition is valid based on the declaration.
     * @param func1
     * @param func2
     * @return
     */
    private boolean areFunctionsIdentical(LCFunction func1, LCFunction func2) {
        // Check if the types and identifiers are the same
        String funcId1 = func1.getIdentifier();
        String funcType1 = func1.getType();
        String funcId2 = func2.getIdentifier();
        String funcType2 = func2.getType();
        if (funcId1.compareTo(funcId2) != 0 || funcType1.compareTo(funcType2) != 0) {
            return false;
        }
        // Check if the functions have the same number of formal parameters
        int numPars1 = func1.getNumOfPars();
        int numPars2 = func2.getNumOfPars();
        if (numPars1 != numPars2) {
            return false;
        }
        // Check the types and ids of each formal parameter
        ArrayList<LCFunction.FormalParameter> parList1 = func1.getFormalParameters();
        ArrayList<LCFunction.FormalParameter> parList2 = func2.getFormalParameters();
        for (int i = 0; i < numPars1; i++) {
            String parId1 = parList1.get(i).getIdentifier();
            String parType1 = parList1.get(i).getType();
            String parId2 = parList2.get(i).getIdentifier();
            String parType2 = parList2.get(i).getType();
            if (parId1.compareTo(parId2) != 0 || parType1.compareTo(parType2) != 0) {
                return false;
            }
        }
        // The functions are identical, return true.
        return true;
    }

    /**
     * Given two LCType objects and a binary operator, create an LCExpr for the binary
     * operation. Throws an exception if the operands are not valid.
     * @param lcType1
     * @param lcType2
     * @param binOprt
     * @return
     */
    private LCExpr createBinOprtExpr(LCType lcType1, LCType lcType2, String binOprt) throws ParsingException {
        boolean validOperands = this.areBinOperansValid(lcType1, lcType2);
        // Check if the operands are valid, throw an error if not.
        if (!validOperands) {
            throw new ParsingException(ParsingException.invalidBinOprt);
        }
        // The operands are valid, continue.
        String exprStr = "";
        if (lcType1.isLCInteger()) {
            exprStr += ((LCInteger)lcType1).getExprStr();
        } else if (lcType1.isLCChar()) {
            exprStr += ((LCChar)lcType1).getExprStr();
        } else if (lcType1.isLCFuncCall()) {
            exprStr += ((LCFuncCall)lcType1).getExprStr();
        }
        exprStr += " " + binOprt + " ";
        if (lcType2.isLCInteger()) {
            exprStr += ((LCInteger)lcType2).getExprStr();
        } else if (lcType2.isLCChar()) {
            exprStr += ((LCChar)lcType2).getExprStr();
        } else if (lcType2.isLCFuncCall()) {
            exprStr += ((LCFuncCall)lcType2).getExprStr();
        }
        return new LCExpr(exprStr);
    }

    /**
     * Checks if the given operands for a binary operation are valid.
     * @param lcType1
     * @param lcType2
     * @return
     */
    private boolean areBinOperansValid(LCType lcType1, LCType lcType2) {
        if (lcType1.isLCIntArray() || lcType1.isLCCharArray() || lcType1.isLCFunction()) {
            return false;
        } else if (lcType2.isLCIntArray() || lcType2.isLCCharArray() || lcType2.isLCFunction()) {
            return false;
        }
        return true;
    }

    /**
     * Returns true if the result of a binary operation is an LCInteger, or false
     * if the result is an LCChar. Throws a ParsingException if either operand is of
     * an invalid type.
     * @param lcType1
     * @param lcType2
     * @return
     */
    private boolean binaryOprtResultLCType(LCType lcType1, LCType lcType2) throws ParsingException {
        if (lcType1.isLCInteger() || lcType2.isLCInteger()) {
            return true;
        } else if (lcType1.isLCFuncCall()) {
            String funcType1 = ((LCFuncCall)lcType1).getFuncType();
            if (funcType1.compareTo("int") == 0) {
                return true;
            }
        } else if (lcType2.isLCFuncCall()) {
            String funcType2 = ((LCFuncCall)lcType2).getFuncType();
            if (funcType2.compareTo("int") == 0) {
                return true;
            }
        } else if (lcType1.isLCChar() && lcType2.isLCChar()) {
            return false;
        }
        throw new ParsingException(ParsingException.invalidBinOprt);
    }

    /**
     * Get the appropriate node type as an int based on the provided string representing the
     * same node type.
     * @param nodeTypeStr
     * @return
     */
    private int getNodeType(String nodeTypeStr) {
        if (nodeTypeStr.compareToIgnoreCase("int") == 0) {
            return Node.INT;
        } else if (nodeTypeStr.compareToIgnoreCase("char") == 0) {
            return Node.CHAR;
        } else if (nodeTypeStr.compareToIgnoreCase("char[]") == 0) {
            return Node.CHAR_AR;
        } else if (nodeTypeStr.compareToIgnoreCase("int[]") == 0) {
            return Node.INT_AR;
        }
        return Node.VOID;
    }

    /**
     * Set the types of the nodes in an ASNNode appropriately. If the right child is a
     * BINOPNode then its type is set to that of the right child's without casting.
     * @param asnNode
     * @return
     */
    private ASNNode setNodeTypesOfASNNode(ASNNode asnNode) {
        // Get the left and right node types
        Node leftChild = asnNode.getChild(0);
        Node rightChild = asnNode.getChild(1);
        int leftType = leftChild.getType();
        int rightType = rightChild.getType();
        boolean isBINOPNode = rightChild instanceof BINOPNode;
        // Both nodes are of the same type
        if (leftType == rightType) {
            asnNode.setType(leftType);
            return asnNode;
        }
        // BINOPNode type should be an int
        else if (leftType == Node.INT && isBINOPNode) {
            rightChild.setType(Node.INT);
            asnNode.setType(Node.INT);
            return asnNode;
        }
        // BINOPNode type should be a char
        else if (leftType == Node.CHAR && isBINOPNode) {
            rightChild.setType(Node.CHAR);
            asnNode.setType(Node.CHAR);
            return asnNode;
        }
        // The right child should be cast as an int
        else if (leftType == Node.INT) {
            CASTNode castNode = new CASTNode(Node.INT);
            castNode.addChild(rightChild);
            ASNNode newAsnNode = new ASNNode(Node.INT);
            newAsnNode.addChild(leftChild);
            newAsnNode.addChild(castNode);
            return newAsnNode;
        }
        // The left child should be cast as a char
        else if (leftType == Node.CHAR) {
            CASTNode castNode = new CASTNode(Node.CHAR);
            castNode.addChild(rightChild);
            ASNNode newAsnNode = new ASNNode(Node.CHAR);
            newAsnNode.addChild(leftChild);
            newAsnNode.addChild(castNode);
            return newAsnNode;
        }
        // No changes
        return asnNode;
    }

    /**
     * Set the type of the given IDNode based on the type of the corresponding
     * declared or soon-to-be-declared identifier if it exists.
     * @param idNode
     * @return
     */
    private IDNode setTypeFromExistingID(IDNode idNode) {
        String id = idNode.getIdentifier();
        // The IDNode is based on a declared identifier
        if (this.symbolTable.isIdDeclared(id)) {
            LCType lcType = this.symbolTable.getLCType(id);
            if (lcType.isLCInteger()) {
                idNode.setType(Node.INT);
            } else if (lcType.isLCChar()) {
                idNode.setType(Node.CHAR);
            } else if (lcType.isLCCharArray()) {
                idNode.setType(Node.CHAR_AR);
            } else if (lcType.isLCIntArray()) {
                idNode.setType(Node.INT_AR);
            }
        }
        // The IDNode is based on a soon-to-be-declared identifier
        else if (this.idToBeDclr.compareTo(id) == 0) {
            if (this.idTypeToBeDclr.compareTo("int") == 0) {
                idNode.setType(Node.INT);
            } else if (this.idTypeToBeDclr.compareTo("char") == 0) {
                idNode.setType(Node.CHAR);
            } else if (this.idTypeToBeDclr.compareTo("char[]") == 0) {
                idNode.setType(Node.CHAR_AR);
            } else if (this.idTypeToBeDclr.compareTo("int[]") == 0) {
                idNode.setType(Node.INT_AR);
            }
        }
        return idNode;
    }

    /**
     * Return the type of node that a binary operation with node types 1 and 2 should
     * produce.
     * @param nodeType1
     * @param nodeType2
     * @return
     */
    private int binaryOpResultNodeType(int nodeType1, int nodeType2) {
        // If either node is an int, the result is an int.
        if (nodeType1 == Node.INT || nodeType2 == Node.INT) {
            return Node.INT;
        }
        // If both nodes are chars, the result is a char.
        else if (nodeType1 == Node.CHAR && nodeType2 == Node.CHAR) {
            return Node.CHAR;
        }
        // The nodes are of incompatible types.
        else {
            return Node.UNKNOWN;
        }
    }

    /**
     * Returns the provided uncast node nested within a CASTNode if the provided target type is
     * different. This method will determine if the node needs to be cast and does so if
     * necessary.
     * @param targetType
     * @param uncastNode
     * @return
     */
    private Node getCASTNode(int targetType, Node uncastNode) {
        // Both the targetType and the uncastNode type are the same (No casting required)
        if (targetType == uncastNode.getType()) {
            return uncastNode;
        }
        // The node needs to be cast as an int
        else if (targetType == Node.INT) {
            CASTNode castNode = new CASTNode(Node.INT);
            castNode.addChild(uncastNode);
            return castNode;
        }
        // The node needs to be cast as a char
        else if (targetType == Node.CHAR) {
            CASTNode castNode = new CASTNode(Node.CHAR);
            castNode.addChild(uncastNode);
            return castNode;
        }
        // The node cannot be casted because the types are incompatible (No casting)
        return uncastNode;
    }

    /**
     * Cast the child nodes of fncallNode in accordance to the types of the
     * formal parameters of lcFunction. Note that this method does not check to see
     * if both lcFunction and fncallNode correspond to the same function; this is
     * assumed to be true when using this method.
     * formal parameters
     * @param lcFunction
     * @param fncallNode
     * @return
     */
    private FNCALLNode castAtlParNodes(LCFunction lcFunction, FNCALLNode fncallNode) {
        FNCALLNode newFncallNode = new FNCALLNode(fncallNode.getType(), fncallNode.getFuncID());
        int parCount = lcFunction.getNumOfPars();
        // Iterate over the list of parameters of both objects
        for (int i = 0; i < parCount; i++) {
            // Get the strings for the types of the FormalParameter and the Node
            Node atlParNode = fncallNode.getChild(i);
            LCFunction.FormalParameter frmPar = lcFunction.getFormalParameter(i);
            String atlParNodeType = atlParNode.getTypeAsString();
            String frmParType = frmPar.getType();
            // If the types are different, casting of the node is necessary.
            if (atlParNodeType.compareToIgnoreCase(frmParType) != 0) {
                // The node has to be cast to an int
                if (frmParType.compareTo("int") == 0) {
                    CASTNode castNode = new CASTNode(Node.INT);
                    castNode.addChild(atlParNode);
                    newFncallNode.addChild(castNode);
                }
                // The node has to be cast as a char
                else if (frmParType.compareTo("char") == 0) {
                    CASTNode castNode = new CASTNode(Node.CHAR);
                    castNode.addChild(atlParNode);
                    newFncallNode.addChild(castNode);
                }
                // The node is being cast to some incompatible type (Cast as UNKNOWN)
                else {
                    CASTNode castNode = new CASTNode(Node.UNKNOWN);
                    castNode.addChild(atlParNode);
                    newFncallNode.addChild(castNode);
                }
            }
            // Casting of the node is not necessary
            else {
                newFncallNode.addChild(atlParNode);
            }
        }
        return newFncallNode;
    }

    //======= Boolean Operations =======
    /**
     * Operations for calculating LittleC boolean values. Zero is false, any other
     * non-zero value true.
     */

    private int notOperation(int x) {
        if (x == 0) {
            return 1;
        }
        return 0;
    }

    private int lessThanOperation(int x, int y) {
        if (x < y) {
            return 1;
        }
        return 0;
    }

    private int lessThanEqualOperation(int x, int y) {
        if (x <= y) {
            return 1;
        }
        return 0;
    }

    private int greaterThanOperation(int x, int y) {
        if (x > y) {
            return 1;
        }
        return 0;
    }

    private int greaterThanEqualOperation(int x, int y) {
        if (x >= y) {
            return 1;
        }
        return 0;
    }

    private int isEqualToOperation(int x, int y) {
        if (x == y) {
            return 1;
        }
        return 0;
    }

    private int isNotEqualToOperation(int x, int y) {
        if (x != y) {
            return 1;
        }
        return 0;
    }

    private int andOperation(int x, int y) {
        if (x != 0 && y != 0) {
            return 1;
        }
        return 0;
    }

    private int orOperation(int x, int y) {
        if (x != 0 || y != 0) {
            return 1;
        }
        return 0;
    }

}

