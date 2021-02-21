package analyzer;

import sjavaparser.Section;

/** This class gets a section and classifies it */
public class SectionClassifier {

    private final static String[] sectionTypes = {"comment line", "variable declaration", "variable assignment",
            "declaring method", "if or while", "empty line", "return statement", "method call"};
    private final static String ILLEGAL_SECTION = "Illegal section";


    public static String classifySection(Section section) {
        if (section.getCodeLinesArray().size() == 0) { // zero lines section
            return sectionTypes[5]; // section is an empty line
        } else if (section.getCodeLinesArray().size() == 1) { // one line section
            if (CommentLineRegEx.documentationRegEx(section)) {
                return sectionTypes[0]; // section is a comment line
            } else if (VariableRegEx.variableDeclarationRegEx(section) != 0) {
                return sectionTypes[1]; // section is a variable declaration
            } else if (VariableRegEx.variableAssignmentRegEx(section) != 0) {
                return sectionTypes[2]; // section is a variable assignment
            }else if (section.getCodeLinesArray().get(0).matches("\\s*")) {
                return sectionTypes[5]; // section is an empty line (only spaces)
            }
            else if (ReturnStatementRegEx.returnStatementRegEx(section)) {
                return sectionTypes[6]; // section is a return statement.
            }
        } else { // multiple lines section
            if (section.getCodeLinesArray().get(0).startsWith("if") ||
                    section.getCodeLinesArray().get(0).startsWith("while") ||
                    section.getCodeLinesArray().get(0).startsWith(" if") ||
                            section.getCodeLinesArray().get(0).startsWith(" while")) {
                if (checkIfOrWhile(section)) {
                    return sectionTypes[4]; // if or while
                }
            }
            if (checkMethod(section)) {
                return sectionTypes[3]; // declaring method
            }
            if (checkMethodCall(section)) {
                return sectionTypes[7];
            }
        }
        return ILLEGAL_SECTION;
    }


    /**
     * an helper function of classifySection.
     * gets a section which is a candidate to be an if/while block, and checks if structure is valid (excluding regex).
     * @param section to be if/while.
     * @return true if an if/while block, false (+sets error in section) otherwise.
     */
    private static boolean checkIfOrWhile(Section section) {
        if (section.getCodeLinesArray().get(0).matches(".*[{]")) {
            return section.getCodeLinesArray().get(section.getCodeLinesArray().size() - 1).matches(".*[}]");
        }
        return false;
    }


    /**
     * Helper to classifySection.
     * gets a section which is a candidate to be a method, and checks if structure is valid (excluding regex)
     * @param section section
     * @return true if structure is valid
     */
    private static boolean checkMethod(Section section) {
        MethodSignatureRegEx methodSignatureRegEx = new MethodSignatureRegEx(section.getCodeLinesArray().get(0));
        // check for return statement is done in the analyzer
        return MethodSignatureRegEx.methodSignatureRegEx(methodSignatureRegEx) && (methodSignatureRegEx.getHasVoid());
    }

    private static boolean checkMethodCall(Section section) {
        if (section.getCodeLinesArray().size() == 1) {
            MethodSignatureRegEx methodSignatureRegEx = new MethodSignatureRegEx(section.getCodeLinesArray().get(0));
            return MethodSignatureRegEx.methodSignatureRegEx(methodSignatureRegEx) &&
                    (!methodSignatureRegEx.getHasVoid());

        }
        return false;
    }
}
