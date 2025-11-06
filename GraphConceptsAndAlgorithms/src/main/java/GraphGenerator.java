import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;

import java.util.ArrayList;


public class GraphGenerator {
    private final ArrayList<Graph> graphs = new ArrayList<>();
    private static int graphId = 0;

    /**
     * returns a mutable version of the graph list of the generator used for parsing purposes
     *
     * @return mutable graph list
     */
    public ArrayList<Graph> getMutableGraphs() {
        return graphs;
    }

    /**
     * returns an immutable version of the graph list of the generator used for UI purposes
     *
     * @return immutable graph list
     */
    public ArrayList<Graph> getImmutableGraphs() {
        //copy constructor to avoid meddling with original
        return new ArrayList<>(graphs);
    }

    /**
     * creates a graph and links it with key identifiers (ID and filename)
     *
     * @param fileName a String with the .gka file this graph belongs to
     * @return a default Graph
     */

    public Graph createNewGraph(String fileName) {
        //unique identifier generated when graph is created
        ++graphId;
        Graph graph = new DefaultGraph("graph:" + graphId);
        graph.setAttribute("sourceFile", fileName);
        return graph;
    }

    /**
     * This method checks if the node that is passed is already part of the graph.
     *
     * @param g    is the current graph
     * @param node is the nodeID that is to be checked.
     * @return true if nodeID already exists in graph.
     */
    public boolean containsNodeId(Graph g, String node) {
        return g.nodes()
                .anyMatch(n ->
                        n.getId()
                                .equals(node));
    }

    /**
     * This method adds nodes, edges and edge weight to the current graph after checking the suitability.
     *
     * @param g          is the current graph.
     * @param source     is the source node.
     * @param target     is the target node.
     * @param directed   is the state of an edge.
     * @param edgeWeight is an integer value associated with an edge.
     */
    public void updateGraph(Graph g, String source, String target, String directed, String edgeLabel, Integer edgeWeight) {
        //generate source
        generateNode(g, source);
        //generate target
        generateNode(g, target);
        //generate edge
        generateEdge(g, source, target, directed, edgeLabel, edgeWeight);
    }

    public void generateNode(Graph g, String name) {
        //if nodeID is not found add it
        if (!containsNodeId(g, name)) {
            g.addNode(name);
            g.getNode(name).setAttribute("ui.label", name);
        }
    }

    /**
     * This method checks if the input is valid for generating an edge and if so generates it
     *
     * @param g          graph to add edge to
     * @param name       name of generated edge
     * @param source     source
     * @param target     target node
     * @param directed   if edge is directed or not
     * @param edgeLabel  a custom name for an edge
     * @param edgeWeight integer value storing an edge weight
     */
    public void generateEdge(Graph g, String source, String target, String directed, String edgeLabel, Integer edgeWeight) {
        //don't generate duplicates
        String name = source + directed + target;
        String reversed = target + directed + source;

       //undirected duplicate checker
        if (g.getEdge(name) != null || g.getEdge(reversed)!= null) {
            System.out.printf("Skipped: s=%s %s t=%s (duplicate edge)\n", source, directed, target);
            return;
        }

        //adds attributes in order: label, source, target, direction (bool)
        g.addEdge(name, g.getNode(source), g.getNode(target), checkDirected(directed));
        Edge currentEdge = g.getEdge(name);
        currentEdge.setAttribute("ui.label", name);

        //add attributes if they exist
        if (edgeWeight != null) currentEdge.setAttribute("weight", edgeWeight);
        if (edgeLabel != null) currentEdge.setAttribute("label", edgeLabel);
    }

    /**
     * @param graph
     * @return true if graph is empty
     */
    public boolean isEmpty(Graph graph) {
        return graph.getNodeCount() == 0;
    }

    /**
     * This method checks if the input is a directed or an undirected edge.
     *
     * @param input Either directed (->) or undirected (--)
     * @return returns true if String is "->" all other inputs are deemed false
     */
    public boolean checkDirected(String input) {
        return input.equals("->");
    }
}