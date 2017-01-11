package net.rugby.foundation.model.shared;

import java.io.Serializable;
import javax.persistence.Id;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Venue implements Serializable, IVenue {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2006937785333121324L;
	@Id
	private Long id;
	private String venueName;
	private String venueCity;
	private String venueCountry;
	
	public Venue() {};
	
	public Venue(Long id, String venueName){
		this.id = id;
		this.venueName = venueName;
	}
	
	public Venue(Long id, String venueName, String venueCity, String venueCountry){
		this.id = id;
		this.venueName = venueName;
		this.venueCity = venueCity;
		this.venueCountry = venueCountry;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	public String getVenueCity() {
		return venueCity;
	}

	public void setVenueCity(String venueCity) {
		this.venueCity = venueCity;
	}

	public String getVenueCountry() {
		return venueCountry;
	}

	public void setVenueCountry(String venueCountry) {
		this.venueCountry = venueCountry;
	}
	
	
}
