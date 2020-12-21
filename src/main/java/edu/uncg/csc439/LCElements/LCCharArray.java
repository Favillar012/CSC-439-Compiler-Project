package edu.uncg.csc439.LCElements;

import java.util.ArrayList;

/**
 * This class implements a Little C character array or a string.
 * @author Fernando Villarreal
 * @date 10/21/2020
 */
public class LCCharArray extends LCExprUnit {

    //================= VARIABLES =================

    private ArrayList<LCChar> characters;
    private final LCExpr size; // Size might be defined as an expression

    //================= CONSTRUCTORS =================

    public LCCharArray(String identifier, LCExpr size) {
        super(identifier);
        this.characters = new ArrayList<>();
        this.size = size;
    }

    public LCCharArray(String identifier, String str, LCExpr size) {
        super(identifier);
        this.characters = LCCharArray.stringToLCCharArray(str);
        this.size = size;
    }

    public LCCharArray(String identifier, ArrayList<LCChar> characters, LCExpr size) {
        super(identifier);
        this.characters = characters;
        this.size = size;
    }

    public LCCharArray(String identifier, LCExpr exprValue, LCExpr size) {
        super(identifier, exprValue);
        this.size = size;
    }

    public LCCharArray(String identifier, LCCharArray lcCharArray) {
        super(identifier);
        if (!lcCharArray.isValueExpr()) {
            this.characters = lcCharArray.getCharacters();
        } else {
            this.setExprValue(lcCharArray.getExprValue());
        }
        this.size = lcCharArray.getSize();
    }

    //================= METHODS =================

    public ArrayList<LCChar> getCharacters() {
        return this.characters;
    }

    public void setCharacters(ArrayList<LCChar> characters) {
        this.characters = characters;
    }

    public void setCharacters(String str) {
        this.characters = LCCharArray.stringToLCCharArray(str);
    }

    /**
     * Get a string that represents this LCCharArray.
     * @return
     */
    public String getAsString() {
        String str = "";
        for (LCChar lcChar : this.characters) {
            char character = lcChar.getCharacter();
            str += character;
        }
        return str;
    }

    /**
     * Get the LCChar at the specified index.
     * @param index
     * @return
     */
    public LCChar getLCCharAt(int index) {
        return this.characters.get(index);
    }

    /**
     * Get the size of this LCCharArray. Note that the size is a LCExpr because it might be
     * defined as an expression instead of an integer.
     * @return
     */
    public LCExpr getSize() {
        return this.size;
    }

    public void insertLCChar(LCChar lcChar, int index) {
        this.characters.add(index, lcChar);
    }

    @Override
    public String getExprStr() {
        if (!this.isValueExpr()) {
            return this.getAsString();
        }
        return super.getExprStr();
    }

    @Override
    public String toString() {
        return "char " + this.getIdentifier() + "[] = " + this.getAsString();
    }

    /**
     * Converts the provided string into an ArrayList of LCChar's.
     * @param str
     * @return
     */
    public static ArrayList<LCChar> stringToLCCharArray(String str) {
        ArrayList<LCChar> charArray = new ArrayList<>();
        int strLength = str.length();
        for (int i = 0; i < strLength; i++) {
            char character = str.charAt(i);
            LCChar lcChar = new LCChar(character);
            charArray.add(lcChar);
        }
        return charArray;
    }
}
