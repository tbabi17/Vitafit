package oss.core;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import oss.report.Collection;
import oss.report.Variant;

  
public class toolController implements constantValues {		
	public static String yearData[][] = {{"1/1-1/7",
										"1/8-1/14",
										"1/15-1/21",
										"1/22-1/28",
										"1/29-1/31",
										"2/1-2/7",
										"2/8-2/14",
										"2/15-2/21",
										"2/22-2/28",
										"2/29-2/29",
										"3/1-3/7",
										"3/8-3/14",
										"3/15-3/21",
										"3/22-3/28",
										"3/29-3/31",
										"4/1-4/7",
										"4/8-4/14",
										"4/15-4/21",
										"4/22-4/28",
										"4/29-4/30",
										"5/1-5/7",
										"5/8-5/14",
										"5/15-5/21",
										"5/22-5/28",
										"5/29-5/31",
										"6/1-6/7",
										"6/8-6/14",
										"6/15-6/21",
										"6/22-6/28",
										"6/29-6/30",
										"7/1-7/7",
										"7/8-7/14",
										"7/15-7/21",
										"7/22-7/28",
										"7/29-7/31",
										"8/1-8/7",
										"8/8-8/14",
										"8/15-8/21",
										"8/22-8/28",
										"8/29-8/31",
										"9/1-9/7",
										"9/8-9/14",
										"9/15-9/21",
										"9/22-9/28",
										"9/29-9/30",
										"10/1-10/7",
										"10/8-10/14",
										"10/15-10/21",
										"10/22-10/28",
										"10/29-10/31",
										"11/1-11/7",
										"11/8-11/14",
										"11/15-11/21",
										"11/22-11/28",
										"11/29-11/30",
										"12/1-12/7",
										"12/8-12/14",
										"12/15-12/21",
										"12/22-12/28",
										"12/29-12/31"}};
	public static String[] monthName = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	public static int langid = 0;
	public static String loggedUser = "", mode = "";
	 
	public static int order_accept_state = 0; //aguulahaas gargadag alhamiig algasah tohioldol
		
	public int getInt(String value) {
		return Integer.parseInt(value.substring(1, value.length()));
	}
	
	public String getString(String value) {
		return value.substring(1, value.length());
	}
	
	public static String toUTF8(String src) {
		if (src == null) return src;
		
		try {
			byte[] bytes = src.getBytes("ISO8859_1");
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) { 
			e.printStackTrace();
		}
		return src;
    }	
	
