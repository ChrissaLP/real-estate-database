Documentation
=============

Database Images
---------------

*Property Tab:*

![Property tab](/Images/property.png)


*Geography Tab:*

![Geography tab](/Images/geography.png)

*Agent Tab:*

![Agent tab](/Images/agent.png)


List of Tables
--------------

The database has six tables:

1.	PROPERTY (*MLS_NUM*, LISTED_PRICE, ASSESSMENT, ADDRESS, CITY, COUNTY_ID, MUNICIPALITY_ID, SCHOOL_DISTRICT_ID, BEDRMS, BATHRMS, SQR_FT, DATE_ON_MKT, LISTING_AGENT_ID)
2.	COUNTY (*COUNTY_ID*, COUNTY_NAME, COUNTY_TAX)
3.	MUNICIPALITY (*MUNICIPALITY_ID*, MUNICIPALITY_NAME, MUNICIPALITY_TAX)
4.	SCHOOL_DISTRICT (*SCHOOL_DISTRICT_ID*, SCHOOL_DISTRICT_NAME, SCHOOL_DISTRICT_TAX)
5.	LISTING_AGENT (*LISTING_AGENT_ID*, FIRSTNAME, LASTNAME, AGENCY, PHONE, EMAIL)
6.	APPOINTMENT (*APPT_ID*, MLS_NUM, APPT_DATE, APPT_TIME, DURATION, TYPE)


In the GUI, the display is divided into 3 tabs:
1. Property (which includes the Property and Appointment tables)
2. Geography (which includes the County, Municipality and School District tables)
3. Agent (for the agent table)
(screenshots of each tab are in the docs folder, with some sample data on display)

Further information on each table is included below.

 

**1.	PROPERTY**

-The PROPERTY table stores important information about each property:

- MLS_NUM (VARCHAR(10)):	The MLS (multiple listing service) number for the property, serves as the primary key for the table. Real estate properties are tracked by MLS number, which is generally 7-10 digits.
- LISTED_PRICE (DECIMAL(6,0)): The listed sale price for the property.
- ASSESSMENT (DECIMAL(6,0)): The most recent tax assessment of the property value.
- ADDRESS	(VARCHAR(60)): Street address.
- CITY	(VARCHAR(30)):	City in the postal address (which may be different from the municipality name).
- COUNTY_ID	(VARCHAR(10)):	County for the property. Foreign key (COUNTY table.)
- MUNICIPALITY_ID	(VARCHAR(10)):	Municipality or township for the property. Foreign key (MUNICIPALITY table.)
- SCHOOL_DISTRICT_ID (VARCHAR(10)):	School district for the property. Foreign key (SCHOOL_DISTRICT table.)
- BEDRMS (INT):	Number of bedrooms.
- BATHRMS	(DECIMAL(2,1)):	Number of bathrooms. A decimal data type to allow for ½ baths. )
- SQR_FT	(INT):	The square feet of the property.
- DATE_ON_MARKET (DATE):	The date the property was entered on the market.
- LISTING_AGENT_ID (VARCHAR(5)):The ID for the listing agent for the property. Foreign Key (LISTING_AGENT table).

**2.	COUNTY**

-This table provides information on the county tax rate.

- COUNTY_ID	(VARCHAR(10)):	An ID to indicate the county, and the primary key for the table.
- COUNTY_NAME	(VARCHAR(45)):	Full county name.
- COUNTY_TAX	(DECIMAL(6,4)):	Tax rate (in mills). (To calculate property tax based on the millage rate, take that rate, multiply it by the taxable value of the property, then divide the result by 1,000.)


**3.	MUNICIPALITY**

- The MUNICIPALITY table provides information on the municipality tax rate.

- MUNICIPALITY_ID	(VARCHAR(10)):	An ID to indicate the municipality, and the primary key for the table.
- MUNICIPALITY_NAME	(VARCHAR(45)):	The full name of the municipality.
- MUNICIPALITY_TAX	(DECIMAL(6,4)):	Tax rate (in mills).


**4.	SCHOOL_DISTRICT**

- The school district table provides information on the school district tax rate.

- SCHOOL_DISTRICT_ID (VARCHAR(10)):	An ID to indicate the school district, and the table’s primary key.
- SCHOOL_DISTRICT_NAME (VARCHAR(45)):	Full school district name.
- SCHOOL_DISTRICT_TAX	(DECIMAL(6,4)):	Tax rate in mills.

 **5.	LISTING_AGENT**

- Each listing agent can represent many properties, so this table has a one-to-many relationship with the main PROPERTY table. This table provides basic information, including contact details, about the listing agent for each property.

- LISTING_AGENT_ID	(VARCHAR(5)):	ID for each listing agent, and the table’s primary key.
- FIRSTNAME	(VARCHAR(45)):	Agent first name.
- LASTNAME	(VARCHAR(45)):	Agent last name.
- AGENCY	(VARCHAR(45)):	Name of the real estate agency
- PHONE	(VARCHAR(10)):	Agent phone number
- EMAIL	(VARCHAR(45)):	Agent email address.

**6.	APPOINTMENT**

- The appointment table holds scheduled appointments.

- APPT_ID (INT):	The APPT_ID field , an INT, is used for the primary key.
- APPT_DATE	(DATE):	Indicates the date of the appointment.
- APPT_TIME	(TIME(0)):	APPT_TIME stored as a time data type. TIME(0) indicates that no fractional seconds will be stored.
- MLS_NUM	(VARCHAR(10)):	The MLS number for the property. Foreign key (PROPERTY table).
- DURATION	(INT):	Indicates the duration of the appointment in minutes. So an appointment lasting one hour would have a DURATION of 60.
-TYPE	(VARCHAR(45)):	Indicates whether it is a scheduled appointment or an open house.


