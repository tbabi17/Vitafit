package oss.report.servlet;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ImageSender
 */
@WebServlet("/images")
public class ImageSender extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /** 
     * @see HttpServlet#HttpServlet()
     */
    public ImageSender() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String param = request.getParameter("p");
			String customer = request.getParameter("c");
			
			if (param.equals("r")) {
				ServletOutputStream out= response.getOutputStream();
				request.setCharacterEncoding("UTF-8");
				
				response.setHeader("Content-Type", "text/xml; charset=UTF-8");
			    response.setCharacterEncoding("UTF-8");
			    
			    String files, subdir;
			    File folder = new File("c:\\data\\images\\files\\sdcard\\voltam\\");
			    File[] listOfFiles = folder.listFiles(); 
			   
			    String result = "{'images':[";
			    for (int i = 0; i < listOfFiles.length; i++)
			    if (customer == null || listOfFiles[i].getName().equals(customer))
			    {					    	
			    	File sub = new File("c:\\data\\images\\files\\sdcard\\voltam\\"+listOfFiles[i].getName()+"\\");			    				    	
			    	File[] listOfFiles1 = sub.listFiles();
			    	for (int j = 0; j < listOfFiles1.length; j++) {
			    		if (listOfFiles1[j].isFile()) {
					    	 files = listOfFiles1[j].getName();					    	 
					    	 subdir = listOfFiles[i].getName();
					         if (files.endsWith(".jpg"))
					         {
					            result += "{'name':'"+subdir+"','url':'"+subdir+"\\"+"/"+files+"','customerCode':'"+subdir+"','lastmod':'"+convertDate(Long.parseLong(files.substring(0, files.length()-4)))+"'},";
					         }
			    		}
			    	}
			    }
			    
			    if (result.length() > 12)
			    	result = result.substring(0, result.length()-1);
			    result += "]}";
			  
			    //String result = "{'images':[{'name':'1315558900038.jpg','url':'C00574"+"\\"+"/1315558900038.jpg'}]}";
			    out.write(result.getBytes("UTF-8"));
			} else {							    
			    response.setContentType("image/jpeg");  
			    OutputStream out = response.getOutputStream();  
			    FileInputStream in = new FileInputStream("c:\\data\\images\\files\\sdcard\\voltam\\"+param);  
			    int size = in.available();  
			    byte[] content = new byte[size];  
			    in.read(content);  
			    out.write(content);  
			    in.close();  
			    out.close();
			}
		} catch (Exception e) {
			
		}
	}
	
	private static String convertDate( long str ) {
		Date epoch = new Date(str);
		DateFormat df2 = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String s2 = df2.format(epoch);
		return s2;
	}

	public static BufferedImage bufferImage(Image image, int type) { 
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type); 		
		return bufferedImage; 
	} 
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
