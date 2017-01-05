package oss.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import oss.additional.ReportData;
import oss.report.Collection;
import oss.report.Variant;

  
public class reportConnection extends httpConnection {	
	public Hashtable<String, Collection> codeAndNames = new Hashtable<String, Collection>();
	
	public reportConnection(systemController sh) {
		super(sh);
	}      
	      
	public void loadCodeAndNames() {
		if (codeAndNames.size() == 0) {
			codeAndNames = new Hashtable<String, Collection>();
		
			Collection route = getCollection("routeID,routeName", "s,s", jsonData("Route", "routeID,routeName", ""));
			codeAndNames.put("routeID", route);
			codeAndNames.put("subid", route);
			codeAndNames.put("mon", route);codeAndNames.put("fri", route);
			codeAndNames.put("thue", route);codeAndNames.put("sat", route);
			codeAndNames.put("wed", route);codeAndNames.put("sun", route);
			codeAndNames.put("thur", route);
			codeAndNames.put("wareHouseID", getCollection("wareHouseID,name", "i,s", jsonData("Ware_House", "wareHouseID,name", "")));
			codeAndNames.put("_group", getCollection("_group,descr", "s,s", jsonData("User_Type", "_group,descr", "")));
			codeAndNames.put("userCode", getCollection("code,firstName", "s,s", jsonData("Users", "code,firstName", "")));
			codeAndNames.put("productCode", getCollection("code,name", "s,s", jsonData("Product", "code,name", "")));
			codeAndNames.put("customerCode", getCollection("code,name", "s,s", jsonData("Customer", "code,name", "")));			
			codeAndNames.put("customerType", getCollection("price_tag,descr", "i,s", jsonData("User_Type", "price_tag,descr", " GROUP by price_tag,descr")));
		}
	}
	
	public String getFieldByTable(String field) {
		if (field.equals("active"))
			return "descr";
		if (field.equals("routeID"))
			return "routeName";
		if (field.equals("_group"))
			return "descr";				
		if (field.equals("userCode"))
			return "firstName";
		if (field.equals("customerType"))
			return "descr";
		if (field.equals("subid") || field.equals("mon") || field.equals("thue") || field.equals("wed") || field.equals("thur") || field.equals("fri") || field.equals("sat") || field.equals("sun"))
			return "routeName";
				
		return "name";
	}	
	
	public String getFieldByCode(String field) {
		if (field.equals("active"))
			return "id";
		if (field.equals("_group"))
			return "_group";
		if (field.equals("wareHouseID"))
			return "wareHouseID";
		if (field.equals("customerType"))
			return "price_tag";
		if (field.equals("subid") || field.equals("mon") || field.equals("thue") || field.equals("wed") || field.equals("thur") || field.equals("fri") || field.equals("sat") || field.equals("sun"))
			return "routeID";
		
		return "code";
	}	
	
	public String getNameForCode(String fieldName, String code) {		
		if (codeAndNames.containsKey(fieldName)) {
			Collection collection = codeAndNames.get(fieldName);
			return collection.query(getFieldByCode(fieldName), code).get(getFieldByTable(fieldName));
		}
		
		return code==null?"":code;
	}
	
	Hashtable<String, ReportData> rds = new Hashtable<String, ReportData>();
	
