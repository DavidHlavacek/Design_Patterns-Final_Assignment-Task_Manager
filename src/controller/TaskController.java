package controller;

import model.Task;
import model.TaskModel;
import model.TaskFactory;
import strategy.TaskSortStrategy;

// the controller is the middleman between the model and the view, the view tells the controller to change the model, 
// but the controller itself doensn't directly interact with the view

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
    
    public void setTaskCompleted(Task task, boolean completed) {
        model.setTaskCompleted(task, completed);
    }
    
    public void editTask(Task task, String newDescription) {
        model.editTask(task, newDescription);
    }
    
    public void setSortStrategy(TaskSortStrategy strategy) {
        model.setSortStrategy(strategy);
    }
}
