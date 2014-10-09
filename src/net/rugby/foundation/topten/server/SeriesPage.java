package net.rugby.foundation.topten.server;

import java.io.FileInputStream;
import java.io.IOException;
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
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

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
	private ICachingFactory<IContent> ctf;
	private IRatingGroupFactory rgf;

	@Inject
	public SeriesPage(ICompetitionFactory cf, IRatingSeriesFactory rsf, IRatingGroupFactory rgf, ITopTenListFactory ttlf, IPlayerFactory pf, ITeamGroupFactory tf, ICachingFactory<IContent> ctf, IPlaceFactory plf) {
		this.cf = cf;
		this.rsf = rsf;
		this.rgf = rgf;
		this.ttlf = ttlf;
		this.pf = pf;
		this.ctf = ctf;
		this.plf = plf;
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
			if (false) {
				// @TODO ************
				// *************************
				//		page caching
				// *************************
			} else {
				// build it from scratch
				ICompetition comp = null;
				IRatingSeries series = null;
				IRatingGroup group = null;
				IRatingMatrix matrix = null;
				IRatingQuery query = null;
				ITopTenList list = null;
				ITopTenItem item = null;

				IServerPlace place = getPlace(req);
				if (place.getType().equals(IServerPlace.PlaceType.SERIES)) {
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
				}

				String players = "";
				String description = "The Rugby Net Top Ten Lists \n";
				String title = "The Rugby Net Top Ten Lists \n";
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
						description += item.getPlayer().getDisplayName() + " of " + item.getTeamName() + " is #" + item.getOrdinal() + " on " + list.getTitle();
					} else if (list != null) {
						description += list.getTitle();			
					} else if (series != null && group != null) {
						description += series.getDisplayName() + " " + group.getLabel();
					} else if (series != null && group != null) {
						description += series.getDisplayName() + " " + group.getLabel();
					} else if (series != null)  {
						description += series.getDisplayName();
					}
					title = description;
				}





				if (list != null) {
					Iterator<ITopTenItem> it = list.getList().iterator();
					ITopTenItem target;
					while (it.hasNext()) {
						target = it.next();
						keywords += target.getPlayer().getDisplayName() + ",";
						players += target.getPlayer().getDisplayName();
						if (it.hasNext()) {
							players += ", ";
						} 
						if (!keywords.contains(target.getTeamName())) {
							keywords += target.getTeamName();
						}
					}


					//		if (	(ttl == null && p == null) ||
					//				(ttl != null && p == null) ||
					//				(ttl != null && p != null)) {

					// write it out
					resp.setContentType("text/html");
					resp.getWriter().print(first);

					resp.getWriter().println("<meta name=\"keywords\" content=\"" + keywords + "\" />");

				
					resp.getWriter().println("<meta name=\"description\" content=\"" + description + "\" />");
					resp.getWriter().println("<meta property=\"og:description\" content=\"" + description + "\" />");
					

					if (item != null) {
						resp.getWriter().println("<meta property=\"og:image\" content=\"" + req.getScheme() + "://" + req.getServerName() + "/resources/" + item.getTeamId() + "/200.png" + "\" />");
					} else {
						resp.getWriter().println("<meta property=\"og:image\" content=\"http://www.rugby.net/resources/logo200_crop.png\" />");				
					}



					resp.getWriter().println("<meta property=\"og:title\" content=\"" + title + "\" />");
					resp.getWriter().println("<title>" + title + "</title>");
					

					String details2Div = getContent(players, description);

					resp.getWriter().print(third);
					resp.getWriter().print(details2Div);
					resp.getWriter().print(fifth);
				} else {
					resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + "/404.html");
					return;
				}
			}


		} catch (Throwable ex) {
			resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + "/404.html");
			return;
		}
	}

	private String getContent(String players, String description) {
		if (details2Content == null) {
			details2Content = ((IContentFactory)ctf).getForDiv("details2");

			if (details2Content != null) {
				details2Div = "<noscrip>" + details2Content.getBody().replaceFirst("<% players %>", players) + "</noscript>\n<div id=\"details2\">";
			} else {
				details2Div = "<noscript>" + description + "</noscript>\n<div id=\"details2\">";
			}
		}

		return details2Div;
	}

	private IServerPlace getPlace(HttpServletRequest req) {
		// parse out the guid
		String guid = req.getRequestURI().split("/")[2];
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
				fifth = chunks[4];

				inputStream.close();
			} catch (Throwable ex) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			} 
		}
	}

	private String first = "";
	private String third = "";
	private String fifth = "";
}
