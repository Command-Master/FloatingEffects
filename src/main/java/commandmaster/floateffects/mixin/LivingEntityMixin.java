package commandmaster.floateffects.mixin;

import commandmaster.floateffects.FloatStatusEffectInstance;
import commandmaster.floateffects.FloatUtils;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract AttributeContainer getAttributes();

    @Shadow
    private boolean effectsChanged;


    @Shadow public @Nullable abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Shadow protected abstract float getJumpVelocity();

    @Inject(at = @At("HEAD"), method = "onStatusEffectApplied", cancellable = true)
    private void onApplied(StatusEffectInstance effect, CallbackInfo ci) {
        if (effect instanceof FloatStatusEffectInstance) {
            this.effectsChanged = true;
            if (!world.isClient) {
                FloatUtils.onApplied(effect.getEffectType() ,(LivingEntity) (Object) (this), this.getAttributes(), ((FloatStatusEffectInstance) effect).amplifier);
            }
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "computeFallDamage", cancellable = true)
    private void computeFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
        StatusEffectInstance statusEffectInstance = getStatusEffect(StatusEffects.JUMP_BOOST);
        if (statusEffectInstance instanceof FloatStatusEffectInstance) {
            float f = (float) (((FloatStatusEffectInstance) statusEffectInstance).amplifier + 1);
            cir.setReturnValue(MathHelper.ceil((fallDistance - 3.0F - f) * damageMultiplier));
        }
    }

    @Inject(at = @At("HEAD"), method = "jump", cancellable = true)
    private void jump(CallbackInfo ci) {
        System.out.println("Jump!");
//
        StatusEffectInstance statusEffectInstance = getStatusEffect(StatusEffects.JUMP_BOOST);
        System.out.println(statusEffectInstance);
        if (statusEffectInstance instanceof FloatStatusEffectInstance) {
            ci.cancel();
            FloatStatusEffectInstance floatInstance = (FloatStatusEffectInstance) statusEffectInstance;
            float f = getJumpVelocity();
            System.out.println(f);
            f += 0.1F * (float)(floatInstance.amplifier + 1);
            System.out.println(f);
            Vec3d vec3d = getVelocity();
            setVelocity(vec3d.x, f, vec3d.z);
            if (isSprinting()) {
                float g = this.yaw * 0.017453292F;
                setVelocity(getVelocity().add(-MathHelper.sin(g) * 0.2F, 0.0D, MathHelper.cos(g) * 0.2F));
            }

            velocityDirty = true;
        }
    }
}
