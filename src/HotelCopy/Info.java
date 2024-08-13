package HotelCopy;

import java.sql.*;
import java.util.Scanner;

class Info extends Information {
    private LoginClass loginClass = new LoginClass();

    public void Choice() {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("1. Login\n2. Create Account\nEnter your choice:");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            if (choice == 1) {
                loginClass.login(connection, sc);
            } else if (choice == 2) {
                createAccount(connection, sc);
            } else {
                System.out.println("Invalid choice!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database.");
        }
    }

    public void createAccount(Connection connection, Scanner sc) {
        String UserName;
        boolean userNameExists;

        // Check if the username is unique
        do {
            System.out.print("Enter Username: ");
            UserName = sc.nextLine();
            userNameExists = checkUserNameExists(connection, UserName);

            if (userNameExists) {
                System.out.println("This username is already taken. Please choose a different username.");
            }
        } while (userNameExists);

        System.out.print("Enter the First Name: ");
        String FirstName = sc.nextLine();

        System.out.print("Enter the Last Name: ");
        String LastName = sc.nextLine();

        String Email;
        boolean emailExists;

        // Check if the email is unique
        do {
            System.out.print("Enter Email: ");
            Email = sc.nextLine();
            emailExists = checkEmailExists(connection, Email);

            if (emailExists) {
                System.out.println("This email is already in use. Please enter a different email.");
            }
        } while (emailExists);

        String Password;
        String reEnterPassword;

        // Repeat until passwords match
        do {
            System.out.print("Enter Password: ");
            Password = sc.nextLine();
            System.out.print("Re-enter Password: ");
            reEnterPassword = sc.nextLine();

            if (!Password.equals(reEnterPassword)) {
                System.out.println("Passwords do not match. Please try again.");
            }
        } while (!Password.equals(reEnterPassword));

        String query = "INSERT INTO createaccount (UserName, FirstName, LastName, Email, Password) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, UserName);
            preparedStatement.setString(2, FirstName);
            preparedStatement.setString(3, LastName);
            preparedStatement.setString(4, Email);
            preparedStatement.setString(5, Password);  // Consider hashing the password

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Account created successfully!");
                loginClass.login(connection, sc);  // Log the user in after account creation
            } else {
                System.out.println("Something went wrong, please try again!");
            }

        } catch (SQLException e) {
            System.out.println("An SQL error occurred: " + e.getMessage());
        }
    }

    private boolean checkUserNameExists(Connection connection, String UserName) {
        String query = "SELECT * FROM createaccount WHERE UserName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, UserName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();  // Returns true if a record with the username is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while checking the username.");
            return true;  // Assume the username exists if there's an error
        }
    }

    private boolean checkEmailExists(Connection connection, String Email) {
        String query = "SELECT * FROM createaccount WHERE Email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, Email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();  // Returns true if a record with the email is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while checking the email.");
            return true;  // Assume the email exists if there's an error
        }
    }
}
