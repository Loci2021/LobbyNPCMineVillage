package de.loci.lobbynpcs.listeners;

import de.loci.lobbynpcs.Main;
import de.loci.lobbynpcs.config.Config;
import de.loci.lobbynpcs.util.NPCManager;
import de.loci.lobbynpcs.util.PacketReader;
import de.loci.lobbynpcs.util.RightClickNPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (NPCManager.getNPCs() != null){
            if (!NPCManager.getNPCs().isEmpty()){
                NPCManager.addNPCJoinPacket(player);
            }
        }

        PacketReader reader = new PacketReader();
        reader.inject(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PacketReader reader = new PacketReader();
        reader.unInject(player);
    }

    @EventHandler
    public void onRightClickNPC(RightClickNPC event) {
        Player player = event.getPlayer();
        AtomicReference<String> ServerName = new AtomicReference<>("lobby");

        Config.config.getConfigurationSection("NPCs").getKeys(false).forEach(npcid ->{
            String name = Config.config.getString("NPCs."+npcid+".Name");
            if (name.equalsIgnoreCase(event.getNpc().getName())){
                ServerName.set(Config.config.getString("NPCs." + npcid + ".BungeeServerName"));
            }
        });

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(ServerName.get());
        } catch (IOException e) {
            Bukkit.getLogger().info("Error");
            e.printStackTrace();
        }
        player.sendPluginMessage(Main.getPlugin(Main.class), "BungeeCord", b.toByteArray());

    }
}
