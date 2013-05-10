package net.rugby.foundation.model.shared;

import java.util.List;

import net.rugby.foundation.model.shared.Position.position;

public interface IPlayerMatchStats {

	public abstract Long getId();

	public abstract void setId(Long id);
	
	public abstract Long getPlayerId();

	public abstract void setPlayerId(Long playerId);

	public abstract Long getMatchId();

	public abstract void setMatchId(Long matchId);
	
	public abstract Long getTeamId();

	public abstract void setTeamId(Long teamId);

	public abstract position getPosition();

	public abstract void setPosition(position pos);

	public abstract Integer getTries();

	public abstract void setTries(Integer tries);

	public abstract Integer getTryAssists();

	public abstract void setTryAssists(Integer tryAssists);

	public abstract Integer getPoints();

	public abstract void setPoints(Integer points);

	public abstract Integer getKicks();

	public abstract void setKicks(Integer kicks);

	public abstract Integer getPasses();

	public abstract void setPasses(Integer passes);

	public abstract Integer getRuns();

	public abstract void setRuns(Integer runs);

	public abstract Integer getMetersRun();

	public abstract void setMetersRun(Integer metersRun);

	public abstract Integer getCleanBreaks();

	public abstract void setCleanBreaks(Integer cleanBreaks);

	public abstract Integer getDefendersBeaten();

	public abstract void setDefendersBeaten(Integer defendersBeaten);

	public abstract Integer getOffloads();

	public abstract void setOffloads(Integer offloads);

	public abstract Integer getTurnovers();

	public abstract void setTurnovers(Integer turnovers);

	public abstract Integer getTacklesMade();

	public abstract void setTacklesMade(Integer tacklesMade);

	public abstract Integer getTacklesMissed();

	public abstract void setTacklesMissed(Integer tacklesMissed);

	public abstract Integer getLineoutsWonOnThrow();

	public abstract void setLineoutsWonOnThrow(Integer lineoutsWonOnThrow);

	public abstract Integer getLineoutsStolenOnOppThrow();

	public abstract void setLineoutsStolenOnOppThrow(
			Integer lineoutsStolenOnOppThrow);

	public abstract Integer getPenaltiesConceded();

	public abstract void setPenaltiesConceded(Integer penaltiesConceded);

	public abstract Integer getYellowCards();

	public abstract void setYellowCards(Integer yellowCards);

	public abstract Integer getRedCards();

	public abstract void setRedCards(Integer redCards);
	
	public abstract void addTimeLog(IPlayerMatchStatTimeLog log);
	
	public abstract void removeTimeLog(Long logId);
	
	public abstract List<IPlayerMatchStatTimeLog> getTimeLogs();
	
	public abstract void  setTimeLogs(List<IPlayerMatchStatTimeLog> logs);
	
	public abstract void setName(String string);
	
	public abstract String getName();

	public abstract Integer getTimePlayed();

	public abstract void setTimePlayed(Integer timePlayed);
	
	public abstract Integer getSlot();

	public abstract void setSlot(Integer slot);

	public abstract void playerOn(int i);
	
	public abstract void playerOff(int i);

	public abstract void matchOver(int time);

	public abstract Long getCountryId();

	public abstract void setCountryId(Long countryId);
	

}