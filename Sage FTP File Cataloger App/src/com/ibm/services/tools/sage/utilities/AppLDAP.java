/****************************************************************************************************************
* IBM Confidential.   © Copyright IBM Corp. 2004, 2015  All Rights Reserved.
 * The source code for this program is not published or otherwise divested of its trade secrets, 
 * irrespective of what has been deposited with the U.S. Copyright office.
 */
package com.ibm.services.tools.sage.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/****************************************************************************************
 * This class is used to lookup specific Bluepages field values from the IBM Enterprise LDAP 
 * Directory using JNDI with a customizable LDAP search filter. The field values to retrieve, 
 * how the data is formatted, and the search filters used to retrieve data can all be customized. 
 * @author		Jason Wallace/Atlanta/IBM (jwalla@us.ibm.com)  -  IBM Global Services  -  EUS Tools and Technology Team
 * @version 	2015.04.16
 */
public class AppLDAP {
	private Properties ldapProperties;
	private DirContext ctx;
	private SearchControls constraints = new SearchControls();
	
	
	
	/****************************************************************************************************************
	* The class constructor accepts the passed parameters from initialization and sets up the necessary 
	* properties, directory context, and search controls that will be used for searching and retrieving data.		
	* @param	ProviderURL		The server hostname and port used to connect to the LDAP server, such as 'ldap://bluepages.ibm.com:389'.
	* @param	returnFieldArray	A String array of the fields that need to be retrieved from the LDAP directory.
	* @param	RecordDelimiter	The value used to delimit the values returned from each record, such as ','.
	* @param	ValueIfNull		The value to return for a field that does not contain a value (instead of NULL).
	* @throws	Exception		If an error occurs while processing, throw exception back to the caller to handle.
	*/
	public AppLDAP (String providerURL, String[] returnFieldArray) throws Exception{
		try{
			//-- Setup the Properties object for holding the JNDI/LDAP properties
			ldapProperties = new Properties();
			ldapProperties.put("java.naming.factory.initial","com.sun.jndi.ldap.LdapCtxFactory");
			ldapProperties.put("java.naming.provider.url",providerURL);
			ldapProperties.put("java.naming.ldap.version","2");
			ldapProperties.put("java.naming.security.authentication","none");
			ldapProperties.put("java.naming.factory.url.pkgs","com.ibm.jndi");
			ldapProperties.put("java.naming.referral","ignore");
			ldapProperties.put("java.naming.ldap.derefAliases", "never");
			//-- Setup the InitialDirContext and SearchControls
			ctx = new InitialDirContext(ldapProperties);
			constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			//-- Setup the returning attributes based on the returnFieldArray passed
			constraints.setReturningAttributes(returnFieldArray); 
		} catch (Exception e){
			throw e;
		}
	}
	
	
	
	
	/****************************************************************************************************************
	* Searches the specified LDAP directory with the passed search filter and returns a Collection of AppLDAPRecord objects 
	* representing the records returned from the search.  The values returned for each record are those that were setup by 
	* the setupStandardLDAPProperties() method after an instance of this class is intialized.	The specified delimiter 
	* and 'value if null' parameters are also used when formatting the results.
	* @param	filter		The filter used when searching the LDAP directory, such as "(&(ibmserialnumber=123456))".
	* @return	Collection 	Returns a collection of AppLDAPRecord objects representing the records returned from the search.
	* @throws	Exception	If an error occurs while processing, throw exception back to the caller to handle.
	*/
	public Collection<AppLDAPRecord> searchLDAP(String filter, String valueIfNull) throws Exception{
		//-- Setup a Collection for holding the results
		Collection<AppLDAPRecord> Records = new ArrayList<AppLDAPRecord>();	
		try {
			//-- Search the LDAP directory using the passed filter
			NamingEnumeration<?> results = null;
			results = ctx.search("ou=bluepages,o=ibm.com", filter, constraints );
			//-- Use the "hasMore" method to verify that records were retrieved
			while (results.hasMore()) {
				//-- Get the next entry in this result set (starts with the first one)
				SearchResult si = (SearchResult)results.next();
				//-- Get the attributes from the SearchResult
				Attributes attrs = si.getAttributes();				
				AppLDAPRecord ldapRecord = new AppLDAPRecord();
				Attribute attr = null;
				NamingEnumeration<?> ids = null;
			
				//-- Get the value of the ibmserialnuber
				try {
					attr = attrs.get("ibmserialnumber");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setIBMSerialNumber(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}	
				} catch (Exception e) {
					ldapRecord.setIBMSerialNumber(valueIfNull);
				}
				
				//-- Get the value of the countrycode
				try {
					attr = attrs.get("employeecountrycode");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setEmployeeCountryCode(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}
				} catch (Exception e) {
					ldapRecord.setEmployeeCountryCode(valueIfNull);
				}
				
				//-- Get the value of the commonname
				try {
					attr = attrs.get("callupname");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setCallupName(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}	
				} catch (Exception e) {
					ldapRecord.setCallupName(valueIfNull);
				}
				
				//-- Get the value of the notesname
				try {
					attr = attrs.get("notesemail");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setNotesEmail(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}	
				} catch (Exception e) {
					ldapRecord.setNotesEmail(valueIfNull);
				}
				
				//-- Get the value of the emailAddress
				try {
					attr = attrs.get("emailaddress");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setEmailAddress(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}	
				} catch (Exception e) {
					ldapRecord.setEmailAddress(valueIfNull);
				}
				
				//-- Get the value of the ismanager
				try {
					attr = attrs.get("ismanager");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setIsManager(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}	
				} catch (Exception e) {
					ldapRecord.setIsManager(valueIfNull);
				}
				
				//-- Get the value of the managerserialnumber
				try {
					attr = attrs.get("managerserialnumber");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setManagerSerialNumber(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}	
				} catch (Exception e) {
					ldapRecord.setManagerSerialNumber(valueIfNull);
				}
				
				//-- Get the value of the managercountrycode
				try {
					attr = attrs.get("managercountrycode");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setManagerCountryCode(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}	
				} catch (Exception e) {
					ldapRecord.setManagerCountryCode(valueIfNull);
				}
				
				//-- Get the value of the dept
				try {
					attr = attrs.get("dept");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setDept(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}	
				} catch (Exception e) {
					ldapRecord.setDept(valueIfNull);
				}
				
				//-- Get the value of the div
				try {
					attr = attrs.get("div");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setDiv(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}
				} catch (Exception e) {
					ldapRecord.setDiv(valueIfNull);
				}
				
				
				//-- Get the value of the workloc
				try {
					attr = attrs.get("workloc");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setWorkLoc(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}
				} catch (Exception e) {
					ldapRecord.setWorkLoc(valueIfNull);
				}				
				
				//-- Get the value of the employeetype
				try {
					attr = attrs.get("employeetype");
					ids = attrs.getIDs();
					if (ids.hasMore()) {
						ldapRecord.setEmployeeType(AppTools.newStringIfObjectStringNullOrError(attr.get(), valueIfNull).toString());
					}
				} catch (Exception e) {
					ldapRecord.setEmployeeType(valueIfNull);
				}					
									
				//-- Add the bpRecord to the collection (only if it has an IBM Serial Number)
				if (!AppTools.newStringIfObjectStringNullOrError(ldapRecord.getIBMSerialNumber(),"").equalsIgnoreCase("")) {
					Records.add(ldapRecord);				
				}
			} 
			//-- Done. Return the Array List.
			return Records;
		} 
		catch (NamingException ne) {
			throw ne;
		}		
		catch (Exception e) {
			throw e;
		}		
	}
		
	
	
	
	/****************************************************************************************************************
	 * Cleans up the ldapProperties and initialcontext
	 * @throws RuntimeException
	 */
	public void cleanup() throws RuntimeException{
		try{
			//-- Cleanup the properties
			if (ldapProperties != null){
				ldapProperties.clear();
				ldapProperties = null;
			}	
			//-- Cleanup the InitialContext
			if (ctx != null){
				ctx.close();
				ctx = null;
			}
		} catch (Exception e) { 
			throw new RuntimeException("Unable to cleanup LDAPTools: " + e.getMessage());   
		}		
	}
	


}
