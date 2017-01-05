Ext.namespace('Ext.sfa');
Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'ux');

Ext.QuickTips.init();
var ossApp;
var ossModule;
var callMain;
var appName = "/vita";
var codec = 0;
var userCode = '';
var section = 'vitafit,pepsi';
var customerCode = '';
var langs = ['mon', 'rus', 'eng'];
var langid = langs[ln];
var loaded = 0;
var init = 0;
var feature = [false, true];
var hidden_values = [];
var product_visible = [];
var productSelection = 'all';
var user_sale_history = [];
var products = [], selections = [];
var currencyChr = '₮';
var weekFields = [];
var help_model;
var entry = 'entry';
var box = false;
var selectedStoreItem = false;

var selectedYear, selectedMonthRange;
var now = new Date();
var currentDate = Ext.Date.format(now, 'Y-m-d');
var currentWeek = new Date(currentDate).getWeek();
var year = now.getFullYear(), month = now.getMonth()+1, week, day = now.getDate();
var firstDay = year+'-'+(month<10?'0'+month:month)+'-01', lastDay = year+'-'+(month<10?'0'+month:month)+'-'+daysInMonth(month, year);
var nextDate = getNextDate();
var detailDate = currentDate;
var begin_time = 0, end_time = 0;
var delay_time = 30;

var digitArray = new Array('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f');

Ext.sfa.translate_arrays = [];
Ext.sfa.stores = [];
Ext.sfa.renderer_arrays = [];
Ext.sfa.params = [];
Ext.sfa.moduleview = [];
Ext.sfa.where = [];
Ext.sfa.staticModels = [];
Ext.sfa.windows = [];
Ext.sfa.combos = [];

Ext.sfa.product_accept = [];

Ext.sfa.where['Users'] = " ";
Ext.sfa.where['Route_User'] = " WHERE 1=1 mnp ";
Ext.sfa.where['Promotion'] = " WHERE section like '#"+mode+"#'";
Ext.sfa.where['Price'] = " WHERE productCode in (select code from Product where vendor='"+mode+"')";
Ext.sfa.where['User_Route_Entry'] = currentDate;
Ext.sfa.where['Plan_Execute'] = ' ORDER by _date desc';
Ext.sfa.where['Product_count'] = ' ORDER by _date';
Ext.sfa.where['Product'] = " order by class";
Ext.sfa.where['Storage_in'] = ' ORDER by _date desc';

//params - [x] search button, [x] - add,remove button, [x] - toolbar button hide show, [x] - feature list, [x] - add form column count 
Ext.sfa.params['Users'] = '010G3';
Ext.sfa.params['Sales'] = '010G3';
Ext.sfa.params['Orders'] = '010G3';
Ext.sfa.params['AllDays'] = '01002';
Ext.sfa.params['Price'] = '11002';
Ext.sfa.params['Cars'] = '01002';
Ext.sfa.params['Customer'] = '110G4';
Ext.sfa.params['Product'] = '110G3';
Ext.sfa.params['Product1'] = '110G3';
Ext.sfa.params['Packet'] = '110G3';
Ext.sfa.params['ItemsTransaction'] = '010G3';
Ext.sfa.params['User'] = '11002';
Ext.sfa.params['Route'] = '01001';
Ext.sfa.params['Message'] = '01001';
Ext.sfa.params['Parent_Names'] = '01001';
Ext.sfa.params['Promotion'] = '010G3';
Ext.sfa.params['Route_User'] = '01003';
Ext.sfa.params['User_Type'] = '01001';
Ext.sfa.params['ModuleName'] = '00000';
Ext.sfa.params['Promotion_Accept'] = '000G2';
Ext.sfa.params['Ware_House'] = '01001';
Ext.sfa.params['Banned'] = '010G2';
Ext.sfa.params['Freedom'] = '010G2';
Ext.sfa.params['Evalution'] = '010G2';
Ext.sfa.params['Stand'] = '01001';
Ext.sfa.params['Package'] = '01002';
Ext.sfa.params['User_Route_Entry'] = '000Q2';
Ext.sfa.params['User_Orders'] = '000S0';
Ext.sfa.params['Lease_Main'] = '000Q2';
Ext.sfa.params['Lease_Table'] = '00LG0';
Ext.sfa.params['Lease_Customer'] = '00LG0';
Ext.sfa.params['Lease_Customer_Detail'] = '000Q2';
Ext.sfa.params['Plan'] = '00PQ2';
Ext.sfa.params['B_PLan'] = '010S3';
Ext.sfa.params['Ocet'] = '000Q0';
Ext.sfa.params['User_Products'] = '000W2';
Ext.sfa.params['Storage'] = '11SS2';
Ext.sfa.params['Storage_in'] = '000S2';
Ext.sfa.params['Storage_q'] = '000G2';
Ext.sfa.params['Storage_out'] = '00002';
Ext.sfa.params['Plan_Execute'] = '00WG2';
Ext.sfa.params['Product_count'] = '000Q2';
Ext.sfa.params['Product_Survey'] = '000Q2';
Ext.sfa.params['Top_Customer'] = '000Q2';
Ext.sfa.params['Report'] = '000Q2';
Ext.sfa.params['Storage_Report'] = '000Q2';
Ext.sfa.params['Report_3'] = '00000';
Ext.sfa.params['NamedProduct'] = '01002';
Ext.sfa.params['KPI'] = '00002';
Ext.sfa.params['Lease_User_Detail'] = '000S0';
Ext.sfa.params['Storage_Out_Report'] = '000Q0';
Ext.sfa.params['Filter_Customer'] = '000Q2';
Ext.sfa.params['report_user_total_plan'] = '000Q2';
Ext.sfa.params['report_product_total_plan'] = '000Q2';
Ext.sfa.params['report_customer_total_plan'] = '000Q2';
Ext.sfa.params['report_user_total_plan_1'] = '000Q2';
Ext.sfa.params['report_product_total_plan_1'] = '000Q2';
Ext.sfa.params['Promotion_report'] = '000Q2';
Ext.sfa.params['Survey_Report'] = '000Q2';
Ext.sfa.params['Monthly_Salary'] = '000Q2';
Ext.sfa.params['Bosgo_Report'] = '000Q2';
Ext.sfa.params['Bosgo_Report_Orders'] = '000Q2';
Ext.sfa.params['Daily_Report'] = '000Q2';
Ext.sfa.params['Order_Daily_Report'] = '000Q2';
Ext.sfa.params['Merchan_Report'] = '000Q2';
Ext.sfa.params['Merchan_Report_Customer'] = '000Q2';
Ext.sfa.params['SR_Report'] = '000W2';
Ext.sfa.params['Bosgo_Report_Orders'] = '000Q2';

