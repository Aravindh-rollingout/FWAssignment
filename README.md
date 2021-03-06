# FWAssignment
Kindly refer screenshots attached for further clarity.
https://github.com/Aravindh-rollingout/FWAssignment/issues/1

-The project uses json files as the key-value data store; 
CRD operations over the data store can be performed from the console.
No third party data stores have been used.

-Google's Gson library has been used and the dependency jar 'gson-2.8.2.jar' has been added to the repo under Lib\
Kindly add this as a dependency in your environment before executing the code.
 
-The application is tested only in the Windows environment; but is intended to support Mac and Linux also.

Main Execution starts at FreshStore.java.(For test case execution run TestCaseRunner)

1.FreshStore - Main class.

2.FreshStoreUtil - contains utility operations pertaining to the application.

3.FreshClient - Class containing client specific data and operations.

4.FreshUserdata - Class used as format for the 'clients.json' file where all client details are stored.

5.ClientStore - contains implementation of CRD operations over data store.

6.FileOperator - performs file operations for client data.


Problem statement interpretation:

[] Once user logs in using his username. He can give a specific filepath where his data store
 <username>.json will be created or the file will be stored in 
 Default location 'root:\\FreshStoreDefault'
 as '<username>.json'

[] Entry of a key with character length more than 32 or a JSON object of length greater 16 Kilobytes will display an error message.

[] Ttl values are stored along with the values with the key name "ttl". 
If the value is lesser than the current time the original key will not be available for Read and Delete operations.

[] Read will print json in pretty format but are stored as plain json strings.  

Non-functional requirements.

[] Every client creates a separate file name whose path is stored corresponding to their username in 'clients.json'.
 So no two tenants share a data store. 

[] Read and write operations are added with Reentrant Read-Write locks so all reads will wait until writes complete.
 

Sample File path:(Windows)
C:\\mystore\data

Sample json String:
{"name":"Aravindh","age":25,"position":["Founder","CTO","Developer"],"skills":["java","python","Django","kotlin"],"salary":{"2018":140000,"2012":120000,"2010":100000}}

