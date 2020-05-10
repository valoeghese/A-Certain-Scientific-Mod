package tk.valoeghese.tknm.client.abilityrenderer;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Quaternion;
import tk.valoeghese.tknm.api.rendering.WORST;

abstract class Beam {
	Beam(Vector3f pos, Vector3f rotationBase, float distance, long tickTarget, float thickness) {
		this.pos = pos;
		this.rotation = new Quaternion(rotationBase.getX(), rotationBase.getY(), rotationBase.getZ(), true);
		this.distance = distance;
		this.tickTarget = tickTarget;
		this.thickness = thickness;
	}

	Vector3f pos;
	Quaternion rotation;
	final float distance;
	final long tickTarget;
	float thickness;

	abstract void bindTexture();

	boolean render(ClientWorld world) {
		WORST.mesh();
		this.bindTexture();
		WORST.basicCube(null, 0.5f, 0, 0);
		// TODO spin so it looks fancy and cool instead of just a cuboid
		WORST.renderMesh(this.pos, this.rotation, new Vector3f(this.distance, this.thickness, this.thickness));
		return world.getTime() >= this.tickTarget;
	}
}
