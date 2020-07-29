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
    private PlayerTimerType playerTimerType;

    public PlayerTimerKey(UUID playerId, PlayerTimerType playerTimerType) {
        this.playerId = playerId;
        this.playerTimerType = playerTimerType;
    }

    public PlayerTimerKey(Player player, PlayerTimerType playerTimerType) {
        this(player.getUniqueId(), playerTimerType);
    }

    public PlayerTimerKey(String playerName, PlayerTimerType playerTimerType) {
        this(CoronaCraft.instance.getServer().getPlayer(playerName).getUniqueId(), playerTimerType);
    }
    
    public Player getPlayer() {
        return CoronaCraft.instance.getServer().getPlayer(playerId);
    }

    public UUID getPlayerID() {
        return playerId;
    }

    public PlayerTimerType getPlayerTimerType() {
        return playerTimerType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof PlayerTimerKey)) return false;
        PlayerTimerKey other = (PlayerTimerKey) o;
        return playerId.equals(other.playerId) && playerTimerType.equals(((PlayerTimerKey) o).playerTimerType);
    }

    @Override
    public int hashCode() {
        return playerId.hashCode() * playerTimerType.hashCode();
    }

    public enum PlayerTimerType {
        FLAG_IND,
        FLAG_BOTH,
    }
}