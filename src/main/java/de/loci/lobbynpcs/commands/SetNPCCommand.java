package de.loci.lobbynpcs.commands;

import de.loci.lobbynpcs.util.NPCManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetNPCCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){return false;}
        // Command: /setnpc <name> <Name des skins> <Ip4> <Port>
        Player player = (Player) sender;
        if (args.length==3){
            String name = args[0];
            String skin = args[1];
            String BungeeServerName = args[2];
            NPCManager.createNPC(player,name,skin,BungeeServerName);

        }else {
            player.sendMessage("§c/setnpc <name> <Name des Skin Besitzers> <Name des Bungeecord Servers>");
            player.sendMessage("§bBeispiel: /setnpc Bob Loci2021 citybuild");
        }
        return false;
    }



}
