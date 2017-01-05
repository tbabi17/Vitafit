package oss.report.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

@WebServlet("/ImageReceiver")
public class ImageReceiver extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ImageReceiver() {
        super();    
    } 

    private static final String TMP_DIR_PATH = "c:/data/images";
	private File tmpDir;
	private static final String DESTINATION_DIR_PATH ="c:/data/images/files/";
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
		System.out.println("yes");
	    PrintWriter out = response.getWriter();
	    response.setContentType("text/plain");
	    out.println("<h1>Servlet File Upload Example using Commons File Upload</h1>");
	    out.println();
	    System.out.println("images recieving");
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();		
		fileItemFactory.setSizeThreshold(5*1024*1024); //1 MB
		
		fileItemFactory.setRepository(tmpDir);
		
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		uploadHandler.setSizeMax(1*1024*1024);
		try {			
			List<?> items = uploadHandler.parseRequest(request);
			Iterator<?> itr = items.iterator();
			while(itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				
				if(item.isFormField()) {
					out.println("File Name = "+item.getFieldName()+", Value = "+item.getString());
				} else {
					
					System.out.println("Field Name = "+item.getFieldName()+
						", File Name = "+item.getName()+
						", Content type = "+item.getContentType()+
						", File Size = "+item.getSize());
					
					String fileName = item.getName();
					Filename fn = new Filename(fileName, '/', '.');
					File main = new File(destinationDir+fn.path());
				    if (!main.exists())
				        	main.mkdirs();  				        
					
					File file = new File(destinationDir, fileName);
					item.write(file);
				}
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

		  public String filename() { 
		    int dot = fullPath.lastIndexOf(extensionSeparator);
		    int sep = fullPath.lastIndexOf(pathSeparator);
		    return fullPath.substring(sep + 1, dot);
		  }

		  public String path() {
		    int sep = fullPath.lastIndexOf(pathSeparator);
		    return fullPath.substring(0, sep);
		  }
	}
}
