import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class Payment {

    public static void makePayment(Connection conn, int tenant_id, String paymentMethod) {
        Scanner in = new Scanner(System.in);
        
        boolean madePayment = false;
        do {
            PreparedStatement pStmt = null;
            PreparedStatement pStmt1 = null;
            PreparedStatement pStmt2 = null;
            ResultSet rs = null;
            ResultSet rs1 = null;

            int lastAmtPaid = 0;
            int amountDue = 0;
            int remainingBal = 0;
            java.sql.Date datePaid = null;
            LocalDate nextPaymentDate = null;
            LocalDate currDate = LocalDate.now();

            try {
                boolean validRate = false;
                int monthly_price = 0;
                // get monthly rate
                do {
                    String rateQuery = "select monthly_price from lease natural join lives_in where tenant_id = ?";
                    pStmt = conn.prepareStatement(rateQuery);
                    pStmt.setInt(1, tenant_id);
                    rs = pStmt.executeQuery();

                    if(rs.next() == false) {
                        System.out.println("[Error]: Could not retrieve monthly lease price. Please try again.");
                    } else {
                        monthly_price = rs.getInt("monthly_price");
                        validRate = true;
                    }
                } while(!validRate);
                

                // get last payment info
                String lastPayQ = "select date_paid, amount from payment_history where tenant_id = ? order by date_paid desc fetch first row only";
                pStmt1 = conn.prepareStatement(lastPayQ);
                pStmt1.setInt(1, tenant_id);
                rs1 = pStmt1.executeQuery();

                boolean makePayment = false;
                if(rs1.next() == false) {
                    // never made a payment, therefore balance is current rate
                    amountDue = monthly_price;
                    System.out.println("You have a balance of $" + amountDue + ". Would you like to make a payment? (y/n)");

                    String answer = in.nextLine();
                    if(answer.equals("y")) {
                        makePayment = true;
                        System.out.println("[Update]: Transfering to Payment. \n");

                        boolean validPayment = false;
                        do {

                            System.out.println("How much of your $" + amountDue + " balance would you like to pay? (Enter a number)");
                            int payAmt = Integer.parseInt(in.nextLine());
                            if(payAmt > amountDue) {
                                System.out.println("[Error]: You cannot pay more than your balance. Please try again.");

                            } else {
                                // insert payment into payment_history
                                remainingBal = amountDue - payAmt;
                                String payQuery = "insert into payment_history (tenant_id, date_paid, amount, method) values (?, SYSDATE, ?, ?)";
                                pStmt2 = conn.prepareStatement(payQuery);
                                // set arguments of query
                                pStmt2.setInt(1, tenant_id);
                                pStmt2.setInt(2, payAmt);
                                pStmt2.setString(3, paymentMethod);
                                pStmt2.setInt(4, remainingBal);
                                int rowsChanged = pStmt2.executeUpdate();

                                if(rowsChanged == 1) {
                                    System.out.println("[Update]: Payment successful.");
                                    validPayment = true;
                                    madePayment = true;
                                    break;
                                } else {
                                    System.out.println("[Error]: Payment failed.");
                                }
                            }
                        } while(!validPayment);

                    } else if(answer.equals("n")) {
                        System.out.println("[Reminder]: If you do not pay by the end of the month, you will be charged a late fee.\n");
                        break;

                    } else {
                        System.out.println("Please enter a valid response (y/n).");
                    }
            

                } else {
                    // made a payment, therefore balance is current rate - last payment
                    lastAmtPaid = rs1.getInt("amount");
                    datePaid = rs1.getDate("date_paid");
                    if(rs1.getInt("remaining_balance") == 0) {
                        remainingBal = 0;
                    } else {
                        remainingBal = rs1.getInt("remaining_balance");
                    }
                    nextPaymentDate = datePaid.toLocalDate().plusMonths(1); 

                    if((remainingBal == 0) && currDate.isBefore(nextPaymentDate)) {
                        amountDue = 0;
                        System.out.println("[Note]: You have no remaining balance due.");

                    } else if ((remainingBal != 0) && currDate.isBefore(nextPaymentDate)) {
                        amountDue = remainingBal;
                        boolean validPayment = false;
                        do {
                            System.out.println("You have a balance of $" + amountDue + ". Would you like to make a payment? (y/n)");
                            String answer = in.nextLine();

                            if(answer.equals("y")) {
                                System.out.println("[Update]: Transferring to Payment...\n");

                                String payQuery = "insert into payment_history (tenant_id, date_paid, amount, method) values (?, SYSDATE, ?, ?)";
                                pStmt2 = conn.prepareStatement(payQuery);
                                // set arguments of query
                                pStmt2.setInt(1, tenant_id);
                                pStmt2.setInt(2, amountDue);
                                pStmt2.setString(3, paymentMethod);
                                pStmt2.setInt(4, remainingBal);
                                int rowsChanged = pStmt2.executeUpdate();

                                if(rowsChanged == 1) {
                                    System.out.println("[Update]: Payment successful.");
                                    validPayment = true;
                                    madePayment = true;
                                    break;
                                } else {
                                    System.out.println("[Error]: Payment failed.");
                                }

                            } else if (answer.equals("n")) {
                                System.out.println("[Reminder]: If you do not pay by the end of the month, you will be charged a late fee.\n");
                                break;

                            } else {
                                System.out.println("[Error]: Please enter a valid choice (y/n).");
                            }

                        } while(!validPayment);

                    }
                
                }

            }
            catch (SQLException sqle) {
                System.out.println("[Error]: Error with Database. Please try again.");
                // sqle.printStackTrace();
            }
            catch (NumberFormatException exp) {
                System.out.println("[Error]: Number Format Exception. Please try again and enter an integer.\n");
            }
            finally {
                try {
                    if (pStmt != null) {
                        pStmt.close();
                    }
                    if (pStmt1 != null) {
                        pStmt1.close();
                    }
                    if (pStmt2 != null) {
                        pStmt2.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (rs1 != null) {
                        rs1.close();
                    }
                } catch (SQLException sqle) {
                    System.out.println("[Error]: Error with Database. Please try again.");
                    // sqle.printStackTrace();
                }
            }
        } while(!madePayment);
    }
    
}
