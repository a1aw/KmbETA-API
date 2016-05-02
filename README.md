# KmbETA-API [![Build Status](https://travis-ci.org/mob41/KmbETA-API.svg?branch=master)](https://travis-ci.org/mob41/KmbETA-API)
An API client for getting KMB bus's ETA.

[Latest API (this) Release](https://github.com/mob41/KmbETA-API/releases/latest)

[Latest DB Builder Release](https://github.com/mob41/KmbETA-DBBuilder/releases/latest)

[How to use the ETA Builder (Auto)](https://github.com/mob41/KmbETA-API/wiki/How-to-use-the-DBBuilder-(Auto-Mode))

Requires: JSON (Use KmbETA-API-x.x.x-jar-with-dependencies.jar for PACKED dependencies)

JavaDoc: [http://mob41.github.io/KmbETA-API](http://mob41.github.io/KmbETA-API)

# Tutorial
The API has been changed from ```KmbApi``` class to ```ArrivalManager```.
You have to create a new ```ArrivalManager``` instance to fetch arrival time.

```java
import com.mob41.kmbeta.api.ArrivalManager;
import com.mob41.kmbeta.exception.CouldNotLoadDatabaseException;
import com.mob41.kmbeta.exception.InvalidArrivalTargetException;

public class GetArrival {
	
	public static void main(String[] args) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		//Setup which stop you want to fetch arrival time.
		final String busno = "2A";
		final String stopcode = "LO02T10000";
		final String stopname = "LOK WAH BUS TERMINUS";
		final int bound = 1;
		
		//Hook the variables to the parameters, create a instance
		ArrivalManager arr = new ArrivalManager(busno, stopcode, bound, ArrivalManager.ENGLISH_LANG);
		
		//Tell the manager to fetch new data
		arr.fetchNewData(); //This also run getServerTime()
		
		//Print out the data
		System.out.println("Arrival status at " + stopname + " is " + arr.getArrivalTimeRemaining_Formatted());
		System.out.println("Stopname: " + stopname);
		System.out.println("Stopcode:" + stopcode);
	}
}
```

In the new API, I also added ```MultiArrivalManager``` to fetch different arrival time directly, conveniently.
It is used in the ```KmbETA-UI``` [[Link]](https://github.com/mob41/KmbETA-UI). Here's an [example](https://github.com/mob41/KmbETA-UI/blob/master/src/main/java/com/mob41/kmbeta/ui/UI.java#L88):

```java
import com.mob41.kmbeta.api.ArrivalManager;
import com.mob41.kmbeta.api.MultiArrivalManager;
import com.mob41.kmbeta.exception.CouldNotLoadDatabaseException;
import com.mob41.kmbeta.exception.InvalidArrivalTargetException;

public class MultiArrivals {
	
	public static void main(String[] args) throws InvalidArrivalTargetException, CouldNotLoadDatabaseException{
		//Creates a new MultiArrivalManager instance with size 50
		MultiArrivalManager mularr = new MultiArrivalManager(50);
		
		//Creates two ArrivalManager instance with different arrival target.
		ArrivalManager arr0 = new ArrivalManager("1A", "SA06T10000", 1, ArrivalManager.ENGLISH_LANG); //SAU MAU PING (CENTRAL)
		ArrivalManager arr1 = new ArrivalManager("1", "CH15T11000", 1, ArrivalManager.ENGLISH_LANG); //CHUK YUEN EST. BUS TERMINUS
		
		//Add them to the MultiArrivalManager
		mularr.addArrivalManager(arr0);
		mularr.addArrivalManager(arr1);
		
		//Request all managers to fetch data
		mularr.fetchAllData(); //This also run getServerTime
		
		//For-loop to loop print out all the data
		for (int i = 0; i < 2; i++){
			ArrivalManager man = mularr.getArrivalManagers().get(i);
			System.out.println("Arr" + i + " BusNo: " + man.getBusNo());
			System.out.println("Arr" + i + " Bound: " + man.getBound());
			System.out.println("Arr" + i + " StopCode: " + man.getStopCode());
			System.out.println("Arr" + i + " StopSeq: " + man.getStopSeq());
			System.out.println("Arr" + i + ": " + man.getArrivalTimeRemaining_Formatted());
		}
	}
}
```

## Bus database
You have to build a bus database for your application to fetch bus information offline.
Download "KMB Database Builder" from the release.
[KMB ETA DB Builder (Auto/Manual) (Beta v.1.5)](https://github.com/mob41/KmbETA-DBBuilder/releases)

See: [How to use the ETA Builder (Auto)](https://github.com/mob41/KmbETA-API/wiki/How-to-use-the-DBBuilder-(Auto-Mode))

Put the "bus_stopdb.properties" database file next to your application, or in the class-path.
Your database file will be loaded automatically with these conditions:
- The database memory in the API is ```null``` when ```ArrivalManager``` is being created
- Manually by ```ArrivalManager.loadDatabase(Object parent, boolean loadFromClassPath)```

It is not recommended to load the database manually, but it is needed if you need to access the database outside the ```ArrivalManager```.

```java
import com.mob41.kmbeta.api.ArrivalManager;

public class LoadDatabase {
	public static void main(String[] args){
  	  //Load the database file that is next to your application (Not in class-path)
  	  ArrivalManager.loadDatabase(false); //Or
  	  ArrivalManager.loadDatabase();
  	  
  	  //Load the database file that is inside your application (In class-path)
  	  ArrivalManager.loadDatabase(true);
  	  
  	  //Alternatively, you can specify the parent(class) to be the class-loader.
  	  ArrivalManager.loadDatabase(LoadDatabase.class, true);
  }
}
```

## Generate database
<b>!! NOTE !!</b> This function wasn't implemented in the API. Generate your database using the ```KmbETA-DB-Builder```!

It is not recommend to generate the database programmatically. As generating a database requires network connection and lots of time, the user may blame about it, it hangs the application.

```java
import com.mob41.kmbeta.api.ArrivalManager;

public class LoadDatabase {
	public static void main(String[] args){
  	  if (!(new File("bus_stopdb.properties")).exists()){
  	  	 //Generates a new database
  	 	 ArrivalManager.generateDatabase(); //A database will be put near the application.
  	  }
  	  
  	  //Load the database
  	  ArrivalManager.loadDatabase(false); //Or
  	  ArrivalManager.loadDatabase();
  }
}
```
