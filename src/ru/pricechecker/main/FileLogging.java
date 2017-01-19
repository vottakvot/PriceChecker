package ru.pricechecker.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileLogging {
	
	private static final String FILE_NAME = "PriceChecker-log.txt";
	private static String filePath = "";
	private static String internalError = "";
	
	public static boolean toLog(String strinToFile){
		PrintWriter out = null;
		try {
			// Default path
			if(filePath == "")
				filePath = System.getProperty("user.dir") + File.separator + FILE_NAME;
						
			// If this file was create yesterday, then delete it
			File checkFileData = new File(filePath);
			if(checkFileData.exists()){
		        BasicFileAttributes attributes = Files.readAttributes(checkFileData.toPath(), BasicFileAttributes.class);
		        
		        // Current day
		        Calendar calendar = Calendar.getInstance();
		        calendar.setTime(new Date());
		        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		        
		        // File day
		        calendar.setTime(new Date(attributes.creationTime().toMillis()));
		        int fileDay = calendar.get(Calendar.DAY_OF_MONTH);
		        
		        if(currentDay != fileDay)
		        	checkFileData.delete();
			}
			
			// Prefix for string
			String prefixStringToFile = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:  ").format(new Date());
			// Append string
			out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
		    // Write to file
			out.println(prefixStringToFile + strinToFile);
		} catch (Exception e) {
			internalError = e.getMessage();
			return false;
		} finally {
			try {
				out.close();
			} catch(Exception e){
				e.printStackTrace();
				internalError = e.getMessage();
				return false;
			}
		}	
		
		return true;
	}
	
	public static String getFilePath() {
		return filePath;
	}
	
	public static String getInternalError() {
		return internalError;
	}
}
