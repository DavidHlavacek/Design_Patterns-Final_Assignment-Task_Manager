package model;

public class TaskFactory {
    private int idCounter = 1;

    public Task createTask(String description) {
        return new Task(idCounter++, description);
    }
}
