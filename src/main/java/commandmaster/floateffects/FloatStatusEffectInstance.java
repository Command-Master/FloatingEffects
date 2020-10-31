package commandmaster.floateffects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;

import java.lang.reflect.Field;

public class FloatStatusEffectInstance extends StatusEffectInstance {
    public double amplifier;

    public FloatStatusEffectInstance(StatusEffect type, int duration, double amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        super(type, duration, (int) amplifier, ambient, showParticles, showIcon);
        this.amplifier = amplifier;
    }

    public boolean update(LivingEntity entity, Runnable overwriteCallback) {
        if (this.getDuration() > 0) {
            if (this.getEffectType() == StatusEffects.BAD_OMEN) {
                return super.update(entity, overwriteCallback);
            }
            if (FloatUtils.canApplyUpdateEffect(this.getEffectType(), this.getDuration(), this.amplifier)) {
                FloatUtils.applyUpdateEffect(this.getEffectType(), entity, this.amplifier);
            }

            try {
                Field dur = StatusEffectInstance.class.getDeclaredField("duration");
                dur.setAccessible(true);
                dur.set(this, this.getDuration() - 1);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        return this.getDuration() > 0;
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putByte("Id", (byte) StatusEffect.getRawId(this.getEffectType()));
        this.typelessToTag(tag);
        return tag;
    }

    private void typelessToTag(CompoundTag tag) {
        tag.putDouble("Amplifier", this.amplifier);
        tag.putInt("Duration", this.getDuration());
        tag.putBoolean("Ambient", this.isAmbient());
        tag.putBoolean("ShowParticles", this.shouldShowParticles());
        tag.putBoolean("ShowIcon", this.shouldShowIcon());

    }

    public static StatusEffectInstance fromTag(CompoundTag tag) {
        int i = tag.getByte("Id");
        StatusEffect statusEffect = StatusEffect.byRawId(i);
        return statusEffect == null ? null : fromTag(statusEffect, tag);
    }

    private static StatusEffectInstance fromTag(StatusEffect type, CompoundTag tag) {
        double i = tag.getDouble("Amplifier");
        int j = tag.getInt("Duration");
        boolean bl = tag.getBoolean("Ambient");
        boolean bl2 = true;
        if (tag.contains("ShowParticles", 1)) {
            bl2 = tag.getBoolean("ShowParticles");
        }

        boolean bl3 = bl2;
        if (tag.contains("ShowIcon", 1)) {
            bl3 = tag.getBoolean("ShowIcon");
        }

        return new FloatStatusEffectInstance(type, j, i < 0 ? 0 : i, bl, bl2, bl3);
    }
}
