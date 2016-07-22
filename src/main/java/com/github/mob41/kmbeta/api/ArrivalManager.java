package com.github.mob41.kmbeta.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import com.github.mob41.kmbeta.exception.CouldNotLoadDatabaseException;
import com.github.mob41.kmbeta.exception.InvalidArrivalTargetException;
import com.github.mob41.kmbeta.exception.NoETADataFetchedError;
import com.github.mob41.kmbeta.exception.NoServerTimeFetchedError;

public class ArrivalManager {
	
//Constants
	
	public static final String lastdeparted_msg = "The last bus has departed from this bus stop";
	
	public static final String ETA_SERVER_URL = "http://etav2.kmb.hk/";
	
	public static final String ETA_DATAFEED_SERVER_URL = "http://etadatafeed.kmb.hk:1933/";
	
	public static final int ENGLISH_LANG = 0;
	
	public static final int CHINESE_LANG = 1;
	
	public static final int DB_FETCH_DIRECTLY = 3;
	
	public static final int DB_LOAD_FROM_WEB = 4;
	
	public static final int DB_LOAD_FROM_FILE = 5;
	
	private static BusDatabase busDatabase; //Only loads once. Avoid loading different instances of database.
	
	private static int db_last_choice = -1;
	
//Instance Memory
	
	private JSONObject data = null;
	
	private ServerTime srvt = null;
	
	private ArrivalTime arrt = null;
	
	private String busno = null;
	
	private String stop_code = null;
	
	private int stop_seq = -1;
	
	private int bound = -1;
	
	private int lang = -1;
	
//Init
	
