package net.rugby.foundation.admin.client.mvp;

import net.rugby.foundation.admin.client.place.AdminCompPlace;
import net.rugby.foundation.admin.client.place.AdminTaskPlace;
import net.rugby.foundation.admin.client.place.PortalPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * PlaceHistoryMapper interface is used to attach all places which the PlaceHistoryHandler should 
 * be aware of. This is done via the @WithTokenizers annotation or by extending 
 * {@link PlaceHistoryMapperWithFactory} and creating a separate TokenizerFactory.
 */
@WithTokenizers({ AdminCompPlace.Tokenizer.class, AdminTaskPlace.Tokenizer.class, PortalPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
