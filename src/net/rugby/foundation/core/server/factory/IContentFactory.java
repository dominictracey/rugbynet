package net.rugby.foundation.core.server.factory;

import java.util.HashMap;
import java.util.List;

import net.rugby.foundation.model.shared.IContent;

public interface IContentFactory extends ICachingFactory<IContent> {
	List<IContent> getAll(Boolean onlyActive);

	IContent getForDiv(String string);

	HashMap<String, Long> getMenuMap(boolean b);

	IContent get(String title);
}
