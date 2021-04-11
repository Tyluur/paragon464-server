package com.paragon464.gameserver.model.entity.mob.player;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.ForceMovement;
import com.paragon464.gameserver.model.entity.mob.player.container.PlayerTransferContainer;
import com.paragon464.gameserver.model.entity.mob.player.container.TransferContainer;
import com.paragon464.gameserver.model.content.BeginnerTutorial;
import com.paragon464.gameserver.model.content.combat.data.CombatAnimations;
import com.paragon464.gameserver.model.content.minigames.barrows.CoffinSession;
import com.paragon464.gameserver.model.content.minigames.duelarena.DuelBattle;
import com.paragon464.gameserver.model.content.minigames.duelarena.DuelTransfer;
import com.paragon464.gameserver.model.content.skills.AbstractSkillAction;
import com.paragon464.gameserver.model.gameobjects.GameObject;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.grounditem.GroundItem;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.model.region.Region;
import com.paragon464.gameserver.model.shop.Shop;
import lombok.Getter;
import lombok.val;

import java.net.InetSocketAddress;
import java.util.List;

import static com.google.common.math.IntMath.checkedAdd;

public class PlayerVariables {

    public Shop Skillcapes = new Shop();
    public int tab1items;
    public int tab2items;
    public int tab3items;
    public int tab4items;
    public int tab5items;
    public int tab6items;
    public int tab7items;
    public int tab8items;
    public int tab9items;
    private Player player;
    // variables we save. // TODO: actually save them lmao
    @Getter
    private String currentAddress = "";
    private int totalExp;
    private boolean expLocked;
    private boolean started;
    // variables we don't save.
    private String lastAddress = "";
    private Position lastPosition;
    private ForceMovement nextForceMovement;
    private Runnable closeInterfaceEvent;
    private List<GameObject> activeTraps;

    public PlayerVariables(Player player) {
        this.player = player;
    }

    public void giveStarter() {
        if (!hasReceivedStarter()) {
            this.setStarted(true);
            player.getSkills().setSkill(SkillType.HITPOINTS, 10, Skills.getExperienceForLevel(10));
            player.getSkills().setSkill(SkillType.HERBLORE, 3, Skills.getExperienceForLevel(3));
            player.getBank().addItem(new Item(995, 250000));
            player.getBank().addItem(new Item(379, 100));
            player.getBank().addItem(new Item(841, 1));
            player.getBank().addItem(new Item(884, 50));
            player.getBank().addItem(new Item(1129, 1));
            player.getBank().addItem(new Item(1095, 1));
            player.getBank().addItem(new Item(1381, 1));
            player.getBank().addItem(new Item(554, 100));
            player.getBank().addItem(new Item(555, 100));
            player.getBank().addItem(new Item(556, 100));
            player.getBank().addItem(new Item(557, 100));
            player.getBank().addItem(new Item(558, 100));
            player.getBank().addItem(new Item(1323, 1));
            player.getBank().addItem(new Item(1731, 1));
            player.getFrames().sendMessage("Starter gear was added to your bank.");
        }

        if (!player.getAttributes().is("received_validation_package") && player.getDetails().emailVerified()
            && player.getBank().freeSlots() >= 1) {
            player.getBank().addItem(new Item(10586, 1));
            player.getAttributes().set("received_validation_package", true);

            player.getFrames().sendMessage("Thank you for helping us keep you safe by validating your account!");
            player.getFrames().sendMessage("A 24-hour, double-experience lamp has been added to your bank.");
        }
    }

    public boolean hasReceivedStarter() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isExpLocked() {
        return expLocked;
    }

    public void setExpLocked(boolean expLocked) {
        this.expLocked = expLocked;
    }

    public Position getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(final Position lastPosition) {
        this.lastPosition = lastPosition;
    }

    public long getLastAction() {
        return player.getAttributes().getLong("last_action");
    }

    public void setLastAction(long var) {
        player.getAttributes().set("last_action", var);
    }

    /**
     * Adds a trap to the list of active traps.
     *
     * @param trap The trap to add.
     * @return
     */
    public boolean addTrap(GameObject trap) {
        return activeTraps.add(trap);
    }

    /**
     * Removes a trap from the list of active traps.
     *
     * @param trap The trap to remove.
     * @return
     */
    public boolean removeTrap(GameObject trap) {
        return activeTraps.remove(trap);
    }

    /**
     * Gets the list of active traps
     *
     * @return The list of active fires.
     */
    public List<GameObject> getActiveTraps() {
        return activeTraps;
    }

