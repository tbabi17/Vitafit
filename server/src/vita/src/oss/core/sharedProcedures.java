package oss.core;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;

import oss.cache.cacheManager;
import oss.cache.logger;
import oss.database.sqlRoute;
import oss.report.Collection;
import oss.report.Variant;


public class sharedProcedures extends toolController {
	public sqlRoute sql;
	public static String[] commaType; 	
	public static Hashtable<String, Hashtable<String, String>> tableTypes;
	public static Hashtable<String, String> specialQuery = new Hashtable<String, String>();; 
	public static String[][] language;
	public static cacheManager cacheMan;	
	
	public sharedProcedures() { 
		initConnection();
	} 
	
	public void initConnection() {				
		specialQuery = new Hashtable<String, String>();
		specialQuery.put("customerName", "(SELECT TOP 1 name+'|'+location from Customer WHERE Customer.code=customerCode) as customerName");
		specialQuery.put("lat", "(SELECT TOP 1 posx from Customer WHERE Customer.code=customerCode) as lat");
		specialQuery.put("lng", "(SELECT TOP 1 posy from Customer WHERE Customer.code=customerCode) as lng");
		specialQuery.put("loanMargin", "(SELECT TOP 1 loanMargin from Customer WHERE Customer.code=customerCode) as loanMargin");
		specialQuery.put("priceTag", "(SELECT TOP 1 pricetag from Customer WHERE Customer.code=customerCode) as priceTag");
		specialQuery.put("parentID", "(SELECT TOP 1 parentID from Customer WHERE Customer.code=customerCode) as parentID");
		specialQuery.put("discount", "(SELECT TOP 1 discount from Customer WHERE Customer.code=customerCode) as discount");
		specialQuery.put("loan", "loanMargin as loanMargin");
		
		if (sql != null) { 
			initSpecialQueries();
			initLanguage();
		}
		
		initData();
		
		if (initComplete()) return;
		
		sql = new sqlRoute();			
		sql.initConnection();								
	}				
	
	public boolean initComplete() {
		return (sql != null && 
				specialQuery != null && 
				tableTypes != null && 
				commaType != null);
	}
	
	public String getLanguageWord(int i) {
		return language[i][toolController.langid];
	}
	
	public void initLanguage() {
		if (language == null) {			
			try {			
				Connection con = sql.getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT * FROM Language");			
				ResultSet rs = ps.executeQuery();			
				language = new String[1000][4];
				while (rs.next()) {												
					language[rs.getInt(1)][0] = rs.getString(2);
					language[rs.getInt(1)][1] = rs.getString(3);
					language[rs.getInt(1)][2] = rs.getString(4);
				}
				
				rs.close();
				ps.close();
			} catch (Exception ex) {
				
			}
		}
	}
	
	public void initSpecialQueries() {
		if (specialQuery == null || specialQuery.size() <= 10) {			
			try {			
				Connection con = sql.getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT * FROM Settings WHERE descr='query' and userCode='15'");			
				ResultSet rs = ps.executeQuery();			
				
				while (rs.next()) {												
					specialQuery.put(rs.getString(1), rs.getString(2));					
				}
				
				rs.close();
				ps.close();
			} catch (Exception ex) {
				
			}
		}
	}
	
	public Connection getConnection() {
		return sql.getConnection();
	}		
	
	public String replaceQueries(String fields) {
		initSpecialQueries();
		String[] fd = fields.split(",");
		String result = "";
		for (int i = 0; i < fd.length; i++) {
			String vl = fd[i];
			
			if (specialQuery.containsKey(vl)) {
				result += specialQuery.get(vl)+",";
			} else
				result += fd[i]+",";
		}
				
		if (result.length() > 1) result = result.substring(0, result.length()-1);
		return result;
	}
	
	public String replaceQueriesSpace(String fields) {
		String[] fd = fields.split(" ");
		String result = "";
		for (int i = 0; i < fd.length; i++) {
			String vl = fd[i];
			
			if (vl.length()>=8 && specialQuery.containsKey(vl)) {
				result += specialQuery.get(vl)+" ";
			} else
				result += fd[i]+" ";
		}
				
		if (result.length() > 1) result = result.substring(0, result.length()-1);
		return result;
	}
	
	public String getComma(String type) {
		return commaType[(int)type.charAt(0)];
	}
	
	public void initData() {
		commaType = new String[256];
		commaType[(int)'n'] = "'";
		commaType[(int)'v'] = "'";
		commaType[(int)'s'] = "'";
		commaType[(int)'d'] = "'";
		commaType[(int)'t'] = "'";
		commaType[(int)'i'] = "";
		commaType[(int)'f'] = "";
		commaType[(int)'m'] = "";						
	}
	
	public void getAllTablesParameters() {
		if (tableTypes != null && tableTypes.size() > 0) return;
		try {			
			tableTypes = new Hashtable<String, Hashtable<String, String>>();
			Connection con = sql.getConnection();
			
			PreparedStatement ps = con.prepareStatement("SELECT name FROM sys.Tables");
			ResultSet rs = ps.executeQuery();
			String table = "";
			while (rs.next()) {
				table = rs.getString(1);		
				if (Character.isLowerCase(table.charAt(0))) continue;
				PreparedStatement ps1 = con.prepareStatement("SELECT column_name,"+
							" case data_type "+
							" when 'datetime' then 'd'"+
							" when 'date' then 'd'"+
							" when 'varchar' then 's'"+
							" when 'nvarchar' then 's'"+
						    " when 'int' then 'i'"+
						    " when 'image' then 's'"+
						    " when 'text' then 's'"+						    
							" when 'float' then 'f'"+
							" when 'ntext' then 's'"+
							" when 'double' then 'f'"+
							" when 'bigint' then 'i'"+
							" when 'money' then 'f'"+						
						" end from information_schema.columns WHERE table_name = ?");
				ps1.setString(1, table);
				ResultSet rs1 = ps1.executeQuery();
				Hashtable<String, String> fields = new Hashtable<String, String>();
				System.out.println(table);
				while (rs1.next()) {
					if (rs1.getString(2) != null)
						fields.put(rs1.getString(1), rs1.getString(2));					
				}
				rs1.close();
				ps1.close();
				tableTypes.put(table, fields);				
			}
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}		
	}
	
	public float getDefetedValue(Variant v, String key) {				
		if (key.equals("ehlel")) {
			int first = v.getInt("last") + v.getInt("sold") - v.getInt("get");			
			v.put("ehlel", "f"+first);
		}
		
		return v.getFloat(key);
	}
	