*A note on the source of Data:*

To populate the database, I used publicly available information on different properties from Zillow.com, primarily for properties in Montgomery, Delaware and Chester counties. In addition, for county, municipality, and school district tax rates, I used government resources to determine the appropriate tax rates.

Stored Procedures:
------------------

**Insert Stored Procedures (insert a record into each of the tables):**

1.	usp_INSERT_APPOINTMENT:
* insert a record into APPOINTMENT table
*	user enters information for all fields, except the APPT_ID field which is an “identity” column—the integer value for APPT_ID is automatically generated by SQL Server

2.	usp_INSERT_COUNTY
*	insert a new row into COUNTY table
*	user enters data for all fields

3.	usp_INSERT_LISTING_AGENT
*	insert a row into LISTING_AGENT table
*	user enters information for all fields

4.	usp_INSERT_MUNICIPALITY
*	insert a record into MUNICIPALITY table
*	user enters data for all fields

5.	usp_INSERT_PROPERTY
*	insert a new property record into PROPERTY table
*	user enters data for all fields

6.	usp_INSERT_SCHOOLDIST
*	add a new row to SCHOOL_DISTRICT table
*	user enters information for all fields in SCHOOL_DISTRICT table


**Update Stored Procedures (update records in the tables):**

1.	usp_UPDATE_AGENT_EMAIL
*	update a listing agent’s email address in the LISTING_AGENT table
*	parameters: new email and the listing agent ID

2.	usp_UPDATE_AGENT_PHONE
*	update a listing agent’s phone number in the LISTING_AGENT table
*	parameters: new phone and the listing agent ID

3.	usp_UPDATE_APPT_DATE_TIME
*	update an appointment date and time in the APPOINTMENT table
*	parameters: new date, new time and appointment ID

4.	usp_UPDATE_APPT_TIME
*	update just an appointment time in the APPOINTMENT table
*	parameters: new time and appointment ID

5.	usp_UPDATE_COUNTY_TAX:
*	update a county tax rate in the COUNTY table
*	parameters: new tax rate and county ID

6.	usp_UPDATE_MUNI_TAX
*	update a municipality’s tax rate in the MUNICIPALITY table
*	parameters: new municipality tax rate and municipality ID

7.	usp_UPDATE_PRICE
*	update a property’s listed price in the PROPERTY table
*	parameters: new price and MLS number for the specific property

8.	usp_UPDATE_PROP_LISTING_AGENT
*	update the listing agent for a selected property in the PROPERTY table
*	parameters: ID of the new listing agent and the MLS number for the property

9.	usp_UPDATE_SCHOOL_TAX
*	update the school district tax rate in the SCHOOL_DISTRICT table
*	parameters: new tax rate and the school district ID


**Delete Stored Procedures (delete selected records from the tables):**

1.	usp_DEL_APPOINTMENT
*	delete an appointment from the APPOINTMENT table
*	parameter: appointment ID

2.	usp_DEL_COUNTY
*	delete a county from the COUNTY table
*	the PROPERTY table has a foreign key relationship with the COUNTY table. I decided to “preserve” the data in the PROPERTY table when a COUNTY is deleted, so that the user doesn’t lose a lot of data if a COUNTY is deleted accidentally. If the user deletes a county from the COUNTY table, the properties “in” that county will remain in the PROPERTY table (they will not be deleted as well). Instead, this stored procedure sets the value for the deleted county to null in the PROPERTY table.
*	parameter: county ID

3.	usp_DEL_LISTING_AGENT
*	delete a listing agent from the LISTING_AGENT table
*	since the PROPERTY table is linked with the LISTING_AGENT table (LISTING_AGENT_ID is a foreign key in the PROPERTY table), this stored procedure will set the listing agent field for the deleted agent to null in the PROPERTY table. As a result, if a listing agent is deleted in error, you do not also lose all of the properties in the PROPERTY table.
*	parameter: listing agent ID

4.	usp_DEL_MUNICIPALITY
*	delete a municipality from the MUNICIPALITY table
*	similar to the “delete” stored procedure for the COUNTY table, the field for a deleted municipality is set to null in the PROPERTY table when a municipality is deleted
*	parameter: municipality ID

5.	usp_DEL_PROPERTY
*	delete a property from the PROPERTY table
*	this stored procedure will also delete all appointments associated with that PROPERTY from the APPOINTMENT table
*	parameter: MLS number for the property in question

6.	usp_DEL_SCHOOLDIST
*	delete a school district from the SCHOOL_DISTRICT table
*	similar to the “delete” stored procedure for the COUNTY and MUNICIPALITY tables, the field for a deleted school district is set to null in the PROPERTY table when a school district is deleted
*	parameter: school district ID


**Stored Procedure for a Calculation:**

1.	usp_CALC_PROP_TAX
*	this stored procedure calculates the total annual property tax for a selected property in the PROPERTY table (using an inner join to retrieve data from the COUNTY, MUNICIPALITY, and SCHOOL_DISTRICT tables)
*	property tax is calculated by adding up the respective tax rates in the COUNTY, MUNICIPALITY and SCHOOL_DISTRICT tables, multiplying that value by the assessed value of the property (ASSESSMENT field in the PROPERTY table), and dividing that number by 1000
*	parameter: the MLS number for the property in question
