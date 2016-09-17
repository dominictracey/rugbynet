package net.rugby.foundation.model.shared;

import net.rugby.foundation.model.shared.Position.position;

public interface ILineupSlot extends IHasId {

	Long getPlayerId();

	void setPlayerId(Long playerId);

	Long getForeignPlayerId();

	void setForeignPlayerId(Long foreignPlayerId);

	int getSlot();

	void setSlot(int slot);

	Long getMatchId();

	void setMatchId(Long matchId);

	Long getForeignMatchId();

	void setForeignMatchId(Long foreignMatchId);
	
	boolean getHome();
	
	void setHome(boolean home);

	IPlayer getPlayer();

	void setPlayer(IPlayer player);

	IMatchGroup getMatch();

	void setMatch(IMatchGroup match);

	position getPos();

	void setPos(position pos);

	String getForeignPlayerName();

	void setForeignPlayerName(String foreignPlayerName);

}