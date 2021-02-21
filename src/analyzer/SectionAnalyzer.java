package analyzer;

import oop.ex6.main.CodeException;
import oop.ex6.main.SyntaxException;
import oop.ex6.main.UsageException;
import sjavaparser.Section;
import java.util.ArrayList;

public class SectionAnalyzer {


    public static void analyzeSection(Section section) throws CodeException {
        String sectionType = SectionClassifier.classifySection(section);
        ArrayList<Section> sectionsInSection;

        if (sectionType.equals("comment line")) {
            section.setType("comment line");
            return;
        }

        // another single line section type : method call
        if (MethodCallRegEx.methodCallRegEx(section)) { // not sure that the regex i wrote is correct.
            section.setType("method call");
            return;
        }

        if (sectionType.equals("return statement")) {
            section.setType("return statement");
            return;
        }
        if (sectionType.equals("method call")) {
            Section sectionCopy = section;
            while (sectionCopy.getOuterSection() != null) {
                sectionCopy = sectionCopy.getOuterSection();
            }
            if (!sectionCopy.getType().equals("method")) {
                throw new UsageException("method call detected not inside a method.");
            }
            /* check method call name and args validity:
               - check that the called method has been previously declared
               - check that args type match method signature
             */
            MethodSignatureRegEx methodCall = new MethodSignatureRegEx(section.getCodeLinesArray().get(0));
            // first mentioned test (declared before)
            String calledMethodName = methodCall.getMethodName();
            if (!section.getGlobalSection().getMethodsInFile().containsKey(calledMethodName)) {
                throw new UsageException("method called but not declared.");
            }
            // second mentioned test (args type)
            ArrayList<String> methodCallArgs = methodCall.getMethodParameters();
            ArrayList<VariableObject> methodCallVariables = new ArrayList<>();
            for (String methodCallArg : methodCallArgs) {
                methodCallVariables.add(new VariableObject(methodCallArg));
            }
            ArrayList<VariableObject> methodDeclarationParameters = section.getGlobalSection().getMethodsInFile().
                    get(methodCallArgs);
            for (VariableObject methodCallVariable : methodCallVariables) {
                for (VariableObject methodDeclarationParameter : methodDeclarationParameters) {
                    if (!methodCallVariable.getType().equals(methodDeclarationParameter.getType())){
                        throw new UsageException("args types are not matched in method call.");
                    }
                }
            }
        }
        if (sectionType.equals("variable declaration")) {
            // if reference, check inside section and outside section
            section.setType("variable declaration");
            if (section.getOuterSection() != null) {
                section.setGlobalSection(section.getOuterSection().getGlobalSection());
            }
            if (section.getGlobalSection().getSectionArrayList().contains(section)) { // outer is global
                if (VariableRegEx.variableDeclarationRegEx(section) == 6) {
                    section.getGlobalSection().setAsReferenceVar(
                            new VariableObject(section.getCodeLinesArray().get(0)));
                    if (!referenceDeclaredInSection(section.getGlobalSection().getReferenceVariables(),
                            section.getGlobalSection().getLocallyDeclaredVariables())) {
                            throw new CodeException("reference variable not declared");
                    }
                    else if (section.getOuterSection() != null){ // outer is local
                        if (!referenceDeclaredInSection(section.getReferenceVariables(),
                                section.getOuterSection().getLocallyDeclaredVariables())) {
                            throw new CodeException("reference variable not declared");
                        }
                    }
                }
                section.getGlobalSection().setAsDeclaredVar( new VariableObject(section.getCodeLinesArray().get(0)));
            }
            return;
            //return sectionVariables;
        }
        if (sectionType.equals("variable assignment")) {
            section.setType("variable assignment");
            if (section.getGlobalSection().getSectionArrayList().contains(section)) { // out is global
                section.getGlobalSection().setAsAssignedVar(new VariableObject(section.getCodeLinesArray().get(0)));
            }
            else { // outer is local

            }
            //checking of assignment validity is done in Sjavac.java
            return;
            //return sectionVariables;
        }
        if (sectionType.equals("declaring method")) {
            // check that method declaration is in global space - has no outer sections.
            if (section.getOuterSection() != null) {
                return;
            }
            // check method content validity
            sectionsInSection = section.getSectionsInSection();
            // check that the method ends with a return statement.
            if ((section.getOuterSection() == null) &&
                    (ReturnStatementRegEx.returnStatementRegEx(sectionsInSection.get(sectionsInSection.size() - 1)))) {
                section.setType("method");
                if (sectionsInSection != null) {
                    for (Section subSection : sectionsInSection) {
                        subSection.setOuterSection(section);
                    }
                    for (Section subSection : sectionsInSection) {
                        analyzeSection(subSection);
                    }
                }
                // handle reference variables
                section.assignReferenceVariables();
                boolean exists;
                for (VariableObject referenceVar : section.getReferenceVariables()) {
                    exists = false;
                    for (VariableObject declaredVar : section.getLocallyDeclaredVariables()) {
                        if (referenceVar.getValue().equals(declaredVar.getName())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {// not declared inside section. check outside
                        for (VariableObject declaredVar : section.getOuterSection().getLocallyDeclaredVariables()) {
                            if (referenceVar.getValue().equals(declaredVar.getName())) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            throw new SyntaxException("reference variable not declared");
                        }
                    }
                }
                return;
            }
            return; // "method does not end with a return statement."
        }
        if (sectionType.equals("if or while")) {
            // check validity (if false, prints 1 and return)
            section.setType("if or while");
            // check loop content validity
            sectionsInSection = section.getSectionsInSection();
            for (Section subSection : sectionsInSection) {
                analyzeSection(subSection);
            }
            // handle reference variables
            section.assignReferenceVariables();
            return;
        }
        if (sectionType.equals("empty line")) {
            // check validity (if false, prints 1 and return)
            section.setType("empty line");
        }
        else {
            throw new SyntaxException("section didn't fit any legal type");
        }
    }


    /**
     * if its value is declared in section: if types don't match return false
     * if its value is declared outside section ( outer + global): if types don't match return false
     * else return true
     *
     */
    public static boolean referenceDeclaredInSection(ArrayList<VariableObject> referenceVariables,
                                                     ArrayList<VariableObject> declaredVariables) {
        boolean exists;
        for (VariableObject referenceVar : referenceVariables) {
            exists = false;
            for (VariableObject declaredVar : declaredVariables) {
                if (referenceVar.getValue().equals(declaredVar.getName()) && declaredVar.getValue() != null) {
                    // variable declared and assigned value before referenced
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                return false;
            }
        }
        return true;
    }


    /**
     * check validity of section's variable regarding its outer section
     * @param section section
     * @return false: invalidity inside section (see method checkValidityInsideSection), or cannot assign local variable
     * to a variable declared outside (see method canAssign)
     */
    public static boolean checkValidityOfVariablesOutsideSection (Section section) {
        if (!checkValidityOfVariablesInsideSection(section)) {
            return false;
        }
        for (VariableObject assignedVar : section.getLocallyAssignedVariables()) {
            // check in outer sections.
            while (section.getOuterSection() != null) {
                if (!canAssign(assignedVar,section.getOuterSection())) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Helper method to checkValidityOfVariablesOutsideSection. Gets a section, and checks if all declarations
     * and assignments of variables are valid.
     * @param sectionToCheck section
     * @return true if valid, else false.
     */
    public static boolean checkValidityOfVariablesInsideSection(Section sectionToCheck) {
        ArrayList<VariableObject> dupVars = checkDuplicates(sectionToCheck.getLocallyDeclaredVariables(),
                sectionToCheck.getLocallyAssignedVariables());
        if (dupVars != null) {
            for (VariableObject dupVar : dupVars) {
                return canAssign(dupVar, sectionToCheck);
            }
        }
        return true;
    }


    /**
     * Gets an array of objects, and checks if there are duplicates of values in the array.
     * @return all duplicate variables if found in an array, null otherwise.
     */
    public static ArrayList<VariableObject> checkDuplicates(ArrayList<VariableObject> declaredVariableArray,
                                                            ArrayList<VariableObject> assignedVariableArray) {
        ArrayList<String> noDuplicatesArray = new ArrayList<>();
        ArrayList<VariableObject> duplicateVariables = new ArrayList<>();

        for (VariableObject var : declaredVariableArray) {
            if (noDuplicatesArray.contains(var.getName())) {
                duplicateVariables.add(var);
            }
            else {
                noDuplicatesArray.add(var.getName());
            }
        }

        for (VariableObject var : assignedVariableArray) {
            if (noDuplicatesArray.contains(var.getName())) {
                duplicateVariables.add(var);
            }
            else {
                noDuplicatesArray.add(var.getName());
            }
        }
        if (duplicateVariables.size() == 0) {
            return null;
        }
        return duplicateVariables;
    }

    /**
     * This method gets a value that has duplicates in the original code.
     * It checks for the duplication, if it's a legal re-assignment or an illegal statement.
     * @param variable duplicate value to check
     * @param section to check if legal in it..
     * @return false if attpemt to reassign final variable, or type doesn't match. true otherwise
     */
    public static boolean canAssign(VariableObject variable, Section section) {
        VariableObject firstDeclaration = findFirstDeclaration(variable.getName(), section);
        if (firstDeclaration != null) {
            if (!firstDeclaration.getIsFinal() && firstDeclaration.getType() != null) {
                return firstDeclaration.getType().equals(variable.getType()) ||
                        firstDeclaration.setValue(variable.getValue());
            }
        }
        return false; // variable is final, or has not been declared ( type is null)
    }

    /**
     * Helper to canAssign - Finds the code line that the specified variable first mentioned.
     * @param varName variable name to find.
     * @return the first mention of the variable in code.
     */
    private static VariableObject findFirstDeclaration (String varName, Section section) {
        for (String line : section.getCodeLinesArray()) {
            Section sectionLine = new Section(line);
            if (SectionClassifier.classifySection(sectionLine).equals("declaring variable")) {
                VariableObject candidate = new VariableObject(line);
                if (candidate.getName().equals(varName)) {
                    return candidate;
                }
            }
        }
        return null; // notice this cannot happen because the method is called only when there are duplicates.
    }
}
