package com.mob41.kmbeta.api.tests;

import org.junit.Test;

import com.mob41.kmbeta.api.ArrivalManager;
import com.mob41.kmbeta.exception.CouldNotLoadDatabaseException;
import com.mob41.kmbeta.exception.InvalidArrivalTargetException;

public class Get2ABusArrival {
	
	@Test
	public void test() throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		final String busno = "2A";
		final String stopcode = "LO02T10000";
		final String stopname = "LOK WAH BUS TERMINUS";
		final int bound = 1;
		ArrivalManager arr = new ArrivalManager(busno, stopcode, bound, ArrivalManager.ENGLISH_LANG, false);
		arr.fetchNewData(); //This also run getServerTime()
		System.out.println("Arrival status at " + stopname + " is " + arr.getArrivalTimeRemaining_Formatted());
		System.out.println("Stopname: " + stopname);
		System.out.println("Stopcode:" + stopcode);
	}
}
