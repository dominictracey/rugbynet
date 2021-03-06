package net.rugby.foundation.admin.server.factory.espnscrum;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.model.EspnCompetitionFetcher;
import net.rugby.foundation.admin.server.model.IForeignCompetitionFetcher;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.core.server.factory.IVenueFactory;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;

public class EspnCompetitionFetcherFactory implements IForeignCompetitionFetcherFactory {

	private IRoundFactory rf = null;
	private IMatchGroupFactory mf;
	private IResultFetcherFactory rff;
	private ITeamGroupFactory tf;
	private IConfigurationFactory ccf;

	private Map<String, IForeignCompetitionFetcher> fetcherMap = new HashMap<String, IForeignCompetitionFetcher>();
	private ICompetitionFactory cf;
	private IUniversalRoundFactory urf;
	private IVenueFactory vf;
	
	@Inject
	public void setFactories(IRoundFactory rf, IMatchGroupFactory mf, IResultFetcherFactory rff, ITeamGroupFactory tf, IConfigurationFactory ccf, 
			ICompetitionFactory cf, IUniversalRoundFactory urf, IVenueFactory vf) {
		this.rf  = rf;
		this.mf = mf;
		this.rff = rff;
		this.tf = tf;
		this.ccf = ccf;
		this.cf = cf;
		this.urf = urf;
		this.vf = vf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory#getForeignCompetitionFetcher(java.lang.String, net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory.CompetitionFetcherType)
	 */
	@Override
	public IForeignCompetitionFetcher getForeignCompetitionFetcher(String url, CompetitionType fetcherType) {
		
		assert(rf!=null);
		
		if (fetcherMap.containsKey(url)) {
			return fetcherMap.get(url);
		} else {
			IForeignCompetitionFetcher scf =  new EspnCompetitionFetcher(rf,mf,rff, tf, ccf, cf, urf, vf);
			if (url != null && !url.isEmpty()) {
				scf.setURL(url);
			}
			fetcherMap.put(url, scf);
			return scf;
		}

	}


}
