$(document).ready(function(){
	$("#waitMapModal").modal({backdrop: 'static', keyboard: false});
});

function initMap(){
	var map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 22.25, lng: 114.1667},
        zoom: 12
    });

	//$("#waitMapModal").modal('hide');
}