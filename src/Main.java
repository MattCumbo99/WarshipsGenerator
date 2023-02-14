import com.warships.tree.CommandProcessor;
import com.warships.tree.TechTree;
import com.warships.utils.StringUtility;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TechTree tree = new TechTree();
        tree.generate();

        tree.displayNodes();

        System.out.println("Enter a command, or type 'help' for a list of commands.");

        Scanner scanner = new Scanner(System.in);
        String command;

        do {
            System.out.print(">");
            command = scanner.nextLine();

            if (StringUtility.isNotBlank(command) && !command.equalsIgnoreCase("exit")) {
                CommandProcessor.process(command, tree);
            }

        } while (!command.equalsIgnoreCase("exit"));

        scanner.close();
    }

}