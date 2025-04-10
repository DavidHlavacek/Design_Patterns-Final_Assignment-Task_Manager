package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.Consumer;
import model.Task;
import model.TaskObserver;
import model.TaskModel;
import controller.TaskController;
import strategy.TaskSortStrategy;
import strategy.IdSortStrategy;
import strategy.AlphabeticalSortStrategy;
import strategy.StatusSortStrategy;

// the view is the frontend, it only reads data from the model and displays it
// it changes the model through the controller, if there is input from the user (adding tasks, changing sort strategy, etc.)

public class TaskView extends JFrame implements TaskObserver {
    private static final Color BG_COLOR = new Color(245, 245, 250),
                              HEADER_COLOR = new Color(70, 130, 180),
                              ACTIVE_TASK_BG = new Color(210, 235, 255),
                              COMPLETED_TASK_BG = new Color(245, 245, 245),
                              EDIT_COLOR = new Color(100, 180, 220),
                              DELETE_COLOR = new Color(255, 140, 140);
    
    private TaskModel model;
    private TaskController controller;
    
    private JPanel tasksPanel;
    
    private JTextArea taskDescriptionField;
    private JButton addButton;
    
    private JCheckBox showCompletedCheckbox;
    private JComboBox<String> sortSelector;
    private JLabel counterLabel;
    
    private TaskSortStrategy[] sortStrategies;
    private JScrollPane contentScrollPane;

    public TaskView(TaskModel model, TaskController controller) {
        this.model = model;
        this.controller = controller;
        model.addObserver(this);
        try {UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());} 
        catch (Exception e) {e.printStackTrace();}
        
        // init sort strategies
        sortStrategies = new TaskSortStrategy[] {
            new IdSortStrategy(),
            new StatusSortStrategy(),
            new AlphabeticalSortStrategy()
        };
        
