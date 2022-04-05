import java.sql.*;
import java.util.*;

public class Tenant {

   public static void tenantInterface() {

        final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

        // instantiate connection and scanner
        Connection conn = null;
        Scanner in = new Scanner(System.in);

        do {
            try {

                 // get tenant login info (to oracle)
                 System.out.println("Enter the Tenant Username: ");
                 String user = in.nextLine();
                 System.out.println("Enter the Tenant Password: ");
                 String pass = in.nextLine();

                 // attempt connection to database
                 conn = DriverManager.getConnection(DB_URL, user, pass);
                 // on successful connection, clear console
                 System.out.print("\033\143");

                 int choice;
                 
                 do {
                     // interface menu
                     System.out.println("Welcome to the NUMA Tenant Interface!");
                     System.out.println("Please select an option below:");
                     System.out.println("\t1: ");
                     System.out.println("\t2: ");
                     System.out.println("\t3: ");
                     System.out.println("\t0: Exit");

                     choice = Integer.parseInt(in.nextLine());
                   



                } while (choice != 0);


            }
            catch (SQLException sqle) {
                System.out.println("[Error]: Connect error. Please try again.");
            }

        } while (conn == null);
        



   }

}