	/***
	 * Creates a new <code>ArrivalManager</code> instance and show logs<br>
	 * <br>
	 * By default, it will load the database from the server by downloading it and save the JSON to memory.<br>
	 * It can be found at <a href="https://db.kmbeta.ml/">https://db.kmbeta.ml</a>
	 * @param busno Bus No.
	 * @param stop_code Bus Stop Code (e.g. WO04N12500), probably it is specified from a BUS DB source.
	 * @param bound Bus Direction/Bound (1 or 2)
	 * @param language Language to be selected <code>ArrivalManager.ENGLISH_LANG</code> or <code>ArrivalManager.CHINESE_LANG</code>
	 * @throws InvalidArrivalTargetException If the specified target arrival was invalid
	 * @throws CouldNotLoadDatabaseException If the API could not load the database
	 */
	public ArrivalManager(String busno, String stop_code, int bound, int language) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		this(busno, stop_code, bound, language, DB_LOAD_FROM_WEB, null, false, true);
	}
	
	/***
	 * Creates a new <code>ArrivalManager</code> instance.<br>
	 * <br>
	 * By default, it will load the database from the server by downloading it and save the JSON to memory.<br>
	 * It can be found at <a href="https://db.kmbeta.ml/">https://db.kmbeta.ml</a>
	 * @param busno Bus No.
	 * @param stop_code Bus Stop Code (e.g. WO04N12500), probably it is specified from a BUS DB source.
	 * @param bound Bus Direction/Bound (1 or 2)
	 * @param language Language to be selected <code>ArrivalManager.ENGLISH_LANG</code> or <code>ArrivalManager.CHINESE_LANG</code>
	 * @param showLog Show log
	 * @throws InvalidArrivalTargetException If the specified target arrival was invalid
	 * @throws CouldNotLoadDatabaseException If the API could not load the database
	 */
	public ArrivalManager(String busno, String stop_code, int bound, int language, boolean showLog) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		this(busno, stop_code, bound, language, DB_LOAD_FROM_WEB, null, false, showLog);
	}
	
	/***
	 * Creates a new <code>ArrivalManager</code> instance.<br>
	 * <br>
	 * This will load the database from file.
	 * @param busno Bus No.
	 * @param stop_code Bus Stop Code (e.g. WO04N12500), probably it is specified from a BUS DB source.
	 * @param bound Bus Direction/Bound (1 or 2)
	 * @param language Language to be selected <code>ArrivalManager.ENGLISH_LANG</code> or <code>ArrivalManager.CHINESE_LANG</code>
	 * @param parent The class resource parent.
	 * @param showLog Show log
	 * @throws InvalidArrivalTargetException If the specified target arrival was invalid
	 * @throws CouldNotLoadDatabaseException If the API could not load the database
	 */
	public ArrivalManager(String busno, String stop_code, int bound, int language, Object parent, boolean loadFromClassResources, boolean showLog) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		this(busno, stop_code, bound, language, DB_LOAD_FROM_FILE, parent, loadFromClassResources, showLog);
	}
	
	/***
	 * Creates a new <code>ArrivalManager</code> instance.<br>
	 * <br>
	 * This will load the database from file.
	 * @param busno Bus No.
	 * @param stop_code Bus Stop Code (e.g. WO04N12500), probably it is specified from a BUS DB source.
	 * @param bound Bus Direction/Bound (1 or 2)
	 * @param language Language to be selected <code>ArrivalManager.ENGLISH_LANG</code> or <code>ArrivalManager.CHINESE_LANG</code>
	 * @param parent The class resource parent
	 * @throws InvalidArrivalTargetException If the specified target arrival was invalid
	 * @throws CouldNotLoadDatabaseException If the API could not load the database
	 */
	public ArrivalManager(String busno, String stop_code, int bound, int language, Object parent, boolean loadFromClassResources) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		this(busno, stop_code, bound, language, DB_LOAD_FROM_FILE, parent, loadFromClassResources, true);
	}

	/***
	 * Creates a new <code>ArrivalManager</code> instance.<br>
	 * <br>
	 * This function contains all the parameters. Read the documentation/wiki before continue.<br>
	 * Or simply use another overloaded functions.
	 * @param busno Bus No.
	 * @param stop_code Bus Stop Code (e.g. WO04N12500), probably it is specified from a BUS DB source.
	 * @param bound Bus Direction/Bound (1 or 2)
	 * @param language Language to be selected <code>ArrivalManager.ENGLISH_LANG</code> or <code>ArrivalManager.CHINESE_LANG</code>
	 * @param loadFromWhere Specify the <code>ArrivalManager.DB_*</code> constant fields, to choose the DB source.
	 * @param classParent If <code>ArrivalManager.DB_LOAD_FROM_FILE</code> is specified, and load from class resources, you can specify a class parent.
	 * @param fromClassResources Only if <code>ArrivalManager.DB_LOAD_FROM_FILE</code> is specified, specify whether the file should be loaded from class resources
	 * @throws InvalidArrivalTargetException If the specified target arrival was invalid
	 * @throws CouldNotLoadDatabaseException If the API could not load the database
	 */
	public ArrivalManager(String busno, String stop_code, int bound, int language, int loadFromWhere, Object classParent, boolean fromClassResources, boolean showLog) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		//Check whether the database in the memory is null or not
		if (busDatabase == null || busDatabase.getRoutesNames() == null || db_last_choice != loadFromWhere){
			//If null, load the database
			busDatabase = new BusDatabase();
			
			if (showLog){
				System.out.println(
						"ArrivalManager: Database is not loaded. Loading now...\n" +
						"ArrivalManager: It might take a while..."
						);
			}
			
			//Save start time in ms (for calculating estimated time)
			long startTime = System.currentTimeMillis();
			
			//Load Database
			boolean loaded = false;
			switch (loadFromWhere){
			case DB_FETCH_DIRECTLY:
				loaded = true;
				break;
			case DB_LOAD_FROM_WEB:
				loaded = busDatabase.loadWebDB();
				break;
			case DB_LOAD_FROM_FILE:
				loaded = busDatabase.loadDatabase(classParent, fromClassResources);
				break;
			default:
				if (showLog){
					System.out.println("ArrivalManager: Invalid field specified: " + loadFromWhere);
				}
				throw new CouldNotLoadDatabaseException("Invalid field specified: " + loadFromWhere);
			}
			
			//Save end time in ms (for calculating estimated time)
			long endTime = System.currentTimeMillis();
			
			if (showLog){
				System.out.println(
						"ArrivalManager: " + 
						(loaded ? "Loaded database. Took " + (endTime - startTime) + " ms to load." : "Could not load database. Check your internet connection or the database file placed near your application.")
						);
			}
			
			if (!loaded){
				throw new CouldNotLoadDatabaseException("Could not load database. Check your internet connection or the database file placed near your application.");
			}
			db_last_choice = loadFromWhere;
		}
		
		//Check is language parameter valid
		if (language != 0 && language != 1){
			throw new InvalidArrivalTargetException("Invalid language integer \"" + language + "\". It should be specified from ArrivalManager.ENGLISH_LANG or ArrivalManger.CHINESE_LANG");
		}
		
		if (getBusDatabase().getRouteIndex(busno) == -1){
			throw new InvalidArrivalTargetException("Could not find the bus no \"" + busno + "\" in the database.");
		}
		
		//Find Stop sequence
		int stop_seq = getBusDatabase().getStopSequence(busno, bound, stop_code);
		if (stop_seq == -1){
			throw new InvalidArrivalTargetException("Could not find the stop sequence by BusNo:"
					+ " \"" + busno + "\", StopCode: \"" + stop_code + ", Bound: \"" + bound + "\"");
		}
		
		this.busno = busno;
		this.stop_seq = stop_seq;
		this.stop_code = stop_code;
		this.bound = bound;
		this.lang = language;
		
		fetchNewData();
	}
	
