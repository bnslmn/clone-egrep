import java.util.HashMap;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class Reader {
    public static void grep(String regEx, String fileName) {
        if (isRegex(regEx)) {
            RegExTree tree = null;
            try {
                tree = Parser.parse(regEx);
                System.out.println("  >> Tree result: " + tree.toString() + ".");
            } catch (Exception e) {
                System.err.println("  >> ERROR: syntax error for regEx \"" + regEx + "\".");
            }

            NDFAutomaton ndfa = new NDFAutomaton(tree);
            System.out.println("  >> NDFA construction:\n\nBEGIN NDFA\n" + ndfa.toString() + "END NDFA.\n");
            DFAutomaton dfa = new DFAutomaton(ndfa);
            System.out.println(" >> ...");
            System.out.println(" >> Parsing completed.\n");

            if (fileName.length() < 1) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("  >> Please enter a valid file name: ");
                fileName = scanner.next();
                scanner.close();
            }
            try {
                HashMap<Integer, String> result = readFromFile(fileName, dfa);
                System.out.println(resultLinesToString(result));
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found.");
                return;
            } catch (Exception e) {
                System.out.println(e);
                return;
            }
        } else {
            if (fileName.length() < 1) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("  >> Please enter a valid file name: ");
                fileName = scanner.next();
                scanner.close();
            }
            try {
                HashMap<Integer, String> result = readFromFileKmp(fileName, regEx);
                System.out.println(resultLinesToString(result));
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found.");
                return;
            } catch (Exception e) {
                System.out.println(e);
                return;
            }
        }
    }

    private static Boolean isRegex(String regEx) {
        return (regEx.contains("(") && regEx.contains(")")) || regEx.contains("|") || regEx.contains(".")
                || regEx.contains("*");
    }

    private static HashMap<Integer, String> readFromFile(String fileName, DFAutomaton dfa) throws Exception {
        FileReader file = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(file);
        String line;
        HashMap<Integer, String> resultLines = new HashMap<Integer, String>();
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            String[] splitedLine = line.split(" ");
            Boolean match = false;
            for (String word : splitedLine) {
                int nextTransition = 0;
                for (int i = 0; i < word.length(); i++) {
                    char c = word.charAt(i);
                    if ((int) c > 256 || (int) c < 0)
                        continue;
                    if (!dfa.getTransitionTable()[nextTransition][(int) c].isEmpty()) {
                        nextTransition = dfa.getKeyFromValues(dfa.getTransitionTable()[nextTransition][(int) c]);
                        if (nextTransition == -1)
                            nextTransition = 0;
                    } else if (dfa.getFinals().get(nextTransition) == 1) {
                        match = true;
                        break;
                    } else {
                        nextTransition = dfa.getKeyFromValues(dfa.getTransitionTable()[0][(int) c]);
                        if(nextTransition == -1)
                            nextTransition = 0;
                    }
                }
                if (dfa.getFinals().get(nextTransition) == 1) // End of word.
                    match = true;
            }
            if (match)
                resultLines.put(lineNumber, line);
        }
        reader.close();
        return resultLines;
    }

    private static HashMap<Integer, String> readFromFileKmp(String fileName, String regEx) throws Exception {
        FileReader file = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(file);
        String line;
        HashMap<Integer, String> resultLines = new HashMap<Integer, String>();
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            ArrayList<String> matchs = new ArrayList<String>();
            ArrayList<Integer> indexes = Kmp.kmp(regEx, line);
            if (!indexes.isEmpty()) {
                for (int i : indexes) {
                    matchs.add(line.substring(i, i + regEx.length()));
                }
                for (String match : matchs) {
                    if (!match.isBlank())
                        line = line.replace(match, "\u001B[33m" + match + "\u001B[0m");
                }
                resultLines.put(lineNumber, line);
            }
        }
        reader.close();
        return resultLines;
    }

    private static String resultLinesToString(HashMap<Integer, String> resultLine) {
        if (resultLine.size() == 0)
            return "No result found";
        String str = "Result lines : " + resultLine.size() + " lines.\n";
        ArrayList<Integer> sortedKeySet = new ArrayList<>();
        sortedKeySet.addAll(resultLine.keySet());
        Collections.sort(sortedKeySet);
        for (Integer key : sortedKeySet)
            str += "[" + key.toString() + "] : " + resultLine.get(key) + "\n";
        return str;
    }
}
