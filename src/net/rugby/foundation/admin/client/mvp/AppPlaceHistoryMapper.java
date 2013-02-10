package net.rugby.foundation.admin.client.mvp;

import net.rugby.foundation.admin.client.place.AdminPlace;
import net.rugby.foundation.admin.client.place.AdminTaskPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * PlaceHistoryMapper interface is used to attach all places which the PlaceHistoryHandler should 
 * be aware of. This is done via the @WithTokenizers annotation or by extending 
 * {@link PlaceHistoryMapperWithFactory} and creating a separate TokenizerFactory.
 */
@WithTokenizers({ AdminPlace.Tokenizer.class, AdminTaskPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
