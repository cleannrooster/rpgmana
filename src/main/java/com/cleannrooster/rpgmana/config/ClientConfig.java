package com.cleannrooster.rpgmana.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "client")
public class ClientConfig implements ConfigData {
    public ClientConfig(){}
    @Comment("Alternate Mana Bar (Default: false)")
    public  boolean alt = false;


}
