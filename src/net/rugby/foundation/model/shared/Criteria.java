package net.rugby.foundation.model.shared;

public enum Criteria {
	ROUND("Round", "Best of the weekend"),
	IN_FORM ("In Form", "Recent results count more"),
	BEST_YEAR ("Best", "All matches from the last year"),
	BEST_ALLTIME("All Time", "All matches we have in our database"),
	AVERAGE_IMPACT("Impact", "Contribution per minute played");

	private String menuName;
	private String description;

	private Criteria(String menuName, String description)
	{
		this.menuName = menuName;
		this.description = description;
	}

	public String getMenuName() {
		return menuName;
	}
	
	public String getDescription() {
		return description;
	}
}
