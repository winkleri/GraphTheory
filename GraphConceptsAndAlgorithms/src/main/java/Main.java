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
     * @param num the position on the array list of the graph
     * @param gg graph generator object
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
     * @param gg Graph generator object
     */
    public static void initializeTextBasedUI(GraphGenerator gg) {
        Scanner scanner = new Scanner(System.in);
        System.out.printf("%d graphs found\n", gg.getMutableGraphs().size());
        for (int i = 0; i < gg.getImmutableGraphs().size(); i++) {
            Graph g = gg.getMutableGraphs().get(i);
            System.out.printf("\n %d - %s", i, g.getAttribute("sourceFile"));
        }
        System.out.println("\nEnter the number of the graph you want to display visually:\n");
        UIHelper(Integer.parseInt(scanner.nextLine()), gg);
    }


}
