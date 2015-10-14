/****************************************************************************************************************
* IBM Confidential.   © Copyright IBM Corp. 2004, 2015  All Rights Reserved.
 * The source code for this program is not published or otherwise divested of its trade secrets, 
 * irrespective of what has been deposited with the U.S. Copyright office.
 */
package com.ibm.services.tools.sage.utilities;
import java.sql.*;

/****************************************************************************************************************
 * This class is used as a generic JDBC class for connecting to data sources, disconnecting,
 * querying, retrieving specific database server data, etc... it uses a dynamic class name
 * so it can basically connect to any database server type (DB2, SQL Server, etc...)
 * @author		Jason Wallace/Atlanta/IBM (jwalla@us.ibm.com)  -  IBM Global Services  -  EUS Tools and Technology Team
 * @version 	2015.04.16   
 */
public class AppJDBC {
	 public String ConnectClass;
	 public String ConnectionURL;
	 public String UserID;
	 public String Password;
	 public Connection dbConnection;
	 private Statement dbStatement;
	 private ResultSet dbResultSet;
	 private Statement dbExeStatement;

	 
	 /****************************************************************************************************************
	  * Connects to the SQL data source, such as DB2, SQL Server, or any other by using the connect class, url, user id
	  * and password setup for this class.
	  * @return	boolean 	Returns true if the connection was successful.
	  * @throws	Exception	If an error occurs while processing, throw exception back to the caller to handle.
	  */
	 public boolean connectDataSource() throws Exception{
		//-- Make sure the fields required by this method are available
	 	if (ConnectClass.equalsIgnoreCase("")) {
	 		throw new Exception("Data source connect class is missing."); 		
	 	} 	
	 	try {
	     	//-- Load the JDBC driver 
	     	Class.forName( ConnectClass);
	   	} 
	   	catch (ClassNotFoundException e) {
	   		throw new Exception("Unable to load driver class '" + ConnectClass + "'.");   		
	   	}
	   	//-- Make sure the fields required by this method are available
	   	if (ConnectionURL.equalsIgnoreCase("")) {
	 		throw new Exception("Data source connection URL is missing."); 		
	 	}  
	   	try {
	     	//-- Connect to the database, specifying particular database, username, and password
	     	dbConnection = DriverManager.getConnection(ConnectionURL,UserID,Password);
	    }
	    catch (SQLException se) {       	
	        throw new Exception("Data source connection. SQL Exception: '" + se.getMessage(  ) + "'.");
	    } 
	    //-- Done. Return the results...
	    return true;
	  }
	  
	 
	 
	 /****************************************************************************************************************
	   * Disconnects from the data source.  Uses each sql objects close method to close them off, then makes sure they 
	   * are released by setting each object to null
	   * @return	boolean 	Returns true if the disconnect was successful.
	   * @throws	Exception	If an error occurs while processing, throw exception back to the caller to handle.
	   */
	 public boolean disconnectDataSource () throws Exception {
		 try{
				//-- Cleanup the ResultSet
				if (dbResultSet != null){
					dbResultSet.close();
					dbResultSet = null;				  
				}
				//-- Cleanup the Statement
				if (dbStatement != null){
					dbStatement.close();
					dbStatement = null;				  
				}
				//-- Cleanup the Statement
				if (dbExeStatement != null){
					dbExeStatement.close();
					dbExeStatement = null;				  
				}
				//-- Cleanup the Connection
				if (dbConnection != null){
					dbConnection.close();
					dbConnection = null;				  
				}
				return true;
			} catch (Exception e) {  
				throw e;
			}
	 }
	  
	 
	 
	  /****************************************************************************************************************
		* Sends the passed SQL SELECT statement to the database server to return a result of records mathcing the 
		* SQL query. This is called only after connecting to the database initially.
		* @param	SQL			The SQL used to retrieve the result set from the database connection.
		* @return	ResultSet 	Returns a ResultSet object containing the query results from the database server.
		* @throws	Exception	If an error occurs while processing, throw exception back to the caller to handle.
		*/
	  public ResultSet getResultSet(String SQL, int sqlTimeout) throws Exception{
	  	try{
	  		//-- Create a JDBC statement, execute the passed SQL query, and retrieve the resultset from the database
			if (dbStatement==null){
				dbStatement = dbConnection.createStatement();
		  	  	//-- Set query timeout
		  		dbStatement.setQueryTimeout(sqlTimeout);	
			}
	     	dbResultSet = dbStatement.executeQuery(SQL);
	    } 
	    catch (Exception e) {       	
	        throw e;
	    }
	    //-- Done. Return the results...
	  	return dbResultSet;
	  }
	  
	  
	  
