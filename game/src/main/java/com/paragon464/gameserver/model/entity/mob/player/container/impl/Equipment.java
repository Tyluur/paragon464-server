package com.paragon464.gameserver.model.entity.mob.player.container.impl;

import com.paragon464.gameserver.model.entity.mob.masks.UpdateFlags;
import com.paragon464.gameserver.model.entity.mob.player.AttackInterfaceConfig;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.ContainerInterface;
import com.paragon464.gameserver.model.content.minigames.duelarena.DuelBattle;
import com.paragon464.gameserver.model.content.skills.magic.StaffInterface;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemDefinition;

import static com.paragon464.gameserver.model.item.EquipmentType.TWO_HANDED_WEAPON;

public class Equipment {

    public static final int SIZE = 14;

    public static final int HELMET_SLOT = 0;
    public static final int WEAPON_SLOT = 3;
    public static final int SHIELD_SLOT = 5;
    public static final int RING_SLOT = 12;
    public static final int CAPE_SLOT = 1;
    public static final int AMULET_SLOT = 2;
    public static final int ARROW_SLOT = 13;
    public static final int PLATE_SLOT = 4;
    public static final int LEGS_SLOT = 7;
    public static final int BOOTS_SLOT = 10;
    public static final int GLOVES_SLOT = 9;

    public static String stringForSlot(int slot) {
        switch (slot) {
            case HELMET_SLOT:
                return "Head";
            case CAPE_SLOT:
                return "Back";
            case AMULET_SLOT:
                return "Neck";
            case WEAPON_SLOT:
                return "Main-hand";
            case PLATE_SLOT:
                return "Torso";
            case SHIELD_SLOT:
                return "Off-hand";
            case LEGS_SLOT:
                return "Legs";
            case GLOVES_SLOT:
                return "Gloves";
            case BOOTS_SLOT:
                return "Feet";
            case RING_SLOT:
                return "Fingers";
            case ARROW_SLOT:
                return "Ammunition";
        }
        return null;
    }

    public static int slotForString(String string) {
        switch (string) {
            case "Head":
                return HELMET_SLOT;
            case "Back":
                return CAPE_SLOT;
            case "Neck":
                return AMULET_SLOT;
            case "Main-hand":
                return WEAPON_SLOT;
            case "Torso":
                return PLATE_SLOT;
            case "Off-hand":
                return SHIELD_SLOT;
            case "Legs":
                return LEGS_SLOT;
            case "Gloves":
                return GLOVES_SLOT;
            case "Feet":
                return BOOTS_SLOT;
            case "Fingers":
                return RING_SLOT;
            case "Ammunition":
                return ARROW_SLOT;
        }
        return -1;
    }

    public static boolean equipItem(Player player, int equipping_id, int slot, boolean screen) {
        Item invItem = player.getInventory().get(slot);
        if (invItem == null) {
            return false;
        }
        if (invItem.getId() != equipping_id) {
            return false;
        }
        ItemDefinition equipping_definition = ItemDefinition.forId(equipping_id);
        if (equipping_definition == null) {
            return false;
        }
        if (equipping_definition.equipmentDefinition == null) {
            return false;
        }
        if (!equipping_definition.isWearable()) {
            player.getFrames().sendMessage("You can't wear this!");
            return false;
        }
        if (!equipping_definition.meetsRequirements(player)) {
            return false;
        }
        int weapon = player.getEquipment().getItemInSlot(3);
        int shield = player.getEquipment().getItemInSlot(5);
        ItemDefinition weapon_definition = ItemDefinition.forId(weapon);
        //ItemDefinition shield_definition = ItemDefinition.forId(shield);
        int s = equipping_definition.equipmentDefinition.getSlotId();
        if (s == -1) {
            return false;
        }
        if (s == 3) {//its a weapon
            if (equipping_definition.weaponDefinition == null) {
                return false;
            }
        }
        int amount = player.getInventory().getAmountInSlot(slot);
        boolean stackable = equipping_definition.isStackable();
        boolean twoHanded = false;
        if (equipping_definition.weaponDefinition != null) {
            twoHanded = equipping_definition.equipmentDefinition.matchesEquipmentType(TWO_HANDED_WEAPON);
        }
        if (!player.getControllerManager().processItemEquip(equipping_id, slot)) {
        	return false;
        }
        DuelBattle duel_battle = player.getVariables().getDuelBattle();
        if (duel_battle != null) {
            if (duel_battle.equipmentSlotDisabled(player, s)) {
                return false;
            }
        }
        if (twoHanded) {
            if (player.getInventory().freeSlots() < getNeeded2HSlots(player)) {
                player.getFrames().sendMessage("Not enough space in your inventory.");
                return false;
            }
        }
        if (!player.getInventory().deleteItem(invItem, slot)) {
            return false;
        }
        if (twoHanded && shield != -1) {
            if (!unequipItem(player, -1, 5, screen)) {
                return false;
            }
        }
        if (s == 5) {
            if (weapon != -1) {
                if (weapon_definition.equipmentDefinition.matchesEquipmentType(TWO_HANDED_WEAPON)) {
                    if (!unequipItem(player, -1, 3, screen)) {
                        return false;
                    }
                }
            }
        }
        Item equipItem = player.getEquipment().get(s);
        if (equipItem != null) {
            if (equipItem.getId() != equipping_id) {
                if (!player.getInventory().addItem(equipItem.getId(), equipItem.getAmount(), slot)) {
                    return false;
                }
            } else if (stackable && equipItem.getId() == equipping_id) {
                amount = equipItem.getAmount() + amount;
            } else {
                player.getInventory().addItem(equipItem.getId(), equipItem.getAmount(), slot);
            }
        }
        player.getEquipment().set(new Item(invItem.getId(), amount), s, false);
        player.getEquipment().refresh();
        player.getInventory().refresh();
        if (screen) {
            player.getBonuses().recalc();
            player.getFrames().sendWeight();
            player.getBonuses().refreshStrings();
        }
        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
        if (s == 3) {
            Equipment.setWeapon(player, true);
            StaffInterface.cancel(player, true);
        }
        if (player.getCombatState().getTarget() != null) {
            player.getCombatState().end(1);
        }
        player.getBonuses().recalc();
        return true;
    }

