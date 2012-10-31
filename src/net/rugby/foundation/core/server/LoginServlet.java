package net.rugby.foundation.core.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.rugby.foundation.core.client.Identity;
import net.rugby.foundation.core.client.Identity.Actions;
import net.rugby.foundation.core.client.Identity.Keys;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.LoginInfo.ProviderType;
import net.rugby.foundation.model.shared.LoginInfo.Selector;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


//import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT;
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
