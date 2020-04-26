package tk.valoeghese.tknm.common.ability;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRegistry;
import tk.valoeghese.tknm.util.FloatRandom;
import tk.valoeghese.tknm.util.RandomUtils;

// https://www.desmos.com/calculator/amkiawswds
// Quite a bit of this code is originally from my Misaka Bot repo
// aru nouryoku no kaajinaru no youso
public final class ある能力のカーヂナルの要素 implements ACertainComponent {
	public ある能力のカーヂナルの要素(PlayerEntity player) {
		this.じりき = 0.2f + ABILITY_RANDOM.nextFloat();
		this.能力 = AbilityRegistry.pickAbility(player.getRandom());
	}

	// ============ ABILITY ============ //

	public float 能力けいけんち = 0.0f; // ability user xp for calculations
	private int レブル = 0; // from 0 to 5
	private float じりき;
	private boolean 能力者 = false;
	private Ability 能力;

	private void レブルわりたす() {
		this.レブル = (int) Math.floor((3 * this.じりき * Math.log10(this.能力けいけんち + 1.0f)) + (2 * this.じりき));

		if (this.レブル > 5) {
			this.レブル = 5;
		}
	}

	@Override
	public Text stats() {
		CompoundTag タッグ = new CompoundTag();
		タッグ.putInt("reburu", this.レブル);
		return this.toTag(タッグ).toText();
	}

	@Override
	public float addXp(float xp) {
		this.能力けいけんち += xp;
		this.レブルわりたす();
		return this.能力けいけんち;
	}

	@Override
	@Nullable
	public Ability getAbility() {
		if (this.能力者) {
			return this.能力;
		} else {
			return null;
		}
	}

	@Override
	public int getLevel() {
		return this.レブル;
	}

	@Override
	public void setAbilityUser(boolean abilityUser) {
		this.能力者 = abilityUser;
	}

	@Override
	public void fromTag(CompoundTag タッグ) {
		if (タッグ.contains("nouryoku", 10)) {
			CompoundTag 能力タッグ = タッグ.getCompound("nouryoku");
			this.能力 = AbilityRegistry.getAbility(new Identifier(能力タッグ.getString("shurui")));
			this.能力けいけんち = 能力タッグ.getFloat("keikenchi");
			this.じりき = 能力タッグ.getFloat("jiriki");
			this.能力者 = 能力タッグ.getBoolean("nouryokusha");
		}

		this.レブルわりたす();
	}

	@Override
	public CompoundTag toTag(CompoundTag タッグ) {
		CompoundTag 能力タッグ = new CompoundTag();
		{
			能力タッグ.putString("shurui", AbilityRegistry.getRegistryId(this.能力).toString());
			能力タッグ.putFloat("keikenchi", this.能力けいけんち);
			能力タッグ.putFloat("jiriki", this.じりき);
			能力タッグ.putBoolean("nouryokusha", this.能力者);
		}
		タッグ.put("nouryoku", 能力タッグ);
		return タッグ;
	}

	private static final FloatRandom ABILITY_RANDOM = RandomUtils.naturalDistribution(0.5f, 0.5f);
}
