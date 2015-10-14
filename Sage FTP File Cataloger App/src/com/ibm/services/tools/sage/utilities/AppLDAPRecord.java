/****************************************************************************************************************
* IBM Confidential.   © Copyright IBM Corp. 2004, 2015  All Rights Reserved.
 * The source code for this program is not published or otherwise divested of its trade secrets, 
 * irrespective of what has been deposited with the U.S. Copyright office.
 */
package com.ibm.services.tools.sage.utilities;


/****************************************************************************************************************
 * The data model used for storing LDAP/user related data used throughout the application.
 * @author		Jason Wallace/Atlanta/IBM (jwalla@us.ibm.com)  -  IBM Global Services  -  EUS Tools and Technology Team
 * @version		2015.04.16
 */
public class AppLDAPRecord {
	
	private String IBMSerialNumber;
	private String employeeCountryCode;
	private String callupName;
	private String notesEmail;
	private String emailAddress;
	private String isManager;
	private String managerSerialNumber;
	private String managerCountryCode;
	private String managerCallupName;
	private String managerNotesEmail;
	private String managerEmailAddress;
	private String dept;
	private String div;
	private String workLoc;
	private String employeeType;
	
	
	/****************************************************************************************************************
	 * This is the getter method for the uniqueID
	 * @return the uniqueID
	 */
	public String getUniqueID() {
		return IBMSerialNumber.trim() + employeeCountryCode.trim();
	}
	/****************************************************************************************************************
	 * This is the getter method for the callupName
	 * @return the callupName
	 */
	public String getCallupName() {
		return callupName;
	}
	/****************************************************************************************************************
	 * This is the setter method for the callupName
	 * @param callupName the callupName to set
	 */
	public void setCallupName(String callupName) {
		this.callupName = callupName;
	}
	/****************************************************************************************************************
	 * This is the getter method for the dept
	 * @return the dept
	 */
	public String getDept() {
		return dept;
	}
	/****************************************************************************************************************
	 * This is the setter method for the dept
	 * @param dept the dept to set
	 */
	public void setDept(String dept) {
		this.dept = dept;
	}
	/****************************************************************************************************************
	 * This is the getter method for the div
	 * @return the div
	 */
	public String getDiv() {
		return div;
	}
	/****************************************************************************************************************
	 * This is the setter method for the div
	 * @param div the div to set
	 */
	public void setDiv(String div) {
		this.div = div;
	}
	/****************************************************************************************************************
	 * This is the getter method for the emailAddress
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	/****************************************************************************************************************
	 * This is the setter method for the emailAddress
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	/****************************************************************************************************************
	 * This is the getter method for the employeeCountryCode
	 * @return the employeeCountryCode
	 */
	public String getEmployeeCountryCode() {
		return employeeCountryCode;
	}
	/****************************************************************************************************************
	 * This is the setter method for the employeeCountryCode
	 * @param employeeCountryCode the employeeCountryCode to set
	 */
	public void setEmployeeCountryCode(String employeeCountryCode) {
		this.employeeCountryCode = employeeCountryCode;
	}
	/****************************************************************************************************************
	 * This is the getter method for the employeeType
	 * @return the employeeType
	 */
	public String getEmployeeType() {
		return employeeType;
	}
	/****************************************************************************************************************
	 * This is the setter method for the employeeType
	 * @param employeeType the employeeType to set
	 */
	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}
	/****************************************************************************************************************
	 * This is the getter method for the iBMSerialNumber
	 * @return the iBMSerialNumber
	 */
	public String getIBMSerialNumber() {
		return IBMSerialNumber;
	}
	/****************************************************************************************************************
	 * This is the setter method for the iBMSerialNumber
	 * @param serialNumber the iBMSerialNumber to set
	 */
	public void setIBMSerialNumber(String serialNumber) {
		IBMSerialNumber = serialNumber;
	}
	/****************************************************************************************************************
	 * This is the getter method for the isManager
	 * @return the isManager
	 */
	public String getIsManager() {
		return isManager;
	}
	/****************************************************************************************************************
	 * This is the setter method for the isManager
	 * @param isManager the isManager to set
	 */
	public void setIsManager(String isManager) {
		this.isManager = isManager;
	}
	/****************************************************************************************************************
	 * This is the getter method for the managerCountryCode
	 * @return the managerCountryCode
	 */
	public String getManagerCountryCode() {
		return managerCountryCode;
	}
	/****************************************************************************************************************
	 * This is the setter method for the managerCountryCode
	 * @param managerCountryCode the managerCountryCode to set
	 */
	public void setManagerCountryCode(String managerCountryCode) {
		this.managerCountryCode = managerCountryCode;
	}
	/****************************************************************************************************************
	 * This is the getter method for the managerSerialNumber
	 * @return the managerSerialNumber
	 */
	public String getManagerSerialNumber() {
		return managerSerialNumber;
	}
	/****************************************************************************************************************
	 * This is the setter method for the managerSerialNumber
	 * @param managerSerialNumber the managerSerialNumber to set
	 */
	public void setManagerSerialNumber(String managerSerialNumber) {
		this.managerSerialNumber = managerSerialNumber;
	}
	/****************************************************************************************************************
	 * This is the getter method for the notesEmail
	 * @return the notesEmail
	 */
	public String getNotesEmail() {
		return notesEmail;
	}
	/****************************************************************************************************************
	 * This is the setter method for the notesEmail
	 * @param notesEmail the notesEmail to set
	 */
	public void setNotesEmail(String notesEmail) {
		this.notesEmail = notesEmail;
	}
	/****************************************************************************************************************
	 * This is the getter method for the workLoc
	 * @return the workLoc
	 */
	public String getWorkLoc() {
		return workLoc;
	}
	/****************************************************************************************************************
	 * This is the setter method for the workLoc
	 * @param workLoc the workLoc to set
	 */
	public void setWorkLoc(String workLoc) {
		this.workLoc = workLoc;
	}
	/****************************************************************************************************************
	 * This is the getter method for the cleanNotesName
	 * @return the cleanNotesName
	 */
	public String getCleanNotesName() {
		return AppTools.cleanupNotesName(notesEmail);
	}
	public String getManagerCallupName() {
		return managerCallupName;
	}
	public void setManagerCallupName(String managerCallupName) {
		this.managerCallupName = managerCallupName;
	}
	public String getManagerEmailAddress() {
		return managerEmailAddress;
	}
	public void setManagerEmailAddress(String managerEmailAddress) {
		this.managerEmailAddress = managerEmailAddress;
	}
	public String getManagerNotesEmail() {
		return managerNotesEmail;
	}
	public void setManagerNotesEmail(String managerNotesEmail) {
		this.managerNotesEmail = managerNotesEmail;
	}
	public String getCleanManagerNotesName() {
		return AppTools.cleanupNotesName(managerNotesEmail);
	}
		
}
