package sjavaparser;

import oop.ex6.main.CodeException;
import oop.ex6.main.GlobalSection;
import oop.ex6.main.SyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A factory that creates an array of sections from arrays of strings. */
public class SectionArrayFactory {

    private final static String COMMENT_PREFIX = "//";
    private final static String LINE_SUFFIX = ";";
    private final static String BlOCK_START_SUFFIX = "{";

    /**
     * Helper to createSection. cast List to ArrayList.
     * @param list list to cast
     * @return arrayList
     */
    public static ArrayList<String> listToArrayList(List<String> list) {
        return new ArrayList<>(list);
    }

    /**
     * Creates a single section (section factory).
     * @param arrayToSection arrayList of strings to create a section from.
     * @return a new section.
     */
    public Section createSection(ArrayList<String> arrayToSection, GlobalSection globalSection) {
        if (arrayToSection.size() == 0) {
            return new Section();
        }
        else {
            return new Section(arrayToSection, globalSection);
        }
    }

    /**
     * Creates an array of sections from an arrayList of Strings.
     * @param stringArrayList the raw arrayList.
     * @return an array of sections.
     */
    public ArrayList<Section> createSectionArray(ArrayList<String> stringArrayList, GlobalSection globalSection)
            throws CodeException {
        ArrayList<Integer> lengthsArray = getSectionsLength(stringArrayList);
        ArrayList<Section> sectionArrayList = new ArrayList<>();
        int prevLength = 0;
        for (int length : lengthsArray) {
            List<String> subList = stringArrayList.subList(prevLength, length + prevLength);
            ArrayList<String> arraySublist = listToArrayList(subList);
            sectionArrayList.add(this.createSection(arraySublist, globalSection));
            prevLength += length;

        }
        return sectionArrayList;
    }


    /**
     * Helper to createSectionArray method.
     * Gets a string arrayList, analyses the sections and returns an int array,
     * which each int in is represents the section length.
     * @param stringArrayList string arrayList.
     * @return int array of section lengths.
     */
    public ArrayList<Integer> getSectionsLength(ArrayList<String> stringArrayList) throws CodeException{
        ArrayList<Integer> sectionLengths = new ArrayList<>();
        int i = 0;
        int sectionLength;
        int equalBrackets;
        while (i < stringArrayList.size()) {
            if (stringArrayList.get(i).startsWith(COMMENT_PREFIX) || stringArrayList.get(i).endsWith(LINE_SUFFIX)
                    || stringArrayList.get(i).endsWith(LINE_SUFFIX + " ") || stringArrayList.get(i).equals("")) {
                    // last condition means that if the section is valid, it's a one-line block (// or ; or emptyLine)
                sectionLengths.add(1);
                i++;
                }
            else if (stringArrayList.get(i).endsWith(BlOCK_START_SUFFIX)) { // multiple lines block
                sectionLength = 1;
                equalBrackets = 1;
                Pattern closingBracketPattern = Pattern.compile("\\s*}\\s*");
                Matcher closingBracketMatcher;
                i++; // this line was already accounted for (equalBracket and length are initialized with values of 1)
                while (((equalBrackets != 0) && (stringArrayList.get(i) != null))) {
                    if (stringArrayList.get(i).endsWith(BlOCK_START_SUFFIX)) {
                        equalBrackets += 1;
                    }
                    closingBracketMatcher = closingBracketPattern.matcher(stringArrayList.get(i));
                    if (closingBracketMatcher.matches()) {
                        equalBrackets -= 1;
                    }
                    i++;
                    sectionLength += 1;
                }
                // add section length to lengths list
                if (equalBrackets == 0) {
                    sectionLengths.add(sectionLength);
                }
            }
            else {
                throw new SyntaxException("Syntax Exception");
            }
        }
        return sectionLengths;
    }
}
