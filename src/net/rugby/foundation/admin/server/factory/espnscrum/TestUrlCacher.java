/**
 * 
 */
package net.rugby.foundation.admin.server.factory.espnscrum;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * @author home
 *
 */
public class TestUrlCacher implements IUrlCacher {
	private String url;

	public TestUrlCacher() {
		
	}
	public TestUrlCacher(String url) {
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher#get()
	 */
	@Override
	public List<String> get() {
		//if ( url.equals("match1")) {
			return fetchContent();
		//} else {
		//	return null;
		//}
	}
	
	@SuppressWarnings("unchecked")
	private List<String> fetchContent() {
		// first strip off the ? param string if it exists
		url = url.split("\\?")[0];
		if (!url.isEmpty()) {
			BufferedReader reader;
			try {
				byte[] value = null;
				MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
				List<String> page = null;
				value = (byte[])syncCache.get(url);

				if (value == null) {

					// didn't find - get from werld wyde weeb
					//URL Url = new URL(url);
					reader = new BufferedReader(new FileReader(url)); //"testData\\LeicesterExeter-188725.htm")); //new InputStreamReader(Url.openStream()));
					String line;
					page = new ArrayList<String>();
					while ((line = reader.readLine()) != null) {
						page.add(line);
					}
					reader.close();
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(page);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();

					syncCache.put(url, yourBytes);
				} else {

					// send back the cached version
					ByteArrayInputStream bis = new ByteArrayInputStream(value);
					ObjectInput in = new ObjectInputStream(bis);
					Object obj = in.readObject();
					if (obj instanceof List<?>) {
						page = (List<String>)obj;
					}

					bis.close();
					in.close();

				}
				
				return page;
			}
			catch (MalformedURLException e) {
				Logger.getLogger("Scrum.com").log(Level.SEVERE, e.getMessage());
				return null;
			} catch (IOException e) {
				Logger.getLogger("Scrum.com").log(Level.SEVERE, e.getMessage());
				return null;
			} 
			catch (Throwable ex) {
				Logger.getLogger("Admin Service").log(Level.SEVERE, ex.getMessage(), ex);
				return null;
			}
		} else {
			return null; // no url provided
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher#clear(java.lang.String)
	 */
	@Override
	public void clear(String url) {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.delete(url);
	}
	@Override
	public String getUrl() {
		return url;
	}
	@Override
	public void setUrl(String url) {
		this.url = url;
	}

}