Ext.sfa.params['Top_Customer_Drill'] = '000Q2';
Ext.sfa.params['Rought_Report'] = '000Q2';
Ext.sfa.params['Sales_Group'] = 'ticketID';
Ext.sfa.params['SR_Report_Group'] = 'brand';
Ext.sfa.params['Lease_Table_Group'] = 'userCode';
Ext.sfa.params['Lease_Customer_Group'] = 'discount';
Ext.sfa.params['Storage_q_Group'] = '_date';
Ext.sfa.params['Promotion_Accept_Group'] = 'userCode';
Ext.sfa.params['Plan_Exectue_Group'] = 'eventID';
Ext.sfa.params['Top_Customer_Drill'] = '000Q2';

Ext.sfa.params['Sales_Group'] = 'ticketID';
Ext.sfa.params['Lease_Table_Group'] = 'userCode';
Ext.sfa.params['Lease_Customer_Group'] = 'discount';
Ext.sfa.params['Storage_q_Group'] = '_date';
Ext.sfa.params['Promotion_Accept_Group'] = 'userCode';
Ext.sfa.params['Plan_Exectue_Group'] = 'eventID';

Ext.sfa.moduleview['sale-module-win'] = 'images-view1';
Ext.sfa.moduleview['lease-module-win'] = 'images-view2';
Ext.sfa.moduleview['storage-module-win'] = 'images-view3';
Ext.sfa.moduleview['manual-module-win'] = 'images-view4';
Ext.sfa.moduleview['report-module-win'] = 'images-view5';
Ext.sfa.moduleview['special-work-win'] = 'images-view6';
Ext.sfa.moduleview['module-win'] = 'images-view7';
Ext.sfa.moduleview['galaxy-module-win'] = 'images-view8';
Ext.sfa.moduleview['manual-module-win'] = 'images-view9';

Ext.sfa.genKeys = [{type:'0', key1:'routeID', value:'routeName'},
                   {type:'0', key1:'id', value:'name'}, 
                   {type:'0', key1:'brand', value:'brand'}, 
                   {type:'0', key1:'code', value:'name'}, 
                   {type:'1', key1:'productCode', key2:'customerType', value:'price'}, 
                 //  {type:'0', key1:'productCode', value:'userCode'}, 
                   {type:'0', key1:'code', value:'firstName'},                  
                   {type:'0', key1:'_group', value:'descr'}, 
                   {type:'0', key1:'standID', value:'standName'}, 
                   {type:'0', key1:'wareHouseID', value:'name'}, 
                   {type:'0', key1:'price', value:'price'}, 
                   {type:'0', key1:'code', value:'name'}, 
                   {type:'0', key1:'name', value:'name'}, 
                   {type:'0', key1:'groupName', value:'groupName'}];

var maxLoad = 15;//Ext.sfa.genKeys.length;

Ext.sfa.genFun = [['Route', 'route_list', 'renderRouteID', 'routeID,routeName', 's,s', ' '],
                  ['Parent_Names', 'parent_list', 'renderParentID','id,name', 'i,s', ' ORDER by id'],
                  ['Product', 'brand_list', 'renderBrandName', 'brand', 's', ' GROUP by brand'],
                  ['Product', 'product_list', 'renderProductCode', 'code,name,descr,vendor,brand,unit', 's,s,s,s,s,i', " WHERE isSale=1 ORDER by class"],
                  ['Price', 'price_list', 'renderPriceList', 'productCode,customerType,price', 's,i,i', ' '],
                  ['Users', 'user_list', 'renderUserCode','code,firstName,lastName,_group,_position,section,secCode', 's,s,s,i,i,s,s', " WHERE _group!15 and _position!5"],                  
                  ['User_Type', 'user_type', 'renderUserType', '_group,descr,price_tag', 'i,s,i', ' ORDER by _group'],
                  ['Stand', 'stand_list', 'renderStands', 'standID,standName', 'i,s', ' '],
                  ['Ware_House', 'ware_house', 'renderWareHouseID', 'wareHouseID,name', 'i,s', ' '],
                  ['Price', 'price_counts', 'renderPriceCount', 'price', 'i', ' GROUP by price ORDER by price'],		
                  ['Packet', 'packet_name', 'renderPacketCode', 'code,name', 's,s', ' GROUP by code,name'],
                  ['B_PLan', 'plan_name', 'renderPlanName', 'name', 's', ' GROUP by name'],
                  ['Promotion', 'promo_group_name_list', 'renderPromoGroupName', 'groupName', 's', " WHERE section='"+mode+"' GROUP by groupName"]];


//additional
Ext.sfa.stores['section'] = Ext.create('Ext.data.ArrayStore', {
	   fields: ['id', 'section', 'descr'],
	   data : [[0, 'vitafit', 'vitafit']]
});

Ext.sfa.stores['report_view'] = Ext.create('Ext.data.ArrayStore', {
	   fields: ['view', 'descr'],
	   data : [['amount', 'Үнийн дүнгээр'],
	           ['count', 'Тоо ширхэгээр']]
});		


