package oss.report.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class ToBrowser
 */
@WebServlet("/XlsReceiver")
public class XlsReceiver extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public XlsReceiver() {
        super();
        // TODO Auto-generated constructor stub
    }

    private static final String TMP_DIR_PATH = "c:\\data\\xls";
	private File tmpDir;
	private static final String DESTINATION_DIR_PATH ="c:/data/xls/files/";
	private File destinationDir;
 
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		tmpDir = new File(TMP_DIR_PATH);
		if(!tmpDir.isDirectory()) {
			throw new ServletException(TMP_DIR_PATH + " is not a directory");
		}
		String realPath = DESTINATION_DIR_PATH;
		destinationDir = new File(realPath);
	}
 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out= response.getOutputStream();	    
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		
		fileItemFactory.setSizeThreshold(1*1024*1024); //1 MB
		
		fileItemFactory.setRepository(tmpDir);
 		
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		try {		
			List<?> items = uploadHandler.parseRequest(request);
			Iterator<?> itr = items.iterator();
			while(itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				String fileName = "";

				if(item.isFormField()) {
					out.println("File Name = "+item.getFieldName()+", Value = "+item.getString());
				} else {

					fileName = item.getName();
 				        
					
					File file = new File(destinationDir, fileName);
					item.write(file);
				}
				
				String result = "{success:true, file: 'ok'}";
				
				out.write(result.getBytes("UTF-8"));	
			    out.close();
			}
		}catch(FileUploadException ex) {
			log("Error encountered while parsing the request",ex);
		} catch(Exception ex) {
			log("Error encountered while uploading file",ex);
		}
 
	}

	class Filename {
		  private String fullPath;
		  private char pathSeparator, extensionSeparator;

		  public Filename(String str, char sep, char ext) {
		    fullPath = str;
		    pathSeparator = sep;
		    extensionSeparator = ext;
		  }

		  public String extension() {
		    int dot = fullPath.lastIndexOf(extensionSeparator);
		    return fullPath.substring(dot + 1);
		  }

		  public String filename() { // gets filename without extension
		    int dot = fullPath.lastIndexOf(extensionSeparator);
		    int sep = fullPath.lastIndexOf(pathSeparator);
		    return fullPath.substring(sep + 1, dot);
		  }

		  public String path() {
		    int sep = fullPath.lastIndexOf(pathSeparator);
		    if (sep != -1)			    	
		    	return fullPath.substring(0, sep);
		    return fullPath;
		  }
	}
}
