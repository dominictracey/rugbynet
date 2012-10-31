/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.admin.shared.AdminOrchestrationActions.CompActions;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.MatchActions;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.OrchestrationConfiguration;

/**
 * @author home
 *
 */
public class TestOrchestrationConfigurationFactory implements
		IOrchestrationConfigurationFactory {

	private Long id;
	private Long compId;
	
	public TestOrchestrationConfigurationFactory() {
		
		// for guice
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationConfigurationFactory#get()
	 */
	@Override
	public IOrchestrationConfiguration get() {

		IOrchestrationConfiguration oc = new OrchestrationConfiguration();
		oc.setCompID(id);
		oc.setId(id);
		oc.setSimpleMatchResultScrum(true);
		
		oc.getCompActions().put(CompActions.COMP_COMPLETE.getValue(), true);
		oc.getCompActions().put(CompActions.UPDATENEXTANDPREV.getValue(), true);
		
		oc.getMatchActions().put(MatchActions.FETCH.getValue(), true);
		oc.getMatchActions().put(MatchActions.LOCK.getValue(), true);
		oc.getMatchActions().put(MatchActions.MATCH_STALE_MARK_UNREPORTED.getValue(), true);
		oc.getMatchActions().put(MatchActions.MATCH_STALE_NEEDS_ATTENTION.getValue(), true);
		
		return oc;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationConfigurationFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
		this.compId = null;
		
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationConfigurationFactory#setCompId(java.lang.Long)
	 */
	@Override
	public void setCompId(Long compId) {
		this.id = null;
		this.compId = compId;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationConfigurationFactory#getAll()
	 */
	@Override
	public List<IOrchestrationConfiguration> getAll() {
		
		List<IOrchestrationConfiguration> all = new ArrayList<IOrchestrationConfiguration>();
		setId(1L);
		all.add(get());
		
		// and one for 2L
		IOrchestrationConfiguration config = get();
		config.setId(2L);
		config.setCompID(2L);
		
		all.add(config);
		return all;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationConfigurationFactory#put(net.rugby.foundation.admin.shared.IOrchestrationConfiguration)
	 */
	@Override
	public IOrchestrationConfiguration put(IOrchestrationConfiguration oc) {
		return oc;
		
	}

}
