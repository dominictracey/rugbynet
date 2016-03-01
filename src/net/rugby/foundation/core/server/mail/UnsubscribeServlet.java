package net.rugby.foundation.core.server.mail;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IAppUser.EmailStatus;

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
public class UnsubscribeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private IAppUserFactory auf;

	@Inject
	public UnsubscribeServlet(IAppUserFactory auf) {
		this.auf = auf;
	}


	/** URL Format
	/*		/session/
	 * 
	 **/ 
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			if (req.getParameter("email") != null) {
				auf.setEmail(req.getParameter("email"));
				IAppUser u = auf.get();
				u.setOptOut(true);
				u.setEmailStatus(EmailStatus.OPTOUT);
				auf.put(u);
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,"User " + u.getEmailAddress() + " has unsubscribed.");
				AdminEmailer ae = new AdminEmailer();
				ae.setMessage("User " + u.getEmailAddress() + " has unsubscribed.");
				ae.setSubject("User unsubscribe notice");
				ae.send();
			}
		} catch (Throwable caught) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,caught.getLocalizedMessage(),caught);
		}

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}



}
