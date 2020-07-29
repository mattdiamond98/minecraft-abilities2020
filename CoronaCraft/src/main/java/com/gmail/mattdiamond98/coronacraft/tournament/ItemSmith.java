package com.gmail.mattdiamond98.coronacraft.tournament;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ItemSmith extends ItemStack implements Listener {
    private static CoronaCraft plugin=CoronaCraft.instance;
    Material[] sfa=null;
    BukkitRunnable a=null;
    Action stuff=Action.RIGHT_CLICK_BLOCK;
    int extradamage=0;
    public Material news=null;
    public ItemSmith(){}

    public ItemSmith(Material m, String name, String desc, Boolean enchants, Boolean Attributes, int extradamaget, int amount, BukkitRunnable b){


        this.setAmount(amount);
        extradamage=extradamaget;
        a=b;


        news=m;
        this.setType(m);

        ItemMeta im= this.getItemMeta();
        im.setDisplayName(ChatColor.RESET+name);
        ArrayList<String> lore= new ArrayList<String>();

        lore.add(desc);
        im.setLore(lore);
        PluginManager pm= Bukkit.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
        if(!Attributes){
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);}
        if(!enchants){
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);}
        this.setItemMeta(im);



    }
    public ItemSmith(Material m, String name, String desc, Boolean enchants, Boolean Attributes, int extradamaget, int amount, BukkitRunnable b, Action Stuff){
        stuff=Stuff;
    /*String path="./ResourcePack"+name;
    File file=new File(path);
    try {
		PrintWriter print=new PrintWriter(path+"/pack.mcmeta");
		print.println("{\n\"pack\": {\n\"pack_format\":4,\n\"pack_description\":\"\"\n}\n}");
		print.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    File file2=new File(path+"/assets");
    Boolean bool=file.mkdir();
    Boolean bool2=file2.mkdir();
    File file3=new File(path+"/assets"+"/minecraft");
    File file4=new File(path+"/assets"+"/minecraft"+"/models");
    File file5=new File(path+"/assets"+"/minecraft"+"/textures");
    Boolean bool3=file3.mkdir();
    Boolean bool4=file4.mkdir();
    Boolean bool5=file5.mkdir();*/
        this.setAmount(amount);
        extradamage=extradamaget;
        a=b;

        news=m;
        this.setType(m);

        ItemMeta im= this.getItemMeta();
        im.setDisplayName(ChatColor.RESET+name);
        ArrayList<String> lore= new ArrayList<String>();
        lore.add(desc);
        im.setLore(lore);
        PluginManager pm=Bukkit.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
        if(!Attributes){
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);}
        if(!enchants){
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);}
        this.setItemMeta(im);



    }
    public ItemSmith(Material m, String name, String[] desc, Boolean enchants, Boolean Attributes, int extradamaget, int amount, BukkitRunnable b){
    /*String path="./ResourcePack"+name;
    File file=new File(path);
    try {
		PrintWriter print=new PrintWriter(path+"/pack.mcmeta");
		print.println("{\n\"pack\": {\n\"pack_format\":4,\n\"pack_description\":\"\"\n}\n}");
		print.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    File file2=new File(path+"/assets");
    Boolean bool=file.mkdir();
    Boolean bool2=file2.mkdir();
    File file3=new File(path+"/assets"+"/minecraft");
    File file4=new File(path+"/assets"+"/minecraft"+"/models");
    File file5=new File(path+"/assets"+"/minecraft"+"/textures");
    Boolean bool3=file3.mkdir();
    Boolean bool4=file4.mkdir();
    Boolean bool5=file5.mkdir();*/
        this.setAmount(amount);
        extradamage=extradamaget;
        a=b;

        news=m;
        this.setType(m);

        ItemMeta im= this.getItemMeta();
        im.setDisplayName(ChatColor.RESET+name);
        ArrayList<String> lore= new ArrayList<String>();
        for (int i=0; i<=desc.length-1; i++){
            lore.add(desc[i]);}
        im.setLore(lore);
        PluginManager pm=Bukkit.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
        if(!Attributes){
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);}
        if(!enchants){
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);}
        this.setItemMeta(im);



    }
    public ItemSmith(Material m, String name, String[] desc, Boolean enchants, Boolean Attributes, int extradamaget, int amount, BukkitRunnable b, Material[] applicableon){
    /*String path="./ResourcePack"+name;
    File file=new File(path);
    try {
		PrintWriter print=new PrintWriter(path+"/pack.mcmeta");
		print.println("{\n\"pack\": {\n\"pack_format\":4,\n\"pack_description\":\"\"\n}\n}");
		print.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    File file2=new File(path+"/assets");
    Boolean bool=file.mkdir();
    Boolean bool2=file2.mkdir();
    File file3=new File(path+"/assets"+"/minecraft");
    File file4=new File(path+"/assets"+"/minecraft"+"/models");
    File file5=new File(path+"/assets"+"/minecraft"+"/textures");
    Boolean bool3=file3.mkdir();
    Boolean bool4=file4.mkdir();
    Boolean bool5=file5.mkdir();*/
        this.setAmount(amount);
        extradamage=extradamaget;
        a=b;

        news=m;
        this.setType(m);

        ItemMeta im= this.getItemMeta();
        im.setDisplayName(ChatColor.RESET+name);
        ArrayList<String> lore= new ArrayList<String>();
        for (int i=0; i<=desc.length-1; i++){
            lore.add(desc[i]);}
        im.setLore(lore);
        PluginManager pm=Bukkit.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
        if(!Attributes){
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);}
        if(!enchants){
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);}
        this.setItemMeta(im);



    }
    @EventHandler
    public void OnPlayerInteractBlock(PlayerInteractEvent e){
        Action action=e.getAction();
        Player player=e.getPlayer();



        if(this.isSimilar(player.getInventory().getItemInMainHand())&& ((action==Action.RIGHT_CLICK_AIR||action==Action.RIGHT_CLICK_BLOCK))&&a!=null&&sfa==null){
            a.run();

        }else if(this.isSimilar(player.getInventory().getItemInMainHand())&& ((action==Action.RIGHT_CLICK_AIR||action==Action.RIGHT_CLICK_BLOCK)&&a!=null)){

            for (int i=0; i<=sfa.length-1; i++){
                if(e.getClickedBlock().getType()==sfa[i]){
                    a.run();
                }

            }
        }
    }
    public Material getMaterial(){
        return news;

    }


}
