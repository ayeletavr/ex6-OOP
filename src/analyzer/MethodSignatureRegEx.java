package analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodSignatureRegEx {

    private boolean hasVoid;
    private ArrayList<String> signatureArray;
    private String methodName;
    private ArrayList<String> methodParameters; // shouldn't it be of type String? (originally was "Object")
    public static final String[] reservedWords = {"int", "double", "boolean", "char", "String", "void", "final",
            "if", "while", "true", "false", "return"};


    /**
     * Constructor
     * @param methodSignature method's signature string
     */
    public MethodSignatureRegEx(String methodSignature) {
        this.signatureArray = splitNameArgs(methodSignature);
        if (this.methodName == null) {
            this.assignVoid();
            this.assignMethodName();
            this.assignMethodParameters();
        }
    }

    /** Method name getter. */
    public String getMethodName() {return this.methodName;}
    /** Method Arguments getter. */
    public ArrayList<String> getMethodParameters() {
        if (this.methodParameters.isEmpty()) {
            this.assignMethodParameters();
        }
        return this.methodParameters;}
    /** Has void getter. */
    public boolean getHasVoid() {
        return this.hasVoid;}


    /**
     * Helper to construct a new method object.
     * @param methodSignature the raw line that represents a method signature.
     * @return a String ArrayList contains the method name in index 0 (or void), and the args comes next.
     */
    private ArrayList<String> splitNameArgs(String methodSignature) {
        String[] separated = methodSignature.split("[(,]"); // equal to "\\(|," : "(" or ","
        /* remove ) { from last arg */
        String lastElement = separated[separated.length - 1];
        separated[separated.length - 1] = lastElement.substring(0, lastElement.length() - 3);
        /* add "void" at index 0.
        add method name at index 1*/
        ArrayList<String> finalList = new ArrayList<>();
        finalList.add(separated[0].substring(0, 4));
        finalList.add(separated[0].substring(5));
        /* the first element is dealt with  manually */
        finalList.addAll(Arrays.asList(separated).subList(1, separated.length));
        finalList.remove("");
        return finalList;
    }


    /**
     * method signature RegEx. structure: "void methodName(type parameter1, type parameter, ...) {"
     * method name starts either with lower case or upper case letter
     * @param method method signature regex object
     * @return true is structure is correct
     */
    static boolean methodSignatureRegEx(MethodSignatureRegEx method) {
        if (Arrays.asList(reservedWords).contains(method.methodName)) {
            return false;
        }
        Pattern methodName = Pattern.compile("_?[a-zA-Z]+\\w*\\s*");
        Matcher methodNameMatcher = methodName.matcher(method.methodName);
        if (!methodNameMatcher.matches()) {
            return false;
        }
        Pattern methodParameter = Pattern.compile("\\s*(int|double|boolean|char|String)\\s*\\w+"); // type space name
        Matcher methodParameterMatcher;
        for (String arg : method.methodParameters) { // check each method argument
            methodParameterMatcher = methodParameter.matcher(arg);
            if(!methodParameterMatcher.matches()) {
                return false;
            }
        }
        return true;
    }

    private void assignVoid() {
        this.hasVoid = this.signatureArray.get(0).equals("void");
    }

    private void assignMethodName() {
        if (this.signatureArray.get(0).equals("void")) {
            this.methodName = this.signatureArray.get(1);
        }
        else {
            this.methodName = this.signatureArray.get(0);
        }
    }

    private void assignMethodParameters() {
        if (this.signatureArray.get(0).equals("void")) {
            this.methodParameters = new ArrayList<>();
            for (int i = 2; i < this.signatureArray.size() ; i++) { // the method has parameters
                this.methodParameters.add(this.signatureArray.get(i)); // use to be: .add(i)
            }
        }
        else {
            this.methodParameters = new ArrayList<>();
            for (int i = 2; i < this.signatureArray.size(); i++) {
                this.methodParameters.add(this.signatureArray.get(i));
            }
        }
    }
}
