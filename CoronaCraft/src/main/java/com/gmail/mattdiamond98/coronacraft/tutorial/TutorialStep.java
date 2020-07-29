package com.gmail.mattdiamond98.coronacraft.tutorial;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TutorialStep {

    private String permission;
    private String title;
    private String subtitle;

    private List<TutorialStep> prev = new ArrayList<>();
    private List<TutorialStep> next = new ArrayList<>();

    public TutorialStep(String permission, String title, String subtitle) {
        this.permission = permission;
        this.title = title;
        this.subtitle = subtitle;
    }

    public void sendTitleTo(Player player) {
        player.sendTitle(ChatColor.GOLD + title, ChatColor.GREEN + subtitle, 20, 80, 20);
    }

    public boolean isCompleted(Player player) {
        return player.hasPermission(permission);
    }

    public void setCompleted(Player player, boolean value, boolean showNext) {
        if (!isCompletedAnyPrev(player)) return;
        CoronaCraft.getPermissions().playerAdd(player, permission);
        if (showNext) {
            TutorialStep nextStep = this.getNextIncomplete(player);
            if (nextStep != null) nextStep.sendTitleTo(player);
        }
    }

    public void setCompleted(Player player, boolean value) {
        setCompleted(player, value, true);
    }

    public void setCompleted(Player player) {
        setCompleted(player, true);
    }

    public boolean hasPrev() {
        return !prev.isEmpty();
    }

    public boolean hasNext() {
        return !next.isEmpty();
    }

    public boolean isCompletedAllPrev(Player player) {
        if (prev.size() == 0) return true;
        return prev.stream().allMatch(step -> step.isCompleted(player));
    }

    public boolean isCompletedAnyPrev(Player player) {
        if (prev.size() == 0) return true;
        return prev.stream().anyMatch(step -> step.isCompleted(player));
    }

    public void linkTo(TutorialStep nextStep) {
        this.next.add(nextStep);
        nextStep.prev.add(this);
    }

    public String getPermission() {
        return permission;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public List<TutorialStep> getPrev() {
        return prev;
    }

    public List<TutorialStep> getNext() {
        return next;
    }

    @Override
    public String toString() {
        return permission + ": " + next.toString();
    }

    /***
     * @return the next incomplete step in the graph.
     * Finds based on depth first search
     */
    public @Nullable TutorialStep getNextIncomplete(Player player) {
        if (!this.isCompleted(player)) return this;
        if (!hasNext()) return null;
        for (TutorialStep step : next) {
            TutorialStep nextIncomplete = step.getNextIncomplete(player);
            if (nextIncomplete != null) return nextIncomplete;
        }
        return null;
    }
}
