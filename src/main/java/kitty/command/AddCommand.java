package kitty.command;

import kitty.Parser;
import kitty.Storage;
import kitty.TaskList;
import kitty.Ui;
import kitty.tasks.Deadline;
import kitty.tasks.Event;
import kitty.tasks.Task;
import kitty.tasks.Todo;


import java.io.IOException;

public class AddCommand extends Command {
    private String commandBody;
    private Storage storage;

    public AddCommand(Ui ui, TaskList taskList, String commandBody, Storage storage) {
        super(ui, taskList);
        this.commandBody = commandBody;
        this.storage = storage;
    }

    @Override
    public String run() {
        String[] parts = commandBody.split(" ", 2);
        Task task = new Task("");
        boolean isCreated = false;
        switch (parts[0]) {
        case "todo" -> {
            task = new Todo(parts[1].trim());
            isCreated = true;
        }
        case "deadline" -> {
            if (Parser.checkDeadline(parts[1].trim(), ui)) {
                String[] aux = Parser.parseDeadline(parts[1]);
                task = new Deadline(aux[0], Parser.parseDateTime(aux[1]));
                isCreated = true;
            }
        }
        case "event" -> {
            if (Parser.checkEvent(parts[1], ui)) {
                String[] aux = Parser.parseEvent(parts[1]);
                task = new Event(aux[0],
                        Parser.parseDateTime(aux[1]),
                        Parser.parseDateTime(aux[2]));
                isCreated = true;
            }
        }
        default -> {
            return "";
        }
        }

        if (isCreated) {
            int size = tasks.addTask(task);
            if (size != -1) {
                String data = task.getTaskData();
                try {
                    storage.addContent(data);
                    return ui.showAddTaskMessage(task, size);
                } catch (IOException e) {
                    return ui.showErrorMessage("File writing unsuccessful.\n"
                            + "This task is not updated to hard disk.");
                }

            }
        }
        return "";
    }
}
