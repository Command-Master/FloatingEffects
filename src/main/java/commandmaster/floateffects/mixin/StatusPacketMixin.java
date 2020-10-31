package commandmaster.floateffects.mixin;

import commandmaster.floateffects.FloatPacket;
import commandmaster.floateffects.FloatStatusEffectInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityStatusEffectS2CPacket.class)
public class StatusPacketMixin implements FloatPacket {
    @Shadow private int entityId;
    @Shadow private byte effectId;
    @Shadow private int duration;
    @Shadow private byte flags;
    @Shadow private byte amplifier;
    public boolean isFloat;
    public double value;
    @Inject(method="<init>(ILnet/minecraft/entity/effect/StatusEffectInstance;)V", at = @At("RETURN"))
    private void init(int entityId, StatusEffectInstance effect, CallbackInfo ci) {
        this.isFloat = false;
        if (effect instanceof FloatStatusEffectInstance) {
            this.isFloat = true;
            this.entityId = entityId;
            this.effectId = (byte)(StatusEffect.getRawId(effect.getEffectType()) & 255);
            this.value = ((FloatStatusEffectInstance) effect).amplifier;
            if (effect.getDuration() > 32767) {
                this.duration = 32767;
            } else {
                this.duration = effect.getDuration();
            }
            this.flags = 0;
            if (effect.isAmbient()) {
                this.flags = (byte)(this.flags | 1);
            }

            if (effect.shouldShowParticles()) {
                this.flags = (byte)(this.flags | 2);
            }

            if (effect.shouldShowIcon()) {
                this.flags = (byte)(this.flags | 4);
            }
        }
    }

    @Inject(method = "write", cancellable = true, at = @At("HEAD"))
    private void write(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeVarInt(this.entityId);
        buf.writeByte(this.effectId);
        buf.writeBoolean(this.isFloat);
        if (!this.isFloat) buf.writeByte(this.amplifier);
        else buf.writeDouble(this.value);
        buf.writeVarInt(this.duration);
        buf.writeByte(this.flags);
    }

    @Inject(method = "read", cancellable = true, at = @At("HEAD"))
    private void read(PacketByteBuf buf, CallbackInfo ci) {
        this.entityId = buf.readVarInt();
        this.effectId = buf.readByte();
        this.isFloat = buf.readBoolean();
        if (!this.isFloat) this.amplifier = buf.readByte();
        else this.value = buf.readDouble();
        this.duration = buf.readVarInt();
        this.flags = buf.readByte();
    }

    @Override
    public boolean getIsFloat() {
        return isFloat;
    }

    @Override
    public double getValue() {
        return value;
    }
}
