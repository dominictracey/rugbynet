package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IContent;

public interface IContentFactory extends ICachingFactory<IContent> {
	List<IContent> getAll(Boolean onlyActive);
}