	  /****************************************************************************************************************
		* Returns a specific String from the database server based on the SQL query passed to it.  This can be used to 
		* return a specific value, rather than getting a result set, then retrieving a value from that resultset.  This 
		* does all of that for you. Just pass the SQL SELECT statement and the "ValueField", which is the column that 
		* contains the value you need. This will get the resultset and pull that value out of the top record and return it.
		* @param	SQL			The SQL used to retrieve the result set from the database connection.
		* @param	ValueField	The column from which the String should be retrieved from the resultset.
		* @return	String 		Returns the specified column value from the top record in the resultset.
		* @throws	Exception	If an error occurs while processing, throw exception back to the caller to handle.
		*/	  
	  public String getDatabaseStringValue(String SQL, String ValueField, int sqlTimeout) throws Exception{
	  	//-- Establish a string to hold the results from the database
	  	String Value = "";
	  	try{
	  		//-- Create a JDBC statement, execute the passed SQL query, and retrieve the resultset from the database
	  		dbStatement = dbConnection.createStatement();
	  		//-- Set query timeout
	  		dbStatement.setQueryTimeout(sqlTimeout);
	     	dbResultSet = dbStatement.executeQuery(SQL); 
	     	//-- If record(s) returned from query, get string of the passed 'ValueField' field name of the first record
	     	if (dbResultSet.next()){
	     		Value = dbResultSet.getString(ValueField).trim();	
	     	} 
	    } 
	    catch (Exception e) {    	
	        throw e;
	    }     
	    //-- Done. Return the results...
	  	return Value;
	  }
	  
	  
	  
	  /****************************************************************************************************************
		* Returns a specific int from the database server based on the SQL query passed to it.  This can be used to 
		* return a specific value, rather than getting a result set, then retrieving a value from that resultset.  This 
		* does all of that for you. Just pass the SQL SELECT statement and the "ValueField", which is the column that 
		* contains the int you need. This will get the resultset and pull that value out of the top record and return it.
		* @param	SQL			The SQL used to retrieve the result set from the database connection.
		* @param	ValueField	The column from which the int should be retrieved from the resultset.
		* @return	int 		Returns the specified column int value from the top record in the resultset.
		* @throws	Exception	If an error occurs while processing, throw exception back to the caller to handle.
		*/	  
	  public int getDatabaseIntValue(String SQL, String ValueField, int sqlTimeout) throws Exception{
	  	//-- Establish an int integer to hold the results from the database
	  	int Value = 0;
	  	try{
	  		//-- Create a JDBC statement, execute the passed SQL query, and retrieve the resultset from the database
	  		dbStatement = dbConnection.createStatement(  );
	  		//-- Set query timeout
	  		dbStatement.setQueryTimeout(sqlTimeout);
	     	dbResultSet = dbStatement.executeQuery(SQL);   
	     	//-- If record(s) returned from query, get int of the passed 'ValueField' field name of the first record
	     	if (dbResultSet.next()){
	     		Value = dbResultSet.getInt(ValueField);
	     	} 	
	    } 
	    catch (Exception e) {       	
	        throw e;
	    }  
	    //-- Done. Return the results...
	  	return Value;
	  }
	  
	  
	  
	  /****************************************************************************************************************
		* Executes the passed SQL statement against the database and returns the number of records affected by the SQL. 
		* Can be used to INSERT, UPDATE, or DELETE records.  For instance, you could pass the SQL for deleting a specific 
		* set of records and this method will send the SQL to delete the records and return the number of records that 
		* were actually deleted from the database.
		* @param	SQL			The SQL statement to execute against the database connection.
		* @return	int		 	Returns an int representing the number of records inserted, updated or deleted.
		* @throws	Exception	If an error occurs while processing, throw exception back to the caller to handle.
		*/  
	  public int executeStatement(String SQL, int sqlTimeout) throws Exception{
	  	//-- Establish an int integer to hold the number of database records inserted, updated, or deleted
	  	int recordsUpdated = 0;
	  	try{
	  		//-- Create a JDBC statement, execute passed SQL query, and get # inserted, updated, or deleted
	  		if (dbExeStatement==null){
	  			dbExeStatement = dbConnection.createStatement(  );	  	
	  			//-- Set query timeout
	  			dbExeStatement.setQueryTimeout(sqlTimeout);
	  		}
	  		recordsUpdated = dbExeStatement.executeUpdate(SQL);
	  		dbExeStatement.clearWarnings();
	  		//-- Done. Return the results...
		    return recordsUpdated;	
	    } catch (Exception e) {       	
	    	throw e;	        
	    }
	  }  
	  
	  
	  
