import oop.ex6.main.Sjavac;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tests {


    public static void main(String[] args) throws Exception {

        Pattern p1 = Pattern.compile("java");

        Pattern p2 = Pattern.compile("[0-9]");

        Sjavac tester = new Sjavac();



        File dir = new File("G:\\University\\Computer science\\2nd year\\67125 Object Oriented Programming\\week 11 12\\ex6\\updatedtestdirectory");
        File[] directoryFiles = dir.listFiles();
        String[] currentargs = new String[2];
        int actualResult = 0;
        int expectedResult;
        String currentLine;
        int counter = 0;
        int testsPassed = 0;
        File testResultsFile = new File("G:\\University\\Computer science\\2nd year\\67125 Object Oriented Programming\\week 11 12\\ex6\\updatedtestresults.txt");
        List contents = Files.readAllLines(Paths.get(testResultsFile.getAbsolutePath()));
        for (File file : directoryFiles){
            if (counter < contents.size()) {
                currentLine = contents.get(counter).toString();
                Matcher javaMatch = p1.matcher(currentLine);
                javaMatch.find();
                currentLine = currentLine.substring(javaMatch.end());
                Matcher resultMatch = p2.matcher(currentLine);
                resultMatch.find();
                currentLine = currentLine.substring(resultMatch.start(), resultMatch.end()).trim();

                expectedResult = Integer.parseInt(currentLine);
                currentargs[0] = file.getAbsolutePath();
                currentargs[1] = file.getName();
                actualResult = tester.testRun(currentargs);

                if (actualResult != expectedResult) {
                    System.out.println("====================================");
                    System.out.println("Failed the test: " + file.getName() + ".");
                    System.out.println("Expected Result: " + expectedResult);
                    System.out.println("Actual Result: " + actualResult);
                    System.out.println("Informative message: " + contents.get(counter).toString());
                    System.out.println("====================================");
                }
                else{
                   testsPassed++;
                }

                currentargs = new String[2];
                counter++;
            }
        }
        System.out.println("Results: Passed "+testsPassed+ " tests out of a total of "+ counter + "tests.");
    }
}
