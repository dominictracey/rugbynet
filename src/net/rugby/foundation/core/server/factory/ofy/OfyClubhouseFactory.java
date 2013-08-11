/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory;
import net.rugby.foundation.model.shared.Clubhouse;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IClubhouse;

/**
 * @author home
 *
 */
public class OfyClubhouseFactory implements IClubhouseFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private IClubhouseMembershipFactory chmf;

	@Inject
	public OfyClubhouseFactory(IClubhouseMembershipFactory chmf) {
		this.chmf = chmf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseFactory#getCompetition()
	 */
	@Override
	public IClubhouse get() {
		if (id == null) {
			return new Clubhouse();
		}
		Objectify ofy = DataStoreFactory.getOfy();

		IClubhouse ch = ofy.find(new Key<Clubhouse>(Clubhouse.class,id));

		if (ch == null) {
			ch = new Clubhouse();
		}

		return ch;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseFactory#put(net.rugby.foundation.model.shared.IClubhouse)
	 */
	@Override
	public IClubhouse put(IClubhouse ch) {
		Objectify ofy = DataStoreFactory.getOfy();

		ofy.put(ch);

		//@TODO put the ClubhouseMembership list too?
		return ch;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IClubhouseFactory#getAll()
	 */
	@Override
	public List<IClubhouse> getAll() {
		List<IClubhouse> all = new ArrayList<IClubhouse>();
		Objectify ofy = DataStoreFactory.getOfy();

		Query<Clubhouse> qch = ofy.query(Clubhouse.class).filter("active", true);
		for (Clubhouse ch : qch) {
			all.add(ch);
		}
		return all;
	}

	@Override
	public boolean delete(Long id) {
		try {
			if (id != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				setId(id);
				IClubhouse c = get();

				if (c != null) {
					// delete all the memberships
					chmf.deleteForClubhouse(id);

					ofy.delete(c);
				}
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in delete: " + ex.getLocalizedMessage());
			return false;
		}
		return true;
	}

}
