package tk.valoeghese.tknm.util;

import java.util.Random;

// yes this is also from my misakabot repo
public final class RandomUtils {
	public static FloatRandom naturalDistribution(float amplitude, float offset) {
		final float scale = -(amplitude / MAX_NATURAL);
		return () -> scale * (float) Math.log((1f / BOUNDED_FLOAT.nextFloat()) - 1f) + offset;
	}

	private static float stdND(float in) {
		return -(float) Math.log((1f / in) - 1f);
	}

	public static final Random RAND = new Random();
	public static final FloatRandom BOUNDED_FLOAT;
	private static final float MAX_NATURAL;

	static {
		BOUNDED_FLOAT = () -> {
			float random = RAND.nextFloat();
			if (random < 0.01f) {
				random = 0.01f;
			} else if (random > 0.99f) {
				random = 0.99f;
			}
			return random;
		};

		MAX_NATURAL = stdND(0.99f);
	}
}
