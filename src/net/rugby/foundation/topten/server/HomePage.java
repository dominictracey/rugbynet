package net.rugby.foundation.topten.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.topten.server.staticpages.CompetitionListGenerator;
import net.rugby.foundation.topten.server.staticpages.PositionListGenerator;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class HomePage extends HttpServlet {


	private static final long serialVersionUID = 1L;
	private PositionListGenerator plg;
	private IConfigurationFactory ccf;
	private CompetitionListGenerator clg;



	@Inject
	public HomePage(PositionListGenerator plg, CompetitionListGenerator clg, IConfigurationFactory ccf) {
		this.plg = plg;
		this.clg = clg;
		this.ccf = ccf;
	}

	private final static String HOME_PAGE_CACHE_KEY = "HOMPAGECACHE-KEY";
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			// servlet handles naked domain

			// first see if we've cached this page already and return it if so

			String page = "";

			page = getFromCache(HOME_PAGE_CACHE_KEY);
			
			if (page == null || page.isEmpty()) {
				page = buildPage(req.getScheme(), req.getServerName());
				putToCache(HOME_PAGE_CACHE_KEY,page);
			}


			// write it out
			resp.setContentType("text/html");
			resp.getWriter().print(page);

		} catch (Throwable ex) {
			resp.sendRedirect(req.getScheme() + "://" + req.getServerName()
					+ ":" + req.getServerPort() //gmp Added to support running in Jetty in Eclipse
					+ "/404.html");
			
			//gmp: This does not log, and I think masks actual useful errors on the server side, not all due to
			//invalid URLs as would only be appropriate for 404 results.
			//TODO: Resolve this
			
			return;
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req,resp);
	}

	private void parseHTML() {
		//Temp hack 2 of 2, to not have to restart Jetty on teamTempalte.html changes
		//first = "";

		//if (first.isEmpty()) {
			FileInputStream inputStream = null;
			try {
				//Workaround for unreliable current directory when getting this file
				//File f = new File("home.html");
				//String absPath = f.getAbsolutePath(); ///Users/glennpicher/Desktop/rugby-dot-net/source/rugby/ear/default/teamTemplate.html
				//top path is sibling to WEB-INF folder
				
				inputStream = new FileInputStream("home.html");

				String everything = IOUtils.toString(inputStream);
				inputStream.close();
				
				//Cut out show by sections
			
				String htmlchunks1[] = everything.split("<!-- TRN SHOW BY BEGIN -->");
				first = htmlchunks1[0];
				String htmlchunks2[] = htmlchunks1[1].split("<!-- TRN SHOW BY END -->");
				
				
				first += plg.getForComp(ccf.get().getGlobalCompId());
				first += clg.get(true, false);
				first += clg.get(false, true);
				first += htmlchunks2[1];

				
			} catch (Throwable ex) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			} 
		//}
	}

	protected final String TEAM_PAGE_CACHE_PREFIX = "TPCP-";

	private String getFromCache(String teamSnakeCase) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			String page = null;

			value = (byte[])syncCache.get(TEAM_PAGE_CACHE_PREFIX+teamSnakeCase);
			if (value != null) {
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in;

				in = new ObjectInputStream(bis);

				Object obj = in.readObject();
				if (obj instanceof String) {
					page = (String)obj;
				}

				bis.close();
				in.close();
			}

			return page;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}

	private void putToCache(String teamSnakeCase, String page) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(page);
			byte[] yourBytes = bos.toByteArray(); 

			out.close();
			bos.close();

			syncCache.put(TEAM_PAGE_CACHE_PREFIX+teamSnakeCase, yourBytes);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);

		}

	}

	protected String buildPage(String scheme, String server)
	{
		StringBuilder page = new StringBuilder();
		parseHTML();
		page.append(first);


		return page.toString();
	}

	private String first = "";
	//private String third = "";
	//private String fifth = "";
}
