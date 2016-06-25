package com.github.mob41.kmbeta.api;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a bound in the database
 * @author Anthony
 *
 */
public class RouteBound {
	
	/**
	 * Default maximum bus stops
	 */
	public static final int DEFAULT_MAXIMUM_STOPS = 200;
	
	/**
	 * Stores the route that linked to this bound
	 */
	private final Route route;
	
	/**
	 * Stores the stops in this bound
	 */
	private final List<BusStop> stops;
	
	/**
	 * Creates a new route bound instance<br>
	 * <br>
	 * It is restricted to create a route bound instance out of this package
	 * @param route The route that linked to this bound
	 */
	protected RouteBound(Route route){
		stops = new ArrayList<BusStop>(200);
		this.route = route;
	}
	
	/**
	 * Adds a bus stop instance to this route
	 * @param stop The bus stop instance to be added
	 */
	protected void addStop(BusStop stop){
		stops.add(stop);
	}
	
	/**
	 * Removes a bus stop instance to this route by instance
	 * @param stop The isntance to be searched
	 */
	protected void removeStop(BusStop stop){
		stops.remove(stop);
	}
	
	/**
	 * Removes a bus stop instance to this route by index
	 * @param index The index of the list
	 */
	protected void removeStop(int index){
		stops.remove(index);
	}
	
	/**
	 * Gets the bus stop instance in the list by index
	 * @param index The index of the instance in the list
	 * @return The bus stop instance
	 */
	public BusStop getBusStop(int index){
		return stops.get(index);
	}
	
	/**
	 * Gets the list of bus stops
	 * @return A <code>List</code> instance containing bus stops
	 */
	public List<BusStop> getList(){
		return stops;
	}
	
	/**
	 * Gets the route linked to this bound
	 * @return Route linked
	 */
	public Route getRoute(){
		return route;
	}
}
