package net.rugby.foundation.model.shared;

import java.util.List;

import net.rugby.foundation.model.shared.Position.position;

public class CheckNumberAtPosition implements Constraint<position, PlayerRowData> {

	private position pos;
	private int max;
	private int min = 0;
	
	public CheckNumberAtPosition(position pos, int max) {
		this.pos = pos;
		this.max = max;
		this.min = max;
	}
	
	@Override
	public void setParameter(position val) {
		pos = val;
	}


	@Override
	public boolean canAdd(PlayerRowData val, List<PlayerRowData> currentList) {
		int check = 0;
		for (PlayerRowData p : currentList) {
			if (p.getPosition() == pos)
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
		// must have exactly the min
		int check = 0;
		for (PlayerRowData p : currentList) {
			if (p.getPosition() == pos)
				check++;
		}
		return check==min;
	}

	@Override
	public String getError() {
		return "Too many at " + pos.name().toLowerCase();
	}

	@Override
	public String getDetails() {
		return "You may only have " + max + " players in the " + pos.name().toLowerCase() + " position.";

	}

}
