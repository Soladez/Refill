package de.soladez.refill;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Speichert, ob das automatische Nachfüllen aktiv ist, in {@code config/refill.properties}.
 */
public final class RefillConfig {

	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("refill.properties");

	private static boolean enabled = true;

	private RefillConfig() {
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean value) {
		enabled = value;
		save();
	}

	static void load() {
		if (!Files.exists(FILE)) {
			save();
			return;
		}
		Properties props = new Properties();
		try (Reader reader = Files.newBufferedReader(FILE)) {
			props.load(reader);
			enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
		} catch (IOException e) {
			RefillClient.LOGGER.warn("Konnte {} nicht lesen", FILE, e);
		}
	}

	private static void save() {
		Properties props = new Properties();
		props.setProperty("enabled", Boolean.toString(enabled));
		try {
			Files.createDirectories(FILE.getParent());
			try (Writer writer = Files.newBufferedWriter(FILE)) {
				props.store(writer, "Refill – automatisches Nachfuellen (Umschalten mit ALT GR)");
			}
		} catch (IOException e) {
			RefillClient.LOGGER.warn("Konnte {} nicht schreiben", FILE, e);
		}
	}
}
