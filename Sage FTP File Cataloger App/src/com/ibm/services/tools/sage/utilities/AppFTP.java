/****************************************************************************************************************
* IBM Confidential.   © Copyright IBM Corp. 2004, 2015  All Rights Reserved.
 * The source code for this program is not published or otherwise divested of its trade secrets, 
 * irrespective of what has been deposited with the U.S. Copyright office.
 */
package com.ibm.services.tools.sage.utilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/****************************************************************************************************************
 * This class is used as a generic FTP class for connecting to FTP servers, uploading files, downloading files, etc
 * @author		Jason Wallace/Atlanta/IBM (jwalla@us.ibm.com)  -  IBM Global Services  -  EUS Tools and Technology Team
 * @version 	2015.04.16
 */
public class AppFTP {
	private FTPClient ftpClient;
	public String ftpServerAddress;
	public String ftpServerPort;
	public String ftpUserID;
	public String ftpPassword;
		
	
	
	/****************************************************************************************************************
	 * Connects to the FTP server by host address/IP and port, then authenticates with user's ID and password.
	 * @return String The reply text from the server.
	 * @throws Exception
	 */
	public String connectFTPServer() throws Exception{
		String ftpReplyText = "";
		ftpClient = new FTPClient();
		//-- Make sure the fields required by this method are available
	 	if (AppTools.newStringIfObjectStringNullOrError(ftpServerAddress, "").equalsIgnoreCase("") ||
	 			AppTools.newStringIfObjectStringNullOrError(ftpServerPort, "").equalsIgnoreCase("") ||
	 			AppTools.newStringIfObjectStringNullOrError(ftpUserID, "").equalsIgnoreCase("") ||
	 			AppTools.newStringIfObjectStringNullOrError(ftpPassword, "").equalsIgnoreCase("")) {
	 		throw new Exception("One or more properties are missing. The FTP server address, port, user ID, and password must be provided."); 		
	 	} 
	 	//-- Connect to the FTP server using server address and port. Catch any exceptions and get the reply text 
	   	try {
	   		ftpClient.connect(ftpServerAddress, Integer.parseInt(ftpServerPort));
	   		ftpReplyText = ftpReplyText + ftpClient.getReplyString();
	   		String systemType = "";
	   		try{
	   			systemType = ftpClient.getSystemType();
	   		}
	   		catch (IOException ie){
	   			systemType = "unknown";
	   		}
	   		ftpReplyText = ftpReplyText + ", System: " + systemType;
	   	}
	   	catch (SocketException se) {       	
	        throw new Exception("AppFTP FTPClient connect error. SocketException: " + se.getMessage());
	    } 
	   	catch (IOException ie) {       	
	        throw new Exception("AppFTP FTPClient connect error. IOException: " + ie.getMessage());
	    } 
	   	//-- Log into the FTP server using the userid and password. Check for success and catch any exceptions.
	   	try{	 
	   		boolean loginSuccess = ftpClient.login(ftpUserID, ftpPassword);
	   		if (!loginSuccess) {
            	throw new Exception("AppFTP FTPClient login error: " + ftpClient.getReplyString());
            }
	   		ftpReplyText = ftpReplyText + ", " + ftpClient.getReplyString();
            ftpClient.enterLocalPassiveMode();
	    }
	    catch (SocketException se) {
	        throw new Exception("AppFTP FTPClient login error. SocketException: " + se.getMessage());
	    } 
	   	catch (IOException ie) {       	
	        throw new Exception("AppFTP FTPClient login error. IOException: " + ie.getMessage());
	    }
	    //-- Done. Return the results...
	    return ftpReplyText;
	  }
	
	
	
	
	/****************************************************************************************************************
	 * Disconnects from the FTP server and checks for successful logout.  
	 * @return String The reply text from the server.
	 * @throws Exception
	 */
	public String disconnectFTPServer() throws Exception{
		String ftpReplyText = "";
		//-- If the FTPClient is connected, then logout and verify logout, then disconnect
	   	try {
	   		if (ftpClient.isConnected()) {
                boolean logoutSuccess = ftpClient.logout();
    	   		if (!logoutSuccess) {
                	throw new Exception("AppFTP FTPClient logout error: " + ftpClient.getReplyString());
                }
    	   		ftpReplyText = ftpReplyText + ftpClient.getReplyString();
                ftpClient.disconnect();
            }
	    }
	   	catch (IOException ie) {       	
	        throw new Exception("AppFTP FTPClient logout/disconnect error. IOException: " + ie.getMessage());
	    } 
	  //-- Done. Return the results...
	    return ftpReplyText;
	  }
	
	
	
	
	/****************************************************************************************************************
	 * Uploads a single file to the FTP server in either binary or ascii mode.  
	 * @param localFileName Full path and file name of the file to upload
	 * @param remoteFileName Full path and file name of where to upload the file
	 * @param useBinary True if file upload should use binary mode, False if using ASCII
	 * @return String The reply text from the server.
	 * @throws Exception
	 */
	public String uploadFile(String localFileName, String remoteFileName, boolean useBinary) throws Exception{
		String ftpReplyText = "";
	   	try {
	   		//-- Setup the FTPClient with the file type
	   		if (useBinary){
	            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	   		} else {
	   			ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
	   		}
	   		//-- Setup a new File object for the local file
	   		File localFile = new File(localFileName);	
	   		ftpReplyText = "(size=" + localFile.length() + " bytes) ";
	   		//-- Build a new InputStream for the local file
            InputStream inputStream = new FileInputStream(localFile); 
            //-- Upload the file to the server and check for successful results
            boolean uploadSuccess = ftpClient.storeFile(remoteFileName, inputStream);
            inputStream.close();
            if (!uploadSuccess) {
            	throw new Exception("AppFTP FTPClient upload file error: " + ftpClient.getReplyString());
            }
            ftpReplyText = ftpReplyText + ftpClient.getReplyString();		
	    }
	   	catch (IOException ie) {       	
	        throw new Exception("AppFTP FTPClient upload file error. IOException: " + ie.getMessage());
	    } 
	   	//-- Done. Return the results...
	    return ftpReplyText;
	}
	
	
}
