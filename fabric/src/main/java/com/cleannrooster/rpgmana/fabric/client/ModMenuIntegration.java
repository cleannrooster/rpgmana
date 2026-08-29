package com.cleannrooster.rpgmana.fabric.client;

import com.cleannrooster.rpgmana.client.ConfigChooserScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** Points ModMenu's config button at the general/GUI config chooser. */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigChooserScreen::new;
    }
}
