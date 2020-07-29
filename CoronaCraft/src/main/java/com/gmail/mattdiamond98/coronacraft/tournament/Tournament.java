package com.gmail.mattdiamond98.coronacraft.tournament;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.mysql.fabric.xmlrpc.base.Array;
import com.sun.jnlp.FileOpenServiceNSBImpl;
import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarBattleWinEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.text.EditorKit;
import java.io.File;
import java.util.*;

public class Tournament implements Listener {
   public Inventory GUI= Bukkit.createInventory(null, 9, "Teleport to Battle");
    TournamentLobby lobby;
    TournamentWarzone[] warzones;
    List<Team> teams;
    public int gamesperbattle=1;
    public int totalrounds=0;
    HashMap<Team[], TournamentWarzone> Bracket;
    ArrayList<HashMap<Team[], TournamentWarzone>> RoundBrackets=new ArrayList<HashMap<Team[], TournamentWarzone>>();
    public ItemStack rulebook;
    public boolean started;
    public int round=0;
    public boolean setstart=false;
    public HashMap<TournamentWarzone, Team> FinishedBattles=new HashMap<TournamentWarzone, Team>();
    public String Id;
    public ItemStack navigator=new ItemSmith(Material.COMPASS, "Navigator", "Teleport to different matches", false, false, 0, 1, null);
    public static File datafolder= CoronaCraft.instance.getDataFolder();

    public static HashMap<String, Tournament> tourneys;

    public List<Team> getTeams() {
        return teams;
    }
    public void SetStart(){
        setstart=true;
    }
    public void SetBracket(HashMap<Team[], TournamentWarzone> bracket){

        this.Bracket=bracket;
        Team[][] teasdf = new Team[2][bracket.keySet().size()];
        Team[][] teamso=Arrays.asList(bracket.keySet()).toArray(teasdf);
        teams.clear();
        for(Team[] teamies:teamso){
        for(Team teim:teamies){
            teams.add(teim);
        }

        }
    }
    public void OnWarBattleWIn(WarBattleWinEvent e){
        if(e.getZone() instanceof TournamentWarzone){
            FinishedBattles.put(((TournamentWarzone) e.getZone()), e.getWinningTeams().get(0));

        }

    }
    public static Tournament getTournamentByName(String name){
        File f=  new File(datafolder, "Tournaments.dat");
        tourneys= (HashMap<String, Tournament>) FileUtil.load(f);
        if(tourneys==null){

            return null;
        }
        return tourneys.get(name);


    }
    public boolean SaveTourney(){
      File f=  new File(datafolder, "Tournaments.dat");
    tourneys= (HashMap<String, Tournament>) FileUtil.load(f);
    File folder=new File(datafolder.getPath()+"/Tournaments");
    if(!folder.exists()){
        if(!folder.mkdir()){
            System.out.println("Failed to create tournament folder");
            return false;

        }
    }
    if(tourneys==null){
        tourneys=new HashMap<String, Tournament>();

    }
    tourneys.put(Id, this);
    f=FileUtil.clear(f);
    FileUtil.save(tourneys, f);
    return true;



    }


    public boolean isStarted() {
        return started;
    }
    public void StartTournament(){
        started=true;
        for(int i=0; i<=this.totalrounds; i++){
         if(RoundBrackets.size()<i){
             ArrangeTeams();
             RoundBrackets.add(this.Bracket);
         }else{

             this.SetBracket(RoundBrackets.get(i));
         }
        this.StartRound(RoundBrackets.get(i));}

    }

