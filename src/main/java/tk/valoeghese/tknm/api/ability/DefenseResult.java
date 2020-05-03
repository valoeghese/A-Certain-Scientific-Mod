package tk.valoeghese.tknm.api.ability;

/**
 * Enum for the result of an ability user's passive (or active) defense.
 */
public enum DefenseResult {
	HIT(true, false),
	HIT_AND_STOP(true, true),
	PASSES_BY(false, false),
	STOP(false, true),
	/**
	 * Like {@link DefenseResult#STOP}, but does not allow the extra ability effects to run.
	 */
	ABSOLUTE_STOP(false, true);

	private DefenseResult(boolean hit, boolean blocking) {
		this.hit = hit;
		this.blocking = blocking;
	}

	public final boolean hit;
	public final boolean blocking;
}
