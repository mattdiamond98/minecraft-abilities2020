package com.gmail.mattdiamond98.coronacraft.tournament;

import com.tommytony.war.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TournamentCommand {

    public void onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender instanceof ConsoleCommandSender||sender instanceof Player){
            String lowerCmd= cmd.getName().toLowerCase();
            switch(lowerCmd) {
                case "starttourney":
                    if(sender.isOp()){
                        if(args.length>=1){
                            Tournament.getTournamentByName(args[0]).StartTournament();

                        }

                    }
                case "startround":
                    if(sender.isOp()){
                        if(args.length>=1){
                            Tournament.getTournamentByName(args[0]).SetStart();

                        }

                    }



            }

        }



    }
}
