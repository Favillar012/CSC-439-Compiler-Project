package edu.uncg.csc439.LCElements;

import java.util.ArrayList;

/**
 * This static class creates and returns a list of predefined Little C functions.
 * @author Fernando Villarreal
 * @date 10/17/2020
 */
public class PredefinedLCFunctions {

    public static ArrayList<LCFunction> getFunctions() {
        return PredefinedLCFunctions.createFunctions();
    }

    private static ArrayList<LCFunction> createFunctions() {
        // Create an ArrayList to store the functions in
        ArrayList<LCFunction> functions = new ArrayList<>();
        // Create the function: void main()
        LCFunction main = new LCFunction("void", "main");
        functions.add(main);
        // Create the function: void prints(char[] x)
        LCFunction.FormalParameter printsFP1 = new LCFunction.FormalParameter("char[]", "x");
        ArrayList<LCFunction.FormalParameter> printsFPs = new ArrayList<>();
        printsFPs.add(printsFP1);
        LCFunction prints = new LCFunction("void", "prints", printsFPs, new LCBlock());
        functions.add(prints);
        // Create the function: void printd(int x)
        LCFunction.FormalParameter printdFP1 = new LCFunction.FormalParameter("int", "x");
        ArrayList<LCFunction.FormalParameter> printdFPs = new ArrayList<>();
        printdFPs.add(printdFP1);
        LCFunction printd = new LCFunction("void", "printd", printdFPs, new LCBlock());
        functions.add(printd);
        // Create the function: int read()
        LCFunction read = new LCFunction("int", "read", new LCBlock());
        functions.add(read);
        // Return the ArrayList of functions
        return functions;
    }
}
