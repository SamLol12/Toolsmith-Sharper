package com.samlol12.toolsmithsharper;

import com.samlol12.toolsmithsharper.config.ModConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("config.toolsmithsharper.title"))
                    .setSavingRunnable(ModConfig::saveConfig);

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // ==========================================
            // CATEGORY 1 : BASE MECHANIC
            // ==========================================
            ConfigCategory mechanics = builder.getOrCreateCategory(Text.translatable("config.toolsmithsharper.category.mechanics"));

            // Champ INT : Max Honed Uses
            mechanics.addEntry(entryBuilder.startIntField(Text.translatable("config.toolsmithsharper.max_uses"), ModConfig.MAX_SHARPER_BASE_USES)
                    .setDefaultValue(32)
                    .setMin(1)
                    .setTooltip(Text.translatable("config.toolsmithsharper.max_uses.tooltip"))
                    .setSaveConsumer(newValue -> ModConfig.MAX_SHARPER_BASE_USES = newValue)
                    .build());

            // Champ INT : Max Coating Uses
            mechanics.addEntry(entryBuilder.startIntField(Text.translatable("config.toolsmithsharper.max_coating_uses"), ModConfig.MAX_COATING_BASE_USES)
                    .setDefaultValue(10)
                    .setMin(1)
                    .setTooltip(Text.translatable("config.toolsmithsharper.max_coating_uses.tooltip"))
                    .setSaveConsumer(newValue -> ModConfig.MAX_COATING_BASE_USES = newValue)
                    .build());

            // Champ INT : Max Whetstone Uses
            mechanics.addEntry(entryBuilder.startIntField(Text.translatable("config.toolsmithsharper.max_whetstone_uses"), ModConfig.MAX_WHETSTONE_USES)
                    .setDefaultValue(3)
                    .setMin(1)
                    .requireRestart()
                    .setTooltip(Text.translatable("config.toolsmithsharper.max_whetstone_uses.tooltip"))
                    .setSaveConsumer(newValue -> ModConfig.MAX_WHETSTONE_USES = newValue)
                    .build());

            // Champ INT : Whetstone Use Time
            mechanics.addEntry(entryBuilder.startIntField(Text.translatable("config.toolsmithsharper.whetstone_use_time"), ModConfig.WHETSTONE_USE_TIME)
                    .setDefaultValue(30)
                    .setMin(1)
                    .setTooltip(Text.translatable("config.toolsmithsharper.whetstone_use_time.tooltip"))
                    .setSaveConsumer(newValue -> ModConfig.WHETSTONE_USE_TIME = newValue)
                    .build());

            // Champ INT : XP Cost
            mechanics.addEntry(entryBuilder.startIntField(Text.translatable("config.toolsmithsharper.xp_cost"), ModConfig.XP_COST)
                    .setDefaultValue(1)
                    .setMin(0)
                    .setTooltip(Text.translatable("config.toolsmithsharper.xp_cost.tooltip"))
                    .setSaveConsumer(newValue -> ModConfig.XP_COST = newValue)
                    .build());

            // SLIDER DOUBLE : Repair Percentage
            mechanics.addEntry(entryBuilder.startIntSlider(Text.translatable("config.toolsmithsharper.repair_percentage"), (int)(ModConfig.REPAIR_PERCENTAGE * 100), 0, 100)
                    .setDefaultValue(10)
                    .setMin(0)
                    .setTooltip(Text.translatable("config.toolsmithsharper.repair_percentage.tooltip"))
                    .setTextGetter(value -> Text.literal(value + "%"))
                    .setSaveConsumer(newValue -> ModConfig.REPAIR_PERCENTAGE = newValue / 100.0)
                    .build());


            // ==========================================
            // CATEGORY 2 : COMBAT BALANCE & MINING
            // ==========================================
            ConfigCategory balancing = builder.getOrCreateCategory(Text.translatable("config.toolsmithsharper.category.balancing"));

            // Champ DOUBLE : Damage Multiplier
            balancing.addEntry(entryBuilder.startDoubleField(Text.translatable("config.toolsmithsharper.damage_multiplier"), ModConfig.DAMAGE_MULTIPLIER)
                    .setDefaultValue(0.25)
                    .setMin(0.0)
                    .setTooltip(Text.translatable("config.toolsmithsharper.damage_multiplier.tooltip"))
                    .setSaveConsumer(newValue -> ModConfig.DAMAGE_MULTIPLIER = newValue)
                    .build());

            // Champ DOUBLE : Speed Boost
            balancing.addEntry(entryBuilder.startDoubleField(Text.translatable("config.toolsmithsharper.speed_boost"), ModConfig.SPEED_BOOST)
                    .setDefaultValue(2.0)
                    .setMin(0.0)
                    .setTooltip(Text.translatable("config.toolsmithsharper.speed_boost.tooltip"))
                    .setSaveConsumer(newValue -> ModConfig.SPEED_BOOST = newValue)
                    .build());

            return builder.build();
        };
    }
}