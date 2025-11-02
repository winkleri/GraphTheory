import org.graphstream.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.MultiGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Main {
    //^...$ line anchors ensure entire line matches
    //[p{L}0-9]+ letters of all languages/digits (node name): min. one letter
    //\s* account for whitespace: {0,n} -> *
    //->|-- directed/undirected edge: one per line
    //(:\d+)? number as edge weight {0,1} -> ?
    private final Pattern pattern = Pattern.compile("^([\\p{L}0-9])+\\s*(->|--)\\s*([\\p{L}0-9])+(:\\d+)?;$");

    public ArrayList<File> checkFiles() {
        ArrayList<File> files = new ArrayList<>();
        File dir = new File("src/main/java/graphs");
        for(File f : Objects.requireNonNull(dir.listFiles())) {
            if(f.getName().endsWith(".gka")) files.add(f);
        }
        return files;
    }

    public void parseFiles(ArrayList<File> files) {
        for(File graphFile : files) {
            parserHelper(graphFile);
        }
    }

    //improve readability
    public boolean parserHelper(File file) {
        Scanner parser;
        try {
            parser = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return false;
        }

        while(parser.hasNextLine()) {
            String line = parser.nextLine();
            Matcher matcher = pattern.matcher(parser.nextLine());
            if(matcher.matches()) {
                Node n = new Node();
            }

        }
    }

    public Graph createUndirected() {


        return null;
    }

}
