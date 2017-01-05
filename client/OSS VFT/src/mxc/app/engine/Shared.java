package mxc.app.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import oss.android.vita.app.BixolonManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

public class Shared {
	public static String GW_URL;
	public static String COMPANY = "VITAFIT XXK";
	
	public static Typeface tf;
	public static int customerScrollLocation = 0;
	
	public static boolean DATA_ENABLED = true;
	public static boolean BLUETOOTH_ENABLED = true;
	public static double[] gps_location = new double[2];
		
	public static String PATH = "/sdcard/";
	public static String IMG_PATH = "/sdcard/vitafit/";
	
	public static boolean ROUTE_LIST_FEATURE = true; 
	public static int CASH_DISCOUNT_CONSTANT = 0;
	public static int PAYMENT_FEATURE_ID = 0;
	public static int PROMOTION_FEATURE_ID = 1;
	public static int STORAGE_CHECK_FEATURE_ID = 2;
	public static boolean BLUETOOTH_FEATURE = false;
	public static boolean[] feature_list = {false, false, false};

	public static long wareHouseId = 0;
	public static long current_time = 0;
	public static String unitMode = "p";
	public static String routeID = "";
	public static String sid = "";
	public static String wareHouseName = "";	
	public static String userID = "";
	
	public static int batteryLevel = 0;
	public static BixolonManager bixolon; 
	public static String printLine = "";
	
	public static Variant selectedCustomer;
	public static boolean order_download_request = true;
	public static boolean WARE_HOUSE_SELECT_MODE = false;
	
	private static void saveToInternalSorage(Context context, File directory, Bitmap bitmapImage, String filename) throws IOException{        	            	   
        File file=new File(directory, filename);    
        if (file.exists()) return;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            
        }        
    }
        
    public static void downloadFromUrl(Context context, String imageURL, String fileName) {
        try {        
        	ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("private", Context.MODE_PRIVATE);    
            
        	InputStream is = context.getAssets().open("B"+fileName.replaceAll("-", "_"));
        	if (is.available() > 0) {
        		Bitmap myBitmap = BitmapFactory.decodeStream(is);		            
            	saveToInternalSorage(context, directory, myBitmap, fileName);
            	return;
        	}        	
        	                   
            if (!directory.exists()){
        		directory.createNewFile();
        		directory.mkdir();
         	}
            File f =new File(directory, fileName);            
            
        	if (!f.exists() || f.length() == 403) {
	            URL myImageURL = new URL(imageURL);
	            HttpURLConnection connection = (HttpURLConnection)myImageURL.openConnection();
	            connection.setDoInput(true);
	            connection.connect();
	            InputStream input = connection.getInputStream();
		        
	            if (!f.exists()) {
	            	Bitmap myBitmap = BitmapFactory.decodeStream(input);		            
	            	saveToInternalSorage(context, directory, myBitmap, fileName);
	            } else
	            if (f.length () == 403 && input.available() != 403) {
	            	Bitmap myBitmap = BitmapFactory.decodeStream(input);		            
	            	saveToInternalSorage(context, directory, myBitmap, fileName);
	            } 	            	           
        	}
        } catch (IOException e) {}
    }        
}
