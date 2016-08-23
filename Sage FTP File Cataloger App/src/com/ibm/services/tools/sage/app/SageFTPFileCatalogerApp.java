/**
 * 
 */
package com.ibm.services.tools.sage.app;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.net.URL;
import java.sql.*;
import javax.sql.rowset.*;
import javax.sql.rowset.spi.*;
import com.sun.rowset.CachedRowSetImpl;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import com.ibm.services.tools.sage.utilities.*;
import org.apache.commons.io.*;
import java.text.SimpleDateFormat;
/****************************************************************************************
 * This class is the primary application class used for the backend processing for the "Sage FTP Monitoring" application.
 * The SageFTPFileCatalogerApp is the first of the 2 components that make up the Sage FTP Monitoring application.
 * The 2nd component called Sage FTP Monitor App (which runs in its own JRE), 
 * scans the cataloged file data and generates events based on defined business rukes.
 * It is used to run the full application processing. It utilizes a ".properties" file for instructions, JDBC database 
 * interaction for database record selects/inserts/updates/deletes, and application logging, including logging to a text file. 
 * Specific parameters must be passed when launching the application.  The parameters are as follows:
 *  1) Properties File Name (required):  The name of the properties file containing the application instructions, such as "config.properties".
 * 		   
 * @author		 @author		Yogi Golle/Tampa/Contr/IBM (yogigol@us.ibm.com) - IBM GTS Service Delivery - MWSD Tools and Technology Team
 * @version 	2015.04.24   
 */

public class SageFTPFileCatalogerApp {

	/****************************************************************************************
	 * The main application Class SageFTPFileCatalogerApp is the Class that coordinates the flow of the application
	 * The following methods and properties and their purpose are described here:
	 * *****METHODS*****
	 * runApp -- the Main method that directs the flow of the application
	 * initializeJDBCConnection -- 
	 * insertNewActivityRecordIntoDB --
	 * insertNewFileDataRecordIntoDB --
	 * postAlert --
	 * scanDBRecords --
	 * scanFileSystem --
	 * scanFileSystem (status)--
	 * selectFTPFileDataFromDB --
	 * setupAndVerifyAppProperties --
	 * updateFileDataRecordInDB --
	 * updateFileDataRecordInDB (status change) --
	 * getLocalFileMetaData --
	 * *****PROPERTIES*****
	 * fName (ArrayList) --
	 * fType (ArrayList) --
	 * fSize (ArrayList) --
	 * fSizeUnit (ArrayList) --
	 * fCDate (ArrayList) --
	 * fMod (ArrayList) --
	 * fPath (ArrayList) --
	 * fHostName (ArrayList) --
	 * fPermissions (ArrayList) --
	 * fOwners (ArrayList) --
	 * fHeader (ArrayList) --
	 */
	
	// -- ArrayList of file attributes
	public ArrayList<String> 	fName 		= new ArrayList<String>(); // File Names
	public ArrayList<String> 	fType 		= new ArrayList<String>(); // File Types
	public ArrayList<Long> 		fSize 		= new ArrayList<Long>(); // File Sizes
	public ArrayList<String> 	fSizeUnit 	= new ArrayList<String>(); // File Size Units
	public ArrayList<String> 	fCDate 		= new ArrayList<String>(); // File Creation Dates
	public ArrayList<String>	fMod 		= new ArrayList<String>(); // Last Mod Dates
	public ArrayList<String>	fADate 		= new ArrayList<String>(); // Last Access Dates
	public ArrayList<String> 	fPath		= new ArrayList<String>(); // Directory Paths
	public ArrayList<String> 	fHostName	= new ArrayList<String>(); // Hostnames
	public ArrayList<String> 	fPermissions= new ArrayList<String>(); // File Permissions
	public ArrayList<String> 	fOwners		= new ArrayList<String>(); // File Owners
	public ArrayList<String> 	fHeaders	= new ArrayList<String>(); // File Headers
	
	// -- Array List of Cached DB column values and indexes
	ArrayList<String> 	fileKey_db 			= new ArrayList<String>(); // File Names
	ArrayList<String> 	fileDateModified_db = new ArrayList<String>(); // last modification date of files
	ArrayList<String> 	fileId_db 			= new ArrayList<String>(); // File ID
	ArrayList<Integer> 	fileRowId_db 		= new ArrayList<Integer>(); // Record Number
	
	//-- File DateTime Format
	String fdtFormat;
	//-- Cached DB Rowset Object 1
	CachedRowSet rs1;
	//-- Cached DB Rowset set Object 2
	CachedRowSet rs2;
	//-- Record counter for number of records nserted into the FTP File Activity Table
	long fa = 0;
	
