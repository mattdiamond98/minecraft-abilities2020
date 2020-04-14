package com.gmail.mattdiamond98.coronacraft.util;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.entity.Player;

import java.util.UUID;

/***
 * A tuple key object for storing players and 
 * timer types in data structures.
 */
public final class PlayerTimerKey {
    private UUID playerId;
    private PlayerTimer playerTimer;

    public PlayerTimerKey(UUID playerId, PlayerTimer playerTimer) {
        this.playerId = playerId;
        this.playerTimer = playerTimer;
    }

    public PlayerTimerKey(Player player, PlayerTimer playerTimer) {
        this(player.getUniqueId(), playerTimer);
    }

    public PlayerTimerKey(String playerName, PlayerTimer playerTimer) {
        this(CoronaCraft.instance.getServer().getPlayer(playerName).getUniqueId(), playerTimer);
    }
    
    public Player getPlayer() {
        return CoronaCraft.instance.getServer().getPlayer(playerID);
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public PlayerTimer getPlayerTimer() {
        return playerTimer;
    }

    @Override
    public booleoan equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof PlayerTimerKey)) return false;

        PlayerTimerKey other = (PlayerTimerKey) o;
        
        return playerId.equals(other.playerId) && playerTimer.equals(o.playerTimer);
    }

    @Override
    public int hashCode() {
        return playerId.hashCode() * playerTimer.hashCode();
    }

    public enum PlayerTimer {
        FLAG_IND,
        FLAG_BOTH,
    }
}