package net.rugby.foundation.core.server.factory;

import java.util.Collections;
import java.util.Comparator;
import org.joda.time.DateTime;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;


public abstract class BaseConfigurationFactory extends BaseCachingFactory<ICoreConfiguration> implements IConfigurationFactory {

	private Long id = null;
	protected ICompetitionFactory cf;
	private IUniversalRoundFactory urf;
	
	public BaseConfigurationFactory(ICompetitionFactory cf, IUniversalRoundFactory urf) {
		this.cf = cf;
		this.urf = urf;
	}

	// since there is only one, this convenience method gets it
	@Override
	public ICoreConfiguration get() {
		if (id != null) {
			return get(id);
		} else {
			ICoreConfiguration cc = getFromPersistentDatastore(0L);
			id = cc.getId();
			cc.setCurrentUROrdinal(urf.get(new DateTime()).ordinal);
			return cc;
		}
	}
	
	@Override
	public ICoreConfiguration put(ICoreConfiguration c) {
		
		
		Collections.sort(c.getCompsUnderway(), new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				ICompetition c1 = cf.get(o1);
				ICompetition c2 = cf.get(o2);
				return Float.compare(c2.getWeightingFactor(), c1.getWeightingFactor());
			}
		});

		Collections.sort(c.getCompsForClient(), new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				ICompetition c1 = cf.get(o1);
				ICompetition c2 = cf.get(o2);
				return Float.compare(c2.getWeightingFactor(), c1.getWeightingFactor());
			}
		});

		
		return super.put(c);
		
	}
}
