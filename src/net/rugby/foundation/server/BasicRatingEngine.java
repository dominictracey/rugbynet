package net.rugby.foundation.server;

import java.util.Map;

public interface BasicRatingEngine<P,R> {
	void addRating(P p,R r);
	boolean calculate();
	Map<P,R> getRatings();
	
}
