package ru.pricechecker.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileReader {

	public static final String ERROR_STRING_FORMAT = "Wrong format of string: ";
	public static final String ERROR_BARCODE_NOT_FOUND = "Barcode not found: ";
	public static final String LINE_DELIMETER = ";";
	
	private static Map<String, LoadData> fileData = null;
	private static String path = null;
	private static long lastFileModification = 0;
	private static String currentDelimeter = LINE_DELIMETER;
	
	public static boolean readFile(String path, String delimeter){
		// Set user delimeter
		currentDelimeter = delimeter != null? delimeter : LINE_DELIMETER;
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
	
	public static Map<String, LoadData> getFileData(){
		return fileData;
	}

	
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
