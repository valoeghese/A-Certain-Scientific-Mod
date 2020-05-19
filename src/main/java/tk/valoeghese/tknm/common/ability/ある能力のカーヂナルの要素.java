package tk.valoeghese.tknm.common.ability;

import java.util.Random;

import javax.annotation.Nullable;

import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import tk.valoeghese.tknm.api.ACertainComponent;
import tk.valoeghese.tknm.api.ability.Ability;
import tk.valoeghese.tknm.api.ability.AbilityRegistry;
import tk.valoeghese.tknm.api.ability.AbilityUserData;
import tk.valoeghese.tknm.common.ToaruKagakuNoMod;
import tk.valoeghese.tknm.util.FloatRandom;
import tk.valoeghese.tknm.util.RandomUtils;

// https://www.desmos.com/calculator/amkiawswds
// Quite a bit of this code is originally from my Misaka Bot repo
// aru nouryoku no kaajinaru no youso
public final class ある能力のカーヂナルの要素 implements ACertainComponent {
	public ある能力のカーヂナルの要素(PlayerEntity player) {
		this.じりき = 0.2f + ABILITY_RANDOM.nextFloat();
		this.能力者 = player;
		World world = player.getEntityWorld();
		this.setAbility(world.isClient ? null : AbilityRegistry.pickAbility(new Random(player.getUuid().getLeastSignificantBits() + 31L * ((ServerWorld) world).getSeed())));
		this.レブルわりたす();
	}

	// ============ ABILITY ============ //

	public float 能力けいけんち = 0.0f; // ability user xp for calculations
	private int レブル = 0; // from 0 to 5
	private float プログレス = 0;
	private float じりき;
	private boolean 能力者です = false;
	private Ability<?> 能力;
	private final PlayerEntity 能力者;
	private AbilityUserData データ;
	private CompoundTag dataTagCache = null;

	/**
	 * Calculates the player's level and level progress.
	 */
	private void レブルわりたす() {
		float アウトプット = (float) ((3 * this.じりき * Math.log10(this.能力けいけんち + 1.0f)) + (2 * this.じりき));
		this.レブル = (int) Math.floor(アウトプット);

		if (this.レブル > 5) {
			this.プログレス = 1.0f;
			this.レブル = 5;
		} else {
			float レブルのアウトプット = (float) Math.pow(10, ((this.レブル ) - (2 * this.じりき)) / 3 * this.じりき);
			float つぎのレブルのアウトプット = (float) Math.pow(10, ((this.レブル + 1) - (2 * this.じりき)) / 3 * this.じりき);
			this.プログレス = progressOf(レブルのアウトプット, アウトプット, つぎのレブルのアウトプット);
		}
	}

	@Override
	public float getLevelProgress() {
		return this.プログレス;
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
		this.sync();
		return this.能力けいけんち;
	}

	@Override
	@Nullable
	public Ability<?> getAbility() {
		if (this.能力者です) {
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
		this.能力者です = abilityUser;
		this.sync();
	}

	@Override
	public void fromTag(CompoundTag タッグ) {
		if (タッグ.contains("nouryoku", 10)) {
			CompoundTag 能力タッグ = タッグ.getCompound("nouryoku");
			this.能力 = AbilityRegistry.getAbility(new Identifier(能力タッグ.getString("shurui")));
			this.能力けいけんち = 能力タッグ.getFloat("keikenchi");
			this.じりき = 能力タッグ.getFloat("jiriki");
			this.能力者です = 能力タッグ.getBoolean("nouryokusha");
			this.dataTagCache = 能力タッグ.getCompound("data");
			this.データ = this.能力.createUserData(this.能力者);
			this.データ.fromTag(this.dataTagCache);
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
			能力タッグ.putBoolean("nouryokusha", this.能力者です);

			this.dataTagCache = this.データ.toTag();

			if (this.dataTagCache != null) {
				能力タッグ.put("data", this.dataTagCache);
			}
		}
		タッグ.put("nouryoku", 能力タッグ);
		return タッグ;
	}

	@Override
	public ComponentType<?> getComponentType() {
		return ToaruKagakuNoMod.A_CERTAIN_COMPONENT;
	}

	@Override
	public PlayerEntity getEntity() {
		return this.能力者;
	}

	@Override
	public void setAbility(Ability<?> ability) {
		this.能力 = ability;

		if (ability != null) {
			this.データ = ability.createUserData(this.能力者);
			this.データ.fromTag(this.dataTagCache);
		} else {
			this.データ = null;
		}
	}

	@Override
	public AbilityUserData getData() {
		return this.データ;
	}

	private static float progressOf(float prev, float current, float next) {
		return (current - prev) / (next - prev);
	}

	@Override
	public boolean isAbilityUser() {
		return this.能力者です;
	}

	private static final FloatRandom ABILITY_RANDOM = RandomUtils.naturalDistribution(0.5f, 0.5f);
}
