import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class BreadthFirstSearch implements Algorithm {
    private final String sourceNodeID;
    private final String targetNodeID;
    private Graph graph;
    private String currentNode;

    public BreadthFirstSearch(String sourceNodeID, String targetNodeID) {
        this.sourceNodeID = sourceNodeID;
        this.targetNodeID = targetNodeID;
    }

    @Override
    public void init(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void compute() {
        Node source = graph.getNode(sourceNodeID);
        Node target = graph.getNode(targetNodeID);

        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set viditedNodes = new HashSet();
        queue.add(source);

        while(! (queue.isEmpty())) {
        }
    }
}
