/**
 * @class Ext.ux.desktop.TaskBar
 * @extends Ext.toolbar.Toolbar
 */
Ext.define('OSS.TaskBar', {
    extend: 'Ext.ux.desktop.TaskBar', // TODO - make this a basic hbox panel...
        
    getNotification: function() {        
    	var me = this, ret = {
            minWidth: 20,
            width: 80,
            items: [],
            enableOverflow: true
        };    	    	
    	
    	me.createGrid();
    	
    	Ext.each(this.notify, function (item) {
    		if (item.id == 'notify') { 
	            ret.items.push({
	            	id: item.id,            	
	                tooltip: { text: item.name, align: 'bl-tl' },
	                overflowText: item.name,
	                iconCls: item.iconCls,	                
	                handler: function() {       	                	
                		me.checkNotify();          
                		me.createNotifyWindow();	                	
	                },
	                scope: me
	            });
    		} else {    			    			    			
    			ret.items.push({
	            	id: item.id,            	
	                tooltip: { text: item.name, align: 'bl-tl' },
	                overflowText: item.name,
	                iconCls: item.iconCls,
	                menu: Ext.create('Ext.menu.Menu', {
	                	items: [
	                	        {
	                	        	text: 'Монгол',
	                	        	handler: function() {		                	        		
	                	        		Ext.MessageBox.confirm('Хэл сонгох', 'Монгол хэлийг сонгох уу?', function (btn){
	                	        			if (btn == 'yes')
	                	        	        document.location.href = '?l=0';
	                	        	    });
	                	        	}
	                	        }, 
	                	        {
	                	        	text: 'Русский',
	                	        	handler: function() {		                	        		
	                	        		Ext.MessageBox.confirm('Выберите язык', 'Выберите русский язык?', function (btn){
	                	        			if (btn == 'yes')
	                	        	        document.location.href = '?l=1';
	                	        	    });
	                	        	}
	                	        },
	                	        {
	                	        	text: 'English',
	                	        	handler: function() {	                	        			                	        		
	                	        		Ext.MessageBox.confirm('Select language', 'Select english?', function (btn){
	                	        			if (btn == 'yes')
	                	        	        document.location.href = '?l=2';
	                	        	    });
	                	        	}
	                	        }
	                	]
	                }),
	                scope: me
	            });
    		}
        });

        return ret;
    },
    
    createNotifyWindow: function() {
    	var me = this;    	    	       
        
    	var desktop = this.app.getDesktop();
        me.win = desktop.getWindow('notify-grid-win');
                       
        if(!me.win){        	
        	me.win = desktop.createGadget({
                id: 'notify-grid-win',                
                width:450,
                height:195,                
            	draggable: false,            	
                x : me.getWidth() - 450,
                y : desktop.getHeight() - me.getHeight() - 195,                 
                animCollapse:false,                
                closeable: false,
                frame: false,
                border: false,
                constrainHeader: false,
                minimizable: false,
                maximizable: false,
                closeable: false,        
                closeAction: 'hide',
                layout: 'fit',
                items: [me.grid]                               
            });
        	me.win.show();
        } else {
        	if (me.win.isVisible())
        		me.win.hide();
        	else
        		me.win.show();
        }
    },
    
    checkNotify: function() {
    	var me = this;
    	me.store.load({params:{xml:_donate('_notification', 'SELECT', 'notification', ' ', ' ', logged)},
			callback: function(){
				if (me.store.getCount() > 0) {    		   
					if (me.win && !me.win.isVisible())		        	
		        		me.win.show();
					//me.items.get('notify').enable();    		
					me.notifybar.items.get('notify').setIconCls('notify_on');
				}
				else {
					me.notifybar.items.get('notify').setIconCls('notify');
					//me.items.get('notify').disable();
				}				
			}});    	    	
    },        
    
    onQuickStartClick: function (btn) {    	
    	if (btn.module == 'box-module') {    		
    		ossApp.onBoxMode();
    	} else
    	if (btn.module == 'logout') {
    		ossApp.onLogout();    		
    	} else {
    		var module = this.app.getModule(btn.module);
    		if (module) {
    			module.createWindow();
    		}
    	}
    },        
    
    createStore : function() {
    	var me = this;
    	
    	Ext.regModel('notify', {
    	    idProperty: 'content',
    	    fields: [{name: 'content', type:'string'},{name: 'text', type:'string'}]
    	});	

    	me.store = Ext.create('Ext.data.JsonStore', {
    	    model: 'notify', 
    	    proxy: {
    			type: 'ajax',
    			url: 'httpGW',
    			method: 'POST',
    	        reader: {
    				type: 'json',
    	            root:'items',
    	            totalProperty: 'results'
    	        },
    	        actionMethods: {                    
                    read: 'POST'                   
                }             
    		}
    	});					    	
    },        
    
    callRowModule: function(record) {    	
    	if (record) {
            rec = record;	                    	                    
            if (rec.data['content'] == Ext.sfa.translate_arrays[langid][258]) {
            	var module = ossApp.getModule('order-grid-win'),
                win = module && module.createWindow();

                if (win) {
                	ossApp.getDesktop().restoreWindow(win);
                }
            } 	                    	
            else
            if (rec.data['content'] == 'Бараа олголт') {
            	var module = ossApp.getModule('complete-order-grid-win'),
                win = module && module.createWindow();

                if (win) {
                	ossApp.getDesktop().restoreWindow(win);
                }
            }
            else
            if (rec.data['content'] == 'Аюулгүйн нөөц') {
            	var nodes = ['Storage', '_storage', Ext.sfa.translate_arrays[langid][510], 'id', 500, 550, 'storage-module'];    			    			
    			ossApp.createModule(nodes, 'Storage', '', '');
            }
        }
    },
    
    createGrid : function() {
    	var me = this;
    	me.createStore();
    	
    	me.grid = Ext.create('Ext.grid.GridPanel', {			
		    width:450,
		    height:155,
		    collapsible:false,		    
		    store: me.store,		    
		    viewConfig: {
	            id: 'gv',
	            emptyText: 'No notification',
	            trackOver: false,
	            stripeRows: false	            
	        },
		    
		    columns: [{
		        text: '',
		        width: 24,   
		        dataIndex: 'content',
		        renderer: renderIcon
		    },{
		        text: 'Notifications',
		        flex: 1,		        
		        dataIndex: 'content',
		        renderer: renderTopic
		    }],
		    
		    listeners: {
		    	render: function() {
		    		me.store.load({params:{xml:_donate('_notification', 'SELECT', 'notification', ' ', ' ', logged)},
		    			callback: function(){    				
		    				if (me.store.getCount() > 0) {
		    					
		    				}
		    				else {
		    					
		    				}
		    			}});
		    	},
		    	itemclick: function(grid, record, item, index, e) {
		    		me.callRowModule(record);
		    	},
		    	selectionchange: function(model, records) {
		    		me.callRowModule(records[0]);
	            }
		    }
		});		
    	
    	return me.grid;
    }
});