package ru.pricechecker.main;

import java.util.ArrayList;
import java.util.List;

import ru.pricechecker.main.FileReader.LoadData;

/**
 * @author USER
 *
 */
public class PriceCheckerUtil {

	/**
	 * Position in list for barcode
	 */
	public static final int POSITION_BARCODE = 0;
	
	/**
	 *  Position in list for ip
	 */
	public static final int POSITION_IP = 1;
		
	/**
	 *  Message for error
	 */
	public static final String MESSAGE_NOT_FOUND_1 = "Товар не найден!";
	
	/**
	 *  Message for error
	 */
	private static final String MESSAGE_NOT_FOUND_2 = "Обновите выгрузку...";
	
	/**
	 *  For display on device
	 */
	private static final String MARK_COST = "Ц: "; 
	
	/**
	 *  For display on device
	 */
	private static final String MARK_COUNT = "К: ";
	
	/**
	 *  For display on device
	 */
	private static final String MARK_SPACE = "  ";
		
	/**
	 * @param request barcode from device
	 * @param ip ip from device
	 * @return normalize data
	 */
	public static List<String> handleCheckerRequest(String request, String ip){		
		try {
			// Handle data
			String handledIP = ip.replaceAll("[^0-9|.]+", "");
			String handledBarcode = request.replaceAll("[^0-9]+", "");
			// Put to container
			List<String> handledRequest = new ArrayList<>();
			handledRequest.add(handledBarcode);		
			handledRequest.add(handledIP);
			
			return handledRequest;
			
		} catch (Exception e) {
			FileLogging.toLog(PriceCheckerUtil.class.getSimpleName() + " - handleCheckerRequest: " + e.getMessage());
			return null;
		}
	}
	
	
	/**
	 * @param returnData data for convert
	 * @return converted data
	 */
	public static byte[] getCheckerDataForTwoString(LoadData returnData){
		try {
			// For two strings, return data must be 91 bytes
			byte answer[] = new byte[91];
			
			// Reset array
			for(int i = 0; i < answer.length; i++)
				answer[i] = ' ';
			
			// Preferences
			// Command
			answer[0] = (byte)0xAE;
			// Output time on checker screen
			answer[1] = (byte)50;
			// String length. Default 80
			answer[2] = (byte)80;
			// Type screen output. Running or static
			answer[3] = (byte)0xF2;
			// Speed of first string
			answer[4] = (byte)2;
			// Speed of second string
			answer[5] = (byte)3;
			// Message priority. 5 is highest.
			answer[6] = (byte)5;   			
			// Length of first string. Max - 40
			answer[7] = (byte)40; 
			// Length of second string. Max - 40
			answer[8] = (byte)40; 
			
			// Reserve 4 bytes
			answer[9] = (byte)0; 
			answer[10] = (byte)0; 
			answer[11] = (byte)0; 
			answer[12] = (byte)0;
				
			// Set strings
			String firstString = "";
			String secondString = "";
			if(returnData != null){
				firstString = returnData.getName();
				secondString = MARK_COST + returnData.getCost() + MARK_SPACE + MARK_COUNT + returnData.getCount();
			} else {
				firstString = MESSAGE_NOT_FOUND_1;
				secondString = MESSAGE_NOT_FOUND_2;
			}
			
			// Fill first string
			byte [] convertedFirstString = firstString.getBytes("cp1251");
			// Start from 13's byte
			for(int i = 0, j = 13; i < convertedFirstString.length && j < 53; i++, j++){
				answer[j] = convertedFirstString[i];
			}    				
			 			
			// Fill second string
			byte [] convertedSecondString = secondString.getBytes("cp1251");;
			// Start from 53's byte
			for(int i = 0, j = 53; i < convertedSecondString.length && j < answer.length; i++, j++){
				answer[j] = convertedSecondString[i];
			}
						
			return answer;
			
		} catch (Exception e) {
			FileLogging.toLog(PriceCheckerUtil.class.getSimpleName() + " - getCheckerDataForTwoString: " + e.getMessage());
			return null;
		}
	}
}