	public void runApp(String propertiesFileName) throws Exception{
		
		SageFTPFileCatalogerAppProperties appProps = new SageFTPFileCatalogerAppProperties();
		AppJDBC jdbcDataSource = new AppJDBC();
		
		String processID;	
		Long lastModDir = null;
		String dirName_root = null;
		
		try {   
			
			//-- APPLICATION PARAMETER VALIDATION
			//-- ================================
			//-- Verify the passed parameters for proper data type, values, etc..
			this.verifyPassedParameters(propertiesFileName);						
			
			// -- APPLICATION ID INITIALIZATION
			//--  =============================
			//-- Setup variables that can be used throughout processing
			processID = AppTools.uniqueID();			
			
			//-- WRITE TO LOG TEXT VARIABLE - STARTING APPLICATION
			//-- =================================================
			//-- Keep up with text printed out before log started (then add back to the text log later)
			String preLogText = AppLog.logActivity(appProps,"Starting " + 
			AppConstant.APPLICATION_NAME + ". Java App Runtime ID=" + 
					processID,true,true) + System.getProperty("line.separator");	
			
			//-- WRITE TO PRELOG TEXT VARIABLE
			//-- =============================
			//-- Verify and setup the application properties from the configuration file passed in command line
			preLogText = preLogText + AppLog.logActivity(appProps,"Verifying application properties in '" + propertiesFileName + "' and any linked files...",true,true);
			appProps = this.setupAndVerifyAppProperties(appProps, propertiesFileName);
		
			//-- Now we have the log file properties, so skip 2 lines for the new entry, then log the PreLogText from above to the log file
			AppLog.logActivity(appProps,System.getProperty("line.separator")+System.getProperty("line.separator")+preLogText,false,false);			

			//-- SETUP THE JDBC DATABASE CONNECTION AND CONNECT TO DATASOURCE
			//-- ============================================================
			AppLog.logActivity(appProps,"Connecting to JDBC data source: class=" + appProps.getDbConnectClass() + 
					", url=" + appProps.getDbConnectionURL() + ", userid=" + appProps.getDbUserID(), true, true);
			String jdbcDatabaseDBInfo = this.initializeJDBCConnection(jdbcDataSource, appProps);
			AppLog.logActivity(appProps,"Connected to JDBC data source: " + jdbcDatabaseDBInfo, true, true);
			
			//-- GET A HANDLE TO THE FILE SYSTEM DIRECTORY 
			//-- ========================================
			
			/*--This line has been used for testing reading of file meta data from the local file system 
			(can be either Windows or Linux since file system management is managed below the Application Layer)
			Production configuration will use the 'getFileDirectory' method to retrieve a handle to the root file directory
			*/
			//-- File System Location will either be 'local' or 'sftpRemote'
			String fsType = appProps.getFileSystemLocation();
			
			//-- Initialize the file datetime format
			if (fsType.equalsIgnoreCase("LOCAL")) {
				fdtFormat = AppConstant.FILESDATETIMEFORMAT;
			}
			else if (fsType.equalsIgnoreCase("SFTPREMOTE")) {
				fdtFormat = AppConstant.SFTP_FILESDATETIMEFORMAT;
			}
			else {
				fdtFormat = AppConstant.FILESDATETIMEFORMAT; //-- default to local file datetime format
			}

			//-- Initialize variables
			//-- filesystemMsg is used to report filesystem exception messages from the respective file system
			String fileSystemMsg = "";
			//-- Location of the root directory of the file system
			String directoryLocation = null;
			
			//-- read the directoryLocation for the local file system
			if ( fsType.equalsIgnoreCase("local")) {
				//-- Initialize the file directory object
				File dir = new File("");
				//-- Create a file directory object from the file directory path information in the properties file
				dir = new File(appProps.getFileDirectory());
				if (!dir.exists()) {
					//-- file directory was not found. Create an exception message
					fileSystemMsg = "Could not connect to starting directory: " + appProps.getFileDirectory();
				} else {
					//-- read the full directory path into a local variable
					AppLog.logActivity(appProps,"Directory name: "+dir.getName()+". Full Path: "+FilenameUtils.getFullPath(dir.getName()), true, true);
					directoryLocation = dir.getPath();
					lastModDir = dir.lastModified();
					dirName_root = dir.getName();
					getLocalFileMetaData(appProps,dir);
				}
					
			} else if (fsType.equalsIgnoreCase("sftpRemote"))  {
				//-- The file system is located at an sFTP remote location
				
				directoryLocation 	= appProps.getFileDirectory();
				SageFTPFileCatalogerAppFTPClient ftpc = new SageFTPFileCatalogerAppFTPClient(); 
				ftpc.configureFTPConnection(appProps);
				dirName_root = "";
				dirName_root = ftpc.connectSFTPServer(appProps);
				if (!dirName_root.equals("")) {
					AppLog.logActivity(appProps,"Connected to Server via SSH/SFTP: " + appProps.getFtpServerName(), true, true);	
					AppLog.logActivity(appProps,"Caching File Meta Data in root directory: " + appProps.getFileDirectory(), true, true);
					ftpc.getFileMetaData(appProps,appProps.getFileDirectory());
					AppLog.logActivity(appProps,"File Meta Data has been cached " + ftpc.fn.size() + " File meta data records have been cached", true, true);
					// -- copy the File Meta Data from the set of array Lists in ftpc object to the array List in the current object (this)
					this.fName = ftpc.fn;
					this.fType = ftpc.ft;
					this.fSize = ftpc.fs;
					this.fSizeUnit 	= ftpc.fsu;
					this.fCDate = ftpc.fm; // -- File Creation Date is set to the same value as last mod date, since this API does not support file creation date.
					this.fMod 	= ftpc.fm;
					this.fADate = ftpc.fa;
					this.fPath  = ftpc.fd;
					this.fHostName	= ftpc.fh;
					this.fPermissions = ftpc.fp;
					this.fOwners	= ftpc.fo;
					this.fHeaders	= ftpc.fhd;
							
					directoryLocation = ftpc.sFTPDirectory;
					lastModDir = null;
					dirName_root = directoryLocation;
				}
				else {
					fileSystemMsg = "Connection to host failed using connection properties \n Server: " + appProps.getFtpServerName()  
							+"\nPort: " + appProps.getFtpServerPort() 
							+"\nUser ID: " + appProps.getFtpUserId()  
							+"\nHost Directory"  + appProps.getFileDirectory();
				}
				
				lastModDir 			= null;
				
			} else {
				throw new Exception("Invalid file system location" + fsType + ". Valid values are 'local' and 'sftpRemote'");
			}
			
			//-- Read the file name that will contain any failure alerts to be processed by the FTP Monitor App notification component
			String alertFileName = appProps.getAlertFileName();
			//-- 
			if (!fileSystemMsg.equals("")) {
				// -- Directory does not exist or file system could not be reached.
				// -- post a message to the Alert File Name for use in inter-app communication
				postAlert(alertFileName,fileSystemMsg);
				throw new Exception(fileSystemMsg);
			} else {
				AppLog.logActivity(appProps,"Starting directory name = " + directoryLocation, true, true);
				// --- delete the file if it exists, since no alerts were generated
				Path fileToDelete = Paths.get(alertFileName);
				if (Files.deleteIfExists(fileToDelete)) {
					AppLog.logActivity(appProps,"Alert File: "+alertFileName+" was successfully deleted", true, true);;
				} else {
					AppLog.logActivity(appProps,"Alert File: "+alertFileName+" was not deleted because it does not exist.", true, true);;
				}
			}
			
			/*
			 * DEVELOPER NOTE:
			 * Consider collecting all the file data in an Object List before attempting to
			 * query the database and writing updates to the  table(s)
			 * This should be a performance advantage since all the file meta data is then kept in memory
			 * and will avoid a read from the file system before advancing to the next query each time.
			 */
			
			//-- Establish a DB record cache for the FTP File Catalog table for processing locally in memory
			//-- ===========================================================================================
			
			jdbcDataSource.dbConnection.setAutoCommit(false);  // Need to disable auto-commit for CachedRowSet
			rs1 = null;
			ResultSet rsLocal = jdbcDataSource.getResultSet(appProps.getSqlFetchAllRows(), appProps.getDbSQLTimeout()); //--create a new resulset from the SQL query
		    rs1 = new CachedRowSetImpl(); //-- instantiate a new cached rowset implementation
		    rs1.populate(rsLocal); //-- populate the CachedRowset from the query resultset
		    rs1.setTableName(appProps.getFileCatalogTableName()); // Set the table name that is to be cached
		    
			//-- Establish a DB record cache for the FTP File Activity table for processing locally in memory
			//-- ===========================================================================================
			rs2 = null;
			rsLocal = jdbcDataSource.getResultSet(appProps.getSqlFetchAllRows_Activity(), appProps.getDbSQLTimeout());//--create a new resulset from the SQL query
		    rs2 = new CachedRowSetImpl(); //-- instantiate a new cached rowset implementation
		    rs2.populate(rsLocal); //-- populate the CachedRowset from the query resultset
		    rs2.setTableName(appProps.getFileActivityTableName()); // Set the table name that is to be cached

		    /* The KeyColumns in the CachedRowSet have to be defined independent of underlying dadasource data object
		     * The KeyColumns property is used to uniquely identify a record when the record set is synced with the data source  
		     */
		    int [] keys2 = {1};  // Set column 1 as the cached record key  in the RowSet
	        rs2.setKeyColumns(keys2);
	        
	        //--Build a Hashtable here consisting of fHostName - fPath - fName so each filesystem file can be used to search the db row cache.
	        //-- =============================================================================================================================
        	ResultSetMetaData rsmd;
	    	String colName1,colName2,colName3;
	    	Integer ft = 0;
	    	long recNo = 0;
	        
		 	AppLog.logActivity(appProps,"Number of records in this page?? " + rs1.size() + " page size = " + rs1.getPageSize()  , true, true);
		 	rs1.beforeFirst(); //--Move the row cursor ahead of the first row
		 	
	        recNo = 0;
	        while (rs1.next()) {
	        	recNo++;
	        	rsmd = rs1.getMetaData();
	        	colName1 = rsmd.getColumnName(appProps.getFileName_ColNum());
	        	colName2 = rsmd.getColumnName(appProps.getFilePath_ColNum());
	        	colName3 = rsmd.getColumnName(appProps.getHostName_ColNum());
	        	if (!fileKey_db.add(rs1.getString(colName1)+";"+rs1.getString(colName2)+";"+rs1.getString(colName3))) {
	        		throw new Exception ("Could not add key value " + rs1.getString(colName1)+rs1.getString(colName2)+rs1.getString(colName3) + " to fileKey_db array");
	        	}
			
	        	colName1 = rsmd.getColumnName(appProps.getFileLastMod_Date_ColNum());
	        	fileDateModified_db.add(rs1.getTimestamp(colName1).toString());
	        	fileId_db.add(rs1.getString(1));
	        	fileRowId_db.add(rs1.getRow());
	        } //-- end while(rs1.next())

	        //-- SCAN THE FILE SYSTEM (CACHE) AND UPDATE THE File Catalog Table AS NEEDED 
	        //-- ========================================================================
	        ft = scanFileSystem(appProps, jdbcDataSource);
	        
	        AppLog.logActivity(appProps,"Finished posting records " , true, true);
			AppLog.logActivity(appProps,"Number of cached files scanned: " + fName.size() , true, true);
			AppLog.logActivity(appProps,"Total number of update/insert DB transactions: " + ft.toString() , true, true);	
						
			//-- SCAN THE DB RECORDS AND UPDATE AS 'Deleted' ANY RECORDS THAT ARE NOT FOUND IN THE FILE SYSTEM 
			int fu = scanDBRecords(appProps,jdbcDataSource);
			
			AppLog.logActivity(appProps,"Finished posting file status updates" , true, true);
			AppLog.logActivity(appProps,"Number of DB records scanned: " + rs1.size() , true, true);
			AppLog.logActivity(appProps,"Number of records where status was marked 'Deleted': " + fu , true, true);			
			AppLog.logActivity(appProps,"Source Database posts are complete" , true, true);
			
		
		} catch (Exception e){
	
			String errorMessage = e.getMessage();
			try{
				AppLog.logActivity(appProps, "ERROR!  " + e.getClass() + ": " + errorMessage, true, true);
				try{
					jdbcDataSource.disconnectDataSource();
				} catch (Exception e1){
					// do nothing... this is just in case there was a connection to JDBC
				}		
				//-- Show the stack trace for troubleshooting
				AppLog.logActivity(appProps, AppConstant.TEXT_AFTER_LOG_DATETIME + AppConstant.JAVA_STACK_TRACE_LOG_HEADER, false, true);
				//-- Loop thru the stack trace and log each line
				for (int i=0; i < e.getStackTrace().length ; i++) {
					AppLog.logActivity(appProps, AppConstant.TEXT_AFTER_LOG_DATETIME + "at " + e.getStackTrace()[i].getClassName() + "." +
							e.getStackTrace()[i].getMethodName() + "(" + e.getStackTrace()[i].getFileName() + ":" + 
							e.getStackTrace()[i].getLineNumber() + ")", false, true );
				}				
				//-- Exit with a code of 1 (non zero) so the calling system knows there was an error
				System.exit(1);
			} catch (Exception x) {	
				//-- Only get here if an error handling function failed above
				System.out.println("Error handling system error while processing: " + errorMessage); 
				System.out.println(x.getMessage());
				x.printStackTrace();
				//-- Exit with a code of 1 (non zero) so the calling system knows there was an error
				System.exit(1);
			}
		}		

	}
	/****************************************************************************************************************
	 * Builds an instance of the AppProperties and sets up each property based on the value in the properties file. 
	 * @param appProps
	 * @param fileName
	 * @return AppProperties
	 * @throws Exception
	 */
	public SageFTPFileCatalogerAppProperties setupAndVerifyAppProperties(SageFTPFileCatalogerAppProperties appProps, String fileName) throws Exception{
		Properties props = new Properties();
		//-- Get the text file based on the fileName passed
		try{
			//-- Setup a new URL object for getting the text file from the system
			URL url = ClassLoader.getSystemResource(fileName);
			//-- Load the file by opening a stream... if it fails, then the file is not available
	    	props.load(url.openStream());
		} catch (Exception e) {
			throw new Exception("Failed to open the properties file (" + fileName + ")");
		}
		//-- Retrieve the application properties from the config file, verify each value and add to the AppProperties used by this app
		try{

			//-- LOGGING CONFIGURATION
			//-- =====================
			//-- Setup logging properties ---
			appProps.setAppLogDateTimeFormat(AppConstant.LOG_DATETIME_FORMAT);			
			appProps.setAppLogToFile(AppTools.getAppPropertyValueBoolean(props, fileName, "appLogToFile", true, true, "false"));
			appProps.setAppLogFile(AppTools.getAppPropertyValueString(props, fileName, "appLogFile", true, true, ""));
			appProps.setAppLogNewFileFrequency(AppTools.getAppPropertyValueString(props, fileName, "appLogNewFileFrequency", true, true, ""));
			appProps.setAppLogMaxTextLength(AppTools.getAppPropertyValueInt(props, fileName, "appLogMaxTextLength", true, true, ""));			

			//-- JDBC CONFIGURATION
			//-- =====================
			//-- Setup JDBC database properties ---
			appProps.setDbConnectClass(AppTools.getAppPropertyValueString(props, fileName, "dbConnectClass", true, true, ""));
			appProps.setDbConnectionURL(AppTools.getAppPropertyValueString(props, fileName, "dbConnectionURL", true, true, ""));
			appProps.setDbSQLTimeout(AppTools.getAppPropertyValueInt(props, fileName, "dbSQLTimeout", true, true, "120"));
			appProps.setDbUserID(AppTools.getAppPropertyValueString(props, fileName, "dbUserID", true, true, ""));
			appProps.setDbPassword(AppTools.getAppPropertyValueString(props, fileName, "dbPassword", true, true, ""));
			appProps.setDbVersionQuery(AppTools.getAppPropertyValueString(props, fileName, "dbVersionQuery", false, false, ""));
			appProps.setDbSQLErrorCodesToIgnore(AppTools.getAppPropertyValueString(props, fileName, "dbSQLErrorCodesToIgnore", true, false, "").split(","));
			appProps.setDbDateQualifier(AppTools.getAppPropertyValueString(props, fileName, "dbDateQualifier", true, true, ""));
			appProps.setDbFormat_DateTime(AppTools.getAppPropertyValueString(props, fileName, "dbFormat_DateTime", true, true, ""));
			appProps.setDbFormat_Date(AppTools.getAppPropertyValueString(props, fileName, "dbFormat_Date", true, true, ""));
			appProps.setDbFormat_Time(AppTools.getAppPropertyValueString(props, fileName, "dbFormat_Time", true, true, ""));

			//-- LDAP (BLUEPAGES) CONFIGURATION
			//-- ==============================
			//-- Setup LDAP properties ---
			appProps.setLdapProviderURL(AppTools.getAppPropertyValueString(props, fileName, "ldapProviderURL", true, true, ""));
			appProps.setLdapSearchFilter(AppTools.getAppPropertyValueString(props, fileName, "ldapSearchFilter", true, true, ""));
			appProps.setLdapFieldsToReturn(AppTools.getAppPropertyValueString(props, fileName, "ldapFieldsToReturn", true, true, ""));
			appProps.setLdapValueIfNull(AppTools.getAppPropertyValueString(props, fileName, "ldapValueIfNull", true, true, ""));

			//-- CUSTOM PARAMETERS
			//-- =================
			//-- Setup other custom app properties ---			
			appProps.setSqlInsertIntoFTP_File_Catalog(AppTools.getAppPropertyValueString(props, fileName, "sqlInsertIntoFTP_File_Catalog", true, true, ""));
			appProps.setFileDirectory(AppTools.getAppPropertyValueString(props, fileName, "fileDirectory", true, true, ""));
			appProps.setFileSizeUnit(AppTools.getAppPropertyValueString(props, fileName, "fileSizeUnit", true, true, ""));
			appProps.setDateModifiedFieldName(AppTools.getAppPropertyValueString(props, fileName, "dateModifiedFieldName", true, true, ""));
			appProps.setSqlInsertIntoDB_Activity(AppTools.getAppPropertyValueString(props, fileName, "SqlInsertIntoDB_Activity", true, true, ""));
			appProps.setSqlFetchAllRows(AppTools.getAppPropertyValueString(props, fileName, "SqlFetchAllRows", true, true, ""));
			appProps.setSqlUpdateStatusFTP_File_Catalog(AppTools.getAppPropertyValueString(props, fileName, "SqlUpdateStatusFTP_File_Catalog", true, true, ""));
			appProps.setPageSize(AppTools.getAppPropertyValueInt(props, fileName, "pageSize", true, true, ""));
			
			appProps.setContentHeaderSize(AppTools.getAppPropertyValueInt(props, fileName, "contentHeaderSize", true, true, ""));
			
			appProps.setHostName_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "hostName_ColNum", true, true, ""));
			appProps.setFilePath_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "filePath_ColNum", true, true, ""));
			appProps.setFileName_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileName_ColNum", true, true, ""));
			appProps.setFileSize_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileSize_ColNum", true, true, ""));
			appProps.setFileSize_Unit_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileSize_Unit_ColNum", true, true, ""));
			appProps.setFileCreate_Date_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileCreate_Date_ColNum", true, true, ""));
			appProps.setFileLastMod_Date_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileLastMod_Date_ColNum", true, true, ""));
			appProps.setFilePermissions_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "filePermissions_ColNum", true, true, ""));
			appProps.setFileOwners_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileOwners_ColNum", true, true, ""));
			appProps.setFileStatus_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileStatus_ColNum", true, true, ""));
			appProps.setFileType_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileType_ColNum", true, true, ""));
			
			appProps.setActivityType_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "activityType_ColNum", true, true, ""));
			appProps.setActivityDesc_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "activityDesc_ColNum", true, true, ""));
			appProps.setActivityFileId_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "activityFileId_ColNum", true, true, ""));
			appProps.setSqlFetchAllRows_Activity(AppTools.getAppPropertyValueString(props, fileName, "SqlFetchAllRows_Activity", true, true, ""));

			appProps.setAlertFileName(AppTools.getAppPropertyValueString(props, fileName, "alertFileName", true, true, ""));
			appProps.setFileSystemLocation(AppTools.getAppPropertyValueString(props, fileName, "fileSystemLocation", true, true, ""));

			appProps.setFtpServerName(AppTools.getAppPropertyValueString(props, fileName, "ftpServerName", true, true, ""));
			appProps.setFtpProtocol(AppTools.getAppPropertyValueString(props, fileName, "ftpProtocol", true, true, ""));
			appProps.setFtpServerPort(AppTools.getAppPropertyValueInt(props, fileName, "ftpServerPort", true, true, ""));
			appProps.setFtpUserId(AppTools.getAppPropertyValueString(props, fileName, "ftpUserId", true, true, ""));
			appProps.setFtpPassword(AppTools.getAppPropertyValueString(props, fileName, "ftpPassword", true, true, ""));
			appProps.setExcludeDirs(AppTools.getAppPropertyValueString(props, fileName, "excludeDirs", true, true, ""));
			
			appProps.setFileCatalogTableName(AppTools.getAppPropertyValueString(props, fileName, "fileCatalogTableName", true, true, ""));
			appProps.setFileActivityTableName(AppTools.getAppPropertyValueString(props, fileName, "fileActivityTableName", true, true, ""));
			
			//-- RETURN TO CALLER
			//-- Return the AppProperties object
			return appProps;
			
		} catch (Exception e) {
			//-- If an error occurs, just "bubble it up" to the calling method
			throw e;
		}		
	}
	
	/****************************************************************************************************************
	 * Checks to make sure each parameter/argument passed when launching this app meets specific criteria. For instance, 
	 * the "propertiesFileName" must contain ".properties".
	 * @param propertiesFileName
	 * @throws Exception
	 */
	public void verifyPassedParameters(String propertiesFileName) throws Exception{
		try{
			//-- The "propertiesFileName" must be a file name with ".properties"
			if (propertiesFileName.indexOf(".properties")<0){
				throw new Exception("ERROR: Invalid configuration properties file name argument. File name must have an extension that equals '.properties'.");
			}
		} catch  (Exception e){
			//-- If an error occurs, just "bubble it up" to the calling method
			throw e;
		}	 
	}
	
	/****************************************************************************************************************
	 * Setup the JDBC connection class, URL, user ID and password, then connect to the data source and retrieve some 
	 * basic information about the database server using a custom SQL query from the properties file. Returns a String
	 * representing the database information.
	 * @param jdbcDataSource
	 * @param appProps
	 * @return String
	 * @throws Exception
	 */
	public String initializeJDBCConnection(AppJDBC jdbcDataSource, AppProperties appProps) throws Exception{
		try{
			//-- Setup the JDBC data source with the application properties
			jdbcDataSource.ConnectClass = appProps.getDbConnectClass();
			jdbcDataSource.ConnectionURL = appProps.getDbConnectionURL();
			jdbcDataSource.UserID = appProps.getDbUserID();
			jdbcDataSource.Password = appProps.getDbPassword();			
			//-- Connect to the JDBC database
			jdbcDataSource.connectDataSource();			
			//-- Get information about the database using the SQL version query in the properties
			String jdbcDatabaseDBInfo = "";
			if (!appProps.getDbVersionQuery().equalsIgnoreCase("")){
				jdbcDatabaseDBInfo = jdbcDataSource.getDatabaseStringValue(appProps.getDbVersionQuery(), "DBVersionInfo", appProps.getDbSQLTimeout());
			}
			//-- Return the JDBC database information
			return jdbcDatabaseDBInfo;
		} catch  (Exception e){
			//-- If an error occurs, just "bubble it up" to the calling method
			throw e;
		}	 
	}
	
	
	/****************************************************************************************************************
	 * Builds the necessary SQL using the values in the AppProperties SQL template, then executes the SQL 
	 * against the database using the AppJDBC data source to insert a new record.
	 * @param appProps 
	 * @param jdbcDataSource
	 * @param fileName
	 * @param lastModDate
	 * @param fileSize
	 * @param fileSizeUnit
	 * @throws Exception
	 */
	public int insertNewFileDataRecordIntoDB(
			SageFTPFileCatalogerAppProperties appProps,
			AppJDBC jdbcDataSource,
			String serverName,
			String filePath,
			String fileName,
			String createDate,
			String lastModDate,
			long fileSize, 
			String fileSizeUnit,
			String permissions,
			String owners,
			String fileHeader) throws Exception{
		
		try{
						
			// --- Read the date format for file mod and file creation into local variables
			String lastModDate_Format 	= this.fdtFormat; 
			String createDate_Format 	= this.fdtFormat;
			
			//-- Extract the file extension from the file name
			String fileExt = fileName.substring(fileName.lastIndexOf('.')+1);
			
			//-- Instantiate a meta data object for the result set
		  	ResultSetMetaData rsmd = rs1.getMetaData();
			
		  	 // A updatable ResultSet has a special row that serves as a staging area
	         // for building a row to be inserted.
			rs1.moveToInsertRow();
			
			//-- Generated a new FileID for this new record
			appProps.setCurrentFileID(AppTools.uniqueID());
			rs1.updateString(1, appProps.getCurrentFileID());
			
			//--Populate the cached record columns
			rs1.updateString(rsmd.getColumnName(appProps.getFileName_ColNum()), fileName);
			rs1.updateString(rsmd.getColumnName(appProps.getFilePath_ColNum()), filePath);
			rs1.updateString(rsmd.getColumnName(appProps.getHostName_ColNum()), serverName);
			rs1.updateDouble(rsmd.getColumnName(appProps.getFileSize_ColNum()), fileSize * appProps.getFileSizeMultiplier());
			rs1.updateString(rsmd.getColumnName(appProps.getFileSize_Unit_ColNum()), fileSizeUnit);
			
			//-- Convert the last mod date file system format to a database format
			lastModDate = SageFTPFileCatalogerAppJDBC.prepObjectValueForSQL("datetime",lastModDate,false,appProps.getDbFormat_DateTime().toString(),lastModDate_Format);
			if (lastModDate.indexOf(".") > -1) {
				//-- strip out the single quotes
				lastModDate = lastModDate.substring(1,lastModDate.length()-1);
				//-- Truncate millisecond and timezone portion of the datetime string
				lastModDate = lastModDate.substring(0, lastModDate.indexOf("."));
			} 
			rs1.updateTimestamp(appProps.getFileLastMod_Date_ColNum(), Timestamp.valueOf(lastModDate));

			//-- Convert the create date file system format to a database format
			createDate = SageFTPFileCatalogerAppJDBC.prepObjectValueForSQL("datetime",createDate,false,appProps.getDbFormat_DateTime().toString(),createDate_Format);
			if (createDate.indexOf(".") > -1) {
				//-- strip out the single quotes
				createDate = createDate.substring(1,createDate.length()-1);
				//-- Truncate millisecond and timezone portion of the datetime string
				createDate = createDate.substring(0, createDate.indexOf("."));
			} 
			rs1.updateTimestamp(rsmd.getColumnName(appProps.getFileCreate_Date_ColNum()), Timestamp.valueOf(createDate));
			
			//-- Populate the remaining file attribute columns
			rs1.updateString(rsmd.getColumnName(appProps.getFilePermissions_ColNum()), permissions);
			rs1.updateString(rsmd.getColumnName(appProps.getFileOwners_ColNum()), owners);
			rs1.updateString(rsmd.getColumnName(appProps.getFileStatus_ColNum()), "active");
			rs1.updateString(rsmd.getColumnName(appProps.getFileType_ColNum()), fileExt);
			//-- Insert the row
	        rs1.insertRow();
	        //-- need to move away from insert row before apply changes
	        rs1.moveToCurrentRow();
	        rs1.acceptChanges(jdbcDataSource.dbConnection);
	        rs1.commit();
	    	AppLog.logActivity(appProps,"New Record in Cache: FileKey= " + fileName+"-"+filePath+"-"+serverName+"\nCurrent Row# "+rs1.getRow(), true, true);
	    	
	    	/*
			//-- insert a corresponding DB activity record to record the record update
			String activityType = "insert";
			String activityDesc = "New File Record ";
			insertNewActivityRecordIntoDB(appProps,jdbcDataSource,activityType,activityDesc,appProps.getCurrentFileID());
	    	*/
	    	
	    	return 1;
	    				
		} catch  (Exception e){
			//-- If an error occurs, just "bubble it up" to the calling method
			throw e;
		}	 
	}
	
	/****************************************************************************************************************
	 * Builds the necessary SQL using the values in the AppProperties SQL template, then executes the SQL 
	 * against the database using the AppJDBC data source to update a record.
	 * @param appProps 
	 * @param jdbcDataSource
	 * @param string2 
	 * @param string 
	 * @param pFile
	 * @param fileID
	 * @throws Exception
	 */
	public int updateFileDataRecordInDB(
			SageFTPFileCatalogerAppProperties appProps,
			AppJDBC jdbcDataSource,
			int rowNum,
			String lastModDate,
			long fileSize, 
			String fileSizeUnit,
			String permissions,
			String owners,
			String fileHeader,
			String fileId) throws Exception{
	
		
		try{
			
			// -- ::: Update the File Meta Data record with any changes
				
			String lastModDate_Format 	= this.fdtFormat; 
			
			//--Move the record pointer to the row that is to be updated
			rs1.absolute(rowNum);
			
			//--Instantiate the record meta data object
		  	ResultSetMetaData rsmd = rs1.getMetaData();
			
			//--Populate the cached record columns
		  	String colName = rsmd.getColumnName(appProps.getFileSize_ColNum());
		  	rs1.updateDouble(colName, fileSize * appProps.getFileSizeMultiplier());
		  	int colNum = appProps.getFileSize_Unit_ColNum();
		  	colName = rsmd.getColumnName(colNum);
			rs1.updateString(colName, fileSizeUnit);
			
			//-- Convert the last mod date from file system form to db format
			lastModDate = SageFTPFileCatalogerAppJDBC.prepObjectValueForSQL("datetime",lastModDate,false,appProps.getDbFormat_DateTime().toString(),lastModDate_Format);
			if (lastModDate.indexOf(".") > -1) {
				//-- strip out the single quotes
				lastModDate = lastModDate.substring(1,lastModDate.length()-1);
				//-- Truncate millisecond and timezone portion of the datetime string
				lastModDate = lastModDate.substring(0, lastModDate.indexOf("."));
			} 
			
			//-- populate the remaining db columns with file attribute values and commit the changes
			colName = rsmd.getColumnName(appProps.getFileLastMod_Date_ColNum());
			rs1.updateTimestamp(colName, Timestamp.valueOf(lastModDate));
			colName = rsmd.getColumnName(appProps.getFilePermissions_ColNum());
			rs1.updateString(colName, permissions);
			colName = rsmd.getColumnName(appProps.getFileOwners_ColNum());
			rs1.updateString(colName, owners);
			colName = rsmd.getColumnName(appProps.getFileStatus_ColNum());
			rs1.updateString(colName, "active");
			rs1.updateRow();
			rs1.acceptChanges(jdbcDataSource.dbConnection);
			rs1.commit();
			
			return 1;
			
			/*
			try {
				//-- Execute the UPDATE SQL in the jdbcdataSource
				//-- insert a corresponding DB activity record to record the record update
				String activityType = "update";
				String activityDesc = "LastMod_Date changed to " + lastModDate;
				activityDesc = activityDesc + " All fields have been synced to the current file meta data.";
				insertNewActivityRecordIntoDB(appProps,jdbcDataSource,activityType,activityDesc,fileId);				
				
				return 1;
				
			} catch (Exception e){
				//AppLog.logActivity(appProps,"SQL=" + updateSQL, true, true);
				throw e;
			}
			*/	
			
		} catch  (Exception e){
			//-- If an error occurs, just "bubble it up" to the calling method
			throw e;
		}	 
	}
	/****************************************************************************************************************
	 * Builds the necessary SQL using the values in the AppProperties SQL template, then executes the SQL 
	 * against the database using the AppJDBC data source to update a record.
	 * This version of the overloaded function supports a status change (filestatus)
	 * @param appProps 
	 * @param jdbcDataSource
	 * @param pFile
	 * @param fileID
	 * @param fileStatus
	 * @throws Exception
	 */
	public int updateFileDataRecordInDB(
			SageFTPFileCatalogerAppProperties appProps, AppJDBC jdbcDataSource, String fileID, String fileStatus) throws Exception{
	
		
	
			try {
				//-- instantiate a meta data object for the result set	
			  	ResultSetMetaData rsmd = rs1.getMetaData();
			  	
				rs1.updateString(rsmd.getColumnName(appProps.getFileStatus_ColNum()), fileStatus);
				rs1.updateRow();
				rs1.acceptChanges(jdbcDataSource.dbConnection);
				rs1.commit();
				
				/*
				//-- insert a corresponding DB activity record to record the record update
				String activityType = "update";
				String activityDesc = "Record status was updated to " + fileStatus;
				insertNewActivityRecordIntoDB(appProps,jdbcDataSource,activityType,activityDesc,fileID);
				AppLog.logActivity(appProps,"Inserted Activity Record # " + fa + " of " + rs2.size() , true, true);
				*/
				
				return 1;
				
			} catch (Exception e){
				AppLog.logActivity(appProps,"FileID= " + fileID, true, true);
				throw e;
			}
			
	}
	
	/****************************************************************************************************************
	 * Builds the necessary SQL using the values in the AppProperties SQL template, then executes the SQL 
	 * against the database using the AppJDBC data source to insert a new Activity record.
	 * @param appProps 
	 * @param jdbcDataSource
	 * @throws Exception
	 */
	public void insertNewActivityRecordIntoDB(
			SageFTPFileCatalogerAppProperties appProps, 
			AppJDBC jdbcDataSource, 
			String activityType,
			String activity_Desc,
			String newFileID) throws Exception{
		
		try {
			
			//-- Truncate the description to 500 bytes
			if (activity_Desc.length() > 500) {
				activity_Desc = activity_Desc.substring(0, 500);	
			}
			
			//-- Instantiate the Resultset Meta Data Object
			ResultSetMetaData rsmd = rs2.getMetaData();
			
			rs2.moveToInsertRow();
			
			//-- Generated a new FileID for this new record
			
			rs2.updateString(1, AppTools.uniqueID());
			
			//--Populate the cached record columns
			rs2.updateString(rsmd.getColumnName(appProps.getActivityType_ColNum()), activityType);
			rs2.updateString(rsmd.getColumnName(appProps.getActivityDesc_ColNum()), activity_Desc);
			rs2.updateString(rsmd.getColumnName(appProps.getActivityFileId_ColNum()), newFileID);
			//-- Insert the row
	        rs2.insertRow();
	        //-- need to move away from insert row before apply changes
	        rs2.moveToCurrentRow();
	        //--Increment the file activity row insert counter for the current page
	        fa++;
	        rs2.acceptChanges(jdbcDataSource.dbConnection);
	        rs2.commit();
	   
			
		} catch (Exception e2) {
			throw e2;
		}
		
	} // end of Method - insertNewActivityRecordIntoDB
	
	/****************************************************************************************************************
	 * Scans the file system CACHED in the Class properties as ArrayLists
	 * For each file, the DB is queried for a corresponding meta data record. (After caching the recordset in memory)
	 * If non is found a new record is inserted, otherwise the existing record is updated, if needed
	 * An activity record is created if a record is inserted or updated to maintain an audit trail of db activities
	 * @param appProps
	 * @param jdbcDataSource
	 * @param dir
	 * @throws Exception
	 */
	public Integer scanFileSystem(SageFTPFileCatalogerAppProperties appProps, AppJDBC jdbcDataSource) throws Exception {

		//-- Initialize the variable for tracking number of affected DB records
		Integer fu = 0; // # of Updated records for the current batch
		Integer fi = 0; // # of Inserted Records for the current batch
		Integer fiTotal = 0; // # Total inserted records
		Integer fuTotal = 0; // # Total updated records
		Integer fTotal = 0; // # of total transactions applies to records
		ArrayList<String> colVals = new ArrayList<String>(); // Column values array for the current row
		ArrayList<String> colLabels = new ArrayList<String>(); // Column labels array for the current row
      	ResultSetMetaData rsmd;
    	String fileDateModified;
    	String fileDateCreated;
    	
    	/*
    	 * First loop through the file list and update existing record 
    	 * Next find all the records that need to be inserted
    	 * This double pass through the file list is required, because inserted records cause the rowNum to become out of sync with the
    	 * cached filekey array.
    	 * 
    	 */
    	
    	//-- Update Loop
    	
		//Keep going while there are more files
    	
		for (int i=0; i < fName.size(); i++) {
			
			
			//-- The search key for finding the file in the File Catalog DB is Server-path-filename
			String searchKey = fName.get(i)+";"+fPath.get(i)+";"+fHostName.get(i);
			
			int fileNum = fileKey_db.indexOf(searchKey); //-- for diagnostic purposes
			/* DIAGNOSTICS STATEMENT
			AppLog.logActivity(appProps,"Searching For File: "+fName.get(i)+ " Path: "+fPath.get(i)+" Hostname: "+fHostName.get(i)
					+"\n FileKey Value: " + fName.get(i)+fPath.get(i)+fHostName.get(i)
					+"\n FileKey_db index (fileNum)= "+ fileNum 
					+"\n Current DB Row# is "+ rs1.getRow(), true, true);
			*/
			
			if (fileNum != -1) {

				//A match was found for the file key index
				//Check if the "Last Modified Date" of the file meta data is older than the date on the actual file.
				
				//-- Copy the data modified db value to a local string variable
				String fileDateModified_db_str = fileDateModified_db.get(fileKey_db.indexOf(fName.get(i)+";"+fPath.get(i)+";"+fHostName.get(i)));
				
				//-- convert the date-modified OS format to a format that the DBMS can understand
				fileDateModified = AppTools.dateTimeStringToDBTimeStamp( fMod.get(i),fdtFormat, appProps.getDbFormat_DateTime().toString());

				//-- Copy File Meta Data Record ID to a local string variable
				String fileId_str = fileId_db.get(fileNum);
				
				if (!fileDateModified.equalsIgnoreCase(fileDateModified_db_str)) {
					
					//--File has changed so update the date-modified date and any other attributes that need to be updated
					//-- get the cached row id based on the search (file) key consisting of the unique value fName-fPath-fHostName 
					int rowNum = fileRowId_db.get(fileKey_db.indexOf(fName.get(i)+";"+fPath.get(i)+";"+fHostName.get(i)));					
					
					//--Move the record pointer to the row that that is to be updated
					rs1.absolute(rowNum);
					
					AppLog.logActivity(appProps,"File Mod dates don't match for file;path;hostname \n" + 
							 fName.get(i)+";"+fPath.get(i)+";"+fHostName.get(i) + "\n - FileModDate: " + fileDateModified 
							+ " DBFileModDate: " + fileDateModified_db_str 
							+ "\nRow# to update is "+rowNum
							+"\nfileDateModified_db array index is "+fileKey_db.indexOf(fName.get(i)+";"+fPath.get(i)+";"+fHostName.get(i)) , true, true);
					//--Store Meta Data Info for this row in the rmsd object variable
					rsmd = rs1.getMetaData();
					
					
					// -- Initialize the Column Labels and column values arrays
					colVals.clear();
					colLabels.clear();
					//-- Copy the original row column values to a local ArrayList
					int colCount = rsmd.getColumnCount();
					int x1 = 1;
					while (x1 <= colCount) {
						if (!this.rs1.getString(x1).equals(null)) {
							colVals.add(rs1.getObject(x1).toString());
							colLabels.add(rsmd.getColumnLabel(x1));
							AppLog.logActivity(appProps, rsmd.getColumnLabel(x1)+"="+rs1.getObject(x1).toString() , true, true);
						}
						x1++;
					} // end while
					
					
					fu = fu + updateFileDataRecordInDB(
							appProps,
							jdbcDataSource,
							rowNum,
							fMod.get(i),
							fSize.get(i), 
							fSizeUnit.get(i),
							fPermissions.get(i),
							fOwners.get(i),
							fHeaders.get(i), 
							fileId_str);
			
		
					rs1.absolute(rowNum);
					//-- This db activity type is a record update
					String activityType = "update";
					//-- Construct the Activity description string
					int x = 1;
					String ud = "Updated Values: ";
					while (x <= colCount){
						if (!colVals.get(x-1).equals(null)){
							//###### KNOWN ISSUE #########
							//###### JAVA NULL POINTER EXCEPTION WHEN DB FIELD IS EMPTY/NULL
							//############################
//							String colVal = "col " + x + " from rs = " + rs.getObject(x).toString();
//							String colVal2 = "col " + x + " from rs2 = " + rs2.getObject(x).toString();
							if (!colVals.get(x-1).equals(this.rs1.getObject(x).toString())){
								ud = ud + colLabels.get(x-1) + " changed from '" + colVals.get(x-1) + "' to '" + this.rs1.getObject(x).toString() + "' ";
								AppLog.logActivity(appProps,"Filename: " + fName.get(i) + " " + colLabels.get(x-1) + " old value: " + colVals.get(x-1) + ", new value:" + this.rs1.getObject(x).toString() , true, true);
							}
						}
						x++;
					} // end while
					
				} // end if (!fileDateModified.equalsIgnoreCase(fileDateModified_db_str))
				else {
					//## Diagnostic statement
					//AppLog.logActivity(appProps,"File#"+ i +" File: "+ fName.get(i) + " File Mod dates match- FileModDate: " + fileDateModified 
					//		+ " DBFileModDate: " + dbDateModified , true, true);
				} // end else
			} // end if (fileNum != -1)
		} // end for loop	
		
		fTotal = fTotal + fu;
		fuTotal = fuTotal + fu;
		
		//-- Insert Records Loop
			
		for (int i=0; i < fName.size(); i++) {
						
			//-- The search key for finding the file in the File Catalog DB is Server-path-filename
			String searchKey = fName.get(i)+";"+fPath.get(i)+";"+fHostName.get(i);
			int fileNum = fileKey_db.indexOf(searchKey); //-- for diagnostic purposes
			
			/*
			AppLog.logActivity(appProps,"Searching For File: "+fName.get(i)+ " Path: "+fPath.get(i)+" Hostname: "+fHostName.get(i)
					+"\n FileKey Value: " + fName.get(i)+fPath.get(i)+fHostName.get(i)
					+"\n FileKey_db index (fileNum)= "+ fileNum 
					+"\n Current DB Row# is "+ rs1.getRow(), true, true);
			*/
			
			if (fileNum == -1) {
				//-- The File Record was not found in the database
				//-- So a new record needs to be inserted
					
				AppLog.logActivity(appProps,"Ready to insert file meta data into Cache for file: "+fName.get(i)+" , last modified: "+fMod.get(i), true, true);
				//-- Insert the new record
					
				fi = fi + insertNewFileDataRecordIntoDB(
						appProps,
						jdbcDataSource,
						fHostName.get(i),
						fPath.get(i),
						fName.get(i),
						fCDate.get(i),
						fMod.get(i),
						fSize.get(i),
						fSizeUnit.get(i),
						fPermissions.get(i),
						fOwners.get(i),
						fHeaders.get(i));
/*
				//-- Post records to the data source every dsBatchSize records
				if (fi > appProps.getPageSize()) {
					fTotal = fTotal + fi;
					fi = 0;
					rs1.acceptChanges(jdbcDataSource.dbConnection);  // On non-autocommit Connection
					rs1.commit();
				}
*/				
			}
		}  // end of for loop
		
		fTotal = fTotal + fi;
		fiTotal = fiTotal + fi;
		
		AppLog.logActivity(appProps,"Number of DB records inserted: " + fiTotal.toString() , true, true);
		AppLog.logActivity(appProps,"Number of DB records updated: " + fuTotal.toString() , true, true);
		
		return fTotal;
		
	} // end of Method: scanFileSystem
	
	/****************************************************************************************************************
	 * Scans the Database records to look for file records that no longer have a corresponding physical file on the file system
	 * For each db record, the defined file is checked that it still exists.
	 * If not found the db record is marked 'Inactive' and a corresponding activity record is created 
	 * indicating the details of the activity to maintain an audit trail of db activities.
	 * @param appProps
	 * @param jdbcDataSource
	 * @param dir
	 * @throws Exception
	 */
	public int scanDBRecords(SageFTPFileCatalogerAppProperties appProps, AppJDBC jdbcDataSource) throws Exception {
		
			
			try {
				
				String thisFileName;
				String fileID;
				int fu = 0;
				int recNo = 0;
				int fuTotal = 0;
				
				AppLog.logActivity(appProps,rs1.size() + "File Catalog records are being scanned to check for 'Deleted' status" , true, true);
			    rs1.beforeFirst(); //--Move the row cursor ahead of the first row
				while (rs1.next()) {
					
					recNo++;
					
					//-- read the host name, file path and file name from the db record into local variables
					thisFileName = rs1.getString(appProps.getFileName_ColNum());
					
					//-- attempt to get a handle to the file on the file system Cache from the db record definition
					//AppLog.logActivity(appProps,"Scanning Db Record# " + recNo + " of " + rs1.size() , true, true);
					
					//-- test if the file exists
					if (fName.indexOf(thisFileName) == -1 ) {
						//-- File was not found -- mark the File DB record status 'Deleted'
						//-- first column in resultset is assumed to be the FileID
						//##### DEVELOPER NOTE: consider parameterizing the FileID column number of the result set #####
						fileID = rs1.getString(1);
						
						fu = fu + updateFileDataRecordInDB(appProps,jdbcDataSource,fileID,"Deleted");
						fuTotal = fuTotal++;
						AppLog.logActivity(appProps,"Rec. update # " + fuTotal + " , " + thisFileName + " was not found on the file system. Updating Status to 'Deleted'" , true, true);
						
					}
					
				}
				
				if (recNo == 0) {
					//-- Write a warning message to the log indicating that no rercords were found in the File Catalog table
					//AppLog.logActivity(appProps,appProps.getNoRecordsFoundMsg() , true, true);
					//#### TEMP STATEMENT FOR TESTING ####
					AppLog.logActivity(appProps,"WARNING: No records were found in the File Catalog table" , true, true);
				}
				
				return fu;
		
				
			} catch (Exception e){
				throw e;
			}
	}
	
	/****************************************************************************************************************
	 * Post an alert to a designated file for inter-app communication 
	 * 
	 * @param alertMsg
	 * @throws Exception
	 */
	public void postAlert(String alertFileName, String alertMsg) throws Exception{
		try{
			// --- Write the alert message to the file
			//-- Startup a FileWriter based on the file name (creates a new file if one isn't there)
			// -- the 'false' flag will cause existing content in the file to be overwritten
			FileWriter outFile = new FileWriter(alertFileName,false);
			//-- Startup a PrintWriter and write the new line to the file
			PrintWriter out = new PrintWriter(outFile);
			out.println(alertMsg);
			//-- Close the file
			out.close();
			
			/*
			// ##### Debugging ####
			// Read back the content from the file
			Path readFile = Paths.get(alertFileName);
			byte[] fileArray;
			fileArray = Files.readAllBytes(readFile);
			*/
			
			
		} catch  (Exception e){
			//-- If an error occurs, just "bubble it up" to the calling method
			throw e;
		}	 
	}
	
		
	/****************************************************************************************************************
	 * get File Meta Data from Local File System and store it in the public Class Property arrays for later use in database updates
	 * @param appProps
	 * @throws Exception
	 */
	public void getLocalFileMetaData(SageFTPFileCatalogerAppProperties appProps, File dir) throws Exception {
				//Loop through all the files in current Directory (and sub-directories recursively , if flag is set).
				Boolean isSearchSubDirs = true;
				Iterator<File> files = FileUtils.iterateFiles(dir, null, isSearchSubDirs);
				//Keep going while there are more files
				while (files.hasNext()) {
					//Get the next file in the Iterator
					File thisFile = files.next();
					//Create a File Path object (Java.NIO.2 API)
					Path pFile = thisFile.toPath();
					
					//Log some basic file attributes for diagnostics purposes
					AppLog.logActivity(appProps,"Local File Name = " + pFile.getFileName(), true, true);
					AppLog.logActivity(appProps,"Local Parent Path = " + pFile.getParent(), true, true);
					
					//Filesystem Space stats
					FileStore store = Files.getFileStore(pFile);
					long total = store.getTotalSpace() / 1024 / 1024;
					long avail = store.getUsableSpace() / 1024 / 1024;
					long used = (store.getTotalSpace() - store.getUnallocatedSpace()) / 1024 / 1024;
					AppLog.logActivity(appProps,"Total Space: " + total + " MB, Available Space " + avail + " MB, Used Space " + used + " MB", true, true);
					
					//The Basic File attributes class is platform independent 
					BasicFileAttributes bfa = Files.readAttributes(pFile, BasicFileAttributes.class);
					//Check if File Attributes for POSIX system is supported (Linux/Unix/AIX)
					if (store.supportsFileAttributeView(PosixFileAttributeView.class)) {
						// Here extract File Permission information, Group permissions etc
					}
					
					//Log File activity stats for diagnostics purposes
					AppLog.logActivity(appProps,"Last Modified: " + bfa.lastModifiedTime(), true, true);
					AppLog.logActivity(appProps,"Created: " + bfa.creationTime(), true, true);
					AppLog.logActivity(appProps,"Last Accessed: " + bfa.lastAccessTime(), true, true);
					AppLog.logActivity(appProps,"File Size: " + bfa.size() /1024 + " kb", true, true);
					String fileExt = pFile.getFileName().toString().substring(pFile.getFileName().toString().lastIndexOf('.')+1);
					AppLog.logActivity(appProps,"File Type/Extension: " + fileExt , true, true);
					
					
					// -- write the file meta data to Class properties arrayList 
					fHostName.add(appProps.getFtpServerName());
					fPath.add(pFile.getParent().toString());
					fName.add(pFile.getFileName().toString());
					fCDate.add(bfa.creationTime().toString());
					fADate.add(bfa.lastAccessTime().toString());
					fMod.add(bfa.lastModifiedTime().toString());
					fSize.add(bfa.size());
					fSizeUnit.add(appProps.getFileSizeUnit());
					fPermissions.add("TBD");
					fOwners.add(Files.getOwner(pFile).getName().toString());
					FileReader fr = new FileReader(pFile.toFile());
					char [] fh = new char[appProps.getContentHeaderSize()];
					fr.read(fh);
					fr.close();
					fHeaders.add(fh.toString());
				} // end of while loop	
	}
	
}	// end of SageFTPFileCatalogApp