package com.paragon464.gameserver.model.entity.mob.player;

import com.paragon464.gameserver.model.entity.mob.player.AttackVars.CombatSkill;
import com.paragon464.gameserver.model.entity.mob.player.AttackVars.CombatStyle;
import com.paragon464.gameserver.model.content.skills.magic.StaffInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttackInterfaceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttackInterfaceConfig.class);

    public AttackInterfaceConfig() {
    }

    public static void setButtonForAttackStyle(Player p, int interfaceId) {
        if (interfaceId == -1) {
            return;
        }
        AttackVars av = p.getAttackVars();
        CombatSkill type = av.getSkill();
        CombatStyle type2 = av.getStyle();
        int slot = av.getSlot();
        int button = -1;
        switch (interfaceId) {
            case 87://Guthans spear
                if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) || slot == 1) {
                    button = 3;
                    p.getFrames().sendVarp(43, 1);
                } else if (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 5;
                    p.getFrames().sendVarp(43, 3);
                    av.setSlot(3);
                }
                break;
            case 92: // Unarmed
                if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) || slot == 1) {
                    button = 3;
                    p.getFrames().sendVarp(43, 1);
                } else if (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 4;
                    p.getFrames().sendVarp(43, 2);
                    av.setSlot(2);
                }
                break;

            case 93: // Whip attack interface.
                p.getFrames().sendVarp(43, 0);
                button = 2;
                /*
                 * if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                 * p.getFrames().sendVarp(43, 0); button = 2; } else if
                 * (type.equals(AttackVars.CombatSkill.CONTROLLED) || slot == 1) {
                 * p.getFrames().sendVarp(43, 1); button = 3; } else if
                 * (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 ||
                 * slot == 3) { p.getFrames().sendVarp(43, 2); av.setSlot(2); button
                 * = 4; }
                 */
                break;

            case 89: // Dagger attack interface.
                if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if ((type.equals(AttackVars.CombatSkill.AGGRESSIVE) && type2.equals(AttackVars.CombatStyle.STAB))
                    || slot == 1) {
                    button = 3;
                    p.getFrames().sendVarp(43, 1);
                } else if ((type.equals(AttackVars.CombatSkill.AGGRESSIVE) && type2.equals(AttackVars.CombatStyle.SLASH))) {
                    button = 4;
                    p.getFrames().sendVarp(43, 2);
                } else if (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 5;
                    p.getFrames().sendVarp(43, 3);
                    av.setSlot(3);
                }
                break;

            case 81: // Longsword/scimitar attack interface.
                if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) || slot == 1) {
                    button = 3;
                    p.getFrames().sendVarp(43, 1);
                } else if (type.equals(AttackVars.CombatSkill.CONTROLLED)) {
                    button = 4;
                    p.getFrames().sendVarp(43, 2);
                } else if (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 5;
                    p.getFrames().sendVarp(43, 3);
                    av.setSlot(3);
                }
                break;

            case 78: // Claw attack interface.
                if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) || slot == 1) {
                    button = 5;
                    p.getFrames().sendVarp(43, 1);
                } else if (type.equals(AttackVars.CombatSkill.CONTROLLED)) {
                    button = 4;
                    p.getFrames().sendVarp(43, 2);
                } else if (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 3;
                    p.getFrames().sendVarp(43, 3);
                    av.setSlot(3);
                }
                break;

            case 82:// Godsword attack interface.
                if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) && type2.equals(AttackVars.CombatStyle.SLASH)
                    || slot == 1) {
                    button = 3;
                    p.getFrames().sendVarp(43, 1);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) && type2.equals(AttackVars.CombatStyle.CRUSH)) {
                    button = 4;
                    p.getFrames().sendVarp(43, 2);
                } else if (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 5;
                    p.getFrames().sendVarp(43, 3);
                    av.setSlot(3);
                }
                break;

            case 88: // Mace attack interface.
                if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) || slot == 1) {
                    button = 3;
                    p.getFrames().sendVarp(43, 1);
                } else if (type.equals(AttackVars.CombatSkill.CONTROLLED)) {
                    button = 4;
                    p.getFrames().sendVarp(43, 2);
                } else if (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 5;
                    p.getFrames().sendVarp(43, 3);
                    av.setSlot(3);
                }
                break;

            case 76: // Granite maul attack interface.
                if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) || slot == 1) {
                    button = 4;
                    p.getFrames().sendVarp(43, 1);
                } else if (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 3;
                    p.getFrames().sendVarp(43, 2);
                    av.setSlot(2);
                }
                break;

            case 77: // Bow attack interface.
            case 79: // crossbow attack interface
            case 473:// chinchompa
                if (type2.equals(AttackVars.CombatStyle.RANGE_ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if (type2.equals(AttackVars.CombatStyle.RANGE_RAPID) || slot == 1) {
                    button = 4;
                    p.getFrames().sendVarp(43, 1);
                } else if (type2.equals(AttackVars.CombatStyle.RANGE_DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 3;
                    p.getFrames().sendVarp(43, 2);
                    av.setSlot(2);
                }
                break;

            case 75: // Battleaxe attack interface.
                if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) && type2.equals(AttackVars.CombatStyle.SLASH)
                    || slot == 1) {
                    button = 5;
                    p.getFrames().sendVarp(43, 1);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) && type2.equals(AttackVars.CombatStyle.CRUSH)) {
                    button = 4;
                    p.getFrames().sendVarp(43, 2);
                } else if (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 3;
                    p.getFrames().sendVarp(43, 3);
                    av.setSlot(3);
                }
                break;

            case 91: // Thrown weapon
                if (type2.equals(AttackVars.CombatStyle.RANGE_ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if (type2.equals(AttackVars.CombatStyle.RANGE_RAPID) || slot == 1) {
                    button = 3;
                    p.getFrames().sendVarp(43, 1);
                } else if (type2.equals(AttackVars.CombatStyle.RANGE_DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 4;
                    p.getFrames().sendVarp(43, 2);
                    av.setSlot(2);
                }
                break;

            case 85: // Spear
            case 84://spear
                if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                    button = 2;
                    p.getFrames().sendVarp(43, 0);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) || slot == 1) {
                    button = 3;
                    p.getFrames().sendVarp(43, 1);
                } else if (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 4;
                    p.getFrames().sendVarp(43, 2);
                    av.setSlot(2);
                }
                break;

            case 90: // Staff interface
                if (type.equals(AttackVars.CombatSkill.ACCURATE) || slot == 0) {
                    button = 1;
                    p.getFrames().sendVarp(43, 0);
                } else if (type.equals(AttackVars.CombatSkill.AGGRESSIVE) || slot == 1) {
                    button = 2;
                    p.getFrames().sendVarp(43, 1);
                } else if (type.equals(AttackVars.CombatSkill.DEFENSIVE) || slot == 2 || slot == 3) {
                    button = 3;
                    p.getFrames().sendVarp(43, 2);
                    av.setSlot(2);
                }
                break;
        }
        configureButton(p, interfaceId, button);
    }

    public static void configureButton(Player p, int interfaceId, int button) {
        AttackVars av = p.getAttackVars();
        LOGGER.debug("[ATTACK STYLES]: {}", interfaceId);
        switch (interfaceId) {
            case 92: // Unarmed attack interface.
                switch (button) {
                    case 2: // Punch (Attack XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.ACCURATE);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(0);
                        break;

                    case 3: // Kick (Strength XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(1);
                        break;

                    case 4: // Block (Defence XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(2);
                        break;
                }
                break;

            case 93: // Whip attack interface.
                switch (button) {
                    case 2: // Flick (Attack XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.ACCURATE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(0);
                        break;

                    case 3: // Lash (Shared XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.CONTROLLED);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(1);
                        break;

                    case 4: // Deflect (Defence XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(2);
                        break;
                }
                break;

            case 89: // Dagger attack interface.
                switch (button) {
                    case 2: // Stab (Attack XP) - Stab
                        av.setSkill(AttackVars.CombatSkill.ACCURATE);
                        av.setStyle(AttackVars.CombatStyle.STAB);
                        av.setSlot(0);
                        break;

                    case 3: // Lunge (Strength XP) - Stab
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.STAB);
                        av.setSlot(1);
                        break;

                    case 4: // Slash (Strength XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(2);
                        break;

                    case 5: // Block (Defence XP) - Stab
                        av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                        av.setStyle(AttackVars.CombatStyle.STAB);
                        av.setSlot(3);
                        break;
                }
                break;

            case 82: // Longsword/scimitar attack interface.
                switch (button) {
                    case 2: // Chop (Attack XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.ACCURATE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(0);
                        break;

                    case 3: // Slash (Strength XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(1);
                        break;

                    case 4: // Smash (Strength XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(2);
                        break;

                    case 5: // Block (Defence XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(3);
                        break;
                }
                break;

            case 78: // Claw attack interface.
                switch (button) {
                    case 2: // Chop (Attack XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.ACCURATE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(0);
                        break;

                    case 5: // Slash (Strength XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(1);
                        break;

                    case 4: // Lunge (Shared XP) - Stab
                        av.setSkill(AttackVars.CombatSkill.CONTROLLED);
                        av.setStyle(AttackVars.CombatStyle.STAB);
                        av.setSlot(2);
                        break;

                    case 3: // Block (Defence XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(3);
                        break;
                }
                break;

            case 81: // Godsword attack interface.
                switch (button) {
                    case 2: // Chop (Attack XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.ACCURATE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(0);
                        break;

                    case 3: // Slash (Strength XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(1);
                        break;

                    case 4: // Lunge (Shared XP) - Stab
                        av.setSkill(AttackVars.CombatSkill.CONTROLLED);
                        av.setStyle(AttackVars.CombatStyle.STAB);
                        av.setSlot(2);
                        break;

                    case 5: // Block (Defence XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(3);
                        break;
                }
                break;

            case 88: // Mace attack interface.
                switch (button) {
                    case 2: // Pound (Attack XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.ACCURATE);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(0);
                        break;

                    case 3: // Pummel (Strength XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(1);
                        break;

                    case 4: // Spike (Shared XP) - Stab
                        av.setSkill(AttackVars.CombatSkill.CONTROLLED);
                        av.setStyle(AttackVars.CombatStyle.STAB);
                        av.setSlot(2);
                        break;

                    case 5: // Block (Defence XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(3);
                        break;
                }
                break;

            case 76: // Granite maul attack interface.
                switch (button) {
                    case 2: // Pound (Attack XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.ACCURATE);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(0);
                        break;

                    case 4: // Pummel (Strength XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(1);
                        break;

                    case 3: // Block (Defence XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(2);
                        break;
                }
                break;

            case 77: // Bow attack interface.
            case 79:// crossbow attack interface
                switch (button) {
                    case 2: // Accurate (Range XP) - Accurate
                        av.setSkill(AttackVars.CombatSkill.RANGE);
                        av.setStyle(AttackVars.CombatStyle.RANGE_ACCURATE);
                        av.setSlot(0);
                        break;

                    case 4: // Rapid (Range XP) - Rapid
                        av.setSkill(AttackVars.CombatSkill.RANGE);
                        av.setStyle(AttackVars.CombatStyle.RANGE_RAPID);
                        av.setSlot(1);
                        break;

                    case 3: // Longrange (Range XP) - Defensive
                        av.setSkill(AttackVars.CombatSkill.RANGE);
                        av.setStyle(AttackVars.CombatStyle.RANGE_DEFENSIVE);
                        av.setSlot(2);
                        break;
                }
                break;

            case 75: // Battleaxe attack interface.
                switch (button) {
                    case 2: // Chop (Attack XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.ACCURATE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(0);
                        break;

                    case 5: // Hack (Strength XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(1);
                        break;

                    case 4: // Smash (Strength XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(2);
                        break;

                    case 3: // Block (Defence XP) - Slash
                        av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(3);
                        break;
                }
                break;
            case 473:// Chinchompa
                switch (button) {
                    case 2: // Accurate (Range XP) - Accurate
                        av.setSkill(AttackVars.CombatSkill.RANGE);
                        av.setStyle(AttackVars.CombatStyle.RANGE_ACCURATE);
                        av.setSlot(0);
                        break;

                    case 4: // Rapid (Range XP) - Rapid
                        av.setSkill(AttackVars.CombatSkill.RANGE);
                        av.setStyle(AttackVars.CombatStyle.RANGE_RAPID);
                        av.setSlot(1);
                        break;

                    case 3: // Longrange (Range XP) - Defensive
                        av.setSkill(AttackVars.CombatSkill.RANGE);
                        av.setStyle(AttackVars.CombatStyle.RANGE_DEFENSIVE);
                        av.setSlot(2);
                        break;
                }
                break;
            case 91: // Thrown weapon
                switch (button) {
                    case 2: // Accurate (Range XP) - Accurate
                        av.setSkill(AttackVars.CombatSkill.RANGE);
                        av.setStyle(AttackVars.CombatStyle.RANGE_ACCURATE);
                        av.setSlot(0);
                        break;

                    case 3: // Rapid (Range XP) - Rapid
                        av.setSkill(AttackVars.CombatSkill.RANGE);
                        av.setStyle(AttackVars.CombatStyle.RANGE_RAPID);
                        av.setSlot(1);
                        break;

                    case 4: // Longrange (Range XP) - Defensive
                        av.setSkill(AttackVars.CombatSkill.RANGE);
                        av.setStyle(AttackVars.CombatStyle.RANGE_DEFENSIVE);
                        av.setSlot(2);
                        break;
                }
                break;
            case 87://Guthans spear
                switch (button) {
                    case 2:// Bash (Attack XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.CONTROLLED);
                        av.setStyle(AttackVars.CombatStyle.STAB);
                        av.setSlot(0);
                        break;
                    case 3:// Controlled
                        av.setSkill(AttackVars.CombatSkill.CONTROLLED);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(1);
                        break;
                    case 4://Controlled - crush
                        av.setSkill(AttackVars.CombatSkill.CONTROLLED);
                        av.setStyle(AttackVars.CombatStyle.CRUSH);
                        av.setSlot(2);
                        break;
                    case 5://Controlled - stab
                        av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                        av.setStyle(AttackVars.CombatStyle.STAB);
                        av.setSlot(3);
                        break;
                }
                break;
            case 84: // Spear
                switch (button) {
                    case 2:// Bash (Attack XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.CONTROLLED);
                        av.setStyle(AttackVars.CombatStyle.STAB);
                        av.setSlot(0);
                        break;

                    case 3:// Pound (Strength XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                        av.setStyle(AttackVars.CombatStyle.SLASH);
                        av.setSlot(1);
                        break;

                    case 4:// Block (Defense XP) - Crush
                        av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                        av.setStyle(AttackVars.CombatStyle.STAB);
                        av.setSlot(2);
                        break;
                }
                break;

            case 90: // Staff interface
            case 85: // Wand interface
                if (interfaceId == 90) {
                    if (button >= 1 && button <= 3) {
                        if (p.getAttributes().isSet("autocast_spell")) {
                            int type = p.getCombatState().getTarget() != null ? 1 : 0;
                            p.getCombatState().end(type);
                            StaffInterface.cancel(p, true);
                        }
                    }
                    switch (button) {
                        case 1: // Bash (Attack XP) - Crush
                            av.setSkill(AttackVars.CombatSkill.ACCURATE);
                            av.setStyle(AttackVars.CombatStyle.CRUSH);
                            av.setSlot(0);
                            break;
                        case 2: // Pound (Strength XP) - Crush
                            av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                            av.setStyle(AttackVars.CombatStyle.CRUSH);
                            av.setSlot(1);
                            break;
                        case 3: // Focus (Defense XP) - Crush
                            av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                            av.setStyle(AttackVars.CombatStyle.CRUSH);
                            av.setSlot(2);
                            break;
                    }
                } else if (interfaceId == 85) {
                    /*
                     * if (button >= 2 && button <= 4) { if
                     * (p.getAttributes().isSet("autocastspell")) { int type =
                     * p.getCombatState().getTarget() != null ? 1 : 0;
                     * MainCombat.endCombat(p, type); } StaffsInterface.cancel(p,
                     * true); }
                     */
                    switch (button) {
                        case 2: // Bash (Attack XP) - Crush
                            av.setSkill(AttackVars.CombatSkill.ACCURATE);
                            av.setStyle(AttackVars.CombatStyle.CRUSH);
                            av.setSlot(0);
                            break;
                        case 3: // Pound (Strength XP) - Crush
                            av.setSkill(AttackVars.CombatSkill.AGGRESSIVE);
                            av.setStyle(AttackVars.CombatStyle.CRUSH);
                            av.setSlot(1);
                            break;
                        case 4: // Focus (Defense XP) - Crush
                            av.setSkill(AttackVars.CombatSkill.DEFENSIVE);
                            av.setStyle(AttackVars.CombatStyle.CRUSH);
                            av.setSlot(2);
                            break;
                    }
                }
                break;
        }
    }
}
