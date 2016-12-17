package net.rugby.foundation.core.server.factory;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.CoreConfiguration.Environment;


public abstract class BaseConfigurationFactory extends BaseCachingFactory<ICoreConfiguration> implements IConfigurationFactory {

	private Long id = null;
	protected ICompetitionFactory cf;
	protected IUniversalRoundFactory urf;
	protected IRatingSeriesFactory rsf;
	
	public BaseConfigurationFactory(ICompetitionFactory cf, IUniversalRoundFactory urf, IRatingSeriesFactory rsf) {
		this.cf = cf;
		this.urf = urf;
		this.rsf = rsf;
	}

	// since there is only one, this convenience method gets it
	@Override
	public ICoreConfiguration get() {
		if (id != null) {
			return get(id);
		} else {
			ICoreConfiguration cc = getFromPersistentDatastore(0L);
			id = cc.getId();
			
			
			// now we have to put it in memcache
			putItem(cc.getId(),cc);
			return cc;
		}
	}
	
	@Override
	public ICoreConfiguration put(ICoreConfiguration c) {
		
//		if (c != null && c.getCompsUnderway().size() > 1) {
//			Collections.sort(c.getCompsUnderway(), new Comparator<Long>() {
//				@Override
//				public int compare(Long o1, Long o2) {
//					ICompetition c1 = cf.get(o1);
//					ICompetition c2 = cf.get(o2);
//					if (c1 != null && c2 != null && c1.getWeightingFactor() != null && c2.getWeightingFactor() != null)
//						return Float.compare(c2.getWeightingFactor(), c1.getWeightingFactor());
//					else 
//						return -1;
//				}
//			});
//		}
//		if (c != null && c.getCompsForClient().size() > 1) {
//			Collections.sort(c.getCompsForClient(), new Comparator<Long>() {
//				@Override
//				public int compare(Long o1, Long o2) {
//					ICompetition c1 = cf.get(o1);
//					ICompetition c2 = cf.get(o2);
//					if (c1 != null && c2 != null && c1.getWeightingFactor() != null && c2.getWeightingFactor() != null)
//						return Float.compare(c2.getWeightingFactor(), c1.getWeightingFactor());
//					else 
//						return -1;
//				}
//			});
//		}
//		
		return super.put(c);
		
	}
}
