Ext.Loader.setPath({
    'Ext.ux.desktop': 'js',
    OSS: ''
});

Ext.require('OSS.App');
Ext.onReady(function () {	
	ossModule = new OSS.Module();			
    callMain = function() {			
    	if (loaded == 5) {	    		
  			loadScript();
    	}
    	
    	if (loaded >= maxLoad && init == 0) {    		
    		init = 1;    		    		    	
    		ossApp = new OSS.App();    		    		
    		
    		setTimeout(function(){
    	        Ext.get('loading').remove();
    	        Ext.get('loading-mask').fadeOut({remove:true});
    	    }, 250);      		    		
    	}
    }   
    
    initSpec();    
	initApp();	
});