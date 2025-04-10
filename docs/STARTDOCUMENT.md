# Startdocument for Task Manager

Startdocument of **David Hlavacek**. Student number **5094879**.

## Application Description
TaskManagerApp is a to-do list application that allows users to manage their tasks. Users can add tasks, mark them as completed, edit, and delete them. Other minor features are available such as toggling the visibility of completed tasks, sorting tasks in different ways, or a counter of completed and pending tasks. The application uses the MVC architecture to separate the presentation layer, the business logic, and the flow of data. Additionally, it implements three design patterns to solve specific problems in the application.

### Language Selected
Java

### Design Patterns Selected

1. **Simple Factory Pattern** - Creates task objects with proper initialization
   - **Purpose**: Centralizes object creation and hides the instantiation logic
   - **Problem Solved**: Eliminates the need for client code to know the details of how tasks are created, initialized, and assigned IDs. This pattern simplifies the creation of complex objects and provides a _consistent_ way to generate new tasks.

2. **Observer Pattern** - Updates the UI automatically when data changes
   - **Purpose**: Establishes a one-to-many dependency between objects so that when one object changes state, all its dependents are notified
   - **Problem Solved**: Allows the model to notify the view of changes without being directly aware of it. The model doesn't need to know which views are observing it, enabling loose coupling between components and making the application more maintainable.

3. **Strategy Pattern** - Enables different sorting algorithms to be selected at runtime
   - **Purpose**: Defines a family of algorithms, encapsulates each one, and makes them interchangeable
   - **Problem Solved**: Allows the application to switch between different sorting methods without modifying the model code. This pattern separates sorting algorithms from the client code that uses them, making it easy to add new sorting strategies without changing existing code.

### Architecture
- MVC (Model-View-Controller) - Separates the application into three main components

### Diagram
![Class Diagram](UMLDiagram.png)

### MoSCoW
- **Must have:**
  - Add new tasks.
  - Mark tasks as completed.
- **Should have:**
  - Delete tasks.
  - Edit task.
  - Toggle visbility of completed tasks.
  - Persistent storage for tasks.
- **Could have:**
  - Sort tasks.
  - Setting priority of tasks.
- **Won't have:**
  - Folders for different type of tasks.
  - Multi-user collaboration features.

### Remarks
- For associations where the multiplicity is 1 on both ends, the labels are omitted for clarity.
- The app showcases its functionalities and the usage of design patterns to solve problems. Therefore, we do not implement persisten storage... for now.
- I had fun with the styling.
- Put some effort into the UX.
- After a grade, I will extend this app, such as clicking on tasks opens up a dialog where you can set extra information (such as priority, etc.).
