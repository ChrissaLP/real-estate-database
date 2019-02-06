

import java.sql.*;
import javax.swing.table.DefaultTableModel;

/**
 * The DBManager class has methods to connect to a SQL Server database, retrieve table 
 * data from the database, run queries, and execute Stored Procedures from the database. 
 * As it is written, the class assumes that there is already existing tables 
 * and data in the database.
 */
public class DBManager {
    
    // Instance variables
    
    // DB_URL holds the data needed to connect to the database on the server
    private final String DB_URL = "XXX";
    
    // fields for the Connection, Statement, PreparedStatement, CallableStatement, and ResultSet objects 
    private Connection conn = null;                 // Connection object reference variable
    private Statement stmt = null;                  // Statement object reference varaiable, used to run database queries
    private CallableStatement cStmt = null;         // CallableStatement object used to execute Stored Procedures from the database
    private ResultSet rs = null;                    // ResultSet reference variable to hold result sets from queries
    
    /**
     * Constructor - to create a new DBManager object. All of the methods in the DBManager class initialize the instance data,
     * so the constructor is empty for the moment.
     */
    public DBManager()
    {
        
    }
    
    /**
     * The dbConnect method creates a connection to a SQL Server database.
     */
    public void dbConnect()
    {
        try
        {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(DB_URL);
        }
        
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }

    /*  ------------------------------------------
     *  DATABASE UTILITY METHODS
     *  ------------------------------------------
    */  
    
