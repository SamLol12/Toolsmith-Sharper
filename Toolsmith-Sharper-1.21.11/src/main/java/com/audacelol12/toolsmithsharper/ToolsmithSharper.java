package com.audacelol12.toolsmithsharper;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.command.CommandManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ToolsmithSharper implements ModInitializer {

	public static final String MOD_ID = "toolsmithsharper";

	// ==========================================
	// CONFIGURATION
	// ==========================================
	public static int MAX_SHARPER_USES = 32;
	public static double DAMAGE_MULTIPLIER = 0.25;
	public static double SPEED_BOOST = 2.0;
	// ==========================================

	public static final ComponentType<Integer> SHARPER_USES = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(MOD_ID, "sharper_uses"),
			ComponentType.<Integer>builder().codec(Codec.INT).build()
	);

	public static final Identifier SHARPER_DAMAGE_ID = Identifier.of(MOD_ID, "sharper_damage");
	public static final Identifier SHARPER_SPEED_ID = Identifier.of(MOD_ID, "sharper_speed");

	@Override
	public void onInitialize() {
		loadConfig();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("sharper")
					.then(CommandManager.literal("setUses")
							.then(CommandManager.argument("value", IntegerArgumentType.integer(1))
									.executes(context -> {
										MAX_SHARPER_USES = IntegerArgumentType.getInteger(context, "value");
										saveConfig();
										context.getSource().sendFeedback(() -> Text.literal("§a[Toolsmith Sharper] Max Honed Uses set : §b" + MAX_SHARPER_USES), false);
										return 1;
									})
							)
					)
					.then(CommandManager.literal("setDamage")
							.then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0))
									.executes(context -> {
										DAMAGE_MULTIPLIER = DoubleArgumentType.getDouble(context, "value");
										saveConfig();
										context.getSource().sendFeedback(() -> Text.literal("§a[Toolsmith Sharper] Weapon Damage Mutiplier set : §b" + DAMAGE_MULTIPLIER), false);
										return 1;
									})
							)
					)
					.then(CommandManager.literal("setSpeed")
							.then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0))
									.executes(context -> {
										SPEED_BOOST = DoubleArgumentType.getDouble(context, "value");
										saveConfig();
										context.getSource().sendFeedback(() -> Text.literal("§a[Toolsmith Sharper] Tool Speed Boost set : §b" + SPEED_BOOST), false);
										return 1;
									})
							)
					)
			);
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!player.isSneaking() || hand != Hand.MAIN_HAND) return ActionResult.PASS;

			BlockState state = world.getBlockState(hitResult.getBlockPos());
			if (state.isOf(Blocks.GRINDSTONE)) {
				ItemStack stack = player.getStackInHand(hand);
				if (!stack.isEmpty() && isSharpenable(stack)) {
					if (!world.isClient()) {
						applySharperEffect(stack);
						world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
					}
					return ActionResult.SUCCESS;
				}
			}
			return ActionResult.PASS;
		});

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!world.isClient() && hand == Hand.MAIN_HAND) {
				decrementUses(player.getStackInHand(hand));
			}
			return ActionResult.PASS;
		});

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (!world.isClient()) {
				decrementUses(player.getMainHandStack());
			}
		});
	}

	// =========================================================================
	// TAGS
	// =========================================================================
	private boolean isWeapon(ItemStack stack) {
		return stack.isIn(ItemTags.SWORDS) || stack.isOf(Items.TRIDENT) || stack.isOf(Items.MACE) || stack.isOf(Items.SPEARS);
	}

	private boolean isTool(ItemStack stack) {
		return stack.isIn(ItemTags.PICKAXES) || stack.isIn(ItemTags.SHOVELS) || stack.isIn(ItemTags.HOES);
	}

	private boolean isAxe(ItemStack stack) {
		return stack.isIn(ItemTags.AXES);
	}

	private boolean isSharpenable(ItemStack stack) {
		return isWeapon(stack) || isTool(stack) || isAxe(stack);
	}

	private void applySharperEffect(ItemStack stack) {
		int usesToApply = MAX_SHARPER_USES;

		if (isTool(stack)) {
			usesToApply = MAX_SHARPER_USES * 2;
		}

		stack.set(SHARPER_USES, usesToApply);
		AttributeModifiersComponent.Builder builder = getModifiersBuilder(stack);

		if (isWeapon(stack) || isAxe(stack)) {
			builder.add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(SHARPER_DAMAGE_ID, DAMAGE_MULTIPLIER, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), AttributeModifierSlot.MAINHAND);
		}

		if (isTool(stack) || isAxe(stack)) {
			builder.add(EntityAttributes.MINING_EFFICIENCY, new EntityAttributeModifier(SHARPER_SPEED_ID, SPEED_BOOST, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
		}

		stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
	}

	// =========================================================================

	private void decrementUses(ItemStack stack) {
		if (stack.contains(SHARPER_USES)) {
			int current = stack.getOrDefault(SHARPER_USES, 0);
			if (current <= 1) {
				stack.remove(SHARPER_USES);
				AttributeModifiersComponent.Builder builder = getModifiersBuilder(stack);
				stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
			} else {
				stack.set(SHARPER_USES, current - 1);
			}
		}
	}

	private AttributeModifiersComponent.Builder getModifiersBuilder(ItemStack stack) {
		AttributeModifiersComponent currentMods = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
		AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();

		for (AttributeModifiersComponent.Entry entry : currentMods.modifiers()) {
			if (!entry.modifier().id().equals(SHARPER_DAMAGE_ID) && !entry.modifier().id().equals(SHARPER_SPEED_ID)) {
				builder.add(entry.attribute(), entry.modifier(), entry.slot());
			}
		}
		return builder;
	}

	// =========================================================================
	// SAVE & FILE READING
	// =========================================================================
	private static File getConfigFile() {
		return FabricLoader.getInstance().getConfigDir().resolve("toolsmithsharper.properties").toFile();
	}

	private static void loadConfig() {
		try {
			File file = getConfigFile();
			if (file.exists()) {
				Properties props = new Properties();
				props.load(new FileInputStream(file));
				MAX_SHARPER_USES = Integer.parseInt(props.getProperty("maxUses", "32"));
				DAMAGE_MULTIPLIER = Double.parseDouble(props.getProperty("damageMultiplier", "0.25"));
				SPEED_BOOST = Double.parseDouble(props.getProperty("speedBoost", "2.0"));
			} else {
				saveConfig();
			}
		} catch (Exception e) {
			System.out.println("Error occurred for loading config Toolsmith Sharper file");
		}
	}

	private static void saveConfig() {
		try {
			Properties props = new Properties();
			props.setProperty("maxUses", String.valueOf(MAX_SHARPER_USES));
			props.setProperty("damageMultiplier", String.valueOf(DAMAGE_MULTIPLIER));
			props.setProperty("speedBoost", String.valueOf(SPEED_BOOST));
			props.store(new FileOutputStream(getConfigFile()), "Configuration of Toolsmith Sharper");
		} catch (Exception e) {
			System.out.println("Error occurred for saving config Toolsmith Sharper file");
		}
	}
}