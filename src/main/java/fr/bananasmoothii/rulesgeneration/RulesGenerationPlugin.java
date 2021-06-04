package fr.bananasmoothii.rulesgeneration;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public final class RulesGenerationPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
    }

    private static void test1() {
        CubicChunkEnvironment cce = new CubicChunkEnvironment(ThreadLocalRandom.current().nextInt());
        cce.set(CubicChunk.AIR_CHUNK, 0, 0, 0);
        cce.set(CubicChunk.AIR_CHUNK, -1, -1, -1);
        cce.set(CubicChunk.AIR_CHUNK, -1, -1, 0);
        cce.set(CubicChunk.AIR_CHUNK, -30, -30, -30);
        cce.set(CubicChunk.AIR_CHUNK, -30, -30, 0);
        cce.debugPrint();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("cbg_test")) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    test1();
                    sender.sendMessage("Done!");
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }
}
