var windowIndex = 0;

OSS.ControlPanel.override({
	createModule: function(nodes, name) {				
		if (name == 'Spec_user') {
			showUserInfoSpecial();
			this.called = true;
		} else
		if (name == 'Hand_order') {
			showOrderHandImportWindow();
			this.called = true;
		} else 
		if (name == 'Hand_sale') {
			showSaleHandImportWindow();
			this.called = true;
		}
		
		this.callParent(arguments);
	},
	
	 createStore: function() {
    	var me = this;    

        me.store = Ext.create('Ext.data.ArrayStore', {
        	fields: [
                     {name: 'module'},
                     {name: 'name'},      
                     {name: 'title'},
                     {name: 'id'},
                     {name: 'width', type: 'int'},
                     {name: 'height', type: 'int'},
                     {name: 'icon'}
            ],
            data: [ 
                   ['Users', 'users', Ext.sfa.translate_arrays[langid][264], 'userID', 830, 500, 'user-module'],
                   ['Customer', '_customer_list', Ext.sfa.translate_arrays[langid][265], 'customerID', 980, 580, 'customer-module'],
                   ['Product', 'product', Ext.sfa.translate_arrays[langid][270], 'productID', 960, 550, 'product-module'],
                  // ['Product1', 'product1', 'Барааны мэдээлэл Вита', 'productID', 960, 550, 'product-module'],
                  // ['custom-price-window', 'module', Ext.sfa.translate_arrays[langid][272], 'id', 650, 450, 'price-module'],                   
                   ['ItemsTransaction', 'items_transaction', Ext.sfa.translate_arrays[langid][649], 'itemID', 650, 465, 'item-module'],
                   ['Price', '_price_list', Ext.sfa.translate_arrays[langid][272], 'id', 500, 450, 'price-module'],
                   ['Cars', 'cars', Ext.sfa.translate_arrays[langid][643], 'id', 500, 350, 'cars-module'],
                   ['Route', 'routes', Ext.sfa.translate_arrays[langid][267], 'routeID', 450, 500, 'route-module'],                   
                   ['route-changer-data', 'module', Ext.sfa.translate_arrays[langid][700], 'id', 980, 550, 'route-changer-module'],
                   ['Route_User', 'route_user', Ext.sfa.translate_arrays[langid][288], 'id', 980, 550, 'route-user-module'],
                   ['AllDays', 'calendar', Ext.sfa.translate_arrays[langid][269], 'id', 300, 450, 'calendar-module'],
                   ['Message', 'message', 'Мессеж', 'id', 500, 350, 'message-module'],
                   ['Parent_Names', 'parent_list', 'Сүлжээ дэлгүүр', 'id', 400, 585, 'info-module'],
                   ['Promotion', 'promotion', 'Урамшуулал', 'id', 850, 450, 'promo-module'],
                   ['Packet', 'packet', Ext.sfa.translate_arrays[langid][273], '_group', 800, 400, 'info-module'],
                   ['User_Type', 'user_type', Ext.sfa.translate_arrays[langid][274], '_group', 400, 400, 'info-module'],
                   ['Ware_House', 'ware_house', Ext.sfa.translate_arrays[langid][275], 'wareHouseID', 400, 400, 'info-module'],
            ]
        });						
    }
});
	
OSS.MapModulePanel.override({
	ext: '.JPG'
});

