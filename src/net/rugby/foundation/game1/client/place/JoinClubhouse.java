package net.rugby.foundation.game1.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * A place object representing a particular state of the UI. A Place can be converted to and from a
 * URL history token by defining a {@link PlaceTokenizer} for each {@link Place}, and the 
 * {@link PlaceHistoryHandler} automatically updates the browser URL corresponding to each 
 * {@link Place} in your app.
 */
public class JoinClubhouse extends Place {
  
	private String seps = "\\,;";


	/**
	 * clubhouseId identifies current clubhouse
	 */
	private Long clubhouseId = 0L;
	
	
	/**
	 * 
	 * @param token
	 */
	public JoinClubhouse(String token) {
		String[] tok = token.split(seps);
		
		if (tok.length == 1) {

			if (checkInput(tok[0]))
				this.setClubhouseId(new Long(tok[0]));
			else
				this.setClubhouseId(null);
			
		}
	}

	/**
	 * @param string
	 * @return
	 */
	private boolean checkInput(String string) {
		if (string.matches("^(0|[1-9][0-9]*)$"))
			return true;
		else
			return false;
	}


	public Long getClubhouseId() {
		return clubhouseId;
	}

	public void setClubhouseId(Long clubhouseId) {
		this.clubhouseId = clubhouseId;
	}


	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	public static class Tokenizer implements PlaceTokenizer<JoinClubhouse> {

		@Override
		public String getToken(JoinClubhouse place) {
			String retval = "";
			if (place.getClubhouseId() != null)
				retval += place.getClubhouseId().toString();			
			return  retval;
		}

		@Override
		public JoinClubhouse getPlace(String token) {
			return new JoinClubhouse(token);
		}

	}
}
