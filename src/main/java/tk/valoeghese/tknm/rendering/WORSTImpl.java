package tk.valoeghese.tknm.rendering;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.impl.renderer.RendererAccessImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

// World Oriented Render System Translator
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
		// offset position from camera and set scale
		Vec3d pos = camera.getPos();
		currentStack.translate(-pos.x, -pos.y, -pos.z);
		currentStack.scale(1, 1, 1);
	}

	public static void vertex(int index, float x, float y, float z) {
		emitter.pos(index++, x, y, z);
	}

	public static void startMesh() {
		meshBuilder = renderer.meshBuilder();
		emitter = meshBuilder.getEmitter();
	}

	public static void end() {
		if (!started) {
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
