package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.repository.IRepository;
import interpreter.repository.Repository;

import java.util.Scanner;

public class View {
    private final Scanner scanner = new Scanner(System.in);
    private final IStmt[] programs = {Examples.EX1, Examples.EX2, Examples.EX3, Examples.EX4,Examples.EX5};
    private int selected = -1;

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Your choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                selectProgram();
            } else if (choice.equals("2")) {
                runSelectedProgram();
            } else if (choice.equals("0")) {
                running = false;
            } else {
                System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Select a program");
        System.out.println("2. Run the selected program");
        System.out.println("0. Exit");
    }

    private void selectProgram() {
        for (int i = 0; i < programs.length; i++) {
            System.out.println((i + 1) + ". " + programs[i]);
        }
        System.out.print("Program number: ");
        String text = scanner.nextLine().trim();

        for (int i = 0; i < programs.length; i++) {
            if (text.equals(String.valueOf(i + 1))) {
                selected = i;
                System.out.println("Program " + text + " selected.");
                return;
            }
        }
        System.out.println("There is no such program.");
    }

    private void runSelectedProgram() {
        if (selected == -1) {
            System.out.println("Select a program first.");
            return;
        }

        IStmt program = programs[selected];
        PrgState state = PrgState.of(program);
        IRepository repo = new Repository(state);
        Controller controller = new Controller(repo, true);

        try {
            controller.allStep();
            System.out.println("Program finished.");
        } catch (MyException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}