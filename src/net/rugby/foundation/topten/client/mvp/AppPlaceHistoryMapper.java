package net.rugby.foundation.topten.client.mvp;

import net.rugby.foundation.topten.client.place.ContentPlace;
import net.rugby.foundation.topten.client.place.FeatureListPlace;
import net.rugby.foundation.topten.client.place.LegacyListPlace;
import net.rugby.foundation.topten.client.place.SeriesPlace;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * PlaceHistoryMapper interface is used to attach all places which the PlaceHistoryHandler should 
 * be aware of. This is done via the @WithTokenizers annotation or by extending 
 * {@link PlaceHistoryMapperWithFactory} and creating a separate TokenizerFactory.
 */
@WithTokenizers({ FeatureListPlace.Tokenizer.class, ContentPlace.Tokenizer.class, SeriesPlace.Tokenizer.class, LegacyListPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
