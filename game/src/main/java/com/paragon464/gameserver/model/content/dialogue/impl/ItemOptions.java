package com.paragon464.gameserver.model.content.dialogue.impl;

import com.paragon464.gameserver.model.World;
import com.paragon464.gameserver.model.entity.mob.masks.Animation;
import com.paragon464.gameserver.model.entity.mob.player.Player;
import com.paragon464.gameserver.model.content.JewelsHandler;
import com.paragon464.gameserver.model.content.dialogue.DialogueHandler;
import com.paragon464.gameserver.model.content.skills.magic.TeleportRequirements;
import com.paragon464.gameserver.model.item.Item;
import com.paragon464.gameserver.model.region.Position;
import com.paragon464.gameserver.tickable.Tickable;

public class ItemOptions extends DialogueHandler {

    private JewelsHandler.JewelType type;
    private Item item;
    private int itemIndex = -1;
    private boolean inventory;

    public ItemOptions(Player player, Item item, JewelsHandler.JewelType type, boolean inv) {
        super(player, false);
        this.item = item;
        this.type = type;
        this.inventory = inv;
        for (int i = 0; i < type.getIds().length; i++) {
            if (item.getId() == type.getIds()[i]) {
                this.itemIndex = i;
            }
        }
        this.sendDialogue();
    }

    @Override
    public void sendDialogue() {
    	Position target = type.getTeles()[optionClicked - 1];
        switch (this.stage) {
            case 0:
                this.options("Pick an Option", type.getOptions());
                break;
            default:
                if (TeleportRequirements.prevent(player, target, this.item)) {
                    end();
                    break;
                }
                int nextIndex = this.itemIndex - 1;
                if (inventory) {
                    if (!player.getInventory().hasItem(item.getId())) {
                        return;
                    }
                    if (nextIndex == -1) {
                        player.getInventory().deleteItem(item.getId());
                        player.getInventory().refresh();
                    } else {
                        player.getInventory().replaceItem(item.getId(), type.getIds()[nextIndex]);
                    }
                } else if (!inventory) {
                    if (!player.getEquipment().hasItem(item.getId())) {
                        return;
                    }
                    if (nextIndex == -1) {
                        player.getEquipment().deleteItem(item.getId());
                        player.getEquipment().refresh();
                    } else {
                        player.getEquipment().replaceItem(item.getId(), type.getIds()[nextIndex]);
                    }
                }
                player.getCombatState().end(1);
                player.resetActionAttributes();
                player.getAttributes().set("stopActions", true);
                player.playAnimation(9603, Animation.AnimationPriority.HIGH);
                player.playGraphic(1684);
                World.getWorld().submit(new Tickable(4) {
                    @Override
                    public void execute() {
                        this.stop();
                        player.teleport(target);
                        player.playAnimation(65535, Animation.AnimationPriority.HIGH);
                        World.getWorld().submit(new Tickable(0) {
                            @Override
                            public void execute() {
                                this.stop();
                                player.getAttributes().remove("stopActions");
                            }
                        });
                    }
                });
                end();
                break;
        }
    }
}
