import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class Tenant {

    // main interface for tenants
    public static void tenantInterface(Connection conn) {
        // instantiate scanner
        Scanner in = new Scanner(System.in);
        do {
            try {
                 System.out.print("\033\143");
                 System.out.println("Welcome to the NUMA Tenant Interface!\n");
                 // tenant login
                 int t_id = tenantLogin(conn);
                 int choice;
                 do {
                     // interface menu
                     System.out.println("Please select an option below:");
                     System.out.println("\t1: Make a payment.");
                     System.out.println("\t2: View payment history.");
                     System.out.println("\t3: View complimentary amenities.");
                     System.out.println("\t4: Add roommates");
                     System.out.println("\t5: Move out");
                     System.out.println("\t0: Exit");
                     choice = Integer.parseInt(in.nextLine());

                     switch(choice) {
                        case 0: // quit - display exit message
                            System.out.println("Thank you for using the NUMA Tenant Interface!");
                            System.out.println("[Exiting...]");
                            System.out.print("\033\143");
                            break;
                
                        case 1: // make payment on rent
                            String pMethodQ = "select * from payment where tenant_id = ?";
                            PreparedStatement pStmt = conn.prepareStatement(pMethodQ);
                            pStmt.setInt(1, t_id);
                            ResultSet rs1 = pStmt.executeQuery();

                            // if payment method is not set, set it
                            if(rs1.next() == false) {
                                System.out.println("[Note]: You do not have a payment method on file. Would you like to add one? (y/n)");
                                String answer = in.nextLine();
                                if(answer.equalsIgnoreCase("y")) {
                                    // add payment method
                                    addPayment(conn, t_id);
                                } else {
                                    // quit, we need payment method
                                    System.out.println("[Note]: A payment method is required before making a payment. Please try again.");
                                }

                            // if payment method is set, we retrieve then use it to pay
                            } else {
                                // get payment method
                                String payMethod = rs1.getString(2);
                                System.out.println("Your payment method on file is: " + payMethod + ". Would you like to use this to pay? (y/n)");
                                String option = in.nextLine();
                                if(option.equalsIgnoreCase("y")) {
                                    // process payment with payMethod
                                    makePayment(conn, t_id, payMethod);
                                    
                                } else if(option.equalsIgnoreCase("n")) {
                                    System.out.println("[Redirect]: Redirecting to update payment method.");
                                    // call updatePayment method 
                                    addPayment(conn, t_id);
                                    // after setting payment method, make payment
                                    makePayment(conn, t_id, payMethod);
                                }
                            }
                            pStmt.close();
                            break;

                        case 2: // view payment history
                            viewPaymentHistory(conn, t_id);
                            break;
                            
                        case 3: // view amenities
                            viewAmenities(conn, t_id);
                            break;

                        case 4: // add roommamtes
                            addRoommates(conn, t_id);
                            break;

                        case 5: // move out
                            System.out.println("Please confirm you would like to move out of your apartment (y/n): ");
                            String confirm = in.nextLine(); 
                            // validate input
                            if(confirm.equalsIgnoreCase("y")) {
                                // move out
                                System.out.println("[Note]: All tenant information will be deleted upon move out.");
                                System.out.println("[Update]: Move out successful.");
                                moveOut(conn, t_id);
                                break;

                            } else if(confirm.equalsIgnoreCase("n")) {
                                System.out.println("[Reverting to previous menu...]");

                            } else {
                                System.out.println("[Error]: Please enter an appropriate choice (y/n):");;
                            }
                            break;
                        
                        default: // invalid input
                            System.out.println("[Error]: Please make a proper selection: ");
                            System.out.println("\t1: Make a payment.");
                            System.out.println("\t2: View payment history.");
                            System.out.println("\t3: View complimentary amenities.");
                            System.out.println("\t4: Add roommates");
                            System.out.println("\t5: Move out");
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

    // login for tenants, verify tenant_id
    public static int tenantLogin(Connection conn) {
        // instantiate scanner
        Scanner in = new Scanner(System.in);
        int t_id = 0;
        try {
            // get all tenant ids to verify proper input
            String loginQ = "select tenant_id from tenant";
            PreparedStatement loginP = conn.prepareStatement(loginQ);
            ResultSet ids = loginP.executeQuery();
            ResultSetMetaData rsmd = ids.getMetaData();
            int colCount = rsmd.getColumnCount();

            // store all tenant ids into an arraylist
            ArrayList<String> idList = new ArrayList<String>(colCount);
            while(ids.next()) {
                int i = 1;
                while(i <= colCount) {
                    idList.add(ids.getString(i++));
                }
            }
            boolean accessGranted = false;
            do {
                // get user input for tenant_id
                System.out.println("Enter your tenant ID: ");
                t_id = Integer.parseInt(in.nextLine());

                // check against list, if matches allow access
                if(idList.contains(Integer.toString(t_id))) {
                    System.out.println("[Success]: Access granted to the NUMA Tenant Interface");
                    System.out.print("\033\143"); 
                    accessGranted = true;
                    return t_id;    // return t_id to be used in interface()
                } else {
                    System.out.println("[Error]: Invalid Tenant ID. Please try again.");
                }
            } while(accessGranted == false);
        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with Tenant Login.");
            sqle.printStackTrace();
        }
        return t_id;
    }

    // add a payment method to a tenant's account
    public static void addPayment(Connection conn, int tenant_id) {
        Scanner in = new Scanner(System.in);
        boolean paymentSet = false;

        do {
            try {
                System.out.println("What payment method would you like to use?");
                System.out.println("\t0: Quit Interface");
                System.out.println("\t1: Credit Card (CC)");
                System.out.println("\t2: Venmo (V)");
                System.out.println("\t3: Cash (C)");
                int method = Integer.parseInt(in.nextLine());

                switch(method) {
                    // quit payment interface
                    case 0:
                        System.out.println("[Exit]: Returning to Main Interface.]");
                        paymentSet = true;
                        break;

                    // add credit card
                    case 1:
                        // set payment method as credit
                        String pQuery = "insert into payment (tenant_id, method) values (?, ?)";
                        PreparedStatement pStatement = conn.prepareStatement(pQuery);
                        pStatement.setInt(1, tenant_id);
                        pStatement.setString(2, "Credit");

                        // collect credit card info
                        System.out.println("Enter the Credit Card Number: ");
                        String ccNum = in.nextLine();
                        System.out.println("Enter the Expiration Date: (MM/YY) ");
                        String expDate = in.nextLine();
                        System.out.println("Enter the CVV: (XXX)");
                        String cvv = in.nextLine();
        
                        // add cc info to db
                        String ccQuery = "insert into credit (tenant_id, card_num, exp_date, cvv) values (?, ?, ?, ?)";
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
                        }
                        else {
                            System.out.println("[Error]: Payment failed to add. Please login to our portal and try again.");
                        }
                        break;
                    
                    // add venmo
                    case 2:
                        pQuery = "insert into payment (tenant_id, method) values (?, ?)";
                        pStatement = conn.prepareStatement(pQuery);
                        pStatement.setInt(1, tenant_id);
                        pStatement.setString(2, "Venmo");
                        // get venmo info from user
                        System.out.println("What is your venmo username? ");
                        String vUser = in.nextLine();
        
                        // create venmo query
                        String vQuery = "insert into venmo (tenant_id, username) values (?, ?)";
                        PreparedStatement pStmt1 = conn.prepareStatement(vQuery);
                        // set arguments for venmo account
                        pStmt1.setInt(1, tenant_id);
                        pStmt1.setString(2, vUser);
        
                        // check output for success
                        rowsChanged = pStmt1.executeUpdate();
                        if (rowsChanged == 1) {
                            System.out.println("Payment added successfully!");
                        } else {
                            System.out.println("Payment failed to add. Please go through our portal again and try again.");
                        }
                        break;

                    // use cash
                    case 3:
                        pQuery = "insert into payment (tenant_id, method) values (?, ?)";
                        pStatement = conn.prepareStatement(pQuery);
                        pStatement.setInt(1, tenant_id);
                        pStatement.setString(2, "Cash");
                        
                        System.out.println("Your default payment method is now set to be CASH.");
                        break;

                    // invalid input
                    default:
                        System.out.println("[Error]: Invalid input. Please try again.");
                        System.out.println("\t0: Quit Interface");
                        System.out.println("\t1: Credit Card (CC)");
                        System.out.println("\t2: Venmo (V)");
                        System.out.println("\t3: Cash (C)");
                        break;
                } 
            }
            catch (SQLException sqle) {
                System.out.println("[Error]: Connect error. Please try again.");
                sqle.printStackTrace();
            }        
        } while(paymentSet == false);
    }

    // make a payment on rent on tenant's account
    public static void makePayment(Connection conn, int tenant_id, String payMethod) {
        Scanner in = new Scanner(System.in);
        boolean paymentMade = false;

        // iterate while payment has not been made
        do {
            try {
                 // get tenant's monthly rate
                 String rateQuery = "select monthly_price from lease natural join lives_in where tenant_id = ?";
                 PreparedStatement pStatement = conn.prepareStatement(rateQuery);
                 pStatement.setInt(1, tenant_id);
                 ResultSet rs1 = pStatement.executeQuery();
                 int monthly_price = 0;
 
                 // get monthly_price and store in local var
                 if(rs1.next() == false) {
                     System.out.println("[Error]: Failed to retrieve your monthly lease price. Please try again.");
                     break;
                 } else {
                     monthly_price = Integer.parseInt(rs1.getString(1));
                 }
                 // close pstatement and resultset
                 pStatement.close();
                 rs1.close();

                // get tenant's last payment info
                String lastPayQ = "select date_paid, amount from payment_history where tenant_id = ? order by date_paid desc fetch first row only";
                PreparedStatement lastPayStmt = conn.prepareStatement(lastPayQ);
                lastPayStmt.setInt(1, tenant_id);
                ResultSet lastPayRS = lastPayStmt.executeQuery();
                int last_paid = 0;
                // java.sql.Date datePaid = null;
                int amountDue = 0;

                if(lastPayRS.next() == false) {
                    // make first payment
                    System.out.println("You have a payment of $" + monthly_price + " due. Would you like to pay now? (y/n) ");
                    String payNow = in.nextLine();

                    if(payNow.equalsIgnoreCase("y")) {
                        // add to payment history
                        String payQuery = "insert into payment_history (tenant_id, date_paid, amount, method) values (?, SYSDATE, ?, ?)";
                        PreparedStatement pStmt = conn.prepareStatement(payQuery);
                        // set arguments of query
                        pStmt.setInt(1, tenant_id);
                        pStmt.setInt(2, monthly_price);
                        pStmt.setString(3, payMethod);

                        // check resultset, then change paymentMade variable
                        int rowsChanged = pStmt.executeUpdate();
                        if(rowsChanged == 1) {
                            System.out.println("[Thank You]: Payment was made successfully.");
                            paymentMade = true;
                        } else {
                            System.out.println("[Error]: Issue with  payment method. Please login and try again.");
                            paymentMade = true; 
                        }
                        pStmt.close();

                    } else if(payNow.equalsIgnoreCase("n")) {
                        // print warning message, but do nothing
                        System.out.println("[Reminder]: If you do not pay by the end of the month, you will be charged a late fee of $100.\n");
                        break; // exit loop and quit

                    } else {
                        // invalid input
                        System.out.println("[Error]: Please enter either (y/n) as a proper response.");
                    }

                } else {
                    // either make no payment (balance 0) or make payment on remaining balance
                    last_paid = lastPayRS.getInt("amount");
                    // datePaid = lastPayRS.getDate("date_paid");

                    if(last_paid == monthly_price) {
                        System.out.println("[Note]: You have already paid for this month. Remaining balance is 0.\n");
                        paymentMade = true;
                    } else if(last_paid < monthly_price) {
                        amountDue = monthly_price - last_paid;
                        System.out.println("[Not: You have a balance of $" + amountDue + " due. Would you like to pay now? (y/n) ");
                        String payNow = in.nextLine();
                        if(payNow.equalsIgnoreCase("y")) {
                            // add to payment history
                            String payQuery = "insert into payment_history (tenant_id, date_paid, amount, method) values (?, SYSDATE, ?, ?)";
                            PreparedStatement pStmt = conn.prepareStatement(payQuery);
                            // set arguments of query
                            pStmt.setInt(1, tenant_id);
                            pStmt.setInt(2, amountDue);
                            pStmt.setString(3, payMethod);

                            int rowsChanged = pStmt.executeUpdate();
                            if(rowsChanged == 1) {
                                System.out.println("[Thank You]: Payment was made successfully.");
                                paymentMade = true;
                            } else {
                                System.out.println("[Error]: Issue with  payment method. Please login and try again.");
                                paymentMade = true; 
                            }
                            pStmt.close();

                        } else if(payNow.equalsIgnoreCase("n")) {
                            // print warning message, but do nothing
                            System.out.println("[Reminder]: If you do not pay by the end of the month, you will be charged a late fee of $100.\n");
                            break; // exit loop and quit

                        } else {
                            // invalid input
                            System.out.println("[Error]: Please enter either (y/n) as a proper response.");
                        }
                    }
                }
            }
            catch(SQLException sqle) {
                System.out.println("[Error]: Error with Database. Please try again.");
                sqle.printStackTrace();
            }
        } while(paymentMade == false);
    }

    // view payment history for tenant
    public static void viewPaymentHistory(Connection conn, int tenant_id) {
        try {
            // query to get payment history from tenant
            String payHistoryQ = "select * from payment_history where tenant_id = ?";
            PreparedStatement pStmt = conn.prepareStatement(payHistoryQ);
            pStmt.setInt(1, tenant_id);
            ResultSet rs = pStmt.executeQuery();

            if(rs.next() == false) {
                System.out.println("[Error]: No payment history found. Please try again.\n");
            } else {
                // print header
                System.out.println("Payment History");
                System.out.println("================");
                System.out.println("Date Paid\t\t\tAmount\t\tMethod");
                System.out.println("================");

                // print payment history
                do {
                    System.out.println(rs.getString(2) + "\t\t" + rs.getString(3) + "\t\t" + rs.getString(4));
                } while(rs.next());
                System.out.println();
            }
            pStmt.close();
            rs.close();
        }
        catch(SQLException sqle) {
            System.out.println("[Error]: Error with database. Please try again.");
            sqle.printStackTrace();
        }
    }


    // view amenities for tenant
    public static void viewAmenities(Connection conn, int tenant_id) {
        try {
            System.out.println("\nHere at NUMA, all our tenants have complimentary access to their property's amenities.");
            System.out.println("All amenities available at your property: ");

            String aQuery = "select * from amenities where p_id in (select p_id from lives_in natural join apartment where tenant_id = ?)";
            PreparedStatement pStmt = conn.prepareStatement(aQuery);
            pStmt.setInt(1, tenant_id);
            ResultSet rs3 = pStmt.executeQuery();

            while(rs3.next()) {
                System.out.println("\t" + rs3.getString(2));
            }
            System.out.println();
        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with database. Please try again.");
            sqle.printStackTrace();
        }
    }


    // add roommates
    public static void addRoommates(Connection conn, int tenant_id) {
        // instantiate scanner
        Scanner in = new Scanner(System.in);

        try {
            System.out.println("How many roommates would you like to add?");
            int numRoommates = Integer.parseInt(in.nextLine());
            int updatedRows = 0;

            for(int i=0; i < numRoommates; i++) {
                // gather roommate info to add to db
                System.out.println("Enter the first & last name of your new roommate:");
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
                addpStmt.setInt(4, tenant_id);
                // execute query
                if(addpStmt.executeUpdate() == 1) {
                    System.out.println("[Success]: You have successfully added a roommate.");
                    updatedRows++;
                } else {
                    System.out.println("[Error]: Could not add roommate. Please try again.");
                }
            }
            if(updatedRows == numRoommates) {
                System.out.println("[Update]: All roommates added successfully.");
            } 

        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with database. Please try again.");
            sqle.printStackTrace();
        }
    }


    // tenant move-out of building
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


    
}