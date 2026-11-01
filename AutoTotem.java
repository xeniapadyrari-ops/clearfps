package dev.clearfps.feature.impl;

import dev.clearfps.feature.Category;
import dev.clearfps.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;

/** Keeps a Totem of Undying in the offhand (swaps one in from the inventory/hotbar). */
public final class AutoTotem extends Feature {
    private int cooldown;

    public AutoTotem() {
        super("auto_totem", "Auto Totem", Category.COMBAT,
                "Moves a Totem of Undying into your offhand whenever it is empty.");
    }

    @Override
    public void onTick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null || mc.gameMode == null || p.isDeadOrDying()) return;
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        if (p.containerMenu != p.inventoryMenu) return; // another container is open
        if (p.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) return;

        // Player inventory menu: 9..35 = main inventory, 36..44 = hotbar
        for (int slot = 9; slot <= 44; slot++) {
            if (p.inventoryMenu.getSlot(slot).getItem().is(Items.TOTEM_OF_UNDYING)) {
                // SWAP with button 40 = swap with offhand
                mc.gameMode.handleInventoryMouseClick(
                        p.inventoryMenu.containerId, slot, 40, ClickType.SWAP, p);
                cooldown = 4;
                return;
            }
        }
    }
}