OSS.SaleGridWindow.override({
	_group: 0,
	
	updateSale : function() {
    	var me = this;        	
    	me.salesStore.load({params:{xml:_donate(me.mainQuery[me.mainGridType]+me.sub, 'SELECT', 'Sales', 'productCode,quantity', 's,i', me.currentDate+','+me.nextDate+','+me.productSelection+','+langs[ln]+','+logged+','+mode+','+me._group)}});
    },    	
    
    createToolbar: function() {
    	var me = this;
    	
    	me._groupList = me.generateLocalCombo('_group', 'user_type', '_group', 'descr', 'Бүлэг', 150);
    	me._groupList.on('change', function() {
    		me._group = me._groupList.getValue();    		
    		me.reInterface();
    	});
    	 
	    me.tb = {
	            xtype: 'toolbar',
	            items: [{					
			            id		: 'sale-date1',
			    		text    : me.currentDate,        
				        scope   : this,	            	        
				        iconCls : 'calendar',
				        menu	: Ext.create('Ext.menu.DatePicker', {
					    	text: me.currentDate,
					        handler: function(dp, date){
					        	me.currentDate = Ext.Date.format(date, 'Y-m-d');	     				        	
					        	Ext.getCmp('sale-date1').setText(me.currentDate);	            		        		        	
					        	me.updateSale();	                       
					        }
					    })
					},{						
						id		: 'sale-date2',
			    		text    : me.nextDate,        
				        scope   : this,
				        iconCls : 'calendar',
				        menu	: Ext.create('Ext.menu.DatePicker', {
					    	text: me.nextDate,
					        handler: function(dp, date){
					        	me.nextDate = Ext.Date.format(date, 'Y-m-d');	            		        	
					        	Ext.getCmp('sale-date2').setText(me.nextDate);	            		        	
					        	me.updateSale();	                       
					        }
					    })
					},'-',me._groupList,
					{
						iconCls: 'switch',			
						enableToggle: true,
						hidden: true,
						toggleHandler: function(item, pressed) {
							if (!pressed) 
								me.sub = '';
							else
								me.sub = '_sub';
							me.updateSale();
						}
					},
					{
						iconCls: 'refresh',				
						text: Ext.sfa.translate_arrays[langid][260],
						tooltip: '<b>Refresh</b><br/>',
						handler: function() {
							me.updateSale();
						}
					},
					'-',
					{
				    	   text: Ext.sfa.translate_arrays[langid][606],
				    	   iconCls: 'group',
				    	   enableToggle: true,
				    	   pressed: false,
				    	   toggleHandler: function(item, pressed) {
				    		   if (pressed) {
				    			   me.grouping.enable();
				    			   me.summary.disable();
				    		   }
				    		   else {
				    			   me.grouping.disable();
				    			   me.summary.enable();
				    		   }
				    		   
				    		   me.updateSale();
				    	   }
				    },
				    {
			    	   text: Ext.sfa.translate_arrays[langid][533],
			    	   iconCls: 'choose',
			    	   enableToggle: true,
			    	   id: 'choose',
			    	   toggleHandler: function(item, pressed) { 	
			    		   me.fp.setVisible(pressed);
			    	   }
			        }			        
			 ]};
    }
});

OSS.DetailGridWindow.override({
    createToolbar: function() {
    	var me = this;
    	me.users = me.generateRemoteComboWithFilter('_remote_section_users', 'user_list', 'code', 'firstName', Ext.sfa.translate_arrays[langid][310], mode);

    	me.users.on('change', function(e) {
    		 me.updateSale();
    	});
    	
    	if (userCode.length > 0)
    		me.users.setValue(userCode);
    		
    	me.total = Ext.create('Ext.Button', {    			
		     text: Ext.sfa.translate_arrays[langid][616],
		     id: 'total_detail',
		     iconCls: 'bagts',
		     enableToggle: true,
		     pressed: false,
		     toggleHandler: function(item, pressed) {
		    	 me.swapInterface();
		     }
    	});
    	
    	return [{
	            xtype: 'toolbar',
	            items: [
			            {
						id		: 'detail-date',
			    		text    : me.detailDate,        
				        scope   : this,	            	        
				        iconCls : 'calendar',
				        menu	: Ext.create('Ext.menu.DatePicker', {
					    	text: me.detailDate,
					        handler: function(dp, date){
					        	me.detailDate = Ext.Date.format(date, 'Y-m-d');	            		        	
					        	Ext.getCmp('detail-date').setText(me.detailDate);	            		        		        	
					        	me.updateSale();
					        }
					    })
					},me.users,{
						text	: 'Харах',
						iconCls : 'refresh',
						handler: function() {
							me.updateSale();
						}
					},'-',
	            {
	            	text: Ext.sfa.translate_arrays[langid][496],
	            	enableToggle: true,
	            	iconCls: 'customers',
	            	handler: function(item, pressed) { 	            		
	            		if (entry == 'entry') entry = 'noenty';
	            		else entry = 'entry'; 	            		
	            		me.detailStore.load({params:{xml:_donate(me.detailQuery[me.mainGridType], 'SELECT', ' ', ' ', ' ', me.detailDate+','+me.users.getValue()+',all,'+entry)}});
	            	}
	            },	            	           
	            '-',
		        {			                 
                    iconCls: 'icon-xls',		
                    handler: function() {
                    	var model = [];
                    	model['columns'] = [];
                    	model['columns'][0] = {dataIndex:'customerCode',type:'string', header: 'Харилцагч', width: 300};
                    	model['columns'][1] = {dataIndex:'_dateStamp',type:'string', header: 'Огноо'};
                    	model['columns'][2] = {dataIndex:'type',type:'string', header: 'Зээл төлөлт', width: 200};
                    	model['columns'][3] = {dataIndex:'sum_r',type:'int', header:'Зээлээр', width: 200};
                    	model['columns'][4] = {dataIndex:'sum_ar',type:'int', header:'Бэлнээр', width: 200};
                    	model['columns'][5] = {dataIndex:'sum_a',type:'int', header:'Тушаах', width: 200};
                    	model['columns'][6] = {dataIndex:'sum_all',type:'int', header:'Нийт', width: 200};                    	
                    	
                    	doXls(model, '_user_sale_detail,_user_sale_detail');
                    }	                
        		}]
	        }];
    }
}); 

