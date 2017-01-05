Ext.define('OSS.Desktop', {
    extend: 'Ext.ux.desktop.Desktop',

    alias: 'widget.ossdesktop',   
    vanpre : false,
    widgetType_ : '_orders_man',
    
    tplData : new Ext.XTemplate(
   	        '<tpl for=".">',
   	            '<div id=\"tip\" style="width:50px; float:left"><a href=\'javascript:ossApp.callDetailModule(\"{userCode}\")\'><img width=48 height=48 src=\'shared/img/users/{userCode}.JPG\'></img></a></div>',
   	            '<div style="float:left;background: {[this.retColor(values)]}; overflow: hidden; display:inline;padding-left:5px"><table height="50px" width="195px" cellspacing=0><tr><td><b style="font-size:11px">{[this.renderUserCode(values)]}</b> <span style="font-size:9px; color:gray">|{utype}</span></td></tr><tr><td style="font-size:11px; color:green">₮&nbsp;{[this.renderMoneyValue(values)]}</span></td></tr><tr><td style="font-size:11px"><span style="color:gray">{[this.renderLastMod(values)]}</span><img src="images/space.png"/>{[this.renderBatteryLevel(values)]}<img src="images/space.png"/><a style="color:#3B5998; text-decoration: none;" href=\'javascript:ossApp.callDetailModule(\"{userCode}\")\'>{entry} цэг</a><img src="images/space.png"/><a style="color:#3B5998; text-decoration: none;" href=\'javascript:ossApp.callUserInfoModule(\"{userCode}\")\'>Илүү</a></td></tr></table></div>',
   	        '</tpl>',
   	        '<div class="x-clear"></div>',
   	        {
   	        	compiled:true,
   	        	retColor: function(v) {
   	        		if (v.row % 2 == 1) return "#EDEFF4";
   	        		else return "#FFFFFF";
   	        	},
   	        	renderUserCode: function(data) {
   	        		return Ext.sfa.renderer_arrays['renderUserCode'](data.userCode);
   	        	},
   	        	renderMoneyValue: function (v) {
   	        		if (hidden_values['show_money_value'] == 'off')
   	        			return '-.--';
   	        		
   	        		return Ext.util.Format.number(v.amount, '00,00,000.00');
   	        	},
   	        	renderLastMod: function(v) {
   	        		if (v.lastmod >= 60) return ((v.lastmod/60)|0) + ' цаг өмнө';
   	        		
   	        		return (v.lastmod + ' минут өмнө');
   	        	},
				renderBatteryLevel: function(v) {   	        		
   	        		return '<span class="battery"/>'+(v.batteryLevel + '%')+'</span>';
   	        	}
   	        }
   	 ),
    
    runTask: function() {
    	var me = this;
        clearTimeout(me.updateTimer);
        
        me.updateTimer = setTimeout(function() {
        	me.loadStore();
        	me.taskbar.checkNotify();
        	
        	me.runTask();
        }, 3*60*1000);	
    }
});

