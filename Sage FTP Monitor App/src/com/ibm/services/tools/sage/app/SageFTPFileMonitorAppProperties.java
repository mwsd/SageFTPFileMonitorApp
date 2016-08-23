/**
 * 
 */
package com.ibm.services.tools.sage.app;

/**
 * @author yogi
 *
 */
public class SageFTPFileMonitorAppProperties extends AppProperties {
	
		/**
		 * Application properties specific to the SageFTPFileMonitor application
		 */
		private String ftpFileCataloger_alertFile;
		private String SqlInsertIntoDB_AutoEmailer;
		private String dbConnectionURL2;
		private String dbUserID2;
		private String dbPassword2;
		private Integer fileName_Pattern_ColNum;
		private Integer fileNameMode_ColNum;
		private Integer fileFrequency_ColNum;
		private Integer maxAlerts_ColNum;
		private Integer alertCount_ColNum;
		private Integer alertSent_ColNum;
		private String sqlSelectByFileName;
		private String sqlSelectByFilePattern;
		private String exceptionEventRecipients_Default;
		private String sqlSelectBusinessRuleData;
		private String alertMsgTemplate;
		private String warningMsgTemplate;
		private Integer sendTo_ColNum;
		private Integer plannedDay_ColNum;
		private Integer thresholdDay_ColNum;
		private Integer plannedTime_ColNum;
		private Integer thresholdTime_ColNum;
		private Integer lastMod_Date_ColNum;
		private Integer createDate_ColNum;
		private String sqlUpdateBusinessRuleIndex;
		private Integer businessRuleIndexID_ColNum;
		private Integer fileName_ColNum;
		private Integer status_ColNum;
		private Integer alertInterval_ColNum;
		private Integer lastAlertDate_ColNum;
		private Integer comment_ColNum;
		
