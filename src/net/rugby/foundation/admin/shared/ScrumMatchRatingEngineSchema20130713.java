package net.rugby.foundation.admin.shared;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class ScrumMatchRatingEngineSchema20130713 extends ScrumMatchRatingEngineSchema implements
		Serializable, IV1EngineWeightValues {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6239778787762561486L;
	/**
	 * 
	 */

	
	protected  Float triesWeight; // .2F;
	protected  Float tryAssistsWeight; // .2F;
	protected  Float pointsWeight; // .3F;
	protected  Float kicksWeight; // .05F;
	protected  Float passesWeight; // .005F;
	protected  Float runsWeight; // .1F;
	protected  Float metersRunWeight; // .2F;
	protected  Float cleanBreaksWeight; // .2F;
	public void setTriesWeight(Float triesWeight) {
		this.triesWeight = triesWeight;
	}

	public void setTryAssistsWeight(Float tryAssistsWeight) {
		this.tryAssistsWeight = tryAssistsWeight;
	}

	public void setPointsWeight(Float pointsWeight) {
		this.pointsWeight = pointsWeight;
	}

	public void setKicksWeight(Float kicksWeight) {
		this.kicksWeight = kicksWeight;
	}

	public void setPassesWeight(Float passesWeight) {
		this.passesWeight = passesWeight;
	}

	public void setRunsWeight(Float runsWeight) {
		this.runsWeight = runsWeight;
	}

	public void setMetersRunWeight(Float metersRunWeight) {
		this.metersRunWeight = metersRunWeight;
	}

	public void setCleanBreaksWeight(Float cleanBreaksWeight) {
		this.cleanBreaksWeight = cleanBreaksWeight;
	}

	public void setDefendersBeatenWeight(Float defendersBeatenWeight) {
		this.defendersBeatenWeight = defendersBeatenWeight;
	}

	public void setOffloadsWeight(Float offloadsWeight) {
		this.offloadsWeight = offloadsWeight;
	}

	public void setTurnoversWeight(Float turnoversWeight) {
		this.turnoversWeight = turnoversWeight;
	}

	public void setTacklesMadeWeight(Float tacklesMadeWeight) {
		this.tacklesMadeWeight = tacklesMadeWeight;
	}

	public void setTacklesMissedWeight(Float tacklesMissedWeight) {
		this.tacklesMissedWeight = tacklesMissedWeight;
	}

	public void setLineoutsWonOnThrowWeight(Float lineoutsWonOnThrowWeight) {
		this.lineoutsWonOnThrowWeight = lineoutsWonOnThrowWeight;
	}

	public void setLineoutsStolenOnOppThrowWeight(
			Float lineoutsStolenOnOppThrowWeight) {
		this.lineoutsStolenOnOppThrowWeight = lineoutsStolenOnOppThrowWeight;
	}

	public void setPenaltiesConcededWeight(Float penaltiesConcededWeight) {
		this.penaltiesConcededWeight = penaltiesConcededWeight;
	}

	public void setYellowCardsWeight(Float yellowCardsWeight) {
		this.yellowCardsWeight = yellowCardsWeight;
	}

	public void setRedCardsWeight(Float redCardsWeight) {
		this.redCardsWeight = redCardsWeight;
	}

	public void setScrumShareWeight(Float scrumShareWeight) {
		this.scrumShareWeight = scrumShareWeight;
	}

	public void setLineoutShareWeight(Float lineoutShareWeight) {
		this.lineoutShareWeight = lineoutShareWeight;
	}

	public void setRuckShareWeight(Float ruckShareWeight) {
		this.ruckShareWeight = ruckShareWeight;
	}

	public void setMaulShareWeight(Float maulShareWeight) {
		this.maulShareWeight = maulShareWeight;
	}

	public void setMinutesShareWeight(Float minutesShareWeight) {
		this.minutesShareWeight = minutesShareWeight;
	}

	public void setPointsDifferentialWeight(Float pointsDifferentialWeight) {
		this.pointsDifferentialWeight = pointsDifferentialWeight;
	}

	protected  Float defendersBeatenWeight; // .2F;
	protected  Float offloadsWeight; // .2F;
	protected  Float turnoversWeight; // -.2F;
	protected  Float tacklesMadeWeight; // .7F;
	protected  Float tacklesMissedWeight; // -.1F;
	protected  Float lineoutsWonOnThrowWeight; // .2F;
	protected  Float lineoutsStolenOnOppThrowWeight; // .3F;
	protected  Float penaltiesConcededWeight; // -.1F;
	protected  Float yellowCardsWeight; // -.2F;
	protected  Float redCardsWeight; // -.3F;

	// time-skewed team stats
	protected  Float scrumShareWeight; // .4F;
	protected  Float lineoutShareWeight; // .4F;
	protected  Float ruckShareWeight; // .3F;
	protected  Float maulShareWeight; // .3F;
	protected  Float minutesShareWeight; // .2F;
	
	protected  Float pointsDifferentialWeight; // .3F;
	
	public ScrumMatchRatingEngineSchema20130713() {
		
	}
	
	@Override
	public Long getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getTriesWeight()
	 */
	@Override
	public Float getTriesWeight() {
		return triesWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getTryAssistsWeight()
	 */
	@Override
	public Float getTryAssistsWeight() {
		return tryAssistsWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getPointsWeight()
	 */
	@Override
	public Float getPointsWeight() {
		return pointsWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getKicksWeight()
	 */
	@Override
	public Float getKicksWeight() {
		return kicksWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getPassesWeight()
	 */
	@Override
	public Float getPassesWeight() {
		return passesWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getRunsWeight()
	 */
	@Override
	public Float getRunsWeight() {
		return runsWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getMetersRunWeight()
	 */
	@Override
	public Float getMetersRunWeight() {
		return metersRunWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getCleanBreaksWeight()
	 */
	@Override
	public Float getCleanBreaksWeight() {
		return cleanBreaksWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getDefendersBeatenWeight()
	 */
	@Override
	public Float getDefendersBeatenWeight() {
		return defendersBeatenWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getOffloadsWeight()
	 */
	@Override
	public Float getOffloadsWeight() {
		return offloadsWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getTurnoversWeight()
	 */
	@Override
	public Float getTurnoversWeight() {
		return turnoversWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getTacklesMadeWeight()
	 */
	@Override
	public Float getTacklesMadeWeight() {
		return tacklesMadeWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getTacklesMissedWeight()
	 */
	@Override
	public Float getTacklesMissedWeight() {
		return tacklesMissedWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getLineoutsWonOnThrowWeight()
	 */
	@Override
	public Float getLineoutsWonOnThrowWeight() {
		return lineoutsWonOnThrowWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getLineoutsStolenOnOppThrowWeight()
	 */
	@Override
	public Float getLineoutsStolenOnOppThrowWeight() {
		return lineoutsStolenOnOppThrowWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getPenaltiesConcededWeight()
	 */
	@Override
	public Float getPenaltiesConcededWeight() {
		return penaltiesConcededWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getYellowCardsWeight()
	 */
	@Override
	public Float getYellowCardsWeight() {
		return yellowCardsWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getRedCardsWeight()
	 */
	@Override
	public Float getRedCardsWeight() {
		return redCardsWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getScrumShareWeight()
	 */
	@Override
	public Float getScrumShareWeight() {
		return scrumShareWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getLineoutShareWeight()
	 */
	@Override
	public Float getLineoutShareWeight() {
		return lineoutShareWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getRuckShareWeight()
	 */
	@Override
	public Float getRuckShareWeight() {
		return ruckShareWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getMaulShareWeight()
	 */
	@Override
	public Float getMaulShareWeight() {
		return maulShareWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getMinutesShareWeight()
	 */
	@Override
	public Float getMinutesShareWeight() {
		return minutesShareWeight;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IV1EngineWeightValues#getPointsDifferentialWeight()
	 */
	@Override
	public Float getPointsDifferentialWeight() {
		return pointsDifferentialWeight;
	}



}
