$(document).ready(function(){
	if (!window.location.protocol.startsWith("https:") && !window.location.protocol.startsWith("file:")){
		window.location = "https://www.kmbeta.ml/app/";
	}
	
	$("#quickSelectBusRouteBtn").click(function(){
		var val = $("#quickSelectBusRoute").val();
		selectRoute(val, 1, null);
		$("#homeModal").modal("hide");
	});
	
	$("#findNearbyStopsBtn").click(function(){
		$("#homeModal").modal("hide");
		
		currCenterChgListener = map.addListener('center_changed', function(){
            removeAllListRoutes();
			
			clearTimeout(onLocChgTimeoutId);
			onLocChgTimeoutId = setTimeout(function(){
				onLocChg();
			}, 1000);
		});
	});
	
	$("#useCustomLocationBtn").click(function(){
		if (!confirm("This will stop tracking your current position.\nAre you sure?")){
			return;
		}
		$("#homeModal").modal("hide");
		navigator.geolocation.clearWatch(currLocWatchId);
		
		currCenterChgListener = map.addListener('center_changed', function(){
			currLocMarker.setPosition(map.getCenter());
		});
		
		mapClickListener = map.addListener('click', function(){
			$("#homeModal").modal({backdrop: 'static', keyboard: false});
			
			google.maps.event.removeListener(mapClickListener);
			google.maps.event.removeListener(currCenterChgListener);
			
			onLocChg();
		});
		
	});
});

var onLocChgTimeoutId;
var locAccessCheckTimerId;
var currLocUptTimerId;
var currLocWatchId;
var nearbyRoutesEtaUiUpdateTimerId;
var currLocMarker;
var map;
var kmbDb;
var currPos;

var currCenterChgListener;
var mapClickListener;

var selectedRoute;
var selectedBound;
var selectedStop;

var routePathsMarkers = [];
//var routeLines = [];
var routeMarkers = [];

var nearbyRoutes = [];
var nearbyRoutesMgr = [];

const MAX_TOOLBAR_HEIGHT = 10;

function showToolbarAnimate(maxTime = 2000){
	stepShowToolbar(0, maxTime);
}

function stepShowToolbar(percent, maxTime, finishHandler){
	if (percent < 1){
		//console.log("Step " + percent + " Maxtime: " + maxTime);
		const sliceTime = maxTime / 100;
		//console.log("Slicetime: " + sliceTime);
		
		$("#toolbar").css("height", MAX_TOOLBAR_HEIGHT * percent + "%");
		$("#map").css("top", MAX_TOOLBAR_HEIGHT * percent + "%");
	    $("#map").css("height", (100 - MAX_TOOLBAR_HEIGHT * percent) + "%");
		//console.log("ToolbarHeight: " + MAX_TOOLBAR_HEIGHT * percent);
		percent += sliceTime / maxTime;
		//console.log("Percent: " + percent);
		
		//console.log("Enter wait: " + sliceTime);
		setTimeout(function(){
			stepShowToolbar(percent, maxTime, finishHandler);
		}, sliceTime);
	} else {
		if (finishHandler){
		    finishHandler();
		}
		console.log("Done");
	    $("#map").css("height", (100 - MAX_TOOLBAR_HEIGHT) + "%");
		$("#toolbar").css("height", MAX_TOOLBAR_HEIGHT + "%");
		$("#map").css("top", MAX_TOOLBAR_HEIGHT + "%");
	}
}

function hideToolbarAnimate(maxTime = 2000){
	stepHideToolbar(1, maxTime);
}

