/****************************************************************************************************************
* IBM Confidential.   © Copyright IBM Corp. 2004, 2015  All Rights Reserved.
 * The source code for this program is not published or otherwise divested of its trade secrets, 
 * irrespective of what has been deposited with the U.S. Copyright office.
 */
package com.ibm.services.tools.sage.app;

/****************************************************************************************************************
 * This is used to setup constants to be used as default values throughout the application.
 * @author		Yogi Golle/Tampa/Contr/IBM (yogigol@us.ibm.com) -  IBM Global Services  -  EUS Tools and Technology Team
 * @version 	2015.04.16
 */
public final class AppConstant {
	public static final String APPLICATION_NAME = "Sage FTP Monitor App (v2015.06.18)";
	public static final String LOG_DATETIME_FORMAT = "MM/dd/yyyy hh:mm:ss:SSS aaa z";
	public static final int LOG_TEXT_MAX_LENGTH = 99999;
	public static final String TEXT_AFTER_LOG_DATETIME = "     ";
	public static final String JAVA_STACK_TRACE_LOG_HEADER = "- - - - - - - - - - - - - - - - Java Error Stack Trace - - - - - - - - - - - - - - - -";
	public static final String UI_DATE_FORMAT = "MM/dd/yyyy";
	public static final String UI_DATETIME_FORMAT = "MM/dd/yyyy hh:mm:ss a";
	public static final String UI_TIME_FORMAT = "hh:mm:ss a";
	public static final String DB_TIME_FORMAT = "HH:mm:ss";
	public static final String ALLOWABLE_DATE_FORMATS = "MM/dd/yyyy,M/dd/yyyy,MM/d/yyyy,M/d/yyyy,MM/dd/yy,M/dd/yy,MM/d/yy,M/d/yy";
	public static final String ALLOWABLE_TIME_FORMATS = "hh:mm:ss a,h:mm:ss a,h:mm a,hh:mm a";
	public static final String DEFAULT_TIME_IF_MISSING_FROM_TIMESTAMP = "00:00:00.0";
	public static final String DEFAULT_DATE_IF_MISSING_FROM_TIMESTAMP = "1901-01-01";
	//--The NIO2 Files File attributes return date time values in this format. Used for datetime formatting conversions to db datetime formatting
	public static final String FILESDATETIMEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"; 
	//--The jsch API package used for implementing file management for sftp (over SSH) uses this file datetime format
	public static final String SFTP_FILESDATETIMEFORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
}
