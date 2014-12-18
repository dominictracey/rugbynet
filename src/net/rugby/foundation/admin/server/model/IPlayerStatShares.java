package net.rugby.foundation.admin.server.model;

import java.util.Map;

import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.PlayerRating.RatingComponent;

interface IPlayerStatShares {

	public abstract String toString();

	public abstract float getUnscaledScore();
	public abstract float getScaledScore();
	
	
	public abstract IPlayerMatchStats getPlayerMatchStats();

	public abstract IMatchGroup getMatch();

	/**
	 * This is columns H-J in the simulator
	 * @param total
	 * @return
	 */
	float getUnscaledPercentage(float total);
	float getScaledPercentage(float total);

	/**
	 * Adjust the raw score for different factors
	 * @param scalingFactor
	 * @param factorName
	 * @return the scaled score for this factor
	 */
	float scale(Float matchStandingFactor, String factorName);

	/**
	 * This is columns K-M in the simulator
	 * @param total
	 * @return
	 */
	Integer getUnscaledRating(float totalScores, float numStats);
	Integer getScaledRating(float totalScores, float numStats);

	/**
	 * return the scaling factor for the requested aspect.
	 * @param type - something like "Comp", "MatchAge", etc.
	 * @return the factor if one has been set, null otherwise
	 */
	Float getScalingFactor(String type);

	public abstract void setMatchLabel(String matchLabel);
	RatingComponent getRatingComponent(Map<String, Float> scaleTotalNumMap,	Map<String, Float> scaleTotalMap);

	String getSummaryRow(Map<String, Float> scaleTotalNumMap, Map<String, Float> scaleTotalMap);

	String getEmailSummaryRow(Map<String, Float> scaleTotalNumMap, Map<String, Float> scaleTotalMap);


}