package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Id;
import javax.persistence.Transient;

import net.rugby.foundation.model.shared.Position.position;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class ScrumPlayerMatchStats implements Serializable, IPlayerMatchStats {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	@Override
//	public void matchOver(int time) {
//		// TODO Auto-generated method stub
//		
//	}


	/**
	 * 
	 */
	//private static final long serialVersionUID = -5287287099345934745L;

	@Id
	private Long id;

	private Integer tries;
	private Integer tryAssists;
	private Integer points;
	private Integer kicks;
	private Integer passes;
	private Integer runs;
	private Integer metersRun;
	private Integer cleanBreaks;
	private Integer defendersBeaten;
	private Integer offloads;
	private Integer turnovers;
	private Integer tacklesMade;
	private Integer tacklesMissed;
	private Integer lineoutsWonOnThrow;
	private Integer lineoutsStolenOnOppThrow;
	private Integer penaltiesConceded;
	private Integer yellowCards;
	private Integer redCards;
	//@Indexed
	private Integer slot;
	private Long playerId;
	private Long countryId;
	private Long matchId;
	private Long teamId;
	private position pos;
	private String name;
	private String teamAbbr;



	//@REX should implement to be able to save list of Longs.
	@Transient
	private transient List<IPlayerMatchStatTimeLog> timeLogs; // = new ArrayList<IPlayerMatchStatTimeLog>();
	private Integer timePlayed;

	public ScrumPlayerMatchStats() {
		tries = 0;                   
		tryAssists = 0;              
		points = 0;                  
		kicks = 0;                   
		passes = 0;                  
		runs = 0;                    
		metersRun = 0;               
		cleanBreaks = 0;             
		defendersBeaten = 0;         
		offloads = 0;                
		turnovers = 0;               
		tacklesMade = 0;             
		tacklesMissed = 0;           
		lineoutsWonOnThrow = 0;      
		lineoutsStolenOnOppThrow = 0;
		penaltiesConceded = 0;       
		yellowCards = 0;             
		redCards = 0;     
		pos = position.NONE;
	}


	public ScrumPlayerMatchStats(Long id, Integer tries, Integer tryAssists,
			Integer points, Integer kicks, Integer passes, Integer runs,
			Integer metersRun, Integer cleanBreaks, Integer defendersBeaten,
			Integer offloads, Integer turnovers, Integer tacklesMade,
			Integer tacklesMissed, Integer lineoutsWonOnThrow,
			Integer lineoutsStolenOnOppThrow, Integer penaltiesConceded,
			Integer yellowCards, Integer redCards, Integer slot, Long playerId,
			Long matchId, Long teamId, position pos, String name,
			List<IPlayerMatchStatTimeLog> timeLogs, Integer timePlayed) {
		//super();
		this.id = id;
		this.tries = tries;
		this.tryAssists = tryAssists;
		this.points = points;
		this.kicks = kicks;
		this.passes = passes;
		this.runs = runs;
		this.metersRun = metersRun;
		this.cleanBreaks = cleanBreaks;
		this.defendersBeaten = defendersBeaten;
		this.offloads = offloads;
		this.turnovers = turnovers;
		this.tacklesMade = tacklesMade;
		this.tacklesMissed = tacklesMissed;
		this.lineoutsWonOnThrow = lineoutsWonOnThrow;
		this.lineoutsStolenOnOppThrow = lineoutsStolenOnOppThrow;
		this.penaltiesConceded = penaltiesConceded;
		this.yellowCards = yellowCards;
		this.redCards = redCards;
		this.slot = slot;
		this.playerId = playerId;
		this.matchId = matchId;
		this.teamId = teamId;
		this.pos = pos;
		this.name = name;
		this.timeLogs = timeLogs;
		this.timePlayed = timePlayed;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getTries()
	 */
	@Override
	public Integer getTries() {
		return tries;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setTries(java.lang.Integer)
	 */
	@Override
	public void setTries(Integer tries) {
		this.tries = tries;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getTryAssists()
	 */
	@Override
	public Integer getTryAssists() {
		return tryAssists;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setTryAssists(java.lang.Integer)
	 */
	@Override
	public void setTryAssists(Integer tryAssists) {
		this.tryAssists = tryAssists;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getPoints()
	 */
	@Override
	public Integer getPoints() {
		return points;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setPoints(java.lang.Integer)
	 */
	@Override
	public void setPoints(Integer points) {
		this.points = points;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getKicks()
	 */
	@Override
	public Integer getKicks() {
		return kicks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setKicks(java.lang.Integer)
	 */
	@Override
	public void setKicks(Integer kicks) {
		this.kicks = kicks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getPasses()
	 */
	@Override
	public Integer getPasses() {
		return passes;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setPasses(java.lang.Integer)
	 */
	@Override
	public void setPasses(Integer passes) {
		this.passes = passes;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getRuns()
	 */
	@Override
	public Integer getRuns() {
		return runs;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setRuns(java.lang.Integer)
	 */
	@Override
	public void setRuns(Integer runs) {
		this.runs = runs;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getMetersRun()
	 */
	@Override
	public Integer getMetersRun() {
		return metersRun;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setMetersRun(java.lang.Integer)
	 */
	@Override
	public void setMetersRun(Integer metersRun) {
		this.metersRun = metersRun;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getCleanBreaks()
	 */
	@Override
	public Integer getCleanBreaks() {
		return cleanBreaks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setCleanBreaks(java.lang.Integer)
	 */
	@Override
	public void setCleanBreaks(Integer cleanBreaks) {
		this.cleanBreaks = cleanBreaks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getDefendersBeaten()
	 */
	@Override
	public Integer getDefendersBeaten() {
		return defendersBeaten;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setDefendersBeaten(java.lang.Integer)
	 */
	@Override
	public void setDefendersBeaten(Integer defendersBeaten) {
		this.defendersBeaten = defendersBeaten;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getOffloads()
	 */
	@Override
	public Integer getOffloads() {
		return offloads;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setOffloads(java.lang.Integer)
	 */
	@Override
	public void setOffloads(Integer offloads) {
		this.offloads = offloads;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getTurnovers()
	 */
	@Override
	public Integer getTurnovers() {
		return turnovers;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setTurnovers(java.lang.Integer)
	 */
	@Override
	public void setTurnovers(Integer turnovers) {
		this.turnovers = turnovers;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getTacklesMade()
	 */
	@Override
	public Integer getTacklesMade() {
		return tacklesMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setTacklesMade(java.lang.Integer)
	 */
	@Override
	public void setTacklesMade(Integer tacklesMade) {
		this.tacklesMade = tacklesMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getTacklesMissed()
	 */
	@Override
	public Integer getTacklesMissed() {
		return tacklesMissed;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setTacklesMissed(java.lang.Integer)
	 */
	@Override
	public void setTacklesMissed(Integer tacklesMissed) {
		this.tacklesMissed = tacklesMissed;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getLineoutsWonOnThrow()
	 */
	@Override
	public Integer getLineoutsWonOnThrow() {
		return lineoutsWonOnThrow;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setLineoutsWonOnThrow(java.lang.Integer)
	 */
	@Override
	public void setLineoutsWonOnThrow(Integer lineoutsWonOnThrow) {
		this.lineoutsWonOnThrow = lineoutsWonOnThrow;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getLineoutsStolenOnOppThrow()
	 */
	@Override
	public Integer getLineoutsStolenOnOppThrow() {
		return lineoutsStolenOnOppThrow;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setLineoutsStolenOnOppThrow(java.lang.Integer)
	 */
	@Override
	public void setLineoutsStolenOnOppThrow(Integer lineoutsStolenOnOppThrow) {
		this.lineoutsStolenOnOppThrow = lineoutsStolenOnOppThrow;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getPenaltiesConceded()
	 */
	@Override
	public Integer getPenaltiesConceded() {
		return penaltiesConceded;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setPenaltiesConceded(java.lang.Integer)
	 */
	@Override
	public void setPenaltiesConceded(Integer penaltiesConceded) {
		this.penaltiesConceded = penaltiesConceded;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getYellowCards()
	 */
	@Override
	public Integer getYellowCards() {
		return yellowCards;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setYellowCards(java.lang.Integer)
	 */
	@Override
	public void setYellowCards(Integer yellowCards) {
		this.yellowCards = yellowCards;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getRedCards()
	 */
	@Override
	public Integer getRedCards() {
		return redCards;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setRedCards(java.lang.Integer)
	 */
	@Override
	public void setRedCards(Integer redCards) {
		this.redCards = redCards;
	}
	@Override
	public Long getPlayerId() {
		return playerId;
	}
	@Override
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}
	@Override
	public Long getMatchId() {
		return matchId;
	}
	@Override
	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}
	@Override
	public Long getTeamId() {
		return teamId;
	}
	@Override
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}
	@Override
	public position getPosition() {
		return pos;
	}
	@Override
	public void setPosition(Position.position pos) {
		this.pos = pos;
	}
//	@Override
	public void addTimeLog(IPlayerMatchStatTimeLog log) {
		if (timeLogs == null) {
			timeLogs = new ArrayList<IPlayerMatchStatTimeLog>();
		}

		timeLogs.add(log);

	}
	@Override
	public void removeTimeLog(Long logId) {
		if (timeLogs == null) {
			return;
		}

		for (IPlayerMatchStatTimeLog log : timeLogs) {
			if (log.getId().equals(id)) {
				timeLogs.remove(log);
			}
		}
	}
	@Override
	public List<IPlayerMatchStatTimeLog> getTimeLogs() {
		return timeLogs;
	}
	
	@Override
	public void setTimeLogs(List<IPlayerMatchStatTimeLog> logs) {
		timeLogs = logs;
		
	}
	
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public Integer getTimePlayed() {
		return timePlayed;
	}
	@Override
	public void setTimePlayed(Integer timePlayed) {
		this.timePlayed = timePlayed;
	}
	@Override
	public void playerOn(int i) {
		if (timeLogs == null) {
			timeLogs = new ArrayList<IPlayerMatchStatTimeLog>();
		}

		IPlayerMatchStatTimeLog tl;
		if (timeLogs.size() == 0) {
			tl = new ScrumPlayerMatchStatTimeLog();
			tl.start(i, pos, playerId, matchId);
			timeLogs.add(tl);
		} else {

			tl = timeLogs.get(timeLogs.size()-1);
			if (i != 0 && tl.getTimeOff() == 0) {
				// this is bad -- somehow we think they are already on
				throw new RuntimeException("playerOn: The player " + name + " in slot " + slot + " of match " + matchId +  " is already on the field at " + i + " minutes.");			
			} else {
				// they are re-entering the game - blood bin?
				tl = new ScrumPlayerMatchStatTimeLog();
				tl.start(i, pos, playerId, matchId);
				timeLogs.add(tl);
			}
		}

	}
	@Override
	public void playerOff(int i) {
		if (timeLogs == null) {
			timeLogs = new ArrayList<IPlayerMatchStatTimeLog>();
		}

		IPlayerMatchStatTimeLog tl;
		if (timeLogs.size() == 0) {
			// this is bad -- somehow we think they never got on
			throw new RuntimeException("playerOff: The player " + name + " in match " + matchId +  " isn't on the field at " + i + " minutes.");		
		}

		tl = timeLogs.get(timeLogs.size()-1);
		if (tl.getTimeOff() != 0) {
			// this is bad -- somehow we think they never got on
			throw new RuntimeException("playerOff: The player " + name + " in match " + matchId +  " isn't on the field at " + i + " minutes.");			
		} else {
			tl.stop(i);
		}


	}
	@Override
	public void matchOver(int time) {
		if (timeLogs != null) {
			if (timeLogs.size() == 0) {
				return;
			} else {
				IPlayerMatchStatTimeLog tl = timeLogs.get(timeLogs.size()-1);
				if (tl.getTimeOff() == 0) {
					tl.stop(time);
				}

			}
		} 

		timePlayed = 0;
		if (timeLogs != null) {
			for (IPlayerMatchStatTimeLog tl : timeLogs) {
				timePlayed += tl.getTimeOff()-tl.getTimeOn();
			}
		} else {
			//throw new RuntimeException("No time logs for " + name + playerId);
		}

		return;

	}

	@Override
	public Integer getSlot() {
		return slot;
	}
	@Override
	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	@Override
	public Long getCountryId() {
		return countryId;
	}

	@Override
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	@Override
	public String getTeamAbbr() {
		return teamAbbr;
	}

	@Override
	public void setTeamAbbr(String teamAbbr) {
		this.teamAbbr = teamAbbr;
	}

	@Override
	public int isForward() {
		if (	getPosition().equals(position.PROP) ||
				getPosition().equals(position.HOOKER) ||
				getPosition().equals(position.LOCK) ||
				getPosition().equals(position.FLANKER) ||
				getPosition().equals(position.NUMBER8) ) {
			return 1;
		} else {
			return 0;
		}
	}

}
