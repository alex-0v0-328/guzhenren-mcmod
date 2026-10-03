package net.alex.guzhenren.gameplay.mind;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.alex.guzhenren.command.ModCommandSupport;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * The wisdom cells under {@code /gzr mind}, spelled out one literal at a time.
 *
 * <p>Iterates {@link WisdomType} and builds a cell per type,
 * each offering {@code current}/{@code max} with {@code set}/{@code add}/{@code sub}/{@code refill}.
 * All writes delegate to {@link MindService}.
 *
 * <p>⚠ They are literals rather than a single enum argument so that brilliance [才情] can sit beside
 * them as a sibling. Tidying this into one enum argument takes that away.
 *
 * @author Alex
 * @version 1.0.0
 * @see CmdMind
 * @since 1.0.0
 */

public final class CmdWisdom {

    private CmdWisdom() {}

    public static ArgumentBuilder<CommandSourceStack, ?> node() {
        LiteralArgumentBuilder<CommandSourceStack> wisdom = Commands.literal("wisdom");
        for (WisdomType type : WisdomType.values()) {
            wisdom.then(cell(type));
        }
        return wisdom;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> cell(WisdomType type) {
        return Commands.literal(type.getSerializedName())
                .then(Commands.literal("current")
                        .then(ModCommandSupport.longNode("set", (p, v) -> MindService.setCurrent(p, type, v)))
                        .then(ModCommandSupport.longNode("add", (p, v) -> MindService.addCurrent(p, type, v)))
                        .then(ModCommandSupport.longNode("sub", (p, v) -> MindService.addCurrent(p, type, -v))))
                .then(Commands.literal("max")
                        .then(ModCommandSupport.longNode("set", (p, v) -> MindService.setMax(p, type, v)))
                        .then(ModCommandSupport.longNode("add", (p, v) -> MindService.addMax(p, type, v)))
                        .then(ModCommandSupport.longNode("sub", (p, v) -> MindService.addMax(p, type, -v))))
                .then(ModCommandSupport.withTargets(Commands.literal("refill"),
                        context -> ModCommandSupport.apply(context, player -> MindService.refill(player, type))));
    }
}
