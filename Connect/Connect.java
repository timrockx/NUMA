import java.sql.*;
import java.util.*;

public class Connect {

    // static string for link to database
    static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";
       public static void main(String[] args) {

        // instantiate connection and scanner
        Connection conn = null;
        Scanner in = new Scanner(System.in);

        do {
            try {
                // get user and password for database
                System.out.println("Enter Oracle User ID: ");
                String user = in.nextLine();
                System.out.println("Enter Oracle Password: ");
                String pass = in.nextLine();

                // attempt connection to database
                conn = DriverManager.getConnection(DB_URL, user, pass);
                System.out.println("Connection successful!\n");

                conn.close();

            }
            catch (SQLException sqle) {
                // sqle.printStackTrace();
                System.out.println("[Error]: Connect error. Please try again.");
            }

        } while(conn == null);
        
    }
}