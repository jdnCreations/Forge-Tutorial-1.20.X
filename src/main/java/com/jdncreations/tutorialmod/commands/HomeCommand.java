package com.jdncreations.tutorialmod.commands;

import com.jdncreations.tutorialmod.TutorialMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeCommand {
    static Logger logger = LogManager.getLogger(TutorialMod.MOD_ID);
    public static final Map<UUID, Map<String, BlockPos>> homes = new HashMap<>();

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("sethome")
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayer();
                        String homeName = StringArgumentType.getString(context, "name");
                        assert player != null;
                        BlockPos homePos = player.blockPosition();
                        homes.computeIfAbsent(player.getUUID(), k -> new HashMap<>()).put(homeName, homePos);
                        player.sendSystemMessage(Component.literal("Home " + homeName + " set at " + homePos));
                        logger.info("Player {} set home {} at {}", player.getName().getString(), homeName, homePos);
                        return 1;
                    })));
        dispatcher.register(Commands.literal("home")
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            String homeName = StringArgumentType.getString(context, "name");
                            assert player != null;
                            logger.info(homeName);
                            Map<String, BlockPos> playerHomes = homes.get(player.getUUID());
                            BlockPos homePos = playerHomes.get(homeName);
                            logger.info(homePos);
                            player.teleportTo(homePos.getX(), homePos.getY(), homePos.getZ());
                            player.sendSystemMessage(Component.literal("Teleported to " + homeName + "."));
                            return 1;
                        })));
    }
}
