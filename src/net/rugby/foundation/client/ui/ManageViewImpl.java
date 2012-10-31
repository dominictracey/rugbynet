package net.rugby.foundation.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.place.Manage.step;
import net.rugby.foundation.model.shared.DraftWizardState;
import net.rugby.foundation.model.shared.Stage.stageType;

public class ManageViewImpl<T> extends Composite implements ManageView<T>
{
	@UiField Label arg;
	@UiField Label aus;
	@UiField Label eng;
	@UiField Label fra;
	@UiField Label ire;
	@UiField Label nzl;
	@UiField Label rsa;
	@UiField Label wal;
	@UiField HTML instructions;
	@UiField Button previous;
	@UiField Button next;	
	@UiField Button done;
	@UiField Label remainingLabel;
	@UiField HTML remaining;
	@UiField Anchor prop;
	@UiField Anchor hooker;
	@UiField Anchor lock;
	@UiField Anchor flanker;
	@UiField Anchor numbereight;
	@UiField Anchor scrumhalf;
	@UiField Anchor flyhalf;
	@UiField Anchor center;
	@UiField Anchor wing;
	@UiField Anchor fullback;
	@UiField Label numProp;
	@UiField Label numHooker;
	@UiField Label numLock;
	@UiField Label numFlanker;
	@UiField Label numNumber8;
	@UiField Label numScrumhalf;
	@UiField Label numFlyhalf;
	@UiField Label numCenter;
	@UiField Label numWing;
	@UiField Label numFullback;
	
	private static ManageViewImplUiBinder uiBinder = GWT.create(ManageViewImplUiBinder.class);
	
//	@UiTemplate("Home.ui.xml")

	interface ManageViewImplUiBinder extends UiBinder<Widget, ManageViewImpl<?>>
	{
	}


    
	private Presenter<T> presenter;
	
