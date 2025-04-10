package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import strategy.TaskSortStrategy;
import strategy.IdSortStrategy;

// the model is the data layer, it is responsible for managing the data and the business logic
// the model does not know about the view

public class TaskModel {
    private Map<Integer, Task> taskMap; // HashMap for O(1) lookups by ID
    private List<TaskObserver> observers;
    private TaskSortStrategy sortStrategy;

    public TaskModel() {
        taskMap = new HashMap<>();
        observers = new ArrayList<>();
        // default
        sortStrategy = new IdSortStrategy();
    }

    //everything here pretty self-explanatory

    public void addObserver(TaskObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TaskObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (TaskObserver observer : observers) {
            observer.update();
        }
    }

    public void addTask(Task task) {
        taskMap.put(task.getId(), task);
        notifyObservers();
    }

    public void deleteTask(Task task) {
        int id = task.getId();
        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
            notifyObservers();
        }
    }

    public void setTaskCompleted(Task task, boolean completed) {
        Task retrievedTask = taskMap.get(task.getId());
        if (retrievedTask != null) {
            retrievedTask.setCompleted(completed);
            notifyObservers();
        }
    }
    
    public void editTask(Task task, String newDescription) {
        Task retrievedTask = taskMap.get(task.getId());
        if (retrievedTask != null) {
            retrievedTask.setDescription(newDescription);
            notifyObservers();
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>(taskMap.values());
        return sortStrategy.sort(tasks);
    }

    public void setSortStrategy(TaskSortStrategy strategy) {
        this.sortStrategy = strategy;
        notifyObservers();
    }
}
