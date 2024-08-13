package HotelCopy;

import java.sql.*;
import java.util.Scanner;

public class LoginClass extends Information {
    static  String loggedInUserName;

    public void login(Connection connection, Scanner sc) {
        System.out.print("Enter the Username: ");
        String UserName = sc.nextLine();

        System.out.print("Enter the Password: ");
        String Password = sc.nextLine();

        String query = "SELECT * FROM createaccount WHERE UserName = ? AND Password = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, UserName);
            preparedStatement.setString(2, Password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Login Successful!!");
                    loggedInUserName = UserName;  // Store the username in the instance variable
                    Room(); // Call Room after successful login
                } else {
                    System.out.println("Wrong Username or Password, please try again.");
                    login(connection, sc); // Retry login
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred.");
        }
    }
}
