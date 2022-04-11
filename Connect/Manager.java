import java.sql.*;
import java.util.*;

public class Manager {
    
    public static void managerInterface(Connection conn) {

        // instantiate scanner
        Scanner in = new Scanner(System.in);

        do {
            try {
                System.out.print("\033\143");
                System.out.println("Welcome to the NUMA Property Manager Interface!");
                
                int choice;

                do {
                    // interface menu
                    System.out.println("Please select an option below:");
                    System.out.println("\t1: View all property listings.");
                    System.out.println("\t2: View all apartments of a property.");
                    System.out.println("\t3: View all apartments available for rent.");
                    System.out.println("\t0: Exit");
                    // switch through choice
                    choice = Integer.parseInt(in.nextLine());

                    switch (choice) {
                        case 0:
                            // quit program - display exit message
                            System.out.println("Thanks for using the NUMA Property Manager Interface!");
                            System.out.println("Exiting...");
                            System.out.print("\033\143");
                            break;

                        case 1:
                            // execute query to return ALL property listings
                            System.out.println("All property listings in the area:\n");
                            String propertyQuery = "select * from property";
                            PreparedStatement pStmt = conn.prepareStatement(propertyQuery);

                            ResultSet rs1 = pStmt.executeQuery();
                            ResultSetMetaData rsmd = rs1.getMetaData();
                            int colNum1 = rsmd.getColumnCount();

                            // print result set
                            while(rs1.next()) {
                                for(int i = 1; i <= colNum1; i++) {
                                        System.out.print(rs1.getString(i) + " ");
                                }
                                System.out.println();
                            }
                            System.out.println();
                            break;

                        case 2:
                            // display all apartments related to a single property
                            System.out.println("Enter the ID of the property you would like to view.");
                            int pID = Integer.parseInt(in.nextLine());
                            System.out.println("All apartments at Property " + pID + ": \n");
                            
                            // create query to search for all apartments in given property
                            String aptQuery = "select * from apartment where p_id = ?";
                            PreparedStatement pStmt2 = conn.prepareStatement(aptQuery);
                            pStmt2.setInt(1, pID);

                            ResultSet rs2 = pStmt2.executeQuery();
                            ResultSetMetaData rsmd2 = rs2.getMetaData();
                            int colNum2 = rsmd2.getColumnCount();

                            // print result set
                            while(rs2.next()) {
                                for(int i = 1; i <= colNum2; i++) {
                                    System.out.print(rs2.getString(i) + " ");
                                }
                                System.out.println();
                            }
                            System.out.println();
                            break;
                            
                        case 3:
                            // display all apartments available for rent
                            System.out.println("Enter the ID of the property you'd like to view.");
                            pID = Integer.parseInt(in.nextLine());
                            System.out.println("All apartments available for rent at Property " + pID + ": \n");

                            // create query to search for apartments that have a lease but no tenant (available for rent)
                            String leaseQuery = "select * from lease where apt_num not in (select apt_num from lives_in)";
                            PreparedStatement pStmt3 = conn.prepareStatement(leaseQuery);
                            ResultSet rs3 = pStmt3.executeQuery();

                            ResultSetMetaData rsmd3 = rs3.getMetaData();
                            int colNum3 = rsmd3.getColumnCount();

                            // print result set
                            while(rs3.next()) {
                                for(int i = 1; i <= colNum3; i++) {
                                    System.out.print(rs3.getString(i) + " ");
                                }
                                System.out.println();
                            }
                            System.out.println();
                            break;
                        
                        default:
                            System.out.println("Please make a proper selection (1-3).");
                            System.out.println("\t1: View all property listings.");
                            System.out.println("\t2: View all apartments of a property.");
                            System.out.println("\t3: View all available leases of a property.");
                            System.out.println("\t0: Exit");
                            break;
                    }
                } while (choice != 0);
            }
            catch (SQLException sqle) {
                // sqle.printStackTrace();
                System.out.println("[Error]: Connect error. Please try again.");
            }
        } while (conn == null);

    }



}
