package mxc.app.engine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mxc.android.picker.NumberPicker;
import mxc.android.picker.OrderNumberPicker;
import oss.android.vita.app.R;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapView;

public class MasterActivity extends BasicActivity implements OnClickListener, OnItemClickListener {			
	public SaleProductAdapter saleAdapter;
	public OrderProductAdapter orderAdapter;
	public SaleInfoAdapter dataAdapter;
	public CustomerAdapter customerAdapter;
	public ListView listView;	
	public SaleInfoDetailAdapter detailAdapter;
	public ImageLoader imgLoader;
	public String spChannel = "vitafit";
	public ListView lview;
	public TextView  txtTotalGuitsSh;
	public TextView  txtTotalTuluv ;
	public TextView  txtTotalPer ;
	public void addProduct(Variant p, long c, long r) {
		
	}
	
	public void addProductFree(String code, long c, long r) {
		
	}
	
	public void addOrderProduct(Variant p, long quantity) {
		
	}
	
	public void addPacketOrderProduct(Variant p, long quantity) {
		
	}

	public void checkSendCustomer() {
		
	}
	
	public void sendImages() {
		
	}
	
	public void getDisplaySize() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		height = metrics.heightPixels;
		width = metrics.widthPixels;
		dpi = metrics.densityDpi;				
	}	
			
	public void setBackground(int drawable_id, int layout_id, int layout_type) {
		getDisplaySize();		
				
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawable_id);
        BitmapDrawable wallpaperDrawable = new BitmapDrawable(bmp);       
        wallpaperDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        
        switch (layout_type) {
	        case FRAME: 
	    		FrameLayout flayout = (FrameLayout)findViewById(layout_id);
	    		flayout.setBackgroundDrawable(wallpaperDrawable);
	    		break;
        	case LINEAR: 
        		LinearLayout llayout = (LinearLayout)findViewById(layout_id);
        		llayout.setBackgroundDrawable(wallpaperDrawable);
        		break;
        	case RELATIVE: 
        		RelativeLayout rlayout = (RelativeLayout)findViewById(layout_id);
        		rlayout.setBackgroundDrawable(wallpaperDrawable);
        		break;
        }          
        
    }
	
	public void addEdit(int edit_id, String key, String table, String fields, String types, String where, String field) {
		Collection c = sql.selectAll(table, types, fields, where, null);
		String value = "";
		if (c.size() > 0) {
			value = c.elementAt(0).get(field);
		}
		
		EditText edit = (EditText)findViewById(edit_id);
		edit.setOnClickListener(this);
		edit.setTag(key);		
		edit.setText(value);
		edits.put(key, edit);
	}
	
	public void addEdit(int edit_id, String key) {
		EditText edit = (EditText)findViewById(edit_id);
		edit.setOnClickListener(this);
		edit.setTag(key);
		edit.setText("");
		edits.put(key, edit);
	}
	
	public void addButton(int button_id, String key) {			
		Button button = (Button)findViewById(button_id);
		button.setOnClickListener(this);
		button.setTag(key);
		buttons.put(key, button);
	}		
	
	public void addDisabledButton(int button_id, String key) {			
		Button button = (Button)findViewById(button_id);
		button.setOnClickListener(this);
		button.setEnabled(false);
		button.setTag(key);
		buttons.put(key, button);
	}		
	
	public class TaskExecution extends AsyncTask<Void, Void, Void> {
      	
		 @Override
		 protected void onPostExecute(Void result) {
			 completeReading();
		 }

		 @Override
		 protected void onPreExecute() {
			 preStarting();
		 }

		 @Override
		 protected void onProgressUpdate(Void... values) {
			 super.onProgressUpdate(values);
			 Log.d("d", System.currentTimeMillis()+" ms");
		 }

		 @Override
		 protected Void doInBackground(Void... arg0) {
			 preProcessing();
			 return null;
		 }
	}
	
	public void preProcessing() {
		
	}
	
	public void preStarting() {			
		if (pd != null && pd.isShowing()) pd.dismiss();
		pd = ProgressDialog.show(this, "", "", true, false);			
		pd.setContentView(R.layout.dialog_loading);
		
		RotateAnimation anim = new RotateAnimation(0f, 360f, getResources().getDrawable(R.drawable.boot).getIntrinsicWidth()/2, getResources().getDrawable(R.drawable.boot).getIntrinsicWidth()/2);
		anim.setInterpolator(new LinearInterpolator());		
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(700);

		ImageView splash = (ImageView)pd.findViewById(R.id.loading);
		splash.startAnimation(anim);
		
		pd.show();
		
		//timerDelayRemoveDialog(120*1000, pd);
    }
		
	public void sendLocation() {		
		   active = new Variant();
		   active.put("posX", "f"+Shared.gps_location[0]);
		   active.put("posY", "f"+Shared.gps_location[1]);
		   active.put("_date", "dCURRENT_TIMESTAMP");
		   active.put("log", "s"+userCode);
		   sql.updateRouteCustomerPos(selectedCustomer, Shared.gps_location[0], Shared.gps_location[1]);
		   
		   command = "send_location";
		   new TaskExecution().execute();		   		  
	}
	
	public void checkNonCompleteTransactions() {
		Collection collection = sql.selectAllTransaction();
		Collection collection1 = sql.selectAllOrderTransaction();
		int t = collection.size() + collection1.size();
		if (t > 0) {
			new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Анхааруулага")
	        .setMessage("Амжилтгүй гүйлгээ "+t+" ширхэг байна илгээхүү ? Та сүлжээ сайн байгаа газраас илгээвэл тохиромжтой !")
	        .setPositiveButton("Тийм", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	command = "non_complete";
	            	new TaskExecution().execute();		            	
	            }
	        })
	        .setNegativeButton("Үгүй", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                    
	            }
	        })
	        .show();
		} else
			showAlertMessage("Амжилтгүй гүйлгээ байхгүй байна !");
	}
	
	public void timerDelayRemoveDialog(long time, final Dialog d){
	    new Handler().postDelayed(new Runnable() {
	        public void run() {
	        	if (d != null && d.isShowing())
	        		d.dismiss();         
	        }
	    }, time); 
	}
  
    public void completeReading() {
    	if (endingStatus == SUCCESS)
    		Toast.makeText(this, "Амжилттай !", Toast.LENGTH_SHORT).show();
    	if (endingStatus == FAILED)
    		Toast.makeText(this, "Алдаа гарлаа !", Toast.LENGTH_SHORT).show();
    	
    	dialogDismiss.sendEmptyMessage(DISMISS);
    }
    
    public Handler dialogDismiss = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            super.handleMessage(msg);

            switch (msg.what)
            {
                case DISMISS:
                	if (pd != null && pd.isShowing())
                		pd.cancel();
                    break;
            }
        }
    };

    public void toolbarSet(String text) {
    	TextView title = (TextView) findViewById(R.id.title);
		title.setText(text);
		//title.setTypeface(Shared.tf);
		addButton(R.id.back, "back");
    }
    
    public void loginActivity() {    	
    	activeActivity = "login";
		setContentView(R.layout.login_display);
		setBackground(R.drawable.bg, R.id.bg, LINEAR);
				
		addEdit(R.id.login_user, "login_user", "Login", "code,password", "s,s", null, "code");
		addEdit(R.id.login_code, "login_code");
		
		addButton(R.id.login_request, "login_request");
		addButton(R.id.main_exit, "main_exit");						
	}
	
	public void controlActivity() {
		activeActivity = "main";
		setContentView(R.layout.control_main);
		setBackground(R.drawable.bg, R.id.bg, LINEAR);
		Log.d("userType", userType+" ");
		if (userType == SUPERVISOR) {
			addButton(R.id.control_sale, "control_sale");			
			addDisabledButton(R.id.control_order, "control_order");
			addDisabledButton(R.id.control_today, "control_today");
			addButton(R.id.control_refresh, "control_refresh");
			addButton(R.id.control_settings, "control_settings");
		}
		else {		
			addDisabledButton(R.id.control_sale, "control_sale");		
			addButton(R.id.control_order, "control_order");
			addButton(R.id.control_today, "control_today");
			addButton(R.id.control_refresh, "control_refresh");
			addButton(R.id.control_settings, "control_settings");
		}
		addButton(R.id.control_back, "control_back");			
		
		TextView network = (TextView)findViewById(R.id.network);
		texts.put("network", network);
	}		  
	
	public void routeListActivity() {
		activeActivity = "routelist";
		setContentView(R.layout.simple_list);						
		ListView list = (ListView)findViewById(R.id.listview);
		list.setOnItemClickListener(this);
		list.setAdapter(new RouteAdapter(this, R.layout.list_item));
		
		toolbarSet("Чиглэлийн жагсаалт");
	}
	
	public void userListActivity() {
		activeActivity = "userlist";
		setContentView(R.layout.simple_list);						
		ListView list = (ListView)findViewById(R.id.listview);
		list.setOnItemClickListener(this);
		list.setAdapter(new UserAdapter(this, R.layout.list_item));
		
		toolbarSet("Борлуулагчдын жагсаалт");
	}
	
	public void wareHouseListActivity() {
		activeActivity = "warehouselist";
		setContentView(R.layout.simple_list);						
		ListView list = (ListView)findViewById(R.id.listview);
		list.setOnItemClickListener(this);
		list.setAdapter(new AnyAdapter(this, R.layout.list_item));
		registerForContextMenu(list);
		
		toolbarSet("Агуулахын жагсаалт");
	}
	
	public void customerActivity() {
		activeActivity = "customer";
		setContentView(R.layout.search_list);				
        
		listView = (ListView)findViewById(R.id.listview);
		listView.setOnItemClickListener(this);
		customerAdapter = new CustomerAdapter(this, R.layout.list_item);
		listView.setAdapter(customerAdapter);		
		listView.setTextFilterEnabled(true);		
		
		EditText inputSearch = (EditText) findViewById(R.id.inputSearch);		    		         
		inputSearch.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence cs, int start, int before, int count) {		        
		        if (count < before) {
		        	customerAdapter.resetData();
				}

		        customerAdapter.getFilter().filter(cs.toString());
		    }
		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { 
		    	
		    }
		    @Override
		    public void afterTextChanged(Editable arg0) {
		    	
		    }
		});
		
		registerForContextMenu(listView);
		
		if (Shared.customerScrollLocation < 0 || Shared.customerScrollLocation >= collection.size())
			Shared.customerScrollLocation = 0;
		listView.setSelection(Shared.customerScrollLocation);		
		
		toolbarSet("Харилцагчид");
	}
	
	public void detailCustomerActivity() {
		activeActivity = "detail_customer";
		setContentView(R.layout.simple_list);
		toolbarSet(customTitle);
		
		listView = (ListView)findViewById(R.id.listview);
		listView.setOnItemClickListener(this);		
		detailAdapter = new SaleInfoDetailAdapter(this, R.layout.list_item);
		listView.setAdapter(detailAdapter);
	}
	
	public void saleActivity() {
		clearCollection();		
		activeActivity = "sale";
		setContentView(R.layout.sale_list);
		listView = (ListView)findViewById(R.id.list);
		listView.setOnItemClickListener(this);				
		listView.setCacheColorHint(getResources().getColor(R.color.black));
		saleAdapter = new SaleProductAdapter(this, R.layout.product_item);
		listView.setAdapter(saleAdapter);
		listView.setTextFilterEnabled(true);		
		
		EditText inputSearch = (EditText) findViewById(R.id.inputSearch);		    		         
		inputSearch.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence cs, int start, int before, int count) {		        
		        if (count < before) {
		        	saleAdapter.resetData();
				}

		        saleAdapter.getFilter().filter(cs.toString());
		    }
		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { 
		    	
		    }
		    @Override
		    public void afterTextChanged(Editable arg0) {
		    	
		    }
		});
		
		ListView blistView = (ListView)findViewById(R.id.blist);
		blistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {				
				activeBrand = brands.elementAt(pos).getString("brand");
				refreshSaleProducts(activeBrand);
			}
			
		});
		blistView.setTag("blist");
		blistView.setCacheColorHint(getResources().getColor(R.color.dock));		
		blistView.setAdapter(new SimpleAdapter(this, R.layout.list_item));
		
		toolbarSet("Борлуулалт");
		
		TextView company = (TextView) findViewById(R.id.title);
        company.setText(selectedCustomer.get("customerName"));
        company.setTypeface(Shared.tf);
        
        TextView total_row = (TextView) findViewById(R.id.descr);        
        total_row.setTypeface(Shared.tf);        
        
        texts.put("company", company);
        texts.put("sale_total_row", total_row);        
        
        Button button = (Button) findViewById(R.id.sale_filter);
        button.setTag("product_filter");
        button.setOnClickListener(this);
        registerForContextMenu(button);
        buttons.put("product_filter", button);
        
        Button button2 = (Button) findViewById(R.id.lease_payment);
        button2.setTag("lease_payment");
        button2.setOnClickListener(this);        
        buttons.put("lease_payment", button2);
        
        Button button1 = (Button) findViewById(R.id.sale_send);
        button1.setTag("sale_send");
        button1.setOnClickListener(this);        
        buttons.put("sale_send", button1);
	}
	
	public void refreshSaleProducts(String brand) {
		products = sql.selectAll("Product", "s,s,s,s,s,s,f,f", "code,name,descr,brand,sub_brand,vendor,unit,size", "brand='"+brand+"'", "name");
		saleAdapter = new SaleProductAdapter(this, R.layout.product_item);
		listView.setAdapter(saleAdapter);
	}
	
	public void refreshOrderProducts(String brand) {		
		products = sql.selectAll("Product", "s,s,s,s,s,s,f,f", "code,name,descr,brand,sub_brand,vendor,unit,size", "brand='"+brand+"'", "name");
		orderAdapter = new OrderProductAdapter(this, R.layout.product_item);
		listView.setAdapter(orderAdapter);		
	}
	
	public void orderActivity() {
		clearCollection();		
		activeActivity = "order";		
		setContentView(R.layout.order_list);
		listView = (ListView)findViewById(R.id.list);
		listView.setOnItemClickListener(this);
		listView.setCacheColorHint(getResources().getColor(R.color.black));
		orderAdapter = new OrderProductAdapter(this, R.layout.product_item);
		listView.setAdapter(orderAdapter);
		listView.setTextFilterEnabled(true);
		EditText inputSearch = (EditText) findViewById(R.id.inputSearch);		    		         
		inputSearch.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence cs, int start, int before, int count) {		        
		        if (count < before) {
		        	orderAdapter.resetData();
				}

		        orderAdapter.getFilter().filter(cs.toString());
		    }
		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { 
		    	
		    }
		    @Override
		    public void afterTextChanged(Editable arg0) {
		    	
		    }
		});
		
		ListView blistView = (ListView)findViewById(R.id.blist);
		blistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {								
				activeBrand = brands.elementAt(pos).getString("brand");				
				refreshOrderProducts(activeBrand);
			}
			
		});
		blistView.setTag("blist");
		blistView.setCacheColorHint(getResources().getColor(R.color.dock));						
		blistView.setAdapter(new SimpleAdapter(this, R.layout.list_item));
	
		TextView title = (TextView) findViewById(R.id.title);        
		title.setTypeface(Shared.tf);
		if (way == 1)
			title.setText(Shared.wareHouseName+" "+selectedCustomer.getString("customerName"));
		else
			title.setText(Shared.wareHouseName);
        texts.put("order_title", title);
        
		TextView total_row = (TextView) findViewById(R.id.descr);        
        total_row.setTypeface(Shared.tf);           
        texts.put("order_total_row", total_row);     
        
        Button button = (Button) findViewById(R.id.order_filter);
        button.setTag("product_filter");
        button.setOnClickListener(this);
        registerForContextMenu(button);
        buttons.put("product_filter", button);
        
        Button button1 = (Button) findViewById(R.id.order_send);
        button1.setTag("order_send");
        button1.setOnClickListener(this);        
        buttons.put("order_send", button1);
        
        if (way == 1) {			
			toolbarSet(Shared.wareHouseName+"|"+selectedCustomer.getString("customerName"));
        } else
        	toolbarSet(Shared.wareHouseName);
		calculateOrderTotal();				
	}				
	
	public void mapActivity() {
		activeActivity = "map";
		setContentView(R.layout.map);
		
		toolbarSet("Газрын зураг");		
		TextView company = (TextView) findViewById(R.id.title);
        company.setText(selectedCustomer.get("customerName"));
        company.setTypeface(Shared.tf);		
        if (mapView == null) {        	
            mapView = new MapView(this, this.getString(R.string.APIMapKey));
        }
        
        LinearLayout map = (LinearLayout)findViewById(R.id.mapview);        
        map.addView(mapView);
                
		mapView.setBuiltInZoomControls(true);
		//mapView.setStreetView(true);
		mapView.setSatellite(true);
		mapController = mapView.getController();		
		mapController.setZoom(15); // Zoon 1 is world view		
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new GeoUpdateHandler());		
 
        getLocation();
      //  mapView.invalidate();
        
        Button send = (Button)findViewById(R.id.sendloc);
        send.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {                  	
            	sendConfirmation();
            }
        });
        
        Button camera = (Button)findViewById(R.id.snapshot);
        camera.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {                  	
            	cameraActivity();
            }
        });
	}
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}

	public void cameraActivity() {
		activeActivity = "camera";
		setContentView(R.layout.camera);
		
		toolbarSet("Зураг авах");		
		_image = ( ImageView ) findViewById( R.id.image );
        _field = ( TextView ) findViewById( R.id.field );
        Button _button = ( Button ) findViewById( R.id.button );
        _button.setOnClickListener( new PhotoTaker() ); 
        
        Button _button1 = ( Button ) findViewById( R.id.complete );
        _button1.setTag("camera_complete");
        _button1.setEnabled(false);
        _button1.setOnClickListener(this);        
        buttons.put("camera_complete", _button1);
	}
	
	public void todayActivity() {
		products = sql.selectAll("Product", "s,s,s,s,s,s,f,f", "code,name,descr,brand,sub_brand,vendor,unit,size", null, "name");
		prices = sql.selectAll("Price", "s,f,i", "productCode,price,customerType", null, "productCode");
		
		clearCollection();
		
		collection = new Collection();
		command = "order_info";
		if (detail_back.length() > 0)
			command = detail_back;
		
		activeActivity = "today";
		setContentView(R.layout.total_list);
		toolbarSet("Өнөөдөрийн орсон дэлгүүрүүд");
		
		TextView title = (TextView) findViewById(R.id.title);				
		texts.put("today_title", title);
        
		TextView sums = (TextView) findViewById(R.id.sums);				
		texts.put("today_sums", sums);
		
		Button button = (Button) findViewById(R.id.data_date);
        button.setTag("data_date");
        button.setOnClickListener(this);        
        buttons.put("data_date", button);
        
		Button button1 = (Button) findViewById(R.id.switch_data);
        button1.setTag("switch_data");        
        button1.setText("Ачилт хийх");          
        button1.setOnClickListener(this);        
        buttons.put("switch_data", button1);
        registerForContextMenu(button1);        
              
                
        listView = (ListView)findViewById(R.id.list);
		listView.setOnItemClickListener(this);		
		dataAdapter = new SaleInfoAdapter(this, R.layout.list_item);
		listView.setAdapter(dataAdapter);
		
		new TaskExecution().execute();
	}		
	
	public void registerCustomerActivity() {
		activeActivity = "register_customer";
		setContentView(R.layout.register_customer);
		toolbarSet("Харилцагч бүртгүүлэх");
		
		Collection routeList = sql.selectAll("Route", "s,s", "routeID,routeName", null, "routeName");
		Spinner spinner = (Spinner)this.findViewById(R.id.routeList);  
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,  
                routeList.toArray("routeID", "routeName"));
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
        spinner.setAdapter(adapter);  
        spinner.setOnItemSelectedListener(new RouteOnItemSelectedListener());
        
        user_types = sql.selectAll("User_Type", "i,s,i", "_group,descr,price_tag", null, "_group");        
        Spinner spinner1 = (Spinner)this.findViewById(R.id.priceList);  
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,  
                user_types.toArray("price_tag", "descr"));
        
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
        spinner1.setAdapter(adapter1);  
        spinner1.setOnItemSelectedListener(new PriceListOnItemSelectedListener());        
        
        Button button1 = (Button) findViewById(R.id.customer_send);
        button1.setTag("customer_send");
        button1.setOnClickListener(this);        
        buttons.put("customer_send", button1);
	}
	
	public void leaseActivity() {
		activeActivity = "lease";
		setContentView(R.layout.simple_list);		
				
		ListView list = (ListView)findViewById(R.id.listview);
		list.setOnItemClickListener(this);
		list.setAdapter(new LeaseAdapter(this, R.layout.list_item));
		
		toolbarSet("Зээлтэй харилцагчид");
	}
	
	public void myStorageActivity() {
		activeActivity = "mystorage";
		setContentView(R.layout.simple_list);		
				
		ListView list = (ListView)findViewById(R.id.listview);
		list.setOnItemClickListener(this);
		list.setAdapter(new StorageAdapter(this, R.layout.list_item));
		
		toolbarSet("Одоогийн үлдэгдэл");
	}
		
	public void lastOrdersActivity() {
		activeActivity = "lastorders";
		setContentView(R.layout.simple_list);		
				
		ListView list = (ListView)findViewById(R.id.listview);
		list.setOnItemClickListener(this);
		list.setAdapter(new LastOrderAdapter(this, R.layout.list_item));
		
		toolbarSet("Rought Report");
	}
	
	public void currentOrdersActivity() {
		activeActivity = "currentorders";
		setContentView(R.layout.simple_list);		
				
		ListView list = (ListView)findViewById(R.id.listview);
		list.setOnItemClickListener(this);
		list.setAdapter(new CurrentOrderAdapter(this, R.layout.list_item));
		
		toolbarSet("Daily Report");
	}
	
	public void settingsActivity() {
		activeActivity = "settings";
		setContentView(R.layout.simple_list);
		ArrayAdapter<String> adapter;
		if (userType == SUPERVISOR) 
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.control_settings_array_sup));
		else
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.control_settings_array));
		
		ListView list = (ListView)findViewById(R.id.listview);
		list.setOnItemClickListener(this);
		list.setAdapter(adapter);
		
		toolbarSet("Бусад");
	}
	
	
	@Override
	public void onClick(View v) {
		String tag = v.getTag().toString();
		
		if (tag.equals("data_date")) {
			showDialog(DATE_DIALOG_ID);
		} else
		if (tag.equals("out_date")) {
			showDialog(DATE_DIALOG_ID_1);
		} else
		if (tag.equals("switch_data")) {
			for (int i = 0; i < collection.size(); i++) 
    		if (checked[i]) {
    			v.showContextMenu();
    			break;
    		}				
			return;
		} else		
		if (tag.equals("camera_complete")) {
			if (way == 1) {	            		
        		command = "customer_to_order";
        		new TaskExecution().execute();
        	} else {
        		command = "customer_to_sale";
        		new TaskExecution().execute();
        	}
			return;
		}		
		
		if (tag.equals("back")) {
			if (activeActivity.equals("detail_customer")) {
				if (product_order_info.size() > 0) {
					new AlertDialog.Builder(this)
			        .setIcon(android.R.drawable.ic_dialog_alert)
			        .setTitle("Анхааруулга")
			        .setMessage("Та "+product_order_info.size()+" ширхэг барааны буцаалтыг өөрийн үлдэгдэл рүү шилжүүлэх үү ?")
			        .setPositiveButton("Тийм", new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			            	dialog.dismiss();
			            	/*command = "to_customer";
							new TaskExecution().execute();*/
			            }
			        })
			        .setNegativeButton("Үгүй", new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {		            							
							dialog.dismiss();		            	
			            	command = "to_today";
							new TaskExecution().execute();
			            }
			        })
			        .show();
				} else {				
					command = "to_today";
					new TaskExecution().execute();
				}
			} else
			if (activeActivity.equals("customer")) {
				if (userType == SUPERVISOR) {
					command = "to_userlist";
					new TaskExecution().execute();
				} else {
					command = "to_routelist";
					new TaskExecution().execute();
				}
			} else
			if (activeActivity.equals("lease")) {
				settingsActivity();	
			} else		
			if (activeActivity.equals("map")) {
				command = "to_customer";
				new TaskExecution().execute();
			} else	
			if (activeActivity.equals("camera")) {
				command = "to_customer";
				new TaskExecution().execute();
			} else				
			if (activeActivity.equals("routelist")) {
				controlActivity();
			} else
			if (activeActivity.equals("sale")) {							
				new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("Анхааруулга")
		        .setMessage("Уг дэлгүүрт орсон борлуулалт хийгээгүй !")
		        .setPositiveButton("Буцах", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.dismiss();
		            	command = "to_customer";
						new TaskExecution().execute();
		            }
		        })
		        .setNegativeButton("Тийм", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {		            							
						dialog.dismiss();		            	
		            	command = "to_customer_nonsale";
						new TaskExecution().execute();
		            }
		        })
		        .show();				
			} else
			if (activeActivity.equals("order")) {
				if (type == PRE_SALLING && !me) {
					new AlertDialog.Builder(this)
			        .setIcon(android.R.drawable.ic_dialog_alert)
			        .setTitle("Анхааруулга")
			        .setMessage("Уг дэлгүүрт орсон захиалга хийгээгүй !")
			        .setPositiveButton("Тийм", new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			            	dialog.dismiss();
			            	if (me) {
			            		controlActivity();
			            	} else {
			            		command = "to_customer_nonsale";
			            		new TaskExecution().execute();
			            	}
			            }
			        })
			        .setNegativeButton("Үгүй", new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			            	dialog.dismiss();
			            	if (me) {
			            		controlActivity();
			            	} else {
			            		command = "to_customer";
			            		new TaskExecution().execute();
			            	}
			            }
			        })
			        .show();					
				} else {
					command = "to_warehouselist";
					new TaskExecution().execute();
				}
			} else
			if (activeActivity.equals("today")) {
				controlActivity();	
			} else
			if (activeActivity.equals("settings")) {
				controlActivity();
			} else
			if (activeActivity.equals("warehouselist")) {
				controlActivity();
			} else
			if (activeActivity.equals("userlist")) {
				controlActivity();
			} else
			if (activeActivity.equals("register_customer")) {
				settingsActivity();
			} else
			if (activeActivity.equals("mystorage")) {
				settingsActivity();
			} else
			if (activeActivity.equals("lastorders")) {
				settingsActivity();
			} else
			if (activeActivity.equals("currentorders")) {
				settingsActivity();
			}
			
			return;
		}
		
		if (tag.equals("main_exit")) {
			finish();
		} else		
		if (tag.equals("login_request")) {
			new TaskExecution().execute();
		}
		
		//control
		if (tag.equals("control_order")) {
			if (type == PRE_SALLING) way = 1;
			if (Shared.WARE_HOUSE_SELECT_MODE) {
				command = "to_warehouselist";
				new TaskExecution().execute();
			} else {
				command = "to_routelist";
				new TaskExecution().execute();
			}
		} else
		if (tag.equals("control_sale")) {
			if (userType == SUPERVISOR) {
				way = 0;
				command = "to_userlist";
				new TaskExecution().execute();
			} else {
				way = 0;
				command = "to_routelist";
				new TaskExecution().execute();
			}
		} else
		if (tag.equals("control_today")) {
			command = "to_today";
			new TaskExecution().execute();	
		} else			
		if (tag.equals("control_refresh")) {
			refreshRequest();								
		} else
		if (tag.equals("control_back")) {
			loginActivity();	
		} else
		if (tag.equals("control_settings")) {
			settingsActivity();	
		}
		
		//sales
		if (tag.equals("product_filter")) {
			v.showContextMenu();
		} else
		if (tag.equals("sale_send")) {
			saleConfirmation();
		} else
		if (tag.equals("order_send")) {
			orderConfirmation();
		} else
		if (tag.equals("lease_payment")) {
			command = "lease_payment";
			new TaskExecution().execute();
		}
		
		//settings
		if (tag.equals("customer_send")) {
			checkSendCustomer();
		}
	}
	
	public void refreshRequest() {
		String message = "";
		String time = getData("last_download", "");
		if (time.length() > 0)
			message = time+" - д хамгийн сүүлд мэдээлэл шинэчлэгдсэн байна. Шинээр таталтыг хийхүү ?";
		else
			message = "Шинээр таталтыг хийхүү ?";
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Анхааруулага")
        .setMessage(message)
        .setPositiveButton("Тийм", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	command = "refresh_perfect";
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
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {    	
		if (activeActivity.equals("customer")) {
	    	menu.setHeaderTitle("Цэс");
		   	menu.add(0, 1, 0, "Үргэлжлүүлэх");
		   	menu.add(0, 99, 0, "Сарын захиалга");
		   	menu.add(0, 3, 0, "Хаалттай, боломжгүй");		   	
		   	menu.add(0, 5, 0, "Байршил илгээх");
		   	menu.add(0, 4, 0, "Хаах");
		} else
		if (activeActivity.equals("sale") || activeActivity.equals("order")) {
			menu.setHeaderTitle("Барааны төрөл");		   
			Collection sub_brands = new Collection();
			if (way == 2)
				sub_brands = sql.selectWhereGrouped("Product", "sub_brand", "brand='"+activeBrand+"' and wareHouseID="+Shared.wareHouseId, "sub_brand");							
			else 
				sub_brands = sql.selectWhereGrouped("Product", "sub_brand", "brand='"+activeBrand+"'", "sub_brand");
						
			menu.add(0, -1, 0, "Бүх бараа");			
			Log.d("d", brands.size()+" brands"+" "+activeBrand);
			for (int i = 0; i < sub_brands.size(); i++)
				menu.add(0, i, 0, sub_brands.elementAt(i).getString("sub_brand"));			
		} else
		if (activeActivity.equals("warehouselist")) {
			menu.setHeaderTitle("Цэс");
		   	menu.add(0, 1, 0, "Харилцагчид");		   			   	
		   	menu.add(0, 2, 0, "Өөртөө");		   	
		} else
		if (activeActivity.equals("today")) {			
			menu.setHeaderTitle("Жолооч нар");
			
		    for (int i = 0; i < cars.size(); i++) {	    	
		    	Variant w = cars.elementAt(i);	    	
		    	menu.add(0, i, 0, w.getString("firstName")+"|"+w.getFloat("loaded")+" пакет");
		    	menu.setGroupCheckable(0, true, true);
		    	if (w.getString("code").equals(partner))
		    		menu.getItem(i).setChecked(true);
		    }
		}
    }
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
		if (activeActivity.equals("warehouselist")) {
			 currentTicketId = getTicketId();
			 way = 1;
			 switch (item.getItemId()) {
	            case 1: {	   
	            	me = false;
	            	if (type == PRE_SALLING) {	    				
	    				command = "to_routelist";
	    				new TaskExecution().execute();				
	    			} else {			
	    				command = "to_order";
	    				new TaskExecution().execute();
	    			}
	            } break;
	            case 2: {
	            	me = true;
	            	selectCustomer(userCode);
	            	command = "to_order";
    				new TaskExecution().execute();
	            } break;	
			 }
		} else
		if (activeActivity.equals("customer")) {
			currentTicketId = getTicketId();
			selectedCustomer = customerAdapter.getItem(position);			
	        switch (item.getItemId()) {
	            case 1: {	            	
	            	if (userType == SUPERVISOR) {
	            		cameraActivity();
	            	} else {	            			            		
		            	if (way == 1) {	            		
		            		command = "customer_to_order";
		            		new TaskExecution().execute();
		            	} else {
		            		command = "customer_to_sale";
		            		new TaskExecution().execute();
		            	}
	            	}
	        		break;            	
	            }
	            case 2: {
	            	command = "customer_info";
	            	new TaskExecution().execute();
	            	break;
	            }
	            case 3: {
	            	closedTransaction();
	            	updateItemAtPosition(position);
	                break;
	            }
	            case 4: {
	                
	                break;
	            }
	            case 99: {
	            	seeMonthSale();
	            } break;
	            case 5: {
	            	sendConfirmation();
	            	
	            	//mapActivity();
	    			break;
	            }            
	            case 7: {
	            	
	            	break;
	            }
	            case 9: {	            	
	            	break;
	            }
	        }
		} else 
		if (activeActivity.equals("sale")) {			
			if (item.getItemId() == -1) {				
				downloadProductData(null, false);
				buttons.get("product_filter").setText("Бүх бараа");
			} else {				
				Collection sub_brands = sql.selectWhereGrouped("Product", "sub_brand", "brand='"+activeBrand+"'", "sub_brand");
				String text = sub_brands.elementAt(item.getItemId()).getString("sub_brand");
				buttons.get("product_filter").setText(text);
				downloadProductData(text, false);
			}
			listView = (ListView)findViewById(R.id.list);
			listView.setOnItemClickListener(this);		
			listView.setCacheColorHint(getResources().getColor(R.color.black));
			saleAdapter = new SaleProductAdapter(this, R.layout.product_item);
			listView.setAdapter(saleAdapter);
		} else 
		if (activeActivity.equals("order")) {
			if (item.getItemId() == -1) {				
				downloadProductData(null, false);
				buttons.get("product_filter").setText("Бүх бараа");
			} else {				
				Collection sub_brands = sql.selectWhereGrouped("Product", "sub_brand", "brand='"+activeBrand+"'", "sub_brand");
				String text = sub_brands.elementAt(item.getItemId()).getString("sub_brand");
				buttons.get("product_filter").setText(text);
				downloadProductData(text, false);
			}
			listView = (ListView)findViewById(R.id.list);
			listView.setOnItemClickListener(this);
			listView.setCacheColorHint(getResources().getColor(R.color.black));
			orderAdapter = new OrderProductAdapter(this, R.layout.product_item);
			listView.setAdapter(orderAdapter);
		} else
		if (activeActivity.equals("today")) {
			final int t = item.getItemId();
		    String title = item.getTitle().toString();
			new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Анхааруулага")
	        .setMessage("Сонгосон захиалагуудыг ачилтанд шилжүүлэхүү ? Жолоочын нэр :" +title)
	        .setPositiveButton("Тийм", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	String driver = cars.elementAt(t).getString("code");
	            	showAchiltDialog(driver);         	
	            }    	
	        })
	        .setNegativeButton("Үгүй", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                    
	            }
	        })
	        .show();  						
		}
		
        return super.onContextItemSelected(item);
    }
	
	private DatePickerDialog.OnDateSetListener mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
	   	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	   		String date_selected = String.valueOf(year)+"-"+String.valueOf(monthOfYear+1)+"-"+String.valueOf(dayOfMonth);
	   		buttons.get("out_date").setText(date_selected);
	   		dateStr = date_selected;   			   		
	   	}
    };
    
	public void showAchiltDialog(final String code) {
    	final Dialog dialog = new Dialog(this);    			
		dialog.setContentView(R.layout.number_achilt);		
		dialog.setTitle("Ачилтын дугаар");
	    dialog.setCancelable(true);
	    Button ok = (Button)dialog.findViewById(R.id.accept);
        final NumberPicker achilt_value = (NumberPicker)dialog.findViewById(R.id.achilt_id);
        achilt_value.setMax(4);
        achilt_value.setMin(1);
        achilt_value.setValue(1);
        final CheckBox daivar = (CheckBox)dialog.findViewById(R.id.daivar);
	    
        final Button button = (Button)dialog.findViewById(R.id.hurgelt_ognoo);            
        if (me) button.setVisibility(View.GONE);
        button.setTag("out_date");
        button.setOnClickListener(this);        
        buttons.put("out_date", button);
        
        ok.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	if (daivar.isChecked())
	            		achilt_id = 5;
	            	else
	            		achilt_id = (int)achilt_value.getValue();
	            			            		            	      
                	driver = code;
                	command = "achilt_order";
                	new TaskExecution().execute();
                	dialog.dismiss();	                     	
	            }
        	}
        );
        Button close = (Button)dialog.findViewById(R.id.close);
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {            	
            	dialog.dismiss();
            }
        });
        
        dialog.show();
    }
	
	@Override
	public void onItemClick(AdapterView<?> a, View v, int pos, long id) {								
		lastPos = pos;
		if (activeActivity.equals("detail_customer")) {
			if (command.equals("to_customer_orderdetail")) {											
				//showBackOrderInsertBox(v, collection.elementAt(pos));
			} 
		} else
		if (activeActivity.equals("today")) {
			activeItem = collection.elementAt(pos).getString("code");
			customTitle = collection.elementAt(pos).getString("data")+" ["+formatCurrency(collection.elementAt(pos).getFloat("amount"))+" ₮]";			
			String text = buttons.get("switch_data").getText().toString();
			//if (text.equals("Борлуулалт")) {
				command = "to_customer_orderdetail";
				detail_back = "order_info";
			//}
			//else {
			///	activeCustomer = activeItem;//hariltsagch songoh
		//		command = "to_customer_saledetail";
		//		detail_back = "sale_info";
		//	}
			new TaskExecution().execute();	
		} else
		if (activeActivity.equals("routelist")) {
			activeItem = collection.elementAt(pos).getString("routeID");
			Shared.routeID = activeItem;
			command = "to_customer";
			new TaskExecution().execute();	
		} else
		if (activeActivity.equals("lease")) {
			activeItem = collection.elementAt(pos).getInt("dist")+"";
			Collection col1 = sql.selectAll("Route_Customer", "s,s,s,i,i,f,f,f,f", "code,customerName,routeID,priceTag,parentID,discount,lat,lng,loanMargin", "parentID='"+activeItem+"'", "customerName");			
			if (col1.size() > 0)
				selectedCustomer.put("code", col1.elementAt(0).getString("code"));
			
			selectedCustomer.put("parentID", "i"+collection.elementAt(pos).getInt("dist"));
			command = "lease_payment";
			new TaskExecution().execute();
		} else
		if (activeActivity.equals("warehouselist")) {
			activeItem = ""+collection.elementAt(pos).getInt("wareHouseID");
			Shared.wareHouseId = collection.elementAt(pos).getInt("wareHouseID");
			Shared.wareHouseName = collection.elementAt(pos).getString("name");
			v.showContextMenu();
		} else		
		if (activeActivity.equals("userlist")) {
			activeItem = collection.elementAt(pos).getString("code");
			command = "to_customer";
			Shared.userID = activeItem;
			new TaskExecution().execute();
		} else
		if (activeActivity.equals("customer")) {
			position = pos;
			activeItem = collection.elementAt(pos).getString("code");
			v.showContextMenu();
		} else
		if (activeActivity.equals("settings")) {
			if (userType == SUPERVISOR)
				superSettings(pos);
			else
				normalSettings(pos);
		} else
		if (activeActivity.equals("sale")) {
			showSaleInsertBox(v);
		} else
		if (activeActivity.equals("order")) {	
			showOrderInsertBox(v);
		}
	}		
	
	public void superSettings(int pos) {
		switch (pos) {
			case 0: {
				ArrayList<HashMap<String,Object>> items =new ArrayList<HashMap<String,Object>>();					
				PackageManager pm = getPackageManager();
				List<PackageInfo> packs = pm.getInstalledPackages(0);  
				for (PackageInfo pi : packs) {
				if( pi.packageName.toString().toLowerCase().contains("calcul")){
				    HashMap<String, Object> map = new HashMap<String, Object>();
				    map.put("appName", pi.applicationInfo.loadLabel(pm));
				    map.put("packageName", pi.packageName);
				    items.add(map);
				 }
				}
				
				if(items.size()>=1){
					String packageName = (String) items.get(0).get("packageName");
					Intent i = pm.getLaunchIntentForPackage(packageName);
					if (i != null)
					  startActivity(i);
				} else
					showAlertMessage("Таны утсанд тооны машин байхгүй байна !");
			} break;	
			case 1: {
				command = "last_orders";
				new TaskExecution().execute();
			} break;
			case 2: {
				command = "current_orders";
				new TaskExecution().execute();
			} break;
			case 3: {
				registerCustomerActivity();
			} break;					
			case 4: {
				clearAll();
			} break;
			case 5: {
				//showAlertMessage("идэвхгүй байна");
				sendImages();
			} break;				
			case 6: {//nuuts ug oorchloh
				changeCode();					
			} break;
		}
	}
	
	public void normalSettings(int pos) {
		switch (pos) {
			case 0: {
				ArrayList<HashMap<String,Object>> items =new ArrayList<HashMap<String,Object>>();					
				PackageManager pm = getPackageManager();
				List<PackageInfo> packs = pm.getInstalledPackages(0);  
				for (PackageInfo pi : packs) {
				if( pi.packageName.toString().toLowerCase().contains("calcul")){
				    HashMap<String, Object> map = new HashMap<String, Object>();
				    map.put("appName", pi.applicationInfo.loadLabel(pm));
				    map.put("packageName", pi.packageName);
				    items.add(map);
				 }
				}
				
				if(items.size()>=1){
					String packageName = (String) items.get(0).get("packageName");
					Intent i = pm.getLaunchIntentForPackage(packageName);
					if (i != null)
					  startActivity(i);
				} else
					showAlertMessage("Таны утсанд тооны машин байхгүй байна !");
			} break;
			case 1: {
				printLastSaleTransaction();					
			} break;
			case 2: {
				command = "call_mystorage";
				new TaskExecution().execute();
			} break;
			case 3: {
				seePlanAndExecution();
				//showAlertMessage("идэвхгүй байна");				
			} break;
			case 4: {
				command = "call_lease";
				new TaskExecution().execute();
			} break;
			case 5: {
				registerCustomerActivity();
			} break;
			case 6: {//amjiltuig guilgee
				checkNonCompleteTransactions();
			} break;		
			case 7: {
				clearAll();
			} break;
			case 8: {
				//showAlertMessage("идэвхгүй байна");
				sendImages();
			} break;
			case 9: {//packet oorchloh
				changePackedMode();
			} break;	
			case 10: {//nuuts ug oorchloh
				changeCode();					
			} break;
		}
	}
	
	public void showSaleInsertBox(View v) {		
		String code = v.getTag().toString();
		final Variant sel = products.query("code", code);		
		final long max = (long)(product_confirm_info.query("productCode", code).getInt("lastCount")/getUnit(sel));
		final String title = sel.get("descr")+" ("+(max)+" ширхэг)";
		final Dialog dialog = new Dialog(this);    			
		dialog.setContentView(R.layout.number_sale);		
		dialog.setTitle(title);
		
		final NumberPicker sale_value = (NumberPicker)dialog.findViewById(R.id.sale_count);
        final NumberPicker rent_value = (NumberPicker)dialog.findViewById(R.id.rent_count);
        sale_value.setMax(max);
        rent_value.setMax(max);
        sale_value.setMin(0);
        rent_value.setMin(0);
        Variant w = product_sale_info.query("productCode", "s"+code);
        sale_value.setValue((int)(w.getInt("salequantity")/getUnit(sel)));
        rent_value.setValue((int)(w.getInt("rentquantity")/getUnit(sel)));
        
        Button accept = (Button)dialog.findViewById(R.id.sale_insertbox_accept);
        accept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				if (sale_value.getValue() + rent_value.getValue() > max) {
					showInfoMessage("Уг барааны хувьд үлдэгдэл хүрэлцэхгүй байна !");
					return;
				}
					
				addProduct(sel, sale_value.getValue(), rent_value.getValue()); 								
				
				dialog.dismiss();				
				buttons.get("sale_send").setEnabled(true);
				updateItemAtPosition(lastPos);
			}        	
        });
        
        Button close = (Button)dialog.findViewById(R.id.sale_insertbox_close);
        close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        });
        
        dialog.show();
	}				
	
	public boolean newOrderPossible() {
    	if (currentOrderedCount > 0) {
			showAlertMessage("Таны өмнөх захиалагууд зөвшөөрөгдөөгүй байна ! Буруу бол нягтланд хэлж устгуулна уу !");	
			return false;
		}
    	
    	return true;
    }
	
	public void showOrderInsertBox(View v) {

		if (!newOrderPossible()) return;
		
		final Variant sel = products.queryString("code", v.getTag().toString());
		if (sel.getString("sub_brand").indexOf("bagts") != -1) {
			showPacketOrderInsertBox(v);
			return;
		}
		
		/*Variant confirm = product_confirm_info.queryString("productCode", sel.getString("code"));			
		long availCount = (long)(confirm.getInt("lastCount") / getUnit(sel));
		*/
		Variant order = product_order_info.queryString("productCode", sel.getString("code"));		
		
		final long storageCount = (long)(storage.query("productCode", sel.getString("code")).getInt("availCount")/getUnit(sel));
		Log.d("StorageCount", sel.getString("code")+""+getUnit(sel));
		if (Shared.feature_list[Shared.STORAGE_CHECK_FEATURE_ID] && storageCount <= 0) {
			showAlertMessage("Агуулахын боломжит үлдэгдэл хүрэлцэхгүй !");
			return;
		} else {			
			
		}
		
		
		final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.number_order);
        dialog.setTitle(products.queryString("code", sel.getString("code")).get("descr")+" [АҮ:"+storageCount+"]");//[АҮ:"+storageCount+"]");         
        dialog.setCancelable(true);        
        final OrderNumberPicker order_value = (OrderNumberPicker)dialog.findViewById(R.id.order_count);        
        order_value.setValue((int)(order.getInt("requestCount")/getUnit(sel)));
        order_value.setMin(0);
        order_value.setMax(Shared.feature_list[Shared.STORAGE_CHECK_FEATURE_ID] ? (int)storageCount:100000);
        
        Button ok = (Button)dialog.findViewById(R.id.order_insertbox_accept);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {          
            	Log.d("DDDDDDD", "nowsh gej end bnau "+order_value.getValue()+"   "+storageCount);
            	if (Shared.feature_list[Shared.STORAGE_CHECK_FEATURE_ID] && order_value.getValue() > storageCount) {
            		showAlertMessage("Агуулахын боломжит үлдэгдэл хүрэлцэхгүй !");
            		return;
            	}
            	
            	addOrderProduct(sel, (long)order_value.getValue());            	
            	dialog.dismiss();
            	
            	buttons.get("order_send").setEnabled(true);            	            					
            	updateItemAtPosition(lastPos);
            }
        });
        
        Button close = (Button)dialog.findViewById(R.id.order_insertbox_close);
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	dialog.dismiss();
            }
        });
        
        dialog.show();
	}
	
	public void showPacketOrderInsertBox(View v) {
		if (!newOrderPossible()) return;
		final String code = v.getTag().toString();
		final Variant sel = products.queryString("code", code);		
		
		final long storageCount = (long)(storage.query("productCode", sel.getString("code")).getInt("availCount")/getUnit(sel));
		if (Shared.feature_list[Shared.STORAGE_CHECK_FEATURE_ID] && storageCount <= 0) {
			showAlertMessage("Агуулахын боломжит үлдэгдэл хүрэлцэхгүй !");
			return;
		} else {			
			
		}
				
		int column_fixed = 11;
		final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.number_packet_sale);
        LinkedList<Variant> list = packets_products.queryList("code", code);
        String title = "";
        float total = 0;
        Log.d("d", code+" "+list.size());
        for (int i = 0; i < list.size(); i++) {
        	Variant w = list.get(i);        	
        	Collection pro = sql.selectAll("Product", "s,s,s,s,s,s,f,i", "code,name,descr,brand,sub_brand,vendor,unit,wareHouseID", "code='"+w.getString("productCode")+"'", "name");
        	if (pro.size() > 0) {
        		float price = prices.query("productCode", pro.elementAt(0).getString("code"), "customerType", 1).getFloat("price");        	
        		title += getFixedString((i+1)+".",3)+getFixedString(pro.elementAt(0).getString("descr"),10+column_fixed)+getFixedInt(w.getInt("quantity"),3)+"*"+getFixedInt((int)price,4)+"\n";
        		total += w.getInt("quantity")*price;
        	}
        }
        
        dialog.setTitle(packets.query("code", code).get("name")+" ("+formatCurrency(total)+" ₮)"); 
        dialog.setCancelable(true);
        
        final TextView detail = (TextView)dialog.findViewById(R.id.detail);
        detail.setText(title);
        final NumberPicker order_value = (NumberPicker)dialog.findViewById(R.id.sale_count);	              
        order_value.setMin(0);	        
        
        Button ok = (Button)dialog.findViewById(R.id.accept);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {               	        
            	Log.d("HURELTSEHGUI", storageCount+"  "+order_value.getValue());
            	if (storageCount < order_value.getValue()) {
            		showInfoMessage("Хүрэлцэхгүй !");
            	} else
            	if (order_value.getValue() >= 0) {	            			            			                        		          		            		
            		dialog.hide();
            		
            		addPacketOrderProduct(sel, (long)order_value.getValue());	 
            		buttons.get("order_send").setEnabled(true);            	            					
                	updateItemAtPosition(lastPos);
            	}            	            	
            }
        });
        
        Button close = (Button)dialog.findViewById(R.id.close);
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	dialog.dismiss();
            }
        });
        
        dialog.show();
	}
	
	public void showBackOrderInsertBox(View v, Variant w) {
		if (!newOrderPossible()) return;
		
		final Variant sel = products.query("code", w.getString("code"));		
		
		final long availCount = (long)(w.getInt("quantity") / getUnit(sel));		
		if (availCount <= 0) {
			showAlertMessage("Буцаах боломжгүй !");
			return;
		}
				
		final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.number_order);
        dialog.setTitle(sel.get("name")+" ["+availCount+"]");         
        dialog.setCancelable(true);        
        final OrderNumberPicker order_value = (OrderNumberPicker)dialog.findViewById(R.id.order_count);        
        order_value.setValue((int)availCount);
        order_value.setMin(0);
        order_value.setMax((int)availCount);
        
        Button ok = (Button)dialog.findViewById(R.id.order_insertbox_accept);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {          
            	if (order_value.getValue() > availCount) {
            		showAlertMessage("Буцаах утгааас хэтэрсэн байна !");
            		return;
            	}
            	
            	selectCustomer(activeCustomer);
            	addOrderProduct(sel, -(long)order_value.getValue());
            	selectCustomer(userCode);
            	addOrderProduct(sel, +(long)order_value.getValue());
            	
            	dialog.dismiss();
            }
        });
        
        Button close = (Button)dialog.findViewById(R.id.order_insertbox_close);
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	dialog.dismiss();
            }
        });
        
        dialog.show();
	}
	
	public long getTotalQuantityByBrand(String brand) {
		long total = 0;
		for (int i = 0; i < product_sale_info.size(); i++) {
			Variant w = (Variant)collection.elementAt(i);
			String productCode = w.getString("productCode");													
			String br = products.query("code", productCode).getString("brand");
			if (br.startsWith(brand)) {
				long c = (long)(w.getInt("salequantity")/(int)products.query("code", productCode).getFloat("unit"));
				total += c;
			}
			
		}
		
		return total;
	}
	
	public long getTotalQuantityByProduct(String code) {
		long total = 0;
		for (int i = 0; i < product_sale_info.size(); i++) {
			Variant w = (Variant)product_sale_info.elementAt(i);
			String productCode = w.getString("productCode");													
			if (productCode.equals(code)) {			
				long c = (long)(w.getInt("salequantity")/(int)products.query("code", productCode).getFloat("unit"));
				total += c;
			}
			
		}
		
		return total;
	}
	
	public void promotionIntelliegence() {
		Collection promotions = sql.selectAll("Promotion", "s,s,s,i,f,f,s,i,f", "customerCode,productCode,brand,quantity,amount,price,freeProductCode,freeQuantity,precent", "type=2", "quantity DESC");
		
		if (type == VAN_SALLING) { //VAN_SALLING bogood brandeer
			HashMap pcount = new HashMap();
			
			for (int i = 0; i < promotions.size(); i++) {
				Variant w = (Variant)promotions.elementAt(i);
				long k = 0;
				if (pcount.containsKey(w.getString("productCode"))) k = (Long)pcount.get(w.getString("productCode"));
				long dm = getTotalQuantityByProduct(w.getString("productCode")) - k;
				if (k == 0) 
					pcount.put(w.getString("productCode"), new Long(dm));
					
				Log.d("d", dm+" "+w.getInt("quantity"));
				if (dm >= w.getInt("quantity")) {
					long q = w.getInt("quantity");
					long p = (getTotalQuantityByProduct(w.getString("productCode")) / q)*w.getInt("freeQuantity");					
					addProductFree(w.getString("freeProductCode"), p, 0);
					
					dm -= p;
					pcount.put(w.getString("productCode"), new Long(dm));
				}
			}						
		}
		
		promotions = sql.selectAll("Promotion", "s,s,s,i,f,f,s,i,f", "customerCode,productCode,brand,quantity,amount,price,freeProductCode,freeQuantity,precent", "type=5", "quantity DESC");
				
		if (type == VAN_SALLING) { //VAN_SALLING bogood brandeer
			HashMap pcount = new HashMap();
			for (int i = 0; i < promotions.size(); i++) {
				Variant w = (Variant)promotions.elementAt(i);
				long k = 0;
				if (pcount.containsKey(w.getString("brand"))) k = (Long)pcount.get(w.getString("brand"));
				long dm = getTotalQuantityByBrand(w.getString("brand")) - k;
				if (k == 0) 
					pcount.put(w.getString("brand"), new Long(dm));
				
				if (dm >= w.getInt("quantity")) {
					long q = w.getInt("quantity");
					long p = (getTotalQuantityByBrand(w.getString("brand")) / q)*w.getInt("freeQuantity");					
					addProductFree(w.getString("freeProductCode"), p, 0);
					dm -= p;
					pcount.put(w.getString("brand"), new Long(dm));
				}
			}
		}		
	}
	
	public void saleConfirmation() {
    	if (!getLoanMargin()) {
    		showAlertMessage("Зээлийн лимит хэтэрсэн !");
    		return;
    	}
    	
    	if (type == VAN_SALLING && Shared.feature_list[Shared.PROMOTION_FEATURE_ID])
    		promotionIntelliegence();
    	
    	final Dialog dialog = new Dialog(this);    	
    	int column_fixed = 12;
    	dialog.setContentView(R.layout.sale_confirm);			                                         
        
    	String rows = "";        
        int ftotal = 0, row_count = 1, count = 0;
        long rtotal = 0, total = 0;
                
        for (int i = 0; i < product_sale_info.size(); i++) {
        	Variant vt = (Variant)product_sale_info.elementAt(i);        	        
        	if (vt.getInt("rentquantity") > 0) {
        		rows += getFixedInt(row_count,2)+" "+getFixedString(vt.getString("descr", 15),5+column_fixed)+getFixedString("#",1)+getFixedInt(vt.getInt("rentquantity"),5)+"*"+getFixedPrice(vt.getFloat("rentprice"),7)+"\n";
        		rtotal += vt.getInt("rentquantity")*vt.getFloat("rentprice");
        		count += vt.getInt("rentquantity");        		
        		row_count++;
        		if (vt.getInt("salequantity") > 0) {
        			rows += getFixedInt(row_count,2)+" "+getFixedString(vt.getString("descr", 15),5+column_fixed)+getFixedString(" ",1)+getFixedInt(vt.getInt("salequantity"),5)+"*"+getFixedPrice(vt.getFloat("saleprice"),7)+"\n";
        			total += vt.getInt("salequantity")*vt.getFloat("saleprice");
        			count += vt.getInt("salequantity");        			
        			row_count++;        			
        		}
        	} else {
        		rows += getFixedInt(row_count,2)+" "+getFixedString(vt.getString("descr", 15),5+column_fixed)+getFixedString(" ",1)+getFixedInt(vt.getInt("salequantity"),5)+"*"+getFixedPrice(vt.getFloat("saleprice"),7)+"\n";
        		total += vt.getInt("salequantity")*vt.getFloat("saleprice");
        		count += vt.getInt("salequantity");        		
        		row_count++;        		
        	}        	        	          	
        }        
        
        rows+=" "+getSlash(column_fixed-3)+"\n";        
        rows+="  Зээлээр"+getFixedFloat(rtotal,13+column_fixed)+"\n";
        rows+="  Бэлнээр"+getFixedFloat(total,13+column_fixed)+"\n";
        if (ftotal > 0)
        	rows+="  Урамшуулал"+getFixedFloat(ftotal,10+column_fixed)+"\n";
        rows+="  Нийт"+getFixedFloat((total+rtotal),16+column_fixed)+"\n";        
                        
        customerDiscount = selectedCustomer.getFloat("discount");
        
        cashDiscount = 0;
        if (rtotal == 0) cashDiscount = Shared.CASH_DISCOUNT_CONSTANT;
        float totalDiscount = invoiceDiscount+customerDiscount+cashDiscount;
        
        if (totalDiscount > 0) { // бэлэн бол хөнгөлөлт
        	if (invoiceDiscount > 0)
        		rows+="  Урамшуулал"+getFixedFloat(totalDiscount,11+column_fixed)+"\n";
        	
        	rows+="  Төлөх"+getFixedFloat(total-totalDiscount,16+column_fixed)+"\n";
        } else 
        	customerDiscount = 0;
        
        
        final EditText payment = (EditText)dialog.findViewById(R.id.payment);
        
        /*if (type == VAN_SALLING)*/        
        	payment.setVisibility(View.GONE);
                	
        tuluhTotal = total;
        
        dialog.setTitle("Гүйлгээний жагсаалт");         
        dialog.setCancelable(true);
        
    	tuljBaigaa = tuluhTotal-totalDiscount;
        final TextView text1 = (TextView)dialog.findViewById(R.id.hariult);        
        
        final EditText text = (EditText)dialog.findViewById(R.id.payvalue);
        text.setText(Integer.toString((int)tuljBaigaa));
        text.selectAll();
        text.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                String value = text.getText().toString();
                if (isNumeric(value)) {
                	long v = Long.parseLong(value);
                	text1.setText("Хариулт : "+Integer.toString((int)(v-tuljBaigaa)));
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        
        final TextView detail = (TextView)dialog.findViewById(R.id.detail);
        detail.setText(rows);    	
        Button ok = (Button)dialog.findViewById(R.id.accept);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {            	
            	dialog.hide();            	
            	startProcessing();
            }
        });
        
        Button close = (Button)dialog.findViewById(R.id.close);
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {            	
            	dialog.hide();
            }
         });
        
        dialog.show();
    }
	
	public void leaseProcessing(long pay_value) {		
		Variant vt = new Variant();
    	vt.put("customerCode", "s"+selectedCustomer.get("code"));
    	vt.put("userCode", "s"+userCode);
    	vt.put("type", "i"+3); // zeel tulult
		vt.put("posX", "f"+Shared.gps_location[0]);
		vt.put("posY", "f"+Shared.gps_location[1]);
		vt.put("productCode", "s");
		vt.put("quantity", "i0");
		vt.put("price", "f0");
		vt.put("discount", "i"+selectedCustomer.getInt("parentID"));
		vt.put("_dateStamp", "dCURRENT_TIMESTAMP");
		vt.put("amount", "f"+pay_value);
		vt.put("userType", "i"+userType);
		vt.put("ticketID", "i"+currentTicketId);
		int success = -1;
		if ((success = toJSON("rentpayment", "Sales", "_dateStamp,customerCode,userCode,productCode,posX,posY,type,quantity,price,amount,discount,userType,ticketID", vt)) == -1)
			sql.insertTransaction(vt);//amjiltuig guilgee
		
		Toast.makeText(this, "Амжилттай !", Toast.LENGTH_SHORT);
	}
	
	public void leaseConfirmation() {
		long lease_value = 0;
		for (int i = 0; i < product_lease_info.size(); i++) {
			Variant w = (Variant)product_lease_info.elementAt(i);
			lease_value += w.getFloat("flag");
		}
		String rows = lease_value+" ₮";
    	final Dialog dialog = new Dialog(this);    	    	
    	dialog.setContentView(R.layout.lease_confirm);			                                                     	                                              
        dialog.setTitle("Одоогийн зээлийн үлдэгдэл");         
        dialog.setCancelable(true);
        
        final long tuluh = lease_value;
        final TextView text1 = (TextView)dialog.findViewById(R.id.hariult);
        final EditText text = (EditText)dialog.findViewById(R.id.payvalue);
        text.setText(Integer.toString((int)lease_value));
        text.selectAll();
        text.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                String value = text.getText().toString();
                if (isNumeric(value)) {
                	long v = Long.parseLong(value);
                	text1.setText("Хариулт : "+Integer.toString((int)(v-tuluh)));
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        
        final TextView detail = (TextView)dialog.findViewById(R.id.detail);
        detail.setText(rows);    	
        Button ok = (Button)dialog.findViewById(R.id.accept);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {            	            	         
            	String value = text.getText().toString();
            	if (isNumeric(value)) {
                 	long vv = Long.parseLong(value);
                 	leaseProcessing(vv);
            	} else
            		showAlertMessage("Тоо оруулна уу !");
            	dialog.hide();
            }
        });
        
        Button close = (Button)dialog.findViewById(R.id.close);
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {            	
            	dialog.hide();
            }
         });
        
        dialog.show();
    }

	public String getOrderRowTmpl(int row_count, int column_fixed, Variant vt) {
    	return getFixedInt(row_count,2)+" "+getFixedString(vt.getString("descr", 16),8+column_fixed)+getFixedString(getFixedInt(vt.getInt("requestCount"),3)+"*"+formatCurrency(vt.getFloat("price")), 12)+"\n";
    }       
	
	public void orderConfirmation() {
    	if (type == PRE_SALLING && Shared.feature_list[Shared.PROMOTION_FEATURE_ID])
    		promotionIntelliegence();
    	
    	final Dialog dialog = new Dialog(this);
    	int column_fixed = 11;    	
    	dialog.setContentView(R.layout.order_confirm);
                        
        String rows = ""; 
        long sum = 0;
        float sum_packet = 0;
        int row_count = 1;
        for (int i = 0; i < product_order_info.size(); i++) {
        	Variant vt = (Variant)product_order_info.elementAt(i);
        	if (vt.getString("packet").equals("yes")) {//bagts baival
        		long subtotal = 0;
            	LinkedList<Variant> list = packets_products.queryList("code", vt.getString("productCode"));
            	for (int j = 0; j < list.size(); j++) {
            		Variant w = list.get(j);
            		long rt = w.getInt("quantity");
            		//bagts zadlah
            		Collection pro = sql.selectAll("Product", "s,s,s,s,s,s,f,i", "code,name,descr,brand,sub_brand,vendor,unit,wareHouseID", "code='"+w.getString("productCode")+"'", "name");
                	if (pro.size() > 0) {
                		float price = prices.query("productCode", pro.elementAt(0).getString("code"), "customerType", 1).getFloat("price");        	                	                			        	
                		long ct = vt.getInt("requestCount")*rt;            		          	
                		rows += getFixedInt(row_count,2)+"#"+getFixedString(pro.elementAt(0).getString("descr", 16),8+column_fixed)+getFixedString(getFixedInt(ct,3)+"*"+formatCurrency(price), 12)+"\n";        			
                		subtotal += vt.getInt("requestCount")*rt*price;                     		                		
                		row_count++;
                	}
            	}        	
            	            
            	sum+=subtotal;
        	} else {
        		rows += getOrderRowTmpl(row_count, column_fixed, vt);//getFixedInt(row_count,2)+" "+getFixedString(products.query("code", vt.getString("productCode")).getString("descr", 15),7+column_fixed)+getFixedInt(vt.getInt("requestCount"),6)+"*"+vt.getFloat("price")+"\n";
        		sum += (vt.getInt("requestCount") * vt.getFloat("price"));        		        		        		        		       
        		row_count++;
        	}
        }        
        
        sum_packet = 0;
		for (int i = 0; i < product_order_info.size(); i++) {
    		Variant v = (Variant)product_order_info.elementAt(i);    		 	    	    		   			  
        	sum_packet += v.getFloat("packetCount");    		
    	}  
        
        rows+=" "+getSlash(column_fixed-2)+"\n";
        rows+="  Пакет"+getFixedFloat((sum_packet),14+column_fixed)+"\n";
        rows+="  Нийт"+getFixedFloat((sum),17+column_fixed)+"\n"; 
        
        String title = "Захиалагын жагсаалт";        
        dialog.setTitle(title); 
        dialog.setCancelable(true);                       
        
        final TextView detail = (TextView)dialog.findViewById(R.id.detail);
        detail.setText(rows);    	
        Button ok = (Button)dialog.findViewById(R.id.order_accept);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {               	
            	dialog.dismiss();
            	startProcessing();         	           	            
            }
        });
        
        Button close = (Button)dialog.findViewById(R.id.order_close);
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {            	
            	dialog.dismiss();
            }
         });
        
        dialog.show();
    }
	
	public void startProcessing() {
		
	}
		
	public void updateItemAtPosition(int position) {
		 if (activeActivity.equals("customer")) {
			 int visiblePosition = listView.getFirstVisiblePosition();
			 View v = listView.getChildAt(position - visiblePosition);
			 TextView cs = (TextView) v.findViewById(R.id.success);			 
			 String status = "";	            
             if (getCustomerStatus("leased", ":"+selectedCustomer.getInt("parentID")+":")) status = " Зээлтэй ";            
             if (getCustomerStatus("no_entry", selectedCustomer.get("code")+",")) status = " Хийгээгүй |";
             if (getCustomerStatus("closed", selectedCustomer.get("code")+",")) status += " Хаалттай |";
             if (getCustomerStatus("worked", selectedCustomer.get("code")+",")) status += " Oрсон ";            
             cs.setText(status);
		 } else {
			 int visiblePosition = listView.getFirstVisiblePosition();
			 View v = listView.getChildAt(position - visiblePosition);			 
			 if (v == null) return;			 
			 Variant o = products.elementAt(position);
	         TextView vs = (TextView) v.findViewById(R.id.values);
	         
			 if (activeActivity.equals("order")) {				         
		         Variant w = product_confirm_info.query("productCode", o.getString("code"));	         
		         int total = 0;
		         if (w.getInt("flagStatus") > 0) {
		         	vs.setText((w.getInt("flagStatus")/getUnit(o))+"/"+(w.getInt("lastCount")/getUnit(o)));
		         	total = 1;
		         }
		         else {
		         	Variant q = product_order_info.query("productCode", "s"+o.getString("code"));
		         	vs.setText(getFixedFloat(q.getFloat("packetCount"), 1)+"/"+(w.getInt("lastCount")/getUnit(o)));
		         	total = (int)w.getInt("lastCount")+(int)q.getInt("requestCount");
		         }
		                               
		         if (total > 0)
		         	vs.setTextColor(getResources().getColor(R.color.white));
			 } else 
		     if (activeActivity.equals("sale")) {	    	 
				 Variant w = product_sale_info.query("productCode", "s"+o.getString("code"));
	             vs.setText((w.getInt("salequantity")/getUnit(o))+"/"+(w.getInt("rentquantity")/getUnit(o)));
	             if (w.getInt("salequantity") + w.getInt("rentquantity") > 0)
	            	vs.setTextColor(getResources().getColor(R.color.white));
			 }
		 }
	}
	
	public class SaleInfoAdapter extends ArrayAdapter<Variant> {

        public SaleInfoAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, collection.getCollection());
        }                

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = collection.getCollection().get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.check_list_item, null);
            
            v.setBackgroundColor(0x000000FF);                                                
            
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            TextView cs = (TextView) v.findViewById(R.id.success);
            String value = o.getString("data");                                        
            tt.setText((position+1)+". "+value);                
            if (command.equals("today_info"))
            	bt.setText(formatCurrency(o.getFloat("amount"))+" ₮");            	
            else
            if (command.equals("order_info"))
               	bt.setText(formatCurrency(o.getFloat("amount"))+" ₮");
            if (command.equals("sale_info"))
            	bt.setText(formatCurrency(o.getFloat("amount"))+" ₮");
            else            
            	bt.setText(o.getInt("quantity")+" пакет, "+formatCurrency(o.getFloat("amount"))+" ₮ | Түгээгч:"+o.getString("partner"));
            
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);
            cs.setTypeface(Shared.tf);
            
            CheckBox ch = (CheckBox) v.findViewById(R.id.checkbox);
            ch.setTag(position+"");
            ch.setChecked(checked[position]);
            if (!o.getString("partner").equals(partner) || o.getFloat("amount") > 0) {
            	ch.setVisibility(View.INVISIBLE);
            }
            ch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					int pos = Integer.parseInt(buttonView.getTag().toString());
					checked[pos] = isChecked;
					calculateV();
				}		
    		});
            
            return v;
        }
        
        public void calculateV() {
        	long total = 0;
        	for (int i = 0; i < collection.getCollection().size(); i++) {
        		if (checked[i]) {
        			Variant w = collection.elementAt(i); 
        			total += w.getInt("quantity");
        		}
        	}
        	
        	texts.get("today_sums").setText("Сонгосон тоо пакет : "+Long.toString(total));
        }
    }		
	
	private class AnyAdapter extends ArrayAdapter<Variant> {

        public AnyAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, collection.getCollection());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = collection.getCollection().get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
            
            v.setBackgroundColor(0x000000FF);                                                
            
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            TextView cs = (TextView) v.findViewById(R.id.success);
            String value = o.getString("name");                       
            
            tt.setText((position+1)+". "+value);                        
            
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);
            cs.setTypeface(Shared.tf);
            
            return v;
        }
    }
	
	private class LeaseAdapter extends ArrayAdapter<Variant> {

        public LeaseAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, collection.getCollection());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = collection.getCollection().get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
            
            v.setBackgroundColor(0x000000FF);                                                
            
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            TextView cs = (TextView) v.findViewById(R.id.success);            
            String value = o.getString("cusName");
            
            tt.setText((position+1)+". "+value);
            bt.setText("Үлдэгдэл: "+formatCurrency(o.getFloat("amount"))+" ₮");
            
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);
            cs.setTypeface(Shared.tf);
            
            return v;
        }
    }
	
	private class CurrentOrderAdapter extends ArrayAdapter<Variant> {    	    	
        public CurrentOrderAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, collection.getCollection());            
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = collection.getCollection().get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
            
            v.setBackgroundColor(0x000000FF);                                                
            
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            TextView cs = (TextView) v.findViewById(R.id.success);
            String value = o.get("firstName");
            bt.setText("Өдрийн борлуулалт: "+o.getFloat("odriinBorluulalt")+"\n"+"Daily Plan: "+o.getFloat("dailyPlan")+"\nOutlets buying: "+o.getFloat("outletsBuying")+"\nBuying %: "+o.getFloat("buyingPrecentfrom"));
            
            
            tt.setText((position+1)+". "+value);
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);            
            cs.setTypeface(Shared.tf);
            
            return v;
        }
    }
	
	private class LastOrderAdapter extends ArrayAdapter<Variant> {    	    	
        public LastOrderAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, collection.getCollection());            
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = collection.getCollection().get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
            
            v.setBackgroundColor(0x000000FF);                                                
            
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            TextView cs = (TextView) v.findViewById(R.id.success);
            String value = o.get("firstName");
            bt.setText(o.getString("lastSale")+" "+"\nСүүлд орсон: "+o.getString("lastSalesTimestamp")+"\n Хугацаа: "+o.getInt("ZahialgaHoorondiinMinute")+" минут");
            
            
            tt.setText((position+1)+". "+value);
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);            
            cs.setTypeface(Shared.tf);
            
            return v;
        }
    }
	
	private class StorageAdapter extends ArrayAdapter<Variant> {

        public StorageAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, collection.getCollection());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = collection.getCollection().get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
            
            v.setBackgroundColor(0x000000FF);                                                
            
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            TextView cs = (TextView) v.findViewById(R.id.success);            
            String value = o.getString("data");
            
            tt.setText((position+1)+". "+value);
            bt.setText("Үлдэгдэл: "+o.getFloat("quantity"));
            
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);
            cs.setTypeface(Shared.tf);
            
            return v;
        }
    }
	
	private class RouteAdapter extends ArrayAdapter<Variant> {

        public RouteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, collection.getCollection());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = collection.getCollection().get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
            
            v.setBackgroundColor(0x000000FF);                                                
            
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            TextView cs = (TextView) v.findViewById(R.id.success);
            String value = o.getString("routeName");                       
            
            tt.setText((position+1)+". "+value);           
            
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);
            cs.setTypeface(Shared.tf);
            
            return v;
        }
    }
	
	private class UserAdapter extends ArrayAdapter<Variant> {

        public UserAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, collection.getCollection());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = collection.getCollection().get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
            
            v.setBackgroundColor(0x000000FF);                                                
            
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            TextView cs = (TextView) v.findViewById(R.id.success);
            String value = o.getString("firstName");                       
            
            tt.setText((position+1)+". "+value);           
            
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);
            cs.setTypeface(Shared.tf);
            
            return v;
        }
    }
	
	private class SaleProductAdapter extends ArrayAdapter<Variant> implements Filterable {		
		
        public SaleProductAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, products.getCollection());
            
            this.planetList = products.getCollection();    		
    		this.origPlanetList = planetList;
        }        
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = planetList.get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.product_item, null);                                                                                   
            
            TextView tt = (TextView) v.findViewById(R.id.title);
            TextView bt = (TextView) v.findViewById(R.id.descr);
            TextView cs = (TextView) v.findViewById(R.id.price);
            TextView vs = (TextView) v.findViewById(R.id.values);
            ImageView img = (ImageView) v.findViewById(R.id.list_image);
            ImageView arr = (ImageView) v.findViewById(R.id.arrow);
            
            tt.setText(o.getString("name"));
            bt.setText(o.getString("code")+" "+o.getString("sub_brand"));          
    		
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);
            cs.setTypeface(Shared.tf);
            
            Variant p = prices.query("productCode", o.getString("code"), "customerType", selectedCustomer.getInt("priceTag"));
            float price = p.getFloat("price");                        
            cs.setText(price+" ₮");            
            
            Variant w = product_sale_info.query("productCode", "s"+o.getString("code"));
            vs.setText((w.getInt("salequantity")/getUnit(o))+"/"+(w.getInt("rentquantity")/getUnit(o))+packedChr());
            if (w.getInt("salequantity") + w.getInt("rentquantity") > 0)
            	vs.setTextColor(getResources().getColor(R.color.twit));
            
            img.setImageBitmap(getBitmapFromAsset(o.getString("code")+".gif"));
           // Variant q = product_confirm_info.query("productCode", o.getString("code"));
            Variant q = storage.query("productCode", o.getString("code"));
            if (q.getInt("availCount") > 0 || !Shared.feature_list[Shared.STORAGE_CHECK_FEATURE_ID])
            	arr.setVisibility(View.GONE);
            else
            	arr.setVisibility(View.VISIBLE);
            
            v.setTag(o.getString("code"));
            
            return v;
        }
        
        public List<Variant> planetList;    	
        public Filter planetFilter;
        public List<Variant> origPlanetList;                

    	@Override
    	public Filter getFilter() {
    		if (planetFilter == null)
    			planetFilter = new PlanetFilter();

    		return planetFilter;
    	}

    	public void resetData() {
    		planetList = origPlanetList;
    	}
    	
    	private class PlanetFilter extends Filter {
    		@Override
    		protected FilterResults performFiltering(CharSequence constraint) {
    			FilterResults results = new FilterResults();
    			if (constraint == null || constraint.length() == 0) {
    				results.values = origPlanetList;
    				results.count = origPlanetList.size();
    			}
    			else {
    				List<Variant> nPlanetList = new ArrayList<Variant>();
    				Log.d("d", constraint.toString());
    				for (Variant p : planetList) {
    					if (p.getString("name").indexOf(constraint.toString()) != -1 || p.getString("code").startsWith(constraint.toString()) || convertToLatin(p.getString("name")).trim().toLowerCase().startsWith(constraint.toString().trim().toLowerCase()))
    						nPlanetList.add(p);
    				}

    				results.values = nPlanetList;
    				results.count = nPlanetList.size();

    			}
    			return results;
    		}

    		@Override
    		protected void publishResults(CharSequence constraint, FilterResults results) {    		
    			if (results.count == 0)
    				notifyDataSetInvalidated();
    			else {
    				planetList = (List<Variant>) results.values;
    				notifyDataSetChanged();
    			}
    		}
    	}
    	
    	public int getCount() {
    		return planetList.size();
    	}

    	public Variant getItem(int position) {
    		return planetList.get(position);
    	}

    	public long getItemId(int position) {
    		return planetList.get(position).hashCode();
    	}
    }
	
	private class SimpleAdapter extends ArrayAdapter<Variant> {				
		
        public SimpleAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, brands.getCollection());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Variant o = brands.getCollection().get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);                                                                      
            
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.success);
            
            tt.setText(o.getString("brand"));    		
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);                                    
            
            v.setTag(o.getString("brand"));
            
            return v;
        }
    }
	
	private class OrderProductAdapter extends SaleProductAdapter {						
        public OrderProductAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = planetList.get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.order_product_item_layout, null);                                                                                   
            
            TextView tt = (TextView) v.findViewById(R.id.title);
            TextView bt = (TextView) v.findViewById(R.id.descr);
            TextView cs = (TextView) v.findViewById(R.id.price);
            TextView vs = (TextView) v.findViewById(R.id.values);
            ImageView img = (ImageView) v.findViewById(R.id.list_image);
            ImageView arr = (ImageView) v.findViewById(R.id.arrow);
            
            tt.setText(o.getString("name"));
            bt.setText(o.getString("code")+" "+o.getString("sub_brand"));            
    		
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);
            cs.setTypeface(Shared.tf);
                             
            int priceTag = 1;
            if (type == PRE_SALLING && !me)
    			priceTag = (int)selectedCustomer.getInt("priceTag");                        
            
            Variant p = prices.query("productCode", o.getString("code"), "customerType", priceTag);
            float price = p.getFloat("price");
            if (price == 0) {            	
            	p = prices.query("productCode", o.getString("code"), "customerType", 1);
            	price = p.getFloat("price");
            }
            
            cs.setText(price+" ₮");
            
            Variant w = product_confirm_info.query("productCode", o.getString("code"));            
            int total = 0, requestCount = (int)w.getInt("requestCount");
            if (w.getInt("flagStatus") > 0) {
            	vs.setText((w.getInt("flagStatus")/getUnit(o))+"|"+(w.getInt("lastCount")/getUnit(o))+packedChr());
            	total = 1;
            	currentOrderedCount = 1;
            }
            else {
            	Variant q = product_order_info.query("productCode", "s"+o.getString("code"));
            	if (w.getInt("requestCount") == 0) { 
            		requestCount = (int)q.getInt("requestCount");
            	} else
            		currentOrderedCount = 1;
            	            
            	vs.setText((getFixedFloat(q.getFloat("packetCount"),1))+"/"+(w.getInt("lastCount")/getUnit(o))+packedChr());
            	total = (int)w.getInt("lastCount")+requestCount;
            }
                                  
            if (total > 0)
            	vs.setTextColor(getResources().getColor(R.color.twit));            	
            
            //image here
            //img.setImageBitmap(getBitmapFromAsset(o.getString("code")+".gif"));
    		String filename = o.getString("code")+".gif";
            imgLoader.DisplayImage("http://103.48.116.112:85/svn/vit/"+filename, img);
            
            Variant q = storage.query("productCode", o.getString("code"));
            if (q.getInt("availCount") / getUnit(o) > 0 || !Shared.feature_list[Shared.STORAGE_CHECK_FEATURE_ID])
            	arr.setVisibility(View.GONE);
          
            v.setTag(o.getString("code"));
            
            return v;
        }
    }
	
	private class CustomerAdapter extends ArrayAdapter<Variant> implements Filterable {        	
        public CustomerAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, collection.getCollection());
            
            this.planetList = collection.getCollection();    		
    		this.origPlanetList = planetList;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = planetList.get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
            
            v.setBackgroundColor(0x000000FF);
            
            if (getCustomerStatus("leased", ":"+o.getInt("parentID")+":")) v.setBackgroundColor(0xFF333333);
            if (getCustomerStatus("worked", o.get("code")+",")) v.setBackgroundColor(0xFF333333);                        	
            
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            TextView cs = (TextView) v.findViewById(R.id.success);
            String value = o.get("customerName");
            int pos = value.indexOf('|');
            String name = value.substring(0, pos==-1?value.length():pos);
            String location = "";
            if (pos < value.length() - 2)
            	location = value.substring(pos+1, value.length());
            
            tt.setText((position+1)+". "+name);
            tt.setTypeface(Shared.tf);
            double dist = distVincenty(Shared.gps_location[0], Shared.gps_location[1], o.getFloat("lat"), o.getFloat("lng"));
            if (dist > 0 && dist < 50)
            	bt.setText(location+" | "+formatCurrency((float)dist)+" km");
            else
            	bt.setText(location);
            bt.setTypeface(Shared.tf);
            String status = "";
            
            if (getCustomerStatus("leased", ":"+o.getInt("parentID")+":")) status = " Зээлтэй ";            
            if (getCustomerStatus("no_entry", o.get("code")+",")) status = " Хийгээгүй |";
            if (getCustomerStatus("closed", o.get("code")+",")) status += " Хаалттай |";
            if (getCustomerStatus("worked", o.get("code")+",")) status += " Oрсон ";                             
            
            cs.setText(status);
            cs.setTypeface(Shared.tf);
            bt.setTextColor(getResources().getColor(R.color.twit));
            return v;
        }
        
        public List<Variant> planetList;    	
        public Filter planetFilter;
        public List<Variant> origPlanetList;                

    	@Override
    	public Filter getFilter() {
    		if (planetFilter == null)
    			planetFilter = new PlanetFilter();

    		return planetFilter;
    	}

    	public void resetData() {
    		planetList = origPlanetList;
    	}
    	
    	private class PlanetFilter extends Filter {
    		@Override
    		protected FilterResults performFiltering(CharSequence constraint) {
    			FilterResults results = new FilterResults();
    			if (constraint == null || constraint.length() == 0) {
    				results.values = origPlanetList;
    				results.count = origPlanetList.size();
    			}
    			else {
    				List<Variant> nPlanetList = new ArrayList<Variant>();
    				Log.d("d", constraint.toString());
    				for (Variant p : planetList) {
    					if (p.getString("name").indexOf(constraint.toString()) != -1 || p.getString("code").startsWith(constraint.toString()) || convertToLatin(p.getString("name")).trim().toLowerCase().startsWith(constraint.toString().trim().toLowerCase()))
    						nPlanetList.add(p);
    				}

    				results.values = nPlanetList;
    				results.count = nPlanetList.size();

    			}
    			return results;
    		}

    		@Override
    		protected void publishResults(CharSequence constraint, FilterResults results) {    		
    			if (results.count == 0)
    				notifyDataSetInvalidated();
    			else {
    				planetList = (List<Variant>) results.values;
    				notifyDataSetChanged();
    			}
    		}
    	}
    	
    	public int getCount() {
    		return planetList.size();
    	}

    	public Variant getItem(int position) {
    		return planetList.get(position);
    	}

    	public long getItemId(int position) {
    		return planetList.get(position).hashCode();
    	}
    }
	
	public class SaleInfoDetailAdapter extends ArrayAdapter<Variant> {

        public SaleInfoDetailAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId, collection.getCollection());            
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;            
            Variant o = collection.getCollection().get(position);
            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
            
            v.setBackgroundColor(0x000000FF);                                                
            
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            TextView cs = (TextView) v.findViewById(R.id.success);
            String value = o.getString("data");            
            tt.setText((position+1)+". "+value);                
            if (command.equals("today_info"))
            	bt.setText(formatCurrency(o.getFloat("amount"))+" ₮");            	
            else {
            	if (command.equals("to_customer_orderdetail")) {
            		Variant p = product_order_info.query("productCode", o.getString("code"));
            		
            		if (p.getInt("requestCount") > 0) {
            			bt.setText(o.getInt("quantity")+" ширхэг, "+formatCurrency(o.getFloat("amount"))+" ₮");
            			cs.setText("-"+p.getInt("requestCount")+" ширхэг");
            		}
            		else
            			bt.setText(o.getInt("quantity")+" ширхэг, "+formatCurrency(o.getFloat("amount"))+" ₮");
            	} else {
	            	if (o.getInt("type") == 1)
	            		bt.setText(o.getInt("quantity")+" ширхэг, "+formatCurrency(o.getFloat("amount"))+" ₮ (зээлээр)");
	            	else
	            		bt.setText(o.getInt("quantity")+" ширхэг, "+formatCurrency(o.getFloat("amount"))+" ₮");
            	}
            }
            
            tt.setTypeface(Shared.tf);            
            bt.setTypeface(Shared.tf);
            cs.setTypeface(Shared.tf);
            
            return v;
        }
    }
	
	@Override
    protected Dialog onCreateDialog(int id) {
	   	Calendar c = Calendar.getInstance();
	   	int cyear = c.get(Calendar.YEAR);
	   	int cmonth = c.get(Calendar.MONTH);
	   	int cday = c.get(Calendar.DAY_OF_MONTH);
	   	switch (id) {
	   		case DATE_DIALOG_ID:
	   				return new DatePickerDialog(this,  mDateSetListener,  cyear, cmonth, cday);
	   		case DATE_DIALOG_ID_1:
   				return new DatePickerDialog(this,  mDateSetListener1,  cyear, cmonth, cday);	
	   	}
	   	return null;
    }
   
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
	   	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	   		String date_selected = String.valueOf(year)+"-"+String.valueOf(monthOfYear+1)+"-"+String.valueOf(dayOfMonth);
	   		buttons.get("data_date").setText(date_selected);
	   		dateStr = date_selected;
   		
	   		new TaskExecution().execute();
	   	}
    };
    
    
    public void setCode(String value) {
    	if (value != null && value.length() == 4) {
    		active = new Variant();
    		active.put("password", "s"+value);
    		
    		command = "change_code";
    		new TaskExecution().execute();    		    		
    	} else
    		Toast.makeText(this, "Ажмилтгүй боллоо !!!", Toast.LENGTH_SHORT).show();
    }
}