package net.rugby.foundation.admin.server.factory.espnscrum;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.model.IForeignCompetitionFetcher;
import net.rugby.foundation.admin.server.model.ScrumCompetitionFetcher;
import net.rugby.foundation.admin.server.model.ScrumInternationalCompetitionFetcher;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;

public class ScrumCompetitionFetcherFactory implements IForeignCompetitionFetcherFactory {

	private IRoundFactory rf = null;
	private IMatchGroupFactory mf;
	private IResultFetcherFactory rff;
	private ITeamGroupFactory tf;
    private IUrlCacher uc;

	@Inject
	public void setFactories(IRoundFactory rf, IMatchGroupFactory mf, IResultFetcherFactory rff, ITeamGroupFactory tf, IUrlCacher uc) {
		this.rf  = rf;
		this.mf = mf;
		this.rff = rff;
		this.tf = tf;
		this.uc = uc;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory#getForeignCompetitionFetcher(java.lang.String, net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory.CompetitionFetcherType)
	 */
	@Override
	public IForeignCompetitionFetcher getForeignCompetitionFetcher(String url, CompetitionType fetcherType) {
		
		assert(rf!=null);
		
		if (fetcherType == CompetitionType.AUTUMN_INTERNATIONALS) {
			IForeignCompetitionFetcher scf =  new ScrumInternationalCompetitionFetcher(rf,mf,rff, tf, uc);
			scf.setURL(url);
			return scf;
		} else  {
			IForeignCompetitionFetcher scf =  new ScrumCompetitionFetcher(rf,mf,rff, tf);
			scf.setURL(url);
			return scf;
		} 

	}


}
