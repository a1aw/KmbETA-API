package com.github.mob41.kmbeta.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.mob41.kmbeta.exception.DatabaseNotLoadedException;
import com.github.mob41.kmbeta.exception.NoSuchRouteException;

public class BusDatabase {
	
	private static final String busesdb = "http://etadatafeed.kmb.hk:1933/GetData.ashx?type=ETA_R";
	
	private static final String routedb = "http://www.kmb.hk/ajax/getRouteMapByBusno.php";
	
	private static final String db = "https://db.kmbeta.ml/";
	
	private String[] BUS_NO;
	
	private JSONObject pureJson;
		
	private List<Route> routes = null;
	
	private boolean staticDatabase = false;
	
	/**
	 * Get the all the routes' names.<br>
	 * <br>
	 * Returns <code>null</code> if database not loaded.
	 * @return A String Array containing all the routes' names
	 */
	public String[] getRoutesNames(){
		return BUS_NO;
	}
	
	/**
	 * Gets all the routes<br>
	 * <br>
	 * Returns <code>null</code> if database not loaded.
	 * @return A list of <code>Route</code> instances
	 */
	public List<Route> getRoutes(){
		return routes;
	}
	
	/**
	 * Gets the pure <code>JSONObject</code> fetched.
	 * <br>
	 * Returns <code>null</code> if database not loaded.
	 * @return A pure <code>JSONObject</code>
	 */
	public JSONObject getPureJSON(){
		return pureJson;
	}
	
	/**
	 * Loads the database from a <code>InputStream</code>
	 * @param in The <code>InputStream</code> to be loaded.
	 * @return whether the database is loaded successfully or not.
	 */
	public boolean loadDatabase(InputStream in){
		try {
			String line;
			String data = "";
		    try {
		    	BufferedReader reader = new BufferedReader(new 
		                InputStreamReader(in));
			    while ((line = reader.readLine()) != null) {
			        data += line;
			     }
		    } catch (Exception e){
		    	e.printStackTrace();
		    	return false;
		    }
		    
		    if (data == null || data == ""){
		    	return false;
		    }
			
			JSONObject jsonDb = new JSONObject(data);
			pureJson = jsonDb;
			//Load routes list
			JSONArray routesArr = jsonDb.getJSONArray("routes");
			int numOfRoutes = routesArr.length();
			BUS_NO = new String[numOfRoutes];
			for (int i = 0; i < numOfRoutes; i++){
				BUS_NO[i] = routesArr.getString(i);
			}
			
			int bounds;
			
			int stops;
			
			if (routes != null){
				routes.clear();
			} else {
				routes = new ArrayList<Route>(BUS_NO.length);
			}
			
			Route route;
			RouteBound bound;
			BusStop stop;
			
			JSONArray busesArr = jsonDb.getJSONArray("buses");
			JSONObject busJson;
			JSONArray boundsArr;
			JSONObject boundJson;
			JSONArray stopsArr;
			JSONObject stopJson;
			
			for (int i = 0; i < numOfRoutes; i++){
				busJson = busesArr.getJSONObject(i);
				
				route = new Route(busJson.getString("name"));
				
				boundsArr = busJson.getJSONArray("bounds");
				
				bounds = boundsArr.length();
				
				for (int j = 0; j < bounds; j++){
					bound = new RouteBound(route);
					
					boundJson = boundsArr.getJSONObject(j);
					
					stopsArr = boundJson.getJSONArray("stops");
					
					stops = stopsArr.length();
					
					for (int s = 0; s < stops; s++){
						stopJson = stopsArr.getJSONObject(s);
						
						stop = new BusStop(route, bound, j, stopJson.getDouble("lat"), stopJson.getDouble("lng"),
								stopJson.getString("area"), stopJson.getString("stopcode"), stopJson.getString("stopname_eng"),
								stopJson.getString("stopname_chi"), stopJson.getString("addr_eng"), stopJson.getString("addr_chi"),
								stopJson.getDouble("normal_fare"), stopJson.getDouble("air_cond_fare"), stopJson.getInt("stopseq"));
						
						bound.addStop(stop);
					}
					route.addBound(bound);
				}
				routes.add(route);
			}
			in.close();
			staticDatabase = true;
			return true;
		} catch (Exception e){
			e.printStackTrace();
			staticDatabase = false;
			return false;
		}
	}
	
