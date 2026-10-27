/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package todolistconnection;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ToDoListConnection extends JFrame {

    private final String url =
            "jdbc:mysql://localhost:3306/todo_list_db";
    private final String user = "root";
    private final String password = "ruthu";

    private final JTextField taskField = new JTextField(15);
    private final JComboBox<String> statusBox =
            new JComboBox<>(new String[]{"Pending", "Completed"});

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Task Name", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public ToDoListConnection() {
        setTitle("To-Do List System");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Task Name:"));
        inputPanel.add(taskField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusBox);

        JButton addButton = new JButton("Add");
        JButton viewButton = new JButton("View");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addTask());
        viewButton.addActionListener(e -> viewTasks());
        updateButton.addActionListener(e -> updateTask());
        deleteButton.addActionListener(e -> deleteTask());

        viewTasks();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // INSERT: Add a task
    private void addTask() {
        String task = taskField.getText().trim();
        String status = statusBox.getSelectedItem().toString();

        if (task.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a task name!");
            return;
        }

        String sql =
                "INSERT INTO tasks (task_name, status) VALUES (?, ?)";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, task);
            ps.setString(2, status);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Task added successfully!");
            taskField.setText("");
            viewTasks();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // SELECT: Display all tasks
    private void viewTasks() {
        model.setRowCount(0);

        String sql = "SELECT * FROM tasks ORDER BY id";

        try (Connection con = connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("task_name"),
                    rs.getString("status")
                });
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // UPDATE: Change task name or status
    private void updateTask() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a task from the table first!");
            return;
        }

        String task = taskField.getText().trim();

        if (task.isEmpty()) {
            task = model.getValueAt(row, 1).toString();
        }

        String status = statusBox.getSelectedItem().toString();
        int id = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        String sql =
                "UPDATE tasks SET task_name = ?, status = ? WHERE id = ?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, task);
            ps.setString(2, status);
            ps.setInt(3, id);

            int count = ps.executeUpdate();

            if (count > 0) {
                JOptionPane.showMessageDialog(this, "Task updated!");
                taskField.setText("");
                viewTasks();
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // DELETE: Remove a selected task
    private void deleteTask() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a task from the table first!");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this, "Do you want to delete this task?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        int id = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int count = ps.executeUpdate();

            if (count > 0) {
                JOptionPane.showMessageDialog(this, "Task deleted!");
                taskField.setText("");
                viewTasks();
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ToDoListConnection().setVisible(true);
        });
    }
}
