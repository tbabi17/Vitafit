package oss.app;

import java.util.Calendar;
import java.util.Date;

import oss.cache.cacheManager;
import oss.core.coreProcedures;
import oss.report.Collection;
import oss.report.Variant;

public class systemController extends coreProcedures {
		
	public systemController() {
		httpConn = new httpConnection(this);
		mobileConn = new mobileConnection(this);
		reportConn = new reportConnection(this);
		cacheMan = new cacheManager();
	}			 
	  
	public void customAction(Variant w) { 
		String func = w.get("func");
		String action = w.get("action");
		String tableName = w.get("table");
		cacheMan.manageCache(tableName, action);		 
							
		if (func.equals("business_plan")) {	
			action = "insert";
			String values = w.get("types");
			values = values.replaceAll("%20", " ");
			String users = w.get("where");
			String products = w.get("fields");				
			httpConn.actionDataBPlan(action, values, products, users);
		} else
		if (func.equals("business_plan_delete")) {	
			action = "delete";
			String values = w.get("types");
			String users = w.get("where"); 
			String products = w.get("fields");				
			httpConn.actionDataBPlan(action, values, products, users);
		}
	}
	
	//****************** TAILANTAI HOLBOOTOI JSON BUTSAAH FUNCTION ******************************//
	public String reportSelect(Variant w) {
		String func = w.get("func");				
		String where = "", result = "";
		if (func.equals("_report_by_customer")) {
			where = w.get("where");
			String[]wh = where.split(",");															
			result = httpConn.jsonCustomerSalesDetailByUser(wh[0], wh[1], wh[2]);
			cacheMan.saveAlternateCache("Top_Customer", result);
			return result;
		} else
		if (func.equals("_report_by_customer_drill")) {
			where = w.get("where");
			String[]wh = where.split(",");															
			result = httpConn.jsonCustomerSalesDrillDetailByUser(wh[1], wh[2], wh[0]);
			cacheMan.saveAlternateCache("Top_Customer_Drill", result);
			return result;
		} else
		if (func.equals("_report_one")) {						
			result = httpConn.getReport();															
			return result;
		}
		
		return "";
	}
	
