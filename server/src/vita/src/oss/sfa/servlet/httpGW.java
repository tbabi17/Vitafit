package oss.sfa.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
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
import oss.core.toolController;
import oss.report.Variant;
import sun.misc.BASE64Decoder;


/**
 * Servlet implementation class today
 */
@WebServlet("/httpGW")
public class httpGW extends HttpServlet implements constantValues {
	private static final long serialVersionUID = 1L;	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public httpGW() {
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
    
    private boolean authenticate(HttpServletRequest req)
    {     
    	String authhead=req.getHeader("Authorization");    	
        if(authhead!=null)
        {       	        
        	if (authhead.length() < 12) return false;
            String usernpass = authhead.substring(6, authhead.length());      
            usernpass = decode(usernpass);
            usernpass = fromHexString(usernpass);                 
            usernpass = decode(usernpass);                     
            String user=usernpass.substring(0,usernpass.indexOf(":"));
            String password=usernpass.substring(usernpass.indexOf(":")+1);
               
         	if (user.equals("voltam_llc") && password.equals("Twi1ig#7@3cli8$E")) {         		
         		return true;
         	}
        }
        
        return true;
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	public boolean trustedZone(String addr) {					
		return trustedZone[0].indexOf(addr) != -1 || addr.startsWith(trustedZone[1]) || true;
	}
	
	public static String fromHexString(String hex) {
		if (hex == null) hex = "";
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		for (int i = 0; i < hex.length(); i+=2) {
			int b = Integer.parseInt(hex.substring(i, i + 2), 16);
			bas.write(b);
		}
		return bas.toString();
	}
	
	private String _donate(String func, String where) {
		return "<SfaWebRequest>" +
				"<func>"+func+"</func>" +
				"<action>SELECT</action>" +
				"<table>"+func+"</table>" +
				"<fields> </fields>" +
				"<types> </types>" +
				"<where>"+where+"</where>" +
				"</SfaWebRequest>";	
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String addr = request.getRemoteAddr();
		String logged = (String)request.getSession().getAttribute("logged");
		toolController.loggedUser = logged;
		toolController.mode = (String)request.getSession().getAttribute("mode1");
		
		if (logged == null) {
			response.setHeader("WWW-Authenticate","Basic realm=\"Authorisation need\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "");
			logger._.info("empty access");
			return;
		}
		if (/*trustedZone(addr) &&*/ authenticate(request)) {			
			try {
				ServletOutputStream out= response.getOutputStream();
				request.setCharacterEncoding("UTF-8");
				String body = request.getParameter("xml");	
				if (body == null) body = "";												
				body = URLDecoder.decode(body, "UTF-8");				
				
				String query = request.getParameter("query");
				String start = request.getParameter("start");
				String limit = request.getParameter("limit");
				String table = request.getParameter("table");						
				if (query != null && query.length() > 0 && (table == null || table.length() == 0 || table.equals("null"))) {
					logger._.info(query);
					body = _donate("_live_search", toolController.toUTF8(query));
				}				
					 				
				body = "<?xml version='1.0' encoding='UTF-8'?>"+body;							
				body = body.replaceAll("&",";");
				body = body.replaceAll("#","%");
				logger._.info(body);
	            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	            InputStream inputStream = new ByteArrayInputStream(body.getBytes("UTF-8"));
            
				Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
				doc.getDocumentElement().normalize();
				
				String root = doc.getDocumentElement().getNodeName();
				
			    NodeList nList = doc.getElementsByTagName(root);
			    
			    response.setHeader("Content-Type", "text/xml; charset=UTF-8");
			    response.setCharacterEncoding("UTF-8");
			    for (int temp = 0; temp < nList.getLength(); temp++) {
			 
			       Node nNode = nList.item(temp);	    
			       if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			  
			          Element eElement = (Element) nNode;
			          logger._.info("func : "  + getTagValue("func",eElement));
			          logger._.info("action : "  + getTagValue("action",eElement));
			          logger._.info("table : "  + getTagValue("table",eElement));
			          logger._.info("fields : "  + getTagValue("fields",eElement));
			          logger._.info("where : "  + getTagValue("where",eElement));
			          logger._.info("types : "  + getTagValue("types",eElement));
			          			           
			          Variant w = new Variant();
			          w.put("func", getTagValue("func",eElement));
			          w.put("action", getTagValue("action",eElement));
			          w.put("table", getTagValue("table",eElement));
			          w.put("fields", getTagValue("fields",eElement));
			          w.put("where", getTagValue("where",eElement));
			          w.put("types", getTagValue("types",eElement));
			          w.put("query", query);
			          w.put("start", start);
			          w.put("limit", limit);
			          
			          String result = "";
			          
			          if (root.equals("SfaWebRequest")) {			        	  
			        	  response.setHeader("Content-Type", "text/json; charset=UTF-8");
			        	  response.setHeader("Content-Encoding", "gzip");
			        	  result = servletHandler.distributeHandler(WEB_HANDLER, w);
			        	  
			        	  ByteArrayOutputStream outB = new ByteArrayOutputStream();  
			      	      GZIPOutputStream gout = new GZIPOutputStream(outB);	   			      	        	     
			      	    
			      	      gout.write(result.getBytes("UTF-8"));  
  			              gout.flush();  
			              gout.close();
			              byte[] buf = outB.toByteArray();  
			              response.getOutputStream().write(buf);
			          }
			          else {
			        	  result = servletHandler.distributeHandler(TOUCH_HANDLER, w);
			        	  out.write(result.getBytes("UTF-8")); 
			          }
			          
			          //logger._.info(result);
			        }
			    }
			} catch (SAXException e) {

			} catch (ParserConfigurationException e) { 

			}
			
		} else 
		{  
		   logger._.info("dry access");
		   response.setHeader("WWW-Authenticate","Basic realm=\"Authorisation need\"");
		   response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "");		   
		}
			
	}
	
	private static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	    if (nValue == null) return "";
	    return nValue.getNodeValue();    
	}	
}
