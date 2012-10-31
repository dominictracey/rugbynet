package net.rugby.foundation.admin.server.factory.espnscrum;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.admin.server.model.ScrumSimpleScoreResultFetcher;
import net.rugby.foundation.admin.server.model.ScrumSuperRugbySimpleScoreResultFetcher;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IMatchResult.ResultType;
import net.rugby.foundation.model.shared.IRound;

public class ScrumResultFetcherFactory implements IResultFetcherFactory {

	private ICompetitionFactory cf;

	@Inject
	public void setFactories(ICompetitionFactory cf) {
		this.cf = cf;
	}
	
	@Override
	public IResultFetcher getResultFetcher(Long sourceCompID, IRound round, IMatchResult.ResultType resultType) {
		if (resultType == ResultType.SIMPLE_SCORE) {
			cf.setId(sourceCompID);
			ICompetition comp = cf.getCompetition();
			
			IResultFetcher fetcher = null;
			if (!comp.getLongName().contains("Super Rugby")) {
				fetcher =  new ScrumSimpleScoreResultFetcher();
			} else {
				fetcher =  new ScrumSuperRugbySimpleScoreResultFetcher();
			}

			

			fetcher.setComp(comp);
			fetcher.setRound(comp.getPrevRound());
			
			return fetcher;
		} else {
			Logger.getLogger("Result Fetcher").log(Level.SEVERE, "Unrecognized resultType requested " + resultType.toString());
			return null;
		}
	}

}
