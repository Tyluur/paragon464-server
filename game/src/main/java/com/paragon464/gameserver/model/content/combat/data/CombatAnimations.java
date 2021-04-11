package com.paragon464.gameserver.model.content.combat.data;

import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.npc.NPC;
import com.paragon464.gameserver.model.entity.mob.player.AttackVars;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.item.ItemDefinition;

public class CombatAnimations {

    /**
     * Gets attack animation.
     *
     * @param killer
     * @param weaponId
     * @return
     */
    public static int getAttackAnim(Mob killer, int weaponId) {
        ItemDefinition definition = ItemDefinition.forId(weaponId);
        if (killer.isNPC()) {
            NPC npc = (NPC) killer;
            if (npc.getCombatDefinition() == null)
                return 404;
            return npc.getCombatDefinition().getAttackAnim();
        }
        Player p = (Player) killer;
        AttackVars av = p.getAttackVars();
        if (weaponId == -1) {
            if (av.getSkill() == AttackVars.CombatSkill.AGGRESSIVE) {
                return 423;
            } else {
                return 422;
            }
        }
        ItemDefinition.WeaponDefinition wepDef = definition.weaponDefinition;
        if (wepDef == null)
            return 422;
        if (av.getSkill().name().equalsIgnoreCase("RANGE")) {
            switch (av.getStyle().name()) {
                case "RANGE_ACCURATE":
                    return wepDef.getAnimAccurate();
                case "RANGE_RAPID":
                    return wepDef.getAnimAggressive();
                case "RANGE_DEFENSIVE":
                    return wepDef.getAnimDefensive();
            }
        }
        switch (av.getSkill().name()) {
            case "ACCURATE":
                int accurateAnim = wepDef.getAnimAccurate();
                if (p.getDetails().getClientMode() > 530) {
                    if (accurateAnim == 2067) {
                        accurateAnim = 2066;
                    } else if (accurateAnim == 451) {
                        accurateAnim = 12029;
                    }
                }
                return accurateAnim;
            case "AGGRESSIVE":
                int aggressiveAnim = wepDef.getAnimAggressive();
                if (p.getDetails().getClientMode() > 530) {
                    if (aggressiveAnim == 2067) {
                        aggressiveAnim = 2066;
                    } else if (aggressiveAnim == 451) {
                        aggressiveAnim = 12029;
                    }
                }
                return aggressiveAnim;
            case "DEFENSIVE":
                int defensiveAnim = wepDef.getAnimDefensive();
                if (p.getDetails().getClientMode() > 530) {
                    if (defensiveAnim == 451) {
                        defensiveAnim = 12029;
                    }
                }
                return defensiveAnim;
            case "CONTROLLED":
                int controlledAnim = wepDef.getAnimControlled();
                if (p.getDetails().getClientMode() > 530) {
                    if (controlledAnim == 451) {
                        controlledAnim = 12028;
                    }
                }
                return controlledAnim;
        }
        return 422;
    }

    /**
     * Gets the mob defence animation.
     *
     * @param mob
     * @return
     */
    public static int getDefendAnim(Mob mob) {
        if (mob.isNPC()) {
            return ((NPC) mob).getCombatDefinition().getDefendAnim();
        }
        int anim = 404;
        Player p = (Player) mob;
        int weaponId = p.getEquipment().getItemInSlot(3);
        int shield = p.getEquipment().getItemInSlot(5);
        boolean shieldPriority = false;
        if (shield != -1) {
            ItemDefinition def = ItemDefinition.forId(shield);
            ItemDefinition.WeaponDefinition shieldDef = def.weaponDefinition;
            if (shieldDef != null) {
                String name = def.getName().toLowerCase();
                anim = shieldDef.getBlock();
                shieldPriority = ((shield >= 8844 && shield <= 8850) || shield == 1540 || shield == 11283 || shield == 6524
                    || name.endsWith("kiteshield") || name.endsWith("sq shield") || weaponId == -1);
            }
        }
        if (weaponId != -1 && !shieldPriority) {
            ItemDefinition weaponDefinition = ItemDefinition.forId(weaponId);
            if (weaponDefinition != null) {
                ItemDefinition.WeaponDefinition weapDef = weaponDefinition.weaponDefinition;
                if (weapDef != null) {
                    if (weapDef.getBlock() != -1) {
                        anim = weapDef.getBlock();
                    }
                }
            }
        } else if (weaponId == -1) {
            anim = 424;
        }
        return anim;
    }

    public static int getStandAnimation(Player player) {
        int weaponId = player.getEquipment().getItemInSlot(3);
        if (weaponId != -1) {
            ItemDefinition def = ItemDefinition.forId(weaponId);
            if (def != null) {
                ItemDefinition.WeaponDefinition wepDef = def.weaponDefinition;
                if (wepDef == null)
                    return 808;
                return wepDef.getStand();
            }
        }
        return 808;
    }

    public static int getRunAnimation(Player player) {
        int weaponId = player.getEquipment().getItemInSlot(3);
        if (weaponId != -1) {
            ItemDefinition def = ItemDefinition.forId(weaponId);
            if (def != null) {
                ItemDefinition.WeaponDefinition wepDef = def.weaponDefinition;
                if (wepDef == null)
                    return 824;
                return wepDef.getRun();
            }
        }
        return 824;
    }

    public static int getTurnAnimation(Player player) {
        int weaponId = player.getEquipment().getItemInSlot(3);
        if (weaponId != -1) {
            return getWalkAnimation(player);
        }
        return 823;
    }

    public static int getWalkAnimation(Player player) {
        int weaponId = player.getEquipment().getItemInSlot(3);
        if (weaponId != -1) {
            ItemDefinition def = ItemDefinition.forId(weaponId);
            if (def != null) {
                ItemDefinition.WeaponDefinition wepDef = def.weaponDefinition;
                if (wepDef == null)
                    return 819;
                return wepDef.getWalk();
            }
        }
        return 819;
    }

    public static int getTurn90ClockwiseAnimation(Player player) {
        int weaponId = player.getEquipment().getItemInSlot(3);
        if (weaponId != -1) {
            return getWalkAnimation(player);
        }
        return 821;
    }

    public static int getTurn90CounterClockwiseAnimation(Player player) {
        int weaponId = player.getEquipment().getItemInSlot(3);
        if (weaponId != -1) {
            return getWalkAnimation(player);
        }
        return 822;
    }

    public static int getTurn180Animation(Player player) {
        int weaponId = player.getEquipment().getItemInSlot(3);
        if (weaponId != -1) {
            return getWalkAnimation(player);
        }
        return 820;
    }
}
