# Telebrown TCP Server

{Put description here}

##  Requirements
- JDK 11
- NettyIO 4.1.67.Final
- Apache Log2j 2.14.1

## Configuration
The server automatically creates Config,json if does not exists.

**Config_Server.json**

|                |Default Value                  |Desription                   |
|----------------|-------------------------------|-----------------------------|
|databaseIp		 |`127.0.0.1`			         |IP of Database to Connect    |
|databasePort    |`3306`         			     |Port of Database to Connect  |
|databaseUsername|`root`         			     |User of Database to Connect  |
|databasePassword|`root`						 |Pass of Database to Connect  |
|socketIp		 |`127.0.0.1`					 |Socket to Bind to			   |
|socketPort		 |`6100`					 	 |Port to Bind to			   |


> **NOTE**: log4j2_server.xml is required to be in the same directory as the .jar file.
