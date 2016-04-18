package com.mob41.kmbeta.exception;

/***
 * The base class of all Invalid Exception
 * @author mob41
 *
 */
public class InvalidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidException(){
		super();
	}
	
	public InvalidException(String message){
		super(message);
	}
	
	public InvalidException(String message, Throwable cause){
		super(message, cause);
	}
	
	public InvalidException(Throwable cause){
		super(cause);
	}
}
