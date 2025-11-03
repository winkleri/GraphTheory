import org.graphstream.graph.Edge;
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
        private final String node = "([\\wöäü]+)";
        private final String trailingParentheses = "(?:\\([\\w-]+\\))?";
        private final String direction = "(->|--)";
        private final String weight = "(?::(\\d+);?)?";
        private final String whitespace = "\\s*";
        private final Pattern pattern = Pattern.compile("^" + node + whitespace + direction + whitespace + node + whitespace + trailingParentheses + whitespace + weight + whitespace + "$");
        //private final Pattern pattern = Pattern.compile("(\\p{L}0-9]+)\\s*(->|--)\\s*([\\p{L}0-9]+)(?:\\s*:\\s*(\\d+);)?");
        private final List<Graph> graphs = new ArrayList<>();
        private static int graphId = 0;

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
         * @param file selects a file to be parsed by scanner object
         * @returns the initialized scanner
         */
        public Scanner initializeScanner(File file) {
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
         * @param files list of files to be parsed one by one
         */
        public void fileParser(ArrayList<File> files) {
            for(File file : files) {
                Scanner parser = initializeScanner(file);
                if(parser == null) continue;
                //skip file not found exceptions
                ++graphId;
                Graph graph = new DefaultGraph("graph:" + graphId);
                graph.setAttribute("sourceFile", file.getName());
                System.out.println(file.getName());
                while(parser.hasNextLine()) {
                    String line = parser.nextLine().trim();
                    if(line.startsWith(" ")) continue;
                    Matcher matcher = pattern.matcher(line);
                    if(matcher.matches()) {
                        final String source = matcher.group(1);
                        final String target = matcher.group(3);
                        final String directed = matcher.group(2);
                        final Integer edgeWeight =  intParser(matcher.group(4));
                        System.out.printf("Parsed: %s %s %s weight=%s%n", source, directed, target, edgeWeight);
                        updateGraph(graph, source, target, directed, edgeWeight);
                    }
                }
                graphs.add(graph);
                parser.close();
            }
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
         * This method adds nodes, edges and edge weight to the current graph after checking the suitability.
         * @param g is the current graph.
         * @param source is the source node.
         * @param target is the target node.
         * @param directed is the state of an edge.
         * @param edgeWeight is an integer value associated with an edge.
         */
        public void updateGraph(Graph g, String source, String target, String directed, Integer edgeWeight) {
            //source
            if(!containsNodeId(g, source)) {
                g.addNode(source);
                g.getNode(source).setAttribute("ui.label", source);
            }
            //target
            if(!containsNodeId(g, target)) {
                g.addNode(target);
                g.getNode(target).setAttribute("ui.label", target);
            }
            //edge
            String edgeName = source + directed + target;
            generateEdge(g, edgeName, source, target, directed, edgeWeight);
        }

        /**
        * This method checks if the input is valid for generating an edge and if so generates it
        * @param g graph to add edge to
         * @param name name of generated edge
         * @param source source
         * @param target target node
         * @param directed if edge is directed or not
         * @param edgeWeight integer value storing an edge weight
         */
        public void generateEdge(Graph g, String name, String source, String target, String directed, Integer edgeWeight) {
            if(!checkDirected(directed)) {
                String edgeId1 = source + directed + target;
                String edgeId2 = target + directed + source;
                if(g.getEdge(edgeId2) != null || g.getEdge(edgeId1) !=null ) {
                    System.out.printf("\nSkipping duplicate edge: %s -- %s\n", source, target);
                }
            }
            g.addEdge(name, g.getNode(source), g.getNode(target), checkDirected(directed));
            Edge currentEdge = g.getEdge(name);
            currentEdge.setAttribute("ui.label", name);
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
         * This method parses Integers. If none is found null is returned
         * @param input is the String that is to be parsed
         * @return returns the number or Null if the number does not exist
         * */
        public Integer intParser(String input) {
            Integer result = null;
            try {
                result = Integer.parseInt(input);
            } catch(NumberFormatException e) {
                //System.out.println(e.getMessage());
            }
            return result;
        }


}