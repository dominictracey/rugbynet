package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;


import com.googlecode.objectify.annotation.Entity;

@Entity
public class ServerPlace implements IServerPlace, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5987099863450115375L;
	@Id
	Long id;
	
	protected String guid;
	protected String name;

	protected PlaceType type;
	protected Long compId;
	protected Long featureId;
	protected Long seriesId;
	protected Long groupId;
	protected Long matrixId;
	protected Long queryId;
	protected Long listId;
	protected Long itemId;
	
	/**
	 * @param type
	 * @param compId
	 * @param featureId
	 * @param seriesId
	 * @param groupId
	 * @param matrixId
	 * @param queryId
	 * @param listId
	 * @param itemId
	 */
	public ServerPlace(PlaceType type, Long compId, Long featureId,
			Long seriesId, Long groupId, Long matrixId, Long queryId,
			Long listId, Long itemId) {
		super();
		this.type = type;
		this.compId = compId;
		this.featureId = featureId;
		this.seriesId = seriesId;
		this.groupId = groupId;
		this.matrixId = matrixId;
		this.queryId = queryId;
		this.listId = listId;
		this.itemId = itemId;
	}
	
	public ServerPlace() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public String getGuid() {
		return guid;
	}

	@Override
	public void setGuid(String guid) {
		this.guid = guid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public PlaceType getType() {
		return type;
	}
	@Override
	public void setType(PlaceType type) {
		this.type = type;
	}
	@Override
	public Long getCompId() {
		return compId;
	}

	@Override
	public void setCompId(Long compId) {
		this.compId = compId;
	}

	@Override
	public Long getFeatureId() {
		return featureId;
	}

	@Override
	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}

	@Override
	public Long getSeriesId() {
		return seriesId;
	}

	@Override
	public void setSeriesId(Long seriesId) {
		this.seriesId = seriesId;
	}

	@Override
	public Long getGroupId() {
		return groupId;
	}

	@Override
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Override
	public Long getMatrixId() {
		return matrixId;
	}

	@Override
	public void setMatrixId(Long matrixId) {
		this.matrixId = matrixId;
	}

	@Override
	public Long getQueryId() {
		return queryId;
	}

	@Override
	public void setQueryId(Long queryId) {
		this.queryId = queryId;
	}

	@Override
	public Long getListId() {
		return listId;
	}

	@Override
	public void setListId(Long listId) {
		this.listId = listId;
	}

	@Override
	public Long getItemId() {
		return itemId;
	}

	@Override
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	
	
}