//Web Request
	
	/**
	 * Send a request to fetch a new bus arrival ETA data
	 * @return JSONObject with the bus arrival ETA
	 */
	public JSONObject fetchNewData(){
		data = getETAdata(busno, stop_code, lang, bound, stop_seq);
		srvt = new ServerTime();
		arrt = new ArrivalTime(data);
		return data;
	}
	
//Getters
	
	public BusDatabase getBusDatabase(){
		return busDatabase;
	}
	
	public String getBusNo(){
		return busno;
	}
	
	public String getStopCode(){
		return stop_code;
	}
	
	public int getStopSeq(){
		return stop_seq;
	}
	
	public int getBound(){
		return bound;
	}
	
	public int getLang(){
		return lang;
	}
	
//Arrival Time Calculation
	
	public ServerTime getServerTime(){
		return srvt;
	}
	
	public ArrivalTime getArrivalTime(){
		return arrt;
	}
	
	/**
	 * Get remaining arrival minute formatted<br>
	 * <br>
	 * Returns a string with this format:<br>
	 * <b>RR min(s)</b><br>
	 * which RR is the remaining time.<br>
	 * <br>
	 * Different cases to return another string:<br>
	 * <b>---</b>: If <code>srvhr</code>, <code>srvmin</code>, <code>arrhr</code>, <code>arrmin</code> is -1<br>
	 * <b>Arrived</b>: If <code>remainMin</code> smaller or equal to 0
	 * @return A String with the format mentioned above
	 */
	public String getRemainingArrivalMinuteText(){
		int srvhr = srvt.getServerHour();
		int srvmin = srvt.getServerMin();
		int arrhr = arrt.getArrivalHour();
		int arrmin = arrt.getArrivalMin();
		int remainMin = 0;
		
		if (srvhr == -1 || srvmin == -1 || arrhr == -1 || arrmin == -1){
			return "Invalid";
		} else if (arrhr == -3 || arrmin == -3){
			return "End";
		}
		
		if (arrhr > srvhr){
			remainMin = (60 - srvmin) + arrmin;
		}
		else
		{
			remainMin = arrmin - srvmin;
		}
		
		String output;
		if (remainMin <= 0){
			output = "Arrived";
		} else {
			output = remainMin + " min(s)";
		}
		return output;
	}
	
	/***
	 * Calculates the remaining arrival minutes left.<br>
	 * <br>
	 * <b>Using the time from KMB Datafeed Server</b>
	 * 
	 * @return Remaining minutes
	 */
	public int getRemainingArrivalTime(){
		int srvhr = srvt.getServerHour();
		int srvmin = srvt.getServerMin();
		int arrhr = arrt.getArrivalHour();
		int arrmin = arrt.getArrivalMin();
		int remainMin = 0;
		
		if (arrhr <= -1 || arrmin <= -1 || srvhr <= -1 || srvmin <= -1){
			return -1;
		}
		
		if (arrhr > srvhr){
			remainMin = (60 - srvmin) + arrmin;
		}
		else
		{
			remainMin = arrmin - srvmin;
		}
		
		return remainMin;
	}
	
	/**
	 * Returns a formatted arrival time text.<br>
	 * <br>
	 * The arrival time is formatted to: <code>HH:MM</code><br>
	 * If hour/minute is less than <code>10</code>, a <code>0</code> will be added to the front.<br>
	 * If the received response is equal to the last departed message, it will return <code>"End"</code>.<br>
	 * If the received response is invalid, it will return <code>"Invalid"</code>.<br>
	 * If no response received, it will probably return <code>"NoData"</code> or <code>"Failed"</code>.<br>
	 * If the separation of Hour and Minutes failed, it will return the raw arrival time.
	 * @return
	 * @throws NoETADataFetchedError
	 * @throws NoServerTimeFetchedError
	 */
	public String getArrivalTimeText() throws NoETADataFetchedError, NoServerTimeFetchedError{
		int hr;
		int min;
		
		if (data == null){
			throw new NoETADataFetchedError("No ETA Data was fetched before. Have you did \"fetchNewData()\" or initialized the ArrivalManager?");
		} else if (srvt == null){
			throw new NoServerTimeFetchedError("No Server Time was fetched before. Have you did \"fetchNewData()\" or initialized the ArrivalManager?");
		}
		if (arrt.getRawArrivalTime() == lastdeparted_msg){
			return "End";
		}
		else
		{
			hr = arrt.getArrivalHour();
			min = arrt.getArrivalMin();
		}
		if (hr == -3 || min == -3){
			return "End";
		} else if (hr <= -1 || min <= -1){
			return arrt.getRawArrivalTime();
		}
		String hour = hr < 10 ? "0" + hr : Integer.toString(hr);
		String minute = min < 10 ? "0" + min : Integer.toString(min);
		String output = hour + ":" + minute;
		return output;
	}
	
