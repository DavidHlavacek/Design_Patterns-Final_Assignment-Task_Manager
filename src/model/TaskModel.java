package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskModel {
    private Map<Integer, Task> taskMap; // HashMap for O(1) lookups by ID
    private List<TaskObserver> observers;

    public TaskModel() {
        taskMap = new HashMap<>();
        observers = new ArrayList<>();
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

    public void markTaskCompleted(Task task) {
        Task storedTask = taskMap.get(task.getId());
        if (storedTask != null) {
            storedTask.setCompleted(true);
            notifyObservers();
        }
    }
    
    public void markTaskIncomplete(Task task) {
        Task storedTask = taskMap.get(task.getId());
        if (storedTask != null) {
            storedTask.setCompleted(false);
            notifyObservers();
        }
    }
    
    public void editTask(Task task, String newDescription) {
        Task storedTask = taskMap.get(task.getId());
        if (storedTask != null) {
            storedTask.setDescription(newDescription);
            notifyObservers();
        }
    }

    public List<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public Task getTaskById(int id) {
        return taskMap.get(id);
    }
}
