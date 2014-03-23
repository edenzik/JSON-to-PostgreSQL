JSON-to-PostgreSQL
==================

NoSQL databases, albit powerful, need to at times communicate or be parsed into SQL databases such as PostgreSQL.

JSON-to-PostgreSQL is a simple Java utility to take JSON files and extract a usable Schema from them. Said Schema can then be used to create SQL INSERT commands from additional JSON files of the same Schema.

This enables quick conversion of a series of JSON files to SQL commands which can then be uploaded to a PostgreSQL server.

Setup is done by a sample JSON file of the desired schema.

This file will create a SQL command for a table consisting of the attributes it contains. 

Each JSON file can then be inserted into the table. 

Each insertion generates a unique SQL command handled by Postgres.

All commands are then saved to an SQL file.

Interaction with JSON-to-PostgreSQL is via terminal:
```
>javac Parser.java
>java Parser
Glue Utility: JSON -> SQL Parser -> PostgreSQL Server
Indicate the location of a JSON file to produce a Schema for an SQL table
>lib/sampleJSON1.json
JSON Loaded. Name of Table:
item_info
Schema produced:
CREATE TABLE item_info(
price varchar(255),
name varchar(255),
id varchar(255),
store varchar(255)
);
To produce SQL Queries from JSON Files, indicate the location of an additional JSON file (files must have matching attributes)
>lib/sampleJSON2.json
INSERT INTO TABLE item_info
VALUES('12.5','A green door','1','home depot')

Log another file, or type Q to quit.
>lib/sampleJSON3.json
INSERT INTO TABLE item_info
VALUES('19','Mik Carton','5','target')

Log another file, or type Q to quit.
Q
Insertion Done.
Queries saved in file "item_info.sql"
Would you like to input this Query to a Postgre Server? (Y/N)
N
```

If a Schema JSON file is not found, a blank one is produced. This is in order to make the utility more flexible once an "append attribute" option is available.

Bugs:

*	Connection to server sometimes fails, reason unknown.
*	Type inference for a string, int, or double for JSON attributes. Default is a varchar of length 255.