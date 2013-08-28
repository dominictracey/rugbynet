package net.rugby.foundation.topten.server;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
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

	@Inject
	public MetaTagGenerator(ITopTenListFactory ttlf, IPlayerFactory pf, ITeamGroupFactory tf) {
		this.ttlf = ttlf;
		this.pf = pf;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

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
				p = pf.getById(playerId);
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
				resp.getWriter().println("<meta property=\"og:image\" content=\"http://www.rugby.net/resources/banner_150.jpg\" />");				
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
			
			resp.getWriter().print(second);
			resp.getWriter().print(players);
			resp.getWriter().print(third);
		} else {
			resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + "/404.html");
			return;
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req,resp);
	}

	private String defaultTitle = "Rugby Network Weekly Top Ten Perfomances";
	private String defaultDescription = "Weekly top ten lists of the best performances in the Rugby World Cup, The Rugby Championship, Six Nations, Aviva Premiership and Super Rugby.";
	private String first = "<!doctype html><html>  <head>" +
			"<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">" +
			"<script type=\"text/javascript\" language=\"javascript\" src=\"/topten/topten.nocache.js\"></script>" +
			"<script type=\"text/javascript\" language=\"javascript\" src=\"/core/core.nocache.js\"></script>"  +
			"<!-- Bootstrap --><link href=\"/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\" media=\"screen\">";
	private String second = "<meta property=\"og:type\" content=\"sport\" />" + 
			"<meta name=\"google-site-verification\" content=\"UlslUtViFtnEFDorsx8irllq-I5bqyXWLxAH9HeVbsI\" />" +
			"<!-- spiders -->" +
			"<META HTTP-EQUIV=\"CONTENT-LANGUAGE\" CONTENT=\"EN\">" +
			"<META NAME=\"revisit-after\" CONTENT=\"7 days\">" +
			"<META NAME=\"robots\" CONTENT=\"all\">" +
			"<META NAME=\"Author\" CONTENT=\"Rugby Network\">" +
			"<META NAME=\"Copyright\" CONTENT=\"Copyright 2013 RUGBY.NET LLC\">" +
			"<!-- Weekly top ten lists of the best performances in the Rugby World Cup, The Rugby Championship, Six Nations, Aviva Premiership and Super Rugby.  -->" +
			"<!-- Google Analytics -->" +
			"<script type=\"text/javascript\">" +
			"  var _gaq = _gaq || [];" +
			"  _gaq.push(['_setAccount', 'UA-2626751-1']);" +
			"  _gaq.push(['_setDomainName', 'rugby.net']);" +
			"  _gaq.push(['_trackPageview']);" +
			"" +
			"  (function() {" +
			"    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;" +
			"    ga.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'stats.g.doubleclick.net/dc.js';" +
			"    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);" +
			"  })();" +
			"</script>" +
			"<link href=\"/bootstrap/css/bootstrap-responsive.css\" rel=\"stylesheet\">" +
			"<link href=\"/stylesheets/push.css\" rel=\"stylesheet\">" +
			"<link href=\"/stylesheets/openid.css\" rel=\"stylesheet\">" +
			"</head>" +
			"<body>" +
			"<div id=\"wrap\">" +
			"<div id=\"navbar\"></div>" +
			"<div class=\"hero-unit\" id=\"hero\">" +
			"  <h3 id=\"heading\"></h3>" +
			"  <p id=\"details1\"></p>" +
			"  <p id=\"details2\">Check back every Monday for Top Ten Performances from global competitions. We have coverage for The Rugby Championship, Aviva Premiership, Six Nations, Super Rugby and Rugby World Cup - with others to follow! ";
private String third = " Please like us on Facebook and let us know which choices you agree with by liking below!</p>" +
			"  <div id=\"fbLike\"> </div>" +
			"</div>" +
			"<div class=\"span2\"></div>" +
			"<script async src=\"http://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\"></script>" +
			"<!-- top ten top banner -->" +
			"<ins class=\"adsbygoogle\"" +
			"     style=\"display:inline-block;width:728px;height:90px\"" +
			"     data-ad-client=\"ca-pub-9355939918394201\"" +
			"     data-ad-slot=\"1278737769\"></ins>" +
			"<script>" +
			"(adsbygoogle = window.adsbygoogle || []).push({});" +
			"</script>" +
			"<div id=\"app\" style=\"padding-top:20px\"></div>" +
			"<div id=\"fb-root\"></div>" +
			"<script>(function(d, s, id) {" +
			"  var js, fjs = d.getElementsByTagName(s)[0];" +
			"  if (d.getElementById(id)) return;" +
			"  js = d.createElement(s); js.id = id;" +
			"  js.src = \"//connect.facebook.net/en_US/all.js#xfbml=1&appId=499268570161982\";" +
			"  fjs.parentNode.insertBefore(js, fjs);" +
			"}(document, 'script', 'facebook-jssdk'));</script>" +
			"<div id=\"push\"></div></div>" +
			"	<div id=\"footer\">" +
			"<div class='navbar'>" +
			"<div class='navbar-inner'>" +
			"	<div class='container'>" +
			"		<ul  id='footerLinks' class='nav'>" +
			"			<li>" +
			"				<span class='nav'>&copy; 2011-2013 RUGBY.NET LLC</span>" +
			"			</li>" +
			"		</ul>" +
			"	</div>" +					
			"</div>" +
			"</div>" +
			"	</div>" +
			"   <iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" tabIndex='-1' style=\"position:absolute;width:0;height:0;border:0\"></iframe>" +
			"" +
			"  </body>" +
			"</html>";
}
