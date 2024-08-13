package HotelCopy;

import java.sql.*;
import java.util.Scanner;

public class Information {
    static String url = "jdbc:mysql://localhost:3306/hoteldb";
    static String user = "root";
    static String password = "#@solunke8010";
    static Connection connection = null;
    static Scanner sc = null;

    public static void main(String[] args) {
        Info info = new Info();
        info.Choice();
        // You should not call Room() here since it needs to be called after login.
    }

    static void Room() {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("1. Room booking");
            System.out.println("2. Check Room is Booked or Not");
            System.out.println("3. Exit");
            System.out.print("Select the option: ");
            int option = sc.nextInt();
            sc.nextLine();  // Consume the newline character

            switch (option) {
                case 1:
                    RoomBooking(connection, sc);
                    break;
                case 2:
                    CheckRoom(connection, sc);
                    break;
                case 3:
                    exit();
                    break;
                default:
                    System.out.println("Enter a valid option.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    static void RoomBooking(Connection connection, Scanner sc) {
        System.out.println("Rooms available now:");

        // Access the logged-in username from LoginClass
        String userName = LoginClass.loggedInUserName; // Assuming this variable holds the logged-in user's name

        // Check if the user is logged in
        if (userName == null) {
            System.out.println("You must be logged in to book a room.");
            return; // Exit if not logged in
        }

        String query = "SELECT * FROM rooms"; // Only show available rooms
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int roomNO = rs.getInt("RoomNO");
                String beds = rs.getString("Beds");
                String ac = rs.getString("Ac");
                double price = rs.getDouble("Price");

                System.out.println("|----------------------------------------------------------------------------------|");
                System.out.println(" Room ID: " + roomNO + ", Beds: " + beds + ", Ac: " + ac + ", Price: " + price);
            }
            System.out.println();

            int roomId;
            boolean isBooked;
            do {
                System.out.print("Enter the Room ID you want to book: ");
                roomId = sc.nextInt();
                isBooked = CheckId(connection, roomId);

                if (isBooked) {
                    System.out.println("Room is already booked, please choose another room.");
                }
            } while (isBooked);

            // Continue with the booking process if the room is not booked
            sc.nextLine();  // Consume the newline character

            // Booking the room
            String updateQuery = "INSERT INTO bookedrooms (RoomNO, Beds, Ac, Price, UserName) " +
                    "SELECT RoomNO, Beds, Ac, Price, ? " +  // Insert username
                    "FROM rooms " +
                    "WHERE RoomNO = ?;"; // Assuming RoomNO is the ID

            try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                // Set the parameters for the prepared statement
                pstmt.setString(1, userName); // Set the username
                pstmt.setInt(2, roomId);       // Set the room ID
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Room booked successfully!");
                } else {
                    System.out.println("Booking failed. Please try again.");
                }
                System.out.println();
                Room(); // Call to the next method after booking
            }

        } catch (SQLException e) {
            System.out.println("An error occurred during room booking: " + e.getMessage());
        }
    }



    static boolean CheckId(Connection connection, int roomId) {
        String query = "SELECT * FROM bookedrooms WHERE RoomNo = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, roomId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true; // Room is booked
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while checking the room ID: " + e.getMessage());
        }
        return false; // Room is not booked
    }



    static void CheckRoom(Connection connection, Scanner sc) {
        // Access the logged-in username
        String userName = LoginClass.loggedInUserName; // Assuming this variable holds the logged-in user's name

        // Check if the user is logged in
        if (userName == null) {
            System.out.println("You must log in first.");
            return;
        }

        // SQL query to check booked rooms for the logged-in user
        String query = "SELECT * FROM bookedrooms WHERE UserName = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);  // Set the logged-in username in the query

            try (ResultSet rs = pstmt.executeQuery()) { // Execute the query
                if (rs.next()) { // Check if any results are returned
                    System.out.println("Rooms booked under your username:");
                    do {
                        int roomNo = rs.getInt("RoomNO"); // Get the RoomNO from the result set
                        String bookedUserName = rs.getString("UserName"); // Get the UserName
                        // Add more fields if needed
                        System.out.println("Room ID: " + roomNo + " is booked by " + bookedUserName);
                    } while (rs.next()); // Loop through all results
                } else {
                    System.out.println("No rooms booked under your username.");
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while checking the room: " + e.getMessage());
        } finally {
            Room(); // Call the Room method regardless of the outcome
        }
    }


    static void exit() {
        System.out.println("Exiting the system. Thank you!");
    }
}
