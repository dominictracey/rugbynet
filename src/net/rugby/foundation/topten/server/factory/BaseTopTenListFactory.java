package net.rugby.foundation.topten.server.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.TopTenItem;
import net.rugby.foundation.topten.model.shared.TopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

public abstract class BaseTopTenListFactory implements ITopTenListFactory {

	private IMatchGroupFactory mf;
	private ITeamGroupFactory tf;

	public BaseTopTenListFactory(IMatchGroupFactory mf, ITeamGroupFactory tf) {
		this.mf = mf;
		this.tf = tf;
	}

	@Override
	public ITopTenList delete(ITopTenList list) {
		ITopTenList prev = null;
		ITopTenList prevPub = null;
		ITopTenList next = null;
		ITopTenList nextPub = null;

		if (list.getPrevId() != null) {
			prev = get(list.getPrevId());
		}

		if (list.getPrevPublishedId() != null) {
			prevPub = get(list.getPrevPublishedId());
		}

		if (list.getNextId() != null) {
			next = get(list.getNextId());
		}

		if (list.getNextPublishedId() != null) {
			nextPub = get(list.getNextPublishedId());
		}

		if (prev != null) {
			prev.setNextId(list.getNextId());
			put(prev);
		}

		if (prevPub != null) {
			prevPub.setNextPublishedId(list.getNextPublishedId());
			put(prevPub);
		}

		if (next != null) {
			next.setPrevId(list.getPrevId());
			put(next);
		}

		if (nextPub != null) {
			nextPub.setPrevPublishedId(list.getPrevPublishedId());
			put(nextPub);
		}

		return null;
	}

	@Override
	public ITopTenList publish(ITopTenList list) {
		ITopTenList prevPub = null;
		ITopTenList nextPub = null;

		if (list.getPrevPublishedId() != null) {
			prevPub = get(list.getPrevPublishedId());
		}

		if (list.getNextPublishedId() != null) {
			nextPub = get(list.getNextPublishedId());
		}

		if (list.getLive() == true) {  //unpublishing
			list.setPublished(null);
			list.setLive(false);

			//correct linked lists
			if (prevPub != null) {
				prevPub.setNextPublishedId(list.getNextPublishedId());
				put(prevPub);
			}
			list.setNextPublishedId(null);
			if (nextPub != null) {
				nextPub.setPrevPublishedId(list.getPrevPublishedId());
				put(nextPub);
			}
			list.setPrevPublishedId(null);
			put(list);
		} else { //publishing
			list.setPublished(new Date());
			list.setLive(true);
			//correct linked lists by chasing through the prev and next chains 
			// looking for either a published list or the end of the line
			boolean found = false;
			ITopTenList prev = null;
			if (list.getPrevId() != null) {
				prev = get(list.getPrevId());
			}

			boolean more = (prev != null);
			while (!found && more) {
				if (prev.getLive()) {
					found = true;
				} else {
					if (prev.getPrevId() == null) {
						more = false;
					} else {
						prev = get(prev.getPrevId());
					}
				}
			}

			// if we found a previously published one, insert ourselves there
			ITopTenList next = null;
			if (found) {
				if (prev.getNextPublishedId() != null) {
					next = get(prev.getNextPublishedId());
					list.setNextPublishedId(next.getId());
					next.setPrevPublishedId(list.getId());
					put(list);
					put(next);
				}
				list.setPrevPublishedId(prev.getId());
				prev.setNextPublishedId(list.getId());
				put(list);
				put(prev);
			} else {
				// if we didn't find a prior published one, we actually have to scan forward to see if there is one published after
				nextPub = null;
				if (list.getNextId() != null) {
					next = get(list.getNextId());
				}

				more = (next != null);
				while (!found && more) {
					if (next.getLive()) {
						found = true;
					} else {
						if (next.getNextId() == null) {
							more = false;
						} else {
							next = get(next.getNextId());
						}
					}
				}

				if (found) {
					list.setNextPublishedId(next.getId());
					next.setPrevPublishedId(list.getId());
					put(list);
					put(next);
				}
			}
		}
		return list;
	}

	@Override
	public ITopTenList create(TopTenSeedData tti) {
		ITopTenList list = new TopTenList();
		list.setCompId(tti.getCompId());
		list.setContent(tti.getDescription());
		list.setCreated(new Date());
		list.setEditorId(null);
		list.setExpiration(null);
		list.setLive(false);
		list.setPipeLineId(null);
		list.setPublished(null);
		list.setTitle(tti.getTitle());
		list.setList(new ArrayList<ITopTenItem>());

		// create TTIs
		if (tti.getPmiList() != null && tti.getPmiList().size() > 0) {
			Iterator<IPlayerMatchInfo> it = tti.getPmiList().iterator();
			int count = 0;
			while (it.hasNext() && count < 10) {
				IPlayerMatchInfo pmi = it.next();
				mf.setId(pmi.getPlayerMatchStats().getMatchId());
				IMatchGroup match = mf.getGame();
				tf.setId(pmi.getPlayerMatchStats().getTeamId());
				ITeamGroup team = tf.getTeam();
				//TopTenItem(Long id, Long playerId, IPlayer player, String text,
				//String image, Long contributorId, Long editorId, boolean isSubmitted, 
				//String matchReportLink, String teamName)
				ITopTenItem item = new TopTenItem(null, pmi.getMatchRating().getPlayerId(), pmi.getMatchRating().getPlayer(), "",
						"", null, null, false, match.getForeignUrl(), team.getShortName());
				list.getList().add(item);
				count++;
			}
		}

		//get an id
		put(list);
		assert(list.getId() != null);

		// append it onto the comp's linked list
		ITopTenList last = getLastCreatedForComp(list.getCompId());
		assert (last.getNextId() == null);
		last.setNextId(list.getId());
		put(last);

		list.setPrevId(last.getId());
		put(list);

		return list;
	}

}
