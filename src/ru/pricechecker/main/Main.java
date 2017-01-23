package ru.pricechecker.main;

import java.util.HashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		// Get args from command line, else load from file
		Map<String, String> arguments = getArgs(args);
		if(arguments == null || !arguments.containsKey(FileSettings.ARG_FILE_PATH) || !arguments.containsKey(FileSettings.ARG_PORT)){
			arguments = FileSettings.readFromFile();
			if(arguments == null || !arguments.containsKey(FileSettings.ARG_FILE_PATH) || !arguments.containsKey(FileSettings.ARG_PORT)){
				printHelp();
				System.exit(1);
			}
		} else {
			// Set new settings
			FileSettings.writeToFile(arguments);
		}
		
		// Load check data from file
		if(FileReader.readFile(arguments.get(FileSettings.ARG_FILE_PATH), arguments.get(FileSettings.ARG_DELIMITER))){
			TCPServer tcpServer = new TCPServer(Integer.parseInt(arguments.get(FileSettings.ARG_PORT)));
			if(tcpServer.startServer()){
				System.out.println("��������� ���������...");
				tcpServer.startRequestHandle();
			} else {
				System.out.println("�� ������ ������� ����: " + arguments.get(FileSettings.ARG_PORT));
				printHelp();
				System.exit(2);
			}
				
		} else {
			System.out.println("�� ������ ��������� ����: " + arguments.get(FileSettings.ARG_FILE_PATH));
			printHelp();
			System.exit(3);
		}
	}
		
	public static Map<String, String> getArgs(String[] args){
		try {
			Map<String, String> argsContainer = new HashMap<>();
			for(int i = 0; i < args.length; i++){
				// If this key for argument
				if(args[i].equals(FileSettings.ARG_FILE_PATH) || args[i].equals(FileSettings.ARG_PORT) || args[i].equals(FileSettings.ARG_DELIMITER))
					argsContainer.put(args[i], args[i + 1]);
			}
					
			return argsContainer;
		} catch(RuntimeException e){
			FileLogging.toLog(Main.class.getName() + " - getArgs: " + e.getMessage());
			return null;
		} 
	}
	
	public static void printHelp(){
		System.out.println("");
		System.out.println("");
		System.out.println("������ ������������� ������� ��� PriceChecker:");
		System.out.println("�����:");
		System.out.println("	" + FileSettings.ARG_FILE_PATH + " - ���� � ������");
		System.out.println("	" + FileSettings.ARG_PORT + " - ���� ��� ������");
		System.out.println("	" + FileSettings.ARG_DELIMITER + " - ����������� �����");
		System.out.println("");
		System.out.println("������:	PriceChecker.exe --file \"C:\\datafile.txt\" --port 2004");
		System.out.println("");
		System.out.println("������ �����: ����_�����-����<delimeter>������������<delimeter>����<delimeter>���_��");
		System.out.println("");
		System.out.println("� ���������� ����� �������� ��������� ������.");
		System.out.println("���� ��� ����������, �� ����� ����� � �������� CONFIG.xml.");
		System.out.println("����� ����� �� ���������.");
	}
}
