package net.rugby.foundation.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.model.shared.LoginInfo;

public class HomeViewImpl<T> extends Composite implements HomeView<T>
{
	

	private static HomeViewImplUiBinder uiBinder = GWT.create(HomeViewImplUiBinder.class);
	//@UiTemplate("HomeViewImpl.ui.xml")	
	


	interface HomeViewImplUiBinder extends UiBinder<Widget, HomeViewImpl<?>>
	{
	}

	@UiField HTMLPanel noLogin;
	@UiField HTMLPanel noDraft;
	@UiField HTMLPanel noCurrentRoundPicked;
	@UiField HTMLPanel complete;
	@UiField Anchor signin;	
	@UiField Anchor signup;	
	@UiField Anchor draft;
	@UiField Anchor randomDraft;  
	@UiField Anchor currentRound;
	@UiField Anchor createLeague;
	@UiField Anchor reuseLastPick;
	
	private Presenter<T> presenter;
	private LoginInfo user;
	
	public HomeViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	ClientFactory clientFactory;
	

	@UiHandler("signin")
	void onSignInButtonClicked(ClickEvent event) {	
		if (presenter != null) {
		      presenter.onSignInButtonClicked();
		    }
	  }	
	
	@UiHandler("signup")
	void onSignUpButtonClicked(ClickEvent event) {	
		if (presenter != null) {
		      presenter.onSignUpButtonClicked();
		    }
	  }
	@UiHandler("draft")
	void onDraftButtonClicked(ClickEvent event) {	
		if (presenter != null) {
		      presenter.onDraftButtonClicked();
		    }
	  }
	
	  @UiHandler("randomDraft")
	  void onRandomButtonClicked(ClickEvent event) {	
		    if (presenter != null) {
		      presenter.onRandomButtonClicked();
		    }
	  }
	  @UiHandler("currentRound")
	  void onRoundButtonClicked(ClickEvent event) {	
		    if (presenter != null) {
		      presenter.onRoundButtonClicked();
		    }
	  }
	  @UiHandler("createLeague")
	  void onCreateLeagueClicked(ClickEvent event) {	
		    if (presenter != null) {
		      presenter.onCreateLeagueClicked();
		    }
	  }
	  
	@UiHandler("reuseLastPick")
	void onReuseClicked(ClickEvent event) {	
		if (presenter != null) {
		      presenter.onReuseButtonClicked();
		    }
	  }	
		
	@Override
	public void setPresenter(Presenter<T> listener) {
		presenter = listener;
		
	}

	@Override
	public void setUser(T loggedInUser) {
		if (loggedInUser instanceof LoginInfo) {
			user = (LoginInfo)loggedInUser;
		}
		
	}

	@Override
	public void clear() {
		noLogin.setVisible(false);
		noDraft.setVisible(false);
		noCurrentRoundPicked.setVisible(false);
		complete.setVisible(false);	
	}

	@Override
	public void showDraftButton(boolean show) {
		draft.setVisible(show);
		
	}

	@Override
	public void showRandomButton(boolean show) {
		randomDraft.setVisible(show);
		
	}

	@Override
	public void showRoundButton(boolean show) {
		currentRound.setVisible(show);
		
	}

	@Override
	public void showSignInButton(boolean show) {
		signin.setVisible(show);
		
	}

	@Override
	public void showSignUpButton(boolean show) {
		signup.setVisible(show);
		
	}

	@Override
	public void showCreateLeague(boolean show) {
		createLeague.setVisible(show);
		
	}
	
	@Override
	public void showNoLogin(boolean show) {
		noLogin.setVisible(show);
		noDraft.setVisible(!show);
		noCurrentRoundPicked.setVisible(!show);
		complete.setVisible(!show);		
	}

	@Override
	public void showNoDraft(boolean show) {
		noLogin.setVisible(!show);
		noDraft.setVisible(show);
		noCurrentRoundPicked.setVisible(!show);
		complete.setVisible(!show);		
	}

	@Override
	public void showNoRoundSelection(boolean show) {
		noLogin.setVisible(!show);
		noDraft.setVisible(!show);
		noCurrentRoundPicked.setVisible(show);
		complete.setVisible(!show);
	}

	@Override
	public void showComplete(boolean show) {
		noLogin.setVisible(!show);
		noDraft.setVisible(!show);
		noCurrentRoundPicked.setVisible(!show);
		complete.setVisible(show);	}

	@Override
	public void setNoDraftContent(String content) {
		HTML html = new HTML(content);
		noDraft.clear();
		noDraft.add(html);
		
	}

	@Override
	public void setNoRoundContent(String content) {
		HTML html = new HTML(content);
		noCurrentRoundPicked.clear();
		noCurrentRoundPicked.add(html);
		
	}

	@Override
	public void setCompleteContent(String content) {
		HTML html = new HTML(content);
		complete.clear();
		complete.add(html);
		
	}

	@Override
	public void setNoLoginContent(String content) {
		HTML html = new HTML(content);
		noLogin.clear();
		noLogin.add(html);
		
	}

	@Override
	public void showReuse(boolean show) {
		reuseLastPick.setVisible(show);
	}


}
