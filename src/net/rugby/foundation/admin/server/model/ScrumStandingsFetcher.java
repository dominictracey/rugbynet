package net.rugby.foundation.admin.server.model;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;

public class ScrumStandingsFetcher implements IStandingsFetcher {

	private IRound r = null;
	private ICompetition c = null;
	private IUrlCacher uc;
	private IStandingFactory sf;
	private String url;
	
	ScrumStandingsFetcher(IRound r, ICompetition c, String url, IUrlCacher uc, IStandingFactory sf) {
		this.r = r;
		this.c = c;
		this.url = url;
		this.uc = uc;
		this.sf = sf;
	}
	
	
	@Override
	public void setRound(IRound r) {
		this.r = r;

	}

	@Override
	public void setComp(ICompetition c) {
		this.c = c;

	}

	@Override
	public boolean getStandings() {

		try {
		uc.setUrl(url);
		Iterator<String> it = uc.get().iterator();
		
		// do some stuff and find the standings from the ?template=pointstable file
		// testData is http://www.espnscrum.com/heineken-cup-2013-14/rugby/series/191757.html?template=pointstable on 10/13/13
		
		
		for (ITeamGroup t: c.getTeams()) {
			
			IStanding s = sf.create();
			s.setRound(r);
			s.setRoundId(r.getId());
			s.setTeam(t);
			s.setTeamId(t.getId());
			
			s.setStanding(9999); // << There's the thing you have to fill in
			
			sf.put(s);
		}
		return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,ex.getMessage(),ex);
			return false;
		}
	}

}
