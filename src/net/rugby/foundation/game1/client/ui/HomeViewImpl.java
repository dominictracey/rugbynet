package net.rugby.foundation.game1.client.ui;

import java.util.List;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity;
import net.rugby.foundation.core.client.ui.CreateClubhouse;
import net.rugby.foundation.game1.client.ClientFactory;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample implementation of {@link HomeView}.
 */
public class HomeViewImpl extends Composite implements HomeView {

	interface Binder extends UiBinder<Widget, HomeViewImpl> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	private Presenter listener;
	@UiField
	HTMLPanel entryNames;
	@UiField
	Button createNewEntry;
	@UiField
	Button signInButton;
	@UiField
	Button signUpButton;
	@UiField
	Anchor signIn;
	@UiField
	Anchor signUp;
	@UiField
	Anchor createClubhouse;
	@UiField
	HTMLPanel noLogonWelcome;
	@UiField
	Label clubhouseToJoin;
	@UiField
	HTMLPanel createClubhousePanel;
	@UiField
	Label currentCompName;
	@UiField
	HTMLPanel compNotUnderway;

	private JoinClubhousePresenter joinClubhouseListener;
		
	public HomeViewImpl() {
		initWidget(binder.createAndBindUi(this));
		
		getElement().addClassName("playarea");
		Identity identity = Core.getCore().getClientFactory().getIdentityManager();
		signIn.addClickHandler(identity.getSignInHandler());
		signUp.addClickHandler(identity.getSignUpHandler());
		signInButton.addClickHandler(identity.getSignInHandler());
		signUpButton.addClickHandler(identity.getSignUpHandler());
		//entryNames.getElement().addClassName("resultPanel");
		
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
		

		
		if (listener instanceof CreateClubhouse.Presenter) {
			createClubhouse.addClickHandler(Core.getCore().showCreateClubhouse((CreateClubhouse.Presenter)listener));
		}
		
	}

	
	@UiHandler("createNewEntry")
	void onNewEntryClick(ClickEvent event) {
		
		if (listener != null) {
			listener.newEntryClicked();
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.HomeView#setEntries(java.util.ArrayList)
	 */
	@Override
	public void setEntries(List<IEntry> entries) {
		clubhouseToJoin.setVisible(false);
		noLogonWelcome.setVisible(false);
		
		if (entryNames.getWidgetCount() > 0)
			entryNames.remove(0);
		
		if (entries != null) {
			Grid vp = new Grid(entries.size()+1, 2);
			vp.setWidget(0, 0, new HTML("Entry"));
			vp.setHTML(0, 1, "Score");
			vp.getRowFormatter().setStylePrimaryName(0, "leaderboardRow");
			vp.getRowFormatter().addStyleName(0, "leaderboardRow-header");
			vp.getCellFormatter().setStylePrimaryName(0, 0, "leaderboardCell");
			boolean odd = false;
			vp.setStylePrimaryName("panel");
	
			entryNames.add(vp);
			int count = 1;
			for (IEntry e : entries) {
	  			Anchor entryLink = new Anchor(e.getName());
	  			entryLink.addClickHandler(new EntryClickHandler(e, listener));
	  			vp.setWidget(count, 0, entryLink);
	  			vp.getCellFormatter().setStylePrimaryName(count, 0, "leaderboardCell");
	  			vp.setWidget(count, 1, new Label(e.getScore().toString()));
				vp.getRowFormatter().setStylePrimaryName(count, "leaderboardRow");
				if (odd) {
					vp.getRowFormatter().addStyleName(count, "leaderboardRow-odd");
				}
				else {
					vp.getRowFormatter().addStyleName(count, "leaderboardRow-even");
				}
				odd = !odd;
				count++;
			}
			if (entries.size() > 0)
				createNewEntry.removeStyleName("highlight");
		} else {
			createNewEntry.setStylePrimaryName("highlight");
		}
		
//		entryNames.add(new HTML("</div>"));
		
	}

	class EntryClickHandler implements ClickHandler {
		private IEntry e;
		private Presenter listener;
		public EntryClickHandler(IEntry e, Presenter p) {
			this.setE(e);
			this.listener = p;
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
		 */
		@Override
		public void onClick(ClickEvent event) {
			listener.entryClicked(e);
			
		}

		public IEntry getE() {
			return e;
		}

		public void setE(IEntry e) {
			this.e = e;
		}
		
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.HomeView#showAccountButtons(boolean)
	 */
	@Override
	public void showAccountButtons(boolean show) {
		signInButton.setVisible(show);
		signUpButton.setVisible(show);
		createNewEntry.setVisible(!show);
		compNotUnderway.setVisible(false);
		if (show) {
			noLogonWelcome.setVisible(true);
			ClientFactory cf = null;
			if (listener != null) {
				cf = listener.getClientFactory();
			} else if (joinClubhouseListener != null) {
				cf = joinClubhouseListener.getClientFactory();
			}
			if (cf != null) {
				if (cf.isJoiningClubhouse()) {
					Core.getCore().getClubhouse(cf.getClubhouseToJoinId(), new AsyncCallback<IClubhouse>() {

						@Override
						public void onFailure(Throwable caught) {
							// no-op
							
						}

						@Override
						public void onSuccess(IClubhouse result) {
							// let them know we know they are joining a clubhouse
							clubhouseToJoin.setText("We see you are joining the clubhouse for " + result.getName() + " but let's get you signed in first.");
							clubhouseToJoin.setVisible(true);
							
						}
						
					});
					
				}
			} 
		}
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.HomeView#showCreateClubhouse(boolean)
	 */
	@Override
	public void showCreateClubhouse(boolean show) {
			createClubhousePanel.setVisible(show);
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.HomeView#setJoinClubhousePresenter(net.rugby.foundation.game1.client.ui.HomeView.JoinClubhousePresenter)
	 */
	@Override
	public void setJoinClubhousePresenter(JoinClubhousePresenter presenter) {
		this.joinClubhouseListener = presenter;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.HomeView#showCompOver(net.rugby.foundation.model.shared.ICompetition)
	 */
	@Override
	public void showCompOver(ICompetition comp) {
		noLogonWelcome.setVisible(false);
		signInButton.setVisible(false);
		signUpButton.setVisible(false);
		createNewEntry.setVisible(false);		
		currentCompName.setText(comp.getShortName());
		compNotUnderway.setVisible(true);		
	}

}
