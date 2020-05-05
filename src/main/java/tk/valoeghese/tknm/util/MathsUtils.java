package tk.valoeghese.tknm.util;

public class MathsUtils {
	public static double progress(double start, double current, double end) {
		return (start - current) / (start - end);
	}

	public static double progress(long start, long current, long end) {
		return (double) (start - current) / (double) (start - end);
	}

	public static float progress(float start, float current, float end) {
		return (start - current) / (start - end);
	}
}