    public void increaseTotalExp(int amt) {
        if ((long) totalExp + amt > Integer.MAX_VALUE) {
            this.totalExp = Integer.MAX_VALUE;
        } else {
            this.totalExp += amt;
        }
    }

    public void open_counter() {
        int val = player.getAttributes().getInt("counter");
        if (val == 0) {
            player.getAttributes().set("counter", 1);
        } else {
            player.getAttributes().set("counter", 0);
        }
        val = player.getAttributes().getInt("counter");
        player.getFrames().sendVarp(554, getTotalExp());
        player.getFrames().sendVarp(555, val <= 0 ? 1 : 0);
    }

    public int getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(int totalExp) {
        this.totalExp = totalExp;
    }

    public String getLastAddress() {
        return lastAddress;
    }

    public void setLastAddress(String ad) {
        this.lastAddress = ad;
    }

    public void refreshCurrentAddress() {
        if (player.getSession() != null && player.getSession().getRemoteAddress() != null) {
            this.currentAddress = ((InetSocketAddress) player.getSession()
                .getRemoteAddress()).getAddress().getHostAddress();
        }
    }

    public void refreshSpawnedItems() {
        for (int i = player.getMapRegions().size() - 1; i >= 0; i--) {
            Region region = player.getMapRegions().get(i);
            if (region != World.getRegion(player)) {
                continue;
            }

            for (int item = region.getGroundItems().size() - 1; item >= 0; item--) {
                player.getFrames().clearGroundItem(region.getGroundItems().get(item));
            }

            for (int item = region.getGroundItems().size() - 1; item >= 0; item--) {
                final GroundItem groundItem = region.getGroundItems().get(item);

                if (groundItem.isVisible() || player.equals(groundItem.getOwner().orElse(null))) {
                    player.getFrames().sendGroundItem(groundItem);
                }
            }
        }
    }

    public void refreshSpawnedObjects() {
        for (int i = player.getMapRegions().size() - 1; i >= 0; i--) {
            Region region = player.getMapRegions().get(i);
            if (region != World.getRegion(player)) {
                continue;
            }

            List<GameObject> removedObjects = region.getRemovedOriginalObjects();
            for (GameObject object : removedObjects)
                player.getFrames().removeObject(object);
            List<GameObject> spawnedObjects = region.getSpawnedObjects();
            for (GameObject object : spawnedObjects)
                player.getFrames().createObject(object);
        }
    }

    public ForceMovement getNextForceMovement() {
        return nextForceMovement;
    }

    public void setNextForceMovement(ForceMovement nextForceMovement) {
        this.nextForceMovement = nextForceMovement;
    }

    public int getWalkAnimation() {
        int anim = CombatAnimations.getWalkAnimation(player);
        int walkAnimation = player.getAttributes().getInt("walkAnimation");
        if (walkAnimation != 0) {
            return walkAnimation;
        }
        return anim;
    }

    public void setWalkAnimation(int walkAnimation) {
        player.getAttributes().set("walkAnimation", walkAnimation);
    }

    public int getStandAnimation() {
        int anim = CombatAnimations.getStandAnimation(player);
        int standAnimation = player.getAttributes().getInt("standAnimation");
        if (standAnimation != 0) {
            return standAnimation;
        }
        return anim;
    }

    public void setStandAnimation(int standAnimation) {
        player.getAttributes().set("standAnimation", standAnimation);
    }

    public int getRunAnimation() {
        int anim = CombatAnimations.getRunAnimation(player);
        int runAnimation = player.getAttributes().getInt("runAnimation");
        if (runAnimation != 0) {
            return runAnimation;
        }
        return anim;
    }

    public void setRunAnimation(int runAnimation) {
        player.getAttributes().set("runAnimation", runAnimation);
    }

    public int getTurnAnimation() {
        int anim = CombatAnimations.getTurnAnimation(player);
        int turnAnimation = player.getAttributes().getInt("turnAnimation");
        if (turnAnimation != 0) {
            return turnAnimation;
        }
        return anim;
    }

    public void setTurnAnimation(int turnAnimation) {
        player.getAttributes().set("turnAnimation", turnAnimation);
    }

    public int getTurn180Animation() {
        int anim = CombatAnimations.getTurn180Animation(player);
        int turn180Animation = player.getAttributes().getInt("turn180Animation");
        if (turn180Animation != 0) {
            return turn180Animation;
        }
        return anim;
    }

