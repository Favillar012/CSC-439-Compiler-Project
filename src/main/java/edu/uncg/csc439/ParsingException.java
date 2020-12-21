package edu.uncg.csc439;

/**
 * This exception class recognizes Parsing exceptions.
 * @author Fernando Villarreal
 * @date 10/30/2020
 */
public class ParsingException extends Exception {

    // Parsing Error Messages
    public static final String defaultVrbl = "VRBL";
    public static final String idNotDeclared = "The identifier VRBL has not been declared!";
    public static final String idAlreadyDeclared = "The identifier VRBL has already been declared!";
    public static final String functionAlreadyDefined = "The function VRBL has already been defined!";
    public static final String unsupportedType = "The type VRBL is not supported!";
    public static final String typeMismatch = "The types do not match!";
    public static final String invalidFuncCall = "The function call VRBL is not valid!";
    public static final String undefinedFunc = "The function VRBL has not been defined!";
    public static final String invalidPostOprtUse = "Post operations cannot be used on non-identifier operands!";
    public static final String invalidPostPreOprtUse = "Post/Pre operations can only be used on integers and characters!";
    public static final String funcFrwdDefIsInvalid = "The forward definition of function VRBL differs from prior declaration!";
    public static final String idIsNotAnArray = "The id VRBL is not an array!";
    public static final String invalidUnaryOprtUse = "The unary operator 'VRBL' cannot be used here!";
    public static final String invalidBinOprt = "The binary operation is not valid!";
    public static final String invalidIdInExpr = "The identifier VRBL points to a type that is not acceptable in an expression!";

    //=========== CONSTRUCTORS ===========

    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, String inMessageVariable) {
        super(message.replaceAll(defaultVrbl, inMessageVariable));
    }
}