    private static int getNeeded2HSlots(Player player) {
        int shield = player.getEquipment().getItemInSlot(5);
        int weapon = player.getEquipment().getItemInSlot(3);
        if ((shield != -1 && weapon == -1) || (shield == -1 && weapon != -1) || (shield == -1 && weapon == -1)) {
            return 0;
        }
        return 1;
    }

    public static boolean unequipItem(Player p, int itemId, int slot, boolean screen) {
        Item item = p.getEquipment().get(slot);
        if (item == null) {
            return false;
        }
        if (itemId != -1) {
            if (item.getId() != itemId) {
                return false;
            }
        }
        if (p.getAttributes().isSet("stopActions")) {
            return false;
        }
        if (p.getCombatState().isDead()) {
            return false;
        }
        if (p.getInventory().addItem(item)) {
            p.getInventory().refresh();
            p.getEquipment().set(null, slot, false);
            p.getEquipment().refresh();
            p.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
            p.getBonuses().recalc();
            if (screen) {
                p.getBonuses().refreshStrings();
                p.getFrames().sendWeight();
            }
            p.setInteractingMob(null);
            if (slot == 3) {
                setWeapon(p, true);
            }
            if (p.getCombatState().getTarget() != null) {
                p.getCombatState().end(1);
                StaffInterface.cancel(p, true);
            }
            if (!screen) {
                p.resetActionAttributes();
            }
            return true;
        }
        return false;
    }

    public static void setWeapon(Player player, boolean set_interface_configs) {
        int tabId = player.getSettings().isInResizable() ? 65 : 86;
        Item item = player.getEquipment().get(3);
        if (item == null) {
            player.getFrames().sendTab(tabId, 92);
            player.getFrames().modifyText("Unarmed", 92, 0);
            AttackInterfaceConfig.setButtonForAttackStyle(player, 92);
            return;
        }
        ItemDefinition definition = item.getDefinition();
        if (definition == null) {
            return;
        }
        String weapon = definition.getName();
        ItemDefinition.WeaponDefinition config = definition.weaponDefinition;
        if (config == null) {
            return;
        }
        int interfaceId = config.getInterfaceId();
        if (set_interface_configs) {
            int child = config.getChildId();
            player.getFrames().sendTab(tabId, interfaceId);
            boolean set = config.getSpecialEnergy() > 0;
            AttackInterfaceConfig.setButtonForAttackStyle(player, interfaceId);
            player.getFrames().sendInterfaceVisibility(interfaceId, child, set);
            player.getSettings().setSpecial(false);
            player.getSettings().refreshBar();
        }
        player.getFrames().modifyText(weapon, interfaceId, 0);
    }

    public static void displayEquipmentScreen(Player p) {
        if (!p.getCombatState().outOfCombat()) {
            p.getFrames().sendMessage("You can't open this while in combat.");
            return;
        }
        if (p.getAttributes().isSet("new_account_verify")) {
            return;
        }
        p.getCombatState().end(1);
        p.getFrames().clearMapFlag();
        // p.getFrames().sendTBC(168, 3);
        p.resetActionAttributes();
        Object[] opts = new Object[]{"", "", "", "", "Wear<col=ff9040>", -1, 0, 7, 4, 98, 22020096};
        p.getInterfaceSettings().openInterface(465);
        p.getBonuses().recalc();
        p.getBonuses().refreshStrings();
        p.getFrames().displayInventoryInterface(336);
        p.getFrames().sendClientScript(150, opts, "IviiiIsssss");
        p.getFrames().sendClickMask(0, 28, 336, 0, 1278);
        p.getInterfaceSettings().addListener(p.getInventory(), new ContainerInterface(-1, 1, 98));
        p.getInterfaceSettings().addListener(p.getEquipment(), new ContainerInterface(465, 103, 95));
        p.getInventory().refresh();
        p.getEquipment().refresh();
    }
}
