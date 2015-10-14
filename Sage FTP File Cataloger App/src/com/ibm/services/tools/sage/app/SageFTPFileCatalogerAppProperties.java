/**
 * 
 */
package com.ibm.services.tools.sage.app;


/**
 * @author yogi
 *
 */
public class SageFTPFileCatalogerAppProperties extends AppProperties {

	/**
	 * Application properties specific to the SageFTPFileCataloger application
	 */
	private String fileDirectory;
	private String ftpServerName;
	private Integer ftpServerPort;
	private String ftpUserId;
	private String ftpPassword;
	private String dateModifiedFieldName;
	private String sqlSelectFileData;
	private String SqlInsertIntoFTP_File_Catalog;
	private String SqlUpdateFTP_File_Catalog;
	private String SqlUpdateStatusFTP_File_Catalog;
	private String fileSizeUnit;
	private Double fileSizeMultiplier;
	private String fileSizeUnitValidValues[] = {"byte","kbyte","mbyte","gbyte","tbyte"};
	private Double fileSizeMultiplierValidValues[] = {1.0,1024.0,1024.0*1024.0,1024.0*1024.0*1024.0,1024.0*1024.0*1024.0*1024.0};
	private String SqlInsertIntoDB_Activity;
	private Integer lastModDate_ColNum;
	private String currentFileID;
	private String SqlFetchAllRows;
	private Integer hostName_ColNum;
	private Integer filePath_ColNum;
	private Integer fileName_ColNum;
	private Integer contentHeaderSize;
	private String alertFileName;
	private String fileSystemLocation;
	private String ftpProtocol;
	private String excludeDirs;
	
	
	/**
	 * @return the alertFileName
	 */
	public String getAlertFileName() {
		return alertFileName;
	}
	/**
	 * @param alertFileName the alertFileName to set
	 */
	public void setAlertFileName(String alertFileName) {
		this.alertFileName = alertFileName;
	}
	/**
	 * @return the hostName_ColNum
	 */
	public Integer getHostName_ColNum() {
		return hostName_ColNum;
	}
	/**
	 * @param hostName_ColNum the hostName_ColNum to set
	 */
	public void setHostName_ColNum(Integer hostName_ColNum) {
		this.hostName_ColNum = hostName_ColNum;
	}
	/**
	 * @return the filePath_ColNum
	 */
	public Integer getFilePath_ColNum() {
		return filePath_ColNum;
	}
	/**
	 * @param filePath_ColNum the filePath_ColNum to set
	 */
	public void setFilePath_ColNum(Integer filePath_ColNum) {
		this.filePath_ColNum = filePath_ColNum;
	}
	/**
	 * @return the fileName_ColNum
	 */
	public Integer getFileName_ColNum() {
		return fileName_ColNum;
	}
	/**
	 * @param fileName_ColNum the fileName_ColNum to set
	 */
	public void setFileName_ColNum(Integer fileName_ColNum) {
		this.fileName_ColNum = fileName_ColNum;
	}
	/**
	 * @return the contentHeaderSize
	 */
	public Integer getContentHeaderSize() {
		return contentHeaderSize;
	}
	/**
	 * @param contentHeaderSize the contentHeaderSize to set
	 */
	public void setContentHeaderSize(Integer contentHeaderSize) {
		this.contentHeaderSize = contentHeaderSize;
	}
	/**
	 * @return the sqlUpdateStatusFTP_File_Catalog
	 */
	public String getSqlUpdateStatusFTP_File_Catalog() {
		return SqlUpdateStatusFTP_File_Catalog;
	}
	/**
	 * @param sqlUpdateStatusFTP_File_Catalog the sqlUpdateStatusFTP_File_Catalog to set
	 */
	public void setSqlUpdateStatusFTP_File_Catalog(
			String sqlUpdateStatusFTP_File_Catalog) {
		SqlUpdateStatusFTP_File_Catalog = sqlUpdateStatusFTP_File_Catalog;
	}

