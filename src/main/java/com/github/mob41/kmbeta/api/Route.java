package com.github.mob41.kmbeta.api;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a route in the database
 * @author Anthony
 *
 */
public class Route {
	
	/**
	 * Default maximum bounds of a route
	 */
	public static final int DEFAULT_MAXIMUM_BOUNDS = 2;

	/**
	 * This route's name
	 */
	private final String routeName;
	
	/**
	 * Stores bounds of this route
	 */
	private final List<RouteBound> bounds;
	
	/**
	 * Creates a new Route instance, representing a route in the database.<br>
	 * <br>
	 * It is restricted to create a instance out of this package.
	 * @param routeName This route's name
	 */
	protected Route(String routeName){
		this.routeName = routeName;
		bounds = new ArrayList<RouteBound>(DEFAULT_MAXIMUM_BOUNDS);
	}
	
	/**
	 * Adds a new route bound to this route.
	 * @param bound The instance of RouteBound
	 */
	protected void addBound(RouteBound bound){
		bounds.add(bound);
	}
	
	/**
	 * Removes a route bound from this route by index
	 * @param index The index of the bound in the list
	 */
	protected void removeBound(int index){
		bounds.remove(bounds);
	}
	
	/**
	 * Removes a route bound from this route by the instance
	 * @param bound The instance of RouteBound
	 */
	protected void removeBound(RouteBound bound){
		bounds.remove(bound);
	}
	
	/**
	 * Gets the route bound list in the route.
	 * @return The list of RouteBound instances
	 */
	public List<RouteBound> getList(){
		return bounds;
	}
	
	/**
	 * Gets the name of this route
	 * @return A string
	 */
	public String getName(){
		return routeName;
	}
	
	/**
	 * Gets a <code>RouteBound</code> from the route.
	 * @param index The index of the RouteBound in the list. Probably <code>0</code> or <code>1</code>
	 * @return The route bound instance. If not exist, returns <code>null</code>
	 */
	public RouteBound getBound(int index){
		return bounds.get(index);
	}
	
	
}