staticInterface = function() {
	//if (Ext.sfa.staticModels['Plan'])
		//return;    		    	
		    		
	//plan
	var model = [];
	
	var fields = [{name: 'productCode', type: 'string'}];           		
	
    var count = 1;        
	for (i = 0; i < Ext.sfa.stores['section'].getCount(); i++) {
		var rec = Ext.sfa.stores['section'].getAt(i);
		var price = 'price' + rec.get('id');
		var countTheshold = 'countTheshold' + rec.get('id');
		var amountTheshold = 'amountTheshold' + rec.get('id');
		fields[count] = {name : price, type: 'int'};
		fields[count+1] = {name : countTheshold, type: 'int'};
		fields[count+2] = {name : amountTheshold, type: 'int'};
		count+=3;
	}
	
	Ext.regModel('plan', {	        
        fields: fields 
    });
	
	var columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'productCode', header: Ext.sfa.translate_arrays[langid][345], width:180, renderer: Ext.sfa.renderer_arrays['renderProductCode']};			    	
	
	count = 2;	
	for (i = 0; i < Ext.sfa.stores['section'].getCount(); i++) {
		var rec = Ext.sfa.stores['section'].getAt(i);
		if (rec.get('section') == mode) { 
			var price = 'price' + rec.get('id');
			var countTheshold = 'countTheshold' + rec.get('id');
			var amountTheshold = 'amountTheshold' + rec.get('id');			
			columns[count] = 
             	{
             		text: rec.get('descr'),
             		columns : [
						{dataIndex: price, header: Ext.sfa.translate_arrays[langid][392], width:70, align: 'right'},
						{dataIndex: countTheshold, header: Ext.sfa.translate_arrays[langid][347], width:100, align: 'right', summaryType: 'sum', renderer: Ext.sfa.renderer_arrays['renderNumber'], summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber']},
						{dataIndex: amountTheshold, header: Ext.sfa.translate_arrays[langid][447], width:160, align: 'right', summaryType: 'sum', renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney']}
             		]
             	};
			count++;
		}
	}
	
	model['fields'] = ' ';
	model['types'] = ' ';
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
        model: 'plan',	        
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'plan',	        
        proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: {
	           type: 'json'
	        }
		}
    });
	
	Ext.sfa.staticModels['Plan'] = model;				  	
	// product survey
	
	model = [];
	fields = [{name: 'customerCode', type: 'string', dateIndex: 'customerCode', title: Ext.sfa.translate_arrays[langid][441], width:120, renderer: Ext.sfa.renderer_arrays['renderCustomerCode']}];
	
	count = 1;        
	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
		var rec = Ext.sfa.stores['product_list'].getAt(i);		
		fields[count] = {name : rec.get('code'), type: 'int', title: rec.get('descr'), dataIndex: rec.get('code'), width: 50, summaryType:'sum', align: 'center', renderer: Ext.sfa.renderer_arrays['renderSurvey']};		
		count++;
	}
	var columns1 = [];
	columns1[0] = new Ext.grid.RowNumberer({width:30});
	columns1[1] = {dataIndex: 'customerCode', header: Ext.sfa.translate_arrays[langid][441], width: 150, renderer:Ext.sfa.renderer_arrays['renderCustomerCode']};			    	
	       	
   	count = 2;	
   	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
   		var rec = Ext.sfa.stores['product_list'].getAt(i);		
			columns1[count] = {dataIndex: rec.get('code'), header: rec.get('descr'), width:48, align: 'center', renderer: Ext.sfa.renderer_arrays['renderSurvey'], summaryType: 'sum'};
   		count++;
   	}
	       		
	model['columns'] = columns1;
	
	Ext.regModel('product_survey', {	        
	     fields: fields
	});
	 
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
       model: 'product_survey',       
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
	
   Ext.sfa.staticModels['Product_Survey'] = model;
   
   // Xariltsagchiin idevh
   
   model = [];
	
   fields = [{name: 'userCode', type: 'string', width:120, renderer:Ext.sfa.renderer_arrays['renderUserCode']}];           		
   var tt = [Ext.sfa.translate_arrays[langid][607],Ext.sfa.translate_arrays[langid][608],Ext.sfa.translate_arrays[langid][609]];
   var pt = [14,25,36];
   
   var count = 1;        
	for (i = 0; i < tt.length; i++) {		
		var total = 'total'+pt[i];
		var active = 'active'+pt[i];
		var not_active = 'not_active'+pt[i];		
		fields[count] = {name : total, type: 'int', width:60};
		fields[count+1] = {name : active, type: 'int', width:60};
		fields[count+2] = {name : not_active, type: 'int', width:60};
		count+=3;
	}
	
	fields[count] = {name : 'sum', type: 'int', width:60};
	fields[count+1] = {name : 'asum', type: 'int', width:60};
	fields[count+2] = {name : 'no_sum', type: 'int', width:60};
	count+=3;
	
	Ext.regModel('report', {
       fields: fields 
    });
	
	var columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'userCode', header: Ext.sfa.translate_arrays[langid][310], width: 100, renderer:Ext.sfa.renderer_arrays['renderAllUserCode']};    	
	
	count = 2;	
	for (i = 0; i < tt.length; i++) {
		var total = 'total'+pt[i];
		var active = 'active'+pt[i];
		var not_active = 'not_active'+pt[i];	
			columns[count] = 
            	{
            		header: tt[i],            		
            		columns : [
						{dataIndex: total, header: Ext.sfa.translate_arrays[langid][439], width:60, align: 'right', summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'], sortable : true},
						{dataIndex: active, header: Ext.sfa.translate_arrays[langid][376], width:60, align: 'right', summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true},
						{dataIndex: not_active, header: Ext.sfa.translate_arrays[langid][377], width:60, align: 'right', summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true}
            		]
            	};    		    
		count++;
	}
	
	columns[count] = 
      	{
      		header: Ext.sfa.translate_arrays[langid][439],
      		width: 180,
      		columns : [
				{dataIndex: 'sum', header: Ext.sfa.translate_arrays[langid][439], width:60, align: 'right', summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true},
				{dataIndex: 'asum', header: Ext.sfa.translate_arrays[langid][376], width:60, align: 'right', summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true},
				{dataIndex: 'no_sum', header: Ext.sfa.translate_arrays[langid][377], width:60, align: 'right', summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true}
      		]
      	};        	  		
	
	model['fields'] = ' ';
	model['types'] = ' ';
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
       model: 'report',	        
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'report',	        
       proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: {
	           type: 'json'
	        }
		}
   });
	
	Ext.sfa.staticModels['Report'] = model;
	
	
	
   // Storage Report
	
	model = [];
	
	fields = [];
    fields[0] = {name: 'userCode', type: 'string', width:120, renderer:Ext.sfa.renderer_arrays['renderUserCode']};        
    
    var count = 1;        
    for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
		var record = Ext.sfa.stores['product_list'].getAt(i);		
		fields[count] = {name: record.data['code']+'_in', type: 'int'};
		fields[count+1] = {name: record.data['code']+'_out', type: 'int'};
		count+=2;		
	}
 	     
 	Ext.regModel('storage_report', {
        fields: fields 
     });
 	
 	columns = [];
 	columns[0] = new Ext.grid.RowNumberer();
 	columns[1] = {dataIndex: 'userCode', header: Ext.sfa.translate_arrays[langid][310], width: 120, renderer:Ext.sfa.renderer_arrays['renderAllUserCode']};			     
 	
 	count = 2;	
 	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
		var record = Ext.sfa.stores['product_list'].getAt(i);		
		columns[count] =
         	{
         		header: record.data['name'],
         		width: 150,
         		columns : [
         		  {dataIndex: record.data['code']+'_in', header: 'Орлого', width:75, align: 'right', renderer: Ext.sfa.renderer_arrays['renderNumber'], summaryType: 'last', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true},
         		  {dataIndex: record.data['code']+'_out', header: 'Зарлага', width:75, align: 'right', renderer: Ext.sfa.renderer_arrays['renderNumber'], summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true}             		  
         		]
        	};
		
		count++;		
	}
 	     	
 	model['fields'] = ' ';
 	model['types'] = ' ';
 	model['columns'] = columns;	
 	model['rowEditor'] = [];
 	
 	model['readStore'] = Ext.create('Ext.data.JsonStore', {
        model: 'storage_report',	   
        sortInfo: { field: 'userCode', direction: 'ASC'},
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
 	
 	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
 		model: 'storage_report',	        
        proxy: {
 			type: 'ajax',
 			url: 'httpGW',
 			writer: {
 	           type: 'json'
 	        }
 		}
    });
 	
 	Ext.sfa.staticModels['Storage_Report'] = model;    


	 // Product Count Report
	
	model = [];
	
	fields = [];
	fields[0] = {name: 'customerCode', type: 'string', width:180, renderer:Ext.sfa.renderer_arrays['renderCustomerCode']};        
	
	var count = 1;        
	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
		var record = Ext.sfa.stores['product_list'].getAt(i);		
		if (productAble(record)) {
			fields[count] = {name: record.data['code']+'_q', type: 'int'};
			fields[count+1] = {name: record.data['code']+'_a', type: 'int'};
			count+=2;
		}
	}
	     
	Ext.regModel('product_count_report', {
	    fields: fields 
	 });
	
	columns = [];
	columns[0] = new Ext.grid.RowNumberer();
	columns[1] = {dataIndex: 'customerCode', header: Ext.sfa.translate_arrays[langid][441], width: 180, renderer:Ext.sfa.renderer_arrays['renderCustomerCode']};			     
	
	count = 2;	
	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
		var record = Ext.sfa.stores['product_list'].getAt(i);		
		if (productAble(record)) {
			columns[count] =
		     	{
		     		header: record.data['descr'],
		     		width: 120,
		     		columns : [
		     		  {dataIndex: record.data['code']+'_q', header: 'Тоо', width:60, align: 'right', renderer: Ext.sfa.renderer_arrays['renderNumber'], summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true},
		     		  {dataIndex: record.data['code']+'_a', header: 'Үнэ', width:60, align: 'right', renderer: Ext.sfa.renderer_arrays['renderNumber'], summaryType: 'avgif', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true}             		  
		     		]
		    	};
			
			count++;
		}
	}
	     	
	model['fields'] = ' ';
	model['types'] = ' ';
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
	    model: 'product_count_report',	   
	    sortInfo: { field: 'userCode', direction: 'ASC'},
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'product_count_report',	        
	    proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: {
	           type: 'json'
	        }
		}
	});
	
	Ext.sfa.staticModels['Product_count'] = model;
	
	
	// Top Customer Report
	model = [];
	
	fields = [];
	fields[0] = {name: 'customerCode', type: 'string'};        
	fields[1] = {name: 'entry', type: 'int'};
	fields[2] = {name: 'itemID', type: 'int'};
	
	var count = 3;        
	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
		var record = Ext.sfa.stores['product_list'].getAt(i);		
		if (productAble(record)) {
			fields[count] = {name: record.data['code'], type: 'int'};
			count+=1;
		}
	}
	
	fields[count] = {name: 'sum_p', type: 'int'};
	fields[count+1] = {name: 'sum_r', type: 'int'};	
	fields[count+2] = {name: 'sum_ar', type: 'int'};
	fields[count+3] = {name: 'sum_a', type: 'int'};
	
	     
	Ext.regModel('top_customer_report', {
	    fields: fields 
	 });
	
	columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'customerCode', type: 'string', header: Ext.sfa.translate_arrays[langid][441], width: 180, summaryType:'count', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'], renderer:Ext.sfa.renderer_arrays['renderCustomerCode']};
	columns[2] = {dataIndex: 'entry',  type: 'int', header: 'Орсон тоо', width: 60, summaryType:'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'], renderer:Ext.sfa.renderer_arrays['renderNumber'], align: 'right'};
	columns[3] = {dataIndex: 'itemID', type: 'string', summaryType: 'sum', renderer: Ext.sfa.renderer_arrays['renderItemAble'], summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'], header: Ext.sfa.translate_arrays[langid][668], width: 80};
	count = 4;	
	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
		var record = Ext.sfa.stores['product_list'].getAt(i);
		if (productAble(record)) {
			columns[count] =  {header: record.data['name'],  
								dataIndex: record.data['code'],  type: 'int', summaryType: 'sum', width: 80, align: 'center', renderer: Ext.sfa.renderer_arrays['renderNumber'], summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber']};
			count+=1;
		}		
	}
	
	
	columns[count] = {dataIndex: 'sum_p', type: 'int',  type: 'int', header: Ext.sfa.translate_arrays[langid][658], width: 100, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], summaryType: 'sum', align: 'right'};
	columns[count+1] = {dataIndex: 'sum_r', type: 'int',  type: 'int', header: Ext.sfa.translate_arrays[langid][311], width: 100, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], summaryType: 'sum', align: 'right'};	
	columns[count+2] = {dataIndex: 'sum_ar', type: 'int', type: 'int',  header: Ext.sfa.translate_arrays[langid][312], width: 100, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], align: 'right', summaryType: 'sum'};
	columns[count+3] = {dataIndex: 'sum_a', type: 'int',  type: 'int', header: Ext.sfa.translate_arrays[langid][313], width: 100, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], align: 'right', summaryType: 'sum'};    	    	    	     	
	     	
	model['fields'] = ' ';
	model['types'] = ' ';
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
	    model: 'top_customer_report',	   
	    sortInfo: { field: 'customerCode', direction: 'ASC'},
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
	
	Ext.sfa.staticModels['Top_Customer'] = model;
	
	
	//Top Customer Drill
	model = [];
	
	fields = [];
	fields[0] = {name: 'data', type: 'string'};        
	
	var count = 1;        
	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
		var record = Ext.sfa.stores['product_list'].getAt(i);		
		if (productAble(record)) {
			fields[count] = {name: record.data['code'], type: 'int'};
			count+=1;
		}
	}
	
	fields[count] = {name: 'sum_p', type: 'int'};
	fields[count+1] = {name: 'sum_r', type: 'int'};	
	fields[count+2] = {name: 'sum_ar', type: 'int'};
	fields[count+3] = {name: 'sum_a', type: 'int'};
	
	     
	Ext.regModel('top_customer_report_drill', {
	    fields: fields 
	 });
	
	columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'data', header: Ext.sfa.translate_arrays[langid][341], width: 80};
	count = 2;	
	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
		var record = Ext.sfa.stores['product_list'].getAt(i);
		if (productAble(record)) {
			columns[count] =  {header: record.data['name'],  
								dataIndex: record.data['code'], summaryType: 'sum', width: 80, align: 'center', renderer: Ext.sfa.renderer_arrays['renderNumber'], summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber']};
			count+=1;
		}		
	}
	
	columns[count] = {dataIndex: 'sum_p', type: 'int', header: Ext.sfa.translate_arrays[langid][658], width: 100, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], summaryType: 'sum', align: 'right'};
	columns[count+1] = {dataIndex: 'sum_r', type: 'int', header: Ext.sfa.translate_arrays[langid][311], width: 100, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], summaryType: 'sum', align: 'right'};	
	columns[count+2] = {dataIndex: 'sum_ar', type: 'int', header: Ext.sfa.translate_arrays[langid][312], width: 100, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], align: 'right', summaryType: 'sum'};
	columns[count+3] = {dataIndex: 'sum_a', type: 'int', header: Ext.sfa.translate_arrays[langid][313], width: 100, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], align: 'right', summaryType: 'sum'};    	    	    	     	
	     	
	model['fields'] = ' ';
	model['types'] = ' ';
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
	    model: 'top_customer_report_drill',	   
	    sortInfo: { field: 'data', direction: 'ASC'},
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
	
	
	Ext.sfa.staticModels['Top_Customer_Drill'] = model;
	
	//Filter_Customer
	model = [];
	
	fields = [];
	fields[0] = {name: 'customerCode', type: 'string'};        		
	var count = 1;        				
	fields[count] = {name: 'belong', type: 'int'};
	fields[count+1] = {name: 'sum_ar', type: 'int'};
	fields[count+2] = {name: 'sum_a', type: 'int'};    	    
	fields[count+3] = {name: 'entry', type: 'int'};
	fields[count+4] = {name: 'avgsale', type: 'int'};    	
	fields[count+5] = {name: 'routeID', type: 'string'};
	fields[count+6] = {name: 'loanMargin', type: 'int'};
	fields[count+7] = {name: 'promoAmount', type: 'int'};    	
	fields[count+8] = {name: 'itemID', type: 'string'};
	
	     
	Ext.regModel('filter_customer_report', {
	    fields: fields 
	 });
	
	columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:40});
	columns[1] = {dataIndex: 'customerCode', type: 'string', header: Ext.sfa.translate_arrays[langid][441], width: 200, summaryType:'count', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'], renderer:Ext.sfa.renderer_arrays['renderCustomerCode']};
	columns[2] = {dataIndex: 'entry', type: 'int', header: 'Худалдаа хийсэн тоо', width: 60, summaryType:'average', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'], renderer:Ext.sfa.renderer_arrays['renderNumber'], align: 'right'};
	columns[3] = {dataIndex: 'itemID', type: 'string', header: Ext.sfa.translate_arrays[langid][668], width: 80, renderer:Ext.sfa.renderer_arrays['renderRouteID']};
	columns[4] = {dataIndex: 'promoAmount', type: 'float', header: 'Урамшуулал', width: 90, align: 'right', summaryType:'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], renderer:Ext.sfa.renderer_arrays['renderMoney']};		
	columns[5] = {dataIndex: 'avgsale', type: 'float', header: 'Дундаж худалдан авалт', width: 90, summaryType:'average', summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], renderer:Ext.sfa.renderer_arrays['renderMoney'], align: 'right'};				
	count = 6;		
	columns[count] = {dataIndex: 'loanMargin', type: 'float', header: 'Зээлийн дээд лимит', width: 90, renderer: Ext.sfa.renderer_arrays['renderMoney'], align: 'right'};
	columns[count+1] = {dataIndex: 'belong', type: 'int',header: 'Өөрийн өмч эсэх', width: 70, renderer: Ext.sfa.renderer_arrays['renderYesNo'],  align: 'right'};
	columns[count+2] = {dataIndex: 'sum_ar', type: 'float',header: 'Зээлээр худалдаа хийсэн дүн', width: 110, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], align: 'right', summaryType: 'sum'};
	columns[count+3] = {dataIndex: 'sum_a', type: 'float', header: 'Худалдан авалтын дүн', width: 110, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], align: 'right', summaryType: 'sum'};
	    	
	     	
	model['fields'] = ' ';
	model['types'] = ' ';
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
	    model: 'filter_customer_report',	   
	    sortInfo: { field: 'customerCode', direction: 'ASC'},
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'filter_customer_report',	        
	    proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: {
	           type: 'json'
	        }
		}
	});
	
	Ext.sfa.staticModels['Filter_Customer'] = model;
	
	
	// Lease Main Report
	
	model = [];
	
	fields = [];
	fields[0] = {name: 'day', type: 'string', width:120};        
	
	var count = 1;        
	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
		var record = Ext.sfa.stores['product_list'].getAt(i);			
		var amount = record.data['code'];
		fields[count] = {name: amount, type: 'int'};			
		count+=1;
	}
	
	fields[count] = {name: 'sum_a', type: 'int'};	
	fields[count+1] = {name: 'sum_r', type: 'int'};   
	fields[count+2] = {name: 'sum_l', type: 'int'};
	fields[count+3] = {name: 'data', type: 'string'};
	     
	Ext.regModel('lease_report', {
	    fields: fields 
	 });
	
	columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'day', type: 'string', header: ' ', width: 140};			     
	
	count = 2;	
	for (i = 0; i < Ext.sfa.stores['product_list'].getCount(); i++) {
		var record = Ext.sfa.stores['product_list'].getAt(i);	
		if (productAble(record)) {
			columns[count] =  {name: record.data['code'], type: 'int', header: record.data['name'], hidden: product_visible[record.data['code']], 
							   dataIndex: record.data['code'], summaryType: 'sum', width: 80, align: 'center', renderer: renderZeel, summaryType: 'sum', summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber']};			
		
			count++;
		}
	}
	
	columns[count] = {dataIndex: 'sum_a', type: 'int', header: Ext.sfa.translate_arrays[langid][312], width: 120, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], summaryType: 'sum', align: 'right'};	
	columns[count+1] = {dataIndex: 'sum_r', type: 'int', header: Ext.sfa.translate_arrays[langid][311], width: 120, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], align: 'right', summaryType: 'sum'};
	columns[count+2] = {dataIndex: 'sum_l', type: 'int', header: Ext.sfa.translate_arrays[langid][444], width: 120, renderer: Ext.sfa.renderer_arrays['renderMoney'], summaryRenderer: Ext.sfa.renderer_arrays['renderTMoney'], align: 'right', summaryType: 'last'};
	columns[count+3] = {dataIndex: 'data', type: 'string', header: ' ', hidden:true};
	
	model['fields'] = ' ';
	model['types'] = ' ';
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
	    model: 'lease_report',	   
	    sortInfo: { field: 'customerCode', direction: 'ASC'},
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'lease_report',	        
	    proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: {
	           type: 'json'
	        }
		}
	});
	
	Ext.sfa.staticModels['Lease_Main'] = model;		
	Ext.sfa.staticModels['Lease_Customer_Detail'] = model;		
   // User route entry
       
   model = [];
	
   fields = [{name: 'userCode', type: 'string', width:120, renderer:Ext.sfa.renderer_arrays['renderUserCode']}];           		       
   var pt = [];
   var qt = [];
   for (i = 1; i<=31; i++) { pt[i-1] = i; qt[i-1] = i;}
   
   var title = '', types = 's,', flds = 'userCode,';
   var count = 1;        
	for (i = 0; i < pt.length; i++) {		
		var entry = 'entry'+qt[i];
		var sale = 'sale'+qt[i];
		var noentry = 'noentry'+qt[i];		
		var total = 'total'+qt[i];
		fields[count] = {name : entry, type: 'int', width:60};
		fields[count+1] = {name : sale, type: 'int', width:60};
		fields[count+2] = {name : noentry, type: 'int', width:60};
		fields[count+3] = {name : total, type: 'int', width:60};
		count+=4;
		flds += entry+','+sale+','+noentry+','+total+',';
		types += 'i,i,i,'
	}
	
	fields[count] = {name : 'total_1', type: 'int', width:60};
	fields[count+1] = {name : 'total_2', type: 'int', width:60};
	fields[count+2] = {name : 'total_3', type: 'int', width:60};
	flds += 'total_1,total_2,total_3';
	types += 'i,i,i';
	
	Ext.regModel('report', {
       fields: fields 
    });
	
	var columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'userCode', header: Ext.sfa.translate_arrays[langid][310], width: 100, renderer:Ext.sfa.renderer_arrays['renderAllUserCode']};    	
	
	count = 2;
	for (i = 0; i < pt.length; i++) {
		var entry = 'entry'+qt[i];
		var sale = 'sale'+qt[i];
		var noentry = 'noentry'+qt[i];		
		var total = 'total'+qt[i];	
			columns[count] = 
            	{
            		header: pt[i]+'-н',            		
            		columns : [
						{dataIndex: entry, header: '+', width:45, align: 'center', summaryType: 'sum', renderer: Ext.sfa.renderer_arrays['renderEntryNumber'], summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'], sortable : true},
						{dataIndex: sale, header: '*', width:45, align: 'center', summaryType: 'sum', renderer: Ext.sfa.renderer_arrays['renderEntryNumber'], summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true},
						{dataIndex: noentry, header: '-', width:45, align: 'center', summaryType: 'sum', renderer: Ext.sfa.renderer_arrays['renderEntryNumber'],summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true},
						{dataIndex: total, header: Ext.sfa.translate_arrays[langid][439], width:50, align: 'center', summaryType: 'sum', renderer: Ext.sfa.renderer_arrays['renderEntryNumber'], summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true}
            		]
            	};    		    
		count++;
	}
	
	columns[count] = {dataIndex: 'total_1', header: Ext.sfa.translate_arrays[langid][342], width:70, align: 'right', summaryType: 'sum', renderer: Ext.sfa.renderer_arrays['renderNumber'], summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true}
	columns[count+1] = {dataIndex: 'total_2', header: Ext.sfa.translate_arrays[langid][343], width:70, align: 'right', summaryType: 'sum', renderer: Ext.sfa.renderer_arrays['renderNumber'], summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true}
	columns[count+2] = {dataIndex: 'total_3', header: Ext.sfa.translate_arrays[langid][439], width:70, align: 'right', summaryType: 'sum', renderer: Ext.sfa.renderer_arrays['renderNumber'], summaryRenderer: Ext.sfa.renderer_arrays['renderTNumber'],  sortable : true}
	
	model['fields'] = flds;
	model['types'] = types;    	
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
       model: 'report',	        
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'report',	        
       proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: {
	           type: 'json'
	        }
		}
   });
	
   Ext.sfa.staticModels['User_Route_Entry'] = model;                           	
	
	
