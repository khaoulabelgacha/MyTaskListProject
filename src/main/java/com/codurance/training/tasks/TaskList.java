package com.codurance.training.tasks;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream;

public final class TaskList implements Runnable {
    private static final String QUIT = "quit";

    private final Map<String, List<Task>> tasks = new LinkedHashMap<>();
    private final BufferedReader input;
    private final PrintWriter output;

    private long lastId = 0;

    public static void main(String[] args) throws Exception {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter output = new PrintWriter(System.out);
        new TaskList(input, output).run();
    }

    public TaskList(BufferedReader input, PrintWriter output) {
        this.input = input;
        this.output = output;
    }

    public void run() {
        while (true) {
            output.print("> ");
            output.flush();
            String command;
            try {
                command = input.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (command.equals(QUIT)) {
                break;
            }
            executeCommand(command);
        }
    }

    private void executeCommand(String commandLine) {
            String[] commandRest = commandLine.split(" ", 2);
            String command = commandRest[0];
            switch (command) {
            case "show":
                showTask();
                return;
            case "add":
                addCommand(commandRest[1]);
                return;
            case "check":
                checkTask(commandRest[1]);
                return;
            case "uncheck":
                uncheckTask(commandRest[1]);
                return;
            case "help":
                help();
                return;
            }

            showError(command);
    }

    private void showTask() {
            tasks.forEach((projectName, tasks) -> {
                output.println(projectName);
                tasks.forEach(task -> output.printf("    [%c] %d: %s%n", (task.done ? 'x' : ' '), task.id, task.description));
                output.println();
            });
    }


    private void addCommand(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);
        String subcommand = subcommandRest[0];
        if (subcommand.equals("project")) {
            addProject(subcommandRest[1]);
            return;
        }
        if (subcommand.equals("task")) {
            String[] projectTask = subcommandRest[1].split(" ", 2);
            addTask(projectTask[0], projectTask[1]);
        }
    }


    private void addProject(String name) {
        tasks.put(name, new ArrayList<Task>());
    }

    private void addTask(String project, String description) {
        tasks.entrySet().stream()
                .filter(entry -> entry.getKey().equals(project))
                .findFirst()
                .ifPresentOrElse(entry -> entry.getValue().add(new Task(nextId(), description, false)),
                        () -> output.printf("Could not find a project with the name \"%s\".", project));
    }


    private void checkTask(String idString) {
        setTaskDone(idString, true);
    }

    private void uncheckTask(String idString) {
        setTaskDone(idString, false);
    }
    private void setTaskDone(String idString, boolean done) {
        int id = Integer.parseInt(idString);
        tasks.values().stream()
                .flatMap(List::stream)
                .filter(task -> task.id == id)
                .findFirst()
                .ifPresentOrElse(task -> task.done = done,
                () -> output.printlf("Could not find a task with an ID of %d.", id));
        
    }


    private void help() {
        output.println("Commands:");
        COMMANDS.forEach(command -> out.println("  " + command));
    }

    private static final List<String> COMMANDS = Arrays.asList("show", "add", "check", "uncheck", "help", "quit");
    private void showError(String command) {
        if (!COMMANDS.stream().anyMatch(command -> command.equals(command))) {
            output.printf("I don't know what the command \"%s\" is.", command);
            output.println();
        }
    }

    private long nextId() {
        return ++lastId;
    }

}
    
