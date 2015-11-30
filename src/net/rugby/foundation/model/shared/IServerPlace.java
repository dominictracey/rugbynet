package net.rugby.foundation.model.shared;


public interface IServerPlace extends IHasId {

	enum PlaceType { SERIES, FEATURE }
	
	void setItemId(Long itemId);

	Long getItemId();

	void setListId(Long listId);

	Long getListId();

	void setQueryId(Long queryId);

	Long getQueryId();

	void setMatrixId(Long matrixId);

	Long getMatrixId();

	void setGroupId(Long groupId);

	Long getGroupId();

	void setSeriesId(Long seriesId);

	Long getSeriesId();

	void setFeatureId(Long featureId);

	Long getFeatureId();

	void setCompId(Long compId);

	Long getCompId();

	void setId(Long id);

	Long getId();

	void setType(PlaceType type);

	PlaceType getType();

	void setName(String name);

	String getName();

	void setGuid(String guid);

	String getGuid();

}
