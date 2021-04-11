package com.paragon464.gameserver.model.content.skills.firemaking;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

/**
 * Handles the fire object
 *
 * @author Reece <valiw@hotmail.com>
 * @since Tuesday, November 17th. 2015.
 */
public class Fire extends GameObject {

    /**
     * The fire object's consturctor
     *
     * @param position the position the fire will be created at
     * @param objectId the fire's object identifier
     * @param type     the fire's object type
     * @param rotation the fire's rotation identifier
     */
    public Fire(Position position, int objectId, int type, int rotation) {
        super(position, objectId, type, rotation);
    }

    /**
     * Sends the fire object to the position made at
     *
     * @param owner the player the object blongs to
     * @param log
     */
    public static void start(Player owner, FireData log) {
        Position position = owner.getPosition();
        Region region = World.getRegion(position.getRegionId(), false);
        Fire fire = new Fire(position, log.getFireId(), 10, 0);
        region.addFire(fire);
        Fire.tick(owner, region, fire, log);
    }

    /**
     * Submits a new {@link Tickable} to handle the putting out of a fire
     *
     * @param region the region the fire exisits within
     * @param fire   the fire we are handling
     * @param log    the log that was used to create the fire type
     */
    private static void tick(Player player, Region region, Fire fire, FireData log) {
        int duration = log.getLife() + NumberUtils.random(3);
        World.spawnObjectTemporary(fire, duration);
        region.addFire(fire);
        World.getWorld().submit(new Tickable(duration) {
            @Override
            public void execute() {
                region.removeFire(fire);
                // create the ashes ground item for the player
                GroundItem ashes = new GroundItem(new Item(592), player, fire.getPosition());
                GroundItemManager.registerGroundItem(ashes);
                stop();
            }
        });
    }

    /**
     * Checks if the player meets the requirements to create a new {@link Fire}
     *
     * @param player the player being checked
     * @param log    the log type being checked
     * @return the outcome of the check
     */
    public static boolean canStart(Player player, FireData log) {
        if (log != null) {
            if (World.objectExists(player)) {
                player.getFrames().sendMessage("You can't light a fire here.");
                return false;
            }
            if (player.getSkills().getCurrentLevel(SkillType.FIREMAKING) < log.getLevel()) {
                player.getFrames().sendMessage("You need " + log.getLevel() + " Firemaking to light "
                    + ItemDefinition.forId(log.getLogId()).getName() + ".");
                return false;
            }
            return true;
        }
        return false;
    }
}
