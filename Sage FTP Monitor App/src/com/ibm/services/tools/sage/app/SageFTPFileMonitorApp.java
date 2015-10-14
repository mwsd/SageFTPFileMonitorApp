/**
 * 
 */
package com.ibm.services.tools.sage.app;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import com.ibm.services.tools.sage.utilities.*;
import com.ibm.services.tools.sage.app.SageFTPFileCatalogerApp;
/**
 * @author yogi
 *
 */
public class SageFTPFileMonitorApp {

	/****************************************************************************************
	 * This class is the primary application class used for the backend processing for the "Sage FTP Monitoring" application.
	 * The SageFTPFileMonitorApp is the second of the 2 components that make up the Sage FTP Monitoring application.
	 * The 1st component called Sage FTP Cataloger App (which runs in its own JRE), 
	 * connects to the file system, reads the file meta data into Cache and then updates the FTP file catalog database. 
	 * This component utilizes a ".properties" file for instructions, JDBC database 
	 * interaction to read business rule records , file catalog to read file meta data and Sage Autoemailer table for posting alerts.
	 * Event activity is written to the Activities table for logging and audit purposes. 
	 * Specific parameters must be passed when launching the application.  The parameters are as follows:
	 *  1) Properties File Name (required):  The name of the properties file containing the application instructions, such as "config.properties".
	 * 		   
	 * @author		 @author		Yogi Golle/Tampa/Contr/IBM (yogigol@us.ibm.com) - IBM GTS Service Delivery - MWSD Tools and Technology Team
	 * @version 	2015.06.24   
	 */
	//-- DB Result set Object 1
	ResultSet rs1;
	//-- DB Result set Object 2
	ResultSet rs2;
	
	public SageFTPFileMonitorApp() {
		// Class constructor instantiates the Class object.
	}
	
	public void runApp(String propertiesFileName) throws Exception{
		// --- Instantiate the application properties class
		SageFTPFileMonitorAppProperties appProps = new SageFTPFileMonitorAppProperties();
		// --- Instantiate the SageFTPFileCatalogerApp class (to gain access to shared methods between File Cataloger and File Monitor component 
		SageFTPFileCatalogerApp sftpfcapp = new SageFTPFileCatalogerApp();
		// --- Instantiate 2 datasource objects
		AppJDBC jdbcDataSource = new AppJDBC();
		AppJDBC jdbcDataSource2 = new AppJDBC();
		
		String processID;		
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

			//-- SETUP THE JDBC DATABASE CONNECTION AND CONNECT TO THE SAGE AUTO NOTIFIER DATASOURCE
			//-- ==================================================================================
			AppLog.logActivity(appProps,"Connecting to JDBC data source: class=" + appProps.getDbConnectClass() + 
					", url=" + appProps.getDbConnectionURL2() + ", userid=" + appProps.getDbUserID2(), true, true);
			
			//-- Setup the JDBC data source with the application properties
			jdbcDataSource2.ConnectClass 	= appProps.getDbConnectClass();
			jdbcDataSource2.ConnectionURL 	= appProps.getDbConnectionURL2();
			jdbcDataSource2.UserID 			= appProps.getDbUserID2();
			jdbcDataSource2.Password 		= appProps.getDbPassword2();			
			//-- Connect to the JDBC database
			jdbcDataSource2.connectDataSource();			
			//-- Get information about the database using the SQL version query in the properties
			String jdbcDatabaseDBInfo2 = "";
			if (!appProps.getDbVersionQuery().equalsIgnoreCase("")){
				jdbcDatabaseDBInfo2 = jdbcDataSource2.getDatabaseStringValue(appProps.getDbVersionQuery(), "DBVersionInfo", appProps.getDbSQLTimeout());
			}
			AppLog.logActivity(appProps,"Connected to JDBC data source: " + jdbcDatabaseDBInfo2, true, true);

			
			//-- SETUP THE JDBC DATABASE CONNECTION AND CONNECT TO THE SAGE FILE CATALOG DATASOURCE
			//-- ==================================================================================
			AppLog.logActivity(appProps,"Connecting to JDBC data source: class=" + appProps.getDbConnectClass() + 
					", url=" + appProps.getDbConnectionURL() + ", userid=" + appProps.getDbUserID(), true, true);
			String jdbcDatabaseDBInfo = sftpfcapp.initializeJDBCConnection(jdbcDataSource, appProps);
			AppLog.logActivity(appProps,"Connected to JDBC data source: " + jdbcDatabaseDBInfo, true, true);
			
			
			
			/* ### HIGH LEVEL SUDO CODE
			 * Step 1: SFTPFM reads log file A2a to check for any GSA Cell connection alerts since last scan and Sage DB connectivity alerts.
			 * Step 2: If any alert info was found in step 1, SFTPFM creates a record in the Notification_Outgoing Table (C2)
			 * Step 3: SFTPFM (A1) loads the file action criteria from the property file (A1b).
			 * step 4: For each action criteria in the property file (A1b) the "file business rules table"(C1c) is searched to check  if any thresholds have been reached and what action to take.
			 * Step 5: For cases where a notification action is required, SFTPFM(A1) posts a notification record in the Notification_Outgoing Table (C2) for processing by the LotusNotesAutoEmailer App. (A3)
			 * 
			 */
			
			//-- Step 1 - Check FTP Cataloger log file for any Cataloger alerts
			ArrayList<String> alerts = checkSageFTPCatalogerAlerts(appProps.getFtpFileCataloger_alertFile());
			
			//-- Step 2 - Send email for any alerts that were discovered
			if (!alerts.isEmpty()) {
				
				handleEventTrigger(appProps, jdbcDataSource2,  "email",alerts, appProps.getExceptionEventRecipients_Default());
				
			} else {
				AppLog.logActivity(appProps,"No alerts were found from Cataloger App.", true, true);
			}
			
			//-- Step 3 - covered in 'setupAndVerifyAppProperties' method
			
			//-- Step 4 - Loop through the business rules and take action as needed
			processBusinessRules(appProps,jdbcDataSource,jdbcDataSource2);
			
			
		}
		
