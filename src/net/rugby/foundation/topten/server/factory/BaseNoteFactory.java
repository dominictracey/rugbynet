package net.rugby.foundation.topten.server.factory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.INoteRef;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public abstract class BaseNoteFactory extends BaseCachingFactory<INote> implements INoteFactory {

	protected INoteRefFactory nrf;

	public BaseNoteFactory(INoteRefFactory nrf) {
		this.nrf = nrf;
	}

	private final String prefix = "NOTE-Rid";
	private final String TTLprefix = "NOTE-TTLid";

	@Override
	public List<INote> getByUROrdinal(int urOrd)
	{
		try {
			List<INote> list = null;

			list = getList(getCacheId(urOrd));


			if (list == null) {
				list = getFromPersistentDatastoreByUROrdinal(urOrd);

				if (list != null) {
					putList(getCacheId(urOrd), list);
				}	else {
					return null;
				}
			} 
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getByUROrdinal" + ex.getMessage(), ex);
			return null;
		}

	}

	@Override
	public List<INote> getForList(ITopTenList ttl)
	{
		try {
			List<INote> list = null;

			list = getList(getTTLCacheId(ttl.getId()));


			if (list == null) {
				list = getFromPersistentDatastoreByList(ttl);

				if (list != null) {
					putList(getTTLCacheId(ttl.getId()), list);
				}	else {
					return null;
				}
			} 
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForList" + ex.getMessage(), ex);
			return null;
		}

	}

	protected abstract List<INote> getFromPersistentDatastoreByList(ITopTenList ttl);
	private String getTTLCacheId(Long id) {
		return TTLprefix + id;
	}

	protected abstract List<INote> getFromPersistentDatastoreByUROrdinal(int urOrd);
	private String getCacheId(int id) {
		return prefix + id;
	}


	/**
	 * Delete from notes where the note.contextListId = list.id 
	 * Delete from noteref where the noteId = notes deleted above
	 */
	@Override
	public boolean deleteForList(ITopTenList list) {
		try {
			deleteItemFromMemcache(getTTLCacheId(list.getId()));
			
			List<INote> notes = getForList(list);
						
			for (INote n : notes) {
				List<INoteRef> refs = nrf.getForNote(n);
				for (INoteRef ref : refs) {
					nrf.delete(ref);
				}
				
				delete(n);
			}
			
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForList" + ex.getMessage(), ex);
			return false;
		}
	}
	
}