	private void getReportData(String func)
    {
    	Connection con = shared.getConnection();			
		PreparedStatement ps;
		String s="";
		ReportData rd = new ReportData();
		rd.index = 0;				
		try {
			if (func.startsWith("_bosgo_report")) {
				String query = shared.cacheMan.loadQueryCache(func);				
				ps = con.prepareStatement(query);
				ResultSet rs = ps.executeQuery();
				while(rs.next())
				{
					if(s.length() == 0)				
						s =rs.getString("userCode");
					
					if(!s.equals(rs.getString("userCode")))
					{
						s = rs.getString("userCode");				
						rd.index+=1;					
					}
					
					rd.array[rd.index][0][0] = rs.getInt("ht");rd.words[rd.index][0] = "money";
					rd.array[rd.index][1][0] = rs.getInt("hg");rd.words[rd.index][1] = "money";
					rd.array[rd.index][2][0] = rs.getInt("ho");rd.words[rd.index][2] = "";
					rd.array[rd.index][3][0] = rs.getInt("nt");rd.words[rd.index][3] = "money";
					rd.array[rd.index][4][0] = rs.getInt("ng");rd.words[rd.index][4] = "money";
					rd.array[rd.index][5][0] = rs.getInt("no");rd.words[rd.index][5] = "";
					rd.array[rd.index][6][0] = rs.getInt("bt");rd.words[rd.index][6] = "money";
					rd.array[rd.index][7][0] = rs.getInt("bg");rd.words[rd.index][7] = "money";
					rd.array[rd.index][8][0] = rs.getInt("bo");rd.words[rd.index][8] = "";
					rd.names[rd.index] = s;
				}
							
				rs.close();
				ps.close();
			} else
			if (func.startsWith("_monthly_salary")) {
				String query = shared.cacheMan.loadQueryCache(func);				
				ps = con.prepareStatement(query);
				ResultSet rs = ps.executeQuery();
				while(rs.next())
				{
					if(s.length() == 0)				
						s =rs.getString("userCode");
					
					if(!s.equals(rs.getString("userCode")))
					{
						s = rs.getString("userCode");				
						rd.index+=1;					
					}
					
					rd.array[rd.index][0][0] = rs.getInt("month");rd.words[rd.index][0] = "";
					rd.array[rd.index][1][0] = rs.getInt("days");rd.words[rd.index][1] = "";
					rd.array[rd.index][2][0] = rs.getInt("daysIn");rd.words[rd.index][2] = "";
					rd.array[rd.index][3][0] = rs.getFloat("carMoney");rd.words[rd.index][3] = "money";
					rd.array[rd.index][4][0] = rs.getFloat("precentDay");rd.words[rd.index][4] = "precent";
					rd.array[rd.index][5][0] = rs.getInt("addTime");rd.words[rd.index][5] = "";
					rd.array[rd.index][6][0] = rs.getInt("freeTime");rd.words[rd.index][6] = "";
					rd.array[rd.index][7][0] = rs.getInt("patientTime");rd.words[rd.index][7] = "";
					rd.array[rd.index][8][0] = rs.getInt("totalTime");rd.words[rd.index][8] = "";
					rd.array[rd.index][9][0] = rs.getInt("passedTime");rd.words[rd.index][9] = "";
					rd.array[rd.index][10][0] = rs.getInt("deleteTime");rd.words[rd.index][10] = "";
					rd.array[rd.index][11][0] = rs.getInt("error");rd.words[rd.index][11] = "";
					rd.array[rd.index][12][0] = rs.getInt("nonRule");rd.words[rd.index][12] = "";
					rd.array[rd.index][13][0] = rs.getInt("totalRule");rd.words[rd.index][13] = "";
					rd.array[rd.index][14][0] = rs.getFloat("a1");rd.words[rd.index][14] = "precent";
					rd.array[rd.index][15][0] = rs.getFloat("a2");rd.words[rd.index][15] = "precent";
					rd.array[rd.index][16][0] = rs.getFloat("a3");rd.words[rd.index][16] = "precent";
					rd.array[rd.index][17][0] = rs.getFloat("a4");rd.words[rd.index][17] = "precent";
					rd.array[rd.index][18][0] = rs.getFloat("aTotal");rd.words[rd.index][18] = "precent";
					rd.array[rd.index][19][0] = rs.getFloat("finMoney");rd.words[rd.index][19] = "money";
					rd.words[rd.index][20] = rs.getString("financeSection");rd.words[rd.index][20] = "";
					rd.array[rd.index][21][0] = rs.getFloat("promoMoney");rd.words[rd.index][21] = "money";
					rd.array[rd.index][22][0] = rs.getFloat("promoPrecent");rd.words[rd.index][22] = "";
					rd.names[rd.index] = s;
				}
							
				rs.close();
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
				
		rds.put(func, rd);    	 
    }
	
	public HSSFSheet setReportData(String func, HSSFSheet sheet, HSSFWorkbook wb) {
		ReportData rd = rds.get(func);				
				

		if (func.startsWith("_bosgo_report")) {
			HSSFSheet sheet1 = wb.getSheetAt(1);				
			HSSFCellStyle cellCurrencyStyle = sheet1.getRow((short)0).getCell((short)0).getCellStyle();
			HSSFCellStyle cellPrecentStyle = sheet1.getRow((short)0).getCell((short)1).getCellStyle();
			HSSFCellStyle summaryCurrencyStyle = sheet1.getRow((short)0).getCell((short)2).getCellStyle();
			HSSFCellStyle summaryPrecentStyle = sheet1.getRow((short)0).getCell((short)3).getCellStyle();
			HSSFCellStyle cellStyle = sheet1.getRow((short)0).getCell((short)4).getCellStyle();
			HSSFCellStyle summaryStyle = sheet1.getRow((short)0).getCell((short)5).getCellStyle();
			
			int row_index = 0;
			for(int i=0; i <= rd.index; i++)
			{
				HSSFRow row = sheet.createRow(i+2);				
				row.createCell((short)0).setCellValue(getNameForCode("userCode", rd.names[i]));
				row.getCell((short)0).setCellStyle(cellStyle);
				int d=1;
				for(int j=0;j<9;j++)
				{					
					row.createCell((short) ((short)d));
					row.getCell((short) ((short)d)).setCellValue(rd.array[i][j][0]);
					row.getCell((short) ((short)d)).setCellStyle(cellStyle);
					if (rd.words[rd.index][j].equals("money"))						
						row.getCell((short)d).setCellStyle(cellCurrencyStyle);
					else 
					if (rd.words[rd.index][j].equals("precent"))									
						row.getCell((short)d).setCellStyle(cellPrecentStyle);
					
					d++;
				}
			}
			row_index += 2+rd.index+1;
			HSSFRow row = sheet.createRow(row_index);									
			int d=1;
			String [] fms = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
			String column = "";
			for(int j=0;j<9;j++) {	
				if (j < 26) column = fms[j+1];
				String columnMize = column+"2:"+(column+row_index);
				if (rd.words[rd.index][j].equals("money")) {
					row.createCell((short)d).setCellFormula("SUM("+columnMize+")");				
					row.getCell((short)d).setCellStyle(summaryCurrencyStyle);
				} else 
				if (rd.words[rd.index][j].equals("precent")) {
					row.createCell((short)d).setCellFormula("AVERAGE("+columnMize+")");				
					row.getCell((short)d).setCellStyle(summaryPrecentStyle);
				}
				d++;
			}
		} else 
		if (func.startsWith("_monthly_salary")) {
			HSSFSheet sheet1 = wb.getSheetAt(1);				
			HSSFCellStyle cellCurrencyStyle = sheet1.getRow((short)0).getCell((short)0).getCellStyle();
			HSSFCellStyle cellPrecentStyle = sheet1.getRow((short)0).getCell((short)1).getCellStyle();
			HSSFCellStyle summaryCurrencyStyle = sheet1.getRow((short)0).getCell((short)2).getCellStyle();
			HSSFCellStyle summaryPrecentStyle = sheet1.getRow((short)0).getCell((short)3).getCellStyle();
			HSSFCellStyle cellStyle = sheet1.getRow((short)0).getCell((short)4).getCellStyle();
			HSSFCellStyle summaryStyle = sheet1.getRow((short)0).getCell((short)5).getCellStyle();
			
			int row_index = 0;
			for(int i=0; i <= rd.index; i++)
			{
				int d=1;
				HSSFRow row = sheet.createRow(i+2);	
				row.createCell((short)0).setCellValue(getNameForCode("userCode", rd.names[i]));
				row.getCell((short)0).setCellStyle(cellStyle);
				for(int j = 0;j < 23; j++)
				{							
					row.createCell((short) ((short)d));
					/*if (rd.array[i][j][0] == 0 && rd.words[i][j] != null && rd.words[i][j].length() > 0)
						row.getCell((short) ((short)d)).setCellValue(rd.words[i][j]);
					else*/
						row.getCell((short) ((short)d)).setCellValue((double)rd.array[i][j][0]);			
					
					row.getCell((short) ((short)d)).setCellStyle(cellStyle);
					if (rd.words[rd.index][j].equals("money"))						
						row.getCell((short)d).setCellStyle(cellCurrencyStyle);
					else 
					if (rd.words[rd.index][j].equals("precent"))									
						row.getCell((short)d).setCellStyle(cellPrecentStyle);					
					d++;							
				}
			}
		

			row_index += 2+rd.index+1;
			HSSFRow row = sheet.createRow(row_index);									
			int d=1;
			String [] fms = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
			String column = "";
			for(int j=0;j<23;j++) {	
				if (j < 26) column = fms[j+1];
				String columnMize = column+"2:"+(column+row_index);
				if (rd.words[rd.index][j].equals("money")) {
					row.createCell((short)d).setCellFormula("SUM("+columnMize+")");				
					row.getCell((short)d).setCellStyle(summaryCurrencyStyle);
				} else 
				if (rd.words[rd.index][j].equals("precent")) {
					row.createCell((short)d).setCellFormula("AVERAGE("+columnMize+")");				
					row.getCell((short)d).setCellStyle(summaryPrecentStyle);
				}	
				d++;
			}
		} else
		if (func.equals("_kpi")) {						
			Collection a1 = getDataCollector("select avg(a3) as a3 from Evalution where 1=1 mnp ", "a3", "i");
			Collection a2 = getDataCollector("select COUNT(distinct productCode) yourA3 from Sales where 1=1 mnp ", "yourA3", "i");
			int a3 = a1.elementAt(0).getInt("a3");
			int ya3 = a2.elementAt(0).getInt("yourA3");
			
			HSSFRow row = sheet.getRow(31);
			row.getCell((short)9).setCellValue(a3);			
			row.getCell((short)15).setCellValue(a3==ya3?25:0);
			
			
			Collection a4 = getDataCollector("select sum(amountTheshold) as planAmount from B_Plan where name='2012-12' and productCode='1009' mnp ", "planAmount", "f");
			Collection a5 = getDataCollector("select sum(amount) as saleAmount  from Sales where amount>0 and _dateStamp>=(select max(startDate) from B_PLan where name='2012-12') and _dateStamp<=(select max(endDate) from B_PLan where name='2012-12') and productCode='1009' mnp ", "saleAmount", "f");
			float planAmount = a4.elementAt(0).getFloat("planAmount");
			float saleAmount = a5.elementAt(0).getFloat("saleAmount");
			row = sheet.getRow(33);
			row.getCell((short)9).setCellValue(planAmount);			
			row.getCell((short)15).setCellValue(saleAmount==planAmount?5:0);
			
			
			Collection a6 = getDataCollector("select sum(amountTheshold) as planAmount from B_Plan where name='2012-12' mnp ", "planAmount", "f");
			Collection a7 = getDataCollector("select sum(amount) as saleAmount  from Sales where amount>0 and _dateStamp>=(select max(startDate) from B_PLan where name='2012-12') and _dateStamp<=(select max(endDate) from B_PLan where name='2012-12') mnp ", "saleAmount", "f");
			float aplanAmount = a6.elementAt(0).getFloat("planAmount");
			float asaleAmount = a7.elementAt(0).getFloat("saleAmount");
			row = sheet.getRow(33);
			row.getCell((short)9).setCellValue(aplanAmount);			
			row.getCell((short)15).setCellValue(asaleAmount==aplanAmount?20:0);
			
			System.out.println(aplanAmount+" "+asaleAmount);
		}
		
		return sheet;
	}
	
	public String doSpecialXls(String func) {
		String output = "c:\\data\\reports\\report.xls";
		loadCodeAndNames();
		try {			
			String q = "c:/data/reports/"+func+".xls";
			InputStream myxls = new FileInputStream(q);		
			HSSFWorkbook wb = new HSSFWorkbook(myxls);			
			HSSFSheet sheet = wb.getSheetAt(0);
			
			HSSFCellStyle cellStyle = wb.createCellStyle();
			HSSFFont font = wb.createFont();	
			font.setFontName("Calibri");
			font.setFontHeightInPoints((short)10);
			cellStyle.setFont(font);
			
			HSSFDataFormat format = wb.createDataFormat();
			HSSFCellStyle summaryCellStyle = wb.createCellStyle();
			HSSFFont font1 = wb.createFont();
		    font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		    font1.setFontName("Calibri");
			font1.setFontHeightInPoints((short)10);
			summaryCellStyle.setFont(font1);
			summaryCellStyle.setDataFormat(format.getFormat("#,##0.00"));
			
			HSSFCellStyle cellCurrencyStyle = wb.createCellStyle();
			cellCurrencyStyle.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);			
			cellCurrencyStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cellCurrencyStyle.setFont(font);
			cellCurrencyStyle.setDataFormat(format.getFormat("#,##0.00"));
			
			getReportData(func);
			sheet = setReportData(func, sheet, wb);			
			
			FileOutputStream fileOut = new FileOutputStream(output);
		    wb.write(fileOut);
		    fileOut.close();	   		    
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return "";
	}
	
	@SuppressWarnings("deprecation")
	public String doXls(String sheetName, String fields, String types, String where, String json, int rindex, String wh) {
		String output = "c:\\data\\reports\\report.xls";
		loadCodeAndNames();
		try {						
			Collection collection = getCollection(fields, types, json);									
			int t = 0;
			String q = "c:/data/reports/templates.xls";
			if (sheetName.equals("User_Orders,_user_orders")) {
				t = 3;
				rindex = 3;
				q = "c:/data/reports/template_order.xls";
			}			
			
			sheetName = "Report";
			
			InputStream myxls = new FileInputStream(q);		
			HSSFWorkbook wb = new HSSFWorkbook(myxls);			
			HSSFSheet sheet = wb.getSheetAt(0);	
			wb.setSheetName(0, sheetName);
								
			HSSFSheet sheet1 = wb.getSheetAt(1);				
			HSSFCellStyle cellCurrencyStyle = sheet1.getRow((short)0).getCell((short)0).getCellStyle();
			HSSFCellStyle cellPrecentStyle = sheet1.getRow((short)0).getCell((short)1).getCellStyle();
			HSSFCellStyle summaryCurrencyStyle = sheet1.getRow((short)0).getCell((short)2).getCellStyle();
			HSSFCellStyle summaryPrecentStyle = sheet1.getRow((short)0).getCell((short)3).getCellStyle();
			HSSFCellStyle cellStyle = sheet1.getRow((short)0).getCell((short)4).getCellStyle();
			HSSFCellStyle summaryStyle = sheet1.getRow((short)0).getCell((short)5).getCellStyle();
									
						
			String [] fd = fields.split(",");
			String [] tp = types.split(",");
			String [] wp = where.split(",");
			
			int row_index = rindex;			
			int row_f_index = row_index+1;
			HSSFRow hrow = sheet.getRow((short) (row_index-1));
			HSSFCellStyle hellStyle = hrow.getCell((short)0).getCellStyle();			
			for (int i = 0; i < wp.length; i++) {
				String[] rh = wp[i].split(":");				
				hrow.createCell((short)i).setCellValue(toUTF8(rh[0]));
				hrow.getCell((short)i).setCellStyle(hellStyle);				
			}						
			
			for (int i = 0; i < collection.size(); i++) {
				Variant w = collection.elementAt(i);
				HSSFRow row = sheet.createRow((short) row_index);
				for (int j = 0; j < fd.length; j++) {								
					switch (tp[j].charAt(0)) {
						case 's': row.createCell((short)j).setCellValue(getNameForCode(fd[j], w.getString(fd[j])));								  
								  break;
						case 'd': row.createCell((short)j).setCellValue(w.getString(fd[j]));
						  		  break;
						case 'i': String r = getNameForCode(fd[j], w.getInt(fd[j])+"");								  
							      if (r.equals(w.getInt(fd[j])+""))
							    	  row.createCell((short)j).setCellValue(w.getInt(fd[j]));
							      else
							    	  row.createCell((short)j).setCellValue(r);
								  break;
						case 'f': row.createCell((short)j).setCellValue((double)w.getFloat(fd[j]));								  									  
						  		  break;
					}
					
					row.getCell((short)j).setCellStyle(cellStyle);
					
					if (isCurrencyField(fd[j]))
						  row.getCell((short)j).setCellStyle(cellCurrencyStyle);
					if (isPrecentField(fd[j]))
						  row.getCell((short)j).setCellStyle(cellPrecentStyle);
				}
								
				row_index++;
			}
			
			String [] fms = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
			//summary row
			HSSFRow row = sheet.createRow((short) row_index);
			for (int j = 0; j < fd.length; j++) {		
				String column = "";				
				if (j < 26) column = fms[j];
				
				if (j >= 26 && j<52) {
					int qq = j - 26;					
					column = "A"+fms[qq];					
				}
				
				if (j >= 52) {
					int qq = j - 52;					
					column = "B"+fms[qq];
				}
					
				String columnMize = column+row_f_index+":"+(column+row_index);				
				boolean summary = false;
				switch (tp[j].charAt(0)) {					
					case 'i': row.createCell((short)j).setCellFormula("SUM("+columnMize+")"); summary = true; break;						      
					case 'f': row.createCell((short)j).setCellFormula("SUM("+columnMize+")"); summary = true; break;
				}			
				
				if (summary) {
					if (isCurrencyField(fd[j]))
					  row.getCell((short)j).setCellStyle(summaryCurrencyStyle);
					else
					if (isPrecentField(fd[j]))
					  row.getCell((short)j).setCellStyle(summaryPrecentStyle);
					else
					  row.getCell((short)j).setCellStyle(summaryStyle);
				}
			}
			
			if (t == 3) {
				String[] params = wh.split(",");
				row = sheet.getRow((short) 0);
				row.getCell((short)1).setCellValue(params[0]+" "+getNameForCode("userCode", params[0]));
				row = sheet.getRow((short) 1);
				row.getCell((short)1).setCellValue(params[1]+" / "+params[2]);
				
				row_index+=3;
				row = sheet.createRow((short) row_index);
				
				row.createCell((short)1).setCellValue("Гарын үсэг /.................../");
			}
			
			for (int i = 0; i < wp.length; i++) {
				if (tp[i].charAt(0) == 's')
					sheet.autoSizeColumn((short)i);
				else
					sheet.setColumnWidth((short)i,(short)2500);
			}
						
			FileOutputStream fileOut = new FileOutputStream(output);
		    wb.write(fileOut);
		    fileOut.close();			    		   
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
						
		return output;
	}		
}
