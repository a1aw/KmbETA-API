package com.mob41.kmbeta.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.json.JSONObject;

import com.mob41.kmbeta.exception.CouldNotLoadDatabaseException;
import com.mob41.kmbeta.exception.InvalidArrivalTargetException;
import com.mob41.kmbeta.exception.NoETADataFetchedError;
import com.mob41.kmbeta.exception.NoServerTimeFetchedError;

public class ArrivalManager {
	
//Constants
	
	public static final String lastdeparted_msg = "The last bus has departed from this bus stop";
	
	public static final String ETA_SERVER_URL = "http://etav2.kmb.hk/";
	
	public static final String ETA_DATAFEED_SERVER_URL = "http://etadatafeed.kmb.hk:1933/";
	
	public static final int ENGLISH_LANG = 0;
	
	public static final int CHINESE_LANG = 1;
	
	public static String[] BUS_NO;
	
//Database Bus Pairs Memory
	
	private static List<List<List<String[]>>> busstop_pair = null;
	
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
	
	public ArrivalManager(String busno, String stop_code, int bound, int language) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		this(busno, stop_code, bound, language, false);
	}

	/***
	 * Creates a new <code>ArrivalManager</code> instance.
	 * @param busno Bus No.
	 * @param stop_code Bus Stop Code (e.g. WO04N12500), probably it is specified from a BUS DB source.
	 * @param bound Bus Direction/Bound (1 or 2)
	 * @param language Language to be selected <code>ArrivalManager.ENGLISH_LANG</code> or <code>ArrivalManager.CHINESE_LANG</code>
	 * @param loadFromClassPath Load the database file from class-path
	 * @throws InvalidArrivalTargetException If the specified target arrival was invalid
	 * @throws CouldNotLoadDatabaseException If the API could not load the database
	 */
	public ArrivalManager(String busno, String stop_code, int bound, int language, boolean loadFromClassPath) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		//Check whether the database in the memory is null or not
		if (busstop_pair == null){
			//If null, load the database in the directory
			
			System.out.println(
					"ArrivalManager: Database is not loaded. Loading now...\n" +
					"ArrivalManager: It might take a while..."
					);
			
			//Save start time in ms (for calculating estimated time)
			long startTime = System.currentTimeMillis();
			
			//Load Database
			boolean loaded = loadDatabase(loadFromClassPath);
			
			//Save end time in ms (for calculating estimated time)
			long endTime = System.currentTimeMillis();
			
			System.out.println(
					"ArrivalManager: " + 
					(loaded ? "Loaded database. Took " + (endTime - startTime) + " ms to load." : "Could not load database. Check whether the DB is exist, valid or not")
					);
			
			if (!loaded){
				throw new CouldNotLoadDatabaseException("Could not load database. Check whether the DB is exist, valid or not");
			}
		}
		
		//Check is language parameter valid
		if (language != 0 && language != 1){
			throw new InvalidArrivalTargetException("Invalid language integer \"" + language + "\". It should be specified from ArrivalManager.ENGLISH_LANG or ArrivalManger.CHINESE_LANG");
		}
		
		if (getBusNoIndex(busno) == -1){
			throw new InvalidArrivalTargetException("Could not find the bus no \"" + busno + "\" in the database.");
		}
		
		//Find Stop sequence
		int stop_seq = getStopSeq(busno, bound, stop_code);
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
	
