/****************************************************************************************************************
 * IBM Confidential.   © Copyright IBM Corp. 2004, 2015  All Rights Reserved.
 * The source code for this program is not published or otherwise divested of its trade secrets, 
 * irrespective of what has been deposited with the U.S. Copyright office.
 */
package com.ibm.services.tools.sage.utilities;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec; 


/****************************************************************************************************************
 * This class is used to encrypt and decrypt values using AES encryption with a secret key.
 * @author		Jason Wallace/Atlanta/IBM (jwalla@us.ibm.com)  -  IBM Global Services  -  EUS Tools and Technology Team
 * @version 	2015.04.16    
 */
public class AppCrypto {  
	private static final byte[] k2 = new byte[]{'6','E','F','2','3','8','8','B'};
	private static final byte[] k1 = new byte[]{'B','B','F','4','7','E','3','2'};
	private static final byte[] k4 = new byte[]{'F','0','3','7','4','E','2','2'};
	private static final byte[] k3 = new byte[]{'C','D','7','A','C','E','4','3'};    
	private static final String keyValue = new String(k1)+new String(k2)+new String(k3)+new String(k4);


	/****************************************************************************************************************
	 * Encrypts the 'value' passed to it with AES encryption using the secret keyValue
	 * @param value
	 * @return String
	 * @throws GeneralSecurityException
	 */
	public static String encrypt(String value) throws GeneralSecurityException {    
		SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(keyValue), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
		byte[] encrypted = cipher.doFinal(value.getBytes());
		return byteArrayToHexString(encrypted);
	}


	/****************************************************************************************************************
	 * Decrypts the 'value' passed to it with AES encryption using the secret keyValue
	 * @param message
	 * @return String
	 * @throws GeneralSecurityException
	 */
	public static String decrypt(String message) throws GeneralSecurityException {
		SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(keyValue), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, sks);
		byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
		return new String(decrypted);
	}


	/****************************************************************************************************************
	 * Converts a byte array to a hex string
	 * @param b
	 * @return String
	 */
	private static String byteArrayToHexString(byte[] b){
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++){
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}
	
	
	/****************************************************************************************************************
	 * Converts a hex string to a byte array
	 * @param s
	 * @return byte[]
	 */
	private static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++){
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte)v;
		}
		return b;
	}
}

