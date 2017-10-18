$(document).ready(function(){
	if (!window.location.protocol.startsWith("https:") && !window.location.protocol.startsWith("file:")){
		window.location = "https://www.kmbeta.ml/app/";
	}
});

var currLocUptTimerId;
var currLocMarker;
var map;

function initMap(){
	map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 22.25, lng: 114.1667},
        zoom: 12
    });

	//$("#waitMapModal").modal('hide');
	
	if (navigator.geolocation){
	    $("#waitMapModal").modal({backdrop: 'static', keyboard: false});
		navigator.geolocation.getCurrentPosition(function(position){
		    $("#waitMapModal").modal('hide');
			
			var pos = {
			  lat: position.coords.latitude,
			  lng: position.coords.longitude
			}
			
			currLocMarker = new google.maps.Marker({position: pos, map: map});
			
			map.setCenter(pos);
			map.setZoom(16);
			setInterval(function(){uptCurrLocMarker()}, 1000);
		}, function(){
		    $("#waitMapModal").modal('hide');
	        $("#noMapModal").modal({backdrop: 'static', keyboard: false});
		});
	} else {
	    $("#noMapModal").modal({backdrop: 'static', keyboard: false});
	}
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