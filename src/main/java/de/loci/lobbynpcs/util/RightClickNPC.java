package de.loci.lobbynpcs.util;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RightClickNPC extends Event implements Cancellable {

    private final Player player;
    private final EntityPlayer npc;
    private boolean isCancelled;
    private static final HandlerList HANDLERS = new HandlerList();

    public RightClickNPC(Player player, EntityPlayer npc){
        this.player = player;
        this.npc = npc;
    }

    public Player getPlayer(){
        return this.player;
    }

    public EntityPlayer getNpc() {
        return this.npc;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled(){
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }


}
