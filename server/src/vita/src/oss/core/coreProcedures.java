package oss.core;

import java.util.Hashtable;

import oss.app.httpConnection;
import oss.app.mobileConnection;
import oss.app.reportConnection;
import oss.cache.logger;
import oss.report.Variant;

public class coreProcedures extends sharedProcedures {
	public httpConnection httpConn;
	public mobileConnection mobileConn; 
	public reportConnection reportConn;		 
	
	public String writerAction(Variant w) {  
		String func = w.get("func");
		String action = w.get("action"); 
		String tableName = w.get("table");  
		cacheMan.manageCache(tableName, action);
		  
		if (func.equals("form_action")) {
			String params = w.get("where");
			if (params != null) {				
				if (tableTypes == null || tableTypes.size() == 0)
					getAllTablesParameters();				
				Hashtable<String, String> temp = (Hashtable<String, String>)tableTypes.get(tableName);				
				if (temp != null) {
					String[] fv = params.split(",");			
					String fields = "", values = ""; 
					String where = "";
					int mod = 0;
					for (int i = 0; i < fv.length; i++) {
						String fd = fv[i].substring(0, fv[i].indexOf("="));
						if (fd.equals("id") || fd.toLowerCase().endsWith("id")) { 
							mod = i;
							break;
						}
					}
					
					for (int i = 0; i < fv.length; i++) {									
						String fd = fv[i].substring(0, fv[i].indexOf("="));
						String tp = temp.get(fd).toString();
						
						if (i == mod) {
							where = fd + "=" + getComma(tp)+fv[i].substring(fv[i].indexOf("=")+1, fv[i].length())+getComma(tp); 
						} else {
							fields += fd+",";
							String v = fv[i].substring(fv[i].indexOf("=")+1, fv[i].length());
							if (tp.charAt(0) == 'i' && (v.equals("on") || v.equals("off"))) 
								v = v.equals("on")?"1":"0"; 
							v = v.replace(';', ',');
							v = v.trim();
							values += tp + v+",";				
						}
					}				
					
					fields = fields.substring(0, fields.length() - 1);
					values = values.substring(0, values.length() - 1);					
					httpConn.actionData("update", tableName, fields, values, where);
				}
			}				
		} else
		if (func.equals("action_rentpayment")) {				
			String values = w.get("types");							
			mobileConn.actionMobileData("rentpayment", "Sales", "_dateStamp,customerCode,userCode,productCode,posX,posY,type,quantity,price,amount,discount,flag,userType", values, " ");
		} else
		if (func.equals("action_sale")) {
			String values = w.get("types");				
			mobileConn.actionMobileData("insert", "Sales", "_dateStamp,customerCode,userCode,productCode,posX,posY,type,quantity,price,amount,discount,flag", values, " ");
		} else
		if (func.equals("action_storage")) {	
			action = "update";							
			int _count = w.getInt("types");
			
			String where = w.get("where");
			String[] wh = where.split(",");
			
			httpConn.actionStorageIn(Integer.parseInt(wh[0]), wh[2], wh[3], _count); // zooj baigaa aguulah				
			httpConn.actionStorageIn(Integer.parseInt(wh[1]), wh[2], wh[3], -_count); // nemegdej baigaa aguulah
		} else	 
		if (func.equals("action_back_storage")) {													
			String where = w.get("where");
			String[] wh = where.split(",");
			int _count = Integer.parseInt(wh[3]);
			
			httpConn.actionData("delete", "Orders", " ", " ", " flag=1 and userCode='"+wh[1]+"' and productCode='"+wh[2]+"' and inCount="+_count);
			httpConn.actionData("update", "Orders", "confirmedCount", "i0", " userCode='"+wh[1]+"' and productCode='"+wh[2]+"' and confirmedCount="+_count+" and requestCount>0 and id=(select top 1 id from Orders where userCode='"+wh[1]+"' and productCode='"+wh[2]+"' and requestCount>0 and confirmedCount="+_count+" order by _date desc)");
			httpConn.actionStorage(Integer.parseInt(wh[0]), wh[2], -_count);								
		} else
		{ //any update action
			action = func;
			tableName = w.get("table");
			String fields = w.get("fields");
			String values = w.get("types");
			String where = w.get("where");
			values = values.replaceAll("CURRENT_TIMESTAMP", mobileConnection.convertDateTimeToString());
			httpConn.actionData(action, tableName, fields, values, where);
		}
		return "";
	}
	
