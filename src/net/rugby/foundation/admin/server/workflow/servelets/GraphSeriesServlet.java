package net.rugby.foundation.admin.server.workflow.servelets;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.topten.server.factory.IRoundNodeFactory;
import net.rugby.foundation.topten.server.rest.RoundNode;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Parameter:compId to create data for
 * Parameter:action as either [create|delete]
 */
@Singleton
public class GraphSeriesServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2415381790205617886L;
	private IRoundNodeFactory rnf;
	private ICompetitionFactory cf;


	@Inject
	public GraphSeriesServlet(ICompetitionFactory cf, IRoundNodeFactory rnf) {
		this.rnf = rnf;
		this.cf = cf;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {
			DateTime timestamp = new DateTime();
			resp.setContentType("text/html");
			String sCompId = req.getParameter("compId");
			Long compId = Long.parseLong(sCompId);
			if (compId != null) {
				ICompetition c = cf.get(compId);
				resp.getWriter().println("<h3>Creating graph components for " + c.getLongName() + "</h3>");
				resp.getWriter().println("<code>" + timestamp.toString() + "</code><hr/>");


				if (req.getParameter("action").equals("create")) {
					for (int i=0; i<10; ++i) {
						List<RoundNode> rns = rnf.get(compId,i);
						resp.getWriter().println("Success creating " + rns.size() + " " + Position.position.getAt(i+1).getPlural() + "...<br>");
					}
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Completed creation of graphing data<br>");
				} else if (req.getParameter("action").equals("delete")) {
					for (int i=0; i<10; ++i) {
						List<RoundNode> rns = rnf.get(compId,i);
						resp.getWriter().println("Deleting " + rns.size() + " rounds of " + Position.position.getAt(i).getPlural() + " data...<br>");
						for (RoundNode rn: rns) {
							rnf.delete(rn);
						}
						rnf.dropListFromCache(compId,i);
					}

					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Successful deletion of graphing data.");
					resp.getWriter().println("Successful deletion of graphing data<br>");
				} else {
					resp.getWriter().println( "Unrecognized or missing action parameter<hr>");
					showUsage(resp);
				}

			} else {
				resp.getWriter().println("<p>must provide compId GET parameter</p><hr>");
				showUsage(resp);
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Bad news", ex);
			resp.getWriter().println( "FAILURE: " + ex.toString());
		}
	}

	private void showUsage(HttpServletResponse resp) throws IOException {
		resp.getWriter().println("Invalid usage.</br> try /admin/workflow/graph?compId=xxxxxx&action=[create|delete]");

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req,resp);
	}
}
