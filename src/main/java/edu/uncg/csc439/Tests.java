package edu.uncg.csc439;

import edu.uncg.csc439.LCElements.*;

/**
 * Class for general testing.
 * @author Fernando Villarreal
 * @date 10/2/2020
 */
public class Tests {

    public static void main(String[] args) {
        test02();
    }

    /**
     * LCType.isLCInteger() and LCType.isLCFunction() tests.
     */
    public static void test01() {
        LCInteger lcInteger = new LCInteger("myInt01", 4);
        LCFunction lcFunction = new LCFunction("int", "myFunc01");
        boolean isLCInteger = lcInteger.isLCInteger();
        boolean isLCFunction = lcFunction.isLCFunction();
        if (isLCInteger) {
            System.out.println("This is an LCInteger");
        } else {
            System.out.println("This is not an LCIntger");
        }
    }

    /**
     * Symbol table tests.
     */
    public static void test02() {
        // New symbol table
        SymbolTable symbolTable = new SymbolTable();
        // New variables and a function
        LCInteger lcint01 = new LCInteger("int01", 4);
        LCFunction lcfunc01 = new LCFunction("void", "foo");
        lcfunc01.addFormalParameter(new LCFunction.FormalParameter("int", "x"));
        LCInteger lcint02 = new LCInteger("int02", 5);
        // Add variables and function to symbol table in two scopes
        symbolTable.addNewSymbol(lcint01);
        symbolTable.addNewSymbol(lcfunc01);
        symbolTable.pushNewScope();
        symbolTable.addNewSymbol(lcint02);
        // Print the symbol table
        symbolTable.printSymbolTable();
        // Check if an id is declared
        System.out.println("isIdDeclared: " + symbolTable.isIdDeclared("int01"));
        System.out.println("Object: " + symbolTable.getLCType("int01").toString());
    }

    public static void test03() {

    }
}
