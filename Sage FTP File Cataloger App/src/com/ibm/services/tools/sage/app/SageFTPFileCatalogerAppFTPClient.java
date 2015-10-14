/**
 * 
 */
package com.ibm.services.tools.sage.app;
import com.ibm.services.tools.sage.utilities.*;
import org.apache.commons.net.ftp.*;
import java.io.IOException;
import java.net.SocketException;
import com.jcraft.jsch.*;
import com.jcraft.jsch.JSchException;
import java.util.Vector;
import java.util.ArrayList;

/**
 * @author Yogi Golle (yogigol@us.ibm.com)
 *
 */
public class SageFTPFileCatalogerAppFTPClient extends AppFTP {

	/**
	 * 
	 */
	public FTPSClient ftpsClient;
	// -- private global variable of the SFTP channel object for the ftp over SSH connection connection
	// -- (Object instance is created by the connectSFTPServer method and used by the getFileMetaData method
	private ChannelSftp cSftp;
	// -- Root Directory of the sFTP host system
	public String sFTPDirectory;
	
	// -- Class File Counter
	private Long fileCount = Long.valueOf("0");
	
	// -- ArrayList of file attributes
	public ArrayList<String> 	fl = new ArrayList<String>(); // Long File Name
	public ArrayList<String> 	fn = new ArrayList<String>(); // File Name
	public ArrayList<String> 	ft = new ArrayList<String>(); // File Type
	public ArrayList<Long> 		fs = new ArrayList<Long>(); // File Size
	public ArrayList<String> 	fsu = new ArrayList<String>(); // File Size Unit
	public ArrayList<String> 	fa = new ArrayList<String>(); // Last Accessed
	public ArrayList<String> 	fm = new ArrayList<String>(); // Last Modified
	public ArrayList<String> 	fp = new ArrayList<String>(); // File Permissions
	public ArrayList<String> 	fd = new ArrayList<String>(); // Directory Paths
	public ArrayList<String> 	fh = new ArrayList<String>(); // Host Names
	public ArrayList<String> 	fo = new ArrayList<String>(); // Owners
	public ArrayList<String> 	fhd = new ArrayList<String>(); // File Header
	
	
	public SageFTPFileCatalogerAppFTPClient() throws JSchException {
		// Constructor Stub
	}
	
