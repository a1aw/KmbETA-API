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
	
	public static final String[] BUS_NO = {
			"1", "1A", "10", "11", "11B", "11C", "11D", "11K", "11X", "12", "12A",
			"13D", "13M", "13P", "13S", "13X", "14", "14B", "14D", "14X", "15", "15A", "15P", "15X",
			"16", "16M", "16X", "17", "18", "108",  "2", "2A", "2B", "2D", "2E", "2F", "2X",
			"21", "23", "23M", "24", "26", "26M", "27", "28", "28B", "28S", "29M", "203C", "203E",
			"203S", "208", "211", "212", "215P", "215X", "216M", "219X", "224X", "230X", "234A",
			"234B", "234C", "234P", "234X", "235", "235M", "237A", "238M", "238P", "238S", "238X",
			"240X", "242X", "243M", "243P", "248M", "249M", "249X", "251A", "251B", "251M", "252B",
			"258D", "258P", "258S", "259B", "259C", "259D", "259E", "259X", "260B", "260C", "260X",
			"261", "261B", "261P", "263", "264R", "265B", "265M", "265S", "267S", "268B", "268C",
			"268P", "268X", "269A", "269B", "269C", "269D", "269M", "269P", "270", "270A", "270B",
			"270P", "270S", "271", "271P", "272A", "272K", "272P", "272S", "272X", "273", "273A",
			"273B", "273C", "273D", "273P", "273S", "274P", "275R", "276", "276A", "276B", "276P",
			"277E", "277P", "277X", "278K", "278P", "278X", "279X", "280X", "281A", "281B", "281M",
			"281X", "282", "283", "284", "286C", "286M", "286P", "286X", "287X", "288", "289K", "290",
			"290A", "292P", "296A", "296C", "296D", "296M", "297", "297P", "298E", "299X", 
			"3B", "3C", "3D", "3M", "3P", "30", "30X", "31", "31B", "31M", "32", "32M", "33A", "34",
			"34M", "35A", "35X", "36", "36A", "36B", "36M", "36X", "37", "37M", "38", "38A", "39A",
			"39M", "373",  "40", "40P", "40X", "41", "41A", "41M", "41P", "42", "42A", "42C",
			"42M", "43", "43A", "43B", "43C", "43M", "43P", "43X", "44", "44M", "45", "46", "46P", "46X",
			"47X", "48X", "49P", "49X",  "5", "5A", "5C", "5D", "5M", "5P", "5R", "5S", "51",
			"52X", "53", "54", "57M", "58M", "58P", "58X", "59A", "59M", "59X",  "6", "6C", "6D",
			"6F", "60M", "60X", "61M", "61X", "62X", "63X", "64K", "64S", "65K", "66M", "66X", "67M",
			"67X", "68A", "68E", "68F", "68M", "68X", "69C", "69M", "69P", "69X", "603", "603P", "603S",
			"673",  "7", "7B", "7M", "70K", "71A", "71B", "71K", "71S", "72", "72A", "72C", "72X",
			"73", "73A", "73K", "73X", "74A", "74B", "74C", "74D", "74K", "74P", "74X", "75K", "75P", "75X",
			"76K", "77K", "78K", "79K",  "8", "8A", "8P", "80", "80K", "80M", "80P", "80X", "81",
			"81C", "81K", "81S", "82B", "82C", "82K", "82P", "82X", "83A", "83K", "83S", "83X", "84M",
			"85", "85A", "85B", "85K", "85M", "85S", "85X", "86", "86A", "86C", "86K", "86S", "87B",
			"87D", "87K", "87P", "87S", "88K", "88X", "89", "89B", "89C", "89D", "89P", "89X", 
			"9", "91", "91M", "91P", "91R", "92", "93A", "93K", "93M", "94", "95", "95M", "96R", "98A",
			"98C", "98D", "98P", "98S", "99", "99R", "934", "934A", "935", "936", "960", "960A", "960B",
			"960P", "960S", "960X", "961", "961P", "968", "968X", "978", "978A", "978B",  "A31",
			"A33", "A33P", "A36", "A41", "A41P", "A43", "A43P", "A47",  "B1",  "E31",
			"E32", "E33", "E33P", "E34A", "E34B", "E34P", "E34X", "E41", "E42",  "N30", "N30P",
			"N30S", "N31", "N36", "N39", "N42", "N42A", "N42P", "N64", "N73", "N216", "N237", "N241",
			"N260", "N269", "N271", "N281", "N293", "N368", "NA33", "NA34",  "R33", "R42",
			 "S64", "S64C", "S64P", "S64X",  "T270", "T277",  "X42C"
	};
	
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

	/***
	 * Creates a new <code>ArrivalManager</code> instance.
	 * @param busno Bus No.
	 * @param stop_code Bus Stop Code (e.g. WO04N12500), probably it is specified from a BUS DB source.
	 * @param bound Bus Direction/Bound (1 or 2)
	 * @throws InvalidArrivalTargetException If the specified target arrival was invalid
	 * @throws CouldNotLoadDatabaseException If the API could not load the database
	 */
	public ArrivalManager(String busno, String stop_code, int bound, int language) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
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
			boolean loaded = loadDatabase(false);
			
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
	 * <b>Arrived</b>: If <code>remainMin</code> <= 0
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
	 * @param stopcode The Bus-Stop code
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