function stepHideToolbar(percent, maxTime, finishHandler){
	if (percent > 0){
		//console.log("Step " + percent + " Maxtime: " + maxTime);
		const sliceTime = maxTime / 100;
		//console.log("Slicetime: " + sliceTime);
		
		$("#toolbar").css("height", MAX_TOOLBAR_HEIGHT * percent + "%");
		$("#map").css("top", MAX_TOOLBAR_HEIGHT * percent + "%");
	    $("#map").css("height", (100 - MAX_TOOLBAR_HEIGHT * percent) + "%");
		//console.log("ToolbarHeight: " + MAX_TOOLBAR_HEIGHT * percent);
		percent -= sliceTime / maxTime;
		//console.log("Percent: " + percent);
		
		//console.log("Enter wait: " + sliceTime);
		setTimeout(function(){
			stepHideToolbar(percent, maxTime);
		}, sliceTime);
	} else {
		if (finishHandler){
		    finishHandler();
		}
		console.log("Done");
	    $("#map").css("height", "100%");
		$("#toolbar").css("height", "0%");
		$("#map").css("top", "0%");
	}
}

function showToolbar(){
	$("#map").css("height", "90%");
	$("#map").css("top", "10%");
	$("#toolbar").css("height", "10%");
}

function hideToolbar(){
	$("#map").css("height", "100%");
	$("#map").css("top", "0%");
	$("#toolbar").css("height", "0%");
}

function initMap(){
	map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 22.25, lng: 114.1667},
        zoom: 12
    });

	//$("#waitMapModal").modal('hide');
	$("#waitMapModal").modal({backdrop: 'static', keyboard: false});
	locAccessCheckTimerId = setInterval(function(){checkLocAccessPerm()}, 1000);
	
	if (navigator.geolocation){
		navigator.geolocation.getCurrentPosition(function(position){
		    $("#waitMapModal").modal('hide');
			
			var pos = {
			  lat: position.coords.latitude,
			  lng: position.coords.longitude
			}
			currPos = pos;
			
			currLocMarker = new google.maps.Marker({
				position: pos,
				map: map,
				icon: "human.png"
			});
			
			map.setCenter(pos);
			map.setZoom(16);
			
			//map.addListener('center_changed', function(){
			//	removeAllListRoutes();
			//	recenterMarkers();
			//});
			
			currLocWatchId = navigator.geolocation.watchPosition(posChgSuccess, posChgError, {
			    enableHighAccuracy: false,
				timeout: 5000,
				maximumAge: 0
			});
			//currLocUptTimerId = setInterval(function(){uptCurrLocMarker()}, 1000);
			
			kmbEtaLoadDb();
		}, function(){
		    $("#waitMapModal").modal('hide');
	        $("#noMapModal").modal({backdrop: 'static', keyboard: false});
		});
	} else {
	    $("#noMapModal").modal({backdrop: 'static', keyboard: false});
	}
}

function kmbEtaLoadDb(){
    kmbDb = new Database();
	$("#loadDbPb").attr("aria-valuenow", "0");
	$("#loadDbPb").attr("style", "width: 0%");
	$("#loadDbPbText").html("0% Complete");
	$("#loadDbModal").modal({backdrop: 'static', keyboard: false});
	Database.prototype.loadProgressHandler = function(p){
		p = (p * 100).toFixed(2);
	    $("#loadDbPb").attr("aria-valuenow", p);
	    $("#loadDbPb").attr("style", "width: " + p + "%");
	    $(".loadDbPbText").html(p + "% Complete");
	};
	var ajax = kmbDb.loadWebDb();
	ajax.done(function(){
		setTimeout(function(){$("#loadDbModal").modal('hide');}, 1000);
		$("#homeModal").modal({backdrop: 'static', keyboard: false});
		recenterMarkers();
		
		var db = kmbDb.db.routes;
		var node = $("#quickSelectBusRoute");
		node.html("");
		for (var i = 0; i < db.length; i++){
		    $("#quickSelectBusRoute").append("<option>" + db[i] + "</option>");	
		}
		
		updateNearbyStops();
	});
}

function onLocChg(){
    removeAllListRoutes();
	recenterMarkers();
	updateNearbyStops();
}

