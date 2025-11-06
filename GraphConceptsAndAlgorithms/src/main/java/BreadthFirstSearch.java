import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;

public class BreadthFirstSearch implements Algorithm {
    private final String sourceNodeID;
    private final String targetNodeID;
    private Graph graph;
    private List<Node> path;

    public BreadthFirstSearch(String sourceNodeID, String targetNodeID) {
        this.sourceNodeID = sourceNodeID;
        this.targetNodeID = targetNodeID;
        path = new LinkedList<>();
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
        // To calculate duration
        long startTime = System.nanoTime();

        // To calculate
        Runtime runtime = Runtime.getRuntime();
        long beforeUsed = runtime.totalMemory() - runtime.freeMemory();

        final Node source = graph.getNode(sourceNodeID);
        final Node target = graph.getNode(targetNodeID);

        if(source == null || target == null) {
            throw new IllegalStateException("Source or target node are null");
        }

        Queue<Node> queue = new LinkedList<>();
        Map<Node,Node> parent = new HashMap<>();
        Set<Node> visitedNodes = new HashSet<>();

        visitedNodes.add(source);
        queue.add(source);
        while(! (queue.isEmpty())) {
            //extracting node from queue
            Node currentNode = queue.poll();

            //When the current node equals the target node, the bfs algorithm is finished.
            //The path is built and performance measurements are printed
            if (currentNode.equals(target)) {
                //variable that stores the nodes that are part of thg shortest path
                this.path = buildPath(parent, target);

                long endTime = System.nanoTime();
                double durationMs = (endTime - startTime) / 1_000_000.0;

                long afterUsed = runtime.totalMemory() - runtime.freeMemory();
                double usedMb = (afterUsed - beforeUsed) / (1024.0 * 1024.0);

                System.out.println("\nDuration (BFS): " + durationMs + " Seconds.");
                System.out.println("Memory used: " + usedMb + " MB.");
                return;
            }

            //This for-Loop iterates over a list that stores the neighbor node's.
            //Every node has its own neighbor node
            for (Node neighbor : neighbors(currentNode)) {
                //checking if the neighbor nodes are already in the set of elements that have
                //been visited. If not then we add the neighbor to the visited nodes and map the neighbor node
                //to the current Node.
                if (! visitedNodes.contains(neighbor)) {
                    visitedNodes.add(neighbor); //
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
        //iterating over the nodes in the map und updating the current node with the parent node
        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }
        //reversing the list because childNodes are being mapped on parents node
        Collections.reverse(path);
        return path;
    }

    /**
     * Returns all neighboring nodes of a given node. (Handles both directed and undirected edges)
     * @param currentNode the node whose parents are to be determined.
     * @return a list of neighbouring nodes
     */
    public List<Node> neighbors(Node currentNode) {
        List<Node> neighbors = new ArrayList<>();

        //looping over the node degree to get each node's neighbors
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
     * @return String of the shortest path.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Node n : path) {
            sb.append("-").append(n.getId()).append("-");
        }
        int edgeAmount = path.size() - 1;
        return "\nShortest path: Source <-" + sb + "-> Target\nAmount of Edges: " + edgeAmount;
    }

    /**
     * Returns the path found by the BFS algorithm (source -> target)
     * @return an unmodifiable list of nodes forming the path
     */
    public List<Node> getPath() {
        //For the Test class
        return path == null ? Collections.emptyList() : Collections.unmodifiableList(path);
    }

}
