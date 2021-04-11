package com.paragon464.gameserver.model.content.combat.data;

import com.paragon464.gameserver.model.Projectiles;
import com.paragon464.gameserver.model.entity.mob.Mob;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.impl.Equipment;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.util.NumberUtils;

public class RangeEffects extends RangeData {

    public static void deleteAmmo(Player player, int amount, boolean special) {
        if (player.getAttributes().isSet("ammo_saved")) {
            return;
        }
        int ammo = player.getAttributes().getInt("range_ammo");
        player.getEquipment().deleteItem(ammo, amount);
        player.getEquipment().refresh();
        if (RangeData.throwingAmmo(player, ammo) && !player.getEquipment().hasItem(ammo)) {
            Equipment.setWeapon(player, true);
        }
    }

    public static boolean hasEnoughAmmo(Player player, int weapon, int arrows, boolean special) {
        int cape = player.getEquipment().getItemInSlot(Equipment.CAPE_SLOT);
        if ((cape == 10498 || cape == 10499 || cape == 20068) && weapon != 10034 && weapon != 10033) {
            final int avasChance = NumberUtils.random(10);

            if (avasChance < 9) {
                player.getAttributes().set("ammo_saved", true);
            }
        }
        if (weapon == 4212) {// crystal bows
            player.getAttributes().set("ammo_saved", true);
        }
        ItemDefinition weapon_def = ItemDefinition.forId(weapon);
        boolean throwable = false;
        if (weapon_def != null) {
            ItemDefinition.RangedDefinition range_def = weapon_def.rangedDefinition;
            if (range_def != null) {
                if (!range_def.usesAmmo()) {
                    throwable = true;
                }
            }
        }
        int ammo_check = 1;
        if (special) {
            switch (weapon) {
                case 861:// MSB
                    ammo_check = 2;
                    break;
            }
        }
        switch (weapon) {
            case 11235:// Dark bow
                ammo_check = 2;
                break;
        }
        int equip_slot = throwable ? Equipment.WEAPON_SLOT : Equipment.ARROW_SLOT;
        if (player.getEquipment().getAmountInSlot(equip_slot) < ammo_check) {
            player.getFrames().sendMessage("You don't have enough ammo in your quiver.");
            return false;
        }
        player.getAttributes().set("range_ammo", throwable ? weapon : arrows);
        return true;
    }

    public static void displayRangeProjectile(Player killer, Mob target, int weapon, int arrows) {
        ItemDefinition ammo_def = ItemDefinition.forId(arrows);
        ItemDefinition weapon_def = ItemDefinition.forId(weapon);
        int projectileId = -1;
        boolean throwable = false;
        ItemDefinition.RangedDefinition range_def = null;
        if (weapon_def != null) {
            range_def = weapon_def.rangedDefinition;
            if (range_def != null) {
                throwable = !range_def.usesAmmo();
            }
            if (throwable) {
                range_def = weapon_def.rangedDefinition;
                if (range_def != null) {
                    projectileId = range_def.getProjectile();
                }
            } else {
                /*
                 * if (weapon == 4212) {//crystal bow
                 *
                 * }
                 */
                range_def = ammo_def.rangedDefinition;
                if (range_def != null) {
                    projectileId = range_def.getProjectile();
                }
            }
        }
        if (weapon == 11235) {
            int airSpeed = 65;
            int startSpeed = 45;
            Projectiles projectile = Projectiles.create(killer.getPosition(), target.getPosition(), target,
                projectileId, startSpeed, airSpeed - 10, 50, 35, 34, 15 - 6, 64);
            killer.executeProjectile(projectile);
            // 2nd proj
            Projectiles projectile2 = Projectiles.create(killer.getPosition(), target.getPosition(), target,
                projectileId, startSpeed, airSpeed + 5, 50, 35, 34, 15 + 3, 64);
            killer.executeProjectile(projectile2);
            return;
        }
        int slope = 15;
        int startHeight = 43;
        int endHeight = 36;
        int startSpeed = 45;
        int airSpeed = 55;
        int distance = killer.getPosition().getDistanceFrom(target.getPosition());
        airSpeed = 55 + (distance * 5);
        if (weapon == 6522) {
            airSpeed = 65;
        }
        String name = ItemDefinition.forId(weapon).getName().toLowerCase();
        if (name.endsWith("crossbow")) {
            airSpeed = 55;
            startSpeed = 45;
            slope = 5;
        }
        if (name.endsWith("dart")) {
            airSpeed = 35;
            startSpeed = 20;
            slope = 5;
        }
        if (name.equals("hand cannon") || weapon == 10034) {
            startHeight = 21;
            endHeight = 18;
            startSpeed = 25;
            airSpeed = 40;
        }
        Projectiles projectile = Projectiles.create(killer.getPosition(), target.getPosition(), target, projectileId,
            startSpeed, airSpeed, 50, startHeight, endHeight, slope, 86);
        projectile.setSpeedRange(killer, target, false);
        killer.executeProjectile(projectile);
    }
}
