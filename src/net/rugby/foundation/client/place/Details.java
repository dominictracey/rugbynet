package net.rugby.foundation.client.place;

import java.util.ArrayList;

import net.rugby.foundation.model.shared.Group.GroupType;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class Details extends Place
{
	private Long RootID;
	public Long getRootID() {
		return RootID;
	}

	public void setRootID(Long rootID) {
		RootID = rootID;
	}

	public GroupType getGroupTypeID() {
		return GroupTypeID;
	}

	public void setGroupTypeID(GroupType groupTypeID) {
		GroupTypeID = groupTypeID;
	}

	public Long getGroupID() {
		return GroupID;
	}

	public void setGroupID(Long groupID) {
		GroupID = groupID;
	}

	private GroupType GroupTypeID;
	private Long GroupID;
	private ArrayList<Long> editPlayerIDs;
	private static String seps = "\\+";
	
	public Details(String token)
	{
		ArrayList<String> editPlayerIDs = new ArrayList<String>();
		
		String[] tok = token.split(seps);
		for (int i=0; i<tok.length; ++i)
			editPlayerIDs.add(tok[i]);

	}

	public Details(Long rootID, GroupType groupTypeID, Long groupID,
			ArrayList<Long> editPlayerIDs) {
		super();
		RootID = rootID;
		GroupTypeID = groupTypeID;
		GroupID = groupID;
		this.editPlayerIDs = editPlayerIDs;
	}

	public Details() {
		editPlayerIDs = new ArrayList<Long>();
	}

	public ArrayList<Long> getEditPlayerIDs() {
		return editPlayerIDs;
	}
	
	public void setEditPlayerIDs(ArrayList<Long> editPlayerIDs) {
		this.editPlayerIDs = editPlayerIDs;
	}

	public static class Tokenizer implements PlaceTokenizer<Details>
	{
		@Override
		public String getToken(Details place)
		{
			String pList = place.RootID + "+" + place.GroupTypeID + "+" + place.GroupID ;
			for (Long p : place.getEditPlayerIDs())
			{
				pList += "+" + p;
			}

			return pList;
		}

		@Override
		public Details getPlace(String token)
		{
			
			return new Details(token);
		}
	}

}
