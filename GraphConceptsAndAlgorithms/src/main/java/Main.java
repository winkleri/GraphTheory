import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.ui.view.Viewer;

import java.awt.*;
import java.util.Scanner;

public class Main {
    static void main() {
        System.setProperty("org.graphstream.ui", "swing");
        GraphGenerator gg = new GraphGenerator();
        gg.fileParser(gg.checkFiles());
        Scanner scanner = new Scanner(System.in);
        System.out.printf("%d graphs found\n", gg.getGraphs().size());
        for (int i = 0; i < gg.getGraphs().size(); i++) {
            Graph g = gg.getGraphs().get(i);
            System.out.printf("\n %d - %s", i, g.getAttribute("sourceFile"));
        }
        System.out.println("\nEnter the graph you want to display visually:\n");
        int num = Integer.parseInt(scanner.nextLine());

        Graph current = gg.getGraphs().get(num);
        String src = (String) current.getAttribute("sourceFile");
        Viewer viewer = current.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        javax.swing.JFrame frame = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor((Component) viewer.getDefaultView());
        frame.setTitle("Graph Viewer - " + src);

    }

    public static void uiHelper() {

    }
}
