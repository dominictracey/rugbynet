package net.rugby.foundation.admin.server.factory.espnscrum;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.model.IForeignCompetitionFetcher;
import net.rugby.foundation.admin.server.model.ScrumCompetitionFetcher;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;

public class ScrumCompetitionFetcherFactory implements IForeignCompetitionFetcherFactory {

	private IRoundFactory rf = null;
	private IMatchGroupFactory mf;

	@Override
	public void setFactories(IRoundFactory rf, IMatchGroupFactory mf) {
		this.rf  = rf;
		this.mf = mf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory#getForeignCompetitionFetcher(java.lang.String, net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory.CompetitionFetcherType)
	 */
	@Override
	public IForeignCompetitionFetcher getForeignCompetitionFetcher(String url, CompetitionFetcherType fetcherType) {
		
		assert(rf!=null);
		
		if (fetcherType == CompetitionFetcherType.ESPNSCRUM_BASIC) {
			ScrumCompetitionFetcher scf =  new ScrumCompetitionFetcher(rf,mf);
			scf.setURL(url);
			return scf;
		} else {
			Logger.getLogger("CompetitionFetcherFactory").log(Level.SEVERE, "Unrecognized fetcherType requested " + fetcherType.toString());
			return null;
		}
	}


}
