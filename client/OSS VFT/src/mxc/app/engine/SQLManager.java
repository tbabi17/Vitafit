package mxc.app.engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class SQLManager {
	private final String DATABASE_NAME = "transaction.db";
	private final int DATABASE_VERSION = 23;
	   
	private Context context;
	private SQLiteDatabase db;
	private SQLiteStatement insertStmt;
	private String INSERT_SQL = "INSERT INTO SALES (_date, userCode, customerCode, productCode, type, quantity, price, amount, posx, posy, discount, flag, userType, ticketID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private String INSERT_ORDER_SQL = "INSERT INTO REQORDER (_date, userCode, customerCode, productCode, requestCount, wareHouseID, price, posx, posy,ticketID, packet) values (?,?,?,?,?,?,?,?,?,?,?)";
	   
	public SQLManager(Context context) {
	      this.context = context;
	      OpenHelper openHelper = new OpenHelper(this.context);
	      this.db = openHelper.getWritableDatabase();  	 	     
	}
	
	public void init() {
		Collection collection = selectAll("Status", "s,s", "params,value", null, null);
		if (collection.size() == 0) {
			Variant w = new Variant();
			w.put("params", "leased");
			w.put("value", "");
			insertVariant("Status", w, "s,s", "params,value");
			
			w = new Variant();
			w.put("params", "worked");
			w.put("value", "");
			insertVariant("Status", w, "s,s", "params,value");
			
			w = new Variant();
			w.put("params", "closed");
			w.put("value", "");
			insertVariant("Status", w, "s,s", "params,value");
			
			w = new Variant();
			w.put("params", "no_entry");
			w.put("value", "");
			insertVariant("Status", w, "s,s", "params,value");
		}
	}

	public void insertTransaction(Variant w) {	   	
	   try {
		  this.insertStmt = this.db.compileStatement(INSERT_SQL);
		  this.insertStmt.bindString(1, w.getString("_dateStamp"));
	      this.insertStmt.bindString(2, w.getString("userCode"));
	      this.insertStmt.bindString(3, w.getString("customerCode"));
	      this.insertStmt.bindString(4, w.getString("productCode"));
	      this.insertStmt.bindLong(5, w.getInt("type"));
	      this.insertStmt.bindLong(6, w.getInt("quantity"));
	      this.insertStmt.bindDouble(7, w.getFloat("price"));
	      this.insertStmt.bindDouble(8, w.getFloat("amount"));
	      this.insertStmt.bindString(9, w.get("posX"));
	      this.insertStmt.bindString(10, w.get("posY"));
	      this.insertStmt.bindLong(11, w.getInt("discount"));
	      this.insertStmt.bindDouble(12, w.getFloat("flag"));
	      this.insertStmt.bindLong(13, w.getInt("userType"));
	      this.insertStmt.bindLong(14, w.getInt("ticketID"));
	      this.insertStmt.executeInsert();
	      insertStmt.close();
	   } catch (Exception ex) {
		   ex.printStackTrace();
	   }
    }
	
	public void insertOrderTransaction(Variant w) {	   	
	   try {
		  this.insertStmt = this.db.compileStatement(INSERT_ORDER_SQL);
		  this.insertStmt.bindString(1, w.getString("_date"));
	      this.insertStmt.bindString(2, w.getString("userCode"));
	      this.insertStmt.bindString(3, w.getString("customerCode"));
	      this.insertStmt.bindString(4, w.getString("productCode"));
	      this.insertStmt.bindLong(5, w.getInt("requestCount"));
	      this.insertStmt.bindLong(6, w.getInt("wareHouseID"));	 
	      this.insertStmt.bindDouble(7, w.getFloat("price"));
	      this.insertStmt.bindString(8, w.get("posX"));
	      this.insertStmt.bindString(9, w.get("posY"));
	      this.insertStmt.bindLong(10, w.getInt("ticketID"));
	      this.insertStmt.bindString(11, w.getString("packet"));	      
	      this.insertStmt.executeInsert();
	      insertStmt.close();
	   } catch (Exception ex) {
		   ex.printStackTrace();
	   }
	}
	
	public void insertCollection(String tableName, Collection collection, String types, String fields) {
	   String[] fd = fields.split(",");
	   String[] tp = types.split(",");
	   String str = "?";
	   for (int i = 1; i < fd.length; i++)
		   str += ",?";
	   
	   this.insertStmt = this.db.compileStatement("INSERT INTO "+tableName+" ("+fields+") VALUES ("+str+")");	   
	   for (int i = 0; i < collection.size(); i++) {		   
		   try {			  			  
			  Variant w = collection.elementAt(i);
			  if (tableName.equals("Product"))
				  Shared.downloadFromUrl(context, Shared.GW_URL+"fileGW?fn="+w.getString("code")+".gif", w.getString("code")+".gif");
			  for (int j = 0; j < fd.length; j++) {
	        		switch (tp[j].charAt(0)) {
	        			case 's': 	        					  
	        					  this.insertStmt.bindString(j+1, w.getString(fd[j]));
	        					  break;
	        			case 'i': 	        					  
	        					  this.insertStmt.bindLong(j+1, w.getInt(fd[j]));
	        					  break; 	
	        			case 'f': 	        					  
	        					  this.insertStmt.bindDouble(j+1, w.getFloat(fd[j]));
	        					  break; 					  	        					  
	        		}
			  }			  
			  this.insertStmt.executeInsert();		      
		   } catch (Exception ex) {
			   
		   }
	   }
	   insertStmt.close();
    }
   
    public void insertVariant(String tableName, Variant w, String types, String fields) {
	   String[] fd = fields.split(",");
	   String[] tp = types.split(",");
	   String str = "?";
	   for (int i = 1; i < fd.length; i++)
		   str += ",?";
	   
	   this.insertStmt = this.db.compileStatement("INSERT INTO "+tableName+" ("+fields+") VALUES ("+str+")");	   	   		   
	   try {			  			
		  for (int j = 0; j < fd.length; j++) {
        		switch (tp[j].charAt(0)) {
        			case 's': 	        					  
        					  this.insertStmt.bindString(j+1, w.getString(fd[j]));
        					  break;
        			case 'i': 	        					  
        					  this.insertStmt.bindLong(j+1, w.getInt(fd[j]));
        					  break; 	
        			case 'f': 	        					  
        					  this.insertStmt.bindDouble(j+1, w.getFloat(fd[j]));
        					  break; 					  	        					  
        		}
		  }			  
		  this.insertStmt.executeInsert();		      
	   } catch (Exception ex) {
		   
	   }
	   insertStmt.close();
    }
    
    public Collection selectAll(String table, String types, String fields, String where, String orderby) {
		  Collection vd = new Collection();      
		  String[] fd = fields.split(",");
		  String[] tp = types.split(",");
	      Cursor cursor = this.db.query(table, fd, where, null, null, null, orderby);
	        if (cursor.moveToFirst()) {         
	         do {
	        	Variant w = new Variant();
	        	for (int i = 0; i < fd.length; i++) {
	        		switch (tp[i].charAt(0)) {
	        			case 's': w.put(fd[i], cursor.getString(i)); break;
	        			case 'i': w.put(fd[i], cursor.getInt(i)+""); break;
	        			case 'f': w.put(fd[i], cursor.getDouble(i)+""); break;
	        		}
	        		
	        	}	                       
	            vd.addCollection(w);
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return vd;
    }
    
    public Collection selectAllGrouped(String table, String where, String fields, String groupby) {
		  Collection vd = new Collection();  
		  String[] fd = fields.split(",");
	      Cursor cursor = this.db.query(table, fd, where, null, groupby, null, null);
	        if (cursor.moveToFirst()) {
	         do {
	        	Variant w = new Variant();	        		        	
	        	w.put(fd[0], cursor.getString(0));	        				        			        	
	            vd.addCollection(w);
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return vd;
    }
    
    public Collection selectWhereGrouped(String table, String fields, String where, String groupby) {
		  Collection vd = new Collection();  
		  String[] fd = fields.split(",");
	      Cursor cursor = this.db.query(table, fd, where, null, groupby, null, null);
	        if (cursor.moveToFirst()) {
	         do {
	        	Variant w = new Variant();	        		        	
	        	w.put(fd[0], cursor.getString(0));	        				        			        	
	            vd.addCollection(w);
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return vd;
  }
    
    public void deleteAll(String table) {
  	   this.db.delete(table, null, null);
    }
     
    public void deleteWhere(String table, String where) {
  	   this.db.delete(table, where, null);
    }               
     
    public void updateRouteCustomerPos(Variant w, double lat, double lng) {
	    ContentValues cv=new ContentValues();
	    cv.put("lat", lat);
	    cv.put("lng", lng);
	    this.db.update("Route_Customer", cv, "code='"+w.getString("code")+"'", null);
    }
    
    public void update(String table, Variant w, String fields, String where) {
    	String[] fd = fields.split(",");
	    ContentValues cv = new ContentValues();
	    for (int i = 0; i < fd.length; i++)
	    	cv.put(fd[i], w.getString(fd[i]));
	    
	    this.db.update(table, cv, where, null);
    }
    
    public Collection selectAllTransaction() {
		  Collection vd = new Collection();      
	      Cursor cursor = this.db.query("SALES", new String[] {"_date", "userCode", "customerCode", "productCode", "type", "quantity", "price", "amount", "posx", "posy", "discount", "flag", "userType", "ticketID", "id"}, 
	        null, null, null, null, "id");
	        if (cursor.moveToFirst()) {         
	         do {
	        	Variant w = new Variant();
	            w.put("_dateStamp", cursor.getString(0));
	            w.put("userCode", "s"+cursor.getString(1));
	            w.put("customerCode", "s"+cursor.getString(2));
	            w.put("productCode", "s"+cursor.getString(3));
	            w.put("type", "i"+cursor.getLong(4));
	            w.put("quantity", "i"+cursor.getLong(5));
	            w.put("price", "f"+cursor.getFloat(6));
	            w.put("amount", "f"+cursor.getFloat(7));
	            w.put("posX", cursor.getString(8));
	            w.put("posY", cursor.getString(9));
	            w.put("discount", "i"+cursor.getLong(10));
	            w.put("flag", "f"+cursor.getFloat(11));
	            w.put("userType", "i"+cursor.getLong(12));
	            w.put("ticketID", "i"+cursor.getLong(13));
	            w.put("id", "i"+cursor.getLong(14));	            
	            vd.addCollection(w);
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return vd;
    }
    
    public Collection selectAllOrderTransaction() {
		  Collection vd = new Collection();      
	      Cursor cursor = this.db.query("REQORDER", new String[] {"_date", "userCode", "customerCode", "productCode", "requestCount", "wareHouseID", "price", "posx", "posy", "ticketID", "packet", "id"}, 
	        null, null, null, null, "id");
	        if (cursor.moveToFirst()) {         
	         do {
	        	Variant w = new Variant();
	            w.put("_date", cursor.getString(0));
	            w.put("userCode", "s"+cursor.getString(1));
	            w.put("customerCode", "s"+cursor.getString(2));
	            w.put("productCode", "s"+cursor.getString(3));
	            w.put("requestCount", "i"+cursor.getLong(4));
	            w.put("wareHouseID", "i"+cursor.getLong(5));	    
	            w.put("price", "f"+cursor.getDouble(6));	  
	            w.put("posX", cursor.getString(7));
	            w.put("posY", cursor.getString(8));
	            w.put("ticketID", "i"+cursor.getLong(9));
	            w.put("packet", cursor.getString(10));	            
	            w.put("id", "i"+cursor.getLong(11));    
	            vd.addCollection(w);
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return vd;
    }
    
    public void deleteTransaction(Variant w) {
	    this.db.delete("SALES", "id="+w.getInt("id"), null);
    }
      
    public void deleteOrderTransaction(Variant w) {
	    this.db.delete("REQORDER", "id="+w.getInt("id"), null);
    }
   
	private class OpenHelper extends SQLiteOpenHelper {
	      OpenHelper(Context context) {
	         super(context, DATABASE_NAME, null, DATABASE_VERSION);
	      }
	
	      @Override
	      public void onCreate(SQLiteDatabase db) {    	  
	         db.execSQL("CREATE TABLE log (_date TEXT, descr TEXT, _where TEXT, success TEXT)");
	         db.execSQL("CREATE TABLE SALES (_date text, userCode VARCHAR(5), customerCode VARCHAR(10), productCode VARCHAR(10), type INTEGER, quantity INTEGER, price FLOAT, amount FLOAT, posx TEXT, posy TEXT, discount INTEGER, flag FLOAT, userType INTEGER, ticketID INTEGER, id INTEGER PRIMARY KEY AUTOINCREMENT)");
	         db.execSQL("CREATE TABLE SALESQUEUE (_date text, customerCode VARCHAR(10), userCode VARCHAR(5), amount FLOAT, payed FLOAT, flag INTEGER, id INTEGER PRIMARY KEY AUTOINCREMENT)");
	         db.execSQL("CREATE TABLE REQORDER (_date text, userCode VARCHAR(5), customerCode VARCHAR(10), productCode VARCHAR(10), requestCount INTEGER, wareHouseID INTEGER, price FLOAT, posx TEXT, posy TEXT, ticketID INTEGER, packet VARCHAR(10), id INTEGER PRIMARY KEY AUTOINCREMENT)");
	         db.execSQL("CREATE TABLE PAYMENTS (customerCode VARCHAR(10), payed FLOAT)");
	         db.execSQL("CREATE TABLE Product (code VARCHAR(10), name TEXT, descr TEXT, vendor TEXT, brand TEXT, sub_brand TEXT, unit FLOAT, size FLOAT, wareHouseID INTEGER)");
	         db.execSQL("CREATE TABLE Price (productCode VARCHAR(10), customerType INTEGER, price FLOAT)");         
	         db.execSQL("CREATE TABLE Packet (code VARCHAR(10), name TEXT)");
	         db.execSQL("CREATE TABLE PacketProducts (code INTEGER,name TEXT,productCode VARCHAR(10),quantity INTEGER,price FLOAT)");
	         db.execSQL("CREATE TABLE Route_Customer (code VARCHAR(10), customerName TEXT, routeID VARCHAR(4), priceTag INTEGER, parentID INTEGER, discount FLOAT, lat FLOAT, lng FLOAT, loanMargin FLOAT)");
	         db.execSQL("CREATE TABLE Storage (productCode VARCHAR(10), availCount INTEGER, wareHouseID INTEGER)");
	         db.execSQL("CREATE TABLE Orders (customerCode VARCHAR(10), productCode VARCHAR(10), requestCount INTEGER, lastCount INTEGER, flagStatus INTEGER, price FLOAT)");
	         db.execSQL("CREATE TABLE Templates (productCode VARCHAR(10), requestCount INTEGER, name VARCHAR(20))");
	         db.execSQL("CREATE TABLE OrderList (customerCode VARCHAR(10), productCode VARCHAR(10), orderedCount INTEGER, price FLOAT)");
	         db.execSQL("CREATE TABLE Promotion (customerCode VARCHAR(10), productCode VARCHAR(10), brand TEXT, quantity INTEGER, amount FLOAT, price FLOAT, freeProductCode VARCHAR(10), freeQuantity INTEGER, freeAmount FLOAT, precent FLOAT, type INTEGER, startDate TEXT, endDate TEXT, userType INTEGER, name TEXT)");
	         db.execSQL("CREATE TABLE Route (routeID VARCHAR(5), routeName VARCHAR(100))");         
	         db.execSQL("CREATE TABLE Package (productCode VARCHAR(10), count FLOAT, name VARCHAR(50))");
	         db.execSQL("CREATE TABLE WareHouse (wareHouseID INTEGER, name VARCHAR(80), section VARCHAR(50))");
	         db.execSQL("CREATE TABLE Today (today VARCHAR(50),weekday VARCHAR(50),routeID VARCHAR(40),packet VARCHAR(40),rentedCustomers VARCHAR(50),message VARCHAR(50),userName VARCHAR(50),userType VARCHAR(50),priceTag VARCHAR(50),wareHouseId VARCHAR(10),section VARCHAR(50))");
	         db.execSQL("CREATE TABLE SurveyList (id INTEGER, message TEXT)");
	         db.execSQL("CREATE TABLE Login (code VARCHAR(10), password VARCHAR(5),firstName VARCHAR(30), lastName VARCHAR(30), _group INTEGER, work_type INTEGER, section VARCHAR(20), wareHouseID INTEGER, partnerCode VARCHAR(10), lastlog VARCHAR(20))");
	         db.execSQL("CREATE TABLE Status (params VARCHAR(10), value TEXT)");
	         db.execSQL("CREATE TABLE Users (code VARCHAR(10), firstName TEXT, lastName TEXT)");
	         db.execSQL("CREATE TABLE GPS (lat FLOAT, lng FLOAT, seq TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT)");
	         db.execSQL("CREATE TABLE Lease (cusName TEXT, dist INTEGER,amount FLOAT,total FLOAT)");
	         db.execSQL("CREATE TABLE User_Type (_group INTEGER, descr VARCHAR(20), price_tag INTEGER)");
	         	         
	         Log.d("d", "Databases created !");
	      }     
	
	      @Override
	      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	         Log.w("Example", "Upgrading database, this will drop tables and recreate.");
	         db.execSQL("DROP TABLE IF EXISTS log");
	         db.execSQL("DROP TABLE IF EXISTS SALES");
	         db.execSQL("DROP TABLE IF EXISTS SALESQUEUE");
	         db.execSQL("DROP TABLE IF EXISTS PAYMENTS");
	         db.execSQL("DROP TABLE IF EXISTS REQORDER");  
	         db.execSQL("DROP TABLE IF EXISTS Product");
	         db.execSQL("DROP TABLE IF EXISTS Price");
	         db.execSQL("DROP TABLE IF EXISTS Packet");
	         db.execSQL("DROP TABLE IF EXISTS PacketProducts");
	         db.execSQL("DROP TABLE IF EXISTS Route_Customer");
	         db.execSQL("DROP TABLE IF EXISTS Storage");
	         db.execSQL("DROP TABLE IF EXISTS Orders");
	         db.execSQL("DROP TABLE IF EXISTS Templates");
	         db.execSQL("DROP TABLE IF EXISTS OrderList");
	         db.execSQL("DROP TABLE IF EXISTS Promotion");
	         db.execSQL("DROP TABLE IF EXISTS Route");
	         db.execSQL("DROP TABLE IF EXISTS Package");
	         db.execSQL("DROP TABLE IF EXISTS WareHouse");
	         db.execSQL("DROP TABLE IF EXISTS GPS");
	         db.execSQL("DROP TABLE IF EXISTS Users");
	         db.execSQL("DROP TABLE IF EXISTS Today");
	         db.execSQL("DROP TABLE IF EXISTS Login");
	         db.execSQL("DROP TABLE IF EXISTS Status");
	         db.execSQL("DROP TABLE IF EXISTS SurveyList");
	         db.execSQL("DROP TABLE IF EXISTS Lease");
	         db.execSQL("DROP TABLE IF EXISTS User_Type");
	         
	         onCreate(db);
	      }
   }
}
