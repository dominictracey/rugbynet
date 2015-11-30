package net.rugby.foundation.model.shared;

import java.util.ArrayList;

import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.fantasy.Constraint;

public interface ManagementEngine<T,S> {
	

	void initialize(ArrayList<T> selected, ArrayList<T> options, S state, int draftSize);
	boolean canAdd(T player);
	boolean checkPositionContraint(T player);
	boolean checkTeamConstraint(T player);
	boolean checkValueConstraint(T player);
	boolean add(T player);
	boolean remove(T player);
	ArrayList<T> getOptions(position pos);
	ArrayList<T> getSelected(position pos);
	ArrayList<T> getSelected();
	boolean isSelected(T player);
	int getNumSelectedTeam(Long teamID);
	int getNumSelectedPosition(position pos);
	S getState();
	void setState(S state);
	void addTeamConstraint(Long teamID, Constraint<Long, T> c);
	void addPositionConstraint(position pos, Constraint<position, T> c);
	void setValueConstraint(Constraint<Long, T> c);
}
