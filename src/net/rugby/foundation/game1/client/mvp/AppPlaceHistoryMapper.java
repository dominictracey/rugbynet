package net.rugby.foundation.game1.client.mvp;

import net.rugby.foundation.game1.client.place.Game1Place;
import net.rugby.foundation.game1.client.place.JoinClubhouse;
import net.rugby.foundation.game1.client.place.Profile;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * PlaceHistoryMapper interface is used to attach all places which the PlaceHistoryHandler should 
 * be aware of. This is done via the @WithTokenizers annotation or by extending 
 * {@link PlaceHistoryMapperWithFactory} and creating a separate TokenizerFactory.
 */
@WithTokenizers({ Game1Place.Tokenizer.class, JoinClubhouse.Tokenizer.class, Profile.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
