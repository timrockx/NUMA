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
- changing payment method has to be different than adding a payment method


### Functionality:

#### Tenant Interface
- Login using a tenant_id of any integer (1-8)
- Interface Notes:
  - Every tenant is currently assigned to a lease for an apartment
  - Tenants have the following functionality: 
    - Make payments on their lease (working)
    - Add roommates to their apartment (working)
    - Cancel/move out of their apartment (partially working)
    - View all amenities their apartment/property (working)

#### NUMA Property Manager Interface
- Login using a predesignated mannager id of 1
- Notes:
  - Interface offers the following functionality:
    - View all properties in the area
    - View all apartments and leases for a property
    - View all amenities offered by each property