		/**
		 * @return the ftpFileCataloger_alertFile
		 */
		public String getFtpFileCataloger_alertFile() {
			return ftpFileCataloger_alertFile;
		}
		/**
		 * @param ftpFileCataloger_alertFile the ftpFileCataloger_alertFile to set
		 */
		public void setFtpFileCataloger_alertFile(String ftpFileCataloger_alertFile) {
			this.ftpFileCataloger_alertFile = ftpFileCataloger_alertFile;
		}
		/**
		 * @return the sqlInsertIntoDB_AutoEmailer
		 */
		public String getSqlInsertIntoDB_AutoEmailer() {
			return SqlInsertIntoDB_AutoEmailer;
		}
		/**
		 * @param sqlInsertIntoDB_AutoEmailer the sqlInsertIntoDB_AutoEmailer to set
		 */
		public void setSqlInsertIntoDB_AutoEmailer(String sqlInsertIntoDB_AutoEmailer) {
			SqlInsertIntoDB_AutoEmailer = sqlInsertIntoDB_AutoEmailer;
		}
		/**
		 * @return the dbConnectionURL2
		 */
		public String getDbConnectionURL2() {
			return dbConnectionURL2;
		}
		/**
		 * @param dbConnectionURL2 the dbConnectionURL2 to set
		 */
		public void setDbConnectionURL2(String dbConnectionURL2) {
			this.dbConnectionURL2 = dbConnectionURL2;
		}
		/**
		 * @return the dbUserID2
		 */
		public String getDbUserID2() {
			return dbUserID2;
		}
		/**
		 * @param dbUserID2 the dbUserID2 to set
		 */
		public void setDbUserID2(String dbUserID2) {
			this.dbUserID2 = dbUserID2;
		}
		/**
		 * @return the dbPassword2
		 */
		public String getDbPassword2() {
			return dbPassword2;
		}
		/**
		 * @param dbPassword2 the dbPassword2 to set
		 */
		public void setDbPassword2(String dbPassword2) {
			this.dbPassword2 = dbPassword2;
		}
		/**
		 * @return the fileName_Pattern_ColNum
		 */
		public Integer getFileName_Pattern_ColNum() {
			return fileName_Pattern_ColNum;
		}
		/**
		 * @param fileName_Pattern_ColNum the fileName_Pattern_ColNum to set
		 */
		public void setFileName_Pattern_ColNum(Integer fileName_Pattern_ColNum) {
			this.fileName_Pattern_ColNum = fileName_Pattern_ColNum;
		}
		/**
		 * @return the fileNameMode_ColNum
		 */
		public Integer getFileNameMode_ColNum() {
			return fileNameMode_ColNum;
		}
		/**
		 * @param fileNameMode_ColNum the fileNameMode_ColNum to set
		 */
		public void setFileNameMode_ColNum(Integer fileNameMode_ColNum) {
			this.fileNameMode_ColNum = fileNameMode_ColNum;
		}
		/**
		 * @return the fileFrequency_ColNum
		 */
		public Integer getFileFrequency_ColNum() {
			return fileFrequency_ColNum;
		}
		/**
		 * @param fileFrequency_ColNum the fileFrequency_ColNum to set
		 */
		public void setFileFrequency_ColNum(Integer fileFrequency_ColNum) {
			this.fileFrequency_ColNum = fileFrequency_ColNum;
		}
		/**
		 * @return the maxAlerts_ColNum
		 */
		public Integer getMaxAlerts_ColNum() {
			return maxAlerts_ColNum;
		}
		/**
		 * @param maxAlerts_ColNum the maxAlerts_ColNum to set
		 */
		public void setMaxAlerts_ColNum(Integer maxAlerts_ColNum) {
			this.maxAlerts_ColNum = maxAlerts_ColNum;
		}
		/**
		 * @return the alertCount_ColNum
		 */
		public Integer getAlertCount_ColNum() {
			return alertCount_ColNum;
		}
		/**
		 * @param alertCount_ColNum the alertCount_ColNum to set
		 */
		public void setAlertCount_ColNum(Integer alertCount_ColNum) {
			this.alertCount_ColNum = alertCount_ColNum;
		}
		/**
		 * @return the alertSent_ColNum
		 */
		public Integer getAlertSent_ColNum() {
			return alertSent_ColNum;
		}
		/**
		 * @param alertSent_ColNum the alertSent_ColNum to set
		 */
		public void setAlertSent_ColNum(Integer alertSent_ColNum) {
			this.alertSent_ColNum = alertSent_ColNum;
		}
		/**
		 * @return the sqlSelectByFileName
		 */
		public String getSqlSelectByFileName() {
			return sqlSelectByFileName;
		}
		/**
		 * @param sqlSelectByFileName the sqlSelectByFileName to set
		 */
		public void setSqlSelectByFileName(String sqlSelectByFileName) {
			this.sqlSelectByFileName = sqlSelectByFileName;
		}
		/**
		 * @return the sqlSelectByFilePattern
		 */
		public String getSqlSelectByFilePattern() {
			return sqlSelectByFilePattern;
		}
		/**
		 * @param sqlSelectByFilePattern the sqlSelectByFilePattern to set
		 */
		public void setSqlSelectByFilePattern(String sqlSelectByFilePattern) {
			this.sqlSelectByFilePattern = sqlSelectByFilePattern;
		}
		/**
		 * @return the exceptionEventRecipients_Default
		 */
		public String getExceptionEventRecipients_Default() {
			return exceptionEventRecipients_Default;
		}
		/**
		 * @param exceptionEventRecipients_Default the exceptionEventRecipients_Default to set
		 */
		public void setExceptionEventRecipients_Default(
				String exceptionEventRecipients_Default) {
			this.exceptionEventRecipients_Default = exceptionEventRecipients_Default;
		}
		/**
		 * @return the sqlSelectBusinessRuleData
		 */
		public String getSqlSelectBusinessRuleData() {
			return sqlSelectBusinessRuleData;
		}
		/**
		 * @param sqlSelectBusinessRuleData the sqlSelectBusinessRuleData to set
		 */
		public void setSqlSelectBusinessRuleData(String sqlSelectBusinessRuleData) {
			this.sqlSelectBusinessRuleData = sqlSelectBusinessRuleData;
		}
		/**
		 * @return the alertMsgTemplate
		 */
		public String getAlertMsgTemplate() {
			return alertMsgTemplate;
		}
		/**
		 * @param alertMsgTemplate the alertMsgTemplate to set
		 */
		public void setAlertMsgTemplate(String alertMsgTemplate) {
			this.alertMsgTemplate = alertMsgTemplate;
		}
		/**
		 * @return the sendTo_ColNum
		 */
		public Integer getSendTo_ColNum() {
			return sendTo_ColNum;
		}
		/**
		 * @param sendTo_ColNum the sendTo_ColNum to set
		 */
		public void setSendTo_ColNum(Integer sendTo_ColNum) {
			this.sendTo_ColNum = sendTo_ColNum;
		}
		/**
		 * @return the plannedDay_ColNum
		 */
		public Integer getPlannedDay_ColNum() {
			return plannedDay_ColNum;
		}
		/**
		 * @param plannedDay_ColNum the plannedDay_ColNum to set
		 */
		public void setPlannedDay_ColNum(Integer plannedDay_ColNum) {
			this.plannedDay_ColNum = plannedDay_ColNum;
		}
		/**
		 * @return the thresholdDay_ColNum
		 */
		public Integer getThresholdDay_ColNum() {
			return thresholdDay_ColNum;
		}
		/**
		 * @param thresholdDay_ColNum the thresholdDay_ColNum to set
		 */
		public void setThresholdDay_ColNum(Integer thresholdDay_ColNum) {
			this.thresholdDay_ColNum = thresholdDay_ColNum;
		}
		/**
		 * @return the plannedTime_ColNum
		 */
		public Integer getPlannedTime_ColNum() {
			return plannedTime_ColNum;
		}
		/**
		 * @param plannedTime_ColNum the plannedTime_ColNum to set
		 */
		public void setPlannedTime_ColNum(Integer plannedTime_ColNum) {
			this.plannedTime_ColNum = plannedTime_ColNum;
		}
		/**
		 * @return the thresholdTime_ColNum
		 */
		public Integer getThresholdTime_ColNum() {
			return thresholdTime_ColNum;
		}
		/**
		 * @param thresholdTime_ColNum the thresholdTime_ColNum to set
		 */
		public void setThresholdTime_ColNum(Integer thresholdTime_ColNum) {
			this.thresholdTime_ColNum = thresholdTime_ColNum;
		}
		/**
		 * @return the lastMod_Date_ColNum
		 */
		public Integer getLastMod_Date_ColNum() {
			return lastMod_Date_ColNum;
		}
		/**
		 * @param lastMod_Date_ColNum the lastMod_Date_ColNum to set
		 */
		public void setLastMod_Date_ColNum(Integer lastMod_Date_ColNum) {
			this.lastMod_Date_ColNum = lastMod_Date_ColNum;
		}
		/**
		 * @return the sqlUpdateBusinessRuleIndex
		 */
		public String getSqlUpdateBusinessRuleIndex() {
			return sqlUpdateBusinessRuleIndex;
		}
		/**
		 * @param sqlUpdateBusinessRuleIndex the sqlUpdateBusinessRuleIndex to set
		 */
		public void setSqlUpdateBusinessRuleIndex(String sqlUpdateBusinessRuleIndex) {
			this.sqlUpdateBusinessRuleIndex = sqlUpdateBusinessRuleIndex;
		}
		/**
		 * @return the businessRuleIndexID_ColNum
		 */
		public Integer getBusinessRuleIndexID_ColNum() {
			return businessRuleIndexID_ColNum;
		}
		/**
		 * @param businessRuleIndexID_ColNum the businessRuleIndexID_ColNum to set
		 */
		public void setBusinessRuleIndexID_ColNum(Integer businessRuleIndexID_ColNum) {
			this.businessRuleIndexID_ColNum = businessRuleIndexID_ColNum;
		}
		/**
		 * @return the createDate_ColNum
		 */
		public Integer getCreateDate_ColNum() {
			return createDate_ColNum;
		}
		/**
		 * @param createDate_ColNum the createDate_ColNum to set
		 */
		public void setCreateDate_ColNum(Integer createDate_ColNum) {
			this.createDate_ColNum = createDate_ColNum;
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
		 * @return the status_ColNum
		 */
		public Integer getStatus_ColNum() {
			return status_ColNum;
		}
		/**
		 * @param status_ColNum the status_ColNum to set
		 */
		public void setStatus_ColNum(Integer status_ColNum) {
			this.status_ColNum = status_ColNum;
		}
		/**
		 * @return the alertInterval_ColNum
		 */
		public Integer getAlertInterval_ColNum() {
			return alertInterval_ColNum;
		}
		/**
		 * @param alertInterval_ColNum the alertInterval_ColNum to set
		 */
		public void setAlertInterval_ColNum(Integer alertInterval_ColNum) {
			this.alertInterval_ColNum = alertInterval_ColNum;
		}
		/**
		 * @return the lastAlertDate_ColNum
		 */
		public Integer getLastAlertDate_ColNum() {
			return lastAlertDate_ColNum;
		}
		/**
		 * @param lastAlertDate_ColNum the lastAlertDate_ColNum to set
		 */
		public void setLastAlertDate_ColNum(Integer lastAlertDate_ColNum) {
			this.lastAlertDate_ColNum = lastAlertDate_ColNum;
		}
		/**
		 * @return the warningMsgTemplate
		 */
		public String getWarningMsgTemplate() {
			return warningMsgTemplate;
		}
		/**
		 * @param warningMsgTemplate the warningMsgTemplate to set
		 */
		public void setWarningMsgTemplate(String warningMsgTemplate) {
			this.warningMsgTemplate = warningMsgTemplate;
		}
		/**
		 * @return the comment_ColNum
		 */
		public Integer getComment_ColNum() {
			return comment_ColNum;
		}
		/**
		 * @param comment_ColNum the comment_ColNum to set
		 */
		public void setComment_ColNum(Integer comment_ColNum) {
			this.comment_ColNum = comment_ColNum;
		}
		
		
	
		

		

}
