package net.rugby.foundation.topten.server.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.TopTenItem;
import net.rugby.foundation.topten.model.shared.TopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

public abstract class BaseTopTenListFactory implements ITopTenListFactory {

	private IMatchGroupFactory mf;
	private ITeamGroupFactory tf;

	private String lastCreatedMemcacheKey = "TTL-lastCreated-";
	private String latestPublishedMemcacheKey = "TTL-latestPublished-";

	public BaseTopTenListFactory(IMatchGroupFactory mf, ITeamGroupFactory tf) {
		this.mf = mf;
		this.tf = tf;
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
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
			// it was the last created so reset our special memcache object
			setLastCreatedForComp(prev,list.getCompId());
		}

		if (nextPub != null) {
			nextPub.setPrevPublishedId(list.getPrevPublishedId());
			put(nextPub);
		} else {
			// it was the last
			setLatestPublishedForComp(prevPub,list.getCompId());
		}

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

		return list;
	}

	abstract protected void deleteFromPersistentDatastore(ITopTenList list);

	private void deleteFromMemcache(ITopTenList list) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(list.getId());
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** deleted item from memcache" + list.getId() + " *** \n" + syncCache.getStatistics());
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
			} else {
				// this was the latest published, reset the special memcache object
				setLatestPublishedForComp(prevPub,list.getCompId());
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
				} else {
					// the very first one!
					put(list);
				} 
			}
			// is this now the latest published?
			if (list.getNextPublishedId() == null) {
				// this is the latest published, reset the special memcache object
				setLatestPublishedForComp(list,list.getCompId());
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
		list.setItemIds(new ArrayList<Long>());

		// first prune the PMI list down to the actual Top Ten
		// put the PMRs in a SortedSet
		TreeSet<IPlayerMatchRating> set = new TreeSet<IPlayerMatchRating>();
		for (IPlayerMatchInfo pmi : tti.getPmiList()) {
			if (pmi.getMatchRating() != null && pmi.getMatchRating().getRating() != null) {
				set.add(pmi.getMatchRating());
			}
		}

		//get an id
		put(list);
		assert(list.getId() != null);

		// create TTIs
		if (tti.getPmiList() != null && tti.getPmiList().size() > 0) {
			Iterator<IPlayerMatchRating> it = set.iterator();
			int count = 0;
			while (it.hasNext() && count < 10) {
				IPlayerMatchRating pmr = it.next();
				mf.setId(pmr.getPlayerMatchStats().getMatchId());
				IMatchGroup match = mf.getGame();
				tf.setId(pmr.getPlayerMatchStats().getTeamId());
				ITeamGroup team = tf.getTeam();
				//TopTenItem(Long id, Long playerId, IPlayer player, String text,
				//String image, Long contributorId, Long editorId, boolean isSubmitted, 
				//String matchReportLink, String teamName, ITopTenList parent)
				ITopTenItem item = new TopTenItem(null, pmr.getPlayerId(), pmr.getPlayer(), "",
						"", null, null, false, match.getForeignUrl(), team.getDisplayName(), team.getId(), pmr.getPlayerMatchStats().getPosition(), list);
				put(item);
				list.getList().add(item);
				list.getItemIds().add(item.getId());
				count++;
			}
		}



		// append it onto the comp's linked list
		ITopTenList last = getLastCreatedForComp(list.getCompId());
		if (last != null && !last.getId().equals(list.getId())) { // might be the first one
			assert (last.getNextId() == null);
			last.setNextId(list.getId());
			put(last);

			list.setPrevId(last.getId());

 		}

		put(list);

		setLastCreatedForComp(list,list.getCompId());

		return list;
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

				if (mr != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(mr);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();

					syncCache.put(id, yourBytes);
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** getting item (and putting in memcache)" + mr.getId() + " *** \n" + syncCache.getStatistics());

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
			item = putToPersistentDatastore(item);

			// now update the memcache version of the parent
			//			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			//			syncCache.delete(item.getId());
			//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			//			ObjectOutput out = new ObjectOutputStream(bos);   
			//			out.writeObject(item);
			//			byte[] yourBytes = bos.toByteArray(); 
			//
			//			out.close();
			//			bos.close();
			//
			//			syncCache.put(item.getId(), yourBytes);

			//			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			//			syncCache.delete(item.getParentId());
			//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			//			ObjectOutput out = new ObjectOutputStream(bos);   
			//			out.writeObject(item.getParent());
			//			byte[] yourBytes = bos.toByteArray(); 
			//
			//			out.close();
			//			bos.close();
			//
			//			syncCache.put(item.getParentId(), yourBytes);
			//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** putting item " + item.getId() + " for list " + item.getParent().toString() + "id (" + item.getParent().getId() + ") *** \n" + syncCache.getStatistics());

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
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** putting list " + list.getId() + " *** \n" + syncCache.getStatistics());
			
			// refresh latest and last
			ITopTenList last = getLastCreatedForComp(list.getCompId());
			if (last != null) {
				setLastCreatedForComp(get(last.getId()), list.getCompId());
			}
	
			ITopTenList latest = getLatestForComp(list.getCompId());
			if (latest != null) {
				setLatestPublishedForComp(get(latest.getId()), list.getCompId());
			}

			
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}	}

	abstract protected ITopTenList putToPersistentDatastore(ITopTenList list);

	@Override
	public ITopTenItem submit(ITopTenItem item) {
		try {
			item.setSubmitted(!item.isSubmitted());
			put(item);
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

}
