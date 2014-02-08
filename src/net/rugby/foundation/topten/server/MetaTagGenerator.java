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
import net.rugby.foundation.core.server.factory.IContentFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MetaTagGenerator extends HttpServlet {


	private static final long serialVersionUID = 1L;


	private ITopTenListFactory ttlf;
	private IPlayerFactory pf;
	private ICachingFactory<IContent> ctf;


	private IContent details2Content;


	private String details2Div;

	@Inject
	public MetaTagGenerator(ITopTenListFactory ttlf, IPlayerFactory pf, ITeamGroupFactory tf, ICachingFactory<IContent> ctf) {
		this.ttlf = ttlf;
		this.pf = pf;
		this.ctf = ctf;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		parseHTML();
		
		// so we can have as parameters:
		//	1) none
		//	2) a listId
		//	3) a listId and playerId
		//
		// the development server query string parameter is ignored.
		//	Everything else is currently not valid and should toss a 404

		Long listId = null;
		Long playerId = null;
		ITopTenList ttl = null;
		IPlayer p = null; 
		ITopTenItem tti = null;
		int count = 0;
		int finalCount = 0;
		String keywords ="Rugby World Cup, RWC, rugby, rugby union, The Rugby Championship, Aviva Premiership, Six Nations, Super Rugby, ";
		String players = " Congratulations to this week's winners ";
		try {

			if (req.getParameter("listId") != null && !req.getParameter("listId").isEmpty()) {
				listId = Long.parseLong(req.getParameter("listId"));
				ttl = ttlf.get(listId);
			}

			if (req.getParameter("playerId") != null && !req.getParameter("playerId").isEmpty()) {
				playerId = Long.parseLong(req.getParameter("playerId"));
				p = pf.get(playerId);
			}
			
			if (ttl != null) {
				Iterator<ITopTenItem> it = ttl.getList().iterator();

				count = 0;
			
				ITopTenItem target;
				while (it.hasNext()) {
					target = it.next();
					keywords += target.getPlayer().getDisplayName() + ",";
					players += target.getPlayer().getDisplayName();
					if (it.hasNext()) {
						players += ", ";
					} else {
						players += " and the great runners up.";
					}
					if (!keywords.contains(target.getTeamName())) {
						keywords += target.getTeamName();
					}
					count++;
					if (target.getPlayerId().equals(playerId)) {
						tti = target;
						finalCount = count;
					}
				}
			}
			
		} catch (Throwable ex) {
			resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + "/404.html");
			return;
		}

		if (	(ttl == null && p == null) ||
				(ttl != null && p == null) ||
				(ttl != null && p != null)) {

			resp.setContentType("text/html");
			resp.getWriter().print(first);
			
			resp.getWriter().println("<meta name=\"keywords\" content=\"" + keywords + "\" />");
			
			if (tti != null) {
				String pattern = tti.getText().replaceAll("\\<[^>]*>","");
				resp.getWriter().println("<meta name=\"description\" content=\"" + pattern + "\" />");
				resp.getWriter().println("<meta property=\"og:description\" content=\"" + pattern + "\" />");
			} else if (ttl != null) {
				resp.getWriter().println("<meta name=\"description\" content=\"" + ttl.getContent() + "\" />");
				resp.getWriter().println("<meta property=\"og:description\" content=\"" + ttl.getContent() + "\" />");
			} else {
				resp.getWriter().println("<meta name=\"description\" content=\"" + defaultDescription + "\" />");
				resp.getWriter().println("<meta property=\"og:description\" content=\"" + defaultDescription + "\" />");
			}

			if (tti != null) {
				resp.getWriter().println("<meta property=\"og:image\" content=\"" + req.getScheme() + "://" + req.getServerName() + "/resources/" + tti.getTeamId() + "/200.png" + "\" />");
			} else {
				resp.getWriter().println("<meta property=\"og:image\" content=\"http://www.rugby.net/resources/logo200_crop.png\" />");				
			}



			if (p != null) {	
				resp.getWriter().println("<meta property=\"og:title\" content=\"#" + finalCount + ". " + p.getDisplayName() + " : " + ttl.getTitle() + "\" />");
				resp.getWriter().println("<title>#" + finalCount + ". " + p.getDisplayName() + " : " + ttl.getTitle() + "</title>");
			} else {
				if (ttl != null) {
					resp.getWriter().println("<meta property=\"og:title\" content=\"" + ttl.getTitle() + "\" />");
					resp.getWriter().println("<title>" + ttl.getTitle() + "</title>");
				} else {
					resp.getWriter().println("<meta property=\"og:title\" content=\"" + defaultTitle + "\" />");
					resp.getWriter().println("<title>" + defaultTitle + "</title>");
				}
			}
			
			String details2Div = getContent(players);
			
			resp.getWriter().print(third);
			resp.getWriter().print(details2Div);
			resp.getWriter().print(fifth);
		} else {
			resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + "/404.html");
			return;
		}
	}

	private String getContent(String players) {
		if (details2Content == null) {
			details2Content = ((IContentFactory)ctf).getForDiv("details2");
			
			if (details2Content != null) {
				details2Div = "<div id=\"details2\">" + details2Content.getBody().replaceFirst("<% players %>", players) + "</div>";
			}
		}
		
		return details2Div;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req,resp);
	}
	
	private void parseHTML() {
		if (first.isEmpty()) {
			FileInputStream inputStream = null;
		    try {
		    	inputStream = new FileInputStream("topten.html");
	
		        String everything = IOUtils.toString(inputStream);
		        
		        String[] chunks = everything.split("(<!-- split -->|<div id=\"split\"></div>)");
		        first = chunks[0];
		        //second = chunks[1];
		        third = chunks[2];
		        //fourth = chunks[3];
		        fifth = chunks[4];
		        
		        inputStream.close();
		    } catch (Throwable ex) {
		    	Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		    } 
		}
	}

	private String defaultTitle = "The Rugby Net's Top Ten Perfomances";
	private String defaultDescription = "Top ten lists of the best performances in the Rugby World Cup, The Rugby Championship, Six Nations, Heineken Cup, Aviva Premiership and Super Rugby.";

	private String first = "";
	//private String second = "";
	private String third = "";
	//private String fourth = "";
	private String fifth = "";
}
