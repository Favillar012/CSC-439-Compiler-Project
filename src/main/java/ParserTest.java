import edu.uncg.csc439.LCErrorListener;
import edu.uncg.csc439.LCListener;
import edu.uncg.csc439.SymbolTable;
import edu.uncg.csc439.syntaxtree.LCSyntaxTree;
import edu.uncg.csc439.antlr4.*;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

/**
 * Basic parser tester. Has a main() so can be run from the command
 * line, with one optional command line parameter. If provided, this is a
 * filename to use for input. Otherwise, input is taken from standard input.
 * More importantly, the parseFromFile and parseFromStdin methods are public
 * static methods and can be called from automated tests. They return the
 * edu.uncg.csc439.LCListener object that was used in parsing, giving access to both the
 * final syntax tree and the final symbol table.
 *
 * @author Steve Tate (srtate@uncg.edu)
 */

public class ParserTest {
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
            SymbolTable symbolTable = parser.getSymbolTable();
            System.out.println("Global Variables:\n");
            symbolTable.printGlobalVars();

            System.out.println("\nGlobal Functions:\n");
            symbolTable.printGlobalFns();

            System.out.println("\nSyntax Tree:\n");
            result.printSyntaxTree();
        }
    }
}
