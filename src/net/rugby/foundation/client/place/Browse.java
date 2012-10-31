package net.rugby.foundation.client.place;

import net.rugby.foundation.model.shared.Group.GroupType;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

//public class PlayerListPlace extends ActivityPlace<PlayerListActivity>
public class Browse extends Place
{
	private Long RootID;
	private GroupType GroupTypeID;
	private Long GroupID;
	private Long PlayerID;
	
	private String seps = "\\+";
	
	public Browse(String token)
	{
		// shouldn't use this in our code 
		String[] tok = token.split(seps);
		this.RootID = new Long(tok[0]);
		
		//ADHOC, TEAM, POSITION, MATCH, FEATURED, NONE
		if (tok[1].equalsIgnoreCase("TEAM")) { this.GroupTypeID = GroupType.TEAM; }
		else if (tok[1].equalsIgnoreCase("POSITION")) { this.GroupTypeID = GroupType.POSITION; }
		else if (tok[1].equalsIgnoreCase("ADHOC")) { this.GroupTypeID = GroupType.ADHOC; }
		else if (tok[1].equalsIgnoreCase("MATCH")) { this.GroupTypeID = GroupType.MATCH; }
		else if (tok[1].equalsIgnoreCase("FEATURED")) { this.GroupTypeID = GroupType.FEATURED; }
		else if (tok[1].equalsIgnoreCase("NONE")) { this.GroupTypeID = GroupType.NONE; }
		else if (tok[1].equalsIgnoreCase("MY")) { this.GroupTypeID = GroupType.MY; }
		
		this.GroupID = new Long(tok[2]);
		this.PlayerID = new Long(tok[3]);

	}

	public Browse()
	{
		//defaults
		RootID = 0L;
		GroupTypeID = GroupType.TEAM;
		GroupID = 0L;
		PlayerID=0L;
	}
	
	public Browse(Long RootID, GroupType GroupTypeID, Long GroupID, Long PlayerID) {
		this.RootID = RootID;
		this.GroupTypeID = GroupTypeID;
		this.GroupID = GroupID;
		this.PlayerID = PlayerID;
	}

	public GroupType getGroupTypeID() {
		return GroupTypeID;
	}
	
	public Long getGroupID()
	{
		return GroupID;
	}
	
	public Long getRootID() {
		return RootID;
	}

	public void setRootID(Long rootID) {
		RootID = rootID;
	}

	public void setGroupTypeID(GroupType groupTypeID) {
		GroupTypeID = groupTypeID;
	}

	public void setGroupID(Long groupID) {
		GroupID = groupID;
	}

	public void setPlayerID(Long playerID) {
		PlayerID = playerID;
	}

	public Long getPlayerID()
	{
		return PlayerID;
	}

	public static class Tokenizer implements PlaceTokenizer<Browse>
	{

		@Override
		public String getToken(Browse place)
		{
			return place.RootID + "+" + place.GroupTypeID.name() + "+" + place.GroupID + "+" + place.PlayerID;
		}

		@Override
		public Browse getPlace(String token)
		{
			return new Browse(token);
		}
	}
}
