import java.sql.*;
import java.util.*;

public class numaManager {

    public static void numaManagerInterface(Connection conn) {
        // instantiate scanner
        Scanner in = new Scanner(System.in);
        do {
            try {
                System.out.print("\033\143");
                System.out.println("Welcome to the NUMA Manager Interface!\n");

                int choice;
                do {
                    // menu options
                    System.out.println("Choose one of the options below: ");
                    System.out.println("\t0. Quit Interface.");
                    System.out.println("\t1. Add a new property.");
                    System.out.println("\t2. Add new amenities to an exisitng property.");
                    choice = Integer.parseInt(in.nextLine());

                    switch(choice) {
                        case 0: // quit interface
                            System.out.println("Thanks for using the NUMA Manager Interface!");
                            System.out.println("Exiting...");
                            System.out.print("\033\143");
                            break;

                        case 1: // add a new property to datbase
                            addProperty(conn);
                            break;

                        case 2: // add new amenities to pre-existing properties
                            addAmenity(conn);    
                            break;

                        default:
                            System.out.println("Please select a valid option: ");
                            System.out.println("\t1. Add a new property.");
                            System.out.println("\t2. Add new amenities to a property.");
                            break;
                    }

                } while (choice != 0);

            }
            // catch (SQLException sqle) {
            //     System.out.println("[Error]: Database error. Please try again.");
            //     sqle.printStackTrace();
            // }
            catch (InputMismatchException e) {
                System.out.println("[Error]: Invalid input. Please try again.");
                //e.printStackTrace();
            }
            catch (Exception e) {
                System.out.println("[Error]: Invalid input. Please try again.");
                //e.printStackTrace();
            }
        } while(conn == null);

    }


    public static void addProperty(Connection conn) {
        Scanner in = new Scanner(System.in);
        try {
            boolean propAdded = false;
            String street, city, state, zip;
            int numApartments, p_id;
            do {
                System.out.println("------Add a new property------");
                // get basic info to add to propety table
                System.out.println("Please enter the following information: ");
                System.out.println("Street: ");
                street = in.nextLine();
                System.out.println("City: ");
                city = in.nextLine();
                System.out.println("State: ");
                state = in.nextLine();
                System.out.println("Zip Code: ");
                zip = in.nextLine();
                System.out.println("How many apartments do you want in this property? (1-20) Since this is a new development property.");
                numApartments = Integer.parseInt(in.nextLine());

                p_id = createPropertyID(conn);

                // insert statement
                String newPropQ = "insert into property values (?, ?, ?, ?, ?, ?)";
                PreparedStatement pStmt = conn.prepareStatement(newPropQ);
                pStmt.setInt(1, p_id);
                pStmt.setString(2, street);
                pStmt.setString(3, city);
                pStmt.setString(4, state);
                pStmt.setString(5, zip);
                pStmt.setInt(6, numApartments);
                int rowsChanged = pStmt.executeUpdate();
                if(rowsChanged == 1) {
                    System.out.println("[Update]: Property added successfully!");
                    propAdded = true;
            
                } else {
                    System.out.println("[Error]: Property could not be added.");
                }

            } while (!propAdded);
            
            // for each apartment we insert into the apartment table
            for(int i = 1; i < numApartments; i++) {
                // generate apt_num
                int apt_num = createAptNum(conn, numApartments);
                if(apt_num == 0) {
                    System.out.println("[Error]: Could not generate apartment number.");
                    break;
                }

                // number of beds
                int [] beds = {1, 2, 3};
                int ind = new Random().nextInt(beds.length);
                int numBeds = beds[ind];

                // number of baths
                double [] baths = {1, 1.5, 2, 2.5};
                ind = new Random().nextInt(baths.length);
                double numBaths = baths[ind];

                // sq footage
                int sq_foot = 0;
                if(numBeds == 1 && (numBaths == 1 || numBaths == 1.5 )) {
                    // range 700 - 850
                    sq_foot = new Random().nextInt(150) + 700;

                } else if(numBeds == 2 && (numBaths == 1.5 || numBaths == 2)) {
                    // range 850 - 1100
                    sq_foot = new Random().nextInt(250) + 850;

                } else if(numBeds == 3 && (numBaths == 2 || numBaths == 2.5)) {
                    // range 1100 - 1400
                    sq_foot = new Random().nextInt(300) + 1100;

                } else {
                    // default range 700-950
                    sq_foot = new Random().nextInt(250) + 700;
                }

                String addAptQ = "insert into apartment values (?, ?, ?, ?, ?)";
                PreparedStatement pStmt = conn.prepareStatement(addAptQ);
                pStmt.setInt(1, apt_num);
                pStmt.setInt(2, numBeds);
                pStmt.setDouble(3, numBaths);
                pStmt.setInt(4, sq_foot);
                pStmt.setInt(5, p_id);
                int rowsChanged = pStmt.executeUpdate();

                if(!(rowsChanged == 1)) {
                    System.out.println("[Error]: Apartment could not be added.");
                }
            }
            
        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Database error. Please try again.");
            sqle.printStackTrace();
        }
        catch (InputMismatchException e) {
            System.out.println("[Error]: Invalid input. Please try again.");
            //e.printStackTrace();
        }
        catch (Exception e) {
            System.out.println("[Error]: Invalid input. Please try again.");
            //e.printStackTrace();
        }


    }

