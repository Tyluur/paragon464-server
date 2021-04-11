package com.paragon464.gameserver.model.area;

import com.paragon464.gameserver.Config;
import com.paragon464.gameserver.cache.Cache;
import com.paragon464.gameserver.cache.CacheFileManager;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.godwars.GodWars;
import com.paragon464.gameserver.model.region.Position;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 */
public class AreaHandler {

    public static void handleAreas(Mob mob) {
        Position loc = mob.getPosition();
        int currentLevel = mob.getWildLevel();
        if (Areas.inWilderness(loc)) {
            if (currentLevel != mob.getLastWildLevel()) {
                if (currentLevel > 0) {
                    mob.setLastwildLevel(currentLevel);
                }
            }
            if (!mob.getAttributes().isSet("wildy")) {
                mob.getAttributes().set("wildy", true);
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    player.getInterfaceSettings().openOverlay(381);
                }
            }
        } else {
            if (mob.getAttributes().isSet("wildy")) {
                mob.getAttributes().remove("wildy");
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    player.getInterfaceSettings().closeOverlay();
                }
            }
        }
        if (Areas.inAttackableArea(loc)) {
            if (!mob.getAttributes().isSet("attackablearea")) {
                mob.getAttributes().set("attackablearea", true);
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    player.getFrames().sendPlayerOption(mob.getAttributes().isSet("duelArea") ? "Fight" : "Attack",
                        1, mob.getAttributes().isSet("duelArea"));
                }
            }
        } else {
            if (!Areas.inWilderness(loc)) {
                if (mob.getAttributes().isSet("attackablearea")) {
                    mob.getAttributes().remove("attackablearea");
                    if (!mob.getAttributes().isSet("challengearea")) {
                        if (mob.isPlayer()) {
                            Player player = (Player) mob;
                            player.getFrames().sendPlayerOption("null", 1, mob.getAttributes().isSet("duelArea"));
                        }
                    } else if (mob.getAttributes().isSet("challengearea")) {
                        if (mob.isPlayer()) {
                            Player player = (Player) mob;
                            player.getFrames().sendPlayerOption("Challenge", 1, false);
                        }
                    }
                }
            } else if (mob.getAttributes().isSet("attackablearea")) {
                mob.getAttributes().remove("attackablearea");
                if (!mob.getAttributes().isSet("challengearea")) {
                    if (mob.isPlayer()) {
                        Player player = (Player) mob;
                        player.getFrames().sendPlayerOption("null", 1, mob.getAttributes().isSet("duelArea"));
                    }
                } else if (mob.getAttributes().isSet("challengearea")) {
                    if (mob.isPlayer()) {
                        Player player = (Player) mob;
                        player.getFrames().sendPlayerOption("Challenge", 1, false);
                    }
                }
            }
        }
        if (Areas.inChallengeArea(loc)) {
            if (!mob.getAttributes().isSet("challengearea")) {
                mob.getAttributes().set("challengearea", true);
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    player.getFrames().sendPlayerOption("Challenge", 1, false);
                }
            }
        } else {
            if (mob.getAttributes().isSet("challengearea")) {
                mob.getAttributes().remove("challengearea");
                if (!mob.getAttributes().isSet("attackablearea")) {
                    if (mob.isPlayer()) {
                        Player player = (Player) mob;
                        player.getFrames().sendPlayerOption("null", 1, false);
                    }
                } else if (mob.getAttributes().isSet("attackablearea")) {
                    if (mob.isPlayer()) {
                        Player player = (Player) mob;
                        player.getFrames().sendPlayerOption("Attack", 1, false);
                    }
                }
            }
        }
        if (Areas.atDuelArena(loc)) {
            if (!mob.getAttributes().isSet("duelArea")) {
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    player.getInterfaceSettings().openOverlay(105);
                }
                mob.getAttributes().set("duelArea", true);
            }
        } else if (!Areas.atDuelArena(loc)) {
            if (mob.getAttributes().isSet("duelArea")) {
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    player.getInterfaceSettings().closeOverlay();
                }
                mob.getAttributes().remove("duelArea");
            }
        }
        boolean multiAttribs = (mob.getAttributes().isSet("force_multi"));
        boolean multi = (Areas.isInMultiZone(mob, loc) || multiAttribs);
        if (multi) {
            if (!mob.getAttributes().isSet("multi")) {
                mob.getAttributes().set("multi", true);
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    boolean showMulti = !(mob.getAttributes().isSet("caves_session"));
                    if (showMulti) {
                        player.getFrames().sendInterfaceVisibility(player.getSettings().getWindowScreen(), player.getSettings().isInResizable() ? 90 : 65, true);
                    }
                }
            }
        } else {
            if (mob.getAttributes().isSet("multi")) {
                mob.getAttributes().remove("multi");
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    player.getFrames().sendInterfaceVisibility(player.getSettings().getWindowScreen(), player.getSettings().isInResizable() ? 90 : 65, false);
                }
            }
        }
        if (Areas.atGodwars(loc)) {
            if (!mob.getAttributes().isSet("god_wars")) {
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    GodWars.display(player);
                }
                mob.getAttributes().set("god_wars", true);
            }
        } else {
            if (mob.getAttributes().isSet("god_wars")) {
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    if (!mob.getAttributes().isSet("wildy")) {
                        player.getInterfaceSettings().closeOverlay();
                    }
                }
                mob.getAttributes().remove("god_wars");
            }
        }
        if (Areas.atBarrows(loc)) {
            if (!mob.getAttributes().isSet("barrows")) {
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    player.getInterfaceSettings().openOverlay(24);
                    player.getFrames().modifyText("Kill Count: " + player.getVariables().getCoffinSession().getKC(), 24, 0);
                }
                mob.getAttributes().set("barrows", true);
            }
        } else {
            if (mob.getAttributes().isSet("barrows")) {
                if (mob.isPlayer()) {
                    Player player = (Player) mob;
                    player.getInterfaceSettings().closeOverlay();
                }
                mob.getAttributes().remove("barrows");
            }
        }
    }

    public static void fixPlayer(final Player player) {
        int mapX = player.getPosition().getX() / 64, mapY = player.getPosition().getY() / 64;
        CacheFileManager manager = Cache.getCacheFileManagers()[5];
        int regionFileSize = manager.getFilesSize(manager.getContainerId("m" + mapX + "_" + mapY));
        // System.out.println("regionFileSize; "+regionFileSize +
        // ";"+mapX+";"+mapY);
        if (regionFileSize == -1) {
            player.setPosition(Config.RESPAWN_POSITION);
        } else {
            if (Areas.atWarriorsGuild(player.getPosition(), true)) {
                player.setLocation(2845, 3541, 2);
            }
        }
    }
}
