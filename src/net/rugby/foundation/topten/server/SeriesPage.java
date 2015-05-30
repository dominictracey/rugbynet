package net.rugby.foundation.topten.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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
public class SeriesPage extends HttpServlet {


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
	public SeriesPage(ICompetitionFactory cf, IRatingSeriesFactory rsf, IRatingGroupFactory rgf, ITopTenListFactory ttlf, IPlayerFactory pf, ITeamGroupFactory tf, IContentFactory ctf, IPlaceFactory plf) {
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
			// servlet handles /s/* 
			//
			// so we can have as parameters:
			//	1) none
			//	2) a name e.g. "/s/Richie-McCaw-on-Top-Ten-Flankers-in-The-Rugby-Championship-2014-Round-4"
			//	3) a place-guid e.g. "/s/Zc3f4fF90" 
			//
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
			// parse out the guid
			String chunks[] = req.getRequestURI().split("/");
			String guid = "";
			String page = "";
			if (chunks.length > 2) {
				guid = chunks[2];
			}

			page = getFromCache(guid);
			if (page == null || page.isEmpty()) {
				IServerPlace place = getPlace(req);
				page = buildPage(place, req.getScheme(), req.getServerName());
				putToCache(guid,page);
			}


			// write it out
			resp.setContentType("text/html");
			resp.getWriter().print(page);

		} catch (Throwable ex) {
			resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + "/404.html");
			return;
		}
	}

	private String getContent(String players, String description) {
		if (details2Content == null) {
			details2Content = ((IContentFactory)ctf).getForDiv("details2");

			if (details2Content != null) {
				details2Div = "<noscript>" + details2Content.getBody().replaceFirst("<% players %>", players) + "</noscript>\n<div id=\"details2\">";
			} else {
				details2Div = "<noscript>" + description + "</noscript>\n<div id=\"details2\">";
			}
		}

		return details2Div;
	}

	private IServerPlace getPlace(HttpServletRequest req) {
		// parse out the guid
		String chunks[] = req.getRequestURI().split("/");
		String guid = "";
		if (chunks.length > 2) {
			guid = chunks[2];
		}
		return plf.getForGuid(guid);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req,resp);
	}

	private void parseHTML() {
		if (first.isEmpty()) {
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream("template.html");

				String everything = IOUtils.toString(inputStream);

				String[] chunks = everything.split("(<!-- split -->|<div id=\"split\"></div>)");
				first = chunks[0];
				third = chunks[2];
				fifth = chunks[3];

				inputStream.close();
			} catch (Throwable ex) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			} 
		}
	}

	protected final String SERIES_PAGE_CACHE_PREFIX = "SPCP-";

	private String getFromCache(String guid) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			String page = null;

			value = (byte[])syncCache.get(SERIES_PAGE_CACHE_PREFIX+guid);
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

	private void putToCache(String guid, String page) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(page);
			byte[] yourBytes = bos.toByteArray(); 

			out.close();
			bos.close();

			syncCache.put(SERIES_PAGE_CACHE_PREFIX+guid, yourBytes);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);

		}

	}

	protected String buildPage(IServerPlace place, String scheme, String server)
	{
		StringBuilder page = new StringBuilder();

		// build it from scratch
		ICompetition comp = null;
		IRatingSeries series = null;
		IRatingGroup group = null;
		IRatingMatrix matrix = null;
		IRatingQuery query = null;
		ITopTenList list = null;
		ITopTenItem item = null;


		if (place != null) {
		//if (place.getType().equals(IServerPlace.PlaceType.SERIES) || place.getType().equals(IServerPlace.PlaceType.FEATURE)) {
			parseHTML();
			// populate
			if (place.getCompId() != null) {
				comp = cf.get(place.getCompId());
			}
			if (place.getSeriesId() != null) {
				series = rsf.get(place.getSeriesId());
			}
			if (place.getGroupId() != null) {
				group = rgf.get(place.getGroupId());
			}
			if (place.getListId() != null) {
				list = ttlf.get(place.getListId());
			}
			if (place.getItemId() != null) {
				for (ITopTenItem i : list.getList()) {
					if (i.getId().equals(place.getItemId())) {
						item = i;
						break;
					}
				}
			}
	//	}
		}
//
		String players = "";
		String description = ""; //"The Rugby Net Top Ten Lists \n";
		String title = ""; //"The Rugby Net Top Ten Lists \n";
		String keywords ="Rugby World Cup, RWC, rugby, rugby union, The Rugby Championship, Aviva Premiership, Six Nations, Super Rugby, ";
		if (list != null && list.getContent() != null && !list.getContent().contains("<p>\n</p>\n<p>\n</p>")) {
			description = list.getContent().replaceAll("\\<[^>]*>","");
			if (item != null) {
				title = item.getPlayer().getDisplayName() + " of " + item.getTeamName() + " is #" + item.getOrdinal() + " on " + list.getTitle();
			} else {
				title = list.getTitle();
			}
		} else {
			// not a feature - create a description 
			if (item != null && list != null) {
				title = item.getPlayer().getDisplayName() + " of " + item.getTeamName() + " is #" + item.getOrdinal() + " on " + list.getTitle();
			} else if (list != null) {
				title = list.getTitle();			
			} else if (series != null && group != null) {
				title = series.getDisplayName() + " " + group.getLabel();
			} else if (series != null && group != null) {
				title = series.getDisplayName() + " " + group.getLabel();
			} else if (series != null)  {
				title = series.getDisplayName();
			}
			
			description = "\n... \n";
			for (int i = 4; i < 7; ++i) {
				if (list != null && list.getList() != null && list.getList().get(i) != null) {
					description += "#" + i + " " + list.getList().get(i).getPlayer().getDisplayName() + " (" + list.getList().get(i).getTeamName() + ")\n";
				}
			}
			
			description += "\n...";
		}





		if (list != null) {
			Iterator<ITopTenItem> it = list.getList().iterator();
			ITopTenItem target;
			while (it.hasNext()) {
				target = it.next();
				if (!keywords.endsWith(", ")) {
					keywords += ", ";
				}
				keywords += target.getPlayer().getDisplayName();
				players += target.getPlayer().getDisplayName();
				if (it.hasNext()) {
					players += ", ";
					keywords += ", ";
				} 
				if (!keywords.contains(target.getTeamName())) {
					if (!keywords.endsWith(", ")) {
						keywords += ", ";
					}
					keywords += target.getTeamName();
				}
			}
		}


		//		if (	(ttl == null && p == null) ||
		//				(ttl != null && p == null) ||
		//				(ttl != null && p != null)) {


		page.append(first);

		page.append("<meta name=\"keywords\" content=\"" + keywords + "\" />\n");


		page.append("<meta name=\"description\" content=\"" + description.trim() + "\" />\n");
		page.append("<meta property=\"og:description\" content=\"" + description.trim() + "\" />\n");


		if (item != null) {
			ITeamGroup t = tf.get(item.getTeamId());
			String abbr = "NON";
			if (t != null) {
				abbr = t.getAbbr();
			}
			page.append("<meta property=\"og:image\" content=\"" + scheme + "://" + server + "/resources/teams/" + abbr + "/200.png" + "\" />\n");
		} else {
			if (comp != null) {
				page.append("<meta property=\"og:image\" content=\"http://www.rugby.net/resources/comps/" + comp.getAbbr() + "/200.png\" />\n");
			} else {
				page.append("<meta property=\"og:image\" content=\"http://www.rugby.net/resources/logo200_crop.png\" />\n");	
			}
		}



		page.append("<meta property=\"og:title\" content=\"The Rugby Net: " + title + "\" />\n");
		page.append("<title>The Rugby Net: " + title + "</title>\n");
		page.append("<meta property=\"og:url\" content=\"http://www.rugby.net/s/" + place.getGuid() +"\" />\n");


		page.append(third);
		
		// at the end
		String details2Div = getContent(players, description);
		page.append(details2Div);
		
		// the entry event
		String action = "none";
		String label = title;
		if (place.getCompId() == null) {
			action = "home";
		} else if (place.getType() == null || place.getType().equals(PlaceType.FEATURE)) {
			if (place.getItemId() == null) {
				action = "feature";
			} else {
				action = "fPlayer";
			}
		} else { // series
			if (place.getItemId() == null) {
				action = "series";
			} else {
				action = "sPlayer";
			}
		}
		
		page.append("\n<script>ganew('send', 'event', 'entry', '" + action + "', '" + label + "', 1);</script>\n");
		
		page.append(fifth);

		return page.toString();
	}

	private String first = "";
	private String third = "";
	private String fifth = "";
}
