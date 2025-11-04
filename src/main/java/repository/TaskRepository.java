package repository;

import model.Quehacer;
import java.util.List;

public interface TaskRepository {
    List<Quehacer> getCompletedTasksByUser(Long miembroId);
}