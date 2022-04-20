import java.sql.*;
import java.util.*;

public class Manager {
    
    // manger interface
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
                    System.out.println("\t4: Record a visit by a prospective tenant.");
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
                            
                            // create query to search for all apartments in given property
                            String aptQuery = "select * from apartment where p_id = ?";
                            PreparedStatement pStmt2 = conn.prepareStatement(aptQuery);
                            pStmt2.setInt(1, pID);
                            ResultSet rs2 = pStmt2.executeQuery();

                            // display results
                            System.out.println("Apartments at Property " + pID + ":");
                            System.out.println("================");
                            System.out.println("Apt. Num\tBeds\t\tBaths\t\tSq. Ft.");

                            if(rs2.next() == false) {
                                System.out.println("[Error]: No apartments found at specified property.");
                            } else {
                                // print apartment info
                                do {
                                    System.out.println(rs2.getString(1) + "\t\t" + rs2.getString(2) + "\t\t" + rs2.getString(3) + "\t\t" + rs2.getString(4));
                                } while(rs2.next());
                                System.out.println();
                            }
                            break;
                            
                        case 3:
                            // display all apartments available for rent
                            System.out.println("Enter the ID of the property you'd like to view.");
                            pID = Integer.parseInt(in.nextLine());

                            // query to search for apartments that have a lease but no tenant (available for rent)
                            String leaseQuery = "select apt_num, num_beds, num_baths, sq_foot, monthly_price from lease natural join apartment where apt_num not in (select apt_num from lives_in)";
                            PreparedStatement pStmt3 = conn.prepareStatement(leaseQuery);
                            ResultSet rs3 = pStmt3.executeQuery();

                            // display results
                            System.out.println("Apartments for Rent at Property " + pID + ":");
                            System.out.println("================");
                            System.out.println("Apt. Num\tBeds\t\tBaths\t\tSq. Ft.\t\tPrice (per M)");

                            if(rs3.next() == false) {
                                System.out.println("[Error]: No apartments available for rent at specified property.");
                            } else {
                                // print out apartment info
                                do {
                                    System.out.println(rs3.getString(1) + "\t\t" + rs3.getString(2) + "\t\t" + rs3.getString(3) + "\t\t" + rs3.getString(4) + "\t\t" + rs3.getString(5));
                                } while(rs3.next());
                                System.out.println();
                            }
                            break;

                        case 4:
                            // record a visit by a prospective tenant
                            System.out.println("Enter the visitor's name: ");
                            String visitorName = in.nextLine();
                            System.out.println("Enter the visitor's phone number: ");
                            String visitorPhone = in.nextLine();
                            System.out.println("Enter the visitor's email: ");
                            String visitorEmail = in.nextLine();
                            // insert query
                            String visitorQ = "insert into visitor values (?, ?, ?)";
                            PreparedStatement pStmt4 = conn.prepareStatement(visitorQ);
                            pStmt4.setString(1, visitorName);
                            pStmt4.setString(2, visitorPhone);
                            pStmt4.setString(3, visitorEmail);
                            // execute query
                            int rowsChanged = pStmt4.executeUpdate();
                            if(rowsChanged == 1) {
                                System.out.println("[Update]: Visitor " + visitorName + " has been added to the database.\n");
                            } else {
                                System.out.println("[Error]: Visitor was not added to the database.\n");
                            }
                            break;
                        
                        default:
                            // invalid choice
                            System.out.println("Please make a proper selection (1-3).");
                            System.out.println("\t1: View all property listings.");
                            System.out.println("\t2: View all apartments of a property.");
                            System.out.println("\t3: View all available leases of a property.");
                            System.out.println("\t4: Record a visit by a prospective tenant.");
                            System.out.println("\t0: Exit");
                            break;
                    }
                } while (choice != 0);
            }
            catch (SQLException sqle) {
                System.out.println("[Error]: Connect error. Please try again.");
                sqle.printStackTrace();
            }
        } while (conn == null);

    }



}
