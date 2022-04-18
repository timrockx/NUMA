## NUMA CSE 241 Project Interface
- [NUMA CSE 241 Project Interface](#numa-cse-241-project-interface)
  - [Upcoming Changes:](#upcoming-changes)
  - [Functionality:](#functionality)
    - [Tenant Interface](#tenant-interface)
    - [NUMA Property Manager Interface](#numa-property-manager-interface)


&nbsp;

### Upcoming Changes:
- payment history table
- view amenities functionality?
- prospective tenants?


### Functionality:

#### Tenant Interface
- Login using a tenant_id of any integer (1-8)
- Notes:
  - The program will validate that the tenant id is in the database
  - Every tenant is currently assigned to a lease for an apartment
  - Tenants have the following functionality: 
    - Make payments on their lease
    - Add roommates to their apartment
    - Cancel/move out of their apartment
    - View all amenities their apartment/property offers

#### NUMA Property Manager Interface
- Login using a predesignated mannager id of 1
- Notes:
  - Interface offers the following functionality:
    - View all properties in the area
    - View all apartments and leases for a property
    - View all amenities offered by each property

