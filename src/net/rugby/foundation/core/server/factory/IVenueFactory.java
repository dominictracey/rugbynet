package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.IVenue;

public interface IVenueFactory extends ICachingFactory<IVenue> {

	IVenue getByVenueName(String venueName);
}
