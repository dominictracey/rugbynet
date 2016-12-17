package net.rugby.foundation.model.shared;

import java.util.Date;
import java.util.List;

public interface ITeamMatchStats  extends IHasId {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Integer getTries();

	public abstract void setTries(Integer tries);

	public abstract Integer getConversionsAttempted();

	public abstract void setConversionsAttempted(Integer conversionsAttempted);

	public abstract Integer getConversionsMade();

	public abstract void setConversionsMade(Integer conversionsMade);

	public abstract Integer getPenaltiesAttempted();

	public abstract void setPenaltiesAttempted(Integer penaltiesAttempted);

	public abstract Integer getPenaltiesMade();

	public abstract void setPenaltiesMade(Integer penaltiesMade);

	public abstract Integer getDropGoalsAttempted();

	public abstract void setDropGoalsAttempted(Integer dropGoalsAttempted);

	public abstract Integer getDropGoalsMade();

	public abstract void setDropGoalsMade(Integer dropGoalsMade);

	public abstract Integer getKicksFromHand();

	public abstract void setKicksFromHand(Integer kicksFromHand);

	public abstract Integer getPasses();

	public abstract void setPasses(Integer passes);

	public abstract Integer getRuns();

	public abstract void setRuns(Integer runs);

	public abstract Float getPossesion();

	public abstract void setPossesion(Float possesion);

	public abstract Float getTerritory();

	public abstract void setTerritory(Float territory);

	public abstract Integer getCleanBreaks();

	public abstract void setCleanBreaks(Integer cleanBreaks);

	public abstract Integer getDefendersBeaten();

	public abstract void setDefendersBeaten(Integer defendersBeaten);

	public abstract Integer getOffloads();

	public abstract void setOffloads(Integer offloads);

	public abstract Integer getRucks();

	public abstract void setRucks(Integer rucks);

	public abstract Integer getRucksWon();

	public abstract void setRucksWon(Integer rucksWon);

	public abstract Integer getMauls();

	public abstract void setMauls(Integer mauls);

	public abstract Integer getMaulsWon();

	public abstract void setMaulsWon(Integer maulsWon);

	public abstract Integer getTurnoversConceded();

	public abstract void setTurnoversConceded(Integer turnoversConceded);

	public abstract Integer getTacklesMade();

	public abstract void setTacklesMade(Integer tacklesMade);

	public abstract Integer getTacklesMissed();

	public abstract void setTacklesMissed(Integer tacklesMissed);

	public abstract Integer getScrumsPutIn();

	public abstract void setScrumsPutIn(Integer scrumsPutIn);

	public abstract Integer getScrumsWonOnOwnPut();

	public abstract void setScrumsWonOnOwnPut(Integer scrumsWonOnOwnPut);

	public abstract Integer getLineoutsThrownIn();

	public abstract void setLineoutsThrownIn(Integer lineoutsThrownIn);

	public abstract Integer getLineoutsWonOnOwnThrow();

	public abstract void setLineoutsWonOnOwnThrow(Integer lineoutsWonOnOwnThrow);

	public abstract Integer getPenaltiesConceded();

	public abstract void setPenaltiesConceded(Integer penaltiesConceded);

	public abstract Integer getYellowCards();

	public abstract void setYellowCards(Integer yellowCards);

	public abstract Integer getRedCards();

	public abstract void setRedCards(Integer redCards);
	
	public abstract Integer getMetersRun();

	public abstract void setMetersRun(Integer metersRun);

	void setTeamId(Long teamId);

	Long getTeamId();

	void setMatchId(Long matchId);

	Long getMatchId();

	public abstract void add(ITeamMatchStats stats);

	Boolean getIsHome();

	void setIsHome(Boolean isHome);

	Date getCreated();

	void setCreated(Date created);

	Date getModified();

	void setModified(Date modified);

	String getTeamAbbr();

	void setTeamAbbr(String teamAbbr);

	List<Long> getTaskIds();

	void setTaskIds(List<Long> taskIds);

	public abstract List<Long> getBlockingTaskIds();

}