function updateNearbyStops(){
	var node = $("#nearbyStopsListGp");
	var lat = map.getCenter().lat();
	var lng = map.getCenter().lng();
	var sr = searchNearbyStops({lat: lat, lng: lng});
	
	node.html("");
	
	nearbyRoutes = [];
	for (var i = 0; i < sr.length; i++){
		var r = getRoutesByStop(sr[i].stopcode);
		for (var j = 0; j < r.length; j++){
		    if (!isRouteNameInArray(nearbyRoutes, r[j].name)){
			    var data = r[j];
			    data.stopData = sr[i];
			    nearbyRoutes.push(data);
		    }	
		}
	}
	
	nearbyRoutesMgr = [];
	for (var i = 0; i < nearbyRoutes.length; i++){
	    node.append("<a href=\"#\" onclick=\"selectRoute('" + nearbyRoutes[i].name + "', " + nearbyRoutes[i].bound + ", '" + nearbyRoutes[i].stopData.stopcode + "'); $('#homeModal').modal('hide');\" class=\"list-group-item\"><h5 class=\"list-group-item-heading\">" + nearbyRoutes[i].name + "</h5><p class=\"list-group-item-text\" id=\"nearbyRouteEta_" + nearbyRoutes[i].stopData.stopcode + "_" + nearbyRoutes[i].name + "\">---</p><p class=\"list-group-item-text\">" + nearbyRoutes[i].stopData.stopname_eng + " (" + Math.ceil(nearbyRoutes[i].stopData.distance * 1000) + " m)</p></a>");
		var v = new ArrivalManager(nearbyRoutes[i].name, nearbyRoutes[i].bound, nearbyRoutes[i].stopData.stopcode, "en", nearbyRoutes[i].stopseq);
		v.getEtaData();
		nearbyRoutesMgr.push(v);
	}
	
	nearbyRoutesEtaUiUpdateTimerId = setInterval(function(){updateNearbyRoutesEtaUi()}, 5000);
}

function updateNearbyRoutesEtaUi(){
    for (var i = 0; i < nearbyRoutesMgr.length; i++){
		var n = nearbyRoutesMgr[i];
		var at = new ArrivalTime(n.etaData);
		
		if (at.getNumberOfIndex() > 0){
			console.log(at.getResponseByIndex(0));
		    $("#nearbyRouteEta_" + n.stopCode + "_" + n.route).html(at.getResponseByIndex(0).t);	
		} else {
		    $("#nearbyRouteEta_" + n.stopCode + "_" + n.route).html("No Response");	
		}
    }	
}

function getStopSeq(route, bound, stopcode){
	var db = kmbDb.db.buses;
	
	var index = findRouteIndex(route);
	if (index == -1){
	    return -1;	
	}
	
	console.log(stopcode);
	var stops = db[index].bounds[bound].stops;
	for (var i = 0; i < stops.length; i++){
	    console.log(i + ": " + stops[i].stopcode + " compare " + stopcode + ": " + (stops[i].stopcode == stopcode));
		if (stops[i].stopcode == stopcode){
	        console.log(stops[i]);
		    return i;	
		}
	}
	return -1;
}

function searchNearbyStops(pos){
    var node = $("#nearbyStopsListGp");
	node.html("");
	
	var sr = findStopsInRange(pos, 0.5, true);
	
	sr.sort(function(a, b){
		if (a.distance < b.distance){
		    return -1;	
		} else if (a.distance > b.distance){
		    return 1;	
		} else {
		    return 0;	
		}
	});
	
	return sr;
}

function recenterMarkers(){
	if (selectedRoute != null && selectedStop != null){
		selectRoute(selectedRoute, selectedBound, selectedStop);
		return;
	}
	var lat = map.getCenter().lat();
	var lng = map.getCenter().lng();
	listAllStopsInRange({lat: lat, lng: lng}, 2);	
}

