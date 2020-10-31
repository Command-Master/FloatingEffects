package commandmaster.floateffects.mixin;

import commandmaster.floateffects.FloatPacket;
import commandmaster.floateffects.FloatStatusEffectInstance;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientHandlerMixin {
    @Shadow private ClientWorld world;

    @Shadow private MinecraftClient client;

    @Inject(method = "onEntityPotionEffect", at = @At("HEAD"), cancellable = true)
    public void onEntityPotionEffect(EntityStatusEffectS2CPacket packet, CallbackInfo ci) {
        if (((FloatPacket)packet).getIsFloat()) {
            ci.cancel();
            NetworkThreadUtils.forceMainThread(packet, (ClientPlayNetworkHandler)(Object)this, this.client);
            Entity entity = this.world.getEntityById(packet.getEntityId());
            if (entity instanceof LivingEntity) {
                StatusEffect statusEffect = StatusEffect.byRawId(packet.getEffectId());
                if (statusEffect != null) {
                    StatusEffectInstance statusEffectInstance = new FloatStatusEffectInstance(statusEffect, packet.getDuration(), ((FloatPacket) packet).getValue(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon());
                    statusEffectInstance.setPermanent(packet.isPermanent());
                    ((LivingEntity)entity).applyStatusEffect(statusEffectInstance);
                }
            }
        }
    }
}
