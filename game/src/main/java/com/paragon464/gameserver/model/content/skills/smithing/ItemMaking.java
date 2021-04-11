package com.paragon464.gameserver.model.content.skills.smithing;

import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.SkillType;
import com.paragon464.gameserver.model.item.Item;

public class ItemMaking {

    private static final int HAMMER = 2347;

    public static void makeDFS(final Player player, final Item item) {
        if (player.getSkills().getCurrentLevel(SkillType.SMITHING) < 90) {
            player.getFrames().sendMessage("You need a Smithing level of 90 to make this.");
            return;
        }
        if (!player.getInventory().hasItem(HAMMER)) {
            player.getFrames().sendMessage("You need a Hammer to use the Anvil.");
            return;
        }
        if (!player.getInventory().hasItem(1540)) {
            player.getFrames().sendMessage("You need an Anti-dragon shield.");
            return;
        }
        player.getInventory().deleteItem(11286);
        player.getInventory().deleteItem(1540);
        player.getInventory().addItem(new Item(11283, 1));
        player.getInventory().refresh();
        player.getSkills().addExperience(SkillType.SMITHING, 2000);
        player.playAnimation(898, Animation.AnimationPriority.HIGH);
    }

    public static void makeGSBlade(final Player player, final int itemOne, final int itemTwo) {
        if (player.getSkills().getCurrentLevel(SkillType.SMITHING) < 80) {
            player.getFrames().sendMessage("You need a Smithing level of 80 to make this.");
            return;
        }
        if (itemOne == 11710 && itemTwo == 11712) {// shards
            player.getInventory().deleteItem(11710);
            player.getInventory().deleteItem(11712);
            player.getInventory().addItem(new Item(11686, 1));
            player.getSkills().addExperience(SkillType.SMITHING, 100);
        } else if (itemOne == 11712 && itemTwo == 11714) {// shards
            player.getInventory().deleteItem(11712);
            player.getInventory().deleteItem(11714);
            player.getInventory().addItem(new Item(11692, 1));
            player.getSkills().addExperience(SkillType.SMITHING, 100);
        } else if (itemOne == 11686 && itemTwo == 11714) {// shards
            player.getInventory().deleteItem(11686);
            player.getInventory().deleteItem(11714);
            player.getInventory().addItem(new Item(11690, 1));
        } else if (itemOne == 11710 && itemTwo == 11692) {// shards
            player.getInventory().deleteItem(11710);
            player.getInventory().deleteItem(11692);
            player.getInventory().addItem(new Item(11690, 1));
        } else if (itemOne == 11712 && itemTwo == 11688) {// shards
            player.getInventory().deleteItem(11712);
            player.getInventory().deleteItem(11688);
            player.getInventory().addItem(new Item(11690, 1));
        }
        player.getInventory().refresh();
    }
}
