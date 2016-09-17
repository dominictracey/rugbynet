package net.rugby.foundation.admin.server.factory.espnscrum;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.model.EspnSimpleScoreFetcher;
import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.admin.server.model.ScrumSimpleScoreResultFetcher;
import net.rugby.foundation.admin.server.model.ScrumSuperRugbySimpleScoreResultFetcher;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IMatchResult.ResultType;
import net.rugby.foundation.model.shared.IRound;

public class ScrumResultFetcherFactory implements IResultFetcherFactory {

	private ICompetitionFactory cf;
	private IMatchGroupFactory mf;
	private IMatchResultFactory mrf;
	private IUrlCacher uc;
	private IConfigurationFactory ccf;

	@Inject
	public void setFactories(IConfigurationFactory ccf, ICompetitionFactory cf, IMatchGroupFactory mf, IMatchResultFactory mrf, IUrlCacher uc) {
		this.ccf = ccf;
		this.cf = cf;
		this.mf = mf;
		this.mrf = mrf;
		this.uc = uc;
	}

	@Override
	public IResultFetcher getResultFetcher(Long sourceCompID, IRound round, IMatchResult.ResultType resultType) {
		ICompetition comp = null;
		if (sourceCompID != null) {
			comp = cf.get(sourceCompID);
		}

		IResultFetcher fetcher =  new EspnSimpleScoreFetcher(ccf, mf,mrf);
		fetcher.setComp(comp);
		
		return fetcher;
		
//		if (comp == null) {
//			//Logger.getLogger("Result Fetcher").log(Level.SEVERE, "Unrecognized compId specified: " + sourceCompID);
//			return new ScrumSuperRugbySimpleScoreResultFetcher(mf,mrf,uc);
//		}
//
//		if (resultType == ResultType.SIMPLE_SCORE) {
//
//			IResultFetcher fetcher = null;
//			if (comp != null && !comp.getLongName().contains("Super Rugby")) {
//				fetcher =  new ScrumSimpleScoreResultFetcher(mf,mrf, uc);
//			} else {
//				fetcher =  new ScrumSuperRugbySimpleScoreResultFetcher(mf,mrf,uc);
//			}
//
//			if (comp != null) {
//				fetcher.setComp(comp);
//				fetcher.setRound(comp.getPrevRound());
//			}
//
//			return fetcher;
//		} else if (resultType == ResultType.MATCHES) {
//			IResultFetcher fetcher = new ScrumSuperRugbySimpleScoreResultFetcher(mf,mrf,uc);
//			if (comp != null) {
//				fetcher.setComp(comp);
//				fetcher.setRound(round);
//			}
//			return fetcher;
//		} else {
//			Logger.getLogger("Result Fetcher").log(Level.SEVERE, "Unrecognized resultType requested " + resultType.toString());
//			return null;
//		}
	}

}
