package net.rugby.foundation.core.client.nav;

import java.util.HashMap;

import org.gwtbootstrap3.client.ui.Nav;

import net.rugby.foundation.core.client.CoreClientFactory;
import net.rugby.foundation.model.shared.LoginInfo;

public abstract class ContentBuilder implements INavBuilder {

	protected Nav parent;
	protected CoreClientFactory clientFactory;
	protected LoginInfo loginInfo;
	protected HashMap<String, Long> map;
	
	public HashMap<String, Long> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Long> map) {
		this.map = map;
	}

	@Override
	public void setParent(Nav div) {
		this.parent = div;

	}

	public abstract void build(HashMap<String, Long> map);
	

}
