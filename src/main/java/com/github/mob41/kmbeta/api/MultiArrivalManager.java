package com.github.mob41.kmbeta.api;

import java.util.ArrayList;
import java.util.List;

public class MultiArrivalManager {
	
	private List<ArrivalManager> multiman = null;
	
	public MultiArrivalManager(int multiples){
		multiman = new ArrayList<ArrivalManager>(multiples);
	}
	
	public void addArrivalManager(ArrivalManager man){
		multiman.add(man);
	}
	
	public void removeArrivalManager(int index){
		multiman.remove(index);
	}
	
	public List<ArrivalManager> getArrivalManagers(){
		return multiman;
	}
	
	public void fetchAllData(){
		for (int i = 0; i < multiman.size(); i++){
			ArrivalManager man = multiman.get(i);
			man.fetchNewData();
		}
	}
}