    public ArrayList<Team> StartRound(HashMap<Team[], TournamentWarzone> bracket){
        ArrayList<Team> returnvalue=new ArrayList<Team>();
        int i=0;
        HashMap<TournamentWarzone, ArrayList<Team>> Winningteams=new HashMap<TournamentWarzone, ArrayList<Team>>();
        ArrayList<TournamentWarzone> waryzones=new ArrayList<TournamentWarzone>();
        for(Team[] teame:Bracket.keySet()){
            if(teame.length==2){
                for(Player p:teame[0].getPlayers()){
                    BattleSpectator.setPlayerNotSpectator(p);
                    p.teleport(Bracket.get(teame).getTeleport());
                }
                for(Player p:teame[1].getPlayers()){
                    BattleSpectator.setPlayerNotSpectator(p);
                    p.teleport(Bracket.get(teame).getTeleport());
                }
                waryzones.add(Bracket.get(teame));
                Winningteams.put(Bracket.get(teame), new ArrayList<Team>());
            }else{
                for(Player p:teame[0].getPlayers()){
                    BattleSpectator.setPlayerSpectator(p);
                    p.getInventory().addItem(navigator);
                }

            }

        }
        while(!this.setstart){

        }
        this.setstart=false;
        for(TournamentWarzone zoneman:waryzones){
            zoneman.StartBattle();

        }
        Boolean hihihih=false;
        while(!hihihih){
        Boolean bollean=false;
        Team toller=null;
        TournamentWarzone zonnnnnne = null;
            while (!bollean){
                for (TournamentWarzone zonewoman:waryzones){
                bollean=this.FinishedBattles.keySet().contains(zonewoman);
                if(bollean){
                    toller=this.FinishedBattles.get(zonewoman);
                    zonnnnnne=zonewoman;
                }
                }

            }
            ArrayList<Team> HHAHHAHAHAHA=Winningteams.get(zonnnnnne);
        HHAHHAHAHAHA.add(toller);
            Winningteams.remove(zonnnnnne);
            Winningteams.put(zonnnnnne, HHAHHAHAHAHA);
            Team[] teame={};
            if(HHAHHAHAHAHA.size()<gamesperbattle){
                for(Team[] EDDDDD: Bracket.keySet()){
                    if(Bracket.get(EDDDDD).equals(zonnnnnne)){
                        teame=EDDDDD;
                    }
                }
                for(Player p:teame[0].getPlayers()){
                    p.teleport(Bracket.get(teame).getTeleport());
                }
                for(Player p:teame[1].getPlayers()){
                    p.teleport(Bracket.get(teame).getTeleport());
                }
                zonnnnnne.StartBattle();

            }else{

                if(HHAHHAHAHAHA.size()<gamesperbattle){
                    for(Team[] EDDDDD: Bracket.keySet()){
                        if(Bracket.get(EDDDDD).equals(zonnnnnne)){
                            teame=EDDDDD;
                        }
                    }}
                int team1value=0;
                int team2value=0;
                for(Team t:Winningteams.get(zonnnnnne)){
                    if(t.equals(teame[0])){
                        team1value++;
                    }else if(t.equals(teame[1])){
                        team2value++;
                    }
                }
                if(team1value>team2value){
                    returnvalue.add(teame[0]);
                    teams.remove(teame[1]);

                }else if(team2value>team1value){
                    returnvalue.add(teame[1]);
                    teams.remove(teame[0]);
                }

                if(returnvalue.size()>=Bracket.keySet().size()-(teams.size()%2)){
                    hihihih=true;
                }else{
                    for(Team t:teame){
                        for(Player p:t.getPlayers()){
                            BattleSpectator.setPlayerSpectator(p);
                        }
                    }

                }
            }


        }

return returnvalue;
    }
    public Tournament(List<Team> teamsy, TournamentWarzone[] warzonesy, TournamentLobby lobbyy, ItemStack book, String idy, @Nullable HashMap<Team[], TournamentWarzone> bracket, @Nullable ArrayList<HashMap<Team[], TournamentWarzone>> roundbrackets, int gamesperbattled, int roundys){
        if(gamesperbattled!=0){this.gamesperbattle=gamesperbattled;}
        this.rulebook=book;
        this.teams=teamsy;
        this.warzones=warzonesy;

        this.lobby=lobbyy;
        for(TournamentWarzone hi:warzonesy){
            hi.setLobby(lobby);

        }
        this.Id=idy;
        if(bracket==null){
        ArrangeTeams();}
        else {
        this.SetBracket(bracket);
            }
        if(roundys!=0){this.totalrounds=roundys;}else{roundys=teams.size() == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(teams.size() - 1);}
        if(roundbrackets==null){RoundBrackets.add(this.Bracket);}else{this.RoundBrackets=roundbrackets; this.totalrounds=roundbrackets.size();}
        Warzone zone=warzonesy[0];

    }

    public void OpenTourneyGUI(Player player){
    GUI.setItem(0, new ItemStack(Material.AIR));
    if(!Bracket.isEmpty()){
        String newstring="Teleport to match: "+teams.get(0).getName()+" vs. "+teams.get(1).getName();
        GUI.setItem(1, new ItemSmith(Material.ENDER_EYE, newstring, "",  false, false, 0, 1, null));

    }else{
        GUI.setItem(1, new ItemStack(Material.AIR));
    }
    GUI.setItem(2, new ItemStack(Material.AIR));
        if(teams.size()>=2){
            String newstring="Teleport to match: "+teams.get(2).getName()+" vs. "+teams.get(3).getName();
            GUI.setItem(3, new ItemSmith(Material.ENDER_EYE, newstring, "",  false, false, 0, 1, null));

        }else{
            GUI.setItem(3, new ItemStack(Material.AIR));
        }
        GUI.setItem(4, new ItemStack(Material.AIR));
        if(teams.size()>=4){
            String newstring="Teleport to match: "+teams.get(4).getName()+" vs. "+teams.get(5).getName();
            GUI.setItem(5, new ItemSmith(Material.ENDER_EYE, newstring, "",  false, false, 0, 1, null));

        }else{
            GUI.setItem(5, new ItemStack(Material.AIR));
        }
        GUI.setItem(6, new ItemStack(Material.AIR));
        if(teams.size()>=6){
            String newstring="Teleport to match: "+teams.get(6).getName()+" vs. "+teams.get(7).getName();
            GUI.setItem(5, new ItemSmith(Material.ENDER_EYE, newstring, "",  false, false, 0, 1, null));

        }else{
            GUI.setItem(5, new ItemStack(Material.AIR));
        }
        player.openInventory(this.GUI);

    }

    @EventHandler
    public void OnPlayerMoveEvent(PlayerMoveEvent e){
        if(e.getTo().getBlock().getType()== Material.NETHER_PORTAL&&e.getTo().subtract(0, 1, 0).getBlock().getType()!=Material.OBSIDIAN){
     TeleporttoColloseum(e.getPlayer());

        }

    }
    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent e){
        if(e.getItem().isSimilar(this.navigator)){
            OpenTourneyGUI(e.getPlayer());

        }


    }
    public void TeleporttoColloseum(Player p) {
        p.openBook(this.rulebook);



        boolean condition = false;
        if (condition) {


        } else {
            BattleSpectator.setPlayerSpectator(p);
            p.getInventory().addItem(navigator);
        }


    }

    public void ArrangeTeams() {


        Collections.shuffle(teams);

        Team bye = null;
        if (teams.size() % 2 != 0) {
            bye = teams.get(0);
            teams.remove(bye);

        }
        Bracket.clear();
        int i = 0;
        for (Team team : teams) {
            Bracket.put(new Team[]{teams.get(i), teams.get(i + 1)}, warzones[i/2]);
            i += 2;
            if(i+1==teams.size()-1){
                break;

            }

        }
        if(bye!=null){
           Bracket.put(new Team[]{bye}, null);
            teams.add(bye);
        }


    }
}