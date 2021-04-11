package com.paragon464.gameserver.model.content.minigames.duelarena;

import com.paragon464.gameserver.io.database.table.log.TradeTable;
import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.Trade;
import com.paragon464.gameserver.model.entity.mob.player.TradeType;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.item.grounditem.GroundItemManager;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;
import com.paragon464.gameserver.tickable.Tickable;
import com.paragon464.gameserver.util.NumberUtils;

import static com.paragon464.gameserver.model.item.EquipmentType.TWO_HANDED_WEAPON;

public class DuelBattle {

    private Player player, other;
    private Duel_State state;
    private boolean[] rules = null;
    private Item[] winnings;

    public DuelBattle(final Player player, final Player other, boolean[] rules) {
        this.player = player;
        this.other = other;
        this.state = Duel_State.COUNT_DOWN;
        this.rules = rules;
        this.setWinnings(other.getDuel().getItems());
        start();
    }

    public void setWinnings(Item[] items) {
        Item[] winnings = new Item[28];
        int j = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                winnings[j] = items[i];
                j++;
            }
        }
        this.winnings = winnings;
    }

    public void start() {
        boolean obstacle = (ruleEnabled(8));
        boolean noMovement = (ruleEnabled(1));
        Region region = player.getLastRegion();
        final Position playerPlacement = DuelArena.getRandomArenaPosition(region, obstacle, 0);
        player.teleport(playerPlacement);
        player.getFrames().sendHintArrow(other);
        player.getCombatState().end(1);
        player.resetVariables();
        if (noMovement) {
            player.getAttributes().set("stopMovement", true);
        }
        other.teleport(noMovement ? DuelArena.getNextToPlayerPosition(region, playerPlacement)
            : DuelArena.getRandomArenaPosition(region, obstacle, 0));
        other.getFrames().sendHintArrow(player);
        other.getCombatState().end(1);
        other.resetVariables();
        if (noMovement) {
            other.getAttributes().set("stopMovement", true);
        }
        removeWornItems();
        World.getWorld().submit(new Tickable(2) {
            int count = 3;

            @Override
            public void execute() {
                if (player.isDestroyed() || other.isDestroyed()) {
                    this.stop();
                    return;
                }
                if (player.getVariables().getDuelBattle() == null || other.getVariables().getDuelBattle() == null) {
                    this.stop();
                    return;
                }
                if (count > 0) {
                    other.playForcedChat("" + count);
                    player.playForcedChat("" + count);
                } else {
                    other.playForcedChat("Fight!");
                    player.playForcedChat("Fight!");
                    state = Duel_State.COMMENCE;
                    other.getVariables().getDuelBattle().setState(state);
                    this.stop();
                }
                count--;
            }
        });
    }

    public boolean ruleEnabled(int i) {
        return rules[i];
    }

    public void removeWornItems() {
        int[] slot = {2, 3, 4, 5, 7, 9, 10, 12, 13, 0, 1};
        int[] rule = {13, 14, 15, 16, 17, 18, 19, 20, 21, 11, 12};
        int weapon = player.getEquipment().getItemInSlot(3);
        ItemDefinition def = ItemDefinition.forId(weapon);
        boolean twoHanded = false;
        if (def != null) {
            twoHanded = def.equipmentDefinition.matchesEquipmentType(TWO_HANDED_WEAPON);
        }
        for (int j = 0; j < rule.length; j++) {
            if (rules[rule[j]]) {
                if (j == 20) {
                    continue;
                }
                if (ruleEnabled(14) && twoHanded) {
                    Equipment.unequipItem(player, -1, 3, false);
                    Equipment.unequipItem(other, -1, 3, false);
                }
                Equipment.unequipItem(player, -1, slot[j], false);
                Equipment.unequipItem(other, -1, slot[j], false);
            }
        }
    }

    public void setState(Duel_State state) {
        this.state = state;
    }

    public void logout() {
        end(false, true, true);
    }

    public boolean end(boolean killed, boolean logged, boolean forfeit) {
        this.state = Duel_State.ENDED;
        other.getVariables().getDuelBattle().setState(Duel_State.ENDED);

        boolean tied = other.getCombatState().isDead();
        finished(killed, forfeit, tied);
        if (logged) {
            player.setPosition(new Position(3360 + NumberUtils.random(19), 3274 + NumberUtils.random(3), 0));
        } else {
            player.teleport(3360 + NumberUtils.random(19), 3274 + NumberUtils.random(3), 0);
        }
        player.resetVariables();
        player.playAnimation(-1, AnimationPriority.HIGH);
        player.getFrames().sendHintArrow(null);
        player.getAttributes().remove("duel_battle");
        player.getAttributes().remove("stopMovement");
        player.getCombatState().end(1);

        other.teleport(3360 + NumberUtils.random(19), 3274 + NumberUtils.random(3), 0);
        other.resetVariables();
        other.playAnimation(-1, AnimationPriority.HIGH);
        other.getFrames().sendHintArrow(null);
        other.getAttributes().remove("duel_battle");
        other.getAttributes().remove("stopMovement");
        other.getCombatState().end(1);
        return true;
    }

    public void finished(boolean killed, boolean forfeit, boolean tied) {
        if (killed || forfeit) {
            if (forfeit) {
                player.getFrames().sendMessage("You forfeited the duel!");
                other.getFrames().sendMessage("Your opponent forfeited the duel!");
            } else if (killed) {
                player.getFrames().sendMessage("You lost the duel.");
                other.getFrames().sendMessage("You won the duel!");
            }

            other.getFrames().modifyText("" + player.getSkills().getCombatLevel(), 110, 104);
            other.getFrames().modifyText(player.getDetails().getName(), 110, 105);
            Item[] winnings = other.getVariables().getDuelBattle().winnings;
            other.getFrames().sendItems(110, 88, 93, winnings);
            other.getInterfaceSettings().openInterface(110);
            for (int i = 0; i < winnings.length; i++) {
                if (winnings[i] != null) {
                    if (other.getInventory().addItem(winnings[i])) {
                        winnings[i] = null;
                    } else {
                        GroundItemManager.registerGroundItem(new GroundItem(winnings[i], player));
                    }
                }
            }
            other.getInventory().refresh();
            if (player.getDuel().getTotalItems() > 0) {
                TradeTable.save(TradeType.STAKE, new Trade(player.getDuel().getItems(), winnings, player, other));
            }
            player.getDuel().clear();
            other.getVariables().getDuelBattle().returnItems();
        } else if (tied) {
            this.returnItems();
            player.getDuel().clear();
            player.getFrames().sendMessage("The duel ended in a tie.");
            other.getVariables().getDuelBattle().returnItems();
            other.getFrames().sendMessage("The duel ended in a tie.");
            other.getDuel().clear();
        }
    }

    public void returnItems() {
        for (Item item : player.getDuel().getItems()) {
            if (item != null) {
                if (player.getInventory().addItem(item)) {
                    player.getDuel().deleteItem(item);
                }
            }
        }
        player.getInventory().refresh();
    }

    public boolean ableToAttack() {
        CombatType combatType = player.getCombatState().getCombatType();
        if (combatType.equals(CombatType.MELEE)) {
            if (meleeDisabled()) {
                player.getCombatState().end(2);
                player.getFrames().sendMessage("Your melee attacks have been disabled this duel.");
                return false;
            }
        } else if (combatType.equals(CombatType.RANGED)) {
            if (rangeDisabled()) {
                player.getCombatState().end(2);
                player.getFrames().sendMessage("Your ranged attacks have been disabled this duel.");
                return false;
            }
        } else if (combatType.equals(CombatType.MAGIC)) {
            if (magicDisabled()) {
                player.getCombatState().end(2);
                player.getFrames().sendMessage("Your magic attacks have been disabled this duel.");
                return false;
            }
        }
        if (isCommencing()) {
            Player duel_other = getOther();
            if (duel_other.getDetails().getUserId() != other.getDetails().getUserId()) {
                player.getFrames().sendMessage("That player is not your Opponent.");
                return false;
            }
        }
        return true;
    }

    public boolean meleeDisabled() {
        return this.rules[3];
    }

    public boolean rangeDisabled() {
        return this.rules[2];
    }

    public boolean magicDisabled() {
        return this.rules[4];
    }

    public boolean isCommencing() {
        return state.equals(Duel_State.COMMENCE);
    }

    public Player getOther() {
        return other;
    }

    public boolean handleInterfaceOptions(final Player player, final int option, final int interfaceId,
                                          final int childId, final int itemId, final int slot) {// TODO
        return false;
    }

    public void handleItemClicks(final Item item, int option) {
    }

    public void handleObjectClicks(final GameObject object, int option) {
        switch (object.getId()) {
            case 3203:// trap door
                if (isCommencing()) {
                    if (this.other.getCombatState().isDead()) {
                        break;
                    }
                    this.end(false, false, true);
                } else {
                    player.getFrames().sendMessage("You can't forfeit so soon!");
                }
                break;
        }
    }

    public boolean equipmentSlotDisabled(Player player, int s) {
        int[] slot = {2, 3, 4, 5, 7, 9, 10, 12, 13, 0, 1};
        int[] rule = {13, 14, 15, 16, 17, 18, 19, 20, 21, 11, 12};
        for (int j = 0; j < rule.length; j++) {
            if (ruleEnabled(rule[j])) {
                if (j == 20) {
                    continue;
                }
                if (s == slot[j]) {
                    player.getFrames().sendMessage("That equipment slot is disabled in this duel.");
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCountingDown() {
        return state.equals(Duel_State.COUNT_DOWN);
    }

    public void setRules(boolean[] rules) {
        this.rules = rules;
    }

    public boolean specialsDisabled() {
        return this.rules[10];
    }

    public boolean prayersDisabled() {
        return this.rules[7];
    }

    public boolean foodsDisabled() {
        return this.rules[6];
    }

    public boolean drinksDisabled() {
        return this.rules[5];
    }

    public boolean forfeitDisabled() {
        return this.rules[0];
    }

    public enum Duel_State {
        COUNT_DOWN, COMMENCE, ENDED
    }
}
