package net.rugby.foundation.admin.server.factory.espnscrum;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.model.EspnCompetitionFetcher;
import net.rugby.foundation.admin.server.model.IForeignCompetitionFetcher;
import net.rugby.foundation.admin.server.model.ScrumCompetitionFetcher;
import net.rugby.foundation.admin.server.model.ScrumInternationalCompetitionFetcher;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;

public class EspnCompetitionFetcherFactory implements IForeignCompetitionFetcherFactory {

	private IRoundFactory rf = null;
	private IMatchGroupFactory mf;
	private IResultFetcherFactory rff;
	private ITeamGroupFactory tf;
    private IUrlCacher uc;
	private IConfigurationFactory ccf;

	@Inject
	public void setFactories(IRoundFactory rf, IMatchGroupFactory mf, IResultFetcherFactory rff, ITeamGroupFactory tf, IUrlCacher uc, IConfigurationFactory ccf) {
		this.rf  = rf;
		this.mf = mf;
		this.rff = rff;
		this.tf = tf;
		this.uc = uc;
		this.ccf = ccf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory#getForeignCompetitionFetcher(java.lang.String, net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory.CompetitionFetcherType)
	 */
	@Override
	public IForeignCompetitionFetcher getForeignCompetitionFetcher(String url, CompetitionType fetcherType) {
		
		assert(rf!=null);
		
		IForeignCompetitionFetcher scf =  new EspnCompetitionFetcher(rf,mf,rff, tf, ccf);
		scf.setURL(url);
		return scf;
		

	}


}