function selectRoute(route, bound, stopcode){
	google.maps.event.removeListener(currCenterChgListener);
	
	selectedRoute = route;
	selectedStop = stopcode;
	selectedBound = bound;
    removeAllListRoutes();
	
	var i = findRouteIndex(route);
	
	if (i == -1){
		return;
	}
	
	var stopseq = getStopSeq(route, bound, stopcode);
	console.log(stopseq);
	
	showToolbarAnimate(250);
    buildRouteLinesAndMarkers(i, selectedBound, stopseq);	
}

function deselectRoute(){
    removeAllListRoutes();
	selectedRoute = null;
	selectedStop = null;
	recenterMarkers();
	
	hideToolbarAnimate(250);
	$("#homeModal").modal({backdrop: 'static', keyboard: false});
}

function removeAllListRoutes(){
    for (var i = 0; i < routePathsMarkers.length; i++){
        routePathsMarkers[i].path.setMap(null);
		for (var x = 0; x < routePathsMarkers[i].markers.length; x++){
		    routePathsMarkers[i].markers[x].setMap(null);
		}
    }
	routePathsMarkers = [];
	
    for (var i = 0; i < routeMarkers.length; i++){
        routeMarkers[i].setMap(null);
    }
	routeMarkers = [];
}

function listAllRoutesInRange(pos, range){
	console.log(pos);
	var rr = findRoutesInRange(pos, range);
	
	for (var i = 0; i < rr.length; i++){
	    var index = findRouteIndex(rr[i].name);
        if (index != -1){
			console.log("Listing " + rr[i].name);
			buildRouteLinesAndMarkers(index, 0);
        } else {
            console.log("Error: Could not find route index for " + rr[i].name);    
			alert("Error: Could not find route index for " + rr[i].name + "\nPlease report to GitHub issue tracker!");   
		}		
	}
	console.log("Listed all");
}

function getRoutesByStop(stopcode){
	var db = kmbDb.db.buses;
	
	var o = [];
	for (var i = 0; i < db.length; i++){
		var bounds = db[i].bounds;
	    for (var x = 0; x < bounds.length; x++){
		    var stops = bounds[x].stops;
            for (var y = 0; y < stops.length; y++){
                if (stops[y].stopcode === stopcode){
					db[i].bound = x;
					db[i].stopseq = y;
				    o.push(db[i]);	
				}
            }			
		}
	}
	return o;
}

function listAllStopsInRange(pos, range){
	var sr = findStopsInRange(pos, range);
	
	for (var i = 0; i < sr.length; i++){
		var lat = parseFloat(sr[i].lat);
		var lng = parseFloat(sr[i].lng);
		var coord = {
			lat, lng
		};
		var m = new google.maps.Marker({
			position: coord,
			map: map
		});
		
		m.addListener('click', function(){
			var d = getStopInfo(this.getPosition().lat(), this.getPosition().lng());
	        var iw = new google.maps.InfoWindow({
		        content: d
	        });
	        iw.open(map, this);
		});
		routeMarkers.push(m);
	}
}

function getStopInfo(lat, lng){
	var stop = getStopByLatLng(lat, lng);
	var cs = "<div id=\"content\"><strong>" + stop.stopname_eng + "</strong><p>Stop-Code: " + stop.stopcode + "</p><p>Buses: ";
	
	var ss = getRoutesByStop(stop.stopcode);
	
	for (var x = 0; x < ss.length; x++){
	    cs += "<a href=\"javascript:selectRoute('" + ss[x].name + "', " + stop.bound + ", '" + stop.stopcode + "')\">" + ss[x].name + "</a>";
        if (x != ss.length - 1){
			cs += ", ";
        }			
	}
	
	cs += "</p></div>";
	
	return cs;
}

