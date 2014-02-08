package net.rugby.foundation.topten.server.factory.ofy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList.ITopTenListSummary;
import net.rugby.foundation.topten.model.shared.TopTenItem;
import net.rugby.foundation.topten.model.shared.TopTenList;
import net.rugby.foundation.topten.server.factory.BaseTopTenListFactory;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

public class OfyTopTenListFactory extends BaseTopTenListFactory implements ITopTenListFactory {

	protected IPlayerFactory pf;
	protected ICompetitionFactory cf;
	Objectify ofy;

	@Inject
	public OfyTopTenListFactory(IPlayerFactory pf, ICompetitionFactory cf, IMatchGroupFactory mf, ITeamGroupFactory tf, IRoundFactory rf, IPlayerMatchStatsFactory pmsf, IRatingQueryFactory rqf, IPlayerRatingFactory prf) {
		super(mf,tf, rf, pmsf, rqf, prf);
		this.pf = pf;
		this.cf = cf;

		ofy = DataStoreFactory.getOfy();
	}


	@Override
	public ITopTenList getFromPeristentDatastore(Long id) {

		try {
			ITopTenList list = ofy.get(TopTenList.class, id);
			list.setList(new ArrayList<ITopTenItem>());
			if (list != null && list.getItemIds() != null) {
				//hydrate items
				Iterator<Long> it = list.getItemIds().iterator();
				int ordinal = 1;
				while (it.hasNext()) {
					list.getList().add(getItem(it.next(), list, ordinal++));
				}
			}

			return list;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "get()" + e.getLocalizedMessage(), e);
			return null;
		}

	}


	private ITopTenItem getItem(Long id, ITopTenList parent, int ordinal) {
		try {
			ITopTenItem item = ofy.get(TopTenItem.class,id);
			item.setPlayer(pf.get(item.getPlayerId()));
			item.setOrdinal(ordinal);
			assert(parent.getId()==item.getParentId());
			//item.setParent(parent);
			return item;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getItem()" + e.getLocalizedMessage(), e);
			return null;
		}
	}


	public List<ITopTenListSummary> getSummariesForComp(Long compId) {

		return null;

	}

	@Override
	public ITopTenItem putToPersistentDatastore(ITopTenItem item) {
		try {
			ofy.put(item);
			return item;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put(item)" + e.getLocalizedMessage(), e);
			return null;
		}
	}


	@Override
	public ITopTenList putToPersistentDatastore(ITopTenList list) {
		try {
			ofy.put(list);
			return list;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put(list)" + e.getLocalizedMessage(), e);
			return null;
		}
	}



	@Override
	public ITopTenList getLatestForComp(Long compId) {
		try {
			// first check memcache
			ITopTenList ttl = super.getLatestForComp(compId);
			if (ttl != null) {
				return ttl;
			} else {
				Query<TopTenList> q = ofy.query(TopTenList.class).filter("compId", compId).filter("live",true).filter("nextPublishedId", null);
				if (q.count() == 0) {
					return null;
				} else if (q.count() == 1) {
					// memcache it
					setLatestPublishedForComp(q.get(), compId);
					return get(q.get().getId());
				} else {
					// something really wrong!
					throw new RuntimeException("Corrupt TopTen database, there are multiple latest top ten lists for the comp " + compId);
				}
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getLatestForComp()" + e.getLocalizedMessage(), e);
			return null;
		}
	}


	@Override
	public ITopTenList getLastCreatedForComp(Long compId) {
		try {
			// first check memcache
			ITopTenList ttl = super.getLastCreatedForComp(compId);
			if (ttl != null) {
				return ttl;
			} else {
				Query<TopTenList> q = ofy.query(TopTenList.class).filter("compId", compId).filter("nextId", null);
				if (q.count() == 0) {
					return null;
				} else if (q.count() == 1 || q.count() == 2) {
					// If it is 2 it is the case where we have created but haven't linked yet. Return the first in the list, which is the previously existing one.
					setLastCreatedForComp(get(q.get().getId()),compId);
					return get(q.get().getId());
				} else {
					throw new RuntimeException("Corrupt TopTen database, there are multiple Last Created top ten lists for the comp " + compId);
				}
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getLastCreatedForComp()" + e.getLocalizedMessage(), e);
			return null;
		}
	}


	@Override
	protected void deleteFromPersistentDatastore(ITopTenList list) {
		// first the items
		ofy.delete(list.getList());
		ofy.delete(list);
	}

}
