package net.rugby.foundation.admin.server.model;

import net.rugby.foundation.model.shared.IPlayerMatchStats;

interface IPlayerStatShares {

	public abstract String toString();

	public abstract float getPlayerScore();

	public abstract IPlayerMatchStats getPlayerMatchStats();

	public abstract Integer getRating(float totalScores);

	public abstract void scaleForStandings(Float matchStandingFactor);

	public abstract void scaleForCompWeight(Float compWeightFactor);

}