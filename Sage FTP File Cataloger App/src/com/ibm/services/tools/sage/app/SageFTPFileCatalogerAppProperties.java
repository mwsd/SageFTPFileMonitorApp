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
	//private String sqlSelectFileData;
	private String SqlInsertIntoFTP_File_Catalog;
	//private String SqlUpdateFTP_File_Catalog;
	private String SqlUpdateStatusFTP_File_Catalog;
	private Integer pageSize;
	private String fileSizeUnit;
	private Double fileSizeMultiplier;
	private String fileSizeUnitValidValues[] = {"byte","kbyte","mbyte","gbyte","tbyte"};
	static final double KB = 1024.0;
	static final double ONE = 1.0;
	private double fileSizeMultiplierValidValues[] = {ONE,KB,KB*KB,KB*KB*KB,KB*KB*KB*KB};
	private String SqlInsertIntoDB_Activity;
	//private Integer lastModDate_ColNum;
	private String currentFileID;
	private String SqlFetchAllRows;
	private String SqlFetchAllRows_Activity;
	private Integer hostName_ColNum;
	private Integer filePath_ColNum;
	private Integer fileName_ColNum;
	private Integer FileSize_ColNum;
	private Integer FileSize_Unit_ColNum;
	private Integer FileCreate_Date_ColNum;
	private Integer FileLastMod_Date_ColNum;
	private Integer FilePermissions_ColNum;
	private Integer FileOwners_ColNum;
	private Integer FileStatus_ColNum;
	private Integer FileType_ColNum;
	private Integer activityType_ColNum;
	private Integer activityDesc_ColNum;
	private Integer activityFileId_ColNum;
	
	private Integer contentHeaderSize;
	private String alertFileName;
	private String fileSystemLocation;
	private String ftpProtocol;
	private String excludeDirs;
	private String FileCatalogTableName;
	private String FileActivityTableName;
	
	
	
	
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
	/*
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
	public double getFileSizeMultiplier() {
		
		 for (int i = 0; i < this.fileSizeUnitValidValues.length ; i++) {
			if (fileSizeUnit.equalsIgnoreCase(this.fileSizeUnitValidValues[i])) {				
				this.fileSizeMultiplier = 1.0/this.fileSizeMultiplierValidValues[i];
			}
		 }	
		 if (this.fileSizeMultiplier.isNaN() ) {
			 return 0.0;
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
	/**
	 * @return the fileSize_ColNum
	 */
	public Integer getFileSize_ColNum() {
		return FileSize_ColNum;
	}
	/**
	 * @param fileSize_ColNum the fileSize_ColNum to set
	 */
	public void setFileSize_ColNum(Integer fileSize_ColNum) {
		FileSize_ColNum = fileSize_ColNum;
	}
	/**
	 * @return the fileSize_Unit_ColNum
	 */
	public Integer getFileSize_Unit_ColNum() {
		return FileSize_Unit_ColNum;
	}
	/**
	 * @param fileSize_Unit_ColNum the fileSize_Unit_ColNum to set
	 */
	public void setFileSize_Unit_ColNum(Integer fileSize_Unit_ColNum) {
		FileSize_Unit_ColNum = fileSize_Unit_ColNum;
	}
	/**
	 * @return the fileCreate_Date_ColNum
	 */
	public Integer getFileCreate_Date_ColNum() {
		return FileCreate_Date_ColNum;
	}
	/**
	 * @param fileCreate_Date_ColNum the fileCreate_Date_ColNum to set
	 */
	public void setFileCreate_Date_ColNum(Integer fileCreate_Date_ColNum) {
		FileCreate_Date_ColNum = fileCreate_Date_ColNum;
	}
	/**
	 * @return the fileLastMod_Date_ColNum
	 */
	public Integer getFileLastMod_Date_ColNum() {
		return FileLastMod_Date_ColNum;
	}
	/**
	 * @param fileLastMod_Date_ColNum the fileLastMod_Date_ColNum to set
	 */
	public void setFileLastMod_Date_ColNum(Integer fileLastMod_Date_ColNum) {
		FileLastMod_Date_ColNum = fileLastMod_Date_ColNum;
	}
	/**
	 * @return the filePermissions_ColNum
	 */
	public Integer getFilePermissions_ColNum() {
		return FilePermissions_ColNum;
	}
	/**
	 * @param filePermissions_ColNum the filePermissions_ColNum to set
	 */
	public void setFilePermissions_ColNum(Integer filePermissions_ColNum) {
		FilePermissions_ColNum = filePermissions_ColNum;
	}
	/**
	 * @return the fileOwners_ColNum
	 */
	public Integer getFileOwners_ColNum() {
		return FileOwners_ColNum;
	}
	/**
	 * @param fileOwners_ColNum the fileOwners_ColNum to set
	 */
	public void setFileOwners_ColNum(Integer fileOwners_ColNum) {
		FileOwners_ColNum = fileOwners_ColNum;
	}
	/**
	 * @return the fileStatus_ColNum
	 */
	public Integer getFileStatus_ColNum() {
		return FileStatus_ColNum;
	}
	/**
	 * @param fileStatus_ColNum the fileStatus_ColNum to set
	 */
	public void setFileStatus_ColNum(Integer fileStatus_ColNum) {
		FileStatus_ColNum = fileStatus_ColNum;
	}
	/**
	 * @return the fileType_ColNum
	 */
	public Integer getFileType_ColNum() {
		return FileType_ColNum;
	}
	/**
	 * @param fileType_ColNum the fileType_ColNum to set
	 */
	public void setFileType_ColNum(Integer fileType_ColNum) {
		FileType_ColNum = fileType_ColNum;
	}
	/**
	 * @return the sqlFetchAllRows_Activity
	 */
	public String getSqlFetchAllRows_Activity() {
		return SqlFetchAllRows_Activity;
	}
	/**
	 * @param sqlFetchAllRows_Activity the sqlFetchAllRows_Activity to set
	 */
	public void setSqlFetchAllRows_Activity(String sqlFetchAllRows_Activity) {
		SqlFetchAllRows_Activity = sqlFetchAllRows_Activity;
	}
	/**
	 * @return the activityType_ColNum
	 */
	public Integer getActivityType_ColNum() {
		return activityType_ColNum;
	}
	/**
	 * @param activityType_ColNum the activityType_ColNum to set
	 */
	public void setActivityType_ColNum(Integer activityType_ColNum) {
		this.activityType_ColNum = activityType_ColNum;
	}
	/**
	 * @return the activityDesc_ColNum
	 */
	public Integer getActivityDesc_ColNum() {
		return activityDesc_ColNum;
	}
	/**
	 * @param activityDesc_ColNum the activityDesc_ColNum to set
	 */
	public void setActivityDesc_ColNum(Integer activityDesc_ColNum) {
		this.activityDesc_ColNum = activityDesc_ColNum;
	}
	/**
	 * @return the activityFileId_ColNum
	 */
	public Integer getActivityFileId_ColNum() {
		return activityFileId_ColNum;
	}
	/**
	 * @param activityFileId_ColNum the activityFileId_ColNum to set
	 */
	public void setActivityFileId_ColNum(Integer activityFileId_ColNum) {
		this.activityFileId_ColNum = activityFileId_ColNum;
	}
	/**
	 * @return the pageSize
	 */
	public Integer getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * @return the fileCatalogTableName
	 */
	public String getFileCatalogTableName() {
		return FileCatalogTableName;
	}
	/**
	 * @param fileCatalogTableName the fileCatalogTableName to set
	 */
	public void setFileCatalogTableName(String fileCatalogTableName) {
		FileCatalogTableName = fileCatalogTableName;
	}
	/**
	 * @return the fileActivityTableName
	 */
	public String getFileActivityTableName() {
		return FileActivityTableName;
	}
	/**
	 * @param fileActivityTableName the fileActivityTableName to set
	 */
	public void setFileActivityTableName(String fileActivityTableName) {
		FileActivityTableName = fileActivityTableName;
	}
}