	/**
	 * Loads the database by downloading the database directly from GitHub
	 * @return whether the database is loaded successfully or not.
	 */
	public boolean loadWebDB(){
		try {
			URL url = new URL(db + "kmbeta_db.json");
			URLConnection conn = url.openConnection();
			return loadDatabase(conn.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Unloads the database.
	 */
	public void unloadDatabase(){
		routes = null;
		BUS_NO = null;
		staticDatabase = false;
	}
	
	/***
	 * Load database from class-path or file-system<br>
	 * <br>
	 * It is automatically called if the database in the memory is <code>null</code>.<br>
	 * <b>Must be called before any database reading events.</b>
	 * @param fromClassResources Load from Class-path<br>
	 * @param parent A class parent to be specified
	 * If <code>true</code>, make sure the <code>kmbeta_db.json</code> is attached in the class-path.<br>
	 * If <code>false</code>, make sure the <code>kmbeta_db.json</code> is inside the working directory.
	 * @return Boolean whether the database is successfully loaded.
	 */
	public boolean loadDatabase(Object parent, boolean fromClassResources){
		InputStream in;
		//Check whether the "LoadFromClass" is true or not
		if (fromClassResources){
			
			//Load from class resources
			System.out.println(parent.getClass().getClassLoader().getResource("kmbeta_db.json").getPath());
			in = parent.getClass().getClassLoader().getResourceAsStream("kmbeta_db.json");
			
		} else {
			
			//Load from external file
			File file = new File("kmbeta_db.json");
			if(!file.exists()){
				return false;
			}
			try {
				in = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				in = null;
				e.printStackTrace();
			}
			
		}
		
		return loadDatabase(in);
	}
	
	/**
	 * Load database besides the application or from class resources<br>
	 * <br>
	 * Using <code>BusDatabase.class</code> as the parent
	 * @param fromClassResources If <code>true</code>, it will load from class-path
	 * @return Whether the database loaded successfully or not
	 */
	public boolean loadDatabase(boolean fromClassResources){
		return loadDatabase(BusDatabase.class, fromClassResources);
	}
	
	/**
	 * Get all routes related to the specified bus stop code (Sub-area)<br>
	 * <br>
	 * Warning: This function requires a static database (Web DB/Offline DB)<br>
	 * otherwise, a <code>DatabaseNotLoadedException</code> will be thrown.<br>
	 * @return a <code>List</code> containing all <code>Route</code> instances related to the specified stop code
	 * @throws DatabaseNotLoadedException will only be thrown if a static database isn't loaded.
	 */
	public List<Route> getBusStopRoutes(String stopcode) throws DatabaseNotLoadedException{
		if (!staticDatabase || routes == null){
			throw new DatabaseNotLoadedException();
		}
		
		List<Route> output = new ArrayList<Route>(routes.size());
		for (Route route : routes){
			for (RouteBound bound : route.getList()){
				for (BusStop stop : bound.getList()){
					if (stop.getStopCode().equals(stopcode)){
						output.add(bound.getRoute());
					}
				}
			}
		}
		return output;
	}
	
	/**
	 * Automatically generates a database, and returns a <code>JSONObject</code> with the database.
	 * <br>
	 * It will take a while (probably a lot of time) to generate a database.<br>
	 * Put this into a thread to run.<br>
	 * <br>
	 * Returns <code>null</code> if the generation of database failed.
	 * @return a <code>JSONObject</code> containing the database.
	 */
	public static JSONObject generateDatabase(){
		return null;
	}
	
	/**
	 * Gets the stop code from a stop name (Chinese or English)<br>
	 * <br>
	 * Returns <code>null</code> if the specified stop name wasn't found.<br>
	 * <br>
	 * Warning: This function requires a static database (Web DB/Offline DB)<br>
	 * otherwise, a <code>DatabaseNotLoadedException</code> will be thrown.<br>
	 * @param routeName The route name
	 * @param stopname The stop name in English or Chinese
	 * @return a stop code according to the specified stop name.
	 * @throws NoSuchRouteException It is thrown when the specified route can not be found.
	 * @throws DatabaseNotLoadedException will only be thrown if a static database isn't loaded.
	 */
	public String getStopCodeFromStopName(String routeName, String stopname) throws NoSuchRouteException, DatabaseNotLoadedException{
		int routeindex = getRouteIndex(routeName);
		
		if (routeindex == -1){
			throw new NoSuchRouteException("The specified route wasn't found: " + routeName);
		}
		
		if (!staticDatabase || routes == null){
			throw new DatabaseNotLoadedException();
		}
		
		Route route = routes.get(routeindex);
		
		for (RouteBound bound : route.getList()){
			for (BusStop stop : bound.getList()){
				if (stop.getStopNameInEnglish().equals(stopname) ||
						stop.getStopNameInChinese().equals(stopname)){
					return stop.getStopCode();
				}
			}
		}
		return null;
	}
	
	/**
	 * Gets the stop name in English from a stop code<br>
	 * <br>
	 * Returns <code>null</code> if the specified stop code wasn't found.<br>
	 * <br>
	 * Warning: This function requires a static database (Web DB/Offline DB)<br>
	 * otherwise, a <code>DatabaseNotLoadedException</code> will be thrown.<br>
	 * @param routeName The route name
	 * @param stopcode The stop code of the bus stop
	 * @return a stop name in English according to the stop code specified
	 * @throws NoSuchRouteException It is thrown when the specified route can not be found.
	 * @throws DatabaseNotLoadedException will only be thrown if a static database isn't loaded.
	 */
	public String getStopNameInEnglishFromStopCode(String routeName, String stopcode) throws NoSuchRouteException, DatabaseNotLoadedException{
		int routeindex = getRouteIndex(routeName);
		
		if (routeindex == -1){
			throw new NoSuchRouteException("The specified route wasn't found: " + routeName);
		}
		
		if (!staticDatabase || routes == null){
			throw new DatabaseNotLoadedException();
		}
		
		Route route = routes.get(routeindex);
		
		for (RouteBound bound : route.getList()){
			for (BusStop stop : bound.getList()){
				if (stop.getStopCode().equals(stopcode)){
					return stop.getStopNameInEnglish();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the stop name in Chinese from a stop code<br>
	 * <br>
	 * Returns <code>null</code> if the specified stop code wasn't found.<br>
	 * <br>
	 * Warning: This function requires a static database (Web DB/Offline DB)<br>
	 * otherwise, a <code>DatabaseNotLoadedException</code> will be thrown.<br>
	 * @param routeName The route name
	 * @param stopcode The stop code of the bus stop
	 * @return a stop name in Chinese according to the stop code specified
	 * @throws NoSuchRouteException It is thrown when the specified route can not be found.
	 * @throws DatabaseNotLoadedException will only be thrown if a static database isn't loaded.
	 */
	public String getStopNameInChineseFromStopCode(String routeName, String stopcode) throws NoSuchRouteException{
		int routeindex = getRouteIndex(routeName);
		
		if (routeindex == -1){
			throw new NoSuchRouteException("The specified route wasn't found: " + routeName);
		}
		
		Route route = routes.get(routeindex);
		
		for (RouteBound bound : route.getList()){
			for (BusStop stop : bound.getList()){
				if (stop.getStopCode().equals(stopcode)){
					return stop.getStopNameInChinese();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the stop sequence from a stop code<br>
	 * <br>
	 * Returns <code>-1</code> if the specified stop code wasn't found.<br>
	 * <br>
	 * Warning: This function requires a static database (Web DB/Offline DB)<br>
	 * otherwise, a <code>DatabaseNotLoadedException</code> will be thrown.<br>
	 * @param routeName The route name
	 * @param boundIndex The bound index, probably <code>0</code> or <code>1</code>
	 * @param stopcode The stop code of the bus stop
	 * @return The stop sequence of the bus stop.
	 * @throws NoSuchRouteException It is thrown when the specified route can not be found.
	 * @throws DatabaseNotLoadedException will only be thrown if a static database isn't loaded.
	 */
	public int getStopSequence(String routeName, int boundIndex, String stopcode) throws DatabaseNotLoadedException, NoSuchRouteException{
		if (staticDatabase){
			if (routes == null){
				throw new DatabaseNotLoadedException();
			}
			
			int index = -1;
			try {
				index = getRouteIndex(routeName);
			} catch (DatabaseNotLoadedException e){
				throw e;
			}
			
			if (index == -1){
				throw new NoSuchRouteException("No such route found: " + routeName);
			}
			
			Route route = routes.get(index);
			RouteBound bound = route.getBound(boundIndex);
			
			for (BusStop stop : bound.getList()){
				if (stop.getStopCode().equals(stopcode)){
					return stop.getStopSeq();
				}
			}
			
			return -1;
		} else {
			try {
				JSONArray fetchedData = getBusData(routeName, boundIndex + 1);
				if (fetchedData == null){
					return -3;
				}
				
				for (int i = 0; i < fetchedData.length(); i++){
					if (fetchedData.getJSONObject(i).getString("subarea").replaceAll("[-+.^:,]","").equals(stopcode)){
						return i;
					}
				}
				return -1;
			} catch (Exception e) {
				return -3;
			}
			
		}
	}
	
	private String[] getRoutesDB() throws IOException{
		URL url = new URL(busesdb);
	    URLConnection conn = url.openConnection();
	    
	    String line;
	    String data = "";
	    try {
	    	BufferedReader reader = new BufferedReader(new 
	                InputStreamReader(conn.getInputStream()));
		    while ((line = reader.readLine()) != null) {
		        data += line;
		     }
	    } catch (Exception e){
	    	return null;
	    }
	    
	    if (data == ""){
	    	return null;
	    }
	    
	    JSONArray arr = new JSONArray(data);
	    return separate(arr.getJSONObject(0).getString("r_no"));
	}
	
	private String[] separate(String stringarray){
		int tmp1 = 0;
		int tmp2 = 0;
		List<String> list = new ArrayList<String>(500);
		for (int i = 0; i < stringarray.length(); i++){
			if (i == stringarray.length() || stringarray.charAt(i) == ','){
				tmp2 = i;
				list.add(stringarray.substring(tmp1, tmp2));
				tmp1 = i + 1;
			}
		}
		
		String[] output = new String[list.size()];
		for (int i = 0; i < list.size(); i++){
			output[i] = list.get(i);
		}
		return output;
	}
	
	private JSONArray getBusData(String bn, int dir) throws Exception{
		try {
			URL url = new URL(routedb + "?");
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);

		    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

		    writer.write("bn=" + bn + "&dir=" + dir);
		    writer.flush();
		    
		    String line;
		    String data = "";
		    
		    BufferedReader reader = new BufferedReader(new 
	                InputStreamReader(conn.getInputStream()));
		    while ((line = reader.readLine()) != null) {
		        data += line;
		     }
		    
		    if (data == ""){
		    	return null;
		    }
		    
		    data = data.substring(2, data.length());
		    data = data.substring(0, data.length() - 2);
		    data = "[" + data + "]";
		    
		    if (data.equals("[]")){
		    	return null;
		    }
		    
			return new JSONArray(data);
		} catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Get the index inside the routes' names.<br>
	 * <br>
	 * Returns <code>-1</code> if the specified name isn't found.
	 * @param bus_no The bus name to be searched
	 * @return The index of the name.
	 * @throws DatabaseNotLoadedException API will try to fetch. It will be thrown only when fetching failed.
	 */
	public int getRouteIndex(String bus_no) throws DatabaseNotLoadedException{
		if (BUS_NO == null){
			try {
				BUS_NO = getRoutesDB();
			} catch (IOException e) {
				throw new DatabaseNotLoadedException("API tried to fetch routes. But resulted a exception.", e);
			}
			
			if (BUS_NO == null){
				throw new DatabaseNotLoadedException("API tried to fetch routes. But still null.");
			}
		}
		for (int i = 0; i < BUS_NO.length; i++){
			if (BUS_NO[i].equals(bus_no)){
				return i;
			}
		}
		return -1;
	}
}