//Recall
	
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
	 * Get arrival time remaining<br>
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
	public String getArrivalTimeRemaining_Formatted(){
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
	public int getArrivalTimeRemaining_Min(){
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
	
	public String getArrivalTime_Formatted() throws NoETADataFetchedError, NoServerTimeFetchedError{
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
		if (hr == -1 || min == -1){
			return "Invalid";
		} else if (hr == -3 || min == -3){
			return "End";
		}
		String hour = hr < 10 ? "0" + hr : Integer.toString(hr);
		String minute = min < 10 ? "0" + min : Integer.toString(min);
		String output = hour + ":" + minute;
		return output;
	}
	
//Database In Memory I/O
	
	/**
	 * <b>Get all the buses of this ArrivalManager's bus stop</b><br>
	 * <br> 
	 * Returns a list with nothing if no buses match the bus-stop code
	 * @return List
	 */
	public List<String> getBusStopBuses(){
		List<List<String[]>> bus;
		List<String[]> bound;
		String[] stop;
		List<String> output = new ArrayList<String>(busstop_pair.size());
		for (int i = 0; i < busstop_pair.size(); i++){
			bus = busstop_pair.get(i);
			for (int j = 0; j < bus.size(); j++){
				bound = bus.get(j);
				for (int x = 0; x < bound.size(); x++){
					stop = bound.get(x);
					if (stop[2].equals(this.stop_code)){
						output.add(stop[0]);
					}
				}
			}
		}
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
	
	/**
	 * Automatically generates a <code>bus_stopdb.properties</code> database<br>
	 * <br>
	 * It will take a while (probably a lot of time) to generate a database.<br>
	 * It is <b>not recommended</b> to implement with the application <b>programmatically</b>.
	 * @return Boolean whether the database is successfully generated
	 */
	public static boolean generateDatabase(){
		//TODO Auto-generate Database (From DB-Builder)
		return false;
	}
	
	/***
	 * Load database from class-path or file-system<br>
	 * <br>
	 * It is automatically called if the database in the memory is <code>null</code>.<br>
	 * <b>Must be called before any database reading events.</b>
	 * @param fromClassResources Load from Class-path<br>
	 * @param parent A class parent to be specified
	 * If <code>true</code>, make sure the <code>bus_stopdb.properties</code> is attached in the class-path.<br>
	 * If <code>false</code>, make sure the <code>bus_stopdb.properties</code> is inside the working directory.
	 * @return Boolean whether the database is successfully loaded.
	 */
	public static boolean loadDatabase(Object parent, boolean fromClassResources){
		try {
			File file;
			Properties prop = new Properties();
			InputStream in;
			if (fromClassResources){
				//TODO No class resource is provided right now.
				System.out.println(parent.getClass().getClassLoader().getResource("bus_stopdb.properties").getPath());
				in = parent.getClass().getClassLoader().getResourceAsStream("bus_stopdb.properties");
			} else
			{
				file = new File("bus_stopdb.properties");
				if(!file.exists()){
					return false;
				}
				in = new FileInputStream(file);
			}
			prop.load(in);
			int busdb = Integer.parseInt(prop.getProperty("bus_db"));
			BUS_NO = new String[busdb];
			for (int i = 0; i < busdb; i++){
				BUS_NO[i] = prop.getProperty("bus_db" + i);
			}
			int buses = Integer.parseInt(prop.getProperty("buses"));
			int bounds;
			int stops;
			if (busstop_pair != null){
				busstop_pair.clear();
			} else {
				busstop_pair = new ArrayList<List<List<String[]>>>(BUS_NO.length);
			}
			List<List<String[]>> bus;
			List<String[]> bound;
			String[] stop;
			for (int i = 0; i < buses; i++){
				bounds = Integer.parseInt(prop.getProperty(BUS_NO[i] + "-bounds"));
				bus = new ArrayList<List<String[]>>(bounds);
				for (int j = 1; j <= bounds; j++){
					stops = Integer.parseInt(prop.getProperty(BUS_NO[i] + "-bound" + j + "-stops"));
					bound = new ArrayList<String[]>(stops);
					for (int s = 0; s < stops; s++){
						stop = new String[5];
						stop[0] = BUS_NO[i];
						stop[1] = Integer.toString(j);
						stop[2] = prop.getProperty(BUS_NO[i] + "-bound" + j + "-stop" + s + "-stopcode");
						stop[3] = prop.getProperty(BUS_NO[i] + "-bound" + j + "-stop" + s + "-stopseq");
						stop[4] = prop.getProperty(BUS_NO[i] + "-bound" + j + "-stop" + s + "-stopname");
						bound.add(stop);
					}
					bus.add(bound);
				}
				busstop_pair.add(bus);
			}
			in.close();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean loadDatabase(boolean fromClassResources){
		return loadDatabase(null, fromClassResources);
	}
	
	public static String getStopCodeViaStopName(String route, int bound, String stopname){
		int routeindex = getBusNoIndex(route);
		
		if (routeindex == -1){
			return null;
		}
		
		for (int i = 0; i < busstop_pair.get(routeindex).get(bound).size(); i++){
			if (busstop_pair.get(routeindex).get(bound).get(i)[4].equals(stopname)){
				return busstop_pair.get(routeindex).get(bound).get(i)[2];
			}
		}
		return null;
	}
	
	public static String getStopNameViaStopCode(String route, int bound, String stopcode){
		int routeindex = getBusNoIndex(route.toUpperCase());
		System.out.println("Finding index: " + routeindex + " of " + route);
		if (routeindex == -1){
			System.out.println("Cannot find route");
			return null;
		}
		
		bound--;
		System.out.println("Need to find the index of: [" + stopcode + "]");
		for (int i = 0; i < busstop_pair.get(routeindex).get(bound).size(); i++){
			System.out.println("Finding... " + "[" + busstop_pair.get(routeindex).get(bound).get(i)[2] + "] route: " + busstop_pair.get(routeindex).get(bound).get(i)[0] + " bound: " + busstop_pair.get(routeindex).get(bound).get(i)[1]);
			if (busstop_pair.get(routeindex).get(bound).get(i)[2].equals(stopcode)){
				System.out.println("Found! " +  busstop_pair.get(routeindex).get(bound).get(i)[4]);
				return busstop_pair.get(routeindex).get(bound).get(i)[4];
			}
			System.out.println("Nope.");
		}
		System.out.println("Can't find anything! <->");
		return null;
	}
	
	private static int getBusNoIndex(String bus_no){
		for (int i = 0; i < BUS_NO.length; i++){
			if (BUS_NO[i].equals(bus_no)){
				return i;
			}
		}
		return -1;
	}
	
	/***
	 * <b>Get the bus-stop sequence of the route with the bus-stop code</b><br>
	 * <br>
	 * Returns -1 if the route or the bus-stop code do not exist.
	 * @param route The Bus-Stop Number/Name
	 * @param boundno The Bound of the route
	 * @param stopcode The Bus-Stop code
	 * @return Integer
	 */
	public static int getStopSeq(String route, int boundno, String stopcode){
		
		if (busstop_pair == null){
			//If null, load the database in the directory
			
			System.out.println(
					"ArrivalManager: Database is not loaded. Loading now...\n" +
					"ArrivalManager: It might take a while..."
					);
			
			//Save start time in ms (for calculating estimated time)
			long startTime = System.currentTimeMillis();
			
			//Load Database
			boolean loaded = loadDatabase(false);
			
			//Save end time in ms (for calculating estimated time)
			long endTime = System.currentTimeMillis();
			
			System.out.println(
					"ArrivalManager: " + 
					(loaded ? "Loaded database. Took " + (endTime - startTime) + " ms to load." : "Could not load database. Check whether the DB is exist, valid or not")
					);
			
			if (!loaded){
				return -2;
			}
		}
		
		int busindex = getBusNoIndex(route);
		if (busindex == -1){
			System.out.println("No Bus");
			return -1;
		}
		List<List<String[]>> bus;
		List<String[]> bound;
		String[] stop;
		for (int i = 0; i < busstop_pair.size(); i++){
			bus = busstop_pair.get(i);
			for (int j = 0; j < bus.size(); j++){
				bound = bus.get(j);
				for (int x = 0; x < bound.size(); x++){
					stop = bound.get(x);
					if (stop[0].equals(route) && stop[2].equals(stopcode) && stop[1].equals(Integer.toString(boundno))){
						return Integer.parseInt(stop[3]);
					}
				}
			}
		}
		return -1;
	}
	
	public static List<List<List<String[]>>> getBusStopPair(){
		return busstop_pair;
	}
}
