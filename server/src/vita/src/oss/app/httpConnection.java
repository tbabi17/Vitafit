package oss.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import oss.additional.CrossData;
import oss.additional.Data;
import oss.additional.GroupPlan;
import oss.additional.PlanData;
import oss.additional.UserData;
import oss.cache.logger;
import oss.core.sharedProcedures;
import oss.report.Collection;
import oss.report.Variant;
 
     
public class httpConnection extends sharedProcedures {		
	systemController shared; 
	      
	public httpConnection(systemController th) { 
		shared = th;
		shared.initConnection();  
	}				         
	     
	public String jsonDataPlan(String name, String userCode, String mode) {
		int max = 0;
		String index = ""; 
		LinkedList<GroupPlan> sales = new LinkedList<GroupPlan>();			
		try {
			Connection con = shared.getConnection();
			String query = "";
			if (userCode != null && userCode.length() == 3)
				query = "SELECT code,max(B_Plan.section),avg(price),sum(countTheshold),sum(amountTheshold) FROM B_PLan join product on productCode=product.code WHERE b_plan.name='"+name+"' and b_plan.userCode='"+userCode+"' GROUP by product.code,product.class,B_Plan.section ORDER by product.class";
			else
				query = "SELECT code,max(B_Plan.section),avg(price),sum(countTheshold),sum(amountTheshold) FROM B_PLan join product on productCode=product.code WHERE b_plan.name='"+name+"' GROUP by product.code,product.class,B_Plan.section ORDER by product.class";
			PreparedStatement ps = con.prepareStatement(query);			
			ResultSet rs = ps.executeQuery();					
			GroupPlan sale = new GroupPlan();
			String active = "";			
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) { 
						sales.add(sale);
						sale = new GroupPlan();
					}
					active = rs.getString(1);
					sale.productCode = active;					
				}								
				sale.section = rs.getString(2);
				
