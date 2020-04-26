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

package tk.valoeghese.tknm.api.rendering;

import javax.annotation.Nullable;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import tk.valoeghese.tknm.rendering.WORSTImpl;

/**
 * The World Oriented Render System Translator. "Translates" (read: provides a wrapper over) abstracted functions to functions on fabric/minecraft's rendering system.
 * @author Valoeghese
 */
public interface WORST {
	/**
	 * Creates a new mesh object and bind it to WORST. Additionally, starts a quad.
	 */
	static void mesh() {
		WORSTImpl.mesh();
	}

	/**
	 * Flushes the content of the quad buffer into the quad emitter, and prepares for the next quad.
	 */
	static void nextQuad() {
		WORSTImpl.nextQuadDouble();
	}

	/**
	 * Adds the vertex to the current quad. Can only add 4 vertices to a quad before you {@link WORST#renderMesh have to render the mesh} or call {@link WORST#nextQuad}
	 * @param x the x coordinate of the vertex.
	 * @param y the y coordinate of the vertex.
	 * @param z the z coordinate of the vertex.
	 */
	static void vertex(float x, float y, float z) {
		WORSTImpl.vertex(x, y, z);
	}

	/**
	 * Adds the four vertices that make up a flat square on the ground, with the centre at the centre of the square. You do not need to call nextQuad() to flush the quad buffer to add on to this.
	 */
	static void flatSquare() {
		flatSquare(0, 0, 0);
	}

	/**
	 * Adds the four vertices that make up a flat square on the ground, with the centre offset from the centre of the square by the specified offset parameters. You do not need to call nextQuad() to flush the quad buffer to add on to this.
	 * @param xo the x offset of the plane.
	 * @param yo the y offset of the plane.
	 * @param zo the z offset of the plane.
	 */
	static void flatSquare(float xo, float yo, float zo) {
		vertex(-0.5f + xo, 0 + yo, -0.5f + zo);
		vertex(-0.5f + xo, 0 + yo, 0.5f + zo);
		vertex(0.5f + xo, 0 + yo, 0.5f + zo);
		vertex(0.5f + xo, 0 + yo, -0.5f + zo);
		nextQuad();
	}

	/**
	 * Binds the specified block texture to WORST. Use before calling methods such as nextQuad.
	 * @param identifier the texture identifier.
	 */
	static void bindBlockTexture(Identifier identifier) {
		bindSprite(WORSTImpl.getSprite(SpriteAtlasTexture.BLOCK_ATLAS_TEX, identifier));
	}

	/**
	 * Binds the specified sprite to WORST. 
	 */
	static void bindSprite(Sprite sprite) {
		WORSTImpl.bindSprite(sprite);
	}

	/**
	 * Adds the quads for a basic cube. You do not need to call nextQuad() to flush the quad buffer to add on to this.
	 */
	static void basicCube() {
		basicCube(null, 0, 0, 0);
	}

	/**
	 * Adds the quads for a basic cube. You do not need to call nextQuad() to flush the quad buffer to add on to this.
	 * @param sprites If not null, the sprite textures to be bound to each quad in this order:<ul>
	 * <li>bottom
	 * <li>top
	 * <li>north
	 * <li>south
	 * <li>west
	 * <li>east
	 * @param xo the x offset of the cube vertices.
	 * @param yo the y offset of the cube vertices.
	 * @param zo the z offset of the cube vertices.
	 */
	static void basicCube(@Nullable Sprite[] sprites, float xo, float yo, float zo) {
		boolean rs = sprites != null; // render sprites
		// bottom
		vertex(0.5f + xo, -0.5f + yo, 0.5f + zo);
		vertex(-0.5f + xo, -0.5f + yo, 0.5f + zo);
		vertex(-0.5f + xo, -0.5f + yo, -0.5f + zo);
		vertex(0.5f + xo, -0.5f + yo, -0.5f + zo);

		if (rs) {
			bindSprite(sprites[0]);
		}

		WORSTImpl.nextQuadSingle();
		// top
		vertex(-0.5f + xo, 0.5f + yo, -0.5f + zo);
		vertex(-0.5f + xo, 0.5f + yo, 0.5f + zo);
		vertex(0.5f + xo, 0.5f + yo, 0.5f + zo);
		vertex(0.5f + xo, 0.5f + yo, -0.5f + zo);

		if (rs) {
			bindSprite(sprites[1]);
		}

		WORSTImpl.nextQuadSingle();
		// north
		vertex(-0.5f + xo, 0.5f + yo, -0.5f + zo);
		vertex(0.5f + xo, 0.5f + yo, -0.5f + zo);
		vertex(0.5f + xo, -0.5f + yo, -0.5f + zo);
		vertex(-0.5f + xo, -0.5f + yo, -0.5f + zo);

		if (rs) {
			bindSprite(sprites[2]);
		}

		WORSTImpl.nextQuadSingle();
		// south
		vertex(0.5f + xo, 0.5f + yo, 0.5f + zo);
		vertex(-0.5f + xo, 0.5f + yo, 0.5f + zo);
		vertex(-0.5f + xo, -0.5f + yo, 0.5f + zo);
		vertex(0.5f + xo, -0.5f + yo, 0.5f + zo);

		if (rs) {
			bindSprite(sprites[3]);
		}

		WORSTImpl.nextQuadSingle();
		// west
		vertex(-0.5f + xo, 0.5f + yo, 0.5f + zo);
		vertex(-0.5f + xo, 0.5f + yo, -0.5f + zo);
		vertex(-0.5f + xo, -0.5f + yo, -0.5f + zo);
		vertex(-0.5f + xo, -0.5f + yo, 0.5f + zo);

		if (rs) {
			bindSprite(sprites[4]);
		}

		WORSTImpl.nextQuadSingle();
		// east
		vertex(0.5f + xo, 0.5f + yo, -0.5f + zo);
		vertex(0.5f + xo, 0.5f + yo, 0.5f + zo);
		vertex(0.5f + xo, -0.5f + yo, 0.5f + zo);
		vertex(0.5f + xo, -0.5f + yo, -0.5f + zo);

		if (rs) {
			bindSprite(sprites[5]);
		}

		WORSTImpl.nextQuadSingle();
	}

	/**
	 * @return a matrix stack for performing matrix calculations.
	 */
	static MatrixStack getMatrixStack() {
		return WORSTImpl.getCurrentStack();
	}

	/**
	 * Renders all the quads in the currently bound mesh without flushing the quad buffer.
	 * @param translate the vector translating this mesh.
	 * @param rotation if null, no rotation is performed. Otherwise, provides the rotation of the mesh.
	 * @param scale if null, the mesh is not scaled. Otherwise, provides the scale of the mesh.
	 */
	static void renderMesh(Vector3f translate, @Nullable Quaternion rotation, @Nullable Vector3f scale) {
		WORSTImpl.renderMesh(translate, rotation, scale == null ? WORSTImpl.ONE : scale);
	}

	/**
	 * Renders all the quads in the currently bound mesh.
	 * @param translate the vector translating this mesh.
	 * @param rotation if null, no rotation is performed. Otherwise, provides the rotation of the mesh.
	 * @param scale if null, the mesh is not scaled. Otherwise, provides the scale of the mesh.
	 */
	static void flushAndRenderMesh(Vector3f translate, @Nullable Quaternion rotation, @Nullable Vector3f scale) {
		WORSTImpl.nextQuadDouble();
		WORSTImpl.renderMesh(translate, rotation, scale == null ? WORSTImpl.ONE : scale);
	}
}
