package net.alex.guzhenren.item;

import net.alex.guzhenren.Guzhenren;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;

/**
 * The inventory-toss half of "a Vital Gu [本命蛊] cannot be thrown away"; the Q-key drop is answered
 * on the item itself via {@code onDroppedByPlayer}.
 *
 * <p>Only {@link net.alex.guzhenren.item.GuItem#isVital} stacks are refused. This handler must
 * hand the stack back into the inventory: vanilla's {@link net.neoforged.neoforge.event.entity.item.ItemTossEvent}
 * removes the entity before posting, so canceling alone deletes the item instead of saving it.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.item.GuItem
 * @since 1.0.0
 */

@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public final class ItemTossEvents {

    private ItemTossEvents() {}

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
        if (!GuItem.isVital(stack)) return;

        event.setCanceled(true);
        event.getPlayer().getInventory().placeItemBackInInventory(stack);
    }
}
