package mxc.app.engine;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPInputStream;

import mxc.app.engine.MasterActivity.TaskExecution;

import org.json.JSONArray;
import org.json.JSONObject;

import oss.android.vita.app.BixolonManager;
import oss.android.vita.app.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.squareup.okhttp.OkHttpClient;

public class BasicActivity extends MapActivity {	
	public String GW_URL = getUrl()+"postGW";
	
	public static final String CALCULATOR_PACKAGE = "com.android.app.calculator";  
	public static final String CALCULATOR_CLASS = "com.android.app.calculator.Calculator"; 
	
	public static final int RELATIVE = 1;
	public static final int LINEAR = 2;
	public static final int FRAME = 3;
	
	public static final int VAN_SALLING = 1;
	public static final int PRE_SALLING = 2;
	
	public static final int SUCCESS = 10;
	public static final int FAILED = 11;
	public static final int NOSTARTED = 12;
	
	public static final int DATE_DIALOG_ID = 20;
	public static final int DATE_DIALOG_ID_1 = 21;
	
	public static final int DISMISS = 100;	
	public int width, height;		
	public int dpi;
	public String dateStr;
	public String detail_back = "";
	
	public final int SUPERVISOR = 10;
	public final int MERCHAN = 11;
	
	public int way = 0;
	public String activeActivity = "";
	public String command = "";
	public int endingStatus = 0;
	public int lastPos = 0;
	
	public boolean showMessage = false, orderSent = false;
	public float lastOrderAmount = 0;
	public int currentOrderedCount = 0;
	public String customTitle = "";
	
	public Variant active;
	public int resultCode = 0;
	public ProgressDialog pd;
	
	public Hashtable <String, View> views = new Hashtable<String, View>();
	public Hashtable <String, EditText> edits = new Hashtable<String, EditText>();
	public Hashtable <String, TextView> texts = new Hashtable<String, TextView>();
	public Hashtable <String, Button> buttons = new Hashtable<String, Button>();
	
	public SQLManager sql;
	public Collection collection = new Collection();
	public Collection temp_collection = new Collection();
	public Collection plan_collection = new Collection();
	public Collection products = new Collection();
	public Collection brands = new Collection();
	public Collection prices = new Collection();
	public Collection storage = new Collection();
	public Collection packets = new Collection();
	public Collection packets_products = new Collection();
	public Collection cars = new Collection();
	public Collection user_types = new Collection();
	
	public Collection product_confirm_info = new Collection();
	public Collection product_sale_info = new Collection();
	public Collection product_sale_free_info = new Collection();
	public Collection product_order_info = new Collection();
	public Collection product_lease_info = new Collection();
	
	public Collection change_log = new Collection();
	
	public Variant selectedCustomer = new Variant();
	
	public String activeItem = "";
	public String activeBrand = "";
	public String userCode = "", section = "vitafit", todayvalue = "", userName = "", priceTag = "1", message = "", partner = "", driver = "";	
	public String activeCustomer = "";
	public int type = VAN_SALLING, userType = 0, wareHouseID = 0;
	
	public int currentTicketId = 0;
	public int position = 0;
	
	public boolean show_month_sale = false;
	public boolean show_plan_execution = false;
	
	public boolean me = false;
	public float customerDiscount = 0;
	public float payed = 0;
	public float cashDiscount = 0;
	public float invoiceDiscount = 0;
	public float tuluhTotal = 0;
	public float tuljBaigaa = 0;	
	
	public String selRoute = "";
	public String selPrice = "";
	
	public GPSManager gpsManager;
	public MapController mapController;
	public MapView mapView;
	public LocationManager locationManager;	
	
	public boolean[] checked = new boolean[3000];
	public int achilt_id = 0;
	
	public static class SERVER_INFO {
		public String url;
		public int port;
		public String app_id;
		public String company;
		
		public SERVER_INFO(String u, int p, String ai, String cm) {
			url = u;
			port = p;
			app_id = ai;
			company = cm;
		}
	}
	
	public static String SERVER_HOST = "http://103.48.116.112";
	public static int SERVER_PORT = 8080;
	public static String app_id = "vita/";
	
	public String generateXML(String function, String action, String table, String fields, String types, String where) {
		return	"<RT>"+
		   			"<fn>"+function+"</fn>"+
		   			"<at>"+action+"</at>"+
		   			"<tb>"+table+"</tb>"+
		   			"<fs>"+fields+"</fs>"+
		   			"<ts>"+types+"</ts>"+
		   			"<wh>"+where+"</wh>"+
		   			"<sid>"+Shared.sid+"</sid>"+
		   		"</RT>";
	}	
	