    public void setTurn180Animation(int turn180Animation) {
        player.getAttributes().set("turn180Animation", turn180Animation);
    }

    public int getTurn90Clockwise() {
        int anim = CombatAnimations.getTurn90ClockwiseAnimation(player);
        int turn90Clockwise = player.getAttributes().getInt("turn90Clockwise");
        if (turn90Clockwise != 0) {
            return turn90Clockwise;
        }
        return anim;
    }

    public void setTurn90Clockwise(int turn90Clockwise) {
        player.getAttributes().set("turn90Clockwise", turn90Clockwise);
    }

    public int getTurn90CounterClockwise() {
        int anim = CombatAnimations.getTurn90CounterClockwiseAnimation(player);
        int turn90CounterClockwise = player.getAttributes().getInt("turn90CounterClockwise");
        if (turn90CounterClockwise != 0) {
            return turn90CounterClockwise;
        }
        return anim;
    }

    public void setTurn90CounterClockwise(int turn90CounterClockwise) {
        player.getAttributes().set("turn90CounterClockwise", turn90CounterClockwise);
    }

    public Runnable getCloseInterfacesEvent() {
        return this.closeInterfaceEvent;
    }

    public void setCloseInterfacesEvent(Runnable closeInterfacesEvent) {
        this.closeInterfaceEvent = closeInterfacesEvent;
    }

    public boolean skillActionExecuting(AbstractSkillAction value) {
        AbstractSkillAction current = getSkill();
        if (current != null) {
            current.end();
        }
        player.getAttributes().set("skill_action", value);
        if (value != null) {
            value.init();
        }
        return getSkill() != null;
    }

    public AbstractSkillAction getSkill() {
        return player.getAttributes().get("skill_action");
    }    public PlayerTransferContainer getPlayerTransferContainer() {
        return player.getAttributes().get("playertransfer_container");
    }

    public DuelTransfer getDuelTransfer() {
        return (getPlayerTransferContainer() instanceof DuelTransfer ? (DuelTransfer) getPlayerTransferContainer()
            : null);
    }

    public DuelBattle getDuelBattle() {
        return (DuelBattle) player.getAttributes().get("duel_battle");
    }    public void setPlayerTransferContainer(PlayerTransferContainer container) {
        PlayerTransferContainer current = getPlayerTransferContainer();
        player.getAttributes().set("playertransfer_container", container);
        if (current != null && container == null) {
            current.closed();
        }
    }

    public void increaseTabItems(int tab) {
        switch (tab) {
            case 1:
                tab1items++;
                break;
            case 2:
                tab2items++;
                break;
            case 3:
                tab3items++;
                break;
            case 4:
                tab4items++;
                break;
            case 5:
                tab5items++;
                break;
            case 6:
                tab6items++;
                break;
            case 7:
                tab7items++;
                break;
            case 8:
                tab8items++;
                break;
            case 9:
                tab9items++;
                break;
        }
    }

    public void setTabItems(int tab, int count) {
        switch (tab) {
            case 1:
                tab1items = count;
                break;
            case 2:
                tab2items = count;
                break;
            case 3:
                tab3items = count;
                break;
            case 4:
                tab4items = count;
                break;
            case 5:
                tab5items = count;
                break;
            case 6:
                tab6items = count;
                break;
            case 7:
                tab7items = count;
                break;
            case 8:
                tab8items = count;
                break;
            case 9:
                tab9items = count;
                break;
        }
    }    public TransferContainer getTransferContainer() {
        return player.getAttributes().get("transfer_container");
    }

    public void decreaseTabItems(int tab) {
        switch (tab) {
            case 1:
                tab1items--;
                break;
            case 2:
                tab2items--;
                break;
            case 3:
                tab3items--;
                break;
            case 4:
                tab4items--;
                break;
            case 5:
                tab5items--;
                break;
            case 6:
                tab6items--;
                break;
            case 7:
                tab7items--;
                break;
            case 8:
                tab8items--;
                break;
            case 9:
                tab9items--;
                break;
        }
        int currentTab = player.getAttributes().getInt("viewing_tab");
        boolean resend = tab1items == 0 || tab2items == 0 || tab3items == 0 || tab4items == 0 || tab5items == 0 || tab6items == 0 || tab7items == 0 || tab8items == 0 || tab9items == 0;
        if (resend) {
            for (int i = currentTab; i < 9; i++) {
                if (tab8items == 0) {
                    tab8items = tab9items;
                    tab9items = 0;
                }
                if (tab7items == 0) {
                    tab7items = tab8items;
                    tab8items = 0;
                }
                if (tab6items == 0) {
                    tab6items = tab7items;
                    tab7items = 0;
                }
                if (tab5items == 0) {
                    tab5items = tab6items;
                    tab6items = 0;
                }
                if (tab4items == 0) {
                    tab4items = tab5items;
                    tab5items = 0;
                }
                if (tab3items == 0) {
                    tab3items = tab4items;
                    tab4items = 0;
                }
                if (tab2items == 0) {
                    tab2items = tab3items;
                    tab3items = 0;
                }
                if (tab1items == 0) {
                    tab1items = tab2items;
                    tab2items = 0;
                }
            }
            if (getTabItems(currentTab) <= 0) {
                player.getAttributes().set("viewing_tab", 0);
            }
        }
    }

