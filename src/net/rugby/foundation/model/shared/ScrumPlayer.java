package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class ScrumPlayer implements IPlayer, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2562186381925124329L;

	@Id
	private Long id;
	
	private Long scrumId;
	
	private String displayName;

	private Date birthDate;

	private Float height;

	private Float weight;

	private String imageUri;

	private ICountry country;

	private Position.position position;

	private Integer numCaps;



	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getScrumId() {
		return scrumId;
	}

	@Override
	public void setScrumId(Long scrumId) {
		this.scrumId = scrumId;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public Date getBirthDate() {
		return birthDate;
	}

	@Override
	public void setBirthDate(Date birthDay) {
		this.birthDate = birthDay;
	}

	@Override
	public Float getHeight() {
		return height;
	}

	@Override
	public void setHeight(Float height) {
		this.height = height;
	}

	@Override
	public Float getWeight() {
		return weight;
	}

	@Override
	public void setWeight(Float weight) {
		this.weight = weight;
	}

	@Override
	public String getImageUri() {
		return imageUri;
	}

	@Override
	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}

	@Override
	public ICountry getCountry() {
		return country;
	}

	@Override
	public void setCountry(ICountry country) {
		this.country = country;
	}

	@Override
	public Position.position getPosition() {
		return position;
	}

	@Override
	public void setPosition(Position.position position) {
		this.position = position;
	}

	@Override
	public Integer getNumCaps() {
		return numCaps;
	}

	@Override
	public void setNumCaps(Integer numCaps) {
		this.numCaps = numCaps;
	}

}
