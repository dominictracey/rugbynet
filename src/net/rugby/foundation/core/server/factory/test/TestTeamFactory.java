package net.rugby.foundation.core.server.factory.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.IGroup;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.TeamGroup;

public class TestTeamFactory extends BaseCachingFactory<ITeamGroup> implements ITeamGroupFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4807160728169022839L;
	private Map<Long, ITeamGroup> idMap = new HashMap<Long, ITeamGroup>(); 
	private Map<String, ITeamGroup> nameMap = new HashMap<String, ITeamGroup>(); 
	
	private boolean populated = false;
	
	//@Inject
	TestTeamFactory() {

	}

	@Override
	public ITeamGroup getFromPersistentDatastore(Long id) {
		populate();
		if (id == null) {
			return new TeamGroup();
		} else {
			return idMap.get(id);
		}

	}
	@Override
	public ITeamGroup putToPersistentDatastore(ITeamGroup team) {
		idMap.put(team.getId(), team);
		nameMap.put(team.getDisplayName(), team);
		return team;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamGroupFactory#getTeamByName(java.lang.String)
	 */
	@Override
	public ITeamGroup getTeamByName(String name) {
		populate();
		if (name == null) {
			return new TeamGroup();
		} else {
			return nameMap.get(name);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamGroupFactory#find(net.rugby.foundation.model.shared.ITeamGroup)
	 */
	@Override
	public ITeamGroup find(ITeamGroup team) {
		// hard to implement
		return null;
	}
	
	private void populate() {
		if (!populated) {
			for (Long l=9001L; l<9007L; l++) {
				build(l);
			}
			
			for (Long l=9201L; l<9210L; l++) {
				build(l);
			}
			
			for (Long l=9300L; l<9324L; l++) {
				build(l);
			}
			populated = true;
		}
	}
	
	ITeamGroup build(Long id) {
		ITeamGroup t = new TeamGroup();
		if (id == 9001) {
			t.setAbbr("NZL");
			t.setShortName("All Blacks");
			((IGroup)t).setDisplayName("New Zealand");
			t.setColor("#000000");
		} else if (id == 9002) {
			t.setAbbr("AUS");
			t.setShortName("Wallabies");
			((IGroup)t).setDisplayName("Australia");
			t.setColor("#f0af00");
		} else if (id == 9003) {
			t.setAbbr("RSA");
			t.setShortName("Springboks");
			((IGroup)t).setDisplayName("South Africa");
			t.setColor("#003500");
		} else if (id == 9004) {
			t.setAbbr("ENG");
			t.setShortName("England");
			((IGroup)t).setDisplayName("England");
			t.setColor("#ffffff");
		} else if (id == 9005) {
			t.setAbbr("WAL");
			t.setShortName("Dragons");
			((IGroup)t).setDisplayName("Wales");
			t.setColor("#CC2026");
		} else if (id == 9006) {
			t.setAbbr("IRE");
			t.setShortName("Ireland");
			((IGroup)t).setDisplayName("Ireland");
			t.setColor("#006840");
		} else if (id == 9201) {
			t.setAbbr("MUN");
			t.setShortName("Munster");
			((IGroup)t).setDisplayName("Munster");
			t.setColor("#CC0000");
		} else if (id == 9202) {
			t.setAbbr("WASP");
			t.setShortName("Wasps");
			((IGroup)t).setDisplayName("London Wasps");
			t.setColor("#FFD520");
		} else if (id == 9203) {
			t.setAbbr("BATH");
			t.setShortName("Bath");
			((IGroup)t).setDisplayName("Bath");
			t.setColor("#004FA3");
//		} else if (id == 9204) {
//			t.setAbbr("BIA");
//			t.setShortName("Biarritz");
//			((Group)t).setDisplayName("Biarritz");
//			t.setColor("#EA3E40");
		} else if (id == 9204) {
			t.setAbbr("CAN");
			t.setShortName("Crusaders");
			((IGroup)t).setDisplayName("Crusaders");
			t.setColor("#EA3E40");		
		} else if (id == 9205) {
			t.setAbbr("CAR");
			t.setShortName("Cardiff");
			((IGroup)t).setDisplayName("Cardiff");
			t.setColor("#B0E0E6");
		} else if (id == 9206) {
			t.setAbbr("BRI");
			t.setShortName("Brive");
			((IGroup)t).setDisplayName("Brive");
			t.setColor("#ffffff");
		} else if (id == 9207) {
			t.setAbbr("TOU");
			t.setShortName("Toulouse");
			((IGroup)t).setDisplayName("Toulouse");
			t.setColor("#FF8040");
		} else if (id == 9208) {
			t.setAbbr("TIG");
			t.setShortName("Tigers");
			((IGroup)t).setDisplayName("Leicester");
			t.setColor("#004738");
		} else if (id == 9209) {
			t.setAbbr("SAR");
			t.setShortName("Saracens");
			((IGroup)t).setDisplayName("Saracens");
			t.setColor("#000000");
		} else if (id == 9210) {
			t.setAbbr("WAS");
			t.setShortName("Wasps");
			((IGroup)t).setDisplayName("London Wasps");
			t.setColor("#000000");
		} else if (id == 9211) {
			t.setAbbr("HAR");
			t.setShortName("Quins");
			((IGroup)t).setDisplayName("Harlequins");
			t.setColor("#330033");
		}  else if (id == 9212) {
			t.setAbbr("LIR");
			t.setShortName("London Irish");
			((IGroup)t).setDisplayName("London Irish");
			t.setColor("#00FF00");
		}  else if (id == 9213) {
			t.setAbbr("SAR");
			t.setShortName("Saracens");
			((IGroup)t).setDisplayName("Saracens");
			t.setColor("#000000");
			
			// HEINEKEN CUP 2013-2014
		} else if (id == 9300) {
			t.setDisplayName("Leinster");
		} else if (id == 9301) {
			t.setDisplayName("Castres");
		} else if (id == 9302) {
			t.setDisplayName("Northampton Saints");
		} else if (id == 9303) {
			t.setDisplayName("Ospreys");
		} else if (id == 9304) {
			t.setDisplayName("Toulon");
		} else if (id == 9305) {
			t.setDisplayName("Exeter Chiefs");
		} else if (id == 9306) {
			t.setDisplayName("Cardiff Blues");
		} else if (id == 9307) {
			t.setDisplayName("Glasgow");
		} else if (id == 9308) {
			t.setDisplayName("Toulouse");
		} else if (id == 9309) {
			t.setDisplayName("Saracens");
		} else if (id == 9310) {
			t.setDisplayName("Connacht");
		} else if (id == 9311) {
			t.setDisplayName("Zebre");
		} else if (id == 9312) {
			t.setDisplayName("Scarlets");
		} else if (id == 9313) {
			t.setDisplayName("Racing Metro");
		} else if (id == 9314) {
			t.setDisplayName("Harlequins");
		} else if (id == 9315) {
			t.setDisplayName("Clermont Auvergne");
		} else if (id == 9316) {
			t.setDisplayName("Montpellier");
		} else if (id == 9317) {
			t.setDisplayName("Ulster");
		} else if (id == 9318) {
			t.setDisplayName("Leicester Tigers");
		} else if (id == 9319) {
			t.setDisplayName("Benetton Treviso");
		} else if (id == 9320) {
			t.setDisplayName("Edinburgh");
		} else if (id == 9321) {
			t.setDisplayName("Gloucester Rugby");
		} else if (id == 9322) {
			t.setDisplayName("Munster");
		} else if (id == 9323) {
			t.setDisplayName("Perpignan");
		}
		
		
		
		((TeamGroup)t).setId(id);
		((IGroup)t).setGroupType(GroupType.TEAM);
		
		put(t);
		
		return t;
	}

	@Override
	public ITeamGroup create() {

		return new TeamGroup();
	}

	@Override
	protected boolean deleteFromPersistentDatastore(ITeamGroup t) {

		return true;
	}
	

}
