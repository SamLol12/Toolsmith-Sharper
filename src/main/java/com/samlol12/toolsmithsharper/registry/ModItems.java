package com.samlol12.toolsmithsharper.registry;

import com.samlol12.toolsmithsharper.ToolsmithSharper;
import com.samlol12.toolsmithsharper.config.ModConfig;
import com.samlol12.toolsmithsharper.item.ToolsmithItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final RegistryKey<Item> WHETSTONE_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ToolsmithSharper.MOD_ID, "whetstone"));
    public static final Item WHETSTONE = Registry.register(Registries.ITEM, WHETSTONE_KEY.getValue(),
            new ToolsmithItem(new Item.Settings().registryKey(WHETSTONE_KEY).maxDamage(ModConfig.MAX_WHETSTONE_USES), ModConfig.WHETSTONE_USE_TIME, "none", false));

    public static final RegistryKey<Item> FIRE_OIL_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ToolsmithSharper.MOD_ID, "fire_oil"));
    public static final Item FIRE_OIL = Registry.register(Registries.ITEM, FIRE_OIL_KEY.getValue(),
            new ToolsmithItem(new Item.Settings().registryKey(FIRE_OIL_KEY), 12, "fire", true));

    public static final RegistryKey<Item> POISON_OIL_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ToolsmithSharper.MOD_ID, "poison_oil"));
    public static final Item POISON_OIL = Registry.register(Registries.ITEM, POISON_OIL_KEY.getValue(),
            new ToolsmithItem(new Item.Settings().registryKey(POISON_OIL_KEY), 12, "poison", true));

    public static final RegistryKey<Item> VAMPIRE_OIL_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ToolsmithSharper.MOD_ID, "vampire_oil"));
    public static final Item VAMPIRE_OIL = Registry.register(Registries.ITEM, VAMPIRE_OIL_KEY.getValue(),
            new ToolsmithItem(new Item.Settings().registryKey(VAMPIRE_OIL_KEY), 12, "vampire", true));

    public static final RegistryKey<Item> FROST_OIL_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ToolsmithSharper.MOD_ID, "frost_oil"));
    public static final Item FROST_OIL = Registry.register(Registries.ITEM, FROST_OIL_KEY.getValue(),
            new ToolsmithItem(new Item.Settings().registryKey(FROST_OIL_KEY), 12, "frost", true));

    public static final RegistryKey<Item> LUCK_OIL_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ToolsmithSharper.MOD_ID, "luck_oil"));
    public static final Item LUCK_OIL = Registry.register(Registries.ITEM, LUCK_OIL_KEY.getValue(),
            new ToolsmithItem(new Item.Settings().registryKey(LUCK_OIL_KEY), 12, "luck", true));

    public static void register() {
        // Appelé dans onInitialize
    }
}