import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Librarian_login extends JFrame implements ActionListener {
    private JLabel label1, label2, label3, label4, label5, label6, label7;
    private JTextField textField1, textField2, textField3, textField4, textField5, textField6, textField7;
    private JButton addButton, viewButton, editButton, deleteButton, exitButton, usersButton;
    private JPanel panel;

    // Database connection
    private Connection connection;

    public Librarian_login() {
        // Initialize database connection
        connectToDatabase();

        setTitle("Library Management System");
        setSize(600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        label1 = new JLabel("Book ID");
        label2 = new JLabel("Book Title");
        label3 = new JLabel("Author");
        label4 = new JLabel("Publisher");
        label5 = new JLabel("Year of Publication");
        label6 = new JLabel("ISBN");
        label7 = new JLabel("Number of Copies");

        textField1 = new JTextField(10);
        textField2 = new JTextField(20);
        textField3 = new JTextField(20);
        textField4 = new JTextField(20);
        textField5 = new JTextField(10);
        textField6 = new JTextField(20);
        textField7 = new JTextField(10);

        addButton = new JButton("Add Book");
        viewButton = new JButton("View Inventory");
        editButton = new JButton("Edit Book");
        deleteButton = new JButton("Delete Book");
        exitButton = new JButton("Exit");
        usersButton = new JButton("View Users");

        addButton.addActionListener(this);
        viewButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
        exitButton.addActionListener(this);
        usersButton.addActionListener(this);

        panel = new JPanel(new GridLayout(0, 2)); // Dynamic grid rows based on the number of components

        // Add components to the panel
        panel.add(label1);
        panel.add(textField1);
        panel.add(label2);
        panel.add(textField2);
        panel.add(label3);
        panel.add(textField3);
        panel.add(label4);
        panel.add(textField4);
        panel.add(label5);
        panel.add(textField5);
        panel.add(label6);
        panel.add(textField6);
        panel.add(label7);
        panel.add(textField7);
        panel.add(addButton);
        panel.add(viewButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(usersButton);
        panel.add(exitButton); 

        // Set the exit button to span two columns
        exitButton.setPreferredSize(new Dimension(240, 50)); 
        panel.add(exitButton);

        add(panel);
        setVisible(true);
    }

    // Establish connection to the database
    private void connectToDatabase() {
        try {
            // Load the JDBC driver (e.g., MySQL)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root", "your_password");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addBook();
        } else if (e.getSource() == viewButton) {
            viewBooks();
        } else if (e.getSource() == editButton) {
            editBook();
        } else if (e.getSource() == deleteButton) {
            deleteBook();
        } else if (e.getSource() == exitButton) {
            exitApplication();
        } else if (e.getSource() == usersButton) {
            viewUsers();
        }
    }

    // Add book to the database
    private void addBook() {
        try {
            String query = "INSERT INTO BOOKS (book_id, title, author, publisher, year_of_publication, isbn, number_of_copies) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, textField1.getText());
            statement.setString(2, textField2.getText());
            statement.setString(3, textField3.getText());
            statement.setString(4, textField4.getText());
            statement.setString(5, textField5.getText());
            statement.setString(6, textField6.getText());
            statement.setString(7, textField7.getText());
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book added successfully");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add book: " + ex.getMessage());
        }
    }

    // View books from the database
    private void viewBooks() {
        try {
            String query = "SELECT * FROM BOOKS";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            String[] columns = {"Book ID", "Book Title", "Author", "Publisher", "Year of Publication", "ISBN", "Number of Copies"};
            ArrayList<Object[]> data = new ArrayList<>();
            
            while (resultSet.next()) {
                data.add(new Object[]{
                    resultSet.getString("book_id"),
                    resultSet.getString("title"),
                    resultSet.getString("author"),
                    resultSet.getString("publisher"),
                    resultSet.getString("year_of_publication"),
                    resultSet.getString("isbn"),
                    resultSet.getString("number_of_copies")
                });
            }

            // Convert ArrayList to 2D array
            Object[][] bookData = data.toArray(new Object[0][]);
            JTable table = new JTable(bookData, columns);
            JScrollPane scrollPane = new JScrollPane(table);
            JFrame frame = new JFrame("View Books");
            frame.add(scrollPane);
            frame.setSize(800, 400);
            frame.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve books: " + ex.getMessage());
        }
    }

    // Edit book details in the database
    private void editBook() {
        String bookID = JOptionPane.showInputDialog(this, "Enter book ID to edit:");
        if (bookID == null || bookID.trim().isEmpty()) {
            return; // Exit if the input is invalid
        }

        try {
            // Check if the book exists
            String query = "SELECT * FROM BOOKS WHERE book_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, bookID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Populate text fields with book details
                textField1.setText(resultSet.getString("book_id"));
                textField2.setText(resultSet.getString("title"));
                textField3.setText(resultSet.getString("author"));
                textField4.setText(resultSet.getString("publisher"));
                textField5.setText(resultSet.getString("year_of_publication"));
                textField6.setText(resultSet.getString("isbn"));
                textField7.setText(resultSet.getString("number_of_copies"));
                // Save the edited book on field change
                saveEditedBook(); // Call to save edited book details
            } else {
                JOptionPane.showMessageDialog(this, "Book not found");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve book: " + ex.getMessage());
        }
    }

    // Save edited book details back to the database
    private void saveEditedBook() {
        String bookID = textField1.getText().trim();
        try {
            String query = "UPDATE BOOKS SET title = ?, author = ?, publisher = ?, year_of_publication = ?, isbn = ?, number_of_copies = ? WHERE book_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, textField2.getText());
            statement.setString(2, textField3.getText());
            statement.setString(3, textField4.getText());
            statement.setString(4, textField5.getText());
            statement.setString(5, textField6.getText());
            statement.setString(6, textField7.getText());
            statement.setString(7, bookID);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Book updated successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update book");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update book: " + ex.getMessage());
        }
    }

    // Delete book from the database
    private void deleteBook() {
        String bookID = JOptionPane.showInputDialog(this, "Enter book ID to delete:");
        if (bookID == null || bookID.trim().isEmpty()) {
            return; // Exit if the input is invalid
        }

        try {
            String query = "DELETE FROM BOOKS WHERE book_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, bookID);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Book not found");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete book: " + ex.getMessage());
        }
    }


    // Exit the application
    private void exitApplication() {
        int confirmed = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    // View users in the user_details table
    private void viewUsers() {
        try {
            String query = "SELECT * FROM user_details";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            String[] columns = {"Name", "Phone Number"};
            ArrayList<Object[]> data = new ArrayList<>();
            
            while (resultSet.next()) {
                data.add(new Object[]{
                    resultSet.getString("name"),
                    resultSet.getString("phone_number")
                });
            }

            // Convert ArrayList to 2D array
            Object[][] userData = data.toArray(new Object[0][]);
            JTable table = new JTable(userData, columns);
            JScrollPane scrollPane = new JScrollPane(table);
            JFrame frame = new JFrame("User Records");
            frame.add(scrollPane);
            frame.setSize(600, 300);
            frame.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve users: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Librarian_login());
    }
}