	public static String beforeDay(String date) {        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
        try {                   	
        	calendar.setTime(java.sql.Date.valueOf(date));
        	calendar.add(Calendar.DATE, -1);
            return dateFormat.format(calendar.getTime());            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }
	
	public static String convertDateToString() {        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
        try {           
            return dateFormat.format(calendar.getTime());            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }
	
	public static String convertDateTimeToString() {        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        try {           
            return dateFormat.format(calendar.getTime());            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }	
	
	public static String convertTimeToString() {        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); 
        try {           
            return dateFormat.format(calendar.getTime());            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }
	
	public static String getWeekDay(String date) {		
		date = date.replaceAll("/", "-");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(java.sql.Date.valueOf(date));
		String[] weekdays = {"sun", "mon", "thue", "wed", "thur", "fri", "sat"};
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);
		
		return weekdays[weekday-1];
	}		
	
	public static String today() {        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
        try {           
            return dateFormat.format(calendar.getTime());            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }
	
	public static String dayNumber() {        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd"); 
        try {           
            return dateFormat.format(calendar.getTime());            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }
	
	public Collection getCollection(String fields, String types, String json) {
		Collection collection = new Collection();
		String[] fd = fields.split(",");
        String[] tp = types.split(",");
        try {
            JSONObject outer = new JSONObject(json);
            if (outer != null) {
            	int count = outer.getInt("results");            	
                JSONArray inner = outer.getJSONArray("items");
                if (inner != null) {
	                for (int i = 0; i < count; i++) {
	                	Variant v = new Variant();	                	
	                	JSONObject item = inner.getJSONObject(i);	                	
	                	for (int j = 0; j < fd.length; j++) {	                		
	                		if (fd[j].indexOf("%20as%20") != -1)
	    						fd[j] = fd[j].substring(fd[j].lastIndexOf('0')+1, fd[j].length());		                		
	                		switch (tp[j].charAt(0)) {
	                			case 's': {		                				
	                				String aString = item.getString(fd[j]);
	                				if (aString == null) aString = "";	                				
	                				v.put(fd[j], aString);
	                			} break;
	                			case 'd': {		                				
	                				String aString = item.getString(fd[j]);
	                				if (aString == null) aString = "";	                				
	                				v.put(fd[j], aString);
	                			} break;
	                			case 'i': {		                			                				
	                				Integer aInt = item.getInt(fd[j]);	                				
	                				v.put(fd[j], aInt.toString());		                				
	                			} break;
	                			case 'f': {		                			                				
	                				Double aFloat = item.getDouble(fd[j]);	                				
	                				v.put(fd[j], aFloat.toString());		                				
	                			} break;
	                		}
	                	}		                	
	                	collection.addCollection(v);	
	                }
                }
            }
        } catch (Exception e) {        	
        	e.printStackTrace();        	
        	return new Collection();
        }
        
        return collection;
	}
	
	public static String convertCurrency(long value) {	
		double payment = value;		
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		String st = nf.format(payment); 
		return st.substring(1, st.length());			  
	} 
	  
	public static String npString(String q, String where) {
		if (q.indexOf(" inp ") != -1) {										
			q = q.replaceAll(" inp ", " ");//and productCode in (select productCode from Product_Accept where userCode='"+loggedUser+"')");
		} 
		if (q.indexOf(" cnp ") != -1) {			
			q = q.replaceAll(" cnp ", " 1=1 ");//section like '%"+where+"%'");
		} 
		if (q.indexOf(" vnp ") != -1) {						
			q = q.replaceAll(" vnp ", " ");//vendor='"+where+"'");
		} 
		if (q.indexOf(" pnp ") != -1) {						
			q = q.replaceAll(" pnp ", " vendor='"+mode+"'");
		} 
		  
		if (q.indexOf(" mnp ") != -1) {
			q = q.replaceAll(" mnp ", " ");//and ((userCode in (select code from Users where manager='"+loggedUser+"') or (userCode in (select code from Users where section like '%"+mode+"%') and (select _group from Users where code='"+loggedUser+"')<>20))) ");
		}
		if (q.indexOf(" unp ") != -1) {
			q = q.replaceAll(" unp ", " ");//and (manager='"+loggedUser+"' or (section like '%"+mode+"%' and (select _group from Users where code='"+loggedUser+"')<>20)) and _group<15 and _position<5 ");
		}
		
		if (q.indexOf(" uunp ") != -1) {
			q = q.replaceAll(" uunp ", " ");//and (userCode in (select code from Users where manager='"+loggedUser+"')) ");
		}
		
		if (q.indexOf(" mmnp ") != -1) {
			q = q.replaceAll(" mmnp ", " ");//and (manager='"+loggedUser+"' or section='"+mode+"') ");
		}
		
		if (q.indexOf(" mana ") != -1) {
			if(loggedUser.equals("vita"))
				q = q.replaceAll(" mana ", " and 1=1 ");
			else   
				q = q.replaceAll(" mana ", " and (manager='"+loggedUser+"') ");
		}
		return q;
	}
	
	public static boolean isCurrencyField(String field) {
		field = field.toLowerCase();
		return field.indexOf("amount") != -1 || field.indexOf("price") != -1  || field.indexOf("sum") != -1 || field.indexOf("loan") != -1 || field.indexOf("sale") != -1; 
	}
	
	public static boolean isPrecentField(String field) {
		field = field.toLowerCase();
		return field.indexOf("precent") != -1; 
	}
	
	public String getWeekRanges(int year, int month, int week) {
		int p = -1;
		for (int i = 0; i < yearData[year-2012].length; i++) {
			String str = yearData[year-2012][i];
			if (str.startsWith((month+1)+"/")) {								
				if (week == 0) {
					p = i;
					break;
				}
				week--;
			}
		}		
		if (p == -1)
			return "";
		
		return yearData[year-2012][p];		
	}
	
	public int getWeekNumber(int year, int month) {
		int week = 0;
		for (int i = 0; i < yearData[year-2012].length; i++) {
			String str = yearData[year-2012][i];
			if (str.startsWith((month+1)+"/")) {												
				week++;
			}
		}		
		
		return week;
	}
	
	public List<String> obtenerFechasDiariasIntervalo(Date fechaInicial, Date fechaFinal)
	{
	    List<String> dates = new ArrayList<String>();
	    Calendar calendar = new GregorianCalendar();
	    calendar.setTime(fechaInicial);
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    
	    while (calendar.getTime().before(fechaFinal))
	    {
	        Date resultado = calendar.getTime();
	        dates.add(df.format(resultado));
	        calendar.add(Calendar.DATE, 1);
	    }
	    return dates;
	}
	
	public static int getTicketId() {
    	Calendar now = Calendar.getInstance();    	
    	int hour = now.get(Calendar.HOUR_OF_DAY);
    	int minute = now.get(Calendar.MINUTE);
    	int second = now.get(Calendar.SECOND);    	    	    
    	
    	return (int)System.currentTimeMillis(); //hour*3600+minute*60+second;
	}
	
	public static void main(String [] arg) {
		System.out.println(convertDateTimeToString());
	}
}
