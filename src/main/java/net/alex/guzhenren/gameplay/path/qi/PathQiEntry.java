package net.alex.guzhenren.gameplay.path.qi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * One kind of Qi [气] the player is currently holding. Leaf record nested inside {@link PathQiData};
 * immutable; two components, the amount and the tick the hold ends at. The live amount is derived by
 * {@link PathQiData#current}; this record ticks nothing down itself.
 *
 * <p>⚠ A time anchor, not a running total -- nothing ticks it down, so it needs no clock of its own. ⚠
 * Re-adding does not "take the higher grade": {@link
 * PathQiService#set} re-anchors the hold on the SUM, so
 * re-applying the same kind restarts the hold full. ⚠ Fields floor at 0; {@code holdEndTick == 0} is valid.
 *
 * @author Alex
 * @version 1.0.0
 * @see PathQiData
 * @see PathQiService
 * @since 1.0.0
 */

public record PathQiEntry(long amount, long holdEndTick) {

    public static final Codec<PathQiEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.optionalFieldOf("amount", 0L).forGetter(PathQiEntry::amount),
            Codec.LONG.optionalFieldOf("hold_end_tick", 0L).forGetter(PathQiEntry::holdEndTick)
    ).apply(instance, PathQiEntry::new));
    public static final StreamCodec<ByteBuf, PathQiEntry> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, PathQiEntry::amount,
            ByteBufCodecs.VAR_LONG, PathQiEntry::holdEndTick,
            PathQiEntry::new);

    public PathQiEntry {
        amount = Math.max(0L, amount);
        holdEndTick = Math.max(0L, holdEndTick);
    }
}