    /**
     * The getTableData returns a DefaultTableModel object that can be used to build a JTable in a GUI program. 
     * @param query SQL query, a string, which defines the data you would like to obtain from the table.
     * @return DefaultTableModel object used to build a JTable in a GUI program.
     */
    public DefaultTableModel getTableData(String query)
    {
        String[][] tableData;           // a 2D array to hold the data in the table
        String[] colNames;              // an array with the column names
        int numRows, numCols;           // variables to hold number of rows and columns in result set
        DefaultTableModel dtm = null;   // reference variable for the DefaultTableModel object
        
        try
        {
            // connect to the database
            dbConnect();
            
            // create Statement object that can scroll backwards/forwards in the database, and is read-only 
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // run the query 
            rs = stmt.executeQuery(query);
            
            // move to last row in result set
            rs.last();
            // store the row number in numRows variable, to indicate total num of rows in result set
            numRows = rs.getRow();
            //go back to the first row in the result set
            rs.first();
            
            // create a ResultSetMetaData object in order to obtain meta-data about the result set
            ResultSetMetaData md = rs.getMetaData();
            // get the number of columns in the result set
            numCols = md.getColumnCount();
            // create a colNames arrray to hold column names
            colNames = new String[numCols];
            
            // populate colNames array with column names from database
            // use i+1 for column label index, since SQL columns are indexed starting at 1
            for (int i = 0; i<numCols; i++)
               
                colNames[i] = md.getColumnLabel(i+1);
            
            // initialize tableData 2d array based on total number of rows and columns in table
            tableData = new String[numRows][numCols];
            
            // use for loops to populate the tableData array with the data from the table
            for (int row = 0; row<numRows; row++)
            {
                for (int col = 0; col < numCols; col++)
                {
                    tableData[row][col] = rs.getString(col+1);
                }
             // move to next item in result set
                rs.next();
            }
            
            // initialize the DefaultTableModel reference variable, passing the 2d array tableData
            // and the colNames array to the constructor
            dtm = new DefaultTableModel(tableData, colNames);
            
            // close database resources
            rs.close();
            stmt.close();
            conn.close();
        }
        
        catch(Exception e)
        {
            e.printStackTrace();
        }
        // finally block to free up resources
        finally
        {
            if(rs != null) try { rs.close(); } catch(Exception e) {}
            if(stmt != null) try { stmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        // return the DefaultTableModel object
        return dtm;
    }
    
    /**
     * The getListTableID method provides a list of the contents of a table's ID column from the database,
     * This method  populates into a String array the contents of the column at index 1, which in this database 
     * is equivalent to the table ID field.
     * @param query the SELECT query needed to return the set of necessary information,
     * for instance, SELECT * FROM property, will give a result set including all of the fields from the property table.
     * @return String array that holds the list of required data
     */
    public String[] getListTableID(String query)
    {
        String[] listID = null;
        try
        {
            dbConnect();
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            rs = stmt.executeQuery(query);
            
            rs.last();
            int numRows = rs.getRow();
            rs.first();
            
            listID = new String[numRows];
            
            for (int i = 0; i<numRows; i++)
            {
                listID[i] = rs.getString(1);
                rs.next();
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(rs != null) try { rs.close(); } catch(Exception e) {}
            if(stmt != null) try { stmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        // return the DefaultTableModel object
        
        return listID;
        
    }
    
    /*  ------------------------------------------
     *  A CALCULATION!
     *  ------------------------------------------
     */
    
    /**
     * The calcPropertyTax method calculates the total annual property tax (county, municipality,
     * and school district) for a specific property.
     * @param mls MLS number of the property
     * @return total annual property tax, as a double
     */
    public double calcPropertyTax (String mls)
    {
        // iniitialize property tax variable at 0
        double propTax = 0.0;
        
        try
        { 
            // connect to database
            dbConnect();
            
            // statement to call the relevant Stored Procedure
            String SQL = "{call usp_CALC_PROP_TAX (?)}";
            
            // initialize Callable Statement object, set parameter, and execute Stored Procedure
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, mls);
            cStmt.execute();
            
            // get the result set from the procedure
            rs = cStmt.getResultSet();
            
            while (rs.next())
            {
                propTax = rs.getDouble(1);
            }
            
            // close database resources
            rs.close();
            cStmt.close();
            conn.close();
            
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(rs != null) try { rs.close(); } catch(Exception e) {}
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
        return propTax;
    }
    
    
    /*  ------------------------------------------
     *  PROPERTY TABLE STORED PROCEDURES
     *  ------------------------------------------ 
    */
    
    /**
     * The callInsertProperty method calls the Stored Procedure "usp_INSERT_PROPERTY"
     * in order to insert a new Property row into the property table
     * @param mls String MLS# of property
     * @param price double for property price
     * @param assessment double to hold assessed price of property
     * @param address String property address
     * @param city String property city
     * @param county String property county
     * @param municipality String property municipality
     * @param sdistrict String property school district
     * @param bedrms number of property bedrooms (int)
     * @param bathrms number of bathrooms (a double to allow for 1/2 baths)
     * @param sqrFt int square feet of property
     * @param date date that the property entered onto the market, java.sql.Date object
     * @param agent String listing agent for the property
     */
    public void callInsertProperty (String mls, double price, double assessment, String address,
            String city, String county, String municipality, String sdistrict, int bedrms, double bathrms,
            int sqrFt, java.sql.Date date, String agent)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // create String used to call the Stored Procedure
            // ? indicates each parameter that needs to be passed to the Stored Procedure
            String SQL = "{call usp_INSERT_PROPERTY (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            
            // use prepareCall method to initialize the Callable Statement object
            // set each of the necessary parameters for the Stored Procedure
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, mls);
            cStmt.setDouble(2, price);
            cStmt.setDouble(3, assessment);
            cStmt.setString(4, address);
            cStmt.setString(5, city);
            cStmt.setString(6,county);
            cStmt.setString(7, municipality);
            cStmt.setString(8, sdistrict);
            cStmt.setInt(9, bedrms);
            cStmt.setDouble(10, bathrms);
            cStmt.setInt(11, sqrFt);
            cStmt.setDate(12, date);
            cStmt.setString(13, agent);
            
            // execute the Stored Procedure
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
        
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }
    
    /**
     * callUpdateAgent method updates the listing agent for a particular property
     * @param agent the agent ID, a String
     * @param mls  the MLS number for the property, a String
     */
    public void callUpdateAgent (String agent, String mls)
    {
        try
        {
            // connect to the database
            dbConnect();
            // create the String to execute the Stored procedure
            String SQL = "{call usp_UPDATE_PROP_LISTING_AGENT (?, ?)}";
            
            // initialize Callable Statement object and set the required parameters
            // for the Stored Procedure
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, agent);
            cStmt.setString(2, mls);
            
            // execute the Stored Procedure
            cStmt.execute();
  
            // close the database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }
    /**
     * callUpdatePrice method updates the listed price for a specific property
     * @param price the new price, a double
     * @param mls the MLS number for the property in question, a String
     */
    public void callUpdatePrice (double price, String mls)
    {
        try
        {
            // connect to the Database
            dbConnect();
            
            // String to hold the call to the Stored Procedure
            String SQL = "{call usp_UPDATE_PRICE (?, ?)}";
            
            // prepare call, set parameters, and execute Stored Procedure
            cStmt = conn.prepareCall(SQL);
            cStmt.setDouble(1, price);
            cStmt.setString(2, mls);
            
            cStmt.execute();
            
            //close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }
    
    /**
     * callDeleteProp method deletes a property from the property table
     * @param mls MLS number of the property to be deleted
     */
    public void callDeleteProp (String mls)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // String to hold the call to the Stored Procedure
            String SQL = "{call usp_DEL_PROPERTY (?)}";
            
            // initialize Callable Statement object, set parameters and execute Stored Procedure
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, mls);
            
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }
    
    /*  ------------------------------------------
     *  APPOINTMENT TABLE STORED PROCEDURES
     *  ------------------------------------------
     */
    
    /**
     * callInsertAppt method calls a SQL Server database Stored Procedure to insert a new
     * appointment into the appointment table
     * @param date the appointment date, a java.sql.Date object
     * @param time the appointment time, a java.sql.Time object
     * @param mls the MLS number for the property to be visited
     * @param duration the duration of the appointment (an int) in minutes
     * @param type the type of appointment (open house, private showing, etc.)
     */
    public void callInsertAppt(java.sql.Date date, java.sql.Time time, String mls, int duration, String type)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // String to call the stored procedure used to insert an appointment
            String SQL = "{call usp_INSERT_APPOINTMENT (?, ?, ?, ?, ?)}";
            
            // initialize the Callable Statement using the prepareCall method
            cStmt = conn.prepareCall(SQL);
            
            // set each of the parameters
            cStmt.setDate(1, date);
            cStmt.setTime(2, time);
            cStmt.setString(3, mls);
            cStmt.setInt(4, duration);
            cStmt.setString(5,type);
            
            //execute the Stored Procedure
            cStmt.execute();
  
            // close database resources
            cStmt.close();
            conn.close();
            
        }
        
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
    }
    
    /**
     * callUpdateApptDateTime method to call Stored Procedure to update an appointment's
     * date and time
     * @param date new date of the appointment, java.sql.Date object
     * @param time new time of the appointment, java.sql.Time object
     * @param id   the id of the appointment, an int
     */
    public void callUpdateApptDateTime (java.sql.Date date, java.sql.Time time, int id)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // String to hold the call to the Stored Procedure
            String SQL = "{call usp_UPDATE_APPT_DATE_TIME (?, ?, ?)}";
            
            // initialize the Callable Statement object, initialize each of the parameters
            cStmt = conn.prepareCall(SQL);
            cStmt.setDate(1, date);
            cStmt.setTime(2, time);
            cStmt.setInt(3, id);
            
            // execute Stored Procedure
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }

    /**
     * callUpdateApptTime method updates just the time of an appointment
     * @param time the new time of the appointment, a java.sql.Time object
     * @param id  the ID of the selected appointment, an int
     */
    public void callUpdateApptTime (java.sql.Time time, int id)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // String to hold the call to the database Stored Procedure
            String SQL = "{call usp_UPDATE_APPT_TIME (?, ?)}";
            
            // initialize the Callable Statement object, set of each of the parameters
            cStmt = conn.prepareCall(SQL);
            cStmt.setTime(1, time);
            cStmt.setInt(2, id);
            
            // execute the Stored Procedure
            cStmt.execute();
  
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }

