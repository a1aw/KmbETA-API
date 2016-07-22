package com.github.mob41.kmbeta.exception;

/***
 * ETA database not loaded exception.
 * @author mob41
 *
 */
public class DatabaseNotLoadedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseNotLoadedException(){
		super("The static database wasn't loaded at the moment.");
	}
	
	public DatabaseNotLoadedException(String message){
		super(message);
	}
	
	public DatabaseNotLoadedException(String message, Throwable cause){
		super(message, cause);
	}
	
	public DatabaseNotLoadedException(Throwable cause){
		super(cause);
	}
}
