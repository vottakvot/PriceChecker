package ru.pricechecker.main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileReader {

	public static final String ERROR_STRING_FORMAT = "Wrong format of string: ";
	public static final String ERROR_BARCODE_NOT_FOUND = "Barcode not found: ";
	
	public static final String LINE_SEPARATOR = ";";
	
	private static Map<String, LoadData> fileData = null;

	public static boolean readFile(String path){
		File dataFile = new File(path);
		// Check file, if doesn't exist
		if(!dataFile.exists())
			return false;
		
		// Read file
		Scanner scanner = null;
	    try {
		    scanner = new Scanner(dataFile);
		    fileData = new HashMap<>();
		    
		    // Get line
		    int i = 0;
	        while(scanner.hasNextLine()){
	        	String initString = scanner.nextLine();
	        	String[] oneString = initString.split(LINE_SEPARATOR);
	        	++i;
	        	
	        	// String must contain 4 part!
	        	if(oneString.length == 4){
	        		// First part must barcode!
	        		LoadData item = new LoadData(oneString[1].trim(), oneString[2].trim(), oneString[3].trim());
	        		fileData.put(oneString[0].trim(), item);
	        	} else {
	        		FileLogging.toLog(FileReader.class.getSimpleName() + " - readFile: " + ERROR_STRING_FORMAT + i);
	        		FileLogging.toLog(FileReader.class.getSimpleName() + " - readFile: " + initString);
	        	}
	        }
	        
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
