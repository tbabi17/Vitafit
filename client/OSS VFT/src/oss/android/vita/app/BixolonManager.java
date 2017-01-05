package oss.android.vita.app;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.bixolon.android.library.BxlService;

public class BixolonManager {
	 private static final String TAG = "BXLVOLTAM";
	 private BxlService mBxlService = null;   
	 private boolean conn = false;
	 //private PowerManager pm;
	 //private PowerManager.WakeLock wl;
	 //private static final int Samsung_GalaxyA =0;
	 private static final int Samsung_GalaxyS =1;
	 private static final int HTC_Desire =2;
	 private int DeviceMoldel=Samsung_GalaxyS;
	 private Context mContext;
	 
	 public BixolonManager(Context context)
     {
		mContext = context;
    	Log.e(TAG, "+++ ON Contructor +++"); 
    	String DeviceModeName="HTC Desire";
    	  if(DeviceModeName.equals(Build.MODEL)){
    		DeviceMoldel=HTC_Desire;
    		Log.i(TAG, Build.MODEL+" "+DeviceModeName.equals(Build.MODEL));
    	  }else{
    		DeviceMoldel=Samsung_GalaxyS;
    		Log.i(TAG, Build.MODEL+" "+DeviceModeName.equals(Build.MODEL));
    	  }
     }
	 
	 public void startBixolon() {
		 mBxlService = new BxlService();		 
		 if (mBxlService.Connect() == 0 ) {       
             conn = true;
         }
         else {             
             //Toast.makeText(mContext, "check printer & bluetooth", Toast.LENGTH_SHORT).show();
             conn = false;
         }
		 
		 CheckGC("Connect_End" );
	 }
	 
	 public void stopBixolon() {
		 if (conn) {
			 CheckGC("Disconnect_Start" );                
			 mBxlService.Disconnect(); 
			 mBxlService = null;         
			 conn = false; 
			 CheckGC("Disconnect_End" );
		 }
	 }
	 
	 public boolean printData(String line) {
		 if (!conn) return false;
		 
		 CheckGC("PrintText_Start" );		 
		 int returevlaue= mBxlService.GetStatus();
		 if(returevlaue==BxlService.BXL_SUCCESS){   
			 mBxlService.SetCharacterSet(BxlService.BXL_CS_WPC1251);
        	   returevlaue=mBxlService.PrintText(line,
        			   BxlService.BXL_ALIGNMENT_LEFT, 
        			   BxlService.BXL_FT_DEFAULT, 
        			   BxlService.BXL_TS_0WIDTH | BxlService.BXL_TS_0HEIGHT );
        	   		
        	   CheckGC("PrintText_Loop" );
               return (returevlaue==BxlService.BXL_SUCCESS);           
        }
		 		 
		return false;
	 }
	 
	 public void CheckGC(String FunctionName )
     {
		  long VmfreeMemory =Runtime.getRuntime().freeMemory();
    	  long VmmaxMemory=Runtime.getRuntime().maxMemory();
    	  long VmtotalMemory=Runtime.getRuntime().totalMemory();
    	//long waittime=53;
    	  long Memorypercentage=((VmtotalMemory-VmfreeMemory)*100)/VmtotalMemory; 
    	
    	  Log.i(TAG,FunctionName+"Before Memorypercentage"+Memorypercentage+"% VmtotalMemory["+VmtotalMemory+"] "+"VmfreeMemory["+VmfreeMemory+"] "+"VmmaxMemory["+VmmaxMemory+"] ");
    	  
    	  //Runtime.getRuntime().gc();
    	  System.runFinalization();
    	  System.gc();
    	  VmfreeMemory =Runtime.getRuntime().freeMemory();
          VmmaxMemory=Runtime.getRuntime().maxMemory();
     	  VmtotalMemory=Runtime.getRuntime().totalMemory();
     	  Memorypercentage=((VmtotalMemory-VmfreeMemory)*100)/VmtotalMemory; 
    	  Log.i(TAG,FunctionName+"_After Memorypercentage"+Memorypercentage+"% VmtotalMemory["+VmtotalMemory+"] "+"VmfreeMemory["+VmfreeMemory+"] "+"VmmaxMemory["+VmmaxMemory+"] ");
     }
}
