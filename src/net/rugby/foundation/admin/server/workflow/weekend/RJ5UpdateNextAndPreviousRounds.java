package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IStandingsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.workflow.weekend.results.RS5UpdateNextAndPreviousRoundsResult;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class RJ5UpdateNextAndPreviousRounds extends Job1<RS5UpdateNextAndPreviousRoundsResult, Long> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IRoundFactory rf;
	transient private IStandingsFetcherFactory sfff;
	transient private ICompetitionFactory cf;
	transient private IUrlCacher uc;
	transient private IStandingFactory sf;
	
	public RJ5UpdateNextAndPreviousRounds() {
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
	}

	/**
	 * @param roundId - the id of the round you finished processing
	 */
	@Override
	public Value<RS5UpdateNextAndPreviousRoundsResult> run(Long roundId) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.rf = injector.getInstance(IRoundFactory.class);
			this.cf = injector.getInstance(ICompetitionFactory.class);
			this.sfff = injector.getInstance(IStandingsFetcherFactory.class);
			this.uc = injector.getInstance(IUrlCacher.class);
			this.sf = injector.getInstance(IStandingFactory.class);
			
			IRound r = rf.get(roundId);
			
			RS5UpdateNextAndPreviousRoundsResult retval = new RS5UpdateNextAndPreviousRoundsResult();
			retval.log.add("Standings Fetcher");
			retval.roundId = roundId;
			retval.success = true;			
			if (r != null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, this.getJobDisplayName() + ": Fetching standings for " + r.getName());

				if (r.getCompId() != null) {
					ICompetition c = cf.get(r.getCompId());
					if (c != null) {
						c.setPrevRoundIndex(c.getPrevRoundIndex()+1);
						if (c.getRoundIds().size() > c.getNextRound().getOrdinal()) {
							c.setNextRoundIndex(c.getNextRoundIndex()+1);
						} else {
							c.setNextRoundIndex(-1);
						}
						
						cf.put(c);
						
					} else {
						retval.log.add("Bad comp");
						retval.success = false;
					}
				} else {
					retval.log.add("Bad compId in round");
					retval.success = false;
				}
			} else {
				retval.log.add("Bad round.");
				retval.success = false;
			}
			return immediate(retval);
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);

			RS5UpdateNextAndPreviousRoundsResult retval = new RS5UpdateNextAndPreviousRoundsResult();
			retval.log.add("Exception caught in Next and Prev Round updater: " + ex.getLocalizedMessage());
			
			return immediate(retval);
		}

	}

}
