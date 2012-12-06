package net.rugby.foundation.core.server.factory.test;

import java.io.Serializable;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.TeamGroup;

public class TestTeamFactory implements ITeamGroupFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4807160728169022839L;
	private Long id;
	
	@Inject
	TestTeamFactory() {
	}
		@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public ITeamGroup getTeam() {
		ITeamGroup t = new TeamGroup();
		if (id == 9001) {
			t.setAbbr("NZL");
			t.setShortName("All Blacks");
			((Group)t).setDisplayName("New Zealand");
			t.setColor("#000000");
		} else if (id == 9002) {
			t.setAbbr("AUS");
			t.setShortName("Wallabies");
			((Group)t).setDisplayName("Australia");
			t.setColor("#f0af00");
		} else if (id == 9003) {
			t.setAbbr("RSA");
			t.setShortName("Springboks");
			((Group)t).setDisplayName("South Africa");
			t.setColor("#003500");
		} else if (id == 9004) {
			t.setAbbr("ENG");
			t.setShortName("England");
			((Group)t).setDisplayName("England");
			t.setColor("#ffffff");
		} else if (id == 9005) {
			t.setAbbr("WAL");
			t.setShortName("Dragons");
			((Group)t).setDisplayName("Wales");
			t.setColor("#CC2026");
		} else if (id == 9006) {
			t.setAbbr("IRE");
			t.setShortName("Ireland");
			((Group)t).setDisplayName("Ireland");
			t.setColor("#006840");
		} else if (id == 9201) {
			t.setAbbr("MUN");
			t.setShortName("Munster");
			((Group)t).setDisplayName("Munster");
			t.setColor("#CC0000");
		} else if (id == 9202) {
			t.setAbbr("WASP");
			t.setShortName("Wasps");
			((Group)t).setDisplayName("London Wasps");
			t.setColor("#FFD520");
		} else if (id == 9203) {
			t.setAbbr("BATH");
			t.setShortName("Bath");
			((Group)t).setDisplayName("Bath");
			t.setColor("#004FA3");
//		} else if (id == 9204) {
//			t.setAbbr("BIA");
//			t.setShortName("Biarritz");
//			((Group)t).setDisplayName("Biarritz");
//			t.setColor("#EA3E40");
		} else if (id == 9204) {
			t.setAbbr("CAN");
			t.setShortName("Crusaders");
			((Group)t).setDisplayName("Crusaders");
			t.setColor("#EA3E40");		
		} else if (id == 9205) {
			t.setAbbr("CAR");
			t.setShortName("Cardiff");
			((Group)t).setDisplayName("Cardiff");
			t.setColor("#B0E0E6");
		} else if (id == 9206) {
			t.setAbbr("BRI");
			t.setShortName("Brive");
			((Group)t).setDisplayName("Brive");
			t.setColor("#ffffff");
		} else if (id == 9207) {
			t.setAbbr("TOU");
			t.setShortName("Toulouse");
			((Group)t).setDisplayName("Toulouse");
			t.setColor("#FF8040");
		} else if (id == 9208) {
			t.setAbbr("TIG");
			t.setShortName("Tigers");
			((Group)t).setDisplayName("Leicester");
			t.setColor("#004738");
		} else if (id == 9209) {
			t.setAbbr("SAR");
			t.setShortName("Saracens");
			((Group)t).setDisplayName("Saracens");
			t.setColor("#000000");
		}
		((TeamGroup)t).setId(id);
		((Group)t).setGroupType(GroupType.TEAM);
		return t;
	}
	@Override
	public ITeamGroup put(ITeamGroup team) {
		
		return team;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamGroupFactory#getTeamByName(java.lang.String)
	 */
	@Override
	public ITeamGroup getTeamByName(String name) {
		if (name.equals("New Zealand")) {
			setId(9001L);
			return getTeam();
		}
		
		//@TODO implement the rest (though I think this method is only used at this point in the admin fetcher code...)
		return null;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamGroupFactory#find(net.rugby.foundation.model.shared.ITeamGroup)
	 */
	@Override
	public ITeamGroup find(ITeamGroup team) {
		// hard to implement
		return null;
	}

}
