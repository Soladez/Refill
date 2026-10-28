package de.soladez.refill;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Einstiegspunkt der Mod: registriert die Umschalt-Taste (Standard: ALT GR)
 * und ruft jeden Client-Tick die Nachfüll-Logik auf.
 */
public final class RefillClient implements ClientModInitializer {

	public static final String MOD_ID = "refill";
	public static final Logger LOGGER = LoggerFactory.getLogger("Refill");

	private static final KeyMapping.Category CATEGORY =
			KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));

	private static KeyMapping toggleKey;

	@Override
	public void onInitializeClient() {
		RefillConfig.load();

		// ALT GR ist auf deutschen Tastaturen die rechte Alt-Taste.
		toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.refill.toggle",
				InputConstants.KEY_RALT,
				CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(RefillClient::onTick);

		LOGGER.info("Refill geladen (aktiv: {}).", RefillConfig.isEnabled());
	}

	private static void onTick(Minecraft client) {
		while (toggleKey.consumeClick()) {
			boolean enabled = !RefillConfig.isEnabled();
			RefillConfig.setEnabled(enabled);
			if (client.player != null) {
				client.player.sendOverlayMessage(Component.translatable(
						enabled ? "refill.message.enabled" : "refill.message.disabled"));
			}
		}

		RefillHandler.tick(client);
	}
}
