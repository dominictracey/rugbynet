package net.rugby.foundation.model.shared;

public enum RatingMode {
	BY_COMP ("By Round", 1, "Rounds"), 
	BY_POSITION ("By Position", 2, "Positions"),
	BY_COUNTRY ("By Country", 3, "Countries"),
	BY_TEAM ("By Team", 4, "Teams"),
	BY_MATCH ("By Match", 5, "Matches");

	private String menuName;
	private int ordinal;
	private String plural;

	private RatingMode(String menuName, int ordinal, String plural)
	{
		this.menuName = menuName;
		this.ordinal = ordinal;
		this.plural = plural;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public String getPlural() {
		return plural;
	}

	public void setPlural(String plural) {
		this.plural = plural;
	}
}
