package net.rugby.foundation.model.shared;

import java.util.Date;


import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class SimpleScoreMatchResult extends MatchResult implements ISimpleScoreMatchResult {

	private static final long serialVersionUID = 1L;

	private int homeScore; // match points
	private int visitScore;
	
	public SimpleScoreMatchResult() {
		
	}

	public SimpleScoreMatchResult(Long matchID, Date recordedDate, String source) {
		super(matchID, recordedDate, source);
		setType(IMatchResult.ResultType.SIMPLE_SCORE);
	}

	public SimpleScoreMatchResult(int homeScore, int visitScore) {
		super();
		this.homeScore = homeScore;
		this.visitScore = visitScore;

		setType(IMatchResult.ResultType.SIMPLE_SCORE);
	}

	public SimpleScoreMatchResult(Long matchID, Date recordedDate, String source,
			int homeScore, int visitScore) {
		super(matchID, recordedDate, source);
		this.homeScore = homeScore;
		this.visitScore = visitScore;

		setType(IMatchResult.ResultType.SIMPLE_SCORE);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISimpleScoreMatchResult#getHomeScore()
	 */
	@Override
	public int getHomeScore() {
		return homeScore;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISimpleScoreMatchResult#setHomeScore(int)
	 */
	@Override
	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISimpleScoreMatchResult#getVisitScore()
	 */
	@Override
	public int getVisitScore() {
		return visitScore;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISimpleScoreMatchResult#setVisitScore(int)
	 */
	@Override
	public void setVisitScore(int visitScore) {
		this.visitScore = visitScore;
	}
	
	@Override
	public String toString() {
		assert getMatch() != null;
		return getMatch().getHomeTeam().getDisplayName() + " " + homeScore + " - " + getMatch().getVisitingTeam().getDisplayName() + " " + visitScore;
	}
	
}
