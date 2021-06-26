package com.gmail.mattdiamond98.coronacraft.cosmetics;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.UltimateActivateEvent;
import com.gmail.mattdiamond98.coronacraft.tournament.ItemSmith;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerDeathEvent;
import com.tommytony.war.event.WarScoreCapEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


public class CosmeticManager implements Listener {
    //Permission header declarations
    public static String killEffectPermHeader="coronacraft.killeffects.";
    public static String gameWinEffectPermHeader="coronacraft.gamewineffects.";
    public static String gadgetPermHeader="coronacraft.gadgets.";
    //GUI variable declarations
    public static Inventory killEffectGUI;
    public static Inventory cosmeticGUI;
    public static Inventory gadgetGUI;
    //Active Cosmetic Hashmap Declarations
    public static HashMap<UUID, KillEffect> playerKillEffects=new HashMap<UUID, KillEffect>();
    public static HashMap<UUID, Gadget> playerGadgets=new HashMap<UUID, Gadget>();
    public static HashMap<UUID, GameWinEffect> playerGameWinEffects =new HashMap<UUID, GameWinEffect>();
    public static HashMap<UUID, UltimateActivateEffect> playerUltActivateEffects= new HashMap<UUID, UltimateActivateEffect>();
    //Other variables
    public static Integer cooldownSeconds=5;
    public static ArrayList<UUID> GadgetCooldowns=new ArrayList<UUID>();
    //Listeners go here
    @EventHandler
    public void OnPlayerDeathEvent(WarPlayerDeathEvent e){
        if(e.getKiller() instanceof Player && Warzone.getZoneByLocation((Player)e.getKiller())!=null && Warzone.getZoneByLocation((Player)e.getKiller())==Warzone.getZoneByLocation(e.getVictim())&&getPlayerKillEffect((Player)e.getKiller())!=null){
        getPlayerKillEffect((Player) e.getKiller()).runEffect(e.getVictim().getLocation());

        }
    }
    @EventHandler
    public void OnPlayerInteractGadget(PlayerInteractEvent e){
            if(e.hasItem()&&e.getItem().getItemMeta().hasDisplayName()&&e.getItem().getItemMeta().getDisplayName().contains("Gadget")&&!GadgetCooldowns.contains(e.getPlayer().getUniqueId())){

            getGadgetByItem(e.getItem()).runEffect(e.getPlayer());
            GadgetCooldowns.add(e.getPlayer().getUniqueId());
                BukkitRunnable br=new BukkitRunnable() {
                    @Override
                    public void run() {
                        GadgetCooldowns.remove(e.getPlayer().getUniqueId());
                    }
                };
                br.runTaskLater(CoronaCraft.instance, 20*cooldownSeconds);
            }
    }

    @EventHandler
    public void OnUltimateActivate(UltimateActivateEvent e){
        if(Warzone.getZoneByLocation(e.getPlayer())!=null &&getPlayerUltActivateEffect(e.getPlayer())!=null){
            getPlayerUltActivateEffect(e.getPlayer()).runEffect(e.getPlayer());
        }
    }

