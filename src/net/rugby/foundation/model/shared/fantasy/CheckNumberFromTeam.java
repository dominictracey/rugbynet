package net.rugby.foundation.model.shared.fantasy;

import java.util.List;

public class CheckNumberFromTeam implements Constraint<Long, PlayerRowData> {

	private Long teamID;
	private int max;
	@SuppressWarnings("unused")
	private int min;
	
	public CheckNumberFromTeam(Long id, int max) {
		teamID = id;
		this.max = max;
	}

	@Override
	public void setParameter(Long val) {
		teamID = val;
		
	}

	@Override
	public boolean canAdd(PlayerRowData val, List<PlayerRowData> currentList) {
		int check = 0;
		for (PlayerRowData p : currentList) {
			if (p.getTeamID() == teamID)
				check++;
		}
		return check<max;
	}


	@Override
	public void setMax(int max) {
		this.max = max;
		
	}

	@Override
	public void setMin(int min) {
		this.min = min;
		
	}

	@Override
	public boolean validate(List<PlayerRowData> currentList) {
		// no more than the max
		int check = 0;
		for (PlayerRowData p : currentList) {
			if (p.getTeamID() == teamID)
				check++;
		}
		return check<max;
 
	}

	@Override
	public String getError() {
		return "Too many from that team.";
	}

	@Override
	public String getDetails() {
		return "You may only have " + max + " players from any one team.";

	}
}
