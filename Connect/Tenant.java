import java.sql.*;
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
                     System.out.println("Please select an option below: ");
                     System.out.println("\t1: Make a payment.");
                     System.out.println("\t2: View payment history.");
                     System.out.println("\t3: View complimentary amenities.");
                     System.out.println("\t4: Add roommates.");
                     System.out.println("\t5: Update personal information.");
                     System.out.println("\t0: Exit");
                     choice = Integer.parseInt(in.nextLine());

                     switch(choice) {
                        case 0: // quit - display exit message
                            System.out.println("Thank you for using the NUMA Tenant Interface!");
                            System.out.println("Exiting...");
                            System.out.print("\033\143");
                            break;
                
                        case 1: // make payment on rent
                            String paymentMethod = getPaymentMethod(conn, t_id);
                            
                            if(paymentMethod.isEmpty()) {
                                // no payment method set
                                System.out.println("[Note]: You do not have a payment method on file. Would you like to add one? (y/n)");
                                String answer = in.nextLine();
                                if(answer.equalsIgnoreCase("y")) {
                                    // add payment method
                                    addPayment(conn, t_id);
                                    if(!getPaymentMethod(conn, t_id).isEmpty()) {
                                        paymentMethod = getPaymentMethod(conn, t_id);
                                    }
                                    // make payment after setting method
                                    makePayment(conn, t_id, paymentMethod);
                                    
                                } else {
                                    // quit, we need payment method
                                    System.out.println("[Note]: A payment method is required before making a payment. Please try again.");
                                    break;
                                }

                            } else {
                                // payment method is set
                                System.out.println("Your payment method on file is: " + paymentMethod + ". Would you like to use this to pay? (y/n)");
                                String option = in.nextLine();
                                if(option.equalsIgnoreCase("y")) {
                                    // process payment with payMethod
                                    makePayment(conn, t_id, paymentMethod);
                                    
                                } else if(option.equalsIgnoreCase("n")) {
                                    System.out.println("[Redirect]: Redirecting to Update Payment.");
                                    // call updatePayment method 
                                    updatePayment(conn, t_id);
                                    // after setting payment method, make payment
                                    makePayment(conn, t_id, paymentMethod);
                                }
                            }
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

                        case 5: // update personal information
                            updatePersonalInfo(conn, t_id);
                            break;
                        
                        default: // invalid input
                            System.out.println("[Error]: Please make a proper selection: ");
                            System.out.println("\t1: Make a payment.");
                            System.out.println("\t2: View payment history.");
                            System.out.println("\t3: View complimentary amenities.");
                            System.out.println("\t4: Add roommates.");
                            System.out.println("\t5: Update personal information.");
                            System.out.println("\t0: Exit");
                            break;
                     }  
                } while (choice != 0);
            }
            catch (InputMismatchException e) {
                System.out.println("[Error]: Error with input. Please try again.");
            }
            catch (Exception e) {
                System.out.println("[Error]: Undefined error. Please try again.");
                e.printStackTrace();
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
        catch (InputMismatchException e) {
            System.out.println("[Error]: Error with input. Please try again.");
        }
        catch (Exception e) {
            System.out.println("[Error]: Undefined error. Please try again.");
        }
        return t_id;
    }

    // update payment method
    public static void updatePayment(Connection conn, int tenant_id) {
        Scanner in = new Scanner(System.in);
        String paymentMethod = "";
        int paymentMethodId = -1;
        try {
            // get original payment method
            paymentMethod = getPaymentMethod(conn, tenant_id);

           if(paymentMethod.equals("Credit")) {
                paymentMethodId = 1;
           } else if(paymentMethod.equals("Venmo")) {
               paymentMethodId = 2;
           } else if(paymentMethod.equals("ACH")) {
                paymentMethodId = 3;
           } else {
           }

           boolean updatedPayment = false;
           int method;
           do {
               do {
                    System.out.println("What payment method would you like to use instead?");
                    System.out.println("\t0: Quit Interface");
                    System.out.println("\t1: Credit Card");
                    System.out.println("\t2: Venmo");
                    System.out.println("\t3: ACH Payment");
                    method = Integer.parseInt(in.nextLine());

                    if(method == paymentMethodId) {
                        System.out.println("[Error]: You already have this payment method on file.");
                    }

               } while(method == paymentMethodId);

                switch(method) {
                    case 0: // quit
                        System.out.println("[Exiting]: Exiting Interface.");
                        updatedPayment = true;
                        break;

                    case 1: // credit card
                        paymentMethod = "Credit";
                        String updateQ = "update payment set pay_method = ? where tenant_id = ?";
                        PreparedStatement pStmt = conn.prepareStatement(updateQ);
                        pStmt.setString(1, paymentMethod);
                        pStmt.setInt(2, tenant_id);
                        int rowsChanged = pStmt.executeUpdate();
            
                        if(rowsChanged == 1) {
                            System.out.println("[Success]: Payment method updated to: " + paymentMethod);
                            updatedPayment = true;

                        } else {
                            System.out.println("[Error]: Payment method couldl not be updated. Please try again.");
                        }
                        pStmt.close();
                        break;

                    case 2: // venmo
                        paymentMethod = "Venmo";
                        updateQ = "update payment set pay_method = ? where tenant_id = ?";
                        PreparedStatement pStmt1 = conn.prepareStatement(updateQ);
                        pStmt1.setString(1, paymentMethod);
                        pStmt1.setInt(2, tenant_id);
                        rowsChanged = pStmt1.executeUpdate();
            
                        if(rowsChanged == 1) {
                            System.out.println("[Success]: Payment method updated to: " + paymentMethod);
                            updatedPayment = true;

                        } else {
                            System.out.println("[Error]: Payment method couldl not be updated. Please try again.");
                        }
                        pStmt1.close();
                        break;

                    case 3: // ach
                        paymentMethod = "ACH";
                        updateQ = "update payment set pay_method = ? where tenant_id = ?";
                        PreparedStatement pStmt2 = conn.prepareStatement(updateQ);
                        pStmt2.setString(1, paymentMethod);
                        pStmt2.setInt(2, tenant_id);
                        rowsChanged = pStmt2.executeUpdate();
            
                        if(rowsChanged == 1) {
                            System.out.println("[Success]: Payment method updated to: " + paymentMethod);
                            updatedPayment = true;

                        } else {
                            System.out.println("[Error]: Payment method couldl not be updated. Please try again.");
                        }
                        pStmt2.close();
                        break;

                    default: // invalid input
                        System.out.println("[Error]: Please make a proper selection: ");
                        System.out.println("\t1: Credit Card");
                        System.out.println("\t2: Venmo");
                        System.out.println("\t3: ACH Payment");
                        break;
                }

           } while(updatedPayment == false);

        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with Update Payment.");
            // sqle.printStackTrace();
        }
        catch (InputMismatchException e) {
            System.out.println("[Error]: Error with input. Please try again.");
        }
        catch (Exception e) {
            System.out.println("[Error]: Undefined error. Please try again.");
        }
    }

    // add a payment method to a tenant's account
    public static void addPayment(Connection conn, int tenant_id) {
        Scanner in = new Scanner(System.in);
        boolean paymentSet = false;

        do {
            try {
                System.out.println("What payment method would you like to use?");
                System.out.println("\t0: Quit Interface");
                System.out.println("\t1: Credit Card");
                System.out.println("\t2: Venmo");
                System.out.println("\t3: ACH Payment");
                int method = Integer.parseInt(in.nextLine());

                switch(method) {
                    // quit payment interface
                    case 0:
                        System.out.println("[Exit]: Returning to Main Interface.]");
                        paymentSet = true;
                        break;

                    // add credit card
                    case 1:
                        String pQuery = "insert into payment (tenant_id, pay_method) values (?, ?)";
                        PreparedStatement pStatement = conn.prepareStatement(pQuery);
                        pStatement.setInt(1, tenant_id);
                        pStatement.setString(2, "Credit");
                        int rowsChanged = pStatement.executeUpdate();

                        if(rowsChanged == 1) {
                            System.out.println("[Update]: Payment method was set to CREDIT. Please enter your CC Info Below." );
                            
                        } else {
                            System.out.println("[Error]: Payment could not be updated. Please try again..");
                        }

                        pStatement.close();

                        // collect credit card info
                        boolean validCC = false;
                        String ccNum = "";
                        String expDate = "";
                        String cvv = "";
                        do {

                            System.out.println("Enter the Credit Card Number: ");
                            ccNum = in.nextLine();
                            if(!ccNum.matches("^[0-9]{16}$")) {
                                System.out.println("[Error]: Invalid Credit Card Number. Please try again.");
                                continue;
                            }
                            System.out.println("Enter the Expiration Date: (MM/YY) ");
                            expDate = in.nextLine();
                            if(!expDate.matches("^[0-1]{1}[0-9]{1}/[0-9]{2}$")) {
                                System.out.println("[Error]: Invalid Expiration Date. Please try again.");
                                continue;
                            }
                            System.out.println("Enter the CVV: (XXX)");
                            cvv = in.nextLine();
                            if(!cvv.matches("^[0-9]{3}$")) {
                                System.out.println("[Error]: Invalid CVV. Please try again.");
                                continue;
                            }
                            validCC = true;

                        } while(!validCC);

                        // add cc info to tables
                        String ccQuery = "insert into credit (tenant_id, card_num, exp_date, cvv) values (?, ?, ?, ?)";
                        PreparedStatement pStmt = conn.prepareStatement(ccQuery);
                        pStmt.setInt(1, tenant_id);
                        pStmt.setString(2, ccNum);
                        pStmt.setString(3, expDate);
                        pStmt.setString(4, cvv);

                        // check output, must change 1 row to be successful
                        rowsChanged = pStmt.executeUpdate();
                        if(rowsChanged == 1) {
                            System.out.println("[Update]: Payment added successfully!");
                            paymentSet = true;
                        }
                        else {
                            System.out.println("[Error]: Payment failed to add. Please login to our portal and try again.");
                        }
                        pStmt.close();

                        break;
                    
                    // add venmo
                    case 2:
                        pQuery = "insert into payment (tenant_id, pay_method) values (?, ?)";
                        pStatement = conn.prepareStatement(pQuery);
                        pStatement.setInt(1, tenant_id);
                        pStatement.setString(2, "Venmo");

                        rowsChanged = pStatement.executeUpdate();
                        if(rowsChanged == 1) {
                            System.out.println("[Update]: Payment method was set to VENMO. Please enter your Venmo Info Below." );
                            
                        } else {
                            System.out.println("[Error]: Payment could not be updated. Please try again..");
                        }

                        pStatement.close();

                        // get venmo info from user
                        System.out.println("What is your venmo username? ");
                        String vUser = in.nextLine();
        
                        String vQuery = "insert into venmo (tenant_id, username) values (?, ?)";
                        PreparedStatement pStmt1 = conn.prepareStatement(vQuery);
                        // set arguments for venmo account
                        pStmt1.setInt(1, tenant_id);
                        pStmt1.setString(2, vUser);
        
                        // check output for success
                        rowsChanged = pStmt1.executeUpdate();
                        if (rowsChanged == 1) {
                            System.out.println("[Update]: Payment added successfully!");
                            paymentSet = true;
                        } else {
                            System.out.println("[Error]: Payment failed to add. Please go through our portal again and try again.");
                        }
                        pStmt1.close();

                        break;

                    // add ach payment
                    case 3:
                        pQuery = "insert into payment (tenant_id, pay_method) values (?, ?)";
                        pStatement = conn.prepareStatement(pQuery);
                        pStatement.setInt(1, tenant_id);
                        pStatement.setString(2, "ACH");

                        rowsChanged = pStatement.executeUpdate();
                        if(rowsChanged == 1) {
                            System.out.println("[Update]: Payment method was set to ACH. Please enter your ACH Info Below." );
                            
                        } else {
                            System.out.println("[Error]: Payment could not be updated. Please try again..");
                        }

                        pStatement.close();
                        
                        String routingQ = "select bank from tenant where tenant_id = ?";
                        PreparedStatement pStmt2 = conn.prepareStatement(routingQ);
                        pStmt2.setInt(1, tenant_id);
                        ResultSet rs = pStmt2.executeQuery();
                        String accNum = "";
                        while(rs.next()) {
                            accNum = rs.getString(1);
                        }

                        pStmt2.close();
                        rs.close();

                        // get ach info from user
                        System.out.println("Your account number on file is: " + accNum);
                        System.out.println("Enter your routing number: ");
                        String routingNum = in.nextLine();

                        String achQuery = "insert into ach (tenant_id, routing_num) values (?, ?)";
                        PreparedStatement pStmt3 = conn.prepareStatement(achQuery);
                        // set arguments for ach account
                        pStmt3.setInt(1, tenant_id);
                        pStmt3.setString(2, routingNum);
                        
                        // check output for success
                        rowsChanged = pStmt3.executeUpdate();
                        if (rowsChanged == 1) {
                            System.out.println("[Update]: Payment added successfully!");
                            paymentSet = true;
                        } else {
                            System.out.println("[Error]: Payment failed to add. Please go through our portal again and try again.");
                        }
                        pStmt3.close();

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
            catch (InputMismatchException e) {
                System.out.println("[Error]: Error with input. Please try again.");
            }
            catch (Exception e) {
                System.out.println("[Error]: Undefined error. Please try again.");
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
                java.sql.Date datePaid = null;
                LocalDate nextPaymentDate = null;
                LocalDate currDate = LocalDate.now();
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
                    datePaid = lastPayRS.getDate("date_paid");
                    // add 1 month to datePaid
                    nextPaymentDate = datePaid.toLocalDate();
                    nextPaymentDate.plusMonths(1);

                    // calculate amount due
                    if((last_paid == monthly_price) && (nextPaymentDate.isAfter(currDate))) {
                        System.out.println("[Note]: You have already paid for this month. Remaining balance is 0.\n");
                        paymentMade = true;
                    } else if((last_paid < monthly_price) && (nextPaymentDate.isAfter(currDate))) {
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
            // sqle.printStackTrace();
        }
    }


    // view amenities for tenant
    public static void viewAmenities(Connection conn, int tenant_id) {
        try {
            System.out.println("\nHere at NUMA, all our tenants have complimentary access to their property's amenities.");

            String aQuery = "select amenity_type from amenities where p_id in (select p_id from lives_in natural join apartment where tenant_id = ?)";
            PreparedStatement pStmt = conn.prepareStatement(aQuery);
            pStmt.setInt(1, tenant_id);
            ResultSet rs = pStmt.executeQuery();

            if(rs.next() == false) {
                System.out.println("[Apologies]: We are currently working to get more amenities at your property.");
            } else {

                System.out.println("\nAmenities Offered");
                System.out.println("================");
                do {
                    System.out.println(rs.getString(1));
                } while(rs.next());
                System.out.println();
            }
            pStmt.close();
            rs.close();
        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with database. Please try again.\n");
            // sqle.printStackTrace();
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
            PreparedStatement addpStmt = null;

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
                addpStmt = conn.prepareStatement(addRoommateQ);
                // set parameters for roommate
                addpStmt.setString(1, name);
                addpStmt.setString(2, phone);
                addpStmt.setString(3, email);
                addpStmt.setInt(4, tenant_id);
                // execute query
                if(addpStmt.executeUpdate() == 1) {
                    System.out.println("[Success]: You have successfully added a roommate.\n");
                    updatedRows++;
                } else {
                    System.out.println("[Error]: Could not add roommate. Please try again.");
                }
            }
            if(updatedRows == numRoommates) {
                System.out.println("[Update]: All roommates added successfully.\n");
            } 
            addpStmt.close();
        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with database. Please try again.\n");
            // sqle.printStackTrace();
        }
    }


    // update personal information
    public static void updatePersonalInfo(Connection conn, int tenant_id) {
        Scanner in = new Scanner(System.in);
        int choice;
        try {
            System.out.println("What information would you like to update?");
            System.out.println("\t0. Quit");
            System.out.println("\t1. Phone Number");
            System.out.println("\t2. Social Security Number (SSN)");
            System.out.println("\t3. Bank Routing");
            choice = Integer.parseInt(in.nextLine());

            switch(choice) {
                case 0: // quit interface
                    System.out.println("Returing to Main Interface...");
                    break;

                case 1: // phone number
                    System.out.println("Enter the new phone number you would like on record: (xxx-xxx-xxxx) ");
                    String pNumber = in.nextLine();

                    String phoneUpdate = "update tenant set phone = ? where tenant_id = ?";
                    PreparedStatement pStmt = conn.prepareStatement(phoneUpdate);
                    pStmt.setString(1, pNumber);
                    pStmt.setInt(2, tenant_id);
                    int rowsChanged = pStmt.executeUpdate();

                    if(rowsChanged == 1) {
                        System.out.println("[Update]: Phone number was updated in the database.");
                    } else {
                        System.out.println("[Error]: Phone number could not be updated. Please try again.");
                    }

                    pStmt.close();
                    break;

                case 2: // ssn
                    System.out.println("Enter the SSN you would like on record: (xxx-xx-xxxx)");
                    String ssn = in.nextLine();

                    String emailUpdate = "update tenant set ssn = ? where tenant_id = ?";
                    PreparedStatement pStmt1 = conn.prepareStatement(emailUpdate);
                    pStmt1.setString(1, ssn);
                    pStmt1.setInt(2, tenant_id);
                    rowsChanged = pStmt1.executeUpdate();

                    if(rowsChanged == 1) {
                        System.out.println("[Update]: Your SSN was updated in the system.");
                    } else {
                        System.out.println("[Error]: SSN could not be updated. Please try again.\n");
                    }

                    pStmt1.close();
                    break;

                case 3: // bank routing
                    System.out.println("Enter your new Bank Routing Number: (at least 5 digits)");
                    String routing = in.nextLine();

                    String routingUpdate = "update tenant set routing = ? where tenant_id = ?";
                    PreparedStatement pStmt2 = conn.prepareStatement(routingUpdate);
                    pStmt2.setString(1, routing);
                    pStmt2.setInt(2, tenant_id);
                    rowsChanged = pStmt2.executeUpdate();

                    if(rowsChanged == 1) {
                        System.out.println("[Update]: Your Bank Routing Number was updated in the system.");
                    } else {
                        System.out.println("[Error]: Routing Number could not be updated. Please try again.\n");
                    }

                    pStmt2.close();
                    break;

                default:
                    System.out.println("Please make a valid selection: ");  
                    System.out.println("\t0. Quit");
                    System.out.println("\t1. Phone Number");
                    System.out.println("\t2. Email Address");
                    System.out.println("\t3. Bank Routing");
                    break;
            }

        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with database. Please try again.");
        }
        catch (InputMismatchException e) {
            System.out.println("[Error]: Input Mismatch Error. Please try again.");
        }
        catch (Exception e) {
            System.out.println("[Error]: Undefined error. Please try again.");
        }
    }

    // get payment method of tenant
    public static String getPaymentMethod(Connection conn, int tenant_id) {
        
        String paymentMethod = "";
        try {
            String paymentQ = "select pay_method from payment where tenant_id = ?";
            PreparedStatement pStmt = conn.prepareStatement(paymentQ);
            pStmt.setInt(1, tenant_id);
            ResultSet rs = pStmt.executeQuery();

            if(rs.next()) {
                paymentMethod = rs.getString("pay_method");
                return paymentMethod;
            } else {
                return paymentMethod;
            }

        }
        catch(SQLException sqle) {
            System.out.println("[Error]: Error with database. Please try again.");
        }
        catch(Exception e) {
            System.out.println("[Error]: Undefined error. Please try again.");
        }
        return paymentMethod;
    }
    
}