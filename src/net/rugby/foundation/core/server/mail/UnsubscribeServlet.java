package net.rugby.foundation.core.server.mail;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IAppUser.EmailStatus;

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

	private IConfigurationFactory ccf;

	@Inject
	public UnsubscribeServlet(IAppUserFactory auf, IConfigurationFactory ccf) {
		this.auf = auf;
		this.ccf = ccf;
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
				String code = req.getParameter("optOutCode");
				IAppUser u = auf.get();
				
				resp.getWriter().println("<a href=\"http://www.rugby.net/\"><img width=\"57px\" alt=\"The Rugby Net\" src=\"http://www.rugby.net/icons/apple-touch-icon-57x57.png\" height=\"57px\" style=\"outline:0;text-decoration:none;border:0\"></a><br/>");
				
				if (u == null) {
					resp.getWriter().println("<h3>Unkown email address " + req.getParameter("email") + ". Unsubscribe failed. Try logging on to your account and checking the Opt Out box." );
				} else if ((code == null || code.isEmpty() || "null".equals(code)) && (u.getOptOutCode() == null || u.getOptOutCode().isEmpty())) {
					// legacy accounts don't need optOutCode
					unsubscribeUser(u);
					resp.getWriter().println(u.getEmailAddress() + " has been successfully unsubscribed. You can reactivate some or all of the email notifications on your profile page.");
				} else if (code != null && code.equals(u.getOptOutCode())) {
					// success
					unsubscribeUser(u);
					resp.getWriter().println(u.getEmailAddress() + " has been successfully unsubscribed. You can reactivate some or all of the email notifications on your profile page.");
				} else {
					// they didn't provide a code - hack attempt?
					resp.getWriter().println("Sorry, there was a problem unsubscribing the email address " + req.getParameter("email") + ". Please log on to the site and opt out using the Profile link or send us an email at info@rugby.net and we'll help you out. Sorry for the inconvenience.");

				}
			}
		} catch (Throwable caught) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,caught.getLocalizedMessage(),caught);
		}

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}


	private void unsubscribeUser(IAppUser u) {
		u.setOptOut(true);
		u.setEmailStatus(EmailStatus.OPTOUT);
		u.setOptedOut(new Date());
		auf.put(u);
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,"User " + u.getEmailAddress() + " has unsubscribed.");
		AdminEmailer ae = new AdminEmailer();
		ae.setMessage("User " + u.getEmailAddress() + " has unsubscribed.");
		ae.setSubject("User unsubscribe notice");
		ae.send();
	}

}
