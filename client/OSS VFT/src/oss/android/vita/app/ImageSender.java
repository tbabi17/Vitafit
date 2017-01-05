package oss.android.vita.app;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mxc.app.engine.Shared;
import android.content.Context;
import android.util.Log;

public class ImageSender {

	private static List<String> readSDCard(String path)  
	{  
		List<String> tFileList = new ArrayList<String>();    
		File f = new File(path);  	  
		File[] files=f.listFiles();  	 
		for(int i=0; i<files.length; i++)  
		{  
			File file = files[i];  
			tFileList.add(file.getPath());  
		}  
	  
		return tFileList;  
	}  
	
	public static void sendAll(Context context) {
		List<String> list = readSDCard(Shared.IMG_PATH);
		
		for (int i = 0; i < list.size(); i++) {
			List<String> list1 = readSDCard(list.get(i));
			
			for (int j = 0; j < list1.size(); j++) {				
				sender(list1.get(j));
				
				//File file = new File(sn[j].getPath()+sn[j].getName());
				//file.delete();
			}
		}
	}
	
	public static void sender(String fileName) {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		//DataInputStream inputStream = null;

		String pathToOurFile = fileName;
		String urlServer = Shared.GW_URL+"ImageReceiver";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1024;

		try
		{			
			FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );
	
			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();
	
			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
	
			// Enable POST method
			connection.setRequestMethod("POST");
	
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	
			outputStream = new DataOutputStream( connection.getOutputStream() );
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
			outputStream.writeBytes(lineEnd);
	
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];				
			
			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);			
			while (bytesRead > 0)
			{
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
	
			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
						
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();
			
			Log.d("d", "res="+serverResponseMessage+" "+serverResponseCode);
	
			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
		}
		catch (Exception ex)
		{
			Log.d("d", "asfdasfdasdfasdfasdf");			
		}
	}
}
