package analyzer;

import sjavaparser.Section;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class VariableRegEx {



    /**
     * variable declaration RegEx:
     * multiple variables in the same line
     * final modifier
     * types: int, double, boolean, char, String.
     * variableName start with letter, or stat with '_' and then another symbol.
     * variableValue value does not contain \ ' " ,
     * structure: type space name( space = space value); with or without initialization.
     * @param s: a section which is a candidate for variable declaration.
     * @return 0 invalid, 1 int, 2 double, 3 String, 4 boolean, 5 char, 6 reference
     */
    public static int variableDeclarationRegEx(Section s) {
        // variable declaration should be done in a single line
        if (s.getCodeLinesArray().size() != 1) {
            return 0;
        }
        VariableObject var = new VariableObject(s.getCodeLinesArray().get(0));
        // returns value patterns in order: int, double, String, boolean, char. null if invalid
        ArrayList<Pattern> patterns = variableValuePatterns();
        if (var.getIsFinal()) {
            // since it is final, if no value - return false
            if (var.getValue() == null) {
                return 0;
            }
        }
        // check type
        if (var.getType() == null) {
            return 0;
        }
        // check variable name
        if(!checkVariableName(var)) {
            return 0;
        }
        // check type validity
        if (var.getType().matches("int|double|String|boolean|char")) {
            // check if declaration value is a reference
            if (var.getValue() != null) {
                if (patterns.get(5).matcher(var.getValue()).matches() && !var.getValue().matches("true|false")) {
                    if ( !var.getName().equals(var.getValue())) { // cannot reference to itself
                        return 6;
                    }
                }
            }
        }
        else {
            return 0;
        }
        // check value for each variable type
        if (var.getType().equals("int")) {
            if (var.getValue() == null) {
                return 1;
            }
            if (patterns.get(0).matcher(var.getValue()).matches()) {
                return 1;
            }
        }
        if (var.getType().equals("double")) {
            if (var.getValue() == null) {
                return 2;
            }
            if (patterns.get(1).matcher(var.getValue()).matches()) {
                return 2;
            }

        }
        if (var.getType().equals("String")) {
            if (var.getValue() == null) {
                return 3;
            }
            if (patterns.get(2).matcher(var.getValue()).matches()) {
                return 3;
            }
        }
        if (var.getType().equals("boolean")) {
            if (var.getValue() == null) {
                return 4;
            }
            if (patterns.get(3).matcher(var.getValue()).matches()) {
                return 4;
            }
        }
        if (var.getType().equals("char")) {
            if (var.getValue() == null) {
                return 5;
            }
            if (patterns.get(4).matcher(var.getValue()).matches()) {
                return 5;
            }
        }
        return 0;
    }


    /**
    declaration and assignment were separated so double declaration could be prevented.
     @param s section
     @return 0 = matched none, 1 = int, 2 = double, 3 = String, 4 = boolean, 5 = char
     */
    public static int variableAssignmentRegEx(Section s) {
        // variable declaration should be done in a single line
        if (s.getCodeLinesArray().size() != 1) {
            return 0;
        }
        VariableObject var = new VariableObject(s.getCodeLinesArray().get(0));
        if (var.getName() == null || var.getValue() == null) {
            return 0;
        }
        if (checkVariableName(var)) {
            return 0;
        }
        ArrayList<Pattern> patterns= variableValuePatterns();
        if (patterns.get(0).matcher(var.getValue()).matches()) { // int value pattern
            return 1;
        }
        if (patterns.get(1).matcher(var.getValue()).matches()) { // double value pattern
            return 2;
        }
        if (patterns.get(2).matcher(var.getValue()).matches()) { // String value pattern
            return 3;
        }
        if (patterns.get(3).matcher(var.getValue()).matches()) { // boolean value pattern
            return 4;
        }
        if (patterns.get(4).matcher(var.getValue()).matches()) { // char value pattern
            return 5;
        }
        if (patterns.get(5).matcher(var.getValue()).matches() && !var.getValue().matches("true|false")) {
            // reference value pattern
            return 6;
        }
        return 0;
    }


    /**
     * @return patterns in this order: int, double, String, boolean, char, reference
     */
    private static ArrayList<Pattern> variableValuePatterns() {
        ArrayList<Pattern> patterns= new ArrayList<>();
        patterns.add(Pattern.compile("-?([0-9]+)\\s*")); // int
        patterns.add(Pattern.compile("-?([0-9]+(\\.[0-9]+)?)")); // double
        patterns.add(Pattern.compile("\"\\w*\"")); // String
        patterns.add(Pattern.compile("(true|false|-?[0-9]+(\\.[0-9]+)?)")); // boolean
        patterns.add(Pattern.compile("'.'")); // char
        patterns.add(Pattern.compile("[a-zA-Z]+\\w*|[_]+\\w+")); // reference
        return patterns;
    }


    private static boolean checkVariableName(VariableObject var) {
        // System.out.println("name: " + var.getName());
        // check variable name
        Pattern VarNamePattern = Pattern.compile("\\s*([a-zA-Z]+\\w*|[_]+\\w+)");
        return VarNamePattern.matcher(var.getName()).matches();
    }
}