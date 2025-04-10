package model;

public class Task {
    private int id;
    private String description;
    private boolean completed;

    public Task(int id, String description) {
        this.id = id;
        this.description = description;
        this.completed = false;
    }

    //getters

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    //setters

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
