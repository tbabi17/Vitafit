package oss.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;

import oss.additional.PlanData;
import oss.cache.logger;
import oss.report.Collection;


public class mobileConnection extends httpConnection {	
	
	public mobileConnection(systemController th) {
		super(th);
	}     
	   
	public String jsonOrderData(String tableName, String fields, String where) {		
		if (tableName == null || fields == null) return "";
		if (where.endsWith("GROUP by productCode")) where = where.replaceAll("GROUP by productCode", "GROUP by customerCode,productCode");
		if (fields.indexOf("requestCount") != -1) {									
			fields = fields.replaceAll("requestCount", "sum(case confirmedCount WHEN 0 THEN 1 ELSE 0 END * requestCount) as requestCount");
		}
		
		if (fields.indexOf("lastCount") != -1) {
			String userCode = where.substring(where.indexOf("userCode"), where.indexOf("userCode")+14);			
			fields = fields.replaceAll("lastCount", "(SELECT TOP 1 lastCount FROM Orders WHERE "+userCode+" and productCode=B.productCode and requestCount=0 and flag=0 ORDER BY id DESC) as lastCount");			
		}
		
		if (fields.indexOf("flagStatus") != -1) {
			String userCode = where.substring(where.indexOf("userCode"), where.indexOf("userCode")+14);			
			fields = fields.replaceAll("flagStatus", "(SELECT TOP 1 inCount FROM Orders WHERE "+userCode+" and productCode=B.productCode and requestCount=0 and flag>0 ORDER BY id DESC) as flagStatus");			
		}
		
		if (fields.indexOf("orderedCount") != -1)
			fields = fields.replaceAll("orderedCount", "(select top 1 [lastCount] from Orders where customerCode=b.customerCode and productCode=b.productCode order by _date desc) as orderedCount");
		
		
		if (fields.indexOf("price") != -1)
			fields = fields.replaceAll("price", "(select top 1 [price] from Orders where customerCode=b.customerCode and productCode=b.productCode order by _date desc) as price");
				
		String data = "{'results':%, 'success': 'true', 'items':[";
		int count = 0;				
		String[] fds = fields.split(",");
		
		if (fields.indexOf("lastS") != -1) {			
			fields = fields.replaceAll("lastS", "isnull((select top 1 confirmedCount from Orders where customerCode=b.customerCode and productCode=b.productCode and _date<=DATEADD(dd, 0, DATEDIFF(dd, 0, CURRENT_TIMESTAMP)) order by _date desc),0) as lastS");
		}
		if (fields.indexOf("lastQ") != -1) {			
			fields = fields.replaceAll("lastQ", "isnull((select top 1 _count from Product_count where customerCode=b.customerCode and productCode=b.productCode and _date>=DATEADD(dd, 0, DATEDIFF(dd, 0, CURRENT_TIMESTAMP)) order by _date desc),0) as lastQ");
		}
		if (fields.indexOf("firstQ") != -1) {			
			fields = fields.replaceAll("firstQ", "isnull((select top 1 _count from Product_count where customerCode=b.customerCode and productCode=b.productCode and _date<DATEADD(dd, 0, DATEDIFF(dd, 0, CURRENT_TIMESTAMP)) order by _date desc),0) as firstQ");
		}
		
		try {
			Connection con = shared.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT "+fields+" FROM "+tableName+where);			
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int column = rsmd.getColumnCount();			
			while (rs.next()) {		
				data += "{";
				for (int i = 0; i < column; i++) {				
					String type = rsmd.getColumnTypeName(i+1); 
					String comma = shared.commaType[(int)type.charAt(0)];
					if (fds[i].indexOf(" as ") != -1)
						fds[i] = fds[i].substring(fds[i].lastIndexOf(' ')+1, fds[i].length());
					String v = rs.getString(i+1);
					if (v == null) v="0";
					data += "'"+fds[i]+"':"+comma+v+comma+",";
				}
				data = data.substring(0, data.length() - 1);
				data += "},";
				count++;
			}
			if (count > 0)
				data = data.substring(0, data.length() - 1);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		data = data.replace("%", Integer.toString(count));
		data += "]}";		
		
		return data;
	}
	
	public String jsonData(String tableName, String fields, String where) {
		if (tableName == null || fields == null) return "";
		
		if (fields.indexOf("customerName") != -1)			
			fields = fields.replaceAll("customerName", shared.specialQuery.get("customerName"));					
		if (fields.indexOf("priceTag") != -1)
			fields = fields.replaceAll("priceTag", shared.specialQuery.get("priceTag"));
		if (fields.indexOf("parentID") != -1)
			fields = fields.replaceAll("parentID", shared.specialQuery.get("parentID"));		
		if (fields.indexOf("payed") != -1)
			fields = fields.replaceAll("payed", shared.specialQuery.get("payedLast"));
		if (fields.indexOf("lat") != -1)
			fields = fields.replaceAll("lat", shared.specialQuery.get("lat"));
		if (fields.indexOf("lng") != -1)
			fields = fields.replaceAll("lng", shared.specialQuery.get("lng"));
		if (fields.indexOf("loanMargin") != -1)
			fields = fields.replaceAll("loanMargin", shared.specialQuery.get("loanMargin"));
		
		if (fields.indexOf("daydate") != -1)
			fields = fields.replaceAll("daydate", shared.specialQuery.get("daydate"));
					
		if (fields.indexOf("dist") != -1)
			fields = fields.replaceAll("dist", "discount as dist");
		else
		if (fields.indexOf("discount") != -1)
			fields = fields.replaceAll("discount", shared.specialQuery.get("discount"));
		
		if (fields.indexOf("cusName") != -1)
			fields = fields.replaceAll("cusName", "(select name+'|'+location from Customer where parentID=sales.discount) as cusName");
		
		if (tableName.startsWith("Orders")) 
			return jsonOrderData(tableName, fields, where);				
						
				
		String data = "{'results':%, 'success': 'true', 'items':[";
		int count = 0;				
		String[] fds = fields.split(",");
		fields = fields.replaceAll(";", ",");
		
		if (fields.indexOf("rCount") != -1)
			fields = fields.replaceAll("rCount", "isnull(sum(requestCount),0)");
		if (fields.indexOf("loaded") != -1) {			
			//fields = fields.replaceAll("loaded", "isnull((select SUM(lastCount/unit) from Orders JOIN Product on code=productCode where userCode=Users.code),0) as loaded");
			fields = fields.replaceAll("loaded", "0 as loaded");
		}
		
		try {
			Connection con = shared.getConnection();			
			String query = "";
			query = "SELECT "+fields+" FROM "+tableName+where;
			System.out.println("SELECT "+fields+" FROM "+tableName+where);
			PreparedStatement ps = con.prepareStatement(query);		
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int column = rsmd.getColumnCount();	
			String tmpl = getJsonTmpl(column, rsmd, fds); 
			String tmpl1 = tmpl;
			
			for (int i = 0; i < column; i++)								
				if (fds[i].indexOf(" as ") != -1)
					fds[i] = fds[i].substring(fds[i].lastIndexOf(' ')+1, fds[i].length());
				
			while (rs.next()) {		
				data += "{";
				for (int i = 0; i < column; i++) {				
					String type = rsmd.getColumnTypeName(i+1); 
					String comma = shared.commaType[(int)type.charAt(0)];
					if (fds[i].indexOf(" as ") != -1)
						fds[i] = fds[i].substring(fds[i].lastIndexOf(' ')+1, fds[i].length());
								
					data += "'"+fds[i]+"':"+comma+rs.getString(i+1)+comma+",";
				}
				//for (int i = 0; i < column; i++) 
//					tmpl1 = tmpl1.replaceAll("#"+fds[i], rs.getString(i+1));
				
		//		data += tmpl1;
	//			tmpl1 = tmpl;
				
				data = data.substring(0, data.length() - 1);
				data += "},";
				count++;
			}
			if (count > 0)
				data = data.substring(0, data.length() - 1);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		data = data.replace("%", Integer.toString(count));
		data += "]}";		
		System.out.println(data);
		return data;
	}			
	
	public String jsonDataTouch(String tableName, String fields, String where) {
		if (tableName == null || fields == null) return "";								
				
		String data = "{\"results\":\"%\", \"items\":[";
		int count = 0;						
		String[] fds = fields.split(",");
		fields = shared.replaceQueries(fields);
		try {
			Connection con = shared.getConnection();						
			PreparedStatement ps = con.prepareStatement("SELECT "+fields+" FROM "+tableName+where);						
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int column = rsmd.getColumnCount();			
			while (rs.next()) {		
				data += "{";
				for (int i = 0; i < column; i++) {				
					String type = rsmd.getColumnTypeName(i+1); 
					String comma = shared.commaType[(int)type.charAt(0)];
					if (comma.equals("'")) comma = "\"";
					if (fds[i].indexOf(" as ") != -1)
						fds[i] = fds[i].substring(fds[i].lastIndexOf(' ')+1, fds[i].length());
					data += "\""+fds[i]+"\":"+comma+rs.getString(i+1)+comma+",";
				}
				data = data.substring(0, data.length() - 1);
				data += "},";
				count++;
			}
			if (count > 0)
				data = data.substring(0, data.length() - 1);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}		
		data = data.replace("%", Integer.toString(count));
		data += "]}";		
		
		return data;
	}		
	
	public int actionOrderDataPre(String action, String tableName, String fields, String values, String where) {
		if (tableName == null || fields == null) return -1;
		
		try {
			Connection con = shared.getConnection();
			if (action.equals("insert")) {												
				String[] vls = values.split(",");				
				int[] indexes = {2, 1, 3, 7, 4, 5};
				
				PreparedStatement ps = con.prepareStatement("SELECT TOP 1 lastCount FROM Orders WHERE (inCount<>0 or soldCount<>0) and customerCode=? and userCode=? and productCode=? and flag=0 ORDER by id DESC");//
				ps.setString(1, vls[indexes[1]].substring(1, vls[indexes[1]].length()));				
				ps.setString(2, vls[indexes[0]].substring(1, vls[indexes[0]].length()));
				ps.setString(3, vls[indexes[2]].substring(1, vls[indexes[2]].length()));				
				ResultSet rs = ps.executeQuery();
				int lastCount = 0;
				if (rs.next()) {
					lastCount = rs.getInt(1);
				}
				
				ps = con.prepareStatement("INSERT INTO Orders (_date,userCode,customerCode,productCode,soldCount,posx,posy,lastCount,flag) VALUES (CURRENT_TIMESTAMP,?,?,?,?,?,?,?,0)");				
				for (int i = 1; i <= 6; i++) {
					String value = vls[indexes[i-1]];
					char c = value.charAt(0);
					value = value.substring(1, value.length());						
					switch (c) {
						case 'i': ps.setInt(i, Integer.parseInt(value)); break;
						case 'f': ps.setFloat(i, Float.parseFloat(value)); break;			
						case 'n': case 'v': case 's': ps.setString(i, toUTF8(value)); break;						
					}
				}
				ps.setInt(7, lastCount-Integer.parseInt(vls[7].substring(1, vls[7].length())));
				ps.executeUpdate();								
				ps.close();
				changelog("orders", "insert", fields, values);
			} 
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
					
		return 0;
	}

	public int actionPacketData(String action, String tableName, String fields, String values, String where) {		
		String[] vls = values.split(",");
		//code,quantity,userCode,customerCode,posX,posY
		int count = Integer.parseInt(vls[1].substring(1, vls[1].length()));
				
		fields = "_dateStamp,customerCode,userCode,productCode,posX,posY,type,quantity,price,amount";		
		for (int i = 0; i < count; i++) {			
			try {
				Connection con = shared.getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT productCode,quantity,price,quantity*price FROM Packet WHERE code=?");
				ps.setString(1, getString(vls[0]));
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					values = "d"+convertDateTimeToString()+","+vls[3]+","+vls[2]+",s"+rs.getString(1)+","+vls[4]+","+vls[5]+",i"+getString(vls[0])+",i"+rs.getInt(2)+",f"+rs.getFloat(3)+",f"+rs.getFloat(4);					
					actionData(action, tableName, fields, values, where);
				}
				rs.close();
				ps.close();
			} catch (Exception ex) {
				logger._.error(ex.getMessage());
			}			
		}
		
		return 0;
	}
	
	public String jsonSalesTodayTotal(String userCode, String today) {							
		LinkedList<PlanData> sales = new LinkedList<PlanData>();
		try {
			Connection con = shared.getConnection();
			PreparedStatement ps;		
			ps = con.prepareStatement("select name, sum(quantity), sum(amount), type from Sales left join Product on code=productCode where type<>4 and type<>5 and DATEADD(D, 0, DATEDIFF(D, 0, _dateStamp))='"+today+"' and userCode=? group by productCode,name,type");//"select product.name, sum(quantity), sum(amount), sum(quantity)-sum(quantity*(case [type] when 1 then 1 when 3 then 1 else 0 end)), sum(amount)-sum(amount*(case [type] when 1 then 1 when 3 then 1 else 0 end)),type from sales right join product on code=productCode where DATEADD(D, 0, DATEDIFF(D, 0, _dateStamp))='"+today+"' and userCode=? group by product.class,product.name,type order by product.class,product.name");			
			ps.setString(1, userCode);
			ResultSet rs = ps.executeQuery();									
			while (rs.next()) {
				PlanData sale = new PlanData();
				sale.data = rs.getString(1);						
				sale.value1 = rs.getFloat(2);				
				sale.value2 = rs.getFloat(3);		
				sale.value3 = rs.getFloat(4);				
				if (sale.value3 == 3)
					sale.data = "Зээл төлөлт";
				sales.add(sale);				
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		String data = "{'results':"+(sales.size()+4)+", 'success': 'true', 'items':[";
							
		float values = 0;
		float amounts = 0;
		float rvalues = 0;
		float ramounts = 0;
		float zvalues = 0;
		float zamounts = 0;
		
		for (int i = 0; i < sales.size(); i++) {
			PlanData sale = (PlanData)sales.get(i);
			String ss = "";
			if (sale.value3==0)
				ss = "Бэлнээр, ";
			if (sale.value3 == 1 || sale.value3 >= 10)
				ss = "Зээлээр, ";
			if (sale.value3 == 3)
				ss = "Зээл хураалт, ";
			
			data += "{'data':'"+sale.data+"','type':'"+(ss)+"','quantity':"+sale.value1+",'amount':"+sale.value2+"},";
			if (sale.value3 == 0) {
				values += sale.value1;
				amounts += sale.value2;
			} else
			if (sale.value3 == 1) {
				rvalues += sale.value1;
				ramounts += sale.value2;
			} else			
			if (sale.value3 == 3) {
				zvalues += sale.value1;
				zamounts += sale.value2;
			}
		}
		data = data.substring(0, data.length() - 1);		
		data += ",{'data':'Зээлээр','type':'','quantity':"+rvalues+",'amount':"+ramounts+"}";
		data += ",{'data':'Бэлнээр (Өнөөдөр тушаах)','type':'','quantity':"+(values+zvalues)+",'amount':"+(amounts+zamounts)+"}";
		data += ",{'data':'Нийт зээл төлөлт','type':'','quantity':"+zvalues+",'amount':"+zamounts+"}";		
		data += ",{'data':'Нийт дүн','type':'','quantity':"+(values+rvalues+zvalues)+",'amount':"+(ramounts+amounts+zamounts)+"}]}";
		
		return data;
	}
	
	public String jsonOrderTodayTotal(String userCode, String today) {	
		
		LinkedList<PlanData> sales = new LinkedList<PlanData>();
		try {
			Connection con = shared.getConnection();
			PreparedStatement ps;		
			ps = con.prepareStatement("select product.name, sum(confirmedCount), sum(confirmedCount*price) as amount from orders right join product on code=productCode where DATEADD(D, 0, DATEDIFF(D, 0, _date))='"+today+"' and userCode=? and confirmedCount>0 group by product.class,product.name order by product.class,product.name");			
			ps.setString(1, userCode);
			ResultSet rs = ps.executeQuery();									
			while (rs.next()) {
				PlanData sale = new PlanData();
				sale.data = rs.getString(1);						
				sale.value1 = rs.getFloat(2);				
				sale.value2 = rs.getFloat(3);									
				sales.add(sale);				
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		String data = "{'results':"+(sales.size()+1)+", 'success': 'true', 'items':[";
							
		float values = 0;
		float amounts = 0;
		
		for (int i = 0; i < sales.size(); i++) {
			PlanData sale = (PlanData)sales.get(i);			
			data += "{'data':'"+sale.data+"','quantity':"+sale.value1+",'amount':"+sale.value2+"},";
			values += sale.value1;
			amounts += sale.value2;
		}
				
		data += "{'data':'Нийт','quantity':"+(values)+",'amount':"+(amounts)+"}]}";
		
		return data;
	}
	
	public String jsonUserCurrentTotal(String userCode) {//zahialagchiin odoogiin ooriin uldegdel	
		
		LinkedList<PlanData> sales = new LinkedList<PlanData>();
		try {
			Connection con = shared.getConnection();
			PreparedStatement ps;		
			ps = con.prepareStatement("select name, (select top 1 lastCount from Orders where userCode='"+userCode+"' and customerCode='"+userCode+"' and productCode=b.productCode order by lastCount desc) as lastCount from Orders as b join Product on code=productCode where productCode<>'nul' group by productCode,code,name");						
			ResultSet rs = ps.executeQuery();									
			while (rs.next()) {
				PlanData sale = new PlanData();
				sale.data = rs.getString(1);						
				sale.value1 = rs.getFloat(2);				
				if (sale.value1 > 0)
					sales.add(sale);				
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		String data = "{'results':"+(sales.size()+1)+", 'success': 'true', 'items':[";
							
		float values = 0;
		float amounts = 0;
		
		for (int i = 0; i < sales.size(); i++) {
			PlanData sale = (PlanData)sales.get(i);			
			data += "{'data':'"+sale.data+"','quantity':"+sale.value1+"},";
			values += sale.value1;			
		}
				
		data += "{'data':'Нийт','quantity':"+(values)+"}]}";
		
		return data;
	}
	
	public String getTodayWorkInfo(String userCode, String routeID, String today, String weekday) {
		if (weekday.equals("mon")) weekday = "<b>Даваа</b>";
		if (weekday.equals("thue")) weekday = "<b>Мягмар</b>";
		if (weekday.equals("wed")) weekday = "<b>Лхагва</b>";
		if (weekday.equals("thur")) weekday = "<b>Пүрэв</b>";
		if (weekday.equals("fri")) weekday = "<b>Баасан</b>";
		if (weekday.equals("sat")) weekday = "<b>Бямба</b>";
		if (weekday.equals("sun")) weekday = "<b>Ням</b>";
				
		String json = jsonData("Route", "routeName", " WHERE routeID='"+routeID+"'");		
		Collection c = getCollection("routeName", "s", json);		
		String str = "<br><br>Өнөөдөр "+(today==null?"":today)+" "+(weekday==null?"":weekday)+" гараг. Таны явах чиглэл <b>"+
					 (c == null || c.size() == 0 ? "":c.elementAt(0).getString("routeName"))+"</b>.<br>";				
		return str;
	}
}
