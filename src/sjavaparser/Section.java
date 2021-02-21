package sjavaparser;

import analyzer.VariableObject;
import analyzer.VariableRegEx;
import oop.ex6.main.GlobalSection;

import java.util.ArrayList;

/**This class represents a section in sJava file.
 * A section can be a one-line code, or a local space.
 */
public class Section extends SectionArrayFactory {

    private ArrayList<String> codeLinesArray;
    private ArrayList<VariableObject> locallyDeclaredVariables = new ArrayList<>();
    private ArrayList<VariableObject> locallyAssignedVariables = new ArrayList<>();
    private ArrayList<VariableObject> referenceVariables = new ArrayList<>();
    private ArrayList<Section> sectionsInSection;
    private String sectionType;
    private Section outerSection;
    private GlobalSection globalSection;

    /**
     * Constructor for a non-empty line
     */
    Section(ArrayList<String> arrayOfStrings, GlobalSection globalSection) {
        codeLinesArray = new ArrayList<>();
        this.globalSection = globalSection;
        setCodeLinesArray(arrayOfStrings);
        if (codeLinesArray.size() > 1) {
            /* it's important that sectionsInSection is initialized before methods that set variables, because  they
            use this data member
             */
            this.sectionsInSection = new ArrayList<>();
            this.sectionsInSection = this.assignSectionsInSection();
            this.locallyDeclaredVariables = new ArrayList<>();
            this.locallyAssignedVariables = new ArrayList<>();
            this.referenceVariables = new ArrayList<>();
        }
    }


    /**
     * Constructor for an empty line.
     */
    public Section() {
        codeLinesArray = new ArrayList<>();
    }

    /**
     * constructor for one line String.
     */
    public Section(String line) {
        codeLinesArray = new ArrayList<>();
        codeLinesArray.add(line);
    }

    /**
     * codeLinesArray getter.
     */
    public ArrayList<String> getCodeLinesArray() {
        return codeLinesArray;
    }


    /**
     * Adds code lines to toe codeLinesArray.
     */
    public void setCodeLinesArray(ArrayList<String> arrayOfStrings) {
        codeLinesArray.addAll(arrayOfStrings);
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
     * set local variables is a listArray according to type (declaration/assignment)
     */
    private void setLocalVariables() {
        VariableObject variable;
        if (getCodeLinesArray().size() == 1) {
            if (VariableRegEx.variableDeclarationRegEx(this) != 0) { //XXX
                variable = new VariableObject(this.codeLinesArray.get(0));
                if (this.outerSection != null) {
                    this.outerSection.locallyDeclaredVariables.add(variable);
                }

            }
            if (VariableRegEx.variableAssignmentRegEx(this) != 0) {
                variable = new VariableObject(this.codeLinesArray.get(0));
                if (this.outerSection != null) {
                    this.locallyAssignedVariables.add(variable);
                }
            }
        }
        this.sectionsInSection = getSectionsInSection();
        if (this.sectionsInSection != null) {
            for (Section section : this.sectionsInSection) {
                if (VariableRegEx.variableDeclarationRegEx(section) != 0) {
                    variable = new VariableObject(section.codeLinesArray.get(0));
                    this.locallyDeclaredVariables.add(variable);
                }
                if (VariableRegEx.variableAssignmentRegEx(section) != 0) {
                    variable = new VariableObject(section.codeLinesArray.get(0));
                    this.locallyAssignedVariables.add(variable);
                }
            }
        }
    }

    /** Sets a specified arg to section local variable array. */
    public void setLocallyDeclaredVariable(VariableObject argVar) {
        locallyDeclaredVariables.add(argVar);
    }

    /** Sets a specified arg to section local variable array. */
    public void setLocallyAssignedVariables(VariableObject argVar) {
        locallyAssignedVariables.add(argVar);
    }

    /** Sets a specified arg to section local variable array. */
    public void setReferenceVariable(VariableObject argVar) {
        referenceVariables.add(argVar);
    }


    /**
     * Iterates over brackets in section to find all inner sections.
     */
    private ArrayList<Section> assignSectionsInSection() {

        int openBracketIndex = 1; // so we don't get the global section itself
        int down = 0;
        int up = 0;
        Section section;

        for (int i = openBracketIndex; i < getCodeLinesArray().size(); i++) {  // find first opening brackets
            if (getCodeLinesArray().get(i).matches(".*[{]")) {
                openBracketIndex = i;
                // no need to write here down++, it is done for this line in the next loop.
                break;
            }
        }

        for (int j = 0; j < getCodeLinesArray().size(); j++) {
            for (int i = openBracketIndex; i < getCodeLinesArray().size(); i++) {
                if (getCodeLinesArray().get(i).matches(".*[{]")) {
                    down++;
                }
                if (getCodeLinesArray().get(i).matches(".*[}]")) {
                    up++;
                }
                if (down == up) {
                    section = new Section(SectionArrayFactory.listToArrayList(getCodeLinesArray().
                            subList(openBracketIndex, i + 1)), this.globalSection); // call line constructor
                    section.setOuterSection(this);
                    if (up == 0) { // single line subsection
                        if (VariableRegEx.variableDeclarationRegEx(section) != 0) {
                            section.setOuterSection(this);
                            this.setLocallyDeclaredVariable(new VariableObject(section.getCodeLinesArray().get(0)));
                        }
                    }
                    this.sectionsInSection.add(section);
                    up = 0;
                    down = 0;
                    openBracketIndex = i + 1;
                }
            }
        }
        return this.sectionsInSection;
    }

    /**
     * getter
     * @return sectionsInSection
     */
    public ArrayList<Section> getSectionsInSection() {
        return this.sectionsInSection;
    }

    /**
     * type setter
     * @param type type
     */
    public void setType(String type) {
        this.sectionType = type;
    }


    /**
     * type getter
     * @return type
     */
    public String getType() {
        return this.sectionType;
    }

    public void setOuterSection(Section outer) {this.outerSection = outer;}
    public Section getOuterSection() {return this.outerSection;}

    public void setGlobalSection(GlobalSection globalSection) {
        this.globalSection = globalSection;
    }

    public GlobalSection getGlobalSection() {
        return this.globalSection;
    }



   public void assignReferenceVariables() {
        // declared reference
        for (VariableObject var : this.getLocallyDeclaredVariables()) {
            if (var.getValue() != null) {
                if (var.getValue().matches("[a-zA-Z]+\\w*|[_]+\\w+")) {
                    if (!var.getValue().matches("true|false")) { // answer scenario: int a = a;
                        this.referenceVariables.add(var);
                    }
                }
            }
        }
        // assigned reference
        for (VariableObject var : this.getLocallyAssignedVariables()) {
            if (var.getValue().matches("[a-zA-Z]+\\w*|[_]+\\w+")) {
                if (!var.getValue().matches("true|false")) { // solves scenario int a = a;
                    this.referenceVariables.add(var);
                }
            }
        }
    }


    /**
     * get reference variables
     * @return reference variables
     */
    public ArrayList<VariableObject> getReferenceVariables() {
        return this.referenceVariables;
    }
}
