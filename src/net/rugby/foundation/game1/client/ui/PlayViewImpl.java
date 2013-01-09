package net.rugby.foundation.game1.client.ui;

import net.rugby.foundation.core.client.Core;
import net.rugby.foundation.core.client.Identity;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IRound;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample implementation of {@link PlayView}.
 */
public class PlayViewImpl extends Composite implements PlayView {

	interface Binder extends UiBinder<Widget, PlayViewImpl> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	private Presenter listener;
	@UiField
	Button button;
	@UiField
	TabBar tabBar;
	@UiField
	HTMLPanel pickArea;
	@UiField
	Label tbHomeName;
	@UiField
	TextBox tbHomeScore;
	@UiField
	Label tbVisitName;
	@UiField
	TextBox tbVisitScore;
	@UiField
	HTMLPanel tbInstructions;
	@UiField
	Button signInButton;	
	@UiField
	Button signUpButton;	
	@UiField
	Button createNewEntry;
	@UiField
	HTMLPanel pickPlease;
	@UiField
	HTMLPanel noLogin;
	@UiField
	FlowPanel playPanel;
	
	ICompetition comp;
	IEntry entry;
	IRound currentRound;
	
	private HandlerRegistration homeScoreHandlerRegistration = null;
	private HandlerRegistration visitScoreHandlerRegistration = null;
	
