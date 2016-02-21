package com.mob41.kmbapi;

import java.awt.Color;
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

import javax.swing.JLabel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KmbApi {
	
	private static JSONObject data = null;
	private static String stime = null;
	
	public static final int NO_DATA = -1; 
	
	private static final String etaserver = "http://etav2.kmb.hk/";
	private static final String etadatafeed = "http://etadatafeed.kmb.hk:1933/";
	
	private static String[] bus_db = {//, "15P"
			"1", "1A", "10", "11", "11B", "11C", "11D", "11K", "11X", "12", "12A",
			"13D", "13M", "13P", "13S", "13X", "14", "14B", "14D", "14X", "15", "15A", "15X",
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
	
	/*
	 * Bus Stop Array Item:
	 * 
	 * <-- Bus Line Name -->, <-- Line Bound -->, <-- Stop Code -->, <-- Stop Sequence -->, <-- Stop Name -->
	 * 
	 */
	private static List<String[]> busstop_pair = new ArrayList<String[]>(bus_db.length);
	private static final String lastdeparted_msg = "The last bus has departed from this bus stop";
	
	public static final int ENGLISH_LANG = 0;
	public static final int CHINESE_LANG = 1;
	
	//Must load database
	/***
	 * Load database from file-system<br>
	 * <br>
	 * <b>Must be called before any database reading events.</b>
	 * @return Boolean
	 */
	public static boolean loadDatabase(){
		return loadDatabase(false);
	}
	
	/***
	 * Load database from class-path or file-system<br>
	 * <br>
	 * <b>Must be called before any database reading events.</b>
	 * @param fromClassResources Load from Class-path
	 * @return Boolean
	 */
	private static boolean loadDatabase(boolean fromClassResources){
		try {
			File file;
			Properties prop = new Properties();
			InputStream in;
			if (fromClassResources){
				in = KmbApi.class.getClassLoader().getResourceAsStream("bus_stopdb.properties");
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
			String[] data = new String[5];
			for (int i = 0; i < buses; i++){
				bounds = Integer.parseInt(prop.getProperty(bus_db[i] + "-bounds"));
				for (int j = 1; j <= bounds; j++){
					System.out.println("Bus: " + bus_db[i] + " Bound: " + j);
					try {
						stops = Integer.parseInt(prop.getProperty(bus_db[i] + "-bound" + j + "-stops"));
					} catch (NullPointerException e){
						continue;
					}
					for (int s = 0; s < stops; s++){
						data = new String[5];
						data[0] = bus_db[i];
						data[1] = Integer.toString(j);
						data[2] = prop.getProperty(bus_db[i] + "-bound" + j + "-stop" + s + "-stopcode");
						data[3] = prop.getProperty(bus_db[i] + "-bound" + j + "-stop" + s + "-stopseq");
						data[4] = prop.getProperty(bus_db[i] + "-bound" + j + "-stop" + s + "-stopname");
						System.out.println(Arrays.deepToString(data));
						busstop_pair.add(data);
					}
				}
			}
			System.out.println(Arrays.deepToString(busstop_pair.toArray()));
			in.close();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
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
			String kmbApi = etaserver + "?action=geteta&lang=" + language + "&route=" + route + "&bound=" + 
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
			data = kmbJson;
			return kmbJson;
		} catch (Exception e){
			return null;
		}
	}
	
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
			String kmbApi = etadatafeed + "GetData.ashx?type=Server_T";
			URL url = new URL(kmbApi);
			URLConnection connection = url.openConnection();
			String line;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
			JSONArray kmbJson = new JSONArray(builder.toString());
			String datetime = (String) kmbJson.getJSONObject(0).get("stime");
			stime = datetime;
			return datetime;
		} catch (Exception e){
			return null;
		}
	}
	
	/***
	 * Divide server time to 3 parts (Hour, Min, Seconds)<br>
	 * Get server time hours.<br>
	 * <br>
	 * Load the Server Time data first by using <b>getServerTime()</b> Or 
	 * <b>outdated</b> time <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * <br>
	 * Returns -1 if the server time is <b>null</b>
	 * 
	 * @return Integer
	 */
	public static int getServerHour(){
		if (stime == null){
			return -1;
		}
		int hour = Integer.parseInt(stime.substring(11, 13));
		return hour;
	}
	
	/***
	 * Divide server time to 3 parts (Hour, Min, Seconds)<br>
	 * Get server time hour.<br>
	 * <br>
	 * Load the Server Time data first by using <b>getServerTime()</b> Or 
	 * <b>outdated</b> time <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * <br>
	 * Returns -1 if the server time is <b>null</b>
	 * 
	 * @return Integer
	 */
	public static int getServerMin(){
		if (stime == null){
			return -1;
		}
		int min = Integer.parseInt(stime.substring(14, 16));
		return min;
	}
	
	/***
	 * Divide server time to 3 parts (Hour, Min, Seconds)<br>
	 * Get server time seconds.<br>
	 * <br>
	 * Load the Server Time data first by using <b>getServerTime()</b> Or 
	 * <b>outdated</b> time <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * <br>
	 * Returns -1 if the server time is <b>null</b>
	 * 
	 * @return Integer
	 */
	public static int getServerSec(){
		if (stime == null){
			return -1;
		}
		//TODO Unstable substring!
		int sec = Integer.parseInt(stime.substring(16, 18));
		return sec;
	}
	
	/***
	 * Calculates and formats the remaining arrival time left.<br>
	 * <br>
	 * Load the ETA data first by using <b>getETAdata()</b> Or 
	 * <b>outdated</b> data <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * Load the Server Time data first by using <b>getServerTime()</b> Or 
	 * <b>outdated</b> time <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * <br>
	 * Returns "INVAILD" if the arrival time is null or ETA data not <br>
	 * accessible. <br>
	 * Returns "Arrived" if the bus is arrived / arrivial time is equal <br>
	 *  to the server time.
	 * 
	 * @return String
	 */
	public static String getRemainingFormattedTime(){
		int serverhr = getServerHour();
		int servermin = getServerMin();
		int arrhr = getArrivalHour();
		int arrmin = getArrivalMin();
		int outputmin = 0;
		
		if (serverhr == -1 || servermin == -1 || arrhr == -1 || arrmin == -1){
			return "INVAILD";
		}
		
		if (arrhr > serverhr){
			outputmin = (60 - servermin) + arrmin;
		}
		else
		{
			outputmin = arrmin - servermin;
		}
		
		String output;
		if (outputmin <= 0){
			output = "Arrived";
		} else {
			output = outputmin + " min(s)";
		}
		return output;
	}
	
	/***
	 * Calculates the remaining arrival minutes left.<br>
	 * <br>
	 * Load the ETA data first by using <b>getETAdata()</b> Or 
	 * <b>outdated</b> data <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * Load the Server Time data first by using <b>getServerTime()</b> Or 
	 * <b>outdated</b> time <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * 
	 * @return Integer
	 */
	public static int getRemainingMin(){
		int serverhr = getServerHour();
		int servermin = getServerMin();
		int arrhr = getArrivalHour();
		int arrmin = getArrivalMin();
		int outputmin = 0;
		if (arrhr <= -1 || arrmin <= -1 || serverhr <= -1 || servermin <= -1){
			return -1;
		}
		
		if (arrhr > serverhr){
			outputmin = (60 - servermin) + arrmin;
		}
		else
		{
			outputmin = arrmin - servermin;
		}
		
		return outputmin;
	}
	
	/***
	 * Returns whether the specified arrival time is a scheduled time.
	 * @param i The index of arrival times. See the KMB App for details.
	 * @return Boolean
	 */
	public static boolean isScheTime(int i){
		if (data == null){
			return true;
		}
		String isScheTimeString = (String) data.getJSONArray("response").getJSONObject(i).get("ei");
		boolean isScheTime = true;
		if (isScheTimeString.equals("N")){
			isScheTime = false;
		}
		return isScheTime;
	}
	
	/***
	 * Returns whether the latest arrival time is a scheduled time.
	 * @return Boolean
	 */
	public static boolean isScheTime(){
		return isScheTime(0);
	}
	
	/***
	 * <b>Get the specified Arrival Time of the ETA Data Loaded</b><br>
	 * <br>
	 * Load the ETA data first by using <b>getETAdata()</b> Or 
	 * <b>outdated</b> data <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * Load the Server Time data first by using <b>getServerTime()</b> Or 
	 * <b>outdated</b> time <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * <br>
	 * Returns <b>null</b> if the arrival time is null or ETA data not <br>
	 * accessible
	 * @param i The index of arrival times. See the KMB App for details.
	 * @return String
	 */
	public static String getArrivalTime(int i){
		if (data == null){
			return null;
		}
		
		String output = (String) data.getJSONArray("response").getJSONObject(i).get("t");
		return output;
	}
	
	/***
	 * <b>Get the latest Arrival Time of the ETA Data Loaded</b><br>
	 * <br>
	 * Load the ETA data first by using <b>getETAdata()</b> Or 
	 * <b>outdated</b> data <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * Load the Server Time data first by using <b>getServerTime()</b> Or 
	 * <b>outdated</b> time <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * <br>
	 * Returns <b>null</b> if the arrival time is null or ETA data not <br>
	 * accessible
	 * @return String
	 */
	public static String getArrivalTime(){
		return getArrivalTime(0);
	}
	
	/***
	 * <b>Get Arrival Hour of the ETA Data Loaded</b><br>
	 * <br>
	 * Load the ETA data first by using <b>getETAdata()</b> Or 
	 * <b>outdated</b> data <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * Load the Server Time data first by using <b>getServerTime()</b> Or 
	 * <b>outdated</b> time <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * <br>
	 * Returns KmbApi.NO_DATA if the arrival time is null or ETA data not <br>
	 * accessible
	 * @return Integer
	 */
	public static int getArrivalHour(){
		String arrivaltime = getArrivalTime(0);
		if (arrivaltime == null){
			return NO_DATA;
		}
		if (arrivaltime.equals(lastdeparted_msg)){
			return NO_DATA;
		}
		String preoutput;
		int output;
		preoutput = arrivaltime.substring(0, 2);
		try {
			output = Integer.parseInt(preoutput);
		} catch (NumberFormatException e){
			output = NO_DATA;
		}
		return output;
	}
	
	/***
	 * <b>Get Arrival Minute of the ETA Data Loaded</b><br>
	 * <br>
	 * Load the ETA data first by using <b>getETAdata()</b> Or 
	 * <b>outdated</b> data <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * Load the Server Time data first by using <b>getServerTime()</b> Or 
	 * <b>outdated</b> time <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * <br>
	 * Returns KmbApi.NO_DATA if the arrival time is null or ETA data not <br>
	 * accessible
	 * @return Integer
	 */
	public static int getArrivalMin(){
		String arrivaltime = getArrivalTime(0);
		if (arrivaltime == null){
			return NO_DATA;
		}
		if (arrivaltime.equals(lastdeparted_msg)){
			return NO_DATA;
		}
		String preoutput;
		int output;
		preoutput = arrivaltime.substring(3, 5);
		try {
			output = Integer.parseInt(preoutput);
		} catch (NumberFormatException e){
			output = NO_DATA;
		}
		return output;
	}
	
	/***
	 * <b>Get a formatted arrival time of the ETA Data Loaded</b><br>
	 * <br>
	 * Load the ETA data first by using <b>getETAdata()</b> Or 
	 * <b>outdated</b> data <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * Load the Server Time data first by using <b>getServerTime()</b> Or 
	 * <b>outdated</b> time <br> or <b>java.lang.NullPointerException</b>
	 * might happen.
	 * <br>
	 * <br>
	 * May returns a response with <br>
	 * <b>Data Error</b>, <b>STime Error</b>, <b>END</b>, <b>INVAILD</b>
	 * 
	 * @return String
	 */
	public static String getFormattedTimeDefined(){
		int hr;
		int min;
		
		if (data == null){
			return "Data Error";
		} else if (stime == null){
			return "STime Error";
		}
		if (KmbApi.getArrivalTime() == KmbApi.lastdeparted_msg){
			return "END";
		}
		else
		{
			hr = KmbApi.getArrivalHour();
			min = KmbApi.getArrivalMin();
		}
		if (hr == -1 || min == -1){
			return "INVAILD";
		}
		String hour = hr < 10 ? "0" + hr : Integer.toString(hr);
		String minute = min < 10 ? "0" + min : Integer.toString(min);
		String output = hour + ":" + minute;
		return output;
	}
	
	/* 
	public static String[] getFormattedTableData(int bus_index, int stop_index){
		String[] tabledata = new String[4];
		
		try {
			data.getJSONArray("response");
		} catch (Exception e){
			tabledata[0] = "---";
			tabledata[1] = "---";
			tabledata[2] = "---";
			tabledata[3] = "---";
			return tabledata;
		}
		
		tabledata[0] = bus_db[bus_index];
		tabledata[1] = busstop_db[stop_index][1];
		tabledata[2] = KmbApi.getFormattedTimeDefined();
		tabledata[3] = KmbApi.getArrivalTime().equals(KmbApi.lastdeparted_msg) ? "END" : KmbApi.getRemainingFormattedTime();
		
		return tabledata;
	}
	*/
	
	/***
	 * <b>Get the bus-stop sequence of the route with the bus-stop code</b><br>
	 * <b>!!WARNING!!</b> This only works if the external database is loaded.<br>
	 * <br>
	 * Returns -1 if the route or the bus-stop code do not exist.
	 * @param route The Bus-Stop Number/Name
	 * @param stopcode The Bus-Stop code
	 * @return Integer
	 */
	public static int getStopSeq(String route, String stopcode){
		String[] data;
		for (int i = 0; i < busstop_pair.toArray().length; i++){
			data = busstop_pair.get(i);
			if (data[0].equals(route) && data[2].equals(stopcode)){
				return Integer.parseInt(data[3]);
			}
		}
		return -1;
	}
	
	/***
	 * <b>Get all the buses in List according the bus-stop code</b><br>
	 * <b>!!WARNING!!</b> This only works if the external database is loaded.<br>
	 * <br> 
	 * Returns a list with nothing if no buses match the bus-stop code
	 * @param stopcode The Bus-Stop code
	 * @return List
	 */
	public static List<String[]> getStopBuses(String stopcode){
		String[] data;
		List<String[]> output = new ArrayList<String[]>(busstop_pair.size());
		for (int i = 0; i < busstop_pair.toArray().length; i++){
			data = busstop_pair.get(i);
			if (data[2].equals(stopcode)){
				output.add(busstop_pair.get(i));
			}
		}
		return output;
	}
	
	/***
	 * <b>Get the array index of the bus number.</b><br>
	 * If it does exist, returns the array index<br>
	 * If it does not exist, returns -1
	 * 
	 * @param busno The bus name to be requested
	 * @return Integer
	 */
	public static int getBusArrayIndex(String busno){
		int i;
		for (i = 0; i < bus_db.length; i++){
			if (bus_db[i] == busno){
				return i;
			}
		}
		return -1;
	}
	
	/***
	 * Ask is the bus name specified in the parameter exists.
	 * @param busno The bus name to be requested
	 * @return Boolean
	 */
	public static boolean isBusExistInDB(String busno){
		for (int i = 0; i < bus_db.length; i++){
			if (bus_db[i] == busno){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Future plan: Automatic database builder
	 * 
	 */
	
	
	/*
	public static int getStopsArrayIndex(String name_stops){
		int i;
		for (i = 0; i < busstop_db.length; i++){
			if (busstop_db[i][1] == name_stops){
				break;
			}
		}
		return i;
	}
	
	public static boolean isStopsExistInDB(String name_stops){
		int i;
		for (i = 0; i < busstop_db.length; i++){
			if (busstop_db[i][1] == name_stops){
				return true;
			}
		}
		return false;
	}
	
	public static String getStopCodeFromDB(String name_stops){
		int i;
		for (i = 0; i < busstop_db.length; i++){
			if (busstop_db[i][1] == name_stops){
				break;
			}
		}
		return busstop_db[i][0];
	}
	*/
}
