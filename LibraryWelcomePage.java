import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class LibraryWelcomePage extends JFrame implements ActionListener {
    private JLabel welcomeLabel;
    private JButton librarianLoginButton, borrowerLoginButton;

    // Database connection
    private Connection connection;

    public LibraryWelcomePage() {
        // Initialize database connection
        connectToDatabase();

        setTitle("Library Management System");
        setSize(600, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on the screen

        // Create welcome label
        welcomeLabel = new JLabel("Welcome to our Library", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Create buttons
        librarianLoginButton = new JButton("Librarian Login");
        borrowerLoginButton = new JButton("Borrower Login");

        // Add action listeners
        librarianLoginButton.addActionListener(this);
        borrowerLoginButton.addActionListener(this);

        // Create panel with GridBagLayout for better alignment
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Configure GridBagConstraints
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding

        // Add welcome label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span two columns
        panel.add(welcomeLabel, gbc);

        // Add librarian login button
        gbc.gridwidth = 1; // Reset to default for next components
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(librarianLoginButton, gbc);

        // Add borrower login button
        gbc.gridx = 1;
        panel.add(borrowerLoginButton, gbc);

        // Add panel to frame
        add(panel);
        setVisible(true);
    }

    // Establish connection to the database
    private void connectToDatabase() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root", "your_password");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    // Handle button click events
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == librarianLoginButton) {
            // Ask for username and password
            String username = JOptionPane.showInputDialog(this, "Enter your username:");
            String password = JOptionPane.showInputDialog(this, "Enter your password:");

            // Validate credentials
            if (isValidCredentials(username, password)) {
                new Librarian_login(); // Open the Librarian Login page
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.");
            }
        } else if (e.getSource() == borrowerLoginButton) {
            handleBorrowerLogin(); // Call the new method for borrower login
        }
    }

    // Method to validate username and password
    private boolean isValidCredentials(String username, String password) {
        return (username.equals("librarian1") && password.equals("password1")) ||
               (username.equals("librarian2") && password.equals("password2"));
    }

    // Method to handle borrower login
    private void handleBorrowerLogin() {
        String borrowerName = JOptionPane.showInputDialog(this, "Enter name:");
        if (borrowerName != null && !borrowerName.trim().isEmpty()) {
            String phoneNumber = JOptionPane.showInputDialog(this, "Enter phone number (10 digits):");
            if (isValidPhoneNumber(phoneNumber)) {
                // Store borrower details in the database
                storeBorrowerDetails(borrowerName, phoneNumber);
                new Borrower_login(borrowerName); // Open the Borrower Login page with borrower's name
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid 10-digit phone number.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid name.");
        }
    }

    // Method to validate phone number
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("\\d{10}");
    }

    // Store borrower details in the database
    private void storeBorrowerDetails(String name, String phoneNumber) {
        try {
            String query = "INSERT INTO user_details (name, phone_number) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, phoneNumber);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Borrower details saved successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save borrower details: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new LibraryWelcomePage();
    }
}
