import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class Manager {
    
    public static void managerInterface() {

        final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";
        // instantiate connection and scanner
        Connection conn = null;
        Scanner in = new Scanner(System.in);

        do {
            try {
                // get property manager login info (to oracle)
                System.out.println("Enter the Property Manager Username: ");
                String user = in.nextLine();
                System.out.println("Enter the Property Manager Password: ");
                String pass = in.nextLine();

                // attempt connection to database
                conn = DriverManager.getConnection(DB_URL, user, pass);
                int choice;

                do {
                    // interface menu
                    System.out.println("What actions would you like to complete today?");
                    System.out.println("1: View all property listings.");
                    System.out.println("2: View all apartments of a property listing.");
                    System.out.println("0: Exit Program");

                    // switch through choices
                    choice = Integer.parseInt(in.nextLine());
                    switch (choice) {
                        case 1:
                            // execute query to return ALL property listings
                            System.out.println("Here is a list of all property listings in the area.\n");
                            String propertyQuery = "select * from property";
                            PreparedStatement pStmt = conn.prepareStatement(propertyQuery);

                            ResultSet rs1 = pStmt.executeQuery();
                            ResultSetMetaData rsmd = rs1.getMetaData();
                            int columnsNumber = rsmd.getColumnCount();

                            // print result set
                            while(rs1.next()) {
                                for(int i = 1; i <= columnsNumber; i++) {
                                        System.out.print(rs1.getString(i) + " ");
                                }
                                System.out.println();
                            }
                            System.out.println();
                            break;

                        case 2:
                            System.out.println("Enter the property ID of the property you would like to view.");
                            int pID = Integer.parseInt(in.nextLine());
                            
                            // create query to search for all apartments in given property
                            String aptQuery = "select * from apartment where p_id = ?";
                            PreparedStatement pStmt2 = conn.prepareStatement(aptQuery);
                            pStmt2.setInt(1, pID);

                        ResultSet rs2 = pStmt2.executeQuery();
                        ResultSetMetaData rsmd2 = rs2.getMetaData();
                            int colNum = rsmd2.getColumnCount();

                            // print result set
                            while(rs2.next()) {
                                for(int i = 1; i <= colNum; i++) {
                                    System.out.print(rs2.getString(i) + " ");
                                }
                                System.out.println();
                            }
                            System.out.println();
                            break;
                            
                        case 3:
                            System.out.println("----NUMA Manager Interface----");
                            // call to manager class
                            break;
                        
                        default:
                            System.out.println("Please make a proper selection (1-3).");
                            System.out.println("1. Property Manager");
                            System.out.println("2. Tenant");
                            System.out.println("3. NUMA Manager");
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
