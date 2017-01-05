package oss.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class mapConnection extends httpConnection {
	
	public mapConnection(systemController sh) {
		super(sh);  
	} 
	 
	public String getUserLocations() {					
		String data = "";			 					
		try {									
			Connection con = shared.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT userCode,max(posX),max(posY) FROM Sales Group by userCode");			
			ResultSet rs = ps.executeQuery();					
			while (rs.next()) {		
				data += "{type:'point', account:'voltam',id:'"+rs.getString(1)+"',coordinates:[{lat:"+rs.getFloat(2)+",lng:"+rs.getFloat(3)+"}],title:'"+rs.getString(1)+"',description:'"+rs.getFloat(2)+" "+rs.getFloat(3)+"',style:{icon:{x:"+3+",y:"+3+"}}},";
			}						
			data = data.substring(0, data.length() - 1);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}				
		
		return data;
	}
	
	public String getCustomerLocations(String routeId) {				
		String data = "";								
		try {			
			Connection con = shared.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT code,posX,posY,name,location FROM Customer WHERE posX is not null and posY is not null and code in (select customerCode from Route_Customer where routeId='"+routeId+"')");			
			ResultSet rs = ps.executeQuery();					
			while (rs.next()) {		
				data += "{type:'point', account:'voltam',id:'"+rs.getString(1)+"',coordinates:[{lat:"+rs.getFloat(2)+",lng:"+rs.getFloat(3)+"}],title:'"+rs.getString(4)+"',description:'"+rs.getString(5)+"',style:{icon:{x:"+0+",y:"+0+"}}},";
			}
			if (data.length() > 0)
				data = data.substring(0, data.length() - 1);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		data = "[{status:'success',operation:'get',result:{geometries:{records:["+data+getUserLocations()+"]}}}]";
		
		return data;
	}
	
	public String getRouteList() {					
		String data = "{'results':1,'items':[";		
		try {			
			Connection con = shared.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT routeId,routeName from Route");			
			ResultSet rs = ps.executeQuery();					
			while (rs.next()) {		
				data += "{'id':'"+rs.getString(1)+"','name':'"+rs.getString(2)+"','fname':'"+rs.getString(2)+"','category':'voltam'},";
			}			
			data = data.substring(0, data.length() - 1);
			rs.close();
			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
				
		data += "]}";
		
		return data;
	}
}
