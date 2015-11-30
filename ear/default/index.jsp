<% 
	if (request.getServerName().contains("127.0.0.1")) {
		response.sendRedirect("s/?gwt.codesvr=127.0.0.1:9997"); 
	} else {
		response.sendRedirect("s/"); 
	}
%>