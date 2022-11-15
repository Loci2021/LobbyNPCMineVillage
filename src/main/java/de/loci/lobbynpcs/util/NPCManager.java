package de.loci.lobbynpcs.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.loci.lobbynpcs.config.Config;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCManager {

    private static List<EntityPlayer> NPC = new ArrayList<EntityPlayer>();

    public static void createNPC(Player player, String name, String skin, String BungeeServerName){
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld)Bukkit.getWorld((player.getWorld().getName()))).getHandle();
        name = name.replaceAll("&","§");

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
        EntityPlayer npc = new EntityPlayer(nmsServer,nmsWorld,gameProfile,new PlayerInteractManager(nmsWorld));
        Location location = player.getLocation();
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        String[] skinName = getSkin(player, skin);
        gameProfile.getProperties().put("textures",new Property("textures",skinName[0],skinName[1]));

        addNPCPacket(npc);
        NPC.add(npc);

        int var = 1;
        if (Config.contains("NPCs")){
            var = Config.config.getConfigurationSection("NPCs").getKeys(false).size()+1;}
        Config.set("NPCs."+var+".X",player.getLocation().getX());
        Config.set("NPCs."+var+".Y",player.getLocation().getY());
        Config.set("NPCs."+var+".Z",player.getLocation().getZ());
        Config.set("NPCs."+var+".Yaw",player.getLocation().getYaw());
        Config.set("NPCs."+var+".Pitch",player.getLocation().getPitch());
        Config.set("NPCs."+var+".World",player.getLocation().getWorld().getName());

        Config.set("NPCs."+var+".Name",name);
        Config.set("NPCs."+var+".Skin",skin);
        Config.set("NPCs."+var+".texture",skinName[0]);
        Config.set("NPCs."+var+".signature",skinName[1]);

        Config.set("NPCs."+var+".BungeeServerName",BungeeServerName);
    }

    public static void loadNPC(Location location, GameProfile profile){
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld)location.getWorld()).getHandle();

        GameProfile gameProfile = profile;
        EntityPlayer npc = new EntityPlayer(nmsServer,nmsWorld,gameProfile,new PlayerInteractManager(nmsWorld));
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        addNPCPacket(npc);
        NPC.add(npc);
    }

    private static String[] getSkin(Player player,String name){
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/"+name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid
                    + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties")
                    .getAsJsonArray().get(0).getAsJsonObject();
            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[] {texture,signature};
        }catch (Exception e){
            Bukkit.broadcastMessage("§4Skins konnten nicht geladen werden!");
            e.printStackTrace();
            EntityPlayer p = ((CraftPlayer)player).getHandle();
            GameProfile profile = p.getProfile();
            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = property.getValue();
            String signature = property.getSignature();
            return new String[] {texture,signature};
        }
    }

    public static void addNPCPacket(EntityPlayer npc){
        for (Player player : Bukkit.getOnlinePlayers()){
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc,(byte) (npc.yaw * 256 / 360)));
        }
    }

    public static void addNPCJoinPacket(Player player){
        for (EntityPlayer npc : NPC){
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc,(byte) (npc.yaw * 256 / 360)));
        }
    }

    public static void removeNPC(Player player,EntityPlayer npc){
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
    }

    public static List<EntityPlayer> getNPCs(){
        return NPC;
    }

}
