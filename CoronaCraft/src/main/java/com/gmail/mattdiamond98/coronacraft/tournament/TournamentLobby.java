package com.gmail.mattdiamond98.coronacraft.tournament;

import com.tommytony.war.Warzone;
import com.tommytony.war.structure.ZoneLobby;
import org.bukkit.block.BlockFace;

public class TournamentLobby extends ZoneLobby {
public Warzone scrimagearea;

    public TournamentLobby(Warzone warzone, BlockFace wall) {
        super(warzone, wall);
        this.scrimagearea=warzone;
    }

    public Warzone getScrimagearea() {
        return scrimagearea;
    }
}
