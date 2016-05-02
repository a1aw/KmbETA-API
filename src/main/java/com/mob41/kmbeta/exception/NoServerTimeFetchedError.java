package com.mob41.kmbeta.exception;

/***
 * The Server Time wasn't fetched before. It kept <code>null</code>.
 * @author mob41
 *
 */
public class NoServerTimeFetchedError extends InvalidException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoServerTimeFetchedError(){
		super();
	}
	
	public NoServerTimeFetchedError(String message){
		super(message);
	}
	
	public NoServerTimeFetchedError(String message, Throwable cause){
		super(message, cause);
	}
	
	public NoServerTimeFetchedError(Throwable cause){
		super(cause);
	}
}
