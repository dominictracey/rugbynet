package net.rugby.foundation.topten.server.staticpages;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import net.rugby.foundation.core.server.factory.ICachingFactory;
import net.rugby.foundation.core.server.factory.IContentFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

public class SideNavGenerator{


	private static final long serialVersionUID = 1L;
	private static final String SIDE_NAV_CACHE_PREFIX = "SNCP-";


	public SideNavGenerator() {
	}

	public void getContent()  throws IOException {
	}

	private String getFromCache() {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			String page = null;

			value = (byte[])syncCache.get(SIDE_NAV_CACHE_PREFIX); 
				//no suffix to be added here - same for all pages in site
			if (value != null) {
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in;

				in = new ObjectInputStream(bis);

				Object obj = in.readObject();
				if (obj instanceof String) {
					page = (String)obj;
				}

				bis.close();
				in.close();
			}

			return page;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}
	
	//build content method... like client side nav
}
