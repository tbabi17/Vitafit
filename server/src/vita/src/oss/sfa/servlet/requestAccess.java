package oss.sfa.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oss.core.constantValues;
import oss.core.servletHandler;
import oss.core.toolController;
import oss.report.Variant;


/**
 * Servlet implementation class requestAccess
 */
@WebServlet("/load")
public class requestAccess extends HttpServlet implements constantValues {
	private static final long serialVersionUID = 1L;	  
	private static final String instanceName = "Vitafit LLC";
	private static final String titleName = "web-side";
	private static final String projectHosting = "https://rawgithub.com/tbabi17/labor/master/";//"https://optimal-mxc-project.googlecode.com/svn/trunk/";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public requestAccess() {
        super();
        // TODO Auto-generated constructor stub
    }        
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public boolean trustedZone(String addr) {					
    	int t = 0;
		return trustedZone[0].indexOf(addr) != -1 || addr.startsWith(trustedZone[1]) || t == 0;
	}
    
    public static String convertDateTimeToString() {        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        try {           
            return dateFormat.format(calendar.getTime());            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }	
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println(convertDateTimeToString());
		String addr = request.getRemoteAddr();
		//String[] loading = {"ачааллаж байна", "загрузка", "loading"};
		if (trustedZone(addr)) {
			String action = request.getParameter("action");
			HttpSession session = request.getSession(true);
			
			if (action != null && action.equals("logout")) {
				session.setAttribute("logged", "");
				session.removeAttribute("logged");
				System.out.println("logout");
				response.sendRedirect("");
				return;
			}						
			
			String logged = (String)session.getAttribute("logged");
			String level = (String)session.getAttribute("level");	
			System.out.println("logged="+logged);
			toolController.loggedUser = logged;
			if(request.getParameter("signIn")!=null){
				long time = System.currentTimeMillis();
				long time1 = 0;
				try {
					String value = request.getParameter("signIn");
					BigInteger t = new BigInteger(value, 16);
					time1 = t.longValue();
				} catch(Exception ex) {
					response.sendRedirect("");
					return;
				}
				if (Math.abs(time-time1) < 1000*45) {
					Variant v = new Variant();
					v.put("user", request.getParameter("username"));
					v.put("password", request.getParameter("password"));
					String login = servletHandler.distributeHandler(3, v);  						
					if (!login.equals("-1")) {
						String[] lg = login.split(",");
						logged = request.getParameter("username");
						session.setAttribute("logged", logged);
						session.setAttribute("key",request.getParameter("password"));
						session.setAttribute("level", lg[0]);
						session.setAttribute("mode", lg[1]);
						
						response.sendRedirect("");
						
						return;
					} else {
						response.sendRedirect("");
						return;
					}
				} else {
					response.sendRedirect("");
					return;
				}
			} else
			if (logged != null && logged.length() >= 3 && !logged.equals("null")) {								
				response.setContentType("text/html; charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				
				String serverName = request.getServerName();
				int serverPort = request.getServerPort();
				String serverHost = serverName+":"+serverPort;
				String mapKey = "";
				
				if (serverHost.equals("202.131.237.182:8080")) {
					mapKey = "ABQIAAAApJIsWtcpFKeqhnzeSyKNKhTBab1y1jaRzkbjZrpWRJEzfXtrmxS4PCnBISxxTlbDwvEn6e8xRrS2ug";
				} else
				if (serverHost.equals("oss:8080")) {
					mapKey = "ABQIAAAApJIsWtcpFKeqhnzeSyKNKhSQLJ3SwU-GxjwftP0R1xQ_mKvK_RT2AY4nJWTt_nab34a9ErfBOkDkXQ";
				}
				mapKey = "ABQIAAAApJIsWtcpFKeqhnzeSyKNKhTBab1y1jaRzkbjZrpWRJEzfXtrmxS4PCnBISxxTlbDwvEn6e8xRrS2ug";
				
				String l = request.getParameter("l");
				String mode = request.getParameter("m");
				if (l == null) l = "0";
				if (mode == null) mode = "vitafit";
				if (!level.equals("15")) {
					mode = (String)session.getAttribute("mode");
				} else { 
					String m1 = (String)session.getAttribute("mode");
					if (!m1.equals("admin"))//ahlah admin bish bol
						mode = m1;
				}
											
				toolController.mode = mode;
				session.setAttribute("mode1", mode);
				toolController.langid = Integer.parseInt(l);/*+loading[Integer.parseInt(l)]+*/
				out.println("<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01//EN' 'http://www.w3.org/TR/html4/strict.dtd'><html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'><title>"+titleName+" ["+instanceName+"]</title></head><body><div id='loading-mask' style=''></div>  <div id='loading'><div class='loading-indicator'><img src='shared/img/anim_loading_sm_082208.gif' style='margin-right:8px;' align='absmiddle'/></div></div><script>var windowIndex=0;var mode = '"+mode+"'; var ln = '"+l+"';var logged = '"+logged+"';var module='"+level+"'; </script><script type='text/javascript' src='"+projectHosting+"shared/include-all.js'></script><a href='http://www.mxc.mn' target='_blank' alt='Powered by MXC LLC' id='poweredby'><div></div></a></body></html>");
				
				return;
			} else {		
				String time = Long.toHexString(System.currentTimeMillis());
				response.setContentType("text/html; charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();//<img class='logo' src='shared/icons/fam/gnome.png' alt='OSS'>
				//out.println("<html lang='en'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'><title>OSS: Нэвтрэх цонх</title> <meta name='description' content=''> <style type='text/css'> html, body, div, h1, h2, h3, h4, h5, h6, p, img, dl,dt, dd, ol, ul, li, table, tr, td, form, object, embed,article, aside, canvas, command, details, figcaption,figure, footer, group, header, hgroup, mark, menu,meter, nav, output, progress, section, summary,time, audio, video { margin: 0; padding: 0; border: 0;}article, aside, details, figcaption, figure, footer,header, hgroup, menu, nav, section { display: block;}html { font: 81.25% arial, helvetica, sans-serif; background: #fefefe; color: #333; line-height: 1; direction: ltr;}a { color: #15c; text-decoration: none;}a:active { color: #d14836;}h1, h2, h3, h4, h5, h6 { color: #222; font-size: 1.54em; font-weight: normal; line-height: 24px; margin: 0 0 .46em;}p { line-height: 17px; margin: 0 0 1em;}ol, ul { list-style: none; line-height: 17px; margin: 0 0 1em;}li { margin: 0 0 .5em;}table { border-collapse: collapse; border-spacing: 0;}strong { color: #222;}button, input, select, textarea { font-family: inherit; font-size: inherit;}button::-moz-focus-inner,input::-moz-focus-inner { border: 0;}html, body { position: absolute; height: 100%; min-width: 100%;}.wrapper { position: relative; min-height: 100%;}.content { margin: 0 auto; width: 650px; padding: 0 44px;}.google-header-bar { height: 71px; background: #f5f5f5; border-bottom: 1px solid #e5e5e5; overflow: hidden;}.header .logo { margin: 18px 0 0 -1px; float: left;}.header .signin,.header .signup { margin: 28px 0 0; float: right; font-weight: bold;}.header .signup-button { margin: 22px 0 0; float: right;}.header .signup-button a { position: relative; top: -1px; margin: 0 0 0 1em;}.main { padding-top: 23px; padding-bottom: 125px;}.google-footer-bar { position: absolute; bottom: 0; height: 35px; width: 100%; border-top: 1px solid #ebebeb; overflow: hidden;}.footer { padding-top: 9px; font-size: .85em; white-space: nowrap; line-height: 0;}.footer a { color: #333;}.footer ul { color: #999; margin: 0; float: left;}.footer ul li { display: inline; padding: 0 1.5em 0 0;}.footer form { float: right;}.footer form .change-language-label { display: none;}.announce-bar { position: absolute; bottom: 35px; height: 33px; z-index: 2; width: 100%; background: #f9edbe; border-top: 1px solid #efe1ac; border-bottom: 1px solid #efe1ac; overflow: hidden;}.announce-bar .message { font-size: .85em; line-height: 33px; margin: 0;}.announce-bar a { margin: 0 0 0 1em;}.kd-announce .message { background: #f9edbe; padding: .6em 1em; color: #333;}.kd-announce a { padding-left: .8em; color: #15c;}.clearfix:after { visibility: hidden; display: block; font-size: 0; content: '.'; clear: both; height: 0;}* html .clearfix { zoom: 1;}*:first-child+html .clearfix { zoom: 1;}input[type=email],input[type=password],input[type=text],input[type=url] { display: inline-block; height: 29px; margin: 0; padding-left: 8px; background: #fff; border: 1px solid #d9d9d9; border-top: 1px solid #c0c0c0; -webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box; -webkit-border-radius: 1px; -moz-border-radius: 1px; border-radius: 1px;}input[type=email]:hover,input[type=password]:hover,input[type=text]:hover,input[type=url]:hover { border: 1px solid #b9b9b9; border-top: 1px solid #a0a0a0; -webkit-box-shadow: inset 0 1px 2px rgba(0,0,0,0.1); -moz-box-shadow: inset 0 1px 2px rgba(0,0,0,0.1); box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);}input[type=email]:focus,input[type=password]:focus,input[type=text]:focus,input[type=url]:focus { outline: none; border: 1px solid #4d90fe; -webkit-box-shadow: inset 0 1px 2px rgba(0,0,0,0.3); -moz-box-shadow: inset 0 1px 2px rgba(0,0,0,0.3); box-shadow: inset 0 1px 2px rgba(0,0,0,0.3);}input[type=email][disabled=disabled],input[type=password][disabled=disabled],input[type=text][disabled=disabled],input[type=url][disabled=disabled] { border: 1px solid #e5e5e5; background: #f5f5f5;}input[type=email][disabled=disabled]:hover,input[type=password][disabled=disabled]:hover,input[type=text][disabled=disabled]:hover,input[type=url][disabled=disabled]:hover { -webkit-box-shadow: none; -moz-box-shadow: none; box-shadow: none;}input[type=checkbox],input[type=radio] { -webkit-appearance: none; appearance: none; width: 13px; height: 13px; margin: 0; cursor: pointer; vertical-align: bottom; background: #fff; border: 1px solid #dcdcdc; -webkit-border-radius: 1px; -moz-border-radius: 1px; border-radius: 1px; -webkit-box-sizing: border-box; -moz-box-sizing: border-box; box-sizing: border-box; position: relative;}input[type=checkbox]:active,input[type=radio]:active { border-color: #c6c6c6; background: #ebebeb;}input[type=checkbox]:hover { border-color: #c6c6c6; -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,0.1); -moz-box-shadow: inset 0 1px 1px rgba(0,0,0,0.1); box-shadow: inset 0 1px 1px rgba(0,0,0,0.1);}input[type=radio] { -webkit-border-radius: 1em; -moz-border-radius: 1em; border-radius: 1em; width: 15px; height: 15px;}input[type=checkbox]:checked,input[type=radio]:checked { background: #fff;}input[type=radio]:checked::after { content: ''; display: block; position: relative; top: 3px; left: 3px; width: 7px; height: 7px; background: #666; -webkit-border-radius: 1em; -moz-border-radius: 1em; border-radius: 1em;}input[type=checkbox]:checked::after { content: url(//ssl.gstatic.com/ui/v1/menu/checkmark.png); display: block; position: absolute; top: -6px; left: -5px;}input[type=checkbox]:focus { outline: none; border-color:#4d90fe;}.g-button { display: inline-block; min-width: 54px; text-align: center; color: #555; font-size: 11px; font-weight: bold; height: 27px; padding: 0 8px; line-height: 27px; -webkit-border-radius: 2px; -moz-border-radius: 2px; border-radius: 2px; -webkit-transition: all 0.218s; -moz-transition: all 0.218s; -ms-transition: all 0.218s; -o-transition: all 0.218s; transition: all 0.218s; border: 1px solid rgba(0,0,0,0.1); background-color: #f5f5f5; background-image: -webkit-gradient(linear,left top,left bottom,from(#f5f5f5),to(#f1f1f1)); background-image: -webkit-linear-gradient(top,#f5f5f5,#f1f1f1); background-image: -moz-linear-gradient(top,#f5f5f5,#f1f1f1); background-image: -ms-linear-gradient(top,#f5f5f5,#f1f1f1); background-image: -o-linear-gradient(top,#f5f5f5,#f1f1f1); background-image: linear-gradient(top,#f5f5f5,#f1f1f1); -webkit-user-select: none; -moz-user-select: none; user-select: none; cursor: default;}*+html .g-button { min-width: 70px;}button.g-button,input[type=submit].g-button { height: 29px; line-height: 29px;}.g-button:hover { border: 1px solid #c6c6c6; color: #333; -webkit-transition: all 0.0s; -moz-transition: all 0.0s; -ms-transition: all 0.0s; -o-transition: all 0.0s; transition: all 0.0s; background-color: #f8f8f8; background-image: -webkit-gradient(linear,left top,left bottom,from(#f8f8f8),to(#f1f1f1)); background-image: -webkit-linear-gradient(top,#f8f8f8,#f1f1f1); background-image: -moz-linear-gradient(top,#f8f8f8,#f1f1f1); background-image: -ms-linear-gradient(top,#f8f8f8,#f1f1f1); background-image: -o-linear-gradient(top,#f8f8f8,#f1f1f1); background-image: linear-gradient(top,#f8f8f8,#f1f1f1); -webkit-box-shadow: 0 1px 1px rgba(0,0,0,0.1); -moz-box-shadow: 0 1px 1px rgba(0,0,0,0.1); box-shadow: 0 1px 1px rgba(0,0,0,0.1);}.g-button:active { background-color: #f6f6f6; background-image: -webkit-gradient(linear,left top,left bottom,from(#f6f6f6),to(#f1f1f1)); background-image: -webkit-linear-gradient(top,#f6f6f6,#f1f1f1); background-image: -moz-linear-gradient(top,#f6f6f6,#f1f1f1); background-image: -ms-linear-gradient(top,#f6f6f6,#f1f1f1); background-image: -o-linear-gradient(top,#f6f6f6,#f1f1f1); background-image: linear-gradient(top,#f6f6f6,#f1f1f1); -webkit-box-shadow: inset 0 1px 2px rgba(0,0,0,0.1); -moz-box-shadow: inset 0 1px 2px rgba(0,0,0,0.1); box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);}.g-button:visited { color: #666;}.g-button-submit { border: 1px solid #3079ed; color: #fff; text-shadow: 0 1px rgba(0,0,0,0.1); background-color: #4d90fe; background-image: -webkit-gradient(linear,left top,left bottom,from(#4d90fe),to(#4787ed)); background-image: -webkit-linear-gradient(top,#4d90fe,#4787ed); background-image: -moz-linear-gradient(top,#4d90fe,#4787ed); background-image: -ms-linear-gradient(top,#4d90fe,#4787ed); background-image: -o-linear-gradient(top,#4d90fe,#4787ed); background-image: linear-gradient(top,#4d90fe,#4787ed);}.g-button-submit:hover { border: 1px solid #2f5bb7; color: #fff; text-shadow: 0 1px rgba(0,0,0,0.3); background-color: #357ae8; background-image: -webkit-gradient(linear,left top,left bottom,from(#4d90fe),to(#357ae8)); background-image: -webkit-linear-gradient(top,#4d90fe,#357ae8); background-image: -moz-linear-gradient(top,#4d90fe,#357ae8); background-image: -ms-linear-gradient(top,#4d90fe,#357ae8); background-image: -o-linear-gradient(top,#4d90fe,#357ae8); background-image: linear-gradient(top,#4d90fe,#357ae8);}.g-button-submit:active { -webkit-box-shadow: inset 0 1px 2px rgba(0,0,0,0.3); -moz-box-shadow: inset 0 1px 2px rgba(0,0,0,0.3); box-shadow: inset 0 1px 2px rgba(0,0,0,0.3);}.g-button-share { border: 1px solid #29691d; color: #fff; text-shadow: 0 1px rgba(0,0,0,0.1); background-color: #3d9400; background-image: -webkit-gradient(linear,left top,left bottom,from(#3d9400),to(#398a00)); background-image: -webkit-linear-gradient(top,#3d9400,#398a00); background-image: -moz-linear-gradient(top,#3d9400,#398a00); background-image: -ms-linear-gradient(top,#3d9400,#398a00); background-image: -o-linear-gradient(top,#3d9400,#398a00); background-image: linear-gradient(top,#3d9400,#398a00);}.g-button-share:hover { border: 1px solid #2d6200; color: #fff; text-shadow: 0 1px rgba(0,0,0,0.3); background-color: #368200; background-image: -webkit-gradient(linear,left top,left bottom,from(#3d9400),to(#368200)); background-image: -webkit-linear-gradient(top,#3d9400,#368200); background-image: -moz-linear-gradient(top,#3d9400,#368200); background-image: -ms-linear-gradient(top,#3d9400,#368200); background-image: -o-linear-gradient(top,#3d9400,#368200); background-image: linear-gradient(top,#3d9400,#368200);}.g-button-share:active { -webkit-box-shadow: inset 0 1px 2px rgba(0,0,0,0.3); -moz-box-shadow: inset 0 1px 2px rgba(0,0,0,0.3); box-shadow: inset 0 1px 2px rgba(0,0,0,0.3);}.g-button-red { border: 1px solid transparent; color: #fff; text-shadow: 0 1px rgba(0,0,0,0.1); text-transform: uppercase; background-color: #d14836; background-image: -webkit-gradient(linear,left top,left bottom,from(#dd4b39),to(#d14836)); background-image: -webkit-linear-gradient(top,#dd4b39,#d14836); background-image: -moz-linear-gradient(top,#dd4b39,#d14836); background-image: -ms-linear-gradient(top,#dd4b39,#d14836); background-image: -o-linear-gradient(top,#dd4b39,#d14836); background-image: linear-gradient(top,#dd4b39,#d14836);}.g-button-red:hover { border: 1px solid #b0281a; color: #fff; text-shadow: 0 1px rgba(0,0,0,0.3); background-color: #c53727; background-image: -webkit-gradient(linear,left top,left bottom,from(#dd4b39),to(#c53727)); background-image: -webkit-linear-gradient(top,#dd4b39,#c53727); background-image: -moz-linear-gradient(top,#dd4b39,#c53727); background-image: -ms-linear-gradient(top,#dd4b39,#c53727); background-image: -o-linear-gradient(top,#dd4b39,#c53727); background-image: linear-gradient(top,#dd4b39,#c53727); -webkit-box-shadow: 0 1px 1px rgba(0,0,0,0.2); -moz-box-shadow: 0 1px 1px rgba(0,0,0,0.2); -ms-box-shadow: 0 1px 1px rgba(0,0,0,0.2); -o-box-shadow: 0 1px 1px rgba(0,0,0,0.2); box-shadow: 0 1px 1px rgba(0,0,0,0.2);}.g-button-red:active { border: 1px solid #992a1b; background-color: #b0281a; background-image: -webkit-gradient(linear,left top,left bottom,from(#dd4b39),to(#b0281a)); background-image: -webkit-linear-gradient(top,#dd4b39,#b0281a); background-image: -moz-linear-gradient(top,#dd4b39,#b0281a); background-image: -ms-linear-gradient(top,#dd4b39,#b0281a); background-image: -o-linear-gradient(top,#dd4b39,#b0281a); background-image: linear-gradient(top,#dd4b39,#b0281a); -webkit-box-shadow: inset 0 1px 2px rgba(0,0,0,0.3); -moz-box-shadow: inset 0 1px 2px rgba(0,0,0,0.3); box-shadow: inset 0 1px 2px rgba(0,0,0,0.3);}.g-button-white { border: 1px solid #dcdcdc; color: #666; background: #fff;}.g-button-white:hover { border: 1px solid #c6c6c6; color: #333; background: #fff; -webkit-box-shadow: 0 1px 1px rgba(0,0,0,0.1); -moz-box-shadow: 0 1px 1px rgba(0,0,0,0.1); box-shadow: 0 1px 1px rgba(0,0,0,0.1);}.g-button-white:active { background: #fff; -webkit-box-shadow: inset 0 1px 2px rgba(0,0,0,0.1); -moz-box-shadow: inset 0 1px 2px rgba(0,0,0,0.1); box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);}.g-button-red:visited,.g-button-share:visited,.g-button-submit:visited { color: #fff;}.g-button-submit:focus,.g-button-share:focus,.g-button-red:focus { -webkit-box-shadow: inset 0 0 0 1px #fff; -moz-box-shadow: inset 0 0 0 1px #fff; box-shadow: inset 0 0 0 1px #fff;}.g-button-share:focus { border-color: #29691d;}.g-button-red:focus { border-color: #d14836;}.g-button-submit:focus:hover,.g-button-share:focus:hover,.g-button-red:focus:hover { -webkit-box-shadow: inset 0 0 0 1px #fff, 0 1px 1px rgba(0,0,0,0.1); -moz-box-shadow: inset 0 0 0 1px #fff, 0 1px 1px rgba(0,0,0,0.1); box-shadow: inset 0 0 0 1px #fff, 0 1px 1px rgba(0,0,0,0.1);}.content { width: auto; max-width: 1000px; min-width: 780px;}.product-info { margin: 0 385px 0 0;}.product-info h3 { font-size: 1.23em; font-weight: normal;}.product-info a:visited { color: #61c;}.sign-in { width: 335px; float: right;}.signin-box { margin: 12px 0 0; padding: 20px 25px 15px; background: #ECECEC; border: 1px solid #c5c5c5;}.product-headers { margin: 0 0 1.5em;}.product-headers h1 { color: #dd4b39; font-size: 25px; margin: 0;}.product-headers h2 { font-size: 16px; margin: .4em 0 0;}.features { overflow: hidden; margin: 2em 0 0;}.features li { margin: 3px 0 2em;}.features img { float: left; margin: -3px 0 0;}.features p { margin: 0 0 0 68px;}.features .title { font-size: 16px; margin-bottom: .3em;}.features.no-icon p { margin: 0;}.features .small-title { font-size: 1em; font-weight: bold;}.notification-bar { background: #f9edbe; padding: 8px;}.signin-box h2 { font-size: 16px; line-height: 16px; height: 16px; margin: 0 0 1.2em; position: relative;}.signin-box h2 strong { display: inline-block; position: absolute; right: 0; top: 1px; height: 19px; width: 52px; }.signin-box label { display: block; margin: 0 0 1.5em;}.signin-box input[type=text],.signin-box input[type=password] { display: block; width: 100%; height: 32px; font-size: 15px;}.signin-box .email-label,.signin-box .passwd-label { font-weight: bold; margin: 0 0 .5em; display: block; -webkit-user-select: none; -moz-user-select: none; user-select: none;}.signin-box .reauth { display: inline-block; font-size: 15px; height: 29px; line-height: 29px; margin: 0;}.signin-box label.remember { display: inline-block; position: relative; top: -1px;}.signin-box .remember-label { font-weight: normal; color: #666; line-height: 17px; position: relative; top: 2px; padding: 0 0 0 .4em; -webkit-user-select: none; -moz-user-select: none; user-select: none;}.signin-box input[type=submit] { display: inline-block; min-width: 70px; margin: 0 1.5em 1.2em 0; height: 32px; font-size: 13px;}.signin-box ul { margin: 0;}.errormsg { margin: .5em 0 0; display: block; color: #dd4b39; line-height: 17px;}.training-msg { padding: .5em 8px; background: #f9edbe;}.training-msg p { margin: 0 0 .5em;}input[type=password].form-error,input[type=text].form-error { border: 1px solid #dd4b39;}.help-link { background: #dd4b39; padding: 0 5px; color: #fff; font-weight: bold; display: inline-block; -webkit-border-radius: 1em; -moz-border-radius: 1em; border-radius: 1em; text-decoration: none; position: relative; top: 0px;}.help-link:visited { color: #fff;}.help-link:hover { opacity: .7; color: #fff;}.mail ul.mail-links { list-style: none; margin: 0; overflow: hidden;}.mail ul.mail-links li { float: left; margin: 0 20px 0 0;} </style> </head> <body> <div class='wrapper'> <div class='google-header-bar'> <div class='header content clearfix'> <a id='link-google' href='' ></a> <span class='signup-button'> <a id='link-signup' class='g-button g-button-red' href=''> Хувилбар 3.5 RC1 </a> </span> </div> </div> <div class='main content clearfix'> <div class='sign-in'> <div class='signin-box'> <h2>Нэвтрэх <strong></strong></h2> <form id='gaia_loginform' action='' method='post'> <label> <strong class='email-label'>Хэрэглэгчийн нэр</strong> <input type='text' name='Email' id='Email' value=''> </label> <label> <strong class='passwd-label'>Нууц үг</strong> <input type='password' name='Passwd' id='Passwd' autocomplete='off'> </label> <input type='submit' class='g-button g-button-submit' name='signIn' id='signIn' value='Нэвтрэх'> <label class='remember'> <input type='checkbox' name='PersistentCookie' id='PersistentCookie' value='yes' checked='checked'> <strong class='remember-label'> Үндсэн горим </strong> </label> <input type='hidden' name='rmShown' value='1'> </form> <ul> <li> <a id='link-forgot-passwd' href='' target='_top'> Холбогдож чадахгүй байна уу ? </a> </li> </ul> </div> </div> <div class='product-info mail'> <div class='product-headers'> <h1>Борлуулалтын систем</h1> <h2>Таны IP : "+addr+"</h2> </div> <p> Зөвхөн дотоод сүлжээнд ажиллана !</p> <ul class='features'> <li> <img src='shared/img/filing_cabinet-g42.png' alt=''> <p class='title'>Систем</p> <p> Ext JS Version 4.1 RC3 </p> </li> <li> <img src='shared/img/nosign-r42.png' alt=''> <p class='title'>Хамгаалалт</p> <p>SSL supported, Security Manager</p> </li> <li> <img src='shared/img/mobile_phone-42.gif' alt=''> <p class='title'>Smart phone</p> <p>Гар утасны програм.</p> </li> </ul> </div> <div id='cc_iframe_parent'></div> </div> <div class='google-footer-bar'> <div class='footer content clearfix'> <ul> <li>© 2011 MXC</li> </ul> </div> </div> </div> </body> </html>");
				out.println("<!DOCTYPE HTML><html lang='en-US'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'/><link rel='stylesheet' type='text/css' media='screen' href='shared/login.css'/><title>"+titleName+"</title><meta name='description' content='"+titleName+"'/></head><body><!-- LOGIN --><div id='pageLogin'><div class='new-apple-logo'></div><div class='user-avatar'><div id='avatar'><div id='cover'></div><div class='ava-css'><img src='shared/img/checkin_key.png' /></div><div class='logName'><p>Нэвтрэх хэсэг</p></div><div id='switch'><div class='validate'><form action=''><input type='username' name='username' id='username' placeholder='Хэрэглэгч'/><br><br><input type='password' name='password' id='password' placeholder='Нууц үг' autocomplete='off' /><input type='submit' class='submit' name='signIn' id='signIn' value='"+time+"'/></form></div></div></div></div></div></body></html>");
			}
		} else {
			System.out.println("Restricted IP address " + addr);			
		} 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