function getRouteStopEtaInfo(lat, lng){
	var stop = getStopByLatLng(lat, lng);
	var gid = "route_" + selectedRoute + "_" + stop.bound + "_" + stop.stopcode + "_eta";
	
	var cs = "<div id=\"content\"><strong>" + stop.stopname_eng + "</strong><p>Route: " + selectedRoute + "</p><p>Stop-Code: " + stop.stopcode +"</p><p>ETA: <span id=\"" + gid + "\">Getting ETA data...</span></p><p><a href=\"javascript:deselectRoute()\">Deselect Route</a></p></div>";
	
	console.log(stop.bound);
	var am = new ArrivalManager(selectedRoute, stop.bound , stop.stopcode, 0, stop.stopseq);
	
	am.getEtaData().done(function(){
		console.log(am.etaData);
		var at = new ArrivalTime(am.etaData, 0);
		//$("#" + gid).html(at.getHr() + ":" + at.getMin());
		var n = $("#" + gid);
		if (at.getNumberOfIndex() > 0){
			n.html("");
			for (var i = 0; i < at.getNumberOfIndex(); i++){
			    n.append("<h4>" + at.getResponseByIndex(i).t + "</h4>");
			}
		} else {
		    n.html("No Response Data");
		}
	});
	
	return cs;
}

