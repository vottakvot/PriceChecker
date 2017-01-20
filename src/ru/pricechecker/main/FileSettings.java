package ru.pricechecker.main;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class FileSettings {
	
	public static final String ARG_FILE_PATH = "--file";
	public static final String ARG_PORT = "--port";
	public static final String ARG_DELIMETER = "--delimeter";
		
	private static final String CONFIG_FILE_NAME = "CONFIG.xml";
	private static final String TAG_ROOT = "Settings";
	
	public static boolean writeToFile(Map<String, String> settings) {
		try {
			// Set root tag
			Element company = new Element(TAG_ROOT);
			Document document = new Document();
			document.setRootElement(company);

			// Set tags
			for(String key : settings.keySet()){
				Element contentTags = new Element(key.replaceAll("[^A-Za-z]+", ""));
				contentTags.addContent(settings.get(key));
				document.getRootElement().addContent(contentTags);
			}
			
			// Output to file
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(document, new FileWriter(getDefaultPath()));
		  } catch (Exception e) {
			  FileLogging.toLog(FileSettings.class.getName() + " - writeToFile: " + e.getMessage());
			  return false;
		  	}

		return true;
	}

	public static Map<String, String> readFromFile() {
		try {
			File fileSettings = new File(getDefaultPath());		      
			if(!fileSettings.exists())
				 return null;
			
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(fileSettings);
			Element classElement = document.getRootElement();
			// All tags
			List<Element> allSettingsFromXML = classElement.getChildren();
			// Return data
			Map<String, String> arguments = new HashMap<>();
			// Check all tags
			for (int i = 0; i < allSettingsFromXML.size(); i++) {
				Element currentSetting = allSettingsFromXML.get(i);
				
				// Path
				if(ARG_FILE_PATH.contains(currentSetting.getName()))
					arguments.put(ARG_FILE_PATH, currentSetting.getValue());
				
				// Delimeter
				if(ARG_DELIMETER.contains(currentSetting.getName()))
					arguments.put(ARG_DELIMETER, currentSetting.getValue());
				
				// Port
				if(ARG_PORT.contains(currentSetting.getName()))
					arguments.put(ARG_PORT, currentSetting.getValue());
			}
		
		    return arguments;
		    
		} catch(Exception e){
			return null;
		}
	}
	
	private static String getDefaultPath(){
		return System.getProperty("user.dir") + File.separator + CONFIG_FILE_NAME;
	} 
}
