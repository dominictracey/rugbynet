package net.rugby.foundation.admin.server.model;

import java.util.List;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;

public interface ILineupFetcher extends IJsonFetcher {

	List<ILineupSlot> get(boolean home);

	IMatchGroup getMatch();

	void setMatch(IMatchGroup match);

	ICompetition getComp();

	void setComp(ICompetition comp);

}