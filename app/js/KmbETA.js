/**
 * KmbETA JavaScript Base File
 */
const ETA_SERVER_URL = "https://api.kmbeta.ml/api_v3.php";
const ETA_DATAFEED_SERVER_URL = "https://api.kmbeta.ml/datafeed_v3.php";

var ArrivalManager = function(route, bound, stopCode, lang, stopSeq){
    this.route = route;
    this.bound = bound;
    this.stopCode = stopCode;
    this.lang = lang;
    this.stopSeq = stopSeq;
	this.etaData = {};
    
	var updatePrivateEtaData = function(data){
		console.log(ArrivalManager.prototype);
	};
	this.getCachedEtaData = function(){
		console.log("getCachedEtaData");
		console.log(this.etaData);
	    return this.etaData;
	}
    this.getEtaData = function(){
        var url = ETA_SERVER_URL +
            "?action=geteta" +
            "&lang=" + (this.lang == 1 ? "tc" : "en") +
            "&route=" + this.route +
            "&bound=" + (this.bound + 1) +
            "&stop=" + this.stopCode +
            "&stop_seq=" + this.stopSeq;
        
		var obj = this;
        return $.ajax({
            url:  url,
            dataType: "json",
            cache: false,
            success: function(data){
			    obj.etaData = data;
			},
            error: function(err0, err1, err2){
                
            }
        });
    }
} 

var ArrivalTime = function(etaData){
    if (etaData == null){
         return;
    }
    
    this.etaData = etaData;
}

ArrivalTime.prototype.getNumberOfIndex = function(){
    if (this.etaData.response === undefined){
        return -1;  
    }
    return this.etaData.response.length;
}

ArrivalTime.prototype.getResponseByIndex = function(index){
    var max = this.getNumberOfIndex();
    if (index < max || index >= max){
        //return null;
    }
    return this.etaData.response[index];
}

ArrivalTime.prototype.getHr = function(index){
    var resp = ArrivalTime.prototype.getResponseByIndex(index);
    if (resp == null){
        return -1;  
    }
    return parseInt(resp.substring(0,2));
}

ArrivalTime.prototype.getMin = function(index){
    var resp = ArrivalTime.prototype.getResponseByIndex(index);
    if (resp == null){
        return -1;  
    }
    return parseInt(resps.substring(3,5));
}

ArrivalTime.prototype.isScheduledTime = function(index){
    var resp = ArrivalTime.prototype.getResponseByIndex(index);
    if (resp == null){
        return -1;  
    }
    return resp.ei.equals("Y");
}

ArrivalTime.prototype.isWifi = function(index){
    var resp = ArrivalTime.prototype.getResponseByIndex(index);
    if (resp == null){
        return -1;  
    }
    return resp.wifi.equals("Y");
}

//Unknown W
ArrivalTime.prototype.isW = function(index){
    var resp = ArrivalTime.prototype.getResponseByIndex(index);
    if (resp == null){
        return -1;  
    }
    return resp.w.equals("Y");
}

//Unknown OL
ArrivalTime.prototype.isOl = function(index){
    var resp = ArrivalTime.prototype.getResponseByIndex(index);
    if (resp == null){
        return -1;  
    }
    return resp.ol.equals("Y");
}

//Unknown EOT
ArrivalTime.prototype.getEot = function(index){
    var resp = ArrivalTime.prototype.getResponseByIndex(index);
    if (resp == null){
        return -1;  
    }
    return resp.eot;
}

//Unknown Ex
ArrivalTime.prototype.getEx = function(index){
    var resp = ArrivalTime.prototype.getResponseByIndex(index);
    if (resp == null){
        return -1;  
    }
    return resp.ex;
}

//Unknown bus_service_type
ArrivalTime.prototype.getBusServiceType = function(index){
    var resp = ArrivalTime.prototype.getResponseByIndex(index);
    if (resp == null){
        return -1;  
    }
    return resp.bus_service_type;
}

var ServerTime = function(){
    $.ajax({
        url: ETA_DATAFEED_SERVER_URL,
        cache: false,
        dataType: "json",
        success: function(data){
            ServerTime.prototype.timestr = data[0].stime;
            ServerTime.prototype.hr = parseInt(ServerTime.prototype.timestr.substring(11,13));
            ServerTime.prototype.min = parseInt(ServerTime.prototype.timestr.substring(14,16));
            ServerTime.prototype.sec = parseInt(ServerTime.prototype.timestr.substring(17,19));
        },
        error: function(){
            return -1;
        }
    });
}

var Database = function(){}

Database.prototype.loadWebDb = function(){
    return $.ajax({
        xhr: function(){
            var xhr = new window.XMLHttpRequest();
            xhr.addEventListener("progress", function(evt){
                if (evt.lengthComputable){
                    var p = evt.loaded / evt.total;
                    Database.prototype.loadProgressHandler(p);
                    console.log("Loaded DB: " + (p * 100) + "%");                   
                }
            }, false);
            return xhr;
        },
        url: "https://db.kmbeta.ml/kmbeta_db.b64",
        cache: true,
        dataType: "text",
        success: function(data){
            Database.prototype.db = JSON.parse(atob(data));
            console.log("Load db success");
        },
        error: function(err, err1, err2){
            console.log("error");
            console.log(err);
            console.log(err1); 
        }
    }); 
}
