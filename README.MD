## NUMA Enterprises User Interface <!-- omit in toc -->

##### [Author]: Timothy Lee <!-- omit in toc -->
##### [Last Modified]: 2020-05-06
##### [Description]: User interface for NUMA Enterprises, designed for the final project of CSE 241 - Database Systems.

---

### Table of Contents <!-- omit in toc -->

- [Tenant Interface](#tenant-interface)
- [Property Manager Interface](#property-manager-interface)
- [NUMA Manager Interface](#numa-manager-interface)

---

### Functionality & Specifications:

This program is designed to support a total of 3 user interfaces:
- Tenant Interface
- Property Manager Interface
- NUMA Manager Interface

The program is designed to handle any and all exceptions, along with providing meanful error messages to our users.  

The NUMA Database currently contains a total of 5 Properties, with a range of 8-15 Apartments per Property.

Below will detail the exact functionality of each user interface and how to interact with it.

File Structure: 

    I have already compiled the code in Sunlab, however you can choose to do so again.
    CD into [tyl224] directory, and run [javac *.java]. Then run [mv *.class ../tyl224exec].
    CD into the [tyl224exec] directory, and generate the jar file with [jar cfmv tyl224.jar Manifest.txt Connect.class Manager.class Tenant.class numaManager.class].

    Run project by cd into [tyl224exec] directory, and run [java -jar tyl224.jar].

📦tyl224lee
 ┣ 📂.vscode
 ┃ ┗ 📜settings.json
 ┣ 📂tyl224
 ┃ ┣ 📜Connect.java
 ┃ ┣ 📜Manager.java
 ┃ ┣ 📜Tenant.java
 ┃ ┗ 📜numaManager.java
 ┣ 📂tyl224exec
 ┃ ┣ 📜Connect.class
 ┃ ┣ 📜Manager.class
 ┃ ┣ 📜Manifest.txt
 ┃ ┣ 📜Tenant.class
 ┃ ┣ 📜numaManager.class
 ┃ ┣ 📜ojdbc8.jar
 ┃ ┗ 📜tyl224.jar
 ┣ 📜.DS_Store
 ┣ 📜.Rhistory
 ┣ 📜.gitignore
 ┣ 📜CSE241_ProjectDocs.pdf
 ┗ 📜README.MD

---

## Tenant Interface

    - Login to the Tenant Interface by selecting it on the main menu, then entering a valid Tenant ID.
    - Valid ID's: [1-29 EXCLUDING 21 and 22]. When adding a new tenant, login with 29 + 1 (incrementally increasing from max.)
  
    - Upon a successful login, the user will be able to choose from a list of actions to perform:
        1. Make a payment on rent.
        2. View payment history.
        3. View amenities offered at their property.
        4. Add roommates.
        5. Update personal information.

    - Notes:
      - Tenants [1-5] have all made complete payments on their rent. (0 remaining balance)
      - Tenants [6-10] have made partial payments on their rent.
      - Tenants [10-29, excluding 21 and 22] have not made any payments on rent, but have payment info on record.


##  Property Manager Interface

    - No login is required for the Property Manager Interface. 

    - Upon entering the interface, the user will be able to choose from a list of actions to perform:
        1. View all property listings in the database.
        2. View all apartments available for rent.
        3. Record a visit by a prospective tenant.
        4. Move in a new tenant to an apartment.
        5. Move a tenant out from their apartment.

    - Notes:
        - Tenants can only be moved into available apartments. (Tip: Action 2, then Action 4)
        - Once a tenant is moved out, that apartment is immediately available for rent.
   

## NUMA Manager Interface

    - No login is required for the NUMA Manager Interface.

    - Upon entering the interface, the user will be able to choose from a list of actions to perform:
        1. Add a new property to the database.
        2. Add amenities to an existing property.
        3. Raise rent on a specified apartment.

    - Notes:
        - Adding a new property will also add the specified number of apartments.
        - Apartment specifications are randomized between 1-3 Beds, 1-2.5 Baths, and 700-1400 Sq. Ft.
        - A property can only contain the following amenities: [Pool, Gym, Laundry, Parking, Lounge, Garden]

