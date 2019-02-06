--this script creates the Stored Procedures for the real estate database

CREATE PROCEDURE usp_CALC_PROP_TAX
@mlsnum varchar(10)
AS
SELECT ROUND((ASSESSMENT*(COUNTY_TAX + MUNICIPALITY_TAX + SCHOOL_DISTRICT_TAX)/1000),0) AS PROPERTY_TAX
FROM PROPERTY, COUNTY, MUNICIPALITY, SCHOOL_DISTRICT
WHERE PROPERTY.COUNTY_ID = COUNTY.COUNTY_ID
AND PROPERTY.MUNICIPALITY_ID = MUNICIPALITY.MUNICIPALITY_ID
AND PROPERTY.SCHOOL_DISTRICT_ID = SCHOOL_DISTRICT.SCHOOL_DISTRICT_ID
AND MLS_NUM = @mlsnum

GO
CREATE PROCEDURE usp_DEL_APPOINTMENT
@appt_id int
AS 
DELETE
FROM APPOINTMENT
WHERE APPT_ID = @appt_id

GO
CREATE PROCEDURE usp_DEL_COUNTY
@county_id varchar(10)
AS 
UPDATE PROPERTY
SET COUNTY_ID = NULL
WHERE COUNTY_ID = @county_id

DELETE
FROM COUNTY
WHERE COUNTY_ID = @county_id

GO
CREATE PROCEDURE usp_DEL_LISTING_AGENT
@agent_id varchar(5)
AS 
UPDATE PROPERTY
SET LISTING_AGENT_ID = NULL
WHERE LISTING_AGENT_ID = @agent_id

DELETE
FROM LISTING_AGENT
WHERE LISTING_AGENT_ID = @agent_id

GO
CREATE PROCEDURE usp_DEL_MUNICIPALITY
@muni_id varchar(10)
AS 
UPDATE
PROPERTY
SET MUNICIPALITY_ID = NULL
WHERE MUNICIPALITY_ID = @muni_id

DELETE
FROM MUNICIPALITY
WHERE MUNICIPALITY_ID = @muni_id

GO
CREATE PROCEDURE usp_DEL_PROPERTY
@mls VARCHAR(10)
AS 
DELETE 
FROM APPOINTMENT
WHERE MLS_NUM = @mls

DELETE
FROM PROPERTY
WHERE MLS_NUM = @mls

GO
CREATE PROCEDURE usp_DEL_SCHOOLDIST
@sd_id varchar(10)
AS 
UPDATE PROPERTY
SET SCHOOL_DISTRICT_ID = NULL
WHERE SCHOOL_DISTRICT_ID = @sd_id

DELETE
FROM SCHOOL_DISTRICT
WHERE SCHOOL_DISTRICT_ID = @sd_id

GO
CREATE PROCEDURE usp_INSERT_APPOINTMENT
@apptdate date,
@appttime time(0),
@mls varchar(10),
@duration int,
@type varchar(45)
AS
INSERT INTO APPOINTMENT
(APPT_DATE, APPT_TIME, MLS_NUM, DURATION, TYPE)
VALUES
(@apptdate, @appttime, @mls, @duration, @type)

GO
CREATE PROCEDURE usp_INSERT_COUNTY
@countyid varchar(10),
@countynm varchar(45), 
@tax decimal(6,4) 
AS
INSERT INTO COUNTY
VALUES
(@countyid, @countynm, @tax)

GO
CREATE PROCEDURE usp_INSERT_LISTING_AGENT
@agent_id varchar(5),
@first varchar(45), 
@last varchar(45), 
@agency varchar(45),
@phone varchar(10),
@email varchar(45)
AS
INSERT INTO LISTING_AGENT
VALUES
(@agent_id, @first, @last, @agency, @phone, @email)

GO
CREATE PROCEDURE usp_INSERT_MUNICIPALITY
@muni_id varchar(10),
@muni_nm varchar(45), 
@tax decimal(6,4) 
AS
INSERT INTO MUNICIPALITY
VALUES
(@muni_id, @muni_nm, @tax)

GO
CREATE PROCEDURE usp_INSERT_PROPERTY
@mls varchar(10),
@lprice decimal(6,0),
@assess decimal(6,0),
@addr varchar(60),
@city varchar(30),
@county varchar(10),
@muni varchar(10),
@school varchar(10),
@bed int,
@bath decimal(2,1),
@sqft int,
@dateonmkt date,
@agent varchar(5)
AS
INSERT INTO PROPERTY
VALUES
(@mls, @lprice, @assess, @addr, @city, @county, @muni, @school,
@bed, @bath, @sqft, @dateonmkt, @agent)

GO
CREATE PROCEDURE usp_INSERT_SCHOOLDIST
@sd_id varchar(10),
@sd_nm varchar(45), 
@tax decimal(6,4) 
AS
INSERT INTO SCHOOL_DISTRICT
VALUES
(@sd_id, @sd_nm, @tax)

GO
CREATE PROCEDURE usp_UPDATE_AGENT_EMAIL
@newemail varchar(45),
@agent_id varchar(5)
AS
UPDATE LISTING_AGENT
SET EMAIL = @newemail
WHERE LISTING_AGENT_ID = @agent_id

GO
CREATE PROCEDURE usp_UPDATE_AGENT_PHONE
@newphone varchar(10),
@agent_id varchar(5)
AS
UPDATE LISTING_AGENT
SET PHONE = @newphone
WHERE LISTING_AGENT_ID = @agent_id

GO
CREATE PROCEDURE usp_UPDATE_APPT_DATE_TIME
@newdate date,
@newtime time(0),
@appt_id int
AS 
UPDATE APPOINTMENT
SET APPT_DATE = @newdate,
APPT_TIME = @newtime
WHERE APPT_ID = @appt_id

GO
CREATE PROCEDURE usp_UPDATE_APPT_TIME
@newtime time(0),
@appt_id int
AS 
UPDATE APPOINTMENT
SET APPT_TIME = @newtime
WHERE APPT_ID = @appt_id

GO
CREATE PROCEDURE usp_UPDATE_COUNTY_TAX
@newtax decimal (6,4),
@countyid varchar(10)
AS 
UPDATE COUNTY
SET COUNTY_TAX = @newtax
WHERE COUNTY_ID = @countyid

GO
CREATE PROCEDURE usp_UPDATE_MUNI_TAX
@newtax decimal (6,4),
@muni_id varchar(10)
AS 
UPDATE MUNICIPALITY
SET MUNICIPALITY_TAX = @newtax
WHERE MUNICIPALITY_ID = @muni_id

GO
CREATE PROCEDURE usp_UPDATE_PRICE
@newprice decimal(6,0),
@mls varchar(10)
AS 
UPDATE PROPERTY
SET LISTED_PRICE = @newprice
WHERE MLS_NUM = @mls

GO
CREATE PROCEDURE usp_UPDATE_PROP_LISTING_AGENT
@newagent varchar(5),
@mls varchar(10)
AS 
UPDATE PROPERTY
SET LISTING_AGENT_ID = @newagent
WHERE MLS_NUM = @mls

GO
CREATE PROCEDURE usp_UPDATE_SCHOOL_TAX
@newtax decimal (6,4),
@sd_id varchar(10)
AS 
UPDATE SCHOOL_DISTRICT
SET SCHOOL_DISTRICT_TAX = @newtax
WHERE SCHOOL_DISTRICT_ID = @sd_id