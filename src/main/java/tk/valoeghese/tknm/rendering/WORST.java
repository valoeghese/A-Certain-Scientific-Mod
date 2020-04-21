package tk.valoeghese.tknm.rendering;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Matrix4f;

// World Oriented Render System Translator
public final class WORST {
	private WORST() {
		// NO-OP
	}

	private static final Tessellator TESSELLATOR = Tessellator.getInstance();
	private static BufferBuilder vc = TESSELLATOR.getBuffer();
	private static int mode = -1;
	private static int QUAD = 7;

	private static void mode(int modeIn) {
		mode = modeIn;
		vc.begin(mode, VertexFormats.POSITION);
	}

	public static void quads() {
		mode(QUAD);
	}

	public static void finish() {
		mode = -1;
	}

	public static void quad(Matrix4f transform, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
		vc.vertex(transform, x0, y0, z0);
		vc.vertex(transform, x1, y1, z1);
		vc.vertex(transform, x2, y2, z2);
		vc.vertex(transform, x3, y3, z3);
		TESSELLATOR.draw();
	}
}
