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

                if (uses > 0) {
                    lines.add(Text.literal("§7Effect: §bHoned"));
                    lines.add(Text.literal("§7Honed Uses Remaining: §b" + uses));
                }
            }
        });
    }
}