import java.sql.*;
import java.util.*;

public class Tenant {

   public static void tenantInterface(Connection conn) {

        // instantiate scanner
        Scanner in = new Scanner(System.in);

        do {
            try {
                 // on successful connection, clear console
                 System.out.print("\033\143");

                 int choice;
                 do {
                     // interface menu
                     System.out.println("Welcome to the NUMA Tenant Interface!");
                     System.out.println("Please select an option below:");
                     System.out.println("\t1: Make a payment.");
                     System.out.println("\t2: Add a roomate.");
                     System.out.println("\t3: Move out.");
                     System.out.println("\t0: Exit");

                     choice = Integer.parseInt(in.nextLine());

                     switch(choice) {

                        case 0:
                            // quit
                            break;
                
                        case 1:
                            // make payment (collect info, etc.)
                            System.out.println("What payment method would you like to use?");

                            String paymentQuery = "select * from tenant";
                            PreparedStatement pStmt = conn.prepareStatement(paymentQuery);
                            break;

                        case 2:
                            // add a roommate
                            break;

                        case 3:
                            // move out / cancel lease
                            break;
                        
                        default:
                            System.out.println("Please make a proper selection (1-3).");
                            System.out.println("\t1: Make a payment.");
                            System.out.println("\t2: Add a roomate.");
                            System.out.println("\t3: Move out.");
                            System.out.println("\t0: Exit");
                            break;
                     }  
                } while (choice != 0);
            }
            catch (SQLException sqle) {
                System.out.println("[Error]: Connect error. Please try again.");
            }

        } while (conn == null);
        



   }

}