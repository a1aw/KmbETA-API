package com.github.mob41.kmbeta.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;

/**
 * A class for separating server time fetched from KMB ETA Server.
 * 
 * This class is probably useless for API users.
 * @author Anthony
 *
 */
public class ServerTime {
	
	private String serverTime = null;
	
	public ServerTime(String serverTime){
		this.serverTime = serverTime;
	}
	
	public ServerTime(){
		this.serverTime = getServerTime();
	}
	
	/***
	 * Get Server Time (Hour Part)
	 * 
	 * @return Integer, returns -1 if <code>serverTime</code> is <code>null</code>
	 */
	public int getServerHour(){
		if (serverTime == null){
			return -1;
		}
		int hour = Integer.parseInt(serverTime.substring(11, 13));
		return hour;
	}
	
	/***
	 * Get Server Time (Minute Part)
	 * 
	 * @return Integer, returns -1 if <code>serverTime</code> is <code>null</code>
	 */
	public int getServerMin(){
		if (serverTime == null){
			return -1;
		}
		int min = Integer.parseInt(serverTime.substring(14, 16));
		return min;
	}
	
	/***
	 * Get Server Time (Seconds Part)
	 * 
	 * @return Integer, returns -1 if <code>serverTime</code> is <code>null</code>
	 */
	public int getServerSec(){
		if (serverTime == null){
			return -1;
		}
		
		int sec = Integer.parseInt(serverTime.substring(16, 18));
		return sec;
	}
	
	/**
	 * Get the Whole Server Time (e.g. 15:30:00)
	 * 
	 * @return String
	 */
	public String getWholeTime(){
		return getServerHour() + ":" + getServerMin() + ":" + getServerSec();
	}
	
	/**
	 * Get the raw fetched server time (e.g. 2016-04-18 19:29:06)
	 * 
	 * @return String
	 */
	public String getRawServerTime(){
		return serverTime;
	}
	
//Static functions
	
	/***
	 * Get KMB data feed server time. And save to memory for other functions<br>
	 * <br>
	 * <b>Must be called before any arrival calculating events.</b>
	 * <br>
	 * e.g. 23:45:33
	 * @return String
	 */
	public static String getServerTime(){
		try {
			String datafeedurl = ArrivalManager.ETA_DATAFEED_SERVER_URL + "GetData.ashx?type=Server_T";
			URL url = new URL(datafeedurl);
			URLConnection connection = url.openConnection();
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			JSONArray kmbJson = new JSONArray(builder.toString());
			String datetime = (String) kmbJson.getJSONObject(0).get("stime");
			return datetime;
		} catch (Exception e){
			return null;
		}
	}

}
