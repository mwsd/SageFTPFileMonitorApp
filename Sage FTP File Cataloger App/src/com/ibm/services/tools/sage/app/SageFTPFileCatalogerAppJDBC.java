/**
 * 
 */
package com.ibm.services.tools.sage.app;
import com.ibm.services.tools.sage.utilities.AppJDBC;
import com.ibm.services.tools.sage.utilities.AppTools;

/**
 * @author yogi
 *
 */
public class SageFTPFileCatalogerAppJDBC extends AppJDBC {

	/**
	 * 
	 */
	public SageFTPFileCatalogerAppJDBC() {
		// TODO Auto-generated constructor stub
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
	public static String prepObjectValueForSQL(String type, Object obj, boolean replaceOperator, String dbFormat_DateTime, String fileFormat_DateTime)
			throws Exception{
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
					result = operator + "'" + AppTools.dateTimeStringToDBTimeStamp((String) obj,fileFormat_DateTime,dbFormat_DateTime) + "'";
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
