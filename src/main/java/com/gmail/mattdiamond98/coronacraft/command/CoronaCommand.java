package com.gmail.mattdiamond98.coronacraft.command;

import com.gmail.mattdiamond98.coronacraft.tournament.ItemSmith;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;

public class CoronaCommand implements CommandExecutor {


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {

        }else if(sender instanceof Player){
            switch(cmd.toString().toLowerCase()){
                case "adventcalendar":
                    openAdventCalendar((Player) sender);
                    Bukkit.broadcastMessage("Opened!");
            }
        }
        return true;
    }
    public static void openAdventCalendar(Player p){
    HashMap<Integer, ItemStack> values=new HashMap<Integer, ItemStack>();
    LocalDate now=LocalDate.now(ZoneId.of("America/New_York"));
    int c=0;
    for (int i=now.getDayOfMonth(); i<26; i++){
        String[] desc;
        switch(i){
            case 6:
                desc=new String[]{"30 corona coins!"};
                break;
            case 7:
                desc=new String[]{"Random 10-70 corona coins!"};
                break;
            case 8:
                desc=new String[]{"1 corona gold!"};
                break;
            case 9:
                desc=new String[]{"Random multiple of 9 from", "9-99 corona coins!"};
                break;
            case 10:
                desc=new String[]{"50 corona coins and", "a special cosmetic!"};
                break;
            case 11:
                desc= new String[]{"1.1x corona coin booster for 1 hour!"};
                break;
            case 12:
                desc=new String[]{"Random one of 12 cosmetics!"};
                break;
            case 13:
                desc=new String[]{"Random 10-130 corona coins!"};
                break;
            case 14:
                desc=new String[]{"TBD"};
                break;
            case 15:
                desc=new String[]{"TBD"};
                break;
            case 16:
                desc=new String[]{"TBD"};
                break;
            case 17:
                desc=new String[]{"TBD"};
                break;
            case 18:
                desc=new String[]{"TBD"};
                break;
            case 19:
                desc=new String[]{"TBD"};
                break;
            case 20:
                desc=new String[]{"TBD"};
                break;
            case 21:
                desc=new String[]{"TBD"};
                break;
            case 22:
                desc=new String[]{"TBD"};
                break;
            case 23:
                desc=new String[]{"TBD"};
                break;
            case 24:
                desc=new String[]{"TBD"};
                break;
            case 25:
                desc=new String[]{"50 * the amount of days you've claimed", "in corona coins and 1 corona gold", "for each 3 days claimed", "and a Santa cosmetic pack"};


                break;
            default:
                desc=new String[]{};
                break;
        }
        if(!p.hasPermission("coronacraft.adventcalendar.claimed"+String.valueOf(i))){
        values.put(c, new ItemSmith("dca29a3a-76d3-4979-88a2-2da034b99212", ChatColor.YELLOW+""+ChatColor.RESET+"December "+String.valueOf(i), desc));}
        else{
        values.put(c, new ItemSmith(Material.RED_CONCRETE, ChatColor.YELLOW+""+ChatColor.RESET+"December "+String.valueOf(i), new String[]{ChatColor.GREEN+"Claimed!"}));

        }
        c++;
    }
    p.openInventory(generateGUI(values, Material.AIR, 54, ChatColor.RED+"Advent Calendar"));
    }

    public static Inventory generateGUI(HashMap<Integer, ItemStack> values, Material defaultMaterial, int size, String title){
        Inventory newGUI=Bukkit.createInventory(null, size, title);
        for(int i=0; i<size; i++){
            if(values.containsKey(i)){
                newGUI.setItem(i, values.get(i));
            }else{
                newGUI.setItem(i, new ItemStack(defaultMaterial, 1));
            }
        }
        return newGUI;

    }
}