    /**
     * callDeleteAppt method deletes an appointment from the appointment table
     * @param id id of the appointment, an int
     */
    public void callDeleteAppt (int id)
    {
        try
        {
            //connect to the database
            dbConnect();
            
            // String to store the call to the Stored Procedure
            String SQL = "{call usp_DEL_APPOINTMENT (?)}";
            
            // initialize the Callable Statement object, using prepareCall method, passing the SQL statement
            cStmt = conn.prepareCall(SQL);
            
            // set the one required parameter
            cStmt.setInt(1, id);
            
            // execute Stored Procedure
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }

    
    /*  ------------------------------------------
     *  COUNTY TABLE STORED PROCEDURES
     *  ------------------------------------------
     */
    
    /**
     * callInsertCounty method to execute a Stored Procedure to insert a new county into the county table
     * @param id the county ID (a String)
     * @param name the county name
     * @param tax the county's property tax rate 
     */
    public void callInsertCounty (String id, String name, double tax)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // String to hold the call to the Stored Procedure
            String SQL = "{call usp_INSERT_COUNTY (?, ?, ?)}";
            
            // prepareCall, set parameters and execute the Stored Procedure
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, id);
            cStmt.setString(2, name);
            cStmt.setDouble(3, tax);
            
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }
    
    
    /**
     * callUpdateCountyTax method updates the tax rate for a selected county, 
     * using a SQL Server Stored Procedure
     * @param newtax the new tax rate for the county, a double
     * @param id the county ID, a String
     */
    public void callUpdateCountyTax (double newtax, String id)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // declare and initialize String to call call the Stored Procedure
            String SQL = "{call usp_UPDATE_COUNTY_TAX (?, ?)}";
            
            // initialize Callable Statement object and set parameters
            cStmt = conn.prepareCall(SQL);
            cStmt.setDouble(1, newtax);
            cStmt.setString(2, id);
            
            // execute the Stored Procedure
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }

    /**
     * callDeleteCounty method deletes a county from the county table in the database
     * @param id the county ID, a String
     */
    public void callDeleteCounty (String id)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // String to hold the call to the Stored Procedure
            String SQL = "{call usp_DEL_COUNTY (?)}";
            
            // prepare call, set one needed parameter
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, id);
            
            // execute the Stored Procedure            
            cStmt.execute();
  
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }

    /*  ------------------------------------------
     *  MUNICIPALITY TABLE STORED PROCEDURES
     *  ------------------------------------------
     */ 
    
    /**
     * callInsertMuni method uses a Stored Procedure to insert a new municipality
     * into the municipality table in a SQL Server database
     * @param id municipality ID, a String
     * @param name municipality name, a String
     * @param tax municipality property tax rate, a double
     */
    public void callInsertMuni (String id, String name, double tax)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // create String to hold a SQL statement that calls the relevant Stored Procedure
            String SQL = "{call usp_INSERT_MUNICIPALITY (?, ?, ?)}";
            
            // initialize a Callable Statement object, passing the SQL statement to the prepareCall method
            cStmt = conn.prepareCall(SQL);
            
            // set parameters for the Stored Procedure and execute the Stored Procedure
            cStmt.setString(1, id);
            cStmt.setString(2, name);
            cStmt.setDouble(3, tax);
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }

    /**
     * callUpdateMuniTax method updates a municipality's property tax rate
     * @param newtax for the new municipality tax rate, a double
     * @param id the relevant municipality ID, a String
     */
    
    public void callUpdateMuniTax (double newtax, String id)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // SQL statement to execute the relevant Stored Procedure
            String SQL = "{call usp_UPDATE_MUNI_TAX (?, ?)}";
            
            // prepareCall, set parameters and execute Stored Procedure
            cStmt = conn.prepareCall(SQL);
            cStmt.setDouble(1, newtax);
            cStmt.setString(2, id);
            cStmt.execute();
  
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }
    /**
     * callDeleteMuni method uses a Stored Procedure to delete a municipality from the municipality table
     * @param id municipality ID, a String
     */
    public void callDeleteMuni (String id)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // SQL statement to execute the Stored Procedure
            String SQL = "{call usp_DEL_MUNICIPALITY (?)}";
            
            // initialize Callable Statement Object, set parameters, and execute
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, id);
            cStmt.execute();
  
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }

    /*  ------------------------------------------
     *  SCHOOL DISTRICT TABLE STORED PROCEDURES
     *  ------------------------------------------
     */
    
    /**
     * callInsertSDistrict method uses a Stored Procedure to insert a new school district
     * into the school_district table
     * @param id school district ID, a String
     * @param name school district name, a String
     * @param tax school district property tax rate, a double
     */
    public void callInsertSDistrict (String id, String name, double tax)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // SQL statement to execute the Stored Procedure
            String SQL = "{call usp_INSERT_SCHOOLDIST (?, ?, ?)}";
            
            // initialize Callable Statement object, set parameters and execute Stored Procedure
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, id);
            cStmt.setString(2, name);
            cStmt.setDouble(3, tax);
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }
    /**
     * callUpdateSDistrictTax method updates a school district's tax rate
     * @param tax the new tax rate, a double
     * @param id, the school district ID, a String
     */
    public void callUpdateSDistrictTax (double tax, String id)
    {
        try
        {
            // connect to database
            dbConnect();
            
            // SQL statement to call Stored Procedure
            String SQL = "{call usp_UPDATE_SCHOOL_TAX (?, ?)}";
            
            // initialize Callable Statement object, set parameters and execute
            cStmt = conn.prepareCall(SQL);
            cStmt.setDouble(1, tax);
            cStmt.setString(2, id);
            
            cStmt.execute();
  
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }

    /**
     * callDeleteSDistrict method to delete a school district from the school_district table
     * @param id school district ID, a String
     */
    public void callDeleteSDistrict (String id)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // SQL statement to execute the relevant Stored Procedure
            String SQL = "{call usp_DEL_SCHOOLDIST (?)}";
            
            // prepareCall, set parameter and execute Stored Procedure
            cStmt = conn.prepareCall(SQL);
            
            cStmt.setString(1, id);
            
            cStmt.execute();
  
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }

    /*  ------------------------------------------
     *  LISTING AGENT TABLE STORED PROCEDURES
     *  ------------------------------------------
     */
    
    /**
     * callInsertAgent method inserts a new agent into the listing_agent table in a
     * SQL Server database
     * @param id ID for the listing agent, a String
     * @param first first name of the listing agent
     * @param last last name of the listing agent
     * @param agency agency of the agent
     * @param phone agent phone number, a String
     * @param email agent email, a String
     */
    public void callInsertAgent (String id, String first, String last, String agency, String phone, String email)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // SQL statement to execute the relevant Stored Procedure
            String SQL = "{call usp_INSERT_LISTING_AGENT (?, ?, ?, ?, ?, ?)}";
            
            // initialize Callable Statement object, set parameters, and execute Stored Procedure
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, id);
            cStmt.setString(2, first);
            cStmt.setString(3, last);
            cStmt.setString(4, agency);
            cStmt.setString(5, phone);
            cStmt.setString(6, email);
            
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
         // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }

    /**
     * callUpdateAgentPhone to update an agent's phone number
     * @param phone the new phone number, a String
     * @param id the agent ID, a String
     */
    public void callUpdateAgentPhone (String phone, String id)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // SQL statement to execute the Stored Procedure
            String SQL = "{call usp_UPDATE_AGENT_PHONE (?, ?)}";
            
            // prepare call, set parameters, and execute Stored Procedure
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, phone);
            cStmt.setString(2, id);
            
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
         // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }
    
    /**
     * callUpdateAgentEmail method updates an agent's email address
     * @param email the new email address
     * @param id agent ID, a String
     */
    public void callUpdateAgentEmail (String email, String id)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // statement to call the Stored Procedure
            String SQL = "{call usp_UPDATE_AGENT_EMAIL (?, ?)}";
            
            //  initialize Callable Statement object, set parameters and execute Stored Procedure
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, email);
            cStmt.setString(2, id);
           
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
         // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }
    
    /**
     * callDeleteAgent deletes an agent from the listing_agent table
     * @param id the ID of the agent in question
     */
    public void callDeleteAgent (String id)
    {
        try
        {
            // connect to the database
            dbConnect();
            
            // statement to execute the relevant Stored Procedure
            String SQL = "{call usp_DEL_LISTING_AGENT (?)}";
            
            // prepare call, set parameter and execute
            cStmt = conn.prepareCall(SQL);
            cStmt.setString(1, id);
            
            cStmt.execute();
            
            // close database resources
            cStmt.close();
            conn.close();
        }
    
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
         // finally block to free up resources
        finally
        {
            if(cStmt != null) try { cStmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
        
    }

}

