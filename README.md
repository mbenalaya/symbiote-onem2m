symbIoTe-onem2m
--------------------

SymbIoTe2oneM2M (symbIoTe oneM2M Extension for cross platform smart residence applications) project objectives are: 

- Make available a complete oneM2M-based IoT platform including both HW & SW. 
- Develop the interworking between oneM2M and symphony IoT platforms following SymbIoTe federation approach. 
- Implement a relevant use case showcasing federation with symphony IoT platform for Smart Residence pilot sites.

Test setups
--------------------

The supported setups are:

- oneM2M paltform : a server which offers a full implementation of oneM2M standard. It supports the different types of interfaces and nodes defined in the standard including Infrastructure Node (IN), Middle Node (MN), Application Service Node (ASN) and Application Dedicated Node (ADN).

- oneM2M Application Dedicated Node : we provide a java application, namely simdev, which plays the role of an ADN. It allows to manage devices by respecting oneM2M standard. For each equipment, we create a resource of type AE. Each resource contains two resources of type container: sensor and actuator. The first container resource makes possible to recover data from the equipment and the second container makes possible to operate it.

- oneM2M CLD components (L1 compliance level) 

- oneM2M Rap plugin. 


Test running
-------------------

- Run symbIoTe CLD components

To configure and start L1 CLD components, please fellow the steps described in the following link: 
```
https://github.com/symbiote-h2020/SymbioteCloud/wiki/SymbIoteCloud-from-docker
```

- Run oneM2M sensinov platform

- Run oneM2M simdev application 
- Publish oneM2M resources on SymbIoTe Core
- Run oneM2M RAP plugin
- Access oneM2M symbIoTe resoures after resources discovery. 


