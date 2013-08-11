package net.rugby.foundation.topten.client.mvp;

import net.rugby.foundation.topten.client.place.TopTenListPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * PlaceHistoryMapper interface is used to attach all places which the PlaceHistoryHandler should 
 * be aware of. This is done via the @WithTokenizers annotation or by extending 
 * {@link PlaceHistoryMapperWithFactory} and creating a separate TokenizerFactory.
 */
@WithTokenizers({ TopTenListPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
