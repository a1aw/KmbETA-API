package com.mob41.kmbeta.exception;

/***
 * Could not load the ETA database
 * @author mob41
 *
 */
public class CouldNotLoadDatabaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CouldNotLoadDatabaseException(){
		super();
	}
	
	public CouldNotLoadDatabaseException(String message){
		super(message);
	}
	
	public CouldNotLoadDatabaseException(String message, Throwable cause){
		super(message, cause);
	}
	
	public CouldNotLoadDatabaseException(Throwable cause){
		super(cause);
	}
}
