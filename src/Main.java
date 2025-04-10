import javax.swing.SwingUtilities;
import model.TaskModel;
import controller.TaskController;
import view.TaskView;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                TaskModel model = new TaskModel();
                TaskController controller = new TaskController(model);
                TaskView view = new TaskView(model, controller);
                view.setVisible(true);
            }
        });
    }
}
