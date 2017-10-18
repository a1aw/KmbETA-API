$(document).ready(function(){
	if (!window.location.protocol.startsWith("https:") && !window.location.protocol.startsWith("file:")){
		window.location = "https://www.kmbeta.ml/app/";
	}
});

var locAccessCheckTimerId;
var currLocUptTimerId;
var currLocMarker;
var map;
var kmbDb;
var currPos;

var routePaths = [];
//var routeLines = [];
//var routeMarkers = [];

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
			
			currLocMarker = new google.maps.Marker({position: pos, map: map});
			
			map.setCenter(pos);
			map.setZoom(16);
			currLocUptTimerId = setInterval(function(){uptCurrLocMarker()}, 1000);
			
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
	    $("#loadDbModal").modal('hide');
		listAllRoutesInRange(1);
	});
}

function listAllRoutesInRange(range){
	var rr = findRoutesInRange(range);
	
	for (var i = 0; i < rr.length; i++){
	    var index = findRouteIndex(rr[i].name);
        if (index != -1){
			buildRouteLinesAndMarkers(index, 0);
        } else {
            console.log("Error: Could not find route index for " + rr[i].name);    
			alert("Error: Could not find route index for " + rr[i].name + "\nPlease report to GitHub issue tracker!");   
		}		
	}
}

function getRandomColor() {
  var letters = '0123456789ABCDEF';
  var color = '#';
  for (var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
}

function buildRouteLinesAndMarkers(routeIndex, boundIndex){
	var coord = [];
	
	var routeStops = kmbDb.db.buses[routeIndex].bounds[boundIndex].stops;
	
	for (var i = 0; i < routeStops.length; i++){
	    coord.push({
		    lat: parseInt(routeStops.lat),
            lng: parseInt(routeStops.lng)			
		});	
	}
	
	var color = getRandomColor();
	var path = new google.maps.Polyline({
		path: coord,
		geodesic: true,
		strokeColor: color,
		strokeOpacity: 0.7,
		strokeWeight: 4
	});
	
	path.setMap(map);
	routePaths.push(path);
}

function findRouteIndex(routeName){
    for (var i = 0; i < kmbDb.db.buses.length; i++){
	    if (kmbDb.db.buses[i].name === routeName){
            return i;
		}		
	}
    return -1;	
}

function findRoutesInRange(range){
    var sr = findStopsInRange(range);
	
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

function findStopsInRange(range){
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
                var d = distance(currPos.lat, currPos.lng, stops[y].lat, stops[y].lng);
				if (d <= range && !isStopCodeInArray(o, stops[y].stopcode)){
					o.push(stops[y]);
				}
            }			
		}
    }
	return o;
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

function uptCurrLocMarker(){
	navigator.geolocation.getCurrentPosition(function(position){
		var pos = {
		  lat: position.coords.latitude,
		  lng: position.coords.longitude
		}
		currPos = pos;
		currLocMarker.setPosition(pos);
	}, function(){
		clearInterval(currLocUptTimerId);
		$("#waitMapModal").modal('hide');
	    $("#noMapModal").modal({backdrop: 'static', keyboard: false});
	});
}