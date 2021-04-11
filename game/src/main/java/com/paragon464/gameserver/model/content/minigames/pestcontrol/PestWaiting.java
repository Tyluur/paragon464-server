package com.paragon464.gameserver.model.content.minigames.pestcontrol;

import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.tickable.Tickable;

import java.util.LinkedList;
import java.util.List;

public class
PestWaiting extends Tickable {

    private final static List<Player> players = new LinkedList<>();
    private static int timeLeft = 30;

    public PestWaiting() {
        super(1, true);
    }

    public static void enter(final Player player) {
        if (!players.contains(player)) {
            player.getAttributes().set("stopActions", true);
            player.teleport(2661, 2639, 0);
            player.getInterfaceSettings().openOverlay(133);
            player.getFrames().modifyText("", 133, 2);
            player.getFrames().modifyText("<col=52D017>Next Departure: " + timeLeft, 133, 0);
            player.getFrames().modifyText("<col=52D017>Points: " + player.getAttributes().getInt("pest_points"), 133,
                1);
            player.resetVariables();
            player.submitTickable(new Tickable(1) {
                @Override
                public void execute() {
                    if (Areas.inPestBoat(player.getPosition())) {
                        players.add(player);
                    } else {
                        player.getInterfaceSettings().closeOverlay();
                    }

                    player.getAttributes().remove("stopActions");
                    this.stop();
                }
            });
        }
    }

    public static void leave(final Player player) {
        if (players.contains(player)) {
            player.getAttributes().set("stopActions", true);
            player.teleport(2657, 2639, 0);
            players.remove(player);
            player.getInterfaceSettings().closeOverlay();
            player.submitTickable(new Tickable(1) {
                @Override
                public void execute() {
                    player.getAttributes().remove("stopActions");
                    this.stop();
                }
            });
        }
    }

    public static List<Player> getPlayers() {
        return players;
    }

    @Override
    public void execute() {
        for (Player boatPlayers : players) {
            if (boatPlayers != null) {
                boatPlayers.getFrames().modifyText("", 133, 2);
                boatPlayers.getFrames().modifyText("<col=52D017>Next Departure: " + timeLeft, 133, 0);
                boatPlayers.getFrames()
                    .modifyText("<col=52D017>Points: " + boatPlayers.getAttributes().getInt("pest_points"), 133, 1);
            }
        }
        if (timeLeft == 0) {
            if (players.size() > 0) {
                final List<Player> playersCopy = new LinkedList<>();
                playersCopy.addAll(players);
                players.clear();
                ZombieBattles session = new ZombieBattles(playersCopy);
                session.transferPlayers();
                session = null;
            }
            timeLeft = 30;
        } else if (timeLeft > 0) {
            timeLeft--;
        }
    }
}