	public String decompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }   
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        String outStr = "";
        String line;
        while ((line=bf.readLine())!=null) {
          outStr += line;
        }
        return outStr;
    }
	
	public int getTicketId() {
    	Calendar now = Calendar.getInstance();    	
    	int year = now.get(Calendar.YEAR);
    	int month = now.get(Calendar.MONTH)+1;
    	int day = now.get(Calendar.DAY_OF_MONTH); 
    	
    	int hour = now.get(Calendar.HOUR_OF_DAY);
    	int minute = now.get(Calendar.MINUTE);
    	int second = now.get(Calendar.SECOND);    	
    	
    	todayvalue = year+"-"+(month<10?"0"+month:month)+"-"+(day<10?"0"+day:day);    	
    	
    	return (int)System.currentTimeMillis();//hour*3600+minute*60+second;
    }
	
	public void setUserInfo(Variant w) {
		userCode = w.getString("code");
		userName = w.getString("lastName").charAt(0)+". "+w.getString("firstName");
		partner = w.getString("partnerCode");
		type = (int)w.getInt("work_type");
		userType = (int)w.getInt("_group");
		section = w.getString("section");
		wareHouseID = (int)w.getInt("wareHouseID");
		Shared.wareHouseId = wareHouseID;
	}
	
	public void seeMonthSale() {
		
	}
	
	public void seePlanAndExecution() {
		
	}
	
	public void closedTransaction() {
		currentTicketId = getTicketId();
    	selectedCustomer = collection.elementAt(position);
    	
    	Toast.makeText(this, "Илгээж байна...", Toast.LENGTH_LONG).show();   
    	Variant vt = new Variant();
    	vt.put("userCode", "s"+userCode);
		vt.put("customerCode", "s"+selectedCustomer.get("code"));
		vt.put("productCode", "snul");
		vt.put("type", "i4");
		vt.put("price", "f0");
		vt.put("quantity", "i0");
		vt.put("amount", "f0");
		vt.put("flag", "f0");
		vt.put("discount", "i"+selectedCustomer.getInt("parentID"));
		vt.put("_dateStamp", "dCURRENT_TIMESTAMP");
		vt.put("posX", "f"+Shared.gps_location[0]);
		vt.put("posY", "f"+Shared.gps_location[1]);
		vt.put("userType", "i"+userType);
		vt.put("ticketID", "i"+currentTicketId);
		int success = 0;
    	if ((success = toJSON("insert", "Sales", "_dateStamp,customerCode,userCode,productCode,posX,posY,type,price,quantity,amount,discount,flag,userType,ticketID", vt)) != -1) {    		
    		Toast.makeText(this, "Ажмилттай илгээлээ ...", Toast.LENGTH_LONG).show();			
    	} else
    		sql.insertTransaction(vt);
    	
    	setCustomerStatus("closed", selectedCustomer.get("code"));
	}
	
	public void nonSaleTransaction() {
		currentTicketId = getTicketId();    	
    	    	  
    	Variant vt = new Variant();
    	vt.put("userCode", "s"+userCode);
		vt.put("customerCode", "s"+selectedCustomer.get("code"));
		vt.put("productCode", "snul");
		vt.put("type", "i5");
		vt.put("price", "f0");
		vt.put("quantity", "i0");
		vt.put("amount", "f0");
		vt.put("flag", "f0");
		vt.put("discount", "i"+selectedCustomer.getInt("parentID"));
		vt.put("_dateStamp", "dCURRENT_TIMESTAMP");
		vt.put("posX", "f"+Shared.gps_location[0]);
		vt.put("posY", "f"+Shared.gps_location[1]);
		vt.put("userType", "i"+userType);
		vt.put("ticketID", "i"+currentTicketId);
		int success = 0;
    	if ((success = toJSON("insert", "Sales", "_dateStamp,customerCode,userCode,productCode,posX,posY,type,price,quantity,amount,discount,flag,userType,ticketID", vt)) != -1) {    		
    					
    	} else
    		sql.insertTransaction(vt);
    	
    	setCustomerStatus("worked", selectedCustomer.get("code"));
	}
	
	public void downloadProductData(String sub_brand, boolean d) {
		String where = "";
		/*if (way == 1)
			where = "wareHouseID="+Shared.wareHouseId+" and ";*/
		
		if (sub_brand != null) {			
			where = "brand='"+activeBrand+"' and sub_brand='"+sub_brand+"'";			
		} else
			where += " 1=1 ";
		
		products = sql.selectAll("Product", "s,s,s,s,s,s,f,f,i", "code,name,descr,brand,sub_brand,vendor,unit,size,wareHouseID", null, "name");
		/*for(int i=0; i<products.size(); i++){
			Log.d("products", products.elementAt(i).get("name")+" rows "+where);
		}*/
			
		
		products = sql.selectAll("Product", "s,s,s,s,s,s,f,f,i", "code,name,descr,brand,sub_brand,vendor,unit,size,wareHouseID", where, "name");
		if (products.size() == 0) {
			products = fromJSON("Product", "code,name,descr,vendor,brand,sub_brand,unit,size,wareHouseID", "s,s,s,s,s,s,f,f,i", " WHERE isSale=1 ORDER BY class");
			if (products.size() > 0) {
				sql.deleteAll("Product");
				sql.insertCollection("Product", products,  "s,s,s,s,s,s,f,f,i", "code,name,descr,vendor,brand,sub_brand,unit,size,wareHouseID");
			}
			
			products = sql.selectAll("Product", "s,s,s,s,s,s,f,f,i", "code,name,descr,vendor,brand,sub_brand,unit,size,wareHouseID", where, "name");//serverees amjiltgui tatsan
    	}		
		
		packets = sql.selectAll("Packet", "s,s", "code,name", null, null);
		packets_products = sql.selectAll("PacketProducts", "s,s,s,i,f", "code,name,productCode,quantity,price", null,null);		
		if (packets.size() == 0) {    		    		    		    		
   			packets = fromJSON("Packet", "code,name", "s,s", " WHERE startDate!=CURRENT_TIMESTAMP and enddate@=CURRENT_TIMESTAMP GROUP by code,name");
   			 if (packets.size() > 0) {
   				 sql.deleteAll("Packet");
   				 sql.insertCollection("Packet", packets, "s,s", "code,name");
   				 
   				 packets_products = fromJSON("Packet", "code,name,productCode,quantity,price", "s,s,s,i,f", " WHERE startDate!=CURRENT_TIMESTAMP and enddate@=CURRENT_TIMESTAMP");
   				 if (packets_products.size() > 0) {
   					 sql.deleteAll("PacketProducts");
   					 sql.insertCollection("PacketProducts", packets_products, "s,s,s,i,f", "code,name,productCode,quantity,price");
   				 }
   			 }
   			 
   			packets = sql.selectAll("Packet", "s,s", "code,name", null, null);
   			packets_products = sql.selectAll("PacketProducts", "s,s,s,i,f", "code,name,productCode,quantity,price", null,null);
   		}    	
		
		/*if (way == 1) {
			brands = sql.selectAllGrouped("Product", "wareHouseID="+Shared.wareHouseId, "brand", "brand");
			Log.d("d", brands.size()+" rows brands");
		}
		else*/ {
			brands = sql.selectAllGrouped("Product", null, "brand", "brand");			
		}
		
		if (d)
			loadProductAlways();    
    }
	
	public void loadProductAlways() {		
		prices = sql.selectAll("Price", "s,f,i", "productCode,price,customerType", null, "productCode");
		if (prices.size() == 0) {
	        prices = fromJSON("Price JOIN Product on code=productCode", "productCode,price,customerType", "s,f,i", " ORDER BY customerType,class");
	        if (prices.size() > 0) {
	        	sql.deleteAll("Price");
	        	sql.insertCollection("Price", prices, "s,f,i", "productCode,price,customerType");
	        	setData("last_download", getTime());
	        }
	        
	        prices = sql.selectAll("Price", "s,f,i", "productCode,price,customerType", null, "productCode"); //serverees amjiltgui tatsan
		}
		
		user_types = sql.selectAll("User_Type", "i,s,i", "_group,descr,price_tag", null, "_group");
		if (user_types.size() == 0) {
			user_types = fromJSON("User_Type", "_group,descr,price_tag", "i,s,i", " WHERE price_tag@0 ORDER BY _group");
	        if (user_types.size() > 0) {
	        	sql.deleteAll("User_Type");
	        	sql.insertCollection("User_Type", user_types, "i,s,i", "_group,descr,price_tag");
	        	setData("last_download", getTime());
	        }
	        
	        user_types = sql.selectAll("User_Type", "i,s,i", "_group,descr,price_tag", null, "_group");
		}
				
	    if (Shared.feature_list[Shared.PROMOTION_FEATURE_ID]) {	    
	    	Collection collection = sql.selectAll("Promotion", "s,s,s,i,f,f,s,i,f,f,i,s,s,i,s", "customerCode,productCode,brand,quantity,amount,price,freeProductCode,freeQuantity,freeAmount,precent,type,startDate,endDate,userType,name", null, null);	    		    	
	    	if (collection.size() == 0) {
	    		collection = fromJSON("Promotion", "customerCode,productCode,brand,quantity,amount,price,freeProductCode,freeQuantity,freeAmount,precent,type,startDate,endDate,userType,name", "s,s,s,i,f,f,s,i,f,f,i,s,s,i,s", " WHERE "+getSplittedParams(section, "section")+" and userType="+userType+" and startDate!=CURRENT_TIMESTAMP and endDate@=CURRENT_TIMESTAMP ORDER BY type");
	    		if (collection.size() > 0) {
	    			sql.deleteWhere("Promotion", null);
	    			sql.insertCollection("Promotion", collection, "s,s,s,i,f,f,s,i,f,f,i,s,s,i,s", "customerCode,productCode,brand,quantity,amount,price,freeProductCode,freeQuantity,freeAmount,precent,type,startDate,endDate,userType,name");
	    			setData("last_download", getTime());
	    		}
	    	}	    
	    	
	    	collection = sql.selectAll("Promotion", "s,s,s,i,f,f,s,i,f,f,i,s,s,i,s", "customerCode,productCode,brand,quantity,amount,price,freeProductCode,freeQuantity,freeAmount,precent,type,startDate,endDate,userType,name", null, null);
	    }	
	}
	
	public void checkChangeLog() {
		change_log = fromJSON("Changelog", "userCode,action,status", "s,s,i", userCode);
	}
	
	public boolean orderStateOk() {		
		if (orderSent) { 
			orderSent = false; 
			return true; 
		}
		
		if (change_log.size() > 0) {
			for (int i = 0; i < change_log.size(); i++) {
				Variant w = (Variant)change_log.elementAt(i);
				if (w.getString("action").equals("orders")) 
					return w.getInt("status") == 1;
			}						
		}						
		
		return true;
	}
	
	public boolean storageStateOk() {		
		if (change_log.size() > 0) {
			for (int i = 0; i < change_log.size(); i++) {
				Variant w = (Variant)change_log.elementAt(i);
				if (w.get("action").equals("storage")) 
					return w.getInt("status") == 1;
			}
		}
					
		return true;
	}
	
	public void downloadOrderData() {		
		if (!orderStateOk()) {
			product_confirm_info = sql.selectAll("Orders", "s,i,i,i", "productCode,requestCount,lastCount,flagStatus", null, null);
			if (product_confirm_info.size() > 0) {
				Log.d("d", "orders mixed");
				return;
			}
		}
		
		/*
    	if (type == PRE_SALLING) {
    		product_confirm_info = fromJSON("Orders as B", "customerCode,productCode,requestCount,lastCount,flagStatus", "s,s,i,i,i", " WHERE userCode='"+userCode+"' and customerCode='"+selectedCustomer.getString("code")+"' and flag=0 GROUP by customerCode,productCode");
	    	
	    	if (product_confirm_info.size() > 0) {
	    		sql.deleteWhere("Orders", "customerCode='"+selectedCustomer.getString("code")+"'");
	    		sql.insertCollection("Orders", product_confirm_info, "s,s,i,i,i", "customerCode,productCode,requestCount,lastCount,flagStatus");
	    		Shared.order_download_request = false;
	    		
	    		setData("last_download", getTime());
	    	}
	    	
	    	product_confirm_info = sql.selectAll("Orders", "s,i,i,i", "productCode,requestCount,lastCount,flagStatus", null, null);
	    	Log.d("d", product_confirm_info.size()+" orders size");
	    	
	    	product_confirm_info = sql.selectAll("Orders", "s,i,i,i", "productCode,requestCount,lastCount,flagStatus", "customerCode='"+selectedCustomer.getString("code")+"'", null);
    	} else */
		{    		    	    		
	    	product_confirm_info = fromJSON("Orders as B", "productCode,requestCount,lastCount,flagStatus", "s,i,i,i", " WHERE userCode='"+userCode+"' and userCode=customerCode and flag=0 GROUP by productCode");
	    	
	    	if (product_confirm_info.size() > 0) {
	    		sql.deleteAll("Orders");
	    		sql.insertCollection("Orders", product_confirm_info, "s,i,i,i", "productCode,requestCount,lastCount,flagStatus");
	    		Shared.order_download_request = false;
	    		
	    		setData("last_download", getTime());
	    	}
	    	
	    	product_confirm_info = sql.selectAll("Orders", "s,i,i,i", "productCode,requestCount,lastCount,flagStatus", null, null);
    	}
    }
	
	public void downloadOrderDataForUser() {		
		if (!orderStateOk()) {
			product_confirm_info = sql.selectAll("Orders", "s,i,i,i", "productCode,requestCount,lastCount,flagStatus", null, null);
			if (product_confirm_info.size() > 0) {
				Log.d("d", "orders mixed");
				return;
			}
		}
		
    	if (type == PRE_SALLING) {
    		product_confirm_info = fromJSON("Orders as B", "customerCode,productCode,requestCount,lastCount,flagStatus", "s,s,i,i,i", " WHERE userCode='"+userCode+"' and customerCode='"+userCode+"' and flag=0 GROUP by customerCode,productCode");
	    	
	    	if (product_confirm_info.size() > 0) {
	    		sql.deleteWhere("Orders", "customerCode='"+selectedCustomer.getString("code")+"'");
	    		sql.insertCollection("Orders", product_confirm_info, "s,s,i,i,i", "customerCode,productCode,requestCount,lastCount,flagStatus");
	    		Shared.order_download_request = false;
	    		
	    		setData("last_download", getTime());
	    	}
	    	
	    	product_confirm_info = sql.selectAll("Orders", "s,i,i,i", "productCode,requestCount,lastCount,flagStatus", null, null);
	    	Log.d("d", product_confirm_info.size()+" orders size");
	    	
	    	product_confirm_info = sql.selectAll("Orders", "s,i,i,i", "productCode,requestCount,lastCount,flagStatus", "customerCode='"+selectedCustomer.getString("code")+"'", null);
    	} else {    		    	    		
	    	product_confirm_info = fromJSON("Orders as B", "productCode,requestCount,lastCount,flagStatus", "s,i,i,i", " WHERE userCode='"+userCode+"' and flag=0 GROUP by productCode");
	    	
	    	if (product_confirm_info.size() > 0) {
	    		sql.deleteAll("Orders");
	    		sql.insertCollection("Orders", product_confirm_info, "s,i,i,i", "productCode,requestCount,lastCount,flagStatus");
	    		Shared.order_download_request = false;
	    		
	    		setData("last_download", getTime());
	    	}
	    	
	    	product_confirm_info = sql.selectAll("Orders", "s,i,i,i", "productCode,requestCount,lastCount,flagStatus", null, null);
    	}
    }
	
	public String getTime() {
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR);
		int min = c.get(Calendar.MINUTE);
		int seconds = c.get(Calendar.SECOND);
		
		return todayvalue+" "+hour+":"+(min<10?"0"+min:min)+":"+(seconds<10?"0"+seconds:seconds);
	}
	
	public void downloadStorageData() {    	 
		Log.d("DOWNLOADSTORAGE", "downloadStorageDATA");
		if (!storageStateOk()) {
			storage = sql.selectAll("Storage", "s,i,i", "productCode,availCount,wareHouseID", null, null); //serverees amjiltgui tatsan
			if (storage.size() > 0) {
				Log.d("d", "storage mixed");
				return;
			}
		}
		
		if (Shared.feature_list[Shared.STORAGE_CHECK_FEATURE_ID]) {
			String where = " WHERE wareHouseId!@11 and wareHouseID!@12";
			if (wareHouseID == 11)
				where = " WHERE wareHouseId=11";
			if (wareHouseID == 12)
				where = " WHERE wareHouseId=12";
			
	    	storage = fromJSON("Storage as b", "productCode,_count-(select rCount from Orders where flag=0 and requestCount@0 and confirmedCount=0 and customerCode!@'"+selectedCustomer.get("code")+"' and productCode=b.productCode and wareHouseId=b.wareHouseId) as availCount,wareHouseID", "s,i,i", where);
		    if (storage.size() > 0) {
		       	sql.deleteAll("Storage");//, "wareHouseID="+Shared.wareHouseId);
		       	sql.insertCollection("Storage", storage, "s,i,i", "productCode,availCount,wareHouseID");
		       	setData("last_download", getTime());
		    }
		    
		    storage = sql.selectAll("Storage", "s,i,i", "productCode,availCount,wareHouseID", null, null);//"wareHouseID='"+Shared.wareHouseId+"'", null); //serverees amjiltgui tatsan
		}
    }
	
	public void sendNonCompleteTrans() {
    	Collection trans = sql.selectAllTransaction();
		for (int i = 0; i < trans.size(); i++) {
			Variant w = trans.elementAt(i);
			
			if ((toJSON(w.getInt("type") == 3?"rentpayment":"insert", "Sales", "_dateStamp,customerCode,userCode,productCode,posX,posY,type,quantity,price,amount,discount,flag,userType,ticketID", w)) != -1) {					
				sql.deleteTransaction(w);
				Shared.order_download_request = true;
			}
		}
		
		Collection orders = sql.selectAllOrderTransaction();
		for (int i = 0; i < orders.size(); i++) {
			Variant w = orders.elementAt(i);
			int success = -1;
			
			if (w.getInt("requestCount") > 0) {
				if (w.getString("packet").equals("yes")) 
					success = toJSONPacket("Orders", "productCode,requestCount,userCode,customerCode,posX,posY,wareHouseID,ticketID", w);
				else
					success = toJSON("insert", "Orders", "_date,userCode,customerCode,productCode,requestCount,wareHouseID,price,ticketID,posX,posY", w);
    			
    			if (success != -1) {
    				sql.deleteOrderTransaction(w);
    				Shared.order_download_request = true;
    			}
    		}
		}
		
		setCustomerStatus("worked", selectedCustomer.get("code"));
    }		
	
	
	public void checkMessage() {
    	Collection col = fromJSON("Message", "id,message", "i,s", " WHERE "+getSplittedParams(section, "section")+" ORDER by id desc");
    	if (col != null && col.size() > 0) {
    		if (!message.equals(col.elementAt(0).get("message")) && col.elementAt(0).get("message").length() > 3)
    			showMessage = true;
    		
    		message = col.elementAt(0).get("message");
    	}
    }
	
	public boolean getCustomerStatus(String status, String code) {	
		Collection customer_status = sql.selectAll("Status", "s,s,", "params,value", "params='"+status+"'", null);			
		String value = customer_status.query("params", status).getString("value");		
		return (value.indexOf(code) != -1);    	
    }                	
	
	public void setCustomerStatus(String status, String code) {
		Collection customer_status = sql.selectAll("Status", "s,s,", "params,value", "params='"+status+"'", null);
		String value = customer_status.query("params", status).getString("value");
		Variant w = new Variant();
		w.put("value", value+code+",");
    	sql.update("Status", w, "value", "params='"+status+"'");    	
    }		
	
	public void setData(String key, String value) {
    	SharedPreferences cache = getSharedPreferences("SFA", 0);
 		SharedPreferences.Editor editor = cache.edit();
 		editor.putString(key, value);
 		editor.commit(); 		
    }
	
	public String getData(String key, String value) {
		SharedPreferences cache = getSharedPreferences("SFA", 0);	
		return cache.getString(key, value);
	}
	
	public void confirmCode(String value) {
		Collection c = sql.selectAll("Login", "s,s", "code,password", "code='"+userCode+"'", null);
		String passwordSaved = "1111";
		if (c.size() > 0)
			passwordSaved = c.elementAt(0).getString("password");
    	if (value.equals(passwordSaved)) {
    		 AlertDialog.Builder alert = new AlertDialog.Builder(this);                 
	   		 alert.setTitle("Шинэ нууц код");              	   		 
	   		 LayoutInflater factory = LayoutInflater.from(this);            
		     final View textEntryView = factory.inflate(R.layout.password, null);	        
			 final EditText input = (EditText)textEntryView.findViewById(R.id.code);		 
			 alert.setView(textEntryView);	 
	   		 alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
	   			 public void onClick(DialogInterface dialog, int whichButton) {  
	   		        String value = input.getText().toString();
	   		        setCode(value);
	   		        return;                  
	   			 }  
	   		 });  
	
	   		 alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	   		        public void onClick(DialogInterface dialog, int which) {
	   		        	dialog.dismiss();
	   		            return;   
	   		        }
	   		 });
	   		 alert.show();
        } else
        	Toast.makeText(this, "Буруу байна !", Toast.LENGTH_SHORT).show();    	
    }
	
	public void setCode(String value) {
		
	}
    
	public void changeCode() { 
   	 AlertDialog.Builder alert = new AlertDialog.Builder(this);                 
		 alert.setTitle("Одоогийн нууц код");
		 LayoutInflater factory = LayoutInflater.from(this);            
	     final View textEntryView = factory.inflate(R.layout.password, null);	        
		 final EditText input = (EditText)textEntryView.findViewById(R.id.code);		 
		 alert.setView(textEntryView);
		 alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
			 public void onClick(DialogInterface dialog, int whichButton) {  
		        String value = input.getText().toString();
		        dialog.dismiss();
		        confirmCode(value);		        
		        return;                  
			 }  
		 });  

		 alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	dialog.dismiss();
		            return;   
		        }
		 });
		 alert.show();
   }
	
	public void printLastSaleTransaction() {
    	if (Shared.printLine != null && Shared.printLine.length() > 10) {
	    	Shared.bixolon = new BixolonManager(this);
			Shared.bixolon.startBixolon();	
			if (Shared.bixolon.printData(Shared.printLine)) {			
				new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("Мэдээлэл")
		        .setMessage("Дараагийн хувийг хэвлэх үү !")
		        .setPositiveButton("Тийм", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.dismiss();
		            	Shared.bixolon.printData(Shared.printLine);
		            	Shared.bixolon.stopBixolon();	    		     
		            }
		        })
		        .setNegativeButton("Үгүй", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.dismiss();
		            	Shared.bixolon.stopBixolon();	    		    
		            }
		        })
		        .show();
			} else {
				showAlertMessage("Уучлаарай принтертэй холбогдож чадахгүй байна ! Принтерээ шалгана уу !");
			}
    	} else 
    		showAlertMessage("Уучлаарай гүйлгээ олдохгүй байна !");
    }
	
	public void changePackedMode() {
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Анхааруулага")
        .setMessage("Борлуулалтын утгыг "+(packetMode()?"ширхэгээр":"багцаар")+" оруулдаг болгох уу ?")
        .setPositiveButton("Тийм", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            	Shared.unitMode = getData("unitMode", "p");
            	
            	if (Shared.unitMode.equals("p"))
            		Shared.unitMode = "q";
            	else
            		Shared.unitMode = "p";
            	
            	setData("unitMode", Shared.unitMode);
            }
        })
        .setNegativeButton("Үгүй", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            }
        })
        .show();
	}
	
	public void showAlertMessage(final String text) {
		final Dialog dialog = new Dialog(this){
			  @Override
			  public boolean onTouchEvent(MotionEvent event) {
			    this.dismiss();
			    return true;
			  }
		};			
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);			
		dialog.setContentView(R.layout.dialog_alert_layout);
		TextView message = (TextView)dialog.findViewById(R.id.message);
		message.setText(text);
		dialog.setCancelable(true);
		dialog.getWindow().setLayout(width*90/100, LayoutParams.WRAP_CONTENT);											
		dialog.show();
	}
	
	public void showInfoMessage(final String text) {
		final Dialog dialog = new Dialog(this){
			  @Override
			  public boolean onTouchEvent(MotionEvent event) {
			    this.dismiss();
			    return true;
			  }
		};			
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);			
		dialog.setContentView(R.layout.dialog_info_layout);
		TextView message = (TextView)dialog.findViewById(R.id.message);
		message.setText(text);
		dialog.setCancelable(true);
		dialog.getWindow().setLayout(width*90/100, LayoutParams.WRAP_CONTENT);											
		dialog.show();
	}
	public static String getSplittedParams(String val, String name) {
    	String []v = val.split(",");
    	String result = name+"='"+v[0]+"' ";
    	
    	if (v.length > 1)
    		result += " or "+name+"='"+v[1]+"' ";
    	
    	
    	return " ("+result+") ";
    }
	
	public boolean getLoanMargin() {
    	float amount = 0;
    	for (int i = 0; i < product_sale_info.size(); i++) {
    		Variant w = (Variant)product_sale_info.elementAt(i);
    		amount += w.getFloat("rentamount");
    	}
    	
    	if (selectedCustomer.getFloat("loanMargin") == 0)
    		return true;
    	
    	return (selectedCustomer.getFloat("loanMargin") > amount); 
    }
	
	public void calculateTotal() {
    	float stotal = 0, rtotal = 0;
    	float amount = 0;
    	for (int i = 0; i < product_sale_info.size(); i++) {
    		Variant v = (Variant)product_sale_info.elementAt(i);    		               	    			    		
    		stotal += v.getInt("salequantity")*v.getFloat("saleprice");
    		rtotal += v.getInt("rentquantity")*v.getFloat("rentprice");    		
    		amount += (v.getFloat("saleamount")+v.getFloat("rentamount"));    		
    	}
    	
    	texts.get("sale_total_row").setText(formatCurrency(amount)+" ₮ = "+formatCurrency(stotal)+" ₮ + "+formatCurrency(rtotal)+" ₮");    	
    }
	
	public void calculateOrderTotal() {
	//	float amount = 0;
		int priceTag = 1;
		if (type == PRE_SALLING)
			priceTag = (int)selectedCustomer.getInt("priceTag");
				
		float packet = 0, lpacket = 0;
		for (int i = 0; i < product_order_info.size(); i++) {
    		Variant v = (Variant)product_order_info.elementAt(i);    		 	    	
    		//float price = prices.query("productCode", v.getString("productCode"), "customerType", priceTag).getFloat("price");    		
    		//amount += (v.getInt("requestCount")*price);
    		/*Variant p = products.query("code", v.getString("productCode"));
    		packet += v.getInt("requestCount");///getUnit(p);*/    		  		    			  
        	packet += v.getFloat("packetCount");    	
    	}  
		
		if (lastOrderAmount == 0) {
			for (int i = 0; i < product_confirm_info.size(); i++) {
	    		Variant v = (Variant)product_confirm_info.elementAt(i);    		 	    	
	    		float price = prices.query("productCode", v.getString("productCode"), "customerType", priceTag).getFloat("price");
	    		Variant p = products.query("code", v.getString("productCode"));	    				
	    		lastOrderAmount += (v.getInt("lastCount")*price);
	    		lpacket += v.getInt("lastCount")/getUnit(p);
	    	}
		}
		
    	texts.get("order_total_row").setText(getFixedFloat(packet+lpacket, 1)+" пакет="+getFixedFloat(packet,1)+" пакет + "+getFixedFloat(lpacket,1)+" пакет");    	
    }
	
	public String getUrl() {    	
		Shared.GW_URL = SERVER_HOST+":"+SERVER_PORT+"/" + app_id;
	   	return SERVER_HOST+":"+SERVER_PORT+"/" + app_id;
	}    			       
    
	static OkHttpClient client = new OkHttpClient();
	
    public String post(URL url, byte[] body) throws IOException {
    	System.setProperty("http.keepAlive", "true");
        HttpURLConnection connection = client.open(url);        
        connection.setRequestProperty("Authorization", "Basic " + "voltam_llc" + ":" + "Twi1ig#7@3cli8$E");        
        connection.setReadTimeout(9999);
        connection.setConnectTimeout(9999);
        OutputStream out = null;
        InputStream in = null;
        try {
          connection.setRequestMethod("POST");
          out = connection.getOutputStream();
          out.write(body);
          out.close();
          if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {        	  
            throw new IOException("Unexpected HTTP response: "
                + connection.getResponseCode() + " " + connection.getResponseMessage()+" "+connection.getURL().toString());                        
          }
          in = connection.getInputStream();
          return readFirstLine(in);
        } finally {
          if (out != null) out.close();
          if (in != null) in.close();
        }                
    }
    
    String get(URL url) throws IOException {
        HttpURLConnection connection = client.open(url);
        InputStream in = null;
        try {
          in = connection.getInputStream();
          return readFirstLine(in);
        } finally {
          if (in != null) in.close();
        }                
    }
    
    public String readFirstLine(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder out = new StringBuilder();
        final char[] buffer = new char[16*1024];
        int read;
        do {
        	read = reader.read(buffer, 0, buffer.length);
        	if (read>0) {
        		out.append(buffer, 0, read);
        	}
        }
        while (read>=0);        
        
        return new String(out);
    }	
    
	public Collection fromJSON(String table, String fields, String types, String where) {
		Collection collection = new Collection();				
		if (!Shared.DATA_ENABLED) return collection;
		
		String xml = generateXML("GET", "SELECT", table, fields, types, where); 
		
		if (table.equals("CustomerInfo"))
			xml = generateXML("CustomerInfo", "SELECT", table, fields, types, where);
		if (table.equals("LeaseInfo"))
			xml = generateXML("LeaseInfo", "SELECT", table, fields, types, where);
		if (table.equals("Changelog"))
			xml = generateXML("Changelog", "SELECT", table, fields, types, where);		
		if (table.equals("Today"))
			xml = generateXML("Today", "SELECT", table, fields, types, where); 
		if (table.equals("todaysale"))
			xml = generateXML("TodaySale", "SELECT", table, fields, types, where);
		if (table.equals("todayorder"))
			xml = generateXML("TodayOrder", "SELECT", table, fields, types, where);		
		if (table.equals("mystorage"))
			xml = generateXML("MyStorage", "SELECT", table, fields, types, where);
		if (table.equals("todaydetail"))
			xml = generateXML("TodayDetail", "SELECT", table, fields, types, where);
		if (table.equals("todayorderdetail"))
			xml = generateXML("TodayOrderDetail", "SELECT", table, fields, types, where);		
		if (table.equals("todaysalecustomer"))
			xml = generateXML("TodaySaleCustomer", "SELECT", table, fields, types, where);
		if (table.equals("monthsalecustomer"))
			xml = generateXML("MonthSaleCustomer", "SELECT", table, fields, types, where);		
		if (table.equals("monthplanexecution"))
			xml = generateXML("MonthPlanExecution", "SELECT", table, fields, types, where);
		if (table.equals("todayordercustomer"))
			xml = generateXML("TodayOrderCustomer", "SELECT", table, fields, types, where);		
		if (table.equals("plansale"))
			xml = generateXML("SalePlan", "SELECT", table, fields, types, where);
		if (table.equals("Customers"))
			xml = generateXML("Customers", "SELECT", table, fields, types, where);
		if (table.equals("incomingorders"))
			xml = generateXML("IncomingOrders", "SELECT", table, fields, types, where);
		if (table.equals("ordersmanlist"))
			xml = generateXML("OrderManList", "SELECT", table, fields, types, where);
		if (table.equals("goorder"))
			xml = generateXML("GoOrder", "SELECT", table, fields, types, where);
		if (table.equals("dashboard"))
			xml = generateXML("Dashboard", "SELECT", table, fields, types, where);
		if (table.equals("CustomerByManager"))
			xml = generateXML("CustomerByManager", "SELECT", table, fields, types, where);
		if (table.equals("CurrentOrders"))
			xml = generateXML("CurrentOrders", "SELECT", table, fields, types, where);
		if (table.equals("LastOrders"))
			xml = generateXML("LastOrders", "SELECT", table, fields, types, where);
		
		String json = "";    
        try{        	        	        
        	Log.d("d", System.currentTimeMillis()+" "+table+" started");        	
        	json = post(new URL(GW_URL), xml.getBytes("UTF-8"));        	        	                    	        	        	        	                       
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
		                		if (fd[j].indexOf(" as ") != -1)
		    						fd[j] = fd[j].substring(fd[j].lastIndexOf(' ')+1, fd[j].length());		                		
		                		switch (tp[j].charAt(0)) {
		                			case 's': {		                				
		                				String aString = item.getString(fd[j]);
		                				v.put(fd[j], aString);
		                			} break;
		                			case 'i': {		                			                				
		                				Long aInt = item.getLong(fd[j]);		                				
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
        }
        catch (Exception ex)
        {       
        	ex.printStackTrace();
        	return new Collection();
        }                                        
        
        Log.d("VALUE", json);
        Log.d("d", System.currentTimeMillis()+" "+table+" returned "+json.getBytes().length+" bytes");
        System.gc();
        return collection;	    
	}
	
	public int toJSONPacket(String table, String fields, Variant values) {
		return toJSONUpdate("insert", table, fields, values, "packet"); //packet true									       
	}
	
	public int toJSON(String action, String table, String fields, Variant values) {		
		return toJSONUpdate(action, table, fields, values, "empty");		
	}	
	
	public int toJSONUpdate(String action, String table, String fields, Variant values, String where) {
		//if (!Shared.DATA_ENABLED) return -1;
		String vs = "";
		String [] fd = fields.split(",");		
		for (int i = 0; i < fd.length; i++)
			vs+=values.get(fd[i])+",";				
		Log.d("d", "sent");		       
        try {
        	String xml = generateXML("WRITER", action, table, fields, vs, where);
        	String json = post(new URL(GW_URL), xml.getBytes("UTF-8")); 
        	Log.d("d", json+" sent");
        	if (json != null && json.length() > 2)
        		return 0;
        }
        catch (Exception ex) {     
        	ex.printStackTrace();
        }                
        
        return -1;	    
	}
	
	public void sendGSPTracker() {		
		Runnable runnable = new Runnable() {
	      @Override
	      public void run() {
	    	  Collection gps = sql.selectAll("GPS", "i,f,f,s", "id,lat,lng,seq", null,null);
				Log.d("d", gps.size()+" rows");
				for (int i = 0; i < gps.size(); i++) {
					final Variant w = gps.elementAt(i);
					int success = -1;
					w.put("userCode", "s"+userCode);
					w.put("customerCode", "s"+System.currentTimeMillis());
					w.put("_dateStamp", "dCURRENT_TIMESTAMP");
					w.put("lat", "f"+w.getFloat("lat"));
					w.put("lng", "f"+w.getFloat("lng"));
					w.put("batteryLevel", "i"+Shared.batteryLevel);										
										
		        	success = toJSON("insert", "GPS", "customerCode,lat,lng,userCode,_dateStamp,batteryLevel", w);
		        	Log.d("d", success+" result");
		        	if (success != -1)
		    			sql.deleteWhere("GPS", "id="+w.getInt("id"));				        				    		        		    			  
				}
	      }
	    };
	    new Thread(runnable).start();		   
    }
	
	public Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }
	
	public Drawable readImage(String filename) {
    	ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir("private", Context.MODE_PRIVATE);
    	File file=new File(directory, filename);     	    	    	
    	    	
    	Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());    	
    	Drawable d =new BitmapDrawable(bmp);
    
        return d;   
    }
	
	public static String getFixedString(String s, int t) {
		if (s.length() > t) s = s.substring(0, t);
		
		return String.format("%-"+t+"s", s);		
	}
	
	public static String getFixedInt(long v, int t) {
		return String.format("%"+t+"d", v);
	}
	
	public static String getFixedFloat(float v, int t) {
		NumberFormat nf = NumberFormat.getCurrencyInstance();
    	String s = nf.format(v);
    	s = s.substring(1, s.length());    	
    	s = String.format("%"+t+"s", s);       	
		return s;
	}
	
	public static String getFixedPrice(float v, int t) {    	
    	return String.format("%"+t+".1f", v);
	}
	
	public String getSlash(int column) {
    	String slash = "------------------------";
    	for (int i = 0; i < column; i++) slash += "-";
    	
    	return slash;    	
    }
    
	public boolean isNumeric(String str)  {  
      try {  
        long d = Long.parseLong(str);  
      }  
      catch(NumberFormatException nfe) {  
        return false;  
      }  
      return true;  
    }
	
	public boolean packetMode() {
		return true;
		/*
		Shared.unitMode = getData("unitMode", "p");		
		return Shared.unitMode.equals("p");*/
	}
	
	public float getUnit(Variant o) {
		float unit = packetMode()?o.getFloat("unit"):1;
		
		return unit;
	}
	
	public String packedChr() {
		return (packetMode() ? " ш":" хц");
	}
	
	public static String convertDateToString() {        
       Calendar calendar = Calendar.getInstance();
       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); 
       try {           
           return dateFormat.format(calendar.getTime());
       } catch (Exception e) {
           e.printStackTrace();
       }
       
       return "";
    }
   
    public static String convertDateTimeToString() {        
       Calendar calendar = Calendar.getInstance();
       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
       try {           
           return dateFormat.format(calendar.getTime());            
       } catch (Exception e) {
           e.printStackTrace();
       }
       
       return "";
    }
    
    public static String convertToLatin(String string) {
    	if (string.indexOf('|') != -1) string = string.substring(0, string.indexOf('|'));
    	String [] array = new String[10000];
    	array[(int)'а'] = "a";array[(int)'й'] = "i";array[(int)'т'] = "t";array[(int)'я'] = "ya";
    	array[(int)'б'] = "b";array[(int)'к'] = "k";array[(int)'у'] = "u";
    	array[(int)'в'] = "v";array[(int)'л'] = "l";array[(int)'ү'] = "u";
    	array[(int)'г'] = "g";array[(int)'м'] = "m";array[(int)'ф'] = "f";
    	array[(int)'д'] = "d";array[(int)'н'] = "n";array[(int)'ц'] = "ts";
    	array[(int)'е'] = "e";array[(int)'о'] = "o";array[(int)'ч'] = "ch";
    	array[(int)'ё'] = "yo";array[(int)'ө'] = "u";array[(int)'х'] = "x";
    	array[(int)'з'] = "z";array[(int)'п'] = "p";array[(int)'ш'] = "sh";
    	array[(int)'ж'] = "j";array[(int)'р'] = "r";array[(int)'э'] = "e";
    	array[(int)'и'] = "i";array[(int)'с'] = "s";array[(int)'ю'] = "yu";
    	
    	array[(int)'А'] = "A";array[(int)'Й'] = "I";array[(int)'Т'] = "T";array[(int)'Я'] = "Ya";
    	array[(int)'Б'] = "B";array[(int)'К'] = "K";array[(int)'У'] = "U";
    	array[(int)'В'] = "V";array[(int)'Л'] = "L";array[(int)'Ү'] = "U";
    	array[(int)'Г'] = "G";array[(int)'М'] = "M";array[(int)'Ф'] = "F";
    	array[(int)'Д'] = "D";array[(int)'Н'] = "N";array[(int)'Ц'] = "Ts";
    	array[(int)'Е'] = "E";array[(int)'О'] = "O";array[(int)'Ч'] = "Ch";
    	array[(int)'Ё'] = "Yo";array[(int)'Ө'] = "U";array[(int)'Х'] = "X";
    	array[(int)'З'] = "Z";array[(int)'П'] = "P";array[(int)'Ш'] = "Sh";
    	array[(int)'Ж'] = "J";array[(int)'Р'] = "R";array[(int)'Э'] = "E";
    	array[(int)'И'] = "I";array[(int)'С'] = "S";array[(int)'Ю'] = "Yu";
    	
    	String result = "";
    	for (int i = 0; i < string.length(); i++) {
    		if (array[(int)string.charAt(i)] != null)
    			result += array[(int)string.charAt(i)];
    		else
    			result += string.charAt(i);
    	}
    	
    	return result;
    }
    
    public void clearCollection() {
		product_sale_info = new Collection();
		product_order_info  = new Collection();
		currentOrderedCount = 0;
		customerDiscount = 0;
		payed = 0;
		cashDiscount = 0;
		invoiceDiscount = 0;
		tuluhTotal = 0;
		tuljBaigaa = 0;
	}
    
    public static String formatCurrency(float value) {
    	String tem = "";
    	if (value < 0) { value = Math.abs(value); tem = "-"; }
    	NumberFormat nf = NumberFormat.getCurrencyInstance();
    	String v = nf.format(value);
    	if (value != 0) {
    		v = tem+v.substring(1, v.length());    		
    	} else return "0";
    	
    	if (v.indexOf(".") != -1)
    		v = v.substring(0, v.indexOf("."));
    	return v;
    }
    
    public double distVincenty(double lat1, double lon1, double lat2, double lon2) {
	    double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563; // WGS-84 ellipsoid params
	    double L = Math.toRadians(lon2 - lon1);
	    double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
	    double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
	    double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
	    double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

	    double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
	    double lambda = L, lambdaP, iterLimit = 100;
	    do {
	        sinLambda = Math.sin(lambda);
	        cosLambda = Math.cos(lambda);
	        sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
	        if (sinSigma == 0)
	            return 0; // co-incident points
	        cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
	        sigma = Math.atan2(sinSigma, cosSigma);
	        sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
	        cosSqAlpha = 1 - sinAlpha * sinAlpha;
	        cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
	        if (Double.isNaN(cos2SigmaM))
	            cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (§6)
	        double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
	        lambdaP = lambda;
	        lambda = L + (1 - C) * f * sinAlpha
	                * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
	    } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

	    if (iterLimit == 0)
	        return Double.NaN; // formula failed to converge

	    double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
	    double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
	    double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
	    double deltaSigma = B
	            * sinSigma
	            * (cos2SigmaM + B
	                    / 4
	                    * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
	                            * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
	    double dist = (b * A * (sigma - deltaSigma)) / 1000;

	    return dist;
	}
    
    public void getLocation() {
		MapOverlay myOverlay = new MapOverlay((float)Shared.gps_location[0], (float)Shared.gps_location[1], 0);                        
        		
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(myOverlay);                
        mapController.animateTo(myOverlay.getPoint());               
	}
    
    private double[] getGPS(Location l) {              
        double[] gps = new double[2];        
       
        gps[0] = l.getLatitude();
        gps[1] = l.getLongitude();
        Shared.gps_location[0] = gps[0];
        Shared.gps_location[1] = gps[1];

        return gps;
    }
    
    public Location previousBestLocation = null;
    
    public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {				
		    if (isBetterLocation(location, previousBestLocation)) {
		    	previousBestLocation = location;
		    	getGPS(location);
		    	getLocation();
		    }
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
    
    class MapOverlay extends com.google.android.maps.Overlay
    {
		public GeoPoint p;
		public int[] drawables = {R.drawable.marker, R.drawable.office};
		public int index;
			
		public MapOverlay(double lat, double lng, int id) {			 
			 p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
			 index = id;
		}
		
		public GeoPoint getPoint() {
			return p;
		}
		
        @Override
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);                   

            Point screenPts = new Point();
            mapView.getProjection().toPixels(p, screenPts);
 
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawables[index]);            
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y-32, null);         
            return true;
        }
    }
    
    public class GPSManager {
        private LocationManager lm;
        private LocationListener locationListener;
        private Context con;
        
        public GPSManager(Context context) {
        	con = context;
        	lm=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new MyLocationListener();
                       
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    20000,         
                    0,            
                    locationListener);
          
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    20000,          
                    0,            
                    locationListener); 
        }
        
        public void testFirst() {
        	
        }
        
        public void showMessage1(String text) {
     	   Toast.makeText(con, text, Toast.LENGTH_SHORT).show();
        }
        
    	public class MyLocationListener implements LocationListener
        {
            public void onLocationChanged(Location loc) {  
                getGPS(loc);             
            }
           
            public void onProviderDisabled(String provider) {
            	
            }
       
            public void onProviderEnabled(String provider) {
                Log.d("Hello7","onProviderEnabled");
            }  
            
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        }            	
        
        private double[] getGPS(Location l) {              
            double[] gps = new double[2];        
           
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
            Shared.gps_location[0] = gps[0];
            Shared.gps_location[1] = gps[1];

            if (Shared.current_time == 0)
            	Shared.current_time = System.currentTimeMillis();
            else {
            	long time = System.currentTimeMillis() - Shared.current_time;
            	
            	Date date = new Date();   // given date
            	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            	calendar.setTime(date);   // assigns calendar to given date            
            	if (calendar.get(Calendar.HOUR_OF_DAY) >= 9 && calendar.get(Calendar.HOUR_OF_DAY) < 18) {              	
	            	if (Math.abs(time) >= 5*60*1000) {
	            		Variant w = new Variant();
	            		w.put("lat", "f"+gps[0]);
	            		w.put("lng", "f"+gps[1]);
	            		w.put("seq", "s"+time);
	            		        		        		
	            		sql.insertVariant("GPS", w, "f,f,s", "lat,lng,seq");
	            		
	            		sendGSPTracker();
	            		Shared.current_time = System.currentTimeMillis();        		
	            	}
            	}
            }
            
            return gps;
        }
        
        public void showMessage(String text) {
     	   Toast.makeText(con, text, Toast.LENGTH_LONG).show();
        }
    }
    
    public void exit() {    
    	finish();
    	System.exit(0);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	 AlertDialog.Builder ad = new AlertDialog.Builder(this);  
	    	 ad.setTitle("Гарах");  
	    	 ad.setMessage("Та програмаас гарах уу ?");   
	    	 ad.setPositiveButton("Тийм", new DialogInterface.OnClickListener() {  
	    	    public void onClick(DialogInterface dialog, int id) {  
	    	      dialog.cancel();
	    	      exit();
	    	 }  
	    	 });
	    	 ad.setNegativeButton("Үгүй", new DialogInterface.OnClickListener() {  
		        public void onClick(DialogInterface dialog, int id) {  
			      dialog.cancel();
			  }  
	    	 });
	    	 ad.show();
	    	 return true;
	     }

	     return super.onKeyDown(keyCode, event);
   }
    
   public class RouteOnItemSelectedListener implements OnItemSelectedListener {   	 
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {          
              selRoute = parent.getItemAtPosition(pos).toString();
              if (selRoute.length() > 4) selRoute = selRoute.substring(0, selRoute.indexOf(":"));
        }
     
        public void onNothingSelected(AdapterView parent) {
        }
   }      
   
   public class PriceListOnItemSelectedListener implements OnItemSelectedListener {   	 
       public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {          
             selPrice = parent.getItemAtPosition(pos).toString();
             if (selPrice.length() > 4) selPrice = selPrice.substring(0, selPrice.indexOf(":"));
       }
    
       public void onNothingSelected(AdapterView parent) {
       }
   }
   
   public void clearAll() {
	     AlertDialog.Builder ad = new AlertDialog.Builder(this);  
	  	 ad.setTitle("Гарах");  
	  	 ad.setMessage("Програмын датаг цэвэрлэж шинээр эхлүүлэхийг хүсэж байна уу ?");   
	  	 ad.setPositiveButton("Тийм", new DialogInterface.OnClickListener() {  
	  	    public void onClick(DialogInterface dialog, int id) {
	  	      sql.deleteAll("Orders");	  	      
	  	      sql.deleteAll("Product");
	  	      sql.deleteAll("Route_Customer");
	  	      sql.deleteAll("Price");
	  	      sql.deleteAll("Storage");
	  	      sql.deleteAll("Promotion");
			  sql.deleteAll("Route");
			  sql.deleteAll("WareHouse");
	  	      sql.deleteAll("Status");
	  	      
	  	      dialog.cancel();	  	      
	  	 }  
	  	 });
	  	 ad.setNegativeButton("Үгүй", new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int id) {  
			  dialog.cancel();
			}  
	  	 });
	  	 ad.show();
   }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}      
	
	public void sendLocation() {		
	   
	}
	
	public void sendConfirmation() {
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Анхааруулага")
        .setMessage("Тухайн дэлгүүрийн байршилийг илгээхүү !")
        .setPositiveButton("Тийм", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {            	
            	sendLocation();
            }
        })
        .setNegativeButton("Буцах", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                  
            }
        })
        .show();
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {	
    	Log.i( "MakeMachine", "resultCode: " + resultCode );
    	switch( resultCode )
    	{
    		case 0:
    			Log.i( "MakeMachine", "User cancelled" );
    			break;
    			
    		case -1:
    			onPhotoTaken();
    			break;
    	}
    }
    
    protected void onPhotoTaken()
    {
    	Log.i( "MakeMachine", "onPhotoTaken" );
    	
    	_taken = true;
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
    	
    	Bitmap bitmap = BitmapFactory.decodeFile( _path, options );
    	
    	_image.setImageBitmap(bitmap);
    	
    	_field.setVisibility( View.GONE );    	
    }
    
    @Override 
    protected void onRestoreInstanceState( Bundle savedInstanceState){
    	Log.i( "MakeMachine", "onRestoreInstanceState()");
    	if( savedInstanceState.getBoolean( PHOTO_TAKEN ) ) {
    		onPhotoTaken();
    	}
    }
    
    @Override
    protected void onSaveInstanceState( Bundle outState ) {
    	outState.putBoolean( PHOTO_TAKEN, _taken );
    }
    
    protected void startCameraActivity()
    {
    	String dir = Shared.IMG_PATH+selectedCustomer.get("code")+"/";
        File main = new File(dir);
        if (!main.exists())
        	main.mkdirs();        

    	_path = Shared.IMG_PATH+selectedCustomer.get("code")+"/"+userCode+"-"+System.currentTimeMillis()+".jpg";
    	
    	Log.i("MakeMachine", "startCameraActivity()" );
    	File file = new File( _path );
    	Uri outputFileUri = Uri.fromFile( file );
    	
    	ContentValues values = new ContentValues();
        values.put(Images.Media.TITLE, selectedCustomer.get("customerName"));
        values.put(Images.Media.BUCKET_ID, "1");
        values.put(Images.Media.DESCRIPTION, "");
        values.put(Images.Media.LATITUDE, Shared.gps_location[0]);
        values.put(Images.Media.LONGITUDE, Shared.gps_location[1]);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");

    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);    
    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );

    	startActivityForResult( intent, 0 );    	    	
    }
    
    public void selectCustomer(String code) {
    	selectedCustomer = new Variant();
		selectedCustomer.put("code", code);
		currentTicketId = getTicketId();
    }
    
    public class PhotoTaker implements View.OnClickListener 
    {
    	public void onClick( View view ){
    		Log.i("MakeMachine", "ButtonClickHandler.onClick()" );
    		buttons.get("camera_complete").setEnabled(true);
    		startCameraActivity();
    	}
    }
    
    protected ImageView _image;
	protected TextView _field;
	protected String _path;
	protected boolean _taken;	
	public static final String PHOTO_TAKEN	= "photo_taken";
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;	
	
	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	private static long lastOfficialTime = 0;
	
    public boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        return true;
	    }	 
	    
	    if (Math.abs(lastOfficialTime - System.currentTimeMillis()) < 3000 && Math.abs(distVincenty(location.getLatitude(), location.getLongitude(), currentBestLocation.getLatitude(), currentBestLocation.getLongitude())) > 100.0f)
	    	return false;
	    
	    lastOfficialTime = System.currentTimeMillis();
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}
}
