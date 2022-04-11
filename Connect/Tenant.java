import java.sql.*;
import java.util.*;

public class Tenant {

    public static void addPayment(Connection conn, int tenant_id) {
        Scanner in = new Scanner(System.in);

        try {
            System.out.println("What payment method would you like to use?");
            System.out.println("\t1: Credit Card (CC)");
            System.out.println("\t2: Venmo (V)");
            System.out.println("\t2: Cash (C)");

            String method = in.nextLine();
            if(method.equalsIgnoreCase("cc")) {
                System.out.println("Enter the Credit Card Number: ");
                String ccNum = in.nextLine();
                System.out.println("Enter the Expiration Date: (MM/YY) ");
                String expDate = in.nextLine();
                System.out.println("Enter the CVV: (XXX)");
                String cvv = in.nextLine();

                String ccQuery = "insert into payment (tenant_id, method, cc_num, exp_date, cvv) values (?, ?, ?, ?, ?)";
                PreparedStatement pStmt = conn.prepareStatement(ccQuery);
                // set arguments for cc info
                pStmt.setInt(1, tenant_id);
                pStmt.setString(2, ccNum);
                pStmt.setString(3, expDate);
                pStmt.setString(4, cvv);

                int rowsChanged = pStmt.executeUpdate();
                if(rowsChanged == 1) {
                    System.out.println("Payment added successfully!");
                }
                else {
                    System.out.println("Payment failed to add. Please go through our portal again and try again.");
                }

            } else if(method.equalsIgnoreCase("v")) {
                // gather venmo info
                System.out.println("What is your venmo username? ");
                String vUser = in.nextLine();

                String vQuery = "insert into payment (tenant_id, method, venmo_user) values (?, ?, ?)";
                PreparedStatement pStmt1 = conn.prepareStatement(vQuery);
                // set arguments for venmo account
                pStmt1.setInt(1, tenant_id);
                pStmt1.setString(2, vUser);

                int rowsChanged = pStmt1.executeUpdate();
                if (rowsChanged == 1) {
                    System.out.println("Payment added successfully!");
                } else {
                    System.out.println("Payment failed to add. Please go through our portal again and try again.");
                }

            } else if(method.equalsIgnoreCase("c")) {
                // set payMethod to cash for tenant
                System.out.println("Cash will be used for this, and any future payments.");

            } else {
                // invalid input
                System.out.println("Invalid input. Please try again.");
            }
        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Connect error. Please try again.");
            sqle.printStackTrace();
        }        
    }



   public static void tenantInterface(Connection conn) {
        // instantiate scanner
        Scanner in = new Scanner(System.in);

        do {
            try {
                 // on successful connection, clear console
                 System.out.print("\033\143");
                 System.out.println("Welcome to the NUMA Tenant Interface!");

                 System.out.println("Enter your tenant ID to login: ");
                 int t_id = Integer.parseInt(in.nextLine());

                 int choice;
                 do {
                     // interface menu
                     System.out.println("Please select an option below:");
                     System.out.println("\t1: Make a payment.");
                     System.out.println("\t2: Add a roomate.");
                     System.out.println("\t3: Move out.");
                     System.out.println("\t0: Exit");
                     choice = Integer.parseInt(in.nextLine());

                     switch(choice) {
                        case 0: // quit - display exit message
                            System.out.println("Thanks for using the NUMA Tenant Interface!");
                            System.out.println("Exiting...");
                            System.out.print("\033\143");
                            break;
                
                        case 1: // make payment on rent
                            String pMethodQ = "select * from payment where tenant_id = ?";
                            PreparedStatement pStmt = conn.prepareStatement(pMethodQ);
                            pStmt.setInt(1, t_id);
                            ResultSet rs1 = pStmt.executeQuery();
                            ResultSetMetaData rsmd1 = rs1.getMetaData();

                            if(rs1.next() == false) {
                                System.out.println("You do not have a payment method on file. Would you like to add one? (y/n)");

                            } else {
                                // get payment method
                                String payMethod = rs1.getString(2);
                                System.out.println("Your payment method on file is: " + payMethod + ". Would you like to use this to pay? (y/n)");
                                String option = in.nextLine();
                                if(option.equalsIgnoreCase("y")) {
                                    // process payment with payMethod
                                    System.out.println("You have a payment for $300 due at the end of this month. Would you like to pay now? (y/n) ");
                                } else if(option.equalsIgnoreCase("n")) {
                                    // call addPayment method passing in DB connection and tennat id
                                    addPayment(conn, t_id);
                                }

                            }

                            pStmt.close();
                            break;

                        case 2: // add a roommate
                            System.out.println("How many roommates would you like to add?");
                            int numRoommates = Integer.parseInt(in.nextLine());

                            for(int i=0; i < numRoommates; i++) {
                                // gather roommate info to add to db
                                System.out.println("Enter the first/last name of your new roommate:");
                                String name = in.nextLine();
                                System.out.println("Enter the phone number of your new roommate: (xxx-xxx-xxxx)");
                                String phone = in.nextLine();
                                System.out.println("Enter the email of your new roommate: ");
                                String email = in.nextLine();

                                // add roommate to db (using tenant id collected at start)
                                String addRoommateQ = "insert into roommate values(?, ?, ?, ?)";
                                PreparedStatement addpStmt = conn.prepareStatement(addRoommateQ);
                                // set parameters for roommate
                                addpStmt.setString(1, name);
                                addpStmt.setString(2, phone);
                                addpStmt.setString(3, email);
                                addpStmt.setInt(4, t_id);
                                // execute query
                                System.out.println("about to execute with parameters: " + name + " " + phone + " " + email + " " + t_id);
                                int updatedRows = addpStmt.executeUpdate();
                                System.out.println("executed...");

                                if(updatedRows == numRoommates) {
                                    System.out.println("Roommate added successfully!");
                                } 
                            }
                            break;

                        case 3: // move out / cancel lease
                            System.out.println("Please confirm you would like to move out of your apartment (y/n): ");
                            String confirm = in.nextLine(); 
                            if(confirm.length() > 1) {
                                System.out.println("Please enter y or n.");
                            } else {
                                if(confirm.equals("y")) {
                                    // move out
                                    System.out.println("Moving out...");

                                } else if(confirm.equals("n")) {
                                    System.out.println("Reverting to previous menu...");

                                } else {
                                    System.out.println("Please enter an appropriate choice (y/n): ");;
                                }
                            }

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
                sqle.printStackTrace();
            }

        } while (conn == null);
        



   }

}