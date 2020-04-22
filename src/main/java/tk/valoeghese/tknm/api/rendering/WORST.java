package tk.valoeghese.tknm.api.rendering;

import javax.annotation.Nullable;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;
import tk.valoeghese.tknm.rendering.WORSTImpl;

/**
 * The World Oriented Render System Translator. "Translates" (read: provides a wrapper over) abstracted functions to functions on fabric/minecraft's rendering system.
 * @author Valoeghese
 */
public interface WORST {
	/**
	 * Creates a new mesh object and bind it to WORST.
	 */
	static void mesh() {
		WORSTImpl.mesh();
	}

	static void nextQuad() {
		WORSTImpl.nextQuad();
	}

	/**
	 * 
	 * @param x the x coordinate of the vertex.
	 * @param y the y coordinate of the vertex.
	 * @param z the z coordinate of the vertex.
	 */
	static void vertex(float x, float y, float z) {
		WORSTImpl.vertex(x, y, z);
	}

	static void renderMesh(Vector3f translate, @Nullable Quaternion rotation) {
		WORSTImpl.renderMesh(translate, rotation);
	}
}
