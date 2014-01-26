package net.rugby.foundation.game1.client.place;

import net.rugby.foundation.core.shared.IdentityTypes;
import net.rugby.foundation.core.shared.IdentityTypes.Actions;
import net.rugby.foundation.core.shared.IdentityTypes.Keys;
import net.rugby.foundation.model.shared.LoginInfo;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * A place object representing a particular state of the UI. A Place can be converted to and from a
 * URL history token by defining a {@link PlaceTokenizer} for each {@link Place}, and the 
 * {@link PlaceHistoryHandler} automatically updates the browser URL corresponding to each 
 * {@link Place} in your app.
 */
public class Profile extends Place {


	// URL scheme: #Profile:'action='action&'providerType='providerType'&selector=]selector'&destination'=urlencodedDest
	private String seps = "[=&]";

	private Actions action = null;
	private LoginInfo.ProviderType providerType = null;
	private LoginInfo.Selector selector = null;
	private String destination = null;

	/**
	 * 
	 * @param token
	 */
	public Profile(String token) {

		String[] tok = token.split(seps);

		for (int i=0; i<tok.length; i+=2) {

			if (tok[i].equals(Keys.action.toString())) {
				if (tok.length >= i+1)
					setAction(Actions.valueOf(tok[i+1]));
			} else if (tok[i].equals(Keys.providerType.toString())) {
				if (tok.length >= i+1)
					setProviderType(IdentityTypes.getProviderType(tok[i+1])); 
			} else if (tok[i].equals(Keys.selector.toString())) {
				if (tok.length >= i+1)
					setSelector(IdentityTypes.getSelector(tok[i+1]));
			} 
			else if (tok[i].equals(Keys.destination.toString())) {
				if (tok.length >= i+1)
					setDestination(URL.decode(tok[i+1]));
			}

		}
	}


	public Profile(Actions action, LoginInfo.ProviderType providerType,
			LoginInfo.Selector selector, String destination) {
		super();
		this.action = action;
		this.providerType = providerType;
		this.selector = selector;
		this.destination = destination;
	}

	public Actions getAction() {
		return action;
	}


	public void setAction(Actions action) {
		this.action = action;
	}


	public LoginInfo.Selector getSelector() {
		return selector;
	}


	public void setSelector(LoginInfo.Selector selector2) {
		this.selector = selector2;
	}


	public String getDestination() {
		return destination;
	}


	public void setDestination(String destination) {
		this.destination = destination;
	}


	public LoginInfo.ProviderType getProviderType() {
		return providerType;
	}


	public void setProviderType(LoginInfo.ProviderType providerType) {
		this.providerType = providerType;
	}


	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	public static class Tokenizer implements PlaceTokenizer<Profile> {

		@Override
		public String getToken(Profile place) {

			String retval = ""; 

			if (place.getAction() != null) {
				retval += Keys.action.toString() + "=" + place.getAction().toString();	
			}
			if (place.getProviderType() != null) {
				retval += "&" + Keys.providerType.toString() + "=" + place.getProviderType().toString();
			}
			if (place.getSelector() != null) {
				retval += "&" + Keys.selector.toString() + "=" + place.getSelector().toString();					
			}

			if (place.getDestination() != null) {
				retval += "&" + Keys.destination.toString() + "=" + URL.encode(place.getDestination());			
			}
			return  retval;
		}

		@Override
		public Profile getPlace(String token) {
			return new Profile(token);
		}

	}
}
