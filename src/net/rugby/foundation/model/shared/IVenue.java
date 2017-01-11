package net.rugby.foundation.model.shared;

public interface IVenue extends IHasId {
	
	public abstract Long getId();
	public abstract void setId(Long id);
	
	public abstract String getVenueName();
	public abstract void setVenueName(String venueName);
	
	public abstract String getVenueCity();
	public abstract void setVenueCity(String venueCity);
	
	public abstract String getVenueCountry();
	public abstract void setVenueCountry(String venueCountry);
}
