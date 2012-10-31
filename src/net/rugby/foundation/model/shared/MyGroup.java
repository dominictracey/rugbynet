package net.rugby.foundation.model.shared;

import java.util.ArrayList;

import net.rugby.foundation.model.shared.Stage.stageType;

import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class MyGroup extends Group {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private stageType stageT;
	private int round;
	private ArrayList<Long> playerIds;
	private Long AppUserID;
	
	public MyGroup() {
		setGroupType(GroupType.MY);
		setDisplayName("Full Roster");
	}
	
	  @Override
	  public String getGroupInfo() {
		  if (this.groupInfo == null) {
			  return  "This is the current listing for " + getDisplayName() + ". Your roster below will show the ratings of your chosen players as the tournament progresses.";
		  } else {
			  return this.groupInfo;
		  }
	  }

	public ArrayList<Long> getPlayerIds() {
		return playerIds;
	}

	public void setPlayerIds(ArrayList<Long> playerIds) {
		this.playerIds = playerIds;
	}

	public Long getAppUserID() {
		return AppUserID;
	}

	public void setAppUserID(Long appUserID) {
		AppUserID = appUserID;
	}

	public stageType getStageT() {
		return stageT;
	}

	public void setStageT(stageType stageT) {
		this.stageT = stageT;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}
}