	/****************************************************************************************************************
	 * Connects to the FTP server by host address/IP and port, then authenticates with user's ID and password.
	 * @return String The reply text from the server.
	 * @throws Exception
	 */
	public String connectFTPSServer(String protocol) throws Exception{
		String ftpReplyText = "";

		//-- Instantiate an sFTP Clien Object
		ftpsClient = new FTPSClient(protocol);
		
		//-- Make sure the fields required by this method are available
	 	if (AppTools.newStringIfObjectStringNullOrError(ftpServerAddress, "").equalsIgnoreCase("") ||
	 			AppTools.newStringIfObjectStringNullOrError(ftpServerPort, "").equalsIgnoreCase("") ||
	 			AppTools.newStringIfObjectStringNullOrError(ftpUserID, "").equalsIgnoreCase("") ||
	 			AppTools.newStringIfObjectStringNullOrError(ftpPassword, "").equalsIgnoreCase("")) {
	 		throw new Exception("One or more properties are missing. The FTP server address, port, user ID, and password must be provided."); 		
	 	} 
	 	
	 	//-- Connect to the FTP server using server address and port. Catch any exceptions and get the reply text 
	   	try {
	   		ftpsClient.connect(this.ftpServerAddress, Integer.parseInt(this.ftpServerPort));
	   		System.out.println("Connected to " + this.ftpServerAddress);
	   		ftpReplyText = ftpReplyText + ftpsClient.getReplyString();
	   		String systemType = "";
	   		try{
	   			systemType = ftpsClient.getSystemType();
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
	   		boolean loginSuccess = ftpsClient.login(ftpUserID, ftpPassword);
	   		if (!loginSuccess) {
            	throw new Exception("AppFTP FTPClient login error: " + ftpsClient.getReplyString());
            }
	   		ftpReplyText = ftpReplyText + ", " + ftpsClient.getReplyString();
            ftpsClient.enterLocalPassiveMode();
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
	 * Retrieves a list of files and directories from the FTP Server given the root directory
	 * @return String The reply text from the server.
	 * @throws Exception
	 */
	public FTPFile[] getFTPFiles(String directory) throws Exception {
		try {
			FTPFile[] files = ftpsClient.listFiles(directory);
			return files;
		}
		catch (Exception e) {
			//-- If an error occurs, just "bubble it up" to the calling method
			throw e;
		}
		
	}
	/****************************************************************************************************************
	 * Configues the FTP Server connection
	 * @param appProps
	 * @throws Exception
	 */
	public void configureFTPConnection(SageFTPFileCatalogerAppProperties appProps) throws Exception {
		try {
			//-- Prepare security
			
			
			//-- Connection Details
			this.ftpServerAddress 	= appProps.getFtpServerName();
			this.ftpServerPort 		= appProps.getFtpServerPort().toString();
			this.ftpUserID 			= appProps.getFtpUserId();
			this.ftpPassword 		= appProps.getFtpPassword();
		} 
		catch (Exception e) {
			//-- If an error occurs, just "bubble it up" to the calling method
			throw e;
		}
	}
	public String connectSFTPServer(SageFTPFileCatalogerAppProperties appProps) throws Exception {
		
		String sFTPHost 	= appProps.getFtpServerName();
		Integer sFTPPort	= appProps.getFtpServerPort(); //+++ This property needs to be of type Integer
		String sFTPUser		= appProps.getFtpUserId();
		String sFTPPwd		= appProps.getFtpPassword();
		sFTPDirectory 		= appProps.getFileDirectory();
		String root_dir		= "";
		
		Session session		= null;
		Channel	channel		= null;
		
		try {
			JSch jsch 	= new JSch();
			session		= jsch.getSession(sFTPUser,sFTPHost,sFTPPort);
			session.setPassword(sFTPPwd);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
		}
		catch (JSchException j) {
			throw new Exception("Error creating SSH/sftp session/channel - " + j.getMessage(),j);
		}
		
		try {
			cSftp = (ChannelSftp)channel;
			cSftp.cd(sFTPDirectory);
			root_dir = sFTPDirectory;
			
		}
		catch (SftpException s) {
			throw new Exception("Error in sFTP channel operation - " + s.getMessage(),s);
		}
		return root_dir;
		
	} // -- end of connectSFTPServer method

	/****************************************************************************************************************
	 * Get the File Meta Data for the Directory Tree of the active sFTP connection
	 * (Using the sFTP channel created in the connectSFTPServer method
	 * @throws Exception
	 */
	public void getFileMetaData(SageFTPFileCatalogerAppProperties appProps,String curr_Dir) throws Exception {
		
		// -- Read the file list of the current root directory into a Vector of strings 
		
		@SuppressWarnings("unchecked") Vector<String> fileList = cSftp.ls(curr_Dir);
		String fileType;
		
		if (fileList != null) {
						
			for (int i=0; i < fileList.size(); i++) {
				Object obj = fileList.elementAt(i);
				if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
					ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry)obj;
					
					if (!entry.getAttrs().isDir()) {
						
						try {
							fileCount++;
							fd.add(curr_Dir); 
							fh.add(this.ftpServerAddress);
							fn.add(entry.getFilename());
							fileType = entry.getFilename().substring(entry.getFilename().lastIndexOf('.')+1);
							ft.add(fileType);
							fsu.add(appProps.getFileSizeUnit());
							fl.add(entry.getLongname());
							fs.add(entry.getAttrs().getSize());
							fa.add(entry.getAttrs().getAtimeString());
							fm.add(entry.getAttrs().getMtimeString());
							fp.add(entry.getAttrs().getPermissionsString());
							fo.add(Integer.toString(entry.getAttrs().getUId()));
							fhd.add("n/a");
						}
						catch (Exception e) {
							throw e;
						}
					} else {
						// -- It's a directory
						if (!entry.getFilename().equals("..") && //-- skip parent directory
								!entry.getFilename().equals(".") &&  //-- skip current directory symbol
								appProps.getExcludeDirs().indexOf(entry.getFilename()) == -1) { //--skip excluded directories
							//--Change current directory to the directory name
							cSftp.cd(entry.getFilename());
							AppLog.logActivity(appProps,fileCount + " Files have been scanned so far. Changing current directory to : " + cSftp.pwd(), true, true);
							getFileMetaData(appProps,cSftp.pwd()); // -- Walk down the directory tree (recursive call)
						}
						AppLog.logActivity(appProps,"directory name: " + curr_Dir, true, true);
					}
										
				}
			}
			//-- Reset the current directory to the root directory
			cSftp.cd(this.sFTPDirectory);			
		}
	}
	
} // -- end if SageFTPFileCatalogerAppFTPClient class
