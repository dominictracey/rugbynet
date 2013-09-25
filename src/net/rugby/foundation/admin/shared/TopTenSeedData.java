package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.Position.position;

public class TopTenSeedData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6761184923411466077L;
	protected List<IPlayerMatchInfo> pmiList;
	protected String title;
	protected String description;
	protected Long compId;
	protected Long roundId;
	protected position pos;
	protected Long countryId;
	protected Long teamId;
	protected int playersPerTeam = 10;
	
	public TopTenSeedData() {
		
	}
	
	public TopTenSeedData(List<IPlayerMatchInfo> pmiList, String title,
			String description, Long compId, Long roundId, position pos,
			Long countryId, Long teamId, int playersPerTeam) {
		super();
		this.pmiList = pmiList;
		this.title = title;
		this.description = description;
		this.compId = compId;
		this.roundId = roundId;
		this.pos = pos;
		this.countryId = countryId;
		this.teamId = teamId;
		this.playersPerTeam = playersPerTeam;
	}
	public List<IPlayerMatchInfo> getPmiList() {
		return pmiList;
	}
	public void setPmiList(List<IPlayerMatchInfo> pmiList) {
		this.pmiList = pmiList;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getCompId() {
		return compId;
	}
	public void setCompId(Long compId) {
		this.compId = compId;
	}
	public Long getRoundId() {
		return roundId;
	}
	public void setRoundId(Long roundId) {
		this.roundId = roundId;
	}
	public position getPos() {
		return pos;
	}
	public void setPos(position pos) {
		this.pos = pos;
	}
	public Long getCountryId() {
		return countryId;
	}
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}
	public Long getTeamId() {
		return teamId;
	}
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	public int getPlayersPerTeam() {
		return playersPerTeam;
	}

	public void setPlayersPerTeam(int playersPerTeam) {
		this.playersPerTeam = playersPerTeam;
	}
}
