package com.github.mob41.kmbeta.api;

import org.json.JSONException;
import org.json.JSONObject;

public class ArrivalTime {
	
	private static final String lastdeparted_msg = ArrivalManager.lastdeparted_msg;
	
	private JSONObject etadata = null;
	
	private String raw_time = null;
	
	private int index = -1;
	
	public ArrivalTime(JSONObject etadata) throws JSONException{
		this(etadata, 0);
	}

	public ArrivalTime(JSONObject etadata, int index) throws JSONException{
		if (etadata == null){
			this.raw_time = "Failed";
		} else if (!etadata.isNull("responsecode") && etadata.getInt("responsecode") <= 0){
			this.raw_time = "NoData";
		} else {
			String output = (String) etadata.getJSONArray("response").getJSONObject(index).get("t");
			this.raw_time = output;
		}
		this.etadata = etadata;
		this.index = index;
	}
	
	/**
	 * Gets the pure/untouched JSON passed to this instance.
	 * @return the pure/untouched <code>JSONObject</code> response
	 */
	public JSONObject getRawJSON(){
		return etadata;
	}
	
	/**
	 * Sets the target index. The response contains multiple estimated arrival time data.<br>
	 * <br>
	 * By default, the first one is selected.<br>
	 * @param index
	 */
	public void setTargetIndex(int index){
		String output = (String) etadata.getJSONArray("response").getJSONObject(index).get("t");
		
		this.raw_time = output;
		this.index = index;	
	}
	
	/***
	 * Get the arrival time (Hour Part)
	 * @return
	 * Returns -1 if arrival time is <code>null</code>.<br>
	 * Returns -2 if the fetched time is not an integer.<br>
	 * Returns -3 if last departed.
	 */
	public int getArrivalHour(){
		if (raw_time == null){
			return 1;
		}
		if (raw_time.equals(lastdeparted_msg)){
			return -3;
		}
		String preoutput;
		int output;
		preoutput = raw_time.substring(0, 2);
		try {
			output = Integer.parseInt(preoutput);
		} catch (NumberFormatException e){
			output = -2;
		}
		return output;
	}
	
	/***
	 * Get the arrival time (Minute Part)
	 * @return
	 * Returns -1 if arrival time is <code>null</code>.<br>
	 * Returns -2 if the fetched time is not an integer.<br>
	 * Returns -3 if last departed.
	 */
	public int getArrivalMin(){
		if (raw_time == null){
			return 1;
		}
		if (raw_time.equals(lastdeparted_msg)){
			return -3;
		}
		String preoutput;
		int output;
		preoutput = raw_time.substring(3, 5);
		try {
			output = Integer.parseInt(preoutput);
		} catch (NumberFormatException e){
			output = -2;
		}
		return output;
	}
	
	/***
	 * Returns whether the specified arrival time is a scheduled time.
	 * @param i The index of arrival times. See the KMB App for details.
	 * @return Boolean
	 */
	public boolean isScheTime(int i){
		if (etadata == null){
			return true;
		}
		System.out.println(etadata);
		String isScheTimeString = (String) etadata.getJSONArray("response").getJSONObject(i).get("ei");
		System.out.println(isScheTimeString);
		return isScheTimeString.equals("Y");
	}
	
	/***
	 * Returns whether the selected <code>index</code> arrival time is a scheduled time.
	 * @return Boolean
	 */
	public boolean isScheTime(){
		return isScheTime(index);
	}
	
	/**
	 * Get the raw fetched arrival time
	 * @return Raw String Data
	 */
	public String getRawArrivalTime(){
		return raw_time;
	}
}
