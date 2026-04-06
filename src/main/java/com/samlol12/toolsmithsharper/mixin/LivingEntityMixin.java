package com.samlol12.toolsmithsharper.mixin;

import com.samlol12.toolsmithsharper.registry.ModComponents;
import com.samlol12.toolsmithsharper.util.ModUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "damage", at = @At("RETURN"))
    private void onDamagedBySharperOil(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        
        if (cir.getReturnValue() && source.getAttacker() instanceof PlayerEntity player && source.getSource() == player) {
            ItemStack stack = player.getMainHandStack();
            
            if (stack.contains(ModComponents.SHARPER_USES)) {
                LivingEntity target = (LivingEntity) (Object) this;
                String coating = stack.getOrDefault(ModComponents.SHARPER_COATING, "none");
                String tier = stack.getOrDefault(ModComponents.SHARPER_COATING_TIER, "base");

                ModUtils.applyCoatingHitEffects(player, target, coating, tier);

                ModUtils.decrementUses(stack, player, world);
            }
        }
    }
}