	public PlayViewImpl() {
		initWidget(binder.createAndBindUi(this));
		tbHomeName.setStylePrimaryName("tiebreakerCell");
		tbVisitName.setStylePrimaryName("tiebreakerCell");
		tbInstructions.setStylePrimaryName("tiebreakerInstructions");
		tbHomeScore.setStylePrimaryName("tiebreakerEntry");
		tbVisitScore.setStylePrimaryName("tiebreakerEntry");
		tabBar.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (entry != null) {
					showRound(comp.getRounds().get(event.getSelectedItem()),entry);		
				}
			}
		
		});

		pickPlease.setVisible(false);
		noLogin.setVisible(false);
		playPanel.setVisible(false);
	}

	@Override
	public void setEntry(IEntry entry, ICompetition comp) {
		pickPlease.setVisible(false);
		noLogin.setVisible(false);
		playPanel.setVisible(true);
		
		this.comp = comp;
		this.entry = entry;
		
		clear();	
		
		for (IRound r: comp.getRounds()) {
			tabBar.addTab(r.getAbbr());
		}
		
	
		currentRound = comp.getNextRound();
		//showRound(currentRound, entry);
		if (currentRound != null)
			tabBar.selectTab(currentRound.getOrdinal()-1);
		else
			tabBar.selectTab(tabBar.getTabCount()-1);
				

	}

	/**
	 * @param r - the round to display for picking
	 */
	private void showRound(final IRound r, IEntry e) {
		
		//clear it
		int count = pickArea.getWidgetCount();
		for (int i=0; i < count; ++i) {	
			pickArea.remove(0);
		}
		tbHomeScore.setText("");
		tbVisitScore.setText("");
		if (homeScoreHandlerRegistration != null) {
			homeScoreHandlerRegistration.removeHandler();
			homeScoreHandlerRegistration = null;
		}
		if (visitScoreHandlerRegistration != null)  {
			visitScoreHandlerRegistration.removeHandler();
			visitScoreHandlerRegistration = null;
		}

		IRoundEntry re = entry.getRoundEntries().get(r.getId());
		count = 1; // number of matches thus far so we can find the last one.
		// add for each match
		for (final IMatchGroup m : r.getMatches()) {
			
			pickArea.add(new PlayPanel(m,e,r, (PlayPanel.Presenter)listener));
			
			// we use the last match in the round for the tiebreaker
			if (r.getMatches().size() == count) {
				tbHomeName.setText(m.getHomeTeam().getDisplayName());
				tbVisitName.setText(m.getVisitingTeam().getDisplayName());
				if (m.getStatus() != Status.SCHEDULED && m.getStatus() != Status.RESCHEDULED && m.getStatus() != Status.POSTPONED) {
					tbHomeScore.setEnabled(false);
					tbVisitScore.setEnabled(false);
				} else {
					tbHomeScore.setEnabled(true);
					tbVisitScore.setEnabled(true);
				
					homeScoreHandlerRegistration = tbHomeScore.addBlurHandler(new BlurHandler() {
						
						@Override
						public void onBlur(BlurEvent event) {
							if (!tbHomeScore.getText().isEmpty()) {
								if (!tbHomeScore.getText().matches("\\d*$")) {
									tbHomeScore.setText("");
									Window.alert("Not a valid score");
								} else {
									listener.tiebreakerHomeScoreSet(entry, r, m, m.getHomeTeam(), Integer.parseInt(tbHomeScore.getText()));
								}
							}
						}
						
					});
					
					visitScoreHandlerRegistration = tbVisitScore.addBlurHandler(new BlurHandler() {
						
						@Override
						public void onBlur(BlurEvent event) {
							if (!tbVisitScore.getText().isEmpty()) {
								if (!tbVisitScore.getText().matches("\\d*$")) {
									tbVisitScore.setText("");
									Window.alert("Not a valid score");
								} else {
									listener.tiebreakerVisitScoreSet(entry, r, m, m.getVisitingTeam(), Integer.parseInt(tbVisitScore.getText()));
								}
							}
						}
						
					});
				}
				
				if (re != null) {
					if (m.getId().equals(re.getTieBreakerMatchId())) {
						String homeScore = "";
						String visitScore = "";
						
						if (re.getTieBreakerHomeScore() != null)  {
							homeScore = re.getTieBreakerHomeScore().toString();
						}
						
						if (re.getTieBreakerVisitScore() != null) {
							visitScore = re.getTieBreakerVisitScore().toString();
						}
						
						tbHomeScore.setText(homeScore);
						tbVisitScore.setText(visitScore);

					}
				}
			}
			count++;
		}
		
		button.setHTML("Save Round");

		if (currentRound != null && r.getOrdinal() >= currentRound.getOrdinal()) {
			button.setStylePrimaryName("highlight");
			button.setVisible(true);
		} else {
			button.setVisible(false);
		}
		tbInstructions.setVisible(true);
		tbHomeName.setVisible(true);
		tbHomeScore.setVisible(true);
		tbVisitName.setVisible(true);
		tbVisitScore.setVisible(true);
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
		Identity identity = Core.getCore().getClientFactory().getIdentityManager();

		signInButton.addClickHandler(identity.getSignInHandler());
		signUpButton.addClickHandler(identity.getSignUpHandler());
	}

	@UiHandler("button")
	void onButtonClick(ClickEvent event) {
		// capture the tiebreaker score
		
		listener.saveEntry(entry);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.PlayView#clear()
	 */
	@Override
	public void clear() {
		int count = tabBar.getTabCount();
		for (int i=0; i < count; ++i) {
			tabBar.removeTab(0);			
		}
		
		count = pickArea.getWidgetCount();
		for (int i=0; i < count; ++i) {	
			pickArea.remove(0);
		}
		
		tbInstructions.setVisible(false);
		tbHomeName.setVisible(false);
		tbHomeScore.setVisible(false);
		tbVisitName.setVisible(false);
		tbVisitScore.setVisible(false);
		
		button.setVisible(false);
		
		// tell them to pick or login if we have no entry
		if (entry == null) {
			if (Core.getCore().getClientFactory().getLoginInfo().isLoggedIn()) {
				noLogin.setVisible(false);
				pickPlease.setVisible(true);
			} else {
				pickPlease.setVisible(false);
				noLogin.setVisible(true);
			}
		}
	}
	
	@UiHandler("createNewEntry")
	void onNewEntryClick(ClickEvent event) {
		
		if (listener != null) {
			listener.newEntryClicked();
		}
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
}
