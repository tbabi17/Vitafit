package oss.core;

import oss.app.systemController;
import oss.report.Variant;

public class servletHandler extends toolController implements constantValues {
	public static systemController system;
	public static String serverProcessId = "null_time"; 
	
	public static String distributeHandler(int mode, Variant w) {
		if (system == null) { 
			system = new systemController();
			serverProcessId = convertDateTimeToString();
		}
				
		switch (mode) {
			case WEB_HANDLER: 
				return system.webHandler(w);
			case MOBILE_HANDLER: 
				return system.mobileHandler(w);
			case ACCESS_HANDLER: 
				return system.loginRequest(w);
			case REPORT_HANDLER: 
				return system.reportHandler(w);
			case TOUCH_HANDLER: 
				return system.touchHandler(w);
		}
		
		return "";
	}
}
