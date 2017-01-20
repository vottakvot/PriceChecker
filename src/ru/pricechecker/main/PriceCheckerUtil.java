package ru.pricechecker.main;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.pricechecker.main.FileReader.LoadData;

public class PriceCheckerUtil {

	public static final int POSITION_BARCODE = 0;
	public static final int POSITION_IP = 1;
		
	private static final String MARK_COST = "Цена: "; 
	private static final String MARK_COUNT = "Кол-во: ";
	private static final String MARK_SPACE = "  ";
		
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
	
	
	public static byte[] getCheckerDataForTwoString(LoadData returnData){
		try {
			// For two strings, return data must be 91 bytes
			byte answer[] = new byte[91];
			
			// Reset array
			for(int i = 0; i < answer.length; i++)
				answer[i] = ' ';
			
			// Preferences
			// Command
			answer[0] = new Integer(174).byteValue();
			// Output time on checker screen
			answer[1] = new Integer(50).byteValue();
			// String length. Default 80
			answer[2] = new Integer(80).byteValue();
			// Type screen output. Running or static
			answer[3] = new Integer(34).byteValue();
			// Speed of first string
			answer[4] = new Integer(1).byteValue();
			// Speed of second string
			answer[5] = new Integer(5).byteValue();
			// Message priority. 5 is highest.
			answer[6] = new Integer(5).byteValue();   			
			// Length of first string. Max - 40
			answer[7] = new Integer(40).byteValue(); 
			// Length of second string. Max - 40
			answer[8] = new Integer(40).byteValue(); 
			
			// Reserve 4 bytes
			answer[9] = new Integer(0).byteValue(); 
			answer[10] = new Integer(0).byteValue(); 
			answer[11] = new Integer(0).byteValue(); 
			answer[12] = new Integer(0).byteValue();
			
			// Fill first string
			byte [] convertedFirstString = returnData.getName().getBytes("cp1251");
			// Start from 13's byte
			for(int i = 0, j = 13; i < convertedFirstString.length && j < 53; i++, j++){
				answer[j] = convertedFirstString[i];
			}    				
			 			
			// Fill second string
			String secondString = MARK_COST + returnData.getCost() + MARK_SPACE + MARK_COUNT + returnData.getCount(); 
			byte [] convertedSecondString = secondString.getBytes("cp1251");
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
