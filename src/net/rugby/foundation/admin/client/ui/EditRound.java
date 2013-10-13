/**
 * 
 */
package net.rugby.foundation.admin.client.ui;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class EditRound extends Composite {

	private static EditRoundUiBinder uiBinder = GWT
			.create(EditRoundUiBinder.class);

	interface EditRoundUiBinder extends UiBinder<Widget, EditRound> {
	}
	
	@UiTemplate("EditRound.ui.xml")
	
	public interface RoundPresenter {
		void saveRound(IRound R, List<IStanding> standings);
	} 
	
	public EditRound() {
		initWidget(uiBinder.createAndBindUi(this));
		
		standingTable.addColumn(new Column<IStanding,String>(new TextCell()){
			@Override
            public String getValue(IStanding s)
            {
                return s.getTeam().getDisplayName() == null ? "" : s.getTeam().getDisplayName();
            }
		}, "Team");
		
		Column<IStanding,String> myEditableColumn = new Column<IStanding,String>(new TextInputCell()){
			@Override
            public String getValue(IStanding s)
            {
                return s.getStanding() == null ? "" : s.getStanding().toString();
            }
		};
		
		standingTable.addColumn(myEditableColumn, "Standing");
		
		myEditableColumn.setFieldUpdater(new FieldUpdater<IStanding, String>() {
		    @Override
		    public void update(int index, IStanding s, String value) {
		        s.setStanding(Integer.parseInt(value));
		    }
		});
	}

	@UiField
	Button save;
	@UiField
	TextBox name;
	@UiField
	TextBox abbr;
	@UiField 
	CellTable<IStanding> standingTable;
	
	IRound round = null;
	private RoundPresenter listener;
	private List<IStanding> standings;
	
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		round.setName(name.getText());
		round.setAbbr(abbr.getText());

		List<IStanding> ss = new ArrayList<IStanding>();
		for (IStanding s : standingTable.getVisibleItems()) {
			ss.add(s);
		}
		listener.saveRound(round, ss);
	}
	
	
	public void ShowRound(IRound round, List<IStanding> standings) {
		this.round = round;
		this.standings = standings;
		name.setText(round.getName());
		abbr.setText(round.getAbbr());
		standingTable.setRowData(0, standings);
	}

	public void SetPresenter(RoundPresenter p) {
		listener = p;
	}

}