				sale.values1[sale._group] = rs.getFloat(3);
				sale.values2[sale._group] = rs.getFloat(4);
				sale.values3[sale._group] = rs.getFloat(5);
				if (index.indexOf(sale._group+"") == -1)
					index += sale._group+",";
				if (max < sale._group) max = sale._group;
			}
			if (active.length() > 0)
				sales.add(sale);
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
				
		String[] _g = index.split(",");
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
		for (int i = 0; i < sales.size(); i ++) {
			GroupPlan sale = sales.get(i);
			data += "{'productCode':'"+sale.productCode+"',";
						
			for (int j = 0; j < _g.length; j++) {
					int ind = Integer.parseInt(_g[j]);
					data += "'price"+_g[j]+"':"+sale.values1[ind]+"," +
					"'countTheshold"+_g[j]+"':"+sale.values2[ind]+",'amountTheshold"+_g[j]+"':"+sale.values3[ind]+",";
			}
			
			data = data.substring(0, data.length() - 1);
			data += "},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";							
		return data;
	}
	
	public String jsonOrderComplete() {		
		LinkedList<CrossData> orders = new LinkedList<CrossData>();			
		try {
			Connection con = shared.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT userCode, productCode, (select top 1 inCount from orders where userCode=b.userCode and productCode=b.productCode and flag=1 order by _date desc) from orders as b where productCode<>'nul' and flag>0 group by userCode, productCode order by userCode, productCode");			
			ResultSet rs = ps.executeQuery();					
			CrossData order = new CrossData();
			String active = "";			
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) { 
						orders.add(order);
						order = new CrossData();
					}
					active = rs.getString(1);
					order.userCode = active;					
				}				
				order.products.add(rs.getString(2));
				order.values.add(rs.getFloat(3));							
			}
			if (active.length() > 0)
				orders.add(order);
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		String data = "{'results':"+orders.size()+", 'success': 'true', 'items':[";
		for (int i = 0; i < orders.size(); i ++) {
			CrossData order = orders.get(i);
			
			data += "{'userCode':'"+order.userCode+"',";			
			float total = 0;
			for (int j = 0; j < order.products.size(); j++) {
				total += order.values.get(j);
				data += "'"+order.products.get(j)+"':"+(order.values.get(j))+",";
			}
			data += "'total':"+total+"},";			
		}		
		if (orders.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";							
		
		return data;				
	}							
	
	public String jsonSalesData(String startDate, String endDate, String products, String lan, String _group) {	
		LinkedList<CrossData> sales = new LinkedList<CrossData>();
		try {
			Connection con = shared.getConnection();		
			String query = "";
			 
			if (products.equals("all"))				
				query = "select code, _group, productCode, sum(quantity)-sum((case [type] when 1 then 1 when 3 then 1 else 0 end)*quantity),sum(amount)-sum(amount*(case [type] when 1 then 1 when 3 then 1 else 0 end)),sum((case [type] when 1 then 1 else 0 end)*quantity),sum(amount*(case [type] when 1 then 1 else 0 end)),sum(amount*(case [type] when 3 then 1 else 0 end)),datediff(ss, max(_dateStamp), CURRENT_TIMESTAMP) from Users left join Sales on userCode=code and _dateStamp>='"+startDate+"' and _dateStamp<'"+endDate+"' where 1=1 and userCode in (select code from users where _group=8 or _group=19 or _group=17) unp group by code,userCode,_group,productCode Order by _group,userCode";
			else { 
				query = "select code, _group, productCode, sum(quantity)-sum((case [type] when 1 then 1 when 3 then 1 else 0 end)*quantity),sum(amount)-sum(amount*(case [type] when 1 then 1 when 3 then 1 else 0 end)),sum((case [type] when 1 then 1 else 0 end)*quantity),sum(amount*(case [type] when 1 then 1 else 0 end)),sum(amount*(case [type] when 3 then 1 else 0 end)),datediff(ss, max(_dateStamp), CURRENT_TIMESTAMP) from Users left join Sales on userCode=code and _dateStamp>='"+startDate+"' and _dateStamp<'"+endDate+"' where (CHARINDEX(':'+productCode+':','"+products+"')>0) and _group="+_group+" unp and userCode in (select code from users where _group=8 or _group=19 or _group=17) group by code,userCode,_group,productCode Order by _group,userCode";				
			}
			
			query = npString(query, "");
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			CrossData sale = new CrossData();
			String active = "";			
			while (rs.next()) {				
				if (!active.equals(rs.getString(1)+rs.getString(2))) {
					if (active.length() > 0) { 
						sales.add(sale);
						sale = new CrossData();
					}
					active = rs.getString(1)+rs.getString(2);
					sale.userCode = rs.getString(1);
					sale._group = rs.getString(2);
				}				
				sale.products.add(rs.getString(3));
				sale.values.add(rs.getFloat(4));
				sale.amounts.add(rs.getFloat(5));
				sale.rents.add(rs.getFloat(6));
				sale.ramounts.add(rs.getFloat(7));
				sale.rentAmount += rs.getFloat(8);
				sale.level = rs.getInt(9);				
			}
			if (active.length() > 0)
				sales.add(sale);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
		for (int i = 0; i < sales.size(); i ++) {
			CrossData sale = sales.get(i);						
			data += "{'name':'"+sale.userCode+"','group':'"+sale._group+"','level':"+sale.level+",";
			float sum_quantity = 0, sum_amount = 0, sum_rent = 0, sum_ramount = 0;
			for (int j = 0; j < sale.products.size(); j++) {								
				sum_quantity += sale.values.get(j);
				sum_amount += sale.amounts.get(j);
				sum_rent += sale.rents.get(j);
				sum_ramount += sale.ramounts.get(j);
				data += "'"+sale.products.get(j)+"':"+(sale.values.get(j)+sale.rents.get(j))+",";				
			}
			String sum_q = Float.toString(sum_quantity);
			String sum_a = Float.toString(sum_amount);
			String sum_r = Float.toString(sale.rentAmount);
			String sum_ra = Float.toString(sum_ramount);
			String pay_all = Float.toString(sum_amount+sale.rentAmount);
			String all = Float.toString(sum_ramount+sum_amount+sale.rentAmount);
			data += "'sum_q':"+sum_q+",'sum_r':"+sum_r+",'sum_ar':"+sum_ra+",'sum_a':"+sum_a+",'pay_all':"+pay_all+",'sum_all':"+all+"},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";		
		
		return data;
	}		
	
	public String jsonSalesDataPre(String startDate, String endDate, String products, String lan, String section) {	
		LinkedList<CrossData> sales = new LinkedList<CrossData>();
		try {
			Connection con = shared.getConnection();		
			String query = "";
			
			if (products.equals("all"))
				query = "select code, brand, userCode, sum(quantity)-sum((case [type] when 1 then 1 when 3 then 1 else 0 end)*quantity),sum(amount)-sum(amount*(case [type] when 1 then 1 when 3 then 1 else 0 end)),sum((case [type] when 1 then 1 else 0 end)*quantity),sum(amount*(case [type] when 1 then 1 else 0 end)),sum(amount*(case [type] when 3 then 1 else 0 end)),datediff(ss, max(_dateStamp), CURRENT_TIMESTAMP) from Product right join Sales on productCode=code and _dateStamp>='"+startDate+"' and _dateStamp<'"+endDate+"' where isSale=1 group by code,userCode,brand,userCode Order by brand,userCode";
			else 
				query = "select code, brand, userCode, sum(quantity)-sum((case [type] when 1 then 1 when 3 then 1 else 0 end)*quantity),sum(amount)-sum(amount*(case [type] when 1 then 1 when 3 then 1 else 0 end)),sum((case [type] when 1 then 1 else 0 end)*quantity),sum(amount*(case [type] when 1 then 1 else 0 end)),sum(amount*(case [type] when 3 then 1 else 0 end)),datediff(ss, max(_dateStamp), CURRENT_TIMESTAMP) from Product right join Sales on productCode=code and _dateStamp>='"+startDate+"' and _dateStamp<'"+endDate+"' where (CHARINDEX(':'+code+':','"+products+"')>0) and isSale=1 group by code,userCode,brand,userCode Order by brand,userCode";
			
			query = npString(query, "");
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			CrossData sale = new CrossData();
			String active = "";			
			while (rs.next()) {				
				if (!active.equals(rs.getString(1)+rs.getString(2))) {
					if (active.length() > 0) { 
						sales.add(sale);
						sale = new CrossData();
					}
					active = rs.getString(1)+rs.getString(2);
					sale.userCode = rs.getString(1);
					sale._group = rs.getString(2);
				}				
				sale.products.add(rs.getString(3));
				sale.values.add(rs.getFloat(4));
				sale.amounts.add(rs.getFloat(5));
				sale.rents.add(rs.getFloat(6));
				sale.ramounts.add(rs.getFloat(7));
				sale.rentAmount += rs.getFloat(8);
				sale.level = rs.getInt(9);				
			}
			if (active.length() > 0)
				sales.add(sale);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
		for (int i = 0; i < sales.size(); i ++) {
			CrossData sale = sales.get(i);						
			data += "{'name':'"+sale.userCode+"','group':'"+sale._group+"','level':"+sale.level+",";
			float sum_quantity = 0, sum_amount = 0, sum_rent = 0, sum_ramount = 0;
			for (int j = 0; j < sale.products.size(); j++) {								
				sum_quantity += sale.values.get(j);
				sum_amount += sale.amounts.get(j);
				sum_rent += sale.rents.get(j);
				sum_ramount += sale.ramounts.get(j);
				data += "'"+sale.products.get(j)+"':"+(sale.values.get(j)+sale.rents.get(j))+",";				
			}
			String sum_q = Float.toString(sum_quantity);
			String sum_a = Float.toString(sum_amount);
			String sum_r = Float.toString(sale.rentAmount);
			String sum_ra = Float.toString(sum_ramount);
			String pay_all = Float.toString(sum_amount+sale.rentAmount);
			String all = Float.toString(sum_ramount+sum_amount+sale.rentAmount);
			data += "'sum_q':"+sum_q+",'sum_r':"+sum_r+",'sum_ar':"+sum_ra+",'sum_a':"+sum_a+",'pay_all':"+pay_all+",'sum_all':"+all+"},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";					
		return data;
	}
	
	public String jsonSalesDataBrand(String startDate, String endDate, String lan, String section) {	
		LinkedList<CrossData> sales = new LinkedList<CrossData>();
		try {
			Connection con = shared.getConnection();		
			String query = "select Users.code, _group, brand, sum(quantity)-sum((case [type] when 1 then 1 when 3 then 1 else 0 end)*quantity),sum(amount)-sum(amount*(case [type] when 1 then 1 else 0 end)),sum((case [type] when 1 then 1 else 0 end)*quantity),sum(amount*(case [type] when 1 then 1 else 0 end)),sum(amount*(case [type] when 3 then 1 else 0 end)),datediff(ss, max(_dateStamp), CURRENT_TIMESTAMP) from Users join Sales on userCode=code JOIN Product on Product.code=Sales.productCode where _dateStamp>='"+startDate+"' and _dateStamp<'"+endDate+"' and Users.section='"+section+"' group by Users.code,userCode,_group,brand order by _group,Users.code";
			query = npString(query, "");
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			CrossData sale = new CrossData();
			String active = "";			
			while (rs.next()) {				
				if (!active.equals(rs.getString(1)+rs.getString(2))) {
					if (active.length() > 0) { 
						sales.add(sale);
						sale = new CrossData();
					}
					active = rs.getString(1)+rs.getString(2);
					sale.userCode = rs.getString(1);
					sale._group = rs.getString(2);
				}				
				sale.products.add(rs.getString(3));
				sale.values.add(rs.getFloat(4));
				sale.amounts.add(rs.getFloat(5));
				sale.rents.add(rs.getFloat(6));
				sale.ramounts.add(rs.getFloat(7));
				sale.rentAmount += rs.getFloat(8);
				sale.level = rs.getInt(9);				
			}
			if (active.length() > 0)
				sales.add(sale);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}

		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
		for (int i = 0; i < sales.size(); i ++) {
			CrossData sale = sales.get(i);
			data += "{'name':'"+sale.userCode+"','group':'"+sale._group+"','level':"+sale.level+",";
			float sum_quantity = 0, sum_amount = 0, sum_rent = 0, sum_ramount = 0;
			for (int j = 0; j < sale.products.size(); j++) {
				sum_quantity += sale.values.get(j);
				sum_amount += sale.amounts.get(j);
				sum_rent += sale.rents.get(j);
				sum_ramount += sale.ramounts.get(j);
				data += "'"+sale.products.get(j)+"':"+(sale.values.get(j)+sale.rents.get(j))+",";
			}
			String sum_q = Float.toString(sum_quantity);
			String sum_a = Float.toString(sum_amount);
			String sum_r = Float.toString(sale.rentAmount);
			String sum_ra = Float.toString(sum_ramount);
			String pay_all = Float.toString(sum_amount+sale.rentAmount);
			String all = Float.toString(sum_ramount+sum_amount+sale.rentAmount);
			data += "'sum_q':"+sum_q+",'sum_r':"+sum_r+",'sum_ar':"+sum_ra+",'sum_a':"+sum_a+",'pay_all':"+pay_all+",'sum_all':"+all+"},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";		
		
		return data;
	}
	
	public LinkedList<CrossData> listDetailUser(String entry, String query, String date1, String userCode) {
		LinkedList<CrossData> sales = new LinkedList<CrossData>();
		try {
			Connection con = shared.getConnection();

			if (!entry.equals("entry")) 
				query = "select customerCode,7,'00:00:00','nul',0,0,0,0,0,0 from Route_Customer where routeiD=(select top 1 "+getWeekDay(date1)+" from Route_User where userCode='"+userCode+"') and customerCode not in (select customerCode from Sales where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and userCode='"+userCode+"')";			
			
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			CrossData sale = new CrossData();
			String active = "";
			int group = -1;
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0 && group != -1) {						
						sales.add(sale);
						sale = new CrossData();
					}
					active = rs.getString(1);
					group = rs.getInt(2);
					sale.userCode = active;
					sale.group = group;
					sale._dateStamp = rs.getString(3);
				}				
				sale.products.add(rs.getString(4));
				sale.values.add(rs.getFloat(5));				
				sale.rents.add(rs.getFloat(6));
				sale.amounts.add(rs.getFloat(7));
				sale.ramounts.add(rs.getFloat(8));
				sale.rentAmount += rs.getFloat(9);
				sale.orderAmount += rs.getFloat(10);
			}
			if (active.length() > 0)
				sales.add(sale);
						
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return sales;
	}
	
	public String jsonSalesDetailByUser(String date1, String userCode, String products, String entry) {									
		LinkedList<CrossData> sales = new LinkedList<CrossData>();
		LinkedList<CrossData> sales1 = new LinkedList<CrossData>();
		if (entry.equals("entry")) {
			String query = "SELECT customerCode, MIN(type), convert(varchar, min(_dateStamp), 108),  productCode, (select sum(quantity) from Sales where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and customerCode=b.customerCode and userCode='"+userCode+"' and productCode=b.productCode and type=0 GROUP by productCode),(select sum(quantity) from Sales where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and customerCode=b.customerCode and userCode='"+userCode+"' and productCode=b.productCode and type=1 GROUP by productCode),(select sum(amount) from Sales where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and customerCode=b.customerCode and userCode='"+userCode+"' and productCode=b.productCode and type=0 GROUP by productCode),(select sum(amount) from Sales where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and customerCode=b.customerCode and userCode='"+userCode+"' and productCode=b.productCode and type=1 GROUP by productCode),(select sum(amount) from Sales where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and customerCode=b.customerCode and userCode='"+userCode+"' and productCode=b.productCode and type=3 GROUP by productCode),(select sum(requestCount*price) from Orders where customerCode=b.customerCode and userCode='"+userCode+"' and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))='"+date1+"') FROM Sales as b WHERE DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and userCode='"+userCode+"' GROUP by customerCode,productCode Order by customerCode,min(_dateStamp),productCode";
			String query1 = "select customerCode,0,convert(varchar, min(_date), 108),productCode,0,0,0,0,0,SUM(requestCount*price) from Orders where requestCount>0 and customerCode<>userCode and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))='"+date1+"' and userCode='"+userCode+"' group by customerCode,productCode Order by customerCode,min(_date),productCode";
			sales = listDetailUser(entry, query, date1, userCode);
			sales1 = listDetailUser(entry, query1, date1, userCode);
		} else {
			String query = "select customerCode,7,'00:00:00','nul',0,0,0,0,0,0 from Route_Customer where routeiD=(select top 1 "+getWeekDay(date1)+" from Route_User where userCode='"+userCode+"') and customerCode not in (select customerCode from Sales where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and userCode='"+userCode+"')";
			sales = listDetailUser(entry, query, date1, userCode);
		}
		for (int i = 0; i < sales1.size(); i++)
			sales.add(sales1.get(i));
		
		String[] types = {shared.getLanguageWord(313), shared.getLanguageWord(312), shared.getLanguageWord(311), shared.getLanguageWord(311), shared.getLanguageWord(600), shared.getLanguageWord(601), shared.getLanguageWord(595), shared.getLanguageWord(602)};		
		
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
		for (int i = 0; i < sales.size(); i ++) {
			CrossData sale = sales.get(i);
			data += "{'id':"+(i+1)+",'customerCode':'"+sale.userCode+"','type':'"+types[(sale.group>=10)?7:sale.group]+"','_dateStamp':'"+sale._dateStamp+"',";
			float sum_quantity = 0, sum_amount = 0, sum_rent = 0, sum_ramount = 0;
			for (int j = 0; j < sale.products.size(); j++) {
				sum_quantity += sale.values.get(j);
				sum_amount += sale.amounts.get(j);
				sum_rent += sale.rents.get(j);
				sum_ramount += sale.ramounts.get(j);
				data += "'"+sale.products.get(j)+"A':"+(sale.values.get(j))+",";
				data += "'"+sale.products.get(j)+"R':"+(sale.rents.get(j))+",";
				data += "'"+sale.products.get(j)+"':"+(sale.rents.get(j)+sale.values.get(j))+",";
			}
			String sum_q = Float.toString(sum_quantity);
			String sum_a = Float.toString(sum_amount);
			String sum_r = Float.toString(sale.rentAmount);
			String sum_z = Float.toString(sale.orderAmount);
			String sum_ra = Float.toString(sum_ramount);
			String all = Float.toString(sum_amount+sale.rentAmount);
			data += "'sum_z':"+sum_z+",'sum_q':"+sum_q+",'sum_r':"+sum_r+",'sum_ar':"+sum_ra+",'sum_a':"+sum_a+",'sum_all':"+all+",'sum_alls':"+(sum_amount+sum_ramount+sale.rentAmount)+"},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";						
		
		return data;
	}
	
	public String jsonSalesDetailByUserBrand(String date1, String userCode, String products, String entry) {	
		
		LinkedList<CrossData> sales = new LinkedList<CrossData>();
		try {
			Connection con = shared.getConnection();
			String query = "";
			if (entry.equals("entry")) {
				//if (products.equals("all"))
					query = "SELECT customerCode, min(type), convert(varchar, min(_dateStamp), 108), brand,(select sum(quantity) from Sales LEFT JOIN Product on code=productCode where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and customerCode=b.customerCode and userCode='"+userCode+"' and brand=c.brand and type=0),(select sum(quantity) from Sales LEFT JOIN Product on code=productCode where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and customerCode=b.customerCode and userCode='"+userCode+"' and brand=c.brand and type=1),(select sum(amount) from Sales LEFT JOIN Product on code=productCode where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and customerCode=b.customerCode and userCode='"+userCode+"' and brand=c.brand and type=0),(select sum(amount) from Sales LEFT JOIN Product on code=productCode where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and customerCode=b.customerCode and userCode='"+userCode+"' and brand=c.brand and type=1),(select sum(amount) from Sales LEFT JOIN Product on code=productCode where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and customerCode=b.customerCode and userCode='"+userCode+"' and brand=c.brand and type=3) FROM Sales as b LEFT JOIN Product as c on code=productCode WHERE DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and userCode='"+userCode+"' GROUP by customerCode,brand Order by min(_dateStamp)";
				//else
					//query = "SELECT customerCode, min(type), convert(varchar, min(_dateStamp), 108), productCode, sum(quantity)-sum((case [type] when 1 then 1 else 0 end)*quantity), sum(amount)-sum(amount*(case [type] when 1 then 1 when 3 then 1 else 0 end)), sum((case [type] when 1 then 1 else 0 end)*quantity),sum(amount*(case [type] when 1 then 1 else 0 end)),sum(amount*(case [type] when 3 then 1 else 0 end)) FROM Sales WHERE DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and userCode='"+userCode+"' and CHARINDEX(':'+productCode+':', '"+products+"')>0 GROUP by customerCode,productCode Order by min(_dateStamp),customerCode";
			} else {
				query = "select customerCode,7,'00:00:00','nul',0,0,0,0,0 from Route_Customer where routeiD=(select "+getWeekDay(date1)+" from Route_User where userCode='"+userCode+"') and customerCode not in (select customerCode from Sales where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))='"+date1+"' and userCode='"+userCode+"')";
			}
			
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			CrossData sale = new CrossData();
			String active = "";
			int group = -1;
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0 && group != -1) { 						
						sales.add(sale);
						sale = new CrossData();
					}
					active = rs.getString(1);
					group = rs.getInt(2);
					sale.userCode = active;
					sale.group = group;
					sale._dateStamp = rs.getString(3);
				}				
				sale.products.add(rs.getString(4));
				sale.values.add(rs.getFloat(5));
				sale.rents.add(rs.getFloat(6));
				sale.amounts.add(rs.getFloat(7));				
				sale.ramounts.add(rs.getFloat(8));
				sale.rentAmount += rs.getFloat(9);
			}
			if (active.length() > 0)
				sales.add(sale);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}

		String[] types = {shared.getLanguageWord(313), shared.getLanguageWord(312), shared.getLanguageWord(311), shared.getLanguageWord(311), shared.getLanguageWord(600), shared.getLanguageWord(601), shared.getLanguageWord(595), shared.getLanguageWord(602)};
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
		for (int i = 0; i < sales.size(); i ++) {
			CrossData sale = sales.get(i);
			data += "{'customerCode':'"+sale.userCode+"','type':'"+types[(sale.group>=10)?6:sale.group]+"','_dateStamp':'"+sale._dateStamp+"',";
			float sum_quantity = 0, sum_amount = 0, sum_rent = 0, sum_ramount = 0;
			for (int j = 0; j < sale.products.size(); j++) {
				sum_quantity += sale.values.get(j);
				sum_amount += sale.amounts.get(j);
				sum_rent += sale.rents.get(j);
				sum_ramount += sale.ramounts.get(j);
				data += "'"+sale.products.get(j)+"A':"+(sale.values.get(j))+",";
				data += "'"+sale.products.get(j)+"R':"+(sale.rents.get(j))+",";
				data += "'"+sale.products.get(j)+"':"+(sale.rents.get(j)+sale.values.get(j))+",";
			}
			String sum_q = Float.toString(sum_quantity);
			String sum_a = Float.toString(sum_amount);
			String sum_r = Float.toString(sale.rentAmount);
			String sum_ra = Float.toString(sum_ramount);
			String all = Float.toString(sale.rentAmount+sum_amount);
			data += "'sum_q':"+sum_q+",'sum_r':"+sum_r+",'sum_ar':"+sum_ra+",'sum_a':"+sum_a+",'sum_all':"+all+"},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";		
		
		return data;
	}
	
	public String jsonSalesUserData(String userCode, String name) {	//user iin borluulaltiin uzuuleltiig haruulna
				
		LinkedList<UserData> sales = new LinkedList<UserData>();
		try {			
			Connection con = shared.getConnection();			
			PreparedStatement ps;			
			if (userCode != null && userCode.length() > 1)
				ps = con.prepareStatement("SELECT alldays.dayname, productCode, sum(quantity)-sum((case [type] when 1 then 1*(case userType when 2 then 0 when 6 then 0 else 1 end) else 0 end)*quantity), sum(amount), sum((case [type] when 1 then 1 else 0 end)*quantity),sum(amount*(case [type] when 1 then 1*(case userType when 2 then 0 when 6 then 0 else 1 end) else 0 end)) FROM Sales right join alldays on userCode='"+userCode+"' and (datepart(day, _dateStamp)=convert(int,substring(alldays.dayname, 5, 2)) and DATENAME(month,_dateStamp) like substring(alldays.dayname, 0, 3)+'%') WHERE active=0 and CAST(convert(varchar,_year)+dayName AS DATETIME)>=(select max(startDate) from b_plan where name='"+name+"') and CAST(convert(varchar,_year)+dayName AS DATETIME)<=(select max(endDate) from b_plan where name='"+name+"') GROUP by alldays.dayname,alldays.id,substring(convert(varchar,_dateStamp), 0, 7),productCode ORDER BY alldays.id,substring(convert(varchar,_dateStamp), 0, 7)");
			else
				ps = con.prepareStatement("SELECT alldays.dayname, productCode, sum(quantity)-sum((case [type] when 1 then 1*(case userType when 2 then 0 when 6 then 0 else 1 end) else 0 end)*quantity), sum(amount), sum((case [type] when 1 then 1 else 0 end)*quantity),sum(amount*(case [type] when 1 then 1*(case userType when 2 then 0 when 6 then 0 else 1 end) else 0 end)) FROM Sales right join alldays on (datepart(day, _dateStamp)=convert(int,substring(alldays.dayname, 5, 2)) and DATENAME(month,_dateStamp) like substring(alldays.dayname, 0, 3)+'%') WHERE active=0 and CAST(convert(varchar,_year)+dayName AS DATETIME)>=(select max(startDate) from b_plan where name='"+name+"') and CAST(convert(varchar,_year)+dayName AS DATETIME)<=(select max(endDate) from b_plan where name='"+name+"') GROUP by alldays.dayname,alldays.id,substring(convert(varchar,_dateStamp), 0, 7),productCode ORDER BY alldays.id,substring(convert(varchar,_dateStamp), 0, 7)");
			ResultSet rs = ps.executeQuery();			
			UserData sale = new UserData();
			String active = "";
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) { 
						sales.add(sale);
						sale = new UserData();
					}
					active = rs.getString(1);
					sale.data = active;										
				}				
				sale.products.add(rs.getString(2));
				sale.values1.add(rs.getFloat(3));
				sale.amounts1.add(rs.getFloat(4));
				sale.values2.add(rs.getFloat(5));
				sale.amounts2.add(rs.getFloat(6));				
			}
			if (active.length() > 0)
				sales.add(sale);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}

		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
		for (int i = 0; i < sales.size(); i ++) {
			UserData sale = sales.get(i);
			data += "{'_date':'"+sale.data+"',";
			float sum_loan = 0, sum_total = 0;
			for (int j = 0; j < sale.products.size(); j++) {
				sum_total += sale.amounts1.get(j);				
				sum_loan += sale.amounts2.get(j);
				data += "'"+sale.products.get(j)+"A':"+sale.values1.get(j)+",'"+sale.products.get(j)+"R':"+sale.values2.get(j)+",";
			}
			String sum_l = Float.toString(sum_loan);
			String sum_t = Float.toString(sum_total);
			String sum_b = Float.toString(sum_total-sum_loan);			
			data += "'sum_loan':"+sum_l+",'sum_belen':"+sum_b+",'sum_total':"+sum_t+"},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";		
		
		return data;
	}					
				
	public String jsonProductUserData() {	//user - iin baraanii niit uldegdel		
		LinkedList<UserData> sales = new LinkedList<UserData>();
		try {
			Connection con = shared.getConnection();
			PreparedStatement ps = con.prepareStatement("select userCode,productCode,(select top 1 lastcount from orders where userCode=b.userCode and productCode=b.productCode and datediff(dd, _date, CURRENT_TIMESTAMP)>=1 order by id desc), (select sum(confirmedCount) from orders where userCode=b.userCode and productCode=b.productCode and confirmedCount>0 and _date>=convert(varchar, GETDATE(), 101)), (select top 1 lastcount from orders where userCode=b.userCode and productCode=b.productCode order by id desc) as lastCount, (select sum(quantity) from sales where userCode=b.userCode and productCode=b.productCode and _dateStamp>=convert(varchar, GETDATE(), 101)) as soldCount from Orders as B GROUP by userCode,productCode");
			ResultSet rs = ps.executeQuery();
			UserData sale = new UserData();
			String active = "";
			while (rs.next()) {				
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) { 
						sales.add(sale);
						sale = new UserData();
					}
					active = rs.getString(1);
					sale.data = active;						
				}				
				sale.products.add(rs.getString(2));
				sale.values1.add(rs.getFloat(3));
				sale.values2.add(rs.getFloat(4));
				sale.amounts1.add(rs.getFloat(5));
				sale.amounts2.add(rs.getFloat(6));				
			}
			if (active.length() > 0)
				sales.add(sale);
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
							
		for (int i = 0; i < sales.size(); i ++) {
			UserData sale = sales.get(i);
			data += "{'data':'"+sale.data+"',";
			float sum_first = 0, sum_last = 0,sum_add = 0, sum_s = 0;
			for (int j = 0; j < sale.products.size(); j++) {
				sum_first += sale.values1.get(j);
				sum_add += sale.values2.get(j);
				sum_last += sale.amounts1.get(j);
				sum_s += sale.amounts2.get(j);
			}			
			String sum_f = Float.toString(sum_first);			
			String sum_l = Float.toString(sum_last);
			data += "'firstCount':"+sum_f+",'addCount':"+sum_add+",'lastCount':"+sum_l+",'soldCount':"+sum_s+"},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);		
				
		data += "]}";
		
		return data;
	}
		
	public int actionDataBPlan(String action, String values, String products, String users) {
		int[] price_tag = {0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0};
		
		String[] vl = values.split(";");
		String[] pl = products.split(",");
		int group = Integer.parseInt(vl[1].substring(vl[1].indexOf("=")+1, vl[1].length()));				
		for (int j = 0; j < pl.length; j++) {						
			try {
				Connection con = shared.getConnection();			
				if (action.equals("insert")) {
					String[] ppl = pl[j].split(";");			
					String pcode = ppl[0].substring(ppl[0].indexOf("=")+1, ppl[0].length());
					Float ct = Float.parseFloat(ppl[1].substring(ppl[1].indexOf("=")+1, ppl[1].length()));
					int priceTag = Integer.parseInt(ppl[2].substring(ppl[2].indexOf("=")+1, ppl[2].length()));
					
					String[] us = users.split(",");
					for (int i = 0; i < us.length; i++) {
						PreparedStatement ps1 = con.prepareStatement("SELECT * FROM B_Plan WHERE name=? and userCode=? and productCode=?");
						ps1.setString(1, vl[0].substring(vl[0].indexOf("=")+1, vl[0].length()));
						ps1.setString(2, us[i]);
						ps1.setString(3, pcode);
						ResultSet rs1 = ps1.executeQuery();
						boolean next = rs1.next();
						if (next && ct == 0) {
							PreparedStatement ps = con.prepareStatement("DELETE FROM B_Plan WHERE name=? and userCode=? and productCode=?");														
							ps.setString(1, vl[0].substring(vl[0].indexOf("=")+1, vl[0].length()));
							ps.setString(2, us[i]);
							ps.setString(3, pcode);							
							ps.executeUpdate();								
							ps.close();
						} else
						if (next) {
							PreparedStatement ps = con.prepareStatement("UPDATE B_Plan SET countTheshold=?, amountTheshold=price*? WHERE name=? and userCode=? and productCode=?");							
							ps.setFloat(1, ct);							
							ps.setFloat(2, ct);
							ps.setString(3, vl[0].substring(vl[0].indexOf("=")+1, vl[0].length()));
							ps.setString(4, us[i]);
							ps.setString(5, pcode);							
							ps.executeUpdate();								
							ps.close();
						} else {																	
							PreparedStatement ps = con.prepareStatement("INSERT INTO B_Plan (name,userCode,productCode,price,countTheshold,amountTheshold,_group,startDate,endDate) (SELECT ?,?,?,price,?,price*?,?,?,? FROM Price WHERE productCode='"+pcode+"' and customerType="+price_tag[priceTag]+")");				
							ps.setString(1, vl[0].substring(vl[0].indexOf("=")+1, vl[0].length()));						
							ps.setString(2, us[i]);
							ps.setString(3, pcode);					
							ps.setFloat(4, ct);
							ps.setFloat(5, ct);
							ps.setInt(6, group);
							ps.setDate(7, java.sql.Date.valueOf(vl[2].substring(vl[2].indexOf("=")+1, vl[2].length())));
							ps.setDate(8, java.sql.Date.valueOf(vl[3].substring(vl[3].indexOf("=")+1, vl[3].length())));
							ps.executeUpdate();								
							ps.close();
						}
						rs1.close();
					}
				} else {
					PreparedStatement ps = con.prepareStatement("DELETE FROM B_Plan WHERE name=? and _group=?");				
					ps.setString(1, vl[0].substring(vl[0].indexOf("=")+1, vl[0].length()));							
					ps.setInt(2, Integer.parseInt(vl[1].substring(vl[1].indexOf("=")+1, vl[1].length())));			
					ps.executeUpdate();								
					ps.close();
				}
			} catch (Exception ex) {
				logger._.error(ex.getMessage());
				ex.printStackTrace();
			}
		}		
		return 0;
	}					
	
	public String getLeaseMainInfo() {		
		Collection collect1;				
				
		collect1 = getDataCollector("select descr, SUM(flag) as amount from Sales JOIN User_Type on userType=_group where flag>0 group by userType, descr", "descr,amount", "s,i");		
		
		String data = "{'results':"+(collect1.size()+1)+", 'success': 'true', 'items':[";		
		long total = 0; 
		for (int i = 0; i < collect1.size(); i++) {
			Variant w = (Variant)collect1.elementAt(i);
			total = total + w.getInt("amount");
			data += "{'data':'"+w.get("descr")+"','count':"+w.get("amount")+",'detail':'', 'render': 'renderMMoney'},";
		}
		
		data += "{'data':'Нийт','count':"+total+",'detail':'', 'render': 'renderMMoney'}";		
		data += "]}";
		
		return data;
	}
	
	public String getUserSaleMainInfo(String userCode) {
		if (userCode == null || userCode.length() != 3) return "";
		Collection collect1 = getDataCollector("SELECT firstName,lastName,(select descr from User_Type where _group=Users._group) as type from Users WHERE code='"+userCode+"'", "firstName,lastName,type", "s,s,s");
		Collection collect2 = getDataCollector("SELECT (select routeName from Route WHERE routeId=mon) as mon,(select routeName from Route WHERE routeId=thue) as thue,(select routeName from Route WHERE routeId=wed) as wed from Route_User WHERE userCode='"+userCode+"'", "mon,thue,wed", "s,s,s");
		Collection collect3 = getDataCollector("select sum(amount) as amount from sales where userCode='"+userCode+"' and datePart(month, _dateStamp)=datePart(month, getdate()) and datePart(year, _dateStamp)=datePart(year, getdate())", "amount", "i");
		Collection collect4 = getDataCollector("select sum(amount) as amount from sales where userCode='"+userCode+"' and datePart(wk, _dateStamp)=datePart(wk, getdate()) and datePart(year, _dateStamp)=datePart(year, getdate())", "amount", "i");						
		Collection collect5 = getDataCollector("select sum(flag) as amount from sales where userCode='"+userCode+"'", "amount", "i");
		Collection collect9 = getDataCollector("select name from b_plan where startDate<=DATEADD(dd, 0, DATEDIFF(dd, 0, CURRENT_TIMESTAMP)) and endDate>=DATEADD(dd, 0, DATEDIFF(dd, 0, CURRENT_TIMESTAMP)) and userCode='"+userCode+"'", "name", "s");
		String planName = "1-24";
		if (collect1.size() > 0 && collect9 != null && collect9.size()>0) planName = collect9.elementAt(0).get("name");
		Collection collect6 = getDataCollector("select sum(amountTheshold) as amount,max(startDate) as startDate,max(endDate) as endDate from b_plan where name='"+planName+"' and userCode='"+userCode+"'", "amount,startDate,endDate", "i,s,s"); //(select top 1 name from b_plan where startDate<=getDate() and endDate>=getDate()))
		
		Collection collect7 = getDataCollector("select (sum(amount)-sum(flag)) as omount,(sum(amount)-sum(flag))-(select sum(amountTheshold) from b_plan where userCode='"+userCode+"' and name='"+planName+"') as amount, getdate() as day from sales where userCode='"+userCode+"' and DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))>=(select top 1 startDate from b_plan where name='"+planName+"') and DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))<=(select top 1 endDate from b_plan where name='"+planName+"')", "omount,amount,day", "i,i,s");		
		
		String data = "{'results':"+7+", 'success': 'true', 'items':[";
		data += "{'data':'"+shared.getLanguageWord(373)+"','count':'"+collect1.elementAt(0).get("firstName")+" "+collect1.elementAt(0).get("lastName")+"','detail':''},";
		data += "{'data':'"+shared.getLanguageWord(332)+"','count':'1: "+collect2.elementAt(0).get("mon")+", 2: "+collect2.elementAt(0).get("thue")+", 3: "+collect2.elementAt(0).get("wed")+"','detail':''},";
		data += "{'data':'"+shared.getLanguageWord(333)+"','count':'"+collect3.elementAt(0).get("amount")+"','detail':'"+shared.getLanguageWord(338)+"'},";
		data += "{'data':'"+shared.getLanguageWord(334)+"','count':'"+collect4.elementAt(0).get("amount")+"','detail':'"+shared.getLanguageWord(339)+"'},";
		data += "{'data':'"+shared.getLanguageWord(335)+"','count':'"+collect5.elementAt(0).get("amount")+"','detail':''},";
		data += "{'data':'"+shared.getLanguageWord(336)+"','count':'"+collect6.elementAt(0).get("amount")+"','detail':'"+collect6.elementAt(0).getDate("startDate")+" / "+collect6.elementAt(0).getDate("endDate")+"'},";
		data += "{'data':'"+shared.getLanguageWord(286)+"','count':'"+collect7.elementAt(0).get("omount")+"','detail':''},";
		data += "{'data':'"+shared.getLanguageWord(337)+"','count':'"+collect7.elementAt(0).get("amount")+"','detail':''}";
		
		data += "]}";
		return data;
	}
	
	public String getCustomerSaleMainInfo(String customerCode) {					
		Collection collect1 = getDataCollector("SELECT name,location,(select top 1 descr from User_Type where _group=type) as type,loanMargin,(select top 1 routeName from Route where routeId=subid) as subid, (select MAX(_dateStamp) from Sales where customerCode=code) as active, (select sum(amount) from Sales where customerCode=code) as amount, (select sum(flag) from Sales where customerCode=code) as flag, (select count(distinct isnull(ticketID,0)) from Sales where customerCode=code and (type=4 or type=5))*100/(select count(distinct isnull(ticketID,0)) from Sales where customerCode=code and (type<>4 and type<>5)) as non from Customer WHERE code='"+customerCode+"'", "name,location,type,loanMargin,subid,active,amount,flag,non", "s,s,s,i,s,s,i,i,f");
		Variant w = (Variant)collect1.elementAt(0);
		String data = "{'results':"+7+", 'success': 'true', 'items':[";
		data += "{'data':'Нэр','count':'"+w.get("name")+"','detail':''},";
		data += "{'data':'Байршил','count':'"+w.get("location")+"','detail':''},";		
		data += "{'data':'Төрөл','count':'"+w.get("type")+"','detail':''},";
		data += "{'data':'Чиглэл','count':'"+w.get("subid")+"','detail':''},";
		data += "{'data':'Борлуулагч','count':'','detail':''},";
		data += "{'data':'Төлөв','count':'"+w.get("active")+"','detail':'Хамгийн сүүлийн худалдан авалт хийсэн огноо'},";
		data += "{'data':'Зээлийн лимит','count':'"+w.getInt("loanMargin")+"','detail':''},";
		data += "{'data':'Хаагдаагүй зээлийн дүн','count':'"+w.getInt("flag")+"','detail':''},";
		data += "{'data':'Нийт худалдан авалт','count':'"+w.getInt("amount")+"','detail':''},";
		data += "{'data':'Худалдан авалтын хувь','count':'"+(100-w.getFloat("non"))+"%','detail':'Борлуулалгчийн очилт бүрт худалдан авалт хийж байсан хувь'},";		
		data += "{'data':'Холбоотой зурагууд','count':'<a style=\"color:#3B5998\" href=javascript:ossApp.callImageModule(\""+customerCode+"\")>click view</a>','detail':''}";
		
		data += "]}";
		return data;
	}
	
	public String getSaleMainInfo(String prev, String next, String products, String userCode, String mode) {		
		Collection collect1;
		Collection collect2;
		Collection collect3;		
		Collection collect4;
		Collection collect6;		
		Collection collect5;
		Collection collect8 = new Collection();
		Collection collect9 = new Collection();
		Collection collect7 = getDataCollector("select CURRENT_TIMESTAMP as now", "now", "s");
		

		collect1 = getDataCollector("SELECT type,sum(amount) as amount FROM Sales WHERE amount>0 and _dateStamp>='"+prev+"' and _dateStamp<'"+next+"' mnp GROUP by type ORDER by type", "type,amount", "i,i");
		collect2 = getDataCollector("SELECT count(distinct userCode) as userCount, (select count(code) from users where 1=1 unp ) as totalCount FROM Sales WHERE _dateStamp>='"+prev+"' and _dateStamp<'"+next+"' mnp ", "userCount,totalCount", "i,i");
		collect3 = getDataCollector("select (select firstname from users where code=userCode) as userCode, (select _group from users where code=userCode) as _group, sum(amount) as amount from sales where _dateStamp>='"+prev+"' and _dateStamp<'"+next+"' mnp group by userCode order by sum(amount) desc", "userCode,_group,amount", "s,i,i");
		collect4 = getDataCollector("select top 1 (select top 1 name+'|'+location from Customer where code=customerCode) as customerCode, sum(amount) as amount from sales where _dateStamp>='"+prev+"' and _dateStamp<'"+next+"' mnp group by customerCode order by sum(amount) desc", "customerCode,amount", "s,i");
		collect5 = getDataCollector("select (select firstname from users where code=userCode) as userCode, datediff(ss,max(_dateStamp),CURRENT_TIMESTAMP) as time FROM Sales WHERE _dateStamp>='"+prev+"' and _dateStamp<'"+next+"' mnp group by userCode order by datediff(ss,max(_dateStamp),CURRENT_TIMESTAMP) desc", "userCode,time", "s,i");
		collect6 = getDataCollector("select top 1 (select name from Product where code=productCode) as productCode, sum(amount) as amount from sales where _dateStamp>='"+prev+"' and _dateStamp<'"+next+"' mnp group by productCode order by sum(amount) desc", "productCode,amount", "s,i");
		collect8 = getDataCollector("select sum(amount) as amount from sales where amount<0 and _dateStamp>='"+prev+"' and _dateStamp<'"+next+"' and userCode in (select code from Users where section like '%"+mode+"%') mnp ", "amount", "i");
		collect9 = getDataCollector("select 1 as id, SUM(amount) as amount from Sales where (userCode in (select code from Users where manager='"+userCode+"' or section='"+mode+"')) and DATEPART(YEAR,_dateStamp)=DATEPART(YEAR, CURRENT_TIMESTAMP) and DATEPART(month,_dateStamp)=DATEPART(month, CURRENT_TIMESTAMP) union select 2 as id, SUM(amountTheshold) as amount from B_PLan where DATEPART(YEAR,startDate)=DATEPART(YEAR, CURRENT_TIMESTAMP) and DATEPART(month,startDate)=DATEPART(month, CURRENT_TIMESTAMP) mnp order by id", "id,amount", "i,i");		
		
		String data = "{'results':"+7+", 'success': 'true', 'items':[";		
		
		if (collect1.size() > 0) {
			long total = 0;
			for (int i = 0; i < collect1.size(); i++) {
				if (collect1.elementAt(i).getInt("type")==0 || collect1.elementAt(i).getInt("type")>=10)
					total += collect1.elementAt(i).getLong("amount");
			}
			data += "{'data':'351','count':"+total+",'detail':'<a style=\"color:#3B5998\" href=javascript:ossApp.callModule(\"sale-grid-win\")>["+shared.getLanguageWord(486)+"]</a>', 'render': 'renderLMoney'},";//351
		}
		if (collect1.size() > 0) {
			long rent = 0;
			for (int i = 0; i < collect1.size(); i++) {
				if (collect1.elementAt(i).getInt("type")==1)
					rent += collect1.elementAt(i).getLong("amount");
			}
			data += "{'data':'340','count':"+rent+",'detail':'<a style=\"color:#3B5998\" href=javascript:ossApp.callModule(\"lease-module-win\")>["+shared.getLanguageWord(486)+"]</a>', 'render': 'renderMoney'},";//340
		}
		 
		long promototal = 0;
		if (collect8.size() > 0) {
			for (int i = 0; i < collect8.size(); i++) {			
				promototal += collect8.elementAt(i).getLong("amount");
			}
			//if (total != 0)
				data += "{'data':'658','count':"+Math.abs(promototal)+",'detail':'<a style=\"color:#3B5998\" href=javascript:ossApp.callPromoModule()>["+shared.getLanguageWord(486)+"]</a>', 'render': 'renderMoney'},";//354 
		}
		   
		
		if (collect1.size() > 0) {
			long rent = 0;
			for (int i = 0; i < collect1.size(); i++) {
				if (collect1.elementAt(i).getInt("type")==3)
					rent += collect1.elementAt(i).getLong("amount");
			}
			data += "{'data':'311','count':"+rent+",'detail':'', 'render': 'renderMoney'},";//311
		}
		
		if (collect1.size() > 0) {
			long total = 0;
			for (int i = 0; i < collect1.size(); i++) {
				if ((collect1.elementAt(i).getInt("type")==0 || collect1.elementAt(i).getInt("type")>=10) || (collect1.elementAt(i).getInt("type")==3))
				total += collect1.elementAt(i).getLong("amount");
			}
			total -= Math.abs(promototal);
			data += "{'data':'353','count':"+total+",'detail':'"+shared.getLanguageWord(351)+"+"+shared.getLanguageWord(311)+"-"+shared.getLanguageWord(658)+"', 'render': 'renderLMoney'},";//353
		}
		
		if (collect1.size() > 0) {
			long total = 0;
			for (int i = 0; i < collect1.size(); i++) {			
				total += collect1.elementAt(i).getLong("amount");
			}
			data += "{'data':'354','count':"+total+",'detail':'"+shared.getLanguageWord(351)+"+"+shared.getLanguageWord(598)+"+"+shared.getLanguageWord(311)+"', 'render': 'renderMoney'},";//354 
		}

		if (collect9.size() > 0) {
			long total = collect9.elementAt(0).getLong("amount");
			long ptotal = collect9.elementAt(1).getLong("amount");
			float precent = 0;
			if (ptotal > 0)
			  precent = total*100/ptotal;
			
			data += "{'data':'285','count':"+ptotal+",'detail':'"+shared.getLanguageWord(286)+": <b>"+precent+" %</b>', 'render': 'renderMoney'},";//354
		}
		
		data += "{'data':'355','count':"+collect2.elementAt(0).getInt("userCount")+",'detail':'"+shared.getLanguageWord(599) +" <b>"+collect2.elementAt(0).getInt("totalCount")+"</b>', 'render': 'renderLNumber'},";//355 
		if (collect3.size() > 0)
			data += "{'data':'356','count':"+collect3.elementAt(0).getInt("amount")+",'detail':'"+collect3.elementAt(0).get("userCode")+"', 'render': 'renderMoney'},";//356
		
		if (collect3.size() > 1) {
			for (int i = collect3.size()-1;  i>=0; i--) {
				if (collect3.elementAt(i).getInt("_group") == 1) {					
					data += "{'data':'357','count':"+collect3.elementAt(i).getInt("amount")+",'detail':'"+collect3.elementAt(i).get("userCode")+" ("+shared.getLanguageWord(316)+")', 'render': 'renderMoney'},";//357
					break;
				}
			}
		}		
		
		if (collect5.size() > 1)
			data += "{'data':'358','count':"+0+",'detail':'"+collect5.elementAt(0).getInt("time")/3600+":"+(collect5.elementAt(0).getInt("time")%3600/60<10?"0"+collect5.elementAt(0).getInt("time")%3600/60:collect5.elementAt(0).getInt("time")%3600/60)+" минут ("+(collect5.elementAt(0).get("userCode"))+")'},";//358
		if (collect4.size() > 0)
			data += "{'data':'359','count':"+collect4.elementAt(0).getLong("amount")+",'detail':'"+collect4.elementAt(0).get("customerCode")+"', 'render': 'renderMoney'},";//359
		if (collect6.size() > 0)
			data += "{'data':'360','count':"+collect6.elementAt(0).getLong("amount")+",'detail':'"+collect6.elementAt(0).get("productCode")+"', 'render': 'renderMoney'},";//360
		if (collect7.size() > 0)
			data += "{'data':'361','count':"+0+",'detail':'"+collect7.elementAt(0).get("now")+"'}";//361
		
		data += "]}";
		
		return data;
	}
	
	public String getProductLastCount() {				
		Collection collect = getDataCollector("select name,_count,_count*(select top 1 price from Price where productCode=code and customerType=1) as amount from Storage join Product on code=productCode where wareHouseID=3 order by class", "name,_count,amount", "s,i,i");
				
		String data = "{'results':"+collect.size()+", 'success': 'true', 'items':[";
		
		if (collect.size() > 0) {			
			for (int i = 0; i < collect.size(); i++) {
				Variant w = (Variant)collect.elementAt(i);
				data += "{'data':'"+w.get("name")+"','count':"+w.getInt("_count")+",'detail':'"+w.getInt("amount")+"','render': 'renderMoney'},";
			}
		}
		data = data.substring(0, data.length()-1);
		data += "]}";
		
		return data;
	}
	
	public String getUsersSaleData() {				
		Collection collect = getDataCollector("select firstName, count(distinct customerCode) as customerCount, SUM(amount) as amount from Sales join Users on code=userCode where _dateStamp>=DATEADD(dd, 0, DATEDIFF(dd, 0, CURRENT_TIMESTAMP)) group by userCode,firstName", "firstName,customerCount,amount", "s,i,i");
				
		String data = "{'results':"+collect.size()+", 'success': 'true', 'items':[";
		
		if (collect.size() > 0) {			
			for (int i = 0; i < collect.size(); i++) {
				Variant w = (Variant)collect.elementAt(i);
				data += "{'data':'"+w.get("firstName")+"','count':"+w.getInt("amount")+",'detail':'"+w.getInt("customerCount")+" харилцагчаар орсон', 'render':'renderMoney'},";
			}
		}
		data = data.substring(0, data.length()-1);
		data += "]}";
		
		return data;
	}
	
	public float getLeaseAmount(String year, String month, String userCode) {
		float total = 0;
		try {
			Connection con = shared.getConnection();
			String query = "";			
			if (userCode != null && userCode.length() >= 3 && !userCode.equals("null"))
				query = "select SUM(amount) from Sales as b where userCode='"+userCode+"' and type=1 and _dateStamp<='"+year+"/"+month+"/1'";
			else
				query = "select SUM(amount) from Sales as b where type=1 and _dateStamp<='"+year+"/"+month+"/1'";
			
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			while (rs.next()) {
				total += rs.getFloat(1);
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}				
		
		return total;
	}
	
	public float getPaymentAmount(String year, String month, String userCode) {
		float total = 0;
		try {
			Connection con = shared.getConnection();
			String query = "";			
			if (userCode != null && userCode.length() >= 3 && !userCode.equals("null"))
				query = "select sum(amount) from Sales where userCode='"+userCode+"' and type=3 and _dateStamp<='"+year+"/"+month+"/1'";
			else
				query = "select SUM(amount) from Sales as b where type=3 and _dateStamp<='"+year+"/"+month+"/1'";
			
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			while (rs.next()) {
				total += rs.getFloat(1);
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}				
		
		return total;
	}

	public float getLeaseAmountCustomer(String year, String month, String discount) {
		float total = 0;
		try {
			Connection con = shared.getConnection();
			String query = "";			
			if (discount != null && discount.length() >= 3 && !discount.equals("null"))
				query = "select SUM(amount) from Sales as b where discount="+discount+" and type=1 and _dateStamp<='"+year+"/"+month+"/1'";
			else
				query = "select SUM(amount) from Sales as b where discount>100000 and type=1 and _dateStamp<='"+year+"/"+month+"/1'";
						
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			while (rs.next()) {
				total += rs.getFloat(1);
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}				
		
		return total;
	}
	
	public float getPaymentAmountCustomer(String year, String month, String discount) {
		float total = 0;
		try {
			Connection con = shared.getConnection();
			String query = "";			
			if (discount != null && discount.length() >= 3 && !discount.equals("null"))
				query = "select sum(amount) from Sales where discount="+discount+" and type=3 and _dateStamp<='"+year+"/"+month+"/1'";
			else
				query = "select SUM(amount) from Sales as b where discount>100000 and type=3 and _dateStamp<='"+year+"/"+month+"/1'";
			
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			while (rs.next()) {
				total += rs.getFloat(1);
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}				
		
		return total;
	}		
	
	public String getLeaseMain(String year, String month, String userCode) {				
		LinkedList<UserData> sales = new LinkedList<UserData>();
		try {
			Connection con = shared.getConnection();
			String query = "";						
			if (userCode != null && userCode.length() >= 3 && !userCode.equals("null"))
				query = "select DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp)), productCode, SUM(quantity), SUM(amount), 0 from Sales as b where userCode='"+userCode+"' and type=1 and DATEPART(year, _dateStamp)="+year+" and DATEPART(month, _dateStamp)="+month+" group by DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp)), productCode union select DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp)), '', 0, 0, sum(amount) from Sales where userCode='"+userCode+"' and type=3 and DATEPART(year, _dateStamp)="+year+" and DATEPART(month, _dateStamp)="+month+" mnp group by DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp)) order by DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))";
			else
				query = "select DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp)), productCode, SUM(quantity), SUM(amount), 0 from Sales as b where type=1 and DATEPART(year, _dateStamp)="+year+" and DATEPART(month, _dateStamp)="+month+" group by DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp)), productCode union select DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp)), '', 0, 0, sum(amount) from Sales where type=3 and DATEPART(year, _dateStamp)="+year+" and DATEPART(month, _dateStamp)="+month+" mnp group by DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp)) order by DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))";
			
			query = npString(query, "");
			
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			UserData sale = new UserData();
			String active = "";			
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) {						
						sales.add(sale);
						sale = new UserData();
					}
					active = rs.getString(1);					
					sale.data = active;					
				}				
				sale.products.add(rs.getString(2));
				sale.values1.add(rs.getFloat(3));				
				sale.amounts1.add(rs.getFloat(4));
				sale.amounts2.add(rs.getFloat(5));
			}
			if (active.length() > 0)
				sales.add(sale);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		float first_total = getLeaseAmount(year, month, userCode)-getPaymentAmount(year, month, userCode);
		String data = "{'results':"+(sales.size()+1)+", 'success': 'true', 'items':[";
		data += "{'day':'Эхний үлд','sum_a':"+first_total+",'sum_l':"+first_total+"},";				
		for (int i = 0; i < sales.size(); i ++) {
			UserData sale = sales.get(i);
			data += "{'day':'"+sale.data.substring(0, 10)+"',";			
			float sum_quantity = 0, sum_amount = 0, sum_t_amount = 0;
			for (int j = 0; j < sale.products.size(); j++) {
				sum_quantity += sale.values1.get(j);
				sum_amount += sale.amounts1.get(j);
				sum_t_amount += sale.amounts2.get(j);				
				data += "'"+sale.products.get(j)+"':"+(sale.values1.get(j))+",";				
			}												
			first_total=first_total+sum_amount-sum_t_amount;
			data += "'sum_a':"+sum_amount+",'sum_r':"+sum_t_amount+",'sum_l':"+first_total+"},";			
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";	
		
		
		return data;
	}
	
	public String getLeaseCustomer(String year, String month, String discount) {				
		LinkedList<UserData> sales = new LinkedList<UserData>();
		try {
			Connection con = shared.getConnection();
			String query = "";									
			if (discount != null && discount.length() >= 3 && !discount.equals("null"))
				query = "select name, productCode, SUM(quantity), SUM(amount), 0, customerCode from Sales JOIN Customer on code=customerCode where Sales.type=1 and DATEPART(year, _dateStamp)="+year+" and DATEPART(month, _dateStamp)="+month+" and Sales.discount="+discount+" group by name,customerCode, productCode union select name, '', 0, 0, sum(amount), customerCode from Sales JOIN Customer on code=customerCode  where Sales.type=3 and DATEPART(year, _dateStamp)="+year+"  and DATEPART(month, _dateStamp)="+month+" and Sales.discount="+discount+" group by name,customerCode order by name";
			else
				query = "select name, productCode, SUM(quantity), SUM(amount), 0, '' from Sales JOIN Parent_Names on parent_names.id=discount where type=1 and DATEPART(year, _dateStamp)="+year+" and DATEPART(month, _dateStamp)="+month+" and discount>100000 group by name,discount,productCode union select name, '', 0, 0, sum(amount), '' from Sales JOIN Parent_Names on parent_names.id=discount where type=3 and DATEPART(year, _dateStamp)="+year+" and DATEPART(month, _dateStamp)="+month+" and discount>100000 group by name,discount order by name";
			
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			UserData sale = new UserData();
			String active = "";			
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) {						
						sales.add(sale);
						sale = new UserData();
					}
					active = rs.getString(1);					
					sale.data = active;					
				}				
				sale.products.add(rs.getString(2));
				sale.values1.add(rs.getFloat(3));				
				sale.amounts1.add(rs.getFloat(4));
				sale.amounts2.add(rs.getFloat(5));
				sale.data1 = rs.getString(6);
			}
			if (active.length() > 0)
				sales.add(sale);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		float first_total = getLeaseAmountCustomer(year, month, discount)-getPaymentAmountCustomer(year, month, discount);
		String data = "{'results':"+(sales.size()+1)+", 'success': 'true', 'items':[";
		data += "{'day':'Эхний үлд','sum_a':"+first_total+",'sum_l':"+first_total+"},";				
		for (int i = 0; i < sales.size(); i ++) {
			UserData sale = sales.get(i);
			data += "{'day':'"+sale.data+"',";			
			float sum_quantity = 0, sum_amount = 0, sum_t_amount = 0;
			for (int j = 0; j < sale.products.size(); j++) {
				sum_quantity += sale.values1.get(j);
				sum_amount += sale.amounts1.get(j);
				sum_t_amount += sale.amounts2.get(j);				
				data += "'"+sale.products.get(j)+"':"+(sale.values1.get(j))+",";							
			}									
			first_total=first_total+sum_amount-sum_t_amount;
			data += "'sum_a':"+sum_amount+",'sum_r':"+sum_t_amount+",'sum_l':"+first_total+",'data':'"+sale.data1+"'},";			
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";	
		
		
		return data;
	}		
	
	public String getOrderAfterHandSale(String userCode) {		
		Collection collect1 = getDataCollector("select productCode, (select top 1 lastcount from orders where userCode=b.userCode and productCode=b.productCode and flag=0 and (inCount>0 or soldCount>0) order by id desc) as quantity, (select price from price where customerType=(select price_tag from user_type where _group=(select _group from users where code=userCode)) and productCode=b.productCode) as price from orders as b where userCode='"+userCode+"' group by userCode,productCode", "productCode,quantity,price", "s,i,f");		
		
		
		String data = "{'results':"+collect1.size()+", 'success': 'true', 'items':[";
		
		for (int i = 0; i < collect1.size(); i++) {
			Variant v = collect1.elementAt(i);
			data += "{'productCode':'"+v.get("productCode")+"','quantity':"+v.get("quantity")+",'price':"+v.get("price")+",'total':"+v.getInt("quantity")*v.getFloat("price")+"},";
		}
		
		if (collect1.size() > 0)
			data = data.substring(0, data.length() - 1);
		
		data += "]}";
		
		return data;
	}				
    		
	public String getReport() {				
		LinkedList<CrossData> sales = new LinkedList<CrossData>();			
		try {								
			Connection con = shared.getConnection();
			
			PreparedStatement ps = con.prepareStatement("SELECT userCode FROM Route_User");
			ResultSet rs = ps.executeQuery();
			int f = 1;
			while (rs.next()) {
				String code = rs.getString(1);
				f = 1;
				if (code.startsWith("14")) f = 3;
				
				String[] days = {"mon", "thue", "wed"};
				String[] daysName = {"1,4", "2,5", "3,6"};
				for (int i = 0; i < days.length; i++) {
					PreparedStatement ps1 = con.prepareStatement("select COUNT(*) from route_customer where routeID=(select top 1 "+days[i]+" from route_user where userCode='"+code+"')");
					ResultSet rs1 = ps1.executeQuery();
					int total = 0;
					if (rs1.next()) { //total
						PreparedStatement psu1 = con.prepareStatement("UPDATE ek SET total=? where route='"+daysName[i]+"' and userCode='"+code+"'");
						total = rs1.getInt(1)/f;
						psu1.setInt(1, total);
						int r = psu1.executeUpdate();
						psu1.close();
						       
						if (r == 0) {
							PreparedStatement psu2 = con.prepareStatement("INSERT INTO ek (userCode,route,total,active,not_active) values (?,?,?,0,0)");							
							psu2.setString(1, code);
							psu2.setString(2, daysName[i]);
							psu2.setInt(3, total);							
							psu2.executeUpdate();
							psu2.close();
						}
					}
					
					rs1.close();
					ps1.close();
					
					PreparedStatement ps2 = con.prepareStatement("select COUNT(*) from route_customer where routeID=(select top 1 "+days[i]+" from route_user where userCode='"+code+"') and customerCode in (select customerCode from Sales where _dateStamp>='2012/1/1')");
					ResultSet rs2 = ps2.executeQuery();
					int active = 0;
					if (rs2.next()) { //total
						PreparedStatement psu2 = con.prepareStatement("UPDATE ek SET active=?, not_active=? where route='"+daysName[i]+"' and userCode='"+code+"'");
						active = rs2.getInt(1)/f;
						psu2.setInt(1, active);
						psu2.setInt(2, total-active);
						int r = psu2.executeUpdate();
						psu2.close();												
					}
					rs2.close();
					ps2.close();
				}
			}
			rs.close();
			ps.close();
			
			
			ps = con.prepareStatement("SELECT userCode,route,total,active,not_active FROM ek ORDER by userCode, route");			
			rs = ps.executeQuery();					
			CrossData sale = new CrossData();
			String active = "";			
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) { 
						sales.add(sale);
						sale = new CrossData();
					}
					active = rs.getString(1);
					sale.userCode = rs.getString(1);					
				}				
				sale.products.add(rs.getString(2));
				sale.values.add(rs.getFloat(3));
				sale.amounts.add(rs.getFloat(4));
				sale.rents.add(rs.getFloat(5));		
			}
			if (active.length() > 0)
				sales.add(sale);
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
		for (int i = 0; i < sales.size(); i ++) {
			CrossData sale = sales.get(i);
			data += "{'userCode':'"+sale.userCode+"',";
			int [] ar = {14,25,36};
			int total = 0, atotal = 0, stotal = 0;
			for (int j = 0; j < 3; j++) {
				data += "'total"+ar[j]+"':"+sale.values.get(j)+"," +
				"'active"+ar[j]+"':"+sale.amounts.get(j)+",'not_active"+ar[j]+"':"+sale.rents.get(j)+",";
				total += sale.values.get(j);
				atotal += sale.amounts.get(j);
				stotal += sale.rents.get(j);
			}	
			
			data += "'sum':"+total+","+"'asum':"+atotal+",'no_sum':"+stotal+"";			
			data += "},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";			
		
		return data;
	}
	
	public String storageReport(String d1, String d2, String productCode, int wareHouseID) {				
		LinkedList<UserData> sales = new LinkedList<UserData>();
		try {
			Connection con = shared.getConnection();
			PreparedStatement ps = con.prepareStatement("select N'<b>Эхний үлдэгдэл</b>' as userCode, productCode, _count+(select isnull(sum(count),0) from Storage_in where DATEADD(dd, 0, DATEDIFF(dd, 0, _date))>='"+d1+"' and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))<='"+d2+"' and wareHouseID="+wareHouseID+" and userCode='000' and _count>0 and productCode=st.productCode), 0 from Storage as st where wareHouseID="+wareHouseID+" UNION ALL select userCode, productCode, 0, SUM(inCount) from Orders where DATEADD(dd, 0, DATEDIFF(dd, 0, _date))>='"+d1+"' and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))<='"+d2+"' group by userCode, productCode UNION ALL select (select N'a.<b>Орлого ['+name+']</b>' from Ware_House where wareHouseID="+wareHouseID+"), productCode, SUM(abs(count)), 0 from Storage_In where count<0 and userCode<>'000' and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))>='"+d1+"' and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))<='"+d2+"' and wareHouseID="+wareHouseID+" group by productCode UNION ALL select N'b.<b>Эцсийн үлдэгдэл</b>',productCode,_count,0 from Storage where wareHouseID="+wareHouseID+" order by userCode");
			ResultSet rs = ps.executeQuery();
			UserData sale = new UserData();
			String active = "";
			while (rs.next()) {				
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) { 
						sales.add(sale);
						sale = new UserData();
					}
					active = rs.getString(1);					
					sale.data = active;						
				}
				sale.products.add(rs.getString(2));
				sale.values1.add(rs.getFloat(3));
				sale.values2.add(rs.getFloat(4));
			}
			if (active.length() > 0)
				sales.add(sale);
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
							
		for (int i = 0; i < sales.size(); i ++) {
			UserData sale = sales.get(i);
			data += "{'userCode':'"+sale.data+"',";
						
			for (int j = 0; j < sale.products.size(); j++) {
				String pcode = (String)sale.products.get(j);
				float in = sale.values1.get(j);
				float out = sale.values2.get(j);
				data += "'"+pcode+"_in':"+in+",'"+pcode+"_out':"+out+",";
			}
			data = data.substring(0, data.length() - 1);
			data += "},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);		
				
		data += "]}";
		
		return data;
	}

	public String storageOutReport(String d1, int wareHouseID, String unit) {				
		LinkedList<UserData> sales = new LinkedList<UserData>();
		try {
			Connection con = shared.getConnection();
			String query = "select code, (select _count from Storage where wareHouseID="+wareHouseID+" and productCode=Product.code)/max(unit) as firstCount,(select sum(abs(count)) from Storage_in where count<0 and wareHouseID="+wareHouseID+" and productCode=Product.code and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))>='"+d1+"')/max(unit) as addCount,userCode, SUM(confirmedCount)/max(unit) as getCount from Orders RIGHT JOIN Product on code=productCode and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))>='"+d1+"' and Orders.wareHouseID="+wareHouseID+" where unit_type=1 and vendor='"+mode+"' and isSale=1 group by code, productCode, userCode order by code,productCode,userCode";
			if (unit.equals("0"))
				query = "select code, (select _count from Storage where wareHouseID="+wareHouseID+" and productCode=Product.code) as firstCount,(select sum(abs(count)) from Storage_in where count<0 and wareHouseID="+wareHouseID+" and productCode=Product.code and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))>='"+d1+"') as addCount,userCode, SUM(confirmedCount) as getCount from Orders RIGHT JOIN Product on code=productCode and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))>='"+d1+"' and Orders.wareHouseID="+wareHouseID+" where unit_type=0 and vendor='"+mode+"' and isSale=1 group by code, productCode, userCode order by code,productCode,userCode";
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			UserData sale = new UserData();
			String active = "";
			while (rs.next()) {				
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) { 
						sales.add(sale);
						sale = new UserData();
					}
					active = rs.getString(1);					
					sale.data = active;						
				}
				sale.products.add(rs.getString(4));
				sale.values1.add(rs.getFloat(2));
				sale.values2.add(rs.getFloat(3));
				sale.values3.add(rs.getFloat(5));
			}
			if (active.length() > 0)
				sales.add(sale);
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
							
		for (int i = 0; i < sales.size(); i ++) {
			UserData sale = sales.get(i);
			data += "{'productCode':'"+sale.data+"','firstCount':@,'addCount':#,";
			float total = 0, add = 0, first = 0;
			for (int j = 0; j < sale.products.size(); j++) {
				String pcode = (String)sale.products.get(j);
				float in = sale.values3.get(j);
				total += in;
				first = sale.values1.get(j);
				add = sale.values2.get(j);
				data += "'"+pcode+"':"+in+",";
			}
			data = data.replaceAll("@", (first-add+total)+"");
			data = data.replaceAll("#", (add)+"");
			data += "'lastCount':"+(first)+"},";
		}
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);		
				
		data += "]}";
		
		return data;
	}
	
	
	//************************** Tusgai daalgavartai holbootoi *******************************************//
	public String productCountReport(String d1, String d2, String userCode) {				
		LinkedList<UserData> sales = new LinkedList<UserData>();
		try {
			Connection con = shared.getConnection();
			PreparedStatement ps = con.prepareStatement("select customerCode,productCode, _count, amount from Product_count where userCode='"+userCode+"' and _date>='"+d1+"' and _date<='"+d2+"' order by customerCode,productCode");
			ResultSet rs = ps.executeQuery();
			UserData sale = new UserData();
			String active = "";
			while (rs.next()) {				
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) { 
						sales.add(sale);
						sale = new UserData();
					}
					active = rs.getString(1);					
					sale.data = active;						
				}
				sale.products.add(rs.getString(2));
				sale.values1.add(rs.getFloat(3));
				sale.values2.add(rs.getFloat(4));
			}
			if (active.length() > 0)
				sales.add(sale);
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
							
		for (int i = 0; i < sales.size(); i ++) {
			UserData sale = sales.get(i);
			data += "{'customerCode':'"+sale.data+"',";
						
			for (int j = 0; j < sale.products.size(); j++) {
				String pcode = (String)sale.products.get(j);
				float in = sale.values1.get(j);
				float out = sale.values2.get(j);
				data += "'"+pcode+"_q':"+in+",'"+pcode+"_a':"+out+",";
			}
			data = data.substring(0, data.length() - 1);
			data += "},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);		
				
		data += "]}";
		
		return data;
	}		
	
	
	//***************************** Tailantai holbootoi ***********************************//
	public String jsonCustomerSalesDetailByUser(String date1, String date2, String userCode) {			
		LinkedList<UserData> sales = new LinkedList<UserData>();
		try {
			Connection con = shared.getConnection();
			String query = "";			
			query = "select customerCode, productCode, type, SUM(quantity), SUM(amount) from Sales where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))>='"+date1+"' and DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))<='"+date2+"' and userCode='"+userCode+"' group by customerCode, productCode, type";
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			UserData sale = new UserData();
			String active = "";
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) {						
						sales.add(sale);
						sale = new UserData();
					}					
					active = rs.getString(1);	
					sale.data = active;
				}				
				String p = rs.getString(2);
				if (p.equals("nul")) p = "0";
				sale.products.add(p);
				if (p.equals("0")) {
					sale.v1[0] += rs.getFloat(5);
				}
				else {	
					sale.cc0.put(p+"1"+rs.getInt("type"), rs.getFloat(4));
					sale.cc0.put(p+"2"+rs.getInt("type"), rs.getFloat(5));
				}
			}
			if (active.length() > 0)
				sales.add(sale);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
		for (int i = 0; i < sales.size(); i ++) {
			UserData sale = sales.get(i);
			Collection c = getDataCollector("select COUNT(distinct ticketID) as entry, (select top 1 code from ItemsTransaction where customerCode='"+sale.data+"') itemID from Sales where Sales.customerCode='"+sale.data+"' and userCode='"+userCode+"' and DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))>='"+date1+"' and DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))<='"+date2+"'", "entry,itemID", "i,s");
			int entry = 0;
			String itemID = "0";
			if (c.size() > 0) { entry = c.elementAt(0).getInt("entry"); itemID = c.elementAt(0).get("itemID").length() == 0?"0":"1";} 
			data += "{'customerCode':'"+sale.data+"','entry':"+entry+",'itemID':"+itemID+",";			
			float []sums = new float[4];
			for (int j = 0; j < sale.products.size(); j++) {
				float v = (float)sale.getHashFloat(sale.products.get(j)+"10")+(float)sale.getHashFloat(sale.products.get(j)+"11");							
				data += "'"+sale.products.get(j)+"':"+(v)+",";
				sums[0] += (float)sale.getHashFloat(sale.products.get(j)+"20");//sale.cc0[Integer.parseInt(sale.products.get(j))][0];
				sums[1] += (float)sale.getHashFloat(sale.products.get(j)+"21");// [Integer.parseInt(sale.products.get(j))][1];
				
				sums[2] = sale.v1[0];
				sums[3] += (float)sale.getHashFloat(sale.products.get(j)+"23"); //sale.cc2[Integer.parseInt(sale.products.get(j))][3];
			}
			
			data += "'sum_p':"+sums[2]+",'sum_r':"+sums[3]+",'sum_ar':"+sums[1]+",'sum_a':"+sums[0]+"},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";	
		shared.cacheMan.saveAlternateCache("_report_by_customer", data);
		
		return data;
	}		
	
	public String jsonCustomerSalesDrillDetailByUser(String date1, String date2, String customerCode) {			
		LinkedList<UserData> sales = new LinkedList<UserData>();
		try {
			Connection con = shared.getConnection();
			String query = "";			
			query = "select LEFT(CONVERT(VARCHAR, _dateStamp, 120), 10), productCode, type, sum(quantity), SUM(amount) from Sales where DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))>='"+date1+"' and DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))<='"+date2+"' and customerCode='"+customerCode+"' and type<3 group by LEFT(CONVERT(VARCHAR, _dateStamp, 120), 10), productCode, type";
			
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			UserData sale = new UserData();
			String active = "";
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) {
						sales.add(sale);
						sale = new UserData();
					}
					active = rs.getString(1);	
					sale.data = active;
				}				
				String p = rs.getString(2);
				if (p.equals("nul")) p = "0";
				sale.products.add(p);
				if (p.equals("0")) {
					sale.v1[0] = rs.getFloat(5);
				}
				else {	
					sale.cc1[Integer.parseInt(p)][rs.getInt("type")] = rs.getFloat(4);
					sale.cc2[Integer.parseInt(p)][rs.getInt("type")] = rs.getFloat(5);
				}
			}
			if (active.length() > 0)
				sales.add(sale);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
		
		List<String> list = obtenerFechasDiariasIntervalo(java.sql.Date.valueOf(date1), java.sql.Date.valueOf(date2));
		
		for (int i = 0; i < list.size(); i++) {
			boolean b = false;
			for (int k = 0; k < sales.size(); k++) {
				UserData sale = sales.get(k); 
				if (sale.data.equals(list.get(i))) {
					data += "{'data':'"+sale.data+"',";
					float[] sums = new float[5];
					for (int j = 0; j < sale.products.size(); j++) {
						float v = sale.cc1[Integer.parseInt(sale.products.get(j))][0]+sale.cc1[Integer.parseInt(sale.products.get(j))][1];
						data += "'"+sale.products.get(j)+"':"+(v)+",";
						sums[0] += sale.cc2[Integer.parseInt(sale.products.get(j))][0];
						sums[1] += sale.cc2[Integer.parseInt(sale.products.get(j))][1];
						sums[2] = sale.v1[0];
						sums[3] += sale.cc2[Integer.parseInt(sale.products.get(j))][3];
					}		
					
					data += "'sum_p':"+sums[2]+",'sum_r':"+sums[3]+",'sum_ar':"+sums[1]+",'sum_a':"+sums[0]+"},";
					b = true;
					break;
				}
			}
			
			if (!b) {
				data += "{'data':'"+list.get(i)+"','sum_r':0,'sum_ar':0,'sum_a':0},";
			}
		}
		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";	
				
		return data;
	}
	
	public String jsonUserEntryToCustomer(String year, String month) {			
		LinkedList<UserData> sales = new LinkedList<UserData>();
		try {
			Connection con = shared.getConnection();
			String query = "";			
			query = "select userCode,DATEPART(day, _dateStamp), count(distinct customerCode), (select COUNT(distinct customerCode) from Sales where DATEPART(year, _dateStamp)="+year+" and DATEPART(month, _dateStamp)="+month+" and DATEPART(day, _dateStamp)=DATEPART(day, b._dateStamp) and userCode=b.userCode and amount>0), (select COUNT(customerCode) from Route_Customer where routeID=(select top 1 case DATEPART(DW, _dateStamp)-1 when 1 then mon when 2 then thue when 3 then wed when 4 then thur when 5 then fri when 6 then sat else '' end from Route_User where userCode=b.userCode)) from Sales as b where userType<=5 and DATEPART(year, _dateStamp)="+year+" and DATEPART(month, _dateStamp)="+month+" group by userCode, DATEPART(day, _dateStamp), DATEPART(dw, _dateStamp) ORDER by userCode,DATEPART(day, _dateStamp)";			
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			UserData sale = new UserData();
			String active = "";			
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) {						
						sales.add(sale);
						sale = new UserData();
					}
					active = rs.getString(1);					
					sale.data = active;										
				}				
				sale.products.add(rs.getString(2));
				sale.values1.add(rs.getFloat(3));
				sale.values2.add(rs.getFloat(4));
				sale.values3.add(rs.getFloat(5));
			}
			if (active.length() > 0)
				sales.add(sale);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		String data = "{'results':"+sales.size()+", 'success': 'true', 'items':[";
		for (int i = 0; i < sales.size(); i ++) {
			UserData sale = sales.get(i);					
			data += "{'userCode':'"+sale.data+"',";			
			float total1 = 0, total2 = 0, total3 = 0;
			for (int j = 0; j < sale.products.size(); j++) {										
				data += "'entry"+sale.products.get(j)+"':"+(Math.min(sale.values1.get(j), sale.values3.get(j)))+",";
				data += "'sale"+sale.products.get(j)+"':"+(Math.min(sale.values1.get(j), sale.values2.get(j)))+",";
				data += "'noentry"+sale.products.get(j)+"':"+Math.max(0, sale.values3.get(j)-sale.values1.get(j))+",";
				data += "'total"+sale.products.get(j)+"':"+(sale.values3.get(j))+",";
				total1 += Math.min(sale.values1.get(j), sale.values3.get(j));
				total2 += (Math.min(sale.values1.get(j), sale.values2.get(j)));
				total3 += sale.values3.get(j);
			}								
			
			data += "'total_1':"+total1+",'total_2':"+total2+",'total_3':"+total3+"},";
		}		
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";	
		
		return data;
	}
		
	public String getPromotionReport(String mode, String d1, String d2) {
		String result = "";
		String query = "select ticketID,userType from Sales where amount<0 and DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))>='"+d1+"' and DATEADD(dd, 0, DATEDIFF(dd, 0, _dateStamp))<='"+d2+"' mnp ";
		query = npString(query, "");
		Collection collection = getDataCollector(query, "ticketID,userType", "i,i");
		float[][][] data = new float[1000][50][3];
		String pnames = "", unames = "";
		for (int i = 0; i < collection.size(); i++) {
			Variant w = (Variant)collection.elementAt(i);
			query = "select userCode,Sales.productCode as productCode,Sales.quantity as q1,Sales.amount as a1,Promotion.quantity as q2,Promotion.type as t,freeQuantity as fq,groupName as g from Sales left JOIN Promotion on (Sales.productCode=Promotion.productCode or Sales.productCode in (select code from Product where brand like Promotion.brand+'%' and len(Promotion.brand)>0)) and Sales.userType=Promotion.userType where ticketID="+w.getInt("ticketID")+" and Sales.userType="+w.getInt("userType")+" mnp order by Sales.amount";
			query = npString(query, "");
			Collection collection1 = getDataCollector(query, "userCode,productCode,q1,a1,q2,t,fq,g", "s,s,i,f,i,i,i,s");
			float pamount = 0, amount = 0;
			String gnames = "";
			String userCode = "";
			String products = "";
			for (int t = 0; t < collection1.size(); t++) {
				Variant q = (Variant)collection1.elementAt(t);				
				if (q.getFloat("a1") < 0)
					pamount = q.getFloat("a1");
				if (q.getString("g").length() > 0) {
					gnames = q.getString("g");
					
					if (pnames.indexOf(q.getString("g"))==-1) 
						pnames += q.getString("g")+",";
					
					if (products.indexOf(q.getString("productCode"))==-1 && q.getString("g").length()>10) {
						products += q.getString("productCode")+",";
						amount += q.getFloat("a1");
					}
				}								
				
				userCode = q.getString("userCode");
				
				if (unames.indexOf(userCode)==-1) 
					unames += userCode+",";
			}			
			if (collection1.size() > 0 && userCode.length() == 3) {
				data[Integer.parseInt(userCode)][gnames.length()][0] += pamount;
				data[Integer.parseInt(userCode)][gnames.length()][1] += amount;
			}
		}
		String[] uc = unames.split(",");
		String[] pc = pnames.split(",");
		
		result = "{'results':"+uc.length+", 'success': 'true', 'items':[";
	
		for (int i = 0; i < uc.length; i++)
		if (uc[i].length() == 3)
		{
			result += "{'userCode':'"+uc[i]+"',";
			float f1=0, d10=0, x1 = 0;
			for (int j = 0; j < pc.length; j++)
			if (pc[j].length() > 1)
			{
				float f = Math.abs(data[Integer.parseInt(uc[i])][pc[j].length()][0]);
				float d = data[Integer.parseInt(uc[i])][pc[j].length()][1];
				result += "'p"+pc[j].length()+"':"+f+",";
				result += "'s"+pc[j].length()+"':"+d+",";
				result += "'x"+pc[j].length()+"':"+(d==0?0:f*100/d)+",";
				f1+=f;
				d10+=d;
				x1 += (d==0?0:f*100/d);
			}
			
			result += "'ptotal':"+f1+",";
			result += "'stotal':"+d10+",";
			result += "'xtotal':"+x1+"},";						
		}
		result = result.substring(0, result.length() - 1);
		result += "]}";
				
		return result;
	} 
	
	public String actionOrderAcceptData(int ticketID, String customerCode, String userCode, String driver,String outDate) {										
		try {
			Connection con = shared.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT userCode,customerCode,productCode,requestCount,wareHouseID,price,id FROM Orders WHERE ticketID=? and customerCode=? and userCode=? and requestCount>0 and confirmedCount=0");
			ps.setInt(1, ticketID);
			ps.setString(2, customerCode);
			ps.setString(3, userCode);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String values = "s"+rs.getString(1)+",s"+rs.getString(2)+",s"+rs.getString(3)+",i"+rs.getInt(4)+",i"+rs.getInt(5)+",f"+rs.getFloat(6)+",s"+driver+",s"+outDate;				
				actionData("update", "Orders", "userCode,customerCode,productCode,confirmedCount,wareHouseID,price,driver,_outDate", values, " id="+rs.getInt(7));
			}
			rs.close();
			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
		
		return "{'results':5, 'success': 'true', 'items':[]}";
	}
	
