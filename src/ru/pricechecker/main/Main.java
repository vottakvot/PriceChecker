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
				System.out.println("Обработка сообщений...");
				tcpServer.startRequestHandle();
			} else {
				System.out.println("Не удаётся открыть порт: " + arguments.get(FileSettings.ARG_PORT));
				printHelp();
				System.exit(2);
			}
				
		} else {
			System.out.println("Не удаётся прочитать файл: " + arguments.get(FileSettings.ARG_FILE_PATH));
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
		System.out.println("Пример использования сервера для PriceChecker:");
		System.out.println("Ключи:");
		System.out.println("	" + FileSettings.ARG_FILE_PATH + " - путь к данным");
		System.out.println("	" + FileSettings.ARG_PORT + " - порт для обмена");
		System.out.println("	" + FileSettings.ARG_DELIMITER + " - разделитель полей");
		System.out.println("");
		System.out.println("Пример:	PriceChecker.exe --file \"C:\\datafile.txt\" --port 2004");
		System.out.println("");
		System.out.println("Формат файла: ПОЛЕ_ШТРИХ-КОДА<delimeter>НАИМЕНОВАНИЕ<delimeter>ЦЕНА<delimeter>КОЛ_ВО");
		System.out.println("");
		System.out.println("В приоритете будут работать аргументы вызова.");
		System.out.println("Если нет аргументов, то поиск файла с конфигом CONFIG.xml.");
		System.out.println("Иначе выход из программы.");
	}
}
