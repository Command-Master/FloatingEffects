package commandmaster.floateffects;

import commandmaster.floateffects.mixin.LivingEntityMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

public class FloatUtils {
    public static boolean canApplyUpdateEffect(StatusEffect effect, int duration, double amplifier) {
        double k;
        if (effect == StatusEffects.REGENERATION) {
            k = 50 / Math.pow(2, amplifier);
            if (k > 0.1) {
                return duration % k <= 1;
            } else {
                return true;
            }
        } else if (effect == StatusEffects.POISON) {
            k = 25 / Math.pow(2, amplifier);
            if (k > 0) {
                return duration % k <= 1;
            } else {
                return true;
            }
        } else if (effect == StatusEffects.WITHER) {
            k = 40 / Math.pow(2, amplifier);
            if (k > 0) {
                return duration % k <= 1;
            } else {
                return true;
            }
        } else if (effect instanceof InstantStatusEffect) {
            return duration >= 1;
        } else {
            return effect == StatusEffects.HUNGER;
        }
    }

    public static void applyUpdateEffect(StatusEffect effect, LivingEntity entity, double amplifier) {
        if (effect == StatusEffects.REGENERATION) {
            if (entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(1.0F);
            }
        } else if (effect == StatusEffects.POISON) {
            if (entity.getHealth() > 1.0F) {
                entity.damage(DamageSource.MAGIC, 1.0F);
            }
        } else if (effect == StatusEffects.WITHER) {
            entity.damage(DamageSource.WITHER, 1.0F);
        } else if (effect == StatusEffects.HUNGER && entity instanceof PlayerEntity) {
            ((PlayerEntity) entity).addExhaustion(0.005F * (float) (amplifier + 1));
        } else if (effect == StatusEffects.SATURATION && entity instanceof PlayerEntity) {
            if (!entity.world.isClient) {
                ((PlayerEntity) entity).getHungerManager().add((int) (amplifier + 1), 1.0F);
            }
        } else if ((effect != StatusEffects.INSTANT_HEALTH || entity.isUndead()) && (effect != StatusEffects.INSTANT_DAMAGE || !entity.isUndead())) {
            if (effect == StatusEffects.INSTANT_DAMAGE && !entity.isUndead() || effect == StatusEffects.INSTANT_HEALTH && entity.isUndead()) {
                entity.damage(DamageSource.MAGIC, (float) (6 / Math.pow(2, amplifier)));
            }
        } else {
            entity.heal((float) Math.max(4 / Math.pow(2, amplifier), 0));
        }
    }

    public static void onApplied(StatusEffect effect, LivingEntity entity, AttributeContainer attributes, double amplifier) {
        if (effect instanceof AbsorptionStatusEffect) {
            entity.setAbsorptionAmount(entity.getAbsorptionAmount() + (float)(4 * (amplifier + 1)));
        }
        try {
            Field modifiers = StatusEffect.class.getDeclaredField("attributeModifiers");
            modifiers.setAccessible(true);
            Iterator var4 = ((Map<EntityAttribute, EntityAttributeModifier>)modifiers.get(effect)).entrySet().iterator();

            while (var4.hasNext()) {
                Map.Entry<EntityAttribute, EntityAttributeModifier> entry = (Map.Entry) var4.next();
                EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
                if (entityAttributeInstance != null) {
                    EntityAttributeModifier entityAttributeModifier = entry.getValue();
                    entityAttributeInstance.removeModifier(entityAttributeModifier);
                    entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(entityAttributeModifier.getId(), effect.getTranslationKey() + " " + amplifier, entityAttributeModifier.getValue() * (amplifier + 1), entityAttributeModifier.getOperation()));
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

//    public static void onRemoved(StatusEffect effect, LivingEntity entity, AttributeContainer attributes, double amplifier) {
//
//        // annoinances: healthboost, absorption, damage, instant
//        if (effect instanceof HealthBoostStatusEffect) {
//            nrem(effect, entity, attributes, amplifier);
//            if (entity.getHealth() > entity.getMaxHealth()) {
//                entity.setHealth(entity.getMaxHealth());
//            }
//        } else if (effect instanceof AbsorptionStatusEffect) {
//            entity.setAbsorptionAmount(entity.getAbsorptionAmount() - (float) (4 * (amplifier + 1)));
//            nrem(effect, entity, attributes, amplifier);
//        } else {
//            nrem(effect, entity, attributes, amplifier);
//        }
//    }
//
//    public static void nrem(StatusEffect effect, LivingEntity entity, AttributeContainer attributes, double amplifier) {
//        Iterator var4 = effect.attributeModifiers.entrySet().iterator();
//
//        while (var4.hasNext()) {
//            Map.Entry<EntityAttribute, EntityAttributeModifier> entry = (Map.Entry) var4.next();
//            EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance((EntityAttribute) entry.getKey());
//            if (entityAttributeInstance != null) {
//                entityAttributeInstance.removeModifier((EntityAttributeModifier) entry.getValue());
//            }
//        }
//    }
}