function getRandomColor() {
  var letters = '0123456789ABCDEF';
  var color = '#';
  for (var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
}

function showStopInfo(marker){
    var d = getRouteStopEtaInfo(marker.getPosition().lat(), marker.getPosition().lng());
	var iw = new google.maps.InfoWindow({
		content: d
	});
	iw.open(map, marker);	
}

function buildRouteLinesAndMarkers(routeIndex, boundIndex, openInfoStopSeq){
	var coord = [];
	
	console.log("RI: " + routeIndex + " BI: " + boundIndex);
	console.log(kmbDb.db.buses[routeIndex]);
	console.log(kmbDb.db.buses[routeIndex].bounds[boundIndex]);
	var routeStops = kmbDb.db.buses[routeIndex].bounds[boundIndex].stops;
	
	//Build COORDS
	for (var i = 0; i < routeStops.length; i++){
	    coord.push({
		    lat: parseFloat(routeStops[i].lat),
            lng: parseFloat(routeStops[i].lng)			
		});	
	}
	
	//Render polyline
	var color = getRandomColor();
	var path = new google.maps.Polyline({
		path: coord,
		geodesic: true,
		strokeColor: color,
		strokeOpacity: 1,
		strokeWeight: 8
	});
	path.setMap(map);
	
	var markers = [];
	
	//Render markers
	for (var i = 0; i < coord.length; i++){
		var label = kmbDb.db.buses[routeIndex].name + ": " + (i + 1);
		var m = new google.maps.Marker({
			position: coord[i],
			map: map,
			label: label
		});
		
		m.addListener('click', function(){
			showStopInfo(this);
		});
		
		if (openInfoStopSeq == i){
		    showStopInfo(m);
            map.setCenter(coord[i]);	
            map.setZoom(18);			
		}
		
		markers.push(m);
	}

	path.setMap(map);
	routePathsMarkers.push({
		path: path,
		markers: markers
	});
}

function findRouteIndex(routeName){
    for (var i = 0; i < kmbDb.db.buses.length; i++){
	    if (kmbDb.db.buses[i].name === routeName){
            return i;
		}		
	}
    return -1;	
}

function findRoutesInRange(pos, range, customStops){
	var sr;
	if (customStops){
		sr = customStops;
	} else {
        sr = findStopsInRange(pos, range);
	}
	
    var i;
	var x;
	var y;
    var o = [];
	
	var db = kmbDb.db.buses;
	for (i = 0; i < db.length; i++){
		var bounds = db[i].bounds;
		for (x = 0; x < bounds.length; x++){
		    var stops = bounds[x].stops;
            for (y = 0; y < stops.length; y++){
				if (isStopCodeInArray(sr, stops[y].stopcode) &&
				    !isRouteNameInArray(o, db[i].name)){
					o.push(db[i]);
				}
            }			
		}
    }
	
    return o;	
}

function findStopsInRange(pos, range, includeDistance){
    var db = kmbDb.db.buses;
    var i;
	var x;
	var y;
	var o = [];
    for (i = 0; i < db.length; i++){
		var bounds = db[i].bounds;
		for (x = 0; x < bounds.length; x++){
		    var stops = bounds[x].stops;
            for (y = 0; y < stops.length; y++){
                var d = distance(pos.lat, pos.lng, stops[y].lat, stops[y].lng);
				if (d <= range && !isStopCodeInArray(o, stops[y].stopcode)){
					var data = stops[y];
					if (includeDistance){
					    data.distance = d;	
					}
					o.push(data);
				}
            }			
		}
    }
	return o;
}

function getStopByLatLng(lat, lng){
	console.log("Lat:");
	console.log(lat);
	console.log("Long:");
	console.log(lng);
    var db = kmbDb.db.buses;
    var i;
	var x;
	var y;
	var o = [];
    for (i = 0; i < db.length; i++){
		var bounds = db[i].bounds;
		for (x = 0; x < bounds.length; x++){
		    var stops = bounds[x].stops;
            for (y = 0; y < stops.length; y++){
                if (isDiffNotBigger(stops[y].lat, lat, 0.00001) &&
				isDiffNotBigger(stops[y].lng, lng, 0.00001)){
					stops[y].bound = x;
					console.log("Found");
				    return stops[y];	
				}
            }			
		}
    }
	console.log("Not found");
	return null;
}

function isDiffNotBigger(val0, val1, big){
    if (val0 > val1){
	    return (val0 - val1) < big;	
	} else {
		return (val1 - val0) < big;
	}
}

function isRouteNameInArray(array, name){
	for (var i = 0; i < array.length; i++){
	    if (name === array[i].name){
            return true;
        }		
	}
	return false;
}

function isStopCodeInArray(array, stopcode){
	for (var i = 0; i < array.length; i++){
	    if (stopcode === array[i].stopcode){
            return true;
        }		
	}
	return false;
}

function getIndexOfStopCodeInArray(array, stopcode){
	for (var i = 0; i < array.length; i++){
	    if (stopcode === array[i].stopcode){
            return i;
        }		
	}
	return -1;
}

function distance(lat1, lon1, lat2, lon2) {
  var p = 0.017453292519943295;    // Math.PI / 180
  var c = Math.cos;
  var a = 0.5 - c((lat2 - lat1) * p)/2 + 
          c(lat1 * p) * c(lat2 * p) * 
          (1 - c((lon2 - lon1) * p))/2;

  return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
}

function checkLocAccessPerm(){
    navigator.geolocation.getCurrentPosition(function(position){
		$("#waitMapModal").modal('hide');
		clearInterval(locAccessCheckTimerId);
	}, function(){});
}

function posChgSuccess(position){
	var pos = {
		lat: position.coords.latitude,
		lng: position.coords.longitude
	};
	currPos = pos;
	currLocMarker.setPosition(pos);
}

function posChgError(err){
	if (err.code == 3){
		if (confirm("The device does not return a new location to the application.\nYour location isn't updated. Are you still want to continue?")){
			return;
		}
	}
    navigator.geolocation.clearWatch(currLocWatchId);
	console.log("=================================");
	console.log("Error stacktrace object:");
	console.log(err);
	console.log("=================================");
	alert("Error occurred while watching the position:\n\n" + err + "\n\nThe application is now aborted.");
	$("#waitMapModal").modal('hide');
	$("#noMapModal").modal({backdrop: 'static', keyboard: false});
}

//Deprecated
/*
function uptCurrLocMarker(){
	navigator.geolocation.getCurrentPosition(function(position){
		var pos = {
		  lat: position.coords.latitude,
		  lng: position.coords.longitude
		};
		currPos = pos;
		currLocMarker.setPosition(pos);
	}, function(){
		clearInterval(currLocUptTimerId);
		$("#waitMapModal").modal('hide');
	    $("#noMapModal").modal({backdrop: 'static', keyboard: false});
	});
}
*/