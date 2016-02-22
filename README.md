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

1. Download the builder and put it to a folder.
![1](http://mob41.github.io/images/KmbETA/builder1.png)
![1desc](http://mob41.github.io/images/KmbETA/builder2.png)
2. Go to [KMB ETA Equiry page](http://www.kmb.hk/tc/services/eta_enquiry.html)
![2desc](http://mob41.github.io/images/KmbETA/builder3.png)
3. Select any route
![3desc](http://mob41.github.io/images/KmbETA/builder4.png)
4. Enter the security code
![4desc]((http://mob41.github.io/images/KmbETA/builder5.png)
5. Enter the details to the Builder (See the image)
![5desc](http://mob41.github.io/images/KmbETA/builder6.png)
6. Inspect the code (See the image)
![6desc](http://mob41.github.io/images/KmbETA/builder7.png)
7. Edit the HTML of the List ```<ul>```
![7desc](http://mob41.github.io/images/KmbETA/builder8.png) 
8. Select All ```CTRL+A``` and Copy ```CTRL+C``` the code
![8desc](http://mob41.github.io/images/KmbETA/builder9.png)
9. Paste the code to the Builder and press "Build database"
![9desc](http://mob41.github.io/images/KmbETA/builder10.png)
10. Repeat the process again for the next bound.
![10desc](http://mob41.github.io/images/KmbETA/builder11.png)
11. Repeat the whole process again for the next bus.


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
