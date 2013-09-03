package net.rugby.foundation.core.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.LoginInfo;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
public class LoginRequiredServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private IExternalAuthticatorProviderFactory eapf;
	private IAccountManager am;

	private IAppUserFactory auf;

	@Inject
	public LoginRequiredServlet(IExternalAuthticatorProviderFactory eapf, IAccountManager am, IAppUserFactory auf) {
		this.eapf = eapf;
		this.am = am;
		this.auf = auf;
	}


	private static final Map<String, String> openIdProviders;
	static {
		openIdProviders = new HashMap<String, String>();
		openIdProviders.put("Google", "https://www.google.com/accounts/o8/id");
		openIdProviders.put("Yahoo", "yahoo.com");
		openIdProviders.put("MySpace", "myspace.com");
		openIdProviders.put("AOL", "aol.com");
		openIdProviders.put("MyOpenId.com", "myopenid.com");
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser(); // or req.getUserPrincipal()
		Set<String> attributes = new HashSet();

		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();

		if (user != null) {
			LoginInfo loginInfo = new LoginInfo();

			auf.setEmail(user.getEmail().toLowerCase());
			IAppUser u = auf.get();

			if (u == null) {
				resp.sendError(401, "Unauthorized");
			}

			HttpSession session = req.getSession();
			LoginInfo info = am.getLoginInfo(u);
			session.setAttribute("loginInfo", info);
			resp.sendRedirect("/Admin.html");
		} else {
			out.println("Sign in at: ");
			for (String providerName : openIdProviders.keySet()) {
				String providerUrl = openIdProviders.get(providerName);
				String loginUrl = userService.createLoginURL(req
						.getRequestURI(), null, providerUrl, attributes);
				out.println("[<a href=\"" + loginUrl + "\">" + providerName + "</a>] ");
			}
		}
	}


	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}



}
