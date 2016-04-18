package com.mob41.kmbeta.devtests;

import com.mob41.kmbeta.api.ArrivalManager;

public class GetBusArrival {

	public static void main(String[] args) throws Exception{
		ArrivalManager arr = new ArrivalManager("2X", "PR01W11000", 1, ArrivalManager.ENGLISH_LANG);
		System.out.println(arr.getArrivalTime_Formatted());

	}

}