    // add a new amenity to an existing property
    public static void addAmenity(Connection conn) {
        Scanner in = new Scanner(System.in);
        try {
            System.out.println("What property would you like to add amenities to?");
            int p_id = validateProperty(conn, Integer.parseInt(in.nextLine()));
            if(p_id == -1) {
                // invalid property id
                System.out.println("[Error]: Invalid property ID. Please try again.");
    
            } else {
                // get current amenities of property selected
                String amenityQ = "select * from amenities where p_id = ?";
                PreparedStatement pStmt1 = conn.prepareStatement(amenityQ);
                pStmt1.setInt(1, p_id);
                ResultSet rs1 = pStmt1.executeQuery();
    
                ArrayList<String> offeredAmenities = new ArrayList<>(); 
                // display results & store in array
                if(rs1.next() == false) {
                    System.out.println("[Error]: This property currently offers no amenities.");
    
                } else {
                    System.out.println("Property " + p_id + " currently offers the following amenities: ");
                    while(rs1.next()) {
                        System.out.println("\t" + rs1.getString(2));
                        // add to arraylist
                        offeredAmenities.add(rs1.getString(2)); 
                    }
                    System.out.println();
                }
                
                // all possible amenities (array of options, into list, then as arraylist)
                String [] totalA = {"Pool", "Gym", "Laundry", "Parking", "Lounge", "Garden"};
                List<String> tempList = Arrays.asList(totalA);
                ArrayList<String> allAmenities = new ArrayList<>(tempList);

                // options for new amenities (remove all offered amenities from the total list)
                for(String amenity : offeredAmenities) {
                    allAmenities.remove(amenity);
                }

                // get option for new amenity addition
                System.out.println("Valid options include: " + allAmenities);
                String newAmenity = in.nextLine();
                if(allAmenities.contains(newAmenity)) {
                    // add amenity to amenities table
                    String addAmenityQ = "insert into amenities values (?, ?, ?)";
                    PreparedStatement pStmt2 = conn.prepareStatement(addAmenityQ);
                    int a_id = createAmenityID(conn);
                    pStmt2.setInt(1, a_id);
                    pStmt2.setString(2, newAmenity);
                    pStmt2.setInt(3, p_id);
                    int rowsChanged = pStmt2.executeUpdate();

                    if(rowsChanged == 1) {
                        System.out.println("[Update]: Amenity added successfully!");
                    } else {
                        System.out.println("[Error]: Amenity could not be added.");
                    }

                } else {
                    System.out.println("[Error]: Invalid amenity. Please try again.");
                }
                
            }

        }
        catch(SQLException sqle) {
            System.out.println("[Error]: Database error. Please try again.");
            sqle.printStackTrace();
        }
        catch(InputMismatchException e) {
            System.out.println("[Error]: Invalid input. Please try again.");
            //e.printStackTrace();
        }
        catch(Exception e) {
            System.out.println("[Error]: Invalid input. Please try again.");
            //e.printStackTrace();
        }
 
    }

    // generate a new amenity ID when managers want to add one
    public static int createAmenityID(Connection conn) {
        int a_id = 0;
        try {
            String query = "select max(a_id) from amenities";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();
            if(rs.next()) {
                a_id = rs.getInt(1) + 1;
            } else {
                a_id = 1;
            }
        }
        catch(SQLException sqle) {
            System.out.println("[Error]: Database error. Please try again.");
            // sqle.printStackTrace();
        }
        return a_id;
    }

    public static int createPropertyID(Connection conn) {
        int p_id = 0;
        try {
            String query = "select max(p_id) from property";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();
            if(rs.next()) {
                p_id = rs.getInt(1) + 1;
            } else {
                p_id = 1;
            }
        }
        catch(SQLException sqle) {
            System.out.println("[Error]: Database error. Please try again.");
            // sqle.printStackTrace();
        }
        return p_id;
    }

    public static int createAptNum(Connection conn, int numApts) {
        int apt_num = 0;
        try {
            String query = "select apt_num from apartment";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();
            ArrayList <String> apartmentNums = new ArrayList<>();
            while(rs.next()) {
                apartmentNums.add(rs.getString(1));
            }
            boolean valid = false;
            while(!valid) {
                apt_num = new Random().nextInt(numApts) + 1;
                if(apartmentNums.contains(Integer.toString(apt_num))) {
                    valid = false;
                } else {
                    valid = true;
                    return apt_num;
                }
            }

        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Database error. Please try again.");
            // sqle.printStackTrace();
        }
        return apt_num;
    }
       

    public static int validateProperty(Connection conn, int p_id) {
        try {
            String propertyQ = "select p_id from property";
            PreparedStatement pStmt = conn.prepareStatement(propertyQ);
            ResultSet rs = pStmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            int prop_id = 0;

            // store all property ids into an arraylist
            ArrayList<String> propertyList = new ArrayList<String>(colCount);
            while(rs.next()) {
                int i = 1;
                while(i <= colCount) {
                    propertyList.add(rs.getString(i++));
                }
            }

            if(propertyList.contains(Integer.toString(p_id))) {
                // valid property ID
                prop_id = p_id;
                return prop_id;

            } else {
                // invalid property ID
                prop_id = -1;
                return prop_id;
            }

        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Database error. Please try again.");
            sqle.printStackTrace();
        }
        catch (Exception e) {
            System.out.println("[Error]: Invalid input. Please try again.");
            //e.printStackTrace();
        }
        return -1;
    }
    
}
