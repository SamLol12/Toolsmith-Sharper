package com.audacelol12.toolsmithsharper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;

public class ToolsmithSharperClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.contains(ToolsmithSharper.SHARPER_USES)) {
                int uses = stack.getOrDefault(ToolsmithSharper.SHARPER_USES, 0);
                String coating = stack.getOrDefault(ToolsmithSharper.SHARPER_COATING, "none");
                String tier = stack.getOrDefault(ToolsmithSharper.SHARPER_COATING_TIER, "base");

                String suffix = "";
                if (tier.equals("amplified")) suffix = " II";
                else if (tier.equals("extended")) suffix = " (Extended)";

                if (uses > 0) {
                    switch (coating) {
                        case "fire" -> {
                            lines.add(Text.literal("§7Effect: §cFire Oil" + suffix));
                            lines.add(Text.literal("§7Oil Uses Remaining: §c" + uses));
                        }
                        case "poison" -> {
                            lines.add(Text.literal("§7Effect: §aPoison Oil" + suffix));
                            lines.add(Text.literal("§7Oil Uses Remaining: §a" + uses));
                        }
                        case "vampire" -> {
                            lines.add(Text.literal("§7Effect: §4Vampire Oil" + suffix));
                            lines.add(Text.literal("§7Oil Uses Remaining: §4" + uses));
                        }
                        case "frost" -> {
                            lines.add(Text.literal("§7Effect: §bFrost Oil" + suffix));
                            lines.add(Text.literal("§7Oil Uses Remaining: §b" + uses));
                        }
                        default -> {
                            lines.add(Text.literal("§7Effect: §bHoned"));
                            lines.add(Text.literal("§7Honed Uses Remaining: §b" + uses));
                        }
                    }
                }
            }
        });
    }
}