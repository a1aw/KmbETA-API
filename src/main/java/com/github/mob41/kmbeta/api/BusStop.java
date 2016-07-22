package com.github.mob41.kmbeta.api;

public class BusStop {
	
	/**
	 * Linked route
	 */
	private final Route route;
	
	/**
	 * Linked route bound
	 */
	private final RouteBound routeBound;
	
	/**
	 * This route bound index
	 */
	private final int bound;
	
	/**
	 * This bus stop's latitude
	 */
	private final double lat;
	
	/**
	 * This bus stop's longitude
	 */
	private final double lng;
	
	/**
	 * This bus stop's location (Area)
	 */
	private final String area;
	
	/**
	 * This stop code (Sub-area)
	 */
	private final String stopcode;
	
	/**
	 * This stop name in English
	 */
	private final String stopname_eng;
	
	/**
	 * This stop name in Chinese
	 */
	private final String stopname_chi;
	
	/**
	 * This stop's address in English
	 */
	private final String addr_eng;
	
	/**
	 * This stop's address in Chinese
	 */
	private final String addr_chi;
	
	/**
	 * This stop's normal fare (No longer exist now)
	 */
	private final double normal_fare;
	
	/**
	 * This stop's air conditioner fare (Normal fare)
	 */
	private final double air_cond_fare;
	
	/**
	 * This stop sequence
	 */
	private final int stop_seq;
	
	/**
	 * Creates a new BusStop instance, representing a bus stop in the database<br>
	 * <br>
	 * It is restricted to create a BusStop instance out of the package
	 * @param linkedRoute The linked route
	 * @param linkedBound The linked route bound
	 * @param bound This route bound
	 * @param lat This stop's latitude
	 * @param lng This stop's longitude
	 * @param area This stop's location (Area)
	 * @param stopcode This stop code
	 * @param stopname_eng This stop name in English
	 * @param stopname_chi This stop name in Chinese
	 * @param addr_eng This stop's address in English
	 * @param addr_chi This stop's address in Chinese
	 * @param normal_fare This stop's normal fare/no air-con (No longer exist now)
	 * @param air_cond_fare This stop's air-conditioner fare (Normal fare by now)
	 * @param stop_seq This stop sequence
	 */
	protected BusStop(Route linkedRoute, RouteBound linkedBound, int bound, double lat, double lng,
			String area, String stopcode, String stopname_eng, String stopname_chi, String addr_eng,
			String addr_chi, double normal_fare, double air_cond_fare, int stop_seq){
		this.route = linkedRoute;
		this.routeBound = linkedBound;
		this.bound = bound;
		this.lat = lat;
		this.lng = lng;
		this.area = area;
		this.stopcode = stopcode;
		this.stopname_eng = stopname_eng;
		this.stopname_chi = stopname_chi;
		this.addr_eng = addr_eng;
		this.addr_chi = addr_chi;
		this.normal_fare = normal_fare;
		this.air_cond_fare = air_cond_fare;
		this.stop_seq = stop_seq;
	}
	
	/**
	 * Gets the linked route
	 * @return The Route instance
	 */
	public Route getRoute(){
		return route;
	}
	
	/**
	 * Gets the linked route bound
	 * @return The RouteBound instance
	 */
	public RouteBound getBound(){
		return routeBound;
	}
	
	/**
	 * Gets this linked route bound's index in the route
	 * @return The route bound's index in the route
	 */
	public int getBoundIndex(){
		return bound;
	}
	
	/**
	 * Gets this stop's latitude
	 * @return This stop's latitude
	 */
	public double getLatitude(){
		return lat;
	}
	
	/**
	 * Gets this stop's longitude
	 * @return This stop's longitude
	 */
	public double getLongitude(){
		return lng;
	}
	
	/**
	 * Gets this stop's area
	 * @return This stop's area
	 */
	public String getArea(){
		return area;
	}
	
	/**
	 * Gets this stop code
	 * @return This stop code in String
	 */
	public String getStopCode(){
		return stopcode;
	}
	
	/**
	 * Gets the stop name in English
	 * @return Stop name in English in String
	 */
	public String getStopNameInEnglish(){
		return stopname_eng;
	}
	
	/**
	 * Gets the stop name in Chinese
	 * @return Stop name in Chinese in String
	 */
	public String getStopNameInChinese(){
		return stopname_chi;
	}
	
	/**
	 * Gets the address of this stop in English
	 * @return The address in English
	 */
	public String getAddressInEnglish(){
		return addr_eng;
	}
	
	/**
	 * Gets the address of this stop in Chinese
	 * @return The address in Chinese
	 */
	public String getAddressInChinese(){
		return addr_chi;
	}
	
	/**
	 * @deprecated 
	 * There is no more "No Air-con" buses in Hong Kong<br>
	 * It will return <code>0.0</code> usually<br>
	 * Use <code>getAirCondFare()</code> instead.<br>
	 * <br>
	 * Gets the normal/No air-con fare (No longer exist now)
	 * @return Normal/No air-con fare of this stop
	 */
	public double getNormalFare(){
		return normal_fare;
	}
	
	/**
	 * Gets the air-conditioner fare of this stop, instead of normal fare
	 * @return Air-conditioner fare of this stop
	 */
	public double getAirCondFare(){
		return air_cond_fare;
	}
	
	/**
	 * Gets the stop sequence
	 * @return Stop sequence
	 */
	public int getStopSeq(){
		return stop_seq;
	}	
}