package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.tickable.Tickable;

/**
 * Handles skill cape emotes.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 * @author Omar Saleh Assadi <omar@assadi.co.il>
 */
public class Skillcape {

    /**
     * Handles a skill cape emote: checks appropriate levels, finds the correct
     * animation + graphic, etc.
     *
     * @param player
     */
    public static boolean emote(final Player player) {
        SkillType skill = null;
        int skillcapeAnimation = -1, skillcapeGraphic = -1;
        int cape = player.getEquipment().getItemInSlot(1);
        if (cape <= 0) {
            player.getFrames().sendMessage("You need a Skillcape to perform this emote.");
            return false;
        }
        if (player.getAttributes().isSet("stopActions")) {
            return false;
        }
        boolean didEmote = true;
        int ticks = -1;
        switch (cape) {
            /*
             * Attack cape.
             */
            case 9747:
            case 9748:
                skill = SkillType.ATTACK;
                skillcapeAnimation = 4959;
                skillcapeGraphic = 823;
                ticks = 5;
                break;
            /*
             * Defense cape.skill
             */
            case 9753:
            case 9754:
                skill = SkillType.DEFENCE;
                skillcapeAnimation = 4961;
                skillcapeGraphic = 824;
                ticks = 9;
                break;
            /*
             * Strength cape.
             */
            case 9750:
            case 9751:
                skill = SkillType.STRENGTH;
                skillcapeAnimation = 4981;
                skillcapeGraphic = 828;
                ticks = 17;
                break;
            /*
             * Hitpoints cape.
             */
            case 9768:
            case 9769:
                skill = SkillType.HITPOINTS;
                skillcapeAnimation = 4971;
                skillcapeGraphic = 833;
                ticks = 7;
                break;
            /*
             * Ranging cape.
             */
            case 9756:
            case 9757:
                skill = SkillType.RANGED;
                skillcapeAnimation = 4973;
                skillcapeGraphic = 832;
                ticks = 9;
                break;
            /*
             * Prayer cape.
             */
            case 9759:
            case 9760:
                skill = SkillType.PRAYER;
                skillcapeAnimation = 4979;
                skillcapeGraphic = 829;
                ticks = 10;
                break;
            /*
             * Magic cape.
             */
            case 9762:
            case 9763:
                skill = SkillType.MAGIC;
                skillcapeAnimation = 4939;
                skillcapeGraphic = 813;
                ticks = 5;
                break;
            /*
             * Cooking cape.
             */
            case 9801:
            case 9802:
                skill = SkillType.COOKING;
                skillcapeAnimation = 4955;
                skillcapeGraphic = 821;
                break;
            /*
             * Woodcutting cape.
             */
            case 9807:
            case 9808:
                skill = SkillType.WOODCUTTING;
                skillcapeAnimation = 4957;
                skillcapeGraphic = 822;
                break;
            /*
             * Fletching cape.
             */
            case 9783:
            case 9784:
                skill = SkillType.FLETCHING;
                skillcapeAnimation = 4937;
                skillcapeGraphic = 812;
                break;
            /*
             * Fishing cape.
             */
            case 9798:
            case 9799:
                skill = SkillType.FISHING;
                skillcapeAnimation = 4951;
                skillcapeGraphic = 819;
                break;
            /*
             * Firemaking cape.
             */
            case 9804:
            case 9805:
                skill = SkillType.FIREMAKING;
                skillcapeAnimation = 4975;
                skillcapeGraphic = 831;
                break;
            /*
             * Crafting cape.
             */
            case 9780:
            case 9781:
                skill = SkillType.CRAFTING;
                skillcapeAnimation = 4949;
                skillcapeGraphic = 818;
                break;
            /*
             * Smithing cape.
             */
            case 9795:
            case 9796:
                skill = SkillType.SMITHING;
                skillcapeAnimation = 4943;
                skillcapeGraphic = 815;
                break;
            /*
             * Mining cape.
             */
            case 9792:
            case 9793:
                skill = SkillType.MINING;
                skillcapeAnimation = 4941;
                skillcapeGraphic = 814;
                break;
            /*
             * Herblore cape.
             */
            case 9774:
            case 9775:
                skill = SkillType.HERBLORE;
                skillcapeAnimation = 4969;
                skillcapeGraphic = 835;
                break;
            /*
             * Agility cape.
             */
            case 9771:
            case 9772:
                skill = SkillType.AGILITY;
                skillcapeAnimation = 4977;
                skillcapeGraphic = 830;
                break;
            /*
             * Thieving cape.
             */
            case 9777:
            case 9778:
                skill = SkillType.THIEVING;
                skillcapeAnimation = 4965;
                skillcapeGraphic = 826;
                break;
            /*
             * Slayer cape.
             */
            case 9786:
            case 9787:
                skill = SkillType.SLAYER;
                skillcapeAnimation = 4937;// need animation
                skillcapeGraphic = 812;// need graphic
                break;
            /*
             * Farming cape.
             */
            case 9810:
            case 9811:
                skill = SkillType.FARMING;
                skillcapeAnimation = 4963;
                skillcapeGraphic = 825;
                break;
            /*
             * Runecraft cape.
             */
            case 9765:
            case 9766:
                skill = SkillType.RUNECRAFTING;
                skillcapeAnimation = 4947;
                skillcapeGraphic = 817;
                break;
            /*
             * Hunter's cape
             */
            case 9948:
            case 9949:
                skill = SkillType.HUNTER;
                skillcapeAnimation = 5158;
                skillcapeGraphic = 907;
                break;
            /*
             * Construct. cape.
             */
            case 9789:
            case 9790:
                skill = SkillType.CONSTRUCTION;
                skillcapeAnimation = 4953;
                skillcapeGraphic = 820;
                break;
            /*
             * Summoning cape.
             */
            case 12169:
            case 12170:
                skill = SkillType.SUMMONING;
                skillcapeAnimation = 8525;
                skillcapeGraphic = 1515;
                break;
            /*
             * Quest cape.
             */
            case 9813:
                skillcapeAnimation = 4945;
                skillcapeGraphic = 816;
                break;

            default:
                didEmote = false;
                player.getFrames().sendMessage("You need a Skillcape to perform this emote.");
                break;
        }
        if (skill != null) {
            if (player.getSkills().getLevel(skill) == 99) {
                player.getAttributes().set("stopActions", true);
                player.getWalkingQueue().reset();
                player.playAnimation(skillcapeAnimation, AnimationPriority.HIGH);
                player.playGraphic(skillcapeGraphic);
                if (ticks != -1) {
                    player.submitTickable(new Tickable(ticks) {
                        @Override
                        public void execute() {
                            this.stop();
                            player.getAttributes().remove("stopActions");
                        }
                    });
                } else {
                    player.getAttributes().remove("stopActions");
                }
            } else {
                didEmote = false;
            }
        } else {
            didEmote = false;
        }
        return didEmote;
    }
}
