package oss.wildfire.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import oss.cache.logger;
import oss.core.constantValues;
import oss.core.servletHandler;
import oss.report.Variant;
import sun.misc.BASE64Decoder;


/**
 * Servlet implementation class today
 */
@WebServlet("/postGW")
public class postGW extends HttpServlet implements constantValues {
	private static final long serialVersionUID = 1L;	    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public postGW() {
        super();

    }
    
    public void init() {
    	if (logger._ == null)
    		logger._ = Logger.getRootLogger();
    }
    
    public static String decode (String source) {
		  BASE64Decoder enc = new sun.misc.BASE64Decoder();
		  try {
			byte[] b = enc.decodeBuffer(source);
			return new String(b);
		} catch (IOException e) {		
		}
		
		return source;
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
    
    public String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        String outStr = out.toString("ISO-8859-1");
        return outStr;
    }    
    
    private boolean authenticate(HttpServletRequest req)
    {
     String authhead=req.getHeader("Authorization");          
     if(authhead!=null)
     {
      String usernpass = authhead.substring(6, authhead.length());      
      String user=usernpass.substring(0,usernpass.indexOf(":"));
      String password=usernpass.substring(usernpass.indexOf(":")+1);
            
      	if (user.equals("voltam_llc") && password.equals("Twi1ig#7@3cli8$E")) {
      		
      		return true;
      	}
     }

     return false;
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		init();
		
		if (authenticate(request)) {
			try {
				ServletOutputStream out= response.getOutputStream();
	            char cbuf[] = new char[request.getContentLength()];  
	            request.getReader().read(cbuf);  
	            String body = new String(cbuf);
	            logger._.info(body);	            	           
	            System.out.println(body); 
	            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	            InputStream inputStream = new ByteArrayInputStream(body.getBytes());
            
				Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
				doc.getDocumentElement().normalize();
								
			    NodeList nList = doc.getElementsByTagName("RT");			    
			    response.setHeader("Content-Type", "text/xml; charset=UTF-8");
			    response.setCharacterEncoding("UTF-8");
			    
			    for (int temp = 0; temp < nList.getLength(); temp++) {			 
			       Node nNode = nList.item(temp);	    
			       if (nNode.getNodeType() == Node.ELEMENT_NODE) {			 
			          Element eElement = (Element) nNode;			 			          
			          Variant w = new Variant();
			          w.put("func", getTagValue("fn",eElement));
			          w.put("action", getTagValue("at",eElement));
			          w.put("table", getTagValue("tb",eElement));
			          w.put("fields", getTagValue("fs",eElement));
			          w.put("where", getTagValue("wh",eElement));
			          w.put("types", getTagValue("ts",eElement));
			          
			          String result = servletHandler.distributeHandler(MOBILE_HANDLER, w);
			          
			          response.setHeader("Content-Type", "text/json; charset=UTF-8");
		        	  response.setHeader("Content-Encoding", "gzip");		        	  		        	  
		        	  ByteArrayOutputStream outB = new ByteArrayOutputStream();  
		      	      GZIPOutputStream gout = new GZIPOutputStream(outB);	   			      	        	     
		      	      logger._.info(result);
		      	      gout.write(result.getBytes("UTF-8"));  
		      	      gout.flush();  
		              gout.close();
		              byte[] buf = outB.toByteArray();  
		              response.getOutputStream().write(buf);				              		              
			        }
			    } 
			} catch (SAXException e) {
				logger._.error(e.getMessage());
			} catch (ParserConfigurationException e) {
				logger._.error(e.getMessage());
			} 
			
		} else
		{
		   response.setHeader("WWW-Authenticate","Basic realm=\"Authorisation need\"");
		   response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "");
		}
			
	}
	
	public static boolean booleanSid(String sid) {
		String packList = "352372058470979,355831055472301,358186050433619,355831053962261,355831057023730,358186050456610,358186050494538,354957032210125";		
		return packList.indexOf(sid) != -1;
	}
		
	private static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	    return nValue.getNodeValue();    
	}	
}
