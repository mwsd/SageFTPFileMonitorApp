/****************************************************************************************************************
* IBM Confidential.   © Copyright IBM Corp. 2004, 2015  All Rights Reserved.
 * The source code for this program is not published or otherwise divested of its trade secrets, 
 * irrespective of what has been deposited with the U.S. Copyright office.
 */
package com.ibm.services.tools.sage.utilities;
import java.math.BigDecimal;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.services.tools.sage.app.AppConstant;

/****************************************************************************************************************
 * This class is a simple, generic utilities class that is made up of simple, static methods that can be used
 * by any application that may need them.
 * @author		Jason Wallace/Atlanta/IBM (jwalla@us.ibm.com)  -  IBM Global Services  -  EUS Tools and Technology Team
 * @version 	2015.04.16
 */
public class AppTools {

	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theDate
	 * @param numDays
	 * @return Date
	 */
	public static Date addDaysToDate(Date theDate, int numDays){
		try {
			Calendar c1 = Calendar.getInstance(); 
			c1.setTime(theDate);
			c1.add(Calendar.DATE, numDays);
			return c1.getTime();
		} catch (Exception e){
        	throw new RuntimeException("Unable to add day(s) to the passed date (AppTools.addDaysToDate): " + e.getMessage());
        }		
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theDate
	 * @param numYears
	 * @return Date
	 */
	public static Date addYearsToDate(Date theDate, int numYears){
		try {
			Calendar c1 = Calendar.getInstance(); 
			c1.setTime(theDate);
			c1.add(Calendar.YEAR, numYears);
			return c1.getTime();
		} catch (Exception e){
        	throw new RuntimeException("Unable to add years(s) to the passed date (AppTools.addDaysToDate): " + e.getMessage());
        }		
	}
	
	
	
	/****************************************************************************************************************
	 * Checks the stringToCheck to see if any of the arrayOfValues entries exist anywhere within the stringToCheck as
	 * a substring.
	 * @param arrayOfValues
	 * @param stringToCheck
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean anyValueInStringArrayExistsInString(String[] arrayOfValues, String stringToCheck) throws Exception{
		try{
			boolean atLeastOneExists = false;
			String tmpArrayEntry;
			stringToCheck = stringToCheck.toLowerCase();
			if (arrayOfValues!=null){
				//-- Loop through array and check each value for the string
				for (int i =0; i < arrayOfValues.length ; i++) {
					tmpArrayEntry = arrayOfValues[i].toLowerCase();
					if (stringToCheck.indexOf(tmpArrayEntry)>=0){
						//-- If string exists, set variable to true, then break out of loop
						atLeastOneExists = true;						
						break;
					}
				}
			}		
			return atLeastOneExists;		
		} catch (Exception e){
			throw e;
		}		
	}
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param inDate
	 * @param dateFormat
	 * @return boolean
	 */
	public static boolean checkDate(String inDate, String dateFormat) { 
	    int dateFormatLength = dateFormat.length(); 
	    try { 
	      if (inDate.length() != dateFormatLength) 
	        throw new Exception(); 
	      else { 
	        SimpleDateFormat format = new SimpleDateFormat(dateFormat); 
	        format.setLenient(false); 
	        format.parse(inDate); 
	        return true; 
	      } 
	    } 
	    catch(Exception e) { 
	      return false; 
	    } 
	  }
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param inDateTime
	 * @return boolean
	 */
	public static boolean checkDateTime(String inDateTime) { 
		try{
			//-- Check for a space in the string (if none then error)
			if (inDateTime.indexOf(AppTools.cleanForRegex(" "))<2) {
				throw new Exception(); 
			}
			//-- Split the string by first space (only once)
			String[] values = inDateTime.split(AppTools.cleanForRegex(" "),2);
			if (values.length<1){
				throw new Exception();
			}
			String theDate = values[0];
			String theTime = values[1];
			//-- Check the date and time
			if (  (AppTools.isDate(theDate)) && (AppTools.isTime(theTime))  ) {
				return true;
			} else {
				return false;
			}				
		}
	    catch(Exception e) { 
	      return false; 
	    } 
	  }
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param inTime
	 * @param timeFormat
	 * @return boolean
	 */
	public static boolean checkTime(String inTime, String timeFormat) { 
	    int timeFormatLength = timeFormat.length(); 
	    try { 
	      if (inTime.length() != timeFormatLength+1) //-- add one to the format length to account for "am" or "pm"
	        throw new Exception(); 
	      else { 
	        SimpleDateFormat format = new SimpleDateFormat(timeFormat); 
	        format.setLenient(false); 
	        format.parse(inTime); 
	        return true; 
	      } 
	    } 
	    catch(Exception e) { 
	      return false; 
	    } 
	  }
	
	
	
	
	/****************************************************************************************************************
	 * Replace characters having special meaning in regular expressions (regex) with their escaped 
	 * equivalents, preceded by a '\' character.  The escaped characters include: . | \ ? * + & : { } [ ] ( ) ^ $
	 * @param aRegexFragment A String that may need to be updated with escaped equivalents for regular expressions.
	 * @return String
	 */
	public static String cleanForRegex(String aRegexFragment){
		try {
			final StringBuffer result = new StringBuffer();
			//-- Setup a string character iterator
			final StringCharacterIterator iterator = new StringCharacterIterator(aRegexFragment);
			char character =  iterator.current();
			//-- Loop through all characters checking each to see if it needs replacement
			while (character != CharacterIterator.DONE ){
				if (character == '.') {
					result.append("\\.");
				}
				else if (character == '|') {
					result.append("\\|");
				}
				else if (character == '\\') {
					result.append("\\\\");
				}
				else if (character == '?') {
					result.append("\\?");
				}
				else if (character == '*') {
					result.append("\\*");
				}
				else if (character == '+') {
					result.append("\\+");
				}
				else if (character == '&') {
					result.append("\\&");
				}
				else if (character == ':') {
					result.append("\\:");
				}
				else if (character == '{') {
					result.append("\\{");
				}
				else if (character == '}') {
					result.append("\\}");
				}
				else if (character == '[') {
					result.append("\\[");
				}
				else if (character == ']') {
					result.append("\\]");
				}
				else if (character == '(') {
					result.append("\\(");
				}
				else if (character == ')') {
					result.append("\\)");
				}
				else if (character == '^') {
					result.append("\\^");
				}
				else if (character == '$') {
					result.append("\\$");
				}
				else {
					//-- The char is not a special one add it to the result as is
					result.append(character);
				}
				character = iterator.next();
			}
			return result.toString();
		} catch (Exception e){
			return aRegexFragment;
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
	 * Removes the full hierarchical Notes Name identifiers, such as 'CN=', 'OU=', and 'O='.
	 * @param notesName The full hierarchical Notes Name to cleanup.
	 * @return String
	 * @throws RuntimeException
	 */
	public static String cleanupNotesName(String notesName) throws RuntimeException{
		try{
			if (notesName != null){
				if (!AppTools.isNullOrEmpty(notesName)){
					//-- Use replaceAll to remove the identifiers by replacing with nothing
					notesName = notesName.replaceAll(AppTools.cleanForRegex("CN="), "");
					notesName = notesName.replaceAll(AppTools.cleanForRegex("OU="), "");
					notesName = notesName.replaceAll(AppTools.cleanForRegex("O="), "");		
				}
			}		
			return notesName;
		} catch (Exception e){
			throw new RuntimeException("Unable to 'cleanupNotesName': " + e.getMessage());  
		}		
	}
	
	
	
	/****************************************************************************************************************
	  * Returns the current date/time as a java.util.Date object
	  * @return	java.util.Date	Returns the current date/time value.
	  */
	public static Date currentDateTime() throws Exception{
		try{
			//-- Setup the new SimpleDateFormat, then get an instance of the Calendar
			Calendar c1 = Calendar.getInstance();
		    //-- Return the current date/time value
			return c1.getTime();
		} catch (Exception e){
			throw e;
		}
	}
	
	
	
	
	/****************************************************************************************************************
	  * Returns the current date/time in the specified format passed to it in the 'Format' parameter.
	  * @param	Format	The format of how the current date and time should be returned, such as "MM/dd/yyyy hh:mm:ss:SSS aaa"
	  * @return	String	Returns the current date/time value in the specified format.
	  */
	public static String currentDateTime(String Format) throws Exception{
		try {
			//-- Setup the new SimpleDateFormat, then get an instance of the Calendar
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(Format);
		    Calendar c1 = Calendar.getInstance();
		    //-- Return the current date/time value
			return sdf.format(c1.getTime());
		} catch (Exception e){
			throw e;
		}		
	}
	
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param dateText
	 * @param currentFormat
	 * @param newFormat
	 * @return String
	 * @throws RuntimeException
	 */
	public static String dateStringToDBDate(String dateText, String currentFormat, String newFormat) throws RuntimeException{
		Date parsedDate = new Date();
		try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat(currentFormat);
        	parsedDate = dateFormat.parse(dateText); 
        	dateFormat= new SimpleDateFormat(newFormat);
        	String dateString = dateFormat.format(parsedDate);        	
        	return dateString;
        } catch (Exception e){
        	throw new RuntimeException("Unable to convert date string to DB date (AppTools.dateStringToDBDate): " + e.getMessage());
        }
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param dateText
	 * @return String
	 * @throws RuntimeException
	 */
	public static String dateStringToDBTimeStamp(String dateText) throws RuntimeException{
		Date parsedDate = new Date();
		try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        	parsedDate = dateFormat.parse(dateText); 
        	dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        	String dateString = dateFormat.format(parsedDate);
        	return dateString+"-00.00.00";
        } catch (Exception e){
        	throw new RuntimeException("Unable to convert date to DB TimeStamp (AppTools.dateStringToDBTimeStamp): " + e.getMessage());
        }
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param dateText
	 * @param currentFormat
	 * @param newFormat
	 * @return String
	 * @throws RuntimeException
	 */
	public static String dateStringToDBTimeStamp(String dateText, String currentFormat, String newFormat) throws RuntimeException{
		Date parsedDate = new Date();
		try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat(currentFormat);
        	parsedDate = dateFormat.parse(dateText); 
        	dateFormat= new SimpleDateFormat(newFormat);
        	String dateString = dateFormat.format(parsedDate);        	
        	return dateString + " " + AppConstant.DEFAULT_TIME_IF_MISSING_FROM_TIMESTAMP;
        } catch (Exception e){
        	throw new RuntimeException("Unable to convert date to DB TimeStamp (AppTools.dateStringToDBTimeStamp): " + e.getMessage());
        }
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param dateTimeText
	 * @return String
	 * @throws RuntimeException
	 */
	public static String dateTimeStringToDBTimeStamp(String dateTimeText) throws RuntimeException{
		Date parsedDate = new Date();
		try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        	parsedDate = dateFormat.parse(dateTimeText); 
        	dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	String dateString = dateFormat.format(parsedDate);        	
        	return dateString;
        } catch (Exception e){
        	throw new RuntimeException("Unable to convert datetime to DB TimeStamp (AppTools.dateTimeStringToDBTimeStamp): " + e.getMessage());
        }
	}
	
	
	
		
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param dateTimeText
	 * @param currentFormat
	 * @param newFormat
	 * @return String
	 * @throws RuntimeException
	 */
	public static String dateTimeStringToDBTimeStamp(String dateTimeText, String currentFormat, String newFormat) throws RuntimeException{
		Date parsedDate = new Date();
		String dateString = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(currentFormat);
		try{
			parsedDate = dateFormat.parse(dateTimeText); 
	        dateFormat= new SimpleDateFormat(newFormat);
	        dateString = dateFormat.format(parsedDate);             	
	        return dateString;
		} catch (Exception e){        	
        	throw new RuntimeException("Unable to convert datetime to DB TimeStamp (AppTools.dateTimeStringToDBTimeStamp): " + e.getMessage());
        }
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theDate
	 * @return String
	 */
	public static String formatDateTimeToString(Date theDate){
		try {
			if (theDate==null){
				return null;
			}
			SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.UI_DATETIME_FORMAT);
			return sdf.format(theDate);	
		} catch (Exception e){
			throw new RuntimeException("Unable to format string from date (AppTools.formatDateToString): " + e.getMessage());
		}
		
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theDate
	 * @return String
	 */
	public static String formatDateToString(Date theDate){
		try {
			if (theDate==null){
				return null;
			}
			SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.UI_DATE_FORMAT);
			return sdf.format(theDate);	
		} catch (Exception e){
			throw new RuntimeException("Unable to format string from date (AppTools.formatDateToString): " + e.getMessage());
		}
		
	}
	
	
	
	
	/****************************************************************************************************************
	 * Takes a number of seconds and translates it into HH:MM:SS format for presentation.
	 * @param secsIn The number of seconds to format.
	 * @return String 
	 * @throws RuntimeException
	 */
	public static String formatIntoHHMMSS(int secsIn) throws RuntimeException{		
		try{
			//-- Get hours
			int hours = secsIn / 3600;
			long remainder = secsIn % 3600;
			//-- Get minutes
			long minutes = remainder / 60;
			//-- Get seconds
			long seconds = remainder % 60;	
			//-- Concatanate the values into the HH:MM:SS format and return
			return ( (hours < 10 ? "0" : "") + hours
			+ ":" + (minutes < 10 ? "0" : "") + minutes
			+ ":" + (seconds< 10 ? "0" : "") + seconds );		
		} catch (Exception e){
			throw new RuntimeException("Unable to 'formatIntoHHMMSS': " + e.getMessage());  
		}
	}
	
	
	

		
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theDate
	 * @return Date
	 */
	public static Date formatStringToDate(String theDate){
		return AppTools.formatStringToDate(theDate, AppConstant.UI_DATE_FORMAT);
	}


	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theDate
	 * @param theFormat
	 * @return Date
	 */
	public static Date formatStringToDate(String theDate, String theFormat){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(theFormat);
			sdf.setLenient(false); 
			return sdf.parse(theDate);	
		} catch (Exception e){
			throw new RuntimeException("Unable to format date from string (AppTools.formatStringToDate): " + e.getMessage());
		}		
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theDate
	 * @return Date
	 */
	public static Date formatStringToDateTime(String theDate){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.UI_DATETIME_FORMAT);
			sdf.setLenient(false); 
			return sdf.parse(theDate);	
		} catch (Exception e){
			throw new RuntimeException("Unable to format date/time from string (AppTools.formatStringToDateTime): " + e.getMessage());
		}		
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theTime
	 * @return Date
	 */
	public static Date formatStringToTimeForDB(String theTime){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DB_TIME_FORMAT);
			sdf.setLenient(true); 
			return sdf.parse(theTime);	
		} catch (Exception e){
			throw new RuntimeException("Unable to format time from string (AppTools.formatStringToTimeForDB): " + e.getMessage());
		}		
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theTime
	 * @return Date
	 */
	public static Date formatStringToTimeForUI(String theTime){
		try{
			String timeFormat = AppConstant.UI_TIME_FORMAT;
			if (AppTools.isTime(theTime)) {
				timeFormat = AppTools.getTimeFormat(theTime);
			}
			SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
			sdf.setLenient(true); 
			return sdf.parse(theTime);	
		} catch (Exception e){
			throw new RuntimeException("Unable to format time from string (AppTools.formatStringToTimeForUI): " + e.getMessage());
		}		
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theTime
	 * @return String
	 */
	public static String formatTimeToStringForDB(Date theTime){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DB_TIME_FORMAT);
			return sdf.format(theTime);	
		} catch (Exception e){
			throw new RuntimeException("Unable to format string from time (AppTools.formatTimeToString): " + e.getMessage());
		}
		
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theTime
	 * @return String
	 */
	public static String formatTimeToStringForUI(Date theTime){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.UI_TIME_FORMAT);
			return sdf.format(theTime);	
		} catch (Exception e){
			throw new RuntimeException("Unable to format string from time (AppTools.formatTimeToString): " + e.getMessage());
		}
		
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param delimiter
	 * @param x
	 * @return String
	 */
	public static String getAdjustedMonthAndYearFromCurrentMonth(String delimiter, int x){
		String result = "mm" + delimiter + "yyyy";
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.MONTH,x);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		result = Integer.toString((month+1)) + delimiter + Integer.toString((year));  //(Add one to month to get 1->12 instead of 0->11
		return result;		
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param x
	 * @return int
	 */
	public static int getAdjustedMonthIntFromCurrent(int x){
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.MONTH,x);	
		int month = cal.get(Calendar.MONTH); 
		return month+1;  //(Add one to get 1->12 instead of 0->11 for month
	}
	
	
	
		
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param delimiter
	 * @param x
	 * @return String
	 */
	public static String getAdjustedQuarterAndYearFromCurrentMonth(String delimiter, int x){
		String result = "qq" + delimiter + "yyyy";
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.MONTH,x);
		if (cal.get(Calendar.MONTH)==0){// this is resolve problems with stupid month arrays being zero based in API (if zero, go back another month)
			cal.add(Calendar.MONTH,-1);
		}
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int qtr = AppTools.getQuarterNumberFromMonthNumber(month);		
		result = Integer.toString((qtr)) + delimiter + Integer.toString((year)); 
		return result;		
	}
	
	
	
	/****************************************************************************************************************
	 * Retrieves an integer property from a designated properties file, but includes error handling and linked properties file 
	 * lookups.  For instance, a property file value with "[[file:common.properties]]" means that the code should get that 
	 * property from another file, such as a common file.
	 * @param props
	 * @param fileName
	 * @param propertyName
	 * @param errorIfNotFound
	 * @param errorIfEmpty
	 * @param valueIfEmpty
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean getAppPropertyValueBoolean(Properties props, String fileName, String propertyName, boolean errorIfNotFound, 
			boolean errorIfEmpty, String valueIfEmpty) throws Exception{
		boolean theValue;
		//-- First, get a String representation of the boolean value
		try{
			String tmpValue = AppTools.getAppPropertyValueString(props, fileName, propertyName, errorIfNotFound, errorIfEmpty, valueIfEmpty);
			//-- Now try to convert the String value to boolean
				if (tmpValue.trim().equalsIgnoreCase("false")){
					theValue = false;
				} else if (tmpValue.trim().equalsIgnoreCase("true")){
					theValue = true;
				} else {
					throw new Exception("A non-boolean value was found for the '" + propertyName + "' property in the '" + fileName + "' file. Only a 'true' or 'false' value can be used.");
				}
			return theValue;
		} catch (Exception e){
			throw e;
		}
	}
	
	
	
	
	/****************************************************************************************************************
	 * Retrieves an integer property from a designated properties file, but includes error handling and linked properties file 
	 * lookups.  For instance, a property file value with "[[file:common.properties]]" means that the code should get that 
	 * property from another file, such as a common file.
	 * @param props
	 * @param fileName
	 * @param propertyName
	 * @param errorIfNotFound
	 * @param errorIfEmpty
	 * @param valueIfEmpty
	 * @return int
	 * @throws Exception
	 */
	public static int getAppPropertyValueInt(Properties props, String fileName, String propertyName, boolean errorIfNotFound, 
			boolean errorIfEmpty, String valueIfEmpty) throws Exception{
		int theValue;
		//-- First, get a String representation of the int value
		try{
			String tmpValue = AppTools.getAppPropertyValueString(props, fileName, propertyName, errorIfNotFound, errorIfEmpty, valueIfEmpty);
			//-- Now try to convert the String value to int
			try{
				theValue = Integer.parseInt(tmpValue);
			} catch (NumberFormatException nfe){
				throw new Exception("A non-numeric value was found for the '" + propertyName + "' property in the '" + fileName + "' file. Only a numeric value can be used.");
			}
			return theValue;
		} catch (Exception e){
			throw e;
		}
	}
	
	
	
	
	/****************************************************************************************************************
	 * Retrieves a String property from a designated properties file, but includes error handling and linked properties file 
	 * lookups.  For instance, a property file value with "[[file:common.properties]]" means that the code should get that 
	 * property from another file, such as a common file. Also handles encrypted values, so that a value within double braces, 
	 * such as "{{sg35drzx}}" means that the value is encrypted and this function handles the decryption.
	 * @param props
	 * @param fileName
	 * @param propertyName
	 * @param errorIfNotFound
	 * @param errorIfEmpty
	 * @param valueIfEmpty
	 * @return String
	 * @throws Exception
	 */
	public static String getAppPropertyValueString(Properties props, String fileName, String propertyName, boolean errorIfNotFound, 
			boolean errorIfEmpty, String valueIfEmpty) throws Exception{
		String tmpValue = "";
		//-- Get the property from the file and check for null, then handle any errors.. this checks to make sure the value is there
		try{
			tmpValue = props.getProperty(propertyName);
			if (tmpValue == null){
				throw new Exception ("");
			}
		} catch (Exception e) {
			if (errorIfNotFound){
				throw new Exception("Unable to locate the '" + propertyName + "' property in the '" + fileName + "' file.");
			} else {
				return valueIfEmpty;
			}
		}
		//-- Check for a value of "[[file:" in the value to see if the value should be pulled from another file
		String tmpPropFile = fileName;
		boolean isLinkedProperty = false;
		try{
			String tmpKeyword = "[file:";
			if (tmpValue.indexOf(tmpKeyword)>0){
				isLinkedProperty = true;
				tmpPropFile = tmpValue.replaceAll(AppTools.cleanForRegex("["), "");
				tmpPropFile = tmpPropFile.replaceAll(AppTools.cleanForRegex("file:"), "");
				tmpPropFile = tmpPropFile.replaceAll(AppTools.cleanForRegex("]"), "");
				tmpValue = AppTools.getLinkedPropertiesFileValue(tmpPropFile, propertyName);
				if (tmpValue == null){
					throw new Exception ("");
				}
			}			
		} catch (Exception e){
			if (errorIfNotFound){
				throw new Exception("Unable to locate the '" + propertyName + "' property in the linked '" + tmpPropFile + "' file.");
			} else {
				return valueIfEmpty;
			}
		}
		//-- Check to see if the value is encrypted.. if so, decrypt it with the standard secret key
		String encryptedValue = "";
		try{
			String tmpKeyword = "[encrypted:";
			if (tmpValue.indexOf(tmpKeyword)>0){
				encryptedValue = tmpValue.replaceAll(AppTools.cleanForRegex("["), "");
				encryptedValue = encryptedValue.replaceAll(AppTools.cleanForRegex("encrypted:"), "");
				encryptedValue = encryptedValue.replaceAll(AppTools.cleanForRegex("]"), "");
				tmpValue = AppCrypto.decrypt(encryptedValue);
			}
		} catch (Exception e){
			throw new Exception("Unable to decrypt the '" + propertyName + "' property in the '" + tmpPropFile + "' file. (Error: " + e.getMessage() + ")");
		}		
		//-- Check to make sure the property actually has a value and handle the error... add default value if needed
		try{
			if (tmpValue.trim().equalsIgnoreCase("")){
				if (errorIfEmpty){	
					if (isLinkedProperty){
						throw new Exception("An empty value was found for the required '" + propertyName + "' property in the linked '" + tmpPropFile + "' file.");
					} else {
						throw new Exception("An empty value was found for the required '" + propertyName + "' property in the '" + fileName + "' file.");
					}					
				} else {
					return valueIfEmpty;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return tmpValue;		
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param delimiter
	 * @return String
	 */
	public static String getCurrentMonthAndYear(String delimiter){
		String result = "mm" + delimiter + "yyyy";
		Calendar cal = Calendar.getInstance(); 
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		result = Integer.toString((month+1)) + delimiter + Integer.toString((year));  //(Add one to month to get 1->12 instead of 0->11
		return result;
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @return int
	 */
	public static int getCurrentMonthInt(){
		Calendar cal = Calendar.getInstance();     
		int month = cal.get(Calendar.MONTH)+1;
		return month;
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @return int
	 */
	public static int getCurrentYearInt(){
		Calendar cal = Calendar.getInstance();     
		int year = cal.get(Calendar.YEAR);
		return year;
	}
	
	
	
	

	
	
	/****************************************************************************************************************
	 * Gets a single property from a property file and returns the value. If the value is empty, return null
	 * @param fileName
	 * @param propertyName
	 * @return String
	 * @throws Exception
	 */
	public static String getLinkedPropertiesFileValue(String fileName, String propertyName) throws Exception{
		Properties props = new Properties();
		//-- Get the text file based on the fileName passed
		try{
			//-- Setup a new URL object for getting the text file from the system
			URL url = ClassLoader.getSystemResource(fileName);
			//-- Load the file by opening a stream
	    	props.load(url.openStream());
		} catch (Exception e) {
			throw new Exception("Failed to open the linked properties file (" + fileName + ")");
		}
		//-- Get the property... if empty or not there, just return null
		try{
			String result = props.getProperty(propertyName);
			return result;
		} catch (Exception e) {
			return null;
		}
	} 
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param month
	 * @return String
	 */
	public static String getMonthNameFromNumber(int month){
		month --;
		String[] monthName = {"January", "February", "March", "April", "May", "June", "July", "August", 
				"September", "October", "November", "December"}; 
		return monthName[month]; 
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @return int
	 */
	public static int getPreviousMonthInt(){
		Calendar cal = Calendar.getInstance();     
		int month = cal.get(Calendar.MONTH)+1; 
		if (month == 1){
			month = 12;
		} else {
			month --;
		}
		return month;
	}


	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @return int
	 */
	public static int getPreviousMonthsYearInt(){
		Calendar cal = Calendar.getInstance();     
		int month = cal.get(Calendar.MONTH)+1; 
		int year = cal.get(Calendar.YEAR);
		if (month == 1){
			year --;
		}
		return year;
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param month
	 * @return String
	 */
	public static String getQuarterNameFromMonthNumber(int month){
		month --;
		String[] qtrName = {"1st Quarter", "1st Quarter", "1st Quarter", "2nd Quarter", "2nd Quarter", "2nd Quarter", 
				"3rd Quarter", "3rd Quarter", "3rd Quarter", "4th Quarter", "4th Quarter", "4th Quarter"}; 
		return qtrName[month]; 
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param qtr
	 * @return String
	 */
	public static String getQuarterNameFromQuarterNumber(int qtr){
		qtr --;
		String[] qtrName = {"1st Quarter", "2nd Quarter", "3rd Quarter", "4th Quarter"}; 
		return qtrName[qtr]; 
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param month
	 * @return int
	 */
	public static int getQuarterNumberFromMonthNumber(int month){
		if (month==0){
			month=12;
		}
		month --;  //adjust to meet the list below (so month 1 = array(0))
		int[] qtrName = {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4}; 
		return qtrName[month]; 
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param timeToCheck
	 * @return String
	 */
	public static String getTimeFormat(String timeToCheck) {
		String[] timeFormats = AppConstant.ALLOWABLE_TIME_FORMATS.split(",");
		for(int i =0; i < timeFormats.length ; i++) {
			if (checkTime(timeToCheck,timeFormats[i])){
				return timeFormats[i];
			}
		}
		return null;
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param valueToCheck
	 * @return boolean
	 */
	public static boolean isBigDecimal(String valueToCheck) {
	    try { 
	      new BigDecimal(valueToCheck);
	      return true; 
	    } 
	    catch(Exception e) { 
	      return false; 
	    } 
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param dateToCheck
	 * @return boolean
	 */
	public static boolean isDate(String dateToCheck) {
		String[] dateFormats = AppConstant.ALLOWABLE_DATE_FORMATS.split(",");
		for(int i =0; i < dateFormats.length ; i++) {
			if (checkDate(dateToCheck,dateFormats[i])){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param dateToCheck
	 * @param dateFormat
	 * @return boolean
	 */
	public static boolean isDate(String dateToCheck, String dateFormat) {
		if (checkDate(dateToCheck,dateFormat)){
			return true;
		}
		return false;
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param dateTimeToCheck
	 * @return boolean
	 */
	public static boolean isDateTime(String dateTimeToCheck) {
		if (checkDateTime(dateTimeToCheck)){
			return true;
		}
		return false;
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param valueToCheck
	 * @param leftOfDecimalMaxDigits
	 * @param rightOfDecimalMaxDigits
	 * @return boolean
	 */
	public static boolean isDecimalWithinLimit(String valueToCheck, int leftOfDecimalMaxDigits, int rightOfDecimalMaxDigits){
		try { 
			if (valueToCheck==null){
				//AppTools.debugOut("=====================: NULL");
				return true;
			}
			String leftString = "";
			String rightString = "";
			if (valueToCheck.indexOf(".")>0){
				String strValues[] = valueToCheck.split(AppTools.cleanForRegex("."));
				leftString = strValues[0];
				rightString = strValues[1];
			} else {
				leftString = valueToCheck;
				rightString = "0";
			}
			//AppTools.debugOut("=====================: indexOf=" + valueToCheck.indexOf(".") + ", left=" + leftString + ", right=" + rightString);
			if (leftString.length()>leftOfDecimalMaxDigits){
				return false;
			}
			if (rightString.length()>0 && rightString.length()>rightOfDecimalMaxDigits){
				return false;
			}				
	      return true; 
	    } 
	    catch(Exception e) { 
	    	//AppTools.debugOut("===================== ERROR!!! (" + e.getMessage() + ")");
	      e.printStackTrace();
	    	return false; 
	    } 
	}
	
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param valueToCheck
	 * @return boolean
	 */
	public static boolean isDouble(String valueToCheck) {
	    try { 
	      Double.parseDouble(valueToCheck);
	      return true; 
	    } 
	    catch(Exception e) { 
	      return false; 
	    } 
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param valueToCheck
	 * @return boolean
	 */
	public static boolean isInteger(String valueToCheck) {
	    try { 
	      Integer.parseInt(valueToCheck);
	      return true; 
	    } 
	    catch(Exception e) { 
	      return false; 
	    } 
	}
	
	
	
		
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param value
	 * @return boolean
	 */
	public static boolean isNull(Object value){
		try{
			//-- Check for null object
			if (value==null) {
				return true;
			}
			return false;
		} catch (Exception e){
			return false;
		}		
	}
	
	
	
	
	/****************************************************************************************************************
	 * Very simple method that returns a boolean value for if a passed object is null or blank (for Strings).
	 * @param value Object for which the test should be made (such as a String)
	 * @return boolean
	 */
	public static boolean isNullOrEmpty(Object value){
		try{
			//-- Check for null object
			if (value==null) {
				return true;
			}
			//-- Check to see if the objects value is empty (for Strings)
			if (value.toString().trim().equalsIgnoreCase("")){
				return true;
			}
			//-- Check to see if the objects length is 0 (for String arrays)
			if (((String[]) value).length==0){
				return true;
			}
			return false;
		} catch (Exception e){
			return false;
		}		
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param timeToCheck
	 * @return boolean
	 */
	public static boolean isTime(String timeToCheck) {
		String[] timeFormats = AppConstant.ALLOWABLE_TIME_FORMATS.split(",");
		for(int i =0; i < timeFormats.length ; i++) {
			if (checkTime(timeToCheck,timeFormats[i])){
				return true;
			}
		}
		return false;
	}
	
	
	/****************************************************************************************************************
	 * Simple method to print the stack trace from a Throwable to the error console. Used instead of typical printStackTrace
	 * so that it can be customized and turned on or off from here instead of throughout the code.
	 * @param err The Throwable object containing the error details.
	 * @throws RuntimeException
	 */
	public static void logErrorToConsole(Throwable err, String errOrOut) throws RuntimeException{
		try{
			if (errOrOut.equalsIgnoreCase("err")){
				err.printStackTrace(System.err);
			} else {
				err.printStackTrace(System.out);
			}			
		} catch (Exception e){
			throw new RuntimeException("Unable to 'AppTools.logErrorToConsole': " + e.getMessage());   
		}
	}
	
	
	/****************************************************************************************************************
	 * Checks the passed String object to see if it is a zero-length string and if so, returns the new value.
	 * @param checkObject The String object the check for a string.
	 * @param newValue The new String to return if the passed String object is null or blank.
	 * @return String
	 */
	public static String newStringIfObjectStringNullOrError(Object checkObject, String newValue){
		try{
			//-- Check for null object
			if (checkObject==null) {
				return newValue;
			}
			//-- Check for ToString()value
			if (checkObject.toString().length()==0) {
				return newValue;
			}			
			return checkObject.toString();
		} catch (Exception e){
			return newValue;
		}
	}
	
	
	
	/****************************************************************************************************************
	 * Returns a new String (newvalue) if the passed one (checkvalue) is null or blank.
	 * @param checkValue The String object the check for a string.
	 * @param newValue The new String to return if the passed String object is null or blank.
	 * @return String
	 */
	public static String newValueIfStringEmpty(String checkValue, String newValue){
		try{
			//-- Check for null object
			if (isNullOrEmpty(checkValue)) {
				return newValue;
			}
			return checkValue;
		} catch (Exception e){
			return checkValue;
		}
	}
	
	
	
	/****************************************************************************************************************
	 * Returns a string with the specified number of non breaking page spaces (&nbsp;) for HTML.
	 * @param num Number of spaces to return.
	 * @return String
	 */
	public static String nonBreakingSpaces(int num){
		String result = "";
		//-- Loop for num times and append &nbsp;
		for(int i=0; i < num ; i++) {
			result = result + "&nbsp;";			
		}
		return result;
	}
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param obj1
	 * @param obj2
	 * @param type
	 * @return boolean
	 */
	public static boolean objectValuesMatch (Object obj1, Object obj2, String type){
		try {
			if (type.equalsIgnoreCase("string")){
				String objString1 = new String (AppTools.newStringIfObjectStringNullOrError(obj1, ""));
				String objString2 = new String (AppTools.newStringIfObjectStringNullOrError(obj2, ""));
				if ( objString1.equalsIgnoreCase(objString2) ){
					return true;
				}
			} else if (type.equalsIgnoreCase("date")){
				Date objDate1 = null;
				Date objDate2 = null;
				if ( AppTools.isNull(obj1) ){
					objDate1 = AppTools.formatStringToDate("01/01/0001 00:00:00");
				} else {
					objDate1 = (Date) obj1;
				}
				if ( AppTools.isNull(obj2) ){
					objDate2 = AppTools.formatStringToDate("01/01/0001 00:00:00");
				} else {
					objDate2 = (Date) obj2;
				}
				if (objDate1.compareTo(objDate2)==0){
					return true;
				}
			} else if (type.equalsIgnoreCase("time")){
				Date objTime1 = null;
				Date objTime2 = null;
				if ( AppTools.isNull(obj1) ){
					objTime1 = AppTools.formatStringToTimeForDB("00:00:01");
				} else {
					objTime1 = (Date) obj1;
				}
				if ( AppTools.isNull(obj2) ){
					objTime2 = AppTools.formatStringToTimeForDB("00:00:01");
				} else {
					objTime2 = (Date) obj2;
				}
				if (objTime1.compareTo(objTime2)==0){
					return true;
				}
			} else if (type.equalsIgnoreCase("integer")){
				Integer objInteger1 = null;
				Integer objInteger2 = null;
				if ( AppTools.isNull(obj1) ){
					objInteger1 = Integer.valueOf("0");
				} else {
					objInteger1 = new Integer(obj1.toString());
				}
				if ( AppTools.isNull(obj2) ){
					objInteger2 = Integer.valueOf("0");
				} else {
					objInteger2 = new Integer(obj2.toString());
				}
				if (objInteger1.compareTo(objInteger2)==0){
					return true;
				}
			} else if (type.equalsIgnoreCase("double")){
				Double objDouble1 = null;
				Double objDouble2 = null;
				if ( AppTools.isNull(obj1) ){
					objDouble1 = Double.valueOf("0");
				} else {
					objDouble1 = new Double(obj1.toString());
				}
				if ( AppTools.isNull(obj2) ){
					objDouble2 = Double.valueOf("0");
				} else {
					objDouble2 = new Double(obj2.toString());
				}
				if (objDouble1.compareTo(objDouble2)==0){
					return true;
				}
			} else if (type.equalsIgnoreCase("decimal")){
				BigDecimal objBigDecimal1 = null;
				BigDecimal objBigDecimal2 = null;
				if ( AppTools.isNull(obj1) ){
					objBigDecimal1 = new BigDecimal("0");
				} else {
					objBigDecimal1 = new BigDecimal(obj1.toString());
				}
				if ( AppTools.isNull(obj2) ){
					objBigDecimal2 = new BigDecimal("0");
				} else {
					objBigDecimal2 = new BigDecimal(obj2.toString());
				}
				if (objBigDecimal1.compareTo(objBigDecimal2)==0){
					return true;
				}
			}    
			return false;
		} catch (Exception e){
        	throw new RuntimeException("Unable to compare object values (AppTools.objectValuesMatch): " + e.getMessage());
        }
	}
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theArray
	 * @param value
	 * @return int
	 * @throws Exception
	 */
	public static int positionOfStringInStringArray(String[] theArray, String value) throws Exception{
		try{
			int position = -1;		
			if (theArray!=null){
				//-- Loop through array and check each value for the string
				for (int i =0; i < theArray.length ; i++) {
					if (theArray[i].equalsIgnoreCase(value)){
						//-- If string exists, get i, break loop
						position = i;						
						break;
					}
				}
			}
			return position;		
		} catch (Exception e){
			throw e;
		}		
	}
	
	
	/****************************************************************************************************************
	 * Simple method to log a string to the error console. Used instead of usual System.err.println
	 * so that it can be customized and turned on or off from here.
	 * @param text The message to log out to the error console.
	 */
	public static void printToConsole (String text, String errOrOut){
		if (errOrOut.equalsIgnoreCase("err")){
			System.err.println(text);
		} else {
			System.out.println(text);		
		}	
	}
	
	
	/****************************************************************************************************************
	 * Returns a 6-digit string that is created from using math.random() to generate a random number. 
	 * @return String Representing the new random number
	 * @throws Exception
	 */
	public static String randumNumber() throws Exception{
		try {
			//-- Build the 6-digit random number
			String RandomNumber = "00000" + (int) ( Math.random() * 1000000 );
			RandomNumber = RandomNumber.substring(((int)RandomNumber.length()-6),(int)RandomNumber.length());
			return RandomNumber;
		} catch (Exception e){
			throw e;
		}		
	}
	
	
	public static boolean regexPatternMatchesString(String stringToCheck, String pattern)  throws Exception{
		boolean theResult = false;
		try {
	
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(stringToCheck);
			theResult = m.matches();
			
			return theResult;
			
		} catch (Exception e){
			throw e;
		}
	}
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param theClass
	 * @return String
	 */
	public static String shortClassNameFromObject(Object theClass){
		return theClass.getClass().getName().replaceAll(theClass.getClass().getPackage().getName()+".", "");		
	}
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param longClassName
	 * @return String
	 */
	public static String shortClassNameFromString(String longClassName){
		if (longClassName==null){
			return longClassName;
		}
		int length = longClassName.length();
		int lastDot = longClassName.lastIndexOf(".");
		if (lastDot < 1){
			return longClassName;
		}
		return longClassName.substring(lastDot+1, length);
	}
	
	
	/****************************************************************************************************************
	 * Determines if a specific String value exists in an Array of Strings.
	 * @param theArray The String array to check for the value.
	 * @param value The value to look for within the String array.
	 * @return boolean True if String exists, otherwise false.
	 * @throws Exception
	 */
	public static boolean stringExistsInStringArray(String[] theArray, String value) throws Exception{
		try{
			boolean itExists = false;		
			if (theArray!=null){
				//-- Loop through array and check each value for the string
				for (int i =0; i < theArray.length ; i++) {
					if (theArray[i].equalsIgnoreCase(value)){
						//-- If string exists, set variable to true, then break out of loop
						itExists = true;						
						break;
					}
				}
			}		
			return itExists;		
		} catch (Exception e){
			throw e;
		}		
	}
	
	
	/****************************************************************************************************************
	 * Checks the stringToCheck to see if the value appears anywhere within.  Returns boolean.
	 * @param stringToCheck
	 * @param value
	 * @return boolean
	 */
	public static boolean stringExistsWithinString(String stringToCheck, String value){
		try{
			stringToCheck = AppTools.newStringIfObjectStringNullOrError(stringToCheck,"string is empty").toLowerCase();
			value = AppTools.newStringIfObjectStringNullOrError(value,"").toLowerCase();
			if (value.equalsIgnoreCase("")){
				return false;
			}
			if (stringToCheck.indexOf(value)>0){
				return true;
			}
		} catch (Exception e){
			return false;
		}
		return false;
	}
	
	
	
	
	/****************************************************************************************************************
	 * Checks the stringArrayToCheck to see if the 'value' passed appears as one of the values in the array. This looks
	 * for the entire 'value' to appear as the same value in the array (full value, not partial)
	 * @param stringArrayToCheck
	 * @param value
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean stringIsAFullValueInStringArray(String[] stringArrayToCheck, String value) throws Exception{
		try{
			boolean itExists = false;
			if (stringArrayToCheck!=null){
				//-- Loop through array and check each value for the string
				for (int i =0; i < stringArrayToCheck.length ; i++) {
					if (stringArrayToCheck[i].equalsIgnoreCase(value)){
						//-- If string exists, set variable to true, then break out of loop
						itExists = true;						
						break;
					}
				}
			}		
			return itExists;		
		} catch (Exception e){
			throw e;
		}		
	}
	
	
	
	
	/****************************************************************************************************************
	 * Returns a string representing the characters to the left of the first instance of a passed substring.
	 * @param initial The initial String to check.
	 * @param leftOfThis The substring to check for.
	 * @return String
	 * @throws Exception
	 */
	public static String stringLeftOfString(String initial, String leftOfThis) throws Exception{		
		try{
			//-- Find location of substring
			int location = initial.indexOf(leftOfThis);
			//-- If substring exists, get characters to the left of it
			if (location > 0){
				initial = initial.substring(0, location);
			}
			return initial;
		} catch (Exception e){
			throw e;
		}		
	} 

	
	
	
	/**
	 * @param strToCheck
	 * @param openTag
	 * @param closeTag
	 * @return String[]
	 */
	public static String[] substringsBetweenTags(String strToCheck, String openTag, String closeTag) {
        //-- Check passed values to make sure they are not empty
		if (strToCheck == null || isNullOrEmpty(openTag) || isNullOrEmpty(closeTag)) {
            return null;
        }
		//-- Check the length of the string to check... if zero, then return nothing
        int strLen = strToCheck.length();
        if (strLen == 0) {
            return new String[0];
        }
        int closeLen = closeTag.length();
        int openLen = openTag.length();
        List<String> list = new ArrayList<String>();
        int pos = 0;
        //-- Loop thru each position in the stringToCheck to locate the start and end tags
        while (pos < (strLen - closeLen)) {
            int start = strToCheck.indexOf(openTag, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            int end = strToCheck.indexOf(closeTag, start);
            if (end < 0) {
                break;
            }
            list.add(strToCheck.substring(start, end));
            pos = end + closeLen;
        }
        //-- If the list is empty, just return null, else return a String array
        if (list.isEmpty()) {
            return null;
        } 
        return (String[]) list.toArray(new String [list.size()]);
    }

	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param timeText
	 * @param currentFormat
	 * @param newFormat
	 * @return String
	 * @throws RuntimeException
	 */
	public static String timeStringToDBTime(String timeText, String currentFormat, String newFormat) throws RuntimeException{
		Date parsedDate = new Date();
		try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat(currentFormat);
        	parsedDate = dateFormat.parse(timeText); 
        	dateFormat= new SimpleDateFormat(newFormat);
        	String dateString = dateFormat.format(parsedDate);        	
        	return dateString;
        } catch (Exception e){
        	throw new RuntimeException("Unable to convert date string to DB date (AppTools.dateStringToDBDate): " + e.getMessage());
        }
	}
	
	
	
	/****************************************************************************************************************
	 * Simple static utility function.  Mostly self-explanatory based on the name.
	 * @param timeText
	 * @param currentFormat
	 * @param newFormat
	 * @return String
	 * @throws RuntimeException
	 */
	public static String timeStringToDBTimeStamp(String timeText, String currentFormat, String newFormat) throws RuntimeException{
		Date parsedDate = new Date();
		try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat(currentFormat);
        	parsedDate = dateFormat.parse(timeText); 
        	dateFormat= new SimpleDateFormat(newFormat);
        	String timeString = dateFormat.format(parsedDate);        	
        	return AppConstant.DEFAULT_DATE_IF_MISSING_FROM_TIMESTAMP + " " + timeString;
        } catch (Exception e){
        	throw new RuntimeException("Unable to convert time to DB TimeStamp (AppTools.timeStringToDBTimeStamp): " + e.getMessage());
        }
	}
	
	
	
	/****************************************************************************************************************
	  * Returns a newly created, 25-character unique ID in the format of "YearMonthDate.HourMinuteSecondMillisecond.RandomNumber" 
	  * (such as "20070404.154423987.483265") to be used for uniquely identifying a resource, such as a database record or 
	  * a process ID.
	  * @return	String	Returns a 25-character dynamic unique ID in the format of "yyyymmdd.hhmmssSSS.rrrrrr"
	  */
	public static String uniqueID() throws Exception{
		try {
			//-- Setup a String for holding the result
			String result = "";
			//-- Build the date and time for the unique ID
			String DateTime = AppTools.currentDateTime("yyyyMMdd.HHmmssSSS.");		
			result = DateTime + AppTools.randumNumber();		
			//-- Return the result
			return result;
		} catch (Exception e){
			throw e;
		}		
	}
	
	
	
	/****************************************************************************************************************
	 * Uses "replaceAll" with a cleanup for regex reserved chars to replace a field tag in a String with an actual value.
	 * @param str  String in which the tag should be replaced with a value.
	 * @param tag  The tag, such as [[name]].
	 * @param value The value to replace the tag with.
	 * @return String
	 */
	public static String updateStringTagWithValue(String str, String tag, String value) throws Exception{
		try{
			//-- Replace tag, such as [[tag]] with actual value (after string cleanup for regex special chars)
			str = str.replaceAll(AppTools.cleanForRegex(tag), AppTools.cleanForRegex(value));
			return str;	
		} catch (Exception e){
			throw e;
		}		
	}
	
	
}
