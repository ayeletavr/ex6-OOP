package analyzer;

import sjavaparser.Section;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReturnStatementRegEx {

    public static boolean returnStatementRegEx(Section s) {
        // variable declaration should be done in a single line
        if (s.getCodeLinesArray().size() != 1) {
            return false;
        }
        //return line regex (not a declaration but it's a one-line section so it's a candidate to be one)
        Pattern returnLineRegEx = Pattern.compile("(\\s*(return;))");
        Matcher returnLineMatcher = returnLineRegEx.matcher(s.getCodeLinesArray().get(0));
        return returnLineMatcher.matches();
    }
}
