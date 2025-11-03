import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

import java.awt.*;
import java.util.Scanner;

public class Main {
    //Entry point
    static void main() {
        GraphGenerator gg = new GraphGenerator();
        //Initialize parser with same generator instance
        GraphParser gp = new GraphParser(gg);
        gp.fileParser(gp.checkFiles());
        initializeTextBasedUI(gg);
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
    public static void initializeTextBasedUI(GraphGenerator gg) {
        System.out.printf("%d graphs found\n", gg.getImmutableGraphs().size());
        for (int i = 0; i < gg.getImmutableGraphs().size(); i++) {
            Graph g = gg.getImmutableGraphs().get(i);
            System.out.printf("\n %d - %s", i, g.getAttribute("sourceFile"));
        }
        uiInteraction(gg);
    }

    /**
     * This method allows a user to interact with the program.
     * The user can choose between option 1 and option 2.
     * Option 1: chosen graph is visualized
     * Option 2: The BFS algorithm will be performed on the chosen graph.
     * @param gg is a variable to generate a graph.
     */
    public static void uiInteraction(GraphGenerator gg) {
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
                String sourceID = gg.checkNodeID(gg.getImmutableGraphs().get(graphNum), scanner.nextLine());
                System.out.println("\nEnter the ID of the target node");
                String targetID = gg.checkNodeID(gg.getImmutableGraphs().get(graphNum), scanner.nextLine());

                BreadthFirstSearch bfs = new BreadthFirstSearch(sourceID, targetID);
                bfs.init(gg.getImmutableGraphs().get(graphNum));
                bfs.compute();
                System.out.println(bfs.printPath());
                break;
            default:
                System.out.println("Invalid input");
        }
    }
}