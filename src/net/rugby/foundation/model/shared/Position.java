package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.NotSaved;
//import com.googlecode.objectify.condition;

@SuppressWarnings("serial")
@Entity
public abstract class Position implements Serializable {

	@Id
	Long id;
	//	@NotSaved(IfDefault.class)
	private position primary;
	@NotSaved
	private ArrayList<position> positionList;
	//static private HashMap<grouping, ArrayList<position>> groupingMap;


	//internationalization support
	public abstract String getName(position p);
	public abstract String getName(grouping g);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public position getPrimary() {
		return primary;
	}

	public String getPrimaryName() {
		return getName(primary);
	}

	public void setPrimary(position primary) {
		this.primary = primary;
		if (!positionList.contains(primary))
			positionList.add(primary);
	}

	//	public enum position { 
	//			NONE, TIGHTHEAD, HOOKER, LOOSEHEAD, LEFTLOCK, RIGHTLOCK, BLINDSIDE, OPENSIDE, NUMBER8, 
	//			SCRUMHALF, FLYHALF, LEFTWING, INSIDECENTER, OUTSIDECENTER, RIGHTWING, FULLBACK }

	public enum position { 
		NONE ("None", "---", "Unused", "position-none"), 
		PROP ("Prop", "PRP", "Props", "position-prop"), 
		HOOKER ("Hooker", "HKR", "Hookers", "position-hooker"), 
		LOCK ("Lock", "LCK", "Locks", "position-lock"), 
		FLANKER ("Flanker", "FLK", "Flankers", "position-flanker"), 
		NUMBER8 ("Number 8", "NO8", "No.8s", "position-number8"), 
		SCRUMHALF ("Scrumhalf", "SCR", "Scrumhalves", "position-scrumhalf"), 
		FLYHALF ("Flyhalf", "FLY", "Flyhalves", "position-flyhalf"), 
		CENTER ("Centre", "CTR", "Centres", "position-centre"), 
		WING ("Wing", "WNG", "Wings", "position-wing"), 
		FULLBACK ("Fullback", "FUL", "Fullbacks", "position-fullback"), 
		RESERVE ("Reserve", "RES", "Reserves", "position-reserve");
		
		private String enUsName;
		private String abbr;
		private String plural;
		private String style;
		
		private position(String enUSname, String abbr, String plural, String style)
		{
			this.enUsName = enUSname;
			this.abbr = abbr;
			this.plural = plural;
			this.style = style;
		}

		public position getNext() {
			return values()[(ordinal()+1) % values().length];
		}
		public position getPrev() {
			return values()[(ordinal()-1) % values().length];
		}
		public static position getAt(int i) {
			return values()[i];
		}

		public String getName()
		{
			return enUsName;
		}

		public String getAbbr() {
			return abbr;
		}
		
		public String getPlural() {
			return plural;
		}

