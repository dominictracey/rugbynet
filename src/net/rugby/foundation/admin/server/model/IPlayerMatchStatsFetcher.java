package net.rugby.foundation.admin.server.model;

import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;


public interface IPlayerMatchStatsFetcher {

	public boolean process();
	void set(IPlayerMatchStats stats);
	IPlayerMatchStats getStats();
	void setUrl(String url);
	String getUrl();
	void setSlot(Integer slot);
	Integer getSlot();
	void setHov(Home_or_Visitor hov);
	Home_or_Visitor getHov();
	void setMatch(IMatchGroup match);
	IMatchGroup getMatch();
	void setPlayer(IPlayer player);
	IPlayer getPlayer();
	String getErrorMessage();

}
