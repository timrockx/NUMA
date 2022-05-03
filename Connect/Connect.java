import java.sql.*;
import java.util.*;

// main class to handle landing page, then redirect to different interfaces
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
                System.out.print("\033\143");
                System.out.println("Connection successful!\n");

                int choice;
                  
                do {            
                    System.out.println("Select the interface you would like to use: ");
                    System.out.println("\t0. Quit Program");
                    System.out.println("\t1. Property Manager");
                    System.out.println("\t2. Tenant");
                    System.out.println("\t3. NUMA Manager");
                    // get user's choice for interface
                    choice = Integer.parseInt(in.nextLine());
                    switch (choice) {
                        case 0:
                            // quit interface
                            System.out.println("Exiting NUMA Enterprises. Goodbye!");
                            break;

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
                            // call to numaManager class
                            numaManager.numaManagerInterface(conn);
                            break;
                        
                        default:
                            System.out.println("Please make a proper selection (0-3).");
                            // System.out.println("\t0. Quit Program");
                            // System.out.println("\t1. Property Manager");
                            // System.out.println("\t2. Tenant");
                            // System.out.println("\t3. NUMA Manager\n"); 
                            break;
                    }

                } while(choice != 0);

                // close database connection after user quits
                conn.close();
                
            }
            catch (SQLException sqle) {
                System.out.println("[Error]: Connection error. Please try again.");
                 // sqle.printStackTrace();
            }
            catch (InputMismatchException exp) {
                System.out.println("[Error]: Input Mismatch Error. Please try again.");
            }
            catch (Exception e) {
                System.out.println("[Error]: Unknown error. Please try again.");
            }

        } while(conn == null);
        
    }
}