package net.rugby.foundation.admin.server.factory;

import java.util.List;

import net.rugby.foundation.admin.shared.IBlurb;
import net.rugby.foundation.core.server.factory.ICachingFactory;

public interface IBlurbFactory extends ICachingFactory<IBlurb> {

	List<IBlurb> getActive();
}
