package de.loci.lobbynpcs;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.loci.lobbynpcs.commands.SetNPCCommand;
import de.loci.lobbynpcs.config.Config;
import de.loci.lobbynpcs.listeners.JoinListener;
import de.loci.lobbynpcs.util.NPCManager;
import de.loci.lobbynpcs.util.PacketReader;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public final class Main extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        getCommand("setnpc").setExecutor(new SetNPCCommand());
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(),this);
        new Config();

        if (Config.contains("NPCs")){loadNPC();}


        if (!Bukkit.getOnlinePlayers().isEmpty()){
            Bukkit.getOnlinePlayers().forEach(players -> {
                PacketReader reader = new PacketReader();
                reader.inject(players);
            });
        }

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord",this);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(players -> {
            PacketReader reader = new PacketReader();
            reader.unInject(players);
            for (EntityPlayer npc : NPCManager.getNPCs()){
                NPCManager.removeNPC(players,npc);
            }
        });
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    public void loadNPC(){
        Config.config.getConfigurationSection("NPCs").getKeys(false).forEach(npc ->{

            long x = Config.config.getLong("NPCs."+npc+".X");
            long y = Config.config.getLong("NPCs."+npc+".Y");
            long z = Config.config.getLong("NPCs."+npc+".Z");
            long yaw = Config.config.getLong("NPCs."+npc+".Yaw");
            long pitch = Config.config.getLong("NPCs."+npc+".Pitch");
            String world = Config.config.getString("NPCs."+npc+".World");
            String name = Config.config.getString("NPCs."+npc+".Name");
            String texture = Config.config.getString("NPCs."+npc+".texture");
            String signature = Config.config.getString("NPCs."+npc+".signature");

            Location location = new Location(Bukkit.getWorld(world),x,y,z);
            location.setYaw(yaw);
            location.setPitch(pitch);

            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
            gameProfile.getProperties().put("textures",new Property("textures",texture,signature));

            NPCManager.loadNPC(location,gameProfile);
        });

    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
    }

}
