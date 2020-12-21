package edu.uncg.csc439.LCElements;

import java.util.ArrayList;

/**
 * This class implements a Little C integer array.
 * @author Fernando Villarreal
 * @date 10/21/2020
 */
public class LCIntArray extends LCExprUnit {

    //================= VARIABLES =================

    private ArrayList<LCInteger> integers;
    private final LCExpr size; // Size might be defined as an expression

    //================= CONSTRUCTORS =================

    public LCIntArray(String identifier, LCExpr size) {
        super(identifier);
        this.integers = new ArrayList<>();
        this.size = size;
    }

    public LCIntArray(String identifier, ArrayList<LCInteger> integers, LCExpr size) {
        super(identifier);
        this.integers = integers;
        this.size = size;
    }

    public LCIntArray(String identifier, LCExpr exprValue, LCExpr size) {
        super(identifier, exprValue);
        this.size = size;
    }

    public LCIntArray(String identifier, LCIntArray lcIntArray) {
        super(identifier);
        if (!lcIntArray.isValueExpr()) {
            this.integers = lcIntArray.getIntegers();
        } else {
            this.setExprValue(lcIntArray.getExprValue());
        }
        this.size = lcIntArray.getSize();
    }

    //================= METHODS =================

    public ArrayList<LCInteger> getIntegers() {
        return this.integers;
    }

    public void setIntegers(ArrayList<LCInteger> integers) {
        this.integers = integers;
    }

    /**
     * Get the LCInteger at the specified index.
     * @param index
     * @return
     */
    public LCInteger getLCIntegerAt(int index) {
        return this.integers.get(index);
    }

    /**
     * Get the size of this LCIntArray. Note that the size is a LCExpr because it might be
     * defined as an expression instead of an integer.
     * @return
     */
    public LCExpr getSize() {
        return this.size;
    }

    public void insertLCInteger(LCInteger lcInteger, int index) {
        this.integers.add(index, lcInteger);
    }

    @Override
    public String getExprStr() {
        if (!this.isValueExpr()) {
            return this.getIntegersAsString();
        }
        return super.getExprStr();
    }

    @Override
    public String toString() {
        return "int " + this.getIdentifier() + "[] = " + this.getIntegersAsString();
    }

    public String getIntegersAsString() {
        String str = "[";
        int length = this.integers.size();
        for (int i = 0; i < length; i++) {
            LCInteger lcInteger = this.integers.get(i);
            str += lcInteger.getValue();
            if (i < (length - 1)) {
                str += ", ";
            }
        }
        str += "]";
        return str;
    }
}
