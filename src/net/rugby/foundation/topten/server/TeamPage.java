package net.rugby.foundation.topten.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import net.rugby.foundation.core.server.factory.ICachingFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IContentFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.IHasId;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.IServerPlace.PlaceType;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TeamPage extends HttpServlet {


	private static final long serialVersionUID = 1L;

	private IContent details2Content;
	private String details2Div;

	private IPlaceFactory plf;
	private ICompetitionFactory cf;
	private IRatingSeriesFactory rsf;
	private ITopTenListFactory ttlf;
	private IPlayerFactory pf;
	private IContentFactory ctf;
	private IRatingGroupFactory rgf;

	private ITeamGroupFactory tf;

	@Inject
	public TeamPage(ICompetitionFactory cf, IRatingSeriesFactory rsf, IRatingGroupFactory rgf, ITopTenListFactory ttlf, IPlayerFactory pf, ITeamGroupFactory tf, IContentFactory ctf, IPlaceFactory plf) {
		this.cf = cf;
		this.rsf = rsf;
		this.rgf = rgf;
		this.ttlf = ttlf;
		this.pf = pf;
		this.ctf = ctf;
		this.plf = plf;
		this.tf = tf;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			// servlet handles /teams/* 
			// 
			// so we can have as parameters... 
			//	1) none
			//	2) a name e.g. "/teams/(long name of the team from the database -- t.getLongName() snake_case (force uppper to lower);
			// some sort of map on the fly of snake_case to actual team names.. make new column in the datbase? populate if not there ("self cleaning oven")
			//net.rugby.foundation.core.server.factory.BaseTeamGroupFactory - add getFromSnakeCase()  - factory return a team group object ITeamGroup
			//Carousels will build snake_case if needed when making links for team icons
			
			
			//Richie-McCaw-on-Top-Ten-Flankers-in-The-Rugby-Championship-2014-Round-4"
			//	3) a place-guid e.g. "/teams/Zc3f4fF90" 
			//
			// gmp: the rest of this comment below refers to the older code...
			// TODO: update comment
			
			// the first redirects to /f/ - the default feature page 
			// the second two go to the SeriesPlace table and pull out the matched place
			//
			// two tasks here:
			//		1) Fetch necessary elements to build meta tags for <head> and <noscript>
			//		2) Set up hash fragment for correct place (can we do this? prolly not)
			//		3) write out the reference to the GWT javascript to trigger PlaceParserActivity start
			//
			// the development server query string parameter is ignored.
			//	Everything else is currently not valid and should toss a 404

			// first see if we've cached this page already and return it if so
			// parse out the team name
			String chunks[] = req.getRequestURI().split("/");
			String teamSnakeCase = "";
			String page = "";
			if (chunks.length > 2) {
				teamSnakeCase = chunks[2];
			}

			page = getFromCache(teamSnakeCase);
			if (page == null || page.isEmpty()) {
				page = buildPage(req.getScheme(), req.getServerName());
				putToCache(teamSnakeCase,page);
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
		if (first.isEmpty()) {
			FileInputStream inputStream = null;
			try {
				//Workaround for unreliable current directory when getting this file
				File f = new File("teamTemplate.html");
				String absPath = f.getAbsolutePath(); ///Users/glennpicher/Desktop/rugby-dot-net/source/rugby/ear/default/teamTemplate.html
				//top path is sibling to WEB-INF folder
				
				inputStream = new FileInputStream(absPath);

				String everything = IOUtils.toString(inputStream);
				inputStream.close();
				
				//Cut out mobile and desktop nav
				/*
				String htmlchunks1[] = everything.split("<!-- trn mobile nav -->");
				String beginfirst = htmlchunks1[0];
				String htmlchunks2[] = htmlchunks1[1].split("<!-- trn mobile nav end -->");
				String htmlchunks3[] = htmlchunks2[1].split("<!-- trn desktop nav -->");
				String middlefirst = htmlchunks3[0];
				String htmlchunks4[] = htmlchunks3[1].split("<!-- trn desktop nav end -->");
				String endfirst = htmlchunks4[1];
				
				TopNavGenerator tn = new TopNavGenerator();
				String topNav = tn.getContent();
				SideNavGenerator sn = new SideNavGenerator();
				String sideNav = sn.getContent();
				
				first = beginfirst + topNav +  middlefirst + sideNav + endfirst;
				*/
				
				//No server side processing used after all, at least not yet.
				first = everything;
				
			} catch (Throwable ex) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			} 
		}
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
