package net.rugby.foundation.topten.server.staticpages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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

public class TopNavGenerator{


	private static final long serialVersionUID = 1L;
	private static final String TOP_NAV_CACHE_PREFIX = "TNCP-";


	public TopNavGenerator() {
	}

	public String getContent()  throws IOException {
		String pageFrag = getFromCache();
		if (pageFrag != null) return pageFrag;
		pageFrag = buildContent();
		putToCache(pageFrag);
		return pageFrag;
	}

	private String getFromCache() {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			String page = null;

			value = (byte[])syncCache.get(TOP_NAV_CACHE_PREFIX); 
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

	private void putToCache(String page) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(page);
			byte[] yourBytes = bos.toByteArray(); 

			out.close();
			bos.close();

			syncCache.put(TOP_NAV_CACHE_PREFIX, yourBytes);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		}
	}

	private String buildContent() {
//		return 
//"<div class=\"panel panel-default\">" +
//"    <div class=\"panel-heading\">" +
//"        <h4 class=\"panel-title\">" +
//"            <a data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#topPlaceholder\"><span class=\"glyphicon glyphicon-globe\">" +
//"            </span>Global</a>" +
//"        </h4>" +
//"    </div>" +
//"    <div id=\"topPlaceholder\" class=\"panel-collapse collapse in\">" +
//"        <div class=\"panel-body\">" +
//"            <table class=\"table\">" +
//"                <tr>" +
//"                    <td>" +
//"                        <a href=\"#\">Home Top Placeholder</a>" +
//"                    </td>" +
//"                <tr>" +
//"                    <td>" +
//"                        <a href=\"#\">By Top Placeholder</a>" +
//"                    </td>" +
//"                </tr>" +
//"                " +
//"            </table>" +
//"        </div>" +
//"    </div>" +
//"</div>";
		return "<div id=\"sidebar-nav\" class=\"competition-menu\"><ul class=\"\" id=\"dashboard-menu\"><li class=\"active\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>Global</span><b class=\"caret\"></b></a><ul class=\"submenu\" style=\"display: block;\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li></ul><div class=\"pointer\"><div class=\"arrow\"></div><div class=\"arrow_border\"></div></div></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>Europe</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Round</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li></ul></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>S. Hemisphere</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Round</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li></ul></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>Rugby World Cup</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Match</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Round</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Team</a></li></ul></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>Six Nations</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Match</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Round</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li></ul></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>2015 Friendlies</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Match</a></li></ul></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>Rugby Championship</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Match</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Round</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li></ul></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>Super Rugby</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Match</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Round</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li></ul></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>Top 14 Orange</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Match</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Round</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li></ul></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>Champions Cup</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Match</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Round</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li></ul></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>Guinness PRO12</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Match</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Round</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Team</a></li></ul></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>Aviva Premiership</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Match</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Round</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Team</a></li></ul></li><li class=\"\"><a href=\"javascript:;\" class=\"dropdown-toggle\"><i class=\"fa fa-globe\"></i><span>Challenge Cup</span><b class=\"caret\"></b></a><ul class=\"submenu\"><li class=\"null\"><span></span><a href=\"javascript:;\">Home</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Match</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Round</a></li><li class=\"null\"><span></span><a href=\"javascript:;\">By Position</a></li></ul></li></ul></div>";

	}
}

//Example HTML for menu from designer...
//Only the div ids and hrefs seem to be different

//Mobile...
/*
<div class="panel panel-default">
	<div class="panel-heading">
	    <h4 class="panel-title">
	        <a data-toggle="collapse" data-parent="#accordion" href="#One"><span class="glyphicon glyphicon-globe">
	        </span>Global</a>
	    </h4>
	</div>
	<div id="One" class="panel-collapse collapse in">
	    <div class="panel-body">
	        <table class="table">
	            <tr>
	                <td>
	                    <a href="#">Home</a>
	                </td>
	            </tr>
	            <tr>
	                <td>
	                    <a href="#">By Position</a>
	                </td>
	            </tr>
	            
	        </table>
	    </div>
	</div>
</div>
*/

//Desktop...
/*
<div class="panel panel-default">
    <div class="panel-heading">
        <h4 class="panel-title">
            <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne"><span class="glyphicon glyphicon-globe">
            </span>Global</a>
        </h4>
    </div>
    <div id="collapseOne" class="panel-collapse collapse in">
        <div class="panel-body">
            <table class="table">
                <tr>
                    <td>
                        <a href="#">Home</a>
                    </td>
                </tr>
                <tr>
                    <td>
                        <a href="#">By Position</a>
                    </td>
                </tr>
                
            </table>
        </div>
    </div>
</div>
*/
