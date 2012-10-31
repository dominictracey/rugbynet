/**
 * 
 */
package net.rugby.foundation.model.shared;

/**
 * @author home
 *
 */
public interface ISimpleScoreMatchResult {

	public abstract int getHomeScore();

	public abstract void setHomeScore(int homeScore);

	public abstract int getVisitScore();

	public abstract void setVisitScore(int visitScore);

}