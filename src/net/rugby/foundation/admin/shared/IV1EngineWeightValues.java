package net.rugby.foundation.admin.shared;

public interface IV1EngineWeightValues {

	public abstract Float getTriesWeight();

	public abstract Float getTryAssistsWeight();

	public abstract Float getPointsWeight();

	public abstract Float getKicksWeight();

	public abstract Float getPassesWeight();

	public abstract Float getRunsWeight();

	public abstract Float getMetersRunWeight();

	public abstract Float getCleanBreaksWeight();

	public abstract Float getDefendersBeatenWeight();

	public abstract Float getOffloadsWeight();

	public abstract Float getTurnoversWeight();

	public abstract Float getTacklesMadeWeight();

	public abstract Float getTacklesMissedWeight();

	public abstract Float getLineoutsWonOnThrowWeight();

	public abstract Float getLineoutsStolenOnOppThrowWeight();

	public abstract Float getPenaltiesConcededWeight();

	public abstract Float getYellowCardsWeight();

	public abstract Float getRedCardsWeight();

	public abstract Float getScrumShareWeight();

	public abstract Float getLineoutShareWeight();

	public abstract Float getRuckShareWeight();

	public abstract Float getMaulShareWeight();

	public abstract Float getMinutesShareWeight();

	public abstract Float getPointsDifferentialWeight();

	void setWin(Float win);
	Float getWin();
	Float getScrumLostWeight();

	void setScrumLostWeight(Float scrumLostWeight);

	Float getLineoutLostWeight();

	void setLineoutLostWeight(Float lineoutLostWeight);

	Float getRuckLostWeight();

	void setRuckLostWeight(Float ruckLostWeight);

	Float getMaulLostWeight();

	void setMaulLostWeight(Float maulLostWeight);

	Float getScrumStolenWeight();

	void setScrumStolenWeight(Float scrumStolenWeight);

	Float getLineoutStolenWeight();

	void setLineoutStolenWeight(Float lineoutStolenWeight);

	Float getRuckStolenWeight();

	void setRuckStolenWeight(Float ruckStolenWeight);

	Float getMaulStolenWeight();

	void setMaulStolenWeight(Float maulStolenWeight);



}