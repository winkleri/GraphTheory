import org.graphstream.graph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphParser {
    private final GraphGenerator gg;
    //private final Pattern pattern_bak = Pattern.compile("^([^\\s:;()]+)(?:\\s*(--|->)\\s*([^\\s:;()]+)(?:\\s*\\(([^)]+)\\))?(?:\\s*:\\s*(\\d+(?:\\.\\d+)?))?)?\\s*;?$");

    public GraphParser(GraphGenerator gg) {
        this.gg = gg;
    }

    /**
     * This method helps to extract files from a fixed directory (dir)
     * Strings are matched via the Pattern class.
     *
     * @return ArrayList of files ending with .gka in the specified path
     */
    public ArrayList<File> checkFiles() {
        ArrayList<File> files = new ArrayList<>();
        File dir = new File("C:\\Users\\demyi\\IdeaProjects\\GraphTheory\\GraphConceptsAndAlgorithms\\src\\main\\java\\graphs"); // src/main/java/graphs
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            if (f.getName().endsWith(".gka")) files.add(f);
        }
        return files;
    }

    /**
     * This method is used to parse a list of graph files
     * Strings are matched via the Pattern class.
     *
     * @param file selects a file to be parsed by scanner object
     * @returns the initialized scanner
     */
    private Scanner initializeScanner(File file) {
        Scanner parser = null;
        try {
            parser = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return parser;
    }

    /**
     * This method helps to extract information from given .gka files, while using RegEX and subsequently passes them to a generator method
     * Strings are matched via the Pattern class.
     *
     * @param files list of files to be parsed one by one
     */
    public void fileParser(ArrayList<File> files) {
        for (File file : files) {
            String fileName = file.getName();
            boolean invalidFileContentFlag = true; //assume every file content is corrupted
            Scanner parser = initializeScanner(file);
            if (parser == null) continue;
            //skip file not found exceptions
            System.out.println("--------------");
            System.out.printf("Parsing: %s\n", fileName);
            //if lineParser extracts at least one information fileContentFlag disabled
            invalidFileContentFlag = lineParser(parser, invalidFileContentFlag, fileName);

            if (invalidFileContentFlag) {
                System.out.println("Invalid file content");
                System.out.println("No parsable information detected");
            }
            parser.close();
        }
    }

    /**
     * A parser written for a single .gka file.
     *
     * @param parser                 the initialized parser with a valid .gka file
     * @param invalidFileContentFlag a flag to represent invalid content (corrupted text)
     * @param fileName               (the file name associated with the scanner)
     * @return boolean value. Returns true if no content was parsed in this file
     */
    private boolean lineParser(Scanner parser, boolean invalidFileContentFlag, String fileName) {
        final String node = "(\\w+)";
        final String direction = "(->|--)";
        final String label = "(?:\\s*\\(([^)]+)\\))?";
        //floating point num matchable (cut off at decimal point)
        final String weight = "(?::\\s*(\\d+(?:\\.\\d+)?))?";
        //whitespace
        final String ws = "\\s*";
        final Pattern pattern = Pattern.compile("^" + node + ws + direction + ws + node + label + ws + weight + ws + ";?" + "$");

        Graph graph = gg.createNewGraph(fileName);
        while (parser.hasNextLine()) {
            String line = parser.nextLine().trim();
            //if(line.startsWith(" ")) continue;
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                invalidFileContentFlag = false; // at least one valid information found
                final String source = matcher.group(1);
                final String directed = matcher.group(2);
                final String target = matcher.group(3);
                final String edgeLabel = matcher.group(4);
                final Integer edgeWeight = intParser(matcher.group(5));

                //Only update graph if at least source node dir. and target is known
                if (source != null && directed != null && target != null) {
                    //label and weight are nullable
                    gg.updateGraph(graph, source, target, directed, edgeLabel, edgeWeight);
                }

                parsePrintHelper(source, directed, target, edgeLabel, edgeWeight);
            }
        }
        //mutable array list to expand this with each parsed file
        gg.getMutableGraphs().add(graph);
        if(!gg.isEmpty(graph)) gg.getMutableGraphs().add(graph);
        return invalidFileContentFlag;
    }

    /**
     * This method prints parsing output in a pretty format. It accounts for invalid lines and cases where at least one information could not be parsed properly
     *
     * @param source     source node
     * @param directed   direction of graph
     * @param target     target node
     * @param edgeLabel  label
     * @param edgeWeight weight
     */
    private void parsePrintHelper(String source, String directed, String target, String edgeLabel, Integer edgeWeight) {
        if (edgeLabel == null && edgeWeight == null && source != null && directed != null && target != null) {
            System.out.printf("Parsed: s=%s %s t=%s\n", source, directed, target);
        } else if (edgeLabel != null && edgeWeight != null) {
            System.out.printf("Parsed: s=%s %s t=%s edgeLabel=%s weight=%s%n", source, directed, target, edgeLabel, edgeWeight);
        } else if (edgeLabel != null) {
            System.out.printf("Parsed: s=%s %s t=%s edgeLabel=%s%n", source, directed, target, edgeLabel);
        } else if (edgeWeight != null) {
            System.out.printf("Parsed: s=%s %s t=%s weight=%s%n", source, directed, target, edgeWeight);
        } else {
            System.out.println("Parsed: No parsable information found!"); //there needs to be at least a source, a direction and a target for an edge to be valid
        }
    }


    /**
     * This method parses Integers. If none is found null is returned
     *
     * @param input is the String that is to be parsed
     * @return returns the number or Null if the number does not exist
     *
     */
    private Integer intParser(String input) {
        Integer result = null;
        try {
            result = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            //System.out.println(e.getMessage());
        }
        return result;
    }

}
