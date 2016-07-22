package com.mob41.kmbeta.api.tests;

import org.junit.Test;

import com.github.mob41.kmbeta.api.ArrivalManager;
import com.github.mob41.kmbeta.api.MultiArrivalManager;
import com.github.mob41.kmbeta.exception.CouldNotLoadDatabaseException;
import com.github.mob41.kmbeta.exception.InvalidArrivalTargetException;

public class MultiArrivals {
	
	@Test
	public void test() throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		MultiArrivalManager mularr = new MultiArrivalManager(50);
		ArrivalManager arr0 = new ArrivalManager("1A", "SA06T10000", 1, ArrivalManager.ENGLISH_LANG, false); //SAU MAU PING (CENTRAL)
		ArrivalManager arr1 = new ArrivalManager("1", "CH15T11000", 1, ArrivalManager.ENGLISH_LANG, false); //CHUK YUEN EST. BUS TERMINUS
		mularr.addArrivalManager(arr0);
		mularr.addArrivalManager(arr1);
		
		mularr.fetchAllData(); //Request all manager to fetch data (This also run getServerTime)
		
		for (int i = 0; i < 2; i++){
			ArrivalManager man = mularr.getArrivalManagers().get(i);
			System.out.println("Arr" + i + " BusNo: " + man.getBusNo());
			System.out.println("Arr" + i + " Bound: " + man.getBound());
			System.out.println("Arr" + i + " StopCode: " + man.getStopCode());
			System.out.println("Arr" + i + " StopSeq: " + man.getStopSeq());
			System.out.println("Arr" + i + ": " + man.getRemainingArrivalMinuteText());
		}
	}
}