OSS.OrderGridWindow.override({
    loadStore: function() {
    	var me = this;
    	me.store.load({params:{xml:_donate('Orders', 'SELECT', 'Orders as b', 'id,_date,userCode,productCode,storageCount,availCount,requestSize,requestCount,confirmedCount,price,requestCount*price as amount,wareHouseID', 'i,s,s,i,i,f,f,i', " WHERE wareHouseID=(select wareHouseID from Users where code='"+logged+"') and productCode in (select productCode from Product_Accept where userCode='"+logged+"') and requestCount@0 and confirmedCount=0 and userCode='"+me.users.getValue()+"' and userCode=customerCode and flag=0 ORDER by _date desc,confirmedCount asc")}, 
    		callback: function(){    			
    			me.store.each(function(rec){ rec.set('agree', true) })    			
    		}});
    },
    
    createStore : function() {
    	var me = this;
    	
    	me.columns = [
           {name: 'id', type: 'int', width: 0, title: 'Дд', hidden: true},
           {name: '_date', type: 'datetime', width: 120, title: Ext.sfa.translate_arrays[langid][341], renderer:Ext.util.Format.dateRenderer('Y-m-d h:i:s')},
           {name: 'userCode', type: 'string', width: 100, flex: 1, hidden:true, title: Ext.sfa.translate_arrays[langid][310], renderer: Ext.sfa.renderer_arrays['renderUserCode'], hidden: true},
           {name: 'productCode', type: 'string', width: 200, title: Ext.sfa.translate_arrays[langid][345], renderer: Ext.sfa.renderer_arrays['renderProductCode']},            
           {name: 'storageCount', type: 'int', title: Ext.sfa.translate_arrays[langid][424], align: 'right', width: 90, renderer: Ext.sfa.renderer_arrays['renderStorageNumber']},
           {name: 'availCount', type: 'int', title: Ext.sfa.translate_arrays[langid][425], align: 'right', width: 95, renderer: Ext.sfa.renderer_arrays['renderStorageNumber']},
           {name: 'requestSize', type: 'float', title: 'Хэмжээ', align: 'right', width: 70, summaryType: 'sum', renderer: Ext.sfa.renderer_arrays['renderFNumber'], summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber']},
           {name: 'requestCount', type: 'int', title: Ext.sfa.translate_arrays[langid][426], align: 'right', width: 70, summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney']},           
           {name: 'confirmedCount', type: 'int', title: Ext.sfa.translate_arrays[langid][421], align: 'right', width: 70, field: {xtype: 'numberfield'}},
           {name: 'price', type: 'float', title: Ext.sfa.translate_arrays[langid][414], align: 'right', width: 70, renderer: Ext.sfa.renderer_arrays['renderMoney']},
           {name: 'amount', type: 'float', title: Ext.sfa.translate_arrays[langid][455], align: 'right', width: 110, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney']},
           {name: 'wareHouseID', type: 'int', title: Ext.sfa.translate_arrays[langid][375], width: 100, renderer: Ext.sfa.renderer_arrays['renderWareHouseID']},
           {name: 'agree', type: 'bool', title: 'OK', align: 'right', width: 50, xtype: 'checkcolumn', field: {xtype: 'checkbox'}}                        
        ];
    	
    	Ext.regModel('order', {	        
            fields: me.columns
        });
    	
    	me.store = Ext.create('Ext.data.JsonStore', {
            model: 'order',	        
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
    	
    	me.store_action = Ext.create('Ext.data.JsonStore', {
            model: 'order',	        
            proxy: {
    			type: 'ajax',
    			url: 'httpGW',
    			writer: {
    	           type: 'json'
    	        }
    		}
        });
    }
});


//*************BORLUULALTIIN MODULE************************

OSS.SaleModulePanel.override({
    createStore: function() {
    	var me = this;    
        me.store = Ext.create('Ext.data.ArrayStore', {
        	fields: [
                     {name: 'module'},
                     {name: 'name'},      
                     {name: 'title'},
                     {name: 'id'},
                     {name: 'width', type: 'int'},
                     {name: 'height', type: 'int'},
                     {name: 'icon'}
            ],
            data: [
                   ['sale-grid-win', 'module', Ext.sfa.translate_arrays[langid][614], ' ', 0, 0, 'main-sale-module'],
                   /*['sale-graph-win', 'module', Ext.sfa.translate_arrays[langid][605], ' ', 0, 0, 'sale-pack-module'],*/
                   ['detail-grid-win', 'module', Ext.sfa.translate_arrays[langid][519], ' ', 0, 0, 'detail-user-sale'],                                                        
                   ['padaan-sales-data-win', 'module', Ext.sfa.translate_arrays[langid][680], '_dateStamp', 700, 400, 'invoice-module'],
                   ['user-stat-window', 'module', Ext.sfa.translate_arrays[langid][604], ' ', 0, 0, 'user-sale-info'],                   
                   ['User_Route_Entry', '_user_route_entry', Ext.sfa.translate_arrays[langid][505], 'id', 900, 500, 'user-entry-module']                                                        
            ]
        });
    }
});


//***** TAILANGIIN MODULE *************

OSS.ReportModulePanel.override({

    createStore: function() {
    	var me = this;    
        me.store = Ext.create('Ext.data.ArrayStore', {
        	fields: [
                     {name: 'module'},
                     {name: 'name'},      
                     {name: 'title'},
                     {name: 'id'},
                     {name: 'width', type: 'int'},
                     {name: 'height', type: 'int'},
                     {name: 'icon'}
            ],
            data: [                 		 
				 ['SR_Report', '_sr_report', 'Төлөвлөгөө гүйцэтгэл', 'customerCode', 900, 600, 'plan-md-module'],
				 ['Rought_Report', '_rought_report', 'Rought Report', 'userCode', 900, 500, 'rought-module'],
				 ['Order_Daily_Report', '_order_daily_report', 'Захиалгын тайлан', 'userCode', 900, 500, 'daily-module'],
                 ['Daily_Report', '_daily_report', 'Daily Report', 'userCode', 900, 500, 'daily-module'],
				 ['Merchan_Report', '_merchan_report', 'Merchandise Report', 'userCode', 900, 500, 'merchan-module'],
				 ['Merchan_Report_Customer', '_merchan_customer_report', 'Merchandise Report(Customer)', 'customerCode', 900, 500, 'merchan-cs-module'],
				 ['storage-to-storage-win', 'module', 'Агуулахын хоорондын шилжилт', 'productCode', 900, 500, 'storage-cs-module']
			]
        });
    }    
});

Ext.define('OSS.AllReports', {
    id:'all-reports-win',        
    
    init : function(){  	
    },
    
    createWindow: function() {
    	window.open('http://183.177.103.19/ossReports/?mode=battrade');
    }
});

//***** AGUULAHIIN MODULE *************
OSS.StorageModulePanel.override({
    createStore: function() {
    	var me = this;    
        me.store = Ext.create('Ext.data.ArrayStore', {
        	fields: [
                     {name: 'module'},
                     {name: 'name'},      
                     {name: 'title'},
                     {name: 'id'},
                     {name: 'width', type: 'int'},
                     {name: 'height', type: 'int'},
                     {name: 'icon'}
            ],
            data: [                                      
                   ['Storage', '_storage', Ext.sfa.translate_arrays[langid][510], 'id', 750, 550, 'storage-module'],
                   /*['user-storage-info', 'module', Ext.sfa.translate_arrays[langid][280], 'userCode', 960, 600, 'user-product-module'],*/                                     
                   /*['order-grid-win', 'module', Ext.sfa.translate_arrays[langid][278], 'userCode', 960, 500, 'order-module'],*/
                   ['order-grid-win-pre', 'module', Ext.sfa.translate_arrays[langid][278]+'(PRE)', 'userCode', 960, 500, 'order-module'],                   
                   ['storage-insert-win', 'module', Ext.sfa.translate_arrays[langid][512], 'id', 500, 450, 'storage-in-module'],
                   ['orders-report-win', 'module', Ext.sfa.translate_arrays[langid][639], 'id', 700, 450, 'storage-report-module']
                   /*['Storage_Out_Report', '_storage_out_q', Ext.sfa.translate_arrays[langid][655], 'id', 850, 550, 'storage-out-product']*/
            ] 
        });
    }    
});

OSS.PadaanSalesData.override({
	createStore : function() {
    	var me = this;
    	me.model = me.generateModel('Orders', 'orders');    	
    	me.store = me.model['readStore'];
    	me.store.groupField = 'customerCode';
    },
    
	loadStore : function() {
		var me = this;
		if (customerCode)
			me.store.load({params:{xml:_donate('Orders', 'SELECT', 'Orders', '_date,customerCode,userCode,productCode,confirmedCount/(select unit from product where code=Orders.productCode) as packet,requestCount,price,requestCount*price as amount', 's,s,s,s,f,i,i,i', " WHERE userCode='"+me.users.getValue()+"' and _date@='"+me.sdate1+"' and _date!'"+me.sdate2+"' and customerCode='"+customerCode+"' ORDER by _date")}});
		else
			me.store.load({params:{xml:_donate('Orders', 'SELECT', 'Orders', '_date,customerCode,userCode,productCode,confirmedCount/(select unit from product where code=Orders.productCode) as packet,requestCount,price,requestCount*price as amount', 's,s,s,s,f,i,i,i', " WHERE userCode='"+me.users.getValue()+"' and _date@='"+me.sdate1+"' and _date!'"+me.sdate2+"' ORDER by _date")}});
	}
});

Ext.define('OSS.PlanInsertWindow', {
    extend: 'OSS.ExtendModule',
    id:'user-plan-insert-win',        
	width: 800,
	height: 500,
	init: function() {
		this.title = Ext.sfa.translate_arrays[langid][449];
	},

	createWindow : function(){
    	var desktop = this.app.getDesktop();
        var win = desktop.getWindow(this.id);        
        if(!win){        	        	
			this.createStore();
            win = desktop.createWindow({
                id: this.id,
                title:this.title,
                width: this.width,
                height: this.height,
                iconCls: 'icon-grid',
                animCollapse:false,
                constrainHeader:true,
                layout: 'fit',
				border: true,
                items: [this.createGrid()],
                dockedItems: this.createToolbar()
            });
        }
        win.show();
        return win;               
    },  

	initmans: function() {
		var me = this;
		me.store1.removeAll();	          		
    	for (i = 0; i < Ext.sfa.stores['user_list'].getCount(); i++) {
			var record = Ext.sfa.stores['user_list'].getAt(i);						
			if (record.data['_group'] == me.suvag.getValue() || 1==1) {
				me.store1.add({code: record.data['code']});		    					     		    				
    		}
    	}
		me.grid1.getView().refresh();
	},

	initbase: function () {
		var me = this;
    	me.store2.removeAll();
    	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
			var record = Ext.sfa.stores['product_list'].getAt(i);
			if (record.data['vendor'] == mode) {
				var ptag = me.priceTag.getValue();
				var price = Ext.sfa.renderer_arrays['renderPriceList'](record.get('code')+ptag);
				me.store2.add({code: record.data['code'], count: 0, price: price, amount: 0});	    				
			}
    	}    			
    	me.grid2.getView().refresh();
	},
	
	createStore: function() {
		var me = this;
		me.store1 = Ext.create('Ext.data.Store', {
			model: 'code_model'
		});		

		me.action_store = Ext.create('Ext.data.JsonStore', {	    	        	       
			proxy: {
				type: 'ajax',
				url: 'httpGW',	    			
				writer: {
				   type: 'json'
				}
			}
		});

		Ext.regModel('plan_grid', { 
			fields: [
				{name: 'code', type: 'string'}, 
				{name: 'price', type:'int'}, 
				{name: 'count', type: 'int'},
				{name: 'amount', type: 'int'}	                 
			]
		});

		me.store2 = Ext.create('Ext.data.Store', {
			model: 'plan_grid',
			data: []
		});
	},

	createGrid: function() {
		var me = this;
		me.sm = Ext.create('Ext.selection.CheckboxModel');
		me.grid1 = Ext.create('Ext.grid.GridPanel', {			    			
			xtype: 'gridpanel',
			border: false,	    			
			columnLines: true,
			width: 200,
			split: true,
			region: 'west',
			store: me.store1,	    		
			selModel: me.sm,
			columns: [
				  {
					  text: Ext.sfa.translate_arrays[langid][310],
					  dataIndex: 'code',	    					  
					  flex: 1,
					  renderer: Ext.sfa.renderer_arrays['renderUserCode']
				  }
			]			     			
		});
		
		me.grid2 = Ext.create('Ext.ux.LiveSearchGridPanel', {			    			
			xtype: 'gridpanel',
			border: false,	    			
			columnLines: true,
			split: true,
			region: 'center',
			store: me.store2,
			plugins: [new Ext.grid.plugin.CellEditing({
    	        clicksToEdit: 1,
				pluginId: 'cellplugin',
				listeners: {
					'afteredit': function(e) {
						
					}
				}
    	    })],
			features: [{
				id: 'plan_product_insert',
				ftype: 'summary'
			}],
			columns: [
 				  new Ext.grid.RowNumberer({width:42}),
				  {
					  text: Ext.sfa.translate_arrays[langid][345],
					  dataIndex: 'code',
					  width: 200,
					  renderer: Ext.sfa.renderer_arrays['renderProductCode']
				  },
				  {
					  text: 'Үнэ',
					  dataIndex: 'price',
					  align:'right',
					  width: 80,
					  renderer: Ext.sfa.renderer_arrays['renderMoney']
				  },
				  {
					  text: Ext.sfa.translate_arrays[langid][6],
					  dataIndex: 'count',
					  align:'right',
					  summaryType: 'sum',	     
					  field: { 
						  xtype: 'numberfield'
					  },
					  width: 90,
					  renderer: Ext.sfa.renderer_arrays['renderNumber']
				  }, 
				  {
					  text: Ext.sfa.translate_arrays[langid][5],
					  dataIndex: 'amount',
					  align:'right',
					  width: 120,					  
					  summaryType: function(records){
						  var i = 0,
							  length = records.length,
							  total = 0,
							  record;

						  for (; i < length; ++i) {
							  record = records[i];
							  total += record.get('count') * record.get('price');
						  }
						  return total;
					  },
					  renderer: Ext.sfa.renderer_arrays['renderPriceMoney'],
					  summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney']
				  }
			]
		});	
				
		me.panel = Ext.widget('form', {
			border: false,
			layout: {
				type: 'border',
				align: 'stretch'
			},
			items: [me.grid1, me.grid2]
		});

   	    return me.panel;
	},	

	createToolbar: function() {
    	var me = this;
		me.priceTag = Ext.create('Ext.form.ComboBox', {	        		
			width: 120,
			name: 'priceTag',        
			margins: '0 0 0 5',
			xtype: 'combo',
			emptyText: Ext.sfa.translate_arrays[langid][419],
			store: Ext.sfa.stores['price_types'],
			displayField: 'descr',
			valueField: 'id',
			queryMode: 'local',
			triggerAction: 'all'
		});	
		
		me.priceTag.on('change', function(e) {		
			me.initbase();
			me.grid2.getView().refresh();
		});
		me.priceTag.setValue(1);

		me.suvag = Ext.create('Ext.form.ComboBox', {	        		
			width: 120,
			name: '_group',        
			margins: '0 0 0 5',
			xtype: 'combo',
			store: Ext.sfa.stores['user_type'],
			displayField: 'descr',
			valueField: '_group',
			value: mode,
			queryMode: 'local',
			triggerAction: 'all',
			emptyText: Ext.sfa.translate_arrays[langid][592]
		});	 
		me.suvag.on('change', function(e) {
			me.initmans();
			me.grid1.getView().refresh();
		});		
		me.suvag.setValue(1);

		me.dateMenu = Ext.create('Ext.menu.DatePicker', {
			text: currentDate,
			handler: function(dp, date){	    	        	
				me.startBtn.setText(Ext.Date.format(date, 'Y-m-d'));	    	        		                      
			}
		});
			
		me.dateMenu1 = Ext.create('Ext.menu.DatePicker', {
			text: nextDate,
			handler: function(dp, date){	    	        	
				me.endBtn.setText(Ext.Date.format(date, 'Y-m-d'));	    	        	         
			}
		});
		
		me.startBtn = Ext.create('Ext.button.Button', {
			fieldLabel: Ext.sfa.translate_arrays[langid][481],
			text    : firstDay,        
			scope   : this,
			name	: 'startDate',
			iconCls: 'calendar',
			menu	: me.dateMenu
		});
		me.endBtn = Ext.create('Ext.button.Button', {
			fieldLabel: Ext.sfa.translate_arrays[langid][410],
			text    : lastDay, 
			margins : '0 0 0 5',
			name	: 'endDate',
			scope   : this,
			iconCls: 'calendar',
			menu	: me.dateMenu1
		});
		
		me.planName = Ext.create('Ext.form.ComboBox', {			
			store: Ext.create('Ext.data.JsonStore', {
				model: 'plan_name',	     	   
				mode: 'remote',
				autoLoad: true,
				proxy: {
					type: 'ajax',
					url: 'httpGW?xml='+_donate('_remote_plan_names', 'SELECT', ' ', ' ', ' '),
					reader: {
						type: 'json',
						root:'items',
						totalProperty: 'results'
					}	            
				}
			}),
			xtype: 'combo',
			name: 'name',
			displayField: 'name',
			valueField: 'name',
			typeAhead: true,        
			allowBlank: false,
			queryMode: 'remote',
			mode: 'remote',
			triggerAction: 'all',
			margins: '0 0 0 5', 
			emptyText: Ext.sfa.translate_arrays[langid][448],
			selectOnFocus: true,
			width: 150
		}); 

		me.buttons = [me.startBtn,me.endBtn,/*me.suvag*/,me.planName,me.priceTag,
			{
				text: Ext.sfa.translate_arrays[langid][417],
				iconCls: 'icon-add',
				handler: function() {               
					var users = '';
					var records = me.grid1.getSelectionModel().getSelection();
					var params = 'planName='+me.planName.getValue()+'&_group=0&startDate='+me.startBtn.getText()+'&endDate='+me.endBtn.getText();
					
					Ext.each(records, function(record){
						users += record.get('code')+',';		                    		
					});
													
					var pro = '';
					for (i = 0; i < me.store2.getCount(); i++) {	                    		
						var rec = me.store2.getAt(i);
						if (rec.data['count'] > 0)
							pro = pro + ('productCode='+rec.data['code']+'&count='+rec.data['count']+'&price='+me.priceTag.getValue()+',');	                    		
					}	                    		                    	                    
									
					if (pro != '' && me.planName.getValue() != '' && users != '') {
						me.action_store.load({params:{xml:_donate('business_plan', 'WRITER', 'B_Plan', pro, params, users)}, 
							callback: function() {
							}});
										
						
						Ext.MessageBox.alert(Ext.sfa.translate_arrays[langid][328], Ext.sfa.translate_arrays[langid][584], null);
										
						me.store2.removeAll();
						for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
							var record = Ext.sfa.stores['product_list'].getAt(i);
							if (record.data['vendor'] == section) {
								me.store2.add({code: record.data['code'], count: 0, price: 0, amount: 0});	    				
							}
						}
					} else 
						Ext.MessageBox.alert(Ext.sfa.translate_arrays[langid][328], 'Хоосон байна !', null);
				}
			}, 
			{
				text: Ext.sfa.translate_arrays[langid][471],
                iconCls: 'icon-delete',
				handler: function() {
					me.initbase();
				}
			}];		
		me.addHelpButtons();
		return [{
			xtype: 'toolbar',
			items: me.buttons
		}];
	}
});