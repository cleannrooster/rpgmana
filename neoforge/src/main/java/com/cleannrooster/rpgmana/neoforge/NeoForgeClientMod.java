package com.cleannrooster.rpgmana.neoforge;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.RpgmanaClient;
import com.cleannrooster.rpgmana.client.ConfigChooserScreen;
import com.cleannrooster.rpgmana.client.InGameHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(modid = Rpgmana.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientMod {

    public static final Identifier MANA_HUD_LAYER_ID = Identifier.of(Rpgmana.MOD_ID, "mana_hud");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RpgmanaClient.init();

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (modContainer, parent) -> new ConfigChooserScreen(parent));

        // Game-bus listener: the Fabric side does this with a shouldRenderExperience mixin.
        NeoForge.EVENT_BUS.addListener(NeoForgeClientMod::onRenderGuiLayerPre);
    }

    private static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR) && InGameHud.shouldSuppressExpBar()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.CHAT, MANA_HUD_LAYER_ID, (context, tickCounter) -> {
            if (MinecraftClient.getInstance().options.hudHidden) {
                return;
            }
            RpgmanaClient.HUD.render(context, tickCounter);
        });
    }
}
