package com.jdncreations.tutorialmod.events;

import com.jdncreations.tutorialmod.TutorialMod;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.jdncreations.tutorialmod.commands.HomeCommand.homes;

@Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID)
public class PlayerEventHandler {
    private static final String DATA_FOLDER = "playerdata/";

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {

        UUID playerUUID = event.getEntity().getUUID();
        String filePath = DATA_FOLDER + playerUUID.toString() + ".yaml";

        if (Files.exists(Paths.get(filePath))) {
            System.out.println("File for player " + playerUUID + " exists.");
        } else {
            System.out.println("No file found for player " + playerUUID);
            try {
                Path path = Path.of(filePath);
                Files.createFile(path);
                System.out.println("Created a file for player " + playerUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerUUID = event.getEntity().getUUID();
        Path filePath = Paths.get(DATA_FOLDER, playerUUID.toString() + ".yaml");

        try {
            saveHomesToFile(playerUUID, filePath);
            homes.remove(playerUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveHomesToFile(UUID playerUUID, Path filePath) throws IOException {
        Map<String, BlockPos> playerHomes = homes.get(playerUUID);
        if (playerHomes != null && !playerHomes.isEmpty()) {
            Yaml yaml = new Yaml();

            try (FileWriter writer = new FileWriter(String.valueOf(filePath))) {
                yaml.dump(playerHomes, writer);
                System.out.println("Data written to file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