		public String getStyle() {
			return style;
		}

//		public int getNumberRequired(Stage.stageType stage, int round)  { 
//			switch (ordinal()) {
//			case 1: 
//				if (stage == Stage.stageType.POOL) {
//					if (round == 0)
//						return CoreConfiguration.getNumPropsPoolRoster();
//					else
//						return CoreConfiguration.getNumPropsPoolRound();
//				} else {
//					if (round == 0)
//						return CoreConfiguration.getNumPropsKnockoutRoster();
//					else
//						return CoreConfiguration.getNumPropsKnockoutRound();							
//				}
//			case 2 :
//				if (stage == Stage.stageType.POOL) {
//					if (round == 0)
//						return CoreConfiguration.getNumHookersPoolRoster();
//					else
//						return CoreConfiguration.getNumHookersPoolRound();
//				} else {
//					if (round == 0)
//						return CoreConfiguration.getNumHookersKnockoutRoster();
//					else
//						return CoreConfiguration.getNumHookersKnockoutRound();							
//				}
//			case 3 :
//				if (stage == Stage.stageType.POOL) {
//					if (round == 0)
//						return CoreConfiguration.getNumLocksPoolRoster();
//					else
//						return CoreConfiguration.getNumLocksPoolRound();
//				} else {
//					if (round == 0)
//						return CoreConfiguration.getNumLocksKnockoutRoster();
//					else
//						return CoreConfiguration.getNumLocksKnockoutRound();							
//				}
//			case 4 :
//				if (stage == Stage.stageType.POOL) {
//					if (round == 0)
//						return CoreConfiguration.getNumFlankersPoolRoster();
//					else
//						return CoreConfiguration.getNumFlankersPoolRound();
//				} else {
//					if (round == 0)
//						return CoreConfiguration.getNumFlankersKnockoutRoster();
//					else
//						return CoreConfiguration.getNumFlankersKnockoutRound();							
//				}
//			case 5 : 
//				if (stage == Stage.stageType.POOL) {
//					if (round == 0) 
//						return CoreConfiguration.getNumNumber8sPoolRoster();
//					else
//						return CoreConfiguration.getNumNumber8sPoolRound();
//				} else {
//					if (round == 0)
//						return CoreConfiguration.getNumNumber8sKnockoutRoster();
//					else
//						return CoreConfiguration.getNumNumber8sKnockoutRound();							
//				}
//			case 6 :
//				if (stage == Stage.stageType.POOL) {
//					if (round == 0)
//						return CoreConfiguration.getNumScrumhalvesPoolRoster();
//					else
//						return CoreConfiguration.getNumScrumhalvesPoolRound();
//				} else {
//					if (round == 0)
//						return CoreConfiguration.getNumScrumhalvesKnockoutRoster();
//					else
//						return CoreConfiguration.getNumScrumhalvesKnockoutRound();							
//				}
//			case 7 :
//				if (stage == Stage.stageType.POOL) {
//					if (round == 0)
//						return CoreConfiguration.getNumFlyhalvesPoolRoster();
//					else
//						return CoreConfiguration.getNumFlyhalvesPoolRound();
//				} else {
//					if (round == 0)
//						return CoreConfiguration.getNumFlyhalvesKnockoutRoster();
//					else
//						return CoreConfiguration.getNumFlyhalvesKnockoutRound();							
//				}
//			case 8 :
//				if (stage == Stage.stageType.POOL) {
//					if (round == 0)
//						return CoreConfiguration.getNumCentersPoolRoster();
//					else
//						return CoreConfiguration.getNumCentersPoolRound();
//				} else {
//					if (round == 0)
//						return CoreConfiguration.getNumCentersKnockoutRoster();
//					else
//						return CoreConfiguration.getNumCentersKnockoutRound();							
//				}
//			case 9 :
//				if (stage == Stage.stageType.POOL) {
//					if (round == 0)
//						return CoreConfiguration.getNumWingsPoolRoster();
//					else
//						return CoreConfiguration.getNumWingsPoolRound();
//				} else {
//					if (round == 0)
//						return CoreConfiguration.getNumWingsKnockoutRoster();
//					else
//						return CoreConfiguration.getNumWingsKnockoutRound();							
//				}
//			case 10 :
//				if (stage == Stage.stageType.POOL) {
//					if (round == 0)
//						return CoreConfiguration.getNumFullbacksPoolRoster();
//					else
//						return CoreConfiguration.getNumFullbacksPoolRound();
//				} else {
//					if (round == 0)
//						return CoreConfiguration.getNumFullbacksKnockoutRoster();
//					else
//						return CoreConfiguration.getNumFullbacksKnockoutRound();							
//				}
//			}
//			return 0;
//		}
	}


	public enum grouping { ALL, FORWARD, BACK, FRONTROW, PROP, BACKFIVE, LOCK, FLANKER, BACKROW, HALFBACK, CENTER, BACKTHREE, WING }

	public Position()
	{
		positionList  = new ArrayList<position>();
	}

	public Position(position p)
	{
		positionList  = new ArrayList<position>();
		setPrimary(p);
	}

	public Position(position p, ArrayList<position> l)
	{
		primary = p;
		positionList = l; 
	}

	ArrayList<position> GetPositions()
	{
		return positionList;
	}

	public void SetPositions(ArrayList<position> p)
	{
		positionList = p;
	}

	public static position getFromScrum(String scrumName) {
		
		// substitutes often have their positions listed in parens
		if (scrumName.contains("("))
			scrumName = scrumName.split("[(|)]")[1];
		
		if (scrumName.equals("FB")) {
			return position.FULLBACK;
		} else if (scrumName.equals("W")) {
			return position.WING;
		} else if (scrumName.equals("C")) {
			return position.CENTER;
		} else if (scrumName.equals("FH")) {
			return position.FLYHALF;
		} else if (scrumName.equals("SH")) {
			return position.SCRUMHALF;
		} else if (scrumName.equals("P")) {
			return position.PROP;
		} else if (scrumName.equals("H")) {
			return position.HOOKER;
		} else if (scrumName.equals("L")) {
			return position.LOCK;
		} else if (scrumName.equals("F")) {
			return position.FLANKER;
		} else if (scrumName.equals("N8")) {
			return position.NUMBER8;
		}  else if (scrumName.equals("R")) {
			return position.RESERVE;
		} else
			return position.NONE;
	}

	}
