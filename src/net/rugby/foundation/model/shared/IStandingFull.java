package net.rugby.foundation.model.shared;

public interface IStandingFull extends IStanding {

	int getGamesPlayed();

	void setGamesPlayed(int gamesPlayed);

	int getWins();

	void setWins(int wins);

	int getDraws();

	void setDraws(int draws);

	int getLosses();

	void setLosses(int losses);

	int getByes();

	void setByes(int byes);

	int getPointsFor();

	void setPointsFor(int pointsFor);

	int getPointsAgainst();

	void setPointsAgainst(int pointsAgainst);

	int getTriesFor();

	void setTriesFor(int triesFor);

	int getTriesAgainst();

	void setTriesAgainst(int triesAgainst);

	int getBonusPointTries();

	void setBonusPointTries(int bonusPointTries);

	int getBonusPointLosses();

	void setBonusPointLosses(int bonusPointLosses);

	int getBonusPoints();

	void setBonusPoints(int bonusPoints);

	int getPointsDifferential();

	void setPointsDifferential(int pointsDifferential);

	int getPoints();

	void setPoints(int points);

	String getPool();

	void setPool(String pool);

}