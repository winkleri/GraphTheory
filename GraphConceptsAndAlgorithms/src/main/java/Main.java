import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;

import java.util.Scanner;

public class Main {
    static void main() {
        System.setProperty("org.graphstream.ui", "swing");
        GraphGenerator gg = new GraphGenerator();
        gg.initializeScanner(gg.checkFiles());
        Scanner scanner = new Scanner(System.in);

        while(scanner.hasNextLine()) {
            System.out.println("Enter the graph you want to display visually");
            System.out.printf("%d graphs found", gg.getGraphs().size());
            int num = Integer.parseInt(scanner.nextLine());
            gg.getGraphs().get(num-1).display();

            break;

        }

    }

}
