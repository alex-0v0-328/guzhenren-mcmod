package net.alex.guzhenren.item.material.qi;

import net.alex.guzhenren.custom.enums.aperture.Rank;
import net.alex.guzhenren.custom.enums.qi.QiKind;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Death Qi [死气] material: free to use, and it drains lifespan [寿元] until none is left.
 *
 * <p>Extends {@link net.alex.guzhenren.item.material.qi.QiMaterialItem}. Both the essence cost and
 * the charge duration are overridden to zero -- it is the only Qi material a mortal can use, and that
 * is exactly why: it harms him. To apply is inherited unchanged; the curse and the lifespan debt are
 * handled by {@link net.alex.guzhenren.attachment.service.path.PathQiService} and
 * {@link net.alex.guzhenren.attachment.service.body.BodyService}.
 *
 * <p>⚠ The debt is tallied on the body record, not the effect (no expiry hook); milk and /effect clear cannot cure it.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.item.material.qi.QiMaterialItem
 * @since 1.0.0
 */

public class DeathQiItem extends QiMaterialItem {

    public DeathQiItem(Properties properties, Rank rank) {
        super(properties, rank, QiKind.DEATH);
    }
    @Override
    protected int useDurationTicks(Player player, ItemStack stack) {return 0;}
    @Override
    protected long essenceCost() {return 0L;}
}
