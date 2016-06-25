package com.github.mob41.kmbeta.exception;

/***
 * The ETA Data wasn't fetched before. It kept <code>null</code>.
 * @author mob41
 *
 */
public class NoETADataFetchedError extends InvalidException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoETADataFetchedError(){
		super();
	}
	
	public NoETADataFetchedError(String message){
		super(message);
	}
	
	public NoETADataFetchedError(String message, Throwable cause){
		super(message, cause);
	}
	
	public NoETADataFetchedError(Throwable cause){
		super(cause);
	}
}
