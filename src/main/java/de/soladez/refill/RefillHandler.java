package de.soladez.refill;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;

/**
 * Merkt sich jeden Tick, was in Haupt- und Nebenhand liegt. Ist ein Stapel seit dem
 * letzten Tick aufgebraucht worden (Blöcke gesetzt, Essen gegessen, Werkzeug zerbrochen …),
 * wird ein passender Stapel aus dem Inventar in den leeren Slot getauscht.
 *
 * <p>Getauscht wird mit einem normalen SWAP-Klick im Spielerinventar – genau das, was
 * passiert, wenn man im geöffneten Inventar eine Hotbar-Taste drückt. Es werden also nur
 * reguläre Klick-Pakete gesendet, die Mod funktioniert damit auch auf Servern.</p>
 */
final class RefillHandler {

	/** Erster Slot des Hauptinventars (oberhalb der Hotbar). */
	private static final int MAIN_START = 9;
	/** Größe von Hotbar + Hauptinventar. */
	private static final int MAIN_END = 36;
	/** Im Inventar-Menü liegen die Hotbar-Slots 0–8 auf den Menü-Slots 36–44. */
	private static final int HOTBAR_MENU_OFFSET = 36;
	/** SWAP-Button für die Nebenhand. */
	private static final int OFFHAND_BUTTON = 40;

	private static int lastSelected = -1;
	private static ItemStack lastMain = ItemStack.EMPTY;
	private static ItemStack lastOff = ItemStack.EMPTY;

	private RefillHandler() {
	}

	static void tick(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null || client.gameMode == null) {
			reset();
			return;
		}

		Inventory inventory = player.getInventory();
		int selected = inventory.getSelectedSlot();
		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();

		if (canRefill(client, player)) {
			// Haupthand: gleicher Slot ausgewählt, vorher Items, jetzt leer.
			// Nicht nachfüllen, wenn der Stapel nur in die Nebenhand gewandert ist (Taste F).
			if (selected == lastSelected && !lastMain.isEmpty() && main.isEmpty()
					&& !ItemStack.isSameItemSameComponents(off, lastMain)) {
				int source = findReplacement(inventory, lastMain, selected);
				if (source >= 0) {
					swap(client, player, source, selected);
				}
			}

			// Nebenhand: vorher Items, jetzt leer (und nicht in die Haupthand getauscht).
			if (!lastOff.isEmpty() && off.isEmpty()
					&& !ItemStack.isSameItemSameComponents(player.getMainHandItem(), lastOff)) {
				int source = findReplacement(inventory, lastOff, -1);
				if (source >= 0) {
					swap(client, player, source, OFFHAND_BUTTON);
				}
			}
		}

		lastSelected = selected;
		lastMain = player.getMainHandItem().copy();
		lastOff = player.getOffhandItem().copy();
	}

	private static boolean canRefill(Minecraft client, LocalPlayer player) {
		return RefillConfig.isEnabled()
				// Kein Bildschirm offen: sonst würden wir manuelles Umsortieren stören.
				&& client.gui.screen() == null
				&& !player.isSpectator()
				&& !player.isCreative()
				&& player.containerMenu == player.inventoryMenu
				// Wer den Stapel bewusst wegwirft, will ihn nicht sofort ersetzt bekommen.
				&& !client.options.keyDrop.isDown();
	}

	/**
	 * Sucht einen Ersatzstapel. Zuerst im Hauptinventar (damit die Hotbar-Ordnung erhalten
	 * bleibt), danach in den übrigen Hotbar-Slots. Bevorzugt wird ein exakt gleicher Stapel;
	 * bei Werkzeugen/Rüstung reicht derselbe Item-Typ (Haltbarkeit, Verzauberungen egal).
	 *
	 * @return Inventar-Index des Ersatzes oder -1
	 */
	private static int findReplacement(Inventory inventory, ItemStack wanted, int excludedHotbarSlot) {
		int exact = -1;
		int similar = -1;
		boolean allowSimilar = wanted.isDamageableItem();

		for (int pass = 0; pass < 2 && exact < 0; pass++) {
			int start = pass == 0 ? MAIN_START : 0;
			int end = pass == 0 ? MAIN_END : MAIN_START;
			for (int i = start; i < end; i++) {
				if (i == excludedHotbarSlot) {
					continue;
				}
				ItemStack candidate = inventory.getItem(i);
				if (candidate.isEmpty()) {
					continue;
				}
				if (ItemStack.isSameItemSameComponents(candidate, wanted)) {
					exact = i;
					break;
				}
				if (allowSimilar && similar < 0 && ItemStack.isSameItem(candidate, wanted)) {
					similar = i;
				}
			}
		}

		return exact >= 0 ? exact : similar;
	}

	/**
	 * Tauscht den Inventar-Slot {@code inventorySlot} mit dem Hotbar-Slot bzw. der Nebenhand
	 * ({@code button} 0–8 oder 40).
	 */
	private static void swap(Minecraft client, LocalPlayer player, int inventorySlot, int button) {
		int menuSlot = inventorySlot < MAIN_START ? inventorySlot + HOTBAR_MENU_OFFSET : inventorySlot;
		client.gameMode.handleContainerInput(
				player.inventoryMenu.containerId, menuSlot, button, ContainerInput.SWAP, player);
	}

	private static void reset() {
		lastSelected = -1;
		lastMain = ItemStack.EMPTY;
		lastOff = ItemStack.EMPTY;
	}
}
