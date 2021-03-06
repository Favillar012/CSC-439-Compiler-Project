import edu.uncg.csc439.LCErrorListener;
import edu.uncg.csc439.LCListener;
import edu.uncg.csc439.antlr4.LittleCLexer;
import edu.uncg.csc439.antlr4.LittleCParser;
import edu.uncg.csc439.icode.ICode;
import edu.uncg.csc439.mipsgen.FlowGraph.BasicBlock;
import edu.uncg.csc439.mipsgen.FlowGraph.FCGraph;
import edu.uncg.csc439.mipsgen.MIPSGen;
import edu.uncg.csc439.syntaxtree.LCSyntaxTree;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

/**
 * Compile a LittleC program to intermediate code, printing the IC to standard
 * output.
 *
 * @author Steve Tate (srtate@uncg.edu)
 */

public class OutputICode {
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
        ICode iCode = null;
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

        if (result != null) {
            iCode = new ICode(result);
            String codeAsStr = iCode.toString();
            System.out.println(codeAsStr);
        }

        //============ MY TESTS ============
        if (iCode != null) {
            // FCGraph Tests
            fcGraphTests(iCode);
            // MIPSGen Tests
            mipsGenTests(iCode);
        } else {
            System.out.println("iCode is null!");
        }

    }

    /**
     * Tests for the creation of the FCGraph object.
     * @param iCode
     */
    public static void fcGraphTests(ICode iCode) {
        System.out.println("\n==================== FCGRAPH TESTS ====================\n");
        FCGraph fcGraph = new FCGraph(iCode);
        System.out.println(fcGraph.toString());
        BasicBlock block1 = fcGraph.getBasicBlock(1);
        System.out.println("\nBlock 1 and its nextBlocks:\n");
        System.out.println(block1.toString() + "\n");
        ArrayList<BasicBlock> nextBlocks = block1.getNextBlocks();
        for (BasicBlock nextBlock : nextBlocks) {
            System.out.println("\n" + nextBlock.toString());
        }
    }

    public static void mipsGenTests(ICode iCode) {
        System.out.println("\n==================== MIPSGEN TESTS ====================\n");
        MIPSGen mipsGen = new MIPSGen(iCode);
        System.out.println(mipsGen.toString());
    }
}
