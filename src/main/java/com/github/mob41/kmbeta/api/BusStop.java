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
	 * This stop code
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
	 * @param stopcode This stop code
	 * @param stopname_eng This stop name in English
	 * @param stopname_chi This stop name in Chinese
	 * @param stop_seq This stop sequence
	 */
	protected BusStop(Route linkedRoute, RouteBound linkedBound, int bound, String stopcode,
			String stopname_eng, String stopname_chi, int stop_seq){
		this.route = linkedRoute;
		this.routeBound = linkedBound;
		this.bound = bound;
		this.stopcode = stopcode;
		this.stopname_eng = stopname_eng;
		this.stopname_chi = stopname_chi;
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
	 * Gets the stop sequence
	 * @return Stop sequence
	 */
	public int getStopSeq(){
		return stop_seq;
	}	
}