	//******************************** TUHAILSAN JSON BUTSAAH ************************//
	public String customSelect(Variant w) {
		String func = w.get("func");		
		String tableName = w.get("table");
		String where = "", result = "";
				 
		if (func.equals("_current_user_storage")) {
			where = w.get("where");
			String[] wh = where.split(",");
			String userCode = wh[0];
			String today = "2013-09-26";
			String query = "";
			if (wh.length > 1 && wh[1].equals("amount")) {
				query = "select code,(select SUM(confirmedCount*price) from Orders where userCode='"+userCode+"' and userCode=customerCode and productCode=code and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))='"+today+"') as get,(select SUM(amount) from Sales where userCode='"+userCode+"' and productCode=code and DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+today+"') as sold,(select top 1 lastCount*price from Orders where userCode='"+userCode+"' and productCode=code order by _date desc) as last,0 as ehlel from Product";
			} else
				query = "select code,(select SUM(confirmedCount) from Orders where userCode='"+userCode+"' and userCode=customerCode and productCode=code and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))='"+today+"') as get,(select SUM(quantity) from Sales where userCode='"+userCode+"' and productCode=code and DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+today+"') as sold,(select top 1 lastCount from Orders where userCode='"+userCode+"' and productCode=code order by _date desc) as last,0 as ehlel from Product";
			result = httpConn.getDataCollectorHaveByJson(query, "code,get,sold,last,ehlel", "s,i,i,i,f", "last");			
			System.out.println(result);
			return result;
		} else
		if (func.equals("_user_sale_detail")) {
			where = w.get("where"); 
			String[] wh = where.split(",");
			result = httpConn.jsonSalesDetailByUser(wh[0], wh[1], wh[2], wh[3]);
			cacheMan.saveAlternateCache(func, result);
			return result;
		} else
		if (func.equals("_user_sale_detail_brand")) {
			where = w.get("where");
			String[] wh = where.split(",");
			return httpConn.jsonSalesDetailByUserBrand(wh[0], wh[1], wh[2], wh[3]);
		} else
		if (func.equals("_plan_data")) {
			where = w.get("where");
			String[] wh = where.split(",");
			return httpConn.jsonDataPlan(wh[0],wh[1],wh[2]);
		} else
		if (func.equals("_user_order_complete")) {
			where = w.get("where");
			String query = "SELECT productCode, (select top 1 requestCount from orders where userCode='"+where+"' and productCode=b.productCode and requestCount>0 and confirmedCount>0 order by _date desc) as requestCount, (select top 1 inCount from orders where userCode='"+where+"' and productCode=b.productCode and flag=1 order by _date desc) as confirmedCount,(select top 1 inCount*size from orders JOIN Product on productCode=code where userCode='"+where+"' and productCode=b.productCode and flag=1 order by _date desc) as confirmedSize, (select top 1 wareHouseID from orders where userCode='"+where+"' and productCode=b.productCode and flag=1 order by _date desc) as wareHouseID from orders as b where wareHouseID=(select wareHouseID from Users where code='"+loggedUser+"') and productCode<>'nul' and flag>0 and userCode='"+where+"' group by productCode order by productCode";						
			return httpConn.getDataCollectorByJson(query, "productCode,requestCount,confirmedCount,confirmedSize,wareHouseID","s,i,i,f,i");												
		} else									
		if (func.equals("_user_saledata_for_plan")) {						
			where = w.get("where");
			String[] wh = where.split(",");
			return httpConn.jsonSalesUserData(wh[0], wh[1]);
		} else				
		if (func.equals("_infosale")) {
			where = w.get("where");
			String[]wh = where.split(",");				
			String date1 = wh[0];
			String date2 = wh[1];
			String logged = wh[3];   
			String mode = wh[4];
			String s = httpConn.getSaleMainInfo(date1, date2, wh[2], logged, mode);					
			return s;
		} else
		if (func.equals("_lease-customer-monthly-data")) {						
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
		    cal.setTime(date);
		    where = w.get("where");
		    int year = cal.get(Calendar.YEAR);
		    int month = cal.get(Calendar.MONTH)+1;
		    
			result = httpConn.getLeaseCustomers(year+"", month+"", where);						
			return result;
		} else
		if (func.equals("_infolease")) {					
			String s = httpConn.getLeaseMainInfo();					
			return s;
		} else
		if (func.equals("_info_product_count")) {					
			String s = httpConn.getProductLastCount();					
			return s;
		} else					
		if (func.equals("_info_user_sale")) {
			where = w.get("where");
			String[]wh = where.split(",");									
			String userCode = wh[0];
			return httpConn.getUserSaleMainInfo(userCode);
		} else
		if (func.equals("_info_customer_sale")) {
			where = w.get("where");
			String[]wh = where.split(",");									
			String customerCode = wh[0];
			return httpConn.getCustomerSaleMainInfo(customerCode);
		} else
		if (func.equals("_info_users_sale")) {						
			return httpConn.getUsersSaleData();
		} else																
		if (func.equals("_lease_main")) {
			where = w.get("where");
			String[]wh = where.split(",");
			result = httpConn.getLeaseMain(wh[0], wh[1], wh[2]);
			cacheMan.saveAlternateCache("Lease_Main", result);
			return result;
		} else		
		if (func.equals("_storage_to_storage")) {
			where = w.get("where");
			String[]wh = where.split(",");										
			if (wh != null && wh.length >= 3 && !wh[2].equals("null")) {							
				result = httpConn.jsonStorageToStorage(wh[0], wh[1], Integer.parseInt(wh[2]));
			}
			return result;
		} else		
		if (func.equals("_lease_customer")) {
			where = w.get("where");
			String[]wh = where.split(",");
			result = httpConn.getLeaseCustomer(wh[0], wh[1], wh[2]);
			cacheMan.saveAlternateCache("Lease_Customer", result);
			return result;
		} else
		if (func.equals("_product_user_data")) {
			return httpConn.jsonProductUserData();
		} else				
		if (func.equals("_user_route_entry")) {
			where = w.get("where");
			String[]wh = where.split(",");
			return httpConn.jsonUserEntryToCustomer(wh[0], wh[1]);
		} else		
		if (func.equals("_order_after_hand_sale")) {
			return httpConn.getOrderAfterHandSale(where);
		} else							
		if (func.equals("_product_user_data_detail")) {
			tableName = "Orders as B";
			String fd = "(select firstName from Users WHERE code=userCode) as userCode,productCode,firstCount,firstAmount,addCount,addAmount,soldCount,soldAmount,soldRCount,soldRAmount,lastCount,lastAmount";//w.get("fields");
			where = w.get("where");
			if (where == null || where.length() < 3)
				where = " WHERE productCode<>'nul' and (select top 1 lastCount from Orders where userCode=b.userCode and productCode=b.productCode order by _date desc)>0 GROUP by userCode,productCode ORDER by (select class from Product WHERE code=productCode)";
			else
				where = " WHERE userCode='"+where+"' and productCode<>'nul' and (select top 1 lastCount from Orders where userCode=b.userCode and productCode=b.productCode order by _date desc)>0 GROUP by userCode,productCode ORDER by (select class from Product WHERE code=productCode)";				
			System.out.println("SELECT "+fd+" "+tableName+" "+where);
			result = httpConn.jsonData(tableName, fd, where);
			
			return result;
		} else  
		if (func.equals("_storage_out_q")) {
			where = w.get("where");
			String[] wh = where.split(",");
			if (wh != null && wh.length>=2) {
				if (wh[0] == null || wh[0].equals("null")) wh[0] = "0";
				result = httpConn.storageOutReport(wh[1], Integer.parseInt(wh[0]), wh[2]);						
				cacheMan.saveAlternateCache("Storage_Out_Report", result);
			}
			return result;
		} else						
		if (func.equals("_storage_q")) {
			where = w.get("where");
			String[] wh = where.split(",");
			result = httpConn.storageReport(wh[1], wh[2], " ", Integer.parseInt(wh[0]));						
			return result;
		} else
		if (func.equals("_live_search")) {
			where = w.get("where");
			return httpConn.liveSearch(where); 
		} else
		if (func.equals("_live_detail")) {
			where = w.get("where");
			String[] wh = where.split(",");
			if (wh[0].length() > 3)
				wh[1] = "Customer";
			return httpConn.liveDetail(wh[0], wh[1]); 
		}
		
		return "";
	}
	
