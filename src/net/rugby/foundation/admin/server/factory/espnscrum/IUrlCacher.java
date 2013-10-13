package net.rugby.foundation.admin.server.factory.espnscrum;

import java.util.List;

public interface IUrlCacher {

	public abstract List<String> get();

	public abstract void clear(String url);

	String getUrl();

	void setUrl(String url);

}