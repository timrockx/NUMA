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
                    System.out.println("\t1. Add a new property.");
                    System.out.println("\t2. Add new amenities to an exisitng property.");
                    System.out.println("\t3. Raise rent on an apartment's lease.");
                    System.out.println("\t0. Quit Interface.");
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

                        case 3: // increase rent on apartment
                            raiseRent(conn);
                            break;

                        default:
                            System.out.println("Please select a valid option: ");
                            System.out.println("\t1. Add a new property.");
                            System.out.println("\t2. Add new amenities to a property.");
                            System.out.println("\t3. Raise rent on an apartment's lease.");
                            System.out.println("\t0. Quit Interface.");
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


    // add a new property to the database
    public static void addProperty(Connection conn) {
        Scanner in = new Scanner(System.in);
        try {
            boolean propAdded = false;
            String street, city, state, zip;
            int numApartments, p_id;
            int addedApartments = 0;
            int addedLeases = 0;
            do {
                System.out.println("------Add a new property------");
                // get basic info to add to propety table
                System.out.println("Please enter the following information: ");

                boolean validSt = false;
                do {
                    System.out.println("Street: ");
                    street = in.nextLine();
                    if(street.length() > 50) {
                        System.out.println("[Error]: Street name is too long. Please try again.");
                    }
                    else {
                        validSt = true;
                    }
                } while(!validSt);
                
                boolean validCity = false;
                do {
                    System.out.println("City: ");
                    city = in.nextLine();
                    if(city.length() > 30) {
                        System.out.println("[Error]: City name is too long. Please try again.");
                    }
                    else {
                        validCity = true;
                    }
                } while(!validCity);
                
                boolean validState = false;
                do {
                    System.out.println("State: ");
                    state = in.nextLine();
                    if(!(state.length() == 2)) {
                        System.out.println("[Error]: State name is too long. Please try again.");
                    }
                    else {
                        validState = true;
                    }
                } while(!validState);
                
                boolean validZip = false;
                do {
                    System.out.println("Zip Code: ");
                    zip = in.nextLine();
                    if(!(zip.length() == 5)) {
                        System.out.println("[Error]: Zip code is too long. Please try again.");
                    }
                    else {
                        validZip = true;
                    }
                } while(!validZip);
                
                boolean validNum = false;
                do {
                    System.out.println("How many apartments do you want in this property? (Limit 50) Since this is a new development property.");
                    numApartments = Integer.parseInt(in.nextLine());
                    if(numApartments > 50) {
                        System.out.println("[Error]: Number of apartments cannot exceed 50.");
                    } else {
                        validNum = true;
                    }
                } while(!validNum);
                
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
                    break;
            
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
                } else {
                    // System.out.println("[Update]: Apartment added successfully!");
                    addedApartments++;
                }

                // add corresponding lease for each apartment
                int [] durationChoices = {12, 24, 36};
                int duration = durationChoices[new Random().nextInt(durationChoices.length)];

                int monthly_price;
                if(sq_foot <= 900) {
                    // between 3k to 4k
                    monthly_price = new Random().nextInt(1000) + 3000;
                } else if(sq_foot > 900 && sq_foot <= 1150) {
                    // bewteen 4k to 5k
                    monthly_price = new Random().nextInt(1000) + 4000;
                } else if(sq_foot > 1150 && sq_foot <= 1300) {
                    // between 5k to 7.5k
                    monthly_price = new Random().nextInt(2500) + 5000;
                } else if(sq_foot > 1300 ) {
                    // between 7.5k to 10k
                    monthly_price = new Random().nextInt(2500) + 7500;
                } else {
                    // default is 5k a month
                    monthly_price = new Random().nextInt(1000) + 3000;
                }

                // insert into lease table
                String leaseQ = "insert into lease values (?, ?, ?)";
                PreparedStatement pStmt2 = conn.prepareStatement(leaseQ);
                pStmt2.setInt(1, apt_num);
                pStmt2.setInt(2, monthly_price);
                pStmt2.setInt(3, duration);

                int rowsChanged2 = pStmt2.executeUpdate();
                if(rowsChanged2 == 1) {
                    // System.out.println("[Update]: Lease added successfully!");
                    addedLeases++;
                } else {
                    System.out.println("[Error]: Lease for Apartment " + apt_num + " could not be added.");
                }
            }

            if((addedApartments == numApartments) && (addedLeases == addedApartments)) {
                System.out.println("[Update]: All apartments were added successfully!");
            }
            
        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Database error. Please try again.");
            // sqle.printStackTrace();
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
                System.out.println("[Error]: Invalid property ID. Please try again.\n");
    
            } else {
                // get current amenities of property selected
                String amenityQ = "select * from amenities where p_id = ?";
                PreparedStatement pStmt1 = conn.prepareStatement(amenityQ);
                pStmt1.setInt(1, p_id);
                ResultSet rs1 = pStmt1.executeQuery();
    
                ArrayList<String> offeredAmenities = new ArrayList<>(); 
                // display results & store in array
                if(rs1.next() == false) {
                    System.out.println("[Error]: This property currently offers no amenities.\n");
    
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
                System.out.println("Valid options include: " + allAmenities + ". Enter 0 to cancel this transaction.");
                String newAmenity = in.nextLine();

                if(newAmenity.equals("0")) {
                    System.out.println("[Cancelled]: Transaction cancelled.\n");
                    return;
                }

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
                        System.out.println("[Update]: Amenity added successfully!\n");
                    } else {
                        System.out.println("[Error]: Amenity could not be added.\n");
                    }

                } else {
                    System.out.println("[Error]: Invalid amenity. Please try again.\n");
                }
                
            }

        }
        catch(SQLException sqle) {
            System.out.println("[Error]: Database error. Please try again.");
            // sqle.printStackTrace();
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

    // raise rent on an apartment
    public static void raiseRent(Connection conn) {
        Scanner in = new Scanner(System.in);
        boolean rentRaised = false;

        try { 
            do {
                System.out.println("Enter the apartment number of the lease you want to increase rent on: ");
                int apt_num = Integer.parseInt(in.nextLine());

                // get current rent $
                String currentRentQ = "select monthly_price from lease where apt_num = ?";
                PreparedStatement pStmt1 = conn.prepareStatement(currentRentQ);
                pStmt1.setInt(1, apt_num);
                ResultSet rs1 = pStmt1.executeQuery();
                double currentRent = 0.0;

                if(rs1.next() == false) {
                    System.out.println("[Error]: This apartment is not currently leased.");
                } else {
                    currentRent = rs1.getDouble(1);   
                }
                System.out.println("Apartment " + apt_num + " is currently rented for $" + currentRent + " per month.\n");

                // get new rent $
                System.out.println("What would you like the new monthly rate to be? (0 to quit) ");
                double newRent = Double.parseDouble(in.nextLine());

                // 0 is the exit code
                if(newRent == 0) {
                    System.out.println("Quitting... Monthly rate was not changed.\n");
                    rentRaised = true;
                    break;
                }

                // new rent must be greater than current
                if(newRent <= currentRent) {
                    System.out.println("[Error]: New monthly rate must be greater than the current rate.\n");
                } else {
                    // update rent
                    String updateRentQ = "update lease set monthly_price = ? where apt_num = ?";
                    PreparedStatement pStmt2 = conn.prepareStatement(updateRentQ);
                    pStmt2.setDouble(1, newRent);
                    pStmt2.setInt(2, apt_num);
                    int rowsChanged = pStmt2.executeUpdate();

                    if(rowsChanged == 1) {
                        System.out.println("[Update]: Rent for apartment " + apt_num + " updated successfully!\n");
                        rentRaised = true;
                    } else {
                        System.out.println("[Error]: Rent could not be updated. Please try again.\n");
                    }
                }

            } while(!rentRaised);

        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Database error. Please try again.");
            // sqle.printStackTrace();
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



    // generate a new amenity ID when adding new amenity
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

    // generate new property id when inserting
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

    // generate new apartment num when adding a new apartment
    public static int createAptNum(Connection conn, int numApts) {
        int apt_num = 0;
        try {
            String query = "select max(apt_num) from apartment";
            PreparedStatement pStmt = conn.prepareStatement(query);
            ResultSet rs = pStmt.executeQuery();

            // get the max apt_num, then add one during each iteration
            if(rs.next()) {
                apt_num = rs.getInt(1);
            } 
            apt_num++;
        }
        catch (SQLException sqle) {
            System.out.println("[Error]: Database error. Please try again.");
            // sqle.printStackTrace();
        }
        return apt_num;
    }
       

    // check property id is valid when prompted
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

