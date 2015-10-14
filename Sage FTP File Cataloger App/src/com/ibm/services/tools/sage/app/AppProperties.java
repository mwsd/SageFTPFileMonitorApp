/****************************************************************************************************************
* IBM Confidential.   © Copyright IBM Corp. 2004, 2015  All Rights Reserved.
 * The source code for this program is not published or otherwise divested of its trade secrets, 
 * irrespective of what has been deposited with the U.S. Copyright office.
 */
package com.ibm.services.tools.sage.app;


/****************************************************************************************************************
 * Standard "bean" class for holding application properties retrieved from a properties file. Uses standard getters/setters. This class
 * is meant to be used for the basic application properties, such as those associated with logging. For the custom properties associated 
 * with the application, this class should be extended in the "app" package. 
 * @author		Jason Wallace/Atlanta/IBM (jwalla@us.ibm.com)  -  IBM Global Services  -  EUS Tools and Technology Team
 * @version		2015.04.16   
 */
public class AppProperties {
	//-- Application logging properties
	private boolean appLogToFile = false;
	private String appLogDateTimeFormat = AppConstant.LOG_DATETIME_FORMAT;
	private String appLogFile;
	private String appLogNewFileFrequency;
	private int appLogMaxTextLength = AppConstant.LOG_TEXT_MAX_LENGTH;
	
	//-- Relational database connection properties
	private String dbConnectClass;
	private String dbConnectionURL;
	private int dbSQLTimeout;
	private String dbUserID;
	private String dbPassword;
	private String dbDateQualifier;
	private String dbFormat_DateTime;
	private String dbFormat_Date;
	private String dbFormat_Time;
	private String dbVersionQuery;
	private String[] dbSQLErrorCodesToIgnore;
	
	//-- LDAP properties
	private String ldapProviderURL;
	private String ldapSearchFilter;
	private String ldapFieldsToReturn;
	private String ldapValueIfNull;
	
	//-- Other custom application properties (private fields with getters/setters)
	private String textExportFileName;
	
	
	/**
	 * @return the appLogToFile
	 */
	public boolean isAppLogToFile() {
		return appLogToFile;
	}
	/**
	 * @param appLogToFile the appLogToFile to set
	 */
	public void setAppLogToFile(boolean appLogToFile) {
		this.appLogToFile = appLogToFile;
	}
	/**
	 * @return the appLogDateTimeFormat
	 */
	public String getAppLogDateTimeFormat() {
		return appLogDateTimeFormat;
	}
	/**
	 * @param appLogDateTimeFormat the appLogDateTimeFormat to set
	 */
	public void setAppLogDateTimeFormat(String appLogDateTimeFormat) {
		this.appLogDateTimeFormat = appLogDateTimeFormat;
	}
	/**
	 * @return the appLogFile
	 */
	public String getAppLogFile() {
		return appLogFile;
	}
	/**
	 * @param appLogFile the appLogFile to set
	 */
	public void setAppLogFile(String appLogFile) {
		this.appLogFile = appLogFile;
	}
	/**
	 * @return the appLogNewFileFrequency
	 */
	public String getAppLogNewFileFrequency() {
		return appLogNewFileFrequency;
	}
	/**
	 * @param appLogNewFileFrequency the appLogNewFileFrequency to set
	 */
	public void setAppLogNewFileFrequency(String appLogNewFileFrequency) {
		this.appLogNewFileFrequency = appLogNewFileFrequency;
	}
	/**
	 * @return the appLogMaxTextLength
	 */
	public int getAppLogMaxTextLength() {
		return appLogMaxTextLength;
	}
	/**
	 * @param appLogMaxTextLength the appLogMaxTextLength to set
	 */
	public void setAppLogMaxTextLength(int appLogMaxTextLength) {
		this.appLogMaxTextLength = appLogMaxTextLength;
	}
	public String getDbConnectClass() {
		return dbConnectClass;
	}
	public void setDbConnectClass(String dbConnectClass) {
		this.dbConnectClass = dbConnectClass;
	}
	public String getDbConnectionURL() {
		return dbConnectionURL;
	}
	public void setDbConnectionURL(String dbConnectionURL) {
		this.dbConnectionURL = dbConnectionURL;
	}
	public int getDbSQLTimeout() {
		return dbSQLTimeout;
	}
	public void setDbSQLTimeout(int dbSQLTimeout) {
		this.dbSQLTimeout = dbSQLTimeout;
	}
	public String getDbUserID() {
		return dbUserID;
	}
	public void setDbUserID(String dbUserID) {
		this.dbUserID = dbUserID;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public String getDbDateQualifier() {
		return dbDateQualifier;
	}
	public void setDbDateQualifier(String dbDateQualifier) {
		this.dbDateQualifier = dbDateQualifier;
	}
	public String getDbFormat_DateTime() {
		return dbFormat_DateTime;
	}
	public void setDbFormat_DateTime(String dbFormat_DateTime) {
		this.dbFormat_DateTime = dbFormat_DateTime;
	}
	public String getDbFormat_Date() {
		return dbFormat_Date;
	}
	public void setDbFormat_Date(String dbFormat_Date) {
		this.dbFormat_Date = dbFormat_Date;
	}
	public String getDbFormat_Time() {
		return dbFormat_Time;
	}
	public void setDbFormat_Time(String dbFormat_Time) {
		this.dbFormat_Time = dbFormat_Time;
	}
	public String getDbVersionQuery() {
		return dbVersionQuery;
	}
	public void setDbVersionQuery(String dbVersionQuery) {
		this.dbVersionQuery = dbVersionQuery;
	}
	public String[] getDbSQLErrorCodesToIgnore() {
		return dbSQLErrorCodesToIgnore;
	}
	public void setDbSQLErrorCodesToIgnore(String[] dbSQLErrorCodesToIgnore) {
		this.dbSQLErrorCodesToIgnore = dbSQLErrorCodesToIgnore;
	}
	public String getLdapProviderURL() {
		return ldapProviderURL;
	}
	public void setLdapProviderURL(String ldapProviderURL) {
		this.ldapProviderURL = ldapProviderURL;
	}
	public String getLdapSearchFilter() {
		return ldapSearchFilter;
	}
	public void setLdapSearchFilter(String ldapSearchFilter) {
		this.ldapSearchFilter = ldapSearchFilter;
	}
	public String getLdapFieldsToReturn() {
		return ldapFieldsToReturn;
	}
	public void setLdapFieldsToReturn(String ldapFieldsToReturn) {
		this.ldapFieldsToReturn = ldapFieldsToReturn;
	}
	public String getLdapValueIfNull() {
		return ldapValueIfNull;
	}
	public void setLdapValueIfNull(String ldapValueIfNull) {
		this.ldapValueIfNull = ldapValueIfNull;
	}
	public String getTextExportFileName() {
		return textExportFileName;
	}
	public void setTextExportFileName(String textExportFileName) {
		this.textExportFileName = textExportFileName;
	}
	
}
