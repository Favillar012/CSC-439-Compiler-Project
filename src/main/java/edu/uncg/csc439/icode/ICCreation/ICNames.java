package edu.uncg.csc439.icode.ICCreation;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class creates and maintains lists of names and labels for a particular IC
 * program.
 * @author Fernando Villarreal
 * @date 11/7/2020
 */
public class ICNames {

    //=============== STATIC VARIABLES ===============

    // Name/Label Types
    public static final char globalName = 'g';
    public static final char localName = 'l';
    public static final char parameterName = 'p';
    public static final char temporaryName = 't';
    public static final char lineLabel = 'L';
    public static final char strlitLabel = 'S';

    // Object Type (or Object Width)
    public static final char characterType = '1';
    public static final char integerType = '4';
    public static final char arrayType = '0';
    public static final char functionType = 'f';

    // Separator
    public static final char memoryAtSep = '@';
    public static final char uniqueNameSep = '_';

    // noSuchName String
    public static final String noSuchName = "NoSuchNameExists";

    //=============== CLASS VARIABLES ===============

    private Hashtable<String, String> names;
    private ArrayList<String> labels;
    private int tempNameCount;
    private int lineLabelCount;
    private int strlitLabelCount;

    //=============== CONSTRUCTOR ===============

    public ICNames() {
        this.names = new Hashtable<>();
        this.labels = new ArrayList<>();
        this.tempNameCount = 1;
        this.lineLabelCount = 1;
        this.strlitLabelCount = 1;
    }

    //=============== PUBLIC METHODS ===============

    /**
     * Check if the list of saved generated names contains an association
     * with the provided originalName.
     * @param originalName
     * @return
     */
    public boolean containsName(String originalName) {
        if (this.names.containsKey(originalName)) {
            return true;
        }
        return false;
    }

    /**
     * Get the generated name associated with the provided originalName.
     * @param originalName
     * @return
     */
    public String getGeneratedName(String originalName) {
        if (this.names.containsKey(originalName)) {
            String genName = this.names.get(originalName);
            return genName;
        }
        return ICNames.noSuchName;
    }

    /**
     * Get the name type of the provided generated name.
     * @param genName
     * @return
     */
    public static char getNameTypeOfGenName(String genName) {
        char type = genName.charAt(0);
        if (type == ICNames.globalName) {
            return ICNames.globalName;
        } else if (type == ICNames.localName) {
            return ICNames.localName;
        } else if (type == ICNames.parameterName) {
            return ICNames.parameterName;
        } else if (type == ICNames.temporaryName) {
            return ICNames.temporaryName;
        }
        return '?';
    }

    /**
     * Get the object type of the provided generated name.
     * @param genName
     * @return
     */
    public static char getObjectTypeOfGenName(String genName) {
        char type = genName.charAt(1);
        if (type == ICNames.integerType) {
            return ICNames.integerType;
        } else if (type == ICNames.characterType) {
            return ICNames.characterType;
        } else if (type == ICNames.arrayType) {
            return ICNames.arrayType;
        } else if (type == ICNames.functionType) {
            return ICNames.functionType;
        }
        return '?';
    }

    /**
     * Get the memory offset for the provided generated name. The generated name must be a local or
     * a parameter. Otherwise, -1 is returned.
     * @param genName
     * @return
     */
    public static int getMemoryOffset(String genName) {
        char nameType = ICNames.getNameTypeOfGenName(genName);
        // The nameType is a local or a parameter
        if (nameType == ICNames.localName || nameType == ICNames.parameterName) {
            String memoryOffsetString = genName.substring(3);
            try {
                return Integer.parseInt(memoryOffsetString);
            } catch (NumberFormatException ex) {
                return -1;
            }
        }
        // The nameType is not a local or a parameter
        else {
            return -1;
        }
    }

    /**
     * Remove the listed generated names from the list of generated names.
     * @param genNames
     */
    public void removeGeneratedNames(ArrayList<String> genNames) {
        ArrayList<String> originalNames = new ArrayList<>();
        for (String targetName : genNames) {
            this.names.forEach( (originalName, genName) -> {
                if (genName.compareTo(targetName) == 0) {
                    originalNames.add(originalName);
                }
            });
        }
        for (String originalName : originalNames) {
            this.names.remove(originalName);
        }
    }