public String jsonSalesCurrentOrder(String userCode, String today) {	
		
		LinkedList<PlanData> sales = new LinkedList<PlanData>();
		try {
			Connection con = shared.getConnection();
			PreparedStatement ps;		//and confirmedCount=0
			ps = con.prepareStatement("select firstname,(select SUM(requestCount/unit) from Orders join Product on productCode=code where userCode=p.code and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))='"+today+"') as odriinBorluulalt,(select SUM(amountTheshold) from B_PLan where userCode=p.code and  DATEADD(dd, 0, DATEDIFF(dd, 0,startdate))='"+today+"')/30 as dailyPlan,(select COUNT(code) from Customer where subid=(select case DATENAME(DW, GETDATE()) when 'Monday' then mon when 'Tuesday' then thue when 'Wednesday' then wed when 'Thursday' then thur when 'Friday' then fri when 'Saturday' then sat end from Route_User where userCode=p.code)) as outletsBuying,(100*(select COUNT(distinct customerCode) from Orders where userCode=p.code and DATEADD(dd, 0, DATEDIFF(dd, 0, _date))='"+today+"')/(select COUNT(code) from Customer where subid=(select case DATENAME(DW, GETDATE()) when 'Monday' then mon when 'Tuesday' then thue when 'Wednesday' then wed when 'Thursday' then thur when 'Friday' then fri when 'Saturday' then sat end from Route_User where userCode=p.code))) as buyingPrecent from Users as p where _group=8 and _position=1 and manager is not null and code<>'135' and code<>'136' and manager1='"+userCode+"'");			
			ResultSet rs = ps.executeQuery();									
			while (rs.next()) {
				PlanData sale = new PlanData();
				sale.data = rs.getString(1);				
				sale.value1 = rs.getFloat(2);				
				sale.value2 = rs.getFloat(3);
				sale.value3 = rs.getFloat(4);
				sale.value4 = rs.getFloat(5);				
				sales.add(sale);
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		String data = "{'results':"+(sales.size())+", 'success': 'true', 'items':[";	
		for (int i = 0; i < sales.size(); i++) {
			PlanData sale = (PlanData)sales.get(i);			
			data += "{'firstName':'"+sale.data+"','odriinBorluulalt':"+sale.value1+",'dailyPlan':"+sale.value2+",'outletsBuying':"+sale.value3+",'buyingPrecent':"+sale.value4+"},";			
		}
		data = data.substring(0, data.length() - 1);		
		data += "]}";
				
		return data;
	}
	
	public String jsonSalesLastOrder(String userCode, String today) {	
		
		LinkedList<Data> sales = new LinkedList<Data>();
		try {
			Connection con = shared.getConnection();
			PreparedStatement ps;		//and confirmedCount=0
			ps = con.prepareStatement("select firstname,(select top 1 (select name from Customer where code=customerCode) +' | '+(select location from Customer where code=customerCode) from Orders where userCode=p.code order by _date desc) as lastSale,(select top 1 _date from Orders where userCode=p.code order by _date desc) as lastSalesTimestamp,(SELECT DATEDIFF(MINUTE, (select top 1 _date from orders where userCode=p.code and customerCode<>''+(select top 1 customercode from Orders where userCode=p.code order by _date desc)+'' order by _date desc), (select top 1 _date from orders where userCode=p.code order by _date desc))) as ZahialgaHoorondiinMinute from Users as p where _group=8 and _position=1 and manager is not null and code<>'135' and code<>'136' and manager1='"+userCode+"'");			
			ResultSet rs = ps.executeQuery();									
			while (rs.next()) {
				Data sale = new Data();
				sale.data1 = rs.getString(1);				
				sale.data2 = rs.getString(2);				
				sale.data3 = rs.getString(3);
				sale.intvalue1 = rs.getInt(4);
							
				sales.add(sale);
			}  
			 
			rs.close();
			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		String data = "{'results':"+(sales.size())+", 'success': 'true', 'items':[";	
		for (int i = 0; i < sales.size(); i++) {
			Data sale = (Data)sales.get(i);			
			data += "{'firstName':'"+sale.data1+"','lastSale':'"+sale.data2+"','lastSalesTimestamp':'"+sale.data3+"','ZahialgaHoorondiinMinute':"+sale.intvalue1+"},";			
		}
		data = data.substring(0, data.length() - 1);		  
		data += "]}";
		return data;
	}
	
	public int getWareHouseId(String productCode, String userCode)	{
		int wareHouseID=0;
		try {
			Connection con = shared.getConnection();			
			String query="";
			if(getUserSection(userCode).equals("vitafit,pepsi")){
				query = "SELECT wareHouseID FROM Storage WHERE productCode='"+productCode+"' and wareHouseID<>11 and wareHouseID<>12";
			}
			else{
				query = "SELECT wareHouseID FROM Storage WHERE productCode='"+productCode+"' and wareHouseID=(select wareHouseID from users where code='"+userCode+"')";
			}
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				wareHouseID = rs.getInt("wareHouseID");
			}
			rs.close();
			ps.close();
			return wareHouseID;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return wareHouseID;
	}
	
	public String getUserSection(String userCode)	{
		String loggedUserSection="";
		try {
			Connection con = shared.getConnection();			
			PreparedStatement ps = con.prepareStatement("SELECT section FROM Users WHERE code='"+userCode+"'");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				loggedUserSection = rs.getString("section");
			}
			rs.close();
			ps.close();
			return loggedUserSection;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return loggedUserSection;
	}
	
	public int actionOrderPacketData(String action, String tableName, String fields, String values, String where) {		
		String[] vls = values.split(",");
		fields = "_date,customerCode,userCode,productCode,posX,posY,requestCount,price,wareHouseID,ticketID,packetCode";						
		try {
			Connection con = shared.getConnection();			
			PreparedStatement ps = con.prepareStatement("SELECT Packet.productCode,quantity,Price.price,quantity*Price.price FROM Packet join Price on Packet.productCode=Price.productCode and customerType=1 WHERE code=?");
			ps.setString(1, getString(vls[0]));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int wareHouseID = getWareHouseId(rs.getString(1), getString(vls[2]));
				values = "d"+convertDateTimeToString()+","+vls[3]+","+vls[2]+",s"+rs.getString(1)+","+vls[4]+","+vls[5]+",i"+(rs.getInt(2)*Integer.parseInt(vls[1].substring(1, vls[1].length())))+",f"+rs.getFloat(3)+",i"+wareHouseID+","+vls[7]+","+vls[0];				
				actionData(action, tableName, fields, values, where);
			}
			rs.close();
			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
		
		return 0;
	}	
	
	public String jsonStorageToStorage(String date1, String date2, int wareHouseId) {			
		float[][] cc = new float[5000][20];
		String active = "";
		try {
			Connection con = shared.getConnection();
			String query = "";			
			query = "select productCode,wareHouseID1,sum(count) from Storage_in where _date>='"+date1+"' and _date<'"+date2+"' and wareHouseID="+wareHouseId+" and count<>0 and wareHouseID1>0 group by wareHouseID1,productCode order by productCode";
			System.out.println(query);
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();									
			while (rs.next()) {
				if (active.indexOf(rs.getString(1)+",") == -1)
					active += rs.getString(1)+",";
				cc[Integer.parseInt(rs.getString(1))][rs.getInt(2)] = rs.getInt(3);				
			}
			rs.close();
			ps.close();
		} catch (Exception ex) { 
			ex.printStackTrace();
		}
		String[] ps = active.split(",");
		
		String data = "{'results':"+ps.length+", 'success': 'true', 'items':[";
		
		for (int i = 0; i < ps.length; i ++) {		
			if (ps[i].length() == 0) continue;
			data += "{'productCode':'"+ps[i]+"',";			
			float sums = 0;
			for (int j = 1; j <= 12; j++) {
				float v = cc[Integer.parseInt(ps[i])][j];
				data += "'w"+j+"':"+(v)+",";				
				sums += v;
			}
			
			data += "'sum_packet':"+sums+"},";
		}		
		if (ps.length > 1)
			data = data.substring(0, data.length() - 1);
		data += "]}";				
		return data;
	}
	
	public Hashtable<String, float[][][]> getFullData(String year, String month, String userCode, int type) {
		//float full_data[][][][] = new float[10000][50][12][32];
		Hashtable<String, float[][][]> full_data = new Hashtable<String, float[][][]>();
		
		try {
			Connection con = shared.getConnection();
			String query = "";												
			query = "select discount, DATEPART(year, _dateStamp), DATEPART(month, _dateStamp), DATEPART(day, _dateStamp),SUM(amount) from Sales where DATEPART(year, _dateStamp)="+year+" and DATEPART(month, _dateStamp)="+month+" and type="+type+" and userCode='"+userCode+"' group by discount, DATEPART(year, _dateStamp), DATEPART(month, _dateStamp), DATEPART(day, _dateStamp)";			
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();								
			while (rs.next()) {
				float[][][] data = new float[50][12][32];
				if (full_data.containsKey(rs.getInt(1)+""))
					data = full_data.get(rs.getInt(1)+"");											
				data[rs.getInt(2)-2000][rs.getInt(3)][rs.getInt(4)] = rs.getFloat(5);
				full_data.put(rs.getInt(1)+"", data);				
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return full_data;
	}
	
	public String getLeaseCustomers(String year, String month, String userCode) {
		Hashtable<String, float[][][]> full_data = getFullData(year, month, userCode, 1);
		Hashtable<String, float[][][]> full_data1 = getFullData(year, month, userCode, 3);
		LinkedList<UserData> sales = new LinkedList<UserData>();
		try {
			Connection con = shared.getConnection();
			String query = "";												
			query = "select discount, (select top 1 code from Customer where parentID=Sales.discount) as customerCode, SUM(flag) from Sales where userCode='"+userCode+"' and type=1  group by discount";
			//query = "select discount, (select top 1 code from Customer where parentID=Sales.discount) as customerCode, SUM(flag) from Sales where userCode='"+userCode+"' and type=1 group by discount";
			System.out.println(query);
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();			
			UserData sale = new UserData();
			String active = "";			
			while (rs.next()) {
				if (!active.equals(rs.getString(1))) {
					if (active.length() > 0) {						
						sales.add(sale);
						sale = new UserData();
					}
					active = rs.getString(1);					
					sale.data = active;
					sale.data1 = rs.getString(2);						
				}														
				sale.amounts1.add(rs.getFloat(3));			
			}
			if (active.length() > 0)
				sales.add(sale);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
				
		String data = "{'results':"+(sales.size()+1)+", 'success': 'true', 'items':[";					
		for (int i = 0; i < sales.size(); i ++) {
			UserData sale = sales.get(i);
			data += "{'discount':"+sale.data.substring(0, sale.data.indexOf("."))+",'customerCode':'"+sale.data1+"','first':"+(sale.amounts1.get(0))+",";
			int index = Integer.parseInt(sale.data.substring(0, sale.data.indexOf(".")));
			float fl = 0;
			for (int t = 1; t<=31; t++) {				
				try{
				data += "'z"+t+"':"+full_data.get(index+"")[Integer.parseInt(year)-2000][Integer.parseInt(month)][t]+",";
				fl += full_data.get(index+"")[Integer.parseInt(year)-2000][Integer.parseInt(month)][t];				
				}
				catch (Exception e) {
					// TODO: handle exception
				}				
			}  
			
			data += "'ztotal':"+fl+",";
			fl = 0;
			for (int t = 1; t<=31; t++) {				
				try{
				data += "'t"+t+"':"+full_data1.get(index+"")[Integer.parseInt(year)-2000][Integer.parseInt(month)][t]+",";
				fl += full_data1.get(index+"")[Integer.parseInt(year)-2000][Integer.parseInt(month)][t];
				}
				catch (Exception e) {
					// TODO: handle exception
				}   
			}
			
			data += "'ttotal':"+fl+",'last':0";
			
			data += "},";
		}
		if (sales.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";		
		
		return data;
	}
}