	public String anyJSON(Variant w) {
		String tableName = w.get("table");
		String fd = w.get("fields");
		String where = w.get("where");				
		where = where.replace("!", "<");
		where = where.replace("@", ">");		
		
		if (fd.indexOf("[customerCode]") != -1) {
			fd = fd.replace("[customerCode]", "(SELECT _name FROM Customer WHERE Customer.code=Sales.customerCode) as customerCode");
		}
		if (where == null) where = "";
		if (tableName.equals("Customer")) {
			where = w.get("where");
			String result = httpConn.getDataCollectorByJson("select Customer.code,name+' ['+location+']' as name,parentID from Customer", "code,name,parentID", "s,s,i");
			cacheMan.saveCache(tableName+where, result);	
			cacheMan.saveAlternateCache(tableName, result);
			return result;
		}
		String result = "";
		if (w.getInt("limit") > 0)
			result = httpConn.jsonData(tableName, fd, where, w.getString("query"), w.getInt("start"), w.getInt("limit"));		
		else
			result = httpConn.jsonData(tableName, fd, where);
		
		cacheMan.saveCache(tableName+where+w.getString("query")+w.getInt("start")+w.getInt("limit"), result);	
		cacheMan.saveAlternateCache(tableName, result);
		
		return result;
	}
	
	public String moduleJSON(String func) {
		Variant w = httpConn.getModuleQuery(func);		
		if (w.get("tableName").length() > 0) {			
			String result = httpConn.jsonData(w.get("tableName"), w.get("fields"), w.get("_where"));
			cacheMan.saveAlternateCache(func, result);
			return result;
		}
		
		return "";
	}
	
	public String saleJSON(String func, Variant w) {
		if (func.equals("_main_sale_data")) {
			String[] wh= w.get("where").split(",");
			wh[0] = wh[0].replace("!", "<");
			wh[0] = wh[0].replace("@", ">");					
			return httpConn.jsonSalesData(wh[0], wh[1], wh[2], wh[3], wh[5]);
		} else
		if (func.equals("_main_sale_data_brand")) {					
			String[] wh= w.get("where").split(",");
			wh[0] = wh[0].replace("!", "<");
			wh[0] = wh[0].replace("@", ">");									
			return httpConn.jsonSalesDataBrand(wh[0], wh[1], wh[3], wh[5]);
		} else
		if (func.equals("_main_sale_data_pre")) {
			String[] wh= w.get("where").split(",");
			wh[0] = wh[0].replace("!", "<");
			wh[0] = wh[0].replace("@", ">");					
			return httpConn.jsonSalesDataPre(wh[0], wh[1], wh[2], wh[3], wh[5]);
		}
		
		return "";
	}
	
	public String otherJSON(String func, Variant m) {
		Variant w = httpConn.getRemoteQuery(func);		
		
		if (w.get("query").length() > 10) {			
			String[] wh = m.get("where").split(",");
			m.get("where");
			String query = w.get("query");
			int t = 0;
			String comma = "wareHouseID,discount,";
			while (query.indexOf("?") != -1) {
				String cm = "'";
				if (t < wh.length && comma.indexOf(wh[t]+",") != -1) cm = "";
				int q = query.indexOf("?");
				if (q+1 < query.length() && query.charAt(q+1) >= '0' && query.charAt(q+1) <= '9') {
					int p = (int)query.charAt(q+1)-(int)'0';
					System.out.println(query);
					query = query.substring(0, q) + cm+wh[p]+cm + query.substring(q+2, query.length());
				}
				else {
					System.out.println(query+" "+m.get("where")+" "+t);
					query = query.substring(0, q) + cm+wh[t]+cm + query.substring(q+1, query.length()); 							
					t++;
				}
			}			
			System.out.println(query);
			String result = httpConn.getDataCollectorByJson(query, w.get("fields"), w.get("_type"));			
			cacheMan.saveAlternateCache(func, result);
			cacheMan.saveCache("where", m.get("where"));
			return result;
		}
		
		w = httpConn.getOSSQuery(func);	
		if (w.get("query").length() > 10) {
			String[] wh = m.get("where").split(",");
			String[] wt = w.get("wtypes").split(",");
			String query = w.get("query");
			String comma = "";
			for (int i = 0; i < w.getInt("count"); i++) {
				if (wt[i].equals("s")) comma = "'"; else comma = "";
				query = query.replaceAll(":"+i, comma+wh[i]+comma);
			}
			query = npString(query, "");			
			
			cacheMan.saveQueryCache(func, query);
			String result = "";
			if (m.getInt("limit") > 0)
				result = httpConn.getDataCollectorByJson(query, w.get("fields"), w.get("types"), m.getInt("start"), m.getInt("limit"), m.getString("query"));
			else
				result = httpConn.getDataCollectorByJson(query, w.get("fields"), w.get("types"));
			
			cacheMan.saveAlternateCache(func, result);
			return result;
		}				
			
		return constantJSON(m, func);
	}
	
