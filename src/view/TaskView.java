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
    private JTextArea taskField;
    private JCheckBox showCompletedCheckbox;
    private JLabel counterLabel;
    private JButton addButton;

    public TaskView(TaskModel model, TaskController controller) {
        this.model = model;
        this.controller = controller;
        model.addObserver(this);
        try {UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());} 
        catch (Exception e) {e.printStackTrace();}
        
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
    
    private void initComponents() {
        // header
        JPanel headerPanel = ui(new JPanel(new FlowLayout(FlowLayout.LEFT)), HEADER_COLOR, p -> 
            p.add(ui(new JLabel("Task Manager"), null, c -> {
                c.setFont(new Font("Arial", Font.BOLD, 18));
                c.setForeground(Color.WHITE);
                c.setBorder(new EmptyBorder(10, 10, 10, 0));
            })));
        
        // task panel
        tasksPanel = ui(new JPanel(), Color.WHITE, p -> 
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)));
        
        JScrollPane scrollPane = ui(new JScrollPane(
            ui(new JPanel(new BorderLayout()), Color.WHITE, p -> p.add(tasksPanel, BorderLayout.NORTH))
        ), null, c -> {
            c.setBorder(null);
            c.getViewport().setBackground(Color.WHITE);
            c.getVerticalScrollBar().setUnitIncrement(16);
            c.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        });
        
        // input
        taskField = ui(new JTextArea(3, 30), null, c -> {
            c.setFont(new Font("Arial", Font.PLAIN, 14));
            c.setLineWrap(true);
            c.setWrapStyleWord(true);
            c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            c.addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        e.consume();
                        if (e.isShiftDown()) taskField.append("\n"); 
                        else addButton.doClick();
                    }
                }
            });
        });

        // add task button
        addButton = ui(new JButton("Add Task"), null, c -> {
            c.setFont(new Font("Arial", Font.BOLD, 14));
            c.setBackground(HEADER_COLOR);
            c.setForeground(Color.WHITE);
            c.setFocusPainted(false);
            c.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            c.addActionListener(e -> {
                String description = taskField.getText().trim();
                if (!description.isEmpty()) {
                    controller.addTask(description);
                    taskField.setText("");
                }
            });
        });
        
        // add task input
        JPanel inputPanel = ui(new JPanel(new BorderLayout(5, 0)), BG_COLOR, p -> {
            p.add(ui(new JScrollPane(taskField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), null, 
                c -> c.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)))), 
                BorderLayout.CENTER);
            p.add(addButton, BorderLayout.EAST);
        });
        
        // footer
        showCompletedCheckbox = ui(new JCheckBox("Show Completed Tasks", true), null, c -> {
            c.setFont(new Font("Arial", Font.PLAIN, 12));
            c.setBackground(BG_COLOR);
            c.addActionListener(e -> update());
        });
        
        counterLabel = ui(new JLabel(""), null, c -> {
            c.setFont(new Font("Arial", Font.PLAIN, 11));
            c.setForeground(new Color(150, 150, 150));
        });
        
        // main layout
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_COLOR);
        add(headerPanel, BorderLayout.NORTH);
        add(ui(new JPanel(new BorderLayout(0, 10)), BG_COLOR, p -> {
            p.setBorder(new EmptyBorder(10, 10, 10, 10));
            p.add(inputPanel, BorderLayout.NORTH);
            p.add(scrollPane, BorderLayout.CENTER);
            p.add(ui(new JPanel(new BorderLayout(5, 0)), BG_COLOR, bp -> {
                bp.add(showCompletedCheckbox, BorderLayout.WEST);
                bp.add(counterLabel, BorderLayout.EAST);
            }), BorderLayout.SOUTH);
        }), BorderLayout.CENTER);
    }
    
    @Override
    public void update() {
        // retrives the scroll position to set it after update (since after each update the scroll position was reset)
        JScrollPane scrollPane = (JScrollPane)SwingUtilities.getAncestorOfClass(JScrollPane.class, tasksPanel);
        final int scrollPosition = scrollPane != null ? scrollPane.getVerticalScrollBar().getValue() : 0;
        
        List<Task> tasks = model.getTasks();
        final int[] counts = new int[2]; // [completed, pending]
        boolean showCompleted = showCompletedCheckbox.isSelected();
        
        JPanel newTasksPanel = ui(new JPanel(), Color.WHITE, p -> 
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)));
        
        for (Task task : tasks) {
            counts[task.isCompleted() ? 0 : 1]++;
            if (task.isCompleted() && !showCompleted) continue;
            newTasksPanel.add(createTaskEntry(task));
            newTasksPanel.add(Box.createRigidArea(new Dimension(0, 1)));
        }
        
        // async
        SwingUtilities.invokeLater(() -> {
            tasksPanel.removeAll();
            for (Component c : newTasksPanel.getComponents()) tasksPanel.add(c);
            counterLabel.setText(counts[1] + " pending · " + counts[0] + " completed");
            tasksPanel.revalidate();
            tasksPanel.repaint();
            if (scrollPane != null) scrollPane.getVerticalScrollBar().setValue(scrollPosition);
        });
    }
    
    private JPanel createTaskEntry(Task task) {
        boolean completed = task.isCompleted();
        Color bg = completed ? COMPLETED_TASK_BG : ACTIVE_TASK_BG;
        
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
                c.addActionListener(e -> {
                    if (c.isSelected()) controller.markTaskCompleted(task);
                    else controller.markTaskIncomplete(task);
                });
            }), BorderLayout.WEST);
            
            // add task desciption
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = gbc.weighty = 1.0;
            p.add(ui(new JPanel(new GridBagLayout()), bg, w -> 
                w.add(ui(new JTextArea(task.getDescription()), bg, d -> {
                    d.setWrapStyleWord(true);
                    d.setLineWrap(true);
                    d.setEditable(false);
                    d.setFocusable(false);
                    d.setMargin(new Insets(5, 5, 5, 5));
                    d.setFont(new Font("Arial", Font.PLAIN, 14));
                    d.setForeground(completed ? Color.GRAY : Color.BLACK);
                    d.setBorder(null);
                }), gbc)), BorderLayout.CENTER);
            
            // add edit and delete links/buttons
            p.add(ui(new JPanel(new BorderLayout()), bg, btnPanel -> {
                btnPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
                JPanel links = ui(new JPanel(), bg, l -> {
                    l.setLayout(new BoxLayout(l, BoxLayout.X_AXIS));
                    l.setAlignmentY(Component.CENTER_ALIGNMENT);
                    l.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                    
                    Consumer<JButton> setupButton = b -> {
                        b.setFont(new Font("Arial", Font.BOLD, 14));
                        b.setContentAreaFilled(false);
                        b.setBorderPainted(false);
                        b.setFocusPainted(false);
                        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        b.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    };
                    
                    // edit dialog
                    JButton editBtn = ui(new JButton("Edit"), null, b -> {
                        setupButton.accept(b);
                        b.setForeground(EDIT_COLOR);
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
                    
                    JButton delBtn = ui(new JButton("Delete"), null, b -> {
                        setupButton.accept(b);
                        b.setForeground(DELETE_COLOR);
                        b.addActionListener(e -> controller.deleteTask(task));
                    });
                    
                    l.add(editBtn);
                    l.add(Box.createHorizontalStrut(20));
                    l.add(delBtn);
                });
                btnPanel.add(links, BorderLayout.CENTER);
            }), BorderLayout.EAST);
        });
        
        return entry;
    }
}
