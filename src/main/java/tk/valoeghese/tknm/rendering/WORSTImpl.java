package tk.valoeghese.tknm.rendering;

import java.util.List;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.impl.renderer.RendererAccessImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

/**
 * World Oriented Render System Translator Impl.
 * @author Valoeghese
 */
public final class WORSTImpl {
	private WORSTImpl() {
		// NO-OP
	}

	private static boolean started = false;
	private static MatrixStack currentStack;
	// might be able to make this constant, but not taking chances
	private static VertexConsumerProvider.Immediate immediate;
	private static VertexConsumer vc;
	private static Renderer renderer;
	private static MeshBuilder meshBuilder;
	private static QuadEmitter emitter;
	private static Camera camera;
	// whether we have pushed yet
	private static boolean dirty = false;
	// current index of vertex
	private static int index = 0;
	private static Vector3f[] quadStack = new Vector3f[4];

	public static void init(MatrixStack stack, Camera cameraIn) throws RuntimeException {
		// init notif
		if (started) {
			throw new RuntimeException("WORST already initialised! Call end() before re-initialising!");
		}
		started = true;
		// set variables
		currentStack = stack;
		immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		vc = immediate.getBuffer(RenderLayer.getSolid());
		renderer = RendererAccessImpl.INSTANCE.getRenderer();
		camera = cameraIn;
	}

	public static void mesh() {
		// flush if already dirty
		if (dirty) {
			// flush
			immediate.draw(RenderLayer.getSolid());
			// pop
			currentStack.pop();
		} else {
			dirty = true;
		}
		// push matrices
		currentStack.push();
		// start mesh
		meshBuilder = renderer.meshBuilder();
		emitter = meshBuilder.getEmitter();
		index = 0;
	}

	public static void nextQuad() {
		// normal
		emitter
		.pos(0, quadStack[0])
		.pos(1, quadStack[1])
		.pos(2, quadStack[2])
		.pos(3, quadStack[3]).emit()
		// reverse
		.pos(0, quadStack[0])
		.pos(1, quadStack[3])
		.pos(2, quadStack[2])
		.pos(3, quadStack[1]).emit();
	}

	public static void vertex(float x, float y, float z) {
		quadStack[index++] = new Vector3f(x, y, z);
	}

	public static void renderMesh(Vector3f translate, @Nullable Quaternion rotation) {
		nextQuad();
		// offset position from camera and translate
		Vec3d pos = camera.getPos();
		currentStack.translate(-pos.x + translate.getX(), -pos.y + translate.getY(), -pos.z + translate.getZ());
		currentStack.scale(1, 1, 1);

		if (rotation != null) {
			currentStack.multiply(rotation);
		}

		Mesh m = meshBuilder.build();
		List<BakedQuad>[] quadListArray = ModelHelper.toQuadLists(m);

		for (int i = 0; i < quadListArray.length; ++i) {
			for (BakedQuad bq : quadListArray[i]) {
				vc.quad(currentStack.peek(), bq, 0.5f, 0.5f, 0.5f, 15728880, OverlayTexture.DEFAULT_UV);
			}
		}
	}

	public static void end() {
		if (started) {
			started = false;
			// flush if dirty
			if (dirty) {
				// flush
				immediate.draw(RenderLayer.getSolid());
				// pop
				currentStack.pop();
				// not dirty anymore!
				dirty = false;
			}
			// reset stack and camera variables
			currentStack = null;
			camera = null;
		}
	}
}
