package com.paragon464.gameserver.model.content.combat.data;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.item.ItemDefinition;

public class ArmourSets {

    public static boolean wearingFullGuthans(Mob mob) {
        if (mob.isNPC())
            return false;
        Player player = (Player) mob;
        return wearingDegradableArmourSet(player, DegradableArmourSet.GUTHANS_SET);
    }

    public static boolean wearingDegradableArmourSet(Player player, DegradableArmourSet armour_type) {
        int helmet = player.getEquipment().getItemInSlot(Equipment.HELMET_SLOT);
        int weapon = player.getEquipment().getItemInSlot(Equipment.WEAPON_SLOT);
        int plate = player.getEquipment().getItemInSlot(Equipment.PLATE_SLOT);
        int legs = player.getEquipment().getItemInSlot(Equipment.LEGS_SLOT);
        if (armour_type.equals(DegradableArmourSet.DHAROKS_SET)) {
            return helmet == 4716 && weapon == 4718 && plate == 4720 && legs == 4722;
        } else if (armour_type.equals(DegradableArmourSet.VERACS_SET)) {
            return helmet == 4753 && weapon == 4755 && plate == 4757 && legs == 4759;
        } else if (armour_type.equals(DegradableArmourSet.GUTHANS_SET)) {
            return helmet == 4724 && weapon == 4726 && plate == 4728 && legs == 4730;
        }
        return false;
        /*
         * ItemDefinition helmet_def = ItemDefinition.forId(helmet);
         * ItemDefinition weapon_def = ItemDefinition.forId(weapon);
         * ItemDefinition plate_def = ItemDefinition.forId(plate);
         * ItemDefinition legs_def = ItemDefinition.forId(legs); if (helmet_def
         * != null && plate_def != null && legs_def != null) { DegradableType
         * helmet_type_req = armour_type.getTypes()[0]; DegradableType
         * plate_type_req = armour_type.getTypes()[1]; DegradableType
         * legs_type_req = armour_type.getTypes()[2]; DegradableType
         * weapon_type_req = armour_type.getTypes()[3]; // DegradableType
         * helmet_type = helmet_def.getDegradableType(); DegradableType
         * plate_type = plate_def.getDegradableType(); DegradableType legs_type
         * = legs_def.getDegradableType(); DegradableType weapon_type =
         * weapon_def.getDegradableType(); if (helmet_type != null && plate_type
         * != null && legs_type != null) { if (weapon_type != null) { if
         * (weapon_type_req != null) { return
         * helmet_type.equals(helmet_type_req) &&
         * plate_type.equals(plate_type_req) && legs_type.equals(legs_type_req)
         * && weapon_type.equals(weapon_type_req); } } return
         * helmet_type.equals(helmet_type_req) &&
         * plate_type.equals(plate_type_req) && legs_type.equals(legs_type_req);
         * } }
         */
        // return false;
    }

    public static boolean wearingFullVerac(Mob mob) {
        if (mob.isNPC())
            return false;
        Player player = (Player) mob;
        return wearingDegradableArmourSet(player, DegradableArmourSet.VERACS_SET);
    }

    public static boolean wearingFullDharok(Player player) {
        return wearingDegradableArmourSet(player, DegradableArmourSet.DHAROKS_SET);
    }

    public static boolean wearingFullVoidMelee(Player player) {
        int helm = player.getEquipment().getItemInSlot(Equipment.HELMET_SLOT);
        int plate = player.getEquipment().getItemInSlot(Equipment.PLATE_SLOT);
        int legs = player.getEquipment().getItemInSlot(Equipment.LEGS_SLOT);
        return helm == 11665 && plate == 8839 && legs == 8840;
    }

    public static boolean wearingFullVoidRange(Player player) {
        int helm = player.getEquipment().getItemInSlot(Equipment.HELMET_SLOT);
        int plate = player.getEquipment().getItemInSlot(Equipment.PLATE_SLOT);
        int legs = player.getEquipment().getItemInSlot(Equipment.LEGS_SLOT);
        int gloves = player.getEquipment().getItemInSlot(Equipment.GLOVES_SLOT);
        return helm == 11664 && plate == 8839 && legs == 8840 && gloves == 8842;
    }

