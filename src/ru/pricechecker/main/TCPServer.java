package ru.pricechecker.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import ru.pricechecker.main.FileReader.LoadData;

public class TCPServer {

	public static final String ERRORS_SERVER_PORT_BUSY = "Server port is busy: ";
	public static final String ERRORS_UNKNOWN = "Unknown server error: ";
	public static final String ERROR_PORT_CONNECTION = "Port connection error: ";
	private static final int WAIT_RESPONSE_WRITE = 200;
	
	private volatile boolean isServerOpen = false;
	private volatile boolean isServerRunning = false;
	private ServerSocket serverSocket = null;
	private int port;
	
	public TCPServer(int port){
		this.port = port;
	}
	
	// For server start
	public boolean startServer(){
		// Try open current port
		try {
			if(!isServerOpen && !isServerRunning){
				serverSocket = new ServerSocket(port);
				isServerOpen = true;
			}
				
		} catch (IOException e) {
			isServerOpen = false;
			FileLogging.toLog(TCPServer.class.getSimpleName() + " - startServer: " + e.getMessage());
			FileLogging.toLog(ERRORS_SERVER_PORT_BUSY + port);
			return false;
		}
		
		return true;
	}
	
	// For server handle
	public void startRequestHandle(){
		// Get connections
		try {
			isServerRunning = true;	
		    while(isServerRunning){
		        try {
		        	FileReader.asyncCheckFile();
					Socket clientSocket = serverSocket.accept();
					new ClientServiceThread(clientSocket).start();
				} catch (IOException e) {
					FileLogging.toLog(ERROR_PORT_CONNECTION + e.getMessage());
				}
		      }
		} catch (Exception e) {
			isServerRunning = false;
			FileLogging.toLog(ERRORS_UNKNOWN + e.getMessage());
		}
	}
	
	// For server stop
	public void stopServer(){
		try {
			serverSocket.close();
			isServerOpen = false;
		} catch (Exception e) {
			isServerOpen = false;
			FileLogging.toLog(TCPServer.class.getSimpleName() + " - startRequestHandle: " + e.getMessage());
		}
	}
	
	public boolean isServerRunning() {
		return isServerRunning;
	}

	public void setServerRunning(boolean isServerRunning) {
		this.isServerRunning = isServerRunning;
	}
	
	public boolean isServerOpen() {
		return isServerOpen;
	}
	

	class ClientServiceThread extends Thread { 
	      private Socket clientSocket;
	      			
	      public ClientServiceThread(Socket clientSocket){ 
	         this.clientSocket = clientSocket; 
	      } 
			
	      public void run(){ 
	         // Get data from device
	    	 BufferedReader in = null;
	    	 // Send data to device
	         OutputStream out = null; 
	         
	         try { 
	            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	            out = clientSocket.getOutputStream();
	            	           	            
	            // Client barcode
	            String dataFromDevice = in.readLine();
	            // Handle request data
	            List<String> requestInfo = PriceCheckerUtil.handleCheckerRequest(dataFromDevice, clientSocket.getInetAddress().getHostAddress());   
            	// Search in file array
	            LoadData item = FileReader.getItem(requestInfo.get(PriceCheckerUtil.POSITION_BARCODE));            
	            // Get return data
	            byte[] returnData = PriceCheckerUtil.getCheckerDataForTwoString(item);
	            // Write bytes to device
	            out.write(returnData);
	            out.flush(); 
	            	                      
	            // Wait response. This wrong, must receive response from device!
	            Thread.sleep(WAIT_RESPONSE_WRITE);
	            
	         } catch(Exception e) { 
	        	 FileLogging.toLog(TCPServer.class.getSimpleName() + " - ClientServiceThread: " + e.getMessage());
	         } finally { 
	            try { 
	               out.close();
	               in.close(); 
	               clientSocket.close();  
	            } catch(IOException ioe) { 
	            	FileLogging.toLog(TCPServer.class.getSimpleName() + " - ClientServiceThread: " + ioe.getMessage());
	            } 
	         } 
	      } 
	   } 
}