//Static functions
	
	/***
	 * Get ETA data from KMB server. And save to memory for other functions.<br>
	 * <br>
	 * <b>Must be called before any ETA reading events</b>
	 * @param route The bus number / route name / bus name
	 * @param busStopCode The bus-stop code
	 * @param lang Language to be received. KmbApi.ENGLISH_LANG / KmbApi.CHINESE_LANG
	 * @param bound Bus-Line Bound
	 * @param stop_seq Bus-stop sequence
	 * @return JSONObject
	 */
	public static JSONObject getETAdata(String route, String busStopCode, int lang, int bound, int stop_seq)
	{
		try {
			String language;
			switch (lang)
			{
			default:
			case 0:
				language = "en";
				break;
			case 1:
				language = "tc";
				break;
			}
			String kmbApi = ETA_SERVER_URL + "?action=geteta&lang=" + language + "&route=" + route + "&bound=" + 
							Integer.toString(bound) + "&stop=" + busStopCode + "&stop_seq=" + Integer.toString(stop_seq);
			URL url = new URL(kmbApi);
	        URLConnection connection = url.openConnection();
	        String line;
	        StringBuilder builder = new StringBuilder();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        while((line = reader.readLine()) != null) {
	            builder.append(line);
	        }
			JSONObject kmbJson = new JSONObject(builder.toString());
			return kmbJson;
		} catch (Exception e){
			return null;
		}
	}
}
