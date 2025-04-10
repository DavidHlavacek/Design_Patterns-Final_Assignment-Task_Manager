package strategy;

import model.Task;
import java.util.List;

public interface TaskSortStrategy {
    List<Task> sort(List<Task> tasks);
    
    String getName();
} 