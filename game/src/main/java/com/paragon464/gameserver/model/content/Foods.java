package com.paragon464.gameserver.model.content;

import com.paragon464.gameserver.model.entity.mob.masks.Animation.AnimationPriority;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.content.combat.data.Weapons;
import com.paragon464.gameserver.model.content.minigames.duelarena.DuelBattle;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.util.NumberUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Matrix src
 */
public class Foods {

    public static boolean eat(final Player player, Item item, int slot) {
        Food food = Food.forId(item.getId());
        if (food == null)
            return false;
        if (!canEat(player, food, item, slot)) {
            return false;
        }
        if (player.getCombatState().getFoodTimer() > 0 && !food.isComboFood()) {
            return false;
        }
        if (player.getCombatState().getPotionTimer() > 0 && food.isComboFood()) {
            return false;
        }
        int weapon = player.getEquipment().getItemInSlot(3);
        String name = item.getDefinition().getName().toLowerCase();
        player.getFrames().sendMessage(getStartText(item));
        player.playAnimation(829, AnimationPriority.HIGH);
        if (player.getCombatState().getTarget() != null) {
            player.getCombatState().end(2);
        } else {
            player.getCombatState().end(0);
        }
        int foodTimer = name.contains("half") ? 1 : 3;
        if (name.contains("anchovy pizza") && !name.contains("half")) {
            foodTimer = 1;
        }
        if (!food.isComboFood()) {
            player.getCombatState().setFoodTimer(foodTimer);
            if (player.getCombatState().getAttackTimer() + 3 <= Weapons.speed(player, weapon) + 3) {
                player.getCombatState().increaseAttackTimer(3);
            }
        } else {
            int potTime = 2;
            if (name.contains("anchovy pizza") && !name.contains("half")) {
                potTime = 1;
            }
            player.getCombatState().setPotionTimer(potTime);
            if (player.getCombatState().getAttackTimer() + 3 <= Weapons.speed(player, weapon) + 3) {
                player.getCombatState().increaseAttackTimer(3);
            }
        }
        if (food.getNewId() > 0) {
            player.getInventory().set(new Item(food.getNewId(), 1), slot, true);
        } else {
            if (!item.getDefinition().isStackable()) {
                player.getInventory().set(null, slot, true);
            } else {
                player.getInventory().deleteItem(new Item(item.getId(), 1));
            }
        }
        player.getInventory().refresh();
        int hp = player.getHp();
        player.heal(food.getHeal());
        if (player.getHp() > hp) {
            if (getEndText(item) != null) {
                player.getFrames().sendMessage(getEndText(item));
            }
        }
        if (food.effect != null) {
            food.effect.effect(player);
        }
        return true;
    }

    public static boolean canEat(Player player, Food food, Item item, int slot) {
        if (player.getCombatState().isDead() || item == null || player.getAttributes().is("stopActions")) {
            return false;
        }
        if (!player.getControllerManager().processFood(food)) {
        	return false;
        }
        DuelBattle duel_battle = player.getVariables().getDuelBattle();
        if (duel_battle != null) {
            if (duel_battle.foodsDisabled()) {
                player.getFrames().sendMessage("Your foods have been disabled in this duel.");
                return false;
            }
        }
        return true;
    }

    private static String getStartText(Item item) {
        String name = item.getDefinition().getName().toLowerCase();
        switch (item.getId()) {
            case 10476:
                return "The sugary goodness heals some energy.";
            case 1993:
                return "You drink some of the wine.";
            case 1989:
                return "You drink the rest of the wine.";
        }

        if (name.matches("(.*\\s)?(jug|cup|bottle|glass|wine|beer|stout|bitter|ale|cider|brew|rum|brandy|gin|vodka|tankard|folly|respite|mead)(\\s.*)?"))
            return "You drink the " + name + ".";
        return "You eat the " + name + ".";
    }

    private static String getEndText(Item item) {
        // String name = item.getDefinition().getName().toLowerCase();
        switch (item.getId()) {
            case 10476:
                return null;
        }
        return "It heals some health.";
    }

