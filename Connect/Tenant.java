import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Tenant {

    // add a payment method to a tenant's account
    public static void addPayment(Connection conn, int tenant_id) {
        Scanner in = new Scanner(System.in);
        boolean paymentSet = false;

        do {
            try {
                System.out.println("What payment method would you like to use?");
                System.out.println("\t1: Credit Card (CC)");
                System.out.println("\t2: Venmo (V)");
                System.out.println("\t2: Cash (C)");
                int method = Integer.parseInt(in.nextLine());

                switch(method) {
                    // add credit card
                    case 1:
                        // get credit card info from user
                        System.out.println("Enter the Credit Card Number: ");
                        String ccNum = in.nextLine();
                        System.out.println("Enter the Expiration Date: (MM/YY) ");
                        String expDate = in.nextLine();
                        System.out.println("Enter the CVV: (XXX)");
                        String cvv = in.nextLine();
        
                        // query with info
                        String ccQuery = "insert into payment (tenant_id, method, cc_num, exp_date, cvv) values (?, ?, ?, ?, ?)";
                        PreparedStatement pStmt = conn.prepareStatement(ccQuery);
                        // set arguments for cc info
                        pStmt.setInt(1, tenant_id);
                        pStmt.setString(2, ccNum);
                        pStmt.setString(3, expDate);
                        pStmt.setString(4, cvv);
        
                        // check output, must change 1 row to be successful
                        int rowsChanged = pStmt.executeUpdate();
                        if(rowsChanged == 1) {
                            System.out.println("Payment added successfully!");
                            paymentSet = true;
                        }
                        else {
                            System.out.println("[Error]: Payment failed to add. Please login to our portal and try again.");
                            paymentSet = true;
                        }
                        break;

                    // add venmo
                    case 2:
                        // get venmo info from user
                        System.out.println("What is your venmo username? ");
                        String vUser = in.nextLine();
        
                        // form venmo query
                        String vQuery = "insert into payment (tenant_id, method, venmo_user) values (?, ?, ?)";
                        PreparedStatement pStmt1 = conn.prepareStatement(vQuery);
                        // set arguments for venmo account
                        pStmt1.setInt(1, tenant_id);
                        pStmt1.setString(2, vUser);
        
                        // check output for success
                        rowsChanged = pStmt1.executeUpdate();
                        if (rowsChanged == 1) {
                            System.out.println("Payment added successfully!");
                            paymentSet = true;
                        } else {
                            System.out.println("Payment failed to add. Please go through our portal again and try again.");
                        }
                        break;

                    // use cash
                    case 3:
                        System.out.println("Your default payment method is now set to be CASH.");
                        paymentSet = true;
                        break;

                    // invalid input
                    default:
                        System.out.println("[Error]: Invalid input. Please try again.");
                        System.out.println("\t1: Credit Card (CC)");
                        System.out.println("\t2: Venmo (V)");
                        System.out.println("\t2: Cash (C)");
                        break;
                } 
            }
            catch (SQLException sqle) {
                System.out.println("[Error]: Connect error. Please try again.");
                sqle.printStackTrace();
            }        
        } while(paymentSet == false);
    }


    public static void makePayment(Connection conn, int tenant_id) {
        Scanner in = new Scanner(System.in);
        boolean paymentMade = false;

        // iterate while payment has not been made
        do {
            try {
                // get late payments OR find tenant's monthly rent somehow
                System.out.println("You have a payment of $XYZ due at the end of the month. Would you like to pay now? (y/n) ");
                String payNow = in.nextLine();
                if(payNow.equalsIgnoreCase("y")) {
                    // add to payment history
                    String payQuery = "insert into payment_history (transaction_id, tenant_id, amount, date) values (?, ?, ?, ?)";
                    PreparedStatement pStmt = conn.prepareStatement(payQuery);
                    // set arguments of query
                    pStmt.setInt(1, 1);
                    pStmt.setInt(2, tenant_id);
                    pStmt.setInt(3, 350);
                    pStmt.setDate(4, new Date(System.currentTimeMillis()));

                    // check resultset, then change paymentMade variable
                    int rowsChanged = pStmt.executeUpdate();
                    if(rowsChanged == 1) {
                        System.out.println("[Thank You]: Payment made successfully!");
                        paymentMade = true;
                    } else {
                        System.out.println("[Error]: Issue with payment method. Please login and try again.");
                        paymentMade = true; // true just to quit loop
                    }

                } else if(payNow.equalsIgnoreCase("n")) {
                    // print warning message, but do nothing
                    System.out.println("[Reminder]: If you do not pay by the end of the month, you will be charged a late fee of $100.");
                    System.out.println("Exiting Interface Now... If you choose to make a payment you must login again.");
                    paymentMade = true; // exit loop and quit
                } else {
                    // invalid input
                    System.out.println("[Error]: Please enter either (y/n) as a proper response.");
                    System.out.println("You have a payment of $XYZ due at the end of the month. Would you like to pay now? (y/n) ");
                }

            }
            catch(SQLException sqle) {
                System.out.println("[Error]: Connect error. Please try again.");
                sqle.printStackTrace();
            }
        } while(paymentMade == false);
    }


    public static void moveOut(Connection conn, int tenant_id) {
        
        try {
             // delete tenant from lives_in table
            String deleteLivesInQ = "delete from lives_in where tenant_id = ?";
            PreparedStatement pStmt1 = conn.prepareStatement(deleteLivesInQ);
            pStmt1.setInt(1, tenant_id);
            int deletedRows = pStmt1.executeUpdate();
            if(deletedRows == 1) {
                System.out.println("[Success]: You have successfully moved out of your apartment.");
            }

            // delete tenant from specific payment, then payment table

            // delete tenant from tenant table
            // String deleteTenantQ = "delete from tenant where tenant_id = ?";
            // PreparedStatement pStmt2 = conn.prepareStatement(deleteTenantQ);
            // pStmt2.setInt(1, tenant_id);
            // deletedRows = pStmt2.executeUpdate();
            // if(deletedRows == 1) {
            //     System.out.println("[Success]: You have successfully moved out of your apartment.");
            // }


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
                 System.out.println("Welcome to the NUMA Tenant Interface!\n");

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

                            // if payment method is not set, set it
                            if(rs1.next() == false) {
                                System.out.println("You do not have a payment method on file. Would you like to add one? (y/n)");
                                String answer = in.nextLine();
                                if(answer.equalsIgnoreCase("y")) {
                                    // add payment method
                                    addPayment(conn, t_id);
                                }
                                else {
                                    System.out.println("A payment method is required before making a payment. Please try again.");
                                }

                            // if payment method is set, we retrieve then use it to pay
                            } else {
                                // get payment method
                                String payMethod = rs1.getString(2);
                                System.out.println("Your payment method on file is: " + payMethod + ". Would you like to use this to pay? (y/n)");
                                String option = in.nextLine();
                                if(option.equalsIgnoreCase("y")) {
                                    // process payment with payMethod
                                    makePayment(conn, t_id);
                                    
                                } else if(option.equalsIgnoreCase("n")) {
                                    // call addPayment method passing in DB connection and tennat id
                                    addPayment(conn, t_id);
                                    // after setting payment method, make payment
                                    makePayment(conn, t_id);
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
                            // validate input
                            if(confirm.equalsIgnoreCase("y")) {
                                // move out
                                System.out.println("[Note]: All tenant information will be deleted upon move out.");
                                System.out.println("[Moving out...]");
                                moveOut(conn, t_id);
                                break;

                            } else if(confirm.equalsIgnoreCase("n")) {
                                System.out.println("Reverting to previous menu...");

                            } else {
                                System.out.println("Please enter an appropriate choice (y/n): ");;
                            }
                            break;
                        
                        default:
                            System.out.println("[Error]: Please make a proper selection: ");
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