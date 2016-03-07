package net.rugby.foundation.admin.server.factory;

import net.rugby.foundation.admin.shared.IDigestEmail;
import net.rugby.foundation.core.server.factory.ICachingFactory;

public interface IDigestEmailFactory extends ICachingFactory<IDigestEmail> {

	void populatedigestEmail(IDigestEmail digestEmail);

}
