import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BreadthFirstSearchTest {

    private Graph undirectedLine;
    private Graph diamond;
    private Graph directed;

    @BeforeEach
    void setup() {
        // undirected graph
        undirectedLine = new SingleGraph("line");
        undirectedLine.addNode("A"); undirectedLine.addNode("B");
        undirectedLine.addNode("C"); undirectedLine.addNode("D");
        undirectedLine.addEdge("AB","A","B", false);
        undirectedLine.addEdge("BC","B","C", false);
        undirectedLine.addEdge("CD","C","D", false);

        // diamond graph (two paths that are equally short)
        diamond = new SingleGraph("diamond");
        diamond.addNode("S"); diamond.addNode("A");
        diamond.addNode("B"); diamond.addNode("T");
        diamond.addEdge("SA","S","A", false);
        diamond.addEdge("SB","S","B", false);
        diamond.addEdge("AT","A","T", false);
        diamond.addEdge("BT","B","T", false);
        diamond.addEdge("AB","A","B", false);

        // directed graph
        directed = new SingleGraph("directed");
        directed.addNode("A"); directed.addNode("B"); directed.addNode("C");
        directed.addEdge("A_B","A","B", true);
        directed.addEdge("C_B","C","B", true);
    }

    @Test
    void testFindShortest() {
        //bfs should find A-B-C-D
        BreadthFirstSearch bfs = new BreadthFirstSearch("A","D");
        bfs.init(undirectedLine);
        bfs.compute();

        List<Node> path = bfs.getPath();
        assertEquals(4, path.size(), "path should contain 4 nodes (A-B-C-D)");
        assertEquals("A", path.get(0).getId());
        assertEquals("D", path.get(3).getId());
    }

    @Test
    void testUsesShortest() {
        //bfs should find one of two equally short paths S->A->T or S->B->T
        BreadthFirstSearch bfs = new BreadthFirstSearch("S","T");
        bfs.init(diamond);
        bfs.compute();

        List<Node> path = bfs.getPath();
        assertEquals(3, path.size(), "S->(A|B)->T (3 nodes, 2 edges)");
        assertEquals("S", path.get(0).getId());
        assertEquals("T", path.get(2).getId());
        assertTrue(path.get(1).getId().equals("A") || path.get(1).getId().equals("B"),
                "intermediate node must be A or B");
    }

    @Test
    void testDirectedEdges() {
        //A->B is valid, C->B should not count as outgoing from A
        BreadthFirstSearch bfs = new BreadthFirstSearch("A","B");
        bfs.init(directed);
        bfs.compute();

        List<Node> path = bfs.getPath();
        assertEquals(2, path.size(), "A->B should be reached directly (with one edge)");
        assertEquals("A", path.get(0).getId());
        assertEquals("B", path.get(1).getId());
    }

    @Test
    void testNeighborsUndirected() {
        //B should have A and C as neighbors
        BreadthFirstSearch bfs = new BreadthFirstSearch("A","D");
        bfs.init(undirectedLine);

        var neighborsB = bfs.neighbors(undirectedLine.getNode("B"));
        var ids = neighborsB.stream().map(Node::getId).toList();

        assertTrue(ids.contains("A") && ids.contains("C"),
                "B is neighbor of a A and C in undirected graph");
    }

    @Test
    void testNeighborsDirected() {
        //only outgoing edges count, B has none
        BreadthFirstSearch bfs = new BreadthFirstSearch("A","B");
        bfs.init(directed);

        var neighborsA = bfs.neighbors(directed.getNode("A"));
        var idsA = neighborsA.stream().map(Node::getId).toList();
        assertTrue(idsA.contains("B"), "A has outgoing neighbor B");

        var neighborsB = bfs.neighbors(directed.getNode("B"));
        assertTrue(neighborsB.isEmpty(), "B has no outgoing edges in directed graph");
    }

    @Test
    void testDirectedGraphOneWayOnly() {
        //directed chain A -> B -> C (no reverse edges)
        Graph g = new SingleGraph("oneway");
        g.addNode("A"); g.addNode("B"); g.addNode("C");
        g.addEdge("A_B", "A", "B", true);
        g.addEdge("B_C", "B", "C", true);

        //forward search: A -> C should be reachable
        BreadthFirstSearch bfsForward = new BreadthFirstSearch("A", "C");
        bfsForward.init(g);
        bfsForward.compute();

        List<Node> forwardPath = bfsForward.getPath();
        assertEquals(3, forwardPath.size());
        assertEquals("A", forwardPath.get(0).getId());
        assertEquals("C", forwardPath.get(2).getId());

        //reverse search: C -> A should NOT be reachable
        BreadthFirstSearch bfsReverse = new BreadthFirstSearch("C", "A");
        bfsReverse.init(g);
        bfsReverse.compute();

        List<Node> reversePath = bfsReverse.getPath();
        //expect empty path or only the starting node, depending on BFS implementation
        assertTrue(reversePath.size() <= 1,
                "C is not allowed to reach A because all edges are directed");
    }
}