	public String getDataCollectorByJson(String query, String fields, String types, int start, int limit, String qs) {
		Collection collection = getDataCollector(query, fields, types, qs);		
		String data = "{'results':"+collection.size()+", 'success': 'true', 'items':[";
		String[] fd = fields.split(",");
		String[] tp = types.split(",");
		for (int j = 0; j < collection.size(); j++) {
			if (start > j || start+limit < j) continue;
			Variant v = (Variant)collection.elementAt(j);
			data += "{";
			for (int i = 0; i < fd.length; i++) {				
				switch (tp[i].charAt(0)) {
					case 's': data += "'" + fd[i] +"':'" + v.getString(fd[i]) + "',";  break;
					case 'i': data += "'" + fd[i] +"':" + v.getInt(fd[i]) + ",";  break;
					case 'f': data += "'" + fd[i] +"':" + getDefetedValue(v, fd[i]) + ",";  break;
					case 'b': data += "'" + fd[i] +"':" + (v.getInt(fd[i])>0) + ",";  break;
				}
			}
			
			data = data.substring(0, data.length() - 1);
			data += "},";
		}
		if (collection.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";				
		
		return data;
	}	
	
	public String getDataCollectorByJson(String query, String fields, String types) {
		Collection collection = getDataCollector(query, fields, types);		
		String data = "{'results':"+collection.size()+", 'success': 'true', 'items':[";
		String[] fd = fields.split(",");
		String[] tp = types.split(",");
		for (int j = 0; j < collection.size(); j++) {
			Variant v = (Variant)collection.elementAt(j);
			data += "{";
			for (int i = 0; i < fd.length; i++) {				
				switch (tp[i].charAt(0)) {
					case 's': data += "'" + fd[i] +"':'" + v.getString(fd[i]) + "',";  break;
					case 'i': data += "'" + fd[i] +"':" + v.getInt(fd[i]) + ",";  break;
					case 'f': data += "'" + fd[i] +"':" + getDefetedValue(v, fd[i]) + ",";  break;
					case 'b': data += "'" + fd[i] +"':" + (v.getInt(fd[i])>0) + ",";  break;
				}
			}
			
			data = data.substring(0, data.length() - 1);
			data += "},";
		}
		if (collection.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";				
		
		return data;
	}	
	
	public String getDataCollectorHaveByJson(String query, String fields, String types, String field) {
		Collection collection = getDataCollector(query, fields, types);		
		String data = "{'results':%, 'success': 'true', 'items':[";
		String[] fd = fields.split(",");
		String[] tp = types.split(",");
		int count = 0;
		for (int j = 0; j < collection.size(); j++) {
			Variant v = (Variant)collection.elementAt(j);
			//if (v.getInt(field) <= 0) continue;
			count++;
			data += "{";
			for (int i = 0; i < fd.length; i++) {				
				switch (tp[i].charAt(0)) {
					case 's': data += "'" + fd[i] +"':'" + v.getString(fd[i]) + "',";  break;
					case 'i': data += "'" + fd[i] +"':" + v.getInt(fd[i]) + ",";  break;
					case 'f': data += "'" + fd[i] +"':" + getDefetedValue(v, fd[i]) + ",";  break;
					case 'b': data += "'" + fd[i] +"':" + (v.getInt(fd[i])>0) + ",";  break;
				}
			}
			
			data = data.substring(0, data.length() - 1);
			data += "},";
		}
		if (collection.size() > 0)
			data = data.substring(0, data.length() - 1);
		data = data.replace("%", count+"");
		data += "]}";				
		
		return data;
	}		
	
	public Collection getDataCollector(String query, String fields, String types, String qs) {				
		Collection collect = new Collection();
		try {
			query = npString(query, "");			
			Connection con = getConnection();
			String [] fd = fields.split(",");
			String [] tp = types.split(",");
			
			String search = "";						
			qs = qs.trim();
			for (int i = 0; i < fd.length; i++) {				
				search += fd[i]+" like N'%"+toUTF8(qs)+"%' or ";				
			}
			if (search.length() > 6 && qs != null && qs.length() > 0) 
				search = " and ("+search.substring(0, search.length() - 4)+")";
			else 
				search = "";
			
			PreparedStatement ps = con.prepareStatement(query+search);
						
			ResultSet rs = ps.executeQuery();						
			
			while (rs.next()) {
				Variant v = new Variant();
				for (int i = 0; i < fd.length; i++) {
					switch (tp[i].charAt(0)) {
						case 's':
								 v.put(fd[i], rs.getString(fd[i]));
								 break;
						case 'i':
							 	 v.put(fd[i], ""+rs.getLong(fd[i]));
							 	 break;
						case 'f':
						 	 	 v.put(fd[i], ""+rs.getFloat(fd[i]));
						 	 	 break;	 	 
					}
				}
				collect.addCollection(v);
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}						
		
		return collect;
	}
	
	public Collection getDataCollector(String query, String fields, String types) {				
		Collection collect = new Collection();
		try {
			query = npString(query, "");			
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(query);			
			ResultSet rs = ps.executeQuery();						
			String [] fd = fields.split(",");
			String [] tp = types.split(",");
			while (rs.next()) {
				Variant v = new Variant();
				for (int i = 0; i < fd.length; i++) {
					switch (tp[i].charAt(0)) {
						case 's':
								 v.put(fd[i], rs.getString(fd[i]));
								 break;
						case 'i':
							 	 v.put(fd[i], ""+rs.getLong(fd[i]));
							 	 break;
						case 'f':
						 	 	 v.put(fd[i], ""+rs.getFloat(fd[i]));
						 	 	 break;	 	 
					}
				}
				collect.addCollection(v);
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}						
		
		return collect;
	}
	
	public String jsonData(String query, String fields) {						
		String[] fds = fields.split(",");
		String data = "{'results':%, 'success': 'true', 'items':[";
		int count = 0;						 
		try {			
			query = npString(query, ""); 
			Connection con = getConnection();			
			PreparedStatement ps = con.prepareStatement(query);			
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int column = rsmd.getColumnCount();			
			for (int i = 0; i < fds.length; i++) {
				if (fds[i].equals("pID")) fds[i] = "parentID";
				if (fds[i].equals("ptag")) fds[i] = "pricetag";
				if (fds[i].indexOf(" as ") != -1) {
					fds[i] = fds[i].substring(fds[i].lastIndexOf(" as ")+4, fds[i].length());				
				}
			}			
			
			while (rs.next()) {
				data += "{";
				for (int i = 0; i < column; i++) {				
					if (rsmd.getColumnName(i+1).equals("row")) continue;
					String type = rsmd.getColumnTypeName(i+1);
					String comma = commaType[(int)type.charAt(0)];			
					if (comma == null) comma = "'";
					String value = rs.getString(i+1);
					if (value != null)
						value = value.replaceAll(System.getProperty("line.separator"), " ");
					
					data += "'"+fds[i]+"':"+comma+value+comma+",";
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
	
	public String jsonData(String query, String fields, String qs, int start, int limit) {						
		String[] fds = fields.split(",");
		String data = "{'results':%, 'success': 'true', 'items':[";
		int count = 0;						 
		try {			
			query = npString(query, ""); 
			Connection con = getConnection();			
			String search = "";
			qs = qs.trim();
			for (int i = 0; i < fds.length; i++) {
				if (fds[i].equals("pID")) fds[i] = "parentID";
				if (fds[i].equals("ptag")) fds[i] = "pricetag";
				if (fds[i].indexOf(" as ") != -1) {
					fds[i] = fds[i].substring(fds[i].lastIndexOf(" as ")+4, fds[i].length());				
				}
				if (fds[i].equals("name") || fds[i].equals("code") || fds[i].equals("location") || fds[i].equals("descr"))
					search += fds[i]+" like N'%"+toUTF8(qs)+"%' or ";				
			}		
			if (search.length() > 6 && qs != null && qs.length() > 0) 
				search = " and ("+search.substring(0, search.length() - 4)+")";
			else 
				search = "";
			PreparedStatement ps = con.prepareStatement(query+search);				
			logger._.info(query+search);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int column = rsmd.getColumnCount();						
			
			while (rs.next()) {
				count++;
				if (start > count || start+limit < count) continue;
				data += "{";
				for (int i = 0; i < column; i++) {									
					String type = rsmd.getColumnTypeName(i+1);
					String comma = commaType[(int)type.charAt(0)];			
					if (comma == null) comma = "'";
					String value = rs.getString(i+1);
					if (value != null)
						value = value.replaceAll(System.getProperty("line.separator"), " ");
					
					data += "'"+fds[i]+"':"+comma+value+comma+",";
				}
				data = data.substring(0, data.length() - 1);
				data += "},";				
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
		String fs = fields;		
		fields = replaceQueries(fields);	
		String query = "SELECT "+fields+" FROM "+tableName+where;
		
		return jsonData(query, fs);
	}				
	
	public String jsonData(String tableName, String fields, String where, String qs, int start, int limit) {
		if (tableName == null || fields == null) return "";			
		String fs = fields;		
		fields = replaceQueries(fields);
		String query = "SELECT "+fields+" FROM "+tableName+where;
		return jsonData(query, fs, qs, start, limit);
	}
	
	public Variant getModuleQuery(String func) {							
		Variant w = new Variant();			
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT tableName, fields, _where FROM ModuleQuery WHERE module=?");
			ps.setString(1, func);
			ResultSet rs = ps.executeQuery();					
						
			if (rs.next()) {
				w.put("tableName", rs.getString("tableName"));
				w.put("fields", rs.getString("fields"));
				w.put("_where", rs.getString("_where"));
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return w;
	}
	
	public Variant getOSSQuery(String func) {							
		Variant w = new Variant();			
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT query, count, fields, types, wtypes FROM OSSQuery WHERE name=?");
			ps.setString(1, func);
			ResultSet rs = ps.executeQuery();					
						
			if (rs.next()) {
				w.put("query", rs.getString("query"));
				w.put("count", ""+rs.getInt("count"));		
				w.put("fields", rs.getString("fields"));
				w.put("types", rs.getString("types"));
				w.put("wtypes", rs.getString("wtypes"));
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return w;
	}
	
	public Variant getRemoteQuery(String func) {							
		Variant w = new Variant();			
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT query, fields, _type FROM RemoteQuery WHERE remote=?");
			ps.setString(1, func);
			ResultSet rs = ps.executeQuery();					
						
			if (rs.next()) {
				w.put("query", rs.getString("query"));
				w.put("fields", rs.getString("fields"));
				w.put("_type", rs.getString("_type"));
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return w;
	}
	
	public int actionData(String action, String tableName, String fields, String values, String where) {
		if (tableName == null || fields == null) return -1;

		try {
			Connection con = getConnection();
			if (action.equals("insert")) {				
				String params = "";
				String[] fds = fields.split(",");
				String[] vls = values.split(",");		
				for (int i = 0; i < fds.length; i++)
					params+="?,";		
				params = params.substring(0, params.length()-1);
				PreparedStatement ps = con.prepareStatement("INSERT INTO "+tableName+" ("+fields+") VALUES ("+params+")");				
				for (int i = 1; i <= fds.length; i++) {
					String value = vls[i-1];
					char c = value.charAt(0);					
					value = value.substring(1, value.length());						
					switch (c) {
						case 'i': ps.setInt(i, Integer.parseInt(value)); break;
						case 'f': ps.setFloat(i, Float.parseFloat(value)); break;
						case 'd': {
							if (value.length() <= 10) value += " 00:00:00";							
							ps.setTimestamp(i, java.sql.Timestamp.valueOf(value)); break;
						}
						case 'n': case 'v': case 's': ps.setString(i, toUTF8(value)); break;						
					}
				}
				ps.executeUpdate();								
				ps.close();
				
				if (tableName.toLowerCase().equals("users")) {
					ps = con.prepareStatement("UPDATE Users SET section='vitafit,pepsi' WHERE section='vitafit|pepsi'");
					ps.executeUpdate();
					ps.close();
				}
								
				if (tableName.toLowerCase().equals("customer")) {
					replaceCustomerJS();
					//parentID zaah
					ps = con.prepareStatement("UPDATE Customer set parentID=customerID where parentID is null or parentID = 0");
					ps.executeUpdate();
					ps.close();
					
					//
					String routeId = "", code = "";
					for (int i = 1; i <= fds.length; i++) {
						if (fds[i-1].equals("code")) {													
							String value = vls[i-1];
							code = value.substring(1, value.length());		
						}
						
						if (fds[i-1].equals("subid")) {
							String value = vls[i-1];
							routeId = value.substring(1, value.length());
						}
					}
					if (!routeId.equals("") && code.length()>0) {
						ps = con.prepareStatement("INSERT INTO Route_Customer (routeID,customerCode) VALUES ('"+routeId+"','"+code+"')");
						ps.executeUpdate();
						ps.close();
					}
						
					changelog(tableName, "insert", fields,values);
				}
			} else 
			if (action.equals("update_order_complete")) {
				PreparedStatement ps = con.prepareStatement("SELECT userCode,productCode,inCount FROM Orders WHERE "+where);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					String userCode = rs.getString(1);
					String productCode = rs.getString(2);
					int inCount = rs.getInt(3);	
					
					PreparedStatement ps1 = con.prepareStatement("UPDATE Orders SET lastCount=lastCount+?, flag=0 WHERE userCode=? and productCode=? and flag=1");
					ps1.setInt(1, inCount);
					ps1.setString(2, userCode);
					ps1.setString(3, productCode);
					ps1.executeUpdate();
					ps1.close();
					changelog("Orders", "update", "userCode","s"+userCode);
				}
				rs.close();
				ps.close();
			
			} else
			if (action.equals("update")) {
				String params = "";
				String[] fds = fields.split(",");
				String[] vls = values.split(",");		
				for (int i = 0; i < fds.length; i++)
					params+=fds[i]+"=?,";		
				params = params.substring(0, params.length()-1);
				
				if (tableName.equals("Orders") && (fields != null && (fields.equals("flag") || fields.equals("flag,userCode")))) { //fix order acception later
					String wh = where.substring(0, where.length()-11);					
					
					if (wh.indexOf("userCode") != -1 && wh.indexOf("productCode") != -1) {
						PreparedStatement ps = con.prepareStatement("UPDATE Orders SET "+ 
									" lastCount=lastCount+(select top 1 inCount from Orders where "+wh+" and flag=1 and inCount>0 order by _date desc) "+
									" where flag=0 and "+wh+" and soldCount>0 and _date>(select top 1 _date from orders where "+wh+" and flag=1 order by _date desc)"+
									" and id=(select top 1 id from orders where "+wh+" and flag=0 order by _date desc)");															
						
						ps.executeUpdate();		
						changelog("Orders", "update", fields,values);
					} else
					if (wh.indexOf("userCode") != -1)
					{
						PreparedStatement ps = con.prepareStatement("SELECT productCode FROM Orders where flag=1 and "+wh);
						ResultSet rs = ps.executeQuery();
						
						while (rs.next()) {
							String pc = rs.getString(1);
							PreparedStatement ps1 = con.prepareStatement("UPDATE Orders SET "+ 
									" lastCount=lastCount+(select top 1 inCount from Orders where "+wh+" and productCode='"+pc+"' and flag=1 and inCount>0 order by _date desc) "+
									" where flag=0 and "+wh+" and productCode='"+pc+"' and soldCount>0 and _date>(select top 1 _date from orders where "+wh+" and productCode='"+pc+"' and flag=1 order by _date desc)"+
									" and id=(select top 1 id from orders where "+wh+" and productCode='"+pc+"' and flag=0 order by _date desc)");															
						
							ps1.executeUpdate();
							ps1.close();
							changelog("Orders", "update", fields,values);
						}
					}
				}							
				
				PreparedStatement ps = con.prepareStatement("UPDATE "+tableName+" SET "+params+" WHERE "+where);		
				String str = "";
				for (int i = 1; i <= fds.length; i++) {
					String value = vls[i-1];
					char c = value.charAt(0);
					value = value.substring(1, value.length());					
					switch (c) {
						case 'i': ps.setInt(i, Integer.parseInt(value)); break;
						case 'f': ps.setFloat(i, Float.parseFloat(value)); break;
						case 'd': {
								if (value.length() <= 10) value += " 00:00:00";								
								ps.setTimestamp(i, java.sql.Timestamp.valueOf(value));
								break;
						}
						case 'n': case 'v': case 's': ps.setString(i, toUTF8(value)); break;
					}
					
					str += fds[i-1]+"="+c+value+" ";
				}				
				logger._.info(params+" rows "+str);
				if (ps.executeUpdate() == 0 && !tableName.equals("Orders") && !tableName.equals("Sales")) {					
					ps.close();
					actionData("insert", tableName, fields, values, where);
					changelog(tableName, "update", fields,values);
				} else {
					if (tableName.equals("Orders") && (fields != null && !fields.equals("flag") && !fields.equals("confirmedCount")) && vls.length > 2) { //etsiin uldegdel bodoh  lastCount+
						ps = con.prepareStatement("SELECT TOP 1 lastCount,flag FROM Orders WHERE (inCount<>0 or soldCount<>0) and userCode=? and customerCode=? and productCode=? ORDER by id DESC");
						ps.setString(1, vls[0].substring(1, vls[0].length()));
						ps.setString(2, vls[1].substring(1, vls[1].length()));
						ps.setString(3, vls[2].substring(1, vls[2].length()));
						ResultSet rs = ps.executeQuery();
										 	 							
						if (!rs.next()) {
							ps.close();
							rs.close();
							
							ps = con.prepareStatement("SELECT TOP 1 lastCount,flag FROM Orders WHERE userCode=? and customerCode=? and productCode=? ORDER by id DESC");
							ps.setString(1, vls[0].substring(1, vls[0].length()));
							ps.setString(2, vls[1].substring(1, vls[1].length()));
							ps.setString(3, vls[2].substring(1, vls[2].length()));
							rs = ps.executeQuery();
						}
						
						int lastCount = 0;
						int confirmedCount = getInt(vls[3]);
						if (rs.next()) {
							lastCount = rs.getInt(1);
							int flag = rs.getInt(2);
							
							if (flag == 1) return 0; //fix duplicate acception
						}
						
						if (actionStorage(Integer.parseInt(vls[4].substring(1, vls[4].length())), vls[2].substring(1, vls[2].length()), confirmedCount) == 1) {
							logger._.info(values);
							ps = con.prepareStatement("INSERT INTO Orders (_date,userCode,customerCode,productCode,inCount,lastCount,flag,wareHouseId,price) VALUES (CURRENT_TIMESTAMP,?,?,?,?,?,"+order_accept_state+",?,?)");
							String user = vls[0].substring(1, vls[0].length());
							if (vls.length == 7) user = vls[6].substring(1, vls[6].length());
							ps.setString(1, user);
							ps.setString(2, vls[1].substring(1, vls[1].length()));
							ps.setString(3, vls[2].substring(1, vls[2].length()));
							ps.setInt(4, confirmedCount);
							ps.setInt(5, lastCount + confirmedCount);
							ps.setInt(6, Integer.parseInt(vls[4].substring(1, vls[4].length())));
							ps.setFloat(7, Float.parseFloat(vls[5].substring(1, vls[5].length())));
							ps.executeUpdate();
							ps.close();																					
							
							if (!user.equals(vls[1].substring(1, vls[1].length())))
								actionAutoSaleData(user, vls[1].substring(1, vls[1].length()),vls[2].substring(1, vls[2].length()));
							
							changelog("Orders", "insert", "userCode","s"+user);
						}
					}
				}					
				
				
				if (tableName.toLowerCase().equals("customer")) {					
					String routeId = "", code = "";
					for (int i = 1; i <= fds.length; i++) {
						if (fds[i-1].equals("code")) {													
							String value = vls[i-1];
							code = value.substring(1, value.length());		
						}
						
						if (fds[i-1].equals("subid")) {
							String value = vls[i-1];
							routeId = value.substring(1, value.length());
						}
					}
					
					ps = con.prepareStatement("DELETE FROM Route_Customer WHERE customerCode='"+code+"'");
					ps.executeUpdate();
					ps.close();
					if (!routeId.equals("") && code.length()>0)
						ps = con.prepareStatement("INSERT INTO Route_Customer (routeID,customerCode) VALUES ('"+routeId+"','"+code+"')");
						ps.executeUpdate();
						ps.close();
				}
			} else
			if (action.equals("delete")) {				
				PreparedStatement ps = con.prepareStatement("DELETE FROM "+tableName+" WHERE "+where);
				ps.executeUpdate();
				ps.close();
				changelog(tableName, "update", fields,"all");
				
				if (tableName.toLowerCase().equals("customer")) {
					//route - ees hasah
					ps = con.prepareStatement("DELETE FROM Route_Customer WHERE customerCode not in (SELECT code FROM Customer)");
					ps.executeUpdate();
					ps.close();					
				}
			}
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
			ex.printStackTrace();
		}
					
		return 0;
	}
	
	public void actionAutoSaleData(String userCode, String customerCode, String productCode) {
		Collection collect = getDataCollector("select productCode,lastCount,price from Orders as b where flag=0 and userCode='"+userCode+"' and customerCode='"+customerCode+"' and productCode='"+productCode+"' and lastCount>0", "productCode,lastCount,price", "s,i,i");
		Collection collect1 = getDataCollector("select parentID from Customer where code='"+customerCode+"'", "parentID", "i");
		long ticketID = 0;		
		for (int i = 0; i < collect.size(); i++) {
			Variant w = (Variant)collect.elementAt(i);
			if (w.getInt("lastCount") == 0) continue;			
			int flag = w.getInt("lastCount")*w.getInt("price");			
			int discount = 0;
			if (collect1.size() > 0)
				discount = collect1.elementAt(0).getInt("parentID");
			
			String values = "d"+convertDateTimeToString()+",s"+customerCode+",s"+userCode+",s"+w.getString("productCode")+",f0,f0,i1,i"+w.getInt("lastCount")+",f"+w.getInt("price")+",f"+(w.getInt("lastCount")*w.getInt("price"))+",i"+discount+",i"+flag+",i1,i"+ticketID;			
			actionMobileData("insert", "Sales", "_dateStamp,customerCode,userCode,productCode,posX,posY,type,quantity,price,amount,discount,flag,userType,ticketID", values, " ");
		}
	}
	
	public int actionOneSaleData(String fields, String values) {		
		try {
			Connection con = getConnection();			
			String[] fds = fields.split(",");
			String[] vls = values.split(",");
			String params = "";							
			for (int i = 0; i < fds.length; i++)
				params+="?,";		
			params = params.substring(0, params.length()-1);
			PreparedStatement ps = con.prepareStatement("INSERT INTO Sales ("+fields+") VALUES ("+params+")");				
			for (int i = 1; i <= fds.length; i++) {
				String value = vls[i-1];
				char c = value.charAt(0);					
				value = value.substring(1, value.length());						
				switch (c) {
					case 'i': ps.setInt(i, Integer.parseInt(value)); break;
					case 'f': ps.setFloat(i, Float.parseFloat(value)); break;
					case 'd': {
						if (value.length() <= 10) value += " 00:00:00";
						ps.setTimestamp(i, java.sql.Timestamp.valueOf(value)); break;
					}
					case 'n': case 'v': case 's': ps.setString(i, toUTF8(value)); break;						
				}
			}
			ps.executeUpdate();								
			ps.close();			
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
					
		return 0;
	}		
	
	public int actionRentData(String date, String userCode, String customerCode, int parentID, String productCodes, float amount) {				
		float totalAmount = amount;
		int userType = 0;
		try {									
			Connection con = getConnection();			
			PreparedStatement ps = con.prepareStatement("SELECT id,flag,productCode,price,customerCode,userType FROM Sales WHERE userCode=? and discount=? and type=1 and flag>0 ORDER by _dateStamp");
			ps.setString(1, userCode);
			ps.setInt(2, parentID);
			ResultSet rs = ps.executeQuery();			
			while (rs.next()) {
					String productCode = rs.getString(3);
					userType = rs.getInt(6);
					float price = rs.getFloat(4);
					if (productCodes.equals("nul") || (productCodes.length() == 0 || productCodes.indexOf(productCode+":") != -1)) { // baigaa productsuudaas
						PreparedStatement ps1 = con.prepareStatement("UPDATE Sales SET flag=? WHERE type=1 and flag>0 and id=?");
						float t = rs.getFloat(2);
						float dec = Math.max(0, t-amount);
						ps1.setFloat(1, dec);						
						ps1.setInt(2, rs.getInt(1));
						ps1.executeUpdate();
						ps1.close();
						/*
						if (parentID < 100000) {//zeeliin borluulagchaas busad
							int quantity = (int)(Math.min(amount, rs.getFloat(2)) / price);
							if (date.endsWith("_TIMESTAMP")) date = convertDateTimeToString();
							int userType = rs.getInt(6);
							String values = "d"+date+",s"+customerCode+",s"+userCode+",s"+productCode+",i0,i0,i3,i"+quantity+",f"+price+",f"+(quantity*price)+",i"+parentID+",i"+userType;
							actionOneSaleData("_dateStamp,customerCode,userCode,productCode,posX,posY,type,quantity,price,amount,discount,userType", values);
						}*/
							
						amount = amount - t;						
						if (amount <= 0) break; 
					}
			}
			rs.close();
			ps.close();
			
			if (parentID < 100000) {//zeeliin borluulagchaas busad				
				if (date.endsWith("_TIMESTAMP")) date = convertDateTimeToString();				
				String values = "d"+date+",s"+customerCode+",s"+userCode+",snul,i0,i0,i3,i0,f0,f"+totalAmount+",i"+parentID+",i"+userType+",i"+getTicketId();
				actionOneSaleData("_dateStamp,customerCode,userCode,productCode,posX,posY,type,quantity,price,amount,discount,userType,ticketID", values);
			} else 
			if (parentID >= 100000) {//zeeliinhen
				String values = "d"+convertDateTimeToString()+",s"+customerCode+",s"+userCode+",snul,i0,i0,i3,i0,f0,f"+totalAmount+",i"+parentID;
				actionOneSaleData("_dateStamp,customerCode,userCode,productCode,posX,posY,type,quantity,price,amount,discount", values);
			}
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
			return -1;
		}
		
		return 0;
	}
	
	public int actionOrderData(String action, String tableName, String fields, String values, String where) {
		if (tableName == null || fields == null) return -1;
		
		try {
			Connection con = getConnection();
			if (action.equals("insert")) {												
				String[] vls = values.split(",");				
				int[] indexes = {2, 1, 3, 7, 4, 5};
				
				PreparedStatement ps = con.prepareStatement("SELECT TOP 1 lastCount,price FROM Orders WHERE (inCount<>0 or soldCount<>0) and userCode=? and productCode=? and flag=0 ORDER by id DESC");//
				ps.setString(1, vls[indexes[0]].substring(1, vls[indexes[0]].length()));				
				ps.setString(2, vls[indexes[2]].substring(1, vls[indexes[2]].length()));
				ResultSet rs = ps.executeQuery();
				int lastCount = 0;
				float price = Float.parseFloat(vls[8].substring(1, vls[8].length()));
				if (rs.next()) {					
					lastCount = rs.getInt(1);
					if (price == 0)
						price = rs.getFloat(2);					
				}
				
				ps = con.prepareStatement("INSERT INTO Orders (_date,userCode,customerCode,productCode,soldCount,lastCount,flag,price) VALUES (CURRENT_TIMESTAMP,?,?,?,?,?,0,?)");				
				for (int i = 1; i <= 4; i++) 				
				{
					String value = vls[indexes[i-1]];
					char c = value.charAt(0);					
					value = value.substring(1, value.length());						
					switch (c) {
						case 'i': ps.setInt(i, Integer.parseInt(value)); break;
						case 'f': ps.setFloat(i, Float.parseFloat(value)); break;			
						case 'n': case 'v': case 's': ps.setString(i, toUTF8(value)); break;						
					}
				}
				ps.setInt(5, lastCount-Integer.parseInt(vls[7].substring(1, vls[7].length())));
				ps.setFloat(6, price);
				ps.executeUpdate();								
				ps.close();
				changelog("orders", "insert", fields, values);
			} 
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
					
		return 0;
	}
	
	public int actionMobileData(String action, String tableName, String fields, String values, String where) {		
		if (tableName.equals("Sales")) {
			if (checkRecord(values)) { System.out.println("duplicated record"); return 0; }
			
			String[] vls = values.split(",");
			if (action.equals("rentpayment")) {				
				return actionRentData(getString(vls[0]), getString(vls[2]), getString(vls[1]), getInt(vls[10]), getString(vls[3]), Float.parseFloat(getString(vls[9])));				
			} else
				/*if (checkUser(getString(vls[2])) == ONE_SALLING)*/ 
					actionOrderData(action, tableName, fields, values, where); // Orderoos hasah
				/*else
					actionOrderDataPre(action, tableName, fields, values, where);*/
		}
		if (tableName.equals("Orders")) {
			changelog("Orders", action, fields,values);
			if (checkRecordForOrder(values)) { System.out.println("duplicated record for Orders"); return 0; }
		}
		
		if (tableName == null || fields == null) return -1;
				
		try {
			Connection con = getConnection();
			if (action.equals("insert")) {
				System.out.println("end l bna shdeeeeeeeeeeeee");
				String[] fds = fields.split(",");
				String[] vls = values.split(",");
				String params = "";							
				for (int i = 0; i < fds.length; i++)
					params+="?,";		
				params = params.substring(0, params.length()-1);
				PreparedStatement ps = con.prepareStatement("INSERT INTO "+tableName+" ("+fields+") VALUES ("+params+")");				
				for (int i = 1; i <= fds.length; i++) {
					String value = vls[i-1];
					char c = value.charAt(0);					
					value = value.substring(1, value.length());						
					switch (c) {
						case 'i': ps.setInt(i, Integer.parseInt(value)); break;
						case 'f': ps.setFloat(i, Float.parseFloat(value)); break;
						case 'd': {
							if (value.length() <= 10) value += " 00:00:00";
							ps.setTimestamp(i, java.sql.Timestamp.valueOf(value)); break;
						}
						case 'n': case 'v': case 's': ps.setString(i, toUTF8(value)); break;						
					}
				}
				ps.executeUpdate();								
				ps.close();
				changelog("Orders", "insert", fields, values);
				if (tableName.toLowerCase().equals("customer")) {
					replaceCustomerJS();
					//parentID zaah
					ps = con.prepareStatement("UPDATE Customer set parentID=customerID where parentID is null or parentID = 0 or parentID<>customerID");
					ps.executeUpdate();
					ps.close();
				}
			} else 
			if (action.equals("update")) {
				String params = "";
				String[] fds = fields.split(",");
				String[] vls = values.split(",");		
				for (int i = 0; i < fds.length; i++)
					params+=fds[i]+"=?,";		
				params = params.substring(0, params.length()-1);
				PreparedStatement ps = con.prepareStatement("UPDATE "+tableName+" SET "+params+" WHERE "+where);
				for (int i = 1; i <= fds.length; i++) {
					String value = vls[i-1];
					char c = value.charAt(0);
					if (tableName.equals("Customer") && fields.equals("posX,posY,_date,log")) {
						if (i == fds.length)
							c = 's';
					}
					value = value.substring(1, value.length());					
					switch (c) {
						case 'i': ps.setInt(i, Integer.parseInt(value)); break;
						case 'f': ps.setFloat(i, Float.parseFloat(value)); break;
						case 'd': {
								if (value.length() <= 10) value += " 00:00:00";
								ps.setTimestamp(i, java.sql.Timestamp.valueOf(value));
								break;
						}
						case 'n': case 'v': case 's': ps.setString(i, toUTF8(value)); break;
					}
				}
				if (ps.executeUpdate() == 0) {					
					ps.close();
					actionData("insert", tableName, fields, values, where);
				}
			} else
			if (action.equals("delete")) {
				PreparedStatement ps = con.prepareStatement("DELETE FROM "+tableName+" WHERE "+where);
				ps.executeUpdate();
				ps.close();
			}
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
					
		return 0;
	}
	
	public String convertCollectionToJson(Collection collection, String fields, String types) {
		String data = "{'results':"+collection.size()+", 'success': 'true', 'items':[";
		String[] fd = fields.split(",");
		String[] tp = types.split(",");
		for (int j = 0; j < collection.size(); j++) {
			Variant v = (Variant)collection.elementAt(j);
			data += "{";
			for (int i = 0; i < fd.length; i++) {
				switch (tp[i].charAt(0)) {
					case 's': data += "'" + fd[i] +"':'" + v.getString(fd[i]) + "',";  break;
					case 'i': data += "'" + fd[i] +"':" + v.getInt(fd[i]) + ",";  break;
					case 'f': data += "'" + fd[i] +"':" + v.getFloat(fd[i]) + ",";  break;
					case 'b': data += "'" + fd[i] +"':" + (v.getInt(fd[i])>0) + ",";  break;
				}
			}
			
			data = data.substring(0, data.length() - 1);
			data += "},";
		}
		if (collection.size() > 0)
			data = data.substring(0, data.length() - 1);
		data += "]}";
		
		return data;
	}	
	
	public String jsonTableInfo(String where) {
		String query = "SELECT o.name, ep.value AS descr, c.name AS fname, c.colid AS ordinal, c.xtype AS type, c.length FROM sys.objects o "+ 
					   "INNER JOIN sys.extended_properties ep ON o.object_id = ep.major_id INNER JOIN sys.schemas s ON o.schema_id = s.schema_id LEFT JOIN syscolumns c ON ep.minor_id = c.colid AND ep.major_id = c.id WHERE o.type IN ('V', 'U', 'P') and ep.value<>''"+
					   " ORDER BY o.Name,ordinal";
		
		return jsonData(query, "name,descr,fname,ordinal,type,length");
	}
	
	public String loginRequest(Variant w) {
		String in = "-1";
		System.out.println("PASSWORD ="+w.getString("password"));
		try {			
			Connection con = sql.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT convert(varchar,_group)+','+section FROM Users WHERE code=? and password=?");
			ps.setString(1, w.getString("user"));
			 
			ps.setString(2, w.getString("password")); 
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {		
				in = rs.getString(1);
			}

			rs.close();
			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger._.error(ex.getMessage());
		}
					
		return in;
	}	
	
	public void changelog(String tableName,String action,String fields,String values) {
		String userCode="";		
		String[] fds = fields.split(",");
		String[] vls = values.split(",");
		for(int i=0; i< fds.length; i++) {			
			if (fds[i].equals("userCode")) {
				userCode = vls[i];
				if (userCode.charAt(0) == 's') 
					userCode = userCode.substring(1, userCode.length());
			}
		}		
		
		if(tableName.toLowerCase().equals("orders")) {
			if(action.equals("delete") || action.equals("update") || action.equals("insert")) {
				try {
					Connection con = getConnection();
					String query = "UPDATE Changelog SET status=1 WHERE userCode='"+userCode+"' and action='orders'";
					if(values.equals("all"))
						query = "UPDATE Changelog SET status=1 WHERE action='orders'";
					PreparedStatement ps = con.prepareStatement(query);	
					ps.executeUpdate();
					ps.close();
				} catch (Exception ex) {
					logger._.error(ex.getMessage());
				}
			}
		}
		
		if(tableName.toLowerCase().equals("storage")) {
			if(action.equals("delete") || action.equals("update") || action.equals("insert")){
				try {
					Connection con = getConnection();
					PreparedStatement ps = con.prepareStatement("UPDATE Changelog SET status=1 WHERE action='storage'");
					ps.executeUpdate();
					ps.close();
				} catch (Exception ex) {
					logger._.error(ex.getMessage());
				}
			}
		}
	}
	
	public int actionStorage(int wareHouseId, String productCode, int count) {		
		cacheMan.manageCache("Storage","WRITER");
		int result = -1;
		try {			
			actionData("insert", "Storage_in", "_date,userCode,productCode,count,wareHouseID", "d"+convertDateTimeToString()+",s000,s"+productCode+",i"+count+",i"+wareHouseId, " "); //aguulahaas gargasan log
			
			Connection con = getConnection();			
			PreparedStatement ps = con.prepareStatement("UPDATE Storage SET _count=_count-? WHERE wareHouseID=? and productCode=? and _count>=?");
			ps.setInt(1, count);
			ps.setInt(2, wareHouseId);
			ps.setString(3, productCode);
			ps.setInt(4, count);
			result = ps.executeUpdate();
			ps.close();
			changelog("Storage", "update", "","");
		} catch (Exception ex) {
		}			
		return result;
	}
	
	public int actionStorageIn(int wareHouseId, String productCode, String userCode, int count) {		
		cacheMan.manageCache("Storage","WRITER");
		int result = -1;
		try {			
			actionData("insert", "Storage_in", "_date,userCode,productCode,count,wareHouseID", "d"+convertDateTimeToString()+",s"+userCode+",s"+productCode+",i"+count+",i"+wareHouseId, " ");
			
			Connection con = getConnection();			
			PreparedStatement ps = con.prepareStatement("UPDATE Storage SET _count=_count-? WHERE wareHouseID=? and productCode=?");
			ps.setInt(1, count);
			ps.setInt(2, wareHouseId);
			ps.setString(3, productCode);			
			result = ps.executeUpdate();
			ps.close();
			changelog("Storage", "update", "","");
		} catch (Exception ex) {
		}			
		return result;
	}
	
	public String getRentedCustomers(String userCode) {							
		String rentedCustomers = ":";			
		try {
			Connection con = getConnection();//and _dateStamp<convert(varchar, GETDATE(), 101)
			PreparedStatement ps = con.prepareStatement("SELECT discount FROM Sales WHERE flag>0 and type=1 and userCode='"+userCode+"'  GROUP by discount");			
			ResultSet rs = ps.executeQuery();					
						
			while (rs.next()) {
				rentedCustomers+=rs.getInt(1)+":";
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return rentedCustomers;
	}	
	
	public String getPlanExecuteCustomers(String userCode) {							
		String plannedCustomers = "";			
		try {
			Connection con = getConnection();			
			PreparedStatement ps = con.prepareStatement("SELECT customerCode FROM Plan_Execute WHERE executed=0 and _date>='"+convertDateToString()+"' and userCode='"+userCode+"' GROUP by customerCode");			
			ResultSet rs = ps.executeQuery();					
						
			while (rs.next()) {
				plannedCustomers+=rs.getString(1)+":";
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return plannedCustomers;
	}
	
	public String getRouteIdUsers(String weekday, String userCode) {							
		String routeID = "";			
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT "+weekday+" FROM Route_User WHERE userCode='"+userCode+"'");			
			ResultSet rs = ps.executeQuery();					
						
			if (rs.next()) {
				routeID=rs.getString(1);
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return routeID;
	}
		
	public String getNewUserCode(int type) {
		String name = "";			
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT codeChr from User_Type where _group=?");
			ps.setInt(1, type);
			ResultSet rs = ps.executeQuery();			
						
			if (rs.next()) {
				name=rs.getString(1);
				rs.close();
				ps.close();
				
				PreparedStatement ps1 = con.prepareStatement("SELECT code from Customer where code like '"+name+"%' order by code desc");				
				ResultSet rs1 = ps1.executeQuery();
				if (rs1.next()) {
					String mn = rs1.getString(1);
					mn = mn.substring(1);
					int id = Integer.parseInt(mn);
					
					mn = name + String.format("%5d", id);
					return mn;
				}
				
				rs1.close();
				ps1.close();				
			}
			
			
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return "00000";
	}
	
	public String getUserGroup_PriceTag(String userCode) {							
		String name = "";			
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT convert(varchar,_group)+':'+convert(varchar,work_type)+firstName+', '+lastName+':'+convert(varchar, (select price_tag from User_Type where _group=Users._group))+':'+convert(varchar,wareHouseId)+':'+convert(varchar, section) FROM Users WHERE code='"+userCode+"'");			
			ResultSet rs = ps.executeQuery();					
						
			if (rs.next()) {
				name=rs.getString(1);
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return name;
	}
	
	public String getMessage(String userCode) {				
		String code = "";
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT top 1 message FROM Message where (select section from Users where code='"+userCode+"') like '%'+section+'%' ORDER by id DESC");			
			ResultSet rs = ps.executeQuery();					
						
			if (rs.next()) {
				code=rs.getString(1);
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return code;
	}
	
	public String getHavePacket(String routeID) {				
		String code = "";
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT code FROM Packet WHERE routeList like '"+routeID+"%'");			
			ResultSet rs = ps.executeQuery();					
						
			if (rs.next()) {
				code=rs.getString(1);
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return code;
	}
	
	public String getNotification(String userCode) {		
		Collection collect1 = getDataCollector("select count(distinct userCode) as users, sum(requestCount) as count from orders where requestCount>0 and confirmedCount=0 and flag=0 and productCode in (select productCode from Product_Accept where userCode='"+userCode+"') and wareHouseID=(select wareHouseID from Users where code='"+userCode+"') mnp ", "users,count", "i,i");
		Collection collect2 = getDataCollector("select count(distinct userCode) as users, sum(inCount) as count from orders where flag>0 and productCode in (select productCode from Product_Accept where userCode='"+userCode+"') mnp ", "users,count", "i,i");
		Collection collect3 = getDataCollector("select distinct (select firstName from Users WHERE code=userCode and _position<5) as name from orders where  requestCount>0 and confirmedCount=0 and flag=0 and productCode in (select productCode from Product_Accept where userCode='"+userCode+"') and wareHouseID=(select wareHouseID from Users where code='"+userCode+"') mnp ", "name", "s");
		Collection collect4 = getDataCollector("select distinct (select firstName from Users WHERE code=userCode and _position<5) as name from orders where flag>0 and productCode in (select productCode from Product_Accept where userCode='"+userCode+"') and wareHouseID=(select wareHouseID from Users where code='"+userCode+"') mnp ", "name", "s"); 
		
		String data = "{'results':"+3+", 'success': 'true', 'items':[";
		
		if (collect1.size() > 0 && collect1.elementAt(0).getInt("users")>0) {
			String users = "";
			for (int i = 0; i < collect3.size(); i++) {
				Variant w = (Variant)collect3.elementAt(i);
				if (w.get("name").length() > 3)
					users += "<b>"+w.get("name")+"<b>, ";
			}
			
			data += "{'content':'"+getLanguageWord(258)+"','text':'"+collect1.elementAt(0).get("users")+" борлуулагч "+collect1.elementAt(0).get("count")+" ширхэг бараа захиалага илгээсэн байна. <br>"+users+"'},";			
		}
		if (collect2.size() > 0 && collect2.elementAt(0).getInt("users") > 0) {
			String users = "";
			for (int i = 0; i < collect4.size(); i++) {
				Variant w = (Variant)collect4.elementAt(i);
				if (w.get("name").length() > 3)
					users += "<b>"+w.get("name")+"</b>, ";
			}
			data += "{'content':'"+getLanguageWord(575)+"','text':'"+collect2.elementAt(0).get("users")+" борлуулагчийн "+collect2.elementAt(0).get("count")+" ширхэг бараа олголт хийнэ үү !<br>"+users+"'}";
		}
				
		data += "]}";
		
		return data;
	}
	
	public float getCurrentCarSize(String userCode) {
		float size = 0;
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select (select top 1 lastCount*(select size from Product where code=productCode) from Orders where userCode='"+userCode+"' and productCode=b.productCode and flag=0 and (inCount>0 or soldCount>0) order by _date desc) "+
														"from Orders as b where userCode='"+userCode+"' and productCode<>'nul' group by productCode");

			ResultSet rs = ps.executeQuery();					
			while (rs.next()) {
				size += rs.getFloat(1);
			}
			if (size < 0) size = 0;			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return size;
	}
	
	public float getCurrentCarSizeOut(String userCode) { //borluultand gartsan ni
		float size = 0;
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select (select sum(quantity)*(select size from Product where code=productCode)) from Sales where userCode='"+userCode+"' and productCode<>'nul' and _dateStamp>=DATEADD(dd, 0, DATEDIFF(dd, 0, CURRENT_TIMESTAMP)) group by productCode");			
			ResultSet rs = ps.executeQuery();					
			while (rs.next()) {
				size += rs.getFloat(1);
			}
						
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		return size;
	}
	
	public String getCarsAvailSize() {
		Collection collect = new Collection();
		String data = "";
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select cars.code, userCode, [space] from users as b join Cars on userCode=b.code where _group="+CAR_TUGEEGCH_TYPE);
			ResultSet rs = ps.executeQuery();					
			while (rs.next()) {
				Variant v = new Variant();
				v.put("code", rs.getString(1));
				v.put("userCode", rs.getString(2));
				float space = rs.getFloat(3);
				v.put("space", space);
				float used = getCurrentCarSize(rs.getString(2));//-getCurrentCarSizeOut(rs.getString(2));
				v.put("used", used);
				v.put("free", (space-used));
				collect.addCollection(v);
			}
						
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger._.error(ex.getMessage());
		}
		
		data = convertCollectionToJson(collect, "code,userCode,space,used,free", "s,s,f,f,f");
		return data;				
	}
	
	public boolean checkRecord(String values) {
		boolean result = false;
		try {
			Connection con = getConnection();
						
			String[] vls = values.split(",");
			int[] indexes = {1, 2, 3, 6, 7, 13};
			PreparedStatement ps = con.prepareStatement("SELECT * FROM Sales WHERE customerCode=? and userCode=? and productCode=? and type=? and quantity=? and (abs(DATEDIFF(second, current_timestamp, _dateStamp))<5 or ticketID=?)");//
			ps.setString(1, vls[indexes[0]].substring(1, vls[indexes[0]].length()));
			ps.setString(2, vls[indexes[1]].substring(1, vls[indexes[1]].length()));
			ps.setString(3, vls[indexes[2]].substring(1, vls[indexes[2]].length()));
			ps.setInt(4, Integer.parseInt(vls[indexes[3]].substring(1, vls[indexes[3]].length())));
			ps.setInt(5, Integer.parseInt(vls[indexes[4]].substring(1, vls[indexes[4]].length())));
			ps.setInt(6, Integer.parseInt(vls[indexes[5]].substring(1, vls[indexes[5]].length())));
			ResultSet rs = ps.executeQuery();		
			System.out.println("found row = "+rs.getRow());
			if (rs.next()) {
				result = true;
			}
			rs.close();
			ps.close();
		} catch (Exception ex) {
			result = false;			
		}
					
		return result;
	}
	
	public boolean checkRecordForOrder(String values) {		
		boolean result = false;
		try {
			Connection con = getConnection();
						
			String[] vls = values.split(",");
			int[] indexes = {1, 3, 4, 2, 7};
			System.out.println(values);
			String ticketID = vls[indexes[4]].substring(1, vls[indexes[4]].length());
			
			String userCode = vls[indexes[0]].substring(1, vls[indexes[0]].length());
			String productCode = vls[indexes[1]].substring(1, vls[indexes[1]].length());
			int requestCount = Integer.parseInt(vls[indexes[2]].substring(1, vls[indexes[2]].length()));				
			String customerCode = vls[indexes[3]].substring(1, vls[indexes[3]].length());

			System.out.println(userCode+" "+productCode+" "+requestCount+" "+customerCode+" "+ticketID+" asdkljflkajsdlfkjalksdjfkljasdklfjklasjdfkljaslkdjflaksjdflkajsdlkfj");
			
			if (ticketID != null && ticketID.length() > 5) {
				PreparedStatement ps = con.prepareStatement("SELECT * FROM Orders WHERE userCode=? and productCode=? and requestCount=? and customerCode=? and ticketID="+ticketID);			
				ps.setString(1, vls[indexes[0]].substring(1, vls[indexes[0]].length()));
				ps.setString(2, vls[indexes[1]].substring(1, vls[indexes[1]].length()));
				ps.setInt(3, Integer.parseInt(vls[indexes[2]].substring(1, vls[indexes[2]].length())));				
				ps.setString(4, vls[indexes[3]].substring(1, vls[indexes[3]].length()));				
				 
				ResultSet rs = ps.executeQuery();		 
				System.out.println("found row = "+rs.getRow());
				if (rs.next()) {
					result = true;
				}
				rs.close();
				ps.close();
			}
		} catch (Exception ex) {
			result = false;
		}
					
		return result;
	}
	
	public int checkUser(String userCode) {
		int result = 1;
		try {
			Connection con = getConnection();									
			PreparedStatement ps = con.prepareStatement("SELECT work_type FROM Users WHERE code=?");//
			ps.setString(1, userCode);
			ResultSet rs = ps.executeQuery();			
			if (rs.next()) {
				result = rs.getInt(1);
			}
			rs.close();
			ps.close();
		} catch (Exception ex) {
			result = 1;
		}
					
		return result;
	}
	
	public String getJsonTmpl(int column, ResultSetMetaData rsmd, String[] fds) {	
		String data = "";
		try {
			for (int i = 0; i < column; i++) {				
				String type = rsmd.getColumnTypeName(i+1); 
				String comma = commaType[(int)type.charAt(0)];
				if (fds[i].indexOf(" as ") != -1)
					fds[i] = fds[i].substring(fds[i].lastIndexOf(' ')+1, fds[i].length());
							
				data += "'"+fds[i]+"':"+comma+"#"+fds[i]+comma+",";
			}
		}
		catch (Exception e) {
			
		}
		
		return data;
	}
	
	public void updateCustomerStatus(String customerCode,int status) {	    				    		
		try {
			Connection con = getConnection();						
			if(status == 1 ){
				PreparedStatement ps;
				ps = con.prepareStatement("UPDATE Customer SET active=0 WHERE code='"+customerCode+"'");
				ps.executeUpdate();
				ps.close();
			}
			if(status == 0 ){
				PreparedStatement ps;
				ps = con.prepareStatement("UPDATE Customer SET active=1 WHERE code='"+customerCode+"'");
				ps.executeUpdate();
				ps.close();
			}
			if(status == 0){
				PreparedStatement ps1;
				ps1 = con.prepareStatement("INSERT INTO Route_customer(routeID,customerCode) SELECT subid,code FROM customer WHERE code='"+customerCode+"'");
				ps1.executeUpdate();
				ps1.close();
			}
			if(status == 1){
				PreparedStatement ps2;
				ps2 = con.prepareStatement("DELETE FROM Route_Customer WHERE customerCode='"+customerCode+"'");
				ps2.executeUpdate();
				ps2.close(); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
    }
	
	public void updateCustomerRouteID(String customerCode,String routeID) {	    				    		
		try {
			Connection con = getConnection();									
			PreparedStatement ps1 = con.prepareStatement("UPDATE Route_customer SET routeID='"+routeID+"' WHERE customerCode='"+customerCode+"'");
			ps1.executeUpdate();
			ps1.close();		
			
			PreparedStatement ps2 = con.prepareStatement("UPDATE Customer SET subid='"+routeID+"' WHERE code='"+customerCode+"'");
			ps2.executeUpdate();
			ps2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
    }
	
	public String liveSearch(String key) {		
		String result = getDataCollectorByJson("select code,firstName as name,(select descr from User_Type where _group=Users._group) as descr,(select descr from LiveSearch where id=1) as type,(select top 1 datediff(s, '1970-01-01 00:00:00', _dateStamp) from Sales where userCode=code order by _dateStamp desc) as lastPost from Users where firstName like N'%"+key+"%' or lastName like N'%"+key+"%' union select code,name,(select descr from User_Type where _group=Customer.type) as descr,(select descr from LiveSearch where id=2) as type,(select top 1 datediff(s, '1970-01-01 00:00:00', _dateStamp) from Sales where customerCode=code order by _dateStamp desc) as lastPost from Customer where name like N'%"+key+"%' or code like N'%"+key+"%' or location like N'%"+key+"%'", "code,name,type,descr,lastPost", "s,s,s,s,s");
		
		return result;
	}
	
	public String liveDetail(String find, String mode) {
		Collection all = new Collection();		
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT _key,query,attr FROM ModeQuery WHERE mode=?");
			ps.setString(1, mode);
			ResultSet rs = ps.executeQuery();								
			while (rs.next()) {
				String query = rs.getString(2);
				query = query.replace("?", "'"+find+"'");
				System.out.println(query);
				Collection c = getDataCollector(query, "value", "s");
				if (c.size() > 0) {
					Variant w = new Variant();
					w.put("data", rs.getString(1));
					w.put("detail", c.elementAt(0).getString("value"));
					w.put("attr", rs.getString(3));
					all.addCollection(w);
				}
			}
			
			rs.close();
			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return convertCollectionToJson(all, "data,attr,detail", "s,s,s");
	}
	
	private void replaceCustomerJS()
    {
    	String[] customers = new String[85000];
    	sqlRoute shared = new sqlRoute();
    	Connection con = shared.getConnection();			
		PreparedStatement ps;
		try {
			ps = con.prepareStatement("select 'customer['+''''+code+''''+']='+''''+name+'|'+location+''''+';' as customer from Customer");
			ResultSet rs = ps.executeQuery();
			customers[0]="var customer = [];";
			int i=1;
			while(rs.next())
			{
					customers[i] = rs.getString("customer");
					i+=1;
			}rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String output = "C:\\Users\\Administrator\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\vita\\js\\customer.js";
		
		try
		{
		    PrintWriter pr = new PrintWriter(output, "UTF-8");    

		    for (int i=0; i<customers.length; i++)
		    {
		    	if(customers[i]!=null)
		        pr.println(customers[i]);
		    }
		    pr.close();
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		    System.out.println("No such file exists.");
		}
    }
}
