package net.rugby.foundation.model.shared.fantasy;

import java.util.ArrayList;

import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.model.shared.Position.position;

//import com.google.gwt.place.shared.Place;

/*
 * ** The DWS contains all the information the View(s) need to present the UI to the user.
 */
public class DraftWizardState {

	private Long remainingPoints;
	private ArrayList<Integer> teamChosen;
	private ArrayList<Integer> positionChosen;
	private int step;
	private String instructions;
	private position pos;

	private boolean completed = false;
	
	public DraftWizardState(Long maxpoints, ArrayList<Integer> teamChosen, ArrayList<Integer> positionChosen,
			int step, String instructions, position pos) { 
		super();
		this.remainingPoints = maxpoints;
		this.teamChosen = teamChosen;
		this.positionChosen = positionChosen;
		this.step = step;
		this.instructions = instructions;
		this.pos = pos;
	}

	public Long getRemainingPoints() {
		return remainingPoints;
	}

	public void setRemainingPoints(Long remainingPoints) {
		this.remainingPoints = remainingPoints;
	}

	public ArrayList<Integer> getTeamChosen() {
		return teamChosen;
	}

	public void setTeamChosen(ArrayList<Integer> teamChosen) {
		this.teamChosen = teamChosen;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public position getPos() {
		return pos;
	}

	public void setPos(position pos) {
		this.pos = pos;
	}

	public int getTeamChosen(int index) {
		return teamChosen.get(index);
	}

	public void updateTeamChosen(int index, int val) {
		teamChosen.set(index, val);
		
	}

	public int getPositionChosen(int index2) {
		return positionChosen.get(index2);
	}

	public void updatePositionChosen(int index2, int i) {
		positionChosen.set(index2, i);
		
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	
	
}
