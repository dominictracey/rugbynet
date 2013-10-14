package net.rugby.foundation.admin.server.model;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;

public class ScrumStandingsFetcher implements IStandingsFetcher {

	private IRound r = null;
	private ICompetition c = null;
	private IUrlCacher uc = null;
	private IStandingFactory sf;
	private String url = null;
	
	@Inject
	ScrumStandingsFetcher(IStandingFactory sf) {
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
	public IStanding getStandingForTeam(ITeamGroup t) {

		assert (c != null);
		assert (r != null);
		assert (url != null);
		assert (uc != null);
		
		try {
		uc.setUrl(url);
		Iterator<String> it = uc.get().iterator();
		
		// do some stuff and find the standings from the ?template=pointstable file
		// testData is http://www.espnscrum.com/heineken-cup-2013-14/rugby/series/191757.html?template=pointstable on 10/13/13
		
		

		
		IStanding s = sf.create();
		s.setRound(r);
		s.setRoundId(r.getId());
		s.setTeam(t);
		s.setTeamId(t.getId());
		
		s.setStanding(9999); // << There's the thing you have to fill in
		
		return s;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,ex.getMessage(),ex);
			return null;
		}
	}


	public IUrlCacher getUc() {
		return uc;
	}

	@Override
	public void setUc(IUrlCacher uc) {
		this.uc = uc;
	}


	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

}
