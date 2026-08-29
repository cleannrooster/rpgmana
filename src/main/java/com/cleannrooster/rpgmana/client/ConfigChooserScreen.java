package com.cleannrooster.rpgmana.client;

import com.cleannrooster.globehud.api.GlobeHudApi;
import com.cleannrooster.rpgmana.config.ServerConfigWrapper;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

/** Landing screen for ModMenu's config button: general (cloth) configs or the globe editor. */
public class ConfigChooserScreen extends Screen {
    private final Screen parent;

    public ConfigChooserScreen(Screen parent) {
        super(Text.literal("RPG Mana Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = 200;
        int x = this.width / 2 - w / 2;
        int y = this.height / 2 - 24;
        addDrawableChild(ButtonWidget.builder(Text.literal("General Configs"), button ->
                this.client.setScreen(AutoConfig.getConfigScreen(ServerConfigWrapper.class, this).get())
        ).dimensions(x, y, w, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("GUI Configs"), button ->
                this.client.setScreen(GlobeHudApi.createEditScreen(this))
        ).dimensions(x, y + 24, w, 20).build());
        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close())
                .dimensions(x, y + 58, w, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFF);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
