package net.rugby.foundation.core.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private IExternalAuthticatorProviderFactory eapf;

	@Inject
	public LoginServlet(IExternalAuthticatorProviderFactory eapf) {
		this.eapf = eapf;
	}


	/** URL Format
	/*		/login/providerType/selector&destination=url_encoded_dest
	 * 
	 **/ 
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			IExternalAuthenticatorProvider provider = eapf.get(req);
			if (provider != null)
				provider.handleLogin(req, resp);
		} catch (Throwable caught) {
			Logger.getLogger("LoginServlet").log(Level.SEVERE,caught.getLocalizedMessage(),caught);
		}

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}



}
