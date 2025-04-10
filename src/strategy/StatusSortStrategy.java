package strategy;

import model.Task;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatusSortStrategy implements TaskSortStrategy {
    
    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>(tasks);
        // sort completed first then id
        sortedTasks.sort(Comparator.comparing(Task::isCompleted).thenComparing(Task::getId));
        return sortedTasks;
    }
    
    @Override
    public String getName() {
        return "by Status";
    }
} 