import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;



public class HotelReservationSystem
{
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "Mubbashir@91";

    public static void main(String[] args)
            throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection con = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int ch = sc.nextInt();
                switch (ch) {
                    case 1:
                        reserveRoom(con, sc);
                        break;
                    case 2:
                        viewReservations(con);
                        break;
                    case 3:
                        getRoomNumber(con, sc);
                        break;
                    case 4:
                        updateReservation(con, sc);
                        break;
                    case 5:
                        deleteReservation(con, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    private static void reserveRoom(Connection con, Scanner sc) {
        try {
            System.out.print("Enter guest name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = sc.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = sc.next();

            String query = "INSERT INTO reservation (guest_name, room_number, contact_number) " +
                    "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try (Statement st = con.createStatement()) {
                int affectedRows = st.executeUpdate(query);

                if (affectedRows > 0) {
                    System.out.println("Reservation successful!");
                } else {
                    System.out.println("Reservation failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection con)
            throws SQLException
    {
        String sql = "SELECT reser_id, guest_name, room_number, contact_number, reser_date FROM reservation";

        try (Statement st = con.createStatement();
             ResultSet res = st.executeQuery(sql)) {

            System.out.println("Current Reservations:");

            while (res.next()) {
                int reservationId = res.getInt("reser_id");
                String guestName = res.getString("guest_name");
                int roomNumber = res.getInt("room_number");
                String contactNumber = res.getString("contact_number");
                String reservationDate = res.getTimestamp("reser_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.println("Reservation ID : "+reservationId +"\nGuest Name: "+ guestName +"\nRoom Number: "+ roomNumber +"\nContact Number: "+ contactNumber +"\nTiming: "+ reservationDate+ "\n");
            }
        }
    }


    private static void getRoomNumber(Connection con, Scanner sc) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = sc.next();

            String sql = "SELECT room_number FROM reservation " +
                    "WHERE reser_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement st = con.createStatement();
                 ResultSet res = st.executeQuery(sql)) {

                if (res.next()) {
                    int roomNumber = res.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection con, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine(); // Consume the newline character

            if (!reservationExists(con, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = sc.next();

            String query = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reser_idreser_id = " + reservationId;

            try (Statement st = con.createStatement()) {
                int affectedRows = st.executeUpdate(query);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection con, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = sc.nextInt();

            if (!reservationExists(con, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String query = "DELETE FROM reservation WHERE reser_id = " + reservationId;

            try (Statement st = con.createStatement()) {
                int affectedRows = st.executeUpdate(query);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection con, int reservationId) {
        try {
            String query = "SELECT reser_id FROM reservation WHERE reser_id = " + reservationId;

            try (Statement st = con.createStatement();
                 ResultSet res= st.executeQuery(query)) {

                return res.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }


    public static void exit()
            throws InterruptedException
    {
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}
