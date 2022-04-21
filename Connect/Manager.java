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
                System.out.println("Welcome to the NUMA Property Manager Interface!\n");
                
                int choice;
                do {
                    // interface menu
                    System.out.println("Please select an option below:");
                    System.out.println("\t1: View all property listings.");
                    System.out.println("\t2: View all apartments available for rent.");
                    System.out.println("\t3: Record a visit by a prospective tenant.");
                    System.out.println("\t4: Add a tenant to system and sign a lease.");
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

                            System.out.println("Properties in NUMA Enterprises: ");
                            System.out.println("================");
                            System.out.println("Property ID\tStreet\t\t\tCity\t\t\tState\t\tNum. Apts.\t\t");

                            if(rs1.next() == false) {
                                System.out.println("[Error]: No properties found in NUMA System.");
                            } else {
                                // print property info
                                do {
                                    System.out.println(rs1.getString(1) + "\t\t" + rs1.getString(2) + "\t\t" + rs1.getString(3) + "\t\t" + rs1.getString(4) + "\t\t" + rs1.getString(5) + "\t\t");
                                } while(rs1.next());
                                System.out.println();
                            }
                            break;

                        case 2:
                            // query to search for apartments that have a lease but no tenant (available for rent)
                            String leaseQuery = "select apt_num, num_beds, num_baths, sq_foot, monthly_price, p_id from lease natural join apartment where apt_num not in (select apt_num from lives_in)";
                            PreparedStatement pStmt3 = conn.prepareStatement(leaseQuery);
                            ResultSet rs3 = pStmt3.executeQuery();

                            // display results
                            System.out.println("Apartments for Rent in NUMA Enterprises: ");
                            System.out.println("================");
                            System.out.println("Apt. Num\tBeds\t\tBaths\t\tSq. Ft.\t\tPrice (per M)\t\tProperty ID");

                            if(rs3.next() == false) {
                                System.out.println("[Error]: No apartments available for rent at specified property.");
                            } else {
                                // print out apartment info
                                do {
                                    System.out.println(rs3.getString(1) + "\t\t" + rs3.getString(2) + "\t\t" + rs3.getString(3) + "\t\t" + rs3.getString(4) + "\t\t" + rs3.getString(5) + "\t\t\t" + rs3.getString(6));
                                } while(rs3.next());
                                System.out.println();
                            }
                            break;

                        case 3:
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
                                System.out.println("[Update]: Visitor '" + visitorName + "' has been added to the database.\n");
                            } else {
                                System.out.println("[Error]: Visitor was not added to the database.\n");
                            }
                            break;

                        case 4:
                            System.out.println("\nWhat apartment would you like to rent?");
                            int apt_num = Integer.parseInt(in.nextLine());
                             // get all empty apartment IDs
                             String emptyAptQ = "select apt_num from lease natural join apartment where apt_num not in (select apt_num from lives_in)";
                             PreparedStatement pStmt5 = conn.prepareStatement(emptyAptQ);
                             ResultSet rs5 = pStmt5.executeQuery();
                             ResultSetMetaData rsmd2 = rs5.getMetaData();
                             int colNum2 = rsmd2.getColumnCount();
                             ArrayList<String> aptNums = new ArrayList<String>(colNum2);
                             while(rs5.next()) {
                                 int i = 1;
                                 while(i <= colNum2) {
                                     aptNums.add(rs5.getString(i++));
                                 }
                             }
                             // if empty, then we insert tenant
                             if(aptNums.contains(Integer.toString(apt_num))) {
                                // get tenant info
                                System.out.println("Enter the tenant's Name: ");
                                String name = in.nextLine();
                                System.out.println("Enter the tenant's SSN: ");
                                String ssn = in.nextLine();
                                System.out.println("Enter the tenant's Phone Number: ");
                                String phone = in.nextLine();
                                System.out.println("Enter the tenant's Bank Routing: ");
                                String bank = in.nextLine();

                                // insert into tenant table
                                int tenant_id = createTenantID(conn);
                                System.out.println("TENANT ID: "+ tenant_id);
                                String insertQ = "insert into tenant values (?, ?, ?, ?, ?)";
                                PreparedStatement pStmt6 = conn.prepareStatement(insertQ);
                                pStmt6.setInt(1, tenant_id);
                                pStmt6.setString(2, name);
                                pStmt6.setString(3, ssn);
                                pStmt6.setString(4, phone);
                                pStmt6.setString(5, bank);
                                int rowsChanged2 = pStmt6.executeUpdate();

                                // insert into lives_in table (equivalent to signing the lease)
                                String insertQ2 = "insert into lives_in values (?, ?)";
                                PreparedStatement pStmt7 = conn.prepareStatement(insertQ2);
                                pStmt7.setInt(1, tenant_id);
                                pStmt7.setInt(2, apt_num);
                                int rowsChanged3 = pStmt7.executeUpdate();

                                if(rowsChanged2 == 1 && rowsChanged3 == 1) {
                                    System.out.println("[Update]: Tenant '" + name + "' has been added to the database.\n");

                                } else {
                                    System.out.println("[Error]: Tenant could not be added to the database.\n");
                                    System.out.println("Tenant ID: " + tenant_id);
                                }

                             } else {
                                 // if apartment is not empty, print error message
                                 System.out.println("[Error]: This apartment is currently occupied and not available for rent.");
                                 System.out.println("Please choose another apartment to rent.\n");
                             }
                            break;
                        
                        default:
                            // invalid choice
                            System.out.println("Please make a proper selection (1-3).");
                            System.out.println("\t1: View all property listings.");
                            System.out.println("\t2: View all apartments for rent in a property.");
                            System.out.println("\t3: Record a visit by a prospective tenant.");
                            System.out.println("\t4: Add a tenant to system and sign a lease.");
                            System.out.println("\t0: Exit");
                            break;
                    }
                } while (choice != 0);
            }
            catch (SQLException sqle) {
                System.out.println("[Error]: Database error. Please try again.");
                sqle.printStackTrace();
            }
        } while (conn == null);

    }

    // create new tenant ID for a new tenant
    public static int createTenantID(Connection conn) {
        int newTID = 0;
        try {
            // get all tenant ids 
            String loginQ = "select tenant_id from tenant order by cast(tenant_id as int)";
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
            // get lastID value, since we inserted numerically ordered, this should be the largest/last tennat
            int lastID = Integer.parseInt(idList.get(idList.size() - 1));
            // new ID is last + 1
            newTID = lastID + 1;
            return newTID;
        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with Tenant Login.");
            sqle.printStackTrace();
        }
        return newTID;
    }

}
