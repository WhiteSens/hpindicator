package com.example.hpindicator;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class PositionScreen extends Screen {
	private final HpConfig cfg = HpConfig.get();

	private boolean dragging = false;
	private int grabOffX, grabOffY;
	private ScaleSlider slider;

	public PositionScreen() {
		super(Text.literal("Налаштування індикатора ХП"));
	}

	@Override
	protected void init() {
		int w = 200;
		int cx = this.width / 2 - w / 2;
		int bottom = this.height - 30;

		slider = new ScaleSlider(cx, bottom - 50, w, 20);
		this.addDrawableChild(slider);

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Скинути"), b -> {
			cfg.reset();
			this.clearAndInit();
		}).dimensions(cx, bottom - 25, 98, 20).build());

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Готово"), b -> this.close())
				.dimensions(cx + 102, bottom - 25, 98, 20).build());
	}

	private int boxX() { return HpHud.posX(this.textRenderer, this.width, cfg); }
	private int boxY() { return HpHud.posY(this.textRenderer, this.height, cfg); }
	private int boxW() { return HpHud.pixelWidth(this.textRenderer, cfg.scale); }
	private int boxH() { return HpHud.pixelHeight(cfg.scale); }

	private boolean insideBox(double mx, double my) {
		return mx >= boxX() && mx <= boxX() + boxW() && my >= boxY() && my <= boxY() + boxH();
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);

		String hint = "Перетягніть сердечко мишкою";
		ctx.drawText(this.textRenderer, hint,
				(this.width - this.textRenderer.getWidth(hint)) / 2, 12, 0xFFFFFFFF, true);

		int x = boxX(), y = boxY(), w = boxW(), h = boxH();
		int color = dragging ? 0xFFFFFF55 : 0xFFAAAAAA;
		ctx.fill(x - 2, y - 2, x + w + 2, y - 1, color);
		ctx.fill(x - 2, y + h + 1, x + w + 2, y + h + 2, color);
		ctx.fill(x - 2, y - 2, x - 1, y + h + 2, color);
		ctx.fill(x + w + 1, y - 2, x + w + 2, y + h + 2, color);

		HpHud.draw(ctx, this.textRenderer, x, y, cfg.scale, HpHud.currentHpText());
	}

	@Override
	public boolean mouseClicked(Click click, boolean doubled) {
		if (super.mouseClicked(click, doubled)) {
			return true;
		}
		if (click.button() == 0 && insideBox(click.x(), click.y())) {
			dragging = true;
			grabOffX = (int) (click.x() - boxX());
			grabOffY = (int) (click.y() - boxY());
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(Click click, double offsetX, double offsetY) {
		if (dragging) {
			int rangeX = Math.max(1, this.width - boxW());
			int rangeY = Math.max(1, this.height - boxH());
			float nx = (float) ((click.x() - grabOffX) / rangeX);
			float ny = (float) ((click.y() - grabOffY) / rangeY);
			cfg.x = Math.max(0f, Math.min(1f, nx));
			cfg.y = Math.max(0f, Math.min(1f, ny));
			return true;
		}
		return super.mouseDragged(click, offsetX, offsetY);
	}

	@Override
	public boolean mouseReleased(Click click) {
		if (dragging) {
			dragging = false;
			HpConfig.save();
			return true;
		}
		return super.mouseReleased(click);
	}

	@Override
	public void removed() {
		HpConfig.save();
	}

	private class ScaleSlider extends SliderWidget {
		ScaleSlider(int x, int y, int w, int h) {
			super(x, y, w, h, Text.empty(),
					(cfg.scale - HpConfig.MIN_SCALE) / (HpConfig.MAX_SCALE - HpConfig.MIN_SCALE));
			updateMessage();
		}

		@Override
		protected void updateMessage() {
			setMessage(Text.literal("Розмір: " + String.format("%.2f", cfg.scale) + "x"));
		}

		@Override
		protected void applyValue() {
			cfg.scale = (float) (HpConfig.MIN_SCALE + this.value * (HpConfig.MAX_SCALE - HpConfig.MIN_SCALE));
			updateMessage();
		}
	}
}
