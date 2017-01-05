Ext.define('OSS.ExtendModule', {
	extend: 'OSS.EngineModule',                   
    
    cellDblClick: function(gridView, htmlElement, columnIndex, dataRecord) {
    	var me = this;
    	
    	if (me.name == 'User_Butsaalt_Report') {
    		userCode = dataRecord.get('userCode');
    		if (columnIndex == 2)
    			me.app.callModule('backjilt-level-one-win');
    	} else
    	if (me.name == 'Sector_Manager_Report') {
    		userCode = dataRecord.get('userCode');
    		if (columnIndex >= 2 && columnIndex <=5)
    			this.app.callModule('toroljilt-level-one-win');
    		if (columnIndex >= 6 && columnIndex <=11) {
    			var nodes = ['Baraa_Bosgo_Tatan_Awalt_Report', '_baraa_bosgo_tatan_awalt_report', 'Борлуулалт ХТ бүрээр', 'productCode', 950, 550, 'saler-report-module', 2];
    			me.createModule(nodes, 'Baraa_Bosgo_Tatan_Awalt_Report', userCode);
    		}    			
    		if (columnIndex >= 12 && columnIndex <=13) {
    			var nodes = ['Lease_Table', '_rent_by_user', 'Харилцагчаар харах', 'userCode', 750, 450, 'user-lease-module'];
    			me.createModule(nodes, 'Lease_Table', userCode);
    		}
    		if (columnIndex >= 16 && columnIndex <=19) {
    			var nodes = ['Promotion_report', '_promotion_report', Ext.sfa.translate_arrays[langid][671], 'userCode', 950, 550, 'product-survey-module', 1];
    			me.createModule(nodes, 'Promotion_report');
    		}
    	} else
    	if (me.name == 'Captain_Manager_Report') {
    		userCode = dataRecord.get('userCode');
    		if (columnIndex == 1){
    			var nodes = ['Captain_Manager_Report', '_captain_manager_report', 'Борлуулалт үйл ажиллагааны нэгтгэл', 'userCode', 950, 550, 'product-survey-module', 1];
    			me.createModule(nodes, 'Captain_Manager_Report');
    		}   		
    	}
    },
    
    specialCommand: function() {
    	var me = this;
    	if (me.name == 'User_Route_Entry') {
			me.gridPanel.bbar = [{
 	            id: 'basic-statusbar',
 	            text: '(+) '+Ext.sfa.translate_arrays[langid][436]+',&nbsp;&nbsp;(*)'+Ext.sfa.translate_arrays[langid][441]+',&nbsp;&nbsp;-'+Ext.sfa.translate_arrays[langid][595]
 	        }];			
		}
		
		if (me.name == 'Lease_Table') {
			me.combo.setValue(userCode);
			me.loadStore();
		}
		
		if (me.name == 'Report') {
			me.gridPanel.getSelectionModel().on('selectionchange', function(sm, selectedRecord) {
		        if (selectedRecord.length) {		        	
		        	showUserCustomerStat(selectedRecord[0].get('userCode'));
		        }
		    });
		}		
    },        
   	
    expandDblClick: function(record) {
    	var me = this;
    	if (me.name == 'Filter_Customer') {
			showCustomerInfoSpecial(record.get('customerCode'));
		} else
		if (me.name == 'Top_Customer' || me.name == 'Promotion_Accept') {
			//showCustomerInfoSpecial(record.get('customerCode'));
			var nodes = ['Top_Customer_Drill', '_report_by_customer_drill', Ext.sfa.translate_arrays[langid][486], 'data', 920, 500, 'main-info-module'];
			
			if (me.name == 'Top_Customer')
				me.createModule(nodes, 'Top_Customer_Drill', record.get('customerCode')+','+me.combo1.getText()+','+me.combo2.getText());
			else
				me.createModule(nodes, 'Top_Customer_Drill', record.get('customerCode')+','+me.combo.getText()+','+me.combo1.getText());
		} else
		/*if (me.name == 'Customer') {
			showCustomerInfoSpecial(record.get('code'));
		} else*/
		if (me.name  == 'Lease_Main') {	                			         		
			var nodes = ['Lease_User_Detail', '_lease_user_day_detail', Ext.sfa.translate_arrays[langid][486], 'id', 950, 500, 'main-info-module'];
			var day = record.get('day');
			if (day == 'Эхний үлд')
				day = firstDay;
			me.createModule(nodes, 'Lease_User_Detail', day+','+me.combo2.getValue());
		} else
		if (me.name == 'Lease_Table') { 
			var nodes = ['Lease_User_Detail', '_lease_user_detail', Ext.sfa.translate_arrays[langid][486], 'id', 950, 500, 'main-info-module'];
			me.createModule(nodes, 'Lease_User_Detail', record.get('userCode')+','+record.get('customerCode'));
		} else
		if (me.name == 'Lease_Customer') {
			if (record.get('data')) {
				var nodes = ['Lease_User_Detail', '_lease_customer_detail', Ext.sfa.translate_arrays[langid][486], 'id', 950, 500, 'main-info-module'];
				me.createModule(nodes, 'Lease_User_Detail', record.get('data')+','+me.combo.getValue()+','+me.combo1.getValue());
			}
		}
    }      
});