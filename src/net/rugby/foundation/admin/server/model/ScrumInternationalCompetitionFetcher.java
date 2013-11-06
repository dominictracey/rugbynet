package net.rugby.foundation.admin.server.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.ITeamGroup;

public class ScrumInternationalCompetitionFetcher implements
		IForeignCompetitionFetcher {

	private IRoundFactory rf;
	private IMatchGroupFactory mf;
	private IResultFetcherFactory rff;
	private ITeamGroupFactory tf;

	public ScrumInternationalCompetitionFetcher(IRoundFactory rf,
			IMatchGroupFactory mf, IResultFetcherFactory rff,
			ITeamGroupFactory tf) {
		this.rf = rf;
		this.mf = mf;
		this.rff = rff;
		this.tf = tf;
	}

	@Override
	public ICompetition getCompetition(String homePage, List<IRound> rounds,
			List<ITeamGroup> teams) {
		ICompetition comp = new Competition();
		
		rf.setId(null);
		IRound r = rf.getRound();
		r.setAbbr("1");
		r.setBegin(new Date());
		rf.put(r);
		
		comp.getRoundIds().add(r.getId());
		comp.getRounds().add(r);
		
		IMatchGroup m = mf.create();
		
		ITeamGroup h = tf.getTeamByName("New Zealand");
		ITeamGroup v = tf.getTeamByName("England");
		if (h != null) {
			m.setHomeTeam(h);
			m.setVisitingTeam(v);
			m.setHomeTeamId(h.getId());
			m.setVisitingTeamId(v.getId());
		}
		
		r.getMatches().add(m);
		r.getMatchIDs().add(m.getId());
		
		return comp;
	}

	@Override
	public Map<String, ITeamGroup> getTeams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setURL(String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IRound> getRounds(String url, Map<String, IMatchGroup> matches) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IMatchGroup> getMatches(String url,
			Map<String, ITeamGroup> teams) {
		// TODO Auto-generated method stub
		return null;
	}

}
