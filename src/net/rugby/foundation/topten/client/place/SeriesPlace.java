package net.rugby.foundation.topten.client.place;

import net.rugby.foundation.model.shared.Criteria;

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
public class SeriesPlace extends Place {

	private String token;
	private String seps = "&|=";
	private Long seriesId = null;
	private Long matrixId = null;
	private Long groupId = null;
	private Long queryId = null;
	private Long compId = null;
	private Long playerId = null;

	public SeriesPlace(String token) {
		try {
			this.token = token;

			String[] tok = token.split(seps);

			if (tok.length > 1) {
				if (tok[0].equals("s")) {
					String sseriesId = URL.decode(tok[1]);
					seriesId = Long.parseLong(sseriesId);
				} else if (tok[0].equals("m")) {
//					String Scriteria = URL.decode(tok[1]);
//					criteria = Criteria.valueOf(Scriteria);
					String str = URL.decode(tok[1]);
					matrixId = Long.parseLong(str);
				}  else if (tok[0].equals("g")) {
					String sgroupId = URL.decode(tok[1]);
					groupId = Long.parseLong(sgroupId);
				}   else if (tok[0].equals("q")) {
					String squeryId = URL.decode(tok[1]);
					queryId = Long.parseLong(squeryId);
				}   else if (tok[0].equals("c")) {
					String str = URL.decode(tok[1]);
					compId = Long.parseLong(str);
				}   else if (tok[0].equals("p")) {
					String str = URL.decode(tok[1]);
					playerId = Long.parseLong(str);
				}
			}

			if (tok.length > 3) {
				if (tok[2].equals("s")) {
					String sseriesId = URL.decode(tok[3]);
					seriesId = Long.parseLong(sseriesId);
				} else if (tok[2].equals("m")) {
					String str = URL.decode(tok[3]);
					matrixId = Long.parseLong(str);
				}  else if (tok[2].equals("g")) {
					String sgroupId = URL.decode(tok[3]);
					groupId = Long.parseLong(sgroupId);
				}   else if (tok[2].equals("q")) {
					String squeryId = URL.decode(tok[3]);
					queryId = Long.parseLong(squeryId);
				}   else if (tok[2].equals("c")) {
					String str = URL.decode(tok[3]);
					compId = Long.parseLong(str);
				}   else if (tok[2].equals("p")) {
					String str = URL.decode(tok[3]);
					playerId = Long.parseLong(str);
				}
			}

			if (tok.length > 5) {
				if (tok[4].equals("s")) {
					String sseriesId = URL.decode(tok[5]);
					seriesId = Long.parseLong(sseriesId);
				} else if (tok[4].equals("m")) {
					String str = URL.decode(tok[5]);
					matrixId = Long.parseLong(str);
				}  else if (tok[4].equals("g")) {
					String sgroupId = URL.decode(tok[5]);
					groupId = Long.parseLong(sgroupId);
				}   else if (tok[4].equals("q")) {
					String squeryId = URL.decode(tok[5]);
					queryId = Long.parseLong(squeryId);
				}   else if (tok[4].equals("c")) {
					String str = URL.decode(tok[5]);
					compId = Long.parseLong(str);
				}   else if (tok[4].equals("p")) {
					String str = URL.decode(tok[5]);
					playerId = Long.parseLong(str);
				}
			}
			
			if (tok.length > 7) {
				if (tok[6].equals("s")) {
					String sseriesId = URL.decode(tok[7]);
					seriesId = Long.parseLong(sseriesId);
				} else if (tok[6].equals("m")) {
					String str = URL.decode(tok[7]);
					matrixId = Long.parseLong(str);
				}  else if (tok[6].equals("g")) {
					String sgroupId = URL.decode(tok[7]);
					groupId = Long.parseLong(sgroupId);
				}   else if (tok[6].equals("q")) {
					String squeryId = URL.decode(tok[7]);
					queryId = Long.parseLong(squeryId);
				}   else if (tok[6].equals("c")) {
					String str = URL.decode(tok[7]);
					compId = Long.parseLong(str);
				}   else if (tok[6].equals("p")) {
					String str = URL.decode(tok[7]);
					playerId = Long.parseLong(str);
				}
			}
			
			if (tok.length > 9) {
				if (tok[8].equals("s")) {
					String sseriesId = URL.decode(tok[9]);
					seriesId = Long.parseLong(sseriesId);
				} else if (tok[8].equals("m")) {
					String str = URL.decode(tok[9]);
					matrixId = Long.parseLong(str);
				}  else if (tok[8].equals("g")) {
					String sgroupId = URL.decode(tok[9]);
					groupId = Long.parseLong(sgroupId);
				}   else if (tok[8].equals("q")) {
					String squeryId = URL.decode(tok[9]);
					queryId = Long.parseLong(squeryId);
				}   else if (tok[8].equals("c")) {
					String str = URL.decode(tok[9]);
					compId = Long.parseLong(str);
				}   else if (tok[8].equals("p")) {
					String str = URL.decode(tok[9]);
					playerId = Long.parseLong(str);
				}
			}
			
			if (tok.length > 11) {
				if (tok[10].equals("s")) {
					String sseriesId = URL.decode(tok[11]);
					seriesId = Long.parseLong(sseriesId);
				} else if (tok[10].equals("m")) {
					String str = URL.decode(tok[11]);
					matrixId = Long.parseLong(str);
				}  else if (tok[10].equals("g")) {
					String sgroupId = URL.decode(tok[11]);
					groupId = Long.parseLong(sgroupId);
				}   else if (tok[10].equals("q")) {
					String squeryId = URL.decode(tok[11]);
					queryId = Long.parseLong(squeryId);
				}   else if (tok[10].equals("c")) {
					String str = URL.decode(tok[11]);
					compId = Long.parseLong(str);
				}   else if (tok[10].equals("p")) {
					String str = URL.decode(tok[11]);
					playerId = Long.parseLong(str);
				}
			}
			
		} catch (Throwable ex) {
				// just ignore hack attempts...
			}
		}


