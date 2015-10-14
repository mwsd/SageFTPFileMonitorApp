/**
 * 
 */
package com.ibm.services.tools.sage.app;

/****************************************************************************************
 * This class is used to launch the actual application by setting up an instance of the primary class and 
 * passing over the necessary arguments. The primary class used by this app is "SageFTPFileMonitorApp". Refer to 
 * that class for details about this application.
 * @author		Yogi Golle/Tampa/Contr/IBM (yogigol@us.ibm.com) - IBM GTS Service Delivery - MWSD Tools and Technology Team
 * @version 	2015.06.15 
 */
public class SageFTPFileMonitorApp_Launch {

	/****************************************************************************************************************
	* This is a standard "main" function that is called when the class is launched. Accepts parameters as arguments and
	* calls the necessary functions to process the application.
	* @param	args		Array of the arguments passed to this method at runtime.
	*/
	public static void main(String[] args) {

		try {	
			//-- Make sure the appropriate number of command line arguments was passed
			if (args.length<1){
				throw new Exception("ERROR: Missing the command line argument for the Properties file name.");
			}
			//-- Setup an instance of the primary class used by the application
			SageFTPFileMonitorApp javaApp = new SageFTPFileMonitorApp();
			//-- Run the startup/launch/run method of the primary class based on the number of arguments/parameters passed
			javaApp.runApp(args[0]);
			System.exit(0);
			
        } catch (Exception e){
        	System.out.println(e.getMessage());
			e.printStackTrace();
			//-- Exit with a code of 1 (non zero) so the calling system knows there was an error
			System.exit(1);
		}
	}	

}
