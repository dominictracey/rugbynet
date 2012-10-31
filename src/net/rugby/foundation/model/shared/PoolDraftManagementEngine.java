package net.rugby.foundation.model.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.rugby.foundation.model.shared.Position.position;

public class PoolDraftManagementEngine implements ManagementEngine<PlayerRowData,DraftWizardState> {

	private ArrayList<PlayerRowData> selected;
	private ArrayList<PlayerRowData> options;
	private HashMap<position,Constraint<position,PlayerRowData>> positionConstraintMap = null;
	private HashMap<Long,Constraint<Long,PlayerRowData>> teamConstraintMap = null;
	private Constraint<Long,PlayerRowData> valueConstraint = null;
	private DraftWizardState state = null;
	private int draftSize;
	private String error = "";



	public String getError() {
		return error;
	}

	public PoolDraftManagementEngine() {
		
		positionConstraintMap = new HashMap<position,Constraint<position,PlayerRowData>>();
		teamConstraintMap = new HashMap<Long,Constraint<Long,PlayerRowData>>();
		
	}
	
	@Override
	public void initialize(ArrayList<PlayerRowData> selected,
			ArrayList<PlayerRowData> options, DraftWizardState state, int draftSize) {
		this.selected = selected;
		this.options = options;
		this.state = state;

	}

	@Override
	public boolean canAdd(PlayerRowData player) {
		error = "";
		if (checkPositionContraint(player))
			if (checkTeamConstraint(player))
				if (checkValueConstraint(player))
					return true;
		return false;
	}

	@Override
	public boolean checkPositionContraint(PlayerRowData player) {
		boolean retval = positionConstraintMap.get(player.getPosition()).canAdd(player, selected);
		if (!retval) {
			error = positionConstraintMap.get(player.getPosition()).getDetails();
		}
		return retval;
	}

	@Override
	public boolean checkTeamConstraint(PlayerRowData player) {
		boolean retval = teamConstraintMap.get(player.getTeamID()).canAdd(player, selected);
		if (!retval) {
			error = teamConstraintMap.get(player.getTeamID()).getDetails();
		}
		return retval;
	}

	@Override
	public boolean checkValueConstraint(PlayerRowData player) {
		boolean retval = valueConstraint.canAdd(player, selected);
		if (!retval) {
			error = valueConstraint.getDetails();
		}
		return retval;
	}

	@Override
	public boolean add(PlayerRowData player) {
		if (checkPositionContraint(player)) {
			if (checkTeamConstraint(player)) {
				if (checkValueConstraint(player)) {
					selected.add(player);
					int index = player.getTeamID().intValue()-1;
					int val = state.getTeamChosen(index);
					state.updateTeamChosen(index,++val);
					int index2 = player.getPosition().ordinal()-1;
					int val2 = state.getPositionChosen(index2);
					state.updatePositionChosen(index2, ++val2);
					state.setRemainingPoints(state.getRemainingPoints()-player.getOverallRating());

					if (selected.size() == draftSize)
						state.setCompleted(true);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean remove(PlayerRowData player) {
		int index = findIndex(player);
		if (index != -1) {
			selected.remove(index);
			int index3 = player.getTeamID().intValue()-1;
			int val = state.getTeamChosen(index3);
			state.updateTeamChosen(index3,--val);
			int index2 = player.getPosition().ordinal()-1;
			int val2 = state.getPositionChosen(index2);
			state.updatePositionChosen(index2, --val2);
			state.setRemainingPoints(state.getRemainingPoints()+player.getOverallRating());
			state.setCompleted(false);
			return true;
		} 
		
		return false;
	}

	@Override
	public ArrayList<PlayerRowData> getOptions(position pos) {
		ArrayList<PlayerRowData> posOptions = new ArrayList<PlayerRowData>();
		for (PlayerRowData p : options)
		{
			if (p.getPosition() == pos)
			{
				posOptions.add(p);
			}
		}
		return posOptions;
	}

	@Override
	public ArrayList<PlayerRowData> getSelected(position pos) {
		ArrayList<PlayerRowData> posSelected = new ArrayList<PlayerRowData>();
		for (PlayerRowData p : selected)
		{
			if (p.getPosition() == pos)
			{
				posSelected.add(p);
			}
		}
		return posSelected;
	}

	@Override
	public ArrayList<PlayerRowData> getSelected() {
		return selected;
	}

	@Override
	public int getNumSelectedTeam(Long teamID) {
		int count = 0;
		for (PlayerRowData p : selected)
		{
			if (p.getTeamID() == teamID)
			{
				count++;
			}
		}
		return count;
	}

	@Override
	public int getNumSelectedPosition(position pos) {
		int count = 0;
		for (PlayerRowData p : selected)
		{
			if (p.getPosition() == pos)
			{
				count++;
			}
		}
		return count;		
	}
	

	@Override
	public DraftWizardState getState() {

		assert state != null;
		
		return state;
	}

	@Override
	public void setState(DraftWizardState state) {

		this.state = state;
	}


	@Override
	public void addTeamConstraint(Long teamID, Constraint<Long, PlayerRowData> c) {
		teamConstraintMap.put(teamID, c);
		
	}

	@Override
	public void addPositionConstraint(position pos,
			Constraint<position, PlayerRowData> c) {
		positionConstraintMap.put(pos,c);
		
	}

	@Override
	public void setValueConstraint(Constraint<Long, PlayerRowData> c) {
		valueConstraint = c;
		
	}


	@Override
	public boolean isSelected(PlayerRowData player) {
		int i = findIndex(player);
		if (i == -1)
			return false;
		else
			return true;
		
	}

	private int findIndex(PlayerRowData player) {
		int index = 0;
		Long id = player.getId();
		Iterator<PlayerRowData> it = selected.iterator();
		while(it.hasNext()) {
			if (it.next().getId().equals(id))
				return index;
			index++;	
		}
		return -1;
	}
}