    public enum Food {

        /**
         * Fish
         */

        PURPLE_SWEETS(10476, 2, Effect.PURPLE_SWEETS_EFFECT),

        CRAFISH(13433, 2),

        ANCHOVIE(319, 1),

        SHRIMP(315, 3),

        KARAMBWANJI(3151, 3),

        SARDINE(325, 3),

        POISON_KARAMBWANJI(3146, 0, Effect.POISION_KARMAMWANNJI_EFFECT),

        KARAMBWANI(3144, 18, true),

        SLIMY_EEL(3381, 7 + NumberUtils.random(2)),

        RAINBOW_FISH(10136, 11),

        CAVE_EEL(5003, 8 + NumberUtils.random(2)),

        LAVA_EEL(2149, 7 + NumberUtils.random(2)),

        HERRING(347, 5),

        MACKEREL(355, 6),

        TROUT(333, 7),

        COD(339, 7),

        PIKE(351, 8),

        SALMON(329, 9),

        TUNA(361, 10),

        LOBSTER(379, 12),

        BASS(365, 13),

        SWORDFISH(373, 14),

        MONKFISH(7946, 16),

        SHARK(385, 20),

        TURTLE(397, 21),

        MANTA(391, 22),

        CAVEFISH(15266, 20),

        ROCKTAIL(15272, 23, 0, null, 10),

        /**
         * Meats
         */
        CHICKEN(2140, 3),

        MEAT(2142, 3), // TODO

        RABIT(3228, 5),

        ROAST_RABIT(7223, 7),

        ROASTED_BIRD_MEAT(9980, 6),

        CRAB_MEAT(7521, 10), // TODO

        ROASTED_BEAST_MEAT(9988, 8),

        CHOMPY(2878, 10),

        JUBBLY(7568, 15),

        OOMILE(2343, 14),

        /**
         * Pies
         */
        REDBERRY_PIE_FULL(2325, 5, 2333),

        REDBERRY_PIE_HALF(2333, 5, 2313),

        MEAT_PIE_FULL(2327, 6, 2331),

        MEAT_PIE_HALF(2331, 6, 2313),

        APPLE_PIE_FULL(2323, 7, 2335),

        APPLE_PIE_HALF(2335, 7, 2313),

        GARDEN_PIE_FULL(7178, 6, 7180, Effect.GARDEN_PIE),

        GARDEN_PIE_HALF(7180, 6, 2313, Effect.GARDEN_PIE),

        FISH_PIE_FULL(7188, 6, 7190, Effect.FISH_PIE),

        FISH_PIE_HALF(7188, 6, 2313, Effect.FISH_PIE),

        ADMIRAL_PIE_FULL(7198, 8, 7200, Effect.ADMIRAL_PIE),

        ADMIRAL_PIE_HALF(7200, 8, 2313, Effect.ADMIRAL_PIE),

        WILD_PIE_FULL(7208, 11, 7210, Effect.WILD_PIE),

        WILD_PIE_HALF(7210, 11, 2313, Effect.WILD_PIE),

        SUMMER_PIE_FULL(7218, 11, 7220, Effect.SUMMER_PIE),

        SUMMER_PIE_HALF(7220, 11, 2313, Effect.SUMMER_PIE),

        /**
         * Stews
         */

        STEW(2003, 11, 1923),

        SPICY_STEW(7513, 11, 1923, Effect.SPICY_STEW_EFFECT),

        CURRY(2011, 19, 1923),

        /**
         * Pizzas
         */
        PLAIN_PIZZA_FULL(2289, 7, 2291),

        PLAIN_PIZZA_HALF(2291, 7),

        MEAT_PIZZA_FULL(2293, 8, 2295),

        MEAT_PIZZA_HALF(2295, 8),

        ANCHOVIE_PIZZA_FULL(2297, 9, 2299, true),

        ANCHOVIE_PIZZA_HALF(2299, 9, true),

