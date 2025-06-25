package dev.akarah.qh.client.data;

import dev.akarah.qh.client.ClientUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class EquipmentSlotGetter {
    public static Optional<ItemStack> playerSlot(EquipmentSlot equipmentSlot) {
        return ClientUtil.localPlayer()
                .map(x -> x.getItemBySlot(equipmentSlot));
    }

    public static ItemStack playerSlotOrEmpty(EquipmentSlot equipmentSlot) {
        return ClientUtil.localPlayer()
                .map(x -> x.getItemBySlot(equipmentSlot))
                .orElse(ItemStack.EMPTY);
    }
}
