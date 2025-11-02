import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

    public class GraphGenerator {
        private final Pattern pattern = Pattern.compile("^([\\p{L}0-9])+\\s*(->|--)\\s*([\\p{L}0-9])+\\s*(:\\d+)?\\s*;$");
        private final List<Graph> graphs = new ArrayList<>();
        private static int graphId = 0;
        private static int edgeId = 0;

        /* Regular Expression:
        ^...$ line anchors ensure entire line matches
        \s* account for whitespace
        Group 1/3 - responsible for nodes:  [\\p{L}0-9] character set containing digits/letters from all languages
        Group 2 - responsible for direction: ->|-- directed/undirected
        Group 4 - responsible for edge weight (:\d+)? number as edge weight {0,1}
        */


        /**
         * This method helps to extract files from a fixed directory (dir)
         * Strings are matched via the Pattern class.
         * @return ArrayList of files ending with .gka in the specified path
         */
        public ArrayList<File> checkFiles() {
            ArrayList<File> files = new ArrayList<>();
            File dir = new File("src/main/java/graphs");
            for(File f : Objects.requireNonNull(dir.listFiles())) {
                if(f.getName().endsWith(".gka")) files.add(f);
            }
            return files;
        }

        public ArrayList<Graph> getGraphs() {
            //copy constructor to avoid meddling with original
            return new ArrayList<>(graphs);
        }

        /**
         * This method is used to parse a list of graph files
         * Strings are matched via the Pattern class.
         * @param files List of files to be parsed for graphs (sets of vertices and edges)
         */
        public void initializeScanner(ArrayList<File> files) {
            Scanner parser;

            for(File graphFile : files) {
                try {
                    parser = new Scanner(graphFile);
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                    return;
                }
                fileParser(parser);
                parser.close();
            }

        }

        //improve readability

        /**
         * This method helps to extract information from given .gka files, while using RegEX.
         * Strings are matched via the Pattern class.
         * @param parser given file to extract information from.
         */
        public void fileParser(Scanner parser) {
            ++graphId;
            Graph graph = new DefaultGraph("graph:" + graphId);
            edgeId = 1;
            while(parser.hasNextLine()) {
                Matcher matcher = pattern.matcher(parser.nextLine());
                if(matcher.matches()) {
                    final String source = matcher.group(1);
                    final String target = matcher.group(3);
                    final String directed = matcher.group(2);
                    final Integer edgeWeight =  intParser(matcher.group(4));
                    updateGraph(graph, source, target, directed, edgeWeight);
                    edgeId++;
                }
            }
            graphs.add(graph);
        }

        /**
         * This method checks if the node that is passed is already part of the graph.
         * @param g is the current graph
         * @param node is the nodeID that is to be checked.
         * @return true if nodeID already exists in graph.
         */
        public boolean containsNodeId(Graph g, String node) {
            return  g.nodes()
                    .anyMatch(n ->
                            n.getId()
                                    .equals(node));
        }

        /**
         * This method adds nodes, edges and if applicable edge weight to the current graph.
         * @param g is the current graph.
         * @param source is the source node.
         * @param target is the target node.
         * @param directed is the state of an edge.
         * @param edgeWeight is an integer value associated with an edge.
         */
        public void updateGraph(Graph g, String source, String target, String directed, Integer edgeWeight) {
            //source
            if(!containsNodeId(g, source)) g.addNode(source);
            //target
            if(!containsNodeId(g, target)) g.addNode(target);
            //edge
            String edgeName = "e"+edgeId;
            generateEdge(g, edgeName, source, target, directed, edgeWeight);
        }


        public void generateEdge(Graph g, String name, String source, String target, String directed, Integer edgeWeight) {
            try {
                g.addEdge(name, g.getNode(source), g.getNode(target), checkDirected(directed));
            } catch (EdgeRejectedException e) {
                return;
            }

            Edge currentEdge = g.getEdge(name);
            if(edgeWeight != null) currentEdge.setAttribute("weight", edgeWeight);
        }

        /**
         * This method checks if the input is a directed or an undirected edge.
         * @param input Either directed (->) or undirected (--)
         * @return returns true if String is "->" all other inputs are deemed false
         */
        public boolean checkDirected(String input) {
            return input.equals("->");
        }

        /**
         * This method parses Integers and treats unexpected behavior accordingly
         * @param input is the String that is to be parsed
         * @return returns the number or Null if the number does not exist
         * */
        public Integer intParser(String input) {
            Integer result;
            try {
                result = Integer.parseInt(input);
            } catch(NumberFormatException e) {
                result = null;
            }
            return result;
        }

}
