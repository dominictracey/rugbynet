package net.rugby.foundation.server;

import net.rugby.foundation.model.shared.Player;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class DataService extends RemoteServiceServlet {

	private Objectify ofy;
	
	public DataService() {
		ofy = ObjectifyService.begin();
		
		ObjectifyService.register(Player.class);
	}
	
	public int getNativePlayerId(int irbId)
	{
		return 23098;
	}
	
	public String getPlayerPosition(long playerID)
	{
		return "PROP";
	}
}