        PINEAPPLE_PIZZA_FULL(2301, 11, 2303),

        PINEAPPLE_PIZZA_HALF(2303, 11),

        /**
         * Potato Toppings
         */
        SPICEY_SAUCE(7072, 2, 1923),

        CHILLI_CON_CARNIE(7062, 14, 1923),

        SCRAMBLED_EGG(7078, 5, 1923),

        EGG_AND_TOMATO(7064, 8, 1923),

        FRIED_ONIONS(7084, 9, 1923),

        MUSHROOM_AND_ONIONS(7066, 11, 1923),

        FRIED_MUSHROOMS(7082, 5, 1923),

        TUNA_AND_CORN(7068, 13, 1923),

        /**
         * Baked Potato
         */
        BAKED_POTATO(6701, 4),

        POTATO_WITH_BUTTER(6703, 14),

        CHILLI_POTATO(7054, 14),

        POTATO_WITH_CHEESE(6705, 16),

        EGG_POTATO(7056, 16),

        MUSHROOM_AND_ONION_POTATO(7058, 20),

        TUNA_POTATO(7060, 24),

        /**
         * Gnome Food
         */
        TOAD_CRUNCHIES(2217, 8),

        SPICY_CRUNCHIES(2213, 7),

        WORM_CRUNCHIES(2205, 8),

        CHOCOCHIP_CRUNCHIES(9544, 7),

        FRUIT_BATTA(2277, 11),

        TOAD_BATTA(2255, 11),

        WORM_BATTA(2253, 11),

        VEGETABLE_BATTA(2281, 11),

        CHEESE_AND_TOMATO_BATTA(9535, 11),

        WORM_HOLE(2191, 12),

        VEG_BALL(2195, 12),

        PRE_MADE_VEG_BALL(2235, 12),

        TANGLED_TOAD_LEGS(2187, 15),

        CHOCOLATE_BOMB(2185, 15, true),

        /**
         * Misc
         */
        BEER(1917, 1, 1919, Effect.BEER_EFFECT),

        WINE(1993, 7, 1989, Effect.WINE_EFFECT),

        HALF_WINE(1989, 7, 1935, Effect.WINE_EFFECT),

        CAKE(1891, 4, 1893),

        TWO_THIRDS_CAKE(1893, 4, 1895),

        SLICE_OF_CAKE(1895, 4),

        CHOCOLATE_CAKE(1897, 4, 1899),

        TWO_THIRDS_CHOCOLATE_CAKE(1899, 4, 1901),

        CHOCOLATE_SLICE(1901, 4),

        FISHCAKE(7530, 11),

        BREAD(2309, 5),

        CABBAGE(1965, 1, Effect.CABAGE_MESSAGE),

        ONION(1957, 1, Effect.ONION_MESSAGE),

        EVIL_TURNIP(12134, 1),

        POT_OF_CREAM(2130, 1),

        CHEESE_WHEEL(18789, 2),

        THIN_SNAIL_MEAT(3369, 5 + NumberUtils.random(2)),

        LEAN_SNAIL_MEAT(3371, 8),

        FAT_SNAIL_MEAT(3373, 8 + NumberUtils.random(2));

        /**
         * A map of object ids to foods.
         */
        private static Map<Integer, Food> foods = new HashMap<>();

        /**
         * Populates the tree map.
         */
        static {
            for (final Food food : Food.values()) {
                foods.put(food.id, food);
            }
        }

        /**
         * The food id
         */
        private int id;
        /**
         * The healing health
         */
        private int heal;
        /**
         * The food is a combo food
         */
        private boolean comboFood;
        /**
         * The new food id if needed
         */
        private int newId;
        private int extraHP;
        /**
         * Our effect
         */
        private Effect effect;

        /**
         * Represents a food being eaten
         *
         * @param id   The food id
         * @param heal The healing health received
         */
        Food(int id, int heal) {
            this.id = id;
            this.heal = heal;
        }

