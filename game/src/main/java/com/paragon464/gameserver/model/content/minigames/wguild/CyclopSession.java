package com.paragon464.gameserver.model.content.minigames.wguild;

import com.google.common.collect.ObjectArrays;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.area.Areas;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Fernando Gavilanes <eastwicksnando@hotmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public class CyclopSession extends Tickable {

    private final Player player;
    private Defender type;

    public CyclopSession(Player player, Defender type) {
        super(player, 59);
        this.player = player;
        this.type = type;
        player.submitTickable(this);
    }

    public static Defender getDefender(final Player player) {
        final List<Item> items = new LinkedList<>(Arrays.asList(ObjectArrays.concat(player.getInventory().getItems(),
            player.getBank().getItems(), Item.class)));

        final Item shield = player.getEquipment().get(5);
        if (shield != null) {
            items.add(shield);
        }

        final Defender defender = items.stream().filter(it -> it != null && Defender.fromId(it.getId()).isPresent()).map(item -> Defender.fromId(item.getId()).get()).max(Comparator.comparing(Defender::getDefender)).orElse(null);

        final int index = defender == null ? 0 : defender.ordinal() + 1;
        final int size = Defender.CACHED_VALUES.size();

        if (index >= size) {
            return Defender.CACHED_VALUES.get(size - 1);
        } else {
            return Defender.CACHED_VALUES.get(index);
        }
    }

    public void handleDefenderDrops(NPC npc) {
        if ((NumberUtils.getRandomDouble(99) + 1) <= type.getRate() * 1.5) {
            GroundItemManager.registerGroundItem(new GroundItem(new Item(type.getDefender(), 1), player, npc.getPosition()));
        }
    }

    @Override
    public void execute() {
        if (this.getRemainingTicks() <= 0) {
            if (this.player.getInventory().hasItemAmount(8851, 10)) {
                this.player.getInventory().deleteItem(8851, 10);
                this.player.getInventory().refresh();
            } else {
                end(true);
            }
        }
    }

    public void end(boolean noTokens) {
        final Mob target = player.getCombatState().getTarget();
        if (target != null) {
            target.getCombatState().end(1);
        }

        if (Areas.atWarriorsGuild(player.getPosition(), true)) {
            player.teleport(2845, 3541, 2);

            if (noTokens) {
                player.getFrames().sendMessage("You have run out of tokens.");
            }
        }

        player.getCombatState().end(1);
        player.getAttributes().remove("cyclop_session");
        this.stop();
    }
}
