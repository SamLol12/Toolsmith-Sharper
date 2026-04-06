package com.samlol12.toolsmithsharper.event;

import com.samlol12.toolsmithsharper.item.ToolsmithItem;
import com.samlol12.toolsmithsharper.registry.ModComponents;
import com.samlol12.toolsmithsharper.registry.ModItems;
import com.samlol12.toolsmithsharper.util.ModUtils;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class ModEvents {
    public static void register() {
        // Grindstone
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!player.isSneaking() || hand != Hand.MAIN_HAND) return ActionResult.PASS;
            BlockState state = world.getBlockState(hitResult.getBlockPos());
            if (state.isOf(Blocks.GRINDSTONE)) {
                ItemStack offStack = player.getOffHandStack();
                String coating = "none";
                boolean isValidItem = false;

                if (offStack.isOf(Items.FLINT)) { isValidItem = true; coating = "none"; }
				else if (offStack.isOf(ModItems.FIRE_OIL)) { isValidItem = true; coating = "fire"; }
				else if (offStack.isOf(ModItems.POISON_OIL)) { isValidItem = true; coating = "poison"; }
				else if (offStack.isOf(ModItems.VAMPIRE_OIL)) { isValidItem = true; coating = "vampire"; }
				else if (offStack.isOf(ModItems.FROST_OIL)) { isValidItem = true; coating = "frost"; }
				else if (offStack.isOf(ModItems.LUCK_OIL)) { isValidItem = true; coating = "luck"; }

                if (!isValidItem) return ActionResult.PASS;

                String tier = offStack.getOrDefault(ModComponents.SHARPER_COATING_TIER, "base");
                ActionResult result = ModUtils.trySharpen(player, world, player.getMainHandStack(), coating, tier);
                if (result == ActionResult.SUCCESS && !world.isClient()) {
                    offStack.decrement(1);
                    player.getItemCooldownManager().set(player.getMainHandStack(), 60);
                }
                return result == ActionResult.SUCCESS ? ActionResult.SUCCESS : ActionResult.PASS;
            }
            return ActionResult.PASS;
        });

        // Whetstone
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stackInHand = player.getStackInHand(hand);
            if (stackInHand.getItem() instanceof ToolsmithItem toolsmithItem) {
                Hand otherHand = ModUtils.getOppositeHand(hand);
                ItemStack targetStack = player.getStackInHand(otherHand);
                String tier = stackInHand.getOrDefault(ModComponents.SHARPER_COATING_TIER, "base");

                if (ModUtils.canSharpenPreview(player, world, targetStack, toolsmithItem.coating, tier)) {
                    player.setCurrentHand(hand);
                    return ActionResult.SUCCESS;
                }
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient() && state.getHardness(world, pos) != 0.0F) {
                ModUtils.decrementUses(player.getMainHandStack(), player, world);
            }
        });
    }
}