	public String constantJSON(Variant w, String func) {		
		String where = "", result = "";
		if (func.equals("_get_table_info")) {
			where = w.get("where");
			result = httpConn.jsonTableInfo(where);			
			return result;
		} else
		if (func.equals("_notification")) {			
			return httpConn.getNotification(w.get("where"));
		} else 
		if (func.equals("_customer_list")) { 
			where = w.get("where");
			result = httpConn.jsonData("Customer", "customerID,code,name,location,type,pID,_owner,phone1,staff,phone2,active,loan,stand,belong,_date,ptag,subid,subid1,secCode", " WHERE subID='"+where+"' or subid1='"+where+"'", w.getString("query"), w.getInt("start"), w.getInt("limit"));
			String result1 = httpConn.jsonData("Customer", "customerID,code,name,location,type,pID,_owner,phone1,staff,phone2,active,loan,stand,belong,_date,ptag,subid,subid1,secCode", " WHERE subID='"+where+"' or subid1='"+where+"'", w.getString("query"), w.getInt("start"), 1000);
			cacheMan.saveAlternateCache("Customer", result);
			cacheMan.saveAlternateCache("_customer_list", result1);
			return result;
		} else
		if(func.equals("_update_customer_status")) 
		{						
			where = w.get("where");
			String []wh = where.split(",");						
			httpConn.updateCustomerStatus(wh[0],Integer.parseInt(wh[1]));
			return "success";
		} else
		if(func.equals("_update_customer_routeid"))
		{						
			where = w.get("where");
			String []wh = where.split(",");						
			httpConn.updateCustomerRouteID(wh[0], wh[1]);
			return "success";
		} else
		if (func.equals("_cars_space")) {					
			return httpConn.getCarsAvailSize();
		} else
		if (func.equals("_live_search")) {
			where = w.get("where");
			return httpConn.liveSearch(where); 
		}
		
		return "";
	}
	
	public String remoteJSON(String func, Variant m) {
		Variant w = httpConn.getRemoteQuery(func);
		String result = "";
		if (w.get("query").length() > 0) {			
			String q = w.get("query");
			q = npString(q, m.get("where"));
								 
			result=httpConn.getDataCollectorByJson(q, w.get("fields"), w.get("_type"));			
			cacheMan.saveAlternateCache(func, result);
			return result;
		}
		
		return "";
	}
	
	public String controlJSON(String func, Variant m) {
		if (!func.startsWith("_"))
			return anyJSON(m);				
		else
		if (func.startsWith("_remote"))
			return remoteJSON(func, m);
		else
		if (func.startsWith("_module"))
			return moduleJSON(func);
		else		
		if (func.startsWith("_main"))
			return saleJSON(func, m);
		else 
			return otherJSON(func, m);		
	}
	
