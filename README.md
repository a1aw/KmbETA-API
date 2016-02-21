# KmbETA-API
A API client for getting KMB bus's ETA.

[Pre-release (0.0.1-SNAPSHOT)](https://github.com/mob41/KmbETA-API/releases/tag/0.0.1-SNAPSHOT)

[DB Builder (Pre-release 0.0.1-SNAPSHOT)](https://github.com/mob41/KmbETA-DBBuilder/releases/tag/0.0.1-SNAPSHOT)

Requires: JSON

JavaDoc: [http://mob41.github.io/KmbETA-API](http://mob41.github.io/KmbETA-API)

# Tutorial
You can get ETA data from KMB using:
```
import com.mob41.kmbapi.KmbApi;

public static void main(String[] args){
    //First, you need to call getETAdata(String route, String stopcode, int language, int bound, int stop_sequence)
    //The function will save the data into memory
    KmbApi.getETAdata("1A", "ST01T01100", KmbApi.ENGLISH_LANG, 1, 999);
    
    //Second, you need to call getServerTime() for arrival calculation purpose
    KmbApi.getServerTime();
    
    //Third, you can get the arrival time, remaining time of the bus 1A to reach Tsim Sha Tsui
    System.out.println("Arrival Time: " + KmbApi.getFormattedTimeDefined());
    System.out.println("Remaining Time: " + KmbApi.getRemainingFormattedTime());
}
```
## Bus database
As KMB doesn't give out the bus database, you have to build it yourself.
Download "KMB Database Builder" from the release.
[DB Builder (Pre-release 0.0.1-SNAPSHOT)](https://github.com/mob41/KmbETA-DBBuilder/releases/tag/0.0.1-SNAPSHOT)


--- not available until further notice ---

Put the "bus_stopdb.properties" database file next to your application.
Load your database file by using:
```
import com.mob41.kmbapi.KmbApi;

public static void main(String[] args){
    //First, load the database.
    KmbApi.loadDatabase();
    
    //Second, then you can do database searching stuffs!
    //We can find how many buses in Tsim Sha Tsui bus-stop
    System.out.println(Arrays.deepToString(KmbApi.getStopBuses("ST01T01100").toArray());
    
    //We can also find is this bus-no exist in database
    System.out.println(Boolean.toString(KmbApi.isBusExistInDB("IamNotABus")); //false
    
    System.out.println(Boolean.toString(KmbApi.isBusExistInDB("1A")); //true
}
```
