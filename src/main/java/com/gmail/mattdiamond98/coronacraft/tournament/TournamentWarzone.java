package com.gmail.mattdiamond98.coronacraft.tournament;

import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.TeamConfig;
import com.tommytony.war.event.WarPlayerJoinEvent;
import com.tommytony.war.job.LoadoutResetJob;
import com.tommytony.war.utility.LoadoutSelection;
import com.tommytony.war.utility.PotionEffectHelper;
import com.tommytony.war.volume.Volume;
import com.tommytony.war.volume.ZoneVolume;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;

import java.util.logging.Level;

public class TournamentWarzone extends Warzone {
    public boolean isStarted=false;
    public Tournament tournament;
    public Warzone warzone;
    public boolean BattleStarted(){
        return isStarted;

    }
    public void StartBattle(){
        isStarted=true;

    }
    public TournamentWarzone(World world, String name, Tournament tournamenty, Warzone warzone) {
        super(world, name);
        this.setVolume(warzone.getVolume());
        this.tournament=tournamenty;
    }

@Override
public Team autoAssign(Player p){
        return Team.getTeamByPlayerName(p.getName());

}
@Override
 public boolean assign(Player player, Team team){

    if (!War.war.canPlayWar(player, team)) {
        War.war.badMsg(player, "join.permission.single");
        return false;
    }
    if (player.getWorld() != this.getWorld()) {
        player.teleport(this.getWorld().getSpawnLocation());
    }
    PermissionAttachment attachment = player.addAttachment(War.war);
    this.putinAttachments(player, attachment);
    attachment.setPermission("war.playing", true);
    attachment.setPermission("war.playing." + this.getName().toLowerCase(), true);
    team.addPlayer(player);
    team.resetSign();
    if (this.hasPlayerState(player.getName())) {
        War.war.getLogger().log(Level.WARNING, "Player {0} in warzone {1} already has a stored state - they may have lost items",
                new Object[] {player.getName(), this.getName()});
        this.removeFromPlayerStates(player.getName());
    }
    this.getReallyDeadFighters().remove(player.getName());
    this.keepPlayerState(player);
    War.war.msg(player, "join.inventorystored");
    this.TournamentSpawnPlayer(team, player);
    this.broadcast("join.broadcast", player.getName(), team.getKind().getFormattedName());
    this.tryCallDelayedPlayers();
    Bukkit.getPluginManager().callEvent(new WarPlayerJoinEvent(player, team));
    return true;

}
public void TournamentSpawnPlayer(Team team, Player player){
    this.preventItemHackingThroughOpenedInventory(player);
    player.getInventory().clear();

    // clear potion effects
    PotionEffectHelper.clearPotionEffects(player);

    // Fill hp
    player.setRemainingAir(player.getMaximumAir());
    AttributeInstance ai = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    for (AttributeModifier mod : ai.getModifiers()) {
        ai.removeModifier(mod);
    }
    ai.setBaseValue(20.0);
    player.setHealth(ai.getValue());
    player.setFoodLevel(20);
    player.setSaturation(team.getTeamConfig().resolveInt(TeamConfig.SATURATION));
    player.setExhaustion(0);
    player.setFallDistance(0);
    player.setFireTicks(0);
    player.setLevel(0);
    player.setExp(0);
    player.setAllowFlight(false);
    player.setFlying(false);


    this.setKillCount(player.getName(), 0);

    if (player.getGameMode() != GameMode.SURVIVAL) {
        // Players are always in survival mode in warzones
        player.setGameMode(GameMode.SURVIVAL);
    }


    String potionEffect = team.getTeamConfig().resolveString(TeamConfig.APPLYPOTION);
    if (!potionEffect.isEmpty()) {
        PotionEffect effect = War.war.getPotionEffect(potionEffect);
        if (effect != null) {
            player.addPotionEffect(effect);
        } else {
            War.war.getLogger().log(Level.WARNING,
                    "Failed to apply potion effect {0} in warzone {1}.",
                    new Object[] {potionEffect, this.getName()});
        }
    }
    this.addtoRespawn(player);
    Volume volume= team.getSpawnVolumes().get(team.getRandomSpawn());
    for(int y=volume.getMaxY(); y<=4; y++){
    for(int x=volume.getMinX(); x<=volume.getMaxX(); x++){

        new Location(player.getWorld(), x, y, volume.getMaxZ()+1).getBlock().setType(Material.BARRIER);
        new Location(player.getWorld(), x, y, volume.getMinZ()-1).getBlock().setType(Material.BARRIER);

    }
    for(int z=volume.getMinZ(); z<=volume.getMaxZ(); z++){
        new Location(player.getWorld(), volume.getMaxX()-1, y, z).getBlock().setType(Material.BARRIER);
        new Location(player.getWorld(), volume.getMinX()-1, y, z).getBlock().setType(Material.BARRIER);


    }
    }

        boolean isFirstRespawn = false;
        if (!this.getLoadoutSelections().keySet().contains(player.getName())) {
            isFirstRespawn = true;
            this.getLoadoutSelections().put(player.getName(), new LoadoutSelection(true, 0));
        } else if (this.isReinitializing()) {
            isFirstRespawn = true;
            this.getLoadoutSelections().get(player.getName()).setStillInSpawn(true);
        } else {
            this.getLoadoutSelections().get(player.getName()).setStillInSpawn(true);
        }
      War.war.getKillstreakReward().getAirstrikePlayers().remove(player.getName());

    final LoadoutResetJob job = new LoadoutResetJob(this, team, player, true, false);
    job.run();
    while(!this.BattleStarted()){

        player.sendTitle("Please Wait....", "", 1, 1, 1);
        player.setWalkSpeed(0);
    }
    if(this.BattleStarted()){
        player.setWalkSpeed(0.2F);
this.removeFromRespawn(player);
        for(int y=volume.getMaxY(); y<=4; y++){
            for(int x=volume.getMinX(); x<=volume.getMaxX(); x++){

                new Location(player.getWorld(), x, y, volume.getMaxZ()+1).getBlock().setType(Material.AIR);
                new Location(player.getWorld(), x, y, volume.getMinZ()-1).getBlock().setType(Material.AIR);

            }
            for(int z=volume.getMinZ(); z<=volume.getMaxZ(); z++){
                new Location(player.getWorld(), volume.getMaxX()-1, y, z).getBlock().setType(Material.AIR);
                new Location(player.getWorld(), volume.getMinX()-1, y, z).getBlock().setType(Material.AIR);


            }
        }
player.sendTitle(ChatColor.GREEN+"FIGHT!", "", 1, 20, 1);

    }




}
}
