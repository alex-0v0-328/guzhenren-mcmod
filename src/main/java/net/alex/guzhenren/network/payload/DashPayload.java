package net.alex.guzhenren.network.payload;

import io.netty.buffer.ByteBuf;
import net.alex.guzhenren.Guzhenren;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * The client-intent payload for one dash.
 *
 * <p>Sent by the client while a matching effect is running; carries the vertical and horizontal
 * directions plus the facing they were taken at.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.effect.timed.CrashGuEffect
 * @since 1.0.0
 */

public record DashPayload(int vertical, int horizontal, float yRot) implements CustomPacketPayload {

    public static final Type<DashPayload> TYPE = new Type<>(Guzhenren.id("dash"));
    public static final StreamCodec<ByteBuf, DashPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, DashPayload::vertical,
            ByteBufCodecs.INT, DashPayload::horizontal,
            ByteBufCodecs.FLOAT, DashPayload::yRot,
            DashPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
