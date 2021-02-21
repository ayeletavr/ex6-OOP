package analyzer;

import sjavaparser.Section;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentLineRegEx {

    /**
     * documentation line RegEx starts with //
     * @param s a code section
     * @return true if matches, false otherwise
     */
    public static boolean documentationRegEx(Section s) {
        Pattern docLineP = Pattern.compile("\\s*(//)(.*)"); // ^ is a boundary matcher for beginning of line
        Matcher docLineM = docLineP.matcher(s.getCodeLinesArray().get(0));
        return docLineM.matches() && (s.getCodeLinesArray().size() == 1); // a comment block consists of 1 line
    }
}
