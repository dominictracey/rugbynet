package net.rugby.foundation.client.mvp;

import net.rugby.foundation.client.place.Details;
import net.rugby.foundation.client.place.Browse;
import net.rugby.foundation.client.place.Home;
import net.rugby.foundation.client.place.Manage;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * PlaceHistoryMapper interface is used to attach all places which the
 * PlaceHistoryHandler should be aware of. This is done via the @WithTokenizers
 * annotation or by extending PlaceHistoryMapperWithFactory and creating a
 * separate TokenizerFactory.
 */
@WithTokenizers( { Browse.Tokenizer.class, Details.Tokenizer.class, Home.Tokenizer.class, Manage.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
