package net.rugby.foundation.game1.client.ui;

import java.util.List;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.ui.CreateClubhouse;
import net.rugby.foundation.game1.shared.ILeaderboard;
import net.rugby.foundation.game1.shared.ILeaderboardRow;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IClubhouseMembership;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample implementation of {@link LeaderboardView}.
 */
public class ClubhouseViewImpl extends Composite implements ClubhouseView {

	interface Binder extends UiBinder<Widget, ClubhouseViewImpl> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	private Presenter listener;
	@UiField
	VerticalPanel panel;
	@UiField
	HTMLPanel noLeaderboard;
	@UiField
	HTMLPanel clubhouseMembers;
	@UiField
	Label clubhouseName;
	@UiField 
	HTMLPanel clubhouseInfo;
	@UiField
	Label clubhouseDescription;
	@UiField
	TextBox joinLink;
	@UiField 
	Button createNewClubhouse;
	
	public ClubhouseViewImpl() {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void setData(ILeaderboard data, IClubhouse clubhouse, List<IClubhouseMembership> members) {
		if (data != null) {
			noLeaderboard.setVisible(false);
			int count = 0;
			Grid table = new Grid(data.getRows().size()+1, data.getRoundNames().size()+3);
	
			table.setWidget(0, 0, new HTML("Rank"));
			table.setHTML(0, 1, "Name");
			int numRounds = data.getRoundNames().size();
			for (int i = 0; i<numRounds; ++i) {
				table.setHTML(0, i+2, data.getRoundNames().get(i));
			}
			table.getRowFormatter().setStylePrimaryName(0, "leaderboardRow");
			table.getRowFormatter().addStyleName(0, "leaderboardRow-header");
			
			table.setHTML(0,data.getRoundNames().size()+2, "Total");
			boolean odd = false;
			for (ILeaderboardRow lbr : data.getRows()) {
				count++;
				table.setHTML(count, 0, Integer.toString(lbr.getRank()));
				table.setHTML(count, 1, lbr.getAppUserName()+ ","+ lbr.getEntryName());
				for (int i=0; i<lbr.getScores().size(); ++i) {
					if (lbr.getScores().get(i) != null) 
						table.setHTML(count, i+2, Integer.toString(lbr.getScores().get(i)));
					else
						table.setHTML(count, i+2, "--");
				}
	
				table.setHTML(count, numRounds+2, Integer.toString(lbr.getTotal()));
				if (odd) {
					table.getRowFormatter().setStylePrimaryName(count, "leaderboardRow");
					table.getRowFormatter().addStyleName(count, "leaderboardRow-odd");
				}
				else {
					table.getRowFormatter().setStylePrimaryName(count, "leaderboardRow");
					table.getRowFormatter().addStyleName(count, "leaderboardRow-even");
				}
				odd = !odd;
			}
			table.setStylePrimaryName("panel");
			clear();
			panel.add(table);	
			

				
			} 			
	
		if (members != null) {
			showMembers(members);
		}
		
		if (clubhouse != null) {
			showInfo(clubhouse);
		}
		

	}

	/**
	 * @param clubhouse
	 */
	private void showMembers(List<IClubhouseMembership> members) {
		boolean odd = true;
		int count = 1;
		Grid table = new Grid(members.size()+1, 1);
		table.setWidget(0, 0, new HTML("Members"));
		table.getRowFormatter().setStylePrimaryName(0, "leaderboardRow");
		table.getRowFormatter().addStyleName(0, "leaderboardRow-header");
		String myName = Core.getCore().getClientFactory().getLoginInfo().getNickname();
		for (IClubhouseMembership chm: members) {
			table.setHTML(count,0,chm.getUserName());
			
			if (odd) {
				table.getRowFormatter().setStylePrimaryName(count, "leaderboardRow");
				table.getRowFormatter().addStyleName(count, "leaderboardRow-odd");
			}
			else {
				table.getRowFormatter().setStylePrimaryName(count, "leaderboardRow");
				table.getRowFormatter().addStyleName(count, "leaderboardRow-even");
			}
			
			if (chm.getUserName().equals(myName)) {
				table.getRowFormatter().addStyleName(count, "highlight");
			}
			odd = !odd;
			++count;
		}
		
		while (clubhouseMembers.getWidgetCount()  != 0)  {
			clubhouseMembers.remove(0);  //table
		}
		clubhouseMembers.add(table);
		clubhouseMembers.setVisible(true);
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
		
		
		if (listener instanceof CreateClubhouse.Presenter) {
			createNewClubhouse.addClickHandler(Core.getCore().showCreateClubhouse((CreateClubhouse.Presenter)listener));
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.ClubhouseView#setClubhouse(net.rugby.foundation.model.shared.IClubhouse)
	 */
	@Override
	public void clear() {
		if (panel.getWidgetCount() > 1)  {
			panel.remove(0);  //title
			panel.remove(0);  //table
		}
		
		clubhouseInfo.setVisible(false);
		clubhouseMembers.setVisible(false);

	}
	
	private void showInfo(IClubhouse clubhouse) {
		clubhouseName.setText(clubhouse.getName());
		clubhouseDescription.setText(clubhouse.getDescription());
		joinLink.setText(clubhouse.getJoinLink());
		joinLink.setStylePrimaryName("joinLink");
		//joinLink.setEnabled(false);
		clubhouseInfo.setVisible(true);

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.ClubhouseView#showCreateClubhouse(boolean)
	 */
	@Override
	public void showCreateClubhouse(boolean show) {
			createNewClubhouse.setVisible(show);
		
	}

}