    public int getTabItems(int tab) {
        switch (tab) {
            case 1:
                return tab1items;
            case 2:
                return tab2items;
            case 3:
                return tab3items;
            case 4:
                return tab4items;
            case 5:
                return tab5items;
            case 6:
                return tab6items;
            case 7:
                return tab7items;
            case 8:
                return tab8items;
            case 9:
                return tab9items;
        }
        return -1;
    }    public void setTransferContainer(TransferContainer container) {
        TransferContainer current = getTransferContainer();
        if (current != null) {
            current.closed();
        }
        player.getAttributes().set("transfer_container", container);
    }

    public int tabToIndex(int tab) {
        if (tab == 1) {
            return 0;
        }
        int index = 0;
        if (tab == 2) {
            index = tab1items;
            return index;
        }
        if (tab == 3) {
            index = tab1items + tab2items;
            return index;
        }
        if (tab == 4) {
            index = tab1items + tab2items + tab3items;
            return index;
        }
        if (tab == 5) {
            index = tab1items + tab2items + tab3items + tab4items;
            return index;
        }
        if (tab == 6) {
            index = tab1items + tab2items + tab3items + tab4items + tab5items;
            return index;
        }
        if (tab == 7) {
            index = tab1items + tab2items + tab3items + tab4items + tab5items + tab6items;
            return index;
        }
        if (tab == 8) {
            index = tab1items + tab2items + tab3items + tab4items + tab5items + tab6items + tab7items;
            return index;
        }
        if (tab == 9) {
            index = tab1items + tab2items + tab3items + tab4items + tab5items + tab6items + tab7items + tab8items;
            return index;
        }

        index = tab1items + tab2items + tab3items + tab4items + tab5items + tab6items + tab7items + tab8items + tab9items;
        return index;
    }

    public int indexToTab(int itemIndex) {
        int totalIndex = tab1items;
        if (itemIndex < totalIndex) {
            return 1;
        }
        totalIndex = totalIndex + tab2items;
        if (itemIndex < totalIndex) {
            return 2;
        }
        totalIndex = totalIndex + tab3items;
        if (itemIndex < totalIndex) {
            return 3;
        }
        totalIndex = totalIndex + tab4items;
        if (itemIndex < totalIndex) {
            return 4;
        }
        totalIndex = totalIndex + tab5items;
        if (itemIndex < totalIndex) {
            return 5;
        }
        totalIndex = totalIndex + tab6items;
        if (itemIndex < totalIndex) {
            return 6;
        }
        totalIndex = totalIndex + tab7items;
        if (itemIndex < totalIndex) {
            return 7;
        }
        totalIndex = totalIndex + tab8items;
        if (itemIndex < totalIndex) {
            return 8;
        }
        totalIndex = totalIndex + tab9items;
        if (itemIndex < totalIndex) {
            return 9;
        }
        return 0;
    }

    public BeginnerTutorial getTutorial() {
        return player.getAttributes().get("beginner_tutorial");
    }

    public void setTutorial(BeginnerTutorial tut) {
        player.getAttributes().set("beginner_tutorial", tut);
    }    public CoffinSession getCoffinSession() {
        return player.getAttributes().get("coffin_session");
    }



    public void setCoffinSession(CoffinSession.Brothers type) {
        CoffinSession session = getCoffinSession();
        if (getCoffinSession().brothersDead[type.ordinal()]) {
            player.getFrames().sendMessage("You've already destroyed this brother.");
            return;
        }
        if (session.getBrother() != null) {
            player.getFrames().sendMessage("You've already opened the coffin!");
        } else {
            session.setBrother(type);
            session.openCoffin();
        }
    }








}
