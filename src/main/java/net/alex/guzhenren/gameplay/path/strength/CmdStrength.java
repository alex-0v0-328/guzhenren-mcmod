package net.alex.guzhenren.gameplay.path.strength;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.alex.guzhenren.command.ModCommandSupport;
import net.alex.guzhenren.command.ModEnumArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@code /gzr path strength}: reads and writes the beast and human strengths a body holds.
 *
 * <p>Offers {@code grant}/{@code revoke}/{@code clear} for beast strengths and
 * {@code set}/{@code add}/{@code sub} for human strengths. All writes delegate to
 * {@link PathStrengthService}, which is also the one funnel
 * that refreshes the attack modifier.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.command.ModEnumArgument
 * @since 1.0.0
 */

public final class CmdStrength {

    private CmdStrength() {}

    private static final String ARG_KIND = "kind";

    public static ArgumentBuilder<CommandSourceStack, ?> node() {
        return Commands.literal("strength")
                .then(beastNode("grant", PathStrengthService::grant))
                .then(beastNode("revoke", PathStrengthService::revoke))
                .then(humanStrength())
                .then(ModCommandSupport.withTargets(Commands.literal("clear"),
                        context -> ModCommandSupport.apply(context, PathStrengthService::clear)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> beastNode(
            String literal, ModCommandSupport.EnumOperation<BeastStrength> operation) {
        return Commands.literal(literal).then(ModCommandSupport.withTargets(
                ModEnumArgument.arg(ModCommandSupport.ARG_VALUE, BeastStrength.values()),
                context -> {
                    BeastStrength beast = ModEnumArgument.get(
                            context, ModCommandSupport.ARG_VALUE, BeastStrength.values());
                    return ModCommandSupport.apply(context, player -> operation.apply(player, beast));
                }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> humanStrength() {
        return Commands.literal("human")
                .then(ModEnumArgument.arg(ARG_KIND, HumanStrength.values())
                        .then(countNode("set", PathStrengthService::setHumanStrength))
                        .then(countNode("add", PathStrengthService::addHumanStrength))
                        .then(countNode("sub", (p, k, v) -> PathStrengthService.addHumanStrength(p, k, -v))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> countNode(String literal, HumanStrengthOperation operation) {
        return Commands.literal(literal).then(ModCommandSupport.withTargets(
                Commands.argument(ModCommandSupport.ARG_VALUE, IntegerArgumentType.integer()),
                context -> {
                    HumanStrength kind = kindOf(context);
                    int value = IntegerArgumentType.getInteger(context, ModCommandSupport.ARG_VALUE);
                    return ModCommandSupport.apply(context, player -> operation.apply(player, kind, value));
                }));
    }

    private static HumanStrength kindOf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return ModEnumArgument.get(context, ARG_KIND, HumanStrength.values());
    }

    @FunctionalInterface
    private interface HumanStrengthOperation {

        void apply(ServerPlayer player, HumanStrength kind, int value);
    }
}