	public String customMobileSelect(Variant w) {		
		if (w.get("func").equals("OrderAcceptCommand")) { //mobile-oos order zovshooroh
			String action = "update";
			String tableName = w.get("table");
			String fields = w.get("fields");
			String values = w.get("types");
			String where = w.get("where");			
			httpConn.actionData(action, tableName, fields, values, where);
			
			String[]sp = values.split(",");
			String userCode = sp[0].substring(1, sp[0].length());
			String productCode = sp[1].substring(1, sp[1].length());
			httpConn.actionData(action, tableName, "flag,userCode", "i0,s"+userCode, "userCode='"+userCode+"' and productCode='"+productCode+"' and flag=1");
		} else		
		if (w.get("func").equals("LeaseInfo")) {
			String[] where = w.get("where").split(",");
			String result = mobileConn.jsonData("select sum(flag) as flag from Sales where flag>0 and discount="+where[0], "flag");
			System.out.println(result);
			return result;
		} else
		if (w.get("func").equals("TodaySale")) {
			String[] where = w.get("where").split(",");			 
			String today = httpConnection.today();
			if (where.length > 1) today = where[1]; 
			return mobileConn.jsonSalesTodayTotal(where[0], today);
		} else
		if (w.get("func").equals("TodayOrder")) {
			String[] where = w.get("where").split(",");			 
			String today = httpConnection.today();
			if (where.length > 1) today = where[1]; 
			return mobileConn.jsonOrderTodayTotal(where[0], today);
		} else
		if (w.get("func").equals("TodayDetail")) {
			String[] where = w.get("where").split(",");			 
			String today = httpConnection.today();
			if (where.length > 1) today = where[1]; 
			return mobileConn.getDataCollectorByJson("select productCode as code,(select top 1 name from Product where code=productCode) as data, type, sum(quantity) as quantity, SUM(amount) as amount from sales where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+today+"' and customerCode='"+where[0]+"' group by productCode,type", "code,data,type,quantity,amount", "s,s,i,i,i");
		} else	
		if (w.get("func").equals("TodayOrderDetail")) {
			String[] where = w.get("where").split(",");			 
			String today = httpConnection.today();
			if (where.length > 1) today = where[1]; 
			return mobileConn.getDataCollectorByJson("select productCode as code,(select top 1 name from Product where code=productCode) as data, 0 as type, sum(requestCount) as quantity, SUM(requestCount*price) as amount from orders where DATEADD(dd, 0, DATEDIFF(dd, 0, _date))='"+today+"' and customerCode='"+where[0]+"' group by productCode", "code,data,type,quantity,amount", "s,s,i,i,i");
		} else	
		if (w.get("func").equals("MyStorage")) {
			String[] where = w.get("where").split(",");			 						
			return mobileConn.jsonUserCurrentTotal(where[0]);
		} else			  
		if (w.get("func").equals("MonthSaleCustomer")) {
			String[] where = w.get("where").split(",");			
			return mobileConn.getDataCollectorByJson("select SUM(confirmedCount*price) as amount from orders where datepart(month,_date)=datepart(month,CURRENT_TIMESTAMP) and datepart(year,_date)=datepart(year,CURRENT_TIMESTAMP) and customerCode='"+where[0]+"'", "amount", "i");
		} else	
		if (w.get("func").equals("MonthPlanExecution")) {
			String[] where = w.get("where").split(",");	
			String userCode = where[0];	
			String channel = where[1];
			String subquery = " and brand<>'pepsi1' and brand<>'7up' and brand<>'mirinda' and brand<>'Спорт ундаа' ";
			if(channel.equals("pepsi")){
				subquery = " and (brand='pepsi1' or brand='7up' or brand='mirinda' or brand='Спорт ундаа' )";
				//subquery = " and code between '471001' and '477006'";   
			}
			
			if(channel.equals("total")){
				subquery = " ";
			}
			
			return mobileConn.getDataCollectorByJson("select code,name,isnull((select countTheshold from B_PLan where CURRENT_TIMESTAMP between startDate and endDate and userCode='"+userCode+"' and productCode = p.code),0) as tuluv,SUM(confirmedCount) as guitsSh,((SUM(confirmedCount)*100)/NULLIF((select countTheshold from B_PLan where CURRENT_TIMESTAMP between startDate and endDate and userCode='"+userCode+"' and productCode = p.code),0)) as per from orders as o join Product as p on o.productCode = p.code and userCode='"+userCode+"' and YEAR(_DATE)= YEAR(CURRENT_TIMESTAMP) and MONTH(_date) = MONTH(CURRENT_TIMESTAMP) "+subquery+" group by code,name,brand order by brand", "code,name,guitsSh,tuluv,per", "s,s,f,f,f");
		} else	
		if (w.get("func").equals("TodaySaleCustomer")) {
			String[] where = w.get("where").split(",");			
			String today = httpConnection.today();
			if (where.length > 1) today = where[1];			
			return mobileConn.getDataCollectorByJson("select customerCode as code, name+'|'+location as data,SUM(amount) as amount from sales JOIN Customer on code=customerCode where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+today+"' and userCode='"+where[0]+"' group by customerCode,ticketID,name,location", "code,data,amount", "s,s,i");
		} else
		if (w.get("func").equals("TodayOrderCustomer")) {
			String[] where = w.get("where").split(",");			
			String today = httpConnection.today();
			if (where.length > 1) today = where[1];
			String result = mobileConn.getDataCollectorByJson("select customerCode as code,ticketID,(select name from Customer where code=customerCode)+'|'+(select location from Customer where code=customerCode) as data,(select sum((requestCount/unit)/size) from Orders right JOIN Product on code=productCode where ticketID=b.ticketID and DATEADD(D, 0, DATEDIFF(D, 0, _date))='"+today+"' and userCode='"+where[0]+"' and requestCount>0 and confirmedCount>=0) as quantity, sum(confirmedCount) as amount, isnull((select userCode from Order_Trans where ticketID=b.ticketID and userCode='"+where[0]+"' and  DATEADD(D, 0, DATEDIFF(D, 0, _date))='"+today+"'),(select partnerCode from Users where code='"+where[0]+"')) as partner  from orders as b where requestCount>0 and DATEADD(D, 0, DATEDIFF(D, 0, _date))='"+today+"' and userCode='"+where[0]+"' group by customerCode,ticketID", "code,data,ticketID,quantity,amount,partner", "s,s,i,i,i,s");			
			return result;
		} else			
		if (w.get("func").equals("CustomerByManager")) {
			String where = w.get("where");			
			return mobileConn.getDataCollectorByJson("select code,name+'|'+location as customerName,userCode as routeID,priceTag,parentID,discount,posx as lat,posy as lng,loanMargin from Route_User join Customer on (subid=mon or subid=thue or subid=wed or subid=thur or subid=fri) and userCode='"+where+"'", "code,customerName,routeID,priceTag,parentID,discount,lat,lng,loanMargin", "s,s,s,i,i,f,f,f,f");			
		} else
		if (w.get("func").equals("CurrentOrders")) {
			String[] where = w.get("where").split(",");			
			String today = httpConnection.today();
			if (where.length > 1) today = where[1]; 
			return mobileConn.jsonSalesCurrentOrder(where[0], today);
		} else
		if (w.get("func").equals("LastOrders")) {    
			String[] where = w.get("where").split(",");			
			String today = httpConnection.today();
			if (where.length > 1) today = where[1]; 
			return mobileConn.jsonSalesLastOrder(where[0], today);
		} else
		if (w.get("func").equals("SalePlan")) {
			return "";
		} else
		if (w.get("func").equals("Customers")) {
			String where = w.get("where");
			String[] wh = where.split(",");
			String result = cacheMan.loadCache("Customers"+where);
			if (result.length() > 0) {
				System.out.println("from cache");
				return result;
			}
			result = httpConn.getDataCollectorByJson("select * from (SELECT customerCode as code,(SELECT TOP 1 name+'|'+location from Customer WHERE Customer.code=customerCode) as customerName,routeID,(SELECT TOP 1 pricetag from Customer WHERE Customer.code=customerCode) as priceTag,(SELECT TOP 1 parentID from Customer WHERE Customer.code=customerCode) as parentID,(SELECT TOP 1 discount from Customer WHERE Customer.code=customerCode) as discount,(SELECT TOP 1 posx from Customer WHERE Customer.code=customerCode) as lat,(SELECT TOP 1 posy from Customer WHERE Customer.code=customerCode) as lng, ROW_NUMBER() OVER (ORDER BY customerCode) as row FROM Route_Customer) a where row>"+wh[0]+" and row<="+wh[1], "code,customerName,routeID,priceTag,parentID,discount,lat,lng", "s,s,s,i,i,f,f,f");
			cacheMan.saveCache("Customers"+where, result);
			return result;
		} else		
		if (w.get("func").equals("Today")) {
			String userCode = w.get("where");
			String weekday = getWeekDay(convertDateToString());
			String routeID = httpConn.getRouteIdUsers(weekday, userCode);
			String packet = httpConn.getHavePacket(routeID);	
			String message = httpConn.getMessage(userCode);
			String today = convertDateToString();
			String workinfo = mobileConn.getTodayWorkInfo(userCode, routeID, today, weekday);
			String[] userData = httpConn.getUserGroup_PriceTag(userCode).split(":");			
			String userType = userData[0];
			String userName = userData[1];
			String priceTag = userData[2];
			String wareHouse = userData[3];
			String section = userData[4];
			message+="\n\n"+workinfo;
			
			return "{'results':1, 'success': 'true', 'items':[{'today':'"+today+"','weekday':'"+weekday+"','routeID':'"+routeID+"','packet':'"+packet+"','rentedCustomers':'"+httpConn.getRentedCustomers(userCode)+"','plannedCustomers':'"+httpConn.getPlanExecuteCustomers(userCode)+"','message':'"+message+"','userName':'"+userName+"','userType':'"+userType+"','priceTag':'"+priceTag+"','wareHouseId':'"+wareHouse+"','section':'"+section+"'}]}";			
		} else
		if (w.get("func").equals("IncomingOrders")) {
			String[] u = w.get("where").split(",");
			String userCode = u[0];
			String selUserCode = u[1];
			return httpConn.jsonData("Orders as b", "id,_date,userCode,productCode,storageCount,availCount,requestCount,confirmedCount,price,requestCount*price as amount,wareHouseID", " WHERE productCode in (select productCode from Product_Accept where userCode='"+userCode+"') and requestCount>0 and confirmedCount=0 and userCode='"+selUserCode+"' and flag=0 ORDER by _date desc,confirmedCount asc");			
		} else
		if (w.get("func").equals("IncomingOrders")) {
			String[] u = w.get("where").split(",");
			String userCode = u[0];
			String selUserCode = u[1];
			return httpConn.jsonData("Orders as b", "id,_date,userCode,productCode,storageCount,availCount,requestCount,confirmedCount,price,requestCount*price as amount,wareHouseID", " WHERE productCode in (select productCode from Product_Accept where userCode='"+userCode+"') and requestCount@0 and confirmedCount=0 and userCode='"+selUserCode+"' and flag=0 ORDER by _date desc,confirmedCount asc");			
		} else
		if (w.get("func").equals("OrderManList")) {
			return remoteJSON("_remote_ordered_user_names", w);
		} else
		if (w.get("func").equals("Dashboard")) {
			String where = w.get("where");
			String[]wh = where.split(",");				
			String date1 = wh[0];
			String date2 = wh[1];
			String logged = wh[3];
			String mode = wh[4];
			return mobileConn.getSaleMainInfo(date1, date2, wh[2], logged, mode);			
		} else
		if (w.get("func").equals("GoOrder")) {
			String[] where = w.get("where").split(",");
			String driver = where[2];
			String outDate = "";
			if (where.length > 3) {
				driver = where[3];
				if (where.length > 4)
					outDate = where[4];
			}
			return httpConn.actionOrderAcceptData(Integer.parseInt(where[0]), where[1], where[2], driver, outDate);
		} else
		if (w.get("func").equals("CustomerInfo")) {
			String code = w.get("where");			
			Collection c = httpConn.getDataCollector("select name,location,(select descr from User_Type where _group=type) as type,(select name from Parent_Names where id=parentID) as parentID,loanMargin,_date,belong,(select routeName from Route where routeId=subid) as subid from Customer where code='"+code+"'", "name,location,type,parentID,loanMargin,_date,belong,subid", "s,s,s,s,i,s,i,s");
			if (c.size() > 0) {
				Variant v = c.elementAt(0);
			
				return "{'results':1, 'success': 'true', 'items':[{'name':'"+v.get("name")+"','location':'"+v.get("name")+"','type':'"+v.get("type")+"','parentID':'"+v.get("name")+"','loanMargin':'"+v.get("loanMargin")+"','belong':'"+((v.get("belong").equals("1")?"Тийм":"Үгүй"))+"','subid':'"+v.get("subid")+"'}]}";
			}
			return "";
		}
		
		return "success";
	}	
}
