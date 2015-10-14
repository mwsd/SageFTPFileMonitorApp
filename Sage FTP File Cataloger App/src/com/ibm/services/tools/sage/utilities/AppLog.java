/****************************************************************************************************************
* IBM Confidential.   © Copyright IBM Corp. 2004, 2015  All Rights Reserved.
 * The source code for this program is not published or otherwise divested of its trade secrets, 
 * irrespective of what has been deposited with the U.S. Copyright office.
 */
package com.ibm.services.tools.sage.utilities;
import java.io.*;

import com.ibm.services.tools.sage.app.AppConstant;
import com.ibm.services.tools.sage.app.AppProperties;

/****************************************************************************************************************
 * This class is a generic class that can be used by any application for logging runtimes, activities, or step-by-step 
 * details to database log tables, file system log file and even the system console. Basically, once the log is started 
 * up, a database connection object can be passed, along with application properties to tell this class what to do. 
 * @author		Jason Wallace/Atlanta/IBM (jwalla@us.ibm.com)  -  IBM Global Services  -  EUS Tools and Technology Team
 * @version 	2015.04.16    
 */
public class AppLog {
	
	
	/****************************************************************************************************************
	* Logs the passed log text and amount to the log text file and system console. 
	* @param	textToLog		The text to log in the child activity database record, text file and system console.
	* @throws	Exception		If an error occurs while processing, throw exception back to the caller to handle.
	*/
	public static String logActivity(AppProperties appProps, String textToLog, boolean includeDateTime, boolean printToConsole) throws Exception{
		try {
			if (includeDateTime) {
				textToLog = AppTools.currentDateTime(appProps.getAppLogDateTimeFormat()) + AppConstant.TEXT_AFTER_LOG_DATETIME + textToLog;
			}
			String textForLogFile = textToLog;
			//-- Make sure the text to log is not too long
			if (textForLogFile.length()>appProps.getAppLogMaxTextLength()){
				textForLogFile = textForLogFile.substring(0,appProps.getAppLogMaxTextLength()-3) + "...";
			}
			//-- If the option to log to a text file is true, then write data to the text file
			if (appProps.isAppLogToFile()){				
				writeDataToLogFile(appProps, textForLogFile);
			}
			//-- Print to the system console (if needed)
			if (printToConsole){
				System.out.println(textToLog);
			}	
			return textToLog;
		} catch (Exception e){
			throw e;
		}		
	}
	
	
	
	/****************************************************************************************************************
	* Starts up a new FileWriter to get a handle on the existing log file (or starts a new one if one doesn't 
	* exist).  It then uses a PrintWriter object to print a new line to the file using the 'TextToLog' passed to it. The 
	* 'NewLogFileFrequency' property is used to tell this method how to name new log files based on the current date/time. 
	* @param	TextToLog	The text to write into the text file.
	* @throws	Exception	If an error occurs while processing, throw exception back to the caller to handle.
	*/
	private static void writeDataToLogFile(AppProperties appProps, String TextToLog) throws Exception{
		try {
			//-- Setup the log file name based on the 'NewLogFileFrequency'. Replace only last instance of period with date
			String LogFileName = appProps.getAppLogFile();
			if (appProps.getAppLogNewFileFrequency().equalsIgnoreCase("monthly")){
				LogFileName = LogFileName.replaceAll("\\.(?!.*\\.)",AppTools.currentDateTime("_yyyy_MM")+".");
			}
			if (appProps.getAppLogNewFileFrequency().equalsIgnoreCase("daily")){
				LogFileName = LogFileName.replaceAll("\\.(?!.*\\.)",AppTools.currentDateTime("_yyyy_MM_dd")+".");
			}
			//-- Startup a FileWriter based on the log file name (creates a new file if one isn't there)
			FileWriter outFile = new FileWriter(LogFileName,true);
			//-- Startup a PrintWriter and write the new line to the file
			PrintWriter out = new PrintWriter(outFile);
			out.println(TextToLog);
			//-- Close the file
			out.close();
		} catch (Exception e){
			throw e;
		}
	}
	
}
