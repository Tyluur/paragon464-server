package com.paragon464.gameserver.model.content.skills.magic.boltenchanting;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.entity.mob.player.container.Container;
import com.paragon464.gameserver.model.content.skills.magic.RuneReplacers;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.item.ItemDefinition;
import com.paragon464.gameserver.tickable.Tickable;

import static com.paragon464.gameserver.model.entity.mob.player.SkillType.MAGIC;
import static com.paragon464.gameserver.model.content.skills.magic.boltenchanting.BoltEnchantingConstants.BOLT_AMOUNT;
import static com.paragon464.gameserver.model.content.skills.magic.boltenchanting.BoltEnchantingConstants.ENCHANT_ANIMATION;
import static com.paragon464.gameserver.model.content.skills.magic.boltenchanting.BoltEnchantingConstants.ENCHANT_GFX;
import static com.paragon464.gameserver.model.content.skills.magic.boltenchanting.BoltEnchantingConstants.MAGIC_LEVEL_TOO_LOW;
import static com.paragon464.gameserver.model.content.skills.magic.boltenchanting.BoltEnchantingConstants.NOT_ENOUGH_BOLTS;
import static com.paragon464.gameserver.model.content.skills.magic.boltenchanting.BoltEnchantingConstants.NOT_ENOUGH_SPACE;

public final class BoltsEnchanting {

    /**
     * A default constructor to prevent instantiation.
     */
    private BoltsEnchanting() {
    }

    /**
     * @param player The player attempting to enchant bolts.
     * @param button The interface button ID pressed.
     */
    public static void enchant(final Player player, final int button) {
        final Bolts bolts = Bolts.getBolts().get(button);

        if (bolts != null && !player.getAttributes().is("stopActions")) {
            final Container inventory = player.getInventory();
            final int unenchantedAmount = inventory.getItemAmount(bolts.getUnenchantedBoltId());

            if (unenchantedAmount < BOLT_AMOUNT) {
                player.getFrames().sendMessage(String.format(NOT_ENOUGH_BOLTS, BOLT_AMOUNT,
                    ItemDefinition.forId(bolts.getUnenchantedBoltId()).getName()));
                return;
            }

            if (bolts.getMagicLevelRequired() > player.getSkills().getCurrentLevel(MAGIC)) {
                player.getFrames().sendMessage(String.format(MAGIC_LEVEL_TOO_LOW, bolts.getMagicLevelRequired()));
                return;
            }

            if (!RuneReplacers.hasEnoughRunes(player, bolts.getRequiredRunes(), true)) {
                return;
            }

            final Item enchantedBolts = new Item(bolts.getEnchantedBoltId(), BOLT_AMOUNT);
            if (inventory.hasEnoughRoomFor(enchantedBolts) || unenchantedAmount <= BOLT_AMOUNT) {
                RuneReplacers.deleteRunes(player, bolts.getRequiredRunes());
                inventory.deleteItem(bolts.getUnenchantedBoltId(), BOLT_AMOUNT);
                inventory.addItem(enchantedBolts);
                inventory.refresh();

                player.getSkills().addExperience(MAGIC, bolts.getExperienceGranted());
                player.playAnimation(ENCHANT_ANIMATION, Animation.AnimationPriority.HIGH);
                player.playGraphic(ENCHANT_GFX);

                player.getInterfaceSettings().closeInterfaces(false);
                player.getAttributes().set("stopActions", true);
                World.getWorld().submit(new Tickable(1) {
                    @Override
                    public void execute() {
                        player.getAttributes().remove("stopActions");
                        this.stop();
                    }
                });
            } else {
                player.getFrames().sendMessage(String.format(NOT_ENOUGH_SPACE, BOLT_AMOUNT));
            }
        }
    }
}
