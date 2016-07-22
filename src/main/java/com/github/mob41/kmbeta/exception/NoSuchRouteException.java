package com.github.mob41.kmbeta.exception;

/***
 * ETA database not loaded exception.
 * @author mob41
 *
 */
public class NoSuchRouteException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchRouteException(){
		super();
	}
	
	public NoSuchRouteException(String message){
		super(message);
	}
	
	public NoSuchRouteException(String message, Throwable cause){
		super(message, cause);
	}
	
	public NoSuchRouteException(Throwable cause){
		super(cause);
	}
}
