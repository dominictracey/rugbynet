package net.rugby.foundation.model.shared;

import java.util.ArrayList;

import net.rugby.foundation.model.shared.CoreConfiguration.selectionType;
import net.rugby.foundation.model.shared.Position.position;

public class ManagementEngineFactory<T,S> {

	
	public static ManagementEngine<PlayerRowData,DraftWizardState> getManagementEngine(CoreConfiguration.selectionType type, int maxPerTeam, int draftSize, int currentRound, Long maxPoints, ArrayList<Group> teams) {
		ManagementEngine<PlayerRowData, DraftWizardState> managementEngine = null;

		managementEngine = new PoolDraftManagementEngine();

		ArrayList<Integer> tc = new ArrayList<Integer>();
		for (int i=0; i < CoreConfiguration.getNumberteams(); ++i) { tc.add(0); }
		ArrayList<Integer> tp = new ArrayList<Integer>();
		for (int i=0; i < CoreConfiguration.getNumberpositions(); ++i) { tp.add(0); }
		DraftWizardState state = new DraftWizardState(maxPoints, tc, tp, 0, new String("Welcome"), position.NONE);

		
		if (type == selectionType.POOLROSTER) {		
			
			managementEngine.addPositionConstraint(position.PROP, new CheckNumberAtPosition(position.PROP,CoreConfiguration.getNumPropsPoolRoster()));
			managementEngine.addPositionConstraint(position.HOOKER, new CheckNumberAtPosition(position.HOOKER,CoreConfiguration.getNumHookersPoolRoster()));
			managementEngine.addPositionConstraint(position.LOCK, new CheckNumberAtPosition(position.LOCK,CoreConfiguration.getNumLocksPoolRoster()));
			managementEngine.addPositionConstraint(position.FLANKER, new CheckNumberAtPosition(position.FLANKER,CoreConfiguration.getNumFlankersPoolRoster()));
			managementEngine.addPositionConstraint(position.NUMBER8, new CheckNumberAtPosition(position.NUMBER8,CoreConfiguration.getNumNumber8sPoolRoster()));
			managementEngine.addPositionConstraint(position.SCRUMHALF, new CheckNumberAtPosition(position.SCRUMHALF,CoreConfiguration.getNumScrumhalvesPoolRoster()));
			managementEngine.addPositionConstraint(position.FLYHALF, new CheckNumberAtPosition(position.FLYHALF,CoreConfiguration.getNumFlyhalvesPoolRoster()));
			managementEngine.addPositionConstraint(position.CENTER, new CheckNumberAtPosition(position.CENTER,CoreConfiguration.getNumCentersPoolRoster()));
			managementEngine.addPositionConstraint(position.WING, new CheckNumberAtPosition(position.WING,CoreConfiguration.getNumWingsPoolRoster()));
			managementEngine.addPositionConstraint(position.FULLBACK, new CheckNumberAtPosition(position.FULLBACK,CoreConfiguration.getNumFullbacksPoolRoster()));

		} else if (type == CoreConfiguration.selectionType.POOLROUND) {
			
			managementEngine.addPositionConstraint(position.PROP, new CheckNumberAtPosition(position.PROP,CoreConfiguration.getNumPropsPoolRound()));
			managementEngine.addPositionConstraint(position.HOOKER, new CheckNumberAtPosition(position.HOOKER,CoreConfiguration.getNumHookersPoolRound()));
			managementEngine.addPositionConstraint(position.LOCK, new CheckNumberAtPosition(position.LOCK,CoreConfiguration.getNumLocksPoolRound()));
			managementEngine.addPositionConstraint(position.FLANKER, new CheckNumberAtPosition(position.FLANKER,CoreConfiguration.getNumFlankersPoolRound()));
			managementEngine.addPositionConstraint(position.NUMBER8, new CheckNumberAtPosition(position.NUMBER8,CoreConfiguration.getNumNumber8sPoolRound()));
			managementEngine.addPositionConstraint(position.SCRUMHALF, new CheckNumberAtPosition(position.SCRUMHALF,CoreConfiguration.getNumScrumhalvesPoolRound()));
			managementEngine.addPositionConstraint(position.FLYHALF, new CheckNumberAtPosition(position.FLYHALF,CoreConfiguration.getNumFlyhalvesPoolRound()));
			managementEngine.addPositionConstraint(position.CENTER, new CheckNumberAtPosition(position.CENTER,CoreConfiguration.getNumCentersPoolRound()));
			managementEngine.addPositionConstraint(position.WING, new CheckNumberAtPosition(position.WING,CoreConfiguration.getNumWingsPoolRound()));
			managementEngine.addPositionConstraint(position.FULLBACK, new CheckNumberAtPosition(position.FULLBACK,CoreConfiguration.getNumFullbacksPoolRound()));
			
		} else if (type == CoreConfiguration.selectionType.KNOCKOUTROSTER) {
			
			managementEngine.addPositionConstraint(position.PROP, new CheckNumberAtPosition(position.PROP,CoreConfiguration.getNumPropsKnockoutRoster()));
			managementEngine.addPositionConstraint(position.HOOKER, new CheckNumberAtPosition(position.HOOKER,CoreConfiguration.getNumHookersKnockoutRoster()));
			managementEngine.addPositionConstraint(position.LOCK, new CheckNumberAtPosition(position.LOCK,CoreConfiguration.getNumLocksKnockoutRoster()));
			managementEngine.addPositionConstraint(position.FLANKER, new CheckNumberAtPosition(position.FLANKER,CoreConfiguration.getNumFlankersKnockoutRoster()));
			managementEngine.addPositionConstraint(position.NUMBER8, new CheckNumberAtPosition(position.NUMBER8,CoreConfiguration.getNumNumber8sKnockoutRoster()));
			managementEngine.addPositionConstraint(position.SCRUMHALF, new CheckNumberAtPosition(position.SCRUMHALF,CoreConfiguration.getNumScrumhalvesKnockoutRoster()));
			managementEngine.addPositionConstraint(position.FLYHALF, new CheckNumberAtPosition(position.FLYHALF,CoreConfiguration.getNumFlyhalvesKnockoutRoster()));
			managementEngine.addPositionConstraint(position.CENTER, new CheckNumberAtPosition(position.CENTER,CoreConfiguration.getNumCentersKnockoutRoster()));
			managementEngine.addPositionConstraint(position.WING, new CheckNumberAtPosition(position.WING,CoreConfiguration.getNumWingsKnockoutRoster()));
			managementEngine.addPositionConstraint(position.FULLBACK, new CheckNumberAtPosition(position.FULLBACK,CoreConfiguration.getNumFullbacksKnockoutRoster()));
			
		} else if (type == CoreConfiguration.selectionType.KNOCKOUTROUND) {
			
			managementEngine.addPositionConstraint(position.PROP, new CheckNumberAtPosition(position.PROP,CoreConfiguration.getNumPropsKnockoutRound()));
			managementEngine.addPositionConstraint(position.HOOKER, new CheckNumberAtPosition(position.HOOKER,CoreConfiguration.getNumHookersKnockoutRound()));
			managementEngine.addPositionConstraint(position.LOCK, new CheckNumberAtPosition(position.LOCK,CoreConfiguration.getNumLocksKnockoutRound()));
			managementEngine.addPositionConstraint(position.FLANKER, new CheckNumberAtPosition(position.FLANKER,CoreConfiguration.getNumFlankersKnockoutRound()));
			managementEngine.addPositionConstraint(position.NUMBER8, new CheckNumberAtPosition(position.NUMBER8,CoreConfiguration.getNumNumber8sKnockoutRound()));
			managementEngine.addPositionConstraint(position.SCRUMHALF, new CheckNumberAtPosition(position.SCRUMHALF,CoreConfiguration.getNumScrumhalvesKnockoutRound()));
			managementEngine.addPositionConstraint(position.FLYHALF, new CheckNumberAtPosition(position.FLYHALF,CoreConfiguration.getNumFlyhalvesKnockoutRound()));
			managementEngine.addPositionConstraint(position.CENTER, new CheckNumberAtPosition(position.CENTER,CoreConfiguration.getNumCentersKnockoutRound()));
			managementEngine.addPositionConstraint(position.WING, new CheckNumberAtPosition(position.WING,CoreConfiguration.getNumWingsKnockoutRound()));
			managementEngine.addPositionConstraint(position.FULLBACK, new CheckNumberAtPosition(position.FULLBACK,CoreConfiguration.getNumFullbacksKnockoutRound()));
		}
		

		//@TODO only get the teams available for the comp.
		for (Group g : teams) {
			managementEngine.addTeamConstraint(g.getId(), new CheckNumberFromTeam(g.getId(), maxPerTeam));
		}
		
		managementEngine.setValueConstraint(new CheckValueOfSelection(maxPoints));
		managementEngine.initialize(new ArrayList<PlayerRowData>(), new ArrayList<PlayerRowData>(), state, draftSize);

		return managementEngine;
	}
}
