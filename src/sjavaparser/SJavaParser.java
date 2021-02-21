package sjavaparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/** This class reads an s-Java file and parse it to a valid array of strings.
 * Each string represents a line in the s-java file.
 */
public class SJavaParser {

    private Scanner reader;

    /**
     * parser constructor
     */
    public SJavaParser(String sJavaFile) throws FileNotFoundException {
        reader = new Scanner(new File(sJavaFile));
    }

    /**
     * reads an sJava file, and parses in into an array of strings.
     * Each string is a line in the sJava file.
     * No validity check in this method.
     * @return all lines a a string listArray
     */
    public ArrayList<String> createStringArray() {
        ArrayList<String> lines = new ArrayList<>();
        while (reader.hasNext()) {
            lines.add(reader.nextLine());
        }
        return reduceSpaces(lines);
    }


    private ArrayList<String> reduceSpaces(ArrayList<String> lines) {
        ArrayList<String> result = new ArrayList<>();
        for (String line : lines) {
            line = line.replaceAll("\\s+", " ");
            result.add(line);
        }
        return result;
    }
}
