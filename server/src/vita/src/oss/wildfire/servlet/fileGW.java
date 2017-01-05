package oss.wildfire.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class fileGW
 */
@WebServlet("/fileGW")
public class fileGW extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /** 
     * @see HttpServlet#HttpServlet()
     */
    public fileGW() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileName = request.getParameter("fn");		
		if (fileName == null) return;
		String filename = "c:/data/images/"+fileName;
		if (fileName.equals("xls"))
			filename = "c:/data/reports/report.xls";
		String original_filename = fileName;
		File                f        = new File(filename);		
		if (!f.exists()) return;
        int                 length   = 0;
        ServletOutputStream op       = response.getOutputStream();
        ServletContext      context  = getServletConfig().getServletContext();
        String              mimetype = context.getMimeType( filename );              
        
        response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");//"application/vnd.ms-excel");		
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
