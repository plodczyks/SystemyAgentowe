var map;
var infoWindow;
var connection;
var clients = 0;
var directionService;
var directionsDisplay;
var startLocation = [];
var endLocation = [];
var polyline = [];
var poly2 = [];
var marker = [];
var timerHandle = [];
var factor = [];
var tick = 20; // milliseconds, frequency of marker update
var elapsedtime = [];
var names = [];
var arrivetime = [];
function initialize(){
	
	directionsDisplay = new Array();
	connection = new WebSocket('ws://127.0.0.1:8888');
	connection.onopen = function () {
    console.log('Connected!');
    };
   
    connection.onerror = function (error) {
    console.log('WebSocket Error ' + error);
    };
	
	infowindow = new google.maps.InfoWindow(
    { 
      size: new google.maps.Size(150,50)
    });
	
	var myLatLng = new google.maps.LatLng(38.176730, 15.594710);
    
	var myOptions = {
      zoom: 12,
      mapTypeId: google.maps.MapTypeId.ROADMAP,
	  center : myLatLng
    }
    map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
	
    
	connection.onmessage = function (e) {
		var obj = JSON.parse(e.data);
		if (obj.type=="WAREHOUSE"){
			addMarker(new google.maps.LatLng(obj['start']['lat'],obj['start']['lng']), obj.type);
		} else {
		var markstart = new google.maps.LatLng(obj['start']['lat'],obj['start']['lng']);
		var markend = new google.maps.LatLng(obj['end']['lat'],obj['end']['lng']);
		setRoute(markstart,markend, obj.type, obj.time);
		arrivetime[clients] = obj.time;
		names[clients]=obj.type;
		clients=clients+1;
		}
	}
}
 function addMarker(markerPosition, type) {
	 var ico;
	  if(type=="FERRY"){
		  ico = "ship.png"
	  }
	  else if (type == "TRUCK"){
		  ico = "truck.png"
	  } else if (type == "WAREHOUSE"){
		  ico = "warehouse.png"
	  }
      var marker = new google.maps.Marker({
        position: markerPosition,
        map:map,
        title:type,
		icon: ico
      });
	 return marker;
    }
function setRoute(startLoc, endLoc, type, time){
	
	var rendererOptions = {
        map: map,
        suppressMarkers : true,
        preserveViewport: true
    }
	directionsService = new google.maps.DirectionsService();

    var travelMode = google.maps.DirectionsTravelMode.DRIVING;
	
	var request = {
        origin: startLoc,
        destination: endLoc,
        travelMode: travelMode
    };
	directionsService.route(request,makeRouteCallback(clients,directionsDisplay[clients]));

	function makeRouteCallback(routeNum,disp){
        if (polyline[routeNum] && (polyline[routeNum].getMap() != null)) {
         startAnimation(routeNum,0);
         return;
        }
        return function(response, status){
          
          if (status == google.maps.DirectionsStatus.OK){

            var bounds = new google.maps.LatLngBounds();
            var route = response.routes[0];
            startLocation[routeNum] = new Object();
            endLocation[routeNum] = new Object();

			var distance = 0;
			for(var i=0;i<response.routes[0].legs.length;i++)
				distance+=response.routes[0].legs[i].distance.value;
			
            polyline[routeNum] = new google.maps.Polyline({
            path: [],
            strokeColor: '#FFFF00',
            strokeWeight: 3
            });

            poly2[routeNum] = new google.maps.Polyline({
            path: [],
            strokeColor: '#FFFF00',
            strokeWeight: 3
            });     


            // For each route, display summary information.
            var path = response.routes[0].overview_path;
            var legs = response.routes[0].legs;


            disp = new google.maps.DirectionsRenderer(rendererOptions);     
            disp.setMap(map);
            disp.setDirections(response);

              
            for (i=0;i<legs.length;i++) {
              if (i == 0) { 
                startLocation[routeNum].latlng = legs[i].start_location;
                startLocation[routeNum].address = legs[i].start_address;
                marker[routeNum] = addMarker(legs[i].start_location, type);
              }
              endLocation[routeNum].latlng = legs[i].end_location;
              endLocation[routeNum].address = legs[i].end_address;
              var steps = legs[i].steps;

              for (j=0;j<steps.length;j++) {
                var nextSegment = steps[j].path;                
                var nextSegment = steps[j].path;

                for (k=0;k<nextSegment.length;k++) {
                    polyline[routeNum].getPath().push(nextSegment[k]);
                }

              }
            }

         }       

         polyline[routeNum].setMap(map);
		 factor[routeNum]=parseInt(distance*tick/1000/time);
         startAnimation(routeNum);  
    } // else alert("Directions request failed: "+status);

  }	
	
}
    var lastVertex = 1;
    var stepnum=0;
    var eol= [];
	
//----------------------------------------------------------------------                
 function updatePoly(i,d) {
 // Spawn a new polyline every 20 vertices, because updating a 100-vertex poly is too slow
    if (poly2[i].getPath().getLength() > 80) {
          poly2[i]=new google.maps.Polyline([polyline[i].getPath().getAt(lastVertex-1)]);
          // map.addOverlay(poly2)
        }

    if (polyline[i].GetIndexAtDistance(d) < lastVertex+2) {
        if (poly2[i].getPath().getLength()>1) {
            poly2[i].getPath().removeAt(poly2[i].getPath().getLength()-1)
        }
            poly2[i].getPath().insertAt(poly2[i].getPath().getLength(),polyline[i].GetPointAtDistance(d));
    } else {
        poly2[i].getPath().insertAt(poly2[i].getPath().getLength(),endLocation[i].latlng);
    }
 }
//----------------------------------------------------------------------------

function animate(index,d) {
   if (d>eol[index]) {

      marker[index].setPosition(endLocation[index].latlng);
	  console.log("finished " + names[index] + " " + index + '\n' + "difference: " + (arrivetime[index] - (performance.now()-elapsedtime[index])/1000) + " s");
      return;
   }
    var p = polyline[index].GetPointAtDistance(d);
    marker[index].setPosition(p);
    updatePoly(index,d);
    timerHandle[index] = setTimeout("animate("+index+","+(d+factor[index])+")", tick);
}

//-------------------------------------------------------------------------

function startAnimation(index) {
        if (timerHandle[index]) clearTimeout(timerHandle[index]);
        eol[index]=polyline[index].Distance();
        //map.setCenter(polyline[index].getPath().getAt(0));

        poly2[index] = new google.maps.Polyline({path: [polyline[index].getPath().getAt(0)], strokeColor:"#FFFF00", strokeWeight:3});
        timerHandle[index] = setTimeout("animate("+index+","+factor[index]+")",tick);  // Allow time for the initial map display
		elapsedtime[index]=performance.now();
		
}