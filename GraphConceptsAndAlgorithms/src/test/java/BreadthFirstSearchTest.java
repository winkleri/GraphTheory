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
        // Einfacher ungerichteter Graph A-B-C-D
        undirectedLine = new SingleGraph("line");
        undirectedLine.addNode("A"); undirectedLine.addNode("B");
        undirectedLine.addNode("C"); undirectedLine.addNode("D");
        undirectedLine.addEdge("AB","A","B", false);
        undirectedLine.addEdge("BC","B","C", false);
        undirectedLine.addEdge("CD","C","D", false);

        // Diamond-Graph (zwei gleich kurze Alternativpfade)
        diamond = new SingleGraph("diamond");
        diamond.addNode("S"); diamond.addNode("A");
        diamond.addNode("B"); diamond.addNode("T");
        diamond.addEdge("SA","S","A", false);
        diamond.addEdge("SB","S","B", false);
        diamond.addEdge("AT","A","T", false);
        diamond.addEdge("BT","B","T", false);
        diamond.addEdge("AB","A","B", false);

        // Gerichteter Graph
        directed = new SingleGraph("directed");
        directed.addNode("A"); directed.addNode("B"); directed.addNode("C");
        directed.addEdge("A_B","A","B", true);
        directed.addEdge("C_B","C","B", true);
    }

    @Test
    void testFindShortest() {
        BreadthFirstSearch bfs = new BreadthFirstSearch("A","D");
        bfs.init(undirectedLine);
        bfs.compute();

        List<Node> path = bfs.getPath();
        assertEquals(4, path.size(), "Pfad sollte 4 Knoten enthalten (A-B-C-D)");
        assertEquals("A", path.get(0).getId());
        assertEquals("D", path.get(3).getId());
    }

    @Test
    void testUsesShortest() {
        BreadthFirstSearch bfs = new BreadthFirstSearch("S","T");
        bfs.init(diamond);
        bfs.compute();

        List<Node> path = bfs.getPath();
        assertEquals(3, path.size(), "S->(A|B)->T (3 Knoten, 2 Kanten)");
        assertEquals("S", path.get(0).getId());
        assertEquals("T", path.get(2).getId());
        assertTrue(path.get(1).getId().equals("A") || path.get(1).getId().equals("B"),
                "Zwischenknoten muss A oder B sein");
    }

    @Test
    void testDirectedEdges() {
        BreadthFirstSearch bfs = new BreadthFirstSearch("A","B");
        bfs.init(directed);
        bfs.compute();

        List<Node> path = bfs.getPath();
        assertEquals(2, path.size(), "A->B sollte direkt erreichbar sein");
        assertEquals("A", path.get(0).getId());
        assertEquals("B", path.get(1).getId());
    }

    @Test
    void testNeighborsUndirected() {
        BreadthFirstSearch bfs = new BreadthFirstSearch("A","D");
        bfs.init(undirectedLine);

        var neighborsB = bfs.neighbors(undirectedLine.getNode("B"));
        var ids = neighborsB.stream().map(Node::getId).toList();

        assertTrue(ids.contains("A") && ids.contains("C"),
                "B hat Nachbarn A und C in ungerichtetem Graph");
    }

    @Test
    void testNeighborsDirected() {
        BreadthFirstSearch bfs = new BreadthFirstSearch("A","B");
        bfs.init(directed);

        var neighborsA = bfs.neighbors(directed.getNode("A"));
        var idsA = neighborsA.stream().map(Node::getId).toList();
        assertTrue(idsA.contains("B"), "A hat ausgehenden Nachbarn B");

        var neighborsB = bfs.neighbors(directed.getNode("B"));
        assertTrue(neighborsB.isEmpty(), "B hat keine ausgehenden Kanten im gerichteten Testgraph");
    }
}