package strategy;

import model.Task;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AlphabeticalSortStrategy implements TaskSortStrategy {
    
    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(Comparator.comparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER));
        return sortedTasks;
    }
    
    @Override
    public String getName() {
        return "Alphabetically";
    }
} 