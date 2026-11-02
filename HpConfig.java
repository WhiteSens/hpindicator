package com.example.hpindicator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HpConfig {
	public static final float MIN_SCALE = 0.5f;
	public static final float MAX_SCALE = 4.0f;

	/** Позиція як частка екрана (0..1), щоб не залежати від розміру вікна. */
	public float x = 0.02f;
	public float y = 0.85f;
	public float scale = 1.0f;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static HpConfig instance;

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("hpindicator.json");
	}

	public static HpConfig get() {
		if (instance == null) {
			load();
		}
		return instance;
	}

	public static void load() {
		try {
			Path p = path();
			if (Files.exists(p)) {
				instance = GSON.fromJson(Files.readString(p), HpConfig.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (instance == null) {
			instance = new HpConfig();
		}
		instance.clamp();
	}

	public static void save() {
		try {
			get().clamp();
			Files.writeString(path(), GSON.toJson(instance));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		HpConfig d = new HpConfig();
		x = d.x;
		y = d.y;
		scale = d.scale;
	}

	private void clamp() {
		x = Math.max(0f, Math.min(1f, x));
		y = Math.max(0f, Math.min(1f, y));
		scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
	}
}
