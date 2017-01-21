## PriceChecker
``` groovy
Eclipse+TCP+JDOM2
```


## About
``` groovy
This app is TCP server for Shtrih-M PriceChecker device. 
Exchange with the device takes place according to the TCP protocol.
Use follow commmand: AEh for load 2 string message;
```

<b><u>Example:</u></b> 
      
      ...
      // Command
      answer[0] = (byte)0xAE;
			
      // Output time on checker screen
      answer[1] = (byte)50;
			
      // String length. Default 80
      answer[2] = (byte)80;
			
      // Type screen output. Running or static
      answer[3] = (byte)0xF2;
		
      // Speed of first string
      answer[4] = (byte)2;
			
      // Speed of second string
      answer[5] = (byte)3;
			
      // Message priority. 5 is highest.
      answer[6] = (byte)5;   			
			
      // Length of first string. Max - 40
      answer[7] = (byte)40; 
			
      // Length of second string. Max - 40
      answer[8] = (byte)40; 
      ...
      
      
 <b><u>Input argumets:</u></b> 
 
      --file - path to data file;
      
      --port - port for exchange;
      
      --delimeter - for fields;
 
 
