package commandmaster.floateffects;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;

public class FloatEffects implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
			FloatEffectCommand.register(dispatcher);
//			dispatcher.register(literal("effectt").then(argument("targets", EntityArgumentType.entities()).then()));
		}));
		System.out.println("Hello Fabric world!");
	}
}

/*
.executes(context -> {
				System.out.println("foo");
				return 1;
			})
 */