    /**
     * <p>Generates a new name for a temporary, global, local, or parameter. The objectType parameter
     * is the type of object that the name represents.
     * </p>
     * <p>In temporaries, addInfo is not used and originalName is used only for associating the
     * original name of an array with its temporary pointer.</p>
     * <p>In globals, addInfo is the name of the global variable and originalName is not used.</p>
     * <p>In locals, addInfo is the memory offset and originalName is the name of the local
     * variable.</p>
     * <p>In parameters, addInfo is the memory offset and originalName is the name of the parameter
     * variable.</p>
     * @param nameType
     * @param objectType
     * @return
     */
    public String genNewName(char nameType, char objectType, String addInfo, String originalName) {
        if (nameType == ICNames.temporaryName) {
            String genName = this.genNewTemporaryName(objectType);
            this.names.put(originalName, genName);
            return genName;
        } else if (nameType == ICNames.globalName) {
            String uniqueName = addInfo;
            String genName = this.genNewGlobalName(objectType, uniqueName);
            this.names.put(uniqueName, genName);
            return genName;
        } else if (nameType == ICNames.localName) {
            String memoryOffset = addInfo;
            String genName =  this.genNewLocalName(objectType, memoryOffset);
            this.names.put(originalName, genName);
            return genName;
        } else if (nameType == ICNames.parameterName) {
            String memoryOffset = addInfo;
            String genName = this.genNewParameterName(objectType, memoryOffset);
            this.names.put(originalName, genName);
            return genName;
        } else {
            System.err.println("Invalid name type provided!");
            return "???";
        }
    }

    /**
     * Generates a new label for a string literal or a line of IC code.
     * @param labelType
     * @return
     */
    public String genNewLabel(char labelType) {
        if (labelType == ICNames.lineLabel) {
            String uniqueName = "" + this.lineLabelCount;
            String label = "L" + uniqueName;
            this.labels.add(label);
            this.lineLabelCount++;
            return label;
        } else if (labelType == ICNames.strlitLabel) {
            String uniqueName = "" + this.strlitLabelCount;
            String arrayType = "" + ICNames.arrayType;
            String separator = "" + ICNames.uniqueNameSep;
            String label = "S" + arrayType + separator + uniqueName;
            this.labels.add(label);
            this.strlitLabelCount++;
            return label;
        } else {
            System.err.println("Invalid label type provided!");
            return "???";
        }
    }

    //=================== PRIVATE METHODS ===================

    /**
     * Generates a new temporary name.
     * @return
     */
    private String genNewTemporaryName(char objectType) {
        if (objectType == ICNames.functionType) {
            System.err.println("Invalid object type for the temporary name!");
            return "t???";
        }
        String temp = "" + ICNames.temporaryName;
        String type = "" + objectType;
        String separator = "" + ICNames.uniqueNameSep;
        String uniqueName = "" + this.tempNameCount;
        String tempName = temp + type + separator + uniqueName;
        this.tempNameCount++;
        return tempName;
    }

    /**
     * Generates a new global name.
     * @param objectType
     * @param uniqueName
     * @return
     */
    private String genNewGlobalName(char objectType, String uniqueName) {
        String glb = "" + ICNames.globalName;
        String type = "" + objectType;
        String separator = "" + ICNames.uniqueNameSep;
        String globalName = glb + type + separator + uniqueName;
        return globalName;
    }

    /**
     * Generates a new local name.
     * @param objectType
     * @param memoryOffset
     * @return
     */
    private String genNewLocalName(char objectType, String memoryOffset) {
        if (objectType == ICNames.functionType) {
            System.err.println("Invalid object type for the local name!");
            return "l???";
        }
        String lcl = "" + ICNames.localName;
        String type = "" + objectType;
        String separator = "" + ICNames.memoryAtSep;
        String localName = lcl + type + separator + memoryOffset;
        return localName;
    }

    private String genNewParameterName(char objectType, String memoryOffset) {
        if (objectType == ICNames.functionType) {
            System.err.println("Invalid object type for the parameter name!");
            return "p???";
        }
        String prm = "" + ICNames.parameterName;
        String type = "" + objectType;
        String separator = "" + ICNames.memoryAtSep;
        String parameterName = prm + type + separator + memoryOffset;
        return parameterName;
    }
}
