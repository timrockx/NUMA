import java.sql.*;
import java.util.*;

public class Manager {
    
    // main interface for property manger
    public static void managerInterface(Connection conn) {
        // instantiate scanner
        Scanner in = new Scanner(System.in);
        do {
            try {
                System.out.print("\033\143");
                System.out.println("Welcome to the NUMA Property Manager Interface!\n");

                int p_id = loginPropManager(conn);
                if(p_id == -1) break;
                
                int choice;
                do {
                    // interface menu
                    System.out.println("Please select an option below:");
                    System.out.println("\t1: View all property listings.");
                    System.out.println("\t2: View all apartments available for rent.");
                    System.out.println("\t3: Record a visit by a prospective tenant.");
                    System.out.println("\t4: Add a tenant to system and sign a lease.");
                    System.out.println("\t5: Move out a tenant.");
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
                            String propertyQuery = "select * from property order by p_id";
                            PreparedStatement pStmt = conn.prepareStatement(propertyQuery);
                            ResultSet rs1 = pStmt.executeQuery();

                            if(rs1.next() == false) {
                                System.out.println("[Error]: No properties found in NUMA System.\n");

                            } else {
                                // display results
                                System.out.println("\nProperties in NUMA Enterprises: ");
                                System.out.println("================");
                                System.out.println("Property ID\tStreet\t\t\tCity\t\t\tState\t\tZip Code\t\t# Apts.");

                                do {
                                    System.out.println(rs1.getString(1) + "\t\t" + rs1.getString(2) + "\t\t" + rs1.getString(3) + "\t\t" + rs1.getString(4) + "\t\t" + rs1.getString(5) + "\t\t\t" + rs1.getString(6) + "\t\t");
                                } while(rs1.next());
                                System.out.println();
                            }
                            break;

                        case 2:
                            // query to search for apartments that have a lease but no tenant (available for rent)
                            String leaseQuery = "select apt_num, num_beds, num_baths, sq_foot, monthly_price, p_id from lease natural join apartment where apt_num not in (select apt_num from lives_in) order by p_id, apt_num";
                            PreparedStatement pStmt3 = conn.prepareStatement(leaseQuery);
                            ResultSet rs3 = pStmt3.executeQuery();

                            if(rs3.next() == false) {
                                System.out.println("[Error]: No apartments available for rent in the NUMA System.\n");

                            } else {
                                // display results
                                System.out.println("\nApartments for Rent in NUMA Enterprises: ");
                                System.out.println("================");
                                System.out.println("Apt. Num\tBeds\t\tBaths\t\tSq. Ft.\t\tPrice ($/M)\t\tProperty ID");

                                do {
                                    System.out.println(rs3.getString(1) + "\t\t" + rs3.getString(2) + "\t\t" + rs3.getString(3) + "\t\t" + rs3.getString(4) + "\t\t" + rs3.getString(5) + "\t\t\t" + rs3.getString(6));
                                } while(rs3.next());
                                System.out.println();
                            }
                            break;

                        case 3:
                            // record a visit by a prospective tenant
                            boolean validName = false;
                            String visitorName = "";
                            do {
                                System.out.println("Enter the visitor's name: ");
                                visitorName = in.nextLine();
                                if(visitorName.length() > 40) {
                                    System.out.println("[Error]: Name must be less than 40 characters.\n");
                                } else {
                                    validName = true;
                                }
                            } while(!validName);
                            
                            boolean validPhone = false;
                            String visitorPhone = "";
                            do {
                                System.out.println("Enter the visitor's phone number: (xxx-xxx-xxxx) ");
                                visitorPhone = in.nextLine();
                                if(!visitorPhone.matches("\\d{3}-\\d{3}-\\d{4}")) {
                                    System.out.println("[Error]: Phone number must be in the format xxx-xxx-xxxx.\n");
                                } else {
                                    validPhone = true;
                                }
                            } while(!validPhone);
                            
                            boolean validEmail = false;
                            String visitorEmail = "";
                            do {
                                System.out.println("Enter the visitor's email: ");
                                visitorEmail = in.nextLine();
                                if(visitorEmail.length() > 40 || !visitorEmail.contains("@")) {
                                    System.out.println("[Error]: Email must be less than 40 characters and must contain an @ symbol.\n");
                                } else {
                                    validEmail = true;
                                }
                            } while(!validEmail);
                            
                            // insert query
                            String visitorQ = "insert into visitor values (?, ?, ?)";
                            PreparedStatement pStmt4 = conn.prepareStatement(visitorQ);
                            pStmt4.setString(1, visitorName);
                            pStmt4.setString(2, visitorPhone);
                            pStmt4.setString(3, visitorEmail);
                            // execute query
                            int rowsChanged = pStmt4.executeUpdate();
                            if(rowsChanged == 1) {
                                System.out.println("\n[Update]: Visitor '" + visitorName + "' has been added to the database.\n");
                            } else {
                                System.out.println("\n[Error]: Visitor was not added to the database.\n");
                            }
                            break;

                        case 4:
                            // move a tenant into apartment
                            moveTenantIn(conn);
                            break;

                        case 5:
                            // move out a tenant
                             moveTenantOut(conn);
                             break;
                        
                        default:
                            // invalid choice
                            System.out.println("Please make a proper selection: ");
                            System.out.println("\t1: View all property listings.");
                            System.out.println("\t2: View all apartments for rent in a property.");
                            System.out.println("\t3: Record a visit by a prospective tenant.");
                            System.out.println("\t4: Add a tenant to system and sign a lease.");
                            System.out.println("\t5: Move out a tenant.");
                            System.out.println("\t0: Exit");
                            break;
                    }
                } while (choice != 0);
            }
            catch (SQLException sqle) {
                System.out.println("[Error]: Database error. Please try again.");
                // sqle.printStackTrace();
            }
            catch (InputMismatchException e) {
                System.out.println("[Error]: Error with input. Please try again.");
            }
            catch (Exception e) {
                System.out.println("[Error]: Undefined error. Please try again.");
            }
        } while (conn == null);

    }

    // login in for property manager
    public static int loginPropManager(Connection conn) {
        Scanner in = new Scanner(System.in);
        System.out.println("\n-----Login for Property Manager----- (See README for Login Help)\n");
        boolean validLogin = false;
        int pID = -1;
        try {
            PreparedStatement pStmt = null;
            ResultSet rs = null;

            // get all prop manager ids
            String idQuery = "select id from loginInfo where interface = 'propManager'";
            pStmt = conn.prepareStatement(idQuery);
            rs = pStmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();

            // store all tenant ids into an arraylist
            ArrayList<String> idList = new ArrayList<String>(colCount);
            while(rs.next()) {
                int i = 1;
                while(i <= colCount) {
                    idList.add(rs.getString(i++));
                }
            }
            pStmt.close();
            rs.close();

            do {
                System.out.println("Enter your Property Manager ID: (-1 to Quit) ");
                pID = Integer.parseInt(in.nextLine());
                if(pID == -1) {
                    System.out.println("\n[Exiting]: Returning to Main Interface.\n");
                    return -1;
                }

                if(idList.contains(Integer.toString(pID))) {
                    validLogin = true;
                    return pID;
                } else {
                    System.out.println("[Error]: Invalid ID. Please try again.\n");
                }

            } while(!validLogin);
            
            return pID;
        } 
        catch (SQLException sqle) {
            System.out.println("[Error]: Database error. Please try again.");
            sqle.printStackTrace();
        }
        catch (NumberFormatException exp) {
            System.out.println("[Error]: Invalid input. Please try again.");
        }
        catch (Exception e) {
            System.out.println("[Error]: Undefined error. Please try again.");
        }
        return pID;
    }

    // create new tenant ID for a new tenant
    public static int createTenantID(Connection conn) {
        int newTID = 0;
        try {
            String loginQ = "select max(tenant_id) from tenant";
            PreparedStatement pStmt = conn.prepareStatement(loginQ);
            ResultSet rs = pStmt.executeQuery();

            if(rs.next()) {
                newTID = rs.getInt(1) + 1;
                return newTID;

            } else {
                newTID = 1;
            }
        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with Tenant Login.");
            // sqle.printStackTrace();
        }
        return newTID;
    }

    // move a tenant into an empty apartment
    public static void moveTenantIn(Connection conn) {
        Scanner in = new Scanner(System.in);
        try {
            System.out.println("\nEnter the apartment number to rent: ");
            int apt_num = Integer.parseInt(in.nextLine());

            // get all empty apartments
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

            if(aptNums.contains(Integer.toString(apt_num))) {
                // apartment is open for rent
                boolean validName = false;
                String name = "";
                do {
                    System.out.println("Enter the tenant's name: ");
                    name = in.nextLine();
                    if(name.length() > 40) {
                        System.out.println("[Error]: Name must be less than 40 characters.\n");
                    } else {
                        validName = true;
                    }
                } while(!validName);

                boolean validSSN = false;
                String ssn = "";
                do {
                    System.out.println("Enter the tenant's SSN: (xxx-xx-xxxx) ");
                    ssn = in.nextLine();
                    if(!ssn.matches("\\d{3}-\\d{2}-\\d{4}")) {
                        System.out.println("[Error]: SSN must be in the format (xxx-xx-xxxx).\n");
                    } else {
                        validSSN = true;
                    }
                } while(!validSSN);


                boolean validPhone = false;
                String phone = "";
                do {
                    System.out.println("Enter the tenant's phone number: (xxx-xxx-xxxx) ");
                    phone = in.nextLine();
                    if(!phone.matches("\\d{3}-\\d{3}-\\d{4}")) {
                        System.out.println("[Error]: Phone number must be in the format (xxx-xxx-xxxx).\n");
                    } else {
                        validPhone = true;
                    }
                } while(!validPhone);

                boolean validBank = false;
                String bank = "";
                do {
                    System.out.println("Enter the tenant's bank account number: ");
                    bank = in.nextLine();
                    if(bank.length() <= 5 || bank.length() > 10) {
                        System.out.println("[Error]: Bank account number must be between 5 and 10 characters.\n");
                    } else {
                        validBank = true;
                    }
                } while(!validBank);

                 // insert into tenant table
                int tenant_id = createTenantID(conn);
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
        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with database.");
            sqle.printStackTrace();
        }
        catch (InputMismatchException e) {
            System.out.println("[Error]: Error with input. Please try again.");
        }
        catch (Exception e) {
            System.out.println("[Error]: Undefined error. Please try again.");
        }
    }


    // move tenant out
    public static void moveTenantOut(Connection conn) {
        Scanner in = new Scanner(System.in);
        try {
            System.out.println("\nEnter the tenant's ID: ");
            int tenant_id = Integer.parseInt(in.nextLine());

            // verify tenant id
            tenant_id = verifyTenantID(conn, tenant_id);
            if(tenant_id == 0) {
                System.out.println("[Error]: Tenant ID is invalid. Please try again.\n");
                return;
            }

            // remove from tenant table, rest will follow
            String deleteQ = "delete from tenant where tenant_id = ?";
            PreparedStatement pStmt = conn.prepareStatement(deleteQ);
            pStmt.setInt(1, tenant_id);
            int rowsChanged = pStmt.executeUpdate();

            if(rowsChanged == 1) {
                System.out.println("[Update]: Tenant has been moved out of NUMA Enterprises.\n");
            } else {
                System.out.println("[Error]: Tenant could not be moved out successfully. Please try again.\n");
            }

        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with database.");
            // sqle.printStackTrace();
        }
        catch (InputMismatchException e) {
            System.out.println("[Error]: Error with input. Please try again.");
        }
        
    }


    // verify a tenant ID is in database
    public static int verifyTenantID(Connection conn, int tenant_id) {
        try {
            // get all tenant IDS, then check that the inputted ID is in the list
            String verifyQ = "select tenant_id from tenant";
            PreparedStatement pStmt = conn.prepareStatement(verifyQ);
            ResultSet rs = pStmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();

            // store all tenant ids into an arraylist
            ArrayList<String> idList = new ArrayList<String>(colCount);
            while(rs.next()) {
                int i = 1;
                while(i <= colCount) {
                    idList.add(rs.getString(i++));
                }
            }
            // check ID is in validated list
            if(idList.contains(Integer.toString(tenant_id))) {
                return tenant_id;
            }

        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Error with database.");
            // sqle.printStackTrace();
        }
        return 0;
    }

}