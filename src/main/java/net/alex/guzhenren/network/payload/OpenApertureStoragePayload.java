package net.alex.guzhenren.network.payload;

import io.netty.buffer.ByteBuf;
import net.alex.guzhenren.Guzhenren;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Client intent: open the store of the named aperture [空窍].
 *
 * <p>A zero-data-except-an-index payload -- client intent is the one direction attachment sync cannot
 * carry, and this carries only which aperture to open. The server handler in
 * {@link net.alex.guzhenren.network.ModPayloads} opens the
 * {@link net.alex.guzhenren.menu.ApertureStorageMenu}; no player data travels upstream.
 *
 * <p>⚠ No payload may carry player data -- that always travels the other way, as synced state.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.network.ModPayloads
 * @since 1.0.0
 */

public record OpenApertureStoragePayload(int aperture) implements CustomPacketPayload {

    public static final Type<OpenApertureStoragePayload> TYPE = new Type<>(
            Guzhenren.id("open_aperture_storage"));
    public static final StreamCodec<ByteBuf, OpenApertureStoragePayload> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(OpenApertureStoragePayload::new, OpenApertureStoragePayload::aperture);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
