import java.awt.BorderLayout;
import java.awt.Dimension; // Import Dimension for setting fixed height
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import components.queue.Queue;
import components.queue.Queue1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * ToDoV7 - A simple ordered to-do list application.
 *
 * @author Dylan Earl
 */
public final class ToDoV8 {

    /**
     * +++++++++++++++++++++++++++++++++++++++++++++++++++
     *
     * personal tODO:
     *
     * -figure out how to make java programs into .exe file
     *
     * +++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    /**
     * +++++++++++++++++++++++++++++++++++++++++++++++++++
     *
     * Next Project Ideas:
     *
     * -create GUI for priority
     *
     * -update ordering to consider priority
     *
     * -create GUI for date
     *
     * -update ordering to consider date
     *
     * +++++++++++++++++++++++++++++++++++++++++++++++++++
     */

    /**
     * ToDo v8.
     */
    private ToDoV8() {
    }

    /**
     * Draws the window.
     *
     * @param q
     *            - queue of to-do tasks
     */
    public static void drawWindow(Queue<String> q) {
        /**
         * setup window
         */
        JFrame window = new JFrame();
        window.setTitle("Dylan Earl's Ordered To-Do List V8");
        final int width = 400;
        final int height = 400;
        window.setSize(width, height);
        window.setResizable(true);
        final int startingPosition = 50;
        window.setLocation(startingPosition, startingPosition);
        window.setLayout(new BorderLayout());

        /**
         * set up windows
         */
        JPanel toDoPanel = new JPanel();
        toDoPanel.setLayout(new BoxLayout(toDoPanel, BoxLayout.Y_AXIS));
        JTextField inputToDo = new JTextField();
        inputToDo.addActionListener(createAddTaskListener(inputToDo, toDoPanel, q));

        /**
         * set up scroll pane
         */
        JScrollPane toDoScroll = new JScrollPane(toDoPanel);
        toDoScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        toDoScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        final int minusHeight = 100; // Set a preferred size for the scroll pane

        // Adjust height as needed
        toDoScroll.setPreferredSize(new Dimension(width, height - minusHeight));

        /**
         * setup add button
         */
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(createAddTaskListener(inputToDo, toDoPanel, q));

        /**
         * Add initial tasks to the panel in correct order. Iterate from front
         * to end and add each task.
         */
        Queue<String> tempQueue = new Queue1L<>();
        while (q.length() > 0) {
            String task = q.dequeue();
            addTaskToPanel(toDoPanel, q, task);
            tempQueue.enqueue(task);
        }
        q.transferFrom(tempQueue);

        /**
         * add panels to window and initialize window
         */
        window.add(toDoScroll, BorderLayout.CENTER);
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputToDo, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        window.add(inputPanel, BorderLayout.SOUTH);
        window.setVisible(true);
    }

    /**
     * Creates an ActionListener for adding tasks to the ToDo panel.
     *
     * @param inputToDo
     *            - JTextField for input
     * @param toDoPanel
     *            - JPanel where tasks are displayed
     * @param q
     *            - Queue of tasks
     * @return ActionListener for adding tasks
     */
    private static ActionListener createAddTaskListener(JTextField inputToDo,
            JPanel toDoPanel, Queue<String> q) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String newTask = inputToDo.getText();

                if (!newTask.isEmpty()) {
                    addTaskToPanel(toDoPanel, q, newTask);
                    q.enqueue(newTask);

                    // Update the toDo.txt file
                    updateToDoFile(q);

                    inputToDo.setText(""); // Clear the input field after adding
                }
            }
        };
    }

    /**
     * Adds task to panel.
     *
     * @param toDoPanel
     *            the panel to which the task will be added
     * @param q
     *            the queue containing the tasks
     * @param task
     *            the task to be added
     */
    private static void addTaskToPanel(JPanel toDoPanel, Queue<String> q, String task) {

        /**
         * setup taskPanel
         */
        JPanel taskPanel = new JPanel(new BorderLayout());
        final int taskHeight = 30; // Adjust the height to fit one line of text
        taskPanel.setPreferredSize(
                new Dimension(taskPanel.getPreferredSize().width, taskHeight));
        taskPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, taskHeight));

        /**
         * setup taskField
         */
        JTextField taskField = new JTextField(task);
        taskField.setEditable(false);
        taskField.setPreferredSize(
                new Dimension(taskField.getPreferredSize().width, taskHeight));
        taskField.setMaximumSize(new Dimension(Integer.MAX_VALUE, taskHeight));

        /**
         * setup remove Button
         */
        JButton removeButton = new JButton("X");
        removeButton.addActionListener(e -> removeTask(toDoPanel, q, task, taskPanel));

        /**
         * add everything to toDoPanel
         */
        taskPanel.add(taskField, BorderLayout.CENTER);
        taskPanel.add(removeButton, BorderLayout.EAST);
        toDoPanel.add(taskPanel, toDoPanel.getComponentCount());
        toDoPanel.revalidate(); // Ensure the panel updates
        toDoPanel.repaint();
    }

    /**
     * Removes a task from the panel and updates the queue and toDo file.
     *
     * @param toDoPanel
     *            the panel from which the task will be removed
     * @param q
     *            the queue containing the tasks
     * @param task
     *            the task to be removed
     * @param taskPanel
     *            the panel representing the task
     */
    private static void removeTask(JPanel toDoPanel, Queue<String> q, String task,
            JPanel taskPanel) {
        toDoPanel.remove(taskPanel);
        toDoPanel.revalidate();
        toDoPanel.repaint();

        // Remove the task from the queue
        Queue<String> tempQueue = new Queue1L<>();
        while (q.length() > 0) {
            String currentTask = q.dequeue();
            if (!currentTask.equals(task)) {
                tempQueue.enqueue(currentTask);
            }
        }
        q.transferFrom(tempQueue);

        // Update the toDo.txt file
        updateToDoFile(q);
    }

    /**
     * updates todo file.
     *
     * @param q
     *            - the queue to add to the file
     */
    private static void updateToDoFile(Queue<String> q) {

        SimpleWriter fileOut = new SimpleWriter1L("toDo.txt");
        for (String str : q) {
            fileOut.println(str);
        }
        fileOut.close();

    }

    /**
     * main method.
     *
     * @param args
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        Queue<String> q = new Queue1L<>();

        String filePath = "toDo.txt";
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("The file has been created.");
                } else {
                    System.out.println("The file could not be created.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SimpleReader fileIn = new SimpleReader1L("toDo.txt");

        // Read file contents into the queue
        while (!fileIn.atEOS()) {
            String line = fileIn.nextLine();
            if (!line.isEmpty()) { // Avoid adding empty lines
                q.enqueue(line);
            }
        }
        fileIn.close();

        drawWindow(q);

        in.close();
        out.close();
    }
}
