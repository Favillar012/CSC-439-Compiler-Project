package edu.uncg.csc439.icode.ICCreation;

/**
 * This class produces and returns a list of predefined intermediate code functions.
 * @author Fernando Villarreal
 * @date 11/7/2020
 */
public class ICPredefinedFunctions {

    /**
     * Generates a list of IC predefined functions using the provided ICNames object. The functions
     * will be recognized in programs that use the same ICNames object.
     * @param icNames
     */
    public static void generateFunctions(ICNames icNames) {
        // Identical properties for the generated names
        char globalName = ICNames.globalName;
        char functionType = ICNames.functionType;
        // Create the function: void prints(char[] x)
        icNames.genNewName(globalName, functionType, "prints", "");
        // Create the function: void printd(int x)
        icNames.genNewName(globalName, functionType, "printd", "");
        // Create the function: int read()
        icNames.genNewName(globalName, functionType, "read", "");
    }
}
