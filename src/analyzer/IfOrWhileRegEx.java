package analyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfOrWhileRegEx {

    private String condition;

    /** constructor. */
    public  IfOrWhileRegEx(String ifOrWhileLine) {
        this.condition = extractCondition(ifOrWhileLine);
    }

    public String getCondition() {return this.condition;}

    /**
     * gets an if or while line, and returns only the condition as a string.
     */
    private String extractCondition(String line) {
        String[] separated = line.split("[(]");
        /* remove ) { from last arg */
        String lastElement = separated[separated.length - 1];
        separated[separated.length - 1] = lastElement.substring(0, lastElement.length() - 3);
        return separated[separated.length - 1];
    }

    /** RegEx for condition. */
    public static boolean ifOrWhileConditionRegEx(IfOrWhileRegEx ifOrWhile) {
        Pattern condition = Pattern.compile("\\s*-?([0-9]+\\.[0-9]+)?;");
        Matcher conditionMatcher = condition.matcher(ifOrWhile.condition);
        return conditionMatcher.matches();
    }
}
