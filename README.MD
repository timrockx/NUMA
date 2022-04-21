## NUMA CSE 241 Project Interface
- [NUMA CSE 241 Project Interface](#numa-cse-241-project-interface)
  - [Upcoming Changes:](#upcoming-changes)
  - [Functionality:](#functionality)
    - [Tenant Interface](#tenant-interface)
    - [Property Manager Interface](#property-manager-interface)


&nbsp;

### Upcoming Changes:
- changing payment method has to be different than adding a payment method
- move out functionality (on_delete cascade, move for property manager)
- build out numa manager interface


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

####  Property Manager Interface
- ADD LOGIN FUNCTIONALITY
- Property Manager Interface offers the following functionality:
  1. View all properties
  2. View all apartments for rent in the aera
  3. Record a visit by a prospective tenant
  4. Sign a lease and move-in a tenant
  5. [WIP]: Move-out a tenant


