package oss.android.vita.app;

import java.text.NumberFormat;
import java.util.List;

import mxc.app.engine.BasicActivity;
import mxc.app.engine.Collection;
import mxc.app.engine.Shared;
import mxc.app.engine.Variant;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ShowMap extends BasicActivity {
	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private Collection collect;
	private int command = 0;
	private int success = 0;
	
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
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.map); // bind the layout to the activity
		
		mapView = (MapView)findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setStreetView(true);
		mapView.setSatellite(true);
		mapController = mapView.getController();		
		mapController.setZoom(12); // Zoon 1 is world view				
        getLocation();
        mapView.invalidate();
        
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getIntent().getExtras().getString("name"));
        Button send = (Button)findViewById(R.id.sendloc);
        send.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {                  	
            	finish();
            }
        });
        
        Button camera = (Button)findViewById(R.id.snapshot);
        camera.setVisibility(View.GONE);
        camera.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {                  	
            	//cameraActivity();
            }
        });       
        
        command = 1;
        new TaskExecution().execute();
	}
	
	public void getLocation() {
		MapOverlay myOverlay = new MapOverlay((float)Shared.gps_location[0], (float)Shared.gps_location[1], 0);                        
        		
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(myOverlay);                
        mapController.animateTo(myOverlay.getPoint());               
	}
	
	public static String getFixedFloat(float v, int t) {
		NumberFormat nf = NumberFormat.getCurrencyInstance();
    	String s = nf.format(v);
    	s = s.substring(1, s.length());    	
    	s = String.format("%"+t+"s", s);       	
		return s;
	}
	
	public void viewCustomerLocation(Variant w) {
		if (w.getFloat("posX") > 0 && w.getFloat("posY") > 0) {
			List<Overlay> listOfOverlays = mapView.getOverlays();
			MapOverlay toOverlay = new MapOverlay(w.getFloat("posX"), w.getFloat("posY"), 1);
			//company_list.setText(Shared.selectedCustomer.get("customerName")+" ("+getFixedFloat((float)distVincenty(w.getFloat("posX"), w.getFloat("posY"), Shared.gps_location[0], Shared.gps_location[1]), 4)+" km)");
			
			listOfOverlays.add(toOverlay);
			
			mapController.animateTo(toOverlay.getPoint());
			
			//mapView.invalidate();
		}
	}
	
	public void sendLocation() {
		command = 2;
		new TaskExecution().execute();
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
	
	public void quitForm() {
		finish();
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
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
		 }

		 @Override
		 protected Void doInBackground(Void... arg0) {
			 preProcessing();
			 return null;
		 }
   }
	
   public void preProcessing() {	  
	   if (command == 1)
		   collect = fromJSON("Customer", "posX,posY", "f,f", " WHERE code='"+Shared.selectedCustomer.get("code")+"'");
	   if (command == 2) {
		   Variant w = new Variant();
		   w.put("posX", "f"+Shared.gps_location[0]);
		   w.put("posY", "f"+Shared.gps_location[1]);		   
		   success = toJSONUpdate("update", "Customer", "posX,posY", w, " code='"+Shared.selectedCustomer.get("code")+"'");
	   }	 
   }
	
   public void preStarting() {	    
		
   }
  
   public void completeReading() {
	   if (command == 1) {
		   if (collect.size() > 0) {
			   viewCustomerLocation(collect.elementAt(0));
		   }
	   } else 
	   if (command == 2) {
		   if (success == 0) {
			   alertInfo(this, "Амжилттай шинэчилэлээ !");
			   finish();
		   }
		   else
			   alertInfo(this, "Амжилтгүй боллоо !");
	   }	   	   
   }
   
   public static void alertInfo(Context context, String alert) {    	
		new AlertDialog.Builder(context)
       .setIcon(android.R.drawable.ic_dialog_info)
       .setTitle("Мэдээлэл")
       .setMessage(alert)
       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
           	dialog.dismiss();
           }

       }).
       show();    	
   }
}
