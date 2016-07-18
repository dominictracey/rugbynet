package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

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
	private Long countryId;
	@Transient
	private ICountry country;
	private Position.position position;
	private Integer numCaps;
	private String givenName;
	private String surName;
	private String shortName;	
	private String twitterHandle;
	
	@Unindexed
	private List<Long> taskIds;
	@Unindexed
	private List<Long> blockingTaskIds;

	private Boolean twitterNotAvailable;
	
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

	@Override
	public Long getCountryId() {
		return countryId;
	}

	@Override
	public void setCountryId(Long id) {
		this.countryId = id;
	}
	@Override
	public String getGivenName() {
		return givenName;
	}
	@Override
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	@Override
	public String getSurName() {
		return surName;
	}
	@Override
	public void setSurName(String surName) {
		this.surName = surName;
	}
	@Override
	public String getShortName() {
		return shortName;
	}
	@Override
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Override
	public String getForeignUrl() {
		return "http://en.espn.co.uk/scrum/rugby/player/" + scrumId.toString() + ".html";
	}
	@Override
	public String getTwitterHandle() {
		return twitterHandle;
	}
	@Override
	public void setTwitterHandle(String twitterHandle) {
		this.twitterHandle = twitterHandle;
	}
	@Override
	public List<Long> getTaskIds() {
		if (taskIds == null) {
			taskIds = new ArrayList<Long>();
		}
		return taskIds;
	}
	@Override
	public void setTaskIds(List<Long> taskIds) {
		this.taskIds = taskIds;
	}
	@Override
	public List<Long> getBlockingTaskIds() {
		if (blockingTaskIds == null) {
			blockingTaskIds = new ArrayList<Long>();
		}
		return blockingTaskIds;
	}
	@Override
	public void setBlockingTaskIds(List<Long> taskIds) {
		this.blockingTaskIds = taskIds;
	}

	@Override
	public Boolean getTwitterNotAvailable() {
		return twitterNotAvailable;
	}

	@Override
	public void setTwitterNotAvailable(Boolean twitterNotAvailable) {
		this.twitterNotAvailable = twitterNotAvailable;
		if (twitterNotAvailable){
			setTwitterHandle(null);
		}
	}

}
