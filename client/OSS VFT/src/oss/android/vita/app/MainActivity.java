package oss.android.vita.app;

import java.util.Random;

import mxc.app.engine.Collection;
import mxc.app.engine.ImageLoader;
import mxc.app.engine.MasterActivity;
import mxc.app.engine.SQLManager;
import mxc.app.engine.Shared;
import mxc.app.engine.Variant;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends MasterActivity {		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		init();
		loginActivity();
	}
	
	public void init() {
		imgLoader = new ImageLoader(this);
		Shared.tf = Typeface.createFromAsset(getAssets(), "Comfortaa_Regular.ttf");
		sql = new SQLManager(this);
		sql.init();
		gpsManager = new GPSManager(this);
		gpsManager.testFirst();
		registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//		receiver = new ConnectivityReceiver();        
//      registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();           
        if (imei == null) imei = "000000000000000";
        Shared.sid = imei;
	}	
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
          // TODO Auto-generated method stub
          int level = intent.getIntExtra("level", 0);
          Shared.batteryLevel = level;
        }
    };
	
	@Override
	public void onResume() {
		if (showMessage) {
			showMessage = false;
			showInfoMessage(message);			
    	}
		
		//currentTicketId = getTicketId();
	    super.onResume();
	}
	
	public void routeListLoad() {
		collection = sql.selectAll("Route", "s,s", "routeID,routeName", "", null);
		if (collection.size() == 0) {
			collection = fromJSON("Route", "routeID,routeName", "s,s", " WHERE routeID in (select mon from Route_User where userCode='"+userCode+"') or routeID in (select thue from Route_User where userCode='"+userCode+"') or routeID in (select wed from Route_User where userCode='"+userCode+"') or routeID in (select thus from Route_User where userCode='"+userCode+"') or routeID in (select fri from Route_User where userCode='"+userCode+"') or routeID in (select sat from Route_User where userCode='"+userCode+"') or routeID in (select sun from Route_User where userCode='"+userCode+"')");
    		if (collection.size() > 0) {
    			sql.deleteWhere("Route", null);
    			sql.insertCollection("Route", collection,  "s,s", "routeID,routeName");
    		}
    		
    		collection = sql.selectAll("Route", "s,s", "routeID,routeName", "", null);
    	}		    	
	}
	
	public void userListLoad() {
		collection = sql.selectAll("Users", "s,s,s,f", "code,firstName,lastName", null, null);
    	if (collection.size() == 0) {
    		collection = fromJSON("Users", "code,firstName,lastName", "s,s,s", " WHERE manager1='"+userCode+"'");
    		if (collection.size() > 0) {
    			sql.deleteWhere("Users", null);
    			sql.insertCollection("Users", collection,  "s,s,s", "code,firstName,lastName");
    		}
    		
   			collection = sql.selectAll("Users", "s,s,s", "code,firstName,lastName", null, null);
    	}  	    
	}
	
	public void warehouseListLoad() {
		collection = sql.selectAll("WareHouse", "i,s,s", "wareHouseID,name,section", null, "wareHouseID");
    	if (collection.size() == 0) {
    		collection = fromJSON("Ware_House", "wareHouseID,name,section", "i,s,s", " ");
    		if (collection.size() > 0) {    			
    			sql.deleteWhere("WareHouse", null);
    			sql.insertCollection("WareHouse", collection, "i,s,s", "wareHouseID,name,section");		    			
    		}    			
    		collection = sql.selectAll("WareHouse", "i,s,s", "wareHouseID,name,section", null, "wareHouseID");
    	}   		    	
	}
	
	public void customerListLoad() {	
		if (userType == SUPERVISOR) {	    
	    	collection = sql.selectAll("Route_Customer", "s,s,s,i,i,f,f,f,f", "code,customerName,routeID,priceTag,parentID,discount,lat,lng,loanMargin", "routeID='"+Shared.userID+"'", "customerName");
	    	if (collection.size() == 0) {	    		
	    		collection = fromJSON("CustomerByManager", "code,customerName,routeID,priceTag,parentID,discount,lat,lng,loanMargin", "s,s,s,i,i,f,f,f,f", Shared.userID);    		
	    		if (collection.size() > 0) {
	    			sql.deleteWhere("Route_Customer", "routeID='"+Shared.userID+"'");
	    			sql.insertCollection("Route_Customer", collection,  "s,s,s,i,i,f,f,f,f", "code,customerName,routeID,priceTag,parentID,discount,lat,lng,loanMargin");    			
	    		}
	    		collection = sql.selectAll("Route_Customer", "s,s,s,i,i,f,f,f,f", "code,customerName,routeID,priceTag,parentID,discount,lat,lng,loanMargin", "routeID='"+activeItem+"'", "customerName");
	    	}
		} else {
			collection = sql.selectAll("Route_Customer", "s,s,s,i,i,f,f,f,f", "code,customerName,routeID,priceTag,parentID,discount,lat,lng,loanMargin", "routeID='"+Shared.routeID+"'", "customerName");
	    	if (collection.size() == 0) {	    		
	    		collection = fromJSON("Route_Customer", "customerCode as code,customerName,routeID,priceTag,parentID,discount,lat,lng,loanMargin", "s,s,s,i,i,f,f,f,f", " WHERE routeID='"+Shared.routeID+"' group by customerCode,routeID ORDER by routeID");    		
	    		if (collection.size() > 0) {
	    			sql.deleteWhere("Route_Customer", "routeID='"+Shared.routeID+"'");
	    			sql.insertCollection("Route_Customer", collection,  "s,s,s,i,i,f,f,f,f", "code,customerName,routeID,priceTag,parentID,discount,lat,lng,loanMargin");    			
	    		}
	    		collection = sql.selectAll("Route_Customer", "s,s,s,i,i,f,f,f,f", "code,customerName,routeID,priceTag,parentID,discount,lat,lng,loanMargin", "routeID='"+activeItem+"'", "customerName");
	    	}
		}
	}
	
	public Collection loadCars() {   
    	Collection cars = fromJSON("Users", "code,firstName,lastName,loaded", "s,s,s,f", " WHERE _group=11 and len(secCode)@0");    	
    	return cars;
    }
	
	public void leasedCustomerLoad() {		
		collection = fromJSON("Sales", "cusName,dist,sum(flag) as amount,sum(amount) as total", "s,i,f,f", " WHERE flag>0 and userCode='"+userCode+"' GROUP by discount");		
    	if (collection.size() > 0) {	    		    		    		    		
    		sql.deleteAll("Lease");
    		sql.insertCollection("Lease", collection,  "s,i,f,f", "cusName,dist,amount,total");    			    		    		
    	}    	    	    	
    	
    	collection = sql.selectAll("Lease", "s,i,f,f", "cusName,dist,amount,total", null, "dist");
	}
	
	public void myStorageLoad() {		
		collection = fromJSON("mystorage", "data,quantity", "s,f", userCode);    	
	}
	
	@Override
	public void preProcessing() {				
		endingStatus = NOSTARTED;				
    	
		if (show_month_sale) {
			String fields = "amount";
			String types = "f";
			temp_collection = fromJSON("monthsalecustomer", fields, types, selectedCustomer.getString("code")); 						
			return;
		}
		
		if (show_plan_execution) {
			String fields = "code,name,guitsSh,tuluv,per";
			String types = "s,s,f,f,f";
			String where = userCode+","+spChannel;
			plan_collection = fromJSON("monthplanexecution", fields, types, where); 						
			return;
		}
		
    	if (activeActivity.equals("today")) {
    		if (command.equals("today_info")) {    			
    			String fields = "code,data,amount";
   			    String types = "s,s,i";		
   			    /*if (type == PRE_SALLING)
   			    	collection = fromJSON("todayordercustomer", fields, types, userCode+","+dateStr);
   			    else*/
   			    	collection = fromJSON("todaysalecustomer", fields, types, userCode+","+dateStr); 	
    		} else
    		if (command.equals("sale_info")) {    			
    			String fields = "code,data,amount";
   			    String types = "s,s,i";		
    			collection = fromJSON("todaysalecustomer", fields, types, userCode+","+dateStr); 	
    		} else
			if (command.equals("order_info")) {				
				String fields = "code,data,ticketID,quantity,amount,partner";
   			    String types = "s,s,i,i,i,s";		  
    			collection = fromJSON("todayordercustomer", fields, types, userCode+","+dateStr);
    			cars = loadCars();
			} else
			if (command.equals("to_customer_saledetail")) {
				String fields = "code,data,type,quantity,amount";
   			    String types = "s,s,i,i,f";		   
    			collection = fromJSON("todaydetail", fields, types, activeItem+","+dateStr); 
			} else
    		if (command.equals("to_customer_orderdetail")) {
				String fields = "code,data,type,quantity,amount";
   			    String types = "s,s,i,i,f";		   
    			collection = fromJSON("todayorderdetail", fields, types, activeItem+","+dateStr); 
			} else
			if (command.equals("achilt_order")) {
				if (dateStr == null || dateStr.length() < 8) {
					Toast.makeText(this, "Хүргэлтийн огноо сонгоно уу !", Toast.LENGTH_SHORT).show();
					return;
					/*
					try {					   
						SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );   
						Calendar cal = Calendar.getInstance();        
						cal.add( Calendar.DATE, 1);
						dateStr =dateFormat.format(cal.getTime());  
					} catch (Exception e) {
						
					}*/
				}
				for (int i = 0; i < collection.size(); i++) 
	    		if (checked[i]) {
	    			Variant q = (Variant)collection.elementAt(i);
	    			if (!q.getString("partnerCode").equals(partner)) {
			    		Variant w = new Variant();
			    		w.put("ticketID", "i"+q.getInt("ticketID"));
			    		w.put("userCode", "s"+driver);
			    		w.put("handleID", "i"+achilt_id);
			    		w.put("_date", "s"+dateStr);
			    		String where = " ticketID="+q.getInt("ticketID");
			    		toJSONUpdate("delete", "Order_Trans", " ", new Variant(), where);
			    		toJSONUpdate("insert", "Order_Trans", "ticketID,userCode,handleID,_date", w, " ");		    				    		
	    			}									    			
	    			fromJSON("goorder", " ", " ", q.getInt("ticketID")+","+q.getString("code")+","+userCode+","+driver+","+dateStr);
	    			checked[i] = false;
	    		}
	    		
				String fields = "code,data,ticketID,quantity,amount,partner";
   			    String types = "s,s,i,i,i,s";		  
    			collection = fromJSON("todayordercustomer", fields, types, userCode+","+dateStr);
    			cars = loadCars();
			}
    	} else
		if (activeActivity.equals("detail_customer")) {
			if (command.equals("to_today")) {
				
			}
		} else
		if (activeActivity.equals("settings")) {
			if (command.equals("non_complete")) {
				sendNonCompleteTrans();
			} else
			if (command.equals("call_lease")) {
				leasedCustomerLoad();
			} else
			if (command.equals("send_images")) {
				ImageSender.sendAll(getApplicationContext());
			} else
			if (command.equals("call_mystorage")) {
				myStorageLoad();
			} else
			if (command.equals("change_code")) {
				resultCode = toJSONUpdate("update", "Users", "password", active, "code='"+userCode+"'");    			    		    			
			} else
			if (command.equals("last_orders")) {
				collection = fromJSON("LastOrders", "firstName,lastSale,lastSalesTimestamp,ZahialgaHoorondiinMinute", "s,s,s,s,i", userCode+","+todayvalue);    			    		    			
			} else
			if (command.equals("current_orders")) {
				collection = fromJSON("CurrentOrders", "firstName,odriinBorluulalt,dailyPlan,outletsBuying,buyingPrecent", "s,f,f,f,f", userCode+","+todayvalue);    			    		    			
			}
		} else		
		if (activeActivity.equals("register_customer")) {
			sendRegisterCustomer();
		} else
		if (activeActivity.equals("sale") || activeActivity.equals("order")) {
			if (command.equals("to_customer_nonsale")) {
				nonSaleTransaction();
				customerListLoad();
			} else 
			if (command.equals("to_customer")) {
				customerListLoad();
			} else 
			if (command.equals("lease_payment")) {
				product_lease_info = fromJSON("LeaseInfo", "flag", "f", ""+selectedCustomer.getInt("parentID"));
			} else {
				sendNonCompleteTrans();
				endingStatus = SUCCESS;
			}
		} else
		if (activeActivity.equals("lease")) {
			if (command.equals("lease_payment")) {
				product_lease_info = fromJSON("LeaseInfo", "flag", "f", ""+selectedCustomer.getInt("parentID"));
			}
		} else
		if (activeActivity.equals("login")) {				    		    
			String user = edits.get("login_user").getText().toString();
			String pass = edits.get("login_code").getText().toString();
			
			todayvalue = convertDateToString();			
			Collection cl = sql.selectAll("Login", "s,s,s,s,i,i,s,i,s,s", "code,password,firstName,lastName,_group,work_type,section,wareHouseID,partnerCode,lastlog", "code='"+user+"'", null);			
			if (cl.size() > 0 && !cl.elementAt(0).getString("lastlog").equals(todayvalue)) {
				Variant q = new Variant();
				q.put("lastlog", todayvalue);
				sql.update("Login", q, "lastlog", "code='"+userCode+"'");
				sql.deleteAll("Status");
				sql.init();
			}
						
			collection = fromJSON("Users", "code,password,firstName,lastName,_group,work_type,section,wareHouseID,partnerCode", "s,s,s,s,i,i,s,i,s", " WHERE code='"+user+"'");
			userCode = user;
			if (collection.size() > 0) {				
				Variant v = (Variant)collection.elementAt(0);						
				if (v.getString("password").equals(pass)) {
					setUserInfo(v);
					sql.deleteAll("Login");
					todayvalue = convertDateToString();
					v.put("lastlog", "s"+todayvalue);
					sql.insertVariant("Login", v, "s,s,s,s,i,i,s,i,s,s", "code,password,firstName,lastName,_group,work_type,section,wareHouseID,partnerCode,lastlog");
					endingStatus = SUCCESS;
					checkMessage();
					sendNonCompleteTrans();
				} else
					endingStatus = FAILED;
			} else {				
				if (cl.size() > 0) {					
					endingStatus = SUCCESS;
					setUserInfo(cl.elementAt(0));
					checkMessage();
				}
				else
					endingStatus = FAILED;
			}												
		} else			
		if (activeActivity.equals("main")) { //undsen control window
			if (command.equals("to_userlist")) {
				userListLoad();
			} else
			if (command.equals("to_routelist")) {
				routeListLoad();
			} else
			if (command.equals("to_warehouselist")) {
				warehouseListLoad();
			} else
			if (command.equals("refresh_perfect")) {
				sql.deleteAll("Price");
				sql.deleteAll("Promotion");				
								
				loadProductAlways();
			}				
		} else			 
		if (activeActivity.equals("userlist")) { 			
			if (command.equals("to_customer")) {
				customerListLoad();
			}
		} else
		if (activeActivity.equals("routelist")) { 			
			if (command.equals("to_customer")) {
				customerListLoad();
			}
		} else
		if (activeActivity.equals("camera")) { 			
			if (command.equals("to_customer")) {
				customerListLoad();
			}
		} else			
		if (activeActivity.equals("warehouselist")) { 			
			if (command.equals("to_order")) {
				//checkChangeLog();
				downloadProductData(null, true);
				//downloadOrderData();
				//downloadStorageData();
			} else
			if (command.equals("to_routelist")) {
				routeListLoad();
			}
		} else		
		if (activeActivity.equals("customer")) { 	
			if (command.equals("send_location")) {
				resultCode = toJSONUpdate("update", "Customer", "posX,posY,_date,log", active, " code='"+selectedCustomer.get("code")+"'");
			} else
			if (command.equals("customer_to_sale")) {
				if (type == VAN_SALLING) {
					checkChangeLog();
					downloadProductData(null, true);										
					downloadOrderData();											
		    	} else {
		    		//selectCustomer(userCode);
		    		checkChangeLog();
					downloadProductData(null, true);										
					//downloadOrderData();
					downloadOrderDataForUser();
		    	}
			} else
			if (command.equals("customer_to_order")) {
				//checkChangeLog();
				downloadProductData(null, true);								
				//downloadOrderData();
				//downloadStorageData();
			} else
			if (command.equals("to_routelist")) {
				routeListLoad();
			} else
			if (command.equals("to_userlist")) {
				userListLoad();
			}
		} else
		if (activeActivity.equals("order")) {//order -oos butsah
			if (type == PRE_SALLING) {				
				customerListLoad();
			} else
			if (command.equals("to_warehouselist")) {
				warehouseListLoad();
			}			
		}
	}
	
	@Override
	public void completeReading() {
		super.completeReading();
    	
		if (show_month_sale) {			
			if (temp_collection.size() > 0) {
				showInfoMessage(getFixedFloat(temp_collection.elementAt(0).getFloat("amount"), 3)+" ₮");
			}
			
			show_month_sale = false;			
			return;
		}
		
		if (show_plan_execution) {
			if(plan_collection.size() > 0){
				 final listviewAdapter adapter = new listviewAdapter(this, plan_collection);
				 lview.setAdapter(adapter);
				 Log.d("SPINNER", plan_collection.size()+"");
				 for(int a = 0; a < plan_collection.size(); a++){
					 totalGSh += plan_collection.elementAt(a).getFloat("guitsSh");
					 totalTuluv += plan_collection.elementAt(a).getFloat("tuluv");
					 totalPer += plan_collection.elementAt(a).getFloat("per");
				 }
				 txtTotalGuitsSh.setText(totalGSh+"");
				 txtTotalTuluv.setText(totalTuluv+"");
				 txtTotalPer.setText(totalPer+"");
			}
			
			show_plan_execution = false;			
			return;
		}

		
		
		if (activeActivity.equals("login")) {
			if (endingStatus == SUCCESS) {
				controlActivity();
				if (message != null && message.length() > 0)
					showInfoMessage(message);
			}
			else 
				showAlertMessage("Амжилтгүй боллоо ! Дахин нэвтэрнэ үү !");
		} else
		if (activeActivity.equals("main")) {
			if (command.equals("to_userlist"))
				userListActivity();
			if (command.equals("to_routelist"))
				routeListActivity();
			if (command.equals("to_warehouselist"))
				wareHouseListActivity();
			if (command.equals("to_today")) {
				detail_back = "";
				todayActivity();
			}
		} else
		if (activeActivity.equals("userlist")) {
			if (command.equals("to_customer"))
				customerActivity();
		} else
		if (activeActivity.equals("routelist")) {
			if (command.equals("to_customer"))				
				customerActivity();
		} else
		if (activeActivity.equals("today")) {
			if (command.equals("today_info")) {				
				dataAdapter = new SaleInfoAdapter(this, R.layout.list_item);
				listView.setAdapter(dataAdapter);
			} else
			if (command.equals("sale_info")) {				
				dataAdapter = new SaleInfoAdapter(this, R.layout.list_item);
				listView.setAdapter(dataAdapter);
			} else
			if (command.equals("order_info")) {				
				dataAdapter = new SaleInfoAdapter(this, R.layout.list_item);
				listView.setAdapter(dataAdapter);
			} else
			if (command.equals("to_customer_saledetail")) {
				detailCustomerActivity();
			} else
			if (command.equals("to_customer_orderdetail")) {
				detailCustomerActivity();
			} else
			if (command.equals("achilt_order")) {
				dataAdapter = new SaleInfoAdapter(this, R.layout.list_item);
				listView.setAdapter(dataAdapter);
			}
		} else			
		if (activeActivity.equals("detail_customer")) {//hariltsagchiin odriin detail-aas butsah
			if (command.equals("to_today")) {
				todayActivity();
			}
		} else
		if (activeActivity.equals("warehouselist")) {
			if (command.equals("to_order"))				
				orderActivity();
			if (command.equals("to_routelist"))				
				routeListActivity();
		} else			
		if (activeActivity.equals("customer")) {
			if (command.equals("customer_to_order"))
				orderActivity();
			else
			if (command.equals("customer_to_sale"))
				saleActivity();
			else
			if (command.equals("to_routelist"))
				routeListActivity();
			else
			if (command.equals("to_userlist"))
				userListActivity();
			else
			if (command.equals("send_location")) {
				if (resultCode == 0)
					Toast.makeText(this, "Ажмилттай шинэчиллээ !", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, "Ажмилтгүй !", Toast.LENGTH_SHORT).show();
			}
		} else		
		if (activeActivity.equals("sale")) {
			if (command.equals("to_customer_nonsale")) {
				customerActivity();
			} else
			if (command.equals("to_customer")) {
				customerActivity();
			} else
			if (command.equals("lease_payment")) {
				if (product_lease_info.size() > 0) {
					leaseConfirmation();
				} else
					showInfoMessage("Уг дэлгүүр дээр зээл байхгүй байна !");
			} else
				controlActivity();
		} else			
		if (activeActivity.equals("order")) {
			if (command.equals("to_customer_nonsale")) {
				customerActivity();
			} else
			if (type == PRE_SALLING && !me) {
				customerActivity();
			} else
			if (command.equals("to_warehouselist")) {
				wareHouseListActivity();
			} else
				controlActivity();
		} else
		if (activeActivity.equals("settings")) {
			if (command.equals("call_lease")) {
				leaseActivity();
			} else
			if (command.equals("call_mystorage")) {
				myStorageActivity();
			} else
			if (command.equals("last_orders")) {
				lastOrdersActivity();
			} else
			if (command.equals("current_orders")) {
				currentOrdersActivity();
			} else
			if (command.equals("change_code")) {
				if (resultCode == 0)
					Toast.makeText(this, "Ажмилттай шинэчиллээ !", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, "Ажмилтгүй !", Toast.LENGTH_SHORT).show();
			}
		} else
		if (activeActivity.equals("lease")) {
			if (product_lease_info.size() > 0) {
				leaseConfirmation();
			} else
				showInfoMessage("Уг дэлгүүр дээр зээл байхгүй байна !");
		} else
		if (activeActivity.equals("map")) {			
			customerActivity();
		} else
		if (activeActivity.equals("camera")) {			
			customerActivity();
		}
    }
	
	public void migragate() {
		if (product_sale_free_info.size() > 0) {
			product_sale_info.mergeCollection(product_sale_free_info);
		}
		
		product_sale_free_info = new Collection();
	}
	
	@Override
	public void startProcessing() {		
		if (activeActivity.equals("order")) {
			for (int i = 0; i < product_order_info.size(); i++) {			
	   			Variant vt = (Variant)product_order_info.elementAt(i);
	   			vt.put("posX", "f"+Shared.gps_location[0]);
	   			vt.put("posY", "f"+Shared.gps_location[1]);
	   			sql.insertOrderTransaction(vt);
	   			
	   			orderSent = true;
	    	}
			
			setCustomerStatus("worked", selectedCustomer.get("code"));			
			new TaskExecution().execute();	
		} else 
		if (activeActivity.equals("sale")) {
			command = "send_sale_data";
			if (product_sale_info.size() + product_sale_free_info.size() == 0) {
				showAlertMessage("Мэдээлэл илгээгдсэн байна !");
				return;
			}
			
			migragate();
			
			Shared.printLine = "\n"+Shared.COMPANY+"\n";
			Shared.printLine+= convertToLatin(userName)+"\n";
			Shared.printLine+= convertDateTimeToString()+"\n";
			Shared.printLine+= convertToLatin(selectedCustomer.get("customerName"))+"\n\n";
			int lineNumber = 1;			
			float totalrAmount = 0;			
			float totalAmount = 0;										
					
			//engiineer
			int ftotal = 0;
			boolean leased = false;
			for (int i = 0; i < product_sale_info.size(); i++) {			
	   			Variant vt = (Variant)product_sale_info.elementAt(i);
	   			String name = products.query("code", vt.getString("productCode")).get("descr");
	   			vt.put("posX", "f"+Shared.gps_location[0]);
	   			vt.put("posY", "f"+Shared.gps_location[1]);   			
	   			if (vt.getInt("rentquantity") > 0) {
	   				vt.put("quantity", vt.get("rentquantity"));
	   				vt.put("amount", vt.get("rentamount"));
	   				vt.put("price", vt.get("rentprice"));
	   				vt.put("flag", vt.get("rentamount"));   				
	   				vt.put("type", "i1");   
	   				leased = true;
	   				if (vt.getFloat("rentprice") == 0)
	        			ftotal+=vt.getInt("quantity");
	   				
	   				sql.insertTransaction(vt);   				
	   				Shared.printLine += getFixedString(lineNumber+"", 2)+getFixedString(name, 14)+getFixedString(" Z", 3)+getFixedInt(vt.getInt("quantity"),4)+"*"+getFixedPrice(vt.getFloat("rentprice"),7)+"\n";
		   				
					totalrAmount+=vt.getFloat("amount");
	   				lineNumber++;
	   				if (vt.getInt("salequantity") > 0) {
	   					vt.put("type", "i0");
	   					vt.put("quantity", vt.get("salequantity"));
	   					vt.put("amount", vt.get("saleamount"));
	   					vt.put("price", vt.get("saleprice"));
	   					vt.put("flag", "f0");
	   					sql.insertTransaction(vt);
	   					Shared.printLine += getFixedString(lineNumber+"", 2)+getFixedString(name, 14)+getFixedString(" B", 3)+getFixedInt(vt.getInt("quantity"),4)+"*"+getFixedPrice(vt.getFloat("saleprice"),7)+"\n";   						   					
	   					totalAmount+=vt.getFloat("amount");   					
						lineNumber++;
						if (vt.getFloat("saleprice") == 0)
		        			ftotal+=vt.getInt("quantity");
	   				}
	   			} else {
	   				vt.put("quantity", vt.get("salequantity"));
	   				vt.put("amount", vt.get("saleamount"));
	   				vt.put("price", vt.get("saleprice"));
	   				vt.put("flag", "f0");
	   				sql.insertTransaction(vt);   					
	   				Shared.printLine += getFixedString(lineNumber+"", 2)+getFixedString(name, 14)+getFixedString(" B", 3)+getFixedInt(vt.getInt("quantity"),4)+"*"+getFixedPrice(vt.getFloat("saleprice"),7)+"\n";	   				
					totalAmount+=vt.getFloat("amount");		
	   				lineNumber++;
	   				
	   				if (vt.getFloat("saleprice") == 0)
	        			ftotal+=vt.getInt("quantity");
	   			}
	   		}
			
			if (leased)
				setCustomerStatus("leased", selectedCustomer.get("code"));
			
			setCustomerStatus("worked", selectedCustomer.get("code"));
			insertDiscountRow();	
				
			Shared.printLine += "-------------------------------\n";
			Shared.printLine += "Zeel          "+getFixedFloat(totalrAmount,17)+"\n";
			Shared.printLine += "Belen         "+getFixedFloat(totalAmount,17)+"\n";
			if (ftotal > 0)
				Shared.printLine += "Uramshuulal   "+getFixedFloat(ftotal,17)+"\n";
			Shared.printLine += "Niit          "+getFixedFloat(totalAmount+totalrAmount,17)+"\n";
			float totalDiscount = invoiceDiscount+customerDiscount+cashDiscount;
			
			if (totalDiscount > 0) {
				Shared.printLine += "Uramshuulal   "+getFixedFloat(totalDiscount,17)+"\n";
				Shared.printLine += "Tuluh         "+getFixedFloat(totalAmount+totalrAmount-totalDiscount,17)+"\n";
			}
					
			Shared.printLine += "\n";
			Shared.printLine += "X.avsan             "+".........."+"\n";
			Shared.printLine += "X.ugsun             "+".........."+"\n";
			Shared.printLine += "\n\n\n";
	
			clearCollection();					
			
			if (Shared.BLUETOOTH_ENABLED) {
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
			    		    new TaskExecution().execute(); 
			            }
			        })
			        .setNegativeButton("Үгүй", new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {		            	
			            	dialog.dismiss();		            	
			            	Shared.bixolon.stopBixolon();		            	
			    		    new TaskExecution().execute(); 
			            }
			        })
			        .show();
				} else
					new TaskExecution().execute();			
			} else			
				new TaskExecution().execute();		
		}				
	}
	
	public void insertDiscountRow() {
		if (invoiceDiscount > 0) {
			Variant vt = new Variant();
			vt.put("productCode", "snul");
			vt.put("userCode", "s"+userCode);
			vt.put("_dateStamp", "dCURRENT_TIMESTAMP:"+todayvalue);
			vt.put("discount", "i"+selectedCustomer.getInt("parentID"));
			vt.put("type", "i0");            			            		            	
			vt.put("customerCode", "s"+selectedCustomer.get("code"));
			vt.put("quantity", "i0");
			vt.put("posX", "f"+Shared.gps_location[0]);
   			vt.put("posY", "f"+Shared.gps_location[1]);
			vt.put("amount", "f"+(-invoiceDiscount));
			vt.put("price", "f0");
			vt.put("flag", "i0");   				
			vt.put("userType", "i"+userType);
			vt.put("ticketID", "i"+currentTicketId);
		
			sql.insertTransaction(vt);
		}
	}
	
	@Override
	public void addProduct(Variant p, long quantity, long rquantity) {
		product_sale_info.removeCollection("productCode", "s"+p.getString("code"));		
		long rc = rquantity;
		long c = quantity;				
		rc = (long)(rquantity*getUnit(p));		
		c = (long)(quantity*getUnit(p));
		
		if (quantity + rquantity == 0) {
			calculateTotal();
			return;		
		}
		
		int priceTag = 1;
		if (type == PRE_SALLING) {
		//	priceTag = selectedCustomer.getInt("priceTag");
		}
		Variant vt = new Variant();
		vt.put("productCode", "s"+p.getString("code"));
		vt.put("name", "s"+p.getString("name"));
		vt.put("descr", "s"+p.getString("descr"));
		vt.put("userCode", "s"+userCode);
		vt.put("_dateStamp", "dCURRENT_TIMESTAMP:"+todayvalue);
		vt.put("discount", "i"+selectedCustomer.getInt("parentID"));
		vt.put("type", "i"+(rc>0?1:0));            			            		            	
		vt.put("customerCode", "s"+selectedCustomer.get("code"));	            		
		vt.put("rentquantity", "i"+rc);
		float rprice = prices.query("productCode", p.getString("code"), "customerType", priceTag).getFloat("price");
		vt.put("rentprice", "f"+rprice);
		vt.put("rentamount", "f"+rc*rprice);
			            		
		float price = prices.query("productCode", p.getString("code"), "customerType", priceTag).getFloat("price");
		vt.put("salequantity", "i"+c);
		vt.put("saleprice", "f"+price);
		vt.put("saleamount", "f"+c*price);
		vt.put("userType", "i"+userType);
		vt.put("ticketID", "i"+currentTicketId);
		
		product_sale_info.addCollection(vt);			
		
		calculateTotal();
	}
	
	public void addProductFree(String code, long quantity, long rquantity) {	
		product_sale_free_info.removeCollection("productCode", "s"+code);
		
		if (quantity + rquantity == 0) return;
		long rc = rquantity;
		long c = quantity;
		if (packetMode()) {
			Variant v = products.query("code", code);
			rc = (long)(rquantity*getUnit(v));		
			c = (long)(quantity*getUnit(v));
		}				
		
		Variant vt = new Variant();
		vt.put("productCode", "s"+code);
		vt.put("userCode", "s"+userCode);
		vt.put("_dateStamp", "dCURRENT_TIMESTAMP:"+todayvalue);
		vt.put("discount", "i"+selectedCustomer.getInt("parentID"));
		vt.put("type", "i"+(rc>0?1:0));
		vt.put("customerCode", "s"+selectedCustomer.get("code"));	            		
		vt.put("rentquantity", "i"+rc);
		float rprice = 0;
		vt.put("rentprice", "f"+rprice);
		vt.put("rentamount", "f"+rc*rprice);
			            				
		vt.put("salequantity", "i"+c);
		vt.put("saleprice", "f"+0);
		vt.put("saleamount", "f"+(c*0));
		vt.put("userType", "i"+userType);
		vt.put("ticketID", "i"+currentTicketId);
		
		product_sale_free_info.addCollection(vt);		
	}
	
	@Override
	public void addOrderProduct(Variant p, long quantity) {
		int pricetag = 1;
		if (type == PRE_SALLING && !me)
			pricetag = (int)selectedCustomer.getInt("priceTag");		
		
		float price = prices.query("productCode", p.getString("code"), "customerType", pricetag).getFloat("price");
		Log.d("d", prices.size()+" rows");
		if (price == 0) {
			showInfoMessage("Уг барааны үнэ ороогүй байна ! Таталт хийнэ үү !");
			return;
		}				
		product_order_info.removeCollection("productCode", "s"+p.getString("code"));
		calculateOrderTotal();
		if (quantity == 0) return;
		
		if (type == PRE_SALLING) {			
			price = prices.query("productCode", p.getString("code"), "customerType", pricetag).getFloat("price");			
			Variant q = products.query("code", p.getString("code"));
			int wareHouseID = (int)(storage.query("productCode", p.getString("code")).getInt("wareHouseID")); 
			
			Variant vt = new Variant();
			vt.put("productCode", "s"+p.getString("code"));
			vt.put("name", "s"+p.getString("name"));
			vt.put("descr", "s"+p.getString("descr"));
			vt.put("userCode", "s"+userCode);
			vt.put("customerCode", "s"+selectedCustomer.getString("code"));
			vt.put("_date", "dCURRENT_TIMESTAMP");
			vt.put("requestCount", "i"+(long)(quantity*getUnit(q)));
			Log.d("D", "size "+p.getString("code")+" "+q.getFloat("size"));
			if (q.getFloat("size") == 0) {
				vt.put("packetCount", "f"+(float)(quantity));
			}
			else {
				vt.put("packetCount", "f"+(float)(quantity/q.getFloat("size")));
			}
			vt.put("wareHouseID", "i"+wareHouseID);
			vt.put("packetCode", "i0");
			vt.put("ticketID", "i"+currentTicketId);
			vt.put("price", "f"+price);
			
			product_order_info.addCollection(vt);
		} else {			
			Variant vt = new Variant();
			vt.put("productCode", "s"+p.getString("code"));
			vt.put("name", "s"+p.getString("name"));
			vt.put("descr", "s"+p.getString("descr"));
			vt.put("userCode", "s"+userCode);
			vt.put("customerCode", "s"+userCode);
			vt.put("_date", "dCURRENT_TIMESTAMP");
			vt.put("requestCount", "i"+(long)(quantity*getUnit(p)));
			vt.put("wareHouseID", "i"+Shared.wareHouseId);
			vt.put("packetCode", "i0");
			vt.put("ticketID", "i"+currentTicketId);
					
			vt.put("price", "f"+price);
			
			product_order_info.addCollection(vt);
		}
		
		calculateOrderTotal();
	}		
	
	
	@Override
	public void addPacketOrderProduct(Variant p, long quantity) {
		product_order_info.removeCollection("productCode", "s"+p.getString("code"));				
		if (quantity == 0) {
			calculateOrderTotal();
			return;
		}
		
		if (type == PRE_SALLING) {			
			Variant q = packets.query("code", p.getString("code"));
			int wareHouseID = (int)(storage.query("productCode", p.getString("code")).getInt("wareHouseID")); 
						
			Variant vt  = new Variant();
			vt.put("productCode", "s"+p.getString("code"));    		
			vt.put("requestCount", "i"+quantity);
			vt.put("packetCount", "f"+(quantity));
			vt.put("userCode", "s"+userCode);
			vt.put("customerCode", "s"+selectedCustomer.get("code"));
			vt.put("ticketID", "i"+currentTicketId);
			vt.put("packet", "syes");
			vt.put("wareHouseID", "i"+wareHouseID);						
			
			product_order_info.addCollection(vt);
		} else {			
			Variant q = packets.query("code", p.getString("code"));
			int wareHouseID = (int)(storage.query("productCode", p.getString("code")).getInt("wareHouseID")); 
			
			Variant vt  = new Variant();
			vt.put("productCode", "s"+p.getString("code"));    		
			vt.put("requestCount", "i"+quantity);
			vt.put("packetCount", "f"+quantity);
			vt.put("userCode", "s"+userCode);
			vt.put("packet", "syes");
			vt.put("customerCode", "s"+selectedCustomer.get("code"));
			vt.put("ticketID", "i"+currentTicketId);
			vt.put("wareHouseID", "i"+wareHouseID);						
			
			product_order_info.addCollection(vt);
		}
		
		calculateOrderTotal();
	}	
	
	@Override
	public void checkSendCustomer() {
		   EditText _name = (EditText)findViewById(R.id._name); 
		   EditText location = (EditText)findViewById(R.id.location);
			
			
			if (_name.getText().toString().length() > 0 && location.getText().toString().length() > 0) {
				new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("Анхааруулага")
		        .setMessage("Уг дэлгүүр системд өмнө нь бүртгэгдээгүй гэдэгт итгэлтэй байна уу! Зөв гэдэгтээ итгэлтэй байна уу ?")
		        .setPositiveButton("Тийм", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	command = "send_customer";
		            	new TaskExecution().execute();
		            }

		        })
		        .setNegativeButton("Үгүй", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                    
		            }
		        })
		        .show();
			}			
			else
				Toast.makeText(this, "Мэдээлэл дутуу байна ! Дахин оруулна уу !", Toast.LENGTH_LONG).show();
	   }
	   
	   public void sendRegisterCustomer() {
		   Variant vt = new Variant();
			EditText _name = (EditText)findViewById(R.id._name); 
			EditText location = (EditText)findViewById(R.id.location);
			EditText phone1 = (EditText)findViewById(R.id.phone1);
			
			Collection collection = fromJSON("Customer", "max(customerID) as newid", "i", " ");
			long cd = 0, newid = 0;
			String code;
			if (collection.size() > 0) {
				Variant v = (Variant)collection.elementAt(0);
				code = "H"+(v.getInt("newid")+1);
				newid = v.getInt("newid")+1;
				cd = Integer.parseInt(code.substring(1, code.length()));			
			} else {
				Random generator = new Random();
				int randomIndex = 1000+generator.nextInt( 10000 );
				code = "H"+randomIndex;
			}
				
			vt.put("code", "s"+code);
			vt.put("name", "s"+_name.getText().toString());
			vt.put("location", "s"+location.getText().toString());
			vt.put("customerName", "s"+_name.getText().toString()+"|"+location.getText().toString());
			vt.put("lat", "f"+Shared.gps_location[0]);
			vt.put("lng", "f"+Shared.gps_location[1]);
			vt.put("phone1", "s"+phone1.getText().toString());
			vt.put("posX", "f"+Shared.gps_location[0]);
			vt.put("posY", "f"+Shared.gps_location[1]);
			vt.put("active", "i1");
			vt.put("parentID", "i"+newid);
			vt.put("type", "i"+userType); //horeca
			vt.put("priceTag", "i"+selPrice);
			vt.put("subid", "s"+selRoute);
			vt.put("routeID", "s"+selRoute);
			vt.put("staff", "s"+userCode);
			vt.put("log", "s"+userCode);
			vt.put("_date", "dCURRENT_TIMESTAMP");
			int success = 0;
			if ((success = toJSON("insert", "Customer", "code,name,location,posX,posY,phone1,active,type,priceTag,parentID,subid,staff,log,_date", vt)) != -1) {		
				vt = new Variant();
				vt.put("routeID", "s"+selRoute);
				vt.put("customerCode", "s"+code);
				if ((success = toJSON("insert","Route_Customer", "routeID,customerCode", vt)) != -1) {				
					sql.deleteWhere("Route_Customer", "routeID='"+selRoute+"'");				
				}
			}				
	   }
	   
	   @Override
	   public void sendImages() {
		   new AlertDialog.Builder(this)
	       .setIcon(android.R.drawable.ic_dialog_alert)
	       .setTitle("Анхааруулага")
	       .setMessage("Илгээх үү !")
	       .setPositiveButton("Тийм", new DialogInterface.OnClickListener() {
	           @Override
	           public void onClick(DialogInterface dialog, int which) {
		           	dialog.dismiss();
		           	command = "send_images";
		       		new TaskExecution().execute();
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
	   
	   public class ConnectivityReceiver extends BroadcastReceiver{

	        @Override
	        public void onReceive(Context context, Intent intent) {
	            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
	            
	            if(null != info)
	            {                		
	                String state = getNetworkStateString(info.getState());
	                if (texts.containsKey("network"))
	                	texts.get("network").setText("Сүлжээ : "+state);
	                Shared.DATA_ENABLED = state.equals("Connected");                
	            } 
	        }
		}
		
		private String getNetworkStateString(NetworkInfo.State state){
		    String stateString = "Алдаа";
		    
		    switch(state)
		    {
		            case CONNECTED:         stateString = "Хэвийн";              break;
		            case CONNECTING:        stateString = "Холбогдож байна";     break;
		            case DISCONNECTED:      stateString = "Салсан";   break;
		            case DISCONNECTING:     stateString = "Тасалдсан";  break;
		            case SUSPENDED:         stateString = "Байхгүй";              break;
		            default:                        stateString = "Алдаа";                break;
		    }
		    
		    return stateString;
		}

		public ConnectivityReceiver receiver;
		
		
		public void seeMonthSale() {
			selectedCustomer = collection.elementAt(position);
			show_month_sale = true;
			new TaskExecution().execute();	
		}
		
		float totalGSh = 0,totalTuluv =0, totalPer = 0;
		public void seePlanAndExecution() {
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);                 
			 alert.setTitle("Төлөвлөгөө Гүйцэтгэл");
			 LayoutInflater factory = LayoutInflater.from(this);            
		     final View textEntryView = factory.inflate(R.layout.plan_execution, null);	        
			 txtTotalGuitsSh = (TextView)textEntryView.findViewById(R.id.totalGuitsSh);
			 txtTotalTuluv = (TextView)textEntryView.findViewById(R.id.totalTuluv);
			 txtTotalPer = (TextView)textEntryView.findViewById(R.id.totalPer);
			 final Spinner spinnerChannel = (Spinner)textEntryView.findViewById(R.id.spChannel);
			 lview = (ListView)textEntryView.findViewById(R.id.listviewPlan);
			 final listviewAdapter adapter = new listviewAdapter(this, plan_collection);
			 lview.setAdapter(adapter);
			 
			 spinnerChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { 
				        // Your code here
				    	//seePlanAndExecution();
				    //	lview.setAdapter(null);
				    	totalGSh =0;
						totalTuluv =0;
						totalPer =0;
				    	spChannel = spinnerChannel.getSelectedItem().toString();
				    	show_plan_execution = true;
						new TaskExecution().execute();
						
				    } 

				    public void onNothingSelected(AdapterView<?> adapterView) {
				        return;
				    } 
				}); 
			
			 alert.setView(textEntryView);
			 alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
				 public void onClick(DialogInterface dialog, int whichButton) {  
			       // String value = input.getText().toString();
			        dialog.dismiss();
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
}
