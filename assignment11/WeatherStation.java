package assignment11;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import assignment4.MsgReceiver.type;
import jssc.SerialPortException;
import studio4.aurdinojava.SerialComm;

public class WeatherStation {

	int location;
	static long timeThreshold = 60000;
	static long storeTime = 0;
	static boolean lock;
	int previousLocation;
	
	public static void main(String[] args) throws Exception {
		// Create a new instance of Weather Station


		WeatherStation w = new WeatherStation();
		w.port = new SerialComm("COM7");
		// Based on the name of the instance created above, call xx.sendGet().
		// This will test to the function we'll be creating below.
		w.run();
		

	}
	
	public void run() throws Exception{
		while(true){
			if((System.currentTimeMillis() - storeTime) > timeThreshold || lock == true){
				lock = false;
				storeTime = System.currentTimeMillis();
				sendGet();
			}
			if(port.available()){
				if(port.readByte(false) == '!'){
					int p1 = ((port.readByte(false)&0xff) << 24);
					int p2 = ((port.readByte(false)&0xff) << 16);
					int p3 = ((port.readByte(false)&0xff) << 8);
					int p4 = (port.readByte(false)&0xff);
					int pSum = p1 + p2 + p3 + p4;
					if(pSum <= (1023/3)){
						location = 1;
					}
					else if(pSum > (1023/3) && pSum <= (1023/2)){
						location = 2;
					}
					else{
						location = 3;
					}
					if(previousLocation != location){
						lock = true;
					}
				}
			}	
		}
	}
	SerialComm port;
	String desString;
	// HTTP GET request
	private void sendGet() throws Exception {
		

		// Create a string that contains the URL you created for Lopata Hall in Studio 10
		// Use the URL that DOES NOT have the timestamp included.
		// Since we only need the current data (currently) you can use the API to exclude all of the excess blocks (REQUIRED).
		// Instructions to do that are here: https://darksky.net/dev/docs/forecast
		// Test this new URL by pasting it in your web browser. You should only see the information about the current weather.
		String url;
		if(location == 1){
			url = "https://api.darksky.net/forecast/ca3b3a324f5339fef6e7d966d74f2c23/38.649196,90.306099";
			//System.out.println("1");
			previousLocation = location;
		}
		else if(location == 2){
			url = "https://api.darksky.net/forecast/ca3b3a324f5339fef6e7d966d74f2c23/39.6119444444,126.849166667";
			//System.out.println("2");
			previousLocation = location;
		}
		else{
			url = "https://api.darksky.net/forecast/ca3b3a324f5339fef6e7d966d74f2c23/40.7829,73.9654";
			//System.out.println("3");
			previousLocation = location;
		}
		// Create a new URL object with the URL string you defined above. Reference: https://docs.oracle.com/javase/7/docs/api/java/net/URL.html

		
		
		URL myURL = new URL(url);

		// Follow the instructions in the URL API to open this connection.
		// Cast this to a HttpURLConnection and save it in a new HttpURLConnection object.

		HttpURLConnection myURLConnection =  (HttpURLConnection) myURL.openConnection();


		// Use the HttpURLConnection API to setup the HttpURLConnection you defined above.
		// Reference: https://docs.oracle.com/javase/7/docs/api/java/net/HttpURLConnection.html
		// Set the request method.
		myURLConnection.setRequestMethod("GET");


		// Set the request property "User-Agent" to the User-Agent you saw in Wireshark when you did the first exercise in studio.
		// Repeat the quick wireshark example if you've forgotten. It should be in the form "xxxxxxx/#.#"

		myURLConnection.setRequestProperty("user agent", "Mozilla/5.0");

		// To debug, get and print out the response code.
		//System.out.println(myURLConnection.getResponseCode());


		// The rest of the code should be much more familiar.
		// Create an InputStream that gets the input stream from our HttpURLConnection object.
		InputStream stream = myURLConnection.getInputStream();


		// Wrap it in a DataInputStream
		BufferedReader buff = new BufferedReader(new InputStreamReader(stream));


		// Read a line and save it in a string
		String s = null;
		while(buff.ready()){
			s = buff.readLine();
		}



		// Close the InputStream
		buff.close();


		// Using string manipulation tools, pull out the string between quotes after "icon:"
		// For example: "summary":"Clear","icon":"clear-day","nearestStormDistance":27
		// You should pull out JUST "clear-day"
		String[] s2 = s.split("[,]");
		String s3 = s2[6];
		String[] s4 = s3.split("icon");
		String s5 = s4[1];
		String[]s6 = s5.split(":");
		String s7 = s6[1];
		desString = s7;
		desString = desString.substring(1, desString.length()-1);
		//System.out.println(desString);

		// You will set this char (in a switch statement) to one of the 5 types of weather. (Nothing TODO here)
		char weatherChar = '\0';

		// Create a switch statement based on the string that contains the description (ex. clear-day)
		// The switch statement should be able to handle all 10 of the icon values from the API: https://darksky.net/dev/docs/response
		// If the value is any of the cloudy ones, set weatherChar to C
		// If the value is fog, set it to F
		// If the value if wind, set it to W
		// If the value is any of the clear ones, set it to S
		// If the value is any type of precipitation, set it to P


		switch(desString){
		case "clear-day": 
			weatherChar = 'S';
			break;
		case "clear-night":
			weatherChar = 'S';
			break;
		case "rain":
			weatherChar = 'P';
			break;
		case "snow":
			weatherChar = 'P';
			break;
		case "sleet":
			weatherChar = 'P';
			break;
		case "wind":
			weatherChar = 'W';
			break;
		case "fog":
			weatherChar = 'F';
			break;
		case "cloudy":
			weatherChar = 'C';
			break;
		case "partly-cloudy-day":
			weatherChar = 'C';
			break;
		case "partly-cloudy-night":
			weatherChar = 'C';
			break;

		}
		//InputStreamReader q = new InputStreamReader(System.in);
		//BufferedReader b = new BufferedReader(q);
		//String var = null;
		//var = b.readLine();	
		try {
			port.writeByte(false, (byte) '!');
			port.writeByte(false, (byte) weatherChar);

		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}


// Now you're ready to implement this into your past code to send it to the Arduino.
// You also have to make a couple modifications to handle the switch location requests from Arduino.
// Choose three locations or more, but make sure one is Lopata Hall.

