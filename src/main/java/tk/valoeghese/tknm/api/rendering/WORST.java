/*
 * Although most of the project is under the LGPL v3
 * The files for WORST are licensed under MIT.
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

import net.minecraft.client.util.math.Vector3f;
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
		WORSTImpl.nextQuad();
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
	 * Adds the four vertices that make up a flat square on the ground, with the centre at the centre of the square.
	 */
	static void flatSquare() {
		WORSTImpl.vertex(-0.5f, 0, -0.5f);
		WORSTImpl.vertex(-0.5f, 0, 0.5f);
		WORSTImpl.vertex(0.5f, 0, 0.5f);
		WORSTImpl.vertex(0.5f, 0, -0.5f);
	}

	/**
	 * Renders all the quads in the currently bound mesh.
	 * @param translate the vector translating this mesh.
	 * @param rotation if null, no rotation is performed. Otherwise, provides the rotation of the mesh.
	 * @param scale if null, the mesh is not scaled. Otherwise, provides the scale of the mesh.
	 */
	static void renderMesh(Vector3f translate, @Nullable Quaternion rotation, @Nullable Vector3f scale) {
		WORSTImpl.renderMesh(translate, rotation, scale == null ? WORSTImpl.ONE : scale);
	}
}
