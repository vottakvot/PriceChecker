package ru.pricechecker.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.pricechecker.main.FileReader.LoadData;

/**
 * @author USER
 *
 */
public class TCPServer {

	/**
	 *    Message for error
	 */
	public static final String ERRORS_SERVER_PORT_BUSY = "Server port is busy: ";
	
	/**
	 *   Message for error
	 */
	public static final String ERRORS_UNKNOWN = "Unknown server error: ";
	
	/**
	 *   Message for error
	 */
	public static final String ERROR_PORT_CONNECTION = "Port connection error: ";
	
	/**
	 *   Delay before close connection
	 */
	private static final int WAIT_RESPONSE_WRITE = 200;
	
	/**
	 * For check server open
	 */
	private volatile boolean isServerOpen = false;
	
	/**
	 * For check server running
	 */
	private volatile boolean isServerRunning = false;
	
	/**
	 * Open socket
	 */
	private ServerSocket serverSocket = null;
	
	/**
	 * Server port
	 */
	private int port;
	
	/**
	 * @param port for open
	 */
	public TCPServer(int port){
		this.port = port;
	}
	
	/**
	 * For server start
	 * @return true or false if successful
	 */
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
	
	/**
	 * For server handle
	 */
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
	
	/**
	 * For server stop
	 */
	public void stopServer(){
		try {
			serverSocket.close();
			isServerOpen = false;
		} catch (Exception e) {
			isServerOpen = false;
			FileLogging.toLog(TCPServer.class.getSimpleName() + " - startRequestHandle: " + e.getMessage());
		}
	}
	
	/**
	 * @return true or false server run
	 */
	public boolean isServerRunning() {
		return isServerRunning;
	}

	/**
	 * @param isServerRunning set for server stop
	 */
	public void setServerRunning(boolean isServerRunning) {
		this.isServerRunning = isServerRunning;
	}
	
	/**
	 * @return true or false server open
	 */
	public boolean isServerOpen() {
		return isServerOpen;
	}
	

	/**
	 * Handle and receive result in separate thread
	 * @author USER
	 *
	 */
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
            	// Output for visual
	            System.out.println(	"TIME: " + new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss").format(new Date()) + " - " + 
	            					"IP: " + requestInfo.get(PriceCheckerUtil.POSITION_IP) + "; " + 
	            					"BARCODE: " + requestInfo.get(PriceCheckerUtil.POSITION_BARCODE)  + ";"); 
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
