package oss.report.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oss.core.constantValues;
import oss.core.servletHandler;
import oss.report.Variant;


/**
 * Servlet implementation class ToBrowser
 */
@WebServlet("/ToBrowser")
public class ToBrowser extends HttpServlet implements constantValues {
	private static final long serialVersionUID = 1L;	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ToBrowser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String isPrint = request.getParameter("print");
		
		try {
			String fields = request.getParameter("fields");
			String types = request.getParameter("types");	
			String template = request.getParameter("template");
			String where = request.getParameter("where");
			Variant w = new Variant();
			w.put("fields", fields);
			w.put("types", types);
			w.put("template", template);
			w.put("where", where);
			w.put("isprint", isPrint);
			
			servletHandler.distributeHandler(REPORT_HANDLER, w);
		} finally {
			if (isPrint != null && isPrint.equals("printCommand")) {
				return;
			} else {
				String filename = "c:/data/reports/report.xls";
				String original_filename = "report.xls";
				File                f        = new File(filename);
		        int                 length   = 0;
		        ServletOutputStream op       = response.getOutputStream();
		        ServletContext      context  = getServletConfig().getServletContext();
		        String              mimetype = context.getMimeType( filename );              
		        System.out.println(mimetype);
		        response.setContentType((mimetype != null) ? mimetype : "application/vnd.ms-excel");//"application/vnd.ms-excel");		
				response.setContentLength( (int)f.length() );
		        response.setHeader( "Content-Disposition", "attachment; filename=\"" + original_filename + "\"" );
		
		        byte[] bbuf = new byte[1024];
		        DataInputStream in = new DataInputStream(new FileInputStream(f));
		
		        while ((in != null) && ((length = in.read(bbuf)) != -1))
		        {
		            op.write(bbuf,0,length);
		        }
		
		        in.close();
		        op.flush();
		        op.close();
			}
		}
	}

}
