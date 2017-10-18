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
		p = p * 100;
	    $("#loadDbPb").attr("aria-valuenow", p);
	    $("#loadDbPb").attr("style", "width: " + p + "%");
	    $(".loadDbPbText").html(p + "% Complete");
	};
	var ajax = kmbDb.loadWebDb();
	ajax.done(function(){
	    $("#loadDbModal").modal('hide');
	});
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
		
		currLocMarker.setPosition(pos);
	}, function(){
		clearInterval(currLocUptTimerId);
		$("#waitMapModal").modal('hide');
	    $("#noMapModal").modal({backdrop: 'static', keyboard: false});
	});
}