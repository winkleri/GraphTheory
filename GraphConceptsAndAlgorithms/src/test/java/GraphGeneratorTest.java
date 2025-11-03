import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphGeneratorTest {
    private GraphGenerator gg;

    @BeforeEach
    void setup() {
        gg = new GraphGenerator();
    }

    @Test
    void testCreateNewGraph() {
        Graph g = gg.createNewGraph("sample.gka");
        assertNotNull(g);
        assertEquals("sample.gka", g.getAttribute("sourceFile"));
        assertTrue(g.getId().startsWith("graph:"));
    }

    @Test
    void testContainsNodeId() {
        Graph g = gg.createNewGraph("x.gka");
        g.addNode("A");
        assertTrue(gg.containsNodeId(g, "A"));
        assertFalse(gg.containsNodeId(g, "B"));
    }

    @Test
    void testUpdateGraph() {
        Graph g = gg.createNewGraph("test.gka");
        gg.updateGraph(g, "A", "B", "->", "edgeLabel1", 42);

        Node a = g.getNode("A");
        Node b = g.getNode("B");
        Edge e = g.getEdge("A->B");

        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(e);
        assertEquals("A->B", e.getAttribute("ui.label"));
        assertEquals(42, e.getAttribute("weight"));
        assertEquals("edgeLabel1", e.getAttribute("label"));
        assertTrue(e.isDirected());
    }

    @Test
    void testDuplicateEdgeSkipped() {
        Graph g = gg.createNewGraph("dup.gka");
        gg.updateGraph(g, "A", "B", "--", null, null);
        gg.updateGraph(g, "B", "A", "--", null, null); // should be skipped

        long edgeCount = g.edges().count();
        assertEquals(1, edgeCount);
    }

    @Test
    void testCheckDirected() {
        assertTrue(gg.checkDirected("->"));
        assertFalse(gg.checkDirected("--"));
        assertFalse(gg.checkDirected("invalid"));
    }
}
