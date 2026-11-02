package com.example.hpindicator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class HpHud {
	private static final Identifier HEART = Identifier.of("hpindicator", "textures/gui/heart.png");

	private static final int HEART_SIZE = 18; // розмір сердечка в "одиницях" (текстура 18x18)
	private static final int TEXT_SCALE = 2;  // число малюється вдвічі більшим
	private static final int GAP = 4;

	/** Ширина індикатора в одиницях (до множення на scale). Фіксована, щоб не "стрибала". */
	public static int unitWidth(TextRenderer tr) {
		return tr.getWidth("00") * TEXT_SCALE + GAP + HEART_SIZE;
	}

	public static int unitHeight() {
		return HEART_SIZE;
	}

	public static int pixelWidth(TextRenderer tr, float scale) {
		return Math.round(unitWidth(tr) * scale);
	}

	public static int pixelHeight(float scale) {
		return Math.round(unitHeight() * scale);
	}

	/** Верхній лівий кут індикатора на екрані за збереженою позицією. */
	public static int posX(TextRenderer tr, int screenW, HpConfig cfg) {
		return Math.round(cfg.x * Math.max(0, screenW - pixelWidth(tr, cfg.scale)));
	}

	public static int posY(TextRenderer tr, int screenH, HpConfig cfg) {
		return Math.round(cfg.y * Math.max(0, screenH - pixelHeight(cfg.scale)));
	}

	public static String currentHpText() {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player == null) {
			return "20";
		}
		return Integer.toString((int) Math.ceil(mc.player.getHealth()));
	}

	public static void draw(DrawContext ctx, TextRenderer tr, int x, int y, float scale, String text) {
		int textW = tr.getWidth(text) * TEXT_SCALE;
		int maxTextW = tr.getWidth("00") * TEXT_SCALE;

		ctx.getMatrices().pushMatrix();
		ctx.getMatrices().translate((float) x, (float) y);
		ctx.getMatrices().scale(scale, scale);

		// число (вирівняне по правому краю, ближче до сердечка)
		ctx.getMatrices().pushMatrix();
		ctx.getMatrices().translate((float) (maxTextW - textW), 1f);
		ctx.getMatrices().scale((float) TEXT_SCALE, (float) TEXT_SCALE);
		ctx.drawText(tr, text, 0, 0, 0xFFFFFFFF, true);
		ctx.getMatrices().popMatrix();

		// сердечко
		ctx.drawTexture(RenderPipelines.GUI_TEXTURED, HEART,
				maxTextW + GAP, 0,
				0f, 0f,
				HEART_SIZE, HEART_SIZE,
				HEART_SIZE, HEART_SIZE,
				HEART_SIZE, HEART_SIZE);

		ctx.getMatrices().popMatrix();
	}
}