    @EventHandler
    public void OnGameWin(WarScoreCapEvent e){
       Random r=new Random();


    }
    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e){
        if(getAllPlayerKillEffects(e.getPlayer()).size()!=0&&getPlayerKillEffect(e.getPlayer())==null){
            setPlayerKillEffect(e.getPlayer(), getAllPlayerKillEffects(e.getPlayer()).get(0));
        }else if(e.getPlayer().getName().toLowerCase().contains("bucketofjava")&&e.getPlayer().hasPermission("war.admin")&&hasKillEffect(e.getPlayer(), new LightningKillEffect())){
            //givePlayerKillEffect(e.getPlayer(), new LightningKillEffect());
            setPlayerKillEffect(e.getPlayer(), new LightningKillEffect());

        }
        if(getAllPlayerGameWinEffects(e.getPlayer()).size()!=0&&getPlayerGameWinEffect(e.getPlayer())==null){
            setPlayerGameWinEffect(e.getPlayer(), getAllPlayerGameWinEffects(e.getPlayer()).get(0));
        }


    }

    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent e){
        if(e.getPlayer().getName().toLowerCase().contains("bucketofjava")&&e.getPlayer().hasPermission("war.admin")&&e.getItem()!=null&&e.getItem().getType()==Material.HEAVY_WEIGHTED_PRESSURE_PLATE){
            OpenKillEffectSelectionWindow(e.getPlayer());
        }
    }
    @EventHandler
    public void OnPlayerInventoryClick(InventoryClickEvent e){
        if(e.getView().getTitle().contains("Kill Effect Selection")&&e.getWhoClicked()instanceof Player){
            if(e.getCurrentItem()!=null && !e.getCurrentItem().getType().equals(Material.GRAY_DYE)&&!e.getCurrentItem().getType().equals(Material.ARROW)){
                setPlayerKillEffect(((Player)e.getWhoClicked()), getKillEffectByPermission(killEffectPermHeader+ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).toLowerCase().replaceAll(" ", "")));
                e.getView().getTopInventory().setItem(e.getSlot(), new ItemSmith(e.getCurrentItem().getType(), e.getCurrentItem().getItemMeta().getDisplayName(), new String[]{ChatColor.GREEN+"Selected!"}));
                AbilityUtil.addGlow(e.getCurrentItem());
            }else if(e.getCurrentItem()!=null&&e.getCurrentItem().getType().equals(Material.ARROW)){
                e.getView().close();
                OpenCosmeticWindow((Player)e.getWhoClicked());
            }e.setCancelled(true);
        }else if(e.getView().getTitle().contains("Cosmetic Selection")&&e.getWhoClicked()instanceof Player){
            if(e.getCurrentItem().getType().equals(Material.IRON_SWORD)){
                e.getView().close();
                OpenKillEffectSelectionWindow((Player)e.getWhoClicked());
            }else if(e.getCurrentItem().getType().equals(Material.IRON_PICKAXE)){
                e.getView().close();
                OpenGadgetSelectionWindow((Player)e.getWhoClicked());
            }

            e.setCancelled(true);
        }else if(e.getView().getTitle().contains("Gadget Selection")&&e.getWhoClicked()instanceof Player){
            if(e.getCurrentItem()!=null && !e.getCurrentItem().getType().equals(Material.GRAY_DYE)&&!e.getCurrentItem().getType().equals(Material.ARROW)){

                setPlayerGadget(((Player)e.getWhoClicked()), getGadgetByPermission(gadgetPermHeader+ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).toLowerCase().replaceAll(" ", "")));
                AbilityUtil.addGlow(e.getCurrentItem());
            }else if(e.getCurrentItem()!=null&&e.getCurrentItem().getType().equals(Material.ARROW)){
                e.getView().close();
                OpenCosmeticWindow((Player)e.getWhoClicked());
            }e.setCancelled(true);
        }
    }
    //Setting player cosmetics
    public static void setPlayerKillEffect(Player p, KillEffect k){

        if(playerKillEffects.containsKey(p.getUniqueId())){
        playerKillEffects.replace(p.getUniqueId(), k);}
        else{
            playerKillEffects.put(p.getUniqueId(), k);
        }
    }
    public static void setPlayerGadget(Player p, Gadget k){

        if(playerKillEffects.containsKey(p.getUniqueId())){
            playerGadgets.replace(p.getUniqueId(), k);}
        else{
            playerGadgets.put(p.getUniqueId(), k);
        }
        p.getInventory().setItem(1, new ItemSmith(k.getMaterial(), ChatColor.GREEN+k.getName()+" Gadget",new String[]{}));
    }
    public static void setPlayerUltActivateEffect(Player p, UltimateActivateEffect k){
        if(playerUltActivateEffects.containsKey(p.getUniqueId())){
            playerUltActivateEffects.replace(p.getUniqueId(), k);}
        else{
            playerUltActivateEffects.put(p.getUniqueId(), k);
        }
    }
    public static void setPlayerGameWinEffect(Player p, GameWinEffect k){
        if(playerUltActivateEffects.containsKey(p.getUniqueId())){
            playerGameWinEffects.replace(p.getUniqueId(), k);}
        else{
            playerGameWinEffects.put(p.getUniqueId(), k);
        }
    }

    //Get all of a type of cosmetic
    public static ArrayList<KillEffect> getAllPlayerKillEffects(Player p){
        ArrayList<KillEffect> returnvalue=new ArrayList<KillEffect>();
        for(PermissionAttachmentInfo pa : p.getEffectivePermissions()){
        for(KillEffect s:KillEffectType.ALL_EFFECTS){
            if(s.getName().equals(pa.getPermission())){
                returnvalue.add(s);
            }
        }

        }
        return returnvalue;

    }


    public static ArrayList<GameWinEffect> getAllPlayerGameWinEffects(Player p){
        ArrayList<GameWinEffect> returnvalue=new ArrayList<GameWinEffect>();
        for(PermissionAttachmentInfo pa : p.getEffectivePermissions()){
            for(GameWinEffect s:GameWinEffectType.ALL_EFFECTS){
                if(s.getName().equals(pa.getPermission())){
                    returnvalue.add(s);
                }
            }

        }
        return returnvalue;

    }
    public static ArrayList<Gadget> getAllGadgets(Player p){
        ArrayList<Gadget> returnvalue=new ArrayList<Gadget>();
        for(PermissionAttachmentInfo pa : p.getEffectivePermissions()){
            for(Gadget s:GadgetType.ALL_TYPES){
                if(s.getName().equals(pa.getPermission())){
                    returnvalue.add(s);
                }
            }

        }
        return returnvalue;

    }
    //Getting active cosmetics
    public static KillEffect getPlayerKillEffect(Player p){
        return playerKillEffects.getOrDefault(p.getUniqueId(), null);

    }
    public static GameWinEffect getPlayerGameWinEffect(Player p){
        return playerGameWinEffects.getOrDefault(p.getUniqueId(), null);

    }
    public static UltimateActivateEffect getPlayerUltActivateEffect(Player p){
        return playerUltActivateEffects.getOrDefault(p.getUniqueId(), null);

    }
    public static Gadget getPlayerGadget(Player p){
        return playerGadgets.getOrDefault(p.getUniqueId(), null);

    }
    //Deprecated Methods
    @Deprecated
    public static void givePlayerKillEffect(Player p, KillEffect k){
        p.addAttachment(CoronaCraft.instance).setPermission(killEffectPermHeader+k.getName().toLowerCase().replaceAll(" ", ""), true);
    }

    //Checking if player has a certain cosmetic
    public static boolean hasKillEffect(Player p, KillEffect k){
        return p.hasPermission(killEffectPermHeader+k.getName().toLowerCase().replaceAll(" ", ""));
    }
    public static boolean hasGameWinEffect(Player p, GameWinEffect k){
        return p.hasPermission(gameWinEffectPermHeader+k.getName().toLowerCase().replaceAll(" ", ""));
    }
    public static boolean hasGadget(Player p, Gadget k){
        return p.hasPermission(gadgetPermHeader+k.getName().toLowerCase().replaceAll(" ", ""));
    }
    //GUI opening functions
    public static void OpenCosmeticWindow(Player p){
        cosmeticGUI = Bukkit.createInventory(null, 54, "Cosmetic Selection");
        for (int i=0; i<54; i++){
            switch(i){
                case 10:
                  cosmeticGUI.setItem(i, new ItemSmith(Material.IRON_SWORD, ChatColor.RED+(ChatColor.BOLD+"Kill Effects"), new String[]{}));
                  break;
                case 16:
                  cosmeticGUI.setItem(i, new ItemSmith(Material.IRON_PICKAXE, ChatColor.GREEN+(ChatColor.BOLD+"Gadgets"), new String[]{}));
                  break;
                default:
                    cosmeticGUI.setItem(i, new ItemStack(Material.AIR));
                    break;
            }
        }
        p.openInventory(cosmeticGUI);

    }

    public static void OpenKillEffectSelectionWindow(Player p){
        killEffectGUI = Bukkit.createInventory(null, 54, "Kill Effect Selection");
    for (int i=0; i<54; i++){
    switch(i){
        case 10:
            if(hasKillEffect(p, new LightningKillEffect())&&getPlayerKillEffect(p)!=null&&getPlayerKillEffect(p).getName().toLowerCase().replaceAll(" ", "").contains("lightningstrike")){
            killEffectGUI.setItem(i, AbilityUtil.addGlow(new ItemSmith(Material.IRON_BARS, ChatColor.GREEN+(ChatColor.BOLD+"Lightning Strike"), new String[]{})));}
           else if(hasKillEffect(p, new LightningKillEffect())) {
            killEffectGUI.setItem(i, new ItemSmith(Material.IRON_BARS, ChatColor.GREEN+(ChatColor.BOLD+"Lightning Strike"), new String[]{}));

            }
            else{
                killEffectGUI.setItem(i, new ItemSmith(Material.GRAY_DYE, ChatColor.GREEN+(ChatColor.BOLD+"Lightning Strike"), new String[]{"Buy this for 1", "Corona Gold in the bazaar!"}));
            }
            break;
        case 12:
            if(hasKillEffect(p, new pumpkinKillEffect())&&getPlayerKillEffect(p)!=null&&getPlayerKillEffect(p).getName().toLowerCase().replaceAll(" ", "").contains("pumpkinrain")){
                killEffectGUI.setItem(i, AbilityUtil.addGlow(new ItemSmith(Material.CARVED_PUMPKIN, ChatColor.GREEN+(ChatColor.BOLD+"Pumpkin Rain"), new String[]{})));}
            else if(hasKillEffect(p, new pumpkinKillEffect())) {
                killEffectGUI.setItem(i, new ItemSmith(Material.CARVED_PUMPKIN, ChatColor.GREEN+(ChatColor.BOLD+"Pumpkin Rain"), new String[]{}));

            }
            else{
                killEffectGUI.setItem(i, new ItemSmith(Material.GRAY_DYE, ChatColor.GREEN+(ChatColor.BOLD+"Pumpkin Rain"), new String[]{"Buy this at the", "Halloween trader!"}));
            }
            break;
        case 14:
            if(hasKillEffect(p, new BatKillEffect())&&getPlayerKillEffect(p)!=null&&getPlayerKillEffect(p).getName().toLowerCase().replaceAll(" ", "").contains("spawnbat")){
                killEffectGUI.setItem(i, AbilityUtil.addGlow(new ItemSmith(Material.BAT_SPAWN_EGG, ChatColor.GREEN+(ChatColor.BOLD+"Bat Spawn"), new String[]{})));}
            else if(hasKillEffect(p, new BatKillEffect())) {
                killEffectGUI.setItem(i, new ItemSmith(Material.BAT_SPAWN_EGG, ChatColor.GREEN+(ChatColor.BOLD+"Bat Spawn"), new String[]{}));

            }
            else{
                killEffectGUI.setItem(i, new ItemSmith(Material.BAT_SPAWN_EGG, ChatColor.GREEN+(ChatColor.BOLD+"Bat Spawn"), new String[]{}));
            }
            break;

        case 49:
            killEffectGUI.setItem(i, new ItemSmith(Material.ARROW, ChatColor.GREEN+"Back", new String[]{}));
            break;
        default:
            killEffectGUI.setItem(i, new ItemStack(Material.AIR));
            break;
    }
    }
    p.openInventory(killEffectGUI);

    }
    public static void OpenGadgetSelectionWindow(Player p){
        gadgetGUI = Bukkit.createInventory(null, 54, "Gadget Selection");
        for (int i=0; i<54; i++){
            switch(i){
                case 10:
                    if(hasGadget(p, new PumpkinGun())&&getPlayerGadget(p)!=null&&getPlayerGadget(p).getName().toLowerCase().replaceAll(" ", "").contains("pumpkingun")){
                        gadgetGUI.setItem(i, AbilityUtil.addGlow(new ItemSmith(Material.IRON_HORSE_ARMOR, ChatColor.GREEN+(ChatColor.BOLD+"Pumpkin Gun"), new String[]{ChatColor.GREEN+"Selected!"})));}
                    else if(hasGadget(p, new PumpkinGun())) {
                        gadgetGUI.setItem(i, new ItemSmith(Material.IRON_HORSE_ARMOR, ChatColor.GREEN+(ChatColor.BOLD+"Pumpkin Gun"), new String[]{}));

                    }
                    else{
                        gadgetGUI.setItem(i, new ItemSmith(Material.GRAY_DYE, ChatColor.GREEN+(ChatColor.BOLD+"Pumpkin Gun"), new String[]{"Buy this for 1", "Corona Gold in the bazaar!"}));
                    }
                    break;
                case 12:
                    if(hasGadget(p, new PresentLauncher())&&getPlayerGadget(p)!=null&&getPlayerGadget(p).getName().toLowerCase().replaceAll(" ", "").contains("presentlauncher")){
                        gadgetGUI.setItem(i, AbilityUtil.addGlow(new ItemSmith(Material.DIAMOND_HORSE_ARMOR, ChatColor.GREEN+(ChatColor.BOLD+"Present Launcher"), new String[]{ChatColor.GREEN+"Selected!"})));}
                    else if(hasGadget(p, new PumpkinGun())) {
                        gadgetGUI.setItem(i, new ItemSmith(Material.DIAMOND_HORSE_ARMOR, ChatColor.GREEN+(ChatColor.BOLD+"Present Launcher"), new String[]{}));

                    }
                    else{
                        gadgetGUI.setItem(i, new ItemSmith(Material.GRAY_DYE, ChatColor.GREEN+(ChatColor.BOLD+"Present Launcher"), new String[]{"Buy this for 1", "Corona Gold in the bazaar!", "Or claim it from Santa!"}));
                    }
                    break;
                case 49:
                    gadgetGUI.setItem(i, new ItemSmith(Material.ARROW, ChatColor.GREEN+"Back", new String[]{}));
                    break;
                default:
                    gadgetGUI.setItem(i, new ItemStack(Material.AIR));
                    break;
            }
        }
        p.openInventory(gadgetGUI);

    }
    //Getting cosmetics by perms and other things
    public static KillEffect getKillEffectByPermission(String permission){
      if(permission.equals(killEffectPermHeader+"lightningstrike")){
          return new LightningKillEffect();
      }else if(permission.equals(killEffectPermHeader+"pumpkinrain")){
          return new pumpkinKillEffect();
      }      else if(permission.equals(killEffectPermHeader+"batspawn")){
        return new pumpkinKillEffect();
    }
      return null;

    }
    public static GameWinEffect getGameWinEffectByPermission(String permission){


        return null;

    }
    public static Gadget getGadgetByPermission(String permission){
        if(permission.equals(gadgetPermHeader+"pumpkingun")){
            return new PumpkinGun();
        } else if(permission.equals(gadgetPermHeader+"presentlauncher")){
            return new PresentLauncher();
        }

        return null;

    }
    public static Gadget getGadgetByItem(ItemStack is){
        if(is.getItemMeta().hasDisplayName()&&is.getItemMeta().getDisplayName().contains("Pumpkin Gun")&&is.getType().equals(Material.IRON_HORSE_ARMOR)){

            return new PumpkinGun();
        }else if(is.getItemMeta().hasDisplayName()&&is.getItemMeta().getDisplayName().contains("Present Launcher")&&is.getType().equals(Material.DIAMOND_HORSE_ARMOR)){

            return new PresentLauncher();
        }
        return null;
    }





}
