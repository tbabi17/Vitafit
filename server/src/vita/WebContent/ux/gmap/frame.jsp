<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>

 <style>
  	body {
  		padding: 0;
  		margin: 0;
  		height: 100%;  		
  		width: 100%;
  		overflow:hidden;
  	}
  	
  	iframe {
  		width: 100%;
  		height: 100%;
  		scrolling: no;
  		frameborder: 0;
  		overflow-x:hidden;
		overflow-y:hidden;
  		border: 0;
  	}
  </style>
</head>
<body>
<iframe src="ux/gmap/map.html?xml=<%=request.getParameter("xml")%>" style="border: 0" width="100%" height="100%" frameborder="0" scrolling="no"></iframe>
</body>
</html>