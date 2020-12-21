package edu.uncg.csc439;

import edu.uncg.csc439.LCElements.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

/**
 * Symbol Table class for the LittleC compiler project. Implementation details
 * are up to you, but make sure you fill in the printGlobalVars() and
 * printGlobalFns() methods below.
 * @modifiedBy Fernando Villarreal
 * @date 10/30/2020
 */
public class SymbolTable {

    /**
     * The ArrayList stack acts as a stack which contains the different scopes of the symbol table.
     */
    private ArrayList<Hashtable<String, LCType>> stack;
    private ArrayList<Hashtable<String, LCType>> poppedScopes;

    //================= CONSTRUCTOR =================

    public SymbolTable() {
        this.stack = new ArrayList<>();
        this.poppedScopes = new ArrayList<>();
        this.pushNewScope();
    }

    //================= METHODS =================

    /**
     * This method should print out all of the global variables. It can be
     * called after parsing in order to see what global variables were seen.
     * Names should be output in alphabetical order.
     */
    public void printGlobalVars() {
        // Create an ArrayList to store the global variables and get the global scope
	    ArrayList<LCType> globalVariables = new ArrayList<>();
	    Hashtable<String, LCType> globalScope = this.getScope(0);
	    // Add every global variable to the ArrayList
	    globalScope.forEach( (id, lcType) -> {
	        if (lcType instanceof LCInteger) {
	            globalVariables.add(lcType);
            } else if (lcType instanceof LCChar) {
	            globalVariables.add(lcType);
            }
        });
	    // Sort and print the globalVariables ArrayList
        Collections.sort(globalVariables);
        System.out.println("Global Variables: " + globalVariables.toString());
    }

    /**
     * This method should print out all of the global functions (in LittleC, all
     * functions are global, so that means all functions). It can be called
     * after parsing in order to see what functions were defined. This will
     * probably include the standard functions (prints, printd, read, and
     * readline) as well. Names should be printed in alphabetical order.
     */
    public void printGlobalFns() {
        // Create an ArrayList to store the global functions and get the global scope
        ArrayList<LCType> globalFunctions = new ArrayList<>();
        Hashtable<String, LCType> globalScope = this.getScope(0);
        // Add every global function to the ArrayList
        globalScope.forEach( (id, lcType) -> {
            if (lcType instanceof LCFunction) {
                globalFunctions.add(lcType);
            }
        });
        // Sort and print the globalFunctions ArrayList
        Collections.sort(globalFunctions);
        System.out.println("Global Functions: " + globalFunctions.toString());
    }

    /**
     * Return the scope at the top of the stack.
     * @return
     */
    public Hashtable<String, LCType> topScope() {
        Hashtable<String, LCType> scope = this.stack.get(this.stack.size() - 1);
        return scope;
    }

    /**
     * Pop the scope at the top of the stack. Save the scope to the popped scopes list.
     */
    public void popScope() {
        Hashtable<String ,LCType> poppedScope = this.topScope();
        this.stack.remove(this.stack.size() - 1);
        this.poppedScopes.add(poppedScope);
    }

    /**
     * Pop and return the scope at the the top of the stack.
     * @return
     */
    public Hashtable<String, LCType> topAndPopScope() {
        Hashtable<String, LCType> scope = this.topScope();
        this.popScope();
        return scope;
    }

    /**
     * Push a new scope to the top of the stack.
     */
    public void pushNewScope() {
        Hashtable<String, LCType> scope = new Hashtable<>();
        this.stack.add(scope);
    }

    /**
     * Get the popped scope at the specified index.
     * @param index
     * @return
     */
    public Hashtable<String ,LCType> getPoppedScope(int index) {
        return this.poppedScopes.get(index);
    }

