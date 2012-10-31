/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

/**
 * @author home
 *
 */

public class TestOrchestrationFactory {//implements
//		IOrchestrationFactory  {
//
//	private ICompetitionFactory cf;
//	private IMatchGroupFactory mf;
//	private IOrchestrationConfigurationFactory ocf;
//	private IResultFetcherFactory rff;
//	private IMatchResultFactory mrf;
//
//	@Inject
//	public void setFactories(ICompetitionFactory cf, IMatchGroupFactory mf, IOrchestrationConfigurationFactory ocf,
//			IResultFetcherFactory rff, IMatchResultFactory mrf) {
//		this.cf = cf;
//		this.mf = mf;
//		this.ocf = ocf;
//		this.rff = rff;
//		this.mrf = mrf;
//	}
//	
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.model.shared.IMatchGroup, net.rugby.foundation.admin.server.factory.IOrchestrationActions)
//	 */
//	@Override
//	public IOrchestration<IMatchGroup> get(IMatchGroup target,
//			IOrchestrationActions<IMatchGroup> action) {
//		if (action.equals(AdminOrchestrationActions.MatchActions.FETCH.getValue())) {
//			IOrchestration<IMatchGroup> o = new FetchBasicScoreMatchResultOrchestration();	
//			o.setTarget(target);
//			return o;
//		} else if (action.equals(AdminOrchestrationActions.MatchActions.LOCK)) {
//			IOrchestration<IMatchGroup> o = new LockMatchOrchestration(mf, ocf);
//			o.setTarget(target);
//			return o;		
//		} else if (action.equals(AdminOrchestrationActions.MatchActions.MATCH_STALE_NEEDS_ATTENTION.getValue())) {
//			IOrchestration<IMatchGroup> o = new MatchStaleNeedsAttentionOrchestration();
//			o.setTarget(target);
//			return o;
//		} else if (action.equals(AdminOrchestrationActions.MatchActions.MATCH_STALE_MARK_UNREPORTED.getValue())) {
//			IOrchestration<IMatchGroup> o = new MatchStaleMarkUnreportedOrchestration();
//			o.setTarget(target);
//			return o;
//		}
//		
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.model.shared.ICompetition, net.rugby.foundation.admin.server.factory.IOrchestrationActions)
//	 */
//	@Override
//	public IOrchestration<ICompetition> get(ICompetition target,
//			IOrchestrationActions<ICompetition> action) {
//		if (action.equals(AdminOrchestrationActions.CompActions.UPDATENEXTANDPREV.getValue())) {
//			IOrchestration<ICompetition> o = new UpdateNextAndPrevOrchestration();
//			o.setTarget(target);
//			return o;
//		} else if (action.equals(AdminOrchestrationActions.CompActions.COMP_COMPLETE.getValue())) {
//			IOrchestration<ICompetition> o = new CompetitionCompleteOrchestration();
//			o.setTarget(target);
//			return o;
//		}		
//		
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.model.shared.IRound, net.rugby.foundation.admin.server.factory.IOrchestrationActions)
//	 */
//	@Override
//	public IOrchestration<IRound> get(IRound target,
//			IOrchestrationActions<IRound> action) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.model.shared.IAppUser, net.rugby.foundation.admin.server.factory.IOrchestrationActions)
//	 */
//	@Override
//	public IOrchestration<IAppUser> get(IAppUser target,
//			IOrchestrationActions<IAppUser> action) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.game1.shared.ILeague, net.rugby.foundation.admin.server.factory.IOrchestrationActions)
//	 */
//	@Override
//	public IOrchestration<ILeague> get(ILeague target,
//			IOrchestrationActions<ILeague> action) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
