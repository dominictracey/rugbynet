package net.rugby.foundation.model.shared;

import java.util.List;

public class CheckValueOfSelection implements Constraint<Long, PlayerRowData> {


	private Long value = 0L;
	
	public CheckValueOfSelection(Long maxpoints) {
		value = maxpoints;
	}

	@Override
	public void setParameter(Long val) {
		value = val;
		
	}

	@Override
	public boolean canAdd(PlayerRowData val, List<PlayerRowData> currentList) {
		Long check = 0L;
		for (PlayerRowData p : currentList) {
				check += p.getOverallRating();
		}
		
		// for teh Tenant
		return (check+val.getOverallRating())<=value;
	}


	@Override
	public void setMax(int max) {
		assert false;
		//not used - value only
	}

	@Override
	public void setMin(int min) {
		assert false;
		//not used - value only
	}

	@Override
	public boolean validate(List<PlayerRowData> currentList) {
		// no more than the max
		int check = 0;
		for (PlayerRowData p : currentList) {
			check += p.getOverallRating();
		}
		return check<value;
 
	}

	@Override
	public String getError() {
		return "Maximum team value exceeded.";
	}

	@Override
	public String getDetails() {
		return "All of your selected players may only have a combined rating of " + value + ".";

	}
}
