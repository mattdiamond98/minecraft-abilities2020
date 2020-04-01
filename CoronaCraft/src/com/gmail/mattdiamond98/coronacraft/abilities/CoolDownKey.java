package com.gmail.mattdiamond98.coronacraft.abilities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

/***
 * A tuple key object for storing players and
 * item types in data structures.
 */
public final class CoolDownKey {

    private UUID playerID;
    private Material item;

    public CoolDownKey(UUID playerID, Material item) {
        this.playerID = playerID;
        this.item = item;
    }

    public CoolDownKey(Player player, Material item) {
        this(player.getUniqueId(), item);
    }

    public CoolDownKey(String playerName, Material item) {
        this(CoronaCraft.instance.getServer().getPlayer(playerName).getUniqueId(), item);
    }

    public Player getPlayer() {
        return CoronaCraft.instance.getServer().getPlayer(playerID);
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public Material getItem() {
        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof CoolDownKey)) return false;
        CoolDownKey other = (CoolDownKey) o;
        return playerID.equals(other.playerID) && item.equals(other.item);
    }

    @Override
    public int hashCode() {
        return playerID.hashCode() * item.hashCode();
    }
}
