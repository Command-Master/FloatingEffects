package commandmaster.floateffects.mixin;

import commandmaster.floateffects.FloatStatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public class SaveMixin {
    @Inject(at = @At("HEAD"), method = "fromTag(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/entity/effect/StatusEffectInstance;", cancellable = true)
    private static void fromTag(CompoundTag tag, CallbackInfoReturnable<StatusEffectInstance> cir) {
        if (tag.contains("Amplifier", 6)) {
            cir.setReturnValue(FloatStatusEffectInstance.fromTag(tag));
        }
    }
}
