package com.github.mob41.kmbeta.exception;

/***
 * The specified Bus No. was invalid.
 * @author mob41
 *
 */
public class InvalidArrivalTargetException extends InvalidException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidArrivalTargetException(){
		super();
	}
	
	public InvalidArrivalTargetException(String message){
		super(message);
	}
	
	public InvalidArrivalTargetException(String message, Throwable cause){
		super(message, cause);
	}
	
	public InvalidArrivalTargetException(Throwable cause){
		super(cause);
	}
}
