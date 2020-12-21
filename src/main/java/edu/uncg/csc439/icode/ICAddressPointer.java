package edu.uncg.csc439.icode;

import java.util.ArrayList;

/**
 * This class implements an IC address or pointer based instruction.
 * @author Fernando Villarreal
 * @date 10/31/2020
 */
public class ICAddressPointer extends ICLine {

    //=============== TERMS ===============

    public static final int address_1 = 1;
    public static final int asgn = 2;
    public static final int address_2 = 3;

    // Static Instruction Types
    public static final int copyAddressOf = 0;                    // x = &y
    public static final int copyValueFromPointer = 1;             // x = *y
    public static final int copyValueToPointerLocation = 2;       // *x = y

    // Instruction Type for the Object
    private int instrType;

    //=============== CONSTRUCTORS ===============

    public ICAddressPointer(int instrType, String address_1, String address_2) {
        super();
        ArrayList<String> genAddresses = this.genAddresses(instrType, address_1, address_2);
        String a1 = genAddresses.get(0);
        String a2 = genAddresses.get(1);
        this.instrType = instrType;
        this.addTerm(a1);
        this.addTerm("=");
        this.addTerm(a2);
    }

    //=============== METHODS ===============

    public String getAddress_1() {
        return this.getTerm(ICAddressPointer.address_1);
    }

    public String getAddress_2() {
        return this.getTerm(ICAddressPointer.address_2);
    }

    public void setAddress_1(String term) {
        String address_1 = this.getAddress_1();
        char instr = address_1.charAt(0);
        if (instr == '&') {
            String formerAddress1 = address_1.substring(2);
            String newAddress1 = "&" + address_1.replaceAll(formerAddress1, term);
            this.setTerm(ICAddressPointer.address_1, newAddress1);
        } else if (instr == '*') {
            String formerAddress1 = address_1.substring(2);
            String newAddress1 = "*" + address_1.replaceAll(formerAddress1, term);
            this.setTerm(ICAddressPointer.address_1, newAddress1);
        } else {
            this.setTerm(ICAddressPointer.address_1, "???");
        }
    }

    public void setAddress_2(String term) {
        String address_2 = this.getAddress_2();
        char instr = address_2.charAt(0);
        if (instr == '&') {
            String formerAddress2 = address_2.substring(2);
            String newAddress2 = "&" + address_2.replaceAll(formerAddress2, term);
            this.setTerm(ICAddressPointer.address_2, newAddress2);
        } else if (instr == '*') {
            String formerAddress2 = address_2.substring(2);
            String newAddress2 = "*" + address_2.replaceAll(formerAddress2, term);
            this.setTerm(ICAddressPointer.address_2, newAddress2);
        } else {
            this.setTerm(ICAddressPointer.address_2, "???");
        }
    }

    public int getInstrType() {
        return this.instrType;
    }

    public void setInstrType(int instrType) {
        this.instrType = instrType;
    }

    private ArrayList<String> genAddresses(int instrType, String address_1, String address_2) {
        ArrayList<String> addresses = new ArrayList<>();
        String a1 = address_1;
        String a2 = address_2;
        if (instrType == ICAddressPointer.copyAddressOf) {
            a2 = "& " + address_2;
            addresses.add(a1);
            addresses.add(a2);
            return addresses;
        } else if (instrType == ICAddressPointer.copyValueFromPointer) {
            a2 = "* " + address_2;
            addresses.add(a1);
            addresses.add(a2);
            return addresses;
        } else if (instrType == ICAddressPointer.copyValueToPointerLocation) {
            a1 = "* " + address_1;
            addresses.add(a1);
            addresses.add(a2);
            return addresses;
        } else {
            a1 = "???";
            a2 = "???";
            addresses.add(a1);
            addresses.add(a2);
            return addresses;
        }
    }
}
