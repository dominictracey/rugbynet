package net.rugby.foundation.topten.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IServerPlace;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SiteMap extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1564951734641736353L;
	private IPlaceFactory spf;
	private ICompetitionFactory cf;

	@Inject
	public SiteMap(IPlaceFactory spf, ICompetitionFactory cf) {
		this.spf = spf;
		this.cf = cf;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			// servlet handles /sitemap/* 
			
			// looks for competition abbr
			String chunks[] = req.getRequestURI().split("/");
			String comp = "";
			if (chunks.length > 2) {
				comp = chunks[2];
			}
			
			//have to specify a comp
			if (comp == null || comp.isEmpty()) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Must pass in a competition abbreviation.");
				resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + "/404.html");
				return;
			}

			// do we have this comp?
			List<ICompetition> comps = cf.getAllComps();
			
			ICompetition c = null;
			for (ICompetition b : comps) {
				if (b != null && b.getAbbr() != null && b.getAbbr().equals(comp)) {
					c = b;
					break;
				}
			}
			
			// have to send in a valid abbr
			if (c == null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Must pass in a VALID competition abbreviation.");
				resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + "/404.html");
				return;
			}
			
			// get all the server places for this comp
			List<IServerPlace> places = spf.getForCompId(c.getId());

			// write it out
			resp.setContentType("text/plain");
			StringBuilder sb = new StringBuilder();
			String base = "http://www.rugby.net/s/";
			for (IServerPlace sp : places) {
				sb.append(base + sp.getGuid()+"\n");
			}
			
			resp.getWriter().print(sb.toString());
			

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem generating sitemap.", ex);
			resp.sendRedirect(req.getScheme() + "://" + req.getServerName() + "/404.html");
			return;
		}
	}
	

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req,resp);
	}
}
