package com.paragon464.gameserver.model.content.combat;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.content.minigames.MinigameHandler;
import com.paragon464.gameserver.tickable.Tickable;

public class PlayerDeath extends Tickable {

    private Player player;
    private Mob lastHitter;

    public PlayerDeath(final Player player, final Mob mob) {
        super(5);
        this.player = player;
        this.lastHitter = mob;
        player.resetActionAttributes();
        player.getCombatState().getHitQueue().clear();
    }

    @Override
    public void execute() {
        this.stop();
        if (!player.getCombatState().isDead()) {
            return;
        }
        player.playAnimation(65535, Animation.AnimationPriority.HIGH);
        player.resetVariables();
        player.getCombatState().getDamageMap().removeInvalidEntries();
        Mob killer = player.getCombatState().getDamageMap().highestDamage();
        boolean noKiller = (killer == null);
        if (noKiller) {
            killer = player;
        }
        boolean controllerDeath = !player.getControllerManager().processDeath();
        if (!controllerDeath) {
        	if (!noKiller) {
            	if (killer.isPlayer() && !killer.equals(player)) {
                    Player pKiller = (Player) killer;
                    pKiller.getFrames()
                        .sendMessage("" + player.getDetails().getName() + " regrets the day they met you in combat.");
                } else {
                    if (lastHitter != null && !lastHitter.equals(killer)) {
                        if (lastHitter.isPlayer() && killer.isPlayer()) {
                            Player lastAtker = (Player) this.lastHitter;
                            Player realKiller = (Player) killer;
                            lastAtker.getFrames().sendMessage("You killed " + player.getDetails().getName() + " but "
                                + realKiller.getDetails().getName() + " did more damage so they got the drop.");
                        }
                    }
                }
            }
            player.getFrames().sendMessage("Oh dear, you are dead!");
            if (killer.isPlayer() && Areas.inWilderness(player.getPosition())) {
                player.dropLoot(killer);
            } else {
                player.dropLoot(player);
            }
            player.teleport(player.getDeathArea());
        } else {
        	MinigameHandler.handleDeath(lastHitter, player);
        }
        player.getControllerManager().forceStop();
    }
}
