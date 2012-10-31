package net.rugby.foundation.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BasicRoundEnderEngine implements BasicRatingEngine<Long, Float> {

	private HashMap<Long, ArrayList<Float>> map = new HashMap<Long, ArrayList<Float>>();
	private HashMap<Long, Float> newMap = new HashMap<Long, Float>();
	private HashMap<Long, Float> lnMap = new HashMap<Long, Float>();
	
	Long count = 0L;
	
	@Override
	public void addRating(Long playerID, Float rating) {
		if (map.get(playerID) == null) 
			map.put(playerID,new ArrayList<Float>());
		
		map.get(playerID).add(rating);
		
	}

	@Override
	public boolean calculate() {
		
		// add them all up
		Float total = 0F;
		for (ArrayList<Float> r : map.values())  {
			if (r.size() > 0) {
				for (Float i : r) {
					total += i;
					count++;
				}
			}
		}
		
		// populate the results map with the sum of the player's ratings divided by the sum of all ratings * 500 * count of all ratings
		for (Long p : map.keySet())  {
			Float newRating  = 0F;

			// sum existing ratings
			ArrayList<Float> ratings = map.get(p);
			for (Float oldRating : ratings) {
				newRating += oldRating;
			}
			
			newRating = newRating/total;
			newRating = newRating*500L*count/map.get(p).size();
			
			newMap.put(p,  newRating);
		}
		
		for (Long p : newMap.keySet())  {
			Float ln = ((Double)Math.log(newMap.get(p)/100F)).floatValue();
			if (ln < 0F) {
				ln = 0F;
			}
			lnMap.put(p, ln);
		}
		
		//normalize
		Float lnTotal = 0F;
		for (Float p : lnMap.values())  {		
			lnTotal += p;
		}
		
		newMap.clear();
		
		for (Long p : lnMap.keySet())  {		
			newMap.put(p, lnMap.get(p)/lnTotal*500*lnMap.size());
		}
		
		return true;
	}

	@Override
	public Map<Long, Float> getRatings() {
		return newMap;
	}

}
