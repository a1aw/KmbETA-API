package com.github.mob41.kmbeta.exception;

/***
 * ETA database not loaded exception.
 * @author mob41
 *
 */
public class FetchRoutesException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FetchRoutesException(){
		super("The static database wasn't loaded at the moment.");
	}
	
	public FetchRoutesException(String message){
		super(message);
	}
	
	public FetchRoutesException(String message, Throwable cause){
		super(message, cause);
	}
	
	public FetchRoutesException(Throwable cause){
		super(cause);
	}
}
