package net.rugby.foundation.game1.client.ui;

import net.rugby.foundation.game1.shared.ILeaderboard;
import net.rugby.foundation.game1.shared.ILeaderboardRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample implementation of {@link LeaderboardView}.
 */
public class LeaderboardViewImpl extends Composite implements LeaderboardView {

	interface Binder extends UiBinder<Widget, LeaderboardViewImpl> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	private Presenter listener;
	
	@UiField
	VerticalPanel panel;
	@UiField 
	HTMLPanel noLeaderboard;
	
	public LeaderboardViewImpl() {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void setData(ILeaderboard data) {
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
			boolean odd = true;
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
			if (panel.getWidgetCount() > 0)
				panel.remove(0);
			panel.add(table);		
		} else {
			noLeaderboard.setVisible(true);

		}
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

}
