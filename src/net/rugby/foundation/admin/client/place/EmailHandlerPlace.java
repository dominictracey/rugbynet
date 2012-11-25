package net.rugby.foundation.admin.client.place;

import java.net.URLDecoder;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * A place object representing a particular state of the UI. A Place can be converted to and from a
 * URL history token by defining a {@link PlaceTokenizer} for each {@link Place}, and the 
 * {@link PlaceHistoryHandler} automatically updates the browser URL corresponding to each 
 * {@link Place} in your app.
 */
public class EmailHandlerPlace extends Place {
  
	private String token;
	private String seps = "&|=";
	private String promisedHandle;
	private String name;
	private String referringURL;
	
	public EmailHandlerPlace(String token) {
		this.token = token;
		
		String[] tok = token.split(seps);
		
		if (tok.length > 1) {
			assert (tok[0].equals("promisedHandle"));
			promisedHandle = URL.decode(tok[1]);	
		}
		
		if (tok.length > 3) {
			assert (tok[2].equals("name"));
			name = URL.decode(tok[3]);
		}
		
		if (tok.length > 5) {
			assert (tok[4].equals("referringURL"));
			referringURL = URL.decode(tok[5]);
		}
	}

	public String getToken() {
		token = "";
		token += "promisedHandle=";
		token += promisedHandle;
		token += "&name=";
		token += name;
		token += "&referringURL=";
		token += referringURL;
		
		return token;
	}
	
	public String getPromisedHandle() { return promisedHandle; }
	public String getName() { return name; }
	public String getreferringURL() { return referringURL; }

	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	@Prefix(value="HandleEmailPlace")
	public static class Tokenizer implements PlaceTokenizer<EmailHandlerPlace> {

		@Override
		public String getToken(EmailHandlerPlace place) {
			return place.getToken();
		}

		@Override
		public EmailHandlerPlace getPlace(String token) {
			return new EmailHandlerPlace(token);
		}

	}
}
