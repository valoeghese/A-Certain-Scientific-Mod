/*
 * Although most of the project is under the LGPL v3, the files for WORST are licensed under MIT.
 * 
 * Copyright 2020 Valoeghese
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * - The above copyright notice and this permission notice shall be
 *   included in all copies or substantial portions of the Software.
 * 
 * - THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *   INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *   CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *   FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tk.valoeghese.tknm.util;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.impl.renderer.RendererAccessImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
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
	private static Supplier<Vec3d> offsetPos;
	// whether we have pushed yet
	private static boolean dirty = false;
	// current index of vertex
	private static int index = 0;
	private static Vector3f[] quadBuffer = new Vector3f[4];
	private static boolean dqb = false; // whether the quad buffer is "dirty"
	public static final Vector3f ONE = new Vector3f(1.0f, 1.0f, 1.0f);
	// current sprite
	private static Sprite boundSprite;
	private static RenderLayer nextLayer;
	private static RenderLayer meshLayer;

	public static void init(MatrixStack stack, Supplier<Vec3d> offsetPositionSupplier) throws RuntimeException {
		// init notif
		if (started) {
			throw new RuntimeException("WORST already initialised! Call end() before re-initialising!");
		}
		started = true;
		// set variables
		currentStack = stack;
		immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		renderer = RendererAccessImpl.INSTANCE.getRenderer();
		offsetPos = offsetPositionSupplier;
		meshLayer = RenderLayer.getCutout();
		nextLayer = RenderLayer.getCutout();
	}

	public static void bindRenderLayer(RenderLayer layer) {
		nextLayer = layer;
	}

	public static void mesh() {
		// flush if already dirty
		if (dirty) {
			// flush
			immediate.draw(meshLayer);
			// pop
			currentStack.pop();
		} else {
			dirty = true;
		}

		// set layer
		meshLayer = nextLayer;
		// push matrices
		currentStack.push();
		// start mesh
		vc = immediate.getBuffer(meshLayer);
		meshBuilder = renderer.meshBuilder();
		emitter = meshBuilder.getEmitter();
		index = 0;
	}

	public static MatrixStack getCurrentStack() {
		return currentStack;
	}

	public static void bindSprite(Sprite sprite) {
		boundSprite = sprite;
	}

	public static void nextQuadDouble(int sizex, int sizez) {
		if (dqb) {
			// normal
			emitter
			.pos(0, quadBuffer[0]).sprite(0, 0, sizex, 0)
			.pos(1, quadBuffer[1]).sprite(1, 0, 0, 0)
			.pos(2, quadBuffer[2]).sprite(2, 0, 0, sizez)
			.pos(3, quadBuffer[3]).sprite(3, 0, sizex, sizez);

			if (boundSprite != null) {
				emitter.spriteBake(0, boundSprite, 0);
			}

			emitter.emit()
			// reverse
			.pos(0, quadBuffer[0]).sprite(0, 0, 0, 0)
			.pos(1, quadBuffer[3]).sprite(1, 0, 0, sizez)
			.pos(2, quadBuffer[2]).sprite(2, 0, sizex, sizez)
			.pos(3, quadBuffer[1]).sprite(3, 0, sizex, 0);

			if (boundSprite != null) {
				emitter.spriteBake(0, boundSprite, 0);
			}

			emitter.emit();
			dqb = false;
			index = 0;
		}
	}

	public static void nextQuadSingle(int sizex, int sizez) {
		if (dqb) {
			emitter
			.pos(0, quadBuffer[0]).sprite(0, 0, sizex, 0)
			.pos(1, quadBuffer[1]).sprite(1, 0, 0, 0)
			.pos(2, quadBuffer[2]).sprite(2, 0, 0, sizez)
			.pos(3, quadBuffer[3]).sprite(3, 0, sizex, sizez);

			if (boundSprite != null) {
				emitter.spriteBake(0, boundSprite, 0);
			}

			emitter.emit();
			dqb = false;
			index = 0;
		}
	}

	public static Sprite getSprite(Identifier atlasTexture, Identifier id) {
		return MinecraftClient.getInstance().getSpriteAtlas(atlasTexture).apply(id);
	}

	public static void vertex(float x, float y, float z) {
		dqb = true;
		quadBuffer[index++] = new Vector3f(x, y, z);
	}

	public static Vec3d getOffsetPos() {
		return offsetPos.get();
	}

	public static void renderMesh() {
		Mesh m = meshBuilder.build();
		List<BakedQuad>[] quadListArray = ModelHelper.toQuadLists(m);

		for (int i = 0; i < quadListArray.length; ++i) {
			for (BakedQuad bq : quadListArray[i]) {
				vc.quad(currentStack.peek(), bq, 1.0f, 1.0f, 1.0f, 0xF000F0, OverlayTexture.DEFAULT_UV);
			}
		}
	}

	public static void end() {
		if (started) {
			started = false;
			// flush if dirty
			if (dirty) {
				// flush
				immediate.draw(meshLayer);
				// pop
				currentStack.pop();
				// not dirty anymore!
				dirty = false;
			}
			// reset stack and offset pos fields
			currentStack = null;
			offsetPos = null;
			// and sprite
			boundSprite = null;
		}
	}
}
