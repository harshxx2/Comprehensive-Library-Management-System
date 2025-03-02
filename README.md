# Comprehensive-Library-Management-System
This is a Library Management System implemented in Java Swing that provides two user roles: Librarian and Borrower. The application allows a librarian to manage the inventory of books and track users. Borrowers can log in and borrow books, with their details stored in the system.

## Features
The project has three main pages:

### Welcome Page
Provides two login options:
1. Librarian Login
2. Borrower Login
   
![Screenshot 2024-10-07 202955](https://github.com/user-attachments/assets/f5d4d3c6-debd-4156-a1f0-c1a1cc8741a4)


   
### Librarian Page
Accessible only with a valid librarian username and password.

![Screenshot 2024-10-07 203009](https://github.com/user-attachments/assets/4623a386-43d5-4696-9ca0-87ab873b0629)


![Screenshot 2024-10-07 203022](https://github.com/user-attachments/assets/0f7ae78b-953e-407f-a068-42d560906d6c)



The librarian can:
1. Add a book: Add new books to the library inventory.
2. Edit a book: Update details of existing books.
3. Delete a book: Remove a book from the inventory.
4. View inventory: Display a list of all books stored in the books table.
5. View users: Display a list of all users stored in the user_details table.

![Screenshot 2024-10-07 203036](https://github.com/user-attachments/assets/dafed0a7-d334-4d0c-9f7f-d90176b2f5ba)



### Borrower Page
The borrower logs in by entering their name and phone number.

![Screenshot 2024-10-07 203050](https://github.com/user-attachments/assets/d34aa1ee-96a6-4495-8f5a-bd37e2e69228)



The borrower's details are stored in the user_details table, including books issued to them.
The borrower can issue and return books, which are then tracked in the system.

![Screenshot 2024-10-07 203120](https://github.com/user-attachments/assets/d31bd9ae-5cb2-4b8d-8dc6-1e1b407dfb90)



## Database Management

### Set Up the Database
Ensure that a MySQL or compatible database is installed.
Use the provided SQL queries to create the necessary tables in the library_db database.

![Screenshot 2024-10-07 210816](https://github.com/user-attachments/assets/6e72db84-dc1b-4912-b766-ed463c025f95)


### Configure Database Connection
Open the Database.java file and modify the JDBC connection details (URL, username, password) to match your local database configuration.

### Install Java
Make sure Java (JDK 8 or higher) is installed.

### Run the Application
Compile and run the Main.java file to launch the application.
