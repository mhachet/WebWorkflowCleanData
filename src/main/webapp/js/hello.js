
var urlTest = 'http://data1.kew.org/reconciliation/reconcile/IpniName?query={"query":"Lamiaceae+Congea+chinensis+Moldenke"}';

var url= "my-external-resource.com?param=value";
url = 'proxy.php?url='+urlTest;
$.ajax({
    url: url,
    dataType: 'json',
    success:  function (data) {
        alert("ok");
    }
});

/*
function getXDomainRequest() {
	var xdr = null;
	
	if (window.XDomainRequest) {
		xdr = new XDomainRequest(); 
	} else if (window.XMLHttpRequest) {
		xdr = new XMLHttpRequest(); 
	} else {
		alert("Votre navigateur ne gère pas l'AJAX cross-domain !");
	}
	
	return xdr;	
}


var xdr = getXDomainRequest();
xdr.onload = function() {
	alert(xdr.responseText);
};

xdr.open("GET", urlTest);
*/

function totalRecord(){
    var xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://data1.kew.org/reconciliation/reconcile/IpniName?query={"query":"Lamiaceae+Congea+chinensis+Moldenke"}');
    xhr.addEventListener('readystatechange', function() {
        if (xhr.readyState === 4) {
            var response = JSON.parse(xhr.responseText);
            //totalRecord = lisibilite_nombre(response.totalRecords);
           // $('#totalRecord').text(totalRecord);
            //alert(response);
        }
    }, false);
    xhr.send(null);
}
totalRecord();
//xdr.send();
/*
var url = 'http://data1.kew.org/reconciliation/reconcile/IpniName?query=';

function callOtherDomain(){
	if(invocation) {
		invocation.open('GET', url, true);
		invocation.withCredentials = true;
		invocation.onreadystatechange = handler;
		invocation.send(); 
	}
}
*/
/*
function Hello($scope, $http) {
	$http.get('http://data1.kew.org/reconciliation/reconcile/IpniName?query={"query":"Quercus+alba"}').
	success(function(data) {
		$scope.query = data;
	});
}
 */

//var Reconcile = $resource('http://data1.kew.org/reconciliation/reconcile/IpniName:queryName', {queryName:'@queryName'});
var reconciliation = angular.module("monApp", ["ngResource"]);

reconciliation.config(['$httpProvider', function ($httpProvider) {
	// delete header from client:
	$httpProvider.defaults.useXDomain = true;
	delete $httpProvider.defaults.headers.common['X-Requested-With'];
}]);


reconciliation.factory("Reconcile", function($resource) {
	return $resource(url + ":queryName");//,
	/*{queryName:'@queryName'},
			        {"getQuery": 
			        	{'method': 'GET', 'params': {'query': '{"query":"Quercus+alba"}'}, isArray: true}
			        }*/
	//);
});

reconciliation.controller('myController', function($scope, Reconcile) {
	Reconcile.get({queryName:'{"query":"Lamiaceae+Congea+chinensis+Moldenke"}'}, function(data){
		$scope.query = data;
	});
});



//var recon = Reconciliation.query({'{"query"':'"queryQuercus+alba"}'});
//var param = reconcile.$getQuery();
/*
var urlCORS = 'http://updates.html5rocks.com';
var url = 'http://data1.kew.org/reconciliation/reconcile/IpniName?query={"query":"Quercus+alba"}';
var xhr = createCORSRequest('GET', urlCORS);
xhr.withCredentials = true;

xhr.send();
if (!xhr) {
  throw new Error('CORS not supported');
}
else{
	alert(xhr.responseText);
}

function createCORSRequest(method, url) {
  var xhr = new XMLHttpRequest();
  if ("withCredentials" in xhr) {

    // Check if the XMLHttpRequest object has a "withCredentials" property.
    // "withCredentials" only exists on XMLHTTPRequest2 objects.
    xhr.open(method, url, true);

  } else if (typeof XDomainRequest != "undefined") {

    // Otherwise, check if XDomainRequest.
    // XDomainRequest only exists in IE, and is IE's way of making CORS requests.
    xhr = new XDomainRequest();
    xhr.open(method, url);

  } else {

    // Otherwise, CORS is not supported by the browser.
    xhr = null;

  }
  return xhr;
}

xhr.onload = function() {
	 var responseText = xhr.responseText;
	 console.log(responseText);
	 // process the response.
	};

xhr.onerror = function() {
	console.log('There was an error!');
};

 //Access-Control-Allow-Origin: 'http://data1.kew.org/reconciliation/reconcile/IpniName';
function createRequest() {
  var result = null;
  if (window.XMLHttpRequest) {
    // FireFox, Safari, etc.
    result = new XMLHttpRequest();
    if (typeof xmlhttp.overrideMimeType != 'undefined') {
      result.overrideMimeType('text/xml'); // Or anything else
    }
  }
  else if (window.ActiveXObject) {
    // MSIE
    result = new ActiveXObject("Microsoft.XMLHTTP");
  } 
  else {
    // No known mechanism -- consider aborting the application
  }
  return result;
}

var req = createRequest(); // defined above
// Create the callback:
req.onreadystatechange = function() {
  if (req.readyState != 4) return; // Not there yet
  if (req.status != 200) {
    // Handle request failure here...
    return;
  }
  // Request successful, read the response
  var resp = req.responseText;
  // ... and use it as needed by your app.
};
req.open("GET", url, true);
req.send();
 */