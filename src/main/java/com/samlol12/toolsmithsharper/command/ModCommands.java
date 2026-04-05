package com.samlol12.toolsmithsharper.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.samlol12.toolsmithsharper.config.ModConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("toolsmithsharper")
                .then(CommandManager.literal("setUses").then(CommandManager.argument("value", IntegerArgumentType.integer(1)).executes(context -> { 
                    ModConfig.MAX_SHARPER_BASE_USES = IntegerArgumentType.getInteger(context, "value"); 
                    ModConfig.saveConfig(); 
                    context.getSource().sendFeedback(() -> Text.translatable("command.toolsmithsharper.set_uses", ModConfig.MAX_SHARPER_BASE_USES).formatted(Formatting.GREEN), false); 
                    return 1; 
                })))
                .then(CommandManager.literal("setCoatingUses").then(CommandManager.argument("value", IntegerArgumentType.integer(1)).executes(context -> { 
                    ModConfig.MAX_COATING_BASE_USES = IntegerArgumentType.getInteger(context, "value"); 
                    ModConfig.saveConfig(); 
                    context.getSource().sendFeedback(() -> Text.translatable("command.toolsmithsharper.set_coating_uses", ModConfig.MAX_COATING_BASE_USES).formatted(Formatting.GREEN), false); 
                    return 1; 
                })))
                .then(CommandManager.literal("setWhetstoneUses").then(CommandManager.argument("value", IntegerArgumentType.integer(1)).executes(context -> { 
                    ModConfig.MAX_WHETSTONE_USES = IntegerArgumentType.getInteger(context, "value"); 
                    ModConfig.saveConfig(); 
                    context.getSource().sendFeedback(() -> Text.translatable("command.toolsmithsharper.set_whetstone_uses", ModConfig.MAX_WHETSTONE_USES).formatted(Formatting.GREEN), false); 
                    return 1; 
                })))
                .then(CommandManager.literal("setUseTime").then(CommandManager.argument("value", IntegerArgumentType.integer(1)).executes(context -> { 
                    ModConfig.WHETSTONE_USE_TIME = IntegerArgumentType.getInteger(context, "value"); 
                    ModConfig.saveConfig(); 
                    context.getSource().sendFeedback(() -> Text.translatable("command.toolsmithsharper.set_use_time", ModConfig.WHETSTONE_USE_TIME).formatted(Formatting.GREEN), false); 
                    return 1; 
                })))
                .then(CommandManager.literal("setDamage").then(CommandManager.argument("value (%)", DoubleArgumentType.doubleArg(0.0)).executes(context -> { 
                    ModConfig.DAMAGE_MULTIPLIER = DoubleArgumentType.getDouble(context, "value (%)"); 
                    ModConfig.saveConfig(); 
                    context.getSource().sendFeedback(() -> Text.translatable("command.toolsmithsharper.set_damage", ModConfig.DAMAGE_MULTIPLIER).formatted(Formatting.GREEN), false); 
                    return 1; 
                })))
                .then(CommandManager.literal("setSpeed").then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0)).executes(context -> { 
                    ModConfig.SPEED_BOOST = DoubleArgumentType.getDouble(context, "value"); 
                    ModConfig.saveConfig(); 
                    context.getSource().sendFeedback(() -> Text.translatable("command.toolsmithsharper.set_speed", ModConfig.SPEED_BOOST).formatted(Formatting.GREEN), false); 
                    return 1; 
                })))
                .then(CommandManager.literal("setCost").then(CommandManager.argument("value", IntegerArgumentType.integer(1)).executes(context -> { 
                    ModConfig.XP_COST = IntegerArgumentType.getInteger(context, "value"); 
                    ModConfig.saveConfig(); 
                    context.getSource().sendFeedback(() -> Text.translatable("command.toolsmithsharper.set_cost", ModConfig.XP_COST).formatted(Formatting.GREEN), false); 
                    return 1; 
                })))
                .then(CommandManager.literal("setRepair").then(CommandManager.argument("value (%)", DoubleArgumentType.doubleArg(0.0)).executes(context -> { 
                    ModConfig.REPAIR_PERCENTAGE = DoubleArgumentType.getDouble(context, "value (%)"); 
                    ModConfig.saveConfig(); 
                    context.getSource().sendFeedback(() -> Text.translatable("command.toolsmithsharper.set_repair", (ModConfig.REPAIR_PERCENTAGE * 100)).formatted(Formatting.GREEN), false); 
                    return 1; 
                })))
            );
        });
    }
}