package oop.ex6.main;

import analyzer.VariableObject;
import sjavaparser.Section;
import java.util.ArrayList;
import java.util.HashMap;

public class GlobalSection {

    private ArrayList<VariableObject> locallyDeclaredVariables;
    private ArrayList<VariableObject> locallyAssignedVariables;
    private ArrayList<VariableObject> referenceVariables;
    private HashMap<String,ArrayList<VariableObject>> methodsInFile;
    private ArrayList<Section> sectionArrayList;

    /**
     * Constructor for a non-empty line
     */
    GlobalSection(ArrayList<Section> sectionArrayList) {
        this.sectionArrayList = sectionArrayList;
        this.methodsInFile = new HashMap<>();
        this.locallyDeclaredVariables = new ArrayList<>();
        this.locallyAssignedVariables = new ArrayList<>();
        this.referenceVariables = new ArrayList<>();
        for (Section section : sectionArrayList) {
            section.setGlobalSection(this);
        }
    }

    /**
     *
     * @return get section array list
     */
    public ArrayList<Section> getSectionArrayList() {
        return this.sectionArrayList;
    }



    /**
     * getLocallyDeclaredVariables
     */
    public ArrayList<VariableObject> getLocallyDeclaredVariables() {
        return this.locallyDeclaredVariables;
    }


    /**
     * getLocallyAssignedVariables
     */
    public ArrayList<VariableObject> getLocallyAssignedVariables() {
        return this.locallyAssignedVariables;
    }


    /**
     * Sets a specified arg to section local variable array.
     */
    public void setAsDeclaredVar(VariableObject var) {
        locallyDeclaredVariables.add(var);
    }

    public void setAsAssignedVar(VariableObject var) {
        locallyAssignedVariables.add(var);
    }

    public void setAsReferenceVar(VariableObject var) {
        referenceVariables.add(var);
    }


    /**
     * getter
     * @return getMethodsInFile
     */
    public HashMap<String,ArrayList<VariableObject>> getMethodsInFile () {
        return this.methodsInFile;
    }

    public void assignReferenceVariables() {
        // declared reference
        for (VariableObject var : this.getLocallyDeclaredVariables()) {
            if (var.getValue() != null) {
                if (var.getValue().matches("[a-zA-Z]+\\w*|[_]+\\w+")) {
                    if (!var.getValue().matches("true|false")) {
                        this.referenceVariables.add(var);
                    }
                }
            }
        }
        // assigned reference
        for (VariableObject var : this.getLocallyAssignedVariables()) {
            if (var.getValue().matches("[a-zA-Z]+\\w*|[_]+\\w+")) {
                if (!var.getValue().matches("true|false")) {
                    this.referenceVariables.add(var);
                }
            }
        }
    }

    public ArrayList<VariableObject> getReferenceVariables() {
        return this.referenceVariables;
    }
}
