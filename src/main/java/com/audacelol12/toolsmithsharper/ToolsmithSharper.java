package com.audacelol12.toolsmithsharper;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

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
	public static int XP_COST = 1;
	// ==========================================

	// ==========================================
	// WHETSTONE
	// ==========================================
	public static final RegistryKey<Item> WHETSTONE_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "whetstone"));
	public static final Item WHETSTONE = Registry.register(
			Registries.ITEM,
			WHETSTONE_KEY.getValue(),
			new Item(new Item.Settings().registryKey(WHETSTONE_KEY).maxDamage(3))
	);

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

		// Commands
		registerCommands();

		// Grindstone
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!player.isSneaking() || hand != Hand.MAIN_HAND) return ActionResult.PASS;
			BlockState state = world.getBlockState(hitResult.getBlockPos());
			if (state.isOf(Blocks.GRINDSTONE)) {
				ItemStack offStack = player.getOffHandStack();
				if (!offStack.isOf(Items.FLINT)) {
					if (world.isClient()) player.sendMessage(Text.literal("§cYou need Flint in your offhand to sharpen!"), true);
					return ActionResult.FAIL;
				}
				ActionResult result = trySharpen(player, world, player.getMainHandStack());
				if (result == ActionResult.SUCCESS && !world.isClient()) {
					offStack.decrement(1);
				}
				return result;
			}
			return ActionResult.PASS;
		});

		// Whetstone
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stackInHand = player.getStackInHand(hand);

			if (stackInHand.isOf(WHETSTONE)) {
				Hand otherHand = (hand == Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
				ItemStack targetStack = player.getStackInHand(otherHand);

				ActionResult result = trySharpen(player, world, targetStack);

				if (result == ActionResult.SUCCESS) {
					if (!world.isClient()) {
						stackInHand.damage(1, (ServerWorld) world, player instanceof ServerPlayerEntity ? (ServerPlayerEntity) player : null,
								item -> player.sendEquipmentBreakStatus(item, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
					}
					return ActionResult.SUCCESS;
				}
				return ActionResult.PASS;
			}
			return ActionResult.PASS;
		});

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!world.isClient() && hand == Hand.MAIN_HAND) {
				decrementUses(player.getStackInHand(hand), player, world);
			}
			return ActionResult.PASS;
		});

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (!world.isClient()) {
				decrementUses(player.getMainHandStack(), player, world);
			}
		});
	}

	private ActionResult trySharpen(PlayerEntity player, World world, ItemStack target) {
		if (target.isEmpty() || !isSharpenable(target)) return ActionResult.PASS;

		// Already max sharpened
		int maxUsesForTarget = isTool(target) ? MAX_SHARPER_USES * 2 : MAX_SHARPER_USES;
		if (target.getOrDefault(SHARPER_USES, 0) >= maxUsesForTarget) {
			if (world.isClient()) player.sendMessage(Text.literal("§eThis item is already perfectly honed!"), true);
			return ActionResult.FAIL;
		}

		// XP
		if (player.experienceLevel < XP_COST && !player.getAbilities().creativeMode) {
			if (world.isClient()) player.sendMessage(Text.literal("§cYou need " + XP_COST + " XP level!"), true);
			return ActionResult.FAIL;
		}

		// Durability (20%)
		if (target.isDamageable()) {
			float durability = (float)(target.getMaxDamage() - target.getDamage()) / target.getMaxDamage();
			if (durability < 0.20f) {
				if (world.isClient()) player.sendMessage(Text.literal("§cTool too damaged! (Min 20%)"), true);
				return ActionResult.FAIL;
			}
		}

		if (!world.isClient()) {
			applySharperEffect(target);
			if (!player.getAbilities().creativeMode) player.addExperienceLevels(-XP_COST);

			world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.PLAYERS, 1.0f, 1.5f);
			((ServerWorld)world).spawnParticles(ParticleTypes.CRIT, player.getX(), player.getY() + 1, player.getZ(), 10, 0.3, 0.3, 0.3, 0.1);
		}
		return ActionResult.SUCCESS;
	}

	// =========================================================================
	// TAGS
	// =========================================================================

	private boolean isWeapon(ItemStack stack) {
		return stack.isIn(ItemTags.SWORDS) || stack.isIn(ItemTags.SPEARS);
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

	// =========================================================================

	private void applySharperEffect(ItemStack stack) {
		int usesToApply = MAX_SHARPER_USES;
		if (isTool(stack)) usesToApply = MAX_SHARPER_USES * 2;

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

	private void decrementUses(ItemStack stack, net.minecraft.entity.player.PlayerEntity player, net.minecraft.world.World world) {
		if (stack.contains(SHARPER_USES)) {
			int current = stack.getOrDefault(SHARPER_USES, 0);
			if (current <= 1) {
				stack.remove(SHARPER_USES);
				AttributeModifiersComponent.Builder builder = getModifiersBuilder(stack);
				stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());

				world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.6f, 1.8f);
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
	// COMMANDS
	// =========================================================================

	private void registerCommands() {
		// Commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("toolsmithsharper")
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
							.then(CommandManager.argument("value (%)", DoubleArgumentType.doubleArg(0.0))
									.executes(context -> {
										DAMAGE_MULTIPLIER = DoubleArgumentType.getDouble(context, "value (%)");
										saveConfig();
										context.getSource().sendFeedback(() -> Text.literal("§a[Toolsmith Sharper] Weapon Damage Multiplier set : §b" + DAMAGE_MULTIPLIER), false);
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
					.then(CommandManager.literal("setCost")
							.then(CommandManager.argument("value", IntegerArgumentType.integer(1))
									.executes(context -> {
										XP_COST = IntegerArgumentType.getInteger(context, "value");
										saveConfig();
										context.getSource().sendFeedback(() -> Text.literal("§a[Toolsmith Sharper] XP Cost set : §b" + XP_COST), false);
										return 1;
									})
							)
					)
			);
		});
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
				XP_COST = Integer.parseInt(props.getProperty("xpCost", "1"));
			} else {
				saveConfig();
			}
		} catch (Exception e) {
			System.out.println("Error loading Toolsmith Sharper config");
		}
	}

	private static void saveConfig() {
		try {
			Properties props = new Properties();
			props.setProperty("maxUses", String.valueOf(MAX_SHARPER_USES));
			props.setProperty("damageMultiplier", String.valueOf(DAMAGE_MULTIPLIER));
			props.setProperty("speedBoost", String.valueOf(SPEED_BOOST));
			props.setProperty("xpCost", String.valueOf(XP_COST));
			props.store(new FileOutputStream(getConfigFile()), "Configuration of Toolsmith Sharper");
		} catch (Exception e) {
			System.out.println("Error saving Toolsmith Sharper config");
		}
	}
}