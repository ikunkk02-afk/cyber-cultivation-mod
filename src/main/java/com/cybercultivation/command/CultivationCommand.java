package com.cybercultivation.command;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.CultivationDiscipline;
import com.cybercultivation.cultivation.CultivationPath;
import com.cybercultivation.flysword.FlyingSwordHandler;
import com.cybercultivation.meditation.MeditationHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CultivationCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext registryAccess,
                                Commands.CommandSelection environment) {

        dispatcher.register(
                Commands.literal("cultivation")
                        .then(Commands.literal("meditate")
                                .executes(CultivationCommand::executeMeditate))
                        .then(Commands.literal("flysword")
                                .executes(CultivationCommand::executeFlyingSword))
                        .then(Commands.literal("qi")
                                .executes(CultivationCommand::executeQiQuery)
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(CultivationCommand::executeQiAdd)))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(CultivationCommand::executeQiSet))))
                        .then(Commands.literal("aptitude")
                                .executes(CultivationCommand::executeAptitude))
                        .then(Commands.literal("path")
                                .executes(CultivationCommand::executePathQuery)
                                .then(Commands.literal("choose")
                                        .then(Commands.literal("heavenly")
                                                .executes(context -> executePathChoose(context, CultivationPath.HEAVENLY_DAO)))
                                        .then(Commands.literal("human")
                                                .executes(context -> executePathChoose(context, CultivationPath.HUMAN_DAO)))
                                        .then(Commands.literal("demon")
                                                .executes(context -> executePathChoose(context, CultivationPath.DEMON_DAO)))))
                        .then(Commands.literal("discipline")
                                .executes(CultivationCommand::executeDisciplineQuery)
                                .then(Commands.literal("main")
                                        .then(Commands.literal("choose")
                                                .then(disciplineLiteral("sword", CultivationDiscipline.SWORD, CultivationCommand::executeMainDisciplineChoose))
                                                .then(disciplineLiteral("body", CultivationDiscipline.BODY, CultivationCommand::executeMainDisciplineChoose))
                                                .then(disciplineLiteral("alchemy", CultivationDiscipline.ALCHEMY, CultivationCommand::executeMainDisciplineChoose))
                                                .then(disciplineLiteral("talisman", CultivationDiscipline.TALISMAN, CultivationCommand::executeMainDisciplineChoose))
                                                .then(disciplineLiteral("formation", CultivationDiscipline.FORMATION, CultivationCommand::executeMainDisciplineChoose))
                                                .then(disciplineLiteral("medical", CultivationDiscipline.MEDICAL, CultivationCommand::executeMainDisciplineChoose))))
                                .then(Commands.literal("sub")
                                        .then(Commands.literal("add")
                                                .then(disciplineLiteral("sword", CultivationDiscipline.SWORD, CultivationCommand::executeSubDisciplineAdd))
                                                .then(disciplineLiteral("body", CultivationDiscipline.BODY, CultivationCommand::executeSubDisciplineAdd))
                                                .then(disciplineLiteral("alchemy", CultivationDiscipline.ALCHEMY, CultivationCommand::executeSubDisciplineAdd))
                                                .then(disciplineLiteral("talisman", CultivationDiscipline.TALISMAN, CultivationCommand::executeSubDisciplineAdd))
                                                .then(disciplineLiteral("formation", CultivationDiscipline.FORMATION, CultivationCommand::executeSubDisciplineAdd))
                                                .then(disciplineLiteral("medical", CultivationDiscipline.MEDICAL, CultivationCommand::executeSubDisciplineAdd)))
                                        .then(Commands.literal("remove")
                                                .then(disciplineLiteral("sword", CultivationDiscipline.SWORD, CultivationCommand::executeSubDisciplineRemove))
                                                .then(disciplineLiteral("body", CultivationDiscipline.BODY, CultivationCommand::executeSubDisciplineRemove))
                                                .then(disciplineLiteral("alchemy", CultivationDiscipline.ALCHEMY, CultivationCommand::executeSubDisciplineRemove))
                                                .then(disciplineLiteral("talisman", CultivationDiscipline.TALISMAN, CultivationCommand::executeSubDisciplineRemove))
                                                .then(disciplineLiteral("formation", CultivationDiscipline.FORMATION, CultivationCommand::executeSubDisciplineRemove))
                                                .then(disciplineLiteral("medical", CultivationDiscipline.MEDICAL, CultivationCommand::executeSubDisciplineRemove)))))
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> disciplineLiteral(
            String id,
            CultivationDiscipline discipline,
            DisciplineExecutor executor) {
        return Commands.literal(id).executes(context -> executor.execute(context, discipline));
    }

    private static int executeMeditate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        MeditationHandler.toggleMeditation(player);
        return 1;
    }

    private static int executeFlyingSword(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        FlyingSwordHandler.toggle(player, false);
        return 1;
    }

    private static int executeQiQuery(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        player.sendSystemMessage(
                Component.literal(String.format("§b灵力: §e%d§7/§e%d", data.getCurrentQi(), data.getMaxQi()))
        );
        PlayerQiManager.syncToClient(player);
        return 1;
    }

    private static int executeQiAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        data.setCurrentQi(data.getCurrentQi() + amount);
        player.sendSystemMessage(
                Component.literal(String.format("§a灵力 +%d，当前灵力: §e%d§7/§e%d", amount, data.getCurrentQi(), data.getMaxQi()))
        );
        PlayerQiManager.syncToClient(player);
        return 1;
    }

    private static int executeQiSet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        data.setCurrentQi(amount);
        player.sendSystemMessage(
                Component.literal(String.format("§a灵力已设置为: §e%d§7/§e%d", data.getCurrentQi(), data.getMaxQi()))
        );
        PlayerQiManager.syncToClient(player);
        return 1;
    }

    private static int executeAptitude(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        if (!data.isAptitudeTested()) {
            player.sendSystemMessage(Component.literal("§e你尚未检测修仙资质，请先寻找修仙祭坛。"));
            return 0;
        }
        sendAptitudeResult(player, data);
        return 1;
    }

    private static int executePathQuery(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        player.sendSystemMessage(Component.literal("§b当前路线：§e" + data.getSelectedPathDisplayName()));
        PlayerQiManager.syncToClient(player);
        return 1;
    }

    private static int executePathChoose(CommandContext<CommandSourceStack> context, CultivationPath path) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        if (!requireAptitude(player, data)) {
            return 0;
        }
        data.setSelectedPath(path);
        player.sendSystemMessage(Component.literal("§a已选择路线：§e" + path.getChineseName()));
        PlayerQiManager.syncToClient(player);
        return 1;
    }

    private static int executeDisciplineQuery(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        player.sendSystemMessage(Component.literal("§b当前主科：§e" + data.getMainDisciplineDisplayName()));
        player.sendSystemMessage(Component.literal("§b当前副科：§e" + data.formatSubDisciplines()));
        PlayerQiManager.syncToClient(player);
        return 1;
    }

    private static int executeMainDisciplineChoose(CommandContext<CommandSourceStack> context,
                                                   CultivationDiscipline discipline) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        if (!requireAptitude(player, data)) {
            return 0;
        }
        if (!data.setMainDiscipline(discipline)) {
            player.sendSystemMessage(Component.literal("§c主科不能和副科重复。"));
            return 0;
        }
        player.sendSystemMessage(Component.literal("§a已选择主科：§e" + discipline.getChineseName()));
        PlayerQiManager.syncToClient(player);
        return 1;
    }

    private static int executeSubDisciplineAdd(CommandContext<CommandSourceStack> context,
                                               CultivationDiscipline discipline) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        if (!requireAptitude(player, data)) {
            return 0;
        }
        if (discipline == data.getMainDiscipline()) {
            player.sendSystemMessage(Component.literal("§c主科不能和副科重复。"));
            return 0;
        }
        if (data.getSubDisciplines().contains(discipline)) {
            player.sendSystemMessage(Component.literal("§e你已经选择了该副科。"));
            return 0;
        }
        if (data.getSubDisciplines().size() >= PlayerQiData.MAX_SUB_DISCIPLINES) {
            player.sendSystemMessage(Component.literal("§c副科最多只能选择两个。"));
            return 0;
        }
        data.addSubDiscipline(discipline);
        player.sendSystemMessage(Component.literal("§a已添加副科：§e" + discipline.getChineseName()));
        PlayerQiManager.syncToClient(player);
        return 1;
    }

    private static int executeSubDisciplineRemove(CommandContext<CommandSourceStack> context,
                                                  CultivationDiscipline discipline) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        if (!requireAptitude(player, data)) {
            return 0;
        }
        if (!data.removeSubDiscipline(discipline)) {
            player.sendSystemMessage(Component.literal("§e当前未选择该副科。"));
            return 0;
        }
        player.sendSystemMessage(Component.literal("§a已移除副科：§e" + discipline.getChineseName()));
        PlayerQiManager.syncToClient(player);
        return 1;
    }

    public static void sendAptitudeResult(ServerPlayer player, PlayerQiData data) {
        player.sendSystemMessage(Component.literal("§a你的修仙资质已经显现。"));
        player.sendSystemMessage(Component.literal("§b推荐路线：§e" + data.getRecommendedPathDisplayName()));
        player.sendSystemMessage(Component.literal("§b推荐主科：§e" + data.getRecommendedMainDisciplineDisplayName()));
        player.sendSystemMessage(Component.literal("§b推荐副科：§e" + data.formatRecommendedSubDisciplines()));
        player.sendSystemMessage(Component.literal("§b推荐五行：§e" + data.getRecommendedElementDisplayName()));
    }

    private static boolean requireAptitude(ServerPlayer player, PlayerQiData data) {
        if (data.isAptitudeTested()) {
            return true;
        }
        player.sendSystemMessage(Component.literal("§e你尚未检测修仙资质，请先寻找修仙祭坛。"));
        return false;
    }

    @FunctionalInterface
    private interface DisciplineExecutor {
        int execute(CommandContext<CommandSourceStack> context, CultivationDiscipline discipline) throws CommandSyntaxException;
    }
}
