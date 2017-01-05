Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'ux');
Ext.require([
    'Ext.window.*',
    'Ext.ux.GMapPanel'
]);

Ext.onReady(function(){
    var mapwin;
    
    Ext.regModel('user_markers', {		    
		idProperty: 'code',
	    fields: [                
	        {name: 'code', type: 'string'},
	        {name: 'lat', type: 'float'},
	        {name: 'lng', type: 'float'},
	        {name: 'count', type: 'int'},
	        {name: 'amount', type: 'int'},  
			{name: 'ico', type: 'int'},
	        {name: 'hhmmss', type: 'string'}
	    ]
	});
            
    var store = Ext.create('Ext.data.JsonStore', {
	    model: 'user_markers',    
	    proxy: {
			type: 'ajax',
			url: '../../httpGW',
	        reader: {
				type: 'json',
	            root:'items',
	            totalProperty: 'results'
	        }
		}
	});
    
    var me = this;    
	store.load({params:{xml:getURLParameters('xml')}, 
		 callback: function() {
			 store.each(function(rec){
				 addMarker(rec.data);
			});
		 }
	});
	
	function getIcon(data) {	
		if (data['ico'] == 3)		
			return '../../shared/icons/fam/idle.png';
		if (data['ico'] == 4)
			return '../../shared/icons/fam/home.png';
		if (data['amount'] == 0 && data['ico'] > 0)
			return '../../shared/icons/fam/marker.png';
		if (data['ico'] == 1)		
			return '../../shared/icons/fam/marker_start.png';
		if (data['ico'] == 2)
			return '../../shared/icons/fam/marker_g.png';

		return '../../shared/img/users/'+data['code']+'.jpg';
	}
	
	function getValue(data) {
		if (data['code'].length == 3) {
			v = (data['code']);
			//v = v.substring(11, v.length);
			return v;
		}
		
		if (data['code'].length > 3)
			return (data['code'])+' ['+(data['amount'])+' ₮]';			
	}
	
	function addMarker(data) {		
		var draggable = false;
		if (data['draggable']) draggable = true;
		var size = data['ico']==3?16:32;
	    var icon = new google.maps.MarkerImage(
	    			getIcon(data), //url
		            new google.maps.Size(size, size), //size
		            new google.maps.Point(0,0), //origin
		            new google.maps.Point(size/2, size/2),
		            new google.maps.Size(size, size)//scale 
		);

		 
		var marker = {
			lat: data['lat'],
			lng: data['lng'],					
			time: data['hhmmss'],
			title: getValue(data),
			draggable: draggable,
			icon: icon				
		};
		
		gmap.addMarker(marker);
	}
	
	var gmap = Ext.create('Ext.ux.GMapPanel', {
		xtype: 'gmappanel',
        center: {
        	 geoCodeAddr: '15171, Ulaanbaatar, Mongolia',
             marker: {title:''}
        },
        markers: []
	});
	
    mapwin = Ext.create('Ext.panel.Panel', {                
        renderTo: 'map',
        layout: 'fit',
        width: 1300,
        height: 800,
        border: false,
        items: gmap
    });    
});


function base64_encode (data) { 
  var b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
  var o1, o2, o3, h1, h2, h3, h4, bits, i = 0,
    ac = 0,
    enc = "",
    tmp_arr = [];

  if (!data) {
    return data;
  }

  do { // pack three octets into four hexets
    o1 = data.charCodeAt(i++);
    o2 = data.charCodeAt(i++);
    o3 = data.charCodeAt(i++);

    bits = o1 << 16 | o2 << 8 | o3;

    h1 = bits >> 18 & 0x3f;
    h2 = bits >> 12 & 0x3f;
    h3 = bits >> 6 & 0x3f;
    h4 = bits & 0x3f;

    // use hexets to index into b64, and append result to encoded string
    tmp_arr[ac++] = b64.charAt(h1) + b64.charAt(h2) + b64.charAt(h3) + b64.charAt(h4);
  } while (i < data.length);

  enc = tmp_arr.join('');

  var r = data.length % 3;

  return (r ? enc.slice(0, r - 3) : enc) + '==='.slice(r || 3);

}

function getURLParameters(paramName) 
{
    var sURL = window.document.URL.toString();  
    if (sURL.indexOf("?") > 0)
    {
       var arrParams = sURL.split("?");         
       var arrURLParams = arrParams[1].split("&");      
       var arrParamNames = new Array(arrURLParams.length);
       var arrParamValues = new Array(arrURLParams.length);     
       var i = 0;
       for (i=0;i<arrURLParams.length;i++)
       {
        var sParam =  arrURLParams[i].split("=");
        arrParamNames[i] = sParam[0];
        if (sParam[1] != "")
            arrParamValues[i] = unescape(sParam[1]);
        else
            arrParamValues[i] = "No Value";
       }

       for (i=0;i<arrURLParams.length;i++)
       {
                if(arrParamNames[i] == paramName){
                return arrParamValues[i];
             }
       }
       return "No Parameters Found";
    }
}

function make_base_auth() {	  
	  var hash = base64_encode('646d39736447467458327873597a705564326b786157636a4e30417a593278704f435246');
	  return "Basic " + hash;
}