    public static boolean wearingFullVoidMagic(Player player) {
        int helm = player.getEquipment().getItemInSlot(Equipment.HELMET_SLOT);
        int plate = player.getEquipment().getItemInSlot(Equipment.PLATE_SLOT);
        int legs = player.getEquipment().getItemInSlot(Equipment.LEGS_SLOT);
        int gloves = player.getEquipment().getItemInSlot(Equipment.GLOVES_SLOT);
        return helm == 11663 && plate == 8839 && legs == 8840 && gloves == 8842;
    }

    public static boolean wearingAvas(Player player) {
        int cape = player.getEquipment().getItemInSlot(Equipment.CAPE_SLOT);
        return cape == 10499 || cape == 10498;
    }

    public static boolean wearingSlayerHelm(Player player) {
        int head = player.getEquipment().getItemInSlot(Equipment.HELMET_SLOT);
        return head == 13263 || head == 14636 || head == 14637;
    }

    public static boolean wearingFullSlayerHelm(Player player) {
        int head = player.getEquipment().getItemInSlot(Equipment.HELMET_SLOT);
        return head == 15492 || head == 15496 || head == 15497;
    }

    public static boolean wearingObbyEffect(Player player) {
        int wep = player.getEquipment().getItemInSlot(Equipment.WEAPON_SLOT);
        int amulet = player.getEquipment().getItemInSlot(Equipment.AMULET_SLOT);
        return wep == 6528 && amulet == 11128;
    }

    public enum DegradableArmourSet {

        // helm, plate, legs, weapon
        DHAROKS_SET(new ItemDefinition.DegradableType[]{ItemDefinition.DegradableType.dharoks_helmet, ItemDefinition.DegradableType.dharoks_plate,
            ItemDefinition.DegradableType.dharoks_legs, ItemDefinition.DegradableType.dharoks_axe}),
        VERACS_SET(new ItemDefinition.DegradableType[]{
            ItemDefinition.DegradableType.veracs_helm, ItemDefinition.DegradableType.veracs_plate, ItemDefinition.DegradableType.veracs_skirt,
            ItemDefinition.DegradableType.veracs_flail}),
        GUTHANS_SET(new ItemDefinition.DegradableType[]{ItemDefinition.DegradableType.guthans_helm,
            ItemDefinition.DegradableType.guthans_plate, ItemDefinition.DegradableType.guthans_legs,
            ItemDefinition.DegradableType.guthans_spear}),
        KARILS_SET(
            new ItemDefinition.DegradableType[]{ItemDefinition.DegradableType.karils_coif, ItemDefinition.DegradableType.karils_top,
                ItemDefinition.DegradableType.karils_bottom, ItemDefinition.DegradableType.karils_bow}),
        AHRIMS_SET(
            new ItemDefinition.DegradableType[]{ItemDefinition.DegradableType.ahrims_hood,
                ItemDefinition.DegradableType.ahrims_top, ItemDefinition.DegradableType.ahrims_bottom,
                ItemDefinition.DegradableType.ahrims_staff}),
        TORAGS_SET(
            new ItemDefinition.DegradableType[]{
                ItemDefinition.DegradableType.torags_helm,
                ItemDefinition.DegradableType.torags_plate,
                ItemDefinition.DegradableType.torags_legs,
                ItemDefinition.DegradableType.torags_hammers}),
        ;

        private ItemDefinition.DegradableType[] degradable_types = new ItemDefinition.DegradableType[4];

        DegradableArmourSet(ItemDefinition.DegradableType[] degradable_types) {
            this.degradable_types = degradable_types;
        }

        public ItemDefinition.DegradableType[] getTypes() {
            return degradable_types;
        }
    }
}
