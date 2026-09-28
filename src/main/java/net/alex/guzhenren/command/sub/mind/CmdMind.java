package net.alex.guzhenren.command.sub.mind;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.alex.guzhenren.attachment.service.mind.MindService;
import net.alex.guzhenren.command.ModCommandSupport;
import net.alex.guzhenren.custom.enums.wisdom.Brilliance;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * {@code /gzr mind}: reads and writes the thought [念] pools.
 *
 * <p>Offers graded set/up/down for brilliance [才情] and delegates to
 * {@link net.alex.guzhenren.command.sub.mind.CmdWisdom} for the three wisdom pools. All of
 * {@code /gzr mind} is ungated -- a mortal thinks too.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.command.sub.mind.CmdWisdom
 * @since 1.0.0
 */

public final class CmdMind {

    private CmdMind() {}

    public static ArgumentBuilder<CommandSourceStack, ?> node() {
        return Commands.literal("mind")
                .then(brilliance())
                .then(CmdWisdom.node());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> brilliance() {
        return ModCommandSupport.enumSetNode("brilliance", Brilliance.values(), MindService::setBrilliance,
                        ModCommandSupport.ANYONE, null)
                .then(shift("up", 1))
                .then(shift("down", -1));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> shift(String literal, int delta) {
        return ModCommandSupport.withTargets(Commands.literal(literal),
                context -> ModCommandSupport.apply(context, player -> MindService.shiftBrilliance(player, delta)));
    }
}