	public void writerMobileAction(Variant w) {
		String action = w.get("action");
		String tableName = w.get("table");
		String fields = w.get("fields");
		String values = w.get("types");
		String where = w.get("where");	
		
		if (values.indexOf("CURRENT_TIMESTAMP:") != -1) {			
			String[] vls = values.split(",");
			String values1 = "";
			for (int i = 0; i < vls.length; i++) {
				String v = vls[i];
				
				if (v.indexOf("CURRENT_TIME") != -1) {
					String date = v.substring(v.indexOf(":")+1, v.length());						
					if (date.length() == 10 && !date.equals(convertDateToString())) 
						values1 += "d"+date+" 18:00:00,";
					else
						values1 += "d"+convertDateTimeToString()+",";
				} else					
					values1 += v+",";
			}			 
			logger._.debug("Old="+values);
			values = values1;
			logger._.debug("New="+values);			
		} else 
			values = values.replaceAll("CURRENT_TIMESTAMP", convertDateTimeToString());
					
		if (where.equals("packet")) {			
			if (tableName.equals("Sales")) 
				mobileConn.actionPacketData(action, tableName, fields, values, where);
			else
				mobileConn.actionOrderPacketData(action, tableName, fields, values, where);
		}
		else
			mobileConn.actionMobileData(action, tableName, fields, values, where);
	}
	
	public String touchHandler(Variant w) {
		String func = w.get("func");
		if (func.startsWith("_remote")) {
			String result = remoteJSON(func, w);
			result = result.replaceAll("'", "\"");
			return result;
		}
		
		String where = w.get("where");
		where = where.replace("!", "<");
		where = where.replace("@", ">");
		
		return mobileConn.jsonDataTouch(w.get("table"), w.get("fields"), where);
	}
	
	public String webHandler(Variant w) {		
		String action = w.get("action");
		if (action.equals("WRITER")) {
			writerAction(w);
			customAction(w);
		} else
		if (action.equals("SELECT")) {
			String func = w.get("func");	
			String result = "";
			String tableName = w.get("table");
			String where = w.get("where");			
			
			if (!func.startsWith("_") && (result = cacheMan.loadCache(tableName+where+w.getString("query")+w.getInt("start")+w.getInt("limit"))).length() > 0)
				return result;													
			
			result = controlJSON(func, w);
			if (result.length() > 0)
				return result;
			else {
				result = reportSelect(w);				
				if (result.length() == 0)
					return customSelect(w);
				else
					return result;
			}
		}
		
		return "";
	}
	
	public String mobileHandler(Variant w) {
		logger._.debug(convertDateToString());		
		if (w.get("func").equals("WRITER"))
			writerMobileAction(w);
		else {
			if (w.get("func").equals("GET")) {
				String tableName = w.get("table");
				String fd = w.get("fields");
				String where = w.get("where");	
				if (where == null) where = "";
				else where = where.replace('.', '%');
				where = where.replace("!", "<");
				where = where.replace("@", ">");
				where = where.replace(" thus ", " thur ");
				fd = fd.replace("!", "<");
				fd = fd.replace("@", ">");
				return mobileConn.jsonData(tableName, fd, where);
			} else {
				if (w.get("func").equals("Changelog")) {
					String[] where = w.get("where").split(",");
					String result = mobileConn.jsonData("select userCode,action,status from changelog where userCode='"+where[0]+"'", "userCode,action,status");
					mobileConn.actionData("update", "changelog", "status", "i0", "userCode='"+where[0]+"'");
					System.out.println(result);
					return result;
				} else				
					return customMobileSelect(w);
			}
		}
		
		return "success";
	}		
	
	public String reportHandler(Variant w) {
		String fields = w.get("fields");
		String types = w.get("types");	
		String template = w.get("template");
		String where = w.get("where");
		String spec = "";
		
		String[] names = template.split(",");
		if (spec.indexOf(names[1]) != -1) 
			return reportConn.doSpecialXls(names[1]);
				
		String json = cacheMan.loadAlternateCache(names[0]);
		if (json == null || json.length() == 0)
			json = cacheMan.loadAlternateCache(names[1]);
		
		if (json != null && json.length() > 0) {
			int rindex = 1;
			String wh = cacheMan.loadCache("where");			
			
			return reportConn.doXls(template, fields, types, where, json, rindex, wh);
		}
		
		return "";
	}					
	
	public void customAction(Variant w) {}
	public String customSelect(Variant w) { return ""; }
	public String reportSelect(Variant w) { return ""; }	
	public String customMobileSelect(Variant w) { return ""; }				
}