    /**
     * Search for an identifier in the popped scopes list. Returns true if the identifier was found.
     * @param id
     * @return
     */
    public boolean isIdInPoppedScope(String id) {
        for (Hashtable<String, LCType> poppedScope : this.poppedScopes) {
            if (poppedScope.containsKey(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a list of LCTypes with the provided id in the popped scopes list.
     * @param id
     * @return
     */
    public ArrayList<LCType> getLCTypesInPoppedScopes(String id) {
        ArrayList<LCType> lcTypes = new ArrayList<>();
        for (Hashtable<String, LCType> poppedScope : this.poppedScopes) {
            if (poppedScope.containsKey(id)) {
                LCType matchingLCType = poppedScope.get(id);
                lcTypes.add(matchingLCType);
            }
        }
        return lcTypes;
    }

    /**
     * Add a new symbol to the top scope of the Symbol Table.
     * @param symbol
     */
    public void addNewSymbol(LCType symbol) {
        Hashtable<String, LCType> topScope = this.topScope();
        topScope.put(symbol.getIdentifier(), symbol);
    }

    /**
     * Returns the LCType associated with the given id. Returns null if there is no such LCType.
     * @param id
     * @return
     */
    public LCType getLCType(String id) {
        int topOfStackIndex = this.stack.size() -  1;
        for (int i = topOfStackIndex; i >= 0; i--) {
            Hashtable<String, LCType> scope = this.getScope(i);
            boolean isDeclared = scope.containsKey(id);
            if (isDeclared == true) {
                return scope.get(id);
            }
        }
        return null;
    }

    /**
     * Assign a new value to an already declared identifier. Note that this method will not
     * accept LCFuncCall objects for the newValue
     * @param id
     * @param newValue
     */
    public void assignNewValue(String id, LCType newValue) {
        try {
            LCType lcType = this.getLCType(id);
            // int = int
            if (lcType.isLCInteger() && newValue.isLCInteger()) {
                LCInteger original = (LCInteger)lcType;
                LCInteger newValueCast = (LCInteger)newValue;
                if (newValueCast.isValueExpr()) {
                    original.setExprValue(newValueCast.getExprValue());
                } else {
                    original.setValue(newValueCast.getValue());
                }
            }
            // char = char
            else if (lcType.isLCChar() && newValue.isLCChar()) {
                LCChar original = (LCChar)lcType;
                LCChar newValueCast = (LCChar)newValue;
                if (newValueCast.isValueExpr()) {
                    original.setExprValue(newValueCast.getExprValue());
                } else {
                    original.setCharacter(newValueCast.getCharacter());
                }
            }
            // int = char
            else if (lcType.isLCInteger() && newValue.isLCChar()) {
                LCInteger original = (LCInteger)lcType;
                LCChar newValueCast = (LCChar)newValue;
                if (newValueCast.isValueExpr()) {
                    original.setExprValue(newValueCast.getExprValue());
                } else {
                    original.setValue(newValueCast.getValue());
                }
            }
            // char = int
            else if (lcType.isLCChar() && newValue.isLCInteger()) {
                LCChar original = (LCChar)lcType;
                LCInteger newValueCast = (LCInteger)newValue;
                if (newValueCast.isValueExpr()) {
                    original.setExprValue(newValueCast.getExprValue());
                } else {
                    original.setCharacter((char)newValueCast.getValue());
                }
            }
            // char[] = char[]
            else if (lcType.isLCCharArray() && newValue.isLCCharArray()) {
                LCCharArray original = (LCCharArray)lcType;
                LCCharArray newValueCast = (LCCharArray)newValue;
                if (newValueCast.isValueExpr()) {
                    original.setExprValue(newValueCast.getExprValue());
                } else {
                    original.setCharacters(newValueCast.getCharacters());
                }
            }
            // int[] = int[]
            else if (lcType.isLCIntArray() && newValue.isLCIntArray()) {
                LCIntArray original = (LCIntArray)lcType;
                LCIntArray newValueCast = (LCIntArray)newValue;
                if (newValueCast.isValueExpr()) {
                    original.setExprValue(newValueCast.getExprValue());
                } else {
                    original.setIntegers(newValueCast.getIntegers());
                }
            } else {
                throw new Exception("SymbolTable.assignNewValue() Error: Conflicting types.");
            }
        } catch (NullPointerException ex) {
            System.err.println("SymbolTable.assignNewValue() Error: The provided id points to null.");
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Print the Symbol Table.
     */
    public void printSymbolTable() {
        int scopeCount = 0;
        for (Hashtable<String, LCType> scope : this.stack) {
            System.out.println("Scope " + scopeCount + ":");
            scope.forEach( (id, lcType) -> {
                System.out.println("\t" + lcType.toString());
            });
            scopeCount++;
        }
    }

    /**
     * Determines if the provided identifier has been declared. This condition will be true if an item
     * corresponding to the given identifier is found. The search begins with the table at the top of
     * the symbol table's stack and proceeds towards the bottom.
     * @param id
     * @return
     */
    public boolean isIdDeclared(String id) {
        int topOfStackIndex = this.stack.size() - 1;
        for (int i = topOfStackIndex; i >= 0; i--) {
            Hashtable<String, LCType> scope = this.getScope(i);
            boolean idDeclared = scope.containsKey(id);
            if (idDeclared == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets and returns the scope at the given index of the stack.
     * @param index
     * @return
     */
    private Hashtable<String, LCType> getScope(int index) {
        return this.stack.get(index);
    }

} // public class SymbolTable