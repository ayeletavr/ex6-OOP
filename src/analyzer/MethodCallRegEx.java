package analyzer;

import sjavaparser.Section;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodCallRegEx {

    /**
     * @param s: a section which is a candidate for method call
     * @return true if structure is legal, false otherwise.
     */
    public static boolean methodCallRegEx(Section s) {
        // method call should be done in a single line
        if (s.getCodeLinesArray().size() != 1) {
            return false;
        }
        Pattern methodCallPattern = Pattern.compile("\\s*\\w+\\(\\w*\\);\\s*");
        Matcher methodCallMatcher = methodCallPattern.matcher(s.getCodeLinesArray().get(0));

        return methodCallMatcher.matches();
    }

}
