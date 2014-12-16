package net.rugby.foundation.core.server.factory;

import java.util.Collections;
import java.util.Comparator;

import com.google.inject.Inject;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IPlayerMatchStats;


public abstract class BaseConfigurationFactory extends BaseCachingFactory<ICoreConfiguration> implements IConfigurationFactory {

	private Long id = null;
	protected ICompetitionFactory cf;
	
	@Inject
	public BaseConfigurationFactory(ICompetitionFactory cf) {
		this.cf = cf;
	}

	// since there is only one, this convenience method gets it
	@Override
	public ICoreConfiguration get() {
		if (id != null) {
			return get(id);
		} else {
			ICoreConfiguration cc = getFromPersistentDatastore(0L);
			id = cc.getId();
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

		Collections.sort(c.getCompsUnderway(), new Comparator<Long>() {
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
