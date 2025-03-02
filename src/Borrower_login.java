import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Borrower_login extends JFrame implements ActionListener {
    private JLabel label1, label2, label3, label4, label5;
    private JTextField textField1, textField2, textField3, textField4, textField5;
    private JButton issueButton, returnButton, viewButton, exitButton;
    private JPanel panel;

    // JDBC Variables
    private Connection connection;
    private PreparedStatement insertStatement, deleteStatement;

    // Borrower name (passed from welcome screen)
    private String borrowerName;

    public Borrower_login(String borrowerName) {
        this.borrowerName = borrowerName; // Store the borrower name
        setTitle("Library Management System");
        setSize(600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        label1 = new JLabel("Book ID");
        label2 = new JLabel("Book Title");
        label3 = new JLabel("Author");
        label4 = new JLabel("Publisher");
        label5 = new JLabel("Date of Issue (yyyy-mm-dd)");

        textField1 = new JTextField(10);
        textField2 = new JTextField(20);
        textField3 = new JTextField(20);
        textField4 = new JTextField(20);
        textField5 = new JTextField(10);

        issueButton = new JButton("Issue Book");
        returnButton = new JButton("Return Book");
        viewButton = new JButton("View Issued Books");
        exitButton = new JButton("Exit");

        issueButton.addActionListener(this);
        returnButton.addActionListener(this);
        viewButton.addActionListener(this);
        exitButton.addActionListener(this);

        panel = new JPanel(new GridLayout(10, 2));
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
        panel.add(issueButton);
        panel.add(returnButton);
        panel.add(viewButton);
        panel.add(exitButton);

        add(panel);
        setVisible(true);

        // Establish database connection
        connectToDatabase();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == issueButton) {
            // Issue book logic
            issueBook();
        } else if (e.getSource() == returnButton) {
            // Return book logic
            returnBook();
        } else if (e.getSource() == viewButton) {
            // View issued books logic for this borrower
            viewIssuedBooks();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    private void issueBook() {
        try {
            int bookID = Integer.parseInt(textField1.getText());
            String dateOfIssue = textField5.getText();

            if (!isValidDate(dateOfIssue)) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format. Please enter the date in yyyy-mm-dd format.");
                return;
            }

            String bookTitle = textField2.getText();
            String author = textField3.getText();
            String publisher = textField4.getText();

            // Check if the book is available in the books table
            String checkAvailabilitySQL = "SELECT * FROM books WHERE book_id = ?";
            PreparedStatement checkAvailabilityStatement = connection.prepareStatement(checkAvailabilitySQL);
            checkAvailabilityStatement.setInt(1, bookID);
            ResultSet availabilityResultSet = checkAvailabilityStatement.executeQuery();

            if (availabilityResultSet.next()) {
                // If the book exists, insert into user table
                insertStatement.setInt(1, bookID);
                insertStatement.setString(2, bookTitle);
                insertStatement.setString(3, author);
                insertStatement.setString(4, publisher);
                insertStatement.setString(5, borrowerName); // Associate book with borrower
                insertStatement.setDate(6, java.sql.Date.valueOf(dateOfIssue));

                int rowsAffected = insertStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Book issued successfully.");
                }

            } else {
                // If the book does not exist
                JOptionPane.showMessageDialog(this, "The book is not available currently.");
            }

            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Book ID must be a number.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void returnBook() {
        try {
            int bookID = Integer.parseInt(textField1.getText());

            // Delete the book from the user table if it exists
            deleteStatement.setInt(1, bookID);
            deleteStatement.setString(2, borrowerName); // Ensure it's for the current borrower
            int rowsAffected = deleteStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Book returned successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "The book hasn't been borrowed by you.");
            }

            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Book ID must be a number.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void viewIssuedBooks() {
        try {
            String selectSQL = "SELECT * FROM user WHERE borrower_name = ?";
            PreparedStatement viewIssuedBooksStatement = connection.prepareStatement(selectSQL);
            viewIssuedBooksStatement.setString(1, borrowerName);
            ResultSet resultSet = viewIssuedBooksStatement.executeQuery();

            // Create a DefaultTableModel to hold the book data
            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Book ID", "Book Title", "Author", "Publisher", "Date of Issue"}, 0);

            // Populate the table model with data from the ResultSet
            while (resultSet.next()) {
                int bookID = resultSet.getInt("book_id");
                String bookTitle = resultSet.getString("book_title");
                String author = resultSet.getString("author");
                String publisher = resultSet.getString("publisher");
                Date dateOfIssue = resultSet.getDate("date_of_issue");

                // Add the row to the table model
                tableModel.addRow(new Object[]{bookID, bookTitle, author, publisher, dateOfIssue});
            }

            // Create a JTable with the populated model
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);

            // Create a new JFrame to display the table
            JFrame frame = new JFrame("Issued Books");
            frame.add(scrollPane);
            frame.setSize(500, 300);
            frame.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void clearFields() {
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        textField4.setText("");
        textField5.setText("");
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/library_db";
            String user = "root";
            String password = "your_password";
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, user, password);

            String insertSQL = "INSERT INTO user (book_id, book_title, author, publisher, borrower_name, date_of_issue) VALUES (?, ?, ?, ?, ?, ?)";
            insertStatement = connection.prepareStatement(insertSQL);

            String deleteSQL = "DELETE FROM user WHERE book_id = ? AND borrower_name = ?";
            deleteStatement = connection.prepareStatement(deleteSQL);

            System.out.println("Connected to the database successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Connection Error: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "JDBC Driver not found: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Borrower_login("John Doe");
    }
}
