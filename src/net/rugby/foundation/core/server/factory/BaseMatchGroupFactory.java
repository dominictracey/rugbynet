package net.rugby.foundation.core.server.factory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.MatchGroup;

import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.inject.Inject;

public abstract class BaseMatchGroupFactory extends BaseCachingFactory<IMatchGroup> implements IMatchGroupFactory {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 5536925770981961238L;

	protected ITeamGroupFactory tf;
	protected IMatchResultFactory mrf;
	protected IRoundFactory rf;
	protected ITeamMatchStatsFactory tmsf;
	protected IPlayerMatchStatsFactory pmsf;
	protected IPlayerRatingFactory prf;
	protected ICompetitionFactory cf;

	@Inject
	protected void setFactories(IMatchResultFactory mrf, ITeamGroupFactory tf, IRoundFactory rf, ITeamMatchStatsFactory tmsf, IPlayerMatchStatsFactory pmsf,
			IPlayerRatingFactory prf, ICompetitionFactory cf) {

		this.tf = tf;
		this.mrf = mrf;
		this.rf = rf;
		this.tmsf = tmsf;
		this.pmsf = pmsf;
		this.prf = prf;
		this.cf = cf;
	}

	@Override
	public IMatchGroup put(IMatchGroup g) {
		try {
			if (g == null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Don't call put(null), call create()");
				return null;  
			}

			if (g.getHomeTeam() == null || g.getVisitingTeam() == null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Illegal call to put() with teams not set properly");
				return null; 			}

			if (g.getHomeTeam().getId() == null) {
				tf.put(g.getHomeTeam());
			}

			if (g.getVisitingTeam().getId() == null) {
				tf.put(g.getVisitingTeam());
			}

			((MatchGroup)g).setHomeTeamId(g.getHomeTeam().getId());
			((MatchGroup)g).setVisitingTeamId(g.getVisitingTeam().getId());

			// need to retroactively set these 
			if (g.getWorkflowStatus() == null) {
				g.setWorkflowStatus(WorkflowStatus.FETCHED);
			}

			g = super.put(g);

			// force top-level reload
			if (g.getRoundId() != null) {
				rf.invalidate(g.getRoundId());
			}

			return g;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public List<IMatchGroup> getMatchesForRound(Long roundId) {
		IRound r = rf.get(roundId); 
		if (r != null) {
			return r.getMatches();
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Could not find requested Round " + roundId);
			return null;
		}
	}

	@Override
	public boolean delete(IMatchGroup m) {
		try {

			if (m != null) {
				// delete any results

				if (m.getSimpleScoreMatchResultId() != null) {

					if (!mrf.delete((IMatchResult)m.getSimpleScoreMatchResult())) {
						return false;
					}
				}

				// delete any teamMatchStats
				if (!tmsf.deleteForMatch(m)) {
					return false;
				}

				// delete any playerMatchStats
				if (!pmsf.deleteForMatch(m)) {
					return false;
				}

				// delete any playerMatchRatings
				if (!prf.deleteForMatch(m)) {
					return false;
				}

				// purge any pipeline jobs
				if (m.getFetchMatchStatsPipelineId() != null) {
					PipelineService service = PipelineServiceFactory.newPipelineService();

					try {
						service.deletePipelineRecords(m.getFetchMatchStatsPipelineId(),true,false);
					} catch (NoSuchObjectException nsox) {
						// it's ok, just was a dangling reference in the match record
					}
				}

				return super.delete(m);

			} else {
				return true; // ?
			}
		} 	catch (Throwable ex) {
			String id = "null";
			if (m != null) {
				id = m.getId().toString();
			}
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error deleting match " + id + ex.getLocalizedMessage());
			return false;
		}
	}

	final protected String vCompPrefix = "MGF-VCOMP-";

	@Override
	public List<IMatchGroup> getMatchesForVirualComp(int ordinal, Long virtualCompId) {
		try {
			String key = getCacheKey(ordinal,virtualCompId);
			List<IMatchGroup> matches = super.getList(key);

			if (matches == null) {
				matches = getMatchesForVirualCompFromPersistentDatastore(ordinal, virtualCompId);
				super.putList(key, matches);
			}

			return matches;
		} 	catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"getMatchesForVirualComp", ex);
			return null;
		}
	}

	private String getCacheKey(int ordinal, Long virtualCompId) {
		return vCompPrefix+virtualCompId+"-"+ordinal;
	}

	protected abstract List<IMatchGroup> getMatchesForVirualCompFromPersistentDatastore(int ordinal, Long virtualCompId);
	
}
