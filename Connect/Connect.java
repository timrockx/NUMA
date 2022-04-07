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

                // switch case for which interface to access
                System.out.println("Select the interface you would like to use: ");
                System.out.println("1. Property Manager");
                System.out.println("2. Tenant");
                System.out.println("3. NUMA Manager");   
                
                // get user's choice for interface
                int choice = Integer.parseInt(in.nextLine());
                switch (choice) {
                    case 1:
                        System.out.print("\033\143");
                        System.out.println("------Property Manager Interface------");
                        // call to property manager class
                        Manager.managerInterface(conn);
                        
                        break;

                    case 2:
                        System.out.print("\033\143");
                        System.out.println("------Tenant Interface------");
                        // call to tenant class
                        Tenant.tenantInterface(conn);
                        break;
                    
                    case 3:
                        System.out.print("\033\143");
                        System.out.println("------NUMA Manager Interface------");
                        // call to manager class
                        break;
                    
                    default:
                        System.out.println("Please make a proper selection (1-3).");
                        System.out.println("1. Property Manager");
                        System.out.println("2. Tenant");
                        System.out.println("3. NUMA Manager");
                        break;
                }

                conn.close();

            }
            catch (SQLException sqle) {
                // sqle.printStackTrace();
                System.out.println("[Error]: Connect error. Please try again.");
            }

        } while(conn == null);
        
    }
}