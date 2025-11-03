import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

import java.awt.*;
import java.util.Scanner;

public class Main {
    private static GraphGenerator gg = new GraphGenerator();
    //Entry point
    static void main() {
        //Initialize parser with same generator instance
        GraphParser gp = new GraphParser(gg);
        gp.fileParser(gp.checkFiles());
        initializeTextBasedUI();
    }

    /**
     * This method handles the displaying of actual graphs via swing
     *
     * @param num the position on the array list of the graph
     * @param gg  graph generator object
     */
    public static void UIHelper(int num, GraphGenerator gg) {
        System.setProperty("org.graphstream.ui", "swing");
        Graph current = gg.getImmutableGraphs().get(num);
        String src = (String) current.getAttribute("sourceFile");
        Viewer viewer = current.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        javax.swing.JFrame frame = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor((Component) viewer.getDefaultView());
        frame.setTitle("Graph Viewer - " + src);
    }

    /**
     * This method handles user input and prints out a neat looking list of "ID - samplegraphfile.gka" as these are unordered
     * It also calls the helper method to initialize the window of the graph that is to be displayed
     *
     * @param gg Graph generator object
     */
    public static void initializeTextBasedUI() {
        System.out.printf("%d graphs found\n", gg.getImmutableGraphs().size());
        for (int i = 0; i < gg.getImmutableGraphs().size(); i++) {
            Graph g = gg.getImmutableGraphs().get(i);
            System.out.printf("\n %d - %s", i, g.getAttribute("sourceFile"));
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n\nPress 0 if you want to visualize a graph\nPress 1 if you want to use the BFS algorithm\n");
        String input = scanner.nextLine();

        switch (input) {
            case "0":
                System.out.println("\nEnter the number of the graph you want to display visually:\n");
                UIHelper(Integer.parseInt(scanner.nextLine()), gg);
                break;
            case "1":
                System.out.println("\nEnter the number of the graph that you want the BFS algorithm to be performed on");
                int graphNum = Integer.parseInt(scanner.nextLine());

                System.out.println("\nEnter the ID of the source node");
                String sID = scanner.nextLine();
                String sourceID = "";
                if (gg.containsNodeId(gg.getMutableGraphs().get(graphNum), sID)) {
                    sourceID = sID;
                }

                System.out.println("\nEnter the ID of the target node");
                String tID = scanner.nextLine();
                String targetID = "";
                if (gg.containsNodeId(gg.getImmutableGraphs().get(graphNum), tID)) {
                    targetID = tID;
                }

                BreadthFirstSearch bfs = new BreadthFirstSearch(sourceID, targetID);
                bfs.init(gg.getImmutableGraphs().get(graphNum));
                bfs.compute();
                System.out.println(bfs.toString());
                break;
            default:
                System.out.println("Invalid input");
        }
    }
}