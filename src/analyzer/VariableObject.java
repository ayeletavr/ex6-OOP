package analyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableObject {
    private boolean isFinal;
    private String type; // is allowed to be null - variable assignment
    private String name;
    private String value; // is allowed be null - declaration without assignment

    /**
     * variable object constructor
     * (final )type name( = value);
     */
    public VariableObject(String s) {
        this.assignFinal(s);
        if (s.startsWith(" ")) {
            s = s.substring(1);
        }
        s = s.replace(";",""); // remove ;
        String[] args = s.split("\\s=\\s|\\s"); // {(final, )type, name(, =, value;)}
        if(this.isFinal) {
            if (args.length == 3) { // final type name
                this.type = args[1];
                this.name = args[2];
            }
            if( args.length == 4) { // final type name value
                this.type = args[1];
                this.name = args[2];
                this.value = args[3];
            }
        }
        else {
            if (args.length == 2) {
                if (args[0].matches("int|double|String|boolean|char")) {// type name
                    this.type = args[0];
                    this.name = args[1];
                }
                else { // name value
                    this.name = args[0];
                    this.value = args[1];
                }
            }
            if (args.length == 3) { // type name value
                this.type = args[0];
                this.name = args[1];
                this.value = args[2];
            }
        }
    }


    /**
     *
     * @param variableString a string that represents a variable.
     */
    private void assignFinal(String variableString) {
        this.isFinal = variableString.startsWith("final");
    }


    /**
     *
     * @return is final
     */
    public boolean getIsFinal() {
        return this.isFinal;
    }


    /**
     *
     * @return type
     */
    public String getType() {
        return this.type;
    }


    /**
     *
     * @return name
     */
    public String getName() {
        return this.name;
    }


    /**
     *
     * @return value
     */
    public String getValue() {
        return this.value;
    }


    /**
     * check if value fits variables type
     * @return true if description is met, and false otherwise
     */
    public boolean setValue(String value) {
        if (this.type.equals("int")) {
            Pattern intRegEx = Pattern.compile("[0-9]+");
            Matcher intMatcher = intRegEx.matcher(value);
            if (intMatcher.matches()) {
                this.value = value;
                return true;
            }
        }
        if (this.type.equals("double")) {
            Pattern doubleRegEx = Pattern.compile("[0-9]+\\.[0-9]+");
            Matcher doubleMatcher = doubleRegEx.matcher(value);
            if (doubleMatcher.matches()) {
                this.value = value;
                return true;
            }
        }
        if (this.type.equals("String")) {
            Pattern StringRegEx = Pattern.compile("\\w+");
            Matcher StringMatcher = StringRegEx.matcher(value);
            if (StringMatcher.matches()) {
                this.value = value;
                return true;
            }
        }
        if (this.type.equals("boolean")) {
            Pattern booleanRegEx = Pattern.compile("true|false|[0-9]+(\\.[0-9]+)");
            Matcher booleanMatcher = booleanRegEx.matcher(value);
            if (booleanMatcher.matches()) {
                this.value = value;
                return true;
            }
        }
        if (this.type.equals("char")) {
            Pattern charRegEx = Pattern.compile("('.')");
            Matcher charMatcher = charRegEx.matcher(value);
            if (charMatcher.matches()) {
                this.value = value;
                return true;
            }
        }
        return false;
    }
}
