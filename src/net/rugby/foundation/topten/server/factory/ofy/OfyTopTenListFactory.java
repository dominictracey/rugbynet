package net.rugby.foundation.topten.server.factory.ofy;

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
	public OfyTopTenListFactory(IPlayerFactory pf, ICompetitionFactory cf, IMatchGroupFactory mf, ITeamGroupFactory tf) {
		super(mf,tf);
		this.pf = pf;
		this.cf = cf;

		ofy = DataStoreFactory.getOfy();
	}


	public ITopTenList get(Long id) {
		// @REX this will be getFromDB in memcache'd world
		try {
			ITopTenList list = ofy.get(TopTenList.class, id);

			if (list != null && list.getItemIds() != null) {
				//hydrate items
				Iterator<Long> it = list.getItemIds().iterator();
				while (it.hasNext()) {
					list.getList().add(getItem(it.next()));
				}
			}

			return list;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "get()" + e.getLocalizedMessage());
			return null;
		}

	}


	private ITopTenItem getItem(Long id) {
		try {
			ITopTenItem item = ofy.get(TopTenItem.class,id);
			item.setPlayer(pf.getById(item.getPlayerId()));
			return item;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getItem()" + e.getLocalizedMessage());
			return null;
		}
	}


	public List<ITopTenListSummary> getSummariesForComp(Long compId) {

		return null;

	}

	@Override
	public ITopTenItem put(ITopTenItem item) {
		try {
			ofy.put(item);
			return item;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put(item)" + e.getLocalizedMessage());
			return null;
		}
	}


	@Override
	public ITopTenList put(ITopTenList list) {
		try {
			ofy.put(list);
			return list;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put(list)" + e.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public ITopTenItem submit(ITopTenItem item) {
		try {
			item.setSubmitted(!item.isSubmitted());
			ofy.put(item);
			return item;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "submit()" + e.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public ITopTenList getLatestForComp(Long compId) {
		// @REX - really need to memcache this!
		try {
			Query<TopTenList> q = ofy.query(TopTenList.class).filter("compId", compId).filter("isLive",true).filter("nextPublishedId", null);
			if (q.count() == 0) {
				return null;
			} else if (q.count() == 1) {
				return get(q.get().getId());
			} else {
				// something really wrong!
				return null;
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getLatestForComp()" + e.getLocalizedMessage());
			return null;
		}
	}


	@Override
	public ITopTenList getLastCreatedForComp(Long compId) {
		try {
			Query<TopTenList> q = ofy.query(TopTenList.class).filter("compId", compId).filter("nextId", null);
			if (q.count() == 0) {
				return null;
			} else if (q.count() == 1) {
				return get(q.get().getId());
			} else {
				// something really wrong!
				return null;
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getLastCreatedForComp()" + e.getLocalizedMessage());
			return null;
		}
	}

}
