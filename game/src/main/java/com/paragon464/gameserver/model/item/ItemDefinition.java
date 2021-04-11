package com.paragon464.gameserver.model.item;

import com.paragon464.gameserver.model.entity.mob.CombatType;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemDefinition {

    public static HashMap<Integer, ItemDefinition> definitions = new HashMap<>();
    private final String itemName;
    private final String examinationInfo;
    private final int itemId;
    private final int noteId;
    private final int noteTemplateId;
    private final int lendId;
    private final int lendTemplateId;
    private final int itemValue;
    private final boolean membersOnly;
    private final boolean isDroppable;
    private final boolean isTradable;
    private final boolean isStackable;
    private final double itemWeight;
    public EquipmentDefinition equipmentDefinition;
    public WeaponDefinition weaponDefinition;
    public RangedDefinition rangedDefinition;
    public HashMap<Integer, Integer> skill_requirements = new HashMap<>();

    public ItemDefinition(final String name, final String examine, final int id, final int note, final int noteTemplate,
                          final int lend, final int lendTemplate, final boolean droppable, final boolean tradable,
                          final boolean stackable, final boolean members, final int value, final double weight) {
        super();
        this.itemName = name;
        this.examinationInfo = examine;
        this.itemId = id;
        this.noteId = note;
        this.noteTemplateId = noteTemplate;
        this.lendId = lend;
        this.lendTemplateId = lendTemplate;
        this.isDroppable = droppable;
        this.isTradable = tradable;
        this.isStackable = stackable;
        this.membersOnly = members;
        this.itemValue = value;
        this.itemWeight = weight;
    }

    public static ItemDefinition forId(int id) {
        return definitions.get(id);
    }

    /**
     * Checks if the specified player meets all the requirements necessary to equip the item.
     *
     * @param player The player to check against.
     * @return {@code true} if the player meets all necessary prerequisites to equip the item.
     * Otherwise, {@code false}.
     */
    public boolean meetsRequirements(Player player) {
        if (this.skill_requirements == null || this.skill_requirements.size() <= 0) {
            return true;
        }
        for (int i = 0; i < 7; i++) {
            if (this.skill_requirements.get(i) == null)
                continue;
            SkillType skillType = SkillType.fromId(i);
            int level = this.skill_requirements.get(i);
            int p_level = player.getSkills().getLevel(skillType);
            if (p_level < level) {
                player.getFrames().sendMessage("You need a " + skillType.getDisplayName() + " level of " + level + " to wear this item.");
                return false;
            }
        }
        if (this.getName().equalsIgnoreCase("dragon defender")) {
            final int attackLevel = player.getSkills().getLevel(SkillType.ATTACK);
            final int strengthLevel = player.getSkills().getLevel(SkillType.STRENGTH);
            final int combinedLevel = attackLevel + strengthLevel;
            boolean proceed = (combinedLevel >= 130 || strengthLevel == 99);
            if (!proceed) {
                player.getFrames().sendMessage("You need a Strength level of 99 or an Attack & Strength combined greater than 130.");
            }
            return proceed;
        }
        return true;
    }

    /**
     * @return The displayName of the item.
     */
    public String getName() {
        return itemName;
    }

    /**
     * @return {@code true} if this item can be equipped. Otherwise, {@code false}.
     */
    public boolean isWearable() {
        return equipmentDefinition != null && equipmentDefinition.getSlotId() != -1;
    }

    /**
     * @return The high alchemy value of the item. Typically 60% of the value attribute.
     */
    public int getHighAlch() {
        return (int) ((itemValue / ItemConstants.MAX_VALUE_PERCENTAGE) * ItemConstants.HIGH_ALCH_PERCENTAGE);
    }

    /**
     * @return The low alchemy value of the item. Typically 40% of the value attribute.
     */
    public int getLowAlchPrice() {
        return (int) ((itemValue / ItemConstants.MAX_VALUE_PERCENTAGE) * ItemConstants.LOW_ALCH_PERCENTAGE);
    }

    /**
     * @return {@code true} if this item has any requirements to be equipped. Otherwise, {@code false}.
     */
    public boolean hasRequirements() {
        return skill_requirements.size() > 0;
    }

    /**
     * @return The examination text that corresponds to this item.
     */
    public String getExamine() {
        return examinationInfo;
    }

    /**
     * @return The item ID associated with this item definition.
     */
    public int getId() {
        return itemId;
    }

    /**
     * @return The noted version of the item—if this is a noted item. Otherwise, -1.
     */
    public int getNotedId() {
        if (!isNoted()) {
            return noteId;
        }
        return -1;
    }

    /**
     * @return {@code true} if this item is a bank note of another item. Otherwise, {@code false}.
     */
    public boolean isNoted() {
        return noteTemplateId != -1;
    }

    /**
     * @return The standard version of the item—if this is not a noted item. Otherwise, -1.
     */
    public int getUnnotedId() {
        if (isNoted()) {
            return noteId;
        }
        return -1;
    }

    /**
     * @return The lent version of the item—if this is not a lent item. Otherwisem -1.
     */
    public int getLentId() {
        if (!isLent()) {
            return lendId;
        }

        return -1;
    }

    /**
     * @return {@code true} if this item is the lent form of an item. Otherwise, {@code false}.
     */
    public boolean isLent() {
        return lendTemplateId != -1;
    }

    /**
     * @return The standard version of the item—if this is a lent item. Otherwise, -1.
     */
    public int getUnlentId() {
        if (isLent()) {
            return lendId;
        }

        return -1;
    }

    /**
     * Determines whether or not an item is droppable.
     * Typically, non-droppable items are destroyed upon drop instead of functioning like normal ground items.
     *
     * @return {@code true} if this item can be dropped. Otherwise, {@code false}.
     */
    public boolean isDroppable() {
        return isDroppable;
    }

    /**
     * @return {@code true} if this item can be traded between players without restrictions.
     */
    public boolean isTradable() {
        return isTradable;
    }

    /**
     * @return {@code true} if this item can contain an amount greater than one per slot.
     */
    public boolean isStackable() {
        return isStackable;
    }

    /**
     * @return {@code true} if this item was considered "P2P" only in the official version of the game.
     * Otherwise, {@code false}.
     */
    public boolean isMembersOnly() {
        return membersOnly;
    }

    /**
     * Gets the value of an item. This is typically defined by Jagex.
     * It does not represent the player determined value of an item.
     *
     * @return The value of the item. Note: this is NOT street value.
     */
    public int getValue() {
        return itemValue;
    }

    /**
     * @return The weight of the item in kilograms.
     */
    public double getWeight() {
        return itemWeight;
    }

    /**
     * Gets the slot.
     *
     * @return The slot.
     *//*
        public int getSlot() {
            return slot;
        }
    }*/

    public enum DegradableType {
        recoil(), dharoks_helmet(), dharoks_plate(), dharoks_legs(), dharoks_axe(), torags_helm(), torags_plate(), torags_legs(), torags_hammers(), ahrims_hood(), ahrims_top(), ahrims_bottom(), ahrims_staff(), karils_coif(), karils_top(), karils_bottom(), karils_bow(), veracs_helm(), veracs_plate(), veracs_skirt(), veracs_flail(), guthans_helm(), guthans_plate(), guthans_legs(), guthans_spear(), chaotic_maul(), chaotic_crossbow(), chaotic_longsword(), chaotic_rapier(), crystal_bow(), vesta_spear(), vesta_longsword(), vesta_plateskirt(), vesta_chainbody(), statius_platebody(), statius_platelegs(), statius_helm(), statius_hammer();

        DegradableType() {
        }

        public static DegradableType getType(String set) {
            for (DegradableType type : DegradableType.values()) {
                String name = type.name().replace("_", " ");
                if (set.equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }
    }

    public static class EquipmentDefinition {
        private final double absorbMelee;
        private final double absorbRanged;
        private final double absorbMagic;
        private final double magicDamage;
        private final int defensiveStab;
        private final int defensiveSlash;
        private final int defensiveCrush;
        private final int defensiveRanged;
        private final int defensiveMagic;
        private final int offensiveStab;
        private final int offensiveSlash;
        private final int offensiveCrush;
        private final int defensiveSummoning;
        private final int offensiveRanged;
        private final int offensiveMagic;
        private final int offensiveStrength;
        private final int rangedStrength;
        private final int prayerBonus;
        private final EquipmentSlot equipmentSlot;
        private final EquipmentType equipType;

        public EquipmentDefinition(final int defensiveStab, final int defensiveSlash, final int defensiveCrush,
                                   final int defensiveRanged, final int defensiveMagic, final int defensiveSummoning,
                                   final double absorbMelee, final double absorbRanged, double absorbMagic,
                                   final double magicDamage, final int offensiveStab, final int offensiveSlash,
                                   final int offensiveCrush, int offensiveRanged, final int offensiveMagic,
                                   final int offensiveStrength, final int rangedStrength, final int prayerBonus,
                                   final EquipmentSlot slot, final EquipmentType equipType) {
            super();
            this.defensiveStab = defensiveStab;
            this.defensiveSlash = defensiveSlash;
            this.defensiveCrush = defensiveCrush;
            this.defensiveRanged = defensiveRanged;
            this.defensiveMagic = defensiveMagic;
            this.defensiveSummoning = defensiveSummoning;
            this.absorbMelee = absorbMelee;
            this.absorbRanged = absorbRanged;
            this.absorbMagic = absorbMagic;
            this.offensiveStab = offensiveStab;
            this.offensiveSlash = offensiveSlash;
            this.offensiveCrush = offensiveCrush;
            this.offensiveRanged = offensiveRanged;
            this.offensiveMagic = offensiveMagic;
            this.magicDamage = magicDamage;
            this.offensiveStrength = offensiveStrength;
            this.rangedStrength = rangedStrength;
            this.prayerBonus = prayerBonus;
            this.equipmentSlot = slot;
            this.equipType = equipType;
        }

        public int getDefensiveStab() {
            return defensiveStab;
        }

        public int getDefensiveSlash() {
            return defensiveSlash;
        }

        public int getDefensiveCrush() {
            return defensiveCrush;
        }

        public int getDefensiveRanged() {
            return defensiveRanged;
        }

        public int getDefensiveMagic() {
            return defensiveMagic;
        }

        public int getOffensiveStab() {
            return offensiveStab;
        }

        public int getOffensiveSlash() {
            return offensiveSlash;
        }

        public int getOffensiveCrush() {
            return offensiveCrush;
        }

        public int getOffensiveRanged() {
            return offensiveRanged;
        }

        public int getOffensiveMagic() {
            return offensiveMagic;
        }

        public int getOffensiveStrength() {
            return offensiveStrength;
        }

        public int getRangedStrength() {
            return rangedStrength;
        }

        public int getPrayerBonus() {
            return prayerBonus;
        }

        public EquipmentSlot getSlot() {
            return equipmentSlot;
        }

        public int getSlotId() {
            return equipmentSlot.getSlotId();
        }

        public EquipmentType getEquipType() {
            return equipType;
        }

        public double getAbsorbMelee() {
            return absorbMelee;
        }

        public double getAbsorbRanged() {
            return absorbRanged;
        }

        public double getAbsorbMagic() {
            return absorbMagic;
        }

        public double getMagicDamage() {
            return magicDamage;
        }

        public int getDefensiveSummoning() {
            return defensiveSummoning;
        }

        public boolean matchesEquipmentType(final EquipmentType equipmentType) {
            return this.equipType == equipmentType;
        }
    }

    public static class WeaponDefinition {
        private int interfaceId, childId;
        private int walk, run, block, stand;
        private double specialEnergy;
        private int animAccurate, animAggressive, animDefensive, animControlled;
        private int speedAccurate, speedAggressive, speedDefensive, speedControlled;
        private CombatType combatType;

        public WeaponDefinition(int interfaceId, int childId, int walk, int run, int block,
                                int stand, double specialEnergy, int animAccurate, int animAggressive, int animDefensive,
                                int animControlled, int speedAccurate, int speedAggressive, int speedDefensive, int speedControlled,
                                CombatType combatType) {
            super();
            this.interfaceId = interfaceId;
            this.childId = childId;
            this.walk = walk;
            this.run = run;
            this.block = block;
            this.stand = stand;
            this.specialEnergy = specialEnergy;
            this.animAccurate = animAccurate;
            this.animAggressive = animAggressive;
            this.animDefensive = animDefensive;
            this.animControlled = animControlled;
            this.speedAccurate = speedAccurate;
            this.speedAggressive = speedAggressive;
            this.speedDefensive = speedDefensive;
            this.speedControlled = speedControlled;
            this.combatType = combatType;
        }

        public int getInterfaceId() {
            return interfaceId;
        }

        public int getChildId() {
            return childId;
        }

        public int getWalk() {
            return walk;
        }

        public int getRun() {
            return run;
        }

        public int getBlock() {
            return block;
        }

        public int getStand() {
            return stand;
        }

        public double getSpecialEnergy() {
            return specialEnergy;
        }

        public int getAnimAccurate() {
            return animAccurate;
        }

        public int getAnimAggressive() {
            return animAggressive;
        }

        public int getAnimDefensive() {
            return animDefensive;
        }

        public int getAnimControlled() {
            return animControlled;
        }

        public int getSpeedAccurate() {
            return speedAccurate;
        }

        public int getSpeedAggressive() {
            return speedAggressive;
        }

        public int getSpeedDefensive() {
            return speedDefensive;
        }

        public int getSpeedControlled() {
            return speedControlled;
        }

        public CombatType getCombatType() {
            return combatType;
        }
    }

    /*public enum EquipmentType {
     *//**
     * Item is full plate (sleeves covered)
     *//*
        PLATEBODY("Platebody", Equipment.PLATE_SLOT),

        *//**
     * Item covers over hair
     *//*
        FULL_HELM("Full helm", Equipment.HELMET_SLOT),

        *//**
     * Item covers over head fully
     *//*
        FULL_MASK("Full mask", Equipment.HELMET_SLOT);

        *//**
     * The description.
     *//*
        private String description;

        *//**
     * The slot.
     *//*
        private int slot;

        *//**
     * Creates the equipment type.
     *
     * @param description
     *            The description.
     * @param slot
     *            The slot.
     *//*
        private EquipmentType(String description, int slot) {
            this.description = description;
            this.slot = slot;
        }

        */

    /**
     * Gets the description.
     *
     * @return The description.
     *//*
        public String getDescription() {
            return description;
        }

        */

    public static class RangedDefinition {

        private List<Integer> ammoAllowed = new ArrayList<>();

        private int projectile, drawback;
        private boolean usesAmmo;

        public RangedDefinition(int projectile, int drawback, boolean uses_ammo_slot_strength) {
            super();
            this.projectile = projectile;
            this.drawback = drawback;
            this.usesAmmo = uses_ammo_slot_strength;
        }

        public int getProjectile() {
            return projectile;
        }

        public int getDrawback() {
            return drawback;
        }

        public List<Integer> getAmmoAllowed() {
            return ammoAllowed;
        }

        public boolean usesAmmo() {
            return usesAmmo;
        }
    }
}