        setTitle("Task Manager App");
        setSize(600, 800);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setLocationRelativeTo(null);
    }

    // functions for method chaining (my favorite, easy in js)
    private <T extends JComponent> T ui(T c, Color bg, Consumer<T> setup) {
        if (bg != null) c.setBackground(bg);
        if (setup != null) setup.accept(c);
        return c;
    }
    
    // util for creating fonts
    private Font createFont(int style, int size) {
        return new Font("Arial", style, size);
    }
    
    // util for shared button styling
    private void setupButton(JButton button, Font font, Color fgColor) {
        button.setFont(font);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); 
        button.setForeground(fgColor);
    }
    
    private void initComponents() {
        // 1. init components
        // --------------------------------------
        
        // header components
        JLabel titleLabel = ui(new JLabel("Task Manager"), null, c -> {
            c.setFont(createFont(Font.BOLD, 18));
            c.setForeground(Color.WHITE);
            c.setBorder(new EmptyBorder(10, 10, 10, 0));
        });
        
        // input components
        taskDescriptionField = ui(new JTextArea(3, 30), null, c -> {
            c.setFont(createFont(Font.PLAIN, 14));
            c.setLineWrap(true);
            c.setWrapStyleWord(true);
            c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            c.addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        e.consume();
                        if (e.isShiftDown()) taskDescriptionField.append("\n"); 
                        else addButton.doClick();
                    }
                }
            });
        });

        addButton = ui(new JButton("Add Task"), null, c -> {
            setupButton(c, createFont(Font.BOLD, 14), Color.WHITE);
            c.setBackground(HEADER_COLOR);
            c.setContentAreaFilled(true);
            c.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); //overwrite
            c.addActionListener(e -> {
                String description = taskDescriptionField.getText().trim();
                if (!description.isEmpty()) {
                    controller.addTask(description);
                    taskDescriptionField.setText("");
                    // scroll to bottom after adding new task
                    SwingUtilities.invokeLater(() -> {
                        SwingUtilities.invokeLater(() -> {
                            JScrollBar verticalScrollBar = contentScrollPane.getVerticalScrollBar();
                            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                        });
                    });
                }
            });
        });
        
        // task components
        tasksPanel = ui(new JPanel(), Color.WHITE, p -> 
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)));

        // footer components
        showCompletedCheckbox = ui(new JCheckBox("Show Completed Tasks", true), null, c -> {
            c.setFont(createFont(Font.PLAIN, 12));
            c.setBackground(BG_COLOR);
            c.addActionListener(e -> update());
        });

        counterLabel = ui(new JLabel("0 pending · 0 completed"), null, c -> {
            c.setFont(createFont(Font.PLAIN, 11));
            c.setForeground(new Color(150, 150, 150));
        });
        
        // init sort options
        String[] sortOptions = new String[sortStrategies.length];
        for (int i = 0; i < sortStrategies.length; i++) {
            sortOptions[i] = sortStrategies[i].getName();
        }
        
        sortSelector = ui(new JComboBox<>(sortOptions), null, c -> {
            c.setFont(createFont(Font.PLAIN, 12));
            c.addActionListener(e -> {
                int selectedIndex = sortSelector.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < sortStrategies.length) {
                    model.setSortStrategy(sortStrategies[selectedIndex]);
                }
            });
        });

        // 2. assemble panels
        // --------------------------------------

        // header panel
        JPanel headerPanel = ui(new JPanel(new FlowLayout(FlowLayout.LEFT)), HEADER_COLOR, p -> 
            p.add(titleLabel));
        
        // input panel
        JPanel inputPanel = ui(new JPanel(new BorderLayout(5, 0)), BG_COLOR, p -> {
            p.add(ui(new JScrollPane(taskDescriptionField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), null, 
                c -> c.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)))), 
                BorderLayout.CENTER);
            p.add(addButton, BorderLayout.EAST);
            p.setBorder(new EmptyBorder(0, 0, 10, 0)); // Add padding below input
        });
        
        // task panel
        contentScrollPane = ui(new JScrollPane(
            ui(new JPanel(new BorderLayout()), Color.WHITE, p -> p.add(tasksPanel, BorderLayout.NORTH))
        ), null, c -> {
            c.setBorder(null);
            c.getViewport().setBackground(Color.WHITE);
            c.getVerticalScrollBar().setUnitIncrement(16);
            c.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        });

        // main center panel (input panel + task panel)
        JPanel centerPanel = ui(new JPanel(new BorderLayout(0, 0)), BG_COLOR, p -> {
            p.setBorder(new EmptyBorder(10, 10, 0, 10)); // Remove bottom padding
            p.add(inputPanel, BorderLayout.NORTH);
            p.add(contentScrollPane, BorderLayout.CENTER);
        });

        // footer panel
        JPanel sortPanel = ui(new JPanel(new BorderLayout(5, 0)), BG_COLOR, p -> {
            p.add(new JLabel("Sort: "), BorderLayout.WEST);
            p.add(sortSelector, BorderLayout.CENTER);
        });
        
        JPanel leftFooterPanel = ui(new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)), BG_COLOR, lp -> {
            lp.add(showCompletedCheckbox);
            lp.add(Box.createHorizontalStrut(50)); // Spacing
            lp.add(sortPanel);
        });
        
        JPanel footerPanel = ui(new JPanel(new BorderLayout(5, 0)), BG_COLOR, bp -> {
            bp.add(leftFooterPanel, BorderLayout.WEST);
            bp.add(counterLabel, BorderLayout.EAST);
            bp.setBorder(new EmptyBorder(10, 10, 10, 10));
        });

        // 3. assemble main layout
        // --------------------------------------       

        // add main panels 
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);
        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    @Override
    public void update() {
        // get tasks from model
        List<Task> tasks = model.getTasks();
        final int[] counts = new int[2]; // [completed, pending]
        boolean showCompleted = showCompletedCheckbox.isSelected();
        
        // UI updates
        SwingUtilities.invokeLater(() -> {
            // save scroll position
            JScrollBar verticalScrollBar = contentScrollPane.getVerticalScrollBar();
            int scrollPosition = verticalScrollBar.getValue();

            // clear existing tasks
            tasksPanel.removeAll();
            
            // add tasks
            for (Task task : tasks) {
                counts[task.isCompleted() ? 0 : 1]++;
                if (task.isCompleted() && !showCompleted) continue;
                
                tasksPanel.add(createTaskEntry(task));
                tasksPanel.add(Box.createRigidArea(new Dimension(0, 1)));
            }
            
            // update counter and refresh UI
            counterLabel.setText(counts[1] + " pending · " + counts[0] + " completed");
            tasksPanel.revalidate();
            tasksPanel.repaint();

            // restore scroll position
            SwingUtilities.invokeLater(() -> verticalScrollBar.setValue(scrollPosition));
        });
    }
    
    // create a task entry
    private JPanel createTaskEntry(Task task) {
        boolean completed = task.isCompleted();
        Color bg = completed ? COMPLETED_TASK_BG : ACTIVE_TASK_BG;
        
        // create a task entry panel
        JPanel entry = ui(new JPanel(new BorderLayout()), bg, p -> {
            p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(8, 8, 8, 8)));
            p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            
            // add checkbox
            p.add(ui(new JCheckBox(), bg, c -> {
                c.setSelected(completed);
                c.setMargin(new Insets(0, 5, 0, 12));
                c.setVerticalAlignment(SwingConstants.CENTER);
                c.addActionListener(e -> controller.setTaskCompleted(task, c.isSelected()));
            }), BorderLayout.WEST);
            
            // task description
            p.add(ui(new JPanel(new GridBagLayout()), bg, dw -> { // Create wrapper panel
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL; 
                gbc.weightx = 1.0; 
                dw.add(ui(new JTextArea(task.getDescription()), bg, d -> {
                    d.setWrapStyleWord(true);
                    d.setLineWrap(true);
                    d.setEditable(false);
                    d.setFocusable(false);
                    d.setMargin(new Insets(5, 5, 5, 5));
                    d.setFont(createFont(Font.PLAIN, 14));
                    d.setForeground(completed ? Color.GRAY : Color.BLACK);
                    d.setBorder(null);
                }), gbc); // Add the configured JTextArea using the GBC
            }), BorderLayout.CENTER);
            
            // add edit and delete links/buttons
            p.add(ui(new JPanel(), bg, l -> {
                l.setLayout(new BoxLayout(l, BoxLayout.X_AXIS));
                l.setAlignmentY(Component.CENTER_ALIGNMENT);
                l.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 10));
                
                // edit dialog 
                JButton editBtn = ui(new JButton("Edit"), null, b -> {
                    setupButton(b, createFont(Font.BOLD, 14), EDIT_COLOR);
                    b.addActionListener(e -> {
                        JTextArea textArea = new JTextArea(task.getDescription(), 5, 30);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        if (JOptionPane.showConfirmDialog(this, new JScrollPane(textArea), 
                            "Edit Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                            String newText = textArea.getText().trim();
                            if (!newText.isEmpty()) controller.editTask(task, newText);
                        }
                    });
                });
                
                // delete button 
                JButton delBtn = ui(new JButton("Delete"), null, b -> {
                    setupButton(b, createFont(Font.BOLD, 14), DELETE_COLOR);
                    b.addActionListener(e -> controller.deleteTask(task));
                });
                
                // add buttons to layout
                l.add(editBtn);
                l.add(Box.createHorizontalStrut(20));
                l.add(delBtn);
            }), BorderLayout.EAST);
        });
        
        return entry;
    }
}
