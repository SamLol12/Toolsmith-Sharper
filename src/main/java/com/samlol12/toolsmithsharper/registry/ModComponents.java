package com.samlol12.toolsmithsharper.registry;

import com.mojang.serialization.Codec;
import com.samlol12.toolsmithsharper.ToolsmithSharper;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {
    public static final ComponentType<Integer> SHARPER_USES = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(ToolsmithSharper.MOD_ID, "sharper_uses"), 
            ComponentType.<Integer>builder()
                .codec(Codec.INT)
                .packetCodec(PacketCodecs.INTEGER)
                .build()
    );
    public static final ComponentType<String> SHARPER_COATING = Registry.register(
            Registries.DATA_COMPONENT_TYPE, 
            Identifier.of(ToolsmithSharper.MOD_ID, "sharper_coating"), 
            ComponentType.<String>builder()
                .codec(Codec.STRING)
                .packetCodec(PacketCodecs.STRING)
                .build()
    );
    public static final ComponentType<String> SHARPER_COATING_TIER = Registry.register(
            Registries.DATA_COMPONENT_TYPE, 
            Identifier.of(ToolsmithSharper.MOD_ID, "sharper_coating_tier"), 
            ComponentType.<String>builder()
                .codec(Codec.STRING)
                .packetCodec(PacketCodecs.STRING)
                .build()
    );

    public static final Identifier SHARPER_DAMAGE_ID = Identifier.of(ToolsmithSharper.MOD_ID, "sharper_damage");
    public static final Identifier SHARPER_SPEED_ID = Identifier.of(ToolsmithSharper.MOD_ID, "sharper_speed");

    public static void register() {
        // Appelé dans onInitialize pour charger la classe
    }
}