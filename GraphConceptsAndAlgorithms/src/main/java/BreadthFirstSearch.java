import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;

public class BreadthFirstSearch implements Algorithm {
    private final String sourceNodeID;
    private final String targetNodeID;
    private Graph graph;
    private List<Node> path = Collections.emptyList();

    public BreadthFirstSearch(String sourceNodeID, String targetNodeID) {
        this.sourceNodeID = sourceNodeID;
        this.targetNodeID = targetNodeID;
    }

    /**
     * Returns all neighboring nodes of a given node. (Handles both directed and undirected edges)
     * @param currentNode the node whose parents are to be determined.
     * @return a list of neighbouring nodes
     */
    public List<Node> neighbors(Node currentNode) {
        List<Node> neighbors = new ArrayList<>();

        for(int i = 0; i < currentNode.getDegree(); i++) {
            Edge currentEdge = currentNode.getEdge(i);
            if(currentEdge.isDirected()) {
                // Only consider outgoing edges
                if(currentEdge.getSourceNode().equals(currentNode)) {
                    neighbors.add(currentEdge.getTargetNode());
                }
            } else {
                // For undirected edges, get the opposite node
                neighbors.add(currentEdge.getOpposite(currentNode));
            }
        }
        return neighbors.size() < 2 ? neighbors: neighbors.stream().distinct().toList(); //remove duplicates (multigraphs)
    }

    /**
     * Initialize the algorithm with a specific graph.
     * This method must be called before compute().
     * @param graph the graph on which the BFS will run
     */
    @Override
    public void init(Graph graph) {
        if(graph == null) throw new IllegalArgumentException("Graph is null");
        this.graph = graph;
    }

    /**
     * Executes the Breadth-First Search (BFS) algorithm to find the shortest path
     * from the source node to the target node.
     *
     * @throws IllegalStateException if the source or target node does not exist in graph.
     */
    @Override
    public void compute() {
        Node source = graph.getNode(sourceNodeID);
        Node target = graph.getNode(targetNodeID);

        if(source == null || target == null) {
            throw new IllegalStateException("Source or target node are null");
        }

        Queue<Node> queue = new LinkedList<>();
        Map<Node,Node> parent = new HashMap<>();
        Set<Node> visitedNodes = new HashSet<>();

        visitedNodes.add(source);
        queue.add(source);
        while(! (queue.isEmpty())) {
            Node currentNode = queue.poll();

            if (currentNode.equals(target)) {
                this.path = buildPath(parent, target);
                return;
            }

            for (Node neighbor : neighbors(currentNode)) {
                if (! visitedNodes.contains(neighbor)) {
                    visitedNodes.add(neighbor);
                    parent.put(neighbor, currentNode);
                    queue.add(neighbor);
                }
            }

        }
    }

    /**
     * Reconstructs the path from source to the target node
     * based on the recorded parent relationship
     * @param parent a map linking each node to its predecessor
     * @param target the target node
     * @return a list of nodes representing the path.
     */
    private static List<Node> buildPath(Map<Node, Node> parent, Node target) {
        List<Node> path = new LinkedList<>();
        Node current = target;
        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Returns the path found by the BFS algorithm (source -> target)
     * @return an unmodifiable list of nodes forming the path
     */
    public List<Node> getPath() {
        return path == null ? Collections.emptyList() : Collections.unmodifiableList(path);
    }


}