	public ManageViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		done.setVisible(false);
	}

	ClientFactory clientFactory;

	@Override
	public void setData(T info) {
		if (info instanceof DraftWizardState) {

			arg.setText(Integer.toString(((DraftWizardState) info).getTeamChosen(0)));
			aus.setText(Integer.toString(((DraftWizardState) info).getTeamChosen(1)));
			eng.setText(Integer.toString(((DraftWizardState) info).getTeamChosen(3)));
			fra.setText(Integer.toString(((DraftWizardState) info).getTeamChosen(5)));
			ire.setText(Integer.toString(((DraftWizardState) info).getTeamChosen(7)));
			nzl.setText(Integer.toString(((DraftWizardState) info).getTeamChosen(11)));
			rsa.setText(Integer.toString(((DraftWizardState) info).getTeamChosen(16)));
			wal.setText(Integer.toString(((DraftWizardState) info).getTeamChosen(19)));		
			
			instructions.setText(((DraftWizardState) info).getInstructions());
			remaining.setText(Long.toString(((DraftWizardState) info).getRemainingPoints()));
			stageType st = presenter.getStageType();
			int round = presenter.getRound();
			
			numProp.setText(((DraftWizardState) info).getPositionChosen(0) + "/" + step.PROP.getCount(st,round));
			numHooker.setText(((DraftWizardState) info).getPositionChosen(1) + "/" + step.HOOKER.getCount(st,round));
			numLock.setText(((DraftWizardState) info).getPositionChosen(2) + "/" + step.LOCK.getCount(st,round));
			numFlanker.setText(((DraftWizardState) info).getPositionChosen(3) + "/" + step.FLANKER.getCount(st,round));
			numNumber8.setText(((DraftWizardState) info).getPositionChosen(4) + "/" + step.NUMBER8.getCount(st,round));
			numScrumhalf.setText(((DraftWizardState) info).getPositionChosen(5) + "/" + step.SCRUMHALF.getCount(st,round));
			numFlyhalf.setText(((DraftWizardState) info).getPositionChosen(6) + "/" + step.FLYHALF.getCount(st,round));
			numCenter.setText(((DraftWizardState) info).getPositionChosen(7) + "/" + step.CENTER.getCount(st,round));
			numWing.setText(((DraftWizardState) info).getPositionChosen(8) + "/" + step.WING.getCount(st,round));
			numFullback.setText(((DraftWizardState) info).getPositionChosen(9) + "/" + step.FULLBACK.getCount(st,round));
			
			if (((DraftWizardState)info).isCompleted()) {
				showDoneButton(true);
			} else {
				showDoneButton(false);					
			}
			


			
			if (presenter.getStep() == step.DONE)	{
				showNextButton(false);
			} else {
				showNextButton(true);
			}
			
			if (presenter.getStep() == step.START || presenter.getStep() == step.PROP)	{
				showPrevButton(false);
			} else {
				showPrevButton(true);
			}
			
			if (presenter.getStep() == step.ROUND || presenter.getStep() == step.RANDOM) {
				showPrevButton(false);
				showNextButton(false);				
			}	
			
			if (presenter.getStep() == step.FULLBACK) {
				if (!((DraftWizardState)info).isCompleted())  {
					showNextButton(false);
					showPrevButton(true);
				} else {
					showNextButton(true);
					showPrevButton(true);
				}
			}
		}
		
	}
	
	@UiHandler("prop")
	void onPropButtonClicked(ClickEvent event) {		
		presenter.onPropClicked();
	}
	
	@UiHandler("hooker")
	void onHookerButtonClicked(ClickEvent event) {		
		presenter.onHookerClicked();
	}	
	
	@UiHandler("lock")
	void onLockButtonClicked(ClickEvent event) {		
		presenter.onLockClicked();
	}
	
	@UiHandler("flanker")
	void onflankerButtonClicked(ClickEvent event) {		
		presenter.onFlankerClicked();
	}
	
	@UiHandler("numbereight")
	void onnumber8ButtonClicked(ClickEvent event) {		
		presenter.onNumber8Clicked();
	}
	
	@UiHandler("scrumhalf")
	void onscrumhalfButtonClicked(ClickEvent event) {		
		presenter.onScrumhalfClicked();
	}
	
	@UiHandler("flyhalf")
	void onflyhalfButtonClicked(ClickEvent event) {		
		presenter.onFlyhalfClicked();
	}
	
	@UiHandler("center")
	void oncenterButtonClicked(ClickEvent event) {		
		presenter.onCenterClicked();
	}
	
	@UiHandler("wing")
	void onwingButtonClicked(ClickEvent event) {		
		presenter.onWingClicked();
	}
	
	@UiHandler("fullback")
	void onfullbackButtonClicked(ClickEvent event) {		
		presenter.onFullbackClicked();
	}

	@UiHandler("previous")
	void onpreviousButtonClicked(ClickEvent event) {		
		presenter.onPreviousButtonClicked();
	}
	
	@UiHandler("next")
	void onnextButtonClicked(ClickEvent event) {		
		presenter.onNextButtonClicked();
	}
	
	@UiHandler("done")
	void ondoneButtonClicked(ClickEvent event) {		
		presenter.onDoneButtonClicked();
	}
		
	
	@Override
	public void setPresenter(Presenter<T> listener) {
		presenter = listener;
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		
	}


	@Override
	public void showDoneButton(boolean show) {
		done.setVisible(show);
		
	}

	@Override
	public void showPrevButton(boolean show) {
		previous.setVisible(show);
	}

	@Override
	public void showNextButton(boolean show) {
		next.setVisible(show);
		
	}

	@Override
	public void showPoints(boolean show) {
		remaining.setVisible(show);
		remainingLabel.setVisible(show);
		
	}

	@Override
	public void showWait() {
		previous.setVisible(false);
		next.setVisible(false);
		done.setVisible(false);
		showPoints(false);
		
		instructions.setText("Please wait while we pick a roster for you.");
		
	}

}
