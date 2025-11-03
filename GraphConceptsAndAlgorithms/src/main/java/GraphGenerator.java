import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import java.util.ArrayList;


public class GraphGenerator {
        private final ArrayList<Graph> graphs = new ArrayList<>();
        private static int graphId = 0;

        public ArrayList<Graph> getMutableGraphs() {
            return graphs;
        }

        public ArrayList<Graph> getImmutableGraphs() {
            //copy constructor to avoid meddling with original
            return new ArrayList<>(graphs);
        }

        public Graph createNewGraph(String fileName) {
            ++graphId;
            Graph graph = new DefaultGraph("graph:" + graphId);
            graph.setAttribute("sourceFile", fileName);
            return graph;
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
        public void updateGraph(Graph g, String source, String target, String directed, String edgeLabel, Integer edgeWeight) {
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
                    System.out.printf("Skipping duplicate edge: %s -- %s\n", source, target);
                    return;
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



}