        /**
         * Represents a food being eaten
         *
         * @param id    food id
         * @param heal  amount to heal
         * @param combo is it a combo food
         */
        Food(int id, int heal, boolean combo) {
            this.id = id;
            this.heal = heal;
            this.comboFood = combo;
        }

        Food(int id, int heal, int newId, boolean combo) {
            this.id = id;
            this.newId = newId;
            this.heal = heal;
            this.comboFood = combo;
        }

        /**
         * Represents a part of a food item being eaten (example: cake)
         *
         * @param id    The food id
         * @param heal  The heal amount
         * @param newId The new food id
         */
        Food(int id, int heal, int newId) {
            this(id, heal, newId, null);
        }

        Food(int id, int heal, int newId, Effect effect) {
            this(id, heal, newId, effect, 0);
        }

        Food(int id, int heal, int newId, Effect effect, int extraHP) {
            this.id = id;
            this.heal = heal;
            this.newId = newId;
            this.effect = effect;
            this.extraHP = extraHP;
        }

        Food(int id, int heal, Effect effect) {
            this(id, heal, 0, effect);
        }

        /**
         * Gets a food by an object id.
         *
         * @param itemId The object id.
         * @return The food, or <code>null</code> if the object is not a food.
         */
        public static Food forId(int itemId) {
            return foods.get(itemId);
        }

        /**
         * Gets the id.
         *
         * @return The id.
         */
        public int getId() {
            return id;
        }

        /**
         * Gets the exp amount.
         *
         * @return The exp amount.
         */
        public int getHeal() {
            return heal;
        }

        /**
         * Is this food a combo food
         *
         * @return
         */
        public boolean isComboFood() {
            return comboFood;
        }

        /**
         * Gets the new food id
         *
         * @return The new food id.
         */
        public int getNewId() {
            return newId;
        }

        public int getExtraHP() {
            return extraHP;
        }
    }

    public enum Effect {
        SUMMER_PIE {
            @Override
            public void effect(Object object) {
                // Player player = (Player) object;
                /*
                 * int runEnergy = (int) (player.getRunningEnergy() * 1.1); if
                 * (runEnergy > 100) runEnergy = 100;
                 * player.setRunningEnergy(runEnergy); int level =
                 * player.getSkillsMap().getLevel(Skills.AGILITY); int realLevel =
                 * player.getSkillsMap().getLevelForExperience(Skills.AGILITY);
                 * player.getSkillsMap().setLevel(Skills.AGILITY, level >=
                 * realLevel ? realLevel + 5 : level + 5);
                 */
            }
        },

        GARDEN_PIE {
            @Override
            public void effect(Object object) {
                // Player player = (Player) object;
                /*
                 * int level = player.getSkillsMap().getLevel(Skills.FARMING); int
                 * realLevel =
                 * player.getSkillsMap().getLevelForExperience(Skills.FARMING);
                 * player.getSkillsMap().setLevel(Skills.FARMING,level >= realLevel
                 * ? realLevel + 3 : level + 3);
                 */
            }
        },

        FISH_PIE {
            @Override
            public void effect(Object object) {
                // Player player = (Player) object;
                /*
                 * int level = player.getSkillsMap().getLevel(Skills.FISHING); int
                 * realLevel =
                 * player.getSkillsMap().getLevelForExperience(Skills.FISHING);
                 * player.getSkillsMap().setLevel(Skills.FISHING, level >=
                 * realLevel ? realLevel + 3 : level + 3);
                 */
            }
        },

        ADMIRAL_PIE {
            @Override
            public void effect(Object object) {
                // Player player = (Player) object;
                /*
                 * int level = player.getSkillsMap().getLevel(Skills.FISHING); int
                 * realLevel =
                 * player.getSkillsMap().getLevelForExperience(Skills.FISHING);
                 * player.getSkillsMap().setLevel(Skills.FISHING,level >= realLevel
                 * ? realLevel + 5 : level + 5);
                 */
            }
        },

