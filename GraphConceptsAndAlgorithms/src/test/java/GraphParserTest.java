import org.graphstream.graph.Graph;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GraphParserTest {
    private GraphGenerator gg;
    private GraphParser gp;
    private File graphDir;

    @BeforeEach
    void setup() {
        gg = new GraphGenerator();
        gp = new GraphParser(gg);
        graphDir = new File("src/main/java/graphs");
        graphDir.mkdirs();
        // cleanup any old test files
        for (File f : Objects.requireNonNull(graphDir.listFiles())) {
            f.delete();
        }
    }

    @AfterEach
    void cleanup() {
        for (File f : Objects.requireNonNull(graphDir.listFiles())) {
            f.delete();
        }
    }

    @Test
    void testParsesValidFile() throws IOException {
        File file = new File(graphDir, "valid.gka");
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("A -> B : 5;");
            out.println("B -- C (label1) : 7;");
        }

        ArrayList<File> files = gp.checkFiles();
        gp.fileParser(files);

        assertEquals(1, gg.getImmutableGraphs().size());
        Graph g = gg.getImmutableGraphs().get(0);

        assertEquals(3, g.getNodeCount());
        assertEquals(2, g.getEdgeCount());
        assertEquals(5, g.getEdge("A->B").getAttribute("weight"));
        assertEquals(7, g.getEdge("B--C").getAttribute("weight"));
        assertEquals("label1", g.getEdge("B--C").getAttribute("label"));
    }

    @Test
    void testInvalidLinesIgnored() throws IOException {
        File file = new File(graphDir, "invalid.gka");
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("This is not valid graph data");
            out.println("X -> Y : 1;");
        }

        ArrayList<File> files = gp.checkFiles();
        gp.fileParser(files);

        Graph g = gg.getMutableGraphs().get(0);
        assertEquals(2, g.getNodeCount());
        assertEquals(1, g.getEdgeCount());
    }

    @Test
    void testEmptyFileCreatesEmptyGraph() throws IOException {
        File file = new File(graphDir, "empty.gka");
        file.createNewFile();

        ArrayList<File> files = gp.checkFiles();
        gp.fileParser(files);

        assertEquals(1, gg.getMutableGraphs().size());
        Graph g = gg.getMutableGraphs().get(0);
        assertEquals(0, g.getNodeCount());
        assertEquals(0, g.getEdgeCount());
    }

    @Test
    void testCheckFilesOnlyReturnsGka() throws IOException {
        File f1 = new File(graphDir, "graph1.gka");
        File f2 = new File(graphDir, "notgraph.txt");
        f1.createNewFile();
        f2.createNewFile();

        ArrayList<File> result = gp.checkFiles();

        assertTrue(result.contains(f1));
        assertFalse(result.contains(f2));
    }
}
