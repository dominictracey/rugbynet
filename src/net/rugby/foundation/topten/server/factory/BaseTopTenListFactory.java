package net.rugby.foundation.topten.server.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.PlayerRating;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;
import net.rugby.foundation.model.shared.IServerPlace.PlaceType;
import net.rugby.foundation.topten.model.shared.Feature;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.TopTenItem;
import net.rugby.foundation.topten.model.shared.TopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.utilities.INotesCreator;
import net.rugby.foundation.topten.server.utilities.ISocialMediaDirector;
import net.rugby.foundation.topten.server.utilities.TwitterPromoter;

/**
 * Here are the important attributes of a TTL that are maintained here:
 * 		live - true if it has been published as a feature, false otherwise. Note that for live to be true, series MUST be true.
 * 		series - true if it is a feature, false otherwise (note they can be unpublished features)
 * 		nextid/previd - the id next/prev list that has series == true
 * 		nextPublishedId/prevPublishedId - the id of the next/prev list that has live == true
 * 
 * 
 * @author home
 *
 */
public abstract class BaseTopTenListFactory implements ITopTenListFactory {

	private IMatchGroupFactory mf;
	private ITeamGroupFactory tf;

	private String lastCreatedMemcacheKey = "TTL-lastCreated-";
	private String latestPublishedMemcacheKey = "TTL-latestPublished-";
	private IRoundFactory rf;
	private IPlayerMatchStatsFactory pmsf;
	private IRatingQueryFactory rqf;
	private IPlayerRatingFactory prf;
	private IConfigurationFactory ccf;
	protected IPlaceFactory spf;
	private ICompetitionFactory cf;
	private ISocialMediaDirector smd;
	private INotesCreator nc;
	private IRatingSeriesFactory rsf;
	protected IUniversalRoundFactory urf;
	private INoteFactory nf;
	private IRatingMatrixFactory rmf;
	private IRatingGroupFactory rgf;

