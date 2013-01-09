package net.rugby.foundation.client.place;

import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.Stage.stageType;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class Manage extends Place
{
	// ROOT + MY + stage + round + step + edit
//	public enum stageType { POOL, KNOCKOUT }
	public enum step { 
		START("Let's get started. You'll pick 30 players to carry you through the pool stages. Click next to pick your props.",0,0,0,0), 
		PROP("Pick 4 props and click next", CoreConfiguration.getNumPropsPoolRoster(), CoreConfiguration.getNumPropsPoolRound(),CoreConfiguration.getNumPropsKnockoutRoster(), CoreConfiguration.getNumPropsKnockoutRound()),
		HOOKER("Pick 2 hookers and click next", CoreConfiguration.getNumHookersPoolRoster(), CoreConfiguration.getNumHookersPoolRound(),CoreConfiguration.getNumHookersKnockoutRoster(), CoreConfiguration.getNumHookersKnockoutRound()),
		LOCK("Pick 4 locks and click next", CoreConfiguration.getNumLocksPoolRoster(), CoreConfiguration.getNumLocksPoolRound(),CoreConfiguration.getNumLocksKnockoutRoster(), CoreConfiguration.getNumLocksKnockoutRound()), 
		FLANKER("Pick 4 flankers and click next", CoreConfiguration.getNumFlankersPoolRoster(), CoreConfiguration.getNumFlankersPoolRound(),CoreConfiguration.getNumFlankersKnockoutRoster(), CoreConfiguration.getNumFlankersKnockoutRound()), 
		NUMBER8("Pick 2 Number 8s and click next", CoreConfiguration.getNumNumber8sPoolRoster(), CoreConfiguration.getNumNumber8sPoolRound(),CoreConfiguration.getNumNumber8sKnockoutRoster(), CoreConfiguration.getNumNumber8sKnockoutRound()),
		SCRUMHALF("Pick 2 scrumhalves and click next", CoreConfiguration.getNumScrumhalvesPoolRoster(), CoreConfiguration.getNumScrumhalvesPoolRound(),CoreConfiguration.getNumScrumhalvesKnockoutRoster(), CoreConfiguration.getNumScrumhalvesKnockoutRound()),
		FLYHALF("Pick 2 flyhalves and click next", CoreConfiguration.getNumFlyhalvesPoolRoster(), CoreConfiguration.getNumFlyhalvesPoolRound(),CoreConfiguration.getNumFlyhalvesKnockoutRoster(), CoreConfiguration.getNumFlyhalvesKnockoutRound()),  
		CENTER("Pick 4 centers and click next", CoreConfiguration.getNumCentersPoolRoster(), CoreConfiguration.getNumCentersPoolRound(),CoreConfiguration.getNumCentersKnockoutRoster(), CoreConfiguration.getNumCentersKnockoutRound()), 
		WING("Pick 4 wings and click next", CoreConfiguration.getNumWingsPoolRoster(), CoreConfiguration.getNumWingsPoolRound(),CoreConfiguration.getNumWingsKnockoutRoster(), CoreConfiguration.getNumWingsKnockoutRound()), 
		FULLBACK("Pick 2 fullbacks", CoreConfiguration.getNumFullbacksPoolRoster(), CoreConfiguration.getNumFullbacksPoolRound(),CoreConfiguration.getNumFullbacksKnockoutRoster(), CoreConfiguration.getNumFullbacksKnockoutRound()),  
		DONE("Click \"Done\" to finalize your World Cup squad.", 0,0,0,0),
		RANDOM("Here is your random team!", 0,0,0,0),
		ROUND("Pick your first side 15 for this round from your full roster below", 15, 0, 0, 0);
		
		private String instruction;
		private int numPoolRoster;
		private int numPoolRound;
		private int numKnockoutRoster;
		private int numKnockoutRound;
		
		step(String inst, int numPoolRoster, int numPoolRound, int numKnockoutRoster, int numKnockoutRound) {
			instruction = inst;
			this.numPoolRoster = numPoolRoster;
			this.numPoolRound = numPoolRound;
			this.numKnockoutRoster = numKnockoutRoster;
			this.numKnockoutRound = numKnockoutRound;

		}
		
		public step getNext() {
			return values()[(ordinal()+1) % values().length];
		}
		public step getPrev() {
			return values()[(ordinal()-1) % values().length];
		}
		
		public String getInstructions() {
			return instruction;
		}
		
		public int getCount(stageType stage, int round) {
			if (stage == stageType.POOL) {
				if (round == 0) {
					return numPoolRoster;
				} else {
					return numPoolRound;
				}
			} else if (round == 5) {
				return numKnockoutRoster;
			} else {
				return numKnockoutRound;
			}
		}
	}
	private Long RootID;
	private GroupType groupType;
	private stageType stage;
	private int round;
	private step _step;
	private boolean edit;
	
	private String seps = "\\+";
	
	public Manage(String token)
	{
		// shouldn't use this in our code 
		String[] tok = token.split(seps);
		this.RootID = new Long(tok[0]);

		this.groupType = GroupType.MY;
		//assert tok[1] == "MY";
		
		//ADHOC, TEAM, POSITION, MATCH, FEATURED, NONE
		if (tok[2].equalsIgnoreCase("POOL")) { this.stage = stageType.POOL; }
		else if (tok[2].equalsIgnoreCase("KNOCKOUT")) { this.stage = stageType.KNOCKOUT; }
		
		if (tok.length > 3) {
			
			this.round = Integer.parseInt(tok[3]);
			if (tok.length > 4) {
				this._step = getStepFromString(tok[4]);
				if (tok.length > 5) {
					if(tok[5].equals("0"))
						this.edit = false;
					else
						this.edit = true;
				}
			}
		}

	}

	private step getStepFromString(String parseInt) {
		if (parseInt.equals("START")) {
			return step.START;
		} else 		if (parseInt.equals("PROP")) {
			return step.PROP;
		} else 		if (parseInt.equals("HOOKER")) {
			return step.HOOKER;
		} else 		if (parseInt.equals("LOCK")) {
			return step.LOCK;
		} else 		if (parseInt.equals("FLANKER")) {
			return step.FLANKER;
		} else 		if (parseInt.equals("NUMBER8")) {
			return step.NUMBER8;
		} else 		if (parseInt.equals("SCRUMHALF")) {
			return step.SCRUMHALF;
		} else 		if (parseInt.equals("FLYHALF")) {
			return step.FLYHALF;
		} else 		if (parseInt.equals("CENTER")) {
			return step.CENTER;
		} else 		if (parseInt.equals("WING")) {
			return step.WING;
		} else if (parseInt.equals("FULLBACK")) {
			return step.FULLBACK;
		} else if (parseInt.equals("DONE")) {
			return step.DONE;
		}
		return null;
	}

	public Manage()
	{
		//defaults
		this.RootID = 0L;
		this.stage = stageType.POOL;
		this.round = 1;
		this._step=step.START;
		this.edit = true;
		this.groupType = GroupType.MY;
	}
	

	public Manage(Long rootID, GroupType groupType, stageType stage, int round, step step,
			boolean edit) {
		super();
		this.RootID = rootID;
		this.groupType = groupType;
		this.stage = stage;
		this.round = round;
		this._step = step;
		this.edit = edit;
		this.groupType = groupType;

	}

	
	public Long getRootID() {
		return RootID;
	}

	public void setRootID(Long rootID) {
		RootID = rootID;
	}


	public static class Tokenizer implements PlaceTokenizer<Manage>
	{

		@Override
		public String getToken(Manage place)
		{
			String edit = "0";
			if (place.edit) edit = "1";
			
			return place.RootID + "+" + place.groupType.name() + "+" + place.stage.name() + "+" + place.round + "+" + place._step.name() + "+" + edit;
		}

		@Override
		public Manage getPlace(String token)
		{
			return new Manage(token);
		}
	}


	public stageType getStage() {
		return stage;
	}

	public void setStage(stageType stage) {
		this.stage = stage;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public step getStep() {
		return _step;
	}

	public void setStep(step step) {
		this._step = step;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public GroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
	}
}
