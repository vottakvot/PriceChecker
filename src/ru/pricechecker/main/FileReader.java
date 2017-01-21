package ru.pricechecker.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author USER
 *
 */
public class FileReader {

	/**
	 * Message for error
	 */
	public static final String ERROR_STRING_FORMAT = "Wrong format of string: ";
	
	/**
	 * Message for error
	 */
	public static final String ERROR_BARCODE_NOT_FOUND = "Barcode not found: ";
	
	/**
	 * Default delimiter
	 */
	public static final String LINE_DELIMITER = ";";
	
	/**
	 * Content data
	 */
	private static Map<String, LoadData> fileData = null;
	
	/**
	 * Path to data file
	 */
	private static String path = null;
	
	/**
	 * Mark for last file update
	 */
	private static long lastFileModification = 0;
	
	/**
	 * Delimiter for fields
	 */
	private static String currentDelimeter = LINE_DELIMITER;
	
	/**
	 * @param path from read content data
	 * @param delimiter for fields
	 * @return true or false if successful
	 */
	public static boolean readFile(String path, String delimiter){
		// Set user delimeter
		currentDelimeter = delimiter != null? delimiter : LINE_DELIMITER;
		// Save path for interval's checking
		FileReader.path = path;
		File dataFile = new File(path);
		// Check file, if doesn't exist
		if(!dataFile.exists())
			return false;
		
		// Read file
		Scanner scanner = null;
	    try {
	    	// For file last check
	    	BasicFileAttributes attributes = Files.readAttributes(dataFile.toPath(), BasicFileAttributes.class);
	    	lastFileModification = attributes.lastModifiedTime().toMillis();
	    	// For file read
		    scanner = new Scanner(dataFile);
		    // Collect new data
		    Map<String, LoadData> tempData = new HashMap<>();
		    
		    // Get line
		    int i = 0;
	        while(scanner.hasNextLine()){
	        	String initString = scanner.nextLine();
	        	String[] oneString = initString.split(currentDelimeter);
	        	++i;
	        	
	        	// String must contain 4 part!
	        	if(oneString.length == 4){
	        		// First part must barcode!
	        		LoadData item = new LoadData(oneString[1].trim(), oneString[2].trim(), oneString[3].trim());
	        		tempData.put(oneString[0].trim(), item);
	        	} else {
	        		FileLogging.toLog(FileReader.class.getSimpleName() + " - readFile: " + ERROR_STRING_FORMAT + i);
	        		FileLogging.toLog(FileReader.class.getSimpleName() + " - readFile: " + initString);
	        	}
	        }
	        
	        // Set new data
	        if(tempData.size() > 0)
	        	fileData = tempData;
	        
	    } catch (Exception e) {
	    	FileLogging.toLog(FileReader.class.getSimpleName() + " - readFile: " + e.getMessage());
			return false;
		} finally {
			try {
				scanner.close();
			} catch (Exception e) {
				FileLogging.toLog(FileReader.class.getSimpleName() + " - readFile: " + e.getMessage());
				return false;
			}
	    }		
	    
		return fileData != null && fileData.size() > 0? true : false;
	}
	
	/**
	 * @param barcode for search
	 * @return data for current barcode
	 */
	public static LoadData getItem(String barcode){
		try {
			LoadData item = fileData.get(barcode.trim());
			if(item == null)
				FileLogging.toLog(FileReader.class.getSimpleName() + " - " + ERROR_BARCODE_NOT_FOUND + barcode);
			return item;
		} catch (RuntimeException e) {
			return null;
		}
	}
	
	/**
	 * For asynchronous update content data, if file was update
	 */
	public static void asyncCheckFile(){
		// Separate thread for load new data
		Thread checkThread = new Thread(new Runnable() {
			@Override
			public void run(){
				try {
					File dataFile = new File(FileReader.path);
					BasicFileAttributes attributes = Files.readAttributes(dataFile.toPath(), BasicFileAttributes.class);
					if(FileReader.lastFileModification != attributes.lastModifiedTime().toMillis()){
						FileReader.readFile(FileReader.path, FileReader.currentDelimeter);
					}
					
				} catch (Exception e) {
					FileLogging.toLog(FileReader.class.getSimpleName() + " - " + e.getMessage());
				}
			}
		});
		
		// Start thread
		checkThread.start();
	}
	
	/**
	 * @return current content data or null
	 */
	public static Map<String, LoadData> getFileData(){
		return fileData;
	}

	
	/**
	 * Data for internal exchange
	 * @author USER
	 */
	public static class LoadData {
		
		private String name;
		private String count;
		private String cost;
		
		public LoadData(String name, String cost, String count){
			this.name = name;
			this.count = count;
			this.cost = cost;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCount() {
			return count;
		}

		public void setCount(String count) {
			this.count = count;
		}

		public String getCost() {
			return cost;
		}

		public void setCost(String cost) {
			this.cost = cost;
		}
	}
}