// rought report
	model = [];
	
	fields = [];
	fields[0] = {name: 'userCode', type: 'string'};
	fields[1] = {name: 'lastSale', type: 'string'};
	fields[2] = {name: 'lastSalesTimestamp', type: 'string'};
	fields[3] = {name: 'ZahialgaHoorondiinMinute', type: 'int'};

			    
	Ext.regModel('rought_report', {
	    fields: fields 
	});
	
	columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'userCode', header: Ext.sfa.translate_arrays[langid][310], width: 150, renderer:Ext.sfa.renderer_arrays['renderUserCode']};		     				      	
	columns[2] = {dataIndex: 'lastSale', header: 'Сүүлийн захиалагч', flex: 1,align:'left'};		     				
	columns[3] = {dataIndex: 'lastSalesTimestamp', header: 'Хугацаа', width: 150,align:'right'};		     				
	columns[4] = {dataIndex: 'ZahialgaHoorondiinMinute', header: 'Захиалгын хороондын хугацаа /мин/', width: 100, renderer:Ext.sfa.renderer_arrays['renderNumber'],align:'right'};		     				
	
	
	model['fields'] = ' ';
	model['types'] = ' ';		
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
	    model: 'rought_report',	   		    
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'rought_report',	        
	    proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: {
	           type: 'json'
	        }
		}
	});
	
	Ext.sfa.staticModels['Rought_Report'] = model;
	
	//Order Daily report
	model = [];
	
	fields = [];
	fields[0] = {name: 'title', type: 'string'};
	fields[1] = {name: 'qty', type: 'int'};
	fields[2] = {name: 'packet', type: 'float'};
			    
	Ext.regModel('order_daily_report', {
	    fields: fields 
	});
	
	columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'title', header: 'Төрөл', width: 150};
	columns[2] = {dataIndex: 'qty', header: 'Ширхэг'};
	columns[3] = {dataIndex: 'packet', header: 'Пакет' };
	
	model['fields'] = ' '; 
	model['types'] = ' ';		
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
	    model: 'order_daily_report',	   		    
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'order_daily_report',	        
	    proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: {
	           type: 'json'
	        }
		}
	});
	
	Ext.sfa.staticModels['Order_Daily_Report'] = model;
	
	//SR report
	model = [];
	
	fields = [];
	fields[0] = {name: 'brand', type: 'string'};
	fields[1] = {name: 'code', type: 'string'};
	fields[2] = {name: 'dPlan', type: 'int'};
	fields[3] = {name: 'dGui', type: 'int'};
	fields[4] = {name: 'dPrecent', type: 'float'};
	fields[5] = {name: 'mPlan', type: 'int'};
	fields[6] = {name: 'mGui', type: 'int'};
	fields[7] = {name: 'mPrecent', type: 'float'};

			    
	Ext.regModel('sr_report', {
	    fields: fields 
	});
	
	columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'brand', header: 'Brand', width: 50};
	columns[2] = {dataIndex: 'code', header: Ext.sfa.translate_arrays[langid][345], width: 150, renderer:Ext.sfa.renderer_arrays['renderProductCode']};
	columns[3] = {header: 'Өдрийн',
		columns:[				
			{dataIndex: 'dPlan', header: 'Төлөвлөгөө', width: 150, renderer:Ext.sfa.renderer_arrays['renderNumber'],summaryType:'sum',summaryRenderer:Ext.sfa.renderer_arrays['renderTPrecent'],align:'right'},
			{dataIndex: 'dGui', header: 'Гүйцэтгэл', width: 150, renderer:Ext.sfa.renderer_arrays['renderNumber'],summaryType:'sum',summaryRenderer:Ext.sfa.renderer_arrays['renderTPrecent'],align:'right'},
			{dataIndex: 'dPrecent', header: 'Хувь', width: 50, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'average',summaryRenderer:Ext.sfa.renderer_arrays['renderTPrecent'],align:'right'}				
		]};
	
	columns[4] = {header: 'Сарын',
		columns:[				
			{dataIndex: 'mPlan', header: 'Төлөвлөгөө', width: 150, renderer:Ext.sfa.renderer_arrays['renderNumber'],summaryType:'sum',summaryRenderer:Ext.sfa.renderer_arrays['renderTPrecent'],align:'right'},
			{dataIndex: 'mGui', header: 'Гүйцэтгэл', width: 150, renderer:Ext.sfa.renderer_arrays['renderNumber'],summaryType:'sum',summaryRenderer:Ext.sfa.renderer_arrays['renderTPrecent'],align:'right'},
			{dataIndex: 'mPrecent', header: 'Хувь', width: 50, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'average',summaryRenderer:Ext.sfa.renderer_arrays['renderTPrecent'],align:'right'}
		]};


	

	model['fields'] = ' ';
	model['types'] = ' ';		
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
	    model: 'sr_report',	 
	    groupField:'brand',
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'sr_report',	        
	    proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: {
	           type: 'json'
	        }
		}
	});
	
	Ext.sfa.staticModels['SR_Report'] = model;
	//Daily report
	model = [];
	
	fields = [];
	fields[0] = {name: 'userCode', type: 'string'};
	fields[1] = {name: 'svPlan', type: 'int'};
	fields[2] = {name: 'sActual', type: 'int'};
	fields[3] = {name: 'precent', type: 'int'};
	fields[4] = {name: 'outletsPlan', type: 'int'};
	fields[5] = {name: 'visited', type: 'int'};
	fields[6] = {name: 'precentVisit', type: 'int'};
	fields[7] = {name: 'outletsBuying', type: 'int'};		
	fields[8] = {name: 'buyingPrecent', type: 'int'};
	fields[9] = {name: 'planADSize', type: 'int'};
	fields[10] = {name: 'actualADSize', type: 'int'};
	fields[11] = {name: 'actualPrecent', type: 'int'};
	fields[12] = {name: 'pepsiProduct', type: 'int'};
	fields[13] = {name: 'vitafitProduct', type: 'int'};
	fields[14] = {name: 'namedProduct', type: 'int'};
			    
	Ext.regModel('daily_report', {
	    fields: fields 
	});
	
	columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'userCode', header: Ext.sfa.translate_arrays[langid][310], width: 150, renderer:Ext.sfa.renderer_arrays['renderUserCode']};
	columns[2] = {dataIndex: 'svPlan', header: 'svPlan', width: 70, renderer:Ext.sfa.renderer_arrays['renderNumber'],align:'right',summaryType:'sum'};
	columns[3] = {dataIndex: 'sActual', header: 'sActual', width: 70, renderer:Ext.sfa.renderer_arrays['renderNumber'],align:'right',summaryType:'sum'};
	columns[4] = {dataIndex: 'precent', header: 'precent', width: 70, renderer:Ext.sfa.renderer_arrays['renderPrecent'],align:'right',summaryType:'sum'};
	columns[5] = {dataIndex: 'outletsPlan', header: 'outletsPlan', width: 70, renderer:Ext.sfa.renderer_arrays['renderNumber'],align:'right',summaryType:'sum'};
	columns[6] = {dataIndex: 'visited', header: 'visited', width: 70, renderer:Ext.sfa.renderer_arrays['renderNumber'],align:'right',summaryType:'sum'};
	columns[7] = {dataIndex: 'precentVisit', header: 'precentVisit', width: 70, renderer:Ext.sfa.renderer_arrays['renderPrecent'],align:'right',summaryType:'sum'};
	columns[8] = {dataIndex: 'outletsBuying', header: 'outletsBuying', width: 70, renderer:Ext.sfa.renderer_arrays['renderNumber'],align:'right',summaryType:'sum'};
	columns[9] = {dataIndex: 'buyingPrecent', header: 'buyingPrecent', width: 70, renderer:Ext.sfa.renderer_arrays['renderPrecent'],align:'right',summaryType:'sum'};
	columns[10] = {dataIndex: 'planADSize', header: 'planADSize', width: 70, renderer:Ext.sfa.renderer_arrays['renderNumber'],align:'right',summaryType:'sum'};
	columns[11] = {dataIndex: 'actualADSize', header: 'actualADSize', width: 70, renderer:Ext.sfa.renderer_arrays['renderNumber'],align:'right',summaryType:'sum'};
	columns[12] = {dataIndex: 'actualPrecent', header: 'actualPrecent', width: 70, renderer:Ext.sfa.renderer_arrays['renderPrecent'],align:'right',summaryType:'sum'};
	columns[13] = {dataIndex: 'pepsiProduct', header: 'pepsiProduct', width: 70, renderer:Ext.sfa.renderer_arrays['renderNumber'],align:'right',summaryType:'sum'};
	columns[14] = {dataIndex: 'vitafitProduct', header: 'vitafitProduct', width: 70, renderer:Ext.sfa.renderer_arrays['renderNumber'],align:'right',summaryType:'sum'};
	columns[15] = {dataIndex: 'namedProduct', header: 'namedProduct', width: 70, renderer:Ext.sfa.renderer_arrays['renderNumber'],align:'right',summaryType:'sum'};

	model['fields'] = ' '; 
	model['types'] = ' ';		
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
	    model: 'daily_report',	   		    
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'daily_report',	        
	    proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: {
	           type: 'json'
	        }
		}
	});
	
	Ext.sfa.staticModels['Daily_Report'] = model;
	
	//merchan report
	model = [];
	
	fields = [];
	fields[0] = {name: 'userCode', type: 'string'};
	fields[1] = {name: 'zone', type: 'string'};
	fields[2] = {name: 'position', type: 'float'};
	fields[3] = {name: 'price', type: 'int'};
	fields[4] = {name: 'sku', type: 'float'};
	fields[5] = {name: 'vitafitSOS', type: 'float'};
	fields[6] = {name: 'pepsiSOS', type: 'float'};
	fields[7] = {name: 'posm', type: 'float'};
	fields[8] = {name: 'coolerUses', type: 'float'};
	fields[9] = {name: 'total', type: 'float'};		

			    
	Ext.regModel('merchan_report', {
	    fields: fields 
	});
	
	columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'userCode', header: Ext.sfa.translate_arrays[langid][310], width: 150, renderer:Ext.sfa.renderer_arrays['renderUserCode']};
	columns[2] = {dataIndex: 'zone', header: 'zone', width: 100};
	columns[3] = {dataIndex: 'position', header: 'position', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[4] = {dataIndex: 'price', header: 'price', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[5] = {dataIndex: 'sku', header: 'sku', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[6] = {dataIndex: 'vitafitSOS', header: 'vitafit', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[7] = {dataIndex: 'pepsiSOS', header: 'pepsi', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[8] = {dataIndex: 'posm', header: 'posm', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[9] = {dataIndex: 'coolerUses', header: 'coolerUses', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[10] = {dataIndex: 'total', header: 'total', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};


	model['fields'] = ' ';
	model['types'] = ' ';		
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
	    model: 'merchan_report',	   		    
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'merchan_report',	        
	    proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: { 
	           type: 'json'
	        }
		}
	});
	
	Ext.sfa.staticModels['Merchan_Report'] = model;
	
	//merchan report with customerCode
	model = [];
	
	fields = [];
	fields[0] = {name: 'customerCode', type: 'string'};
	fields[1] = {name: 'position', type: 'float'};
	fields[2] = {name: 'price', type: 'int'};
	fields[3] = {name: 'sku', type: 'float'};
	fields[4] = {name: 'vitafitSOS', type: 'float'};
	fields[5] = {name: 'pepsiSOS', type: 'float'};
	fields[6] = {name: 'posm', type: 'float'};
	fields[7] = {name: 'coolerUses', type: 'float'};
	fields[8] = {name: 'total', type: 'float'};
	fields[9] = {name: 'picture', type: 'string'};		

			     
	Ext.regModel('merchan_report_customer', {
	    fields: fields 
	});
	
	columns = [];
	columns[0] = new Ext.grid.RowNumberer({width:30});
	columns[1] = {dataIndex: 'customerCode', header: 'Харилцагч', flex:1, renderer:Ext.sfa.renderer_arrays['renderCustomerCode']};
	columns[2] = {dataIndex: 'position', header: 'position', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[3] = {dataIndex: 'price', header: 'price', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[4] = {dataIndex: 'sku', header: 'sku', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[5] = {dataIndex: 'vitafitSOS', header: 'vitafit', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[6] = {dataIndex: 'pepsiSOS', header: 'pepsi', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[7] = {dataIndex: 'posm', header: 'posm', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[8] = {dataIndex: 'coolerUses', header: 'coolerUses', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[9] = {dataIndex: 'total', header: 'total', width: 60, renderer:Ext.sfa.renderer_arrays['renderPrecent'],summaryType:'sum'};
	columns[10] = {dataIndex: 'picture', header: 'picture', width: 60};


	model['fields'] = ' ';
	model['types'] = ' ';		
	model['columns'] = columns;	
	model['rowEditor'] = [];
	
	model['readStore'] = Ext.create('Ext.data.JsonStore', {
	    model: 'merchan_report_customer',	   		    
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
	
	model['writeStore'] = Ext.create('Ext.data.JsonStore', {
		model: 'merchan_report_customer',	        
	    proxy: {
			type: 'ajax',
			url: 'httpGW',
			writer: {
	           type: 'json'
	        }
		}
	});
	
	Ext.sfa.staticModels['Merchan_Report_Customer'] = model;
	
}

Ext.sfa.renderer_arrays['renderCustomerCode'] = function renderCustomerCode(v) {
	return customer[''+v];
}

Ext.sfa.renderer_arrays['renderCustomerAutoCode'] = function renderCustomerAutoCode(v) {
	if (!customer[v]) return 'auto';
	return customer[''+v];
}