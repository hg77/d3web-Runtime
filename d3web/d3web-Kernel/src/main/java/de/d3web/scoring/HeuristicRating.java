package de.d3web.scoring;

import de.d3web.core.terminology.DiagnosisState;

public class HeuristicRating extends DiagnosisState {

	private final double score;

	/**
	 * Creates a neutral (unclear) heuristic rating.
	 * 
	 * @param score
	 *            the score of this rating
	 */
	public HeuristicRating() {
		super(State.UNCLEAR);
		this.score = 0.0;
	}

	/**
	 * Creates a heuristic rating representing the specified score.
	 * 
	 * @param score
	 *            the score of this rating
	 */
	public HeuristicRating(Score score) {
		this(score.getScore());
	}

	/**
	 * Creates a heuristic rating representing the specified score.
	 * 
	 * @param score
	 *            the score of this rating
	 */
	public HeuristicRating(double score) {
		super(scoreToState(score));
		this.score = score;
	}

	private static State scoreToState(double score) {
		if (score >= 42) return State.ESTABLISHED;
		if (score >= 10) return State.SUGGESTED;
		if (score >= -41) return State.UNCLEAR;
		return State.EXCLUDED;
	}

	/**
	 * Returns the score of this heuristic rating.
	 * 
	 * @return the rating score
	 */
	public double getScore() {
		return score;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() + 47 * (int) score;
	}

	@Override
	public int compareTo(DiagnosisState other) {
		if (other instanceof HeuristicRating) {
			// if both ratings are heuristic ones, compare by score
			return (int) Math.signum(this.score - ((HeuristicRating) other).score);
		}
		else {
			// otherwise use common compare of ratings
			return super.compareTo(other);
		}
	}

	/**
	 * Sums up a number of HeuristicRatings, creating a new one that represents
	 * the combined score.
	 * 
	 * @param ratings
	 *            the ratings to be summed
	 */
	public static HeuristicRating add(HeuristicRating... ratings) {
		double score = 0.0;
		for (HeuristicRating rating : ratings) {
			score += rating.getScore();
		}
		return new HeuristicRating(score);
	}
}