package tk.valoeghese.tknm.client.abilityrenderer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.world.ClientWorld;

class BeamRenderManager<T extends Beam> {
	private final List<T> beams = new ArrayList<>();

	void add(T beam) {
		this.beams.add(beam);
	}

	void renderUpdate(ClientWorld world) {
		if (!this.beams.isEmpty()) {
			int i = this.beams.size();

			while (--i >= 0) {
				if (this.beams.get(i).render(world)) {
					this.beams.remove(i);
				}
			}
		}
	}
}
