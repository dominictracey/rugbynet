package net.rugby.foundation.admin.server.factory.test;

import java.util.List;

import net.rugby.foundation.admin.server.factory.IPlayerMatchInfoFactory;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;

public class TestPlayerMatchInfoFactory implements IPlayerMatchInfoFactory {

	@Override
	public IPlayerMatchInfo get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IPlayerMatchInfo> getForComp(Long playerId, Long compId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IPlayerMatchInfo> getForMatch(Long matchId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPlayerMatchInfo getForPlayerMatchStats(Long pmsId) {
		// TODO Auto-generated method stub
		return null;
	}

}