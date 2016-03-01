package net.rugby.foundation.core.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Handles OpenId/OAuth and FacebookConnect interactions
 *  
 * @author Dominic Tracey
 *
 * </p>
 */

@Singleton
public class SessionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private IExternalAuthticatorProviderFactory eapf;

	@Inject
	public SessionServlet(IExternalAuthticatorProviderFactory eapf) {
		this.eapf = eapf;
	}


	/** URL Format
	/*		/session/
	 * 
	 **/ 
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			HttpSession session = req.getSession();
			session.setAttribute("sessionStart", DateTime.now());	
		} catch (Throwable caught) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,caught.getLocalizedMessage(),caught);
		}

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}



}