	/***
	 * 
	 * @param compId
	 * @param seriesId
	 * @param groupId
	 * @param matrixId
	 * @param queryId
	 * @param playerId
	 */
		public SeriesPlace(Long compId, Long seriesId, Long groupId, Long matrixId, Long queryId, Long playerId) {
			this.seriesId = seriesId;
			this.matrixId = matrixId;
			this.groupId = groupId;
			this.queryId = queryId;
			this.compId = compId;
			this.playerId = playerId;
		}


		public SeriesPlace() {

		}


		public String getToken() {
			token = "";
			boolean started = false;
			
			if (!(compId == null)) {
				token += "c=";
				token += compId;
				started = true;
			}
			
			if (!(seriesId == null)) {
				if (started) {
					token += "&";
				} else {
					started = true;
				}
				token += "s=";
				token += seriesId;
			}

			if (!(groupId == null)) {
				if (started) {
					token += "&";
				} else {
					started = true;
				}
				token += "g=";
				token += groupId;
			}
			
			if (!(matrixId == null)) {
				if (started) {
					token += "&";
				} else {
					started = true;
				}
				token += "m=";
				token += matrixId;
			}
			
			if (!(queryId == null)) {
				if (started) {
					token += "&";
				} else {
					started = true;
				}
				token += "q=";
				token += queryId;
			}
			
			if (!(playerId == null)) {
				if (started) {
					token += "&";
				} else {
					started = true;
				}
				token += "p=";
				token += playerId;
			}

			return token;
		}

		/**
		 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
		 */
		@Prefix(value="TX")
		public static class Tokenizer implements PlaceTokenizer<SeriesPlace> {

			@Override
			public String getToken(SeriesPlace place) {
				return place.getToken();
			}

			@Override
			public SeriesPlace getPlace(String token) {
				return new SeriesPlace(token);
			}

		}

		public Long getSeriesId() {
			return seriesId;
		}


		public Long getMatrixId() {
			return matrixId;
		}


		public Long getGroupId() {
			return groupId;
		}


		public Long getQueryId() {
			return queryId;
		}


		public Long getCompId() {
			return compId;
		}


		public void setCompId(Long compId) {
			this.compId = compId;
		}


		public Long getPlayerId() {
			return playerId;
		}


		public void setSeriesId(Long seriesId) {
			this.seriesId = seriesId;
		}


		public void setMatrixId(Long matrixId) {
			this.matrixId = matrixId;
		}


		public void setGroupId(Long groupId) {
			this.groupId = groupId;
		}


		public void setQueryId(Long queryId) {
			this.queryId = queryId;
		}


		public void setPlayerId(Long playerId) {
			this.playerId = playerId;
		}
	}
