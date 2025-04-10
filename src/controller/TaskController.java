package controller;

import model.Task;
import model.TaskModel;
import model.TaskFactory;

public class TaskController {
    private TaskModel model;
    private TaskFactory taskFactory;
    
    public TaskController(TaskModel model) {
        this.model = model;
        this.taskFactory = new TaskFactory();
    }
    
    public void addTask(String description) {
        Task task = taskFactory.createTask(description);
        model.addTask(task);
    }
    
    public void deleteTask(Task task) {
        model.deleteTask(task);
    }
    
    public void markTaskCompleted(Task task) {
        model.markTaskCompleted(task);
    }
    
    public void markTaskIncomplete(Task task) {
        model.markTaskIncomplete(task);
    }
    
    public void editTask(Task task, String newDescription) {
        model.editTask(task, newDescription);
    }
}
