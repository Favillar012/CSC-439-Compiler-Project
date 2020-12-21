import edu.uncg.csc439.LCErrorListener;
import edu.uncg.csc439.LCListener;
import edu.uncg.csc439.antlr4.LittleCLexer;
import edu.uncg.csc439.antlr4.LittleCParser;
import edu.uncg.csc439.icode.*;
import edu.uncg.csc439.icode.ICCreation.SyntaxTreeWalker;
import edu.uncg.csc439.syntaxtree.LCSyntaxTree;
import edu.uncg.csc439.syntaxtree.Nodes.Node;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

/**
 * Parse LittleC program and print the resulting syntax tree
 *
 * @author Steve Tate (srtate@uncg.edu)
 */

public class OutputSTree {
    /**
     * Runs the parser and edu.uncg.csc439.LCListener syntax tree constructor for the
     * provided input stream. The returned object can be used to access
     * the syntax tree and the symbol table for either futher processing or
     * for checking results in automated tests.
     *
     * @param input an initialized CharStream
     * @return the edu.uncg.csc439.LCListener object that processed the parsed
     *         input or null if an error was encountered
     */

    private static LCListener parseStream(CharStream input) {
        // "input" is the character-by-character input - connect to lexer
        LittleCLexer lexer = new LittleCLexer(input);
        LCErrorListener catchErrs = new LCErrorListener();
        lexer.addErrorListener(catchErrs);

        // Connect token stream to lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Connect parser to token stream
        LittleCParser parser = new LittleCParser(tokens);
        parser.addErrorListener(catchErrs);
        ParseTree tree = parser.program();
        if (catchErrs.sawError())
            return null;

        // Now do the parsing, and walk the parse tree with our listeners
        ParseTreeWalker walker = new ParseTreeWalker();
        LCListener compiler = new LCListener(parser);
        walker.walk(compiler, tree);

        return compiler;
    }

    /**
     * Public static method to run the parser on an input file.
     *
     * @param fileName the name of the file to use for input
     * @return the edu.uncg.csc439.LCListener object that processed the parsed input
     */
    public static LCListener parseFromFile(String fileName) {
        try {
            return parseStream(CharStreams.fromFileName(fileName));
        } catch (IOException e) {
            if (e instanceof NoSuchFileException) {
                System.err.println("Could not open file " + fileName);
            } else {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Public static method to run the parser on the standard input stream.
     *
     * @return the edu.uncg.csc439.LCListener object that processed the parsed input
     */
    public static LCListener parseFromStdin() {
        try {
            return parseStream(CharStreams.fromStream(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Command line interface -- one argument is filename, and if omitted then
     * input is taken from standard input.
     *
     * @param argv command line arguments
     */
    public static void main(String[] argv) {
        LCListener parser;
        if (argv.length > 1) {
            System.err.println("Can provide at most one command line argument (an input filename)");
            return;
        } else if (argv.length == 1) {
            parser = parseFromFile(argv[0]);
        } else {
            parser = parseFromStdin();
        }

        LCSyntaxTree result = null;
        if (parser != null)
            result = parser.getSyntaxTree();

        if (result == null) {
            System.out.println("Error in compiling -- invalid LittleC program.");
        } else {
            result.printSyntaxTree();
        }
        /*********** MY TESTS ***********/
        LCSyntaxTree syntaxTree = result;
        // Call walkThroughTree
        System.out.println("\n============= walkThroughTree Method =============\n");
        //walkThroughTree(syntaxTree);
        // Call icLineTests
        System.out.println("\n============= icLineTests Method =============\n");
        //icLineTests();
        // Call icCodeTests
        System.out.println("\n============= icCodeTests Method =============\n");
        icCodeTests(syntaxTree);
    }

    /**
     * Use a SyntaxTreeWalker object to walk through the syntax tree.
     * @param syntaxTree
     */
    private static void walkThroughTree(LCSyntaxTree syntaxTree) {
        if (syntaxTree != null) {
            // Create a new SyntaxTreeWalker object
            SyntaxTreeWalker walker = new SyntaxTreeWalker(syntaxTree);
            walker.printSyntaxTree();
            // Start a post order on the syntax tree. Print the first three nodes.
            System.out.println();
            walker.startPostOrder();
            Node curNode = walker.nextPostOrderNode();
            System.out.println(curNode.toString());
            curNode = walker.nextPostOrderNode();
            System.out.println(curNode.toString());
            curNode = walker.nextPostOrderNode();
            System.out.println(curNode.toString());
            // Print the node at the nth index of the post order traversal
            int n = 10;
            curNode = walker.getPostOrderNode(n);
            System.out.println(curNode.toString());
        } else {
            System.err.println("The syntax tree is null!");
        }
    }

    /**
     * ICLine tests.
     */
    private static void icLineTests() {
        ICBinOprt binOprt = new ICBinOprt("t4_1", "*", "p4@0", "t4_2");
        binOprt.setLabel("L1");
        ICReturn icReturn = new ICReturn(ICReturn.returnInt, "t4_1");
        ICLines icLines = new ICLines();
        icLines.addLine(binOprt);
        icLines.addLine(icReturn);
        System.out.println(icLines.toString());
    }

    /**
     * Tests for the creation of an ICode object.
     * @param syntaxTree
     */
    private static void icCodeTests(LCSyntaxTree syntaxTree) {
        ICode iCode = new ICode(syntaxTree);
        System.out.println(iCode.toString());
    }
}
