package com.example.hpindicator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class HpIndicatorClient implements ClientModInitializer {
	private static KeyBinding openKey;

	@Override
	public void onInitializeClient() {
		HpConfig.load();

		// Окрема категорія в Налаштування -> Керування -> Призначення клавіш
		KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("hpindicator", "main"));
		openKey = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("key.hpindicator.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openKey.wasPressed()) {
				if (client.player != null && client.currentScreen == null) {
					client.setScreen(new PositionScreen());
				}
			}
		});

		HudElementRegistry.addLast(Identifier.of("hpindicator", "hp_hud"), (ctx, tickCounter) -> {
			MinecraftClient mc = MinecraftClient.getInstance();
			if (mc.player == null || mc.options.hudHidden || mc.currentScreen instanceof PositionScreen) {
				return;
			}
			HpConfig cfg = HpConfig.get();
			int x = HpHud.posX(mc.textRenderer, ctx.getScaledWindowWidth(), cfg);
			int y = HpHud.posY(mc.textRenderer, ctx.getScaledWindowHeight(), cfg);
			HpHud.draw(ctx, mc.textRenderer, x, y, cfg.scale, HpHud.currentHpText());
		});
	}
}
