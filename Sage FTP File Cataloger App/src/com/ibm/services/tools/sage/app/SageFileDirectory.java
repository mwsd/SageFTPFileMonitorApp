package com.ibm.services.tools.sage.app;

//import org.apache.commons.io.*;
import org.apache.commons.io.DirectoryWalker;
import com.ibm.services.tools.sage.utilities.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;

/****************************************************************************************
 * This class is used for handling File system operations when managing files on the file system
 * The Class handles:
 * Getting a list of files in the directory structure (Method - FileList)
 * Getting a Filtered List of Files (Method - FilelistFiltered)
 * Getting File Statistics (Method - FileListStats)
 * 		   
 * @author		 @author		Yogi Golle/Tampa/Contr/IBM (yogigol@us.ibm.com) - IBM GTS Service Delivery - MWSD Tools and Technology Team
 * @version 	2015.05.21   
 */
public class SageFileDirectory extends DirectoryWalker<File> {

	// Construct new Sage File Directory Structure Object 
	public SageFileDirectory(File startDirectory) {
		 super();
	}
	
	/****************************************************************************************************************
	 * FileList builds a list of files using a start Directory (root directory) as input
	 * and drills down to all subdirectories
	 * @param startDirectory
	 * @return filelist
	 * @throws IOException
	 */
	// Method FileList return a flat list of all the files in the Sage Directory Structure
	 public List<File> FileList(File startDirectory) {
	      List<File> filelist = new ArrayList<File>();
	      try {
	    	  walk(startDirectory, filelist);
		      
	      }
	      catch (Exception IOException) {
	    	  	AppProperties appProps = new AppProperties();
	    		AppJDBC jdbcDataSource = new AppJDBC();
	    		
				String errorMessage = IOException.getMessage();
				try{
					AppLog.logActivity(appProps, "ERROR!  " + IOException.getClass() + ": " + errorMessage, true, true);
					try{
						jdbcDataSource.disconnectDataSource();
					} catch (Exception e1){
						// do nothing... this is just in case there was a connection to JDBC
					}		
					
					//-- Show the stack trace for troubleshooting
					AppLog.logActivity(appProps, AppConstant.TEXT_AFTER_LOG_DATETIME + AppConstant.JAVA_STACK_TRACE_LOG_HEADER, false, true);
					//-- Loop thru the stack trace and log each line
					for (int i=0; i < IOException.getStackTrace().length ; i++) {
						AppLog.logActivity(appProps, AppConstant.TEXT_AFTER_LOG_DATETIME + "at " + IOException.getStackTrace()[i].getClassName() + "." +
								IOException.getStackTrace()[i].getMethodName() + "(" + IOException.getStackTrace()[i].getFileName() + ":" + 
								IOException.getStackTrace()[i].getLineNumber() + ")", false, true );
					}
				}	
				catch (Exception e) {}
	      }
	      
	      return filelist;
	    }
}