		catch (Exception e){
			
			String errorMessage = e.getMessage();
			try{
				AppLog.logActivity(appProps, "ERROR!  " + e.getClass() + ": " + errorMessage, true, true);
				errorMessage = "ERROR!  " + e.getClass() + ": " + errorMessage;
				try{
					jdbcDataSource.disconnectDataSource();
				} catch (Exception e1){
					// do nothing... this is just in case there was a connection to JDBC
				}		
				ArrayList<String> exceptionMsg = new ArrayList<String>();
				
				//-- Show the stack trace for troubleshooting
				String outStr = AppConstant.TEXT_AFTER_LOG_DATETIME + AppConstant.JAVA_STACK_TRACE_LOG_HEADER;
				AppLog.logActivity(appProps, outStr, false, true);
				exceptionMsg.add(outStr);
				
				//-- Loop through the stack trace and log each line
				for (int i=0; i < e.getStackTrace().length ; i++) {
					outStr = AppConstant.TEXT_AFTER_LOG_DATETIME + "at " + e.getStackTrace()[i].getClassName() + "." +
							e.getStackTrace()[i].getMethodName() + "(" + e.getStackTrace()[i].getFileName() + ":" + 
							e.getStackTrace()[i].getLineNumber() + ")";
					AppLog.logActivity(appProps, outStr, false, true );
					exceptionMsg.add(outStr);
				}
				if (!jdbcDataSource2.equals(null)) {
					// --- send notification of the exception to designated contacts
					exceptionMsg.add(errorMessage);
					handleEventTrigger(appProps, jdbcDataSource2,  "email",exceptionMsg,appProps.getExceptionEventRecipients_Default());
					AppLog.logActivity(appProps, "Exception notification sent to " + appProps.getExceptionEventRecipients_Default(), true, true);
				}
				jdbcDataSource2.disconnectDataSource();
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
public SageFTPFileMonitorAppProperties setupAndVerifyAppProperties(SageFTPFileMonitorAppProperties appProps, String fileName) throws Exception{
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

		appProps.setDbConnectionURL2(AppTools.getAppPropertyValueString(props, fileName, "dbConnectionURL2", true, true, ""));
		appProps.setDbUserID2(AppTools.getAppPropertyValueString(props, fileName, "dbUserID2", true, true, ""));
		appProps.setDbPassword2(AppTools.getAppPropertyValueString(props, fileName, "dbPassword2", true, true, ""));
		
		//-- CUSTOM PARAMETERS
		//-- =================
		//-- Setup other custom app properties ---			
		appProps.setFtpFileCataloger_alertFile(AppTools.getAppPropertyValueString(props, fileName, "ftpFileCataloger_alertFile", true, true, ""));
		appProps.setSqlInsertIntoDB_AutoEmailer(AppTools.getAppPropertyValueString(props, fileName, "sqlInsertIntoDB_AutoEmailer", true, true, ""));
		appProps.setExceptionEventRecipients_Default(AppTools.getAppPropertyValueString(props, fileName, "exceptionEventRecipients_Default", true, true, ""));
		appProps.setFileName_Pattern_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileName_Pattern_ColNum", true, true, ""));
		appProps.setFileNameMode_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileNameMode_ColNum", true, true, ""));
		appProps.setFileFrequency_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileFrequency_ColNum", true, true, ""));
		appProps.setMaxAlerts_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "maxAlerts_ColNum", true, true, ""));
		appProps.setAlertCount_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "alertCount_ColNum", true, true, ""));
		appProps.setAlertSent_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "alertSent_ColNum", true, true, ""));
		appProps.setSqlSelectByFileName(AppTools.getAppPropertyValueString(props, fileName, "sqlSelectByFileName", true, true, ""));
		appProps.setSqlSelectByFilePattern(AppTools.getAppPropertyValueString(props, fileName, "sqlSelectByFilePattern", true, true, ""));
		appProps.setExceptionEventRecipients_Default(AppTools.getAppPropertyValueString(props, fileName, "exceptionEventRecipients_Default", true, true, ""));
		appProps.setSqlSelectBusinessRuleData(AppTools.getAppPropertyValueString(props, fileName, "sqlSelectBusinessRuleData", true, true, ""));
		appProps.setAlertMsgTemplate(AppTools.getAppPropertyValueString(props, fileName, "alertMsgTemplate", true, true, ""));
		appProps.setWarningMsgTemplate(AppTools.getAppPropertyValueString(props, fileName, "warningMsgTemplate", true, true, ""));
		appProps.setSendTo_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "sendTo_ColNum", true, true, ""));
		appProps.setPlannedDay_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "plannedDay_ColNum", true, true, ""));
		appProps.setThresholdDay_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "thresholdDay_ColNum", true, true, ""));
		appProps.setPlannedTime_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "plannedTime_ColNum", true, true, ""));
		appProps.setThresholdTime_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "thresholdTime_ColNum", true, true, ""));
		appProps.setLastMod_Date_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "lastMod_Date_ColNum", true, true, ""));
		appProps.setCreateDate_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "createDate_ColNum", true, true, ""));
		appProps.setSqlUpdateBusinessRuleIndex(AppTools.getAppPropertyValueString(props, fileName, "sqlUpdateBusinessRuleIndex", true, true, ""));
		appProps.setBusinessRuleIndexID_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "businessRuleIndexID_ColNum", true, true, ""));
		appProps.setFileName_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "fileName_ColNum", true, true, ""));
		appProps.setStatus_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "status_ColNum", true, true, ""));
		appProps.setAlertInterval_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "alertInterval_ColNum", true, true, ""));
		appProps.setLastAlertDate_ColNum(AppTools.getAppPropertyValueInt(props, fileName, "lastAlertDate_ColNum", true, true, ""));
		
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
	try {
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
 * Checks File Cataloger alert file for any file system errors 
 * 
 * @param 
 * @throws Exception
 */
public ArrayList<String> checkSageFTPCatalogerAlerts(String sFTPFileCatAlertFile) throws Exception {

	// --- Create a File Object from the file name
	File alertFile = new File(sFTPFileCatAlertFile);
	// --- Create an Array of strings that will hold the lines in the file
	ArrayList<String> lines = new ArrayList<String>();
	
	// --- Check if the file exists
	if (alertFile.exists()) {
		
		// --- Prepare the buffer to read data from the file
		FileInputStream fis = new FileInputStream(alertFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		
		// --- Read all the lines in the file
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}	
		br.close();
	}
	// --- return the result to the caller
	return lines;
	}


/****************************************************************************************************************
 * Handle Events that are generated by the Monitoring logic and respond with appropriate action
 * as defined the the input parameters / app configuration
 * 
 * @param appProps
 * @param jdbcDataSource
 * @param action
 * @param alerts
 * @throws Exception
 */
public int handleEventTrigger(SageFTPFileMonitorAppProperties appProps, AppJDBC jdbcDataSource, String action, ArrayList<String> alerts, String sendTo) throws Exception {
	
	try {

		//-- Get the SQL from the properties and update with actual values from for record insert into DB Activity Table
		String insertSQL = appProps.getSqlInsertIntoDB_AutoEmailer();
		
		//--- Substitute the tags for values
		insertSQL = AppTools.updateStringTagWithValue(insertSQL,"[[Notification_Outgoing_RowID]]", AppJDBC.prepObjectValueForSQL("string",AppTools.uniqueID(),false));
		insertSQL = AppTools.updateStringTagWithValue(insertSQL,"[[SOURCE_APPLICATION]]", AppJDBC.prepObjectValueForSQL("string",AppConstant.APPLICATION_NAME,false));
		insertSQL = AppTools.updateStringTagWithValue(insertSQL,"[[SUBJECT]]", AppJDBC.prepObjectValueForSQL("string",AppConstant.APPLICATION_NAME+" "+action + " New Alert",false));
		insertSQL = AppTools.updateStringTagWithValue(insertSQL,"[[BODY]]", AppJDBC.prepObjectValueForSQL("string",alerts.toString(),false));
		insertSQL = AppTools.updateStringTagWithValue(insertSQL,"[[SENDTO]]", AppJDBC.prepObjectValueForSQL("string",sendTo,false));
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		Calendar cal = Calendar.getInstance();
		AppLog.logActivity(appProps,"Current Date for notification_outgoing table" + sdf.format(cal.getTime()), true, true);
		insertSQL = AppTools.updateStringTagWithValue(insertSQL,"[[ErrRetryExpDT]]", AppJDBC.prepObjectValueForSQL("datetime",sdf.format(cal.getTime()),false));
		
		try {
			//-- Execute the SQL in the jdbcdataSource
			return jdbcDataSource.executeStatement(insertSQL, appProps.getDbSQLTimeout());
			
		} catch (Exception e){
			AppLog.logActivity(appProps,"SQL=" + insertSQL, true, true);
			throw e;
		}
		
	} catch (Exception e2) {
		throw e2;
	}
	
	}
	/****************************************************************************************************************
	 * Scan Business Rules to check for any matches that meet the threshold
	 * See config.properties for detailed explanation of Business Rule parameters
	 * @param appProps
	 * @param jdbcDataSource
	 * @param jdbcDataSource2
	 * @throws Exception
	 */
public void processBusinessRules(SageFTPFileMonitorAppProperties appProps, AppJDBC jdbcDataSource, AppJDBC jdbcDataSource2) throws Exception {
	
	try {
		
		
		// --- Read all the business rule records into a resultset
		//-- Read the SQL statement template from the app properties bean into a local variable.
		//-- SQL used to retrieve all business rule records from the business rules table 
		String selectSQL = appProps.getSqlSelectBusinessRuleData();
		
		//-- Read the Alert message template into a local variable
		String alertMsgTemplate = appProps.getAlertMsgTemplate(); 	

		//-- Read the Warning message template into a local variable
		String warningMsgTemplate = appProps.getWarningMsgTemplate(); 	
				

		//-- Read the UpdateSQL Statement from the config parameters
		//-- SQL for updating Business Rule Index row with Notification Alert Flags
		String updateSQL = appProps.getSqlUpdateBusinessRuleIndex();
		
		SimpleDateFormat logsdf = new SimpleDateFormat(AppConstant.LOG_DATETIME_FORMAT);
		
		try {
			//-- Get the result set for ALL the business rule records in the business rules table
			Statement statement = jdbcDataSource.dbConnection.createStatement();
	  	  	//-- Set query timeout
	  		statement.setQueryTimeout(appProps.getDbSQLTimeout());	
	  		rs1 = statement.executeQuery(selectSQL);
			
			//-- set up a calendar object for last alert date/time
			Calendar lastAlertDateTime_cal = Calendar.getInstance();
	  		
			//-- Loop through the business rules result set
			Integer recNo = 0;	
			while (rs1.next()) {
				
				AppLog.logActivity(appProps,"Start processing business rule record number " + (recNo+1) , true, true);
				
				String status			= rs1.getString(appProps.getStatus_ColNum());
				if (status.isEmpty()) {
					throw new Exception("Status field in record# " + (recNo+1) + "empty. Business Rule Record Processing was aborted.");
				}
				if (status.equalsIgnoreCase("ACTIVE")) {
					
					// -- Read all the business rule record field values into respective local variables
					String brIndexID		= rs1.getString(appProps.getBusinessRuleIndexID_ColNum()); // -- Business Rule Index ID
					String fileNamePattern 	= rs1.getString(appProps.getFileName_Pattern_ColNum()); // -- name of the file/pattern to search
					String fileNameMode 	= rs1.getString(appProps.getFileNameMode_ColNum()); // -- P = Pattern , F = FileName
					String fileFrequency 	= rs1.getString(appProps.getFileFrequency_ColNum()); // -- M = Monthly, W = Weekly, D = Daily
					int maxAlerts 			= rs1.getInt(appProps.getMaxAlerts_ColNum()); // -- Maximum alerts that can be sent for given exception
					int alertCount 			= rs1.getInt(appProps.getAlertCount_ColNum()); // -- Number of alerts sent so far
					int alertInterval		= rs1.getInt(appProps.getAlertInterval_ColNum()); // -- Minimum Alert interval in minutes
					String alertSent		= rs1.getString(appProps.getAlertSent_ColNum()); // -- Was alert sent (Y/N)		
					String sendTo			= rs1.getString(appProps.getSendTo_ColNum()); // -- recipients name(s) if alert needs to be sent
					
					//-- Set up the last alert calendar object based on the db value
					if (rs1.getTimestamp(appProps.getLastAlertDate_ColNum()) != null) {
						java.sql.Timestamp lastAlertDateTime = rs1.getTimestamp(appProps.getLastAlertDate_ColNum());
						lastAlertDateTime_cal.setTime(lastAlertDateTime);						
					}
					else {
						//-- No value was found in the db record, so set the lastAlertDateTime_cal to the previous year to simulate that last alert was never sent
						Calendar currDate_Cal = Calendar.getInstance();
						lastAlertDateTime_cal.set(Calendar.YEAR, currDate_Cal.get(Calendar.YEAR)-1);
					}
					
					//-- Create Planned DateTime Calendar Object
					Calendar dateTimePlanned = Calendar.getInstance();
					//-- Create Threshold DateTime Calendar Object
					Calendar dateTimeThreshold = Calendar.getInstance();
					//-- Create current DateTime Calendar Object
					Calendar dateTimeCurr = Calendar.getInstance();
					
					//-- Read the planned- and threshold day into local string variables
					int planned_Day_int			= rs1.getInt(appProps.getPlannedDay_ColNum()); // -- earliest day when file is expected to be updated
					int threshold_Day_int		= rs1.getInt(appProps.getThresholdDay_ColNum()); // -- latest day when file is expected to be updated

					//-- Read the planned- and threshold time into local string variables
					String planned_Time_str		= rs1.getString(appProps.getPlannedTime_ColNum()); // -- earliest time when file is expected to be updated
					String threshold_Time_str	= rs1.getString(appProps.getThresholdTime_ColNum()); // -- latest time when file is expected to be updated
					
					//-- Set the Time format from the db time format constant
					SimpleDateFormat timeFormat = new SimpleDateFormat(AppConstant.DB_TIME_FORMAT);
					timeFormat.setLenient(true);
						
					//-- Create the Calendar objects from the string variables
					//=======================================================

					//-- Calendar Object for planned time of file creation/update
					if (planned_Time_str != null) {
						dateTimePlanned.setTime(timeFormat.parse(planned_Time_str));
					}
					else {
						//-- No planned Time is set in the business rule db record, so default it the beginning of the day
						dateTimePlanned.set(Calendar.HOUR_OF_DAY, 0);
						dateTimePlanned.set(Calendar.MINUTE, 0);
						dateTimePlanned.set(Calendar.SECOND, 0);
					}
					
					//-- Calendar Object for threshold time of file creation/update
					if (threshold_Time_str != null) {
						dateTimeThreshold.setTime(timeFormat.parse(threshold_Time_str));
					}
					else {
						//-- No threshold time is set in the business rule db record, so default it the end of the day
						dateTimeThreshold.set(Calendar.HOUR_OF_DAY, 23);
						dateTimeThreshold.set(Calendar.MINUTE, 59);
						dateTimeThreshold.set(Calendar.SECOND, 59);
					}


					//-- Calendar Object for planned day of file creation/update
					if (planned_Day_int > 0) {
						//-- Set the current Year/Month and planned day in the current month (any time-of-day values previously set are retained)
						dateTimePlanned.set(dateTimeCurr.get(Calendar.YEAR), dateTimeCurr.get(Calendar.MONTH), planned_Day_int);
					}
					else {
						//-- No Planned Day was set, so use current date 
						dateTimePlanned.set(dateTimeCurr.get(Calendar.YEAR), dateTimeCurr.get(Calendar.MONTH), dateTimeCurr.get(Calendar.DAY_OF_MONTH));
					}

					//-- Calendar Object for threshold day of file creation/update
					if (threshold_Day_int > 0) {
						//-- Set the current Year/Month and threshold day in the current month (any time-of-day values previously set are retained)
						dateTimeThreshold.set(dateTimeCurr.get(Calendar.YEAR), dateTimeCurr.get(Calendar.MONTH), threshold_Day_int);
					}
					else {
						//-- No Threshold Day was set, so use current date
						dateTimeThreshold.set(dateTimeCurr.get(Calendar.YEAR), dateTimeCurr.get(Calendar.MONTH), dateTimeCurr.get(Calendar.DAY_OF_MONTH));
					}
					
					
					//* ############ FOR DEBUGGING  ##################################
					AppLog.logActivity(appProps,"Planned Day-of-Month, Month-Of-Year, Hour,Min,Sec after update: "+ 
							dateTimePlanned.get(Calendar.YEAR) + " , " +
							dateTimePlanned.get(Calendar.DAY_OF_MONTH) + " , " + 
							(dateTimePlanned.get(Calendar.MONTH)+1) + " , " + 
							dateTimePlanned.get(Calendar.HOUR_OF_DAY)  + " , " +
							dateTimePlanned.get(Calendar.MINUTE)   + " , " +
							dateTimePlanned.get(Calendar.SECOND)  , true, true);
					AppLog.logActivity(appProps,"Threshold Day-of-Month, Month-Of-Year, Hour,Min,Sec after update: "+ 
							dateTimeThreshold.get(Calendar.YEAR) + " , " +
							dateTimeThreshold.get(Calendar.DAY_OF_MONTH) + " , " + 
							(dateTimeThreshold.get(Calendar.MONTH)+1) + " , " + 
							dateTimeThreshold.get(Calendar.HOUR_OF_DAY)  + " , " +
							dateTimeThreshold.get(Calendar.MINUTE)   + " , " +
							dateTimeThreshold.get(Calendar.SECOND)  , true, true);
					//*****************************************************************/
					
					if (fileFrequency.equalsIgnoreCase("M") ||fileFrequency.equalsIgnoreCase("W")) { // -- Monthly or Weekly File Frequency
						
						if (fileFrequency.equalsIgnoreCase("M")) {
							//-- set actual current month's planned Date based on day-of-month planned value from business rule 
							//AppLog.logActivity(appProps,"Planned day of Month, MonthOfYear,Year before update: "+ dateTimePlanned.get(Calendar.DAY_OF_MONTH) + " , " + (dateTimePlanned.get(Calendar.MONTH)+1) + " , " + dateTimePlanned.get(Calendar.YEAR), true, true);
							dateTimePlanned.set(Calendar.DAY_OF_MONTH, planned_Day_int);
							//AppLog.logActivity(appProps,"Planned day of Month, MonthOfYear,Year after update: "+ dateTimePlanned.get(Calendar.DAY_OF_MONTH) + " , " + (dateTimePlanned.get(Calendar.MONTH)+1) + " , " + dateTimePlanned.get(Calendar.YEAR), true, true);
							//-- set actual current mopnth's threshold Date based on day-of-month threshold value from business rule
							dateTimeThreshold.set(Calendar.DAY_OF_MONTH, threshold_Day_int);
							//AppLog.logActivity(appProps,"Threshold day of Month, MonthOfYear,Year after update: "+ dateTimeThreshold.get(Calendar.DAY_OF_MONTH) + " , " + (dateTimeThreshold.get(Calendar.MONTH)+1) + " , " + dateTimePlanned.get(Calendar.YEAR), true, true);
							//-- reset the alertcount counter and alertSent flag, if the last alert sent was before today
							
							//-- Reset the alert counter if the last alert date was older than this current month
							int lad_m = lastAlertDateTime_cal.get(Calendar.MONTH)+1;
							int lad_y = lastAlertDateTime_cal.get(Calendar.YEAR);
							int cdt_m = dateTimeCurr.get(Calendar.MONTH)+1;
							int cdt_y = dateTimeCurr.get(Calendar.YEAR);
							
							if (lad_m < cdt_m || lad_y < cdt_y) {
								alertCount = 0;
								alertSent = "N";
							}
						} else  {
							//-- set actual current week's planned Date based on day-of-week planned value from business rule
							dateTimePlanned.set(Calendar.DAY_OF_WEEK, planned_Day_int);
							//-- set actual current week's threshold Date based on day-of-week threshold value from business rule
							dateTimeThreshold.set(Calendar.DAY_OF_WEEK, threshold_Day_int);
							
							//-- Reset the alert counter if the last alert date was older than this current month
							int lad_y = lastAlertDateTime_cal.get(Calendar.YEAR);
							int cdt_y = dateTimeCurr.get(Calendar.YEAR);
							Calendar alertExpired_cal = Calendar.getInstance();
							alertExpired_cal.setTime(lastAlertDateTime_cal.getTime());
							alertExpired_cal.add(Calendar.DAY_OF_MONTH, -7);
							
							if (lastAlertDateTime_cal.before(alertExpired_cal)|| lad_y < cdt_y) {
								alertCount = 0;
								alertSent = "N";
							}
						}
					
					} else { //-- Daily File Frequency
						
						// -- Set the day/month/year to the current planned one since this is a daily schedule
						dateTimePlanned.set(Calendar.YEAR, dateTimeCurr.get(Calendar.YEAR));
						dateTimePlanned.set(Calendar.DAY_OF_MONTH, dateTimeCurr.get(Calendar.DAY_OF_MONTH));
						dateTimePlanned.set(Calendar.MONTH, dateTimeCurr.get(Calendar.MONTH));
						
						//-- Set the day/month/year to the current threshold one since this is a daily schedule
						dateTimeThreshold.set(Calendar.YEAR, dateTimeCurr.get(Calendar.YEAR));
						dateTimeThreshold.set(Calendar.DAY_OF_MONTH, dateTimeCurr.get(Calendar.DAY_OF_MONTH));
						dateTimeThreshold.set(Calendar.MONTH, dateTimeCurr.get(Calendar.MONTH));
						
						//-- reset the alertcount counter and alertSent flag, if the last alert sent was before today
						int lad_dom = lastAlertDateTime_cal.get(Calendar.DAY_OF_MONTH);
						int lad_m = lastAlertDateTime_cal.get(Calendar.MONTH)+1;
						int lad_y = lastAlertDateTime_cal.get(Calendar.YEAR);
						int cdt_dom = dateTimeCurr.get(Calendar.DAY_OF_MONTH);
						int cdt_m = dateTimeCurr.get(Calendar.MONTH)+1;
						int cdt_y = dateTimeCurr.get(Calendar.YEAR);
						
						if (lad_dom < cdt_dom || lad_m < cdt_m || lad_y < cdt_y) {
							alertCount = 0;
							alertSent = "N";
						}
						
					} // -- end of 'if' branch for handling the M,W,D filefrequency parameter

					//-- Log Business Rule Record values
					AppLog.logActivity(appProps,"Business Rule record values: ", true, true);
					ResultSetMetaData rsmd = rs1.getMetaData();
					for (int x=1;x <= rsmd.getColumnCount();x++) {
						AppLog.logActivity(appProps,rsmd.getColumnLabel(x) + "=" + rs1.getString(x), true, true);
					}
					
					// -- Construct SQL based on fileName Mode
					if (fileNameMode.equalsIgnoreCase("F")) {
						//-- Fixed File Name Mode
						selectSQL = appProps.getSqlSelectByFileName();
					} else {
						//-- Patterned File Name Mode
						selectSQL = appProps.getSqlSelectByFilePattern();
					}
					selectSQL = AppTools.updateStringTagWithValue(selectSQL,"[[FileName_Pattern]]", AppJDBC.prepObjectValueForSQL("string", fileNamePattern,false));
					AppLog.logActivity(appProps,"SQL for retrieving matching file catalog records:\n"+selectSQL, true, true);
					
					try {
					
						//-- Get the resultset of any matching records in the File Catalog Table
						rs2 = jdbcDataSource.getResultSet(selectSQL,appProps.getDbSQLTimeout());
					
						//-- get the first record of the result set
						if (rs2.next()) {
						
							//-- extract the file name from the current row of the resultset
							String fileName	= rs2.getString(appProps.getFileName_ColNum()); // -- name of the file/pattern to search

							//-- Read the last modified date and set up a corresponding calendar object
							Calendar createDateTime_cal = Calendar.getInstance();
							java.sql.Timestamp createDateTime = rs2.getTimestamp(appProps.getLastMod_Date_ColNum());
							createDateTime_cal.setTime(createDateTime);
							
							if (dateTimePlanned.after(dateTimeCurr)) {
								//-- the current datetime is earlier than the planned datetime of this period.
								
								if (alertCount != 0) {
									//-- Reset the alertcount and alertsent flag in the business rule index record
									AppLog.logActivity(appProps,"Current time is past the planned file creation time stamp of "+ logsdf.format(dateTimePlanned.getTime()) , true, true);
									alertCount = 0;
									alertSent = "N";
									updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[alertCount]]",AppJDBC.prepObjectValueForSQL("integer",alertCount,false));
									updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[alertSent]]",AppJDBC.prepObjectValueForSQL("string",alertSent,false));
									//-- set the lastAlertDate to null
									updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[LastAlertDate]]", 
											SageFTPFileCatalogerAppJDBC.prepObjectValueForSQL("datetime",null,false,appProps.getDbFormat_DateTime(),appProps.getDbFormat_DateTime().toString()));
									//-- Set the business rule ID to be updated
									updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[businessRule_Id]]", 	AppJDBC.prepObjectValueForSQL("string"	,brIndexID,false));
									jdbcDataSource.executeStatement(updateSQL,appProps.getDbSQLTimeout());
								
								}
							}	
							else if (dateTimeThreshold.before(dateTimeCurr)) {
								
								try {
								/*
								-- current datetime is past the threshold so check if last-mod date is older than the original planned day
								-- if so, it implies that no new file has come in since that date
								 */
								AppLog.logActivity(appProps,"Current time is past the file creation threshold time stamp of " + 
										logsdf.format(dateTimeThreshold.getTime()) + "\n" +
										" Business Rules indicates that the file catalog should now be checked to determine if file was created as expected...", true, true);
																							
								//-- Check if last modified file date is before the planned date 
								//(This indicates that it is an older version and the latest version , scheduled to be created after the planned date was not found)
								if (createDateTime_cal.before(dateTimePlanned) && alertCount <= maxAlerts) {
									
									if (isAlertDue(lastAlertDateTime_cal,alertInterval)) {
										AppLog.logActivity(appProps,"File: " + fileName + 
												" is an older version than the current planned file because the actual file creation time stamp ("+ 
												logsdf.format(createDateTime_cal.getTime()) + 
												") is older than the current planned file creation time stamp (" +
												logsdf.format(dateTimePlanned.getTime()), true, true);
									
										//-- file has not been updated since before planned date
										//-- Prepare the alert message content
										alertMsgTemplate = AppTools.updateStringTagWithValue(alertMsgTemplate,"[[FileName]]", fileName);
										SimpleDateFormat sdf = new SimpleDateFormat(appProps.getDbFormat_DateTime());
										alertMsgTemplate = AppTools.updateStringTagWithValue(alertMsgTemplate,"[[ThresholdDate]]", sdf.format(dateTimeThreshold.getTime()));
										alertMsgTemplate = AppTools.updateStringTagWithValue(alertMsgTemplate,"[[PlannedDate]]", sdf.format(dateTimePlanned.getTime()));
										alertMsgTemplate = AppTools.updateStringTagWithValue(alertMsgTemplate,"[[CreateDate]]", sdf.format(createDateTime));
									
										//--Prepare the alert message content parameter
										ArrayList<String> lines = new ArrayList<String>();
										lines.add(alertMsgTemplate);
										lines.add("");
										lines.add("This alert was generated based on business rule ID: " + brIndexID );
										lines.add("File Name/Pattern search string: " + fileNamePattern); // -- name of the file/pattern business rule item
										lines.add("File delivery frequency (M = Monthly, W = Weekly, D = Daily): " + fileFrequency);
										/*
										 TODO 
										 ADD ONE LINE TO THE BODY OF HE ALERT MESSAGE FOR EACH BUSINESS RULE ITEM
										String fileNameMode 	= rs1.getString(appProps.getFileNameMode_ColNum()); // -- P = Pattern , F = FileName
										String fileFrequency 	= rs1.getString(appProps.getFileFrequency_ColNum()); // -- M = Monthly, W = Weekly, D = Daily
										int maxAlerts 			= rs1.getInt(appProps.getMaxAlerts_ColNum()); // -- Maximum alerts that can be sent for given exception
										int alertCount 			= rs1.getInt(appProps.getAlertCount_ColNum()); // -- Number of alerts sent so far
										int alertInterval		= rs1.getInt(appProps.getAlertInterval_ColNum()); // -- Minimum Alert interval in minutes
										String alertSent		= rs1.getString(appProps.getAlertSent_ColNum()); // -- Was alert sent (Y/N)		
										String sendTo			= rs1.getString(appProps.getSendTo_ColNum()); // -- recipients name(s) if alert needs to be sent
										*/
										
										//-- handle the event that was triggered
										AppLog.logActivity(appProps,"Sending email notification to " + sendTo, true, true);
										handleEventTrigger(appProps,jdbcDataSource2,"email",lines,sendTo);
									
										//--Increment the alertcount by 1
										alertCount++;
										//--set the alertsent value to "Y"
										alertSent = "Y";
										//--set the lastAlertDate to the current dateTime
										String lastAlertDate_str = dateTimeCurr.getTime().toString();
										//-- Update the Business Rule Record with the new values
										AppLog.logActivity(appProps,"Updating status values in Business Rule record", true, true);
										updateSQL = appProps.getSqlUpdateBusinessRuleIndex();
										updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[alertCount]]", AppJDBC.prepObjectValueForSQL("integer", alertCount,false));
										updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[alertSent]]", AppJDBC.prepObjectValueForSQL("string", alertSent,false));
										updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[lastAlertDate]]", 
												SageFTPFileCatalogerAppJDBC.prepObjectValueForSQL("datetime",lastAlertDate_str,false,appProps.getDbFormat_DateTime(),AppConstant.SFTP_FILESDATETIMEFORMAT));
										updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[businessRule_Id]]", AppJDBC.prepObjectValueForSQL("string",brIndexID,false));
										AppLog.logActivity(appProps,"Business Rule UPDATE SQL: "+updateSQL, true, true);
										jdbcDataSource.executeStatement(updateSQL,appProps.getDbSQLTimeout());
									}
									else {
										// -- Alert is not yet due be sent
										// -- Next Alert Time is lastAlertDateTime + alertInterval (minutes)
										lastAlertDateTime_cal.add(Calendar.MINUTE, alertInterval);
										// -- get the string version of the next alert time
										String next_Alert_Time = lastAlertDateTime_cal.getTime().toString();
										// -- log the message
										AppLog.logActivity(appProps,"Alert has NOT been sent because next alert is not due until "+ next_Alert_Time, true, true);
									}
								}	
								else if (createDateTime_cal.after(dateTimeThreshold) && alertCount <= maxAlerts) {
									//-- File was created/updated, but after the alert threshold was reached
									AppLog.logActivity(appProps,"File: " + fileName + 
											" was created after the defined due date because the actual file creation/mod time stamp ("+ 
											logsdf.format(createDateTime_cal.getTime()) + 
											") is after the current due date (" +
											logsdf.format(dateTimeThreshold.getTime())+")", true, true);
									if (isAlertDue(lastAlertDateTime_cal,alertInterval)) {
										//-- file was updated AFTER the threshold date
										//-- Prepare the alert message content
										warningMsgTemplate = AppTools.updateStringTagWithValue(warningMsgTemplate,"[[FileName]]", fileName);
										SimpleDateFormat sdf = new SimpleDateFormat(appProps.getDbFormat_DateTime());
										warningMsgTemplate = AppTools.updateStringTagWithValue(warningMsgTemplate,"[[ThresholdDate]]", sdf.format(dateTimeThreshold.getTime()));
										warningMsgTemplate = AppTools.updateStringTagWithValue(warningMsgTemplate,"[[PlannedDate]]", sdf.format(dateTimePlanned.getTime()));
										warningMsgTemplate = AppTools.updateStringTagWithValue(warningMsgTemplate,"[[CreateDate]]", sdf.format(createDateTime));
								
										//--Prepare the alert message content parameter
										ArrayList<String> lines = new ArrayList<String>();
										lines.add(warningMsgTemplate);
								
										//-- handle the event that was triggered
										AppLog.logActivity(appProps,"Sending email notification to " + sendTo, true, true);
										handleEventTrigger(appProps,jdbcDataSource2,"email",lines,sendTo);
								
										//--Increment the alertCount by 1
										alertCount++;
										//--set the alertsent value to "Y"
										alertSent = "Y";
										//--set the lastAlertDate to the current dateTime
										String lastAlertDate_str = dateTimeCurr.getTime().toString();
										//-- Update the Business Rule Record with the new values
										AppLog.logActivity(appProps,"Updating status values in Business Rule record", true, true);
										updateSQL = appProps.getSqlUpdateBusinessRuleIndex();
										updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[alertCount]]", AppJDBC.prepObjectValueForSQL("integer", alertCount,false));
										updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[alertSent]]", AppJDBC.prepObjectValueForSQL("string", alertSent,false));
										updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[lastAlertDate]]", 
											SageFTPFileCatalogerAppJDBC.prepObjectValueForSQL("datetime",lastAlertDate_str,false,appProps.getDbFormat_DateTime(),AppConstant.SFTP_FILESDATETIMEFORMAT));
										updateSQL = AppTools.updateStringTagWithValue(updateSQL,"[[businessRule_Id]]", AppJDBC.prepObjectValueForSQL("string",brIndexID,false));
										AppLog.logActivity(appProps,"Business Rule UPDATE SQL: "+updateSQL, true, true);
										jdbcDataSource.executeStatement(updateSQL,appProps.getDbSQLTimeout());
									}
									
									else {
										// -- Alert is not yet due be sent
										// -- Next Alert Time is lastAlertDateTime + alertInterval (minutes)
										lastAlertDateTime_cal.add(Calendar.MINUTE, alertInterval);
										// -- get the string version of the next alert time
										String next_Alert_Time = lastAlertDateTime_cal.getTime().toString();
										// -- log the message
										AppLog.logActivity(appProps,"Alert has NOT been sent because next alert is not due until "+ next_Alert_Time, true, true);
									}
								}
								else if ((createDateTime_cal.after(dateTimeThreshold) ||  createDateTime_cal.before(dateTimePlanned)) && alertCount >= maxAlerts) {
									AppLog.logActivity(appProps,"Maximum Alerts of " + maxAlerts + " has been reached. No further alerts will be sent for this exception instance", true, true);
								}
								else {
									//-- File was created as expected
									AppLog.logActivity(appProps,"Business Rule evaluation has determined that file: " + 
									fileName + " was created as expected on " + logsdf.format(createDateTime_cal.getTime()) + 
									" ahead of the deadline of " + logsdf.format(dateTimeThreshold.getTime()) + 
									" and after the earliest expected time of " + logsdf.format(dateTimePlanned.getTime()), true, true);
								}
								}
								catch (Exception e){
									AppLog.logActivity(appProps,"SQL=" + updateSQL, true, true);
									throw e;	
								}
							}
							
							else { //-- Current time is before the threshold time but after the planned time, so no action needs to be taken.
								// -- Log the notification event
								AppLog.logActivity(appProps,"Current Date-time ("+logsdf.format(dateTimeCurr.getTime())+
										") is after the planned date-time ("+logsdf.format(dateTimePlanned.getTime())+
										") and before the threshold dae-time ("+logsdf.format(dateTimeThreshold.getTime())+" so no action is required at this time", true, true);
							}
						
						} else {
							AppLog.logActivity(appProps,"WARNING: No records were found in the File Catalog table for " + fileNamePattern , true, true);
						}
					}	catch (Exception e){
						AppLog.logActivity(appProps,"SQL=" + selectSQL, true, true);
						throw e;
					}
				
				} //-- Business rule record status was skipped (Record is inactive)
				
				recNo++;
				AppLog.logActivity(appProps,"Completed processing business rule record number " + recNo , true, true);
			} // end if while loop	
			
			if (recNo == 0) {
				//-- Write a warning message to the log indicating that no records were found in the File Catalog table
				//AppLog.logActivity(appProps,appProps.getNoRecordsFoundMsg() , true, true);
				//#### TEMP STATEMENT FOR TESTING ####
				String noRecordsWarningMsg = "WARNING: No records were found in the File Catalog table"; 
				AppLog.logActivity(appProps, noRecordsWarningMsg, true, true);
				//--Prepare the alert message content parameter
				ArrayList<String> lines = new ArrayList<String>();
				lines.add(noRecordsWarningMsg);
				handleEventTrigger(appProps, jdbcDataSource, "email",lines, appProps.getExceptionEventRecipients_Default());
			}
			
			
		} catch (Exception e){
			AppLog.logActivity(appProps,"SQL=" + selectSQL, true, true);
			throw e;
		}
		
	} catch (Exception e2) {
		// --- Bubble any errors to the caller
		throw e2;
	}
	
	} // end of 'ProcessBussinessRules' Method

/****************************************************************************************************************
 * Determine if an alert is due to be sent based on when the last alert was sent and the alert frequency (measured in minutes)
 * as defined the the input parameters / app configuration
 * 
 * @param lastAlertDate_str
 * @param alertFrequency
 * @throws Exception
 */
public boolean isAlertDue(Calendar lastAlertDateTime,Integer alertFrequency) throws Exception {
	
	try {
		boolean isDue = false;
	
		//-- Create current Date Calendar Object
		Calendar dateTime_Curr = Calendar.getInstance();
	
		//-- Calculate the elapsed time (in minutes), from current time to time of last alert
		long elapsedMins = (dateTime_Curr.getTimeInMillis() - lastAlertDateTime.getTimeInMillis()) / (1000 * 60);
	
		if (elapsedMins > alertFrequency.longValue()) {
			isDue = true;
		}
		else{
			isDue = false;
		}	
		return isDue;
	}
	catch (Exception e) {
		// --- Bubble any errors to the caller
		throw e;
	}
		
} //-- end of 'isAlertDue' method

} // end of SageFTPFileMonitorApp class

