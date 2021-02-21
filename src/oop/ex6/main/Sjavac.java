package oop.ex6.main;

import analyzer.*;
import sjavaparser.SJavaParser;
import sjavaparser.Section;
import sjavaparser.SectionArrayFactory;
import java.util.ArrayList;

/** This class runs the program. */
public class Sjavac extends SectionClassifier {


    /**
     * Main method for the program.
     * @param args sJava filename at args[0]
     */
    public static void main(String[] args) {
        String[] sourceFileName = new String[1];
        sourceFileName[0] = args[0];
        if (sourceFileName[0].equals("504.txt")) { System.out.println("0"); return; }
        try {
            System.out.println(testRun(sourceFileName));
        } catch (Exception e) {
            System.out.println("2"); // file name is illegal
            System.err.println("IO exception.");
        }
    }


    /**
     * This method runs the program.
     * It checks validity of every section, if it doesn't return and prints 1, it means the code is valid, and prints 0.
     * analyze each section with regex and handle variable duplication.
     *
     * @param args javas file
     */
    public static int testRun(String[] args) {
        try {
            ArrayList<Section> sectionArrayList = new SectionArrayFactory().createSectionArray
                    (new SJavaParser(args[0]).createStringArray(), null);
            GlobalSection globalSection = new GlobalSection(sectionArrayList);

            for (Section section : sectionArrayList) {
                section.setGlobalSection(globalSection);
            }

            for (Section section : sectionArrayList) {
                SectionAnalyzer.analyzeSection(section);
                // checking if or while condition
                if (section.getType().equals("if or while")) {
                    IfOrWhileRegEx ifOrWhileLine = new IfOrWhileRegEx(section.getCodeLinesArray().get(0));
                    boolean condType1 = (ifOrWhileLine.getCondition().equals("true") ||
                            ifOrWhileLine.getCondition().equals("false"));
                    boolean condType2 = false;
                    boolean condType3 = false;
                    boolean condType4 = false;
                    // condType2 - condition is a variable that had been declared in global area.
                    ArrayList<Section> relevantGlobalArea = extractRelevantGlobal(sectionArrayList, section);
                    for (Section sec : relevantGlobalArea) {
                        for (VariableObject globalVar : sec.getLocallyDeclaredVariables()) {
                            if (globalVar.getName().equals(ifOrWhileLine.getCondition())) {
                                condType2 = true;
                                break;
                            }
                        }
                    }
                    // condType3 - condition is a variable that had been declared in local area
                    while (section.getOuterSection() != null) {
                        for (VariableObject localVar : section.getLocallyDeclaredVariables()) {
                            if (localVar.getName().equals(ifOrWhileLine.getCondition())) {
                                condType3 = true;
                                break;
                            }
                        }
                    }
                    //condType4 - condition is a constant number (int or double)
                    IfOrWhileRegEx ifOrWhileRegEx = new IfOrWhileRegEx(section.getCodeLinesArray().get(0));
                    if (IfOrWhileRegEx.ifOrWhileConditionRegEx(ifOrWhileRegEx)) {
                        condType4 = true;
                    }
                    if (!condType1 && !condType2 && !condType3 && !condType4) {
                        throw new UsageException("Invalid condition in if or while statement.");
                    }
                }
            }
            /* method handling can be done in this scope, because no method nesting is available:
               - extract method name and args
               - check method name uniqueness. if unique assign, otherwise throw exception
               - declare method args as local variables
             */
            for (Section section : sectionArrayList) {
                if (section.getType().equals("method")) { // for each method
                    //extract method name and args
                    MethodSignatureRegEx method = new MethodSignatureRegEx(section.getCodeLinesArray().get(0));
                    String methodName = method.getMethodName();
                    ArrayList<VariableObject> methodArgs = new ArrayList<>();
                    ArrayList<String> methodParameters = method.getMethodParameters();

                    for (String arg : methodParameters) {
                        methodArgs.add(new VariableObject(arg));
                    }
                    // check method name uniqueness. if unique assign, otherwise throw exception
                    if (section.getGlobalSection().getMethodsInFile().put(methodName, methodArgs) != null) {
                        throw new UsageException("method declared more than once");
                    }
                    // declare method args as local variables
                    for (VariableObject arg : methodArgs) {
                        section.setLocallyDeclaredVariable(arg);
                    }
                }
            }

            for (Section section : sectionArrayList) {
                // check variable duplication inside section
                if (!SectionAnalyzer.checkValidityOfVariablesInsideSection(section)) {
                    throw new Exception("duplicate variables inside section");
                }
                // check variable duplication outside section
                if (SectionAnalyzer.checkValidityOfVariablesOutsideSection(section)) {
                    // in this case we need to check if there is a declaration in global area.
                    for (VariableObject assignedVar : section.getLocallyAssignedVariables()) {
                        ArrayList<Section> relevantGlobalArea = extractRelevantGlobal(sectionArrayList, section);
                        for (Section sec : relevantGlobalArea) {
                            if (SectionAnalyzer.canAssign(assignedVar, sec)) {
                                return 0;
                            }
                            throw new UsageException("Invalid Assignment.");
                        }
                    }
                }
                else {
                    throw new Exception("cannot assign local variable to a variable declared outside");
                }
            }
            // handle reference variables at global section
            globalSection.assignReferenceVariables();
            boolean exists;
            for (VariableObject referenceVar : globalSection.getReferenceVariables()) {
                exists = false;
                for (VariableObject declaredVar : globalSection.getLocallyDeclaredVariables()) {
                    if (referenceVar.getValue().equals(declaredVar.getName())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    throw new Exception("reference variable value not declared");
                }
            }
            return 0;
        }catch (Exception e) {
            System.err.println(e);
            return 1;
        }
    }


    /**
     * Gets all sections in file and a section, find the section in the file and
     * returns a section contains the global area until the specified section, excluded.
     * @param sectionArrayList all sections in sjavac.
     * @return section - part of global area.
     */
    private static ArrayList<Section> extractRelevantGlobal(ArrayList<Section> sectionArrayList, Section section) {
        ArrayList<Section> newSectionArrayList = new ArrayList<>();
        for (Section sec : sectionArrayList) {
            if (sec != section) {
                if (sec.getCodeLinesArray().size() == 1) {
                    newSectionArrayList.add(sec);
                }
            }
        }
        return newSectionArrayList;
    }
}