        WILD_PIE {
            @Override
            public void effect(Object object) {
                // Player player = (Player) object;
                /*
                 * int level = player.getSkillsMap().getLevel(Skills.SLAYER); int
                 * realLevel =
                 * player.getSkillsMap().getLevelForExperience(Skills.SLAYER);
                 * player.getSkillsMap().setLevel(Skills.SLAYER, level >= realLevel
                 * ? realLevel + 4 : level + 4); int level2 =
                 * player.getSkillsMap().getLevel(Skills.RANGE); int realLevel2 =
                 * player.getSkillsMap().getLevelForExperience(Skills.RANGE);
                 * player.getSkillsMap().setLevel(Skills.RANGE, level2 >=
                 * realLevel2 ? realLevel2 + 4 : level2 + 4);
                 */
            }
        },

        SPICY_STEW_EFFECT {
            @Override
            public void effect(Object object) {
                // Player player = (Player) object;
                if (NumberUtils.random(100) > 5) {
                    // int level = player.getSkillsMap().getLevel(Skills.COOKING);
                    // int realLevel = player.getSkillsMap().getLevelForExperience(
                    // Skills.COOKING);
                    // player.getSkillsMap().setLevel(Skills.COOKING,
                    // level >= realLevel ? realLevel + 6 : level + 6);
                } else {
                    // int level = player.getSkillsMap().getLevel(Skills.COOKING);
                    // player.getSkillsMap().setLevel(Skills.COOKING, level <= 6 ?
                    // 0 : level - 6);
                }
            }
        },

        CABAGE_MESSAGE {
            @Override
            public void effect(Object object) {
                Player player = (Player) object;
                player.getFrames().sendMessage("You don't really like it much.");
            }
        },

        ONION_MESSAGE {
            @Override
            public void effect(Object object) {
                Player player = (Player) object;
                String gender = player.getAppearance().getGender() == 0 ? "man" : "woman";
                player.getFrames().sendMessage("It hurts to see a grown " + gender + " cry.");
            }
        },

        PURPLE_SWEETS_EFFECT {
            @Override
            public void effect(Object object) {
                Player player = (Player) object;
                double energy = player.getSettings().getEnergy();
                double percentage = 100 * 0.20;
                double total = energy + percentage;
                if (total > 100) {
                    total = 100;
                }
                player.getSettings().setEnergy((int) total, true);
            }
        },

        POISION_KARMAMWANNJI_EFFECT {
            @Override
            public void effect(Object object) {
                // Player player = (Player) object;
                // player.inflictDamage(5, true);
                // player.applyHit(new Hit(player, 50, HitLook.POISON_DAMAGE));
            }
        },

        BEER_EFFECT {
            @Override
            public void effect(Object object) {
                Player player = (Player) object;
                SkillType skill = SkillType.STRENGTH;
                int amount = (int) (player.getSkills().getCurrentLevel(skill) * 0.04);
                int level = player.getSkills().getLevel(skill);
                int currentLevel = player.getSkills().getCurrentLevel(skill);
                int levelToSet = (currentLevel + amount);
                if (levelToSet > (level + amount)) {
                    levelToSet = (level + amount);
                }
                player.getSkills().setCurrentLevel(skill, levelToSet);
                player.getFrames().sendSkillLevel(skill);
                //
                skill = SkillType.ATTACK;
                amount = (int) (player.getSkills().getCurrentLevel(skill) * 0.07);
                level = player.getSkills().getLevel(skill);
                currentLevel = player.getSkills().getCurrentLevel(skill);
                levelToSet = (currentLevel - amount);
                if (levelToSet < (level - amount)) {
                    levelToSet = (level - amount);
                }
                player.getSkills().setCurrentLevel(skill, levelToSet);
                player.getFrames().sendSkillLevel(skill);
            }
        },

        WINE_EFFECT {
            @Override
            public void effect(final Object object) {
                final Player player = (Player) object;

                player.getSkills().setCurrentLevel(SkillType.ATTACK, player.getSkills().getCurrentLevel(SkillType.ATTACK) - 2);
            }
        };

        public void effect(Object object) {
        }
    }
}
