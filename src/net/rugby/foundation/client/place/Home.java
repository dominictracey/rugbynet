package net.rugby.foundation.client.place;

import net.rugby.foundation.model.shared.Group.GroupType;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class Home extends Place
{
	private Long RootID;
	
	//these values allow us to create "referrers" with our advertising.
	private GroupType GroupTypeID;
	private Long GroupID;
	private Long PlayerID;
	private actions action;
	
	private String seps = "\\+";
	
	public enum actions { LOGIN, LOGOUT, CREATE, UPDATE, NEWLEAGUE, JOIN, OPTOUT, NONE }
	
	public Home(String token)
	{
		// shouldn't use this in our code 
		String[] tok = token.split(seps);
		this.RootID = new Long(tok[0]);
		
		//ADHOC, TEAM, POSITION, MATCH, FEATURED, NONE, MY
		if (tok[1].equalsIgnoreCase("TEAM")) { this.GroupTypeID = GroupType.TEAM; }
		else if (tok[1].equalsIgnoreCase("MY")) { this.GroupTypeID = GroupType.MY; }
		else if (tok[1].equalsIgnoreCase("POSITION")) { this.GroupTypeID = GroupType.POSITION; }
		else if (tok[1].equalsIgnoreCase("ADHOC")) { this.GroupTypeID = GroupType.ADHOC; }
		else if (tok[1].equalsIgnoreCase("MATCH")) { this.GroupTypeID = GroupType.MATCH; }
		else if (tok[1].equalsIgnoreCase("FEATURED")) { this.GroupTypeID = GroupType.FEATURED; }
		else if (tok[1].equalsIgnoreCase("NONE")) { this.GroupTypeID = GroupType.NONE; }

		
		if (tok.length > 2) {
			
			this.GroupID = new Long(tok[2]);
			if (tok.length > 3) {
				this.PlayerID = new Long(tok[3]);
				if (tok.length > 4) {
					String actionS = new String(tok[4]);
					if (actionS.equalsIgnoreCase("LOGIN")) {
						this.action = actions.LOGIN;
					} else if (actionS.equalsIgnoreCase("LOGOUT")) {
						this.action = actions.LOGOUT;
					} else if (actionS.equalsIgnoreCase("CREATE")) {
						this.action = actions.CREATE;
					} else if (actionS.equalsIgnoreCase("NONE")) {
						this.action = actions.NONE;
					} else if (actionS.equalsIgnoreCase("UPDATE")) {
						this.action = actions.UPDATE;
					} else if (actionS.equalsIgnoreCase("NEWLEAGUE")) {
						this.action = actions.NEWLEAGUE;
					} else if (actionS.equalsIgnoreCase("JOIN")) {
						this.action = actions.JOIN;
					} else if (actionS.equalsIgnoreCase("OPTOUT")) {
						this.action = actions.OPTOUT;
					}
				}
			}
		}

	}

	public Home()
	{
		//defaults
		this.RootID = 0L;
		this.GroupTypeID = GroupType.MY;
		this.GroupID = 0L;
		this.PlayerID=0L;
		this.action = actions.NONE;
	}
	
	public Home(Long RootID, GroupType GroupTypeID, Long GroupID, Long PlayerID, actions action) {
		this.RootID = RootID;
		this.GroupTypeID = GroupTypeID;
		this.GroupID = GroupID;
		this.PlayerID = PlayerID;
		this.action = action;
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

	public actions getAction() {
		return action;
	}

	public static class Tokenizer implements PlaceTokenizer<Home>
	{

		@Override
		public String getToken(Home place)
		{
			return place.RootID + "+" + place.GroupTypeID.name() + "+" + place.GroupID + "+" + place.PlayerID + "+" + place.action.name();
		}

		@Override
		public Home getPlace(String token)
		{
			return new Home(token);
		}
	}
}
