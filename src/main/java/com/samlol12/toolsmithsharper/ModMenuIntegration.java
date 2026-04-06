package com.samlol12.toolsmithsharper;

import com.samlol12.toolsmithsharper.config.ModConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("config.toolsmithsharper.title").formatted(Formatting.BOLD, Formatting.GOLD))
                    .setSavingRunnable(ModConfig::saveConfig);

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // ==========================================
            // CATEGORY 1 : CORE MECHANICS
            // ==========================================
            ConfigCategory mechanics = builder.getOrCreateCategory(Text.translatable("config.toolsmithsharper.category.mechanics").formatted(Formatting.GOLD));

            // Sub-category : Tool Maintenance
            SubCategoryBuilder maintenance = entryBuilder.startSubCategory(Text.translatable("config.toolsmithsharper.subcategory.maintenance").formatted(Formatting.YELLOW));
            
            maintenance.add(entryBuilder.startIntField(Text.translatable("config.toolsmithsharper.max_uses"), ModConfig.MAX_SHARPER_BASE_USES)
                    .setDefaultValue(32).setMin(1)
                    .setTooltip(Text.translatable("config.toolsmithsharper.max_uses.tooltip"))
                    .setSaveConsumer(newValue -> ModConfig.MAX_SHARPER_BASE_USES = newValue).build());

            maintenance.add(entryBuilder.startIntSlider(Text.translatable("config.toolsmithsharper.repair_percentage"), (int)(ModConfig.REPAIR_PERCENTAGE * 100), 0, 100)
                    .setDefaultValue(10).setTextGetter(value -> Text.literal(value + "%").formatted(Formatting.GREEN))
                    .setSaveConsumer(newValue -> ModConfig.REPAIR_PERCENTAGE = newValue / 100.0).build());

            maintenance.add(entryBuilder.startIntField(Text.translatable("config.toolsmithsharper.xp_cost"), ModConfig.XP_COST)
                    .setDefaultValue(1).setMin(0)
                    .setSaveConsumer(newValue -> ModConfig.XP_COST = newValue).build());

            mechanics.addEntry(maintenance.build());

            // Sub-category : Whetstone
            SubCategoryBuilder whetstone = entryBuilder.startSubCategory(Text.translatable("config.toolsmithsharper.subcategory.whetstone").formatted(Formatting.AQUA));
            
            whetstone.add(entryBuilder.startIntField(Text.translatable("config.toolsmithsharper.max_whetstone_uses"), ModConfig.MAX_WHETSTONE_USES)
                    .setDefaultValue(3).setMin(1).requireRestart()
                    .setSaveConsumer(newValue -> ModConfig.MAX_WHETSTONE_USES = newValue).build());

            whetstone.add(entryBuilder.startIntField(Text.translatable("config.toolsmithsharper.whetstone_use_time"), ModConfig.WHETSTONE_USE_TIME)
                    .setDefaultValue(30).setMin(1)
                    .setSaveConsumer(newValue -> ModConfig.WHETSTONE_USE_TIME = newValue).build());

            mechanics.addEntry(whetstone.build());


            // ==========================================
            // CATEGORY 2 : BALANCING
            // ==========================================
            ConfigCategory balancing = builder.getOrCreateCategory(Text.translatable("config.toolsmithsharper.category.balancing").formatted(Formatting.RED));

            balancing.addEntry(entryBuilder.startDoubleField(Text.translatable("config.toolsmithsharper.damage_multiplier"), ModConfig.DAMAGE_MULTIPLIER)
                    .setDefaultValue(0.25).setMin(0.0)
                    .setTooltip(Text.translatable("config.toolsmithsharper.damage_multiplier.tooltip").formatted(Formatting.ITALIC))
                    .setSaveConsumer(newValue -> ModConfig.DAMAGE_MULTIPLIER = newValue).build());

            balancing.addEntry(entryBuilder.startDoubleField(Text.translatable("config.toolsmithsharper.speed_boost"), ModConfig.SPEED_BOOST)
                    .setDefaultValue(2.0).setMin(0.0)
                    .setSaveConsumer(newValue -> ModConfig.SPEED_BOOST = newValue).build());


            // ==========================================
            // CATEGORY 3 : OIL EFFECTS
            // ==========================================
            ConfigCategory effects = builder.getOrCreateCategory(Text.translatable("config.toolsmithsharper.category.effects").formatted(Formatting.DARK_GREEN));

            // Group : Global parameters for all coatings
            SubCategoryBuilder globalEffects = entryBuilder.startSubCategory(Text.translatable("config.toolsmithsharper.subcategory.global_effects").formatted(Formatting.GRAY));
            globalEffects.add(entryBuilder.startIntField(Text.translatable("config.toolsmithsharper.effect_duration_base"), ModConfig.EFFECT_DURATION_BASE).setDefaultValue(100).build());
            globalEffects.add(entryBuilder.startIntField(Text.translatable("config.toolsmithsharper.effect_duration_extended"), ModConfig.EFFECT_DURATION_EXTENDED).setDefaultValue(200).build());
            effects.addEntry(globalEffects.build());

            // Group : Fire & Vampire
            SubCategoryBuilder specificOils = entryBuilder.startSubCategory(Text.translatable("config.toolsmithsharper.subcategory.specific_oils").formatted(Formatting.DARK_RED));
            specificOils.add(entryBuilder.startFloatField(Text.translatable("config.toolsmithsharper.fire_seconds_amplified"), ModConfig.FIRE_SECONDS_AMPLIFIED).setDefaultValue(8.0f).build());
            specificOils.add(entryBuilder.startFloatField(Text.translatable("config.toolsmithsharper.vampire_heal_amplified"), ModConfig.VAMPIRE_HEAL_AMPLIFIED).setDefaultValue(2.0f).build());
            effects.addEntry(specificOils.build());

            return builder.build();
        };
    }
}