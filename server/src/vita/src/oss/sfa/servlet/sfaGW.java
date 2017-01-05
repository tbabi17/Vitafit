package oss.sfa.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import oss.core.constantValues;
import oss.core.servletHandler;
import oss.report.Variant;
import sun.misc.BASE64Decoder;


/**
 * Servlet implementation class today
 */
@WebServlet("/sfaGW")
public class sfaGW extends HttpServlet implements constantValues {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public sfaGW() {
        super();

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
		if (authenticate(request)) {
			try {
				ServletOutputStream out= response.getOutputStream();
	            char cbuf[] = new char[request.getContentLength()];  
	            request.getReader().read(cbuf);  
	            String body = new String(cbuf); 
	            
	            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	            InputStream inputStream = new ByteArrayInputStream(body.getBytes());
            
				Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
				doc.getDocumentElement().normalize();
				
				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			    NodeList nList = doc.getElementsByTagName("SfaRequest");
			    System.out.println("-----------------------");
			    //response.setHeader("Content-Type", "text/xml; ");
			    //response.setContentType("text/plain; charset=UTF-8");
			    response.setHeader("Content-Type", "text/xml; charset=UTF-8");
			    response.setCharacterEncoding("UTF-8");
			    
			    for (int temp = 0; temp < nList.getLength(); temp++) {
			 
			       Node nNode = nList.item(temp);	    
			       if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			 
			          Element eElement = (Element) nNode;
			 
			          System.out.println("func : "  + getTagValue("func",eElement));
			          System.out.println("action : "  + getTagValue("action",eElement));
			          System.out.println("table : "  + getTagValue("table",eElement));
			          System.out.println("fields : "  + getTagValue("fields",eElement));
			          System.out.println("where : "  + getTagValue("where",eElement));
			          System.out.println("Types : "  + getTagValue("types",eElement));
			          
			          Variant w = new Variant();
			          w.put("func", getTagValue("func",eElement));
			          w.put("action", getTagValue("action",eElement));
			          w.put("table", getTagValue("table",eElement));
			          w.put("fields", getTagValue("fields",eElement));
			          w.put("where", getTagValue("where",eElement));
			          w.put("types", getTagValue("types",eElement));
			          String result = servletHandler.distributeHandler(MOBILE_HANDLER, w);
			          System.out.println(result);
			          			          			         
			          out.write(result.getBytes("UTF-8"));			          
			        }
			    }
			} catch (SAXException e) {

			} catch (ParserConfigurationException e) {

			}
			
		} else
		{
		   response.setHeader("WWW-Authenticate","Basic realm=\"Authorisation need\"");
		   response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "");
		}
			
	}
	
	private static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	    return nValue.getNodeValue();    
	}	
}
