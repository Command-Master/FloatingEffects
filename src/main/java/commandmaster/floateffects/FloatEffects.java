package commandmaster.floateffects;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class FloatEffects implements ModInitializer {
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
			FloatEffectCommand.register(dispatcher);
		}));
		System.out.println("Hello Fabric world!");
	}
}