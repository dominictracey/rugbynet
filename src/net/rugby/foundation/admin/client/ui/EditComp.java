/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import net.rugby.foundation.model.shared.ICompetition;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class EditComp extends Composite {

	private static EditTeamUiBinder uiBinder = GWT
			.create(EditTeamUiBinder.class);

	interface EditTeamUiBinder extends UiBinder<Widget, EditComp> {
	}

	public interface Presenter {
		void saveCompInfo(ICompetition comp);
		void repairComp(ICompetition comp);
		void deleteComp(ICompetition comp);
		void setCompAsDefault(ICompetition comp);
		void addRound(ICompetition comp);
	} 
	
	public EditComp() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button save;
	@UiField
	Button repair;
	@UiField
	Button delete;
	@UiField
	TextBox longName;
	@UiField
	TextBox shortName;
	@UiField
	TextBox abbr;
	@UiField
	TextBox ttlDesc;
	@UiField
	TextBox twitter;
	@UiField
	TextBox ccid;
	@UiField
	CheckBox underway;
	@UiField
	CheckBox showInClient;
	@UiField
	TextBox weightingFactor;
	@UiField
	ListBox compType;
	@UiField
	Button setAsDefault;
	@UiField
	Button addRound;
	
	ICompetition comp = null;
	private Presenter listener;
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		comp.setLongName(longName.getText());
		comp.setShortName(shortName.getText());
		comp.setAbbr(abbr.getText());
		comp.setTTLTitleDesc(ttlDesc.getText());
		comp.setTwitter(twitter.getText());
		comp.setCompClubhouseId(Long.parseLong(ccid.getText()));
		comp.setUnderway(underway.getValue());
		comp.setWeightingFactor(Float.parseFloat(weightingFactor.getText()));
		comp.setShowToClient(showInClient.getValue());
//		if (!compType.isItemSelected(-1)) {
			comp.setCompType(ICompetition.CompetitionType.values()[compType.getSelectedIndex()]);
//		}
		listener.saveCompInfo(comp);
	}
	
	@UiHandler("repair")
	void onClickRepair(ClickEvent e) {
		comp.setLongName(longName.getText());
		comp.setShortName(shortName.getText());
		comp.setAbbr(abbr.getText());
		comp.setTTLTitleDesc(ttlDesc.getText());
		comp.setTwitter(twitter.getText());
		comp.setCompClubhouseId(Long.parseLong(ccid.getText()));
		comp.setUnderway(underway.getValue());
		comp.setShowToClient(showInClient.getValue());
		listener.repairComp(comp);
	}
	
	@UiHandler("delete")
	void onClickDelete(ClickEvent e) {
		listener.deleteComp(comp);
	}

	@UiHandler("addRound")
	void onClickAddRound(ClickEvent e) {
		listener.addRound(comp);
	}
	
	@UiHandler("setAsDefault")
	void onClickSetAsDefault(ClickEvent e) {
		listener.setCompAsDefault(comp);
	}
	
	public void ShowComp(ICompetition comp) {
		this.comp = comp;
		longName.setText(comp.getLongName());
		shortName.setText(comp.getShortName());
		ccid.setText(comp.getCompClubhouseId().toString());
		abbr.setText(comp.getAbbr());
		ttlDesc.setText(comp.getTTLTitleDesc());
		twitter.setText(comp.getTwitter());
		underway.setValue(comp.getUnderway());
		showInClient.setValue(comp.getShowToClient());
		if (comp.getWeightingFactor() != null) {
			weightingFactor.setValue(comp.getWeightingFactor().toString());
		} else {
			weightingFactor.setValue("1.0");
		}
		compType.clear();
		for (int i=0; i<ICompetition.CompetitionType.values().length; ++i) {
			compType.addItem(ICompetition.CompetitionType.values()[i].toString());
			if (comp.getCompType() == ICompetition.CompetitionType.values()[i]) {
				compType.setSelectedIndex(i);
			}
		}
	}

	public void SetPresenter(Presenter p) {
		listener = p;
	}

}