	/**
	 * @return the sqlFetchAllRows
	 */
	public String getSqlFetchAllRows() {
		return SqlFetchAllRows;
	}
	/**
	 * @param sqlFetchAllRows the sqlFetchAllRows to set
	 */
	public void setSqlFetchAllRows(String sqlFetchAllRows) {
		SqlFetchAllRows = sqlFetchAllRows;
	}
	/**
	 * @return the currentFileID
	 */
	public String getCurrentFileID() {
		return currentFileID;
	}
	/**
	 * @param currentFileID the currentFileID to set
	 */
	public void setCurrentFileID(String currentFileID) {
		this.currentFileID = currentFileID;
	}
	/**
	 * @return the lastModDate_ColNum
	 */
	public Integer getLastModDate_ColNum() {
		return lastModDate_ColNum;
	}
	/**
	 * @param lastModDate_ColNum the lastModDate_ColNum to set
	 */
	public void setLastModDate_ColNum(Integer lastModDate_ColNum) {
		this.lastModDate_ColNum = lastModDate_ColNum;
	}
	/**
	 * @return the sqlInsertIntoDB_Activity
	 */
	public String getSqlInsertIntoDB_Activity() {
		return SqlInsertIntoDB_Activity;
	}
	/**
	 * @param sqlInsertIntoDB_Activity the sqlInsertIntoDB_Activity to set
	 */
	public void setSqlInsertIntoDB_Activity(String sqlInsertIntoDB_Activity) {
		SqlInsertIntoDB_Activity = sqlInsertIntoDB_Activity;
	}
	/**
	 * @return the sqlUpdateFTP_File_Catalog
	 */
	public String getSqlUpdateFTP_File_Catalog() {
		return SqlUpdateFTP_File_Catalog;
	}
	/**
	 * @param sqlUpdateFTP_File_Catalog the sqlUpdateFTP_File_Catalog to set
	 */
	public void setSqlUpdateFTP_File_Catalog(String sqlUpdateFTP_File_Catalog) {
		SqlUpdateFTP_File_Catalog = sqlUpdateFTP_File_Catalog;
	}
	/**
	 * @return the sqlSelectFileData
	 */
	public String getSqlSelectFileData() {
		return sqlSelectFileData;
	}
	/**
	 * @param sqlSelectFileData the sqlSelectFileData to set
	 */	
	public void setSqlSelectFileData(String sqlSelectFileData) {
		this.sqlSelectFileData = sqlSelectFileData;
	}
	/**
	 * @return the ftpServerName
	 */
	public String getFtpServerName() {
		return ftpServerName;
	}
	/**
	 * @param ftpServerName the ftpServerName to set
	 */
	public void setFtpServerName(String ftpServerName) {
		this.ftpServerName = ftpServerName;
	}
	/**
	 * @return the fileSizeUnit
	 */
	public String getFileSizeUnit() {
		return fileSizeUnit;
	}
	/**
	 * @param fileSizeUnit the fileSizeUnit to set
	 */
	public void setFileSizeUnit(String fileSizeUnit) {
		this.fileSizeUnit = fileSizeUnit;
	}
	/**
	 * @return the fileSizeMultiplier (calculated from fileSizeUnit
	 */
	public Double getFileSizeMultiplier() {
		
		 for (int i = 0; i < this.fileSizeUnitValidValues.length ; i++) {
			if (fileSizeUnit.equalsIgnoreCase(this.fileSizeUnitValidValues[i])) {				
				this.fileSizeMultiplier = 1.0/this.fileSizeMultiplierValidValues[i];
			}
		 }	
		 if (this.fileSizeMultiplier.isNaN() ) {
			 return null;
		 }
		 else {
			 return this.fileSizeMultiplier;	 
		 }
		
	}
	/**
	 * @return the sqlInsertIntoFTP_File_Catalog
	 */
	public String getSqlInsertIntoFTP_File_Catalog() {
		return SqlInsertIntoFTP_File_Catalog;
	}
	/**
	 * @param sqlInsertIntoFTP_File_Catalog the sqlInsertIntoFTP_File_Catalog to set
	 */
	public void setSqlInsertIntoFTP_File_Catalog(
			String sqlInsertIntoFTP_File_Catalog) {
		SqlInsertIntoFTP_File_Catalog = sqlInsertIntoFTP_File_Catalog;
	}

	/**
	 * @return the dateModifiedFieldName
	 */
	public String getDateModifiedFieldName() {
		return dateModifiedFieldName;
	}
	/**
	 * @param dateModifiedFieldName the dateModifiedFieldName to set
	 */
	public void setDateModifiedFieldName(String dateModifiedFieldName) {
		this.dateModifiedFieldName = dateModifiedFieldName;
	}
	/**
	 * @return the fileDirectory
	 */
	public String getFileDirectory() {
		return fileDirectory;
	}
	/**
	 * @param fileDirectory the fileDirectory to set
	 */
	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}
	/**
	 * @return the fileSystemLocation
	 */
	public String getFileSystemLocation() {
		return fileSystemLocation;
	}
	/**
	 * @param fileSystemLocation the fileSystemLocation to set
	 */
	public void setFileSystemLocation(String fileSystemLocation) {
		this.fileSystemLocation = fileSystemLocation;
	}
	/**
	 * @return the ftpServerPort
	 */
	public Integer getFtpServerPort() {
		return ftpServerPort;
	}
	/**
	 * @param ftpServerPort the ftpServerPort to set
	 */
	public void setFtpServerPort(Integer ftpServerPort) {
		this.ftpServerPort = ftpServerPort;
	}
	/**
	 * @return the ftpUserId
	 */
	public String getFtpUserId() {
		return ftpUserId;
	}
	/**
	 * @param ftpUserId the ftpUserId to set
	 */
	public void setFtpUserId(String ftpUserId) {
		this.ftpUserId = ftpUserId;
	}
	/**
	 * @return the ftpPassword
	 */
	public String getFtpPassword() {
		return ftpPassword;
	}
	/**
	 * @param ftpPassword the ftpPassword to set
	 */
	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}
	/**
	 * @return the ftpProtocol
	 */
	public String getFtpProtocol() {
		return ftpProtocol;
	}
	/**
	 * @param ftpProtocol the ftpProtocol to set
	 */
	public void setFtpProtocol(String ftpProtocol) {
		this.ftpProtocol = ftpProtocol;
	}
	/**
	 * @return the excludeDirs
	 */
	public String getExcludeDirs() {
		return excludeDirs;
	}
	/**
	 * @param excludeDirs the excludeDirs to set
	 */
	public void setExcludeDirs(String excludeDirs) {
		this.excludeDirs = excludeDirs;
	}

}

