/**
 * 
 */
package net.rugby.foundation.core.server;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

/**
 * @author home
 *
 */
@Singleton
public class FacebookChannelServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
	 
	static{
	httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	 
	public synchronized static String getHttpDate(Date date){
	return httpDateFormat.format(date);
	}
	 
	public synchronized static Date getDateFromHttpDate(String date) throws ParseException{
	return httpDateFormat.parse(date);
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
			
		resp.addHeader("Pragma", "public");
		resp.addHeader("Cache-Control", "max-age=31536000");
		Date now = new Date();
		resp.addHeader("Expires", getHttpDate(new Date(now.getTime() + 31536000)));
	
		resp.getWriter().print("<script src='//connect.facebook.net/en_US/all.js'></script>");
	}
}
