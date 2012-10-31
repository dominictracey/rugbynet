/**
 * 
 */
package net.rugby.foundation.game1.server.factory.test;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;

import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.game1.shared.League;

/**
 * @author home
 *
 */
public class TestLeagueFactory implements ILeagueFactory {

	private Long id;
	private IEntryFactory ef;

	@Inject
	public TestLeagueFactory(IEntryFactory ef) {
		this.ef = ef;
	}
		
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeagueFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeagueFactory#get()
	 */
	@Override
	public ILeague get() {
		ILeague l = new League();
		if (id != null) {
			l.setId(id);
			if (id.equals(80L)) {
				for (Long i=6000L; i<6008L; ++i) {
					ef.setId(i);
					l.getEntryIds().add(i);
					l.getEntryMap().put(i, ef.getEntry());
				}
			} else if (id.equals(81L)) {
				for (Long i=6000L; i<6005L; ++i) {
					ef.setId(i);
					l.getEntryIds().add(i);
					l.getEntryMap().put(i, ef.getEntry());
				}
			}  else if (id.equals(82L)) {
				for (Long i=6003L; i<6008L; ++i) {
					ef.setId(i);
					l.getEntryIds().add(i);
					l.getEntryMap().put(i, ef.getEntry());
				}
			}  else if (id.equals(83L)) {
				for (Long i=6100L; i<6106L; ++i) {
					ef.setId(i);
					l.getEntryIds().add(i);
					l.getEntryMap().put(i, ef.getEntry());
				}
			} else if (id.equals(84L)) {
				for (Long i=6100L; i<6104L; ++i) {
					ef.setId(i);
					l.getEntryIds().add(i);
					l.getEntryMap().put(i, ef.getEntry());
				}
			} else if (id.equals(85L)) {
				for (Long i=6103L; i<6106L; ++i) {
					ef.setId(i);
					l.getEntryIds().add(i);
					l.getEntryMap().put(i, ef.getEntry());
				}
			}  else if (id.equals(86L)) {
				for (Long i=6103L; i<6106L; ++i) {
					ef.setId(i);
					l.getEntryIds().add(i);
					l.getEntryMap().put(i, ef.getEntry());
				}
			}
		}
		return l;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeagueFactory#put(net.rugby.foundation.game1.shared.ILeague)
	 */
	@Override
	public ILeague put(ILeague l) {
		return l;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeagueFactory#getAll()
	 */
	@Override
	public Set<ILeague> getAll() {
		Set<ILeague> set = new HashSet<ILeague>();
		
		for (Long i=80L; i<87L; ++i) {
			setId(i);
			set.add(get());
		}
		
		return set;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeagueFactory#delete()
	 */
	@Override
	public boolean delete() {
		return true;
	}

}