	  /****************************************************************************************************************
		 * Cleans up a String in preparation for use in an SQL statement. It basically just trims the 
		 * value and replaces any single quotes with double single quotes so that the database engine can process it.
		 * @param value The String value to be cleaned.
		 * @return String	The cleaned String.
		 * @throws Exception
		 */
		public static String cleanStringForSQL(String value) throws Exception{
			try{
				//-- Replace all single quotes with two single quotes
				value = value.replaceAll("'", "''");	   
				//-- Trim the values to remove extra spaces
				value = value.trim();
				//-- Done
				return value;
			} catch (Exception e){
				throw e;
			}		
		} 
		
		
		
		/****************************************************************************************************************
		 * Formats the value of a specified object so that it can be properly used in an SQL statement. Depending on the data type,
		 * the format is different.  Also replaces the operator used in the SQL statement to handle nulls or the necessary data type.
		 * @param type
		 * @param obj
		 * @param replaceOperator
		 * @return String
		 * @throws Exception
		 */
		public static String prepObjectValueForSQL(String type, Object obj, boolean replaceOperator) throws Exception{
			try{
				String result = "";
				String operator = "";
				String nullOperator = "";
				if (replaceOperator){
					operator = " = ";
					nullOperator = " IS ";
				}				
				if (type.equalsIgnoreCase("string")){
					if ( AppTools.isNullOrEmpty( obj ) ){
						result = nullOperator + "NULL";
					} else {
						result = operator + "'" + cleanStringForSQL((String) obj) + "'";
					}
				} else if (type.equalsIgnoreCase("date")){
					if ( AppTools.isNullOrEmpty( obj ) ){
						result = nullOperator + "NULL";
					} else {
						result = operator + "'" + AppTools.dateStringToDBTimeStamp((String) obj) + "'";
					}
				} else if (type.equalsIgnoreCase("time")){
					if ( AppTools.isNullOrEmpty( obj ) ){
						result = nullOperator + "NULL";
					} else {
						result = operator + "'" + AppTools.formatTimeToStringForDB(AppTools.formatStringToTimeForUI(obj.toString())) + "'";
					}
				} else if (type.equalsIgnoreCase("datetime")){
					if ( AppTools.isNullOrEmpty( obj ) ){
						result = nullOperator + "NULL";
					} else {
						result = operator + "'" + AppTools.dateTimeStringToDBTimeStamp((String) obj) + "'";
					}
				} else if (type.equalsIgnoreCase("integer")){
					if ( AppTools.isNullOrEmpty( obj ) ){
						result = nullOperator + "NULL";
					} else {
						result = operator + obj.toString();
					}
				} else if (type.equalsIgnoreCase("double")){
					if ( AppTools.isNullOrEmpty( obj ) ){
						result = nullOperator + "NULL";
					} else {
						result = operator + obj.toString();
					}
				} else if (type.equalsIgnoreCase("decimal")){
					if ( AppTools.isNullOrEmpty( obj ) ){
						result = nullOperator + "NULL";
					} else {
						result = operator + obj.toString();
					}
				} else if (type.equalsIgnoreCase("sql")){
					if ( AppTools.isNullOrEmpty( obj ) ){
						result = "";
					} else {
						result = cleanStringForSQL((String) obj).toUpperCase();
					}
				} else if (type.equalsIgnoreCase("stringarray")){
					if ( AppTools.isNullOrEmpty( obj ) ){
						result = nullOperator + "NULL";
					} else {
						String[] strList = (String[]) obj;
						if (strList.length==0){
							result = nullOperator + "NULL";
						} else {
							for(int i =0; i < strList.length ; i++) {
								if (result.equalsIgnoreCase("")){
									result = operator + "'" + cleanStringForSQL((String) strList[i]);
								} else {
									result = result + "|" + cleanStringForSQL((String) strList[i]);
								}
							}
							if (!result.equalsIgnoreCase("")){
								result = result + "'";	
							}						
						}					
					}
				} 
				return result;
			} catch (Exception e){
				throw e;
			}		
		}
		
	  	 
}