	public BaseTopTenListFactory(IMatchGroupFactory mf, ITeamGroupFactory tf, IRoundFactory rf, 
			IPlayerMatchStatsFactory pmsf, IRatingQueryFactory rqf, IPlayerRatingFactory prf, IConfigurationFactory ccf,
			IPlaceFactory spf, ICompetitionFactory cf, ISocialMediaDirector smd, INotesCreator nc, IRatingSeriesFactory rsf,
			IUniversalRoundFactory urf, INoteFactory nf, IRatingMatrixFactory rmf, IRatingGroupFactory rgf) {
		this.mf = mf;
		this.tf = tf;
		this.rf = rf;
		this.pmsf = pmsf;
		this.rqf = rqf;
		this.prf = prf;
		this.ccf = ccf;
		this.spf = spf;
		this.cf = cf;
		this.smd = smd;
		this.nc = nc;
		this.rsf = rsf;
		this.urf = urf;
		this.nf = nf;
		this.rmf = rmf;
		this.rgf = rgf;

		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	@Override
	public ITopTenList delete(ITopTenList list) {
		if (list != null) {
			Long rqId = list.getQueryId();

			boolean isSeries = false;
			IRatingQuery rq = null;
			if (rqId != null) {
				rq = rqf.get(rqId);
				if (rq.getRatingMatrixId() != null) {
					isSeries = true;
				}

				// if it has been tagged as a feature, mark it as not a series so it doesn't get treated as such
				if (list.getNextId() != null || list.getPrevId() != null) {
					isSeries = false;
				}
			}

			ITopTenList prev = null;
			ITopTenList prevPub = null;
			ITopTenList next = null;
			ITopTenList nextPub = null;

			if (list.getPrevId() != null) {
				prev = get(list.getPrevId());
			}

			// there is a weird memcache thing wherein if prev and prevPub are the same, these gets can retrieve different objects and the 
			// putting of prevPub can overwrite the putting of prev. So check for this condition and re-use rather than re-fetch.
			if (list.getPrevPublishedId() != null) {
				if (list.getPrevPublishedId().equals(list.getPrevId())) {
					prevPub = prev;
				} else {
					prevPub = get(list.getPrevPublishedId());
				}
			}

			if (list.getNextId() != null) {
				next = get(list.getNextId());
			}

			if (list.getNextPublishedId() != null) {
				if (list.getNextPublishedId().equals(list.getNextId())) {
					nextPub = next;
				} else {
					nextPub = get(list.getNextPublishedId());
				}
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
			} else {
				if (!isSeries) {
					// it was the last created so reset our special memcache object
					setLastCreatedForComp(prev,list.getCompId());
				}
			}

			if (nextPub != null) {
				nextPub.setPrevPublishedId(list.getPrevPublishedId());
				put(nextPub);
			} else {
				if (!isSeries) {
					// it was the last
					setLatestPublishedForComp(prevPub,list.getCompId());
				}
			}

			// delete any associated seriesPlaces
			for (ITopTenItem item : list.getList()) {
				if (item != null && item.getPlaceGuid() != null) {
					spf.delete(spf.getForGuid(item.getPlaceGuid()));
				}
			}

			// delete the place associated with the list if necessary
			if (list.getGuid() != null && !list.getGuid().isEmpty()) {
				spf.delete(spf.getForGuid(list.getGuid()));
			}

			// delete notes and noteRefs
			nf.deleteForList(list);

			// delete the component items from memcache
			Iterator<ITopTenItem> it = list.getList().iterator();
			while (it.hasNext()) {
				deleteFromMemcache(it.next());
			}

			// delete the list object itself from memcache
			deleteFromMemcache(list);

			// allow subclasses to delete the objects from the persistent datastore
			deleteFromPersistentDatastore(list);

			// and delete it
			list = null;

			// delete the ratingQuery reference
			if (rq != null) {
				if (rq != null) {
					rq.setTopTenListId(null);
					rqf.put(rq);
				}
			}
			return list;
		}
		return null;
	}

	abstract protected void deleteFromPersistentDatastore(ITopTenList list);

	private void deleteFromMemcache(ITopTenList list) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(list.getId());
			//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** deleted item from memcache" + list.getId() + " *** \n" + syncCache.getStatistics());
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}		
	}

	private void deleteFromMemcache(ITopTenItem item) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(item.getId());
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
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

			//			else {
			//				// this was the latest published, reset the special memcache object
			//				setLatestPublishedForComp(prevPub,list.getCompId());
			//			}
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
				} else {
					// the very first one!
					put(list);
				} 
			}
			//			// is this now the latest published?
			//			if (list.getNextPublishedId() == null) {
			//				// this is the latest published, reset the special memcache object
			//				setLatestPublishedForComp(list,list.getCompId());
			//			}
			//			
			//			// is this now the last created?
			//			if (list.getNextId() == null) {
			//				// this is the latest published, reset the special memcache object
			//				setLastCreatedForComp(list,list.getCompId());
			//			}

			// dump the memcached versions of last and latest and let them reconstitute
			clearMemCacheLastAndLatest(list.getCompId());
			
			// flush the latest features from memcache to force a re-read
			MemcacheServiceFactory.getMemcacheService().delete(latestFeatureMemcacheKey);


			smd.PromoteTopTenList(list);
		}
		return list;
	}

	private void clearMemCacheLastAndLatest(Long compId) {
		setLastCreatedForComp(null,compId);
		setLatestPublishedForComp(null,compId);

	}

	@Override
	public ITopTenList create(TopTenSeedData tti) {
		return create(tti, null);
	}

	@Override
	public ITopTenList create(TopTenSeedData tti, ITopTenList lastTTL) {
		ITopTenList list = new TopTenList();
		// a times series TTL won't have a compId set, so we need to get one
		list.setCompId(getCompId(tti));
		list.setContent(tti.getDescription());
		list.setCreated(new Date());
		list.setEditorId(null);
		list.setExpiration(null);
		list.setLive(false);
		list.setPipeLineId(null);
		list.setPublished(null);
		list.setTitle(tti.getTitle());
		list.setContext(tti.getContext());
		list.setList(new ArrayList<ITopTenItem>());
		list.setItemIds(new ArrayList<Long>());
		list.setQueryId(tti.getQueryId());
		list.setSponsorId(tti.getSponsorId());

		// if this is a series generated query we set it up a little differently since it doesn't participate in the publish chain
		IRatingQuery rq = rqf.get(tti.getQueryId());
		
		if (rq.getRatingMatrix() == null && rq.getRatingMatrixId() != null) {
			rq = rqf.buildUplinksForQuery(rq);
		}
		
		if (rq.getRatingMatrix() != null) {
			list.setSeries(true);
			list.setRoundOrdinal(rq.getRatingMatrix().getRatingGroup().getUniversalRoundOrdinal());
		} else {
			list.setSeries(false);
			// set the list's round ordinal from the latest of the component rounds not in the future
			int current = urf.getCurrent().ordinal;
			int uro = -1;
			for (Long rid : tti.getRoundIds()) {
				IRound r = rf.get(rid);
				if (urf.get(r).ordinal > uro) {
					uro = urf.get(r).ordinal;
				}
				if (uro == current) {
					break; // go no further
				}
			}
			list.setRoundOrdinal(uro);
		}

		buildTwitterDescription(list, rq);

		// get the list of player ratings generated by the query
		List<IPlayerRating> prl = prf.query(rq);

		// first prune the PMI list down to the actual Top Ten
		// put the PMRs in a SortedSet
		TreeSet<IPlayerRating> set = new TreeSet<IPlayerRating>();
		for (IPlayerRating pmi : prl) {
			if (pmi != null && pmi.getRating() != null) {
				set.add((PlayerRating)pmi);
			}
		}

		// append it onto the comp's linked list if it is ad hoc
		if (!list.getSeries()) {
			makeFeature(list);
		}

		//get an id
		put(list);
		assert(list.getId() != null);

		// create TTIs
		if (prl != null && prl.size() > 0) {
			// keep track of the number from each team included on the list so we can check specified limit
			Map<Long,Integer> numFromTeam = new HashMap<Long, Integer>();
			Iterator<IPlayerRating> it = set.iterator();
			int count = 0;
			String twitterMatch = null;
			if (!list.getSeries())
				list.setContent("<p>\n</p>\n<p>\n</p>\n<p>\n</p>\n<p>\n</p>\n<p>\n</p>\n");
			else {
				// is this a match list?
				if (rq.getRatingMatrix().getRatingGroup().getRatingSeries().getMode().equals(RatingMode.BY_MATCH)) {
					twitterMatch = " ";
				}
			}

			while (it.hasNext() && count < 10) {
				IPlayerRating pmr = it.next();
				IPlayerMatchStats pms = pmsf.get(pmr.getMatchStatIds().get(0));
				IMatchGroup match = mf.get(pms.getMatchId());
				if (twitterMatch != null) { // will be " ", not null
					twitterMatch = "#" + match.getHomeTeam().getAbbr() + "v" + match.getVisitingTeam().getAbbr();
				}
				ITeamGroup team = tf.get(pms.getTeamId());

				// keep track of players per team
				if (!numFromTeam.containsKey(team.getId())) {
					numFromTeam.put(team.getId(), 0);
				}

				if (numFromTeam.get(team.getId()) < tti.getPlayersPerTeam()) {
					//TopTenItem(Long id, Long playerId, IPlayer player, String text,
					//String image, Long contributorId, Long editorId, boolean isSubmitted, 
					//String matchReportLink, String teamName, ITopTenList parent)

					ITopTenItem item = new TopTenItem(count+1,null, pmr.getPlayerId(), pmr.getPlayer(), "",
							"", null, null, true, null, team.getDisplayName(), team.getId(), pms.getPosition(), list,
							pmr.getId(), pmr.getRating(), null);
					put(item);  // get id for guid hashing
					IServerPlace p = spf.create();
					item.setPlaceGuid(p.getGuid());
					if (list.getSeries()) {
						spf.buildItem(p,item);
					} else {
						p.setCompId(list.getCompId());
						p.setListId(list.getId());
						p.setItemId(null);
						p.setType(PlaceType.FEATURE);
						spf.put(p);  
						list.setContent(list.getContent()+"<b>"+pmr.getPlayer().getDisplayName()+"</b>\n");
					}
					put(item);
					list.getList().add(item);
					list.getItemIds().add(item.getId());
					numFromTeam.put(team.getId(), numFromTeam.get(team.getId())+1);
					count++;
				}
			}

			// update the line item tweets
			TwitterPromoter twitterPromoter = new TwitterPromoter(tf, this, ccf);
			twitterPromoter.process(list, twitterMatch);
		}



		// now figure out where the last TTL we need to compare to lives
		if (lastTTL != null) {
			compareListToLast(list,lastTTL);
		}


		// create the place guid
		IServerPlace p = spf.create();
		if (list.getSeries()) {
			spf.buildList(p,list);
			list.setGuid(p.getGuid());
		} else {
			// and create a feature guid
			p.setCompId(list.getCompId());
			p.setListId(list.getId());
			p.setItemId(null);
			p.setType(PlaceType.FEATURE);
			spf.put(p);  
			list.setGuid(p.getGuid());
		}

		// save the items
		put(list);

		// store reference in ratingQuery
		rq.setTopTenListId(list.getId());
		rqf.put(rq);

		// create notes once the query and TTL are linked up
		nc.createNotes(rq);

		if (!list.getSeries()) {
			setLastCreatedForComp(list,list.getCompId());
		}

		return list;
	}

	@Override
	public IServerPlace makeFeature(ITopTenList list) {
		ITopTenList last = getLastCreatedForComp(list.getCompId());
		if (last != null) { // might be the first one
			assert (last.getNextId() == null);
			if (list.getId() == null) {
				put(list);
			}
			last.setNextId(list.getId());
			put(last);

			list.setPrevId(last.getId());
		} else {
			setLastCreatedForComp(list,list.getCompId());
		}

		// and create a feature guid
		IServerPlace p = spf.create();
		createFeatureGuid(list, p);

		// now add the per-item guids to point to the feature as well
		createFeatureItemGuids(list);
		
		return p;
	}

	private void createFeatureItemGuids(ITopTenList list) {
		for (ITopTenItem item : list.getList()) {
			
			IServerPlace ip = spf.create();
			ip.setCompId(list.getCompId());
			ip.setListId(list.getId());
			ip.setItemId(item.getId());
			ip.setType(PlaceType.FEATURE);
			spf.put(ip); 
			item.setFeatureGuid(ip.getGuid());
			
			put(item);
		}	
		
		// update the line item tweets
		TwitterPromoter twitterPromoter = new TwitterPromoter(tf, this, ccf);
		twitterPromoter.process(list);
	}

	private void createFeatureGuid(ITopTenList list, IServerPlace p) {

		p.setCompId(list.getCompId());
		p.setListId(list.getId());
		p.setItemId(null);
		p.setType(PlaceType.FEATURE);
		spf.put(p);  

		list.setSeries(false);
		list.setFeatureGuid(p.getGuid());
		put(list);
		
	}

	private void buildTwitterDescription(ITopTenList list, IRatingQuery rq) {
		String desc = "Top Ten ";

		// For a match
		if (rq.getRoundIds().size() == 1 && rq.getTeamIds().size() == 2) {
			// find the match
			IMatchGroup match = null;
			String matchTwitterHandle = "in ";
			IRound r = rf.get(rq.getRoundIds().get(0));
			if (r != null) {
				for (IMatchGroup m : r.getMatches()) {
					if (rq.getTeamIds().contains(m.getHomeTeamId()) && rq.getTeamIds().contains(m.getVisitingTeamId())) {
						match = m;
						break;
					}
				}
				if (match != null) {
					matchTwitterHandle += "#" +  match.getHomeTeam().getAbbr() + "v" + match.getVisitingTeam().getAbbr();
				} else {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Could not find match in buildTwitterDescription for TTL " + list.getTitle() + " teamIds " + rq.getTeamIds().toString());
				}
			}
			desc += " Performances " + matchTwitterHandle;
			list.setTwitterDescription(desc);
		} else if (rq.getPositions().size() == 1) {
			// by position : (*** = currently supported)
			//	*** Top Ten In Form Flankers of @premRugby
			//	*** Top Ten In Form Flankers in the World
			//	Top Ten Flankers (in the last year)
			//	Top Ten Flankers in Round 2 of @premRugby
			desc += "In Form " + rq.getPositions().get(0).getPlural();
			if (rq.getCompIds().size() > 1) {
				desc += " in the World";
			} else {
				if (rq.getCompIds().size() == 1) {
					Long compId = rq.getCompIds().get(0);
					if (compId != null) {
						desc += " of " + cf.get(compId).getTwitter();
					}
				}
			}
			list.setTwitterDescription(desc);
		} else {
			// default should be edited by the ad hoc creator

		}
	}

	private ITopTenList compareListToLast(ITopTenList list, ITopTenList last) {

		// key playerId, value position in last list
		Map<Long, Integer> oldPosMap = new HashMap<Long, Integer>();
		for (ITopTenItem newItem : list.getList()) {
			int pos = 1;
			for (ITopTenItem oldItem : last.getList()) {
				if (newItem.getPlayerId().equals(oldItem.getPlayerId())) {
					oldPosMap.put(newItem.getPlayerId(), pos);
					break;
				}
				pos++;
			}
		}

		int count = 1;
		String listDescription = "Notes:<br/>";
		for (ITopTenItem newItem : list.getList()) {
			newItem.setLastOrdinal(oldPosMap.get(newItem.getPlayerId()));
			ITopTenItem oldItem = last.getList().get(newItem.getLastOrdinal());
			String dir = "up";
			if (newItem.getOrdinal() > newItem.getLastOrdinal()) {
				dir = "down";
			}

			IPlayerRating newPr = prf.get(newItem.getPlayerRatingId());
			IPlayerRating oldPr = prf.get(oldItem.getPlayerRatingId());
			String lastAction = " being idle.";
			IPlayerMatchStats newPms = newPr.getMatchStats().get(newPr.getMatchStats().size());
			IPlayerMatchStats oldPms = oldPr.getMatchStats().get(oldPr.getMatchStats().size());
			if (!newPms.getMatchId().equals(oldPms.getMatchId())) {
				IMatchGroup m = mf.get(newPms.getMatchId());
				lastAction = " match " + m.getSimpleScoreMatchResult().toString();
			}

			listDescription += "<p>" + newItem.getPlayer().getDisplayName() + " moves " + dir + " to #" + newItem.getOrdinal() + " after " + lastAction + ".</p>\n";

			count++;
		}
		return list;
	}

	private Long getCompId(TopTenSeedData tti) {
		if (tti.getCompId() != null && !tti.getCompId().equals(-1L)) {
			return tti.getCompId();
		} else {
			// time series
			Long compId = null;
			for (Long rid : tti.getRoundIds()) {
				IRound r = rf.get(rid);
				// for now, all the selected rounds need to have the same compId
				if (compId == null) {
					compId = r.getCompId();
				} else {
					if (!r.getCompId().equals(compId)) {
						//throw new RuntimeException("Currently don't support cross competition Top Ten Lists");
						return ccf.get().getGlobalCompId();
					}
				}
			}
			return compId;
		}

	}

	@Override
	public ITopTenList get(Long id) {
		// handle the memcache in here and allow the fetch from the persistent stores to be implemented in the subclasses
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ITopTenList mr = null;

			if (id == null) {
				throw new RuntimeException("Use create method to get a new TopTenList.");
			}

			value = (byte[])syncCache.get(id);
			if (value == null) {
				mr = getFromPeristentDatastore(id);

				// upgrade server places if needed
				if (mr.getContent() != null && !mr.getContent().isEmpty() && mr.getFeatureGuid() == null) {
					// and create a feature guid
					IServerPlace p = spf.create();
					createFeatureGuid(mr, p);

					// now add the per-item guids to point to the feature as well
					createFeatureItemGuids(mr);

				}
				
				if (mr != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(mr);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();

					syncCache.put(id, yourBytes);
					//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** getting item (and putting in memcache)" + mr.getId() + " *** \n" + syncCache.getStatistics());

				}
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				mr = (ITopTenList)in.readObject();

				bis.close();
				in.close();

			}
			return mr;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	abstract protected ITopTenList getFromPeristentDatastore(Long id); 

	@Override
	public ITopTenItem put(ITopTenItem item) {
		// Allow subclasses to put it in persistent data stores, then put in memcache
		try {
			if (item != null) {
				item = putToPersistentDatastore(item);

				// need to update the list in memcache too

			}

			return item;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	abstract protected ITopTenItem putToPersistentDatastore(ITopTenItem item);

	@Override
	public ITopTenList put(ITopTenList list) {
		// Allow subclasses to put it in peristent data stores, then put in memcache
		try {
			// clear out the memcache copies of this list from the instances that may contain it
			dropContainersFromMemcache(list);

			list = putToPersistentDatastore(list);

			Iterator<ITopTenItem> it = list.getList().iterator();
			while (it.hasNext()) {
				put(it.next());
			}

			// now update the memcache version
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(list.getId());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(list);
			byte[] yourBytes = bos.toByteArray(); 

			out.close();
			bos.close();

			syncCache.put(list.getId(), yourBytes);
			//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** putting list " + list.getId() + " *** \n" + syncCache.getStatistics());

			// refresh latest and last
			//			ITopTenList last = getLastCreatedForComp(list.getCompId());
			//			if (last != null) {
			//				setLastCreatedForComp(last, list.getCompId());
			//			} else {
			//				setLastCreatedForComp(list, list.getCompId());
			//			}
			//
			//			ITopTenList latest = getLatestForComp(list.getCompId());
			//			if (latest != null) {
			//				setLatestPublishedForComp(latest, list.getCompId());
			//			}

			setLastCreatedForComp(null, list.getCompId());
			setLatestPublishedForComp(null, list.getCompId());

			// sanity check
			scan(list.getCompId());

			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}	}

	private void dropContainersFromMemcache(ITopTenList ttl) {
		IRatingMatrix rm = null;
		IRatingGroup rg = null;
		IRatingSeries rs = null;
		IRatingQuery rq = rqf.get(ttl.getQueryId());
		if (rq != null) {
			rm = rmf.get(rq.getRatingMatrixId());
			if (rm != null) {
				rg = rgf.get(rm.getRatingGroupId());
				if (rg != null) {
					rs = rsf.get(rg.getRatingSeriesId());
					if (rs != null)
						rsf.dropFromCache(rs.getId());
					rgf.dropFromCache(rg.getId());
				}
				rmf.dropFromCache(rm.getId());
			}
			rqf.dropFromCache(rq.getId());			
		}
	}

	abstract protected ITopTenList putToPersistentDatastore(ITopTenList list);

	@Override
	public ITopTenItem submit(ITopTenItem item) {
		try {
			item.setSubmitted(!item.isSubmitted());
			put(item);

			// refresh the containing list in memcache by deleting the record and forcing a refetch on the next get
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(item.getParentId());

			//			if (!item.isSubmitted() && item.getParent().getLive()) {
			//				// if we retract an item on a published list, we need to unpublish the list.
			//				publish(item.getParent());
			//				put(item.getParent());
			//			}
			return item;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "submit()" + e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	public ITopTenList getLastCreatedForComp(Long compId) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ITopTenList mr = null;

			String key = lastCreatedMemcacheKey + compId.toString();
			value = (byte[])syncCache.get(key);
			if (value != null) {
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				mr = (ITopTenList)in.readObject();

				bis.close();
				in.close();
			}

			return mr;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}

	@Override
	public ITopTenList getLatestForComp(Long compId) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ITopTenList mr = null;

			String key = latestPublishedMemcacheKey + compId.toString();
			value = (byte[])syncCache.get(key);
			if (value != null) {
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				mr = (ITopTenList)in.readObject();

				bis.close();
				in.close();
			}

			return mr;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}

	@Override
	public void setLastCreatedForComp(ITopTenList ttl, Long compId) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			String key = lastCreatedMemcacheKey + compId.toString();
			syncCache.delete(key);
			if (ttl != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(ttl);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(key, yourBytes);	
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	@Override
	public void setLatestPublishedForComp(ITopTenList ttl, Long compId){
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			String key = latestPublishedMemcacheKey + compId.toString();
			syncCache.delete(key);
			if (ttl != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(ttl);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(key, yourBytes);
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	@Override
	public boolean scan(Long compId) {

		boolean retval = true;
		// start at the purported last created and work backwards to confirm veracity of linkages
		ITopTenList lastCreated = getLastCreatedForComp(compId);
		ITopTenList cursor = lastCreated;
		while (cursor != null && cursor.getPrevId() != null) {
			Long prevId = cursor.getPrevId();
			if (prevId != null) { // might be the first
				ITopTenList prev = get(prevId);
				if (prev.getNextId() == null || !prev.getNextId().equals(cursor.getId())) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Scan of TTL for comp " + compId + " discovered bad nextId for TTL " + prev.getId() + ". The TTL who points to it with it's prevId is TTL " + cursor.getId() + " but the nextId for this TTL is " + prev.getNextId() + ".");
					retval = false;
				}
				if (!cursor.getId().equals(prev.getId())) {
					cursor = prev;
				} else {
					return false; // otherwise infinite loopage
				}
			}
		}

		// now the same with the published list
		cursor = getLatestForComp(compId);
		while (cursor != null && cursor.getPrevId() != null) {
			Long prevId = cursor.getPrevPublishedId();
			if (prevId != null) { 
				ITopTenList prev = get(prevId);
				if (!prev.getNextPublishedId().equals(cursor.getId())) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Scan of TTL for comp " + compId + " discovered bad nextPublishedId for TTL " + prev.getId() + ". The TTL who points to it with it's prevPublishedId is TTL " + cursor.getId() + " but the nexPublishedtId for this TTL is " + prev.getNextPublishedId() + ".");
					retval = false;
				}
				cursor = prev;
			} else {
				// if we get to the point where the cursor's prevPublished is null, we should be done.
				// we should keep scanning to make sure we have no live orphans.
				if (cursor.getPrevId() != null) {
					cursor = get(cursor.getPrevId());
					while (cursor.getPrevId() != null) {
						if (cursor.getLive()) {
							throw new RuntimeException("Scan of TTL for comp " + compId + " discovered a live orphan TTL " + cursor.getTitle() + " (id " + cursor.getId() + ")");
						}
						cursor = get(cursor.getPrevId());
					}
				}
				break; 
			}
		}
		return retval;
	}

	public String getGuidForMatch(IMatchGroup m) {
		// chase back up to the comp
		//ICompetition c = cf.get(rf.get(m.getRoundId()).getCompId());

		// now find the series for Mode=BY_MATCH, Criteria=ROUND
		IRound r = rf.get(m.getRoundId());
		IRatingSeries series = rsf.get(r.getCompId(), RatingMode.BY_MATCH);
		UniversalRound ur = urf.get(r);
		if (series != null) {
			IRatingGroup rg = null;
			for (IRatingGroup rgi : series.getRatingGroups()) {
				if (rgi.getUniversalRoundOrdinal() == ur.ordinal) {
					rg = rgi;
					break;
				}
			}

			if (rg != null) {
				IRatingMatrix rm = null;
				for (IRatingMatrix rmi : rg.getRatingMatrices()) {
					if (rmi.getCriteria().equals(Criteria.ROUND)) {
						rm = rmi;
						break;
					}
				}

				// now the right query
				if (rm != null) {
					IRatingQuery rq = null;
					for (IRatingQuery rqi : rm.getRatingQueries()) {
						if (rqi.getTeamIds().contains(m.getHomeTeamId()) && rqi.getTeamIds().contains(m.getVisitingTeamId())) {
							rq = rqi;
							break;
						}
					}

					// and the TTL to get the guid from
					if (rq.getTopTenListId() != null) {
						ITopTenList ttl = get(rq.getTopTenListId());

						if (ttl != null) {
							return ttl.getGuid();
						}
					}
				}
			}
		}

		return null;
	}

	protected final String latestFeatureMemcacheKey="TopTenListFactory:LatestFeatures";
	@Override
	public List<Feature> getLatestFeatures() {
		List<Feature> retval = new ArrayList<Feature>();
		
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

			value = (byte[])syncCache.get(latestFeatureMemcacheKey);
			if (value != null) {
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				retval = (List<Feature>)in.readObject();

				bis.close();
				in.close();
			} else {
				retval = getLatestFeaturesFromPeristentDatastore();
				if (retval == null) {
					retval = new ArrayList<Feature>();
				}
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(retval);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(latestFeatureMemcacheKey, yourBytes);
			}

			return retval;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
	
	protected abstract List<Feature> getLatestFeaturesFromPeristentDatastore(); 
}
