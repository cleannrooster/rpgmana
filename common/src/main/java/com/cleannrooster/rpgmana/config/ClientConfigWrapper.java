package com.cleannrooster.rpgmana.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = "rpgmana")
public class ClientConfigWrapper extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("client")
    public ClientConfig client = new ClientConfig();
}