import org.graphstream.graph.Graph;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GraphParserTest {
    private GraphGenerator gg;
    private GraphParser gp;
    private File graphDir;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        //initialize objects/directories
        gg = new GraphGenerator();
        gp = new GraphParser(gg);
        graphDir = tempDir.toFile();
        gp = new GraphParser(gg, graphDir);
        graphDir.mkdirs();
    }

    @Test
    void testParsesValidFileAndCreatesGraphCorrectly() throws IOException {
        File file = new File(graphDir, "valid.gka");
        //add information to files
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("A -> B : 5;");
            out.println("B -- C (label1) : 7;");
        }
        ArrayList<File> files = gp.checkFiles();
        gp.fileParser(files);
        assertEquals(1, gg.getImmutableGraphs().size());
        Graph g = gg.getImmutableGraphs().get(0);
        //expect three nodes being added
        assertEquals(3, g.getNodeCount());
        //two edges found
        assertEquals(2, g.getEdgeCount());
        //edge weight 5 found for first
        assertEquals(5, g.getEdge("A->B").getAttribute("weight"));
        //edge weight 7 found for second
        assertEquals(7, g.getEdge("B--C").getAttribute("weight"));
        //label for second
        assertEquals("label1", g.getEdge("B--C").getAttribute("label"));
    }

    @Test
    void testInvalidLinesIgnored() throws IOException {
        File file = new File(graphDir, "invalid.gka");
        //add information to file
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("This is not valid graph data");
            out.println("X -> Y : 1;");
        }
        ArrayList<File> files = gp.checkFiles();
        gp.fileParser(files);
        Graph g = gg.getMutableGraphs().get(0);
        //expect two nodes and one edge being added (invalid line skipped)
        assertEquals(2, g.getNodeCount());
        assertEquals(1, g.getEdgeCount());
    }

    @Test
    void testEmptyFileDoesntCreateGraph() throws IOException {
        File file = new File(graphDir, "empty.gka");
        file.createNewFile();
        ArrayList<File> files = new ArrayList<>();
        files.add(file);
        gp.fileParser(files);
        //expect no graph being created/added to graph list
        assertEquals(0, gg.getMutableGraphs().size());
    }

    @Test
    void testCheckFilesOnlyReturnsGkaFiles() throws IOException {
        File f1 = new File(graphDir, "graph1.gka");
        File f2 = new File(graphDir, "notgraph.txt");
        f1.createNewFile();
        f2.createNewFile();
        ArrayList<File> result = gp.checkFiles();
        //gka file is valid
        assertTrue(result.contains(f1));
        //txt file is invalid
        assertFalse(result.contains(f2));
    }
}
