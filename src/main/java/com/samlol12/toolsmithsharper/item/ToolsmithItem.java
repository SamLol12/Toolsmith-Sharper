package com.samlol12.toolsmithsharper.item;

import com.samlol12.toolsmithsharper.config.ModConfig;
import com.samlol12.toolsmithsharper.registry.ModComponents;
import com.samlol12.toolsmithsharper.util.ModUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ToolsmithItem extends Item {
    public final int useTime;
    public final String coating;
    public final boolean consume;

    public ToolsmithItem(Settings settings, int useTime, String coating, boolean consume) {
        super(settings);
        this.useTime = useTime;
        this.coating = coating;
        this.consume = consume;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!this.coating.equals("none")) return ActionResult.PASS;

        ItemStack stack = user.getStackInHand(hand);
        Hand otherHand = (hand == Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack target = user.getStackInHand(otherHand);
        String tier = stack.getOrDefault(ModComponents.SHARPER_COATING_TIER, "base");

        if (ModUtils.canSharpenPreview(user, world, target, this.coating, tier)) {
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
        }
        return ActionResult.FAIL;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return this.coating.equals("none") ? ModConfig.WHETSTONE_USE_TIME : useTime;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            Hand hand = player.getStackInHand(Hand.MAIN_HAND) == stack ? Hand.MAIN_HAND : Hand.OFF_HAND;
            Hand otherHand = (hand == Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
            ItemStack target = player.getStackInHand(otherHand);
            String tier = stack.getOrDefault(ModComponents.SHARPER_COATING_TIER, "base");

            if (ModUtils.trySharpen(player, world, target, coating, tier) == ActionResult.SUCCESS) {
                player.swingHand(hand);
                if (consume) {
                    stack.decrement(1);
                } else {
                    if (!world.isClient()) {
                        stack.damage(1, (ServerWorld) world, player instanceof ServerPlayerEntity ? (ServerPlayerEntity) player : null,
                                item -> player.sendEquipmentBreakStatus(item, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
                    }
                }
                player.getItemCooldownManager().set(stack, 20);
            }
        }
        return stack;
    }
}